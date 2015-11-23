package pl.edu.agh.scala.auctions

import akka.actor.Actor
import pl.edu.agh.scala.auctions.Notifier.Notify

object AuctionPublisher {
  val ACTOR_NAME = "AUCTION_PUBLISHER"
}

class AuctionPublisher extends Actor {
  override def receive: Receive = {
    case Notify(product, price, winner) =>
      println(s"NEW AUCTION STATE PUBLISHED: ${product} will be sold for ${price}!")
  }
}
