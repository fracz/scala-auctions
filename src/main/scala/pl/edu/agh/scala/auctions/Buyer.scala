package pl.edu.agh.scala.auctions

import akka.actor.Actor
import pl.edu.agh.scala.auctions.AuctionSearch.SearchQuery

import scala.concurrent.duration.DurationInt
import scala.util.Random

object Buyer {

  case object Init

  case object LookForInterestingAuctions

  val possibleInterests = List("Audi", "Citroen", "C4", "A3", "Audi A3", "diesel", "petrol", "automatic", "manual")

}

class Buyer(val number: Int) extends Actor {

  import Auction._
  import Buyer._
  import context._

  private val random: Random = new Random()

  private val interests = possibleInterests(random.nextInt(possibleInterests.size))

  private def log(message: String): Unit = {
    println(s"Buyer #${number} [${interests}]: ${message}")
  }

  override def receive: Actor.Receive = {
    case Init =>
      log("Initialized!")
      think
    case LookForInterestingAuctions =>
      context.actorSelection(s"akka://AuctionSystem/user/${AuctionSearch.ACTOR_NAME}") ! new SearchQuery(interests)
    case AuctionSearch.SearchResponse(auctions) =>
      if (auctions.nonEmpty) {
        val auction = auctions.toList(random.nextInt(auctions.size))
        auction ! Bid(random.nextDouble() * 100.0)
      }
      else
        think
    case BidAccepted =>
      think
    case BidRejected =>
      think
    case Sold(id, price) =>
      log(f"has bought the ${id} for $$$price%1.2f!")
  }

  private def think = {
    context.system.scheduler.scheduleOnce(random.nextInt(2000) milliseconds, self, LookForInterestingAuctions)
  }

}
