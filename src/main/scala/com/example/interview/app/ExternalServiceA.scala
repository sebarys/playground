package com.example.interview.app

import akka.Done

import scala.concurrent.Future

class ExternalServiceA {

  def checkProductAvailability(): Future[Done] = {
    Future.successful(Done)
  }
}

object ExternalServiceA {
  def apply(): ExternalServiceA = new ExternalServiceA
}
