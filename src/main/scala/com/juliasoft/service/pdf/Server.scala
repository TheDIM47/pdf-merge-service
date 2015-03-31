package com.juliasoft.service.pdf

import java.io.{File, FileInputStream}
import java.net.URL

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.finagle.httpx.{Response, Status}
import com.twitter.finagle.{Httpx, Service, SimpleFilter}
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.StrictLogging
import io.finch.jackson._
import io.finch.request._
import io.finch.response._
import io.finch.route._
import io.finch.{Endpoint => _, HttpRequest, HttpResponse, _}
import org.apache.pdfbox.util.PDFMergerUtility

import scala.util.Properties

object Server extends StrictLogging {
  implicit val objectMapper: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  val encodeHttp: EncodeResponse[String] = EncodeResponse("text/html")(s => s)

  case class SourceException(s: String) extends Exception(s)

  val handleDomainErrors: PartialFunction[Throwable, HttpResponse] = {
    case SourceException(src) => BadRequest(JsonUtils.toJson(Map("error" -> "source_error", "source" -> src)))
  }

  val handleExceptions = new SimpleFilter[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = service(req) handle
      (handleDomainErrors orElse Handlers.handleRequestReaderErrors orElse Handlers.handleRouterErrors orElse {
        case _ => InternalServerError()
      })
  }

  val api: Endpoint[HttpRequest, HttpResponse] = (Get /> Index) | (Get / "merges" /> ShowHome) | (Post / "merges" /> Merge)

  val backend: Service[HttpRequest, HttpResponse] = handleExceptions ! api

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
      logger.info("got data: " + req.getParam("data"))
      val paramParser: RequestReader[Seq[String]] = param("data").as[Seq[String]]

      val v = paramParser(req)
      logger.info(s"param parser result: ${v}")
      logger.info(s"param parser map: ${v.map(x => logger.info(s"Item: ${x}"))}")

      val pdfs = Await.result(v)
      logger.info(s"pdfs: ${pdfs}")
      val tmp = mergeUrlFiles(pdfs)
      val rep = if (tmp.nonEmpty) {
        val f = tmp.get
        val rep = Response(Status.Ok)
        val headers = Map("Content-Disposition" -> ("attachment; filename=\"" + f.getName + "\""),
          "Content-Type" -> "application/pdf",
          "Content-Length" -> f.length.toString
        )
        rep.withOutputStream { outputStream =>
          val arr = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(f))
          logger.info(s"File: ${f} Arr.length: ${arr.length}")
          outputStream.write(arr)
        }
        rep.headerMap.clear()
        headers.foreach(x => rep.headerMap.add(x._1, x._2))
        if (f.exists) {
          f.delete
        }
        rep
      } else {
        Ok("No files to process")
      }
      Future(rep)
    }
  }

  case class NoFilesToProcess(pdfs : Seq[String]) extends Exception(JsonUtils.toJson(Map("error" -> "no_files_to_process", "source" -> pdfs)))

  object ShowHome extends Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest) = Future(Ok(homePage)(encodeHttp))
  }

  def mergeUrlFiles(files: Seq[String]): Option[File] = {
    if (files == null || files.isEmpty) {
      None
    } else {
      val m = new PDFMergerUtility()
      files.par.foreach { x =>
        try {
          m.addSource(new URL(x).openStream())
        } catch {
          case e: Exception => println(e);
        }
      }
      val t = File.createTempFile("tmp", ".pdf")
      m.setDestinationFileName(t.getCanonicalPath)
      m.mergeDocuments()
      Some(t)
    }
  }

  val json = """[
               | "http://www.calendarpedia.com/download/2015-calendar-landscape-in-color.pdf",
               | "http://www.calendarpedia.com/download/2015-calendar-landscape.pdf",
               | "http://www.calendarpedia.com/download/2015-calendar-landscape-year-overview-in-color.pdf"
               |]""".stripMargin
  val homePage = s"""<html>
                    |<title>PDF Merge service</title>
                    |<body>
                    |<p>
                    |<h3>Hello from PDF merge application!</h3>
                    |Enter your json to process
                    |</p>
                    |<form action="/merges" method="post">
                    |<textarea name="data" rows="10" cols="100">${json}
      |</textarea>
      |<p>
      |<input type="submit" value="Submit" />
      |</p>
      |</form>
      |</body>
      |</html>""".stripMargin
}
