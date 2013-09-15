package ikitommi.slick.jdbc;

import scala.slick.jdbc.GetResult
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.session.Database
import scala.slick.session.Database.threadLocalSession

object PlainSQL extends App {

  case class Coffee(name: String, price: Double, sales: Int, total: Int)

  implicit val getCoffeeResult = GetResult(r => Coffee(r.<<, r.<<, r.<<, r.<<))

  Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession { 

    Q.updateNA("create table coffee("+
      "name varchar not null, "+
      "price double not null, "+
      "sales int not null, "+
      "total int not null)").execute

    def insert(c: Coffee) = (Q.u + "insert into coffee values (" +? c.name + "," +? c.price + "," +? c.sales + "," +? c.total + ")").execute
          
    Seq(
      Coffee("Colombian", 7.99, 0, 0),
      Coffee("French_Roast", 8.99, 0, 0),
      Coffee("Espresso", 9.99, 0, 0),
      Coffee("Colombian_Decaf", 8.99, 0, 0),
      Coffee("French_Roast_Decaf", 9.99, 0, 0)
    ).foreach(insert)

    import _root_.ikitommi.slick.jdbc.{StaticListQuery => SLQ}
    
    val sql = "select * from coffee where name in (?, ?)"
    val parameters = List("Colombian","Espresso")
    
    println(SLQ[Coffee](sql)(parameters).list)
    // => List(Coffee(Colombian,7.99,0,0), Coffee(Espresso,9.99,0,0))

    println(SLQ.query[Coffee](sql)(parameters).list)
    // => List(Coffee(Colombian,7.99,0,0), Coffee(Espresso,9.99,0,0))

    println(SLQ.queryMaps(sql)(parameters).list)
    // => List(Map(name -> Colombian, price -> 7.99, sales -> 0, total -> 0), Map(name -> Espresso, price -> 9.99, sales -> 0, total -> 0))
    
    println(SLQ.queryMapsWithTableNames(sql)(parameters).list)
    // => List(Map(coffee.name -> Colombian, coffee.price -> 7.99, coffee.sales -> 0, coffee.total -> 0), Map(coffee.name -> Espresso, coffee.price -> 9.99, coffee.sales -> 0, coffee.total -> 0))

    def queryWithImplicitMapMapping() {
      implicit val getMapResult = SLQ.getMap
      val q = Q[String, Map[String, Any]] + "select * from coffee where name = ?"
      println(q("Colombian").list)
    }

    def queryWithImplicitMapWithTableNamesMapping() {
	    implicit val getMapResult = SLQ.getMapWithTableNames
	    val q = Q[String, Map[String,Any]] + "select * from coffee where name = ?"
	    println(q("Colombian").list)
    }

    queryWithImplicitMapMapping()
    queryWithImplicitMapWithTableNamesMapping()
  }
}
