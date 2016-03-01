package org.pfperez.concurrent

import org.scalatest.{Matchers, FlatSpec}

import org.pfcoperez.concurrent.Cancellable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object CancellableUT {
  def measureTime(todo: => Unit): Duration = {
    val init = System.currentTimeMillis()
    todo
    Duration(System.currentTimeMillis() - init, MILLISECONDS)
  }
}

class CancellableUT extends FlatSpec with Matchers {

  import CancellableUT._

  "A cancellable task" should "provide a future interface" in {

    val ct = Cancellable[Int](global) {
      Thread.sleep(1000)
      42
    }

    Await.result(ct.fut, 2 seconds) shouldBe 42

  }

  it should "be able to cancel the running task" in {

    val tsleep = 1 second

    val ct = Cancellable[Int](global) {
      Thread.sleep(1000)
      42
    }

    ct.cancel()
    measureTime(Await.ready(ct.fut, tsleep*2)).toMillis should be < tsleep.toMillis

  }

}
