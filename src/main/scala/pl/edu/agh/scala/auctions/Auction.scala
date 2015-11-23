package pl.edu.agh.scala.auctions

import akka.actor.{Cancellable, ActorRef, Actor}
import akka.persistence.PersistentActor
import pl.edu.agh.scala.auctions.AuctionSearch.NewAuction
import pl.edu.agh.scala.auctions.Notifier.Notify
import scala.concurrent.duration.DurationInt

import scala.util.Random


object Auction {

  case class StartAuction(productName: String)

  case class Bid(price: Double) {
    require(price > 0)
  }

  case object BidAccepted

  case object BidRejected

  case object BidTimerExpired

  case object DeleteTimerExpired

  case class Sold(productName: String, finalPrice: Double)
}

class Auction extends Actor {

  import context._
  import Auction._

  private val random = new Random()

  private var productName: String = _

  private var seller: ActorRef = _

  private var timerExpiredCancel: Cancellable = _

  private var highestPrice: Double = 0

  private var winner: ActorRef = _

  override def receive: Receive = {
    case StartAuction(name) =>
      productName = name
      seller = sender
      context.actorSelection(s"akka://AuctionSystem/user/${AuctionSearch.ACTOR_NAME}") ! new NewAuction(productName)
      enterCreatedState
  }

  private def log(message: String): Unit = {
    println(f"[Auction ${productName} ($$$highestPrice%1.2f)] ${message}")
  }

  private def enterCreatedState = {
    log("Auction started!")
    context become created
    timerExpiredCancel = context.system.scheduler.scheduleOnce(random.nextInt(10000) milliseconds, self, BidTimerExpired)
  }

  private def handleNewBid(bidder: ActorRef, price: Double) = {
    if (price > highestPrice) {
      log(f"Higher bid ($$$price%1.2f) accepted!")
      winner = bidder
      highestPrice = price
      bidder ! BidAccepted
      context.actorSelection(s"akka://AuctionSystem/user/${Notifier.ACTOR_NAME}") ! Notify(productName, highestPrice, winner)
    }
    else {
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
      winner ! Sold(productName, highestPrice)
      seller ! Sold(productName, highestPrice)
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
