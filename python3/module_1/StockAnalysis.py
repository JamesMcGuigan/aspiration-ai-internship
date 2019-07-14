import math
import os
import re

import pandas as pd
import simplejson


class StockAnalysis:
    dateformat = '%d-%b-%Y'

    def __init__(self, input_csv_filename: str):
        ### 1.1: Import the csv file of the stock of your choosing using 'pd.read_csv()' function into a dataframe.
        self.filename = input_csv_filename
        self.data = pd.read_csv(self.filename, parse_dates=['Date'] )        # parse_dates=['Date'] cast to Timestamp
        self.data = self.data.rename(lambda x: re.sub(r'%', 'Percent', x).replace('.', '').replace(' ', '_'), axis=1)  # Rename columns without spaces
        self.data = self.filter_not_eq(self.data)

        ### 1.3: Change the date column from 'object' type to 'datetime64(ns)' | WORKAROUND: cast to pd.Timestamp instead
        # self.data['Date'] = pd.to_datetime(self.data['Date'])              # Cast to Timestamp without parse_dates=["Date"]
        # self.data['Date'] = self.data['Date'].map(lambda date: np.datetime64(date.value, 'ns')) # BUG: Cast to datetime64 returns Timestamp
        # print( type(self.data['Date'][0]), self.data['Date'][0] )                               # BUG: Cast to datetime64 returns Timestamp

        ### 1.4: Create a new dataframe column ‘Month’ + 'Year'
        self.data['Date_Year']  = self.data['Date'].map(lambda date: date.year)
        self.data['Date_Month'] = self.data['Date'].map(lambda date: date.month)


        ### 1.6: Add a column 'Day_Perc_Change' where the values are the daily change in percentages
        self.data['Day_Perc_Change'] = self.data['Close_Price'].pct_change() \
                                           .map(lambda x: x if not math.isnan(x) else 0 ) \
                                           .map(lambda x: round(x, 2) )

        ### 1.7: Add another column 'Trend' whose values
        self.data['Trend'] = self.data['Day_Perc_Change'].map(self.trend)


    def print(self):
        print(self.__class__.__name__)
        print(self.filename)
        print('\nself.data.head()\n',                    self.data.head())
        print('\nself.data.describe()\n',                self.data.describe())

        print( simplejson.dumps(self.stats(), indent=4*' ') )
        return self  # for chaining

    def stats(self):
        return {
            "stats_90_day_close_price":     self.stats_90_day_close_price(),
            "stats_vwap_by_month":          self.stats_vwap_by_month(),
            "stats_average_price":          self.stats_average_price(),
            "stats_profit_loss_percentage": self.stats_profit_loss_percentage(),
            "stats_quantity_trend":         self.stats_quantity_trend()
        }

    def ensure_directory(self, filename):
        dirname = os.path.dirname(filename)
        if( not os.path.isdir(dirname) ):
            os.makedirs(dirname)  # ensure directory exists

    def write_csv(self, output_csv_filename: str):
        ### 1.9 SAVE the dataframe with the additional columns computed as a csv file
        self.ensure_directory(output_csv_filename)
        self.data.to_csv(output_csv_filename)
        print('Wrote: ', output_csv_filename)
        return self  # for chaining

    def write_stats(self, output_stats_filename: str):
        ### 1.9 SAVE the dataframe with the additional columns computed as a csv file
        self.ensure_directory(output_stats_filename)
        with open(output_stats_filename, "w") as stats_file:
            stats_json = simplejson.dumps(self.stats(), indent=4*' ')
            stats_file.write( stats_json )
        return self  # for chaining

    @staticmethod
    def filter_not_eq( data: pd.DataFrame ) -> pd.DataFrame:
        """  1.1 remove all the rows where 'Series' column is NOT 'EQ' """
        return data[data.Series != 'EQ']

    @staticmethod
    def filter_days(df: pd.DataFrame, days=None) -> pd.DataFrame:
        if days is None: return df  # BUGFIX: pd.to_timedelta(math.inf) == {OverflowError}cannot convert float infinity to integer

        date_end:    pd.Timestamp = df['Date'].max()
        date_cutoff: pd.Timestamp = date_end - pd.to_timedelta(round(days), unit='d')
        return df[df.Date > str(date_cutoff)]

    @staticmethod
    def vwap(df: pd.DataFrame) -> float:
        vwap = (df.Close_Price * df.Total_Traded_Quantity).sum() \
               / df.Total_Traded_Quantity.sum()
        return round(vwap, 2)

    @staticmethod
    def average_price(df: pd.DataFrame, days=None) -> float:
        """ 1.5 Write a function to calculate the average price over the last N days of the stock price data where N is a user defined parameter. """
        df_days       = StockAnalysis.filter_days(df, days)
        average_price = df_days.Close_Price.mean()
        return round(average_price, 2)

    @staticmethod
    def profit_loss_percentage(df: pd.DataFrame, days=None) -> float:
        """ 1.5 Write a second function to calculate the profit/loss percentage over the last N days. """
        df_days     = StockAnalysis.filter_days(df, days)
        close_price = df_days.Close_Price.values

        if len(close_price) == 0:
            return 0  # BUGFIX: IndexError: index -1 is out of bounds for axis 0 with size 0
        else:
            profit      = (close_price[-1] - close_price[0]) / close_price[-1]
            profit_pc   = profit * 100  # QUESTION: Should this return as 0.x fractional percentage or 0-100%
            return round(profit_pc, 2)  # return as percentage

    @staticmethod
    def trend( day_perc_change: float ) -> str:
        """
        1.7 Add another column 'Trend' whose values are:
            'Slight or No change' for 'Day_Perc_Change' in between -0.5 and 0.5
            'Slight positive' for 'Day_Perc_Change' in between 0.5 and 1
            'Slight negative' for 'Day_Perc_Change' in between -0.5 and -1
            'Positive' for 'Day_Perc_Change' in between 1 and 3
            'Negative' for 'Day_Perc_Change' in between -1 and -3
            'Among top gainers' for 'Day_Perc_Change' in between 3 and 7
            'Among top losers' for 'Day_Perc_Change' in between -3 and -7
            'Bull run' for 'Day_Perc_Change' >7
            'Bear drop' for 'Day_Perc_Change' <-7
        """
        if -0.5 <= day_perc_change <=  0.5: return 'Slight'
        if  0.5 <= day_perc_change <=  1:   return 'Slight positive'
        if -0.5 >= day_perc_change >= -1:   return 'Slight negative'
        if  1   <= day_perc_change <=  3:   return 'Positive'
        if -1   >= day_perc_change >= -3:   return 'Negative'
        if  3   <= day_perc_change <=  7:   return 'Among top gainers'
        if -3   >= day_perc_change >= -7:   return 'Among top losers'
        if         day_perc_change >   7:   return 'Bull run'
        if         day_perc_change <  -7:   return 'Bear drop'


    def stats_90_day_close_price(self) -> dict:
        """ 1.2 Calculate the maximum, minimum and mean price for the last 90 days. (price=Closing Price unless stated otherwise) """
        data_90_days = self.filter_days(self.data, 90)
        stats = {
            "min":  data_90_days.Close_Price.min(),
            "max":  data_90_days.Close_Price.max(),
            "mean": data_90_days.Close_Price.mean(),
        }
        stats = { k: round(v,2) for k,v in stats.items() }  # round to 2dp
        return stats

    def stats_vwap_by_month_df(self) -> pd.DataFrame:
        """
        1.4 Calculate the monthwise VWAP = sum(price*volume)/sum(volume)
        """
        vwap = {}
        groupByMonth = self.data.groupby(['Date_Year', 'Date_Month'])
        for key, group_df in groupByMonth:
            vwap[key] = {
                "Year":  int(key[0]),
                "Month": int(key[1]),
                "VWAP":  self.vwap(group_df)
            }
        vwap_df = pd.DataFrame(vwap).transpose().reindex(["Year", "Month", "VWAP"], axis=1)
        vwap_df[["Year", "Month"]] = vwap_df[["Year", "Month"]].apply(pd.to_numeric, downcast='integer')
        return vwap_df

    def stats_vwap_by_month(self) -> dict:
        """
        1.4 Calculate the monthwise VWAP = sum(price*volume)/sum(volume)
        """
        vwap = {}
        groupByMonth = self.data.groupby(['Date_Year', 'Date_Month'])
        for key, group_df in groupByMonth:
            month = str(key[0]) + "-" + str(key[1])
            vwap[month] = self.vwap(group_df)
        return vwap


    def stats_average_price(self) -> dict:
        """
        1.5 Calculate the average price AND the profit/loss percentages over the course of
        last - 1 week, 2 weeks, 1 month, 3 months, 6 months and 1 year.
        """
        return {
            "1 week":   self.average_price(self.data, 7*1),
            "2 weeks":  self.average_price(self.data, 7*2),
            "1 month":  self.average_price(self.data, round(365/12 * 1)),
            "2 months": self.average_price(self.data, round(365/12 * 2)),
            "6 months": self.average_price(self.data, round(365/12 * 6)),
            "1 year":   self.average_price(self.data, 365),
        }

    def stats_profit_loss_percentage(self) -> dict:
        """
        1.5 Calculate the average price AND the profit/loss percentages over the course of
        last - 1 week, 2 weeks, 1 month, 3 months, 6 months and 1 year.
        """
        return {
            "1 week":   self.profit_loss_percentage(self.data, 7*1),
            "2 weeks":  self.profit_loss_percentage(self.data, 7*2),
            "1 month":  self.profit_loss_percentage(self.data, round(365/12 * 1)),
            "2 months": self.profit_loss_percentage(self.data, round(365/12 * 2)),
            "6 months": self.profit_loss_percentage(self.data, round(365/12 * 6)),
            "1 year":   self.profit_loss_percentage(self.data, 365),
        }

    def stats_quantity_trend(self) -> dict:
        """ 1.8: Find the average and median values of the column 'Total Traded Quantity' for each of the types of 'Trend'. """
        trends = {}
        groups_df = self.data.groupby(['Trend'])
        for key, group_df in groups_df:
            trends[key] = {
                "mean":   round( group_df['Total_Traded_Quantity'].mean(),   2),
                "median": round( group_df['Total_Traded_Quantity'].median(), 2),
            }
        return trends
