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
  //  Date: String,
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

  def reader(input_csv_filename: String): CsvReader[ReadResult[StockPrice]] = {
    // DOCS: https://nrinaudo.github.io/kantan.csv/rows_as_case_classes.html
    implicit lazy val StockPriceHeaderDecoder: HeaderDecoder[StockPrice] = HeaderDecoder.decoder(
      "Symbol","Series","Date",
      "Prev Close","Open Price","High Price","Low Price","Last Price","Close Price",
      "Average Price",
      "Total Traded Quantity","Turnover",
      "No. of Trades","Deliverable Qty","% Dly Qt to Traded Qty"
    )(StockPrice.apply)

    // DOCS: https://nrinaudo.github.io/kantan.csv/java8.html
    implicit lazy val StockPriceCellEncoder: CellDecoder[LocalDate] = {
      val format = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
      localDateDecoder(format)
    }

    val reader = new File(input_csv_filename).asCsvReader[StockPrice](rfc.withHeader)
    reader
  }

  def print( input_csv_filename: String ): Unit = {
    val reader = this.reader(input_csv_filename)
    this.print(reader)
  }
  def print( reader: CsvReader[ReadResult[StockPrice]] ): Unit = {
    reader.foreach((result: ReadResult[StockPrice]) => result match {
      case Right(stock) => println(stock)
      case Left(stock)  => println("---")
    })
  }

}
