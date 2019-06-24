#!/usr/bin/env python3
from src.module_1.python3 import StockAnalysis

def main():
    StockAnalysis('./data/stocks/Mid_Cap/MUTHOOTFIN.csv') \
        .write('./data/output/week2.csv') \
        .print()

if __name__ == "__main__":
    main()