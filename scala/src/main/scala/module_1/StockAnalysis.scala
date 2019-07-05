package module_1

import java.time.LocalDate

import scala.Numeric._
//import scala.util.chainingOps._  // Requires scala v2.13 - incompatible with kantan

object StockAnalysis extends App {
  val input_csv_filename:  String = if (args.length >= 1) args(0) else "../data/stocks/Mid_Cap/MUTHOOTFIN.csv"
  val output_csv_filename: String = if (args.length >= 2) args(1) else "../data/output/week2-scala.csv"

  var data: List[StockPrice] = StockPriceCSV.read(input_csv_filename)
  data = filter_not_eq(data)

  StockAnalysis.print(data)
  StockPriceCSV.write(output_csv_filename, data)

  def print( stockPrices: List[StockPrice] ) {
    println(this.getClass.getSimpleName)
    println(input_csv_filename)
    println("\nstockPrices.take(5)\n",                 stockPrices.take(5))
    // console.info("\nstockPrices.describe()\n",      stockPrices.describe())  // DOCS: https://stratodem.github.io/pandas.js-docs/#dataframe ???
    println("\nthis.stats_90_day_close_price()\n",     stats_90_day_close_price(stockPrices))
    println("\nthis.stats_vwap_by_month()\n",          stats_vwap_by_month(stockPrices))
    println("\nthis.stats_average_price()\n",          stats_average_price(stockPrices))
    println("\nthis.stats_profit_loss_percentage()\n", stats_profit_loss_percentage(stockPrices))
    // println("\nthis.stats_quantity_trend()\n",      stats_quantity_trend(stockPrices))
  }


  def filter_not_eq( stockPrices: List[StockPrice]): List[StockPrice] = {
    stockPrices.filter( _.Series != "EQ" )
  }

  def filter_days( stockPrices: List[StockPrice], days: Int ): List[StockPrice] = {
    // DOCS: https://stackoverflow.com/questions/38059191/how-make-implicit-ordered-on-java-time-localdate
    implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

    val date_end:    LocalDate = stockPrices.map(_.Date).max
    val date_cutoff: LocalDate = date_end.minusDays(days)

    stockPrices.filter(_.Date.compareTo(date_cutoff) > 0)
  }

  def vwap( stockPrices: List[StockPrice] ): Double = {
    val total = stockPrices.map(stockPrice => stockPrice.Close_Price * stockPrice.Total_Traded_Quantity).sum
    val price = stockPrices.map(stockPrice => stockPrice.Total_Traded_Quantity).sum
    total / price
  }

  def stats_vwap_by_month( stockPrices: List[StockPrice] ): Map[String,Double] = {
    stockPrices
      .groupBy(_.Date.getMonth.name)
      .mapValues(group => vwap(group))
  }

  // 1.5 Write a function to calculate the average price over the last N days of the stock price data where N is a user defined parameter.
  def average_price(stockPrices: List[StockPrice], days: Int): Double = {
    val prices = filter_days(stockPrices, days).map(_.Close_Price)

    prices.length match {
      case 0 => 0
      case 1 => 0
      case _ => grizzled.math.stats.mean( prices.head, prices.tail:_* )  // Weird syntax: https://github.com/bmc/grizzled-scala/blob/master/src/test/scala/grizzled/math/StatsSpec.scala
    }
  }

  // 1.5 Write a second function to calculate the profit/loss percentage over the last N days
  def profit_loss_percentage(stockPrices: List[StockPrice], days: Int): Double = {
    val prices = filter_days(stockPrices, days).map(_.Close_Price)

    prices.length match {
      case 0 => 0
      case 1 => 0
      case _ => (prices.last - prices.head) / prices.head * 100
    }
  }

  // 1.2 Calculate the maximum, minimum and mean price for the last 90 days. (price=Closing Price unless stated otherwise)
  def stats_90_day_close_price(stockPrices: List[StockPrice]): Map[String, Double] = {
    val prices = filter_days(this.data, 90).map(_.Close_Price)

    Map(
      "min"  -> prices.min,
      "max"  -> prices.max,
      "mean" -> grizzled.math.stats.mean( prices.head, prices.tail:_* )
    )
  }

  /**
    * 1.5 Calculate the average price AND the profit/loss percentages over the course of
    * last - 1 week, 2 weeks, 1 month, 3 months, 6 months and 1 year.
    */
  def stats_average_price( stockPrices: List[StockPrice] ): Map[String, Double] = {
    Map(
      "1 week"   -> average_price(stockPrices, 7 * 1),
      "2 weeks"  -> average_price(stockPrices, 7 * 2),
      "1 month"  -> average_price(stockPrices, 365 / 12 * 1),
      "2 months" -> average_price(stockPrices, 365 / 12 * 2),
      "6 months" -> average_price(stockPrices, 365 / 12 * 6),
      "1 year"   -> average_price(stockPrices, 365),
    )
  }

  /**
    * 1.5 Calculate the average price AND the profit/loss percentages over the course of
    * last - 1 week, 2 weeks, 1 month, 3 months, 6 months and 1 year.
    */
  def stats_profit_loss_percentage(stockPrices: List[StockPrice] ): Map[String, Double] = {
    Map(
      "1 week"   -> profit_loss_percentage(stockPrices, 7 * 1),
      "2 weeks"  -> profit_loss_percentage(stockPrices, 7 * 2),
      "1 month"  -> profit_loss_percentage(stockPrices, 365 / 12 * 1),
      "2 months" -> profit_loss_percentage(stockPrices, 365 / 12 * 2),
      "6 months" -> profit_loss_percentage(stockPrices, 365 / 12 * 6),
      "1 year"   -> profit_loss_percentage(stockPrices, 365),
    )
  }

//  // 1.8: Find the average and median values of the column 'Total Traded Quantity' for each of the types of 'Trend'.
//  def stats_quantity_trend( stockPrices: List[StockPrice] ) {
//    stockPrices
//        .groupBy(_.Trend)
//
//    const trends = _(this.data)
//      .groupBy('Trend')
//    .toPairs().sortBy(0).fromPairs() // Sort by keys
//      .mapValues((group) => _.map(group, 'Total_Traded_Quantity'))
//    .mapValues((values) => ({
//      "mean": ss.mean(values),
//      "median": ss.median(values),
//    }))
//      .value();
//    return trends;
//  }
}
