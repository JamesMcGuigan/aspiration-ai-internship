#!/usr/bin/env node
import StockAnalysis from './StockAnalysis';
import glob from "../node_modules/glob/glob.js";
import Promise from "../node_modules/bluebird/js/release/bluebird.js";

// Output single file as original specs
new StockAnalysis('../data/stocks/Mid_Cap/MUTHOOTFIN.csv')
    .print()
    .write_csv('../data_output/module_1/week2-typescript.csv')
    .write_stats('../data_output/module_1/week2-typescript.json')
;


Promise.resolve(
    glob.sync("../data/stocks/**/*.csv")
        .map((input_csv) => {
            let output_csv = input_csv.replace('\.\./data/', '../data_output/module_1/typescript/');
            let output_json = output_csv.replace('\.csv', '\.json');
            return {input_csv, output_csv, output_json}
        })
    )
    .map((path) => {
        new StockAnalysis(path.input_csv)
            .write_csv(path.output_csv)
            .write_stats(path.output_json)
    })
;
