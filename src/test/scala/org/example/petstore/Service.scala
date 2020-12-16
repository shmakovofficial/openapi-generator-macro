package org.example.petstore

import skibcsit.openapi.generator.`macro`.SttpImplementation

@SttpImplementation("https://petstore.swagger.io/v2/swagger.json")
abstract class Service {
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

object Service extends Service {
  override def placeOrder(body: Order): Either[String, Order] = $qmark$qmark$qmark;

  override def uploadFile(petId: Long, body: Option[scala.Tuple2[String, Array[Byte]]]): ApiResponse = $qmark$qmark$qmark;

  override def findPetsByTags(tags: Array[String]): Either[String, Array[Pet]] = $qmark$qmark$qmark;

  override def createUsersWithListInput(body: Array[User]): String = $qmark$qmark$qmark;

  override def updateUser(username: String, body: User): Either[String, String] = $qmark$qmark$qmark;

  override def createUser(body: User): String = $qmark$qmark$qmark;

  override def deleteOrder(orderId: Long): Either[String, String] = $qmark$qmark$qmark;

  override def findPetsByStatus(status: Array[String]): Either[String, Array[Pet]] = $qmark$qmark$qmark;

  override def createUsersWithArrayInput(body: Array[User]): String = $qmark$qmark$qmark;

  override def updatePet(body: Pet): Either[String, String] = $qmark$qmark$qmark;

  override def getOrderById(orderId: Long): Either[String, Order] = $qmark$qmark$qmark;

  override def logoutUser(): String = $qmark$qmark$qmark;

  override def getInventory(): String = $qmark$qmark$qmark;

  override def deleteUser(username: String): Either[String, String] = $qmark$qmark$qmark;

  override def updatePetWithForm(petId: Long, body: Option[scala.Tuple2[String, String]]): Either[String, String] = $qmark$qmark$qmark;

  override def loginUser(username: String, password: String): Either[String, String] = $qmark$qmark$qmark;

  override def addPet(body: Pet): Either[String, String] = $qmark$qmark$qmark;

  override def getUserByName(username: String): Either[String, User] = $qmark$qmark$qmark;

  override def getPetById(petId: Long): Either[String, Pet] = $qmark$qmark$qmark;

  override def deletePet(api_key: Option[String], petId: Long): Either[String, String] = $qmark$qmark$qmark
};
