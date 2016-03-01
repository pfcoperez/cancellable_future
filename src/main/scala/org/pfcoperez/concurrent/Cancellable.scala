package org.pfcoperez.concurrent

import java.util.concurrent.{Callable, FutureTask}

import scala.concurrent.{ExecutionContext, Future}

object Cancellable {
  def apply[T](todo: => T)(implicit executionContext: ExecutionContext): Cancellable[T] =
    new Cancellable[T](executionContext, todo)
}

class Cancellable[T](executionContext: ExecutionContext, todo: => T) {

  private val jf: FutureTask[T] = new FutureTask[T](
    new Callable[T] {
      override def call(): T = todo
    }
  )

  executionContext.execute(jf)

  implicit val _: ExecutionContext = executionContext

  val fut: Future[T] = Future {
    jf.get()
  }

  def cancel(): Unit = jf.cancel(true)

}
