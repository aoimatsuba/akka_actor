import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.{Await, Future}

case object AskActor

class FutureActor extends Actor {

  def receive = {
    // Actor respond to "ask" message by sending back the message
    case AskActor => sender ! "Baj!"
    case _        => println("Actor received a message")
  }
}

object SendActorMessage extends App {

  val system = ActorSystem("SendActorSystem")
  val futureActor = system.actorOf(Props[FutureActor], "FutureActor")

  implicit val timeout = Timeout(5 seconds)

  // ? is the ask method to ask Actor.
  val future = futureActor ? AskActor
  val result = Await.result(future, timeout.duration).toString
  println(result)

  // Another approach to ask Actor
  val future2: Future[String] = ask(futureActor, AskActor).mapTo[String]
  // above mapTo map the type wrapped by Future
  val result2 = Await.result(future2, 1 second)
  println("Second approach: " + result2)

  system.terminate
}
