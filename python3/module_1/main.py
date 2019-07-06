#!/usr/bin/env python3
from module_1.StockAnalysis import StockAnalysis

def main():
    StockAnalysis('../data/stocks/Mid_Cap/MUTHOOTFIN.csv') \
        .print() \
        .write_csv(   '../data/output/week2-python.csv'  ) \
        .write_stats( '../data/output/week2-python.json' )

if __name__ == "__main__":
    main()