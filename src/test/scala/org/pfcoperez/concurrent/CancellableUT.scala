package org.pfcoperez.concurrent

import java.util.concurrent.CancellationException

import com.stratio.common.utils.concurrent.Cancellable
import org.scalatest.concurrent.Timeouts._
import org.scalatest.{Matchers, FlatSpec}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure

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

    val ct = Cancellable[Int] {
      Thread.sleep(1000)
      42
    }

    Await.result(ct.fut, 2 seconds) shouldBe 42

  }

  it should "be able to cancel the running task" in {

    val ct = Cancellable[Int] {
      Thread.sleep(tsleep.toMillis)
      expectedRes
    }

    failAfter(tsleep) {
      ct.cancel()
      Await.ready(ct.fut, tsleep*2).value should matchPattern {
        case Some(Failure(_: CancellationException)) =>
      }
    }

  }

}
