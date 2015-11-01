package pl.edu.agh.scala.auctions

import akka.actor.{Props, ActorRef, Actor}
import pl.edu.agh.scala.auctions.Auction.StartAuction
import pl.edu.agh.scala.auctions.Seller.SellNewProduct

object Seller {

  case object SellNewProduct

  val vehicles = List(
  "Audi A1",
  "Audi A2",
  "Audi A3",
  "Audi A4",
  "Audi A5",
  "Audi A6",
  "Audi A7",
  "Audi A8",
  "Citroen C1",
  "Citroen C2",
  "Citroen C3",
  "Citroen C4",
  "Citroen C5"
  )
}

class Seller extends Actor {
  override def receive: Actor.Receive = {
    case SellNewProduct =>
      val auction: ActorRef = context.system.actorOf(Props(new Auction(i)))
      auction ! StartAuction

  }
}


