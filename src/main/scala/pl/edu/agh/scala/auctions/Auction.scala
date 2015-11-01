package pl.edu.agh.scala.auctions

import akka.actor.{Cancellable, ActorRef, Actor}
import scala.concurrent.duration.DurationInt

import scala.util.Random


object Auction {

  case object StartAuction

  case class Bid(price: Double) {
    require(price > 0)
  }

  case object BidAccepted

  case object BidRejected

  case object BidTimerExpired

  case object DeleteTimerExpired

  case class Sold(id: Int, finalPrice: Double)
}

class Auction(val number: Int) extends Actor {

  import context._
  import Auction._

  private val random = new Random()

  private var timerExpiredCancel: Cancellable = _

  private var highestPrice: Double = 0

  private var winner: ActorRef = _

  override def receive: Receive = {
    case StartAuction =>
      enterCreatedState
  }

  private def log(message: String): Unit = {
    println(s"[Auction #${number} (${highestPrice})] ${message}")
  }

  private def enterCreatedState = {
    log("Auction started!")
    context become created
    timerExpiredCancel = context.system.scheduler.scheduleOnce(random.nextInt(10000) milliseconds, self, BidTimerExpired)
  }

  private def handleNewBid(bidder: ActorRef, price: Double) = {
    if (price > highestPrice) {
      log(s"Bid ${price} accepted!")
      winner = bidder
      highestPrice = price
      bidder ! BidAccepted
    }
    else {
      log(s"Bid ${price} rejected!")
      bidder ! BidRejected
    }
  }

  def created: Receive = {
    case Bid(price) =>
      handleNewBid(sender, price)
      context become activated
    case BidTimerExpired =>
      context become ignored
      context.system.scheduler.scheduleOnce(5 seconds, self, DeleteTimerExpired)
  }

  def ignored: Receive = {
    case Bid =>
      sender ! BidRejected
    case DeleteTimerExpired =>
      log("Have not been bought.")
      context.stop(self)
  }

  def activated: Receive = {
    case Bid(price) =>
      handleNewBid(sender, price)
    case BidTimerExpired =>
      winner ! Sold(number, highestPrice)
      context become sold
      context.system.scheduler.scheduleOnce(5 seconds, self, DeleteTimerExpired)
  }

  def sold: Receive = {
    case Bid =>
      sender ! BidRejected
    case DeleteTimerExpired =>
      context.stop(self)
  }
}
