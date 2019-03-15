import akka.actor._

case object BadMessage
case object NormalMessage

class Normal extends Actor {

  // states can only receive messages that are programmed for
  // Angry state
  def angryState: Receive = {
    case NormalMessage =>
      println("I need to calm down.")
      context.become(normalState)
    case BadMessage => println("ANGER EXPLOSION")
  }

  // Normal state
  def normalState: Receive = {
    case NormalMessage => println("It's ok I am already calm.")
    case BadMessage =>
      println("You are making me angry")
      context.become(angryState)
  }

  def receive = {
    case BadMessage => {
      println("Normal guy is becoming something else")
      context.become(angryState)
    }
    case NormalMessage =>
      println("I received a message")
      context.become(normalState)
  }
}

object ActorBecomeSomethingElse extends App {

  val system = ActorSystem("BecomeSomethingActorSystem")
  val normal = system.actorOf(Props[Normal], "normalActor")

  // initialize to normal state
  normal ! NormalMessage
  // At this point, its state is Normal
  normal ! NormalMessage

  // to Angry state
  normal ! BadMessage
  // make it even more angry
  normal ! BadMessage
  // back to normal
  normal ! NormalMessage

  // Nothing happen because state can only received programmed messages
  normal ! "something else"
  system.terminate()
}
