package org.example.petstore

import skibcsit.openapi.generator.`macro`.SttpImplementation

@SttpImplementation("https://petstore.swagger.io/v2/swagger.json")
trait Service {
  def placeOrder(body: Order): Either[String, Order]

  def uploadFile(petId: Long, body: Option[(String, Array[Byte])]): ApiResponse

  def findPetsByTags(tags: Array[String]): Either[String, Array[Pet]]

  def createUsersWithListInput(body: Array[User]): String

  def updateUser(username: String, body: User): Either[String, String]

  def createUser(body: User): String

  def deleteOrder(orderId: Long): Either[String, String]

  def findPetsByStatus(status: Array[String]): Either[String, Array[Pet]]

  def createUsersWithArrayInput(body: Array[User]): String

  def updatePet(body: Pet): Either[String, String]

  def getOrderById(orderId: Long): Either[String, Order]

  def logoutUser(): String

  def getInventory(): String

  def deleteUser(username: String): Either[String, String]

  def updatePetWithForm(petId: Long, body: Option[(String, String)]): Either[String, String]

  def loginUser(username: String, password: String): Either[String, String]

  def addPet(body: Pet): Either[String, String]

  def getUserByName(username: String): Either[String, User]

  def getPetById(petId: Long): Either[String, Pet]

  def deletePet(api_key: Option[String], petId: Long): Either[String, String]
}
