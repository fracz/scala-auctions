package pl.edu.agh.scala.auctions

import akka.actor.{ActorRef, ActorSystem, Props}
import pl.edu.agh.scala.auctions.Seller.FindSomethingToSell

object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")

  val sellersNum = 5
  val buyersNum = 10

  var i = 0

  system.actorOf(Props[AuctionSearch], AuctionSearch.ACTOR_NAME)

  val sellers: List[ActorRef] = for (i <- (1 to sellersNum).toList) yield {
    val seller: ActorRef = system.actorOf(Props(new Seller()))
    seller ! FindSomethingToSell
    seller
  }

  for (i <- 1 to buyersNum) {
    val buyer = system.actorOf(Props(new Buyer(i)))
    buyer ! Buyer.Init
  }

  system.awaitTermination()
}
