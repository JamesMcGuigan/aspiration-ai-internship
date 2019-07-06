#!/usr/bin/env node
import StockAnalysis from './StockAnalysis';

new StockAnalysis('../data/stocks/Mid_Cap/MUTHOOTFIN.csv')
    .print()
    .write_csv('../data/output/week2-typescript.csv')
    .write_stats('../data/output/week2-typescript.json')
;