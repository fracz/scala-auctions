package pl.edu.agh.scala.auctions

import akka.actor.{Actor, ActorRef, Props}
import pl.edu.agh.scala.auctions.Auction.StartAuction
import pl.edu.agh.scala.auctions.Seller.{FindSomethingToSell, SellNewProduct}

import scala.concurrent.duration.DurationInt
import scala.util.Random

object Seller {

  case object SellNewProduct

  case object FindSomethingToSell

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

  import context._

  private val random: Random = new Random()

  private var wallet: Double = 0

  override def receive: Actor.Receive = {
    case FindSomethingToSell =>
      context.system.scheduler.scheduleOnce(random.nextInt(30) seconds, self, SellNewProduct)
    case SellNewProduct =>
      val vehicle = Seller.vehicles(random.nextInt(Seller.vehicles.size))
      val transmission = if (random.nextBoolean()) "manual" else "automatic"
      val engine = if (random.nextBoolean()) "diesel" else "petrol"
      val vehicleName = vehicle + " " + engine + " " + transmission
      val auction: ActorRef = context.system.actorOf(Props[Auction])
      auction ! StartAuction(vehicleName)
      self ! FindSomethingToSell
    case Auction.Sold(productName, price) =>
      wallet += price
      println(f"I sold the ${productName} for $$$price%1.2f. I have now $$$wallet%1.2f!")
  }
}


