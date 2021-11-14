package com.example.interview.app

object App {

  def main(args: Array[String]): Unit = {
    val serviceA = ExternalServiceA()
    val serviceB = ExternalServiceB()
    val databaseCapability = DatabaseCapability()
    val eventRegistry = EventRegistry()
    ProductManager(serviceA, serviceB, databaseCapability, eventRegistry)
    // TODO add tapir EP and akka-http server
  }

}
