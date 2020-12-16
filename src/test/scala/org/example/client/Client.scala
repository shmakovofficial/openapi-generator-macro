package org.example.client

import com.softwaremill.macwire.wire
import org.example.petstore.Service

object Client {
  val service: Service = wire[Service]
}
