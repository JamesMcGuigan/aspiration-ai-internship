package module_1

object StockAnalysis extends App {
  val input_csv_filename:  String = if (args.length >= 1) args(0) else "../data/stocks/Mid_Cap/MUTHOOTFIN.csv"
  val output_csv_filename: String = if (args.length >= 2) args(1) else "../data/output/week2-scala.csv"

  val data: List[StockPrice] = StockPriceCSV.read(input_csv_filename)
  StockPriceCSV.print(data)
  StockPriceCSV.write(output_csv_filename, data)
}