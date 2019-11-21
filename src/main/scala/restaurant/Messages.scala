package restaurant

// here we define various messages for passing between actors
case object Tick
case object StartCooking
case object StartOrdering

// -------------------------------------------------------------------------------------------------------
// In a real world application, this would be a REST API running on some server. However, for the purposes
// of this test I have chosen to simulate such an API with the below case classes. To simplify things,
// I also choose to assume that all operations will succeed; in a real server the API would return the
// proper HTTP response codes.
// -------------------------------------------------------------------------------------------------------

// queries from the client
// -----------------------
// equivalent to HTTP verb POST, uri = api/orders/<tableNum>, json data = { consumable }
case class PostOrder(tableNum: Int, consumable: String)

// equivalent to HTTP verb GET, uri = api/orders/<tableNum>
// would return json with list of orders
case class GetOrders(tableNum: Int)

// equivalent to HTTP verb DELETE, uri = api/orders/<tableNum>/<orderId>
case class DeleteOrder(tableNum: Int, orderId: Int)

// used for returning data to the client as a query response
// in a real REST API this would be some json data and/or an HTTP response code
// --------------------------------------------------------------
case class ClientData(fromTable: String, data: String)