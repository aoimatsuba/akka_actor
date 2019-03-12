import akka.actor.{Actor, ActorSystem, Props}

class HelloActor extends Actor {
  def receive = {
    case "hello" => println("Hello in English")
    case "konnichiwa" => println("That's Japanese")
    case _ => println("What was that?")
  }
}

object Main extends App {

  val system = ActorSystem("HelloActorSystem")

  // create and start the actor
  // Props: configuration object using in creating an actor
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")

  // send the actor two messages
  helloActor ! "hello"
  helloActor ! "nihao"
  helloActor ! "konnichiwa"
  helloActor ! "meow!"

  // shutdown system
  system.terminate()

}