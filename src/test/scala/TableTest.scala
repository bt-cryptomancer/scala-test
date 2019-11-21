import org.scalatest.FunSuite
import restaurant.{MenuItem, Table}

class TableTest extends FunSuite {
  test("Table") {
    val testTable = new Table(1)

    // test addOrder
    testTable.addOrder(MenuItem("pizza", 123456789))
    testTable.addOrder(MenuItem("soda",  223456789))
    testTable.addOrder(MenuItem("steak", 323456789))
    testTable.addOrder(MenuItem("cake",  123456789))

    var orders = testTable.getOrders
    assert(orders.size === 4)
    assert(orders(0) === MenuItem("pizza", 123456789))
    assert(orders(1) === MenuItem("soda",  223456789))
    assert(orders(2) === MenuItem("steak", 323456789))
    assert(orders(3) === MenuItem("cake",  123456789))

    // test cancelOrder
    testTable.cancelOrder(1)

    orders = testTable.getOrders
    assert(orders.size === 3)
    assert(orders(0) === MenuItem("pizza", 123456789))
    assert(!orders.keySet.contains(1))  // order 1 was removed
    assert(orders(2) === MenuItem("steak", 323456789))
    assert(orders(3) === MenuItem("cake",  123456789))

    // test removeFinishedOrders
    testTable.removeFinishedOrders(123456790)

    orders = testTable.getOrders
    assert(orders.size === 1)
    assert(!orders.keySet.contains(0))  // order 0 was finished
    assert(!orders.keySet.contains(1))  // order 1 was removed
    assert(orders(2) === MenuItem("steak", 323456789))
    assert(!orders.keySet.contains(3))  // order 3 was finished
  }
}