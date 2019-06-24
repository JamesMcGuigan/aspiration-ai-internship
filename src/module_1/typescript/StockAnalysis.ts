import * as _ from 'lodash';
import csv from '../../../node_modules/csv/lib/sync.js';
import fs from 'fs';
import 'datejs';

export default
class StockAnalysis {
    filename: string;
    data:     Object;

    constructor( input_csv_filename: string ) {
        this.filename = input_csv_filename;
        this.data = csv.parse(String(fs.readFileSync(input_csv_filename)), {
            columns: true,
            cast: (value, context): string|number|Date => {
                value = value.trim();
                if( context.header ) {
                    return value
                        .replace(/%/g, 'Percent')
                        .replace(/\./g, '')
                        .replace(/\s+/g, '_')
                    ;
                }
                let numberMatch = value.match(/^[+-]?(\d+\.?|\d*\.\d+)(\w)?$/);
                if( numberMatch ) {
                    let value_number = parseFloat(value);
                    switch( numberMatch[2] ) {
                        case "%": value_number *= 100;       break;
                        case "K": value_number *= 1000;      break;
                        case "M": value_number *= 1000*1000; break;
                    }
                    return value_number
                }
                // BUGFIX: Date.parse('N9') == "2019-06-24T22:35:20.752Z"
                if( value.match(/\b\d{1,2}\b/) && value.match(/\b\d{4}\b/) ) {  // contains both day and year
                    let value_date = Date.parse(value);
                    if( value_date ) {
                        return new Date(value_date);
                    }
                }
                return value;
            }
        });
        console.log(this.data);
    }

    write( output_csv_filename: string ): StockAnalysis {
        let csv_string = csv.stringify(this.data, {
            header: true,
            cast: {
                date: (value) => value.format('Y-m-d')
            }
        });
        fs.writeFileSync(output_csv_filename, csv_string);
        return this;
    }

    print(): StockAnalysis {
        return this;
    }
}