import akka.actor.{Actor, ActorSystem, PoisonPill, Props}

import scala.concurrent.{Await, Future}
import akka.pattern.gracefulStop

import scala.concurrent.duration._
/*
Ways to terminate actors
1. stop method:
performed asynchronously; continue to process current messages but no additional messages
are processed. The additional messages are sent to "deadLetters" actor of the ActorSystem
postStop method will be invoked

2. PoisonPill
This will stop the actor when this message is processed. It is queued as ordinary message.

3. gracefulStop method

 */

class StopActor extends Actor {

  override def postStop(): Unit = println("Stop Actor is being killed!")

  def receive = {
    case _ => println("Stop Actor got some message")
  }
}

class PoisonPillActor extends Actor {

  // This is still called
  override def postStop(): Unit = println("Poison Actor is being killed!")

  def receive = {
    case s: String => println(s"$s")
    case _         => println("PoisonPill Actor got some message")
  }
}

class GracefulActor extends Actor {

  override def postStop(): Unit = println("Graceful Actor is being killed!")

  def receive = {
    case _ => println("Graceful Actor got some message")
  }
}

object TerminateActors extends App {

  val system = ActorSystem("TerminateSystem")
  val stopActor = system.actorOf(Props[StopActor], name = "stopActor")
  val poisonActor = system.actorOf(Props[PoisonPillActor], name = "poisonActor")
  val gracefulActor =
    system.actorOf(Props[GracefulActor], name = "gracefulActor")

  // stop method
  stopActor ! "test!"
  system.stop(stopActor)
  Thread.sleep(500)

  // PoisonPill message
  poisonActor ! "Before killed"
  poisonActor ! PoisonPill
  poisonActor ! "Can you hear it?" // This message will not be processed
  // check if the deadletter is collected
  println(system.deadLetters.toString())
  Thread.sleep(500)

  // GracefulStop method
  try {
    val stopped: Future[Boolean] =
      gracefulStop(gracefulActor, 2 seconds)
    Await.result(stopped, 3 seconds)
    println("Graceful actor has stopped") // this message will be received after postStop message
  } catch {
    case e: Exception => e.printStackTrace
  } finally {
    system.terminate()
  }
}
