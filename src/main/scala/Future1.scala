import scala.concurrent.{Await, Future, future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Future1 extends App {
  implicit val baseTime = System.currentTimeMillis()

  println("starting calculation")
  val f = Future {
    Thread.sleep(300)
    1 + 1
  }

  // Another way to create a function that returns future :DEPRECATED
  def longRunningCalulation(x: Int): Future[Int] = future {
    Thread.sleep(500)
    x + 3
  }

  // This blocking and is not recommended: because it will wait until the result is returned
  // instead , callback methods should be used
  // val result = Await.result(f, 1 second)
  //  println(s"Awated result is $result")

  println("before completing")
  f.onComplete {
    case Success(value) => println(s"Got call back, answer is $value")
    case Failure(e)     => e.printStackTrace
  }

  // Or instead of onComplete use following
  // DEPRECATED
  f onSuccess {
    case result => println(result)
  }
  f onFailure {
    case e => e.printStackTrace
  }

  // Some other stuff might happen before the awaited result
  println("One mississippi...."); Thread.sleep(100)
  println("Two mississippi...."); Thread.sleep(100)
  println("Three mississippi...."); Thread.sleep(100)
  println("Four mississippi...."); Thread.sleep(100)
  println("Five mississippi...."); Thread.sleep(100)
  println("Six mississippi...."); Thread.sleep(100)

  Thread.sleep(300)
}
