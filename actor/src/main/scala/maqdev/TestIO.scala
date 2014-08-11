package maqdev

import java.util.concurrent.atomic.AtomicInteger

import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class Greeting(who: String)
case class Response(s: String)
case class StartClient()
case class StopClient()

object Report {
  val _sent = new AtomicInteger(0)
  val _recv = new AtomicInteger(0)

  def sent = _sent.incrementAndGet()
  def recv = _recv.incrementAndGet()

  def print: Unit = {
    println(s"Sent: ${_sent.get()}, recv: ${_recv.get()}")
  }
}

class GreetingActor extends Actor {
  def receive = {
    case Greeting(who) ⇒
      Report.recv
      Report.sent
      sender() ! Response("Hello was delivered to " + who)
  }
}

class GreetingClientActor(greeter: ActorRef) extends Actor {
  var finished = false

  def receive = {
    case StartClient ⇒
      Report.recv
      Report.sent
      greeter ! Greeting("Charlie Parker Charlie Parker Charlie Parker Charlie Parker Charlie Parker Charlie Parker Charlie Parker")

    case r: Response =>
      Report.recv
      if (!finished) {
        Report.sent
        greeter ! Greeting("Charlie Parker Charlie Parker Charlie Parker Charlie Parker Charlie Parker Charlie Parker Charlie Parker")
      }

    case StopClient =>
      finished = true
  }
}

object TestIO {
  def main(args: Array[String]) {
    val system = ActorSystem("MySystem")
    implicit val timeout: akka.util.Timeout = 5.seconds

    val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
    val client = system.actorOf(Props(classOf[GreetingClientActor], greeter), name = "client-1")

    for (i <- 1 to 4) {
      client ! StartClient
    }

    Thread.sleep(1000)
    client ! StopClient
    Report.print
    Thread.sleep(100)
    system.shutdown()
  }
}
