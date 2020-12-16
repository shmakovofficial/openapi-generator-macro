package skibcsit.openapi.generator.`macro`

import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class Http4sImplementation(path: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ClientImplementation.impl
}
