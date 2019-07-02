package module_1

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import kantan.csv.java8.localDateDecoder
import kantan.csv.ops._
import kantan.csv.{CellDecoder, HeaderDecoder, ReadResult, rfc, _}

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

  // DOCS: https://nrinaudo.github.io/kantan.csv/rows_as_case_classes.html
  implicit lazy val StockPriceHeaderDecoder: HeaderDecoder[StockPrice] = HeaderDecoder.decoder(
    "Symbol", "Series", "Date",
    "Prev Close", "Open Price", "High Price", "Low Price", "Last Price", "Close Price",
    "Average Price",
    "Total Traded Quantity", "Turnover",
    "No. of Trades", "Deliverable Qty", "% Dly Qt to Traded Qty"
  )(StockPrice.apply)

  // DOCS: https://nrinaudo.github.io/kantan.csv/java8.html
  implicit lazy val StockPriceCellEncoder: CellDecoder[LocalDate] = {
    val format = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
    localDateDecoder(format)
  }

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
