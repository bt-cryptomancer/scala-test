package restaurant

import akka.actor.{Actor, ActorRef}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ClientActor(kitchen: ActorRef, tableNum: Int, startAt: Int, interval: Int) extends Actor {
  val log: Logger = LoggerFactory.getLogger("ClientActor")

  // in a real world application this would be organized into some kind of database
  // but for our purposes a simple list for testing will suffice
  val stuffToOrder = Array(
    // food
    "pizza", "ice cream", "sandwich", "crepe", "hamburger", "steak", "pasta", "salad", "toast", "rice", "egg",
    "pork", "chicken", "cereal", "banana", "orange", "corn soup", "pumpkin soup", "minestrone soup", "beef stew",
    "sushi", "tempura", "udon", "soba", "onigiri", "cake", "apple pie", "pumpkin pie", "chocolate pie",

    // drinks
    "water", "beer", "lemonade", "apple juice", "orange juice", "mango juice", "whiskey", "coke", "pepsi", "7-up",
    "sprite", "grapefruit juice", "coffee", "green tea", "vodka", "milk", "soy milk", "eggnog", "lemon tea", "milk tea"
  )

  private val rng = scala.util.Random
  rng.setSeed(System.currentTimeMillis)

  override def receive: Receive = {
    case StartOrdering =>
      context.system.scheduler.scheduleWithFixedDelay(
        startAt.milliseconds,
        interval.milliseconds,
        self,
        Tick)

    case ClientData(fromTable, data) =>
      // receive confirmation data back from the kitchen when actions are
      // carried out
      log.info(fromTable + ", " + data)

    case Tick =>
      // every tick we've got a random chance of taking some random action
      // (note: I have deliberately chosen not to have clients issue "delete"
      // requests, although the API allows this, because I think it's more
      // interesting to ensure all orders are left in the system so you can
      // see what happens when they are finished being prepared)
      val randomChance = rng.nextInt(10)
      if (randomChance > 6) {
        val randomConsumable = stuffToOrder(rng.nextInt(stuffToOrder.length))
        kitchen ! PostOrder(tableNum, randomConsumable)
      }
      else if (randomChance == 1) {
        kitchen ! GetOrders(tableNum)
      }

    case _ =>
      log.info("message not handled")
  }
}