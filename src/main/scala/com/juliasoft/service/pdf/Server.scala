package com.juliasoft.service.pdf

import java.io.FileInputStream

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.finagle.httpx.Status
import com.twitter.finagle.{Httpx, Service, SimpleFilter}
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.StrictLogging
import io.finch.jackson._
import io.finch.request._
import io.finch.response._
import io.finch.route._
import io.finch.{Endpoint => _, HttpRequest, HttpResponse}

import scala.util.Properties

object Server extends StrictLogging {
  implicit val objectMapper: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  val encodeHttp: EncodeResponse[String] = EncodeResponse("text/html")(s => s)

  case class SourceException(s: String) extends Exception(s)

  case class NoFilesToProcess(s: String) extends Exception(s)

  val handleDomainErrors: PartialFunction[Throwable, HttpResponse] = {
    case SourceException(src) => BadRequest(JsonUtils.toJson(Map("error" -> "source_error", "source" -> src)))
    case NoFilesToProcess(src) => BadRequest(JsonUtils.toJson(Map("error" -> "no_files_to_process", "source" -> src)))
  }

  val handleExceptions = new SimpleFilter[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = service(req) handle
      (handleDomainErrors orElse Handlers.handleRequestReaderErrors orElse Handlers.handleRouterErrors orElse {
        case _ => InternalServerError()
      })
  }

  val api: Endpoint[HttpRequest, HttpResponse] = (Get /> Index) | (Get / "merges" /> ShowHome) | (Post / "merges" /> Merge)

  val backend: Service[HttpRequest, HttpResponse] = handleExceptions andThen api

  def main(args: Array[String]) {
    val port = Properties.envOrElse("PORT", "9000").toInt
    println("Starting on port: " + port)
    Await.ready(Httpx.serve(":" + port, backend))
  }

  object Index extends Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest) = {
      val r = Redirect("/merges")
      r(req)
    }
  }

  object Merge extends Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest) = {
      logger.info("got content: " + req.getContentString())
      logger.info("got params: " + req.getParamNames())
      logger.info("got data: " + param("data"))
      val paramParser: RequestReader[Seq[String]] = param("data").as[Seq[String]]

      val v = paramParser(req)
      logger.info(s"param parser result: [$v] map: [${v.map(x => logger.info(s"Item: $x"))}]")

      v flatMap { pdfs =>
        logger.info(s"pdfs: $pdfs")
        Model.mergeUrlFiles(pdfs) match {
          case Some(f) =>
            val rep = new ResponseBuilder(Status.Ok,
              Map("Content-Disposition" -> ("attachment; filename=\"" + f.getName + "\""),
                "Content-Type" -> "application/pdf",
                "Content-Length" -> f.length.toString
              )).apply()
            rep.withOutputStream { outputStream =>
              val arr = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(f))
              logger.info(s"File: ${f} Arr.length: ${arr.length}")
              outputStream.write(arr)
            }
            Future(rep)
          case None =>
            Future(Ok("No files to process"))
        } // model merge
      } // v flatMap
    } // apply
  }

  object ShowHome extends Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest) = Future(Ok(Views.homePage)(encodeHttp))
  }

}
