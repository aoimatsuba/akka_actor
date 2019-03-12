import akka.actor.{Actor, ActorSystem, Props}

class HelloActor(myName: String) extends Actor {
  def receive = {
    case "hello" => println(s"Hello $myName in English")
    case "konnichiwa" => println(s"That's Japanese, $myName!")
    case _ => println(s"What was that $myName")
  }
}

object Main extends App {

  val system = ActorSystem("HelloActorSystem")

  // create and start the actor
  // Props: configuration object using in creating an actor
  val helloActor = system.actorOf(Props(new HelloActor("Aoi")), name = "helloactor")

  // send the actor two messages
  helloActor ! "hello"
  helloActor ! "nihao"
  helloActor ! "konnichiwa"
  helloActor ! "meow!"

  // shutdown system
  system.terminate()

}