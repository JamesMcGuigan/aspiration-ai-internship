package module_1

import java.lang.reflect.Field
import java.time.LocalDate


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
    Percentage_Dly_Qt_to_Traded_Qty: Float,
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

  def unapply(stockPrice: StockPrice): (String, String, LocalDate, Float, Float, Float, Float, Float, Float, Float, Int, Float, Int, Int, Float, Int, Int) = (
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

