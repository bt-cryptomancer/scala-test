# Scala Restaurant Test App

This app was created as an sbt project in IntelliJ. Project files are not part of the github repo, so to open it you will have to import into IntelliJ as a new sbt project.

## Running unit tests

Within IntelliJ, right click on src->test->scala folder and select Run ScalaTests. All 5 test cases should pass. It will take about 25 seconds to execute them.

## Running the program

Within IntelliJ, right click on src->main->scala->restaurant->Main and select Run Main. You will see text output from the different actors in the system, describing what is happening in the restaurant. There are 20 different clients at different tables, which will continuously place random orders at random times, and randomly query for lists of active orders. The KitchenActor will display the outcome of each action as it executes, and the ClientActor will display the returned status message from the kitchen. You will see orders quickly pile up for each table.

If you wait about 5 minutes or so, you should see orders being served - that is, the preparation time (which is also random) will be completed, resulting in orders being removed from the active orders list.

The program will run forever until you stop it manually by pressing the stop button.

## Expected output

As the program runs, you should see output similar to the following (each run will produce different randomized output):

```
00:24:00.711 [Restaurant-akka.actor.default-dispatcher-4] INFO  akka.event.slf4j.Slf4jLogger - Slf4jLogger started
00:24:00.855 [Restaurant-akka.actor.default-dispatcher-4] INFO  restaurant.ChefActor$ - The chef is in the house!
00:24:01.507 [Restaurant-akka.actor.default-dispatcher-4] INFO  restaurant.KitchenActor$ - added new order for corn soup at table 16
00:24:01.508 [Restaurant-akka.actor.default-dispatcher-11] INFO  ClientActor - ack table 16, added orderID: 0
00:24:01.840 [Restaurant-akka.actor.default-dispatcher-11] INFO  restaurant.KitchenActor$ - client at table 1 wants to see the order list
00:24:01.840 [Restaurant-akka.actor.default-dispatcher-11] INFO  ClientActor - ack table 1, orders: no orders for table 1
...
...
00:24:37.369 [Restaurant-akka.actor.default-dispatcher-4] INFO  restaurant.KitchenActor$ - added new order for rice at table 12
00:24:37.369 [Restaurant-akka.actor.default-dispatcher-4] INFO  restaurant.KitchenActor$ - added new order for steak at table 10
00:24:37.369 [Restaurant-akka.actor.default-dispatcher-9] INFO  ClientActor - ack table 12, added orderID: 0
00:24:37.370 [Restaurant-akka.actor.default-dispatcher-9] INFO  ClientActor - ack table 10, added orderID: 2
00:24:38.147 [Restaurant-akka.actor.default-dispatcher-9] INFO  restaurant.KitchenActor$ - client at table 3 wants to see the order list
00:24:38.147 [Restaurant-akka.actor.default-dispatcher-4] INFO  ClientActor - ack table 3, orders: HashMap(0 -> MenuItem(lemonade,1574350690207), 1 -> MenuItem(udon,1574350610178), 2 -> MenuItem(egg,1574350326169))
...
...
00:29:56.429 [Restaurant-akka.actor.default-dispatcher-4] INFO  Table - cake is served at table 15
```
