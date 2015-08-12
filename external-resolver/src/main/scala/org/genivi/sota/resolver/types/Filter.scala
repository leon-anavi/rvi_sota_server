/**
 * Copyright: Copyright (C) 2015, Jaguar Land Rover
 * License: MPL-2.0
 */
package org.genivi.sota.resolver.types

import eu.timepit.refined.{Predicate, Refined}
import org.genivi.sota.refined.SprayJsonRefined.refinedJsonFormat
import org.genivi.sota.resolver.types.FilterParser.parseFilter
import org.genivi.sota.rest.Validation
import spray.json.DefaultJsonProtocol._


case class FilterId(id: Long)

case class Filter (
  id        : Option[FilterId],
  name      : Filter.Name,
  expression: Filter.Expression
)

object Filter {

  trait ValidName
  trait ValidExpression

  type Name       = Refined[String, ValidName]
  type Expression = Refined[String, ValidExpression]

  implicit val validFilterName: Predicate[ValidName, String] =
    Predicate.instance (name =>
      name.length > 1 && name.length <= 100 &&
        name.forall(c => c.isLetter || c.isDigit),
      name => s"($name should be between two and a hundred alphanumeric characters long.)")

  implicit val validFilterExpression: Predicate[ValidExpression, String] =
    Predicate.instance (expr => parseFilter(expr).isRight,
      expr => parseFilter(expr) match {
        case Left(e)  => s"($expr failed to parse: $e.)"
        case Right(_) => "IMPOSSIBLE"
    })

  implicit val filterIdFormat   = jsonFormat1(FilterId.apply)
  implicit val filterFormat     = jsonFormat3(Filter.apply)
  implicit val filterListFormat = seqFormat[Filter]
}
