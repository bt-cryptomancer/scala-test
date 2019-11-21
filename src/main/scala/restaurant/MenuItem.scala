package restaurant

// name = food or drink item, e.g. rice, chicken, pepsi, water, etc
// completeAt = future timestamp of when the item will be ready to serve
case class MenuItem(name: String, completeAt: Long)