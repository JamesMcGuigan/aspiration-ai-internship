package module_1

import better.files.{File, _}

object Main extends App {

  // Output single file as original specs
  StockAnalysis.main(Array(
    "../data_output/week2/scala/stocks/Mid_Cap/MUTHOOTFIN.csv",
    "../data_output/week2/week2-scala.csv",
    "../data_output/week2/week2-scala.json"
  ))


  // Output directory tree of all stocks
  val data_dir:   File         = file"../data/stocks/"
  val input_csvs: List[String] = data_dir.glob("**/*.csv").map(_.path.toString).toList
  input_csvs.foreach((input_csv: String) => {
    val output_csv  = input_csv.replaceAll("/data/", "/data_output/week2/scala/")
    val output_json = output_csv.replaceAll(".csv", ".json")

    StockAnalysis.main(Array(input_csv, output_csv, output_json))
  })
}
