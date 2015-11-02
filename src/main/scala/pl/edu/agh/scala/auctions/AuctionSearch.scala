package pl.edu.agh.scala.auctions

import akka.actor.{Terminated, Actor, ActorRef, Props}
import pl.edu.agh.scala.auctions.Auction.StartAuction
import pl.edu.agh.scala.auctions.AuctionSearch.{NewAuction, SearchResponse, SearchQuery}
import pl.edu.agh.scala.auctions.Seller.{FindSomethingToSell, SellNewProduct}

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.util.Random

object AuctionSearch {
  val ACTOR_NAME = "AuctionSearch"

  case class SearchQuery(val query: String)

  case class SearchResponse(val auctions: Iterable[ActorRef])

  case class NewAuction(val productName: String)
}

class AuctionSearch extends Actor {

  private val random: Random = new Random()

  private var auctions: mutable.Map[String, ActorRef] = mutable.Map()

  override def receive: Actor.Receive = {
    case NewAuction(productName) =>
      auctions += (productName.toLowerCase() -> sender)
      context.watch(sender)
    case SearchQuery(query) =>
      val found = auctions.filterKeys(_.contains(query.toLowerCase))
      sender ! new SearchResponse(found.values)
    case Terminated(auction) =>
      auctions.retain((name, a) => a.equals(auction))
  }
}


