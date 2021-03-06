package com.juliasoft.pdf.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.finagle.httpx.Request
import com.twitter.io.Buf.Utf8
import com.twitter.util.Await
import io.finch.jackson._
import io.finch.request.{RequestReader, body}
import org.jboss.netty.handler.codec.http.HttpHeaders
import org.scalatest.FunSpec

class JsonTest extends FunSpec {
  implicit val objectMapper: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  describe("Json teste") {

    it("Should decode to Seq[String]") {
      val pdf: RequestReader[Seq[String]] = body.as[Seq[String]]

      val bodyText = Utf8("""[]""")
      val req = Request()
      req.content = bodyText
      req.headerMap.update(HttpHeaders.Names.CONTENT_LENGTH, bodyText.length.toString)

      val expected: Seq[String] = Seq.empty
      assert(Await.result(pdf(req)) == expected)
    }

    it("Should decode to Seq[String] EXT") {
      val pdf: RequestReader[Seq[String]] = body.as[Seq[String]]

      val bodyText = Utf8("[\"A\",\"B\",\"C\"]")
      val req = Request()
      req.content = bodyText
      req.headerMap.update(HttpHeaders.Names.CONTENT_LENGTH, bodyText.length.toString)

      val expected: Seq[String] = Seq("A", "B", "C")
      assert(Await.result(pdf(req)) == expected)
    }

    it("Should decode to Seq[String] no urls") {
      val pdf: RequestReader[Seq[String]] = body.as[Seq[String]]

      val bodyText = Utf8("[]")
      val req = Request()
      req.content = bodyText
      req.headerMap.update(HttpHeaders.Names.CONTENT_LENGTH, bodyText.length.toString)

      val expected: Seq[String] = Seq.empty
      assert(Await.result(pdf(req)) == expected)
    }
  }
}