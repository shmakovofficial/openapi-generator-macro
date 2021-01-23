package org.example.petstore

import sttp.client._
import sttp.model._
import java.net.URI
import sttp.client.circe.asJson
import io.circe.generic.auto._
import skibcsit.openapi.generator.`macro`.SttpImplementation

@SttpImplementation("https://petstore.swagger.io/v2/swagger.json")
class Service(backend: SttpBackend[Identity, Nothing, NothingT]) {
  implicit val implicitBackend: SttpBackend[Identity, Nothing, NothingT] = backend
  def logoutUser(): String
  def createUser(body: User): String
  def deleteOrder(orderId: java.lang.Long): Either[String, String]
  def getUserByName(username: String): Either[String, User]
  def createUsersWithListInput(body: Array[User]): String
  def findPetsByTags(tags: Array[String]): Either[String, Array[Pet]]
  def getInventory(): String
  def updatePetWithForm(petId: java.lang.Long, body: (String, String) = null): Either[String, String]
  def findPetsByStatus(status: Array[String]): Either[String, Array[Pet]]
  def placeOrder(body: Order): Either[String, Order]
  def deletePet(api_key: String = null, petId: java.lang.Long): Either[String, String]
  def addPet(body: Pet): Either[String, String]
  def getOrderById(orderId: java.lang.Long): Either[String, Order]
  def createUsersWithArrayInput(body: Array[User]): String
  def uploadFile(petId: java.lang.Long, body: (String, Array[Byte]) = null): ApiResponse
  def loginUser(username: String, password: String): Either[String, String]
  def deleteUser(username: String): Either[String, String]
  def updateUser(username: String, body: User): Either[String, String]
  def updatePet(body: Pet): Either[String, String]
  def getPetById(petId: java.lang.Long): Either[String, Pet]
}