package pl.edu.agh.scala.auctions

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import org.scalatest.{WordSpecLike, Matchers, BeforeAndAfterAll}
import pl.edu.agh.scala.auctions.AuctionSearch.{SearchQuery, NewAuction}

class AuctionSearchTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("AuctionSystem"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An AuctionSearch actor" must {

    "find auctions that matches" in {
      val auctionSearch = system.actorOf(Props[AuctionSearch])
      val auction = new TestProbe(_system)
      auction.send(auctionSearch, new NewAuction("Audi A4"))
      auctionSearch ! new SearchQuery("Audi")
      expectMsgPF() {
        case AuctionSearch.SearchResponse(auctions) =>
          expect(auctions.toList.size)(1)
          assert(auctions.toList.contains(auction.ref))
        case _ =>
          assert(false)
      }
    }
  }

  "An AuctionSearch actor" must {

    "ignore letter case of query" in {
      val auctionSearch = system.actorOf(Props[AuctionSearch])
      val auction = new TestProbe(_system)
      auction.send(auctionSearch, new NewAuction("Audi A4"))
      auctionSearch ! new SearchQuery("aUDi")
      expectMsgPF() {
        case AuctionSearch.SearchResponse(auctions) =>
          expect(auctions.toList.size)(1)
          assert(auctions.toList.contains(auction.ref))
        case _ =>
          assert(false)
      }
    }
  }

  "An AuctionSearch actor" must {

    "provide empty result on non-match" in {
      val auctionSearch = system.actorOf(Props[AuctionSearch])
      val auction = new TestProbe(_system)
      auction.send(auctionSearch, new NewAuction("Audi A4"))
      auctionSearch ! new SearchQuery("Citroen")
      expectMsgPF() {
        case AuctionSearch.SearchResponse(auctions) =>
          expect(auctions.toList.size)(0)
        case _ =>
          assert(false)
      }
    }
  }
}
