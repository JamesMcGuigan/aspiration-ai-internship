import _ from 'lodash';
import csv from '../../../node_modules/csv/lib/sync.js';
import fs from 'fs';
import ss from '../../../node_modules/simple-statistics/dist/simple-statistics.js';
import 'datejs';
// import moment, { Moment } from '../../../node_modules/moment/moment';
// moment['suppressDeprecationWarnings'] = true;  // BUGFIX: TS2339: Property 'suppressDeprecationWarnings' does not exist on type 'typeof moment'.

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
                    //// date.js
                    let value_date = Date.parse(value);
                    if( value_date ) {
                        return new Date(value_date);
                    }
                    //// moment.js
                    // const value_date = moment(value);
                    // if( value_date.isValid() ) {
                    //     return value_date;
                    // }
                }
                return value;
            }
        });
    }

    _filter_csv( data: Array<Object> ): Array<Object> {
        // 1.1 remove all the rows where 'Series' column is NOT 'EQ'
        return _(data)
            .filter((row) => row['Series'] != "EQ")
            .value()
        ;
    }

    _filter_days( data: Array<Object>, days?: Number ): Array<Object> {
        if( days === undefined ) { return data; }

        let date_end    = _(data).map("Date").max();
        let date_cutoff = date_end.clone().add(-days).days();  // DateJS/Moment operations modify existing variable
        return _.filter(data, (row) => row['Date'] > date_cutoff);
    }

    _extend_csv( data: Array<Object> ): Array<Object> {
        return _(data)
            // 1.4: Create a new dataframe column ‘Month’ + 'Year'
            .map((row) => {
                let date = row['Date'] as Date;
                return {
                    ...row,
                    'Date_Year':  date.getFullYear(),
                    'Date_Month': date.getMonth() + 1,  // NOTE: Javascript dates are zero indexed
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
                number: (value, context) => Number.isInteger(value) ? String(value) : value.toFixed(2),
                date:   (value, context) => value.format('Y-m-d'), // DOCS: https://github.com/abritinthebay/datejs/wiki/Format-Specifiers
                // object: (value, context) => {
                //     if( moment.isMoment(value) ) {
                //         return value.format('YYYY-MM-DD') // DOCS: https://momentjs.com/docs/#/displaying/
                //     }
                //     return value;
                // },
            }
        });
        fs.writeFileSync(output_csv_filename, csv_string);
        console.info(`Wrote: ${output_csv_filename}`);
        return this;
    }

    print(): StockAnalysis {
        console.info(this.constructor.name);
        console.info(this.filename);
        console.info('\nthis.data.head()\n',                    _(this.data).take(5).value());
        // console.info('\nthis.data.describe()\n',                      this.data.describe())  // DOCS: https://stratodem.github.io/pandas.js-docs/#dataframe ???
        console.info('\nthis.stats_90_day_close_price()\n',     this.stats_90_day_close_price());
        console.info('\nthis.stats_vwap_by_month()\n',          this.stats_vwap_by_month());
        console.info('\nthis.stats_average_price()\n',          JSON.stringify(this.stats_average_price(), null, 4));
        console.info('\nthis.stats_profit_loss_percentage()\n', JSON.stringify(this.stats_profit_loss_percentage(), null, 4));
        console.info('\nthis.stats_quantity_trend()\n',         JSON.stringify(this.stats_quantity_trend(), null, 4));
        return this  // for chaining
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

    vmap( data: Array<Object> ): Number {
        let total = _(data).map((row) => row['Close_Price'] * row['Total_Traded_Quantity'] ).sum();
        let price = _(data).map((row) => row['Total_Traded_Quantity'] ).sum();
        return total / price;
    }

    // 1.5 Write a function to calculate the average price over the last N days of the stock price data where N is a user defined parameter.
    average_price( data: Array<Object>, days?: Number ): Number {
        return _( this._filter_days(data, days) )
            .map((row) => row['Close_Price'])
            .mean()
        ;
    }

    // 1.5 Write a second function to calculate the profit/loss percentage over the last N days
    profit_loss_percentage( data: Array<Object>, days?: Number ): Number {
        return _.chain( this._filter_days(data, days) )
            .map((row) => row['Close_Price'])
            .thru((prices) => (_.last(prices) - _.first(prices)) / _.first(prices) || 0 )
            .thru((value)  => value * 100 )
            .value()
        ;
    }


    // 1.4 Calculate the monthwise VWAP = sum(price*volume)/sum(volume)
    stats_vwap_by_month() {
        const vwap = _(this.data)
            .groupBy((row) => [ row['Date'].getFullYear(), row['Date'].getMonth()+1 ] )
            .mapValues((group, _key) => this.vmap(group))
            .value()
        ;
        return vwap;
    }

    stats_90_day_close_price() {
        // 1.2 Calculate the maximum, minimum and mean price for the last 90 days. (price=Closing Price unless stated otherwise)
        const data_90_days = this._filter_days(this.data, 90);
        let stats = {
            "min":  Number( _(data_90_days).map('Close_Price').min()  ),
            "max":  Number( _(data_90_days).map('Close_Price').max()  ),
            "mean": Number( _(data_90_days).map('Close_Price').mean() ),
        };
        return stats;
    }

    /**
     * 1.5 Calculate the average price AND the profit/loss percentages over the course of
     * last - 1 week, 2 weeks, 1 month, 3 months, 6 months and 1 year.
     */
    stats_average_price(): Object {
        return {
            "1 week":   this.average_price(this.data, 7 * 1),
            "2 weeks":  this.average_price(this.data, 7 * 2),
            "1 month":  this.average_price(this.data, Math.round(365 / 12 * 1)),
            "2 months": this.average_price(this.data, Math.round(365 / 12 * 2)),
            "6 months": this.average_price(this.data, Math.round(365 / 12 * 6)),
            "1 year":   this.average_price(this.data, 365),
        }
    }

    /**
     * 1.5 Calculate the average price AND the profit/loss percentages over the course of
     * last - 1 week, 2 weeks, 1 month, 3 months, 6 months and 1 year.
     */
    stats_profit_loss_percentage(): Object {
        return {
            "1 week":   this.profit_loss_percentage(this.data, 7 * 1),
            "2 weeks":  this.profit_loss_percentage(this.data, 7 * 2),
            "1 month":  this.profit_loss_percentage(this.data, Math.round(365 / 12 * 1)),
            "2 months": this.profit_loss_percentage(this.data, Math.round(365 / 12 * 2)),
            "6 months": this.profit_loss_percentage(this.data, Math.round(365 / 12 * 6)),
            "1 year":   this.profit_loss_percentage(this.data, 365),
        }
    }

    // 1.8: Find the average and median values of the column 'Total Traded Quantity' for each of the types of 'Trend'.
    stats_quantity_trend(): Object {
        const trends = _(this.data)
            .groupBy('Trend')
            .toPairs().sortBy(0).fromPairs()  // Sort by keys
            .mapValues((group) =>
                _.map(group,'Total_Traded_Quantity')
            )
            .mapValues((values) => ({
                "mean":   ss.mean(   values as number[] ),
                "median": ss.median( values as number[] ),
            }))
            .value()
        ;
        return trends;
    }

}