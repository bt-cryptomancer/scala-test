package restaurant

import akka.actor.{ActorSystem, Props}

object Main {
  private val numClients = 20
  private val rng = scala.util.Random
  rng.setSeed(System.currentTimeMillis)

  def main(args: Array[String]): Unit = {
    val restaurant: ActorSystem = ActorSystem("Restaurant")
    val kitchen = restaurant.actorOf(Props(new KitchenActor(5*60, 15*60)))
    val chef = restaurant.actorOf(ChefActor.props(kitchen))

    // start the process that checks for order completion
    chef ! StartCooking

    // make some clients with various random action delays
    for (tableNum <- 1 to numClients) {
      val startAt = rng.nextInt(1000) + 500
      val interval = rng.nextInt(10000) + 3000
      val client = restaurant.actorOf(Props(new ClientActor(kitchen, tableNum, startAt, interval)))
      client ! StartOrdering
    }
  }
}
