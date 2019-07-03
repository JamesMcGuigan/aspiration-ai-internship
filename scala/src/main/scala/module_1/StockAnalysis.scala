package module_1

object StockAnalysis extends App {
  val input_csv_filename:  String = if (args.length >= 1) args(0) else "../data/stocks/Mid_Cap/MUTHOOTFIN.csv"
  val output_csv_filename: String = if (args.length >= 2) args(1) else "../data/output/week2-scala.csv"

  var data: List[StockPrice] = StockPriceCSV.read(input_csv_filename)
  data = filter_not_eq(data)


  StockPriceCSV.print(data)
  StockPriceCSV.write(output_csv_filename, data)

  def filter_not_eq( stockPrices: List[StockPrice]): List[StockPrice] = {
    stockPrices.filter( _.Series != "EQ" )
  }

}
