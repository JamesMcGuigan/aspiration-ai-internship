package module_1

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import argonaut.Argonaut._
import argonaut.PrettyParams
import kantan.csv._
import kantan.csv.java8.{localDateDecoder, localDateEncoder}
import kantan.csv.ops._

import scala.collection.immutable.Map

object StockPriceCSV {
  // NOTE: Input and Output CSV formats are different to match Python and Typescript implementation specs

  // DOCS: https://nrinaudo.github.io/kantan.csv/java8.html
  implicit lazy val StockPriceCellDecoder: CellDecoder[LocalDate] =
    localDateDecoder( DateTimeFormatter.ofPattern("dd-MMM-yyyy") )

  implicit lazy val StockPriceCellEncoder: CellEncoder[LocalDate] =
    localDateEncoder(DateTimeFormatter.ISO_LOCAL_DATE)


  //// DOCS: https://nrinaudo.github.io/kantan.csv/rows_as_case_classes.html
  implicit lazy val headerDecoder: HeaderDecoder[StockPrice] = HeaderDecoder.decoder(
    "Symbol", "Series", "Date",
    "Prev Close", "Open Price", "High Price", "Low Price", "Last Price", "Close Price",
    "Average Price",
    "Total Traded Quantity", "Turnover",
    "No. of Trades", "Deliverable Qty", "% Dly Qt to Traded Qty"
  )(StockPrice.apply)

  //// BROKEN REFACTOR
  //// BUG: type mismatch;
  //// [error]  found   : (String, String, java.time.LocalDate, Float, Float, Float, Float, Float, Float, Float, Int, Float, Int, Int, Float) => module_1.StockPrice
  //// [error]  required: ? => module_1.StockPrice
  //// [error]     HeaderDecoder.decoder( StockPrice.inputFieldNames:_* )( StockPrice.apply _ )
  //  implicit lazy val headerDecoder: HeaderDecoder[StockPrice] =
  //    HeaderDecoder.decoder( StockPrice.inputFieldNames:_* )( StockPrice.apply _ )


  implicit lazy val headerEncoder: HeaderEncoder[StockPrice] = new HeaderEncoder[StockPrice] {
    override def rowEncoder: RowEncoder[StockPrice] = RowEncoder.ordered(StockPrice.unapply _)
    override val header = Some(StockPrice.fieldNames)
  }

  //// OLD IMPLEMENTATION
  //// DONE:   pass in field names as spat:_* rather than hardcoding strings - keeps complaining about type signatures
  //// BUG:    These values don't actually output to the CSV file
  //  implicit lazy val StockPriceHeaderEncoder: HeaderEncoder[StockPrice] = HeaderEncoder.caseEncoder(
  //    "Symbol","Series","Date","Prev_Close","Open_Price","High_Price","Low_Price",
  //    "Last_Price","Close_Price","Average_Price","Total_Traded_Quantity",
  //    "Turnover","No_of_Trades","Deliverable_Qty","Percentage_Dly_Qt_to_Traded_Qty",
  //    "Year", "Month"
  //  )(StockPrice.unapply)


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

  def write_csv(output_csv_filename: String, data: List[StockPrice]): Unit = {
    // DOCS: https://nrinaudo.github.io/kantan.csv/case_classes_as_rows.html
    // val csvText = data.asCsv(rfc.withHeader)
    val file = new File(output_csv_filename)
    file.createNewFile()

    // BUG: modifying StockPrice.fieldNames doesn't seem to change headers when implicit HeaderEncoder is defined
    // BUG: modifying implicit HeaderEncoder doesn't seem to change header
    file.asCsvWriter[StockPrice]( rfc.withHeader(StockPrice.fieldNames:_*) )
        .write(data)
        .close()

    println("Wrote: " + file.getAbsolutePath)
  }

  val jsonPrettyParams = PrettyParams(
    indent = "    ",
    lbraceLeft = "",
    lbraceRight = "\n",
    rbraceLeft = "\n",
    rbraceRight = "",
    lbracketLeft = "",
    lbracketRight = "\n",
    rbracketLeft = "\n",
    rbracketRight = "",
    lrbracketsEmpty = "",
    arrayCommaLeft = "",
    arrayCommaRight = "\n",
    objectCommaLeft = "",
    objectCommaRight = "\n",
    colonLeft = "",
    colonRight = " ",
    preserveOrder = true,
    dropNullKeys = false,
  )
  
  def write_json( output_json_filename: String, data: Map[String, Map[String, Double]] ): Unit = {
    // TODO: figure out how to get encode nested types: Map[String, Map[String, Either[Double, Map[String, Double]]]]
    val json_string: String = data.asJson.pretty( jsonPrettyParams )

    reflect.io.File(output_json_filename).writeAll(json_string)
    println("Wrote: " + output_json_filename)
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
