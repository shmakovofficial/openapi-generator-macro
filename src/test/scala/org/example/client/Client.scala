package org.example.client

import org.example.petstore.Service

object Client {
  def getClient(implicit service: Service): Service = service
}
