package module_1

import java.lang.reflect.Field
import java.time.LocalDate


class StockPrice(
  val Symbol: String,
  val Series: String,
  val Date: LocalDate,
  val Prev_Close: Double,
  val Open_Price: Double,
  val High_Price: Double,
  val Low_Price: Double,
  val Last_Price: Double,
  val Close_Price: Double,
  val Average_Price: Double,
  val Total_Traded_Quantity: Int,
  val Turnover: Double,
  val No_of_Trades: Int,
  val Deliverable_Qty: Int,
  val Percentage_Dly_Qt_to_Traded_Qty: Double,
) {
  val Year:  Int = Date.getYear
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
  val fieldNames: Array[String] = Utils.classFieldNames[StockPrice]

  def apply(
    Symbol: String,
    Series: String,
    Date: LocalDate,
    Prev_Close: Double,
    Open_Price: Double,
    High_Price: Double,
    Low_Price: Double,
    Last_Price: Double,
    Close_Price: Double,
    Average_Price: Double,
    Total_Traded_Quantity: Int,
    Turnover: Double,
    No_of_Trades: Int,
    Deliverable_Qty: Int,
    Percentage_Dly_Qt_to_Traded_Qty: Double
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

  def apply(
    Symbol: String,
    Series: String,
    Date: LocalDate,
    Prev_Close: Double,
    Open_Price: Double,
    High_Price: Double,
    Low_Price: Double,
    Last_Price: Double,
    Close_Price: Double,
    Average_Price: Double,
    Total_Traded_Quantity: Int,
    Turnover: Double,
    No_of_Trades: Int,
    Deliverable_Qty: Int,
    Percentage_Dly_Qt_to_Traded_Qty: Double,
    Year: Int,
    Month: Int,
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

  def unapply(stockPrice: StockPrice): (String, String, LocalDate, Double, Double, Double, Double, Double, Double, Double, Int, Double, Int, Int, Double, Int, Int) = (
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

