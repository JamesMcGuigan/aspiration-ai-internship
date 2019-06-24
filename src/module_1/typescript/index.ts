#!/usr/bin/env node
import StockAnalysis from './StockAnalysis';

new StockAnalysis('./data/stocks/Mid_Cap/MUTHOOTFIN.csv')
    .write('./data/output/week2-typescript.csv')
    .print()
;