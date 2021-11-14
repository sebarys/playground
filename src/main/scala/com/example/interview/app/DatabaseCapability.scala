package com.example.interview.app

import java.util.UUID
import scala.concurrent.Future

class DatabaseCapability {
  def createOrder(user: UUID, product: Product): Future[UUID] = {
    Future.successful(UUID.randomUUID())
  }
}

object DatabaseCapability {
  def apply(): DatabaseCapability = new DatabaseCapability
}
