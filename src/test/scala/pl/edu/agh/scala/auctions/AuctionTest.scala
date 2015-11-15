package pl.edu.agh.scala.auctions

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.edu.agh.scala.auctions.Auction.StartAuction
import pl.edu.agh.scala.auctions.AuctionSearch.{NewAuction, SearchQuery}

class AuctionTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("AuctionSystem"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Auction actor" must {

    "notify the seller that it was sold" in {
      val seller = new TestProbe(_system)
      val auction = system.actorOf(Props[Auction])
      seller.send(auction, StartAuction("Test"))
      auction ! Auction.Bid(30.0)
      auction ! Auction.BidTimerExpired
      seller.expectMsg(Auction.Sold("Test", 30.0))
    }
  }

}
