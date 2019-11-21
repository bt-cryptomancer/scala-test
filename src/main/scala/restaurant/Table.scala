package restaurant

import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

class Table(tableNum: Int) {
  private val log: Logger = LoggerFactory.getLogger("Table")
  private var orders: mutable.HashMap[Int, MenuItem] = mutable.HashMap.empty[Int, MenuItem]
  private var nextOrderId: Int = 0

  // add an order for this table and return the new order's ID
  def addOrder(order: MenuItem): Int = {
    val orderId = nextOrderId
    orders += (orderId -> order)
    nextOrderId += 1
    orderId
  }

  def cancelOrder(orderId: Int): Int = {
    orders -= orderId
    orderId
  }

  def getOrders: mutable.HashMap[Int, MenuItem] = orders

  // remove orders that have finished being prepared
  def removeFinishedOrders(cutoffTime: Long): Unit = {
    orders.values.foreach((item) => if (item.completeAt <= cutoffTime) {
      val consumable = item.name
      log.info(s"$consumable is served at table $tableNum")
    })
    orders.filterInPlace((_, item) => item.completeAt > cutoffTime)
  }
}