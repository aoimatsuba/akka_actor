import akka.actor.{Actor, ActorSystem, Props}

// Life cycle methods of an actor:
// receive
// preStart
// postStop
// preRestart
// postRestart

case object ForceRestart

class ActorSam extends Actor {

  println("This is Actor Sam constructor")

  def receive = {
    case ForceRestart => throw new Exception("Forced!!")
    case _            => println("Actor Sam received a message!")
  }

  override def preStart(): Unit = println("Actor Sam pre started")
  override def postStop(): Unit = println("Actor Sam post stopped")

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    // There are reason and message
    println(
      s"Actor Sam Restarting: Reason => ${reason.getMessage}, Message => ${message
        .getOrElse("no message")}")

    // It disposes of all children and then calls `postStop()`.'''
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    // It is called right "AFTER" restart on the newly created Actor to allow reinitialization after an Actor crash.
    println(s"ActorSam Post Start: reason => ${reason.getMessage}")
    super.postRestart(reason) // during this default postRestart, preStart is called
  }
}

object LifeCycle extends App {

  val system = ActorSystem("LifeCycleSystem")

  val sam = system.actorOf(Props[ActorSam], name = "Sam")

  // Send normal message
  println("sending message to Sam ........")
  sam ! "Hello Sam!" // enter constructor here
  Thread.sleep(1000) // need to wait here because of the asynchronous processes

  // Force restart
  println("Force Restart Sam.......")
  sam ! ForceRestart
  Thread.sleep(1000)

  println("Stop Sam")
  system.stop(sam)

  system.terminate()

}
