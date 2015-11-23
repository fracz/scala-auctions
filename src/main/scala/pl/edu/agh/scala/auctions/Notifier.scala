package pl.edu.agh.scala.auctions

import akka.actor.{ActorRef, Actor}
import pl.edu.agh.scala.auctions.Notifier.Notify

object Notifier {
  val ACTOR_NAME = "notifier"

  case class Notify(product: String, price: Double, winner: ActorRef)

}

class Notifier extends Actor {

  val auctionPublisher = context.system.actorSelection(s"akka.tcp://PublisherSystem@127.0.0.1:2553/user/${AuctionPublisher.ACTOR_NAME}")

  override def receive: Receive = {
    case Notify(product, price, winner) =>
      auctionPublisher ! Notify(product, price, winner)
  }

}
