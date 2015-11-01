package pl.edu.agh.scala.auctions

import akka.actor.{ActorRef, ActorSystem, Props}
import pl.edu.agh.scala.auctions.Auction.StartAuction

object AuctionSystem extends App {
  val system = ActorSystem("Allegro")

  val auctionsNum = 10
  val biddersNum = 3

  var i = 0
  val auctions: List[ActorRef] = for (i <- (1 to auctionsNum).toList) yield {
    val auction: ActorRef = system.actorOf(Props(new Auction(i)))
    auction ! StartAuction
    auction
  }

  for (i <- 1 to biddersNum) {
    val bidder = system.actorOf(Props(new Buyer(i, auctions)))
    bidder ! Buyer.Init
  }

  system.awaitTermination()
}
