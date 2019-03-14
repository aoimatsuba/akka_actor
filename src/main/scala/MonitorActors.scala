import akka.actor.{Actor, ActorSystem, PoisonPill, Props, Terminated}

case object ThrowException

class Parent2 extends Actor {

  // Start Noot as a child
  val noot = context.actorOf(Props[Child2], "Noot")
  val baj = context.actorOf(Props[Child2], "Baj")
  // Use watch method to monitor when child is died or not
  context.watch(noot)
  context.watch(baj)

  def receive = {
    // It will get Terminated message when noot is killed
    case Terminated(noot) => println(s"I just noticed $noot was killed!!!")
    case Terminated(baj)  => println(s"I just noticed Baj was killed!!!")
    case _                => println("Parent received a message")
  }
}

class Child2 extends Actor {

  override def postStop(): Unit =
    println("Oh no, I am being killed?!" + context.self)

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("I am not dying but restarting. Reason: " + reason)
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit =
    println("I am alive again")

  def receive = {
    case ThrowException => {
      println("Baj needs to throw an exception")
      throw new Exception("END")
    }
    case _ => println("Child received a message")
  }
}

object MonitorActors extends App {

  val system = ActorSystem("MonitoredSystem")
  val parent = system.actorOf(Props[Parent2], "Parent")

  // Look up Noonoo but not start it
  val noot = system.actorSelection("user/Parent/Noot")
  noot ! PoisonPill
  Thread.sleep(1000)

  // But if the child throw exception, they are not dying but restarting
  val baj = system.actorSelection("user/Parent/Baj")
  baj ! ThrowException
  Thread.sleep(1000)

  // There will be no output from case Terminated(baj): because Baj is not actually killed but restarted

  println("system shutdown ...")
  system.terminate()
}
