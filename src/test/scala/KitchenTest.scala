import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import restaurant.{ClientData, DeleteOrder, GetOrders, KitchenActor, PostOrder, Tick}

class KitchenTest extends TestKit(ActorSystem("KitchenTest"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  var kitchen: ActorRef = _

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  override def beforeAll: Unit = {
    kitchen = system.actorOf(Props(new KitchenActor(10, 13)))
    kitchen ! PostOrder(1, "pizza")
    kitchen ! PostOrder(2, "soda")
    kitchen ! PostOrder(2, "cookie")
    kitchen ! PostOrder(2, "toast")
    kitchen ! PostOrder(1, "ice cream")
    kitchen ! PostOrder(3, "steak")
  }

  "A kitchen" must {
    "accept orders from clients" in {
      assert(expectMsgType[ClientData] == ClientData("ack table 1", "added orderID: 0"))
      assert(expectMsgType[ClientData] == ClientData("ack table 2", "added orderID: 0"))
      assert(expectMsgType[ClientData] == ClientData("ack table 2", "added orderID: 1"))
      assert(expectMsgType[ClientData] == ClientData("ack table 2", "added orderID: 2"))
      assert(expectMsgType[ClientData] == ClientData("ack table 1", "added orderID: 1"))
      assert(expectMsgType[ClientData] == ClientData("ack table 3", "added orderID: 0"))
    }

    "cancel orders upon client request" in {
      kitchen ! DeleteOrder(2, 1)
      kitchen ! DeleteOrder(20, 1)
      assert(expectMsgType[ClientData] == ClientData("ack table 2", "deleted orderID: 1"))
      assert(expectMsgType[ClientData] == ClientData("ack table 20", "deleted orderID: no orders for table 20"))
    }

    "show a list of client orders for a given table" in {
      kitchen ! GetOrders(2)
      kitchen ! GetOrders(1)
      kitchen ! GetOrders(3)
      kitchen ! GetOrders(4)
      val response1: ClientData = expectMsgType[ClientData]
      val response2: ClientData = expectMsgType[ClientData]
      val response3: ClientData = expectMsgType[ClientData]
      val response4: ClientData = expectMsgType[ClientData]

      // testing this kind of string data is quite awkward; in a real world application
      // this would be json which is much easier to work with
      assert(response1.fromTable == "ack table 2")
      assert(response1.data.contains("orders: HashMap"))
      assert(response1.data.contains("0 -> MenuItem(soda"))
      assert(!response1.data.contains("1 -> MenuItem(cookie"))
      assert(response1.data.contains("2 -> MenuItem(toast"))

      assert(response2.fromTable == "ack table 1")
      assert(response2.data.contains("orders: HashMap"))
      assert(response2.data.contains("0 -> MenuItem(pizza"))
      assert(response2.data.contains("1 -> MenuItem(ice cream"))

      assert(response3.fromTable == "ack table 3")
      assert(response3.data.contains("orders: HashMap"))
      assert(response3.data.contains("0 -> MenuItem(steak"))

      assert(response4.fromTable == "ack table 4")
      assert(response4.data.contains("orders: no orders for table 4"))
    }

    "serve prepared orders" in {
      Thread.sleep(9000)
      kitchen ! PostOrder(1, "fried egg")
      kitchen ! PostOrder(1, "bacon")
      Thread.sleep(5000)
      kitchen ! Tick
      kitchen ! GetOrders(1)
      kitchen ! GetOrders(2)
      kitchen ! GetOrders(3)
      val response1: ClientData = expectMsgType[ClientData]
      val response2: ClientData = expectMsgType[ClientData]
      val response3: ClientData = expectMsgType[ClientData]
      val response4: ClientData = expectMsgType[ClientData]
      val response5: ClientData = expectMsgType[ClientData]

      assert(response1 == ClientData("ack table 1", "added orderID: 2"))
      assert(response2 == ClientData("ack table 1", "added orderID: 3"))

      // check that older orders have finished being prepared
      assert(response3.fromTable == "ack table 1")
      assert(response3.data.contains("orders: HashMap"))
      assert(!response3.data.contains("0 -> MenuItem(pizza"))
      assert(!response3.data.contains("1 -> MenuItem(ice cream"))
      assert(response3.data.contains("2 -> MenuItem(fried egg"))
      assert(response3.data.contains("3 -> MenuItem(bacon"))

      assert(response4.fromTable == "ack table 2")
      assert(response4.data.contains("orders: no orders for table 2"))

      assert(response5.fromTable == "ack table 3")
      assert(response5.data.contains("orders: no orders for table 3"))

      Thread.sleep(10000)
      kitchen ! Tick
      kitchen ! GetOrders(1)
      val response6: ClientData = expectMsgType[ClientData]

      assert(response6.fromTable == "ack table 1")
      assert(response6.data.contains("orders: no orders for table 1"))
    }
  }
}
