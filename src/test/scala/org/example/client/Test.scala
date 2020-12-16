package org.example.client

import org.scalatest.funsuite.AnyFunSuite

class Test extends AnyFunSuite {
  test("test") {
    println(Client.getClient.getInventory())
  }
}
