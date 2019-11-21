package restaurant

import java.util.Calendar

import akka.actor.{Actor, Props}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

object KitchenActor {
  val log: Logger = LoggerFactory.getLogger(KitchenActor.getClass)
  def props(): Props = Props[KitchenActor]

  private var orderTimeLowerBound: Int = 5*60    // in seconds
  private var orderTimeUpperBound: Int = 15*60   // in seconds
  private val rng = scala.util.Random
  private var tables: mutable.HashMap[Int, Table] = mutable.HashMap.empty[Int, Table]

  rng.setSeed(System.currentTimeMillis)

  def postOrder(tableNum: Int, consumable: String): String = {
    if (!tables.contains(tableNum)) {
      tables += (tableNum -> new Table(tableNum))
    }

    // generate a random completion time for the new order
    val randomNum = rng.nextInt(orderTimeUpperBound - orderTimeLowerBound)
    val calendar = Calendar.getInstance
    calendar.add(Calendar.SECOND, orderTimeLowerBound + randomNum)
    val completionTime = calendar.getTimeInMillis

    // add the order - if this was a real REST API, we would return some json with
    // the new order ID so the client can keep track of it
    log.info(s"added new order for $consumable at table $tableNum")
    tables(tableNum).addOrder(MenuItem(consumable, completionTime)).toString
  }

  // rather than returning the tables object directly, it's safer to turn
  // it into some separate string representation for the client
  // if this was a real REST API, we would return this as json data
  def getOrders(tableNum: Int): String = {
    log.info(s"client at table $tableNum wants to see the order list")
    if (tables.contains(tableNum) && tables(tableNum).getOrders.nonEmpty) {
      tables(tableNum).getOrders.toString
    } else {
      s"no orders for table $tableNum"
    }
  }

  def deleteOrder(tableNum: Int, orderId: Int): String = {
    if (tables.contains(tableNum)) {
      tables(tableNum).cancelOrder(orderId).toString
    } else {
      s"no orders for table $tableNum"
    }
  }

  // check to see if any orders are finished being
  // prepared, and if so serve them (which for this
  // test just means remove them from the system)
  def serveOrders(): Unit = {
    val now = System.currentTimeMillis
    tables.values.foreach((table) => table.removeFinishedOrders(now))
  }
}

class KitchenActor(val lowerBound: Int, val upperBound: Int) extends Actor {
  import KitchenActor._

  orderTimeLowerBound = lowerBound   // in seconds
  orderTimeUpperBound = upperBound   // in seconds

  override def receive: Receive = {
    // we run an orderly kitchen at this restaurant
    case Tick =>
      serveOrders()
    case PostOrder(tableNum: Int, consumable: String) =>
      sender ! ClientData(s"ack table $tableNum", "added orderID: " + postOrder(tableNum, consumable))
    case GetOrders(tableNum: Int) =>
      sender ! ClientData(s"ack table $tableNum", "orders: " + getOrders(tableNum))
    case DeleteOrder(tableNum: Int, orderId: Int) =>
      sender ! ClientData(s"ack table $tableNum", "deleted orderID: " + deleteOrder(tableNum, orderId))
  }
}
