package org.pfcoperez

import org.pfcoperez.CancellableFuture.FutureCancelled

import scala.concurrent.duration.Duration
import scala.concurrent._
import scala.util.{Success, Failure, Try}

import java.util.concurrent.{Future => JFuture, Executor, Callable, FutureTask}

object CancellableFuture {
  case object FutureCancelled extends RuntimeException("Future has been cancelled")

  def apply[T](todo: => T): Future[T] = {
    val p = Promise[T]

    val jf: FutureTask[Unit] = new FutureTask[Unit](
      new Callable[Unit] {
        override def call(): _ = {
          val res: T = todo
          p.complete(Success(res))
        }
      }
    )

  }

}

class CancellableFuture[T](todo: => T) extends Future[T] {

  val jf: FutureTask[T] = new FutureTask[T](
    new Callable[T] {
      override def call(): _ = todo
    }
  )

  implicitly[Executor].execute(jf)

  override def onComplete[U](f: (Try[T]) => U)(implicit executor: ExecutionContext): Unit = {
      executor.
    }

  override def isCompleted: Boolean = jf.isCancelled || jf.isDone

  override def value: Option[Try[T]] =
    if(jf.isCancelled) Some(Failure(FutureCancelled))
    else if(jf.isDone) Some(Success(jf.get))
    else None

  @throws[Exception](classOf[Exception])
  override def result(atMost: Duration)(implicit permit: CanAwait): T =
    jf.get(atMost._1, atMost._2)

  @throws[InterruptedException](classOf[InterruptedException])
  @throws[TimeoutException](classOf[TimeoutException])
  override def ready(atMost: Duration)(implicit permit: CanAwait): CancellableFuture.this.type = {
    jf.get(atMost._1, atMost._2)
    this
  }
}
