package com.example.interview.app

import akka.Done
import com.example.interview.app.EventRegistry.Event

import java.util.UUID
import scala.concurrent.Future

class EventRegistry {

  def createEvent(event: Event): Future[Done] = {
    Future.successful(Done)
  }

}

object EventRegistry {
  case class Event(userId: UUID, productId: UUID, eventType: String, value: Any)

  def apply(): EventRegistry = new EventRegistry
}
