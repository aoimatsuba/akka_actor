import akka.actor.{Actor, ActorSystem, PoisonPill, Props}

case class CreateChild(name: String)
case class GivingName(name: String)

class Parent extends Actor {
  def receive = {
    case CreateChild(name) => {
      println(s"Creating a child: $name")
      val child = context.actorOf(Props[Child], name = s"$name")
      child ! GivingName(name)
    }
    case _ => println("Parent got a message")
  }
}

class Child extends Actor {
  var name = "Baby"

  override def postStop(): Unit =
    println(s"$name is not feeling well: ${self.path}")

  def receive = {
    case GivingName(n) => {
      this.name = n
      println(s"My neme is $name!")
    }
    case _ => println(s"Child $name got message")
  }

}

object StartActors extends App {

  val system = ActorSystem("ParentChildSystem")

  val parent = system.actorOf(Props[Parent], name = "Parent")

  parent ! CreateChild("Noot")
  parent ! CreateChild("Baj")
  Thread.sleep(1000)

  // Look up specific child
  println("Need to kill Baj....")
  val baj = system.actorSelection("user/Parent/Baj")
  baj ! PoisonPill

  // PoisonPill is actually a type of message that kills an actor
  // PoisonPill message is queued as normal message. Processed after all the other message sent first
  // Meanwhile stop method will process the current message if any but no additional messages will be processed
  println("Baj was poisoned")
  Thread.sleep(5000)

  println("Time to terminate everything!")
  system.terminate()
}
