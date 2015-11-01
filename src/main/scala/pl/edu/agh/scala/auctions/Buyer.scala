package pl.edu.agh.scala.auctions

import akka.actor.{Actor, ActorRef}

import scala.concurrent.duration.DurationInt
import scala.util.Random

object Buyer {

  case object Init

  case object PlaceRandomBid

}

class Buyer(val number: Int, val auctions: List[ActorRef]) extends Actor {

  import Auction._
  import Buyer._
  import context._

  private val random: Random = new Random()

  override def receive: Actor.Receive = {
    case Init =>
      think
    case PlaceRandomBid =>
      val auction = auctions(random.nextInt(auctions.size))
      auction ! Bid(random.nextDouble() * 100.0)
    case BidAccepted =>
      think
    case BidRejected =>
      think
    case Sold(id, price) =>
      println(s"Bidder ${number} has bought the #${id} for ${price}!")
  }

  private def think = {
    context.system.scheduler.scheduleOnce(random.nextInt(2000) milliseconds, self, PlaceRandomBid)
  }

}
