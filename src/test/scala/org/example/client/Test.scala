package org.example.client

import org.example.petstore.{Pet, Service}
import org.scalatest.funsuite.AnyFunSuite
import sttp.client._
import sttp.model.Uri

class Test extends AnyFunSuite {
  test("getPetById") {
    val petId = 5
    val service = new Service(HttpURLConnectionBackend())
    val generatedPet = service.getPetById(petId)
    val expectedPet = manualGetPetById(petId)
    (generatedPet, expectedPet) match {
      case (Left(generatedLeft), Left(expectedLeft)) => assert(generatedLeft.equals(expectedLeft))
      case (Right(generatedRight), Right(expectedRight)) => assert(generatedRight.name.equals(expectedRight.name))
      case _ => fail("Different results")
    }
  }

  def manualGetPetById(petId: Long): Either[String, Pet] = {
    import io.circe.generic.auto._
    import sttp.client.circe._
    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
    basicRequest.get(Uri("https", "petstore.swagger.io").path("v2", "pet", petId.toString)).response(asJson[Pet]).send().body.left.map((value: ResponseError[io.circe.Error]) => value.getMessage)
  }
}
