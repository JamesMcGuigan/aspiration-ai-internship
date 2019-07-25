import os

import numpy as np
import pandas as pd
from sklearn.linear_model import LinearRegression

pd.options.mode.chained_assignment = None  # default='warn'  # Disable: SettingWithCopyWarning


class CommodityLinearRegression:
    filename = '../data/commodities/GOLD.csv'
    outfile  = '../data_output/module_3/GOLD.csv'
    X_fields = ['Price','Open','High','Low']
    Y_field  = 'Pred'

    def __init__(self, filename: str, outfile: str):
        self.filename = filename or self.filename
        self.outfile  = outfile  or self.outfile
        self.data     = self.load_data(self.filename)
        self.model    = self.train_model( self.data )
        self.write_file(self.outfile, self.data, self.model)
        pass


    def load_data(self, filename):
        data = {}
        data['raw']     = pd.read_csv( filename, parse_dates=['Date'] )
        data['train']   = data['raw'][ -np.isnan(data['raw']['Pred']) ]  # -Series() == np.invert()
        data['test']    = data['raw'][  np.isnan(data['raw']['Pred']) ]  # Gold dataset has empty Pred values for test data

        data['train_X'] = data['train'][ self.X_fields ]
        data['test_X']  = data['test' ][ self.X_fields ]

        data['train_Y'] = data['train'][ self.Y_field  ]
        data['test_Y']  = data['test' ][ self.Y_field  ]  # This should be all empty fields

        print("Read: ", filename)
        return data


    def train_model(self, data):
        model = {}
        model['model']         = LinearRegression().fit( data['train_X'], data['train_Y'] )
        model['score']         = model['model'].score(   data['train_X'], data['train_Y'] )
        model['coef_']         = model['model'].coef_
        model['intercept_']    = model['model'].intercept_
        model['predict_train'] = model['model'].predict( data['train_X'] )
        model['predict_test']  = model['model'].predict( data['test_X']  )

        print( 'score:      ', model['score'] )
        print( 'coef_:      ', model['coef_'] )
        print( 'intercept_: ', model['intercept_'] )
        return model


    def write_file( self, outfile: str, data: dict, model: dict ):
        train  = data['train']
        test   = data['test']
        train[ self.Y_field ] = model['predict_train']
        test[  self.Y_field ] = model['predict_test']

        os.makedirs( os.path.dirname(outfile), exist_ok=True )  # ensure directory exists
        pd.concat([train, test]).to_csv( outfile, index=False )

if __name__ == "__main__":
    CommodityLinearRegression('../data/commodities/GOLD.csv', '../data_output/module_3/GOLD.csv' )