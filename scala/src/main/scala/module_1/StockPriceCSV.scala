package module_1

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import kantan.csv._
import kantan.csv.java8.{localDateDecoder, localDateEncoder}
import kantan.csv.ops._


final case class StockPrice(
  Symbol: String,
  Series: String,
  Date: LocalDate,
  Prev_Close: Float,
  Open_Price: Float,
  High_Price: Float,
  Low_Price: Float,
  Last_Price: Float,
  Close_Price: Float,
  Average_Price: Float,
  Total_Traded_Quantity: Int,
  Turnover: Float,
  No_of_Trades: Int,
  Deliverable_Qty: Int,
  Percentage_Dly_Qt_to_Traded_Qty: Float,
)

object StockPriceCSV {

  // NOTE: Input and Output CSV formats are different to match Python and Typescript implementation specs
  val stockPriceFieldNames: Seq[String] = Utils.caseClassFieldNames[StockPrice]

  // DOCS: https://nrinaudo.github.io/kantan.csv/rows_as_case_classes.html
  implicit lazy val StockPriceHeaderDecoder: HeaderDecoder[StockPrice] = HeaderDecoder.decoder(
    "Symbol", "Series", "Date",
    "Prev Close", "Open Price", "High Price", "Low Price", "Last Price", "Close Price",
    "Average Price",
    "Total Traded Quantity", "Turnover",
    "No. of Trades", "Deliverable Qty", "% Dly Qt to Traded Qty"
  )(StockPrice.apply)

  //// BUG: https://stackoverflow.com/questions/6675419/in-scala-why-do-i-get-this-polymorphic-expression-cannot-be-instantiated-to-ex
  //  implicit lazy val StockPriceHeaderEncoder: HeaderEncoder[StockPrice] =
  //    HeaderEncoder.caseEncoder( stockPriceFieldNames:_* )(StockPrice.unapply)

  // BUGFIX: No implicits found for parameter evidence$3: HeaderEncoder[StockPrice]
  // BUG:    These values don't actually output to the CSV file
  implicit lazy val StockPriceHeaderEncoder: HeaderEncoder[StockPrice] = HeaderEncoder.caseEncoder(
    "Symbol","Series","Date","Prev_Close","Open_Price","High_Price","Low_Price","Last_Price","Close_Price","Average_Price","Total_Traded_Quantity","Turnover","No_of_Trades","Deliverable_Qty","Percentage_Dly_Qt_to_Traded_Qty",
  )(StockPrice.unapply)

  // DOCS: https://nrinaudo.github.io/kantan.csv/java8.html
  implicit lazy val StockPriceCellDecoder: CellDecoder[LocalDate] =
    localDateDecoder( DateTimeFormatter.ofPattern("dd-MMM-yyyy") )

  implicit lazy val StockPriceCellEncoder: CellEncoder[LocalDate] =
    localDateEncoder(DateTimeFormatter.ISO_LOCAL_DATE)




  def reader(input_csv_filename: String): CsvReader[ReadResult[StockPrice]] = {
    val reader = new File(input_csv_filename).asCsvReader[StockPrice](rfc.withHeader)
    reader
  }

  def read(input_csv_filename: String): List[StockPrice] = {
    // DOCS: https://stackoverflow.com/questions/26576530/how-to-split-a-listeithera-b
    // NOTE: This will filter any error rows
    val list = new File(input_csv_filename)
      .readCsv[List, StockPrice](rfc.withHeader) // => List[ ReadResult[StockPrice] ]
      .flatMap(_.right.toOption) // Either[ Left[ReadError],  Right[StockPrice] ] => StockPrice
    list
  }

  def write(output_csv_filename: String, data: List[StockPrice]): Unit = {
    // DOCS: https://nrinaudo.github.io/kantan.csv/case_classes_as_rows.html
    // val csvText = data.asCsv(rfc.withHeader)
    val file = new File(output_csv_filename)
    file.createNewFile()

    // BUG: modifying stockPriceFieldNames doesn't seem to change headers when implicit HeaderEncoder is defined
    // BUG: modifying implicit HeaderEncoder doesn't seem to change header
    file.asCsvWriter[StockPrice]( rfc.withHeader(stockPriceFieldNames:_*) )
        .write(data)
        .close()
  }

  def print(input_csv_filename: String): Unit = {
    val reader = this.reader(input_csv_filename)
    this.print(reader)
  }
  def print(reader: CsvReader[ReadResult[StockPrice]]): Unit = {
    reader.foreach((result: ReadResult[StockPrice]) => result match {
      case Right(stockPrice) => println(stockPrice)
      case Left(_)  => println("---")
    })
  }
  def print(data: Seq[StockPrice]): Unit = {
    data.foreach(println)
  }

}
