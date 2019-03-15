val v = Vector.range(0, 10)

v.foreach(print)
v.par.foreach(print)
v.par.foreach(print)
v.par.foreach(print)

import scala.collection.parallel.immutable.ParVector
val v1 = ParVector.range(0, 10)

v1.foreach {
  e => Thread.sleep(100); println(e)
}
