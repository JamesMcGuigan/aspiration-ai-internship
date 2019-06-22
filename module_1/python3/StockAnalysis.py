import pandas as pd
import datetime

class StockAnalysis:
    dateformat = '%d-%b-%Y'

    def __init__( self, filename: str ):
        """ 1.1 Import the csv file of the stock of your choosing using 'pd.read_csv()' function into a dataframe. """
        self.filename = filename
        self.data = pd.read_csv(self.filename)\
            .rename(lambda x: x.replace(' ', '_'), axis=1)  # Rename columns without spaces
        self.data = self.filter_not_eq(self.data)


    def print(self):
        print(self.__class__.__name__)
        print(self.filename)
        print('self.data.head()\n',     self.data.head())
        print('self.data.describe()\n', self.data.describe())
        print('self.stats_90_day_close_price()\n', self.stats_90_day_close_price())


    @staticmethod
    def filter_not_eq( data: pd.DataFrame ) -> pd.DataFrame:
        """  1.1 remove all the rows where 'Series' column is NOT 'EQ' """
        return data[data.Series != 'EQ']


    @staticmethod
    def filter_days( data: pd.DataFrame, days=90 ) -> pd.DataFrame:
        date_end    = data['Date'].max()
        date_end    = datetime.datetime.strptime( date_end, StockAnalysis.dateformat )  # cast to datetime
        date_cutoff = date_end - pd.to_timedelta(90, unit='d')
        return data[data.Date > str(date_cutoff)]


    def stats_90_day_close_price(self):
        """ 1.2 Calculate the maximum, minimum and mean price for the last 90 days. (price=Closing Price unless stated otherwise) """
        data_90_days = self.filter_days(self.data, 90)
        stats = {
            "min":  data_90_days['Close_Price'].min(),
            "max":  data_90_days['Close_Price'].max(),
            "mean": data_90_days['Close_Price'].mean(),
        }
        stats = { k: round(v,2) for k,v in stats.items() }  # round to 2dp
        return stats

