package skibcsit.openapi.generator.`macro`

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
object ClientImplementation extends StaticAnnotation {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    println("Annottees: " + annottees)
    import c.universe._
    val traitDef: ClassDef = extractTraitDef(c)(annottees)
    val pathString: String = extractPath(c)(c.prefix.tree.children)
    val annotationString: String = String.valueOf(extractAnnotationIdent(c)(c.prefix.tree.children).name)
    val serviceName: TypeName = traitDef.name
    val openAPI: OpenAPI = new OpenAPIParser().readLocation(pathString, null, null).getOpenAPI
    val endpoints: List[DefDef] = extractDefs(c)(traitDef).map(toEndpoint(openAPI)(c)(_))
    val endpointsImpl: List[DefDef] = endpoints.map((defDef: DefDef) => annotationString match {
      case "Http4sImplementation" => http4sImplementation(openAPI)(c)(defDef)
      case _ => sttpImplementation(openAPI)(c)(defDef)
    })
    val emptyBlock: Block = q"new $serviceName {}".asInstanceOf[Block]
    val fullBlock: Block = fillBlock(c)(emptyBlock, endpointsImpl)
    val implVal: ValDef = q"implicit val impl: $serviceName = $fullBlock".asInstanceOf[ValDef]
    val emptyModule: ModuleDef = q"object ${serviceName.toTermName} {}".asInstanceOf[ModuleDef]
    val fullModule: ModuleDef = fillModule(c)(emptyModule, implVal)
    val resultBlock: Block = Block(List(traitDef, fullModule), Literal(Constant()))
    println("resultBlock: " + resultBlock)
    c.Expr[Any](resultBlock)
  }

  private def extractTraitDef(c: whitebox.Context)(annottees: Seq[c.Expr[Any]]): c.universe.ClassDef =
    annottees.head.asInstanceOf[c.Expr[Nothing]].tree.asInstanceOf[c.universe.ClassDef]

  private def extractPath(c: whitebox.Context)(annotationList: List[c.Tree]): String =
    String.valueOf(extractPathLiteral(c)(annotationList).value.value)

  private def extractPathLiteral(c: whitebox.Context)(annotationList: List[c.Tree]): c.universe.Literal =
    annotationList.tail.head match {
      case namedArg: c.universe.NamedArg => namedArg.rhs.asInstanceOf[c.universe.Literal]
      case tree: c.Tree => tree.asInstanceOf[c.universe.Literal]
    }

  private def extractAnnotationIdent(c: whitebox.Context)(annotationList: List[c.Tree]): c.universe.Ident =
    annotationList.head.asInstanceOf[c.universe.Select]
      .qualifier.asInstanceOf[c.universe.New]
      .tpt.asInstanceOf[c.universe.Ident]

  private def extractDefs(c: whitebox.Context)(traitDef: c.universe.ClassDef): List[c.universe.DefDef] =
    traitDef.children.head.asInstanceOf[c.universe.Template].body.map(_.asInstanceOf[c.universe.DefDef])

  private def toEndpoint(openAPI: OpenAPI)(c: whitebox.Context)(defDef: c.universe.DefDef): c.universe.DefDef =
    c.universe.DefDef(c.universe.Modifiers(c.universe.Flag.OVERRIDE), defDef.name, defDef.tparams, defDef.vparamss, defDef.tpt, generateRhs(c)(openAPI)(defDef.name.toString))

  private def generateRhs(c: whitebox.Context)(openAPI: OpenAPI)(name: String): c.Tree = throwNotImplemented(c)

  private def throwNotImplemented(c: whitebox.Context): c.Tree = {
    import c.universe.Quasiquote
    q"???"
  }

  private def http4sImplementation(openAPI: OpenAPI)(c: whitebox.Context)(defDef: c.universe.DefDef): c.universe.DefDef = defDef

  private def sttpImplementation(openAPI: OpenAPI)(c: whitebox.Context)(defDef: c.universe.DefDef): c.universe.DefDef = defDef

  private def fillBlock(c: whitebox.Context)(block: c.universe.Block, defs: List[c.universe.Tree]): c.universe.Block = {
    val oldClassDef: c.universe.ClassDef = block.children.head.asInstanceOf[c.universe.ClassDef]
    val oldTemplate: c.universe.Template = oldClassDef.children.head.asInstanceOf[c.universe.Template]
    val newTemplate: c.universe.Template = c.universe.Template(oldTemplate.parents, oldTemplate.self, oldTemplate.children.last :: defs)
    val newClassDef: c.universe.ClassDef = c.universe.ClassDef(oldClassDef.mods, oldClassDef.name, oldClassDef.tparams, newTemplate)
    c.universe.Block(List(newClassDef), block.children.last)
  }

  private def fillModule(c: whitebox.Context)(oldModuleDef: c.universe.ModuleDef, valDef: c.universe.ValDef): c.universe.ModuleDef = {
    val oldTemplate: c.universe.Template = oldModuleDef.children.head.asInstanceOf[c.universe.Template]
    val newTemplate: c.universe.Template = c.universe.Template(oldTemplate.parents, oldTemplate.self, List(oldTemplate.body.head, valDef))
    c.universe.ModuleDef(oldModuleDef.mods, oldModuleDef.name, newTemplate)
  }

}