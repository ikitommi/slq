package ikitommi.slick.jdbc

import scala.slick.jdbc.GetResult
import scala.slick.jdbc.SetParameter
import scala.slick.jdbc.StaticQuery
import scala.slick.session.PositionedParameters
import scala.slick.session.PositionedResult

object StaticListQuery {

  protected[this] class GetMap(tableNames: Boolean) extends GetResult[Map[String, Any]] {
    def apply(r: PositionedResult) = {
      var map = Map[String, Any]()
      while (r.hasMoreColumns) {
        val value = r.nextObject // mutates, need to call this before reading resultSet
        val columnName = r.rs.getMetaData().getColumnLabel(r.currentPos).toLowerCase() // TODO: externalize toLowerCase
        val key = if (!tableNames) columnName else {
          val tableName = r.rs.getMetaData().getTableName(r.currentPos).toLowerCase()
          s"$tableName.$columnName"
        }
        map += ((key, value))
      }
      map
    }
  }

  val getMap = new GetMap(false)
  val getMapWithTableNames = new GetMap(true)

  object SetListParameter extends SetParameter[List[Any]] {
    def apply(param: List[Any], pp: PositionedParameters): Unit =
      param.iterator.foreach(v => SetParameter.SetSimpleProduct(Tuple1(v), pp))
  }

  def query[R](sql: String)(parameters: List[Any])(implicit rconv: GetResult[R]) =
    StaticQuery.query[List[Any], R](sql)(GetResult[R], SetListParameter)(parameters)

  def queryMaps(sql: String)(parameters: List[Any]) =
    StaticQuery.query[List[Any], Map[String, Any]](sql)(getMap, SetListParameter)(parameters)

  def queryMapsWithTableNames(sql: String)(parameters: List[Any]) =
    StaticQuery.query[List[Any], Map[String, Any]](sql)(getMapWithTableNames, SetListParameter)(parameters)

  def apply[R](sql: String)(parameters: List[Any])(implicit rconv: GetResult[R]) = query[R](sql)(parameters)
}