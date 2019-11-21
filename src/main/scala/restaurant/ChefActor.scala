package restaurant

import akka.actor.{Actor, ActorRef, Props}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object ChefActor {
  def props(stateActor: ActorRef): Props = Props(new ChefActor(stateActor))
}

// the chef simply sends a message to the system state (the kitchen)
// every so often to update orders that are finished being prepared
class ChefActor(stateActor: ActorRef) extends Actor {
  val log: Logger = LoggerFactory.getLogger(ChefActor.getClass)

  override def receive: Receive = {
    case StartCooking =>
      log.info("The chef is in the house!")
      context.system.scheduler.scheduleWithFixedDelay(
        0.milliseconds,
        1000.milliseconds,
        self,
        Tick)

    case Tick =>
      stateActor ! Tick
  }
}
