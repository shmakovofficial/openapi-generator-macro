package skibcsit.openapi.generator.`macro`

import io.swagger.v3.oas.models.OpenAPI

import scala.reflect.macros.whitebox

object SttpGenerator {

  def generate(c: whitebox.Context)(openAPI: OpenAPI)(operationId: String): c.Tree = ???

}
