package pl.edu.agh.scala.auctions

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.edu.agh.scala.auctions.Seller.FindSomethingToSell

object AuctionSystem extends App {
  val config = ConfigFactory.load()

  val auctionSystem = ActorSystem("AuctionSystem", config.getConfig("auctionapp").withFallback(config))

  val sellersNum = 5
  val buyersNum = 10

  var i = 0

  auctionSystem.actorOf(Props[AuctionSearch], AuctionSearch.ACTOR_NAME)
  auctionSystem.actorOf(Props[Notifier], Notifier.ACTOR_NAME)

  val sellers: List[ActorRef] = for (i <- (1 to sellersNum).toList) yield {
    val seller: ActorRef = auctionSystem.actorOf(Props(new Seller()))
    seller ! FindSomethingToSell
    seller
  }

  for (i <- 1 to buyersNum) {
    val buyer = auctionSystem.actorOf(Props(new Buyer(i)))
    buyer ! Buyer.Init
  }

  val auctionPublisherSystem = ActorSystem("PublisherSystem",  config.getConfig("publisherapp").withFallback(config))
  auctionPublisherSystem.actorOf(Props[AuctionPublisher], AuctionPublisher.ACTOR_NAME)

  auctionSystem.awaitTermination()
}
