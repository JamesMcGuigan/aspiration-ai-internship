#!/usr/bin/env python3
import re

import glob2

from module_1.StockAnalysis import StockAnalysis


def main():
    # Output single file as original specs
    StockAnalysis('../data/stocks/Mid_Cap/MUTHOOTFIN.csv') \
        .print() \
        .write_csv(   '../data_output/week2/week2-python.csv'  ) \
        .write_stats( '../data_output/week2/week2-python.json' )

    # Output directory tree of all stocks
    for input_csv in glob2.iglob('../data/stocks/**/*.csv'):
        output_csv  = re.sub( '\.\./data/', '../data_output/week2/python3/', input_csv )
        output_json = re.sub( '\.csv', '.json', output_csv )

        StockAnalysis(input_csv) \
            .write_csv(   output_csv  ) \
            .write_stats( output_json )

if __name__ == "__main__":
    main()