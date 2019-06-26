#!/usr/bin/env python3
from module_1.StockAnalysis import StockAnalysis

def main():
    StockAnalysis('../data/stocks/Mid_Cap/MUTHOOTFIN.csv') \
        .print() \
        .write('../data/output/week2-python.csv')

if __name__ == "__main__":
    main()