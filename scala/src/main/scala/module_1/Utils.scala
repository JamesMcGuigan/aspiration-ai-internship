package module_1
import scala.reflect.runtime.universe._

// DOCS: https://stackoverflow.com/questions/51854038/scala-how-to-get-attribute-names-of-a-case-class
object Utils {
  // BUGFIX: for some reason typeOf[T].members.collect() iterates backwards over the field accessors
  def caseClassFieldNames[T: TypeTag]: List[String] = typeOf[T].members.collect {
    case m: MethodSymbol if m.isCaseAccessor => m.name.toString
  }.map(_.capitalize).toList.reverse
}
