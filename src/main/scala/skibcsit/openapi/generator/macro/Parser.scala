package skibcsit.openapi.generator.`macro`
import io.swagger.models.{Operation, Path}

import scala.jdk.CollectionConverters._
import scala.reflect.macros.whitebox

object Parser {
  def getIds(swagger: io.swagger.models.Swagger): Iterable[String] = swagger
    .getPaths.values()
    .asScala
    .flatMap((path: Path) => path.getOperations.asScala)
    .map((operation: Operation) => operation.getOperationId)

  def getFoundDefs(c: whitebox.Context)(oldDefs: List[c.universe.DefDef], ids: Iterable[String]): (List[c.universe.DefDef], List[c.universe.DefDef]) =
    oldDefs.partition(defDef => ids.exists(_.equals(defDef.name.toString)))

  def getArgs(c: whitebox.Context)(args: List[c.universe.Tree]): Args = {
    import c.universe._
    val values = args
      .map {
        case namedArg: NamedArg => namedArg.rhs
        case unnamedArg => unnamedArg
      }
      .zipWithIndex
      .map {
        case (arg, 0) => arg.asInstanceOf[Literal].value.value.asInstanceOf[String]
      }
    Args(values.head)
  }
}
