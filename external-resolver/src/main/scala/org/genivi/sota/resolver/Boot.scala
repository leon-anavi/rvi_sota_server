/**
 * Copyright: Copyright (C) 2015, Jaguar Land Rover
 * License: MPL-2.0
 */
package org.genivi.sota.resolver

import org.genivi.sota.core.db.Packages
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.NoContent
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer
import org.genivi.sota.resolver.db.Vins



object Boot extends App with Protocols {
  implicit val system = ActorSystem("sota-external-resolver")
  implicit val materializer = ActorMaterializer()
  implicit val exec = system.dispatcher
  implicit val log = Logging(system, "boot")

  import akka.http.scaladsl.server.Directives._

  log.info(org.genivi.sota.resolver.BuildInfo.toString)

  val route =
    path("addVin") {
      (post & entity(as[Vin])) { vin =>
        complete(Vins.create(vin))
      }
    } ~
    path("packages") {
      get {
        complete {
          NoContent
          //Packages.list
        }
      } ~
        (post & entity(as[Package])) { newPackage =>
        complete {
          Packages.create(newPackage)
        }
      }
    }

  val host = system.settings.config.getString("server.host")
  val port = system.settings.config.getInt("server.port")
  val bindingFuture = Http().bindAndHandle(route, host, port)

  log.info(s"Server online at http://${host}:${port}/")
}
