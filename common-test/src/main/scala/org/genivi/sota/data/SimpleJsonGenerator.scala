/**
 * Copyright: Copyright (C) 2016, ATS Advanced Telematic Systems GmbH
 * License: MPL-2.0
 */
package org.genivi.sota.data

import io.circe.{Json,JsonObject}
import org.scalacheck.{Arbitrary, Gen}

trait SimpleJsonGenerator {

  import Arbitrary._

  val simpleJsonPairGen: Gen[(String, Json)] = for {
    k <- Gen.identifier
    v <- arbitrary[String]
  } yield (k, Json.fromString(v))

  val simpleJsonGen: Gen[Json] = for {
    vs <- Gen.nonEmptyContainerOf[List, (String, Json)](simpleJsonPairGen)
  } yield Json.fromJsonObject(JsonObject.fromMap(vs.toMap))

  implicit lazy val arbSimpleJson: Arbitrary[Json] = Arbitrary(simpleJsonGen)
}

object SimpleJsonGenerator extends SimpleJsonGenerator
