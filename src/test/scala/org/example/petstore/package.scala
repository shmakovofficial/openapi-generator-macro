package org.example

package object petstore {
  case class ApiResponse(code: Option[Integer], `type`: Option[String], message: Option[String])
  case class Category(id: Option[Long], name: Option[String])
  case class Pet(id: Option[Long], category: Option[Category], name: String, photoUrls: Array[String], tags: Option[Array[Tag]], status: Option[String])
  case class Tag(id: Option[Long], name: Option[String])
  case class Order(id: Option[Long], petId: Option[Long], quantity: Option[Integer], shipDate: Option[String], status: Option[String], complete: Option[Boolean])
  case class User(id: Option[Long], username: Option[String], firstName: Option[String], lastName: Option[String], email: Option[String], password: Option[String], phone: Option[String], userStatus: Option[Integer])
}