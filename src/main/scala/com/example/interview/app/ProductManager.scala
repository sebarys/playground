package com.example.interview.app

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class ProductManager(externalServiceA: ExternalServiceA,
                     externalServiceB: ExternalServiceB,
                     databaseCapability: DatabaseCapability,
                     eventRegistry: EventRegistry) {

  def orderProduct(user: UUID, product: Product): Future[UUID] = {
    externalServiceA.checkProductAvailability()
    externalServiceB.checkUser(user)
    val orderId = Await.result(databaseCapability.createOrder(user, product), 60.seconds)
    eventRegistry.createEvent(EventRegistry.Event(user, product.id, "ORDER_CREATED", orderId))
    return Future.successful(orderId)
  }

}

object ProductManager {
  def apply(externalServiceA: ExternalServiceA,
            externalServiceB: ExternalServiceB,
            databaseCapability: DatabaseCapability,
            eventRegistry: EventRegistry): ProductManager = {
    new ProductManager(externalServiceA, externalServiceB, databaseCapability, eventRegistry)
  }
}
