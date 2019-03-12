import akka.actor.{Actor, ActorRef, ActorSystem, Props}

// Type of messages does not have to be String
case object StartMessage
case object EndMessage
case object PongMessage // message from pong
case object PingMessage // message from ping

class Ping(pong: ActorRef) extends Actor {

  var counter = 0
  def receive = {
    case StartMessage =>
      counter += 1
      println(s"Ping: first message received ($counter)")

      // Ping needs to know who Pong is: Cannot use the implicit "sender" in the beginning
      pong ! PingMessage
    case PongMessage =>
      counter += 1
      println(s"Ping: ponged! ($counter)")
      // can actually use "sender" at this point
      if (counter > 5) sender ! EndMessage
      else {
        sender ! PingMessage
        // forgot to stop this actor here as well
        context.stop(self)
      }
    case _ => println("Ping doesn't understand the message")
  }

}

// Pong only receives message from Ping so it can just use "sender" reference
class Pong extends Actor {
  def receive = {
    case PingMessage =>
      println("Pong: received!")
      sender ! PongMessage
    case EndMessage =>
      println("Pong: Will End pingpong")
      // Context stores the context for this actor, including self, and sender.
      context.stop(self)
    case _ => println("Pong: doesn't understand the message")
  }
}

object PingPong extends App {

  val system = ActorSystem("PingPongSystem")

  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")

  ping ! StartMessage

  system.terminate()
}
