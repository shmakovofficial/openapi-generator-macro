package skibcsit.openapi.generator.`macro`

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
object ClientImplementation extends StaticAnnotation {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val traitDef: ClassDef = extractTraitDef(c)(annottees)
    val pathString: String = extractPath(c)(c.prefix.tree.children)
    val annotationString: String = String.valueOf(extractAnnotationIdent(c)(c.prefix.tree.children).name)
    val serviceName: TypeName = traitDef.name
    val openAPI: OpenAPI = new OpenAPIParser().readLocation(pathString, null, null).getOpenAPI
    val methodsImpl: List[DefDef] = extractDefs(c)(traitDef).map(mapDef(c)(_))
    val emptyBlock: Block = q"new $serviceName {}".asInstanceOf[Block]
    val fullBlock: Block = fillBlock(c)(emptyBlock, methodsImpl)
    val defApply: DefDef = q"def apply(): $serviceName = $fullBlock".asInstanceOf[DefDef]
    val emptyModule: ModuleDef = q"object ${serviceName.toTermName} {}".asInstanceOf[ModuleDef]
    val fullModule: ModuleDef = fillModule(c)(emptyModule, defApply)
    val resultBlock: Block = Block(List(traitDef, annottees.tail.head.tree), Literal(Constant()))
    println(resultBlock)
    c.Expr[Any](resultBlock)
  }

  def fillModule(c: whitebox.Context)(oldModuleDef: c.universe.ModuleDef, defApply: c.universe.DefDef): c.universe.ModuleDef = {
    val oldTemplate: c.universe.Template = oldModuleDef.children.head.asInstanceOf[c.universe.Template]
    val newTemplate: c.universe.Template = c.universe.Template(oldTemplate.parents, oldTemplate.self, List(oldTemplate.body.head, defApply))
    c.universe.ModuleDef(oldModuleDef.mods, oldModuleDef.name, newTemplate)
  }

  def fillBlock(c: whitebox.Context)(block: c.universe.Block, defs: List[c.universe.Tree]): c.universe.Block = {
    val oldClassDef: c.universe.ClassDef = block.children.head.asInstanceOf[c.universe.ClassDef]
    val oldTemplate: c.universe.Template = oldClassDef.children.head.asInstanceOf[c.universe.Template]
    val newTemplate: c.universe.Template = c.universe.Template(oldTemplate.parents, oldTemplate.self, oldTemplate.children.last :: defs)
    val newClassDef: c.universe.ClassDef = c.universe.ClassDef(oldClassDef.mods, oldClassDef.name, oldClassDef.tparams, newTemplate)
    c.universe.Block(List(newClassDef), block.children.last)
  }

  def throwNotImplemented(c: whitebox.Context): c.Tree = {
    import c.universe.Quasiquote
    q"???"
  }

  def mapDef(c: whitebox.Context)(defDef: c.universe.DefDef): c.universe.DefDef =
    c.universe.DefDef(c.universe.Modifiers(addFlags(c)(defDef.mods.flags, c.universe.Flag.OVERRIDE)), defDef.name, defDef.tparams, defDef.vparamss, defDef.tpt, throwNotImplemented(c))

  //    c.universe.DefDef(defDef.mods, defDef.name, defDef.tparams, defDef.vparamss, defDef.tpt, throwNotImplemented(c))


  def addFlags(c: whitebox.Context)(flagSets: c.universe.FlagSet*): c.universe.FlagSet = {
    import c.universe._
    flagSets.reduce((set1: c.universe.FlagSet, set2: c.universe.FlagSet) => set1 | set2)
  }

  def extractDefs(c: whitebox.Context)(traitDef: c.universe.ClassDef): List[c.universe.DefDef] =
    traitDef.children.head.asInstanceOf[c.universe.Template].body.map(_.asInstanceOf[c.universe.DefDef])

  def extractParents(c: whitebox.Context)(traitDef: c.universe.ClassDef): List[c.Tree] =
    traitDef.children.head.asInstanceOf[c.universe.Template].parents

  def extractPath(c: whitebox.Context)(annotationList: List[c.Tree]): String =
    String.valueOf(extractPathLiteral(c)(annotationList).value.value)

  def extractPathLiteral(c: whitebox.Context)(annotationList: List[c.Tree]): c.universe.Literal =
    annotationList.tail.head match {
      case namedArg: c.universe.NamedArg => namedArg.rhs.asInstanceOf[c.universe.Literal]
      case tree: c.Tree => tree.asInstanceOf[c.universe.Literal]
    }

  def extractAnnotationIdent(c: whitebox.Context)(annotationList: List[c.Tree]): c.universe.Ident =
    annotationList.head.asInstanceOf[c.universe.Select]
      .qualifier.asInstanceOf[c.universe.New]
      .tpt.asInstanceOf[c.universe.Ident]

  def extractTraitDef(c: whitebox.Context)(annottees: Seq[c.Expr[Any]]): c.universe.ClassDef =
    annottees.head.asInstanceOf[c.Expr[Nothing]].tree.asInstanceOf[c.universe.ClassDef]
}