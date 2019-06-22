#!/usr/bin/env python3
from module_1.python3.StockAnalysis import StockAnalysis

def main():
    StockAnalysis('./data/stocks/Mid_Cap/MUTHOOTFIN.csv').print()

if __name__ == "__main__":
    main()