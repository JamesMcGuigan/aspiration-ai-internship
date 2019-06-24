import _ from 'lodash';
import csv from '../../../node_modules/csv/lib/sync.js';
import fs from 'fs';
import 'datejs';

export default
class StockAnalysis {
    filename: string;
    data:     Array<Object>;

    constructor( input_csv_filename: string ) {
        this.filename = input_csv_filename;
        this.data = this._read_csv(input_csv_filename);
        this.data = this._filter_csv(this.data);
        this.data = this._extend_csv(this.data);
    }

    protected _read_csv( input_csv_filename: string ): Array<Object> {
        // 1.1: Import the csv file of the stock of your choosing using 'pd.read_csv()' function into a dataframe.
        return csv.parse(String(fs.readFileSync(input_csv_filename)), {
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
                // 1.3: Change the date column from 'object' type to 'datetime64(ns)'
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
    }

    protected _filter_csv( data: Array<Object> ): Array<Object> {
        // 1.1 remove all the rows where 'Series' column is NOT 'EQ'
        return _(data)
            .filter((row) => row['Series'] != "EQ")
            .value()
        ;
    }

    protected _extend_csv( data: Array<Object> ): Array<Object> {
        return _(data)
            // 1.4: Create a new dataframe column ‘Month’ + 'Year'
            .map((row) => {
                let date = row['Date'] as Date;
                return {
                    ...row,
                    'Date_Year':  date.getFullYear(),
                    'Date_Month': date.getMonth()
                }
            })
            .map((row, index, data) => {
                // 1.6: Add a column 'Day_Perc_Change' where the values are the daily change in percentages
                // 1.7: Add another column 'Trend'
                // NOTE: pandas.Series.pct_change() doesn't multiply Day_Perc_Change * 100
                let Last_Close_Price = _.get(data, [index - 1, 'Close_Price']);
                let Day_Perc_Change  = (row['Close_Price'] - Last_Close_Price) / Last_Close_Price || 0;
                let Trend            = this.trend(Day_Perc_Change);

                return { ...row, Day_Perc_Change, Trend };
            })
            .value()
        ;
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

    /**
     *  1.7 Add another column 'Trend' whose values are:
     *  'Slight or No change' for 'Day_Perc_Change' in between -0.5 and 0.5
     *  'Slight positive' for 'Day_Perc_Change' in between 0.5 and 1
     *  'Slight negative' for 'Day_Perc_Change' in between -0.5 and -1
     *  'Positive' for 'Day_Perc_Change' in between 1 and 3
     *  'Negative' for 'Day_Perc_Change' in between -1 and -3
     *  'Among top gainers' for 'Day_Perc_Change' in between 3 and 7
     *  'Among top losers' for 'Day_Perc_Change' in between -3 and -7
     *  'Bull run' for 'Day_Perc_Change' >7
     *  'Bear drop' for 'Day_Perc_Change' <-7
     */
    trend( day_perc_change: Number ): string {
        if( -0.5 <= day_perc_change && day_perc_change <=   0.5 ) { return 'Slight'; }
        if(  0.5 <= day_perc_change && day_perc_change  <=  1   ) { return 'Slight positive'; }
        if( -0.5 >= day_perc_change && day_perc_change  >= -1   ) { return 'Slight negative'; }
        if(  1   <= day_perc_change && day_perc_change  <=  3   ) { return 'Positive'; }
        if( -1   >= day_perc_change && day_perc_change  >= -3   ) { return 'Negative'; }
        if(  3   <= day_perc_change && day_perc_change  <=  7   ) { return 'Among top gainers'; }
        if( -3   >= day_perc_change && day_perc_change  >= -7   ) { return 'Among top losers';  }
        if(                            day_perc_change  >   7   ) { return 'Bull run';  }
        if(                            day_perc_change  <  -7   ) { return 'Bear drop'; }
        return "Error";
    }

    print(): StockAnalysis {
        return this;
    }
}