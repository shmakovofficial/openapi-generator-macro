package org.example

package object petstore {
  case class ApiResponse(code: java.lang.Integer = null, `type`: String = null, message: String = null)
  case class Category(id: java.lang.Long = null, name: String = null)
  case class Pet(id: java.lang.Long = null, category: Category = null, name: String, photoUrls: Array[String], tags: Array[Tag] = null, status: String = null)
  case class Tag(id: java.lang.Long = null, name: String = null)
  case class Order(id: java.lang.Long = null, petId: java.lang.Long = null, quantity: java.lang.Integer = null, shipDate: String = null, status: String = null, complete: java.lang.Boolean = null)
  case class User(id: java.lang.Long = null, username: String = null, firstName: String = null, lastName: String = null, email: String = null, password: String = null, phone: String = null, userStatus: java.lang.Integer = null)
}