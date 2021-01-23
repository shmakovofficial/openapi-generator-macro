package skibcsit.openapi.generator.`macro`

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.{OpenAPI, Operation, PathItem}

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.jdk.CollectionConverters._
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
object SttpClientImplementation extends StaticAnnotation {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    println("Annottees: " + annottees)
    val traitDef: ClassDef = extractClassDef(c)(annottees)
    val pathString: String = extractPath(c)(c.prefix.tree.children)
    val openAPI: OpenAPI = new OpenAPIParser().readLocation(pathString, null, null).getOpenAPI
    val methods: List[DefDef] = extractDefs(c)(traitDef).filter(defDef => !defDef.name.toString.contains("init")).map(toEndpoint(openAPI)(c)(_))
    val classDef: ClassDef = fillTrait(c)(traitDef, methods)
    println("Generated result: " + classDef)
    c.Expr[Any](classDef)
  }

  private def extractClassDef(c: whitebox.Context)(annottees: Seq[c.Expr[Any]]): c.universe.ClassDef =
    annottees.head.asInstanceOf[c.Expr[Nothing]].tree.asInstanceOf[c.universe.ClassDef]

  private def extractPath(c: whitebox.Context)(annotationList: List[c.Tree]): String =
    String.valueOf(extractPathLiteral(c)(annotationList).value.value)

  private def extractPathLiteral(c: whitebox.Context)(annotationList: List[c.Tree]): c.universe.Literal =
    annotationList.tail.head match {
      case namedArg: c.universe.NamedArg => namedArg.rhs.asInstanceOf[c.universe.Literal]
      case tree: c.Tree => tree.asInstanceOf[c.universe.Literal]
    }

  private def extractDefs(c: whitebox.Context)(traitDef: c.universe.ClassDef): List[c.universe.DefDef] =
    traitDef.children.head.asInstanceOf[c.universe.Template].body.filter(_.isInstanceOf[c.universe.DefDef]).map(_.asInstanceOf[c.universe.DefDef])

  private def toEndpoint(openAPI: OpenAPI)(c: whitebox.Context)(defDef: c.universe.DefDef): c.universe.DefDef =
    c.universe.DefDef(c.universe.NoMods, defDef.name, defDef.tparams, defDef.vparamss, defDef.tpt, generateRhs(c)(openAPI)(defDef))

  private def generateRhs(c: whitebox.Context)(openAPI: OpenAPI)(defDef: c.universe.DefDef): c.Tree = {
    import c.universe._
    val returnType = defDef.tpt match {
      case appliedTypeTree: AppliedTypeTree => appliedTypeTree.args.last
      case ident: Ident => ident
    }
    val (path, method) = findOperation(openAPI, defDef.name.toString)
    val url = getBaseUrl(openAPI) + path
    val uri = Apply(Ident(TermName("Uri")), List(Apply(Select(Ident(TermName("URI")), TermName("create")), List(generatePath(c)(url)))))
    val request = Apply(Select(Ident(TermName("basicRequest")), TermName(method)), List(uri))
    val response = Apply(Select(request, TermName("response")), List(TypeApply(Ident(TermName("asJson")), List(returnType))))
    val body = Select(Apply(Select(response, TermName("send")), List.empty), TermName("body"))
    defDef.tpt match {
      case _: AppliedTypeTree => Apply(Select(Select(body, TermName("left")), TermName("map")), List(q"(value: ResponseError[io.circe.Error]) => value.getMessage"))
      case _ => Apply(Select(body, TermName("getOrElse")), List(Literal(Constant(null))))
    }
  }

  private def generatePath(c: whitebox.Context)(path: String): c.Tree =
    if (path.contains("{")) c.universe.Apply(c.universe.Select(c.universe.Apply(c.universe.Select(generatePath(c)(extractLeftPath(path)), c.universe.TermName("concat")), List(c.universe.Select(c.universe.Ident(c.universe.TermName(extractPathParamName(path))), c.universe.TermName("toString")))), c.universe.TermName("concat")), List(generatePath(c)(extractRightPath(path))))
    else c.universe.Literal(c.universe.Constant(path))

  private def extractLeftPath(path: String): String =
    path.substring(0, path.indexOf("{"))

  private def extractRightPath(path: String): String =
    path.substring(path.indexOf("}") + 1)

  private def extractPathParamName(path: String): String =
    path.substring(path.indexOf("{") + 1, path.indexOf("}"))

  private def findOperation(openAPI: OpenAPI, name: String): (String, String) = {
    openAPI.getPaths
      .entrySet().asScala
      .map(entry => (entry.getKey, getAllMethods(entry.getValue)))
      .flatMap(tuple1 => tuple1._2.map(tuple2 => (tuple1._1, tuple2._1, tuple2._2)))
      .filter(_._3.getOperationId.equals(name))
      .map(tuple => (tuple._1, tuple._2))
      .head
  }

  private def getAllMethods(pathItem: PathItem): Iterable[(String, Operation)] =
    List(("get", pathItem.getGet), ("put", pathItem.getPut), ("head", pathItem.getHead), ("post", pathItem.getPost), ("delete", pathItem.getDelete), ("patch", pathItem.getPatch), ("options", pathItem.getOptions), ("trace", pathItem.getTrace)).filter(_._2 != null)

  private def getBaseUrl(openAPI: OpenAPI): String =
    openAPI.getServers.asScala.head.getUrl

  def fillTrait(c: whitebox.Context)(oldClassDef: c.universe.ClassDef, defDefs: Iterable[c.universe.DefDef]): c.universe.ClassDef = {
    val names = defDefs.map(_.name).toSet
    val notDefs = oldClassDef.children.head.asInstanceOf[c.universe.Template].body.filter(!_.isInstanceOf[c.universe.DefDef])
    val anotherDefs = extractDefs(c)(oldClassDef).filter(defDef => !names.contains(defDef.name))
    val resultContent = notDefs.concat(anotherDefs).concat(defDefs)
    c.universe.ClassDef(oldClassDef.mods, oldClassDef.name, oldClassDef.tparams, c.universe.Template(extractParents(c)(oldClassDef), c.universe.noSelfType, resultContent))
  }

  def extractParents(c: whitebox.Context)(classDef: c.universe.ClassDef): List[c.Tree] =
    classDef.children.head.asInstanceOf[c.universe.Template].children.filter(_.isInstanceOf[c.universe.Select])

}
