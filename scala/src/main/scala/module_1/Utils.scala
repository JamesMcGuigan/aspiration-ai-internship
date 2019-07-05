package module_1

import scala.reflect.runtime.universe._

// DOCS: https://stackoverflow.com/questions/51854038/scala-how-to-get-attribute-names-of-a-case-class
object Utils {
  // BUGFIX: for some reason typeOf[T].members.collect() iterates backwards over the field accessors
  def classFieldNames[T: TypeTag]: Array[String] = {
    typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor         => m.name.toString
      case m: MethodSymbol if m.isGetter && m.isPublic => m.name.toString
    }.toArray.reverse
  }

  // DOCS: https://stackoverflow.com/questions/14722860/convert-a-scala-list-to-a-tuple
  def listToTuple[A <: Object](list:Seq[A]):Product = {
    val classInstance = Class.forName("scala.Tuple" + list.size)
    classInstance.getConstructors.apply(0).newInstance(list:_*).asInstanceOf[Product]
  }
}
