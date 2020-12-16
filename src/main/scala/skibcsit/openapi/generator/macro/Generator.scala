package skibcsit.openapi.generator.`macro`

import io.swagger.models.Swagger

import scala.reflect.macros.whitebox

object Generator {

  def generateParams(c: whitebox.Context)(oldDef: c.universe.DefDef, swagger: Swagger): List[List[c.universe.ValDef]] = {
    import c.universe._
    List(List(ValDef(NoMods, TermName("petId"), TypeTree(typeOf[Int]), EmptyTree)))
  }

  def generateRhs(c: whitebox.Context)(oldDef: c.universe.DefDef, swagger: Swagger): c.universe.Tree = {
    import c.universe._
    q"""{
        import io.circe.generic.auto._
        sttp.client.basicRequest
          .response(sttp.client.circe.asJson[Pet])
          .get(sttp.model.Uri(${swagger.getSchemes.get(0).toValue}, ${swagger.getHost})
            .path(${swagger.getBasePath.replaceAll("/", "")}, "pet", petId.toString))
    }"""
  }

  def generateClasses(c: whitebox.Context)(ids: Iterable[String], swagger: Swagger): List[c.universe.ClassDef] = {
    import c.universe._
    List(q"case class Pet(id: Int, name: String)".asInstanceOf[ClassDef])
  }

  def generateTemplate(c: whitebox.Context)(moduleDef: c.universe.ModuleDef, swagger: Swagger): c.universe.Template = {
    import c.universe._
    val oldMethods = moduleDef.children.head.children.filter(_.isInstanceOf[DefDef]).map(_.asInstanceOf[DefDef])
    val ids = Parser.getIds(swagger)
    val (foundMethods, notFoundMethods) = Parser.getFoundDefs(c)(oldMethods, ids)
    val foundIds = foundMethods.map((defDef: c.universe.DefDef) => defDef.name.toString)
    val newFoundMethods = foundMethods.map(defDef => DefDef(defDef.mods, defDef.name, defDef.tparams, generateParams(c)(defDef, swagger), TypeTree(), generateRhs(c)(defDef, swagger)))
    val newMethods = notFoundMethods.concat(newFoundMethods)
    val newClasses = generateClasses(c)(foundIds, swagger)
    Template(moduleDef.impl.parents, moduleDef.impl.self, newMethods.concat(newClasses))
  }
}

