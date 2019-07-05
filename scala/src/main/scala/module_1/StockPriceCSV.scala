package module_1

import java.io.File
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import kantan.csv._
import kantan.csv.java8.{localDateDecoder, localDateEncoder}
import kantan.csv.ops._


class StockPrice(
  val Symbol: String,
  val Series: String,
  val Date: LocalDate,
  val Prev_Close: Float,
  val Open_Price: Float,
  val High_Price: Float,
  val Low_Price: Float,
  val Last_Price: Float,
  val Close_Price: Float,
  val Average_Price: Float,
  val Total_Traded_Quantity: Int,
  val Turnover: Float,
  val No_of_Trades: Int,
  val Deliverable_Qty: Int,
  val Percentage_Dly_Qt_to_Traded_Qty: Float,
) {
  val Year: Int = Date.getYear
  val Month: Int = Date.getMonthValue

  // Implement case class style toString()
  // DOCS: https://stackoverflow.com/questions/2016499/how-to-create-a-decent-tostring-method-in-scala-using-reflection/2017192
  override def toString: String = {
    val values = getClass.getDeclaredFields.map {
      field:Field => field.get(this).toString
    }
    s"${getClass.getSimpleName}(${values.mkString(",")})"
  }
}
object StockPrice {
  // NOTE: Input and Output CSV formats are different to match Python and Typescript implementation specs
  val fieldNames: Array[String] = Utils.classFieldNames[StockPrice]

  def apply(
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
    Percentage_Dly_Qt_to_Traded_Qty: Float
  ): StockPrice = {
    new StockPrice(
      Symbol,
      Series,
      Date,
      Prev_Close,
      Open_Price,
      High_Price,
      Low_Price,
      Last_Price,
      Close_Price,
      Average_Price,
      Total_Traded_Quantity,
      Turnover,
      No_of_Trades,
      Deliverable_Qty,
      Percentage_Dly_Qt_to_Traded_Qty
    )
  }

  def unapply(stockPrice: StockPrice) = Option(
    stockPrice.Symbol,
    stockPrice.Series,
    stockPrice.Date,
    stockPrice.Prev_Close,
    stockPrice.Open_Price,
    stockPrice.High_Price,
    stockPrice.Low_Price,
    stockPrice.Last_Price,
    stockPrice.Close_Price,
    stockPrice.Average_Price,
    stockPrice.Total_Traded_Quantity,
    stockPrice.Turnover,
    stockPrice.No_of_Trades,
    stockPrice.Deliverable_Qty,
    stockPrice.Percentage_Dly_Qt_to_Traded_Qty,
    stockPrice.Year,
    stockPrice.Month,
  )
}


object StockPriceCSV {

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
  //    HeaderEncoder.caseEncoder( StockPrice.fieldNames:_* )(StockPrice.unapply)

  // TODO: pass in field names as spat:_* rather than hardcoding strings - keeps complaining about type signatures
  // BUGFIX: No implicits found for parameter evidence$3: HeaderEncoder[StockPrice]
  // BUG:    These values don't actually output to the CSV file
  implicit lazy val StockPriceHeaderEncoder: HeaderEncoder[StockPrice] = HeaderEncoder.caseEncoder(
    "Symbol","Series","Date","Prev_Close","Open_Price","High_Price","Low_Price",
    "Last_Price","Close_Price","Average_Price","Total_Traded_Quantity",
    "Turnover","No_of_Trades","Deliverable_Qty","Percentage_Dly_Qt_to_Traded_Qty",
    "Year", "Month"
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

    // BUG: modifying StockPrice.fieldNames doesn't seem to change headers when implicit HeaderEncoder is defined
    // BUG: modifying implicit HeaderEncoder doesn't seem to change header
    file.asCsvWriter[StockPrice]( rfc.withHeader(StockPrice.fieldNames:_*) )
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
