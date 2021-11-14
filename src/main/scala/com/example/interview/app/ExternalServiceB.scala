package com.example.interview.app

import akka.Done

import java.util.UUID
import scala.concurrent.Future

class ExternalServiceB {

  def checkUser(user: UUID): Future[Done] = {
    Future.successful(Done)
  }
}

object ExternalServiceB {
  def apply(): ExternalServiceB = new ExternalServiceB
}

