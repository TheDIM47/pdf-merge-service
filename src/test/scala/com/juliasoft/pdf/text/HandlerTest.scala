package com.juliasoft.pdf.text

import com.juliasoft.service.pdf.Server
import com.twitter.finagle.httpx.{Method, Request, Response, Status}
import com.twitter.util.Future
import org.scalatest.FunSpec

class HandlerTest extends FunSpec {

  describe("Test service handlers") {
    it("Should parse list") {
      val json = """["A","B"]"""
      val request = Request("data" -> json)
      val f = Server.Merge(request)
      assert(f.isInstanceOf[Future[Response]])
      f.map(x => {
        assert(x.isInstanceOf[Response])
        assert(x.status == Status.Ok)
      })
    }

    it("Should parse zero list") {
      val json = """[""]"""
      val request = Request("data" -> json)
      request.contentString = json
      val f = Server.Merge(request)
      assert(f.isInstanceOf[Future[Response]])
      f.map(x => {
        assert(x.isInstanceOf[Response])
        assert(x.status == Status.Ok)
      })
    }

    it("Should return io.finch.request.NotPresent 2") {
      intercept[io.finch.request.NotValid] {
        val json = ""
        val request = Request("data" -> json)
        Server.Merge(request)
      }
    }

    it("Should test Backend for BadRequest") {
      val json = """[]"""
      val request = Request("data" -> json)
      request.method = Method.Post
      request.uri = "/merges"
      val f = Server.backend.apply(request)
      assert(f.isInstanceOf[Future[Response]])
      f.map(x => {
        assert(x.isInstanceOf[Response])
        assert(x.status == Status.BadRequest)
        assert(x.contentString == "[[\"error\",\"param_not_present\"],[\"param\",\"data\"]]")
      })
    }

    it("Should test Backend for Redirect") {
      val json = """[]"""
      val request = Request("data" -> json)
      request.method = Method.Post
      request.uri = "/"
      val f = Server.backend.apply(request)
      assert(f.isInstanceOf[Future[Response]])
      f.map(x => {
        assert(x.isInstanceOf[Response])
        assert(x.status == Status.SeeOther)
      })
    }

  }
}
