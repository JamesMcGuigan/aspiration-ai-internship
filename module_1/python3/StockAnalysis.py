import re

import pandas as pd


class StockAnalysis:
    dateformat = '%d-%b-%Y'

    def __init__( self, filename: str ):
        ### 1.1 Import the csv file of the stock of your choosing using 'pd.read_csv()' function into a dataframe.
        self.filename = filename
        self.data = pd.read_csv(self.filename, parse_dates=['Date'] )        # parse_dates=['Date'] cast to Timestamp
        self.data = self.data.rename(lambda x: re.sub(r'[^\w\s]+', '', x).replace(r' ', '_'), axis=1)  # Rename columns without spaces
        self.data = self.filter_not_eq(self.data)

        ### 1.3 Change the date column from 'object' type to 'datetime64(ns)' | WORKAROUND: cast to pd.Timestamp instead
        # self.data['Date'] = pd.to_datetime(self.data['Date'])              # Cast to Timestamp without parse_dates=["Date"]
        # self.data['Date'] = self.data['Date'].map(lambda date: np.datetime64(date.value, 'ns')) # BUG: Cast to datetime64 returns Timestamp
        # print( type(self.data['Date'][0]), self.data['Date'][0] )                               # BUG: Cast to datetime64 returns Timestamp

        ### 1.4 Hint : Create a new dataframe column ‘Month’ + 'Year'
        self.data['Date_Month'] = self.data['Date'].map(lambda date: date.month)
        self.data['Date_Year']  = self.data['Date'].map(lambda date: date.year)


    def print(self):
        print(self.__class__.__name__)
        print(self.filename)
        print('\nself.data.head()\n',     self.data.head())
        print('\nself.data.describe()\n', self.data.describe())
        print('\nself.stats_90_day_close_price()\n', self.stats_90_day_close_price())
        print('\nself.stats_vwap()\n', self.stats_vwap_by_month())


    @staticmethod
    def filter_not_eq( data: pd.DataFrame ) -> pd.DataFrame:
        """  1.1 remove all the rows where 'Series' column is NOT 'EQ' """
        return data[data.Series != 'EQ']


    @staticmethod
    def filter_days( data: pd.DataFrame, days=90 ) -> pd.DataFrame:
        date_end:    pd.Timestamp = data['Date'].max()
        date_cutoff: pd.Timestamp = date_end - pd.to_timedelta(90, unit='d')
        return data[data.Date > str(date_cutoff)]


    def stats_90_day_close_price(self) -> dict:
        """ 1.2 Calculate the maximum, minimum and mean price for the last 90 days. (price=Closing Price unless stated otherwise) """
        data_90_days = self.filter_days(self.data, 90)
        stats = {
            "min":  data_90_days['Close_Price'].min(),
            "max":  data_90_days['Close_Price'].max(),
            "mean": data_90_days['Close_Price'].mean(),
        }
        stats = { k: round(v,2) for k,v in stats.items() }  # round to 2dp
        return stats


    @staticmethod
    def vwap(group_df: pd.DataFrame) -> float:
        vwap = (group_df.Close_Price + group_df.Total_Traded_Quantity).sum() \
               / group_df.Total_Traded_Quantity.sum()
        return vwap


    def stats_vwap_by_month(self) -> pd.DataFrame:
        """
        1.4 Calculate the monthwise VWAP = sum(price*volume)/sum(volume)
        :return:
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

