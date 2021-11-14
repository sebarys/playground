package com.example

import akka.{Done, NotUsed}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.{Route, RouteResult}

import scala.concurrent.Future
import com.example.UserRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.event.Logging
import akka.http.scaladsl.server.directives.LogEntry
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import akka.util.Timeout

//#import-json-formats
//#user-routes-class
class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getUsers(): Future[Users] =
    userRegistry.ask(GetUsers)
  def getUser(name: String): Future[GetUserResponse] =
    userRegistry.ask(GetUser(name, _))
  def createUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(CreateUser(user, _))
  def deleteUser(name: String): Future[ActionPerformed] =
    userRegistry.ask(DeleteUser(name, _))

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val userRoutes: Route =
    logRequestResult(requestBasicInfoAndResponseStatus _) {
      pathPrefix("users") {
        concat(
          //#users-get-delete
          pathEnd {
            concat(
              get {
                groupByAndReduce()
                complete(Future.successful("done"))
              },
              post {
                entity(as[User]) { user =>
                  onSuccess(createUser(user)) { performed =>
                    complete((StatusCodes.Created, performed))
                  }
                }
              })
          },
          //#users-get-delete
          //#users-get-post
          path(Segment) { name =>
            concat(
              get {
                //#retrieve-user-info
                rejectEmptyResponse {
                  onSuccess(getUser(name)) { response =>
                    complete(response.maybeUser)
                  }
                }
                //#retrieve-user-info
              },
              delete {
                //#users-delete-logic
                onSuccess(deleteUser(name)) { performed =>
                  complete((StatusCodes.OK, performed))
                }
                //#users-delete-logic
              })
          })
        //#users-get-delete
      }
    }
    //#all-routes

  private def createAndRunPrintingStream(): Future[Done] = {
    val seq: Seq[Int] = 1 to 100
    Source(seq.toList)
      .mapAsyncUnordered(2)(i => {
        Thread.sleep(2000)
        println(s"Processed $i")
        Future.successful(Done)
      })
      .map { in =>
        println(s"`mapAsyncUnordered` emitted event number: $in")
      }
      .runWith(Sink.ignore)
  }


  private def splitWhenStream(): Future[Done] = {
    val sourceSeq = List(
      "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1",
      "user2", "user2", "user2", "user2", "user2", "user2",
      "user3",
      "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4",
      "user5", "user5",
      "user6", "user6", "user6", "user6",
      "user7", "user7", "user7", "user7", "user7",
      "user8",
      "user9",
      "user10", "user10", "user10", "user10", "user10",
    ).map((_, 1))
    Source(sourceSeq)
      .statefulMapConcat(() => {
        // stateful decision in statefulMapConcat
        // keep track of user id, stating from non-existing userId to avoid null or Option
        var currentUserId = "not-existing-user"

        {
          case row@(userId, _) =>
            val receivedRowForNewUser = userId != currentUserId
            if (receivedRowForNewUser)
              currentUserId = userId
            List((row, receivedRowForNewUser))
        }
      })
      .splitWhen(_._2)
      .map(el => {
        Thread.sleep(2000)
        println(s"##### emitting $el with 2 sec delay")
        el
      })
      .fold(("not-existing-user", 0)) {
        case ((accUserId, accValue), ((userId, value), _))  => (userId, accValue + value)
      }
      .mergeSubstreams
      .runForeach(el => println(s"##### received result $el"))
  }

  private def groupByAndReduce(): Future[Done] = {

    val sourceSeq = List(
      "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1", "user1",
      "user2", "user2", "user2", "user2", "user2", "user2",
      "user3",
      "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4", "user4",
      "user5", "user5",
      "user6", "user6", "user6", "user6",
      "user7", "user7", "user7", "user7", "user7",
      "user8",
      "user9",
      "user10", "user10", "user10", "user10", "user10",
    ).map((_, 1))
    Source(sourceSeq)
      .map(el => {
        Thread.sleep(2000)
        println(s"##### emitting $el with 2 sec delay")
        el
      })
      .groupBy(100, _._1) // Substream per RP
      // TODO: This is where a RP configured generator would be created and appended to each element in RP's substream.
      .groupBy(100000, _._2) // Substream per insured
      .reduce[(String, Int)] { // Reduce each insured substream to collect all coverage type
        // TBD: rework so that there is no need to wait for db streaming to finish
        case ((userId, accVal), (_, value)) =>
          (userId, accVal + value)
      }
      .mergeSubstreams
      .mergeSubstreams
      .runForeach(el => println(s"##### received result $el"))
  }

  private def requestBasicInfoAndResponseStatus(req: HttpRequest): RouteResult => Option[LogEntry] = {
    case RouteResult.Complete(res) if res.status.isFailure() => Some(
      LogEntry(s"Request ${req.method} to ${req.uri} resulted in failure with status ${res.status}", Logging.InfoLevel)
    )
    case RouteResult.Complete(res) => Some(
      LogEntry(s"Request ${req.method} to ${req.uri} resulted in response with status ${res.status}", Logging.InfoLevel)
    )
    case RouteResult.Rejected(rejections) => Some(
      LogEntry(s"Request ${req.method} to ${req.uri} was rejected with rejections: $rejections", Logging.InfoLevel)
    )
  }

}
