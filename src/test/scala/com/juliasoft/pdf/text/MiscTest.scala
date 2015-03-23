package com.juliasoft.pdf.text

import java.io.{File, FileInputStream}
import javax.xml.bind.DatatypeConverter

import com.twitter.util.Base64StringEncoder
import org.scalatest.FunSpec

import scala.io.Codec

class MiscTest extends FunSpec {
  val dir = new File("./samples/")

  describe("Misc tests") {
    it("Should encode file to Base64 string") {
      val f = new File("./system.properties")
      val encoded = Base64StringEncoder.encode(scala.io.Source.fromFile(f).map(_.toByte).toArray)
      println(encoded)
      val decoded = new String(Base64StringEncoder.decode(encoded))
      println(decoded)
    }

    it("Should encode to Base64") {
      val f = new File(dir, "mini-pdf-0.pdf")
      val arr = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(f))
      assert(arr.length === f.length())
      val encoded = Base64StringEncoder.encode(arr)
      val decoded = Base64StringEncoder.decode(encoded)
      assert(decoded.length === f.length())
      assert(arr === decoded)
    }

    it("Should encode big file to Base64 string") {
      val f = new File(dir, "mini-pdf-0.pdf")
      val arr = scala.io.Source.fromFile(f)(Codec.ISO8859).map(_.toByte).toArray
      assert(arr.length === f.length())
      val encoded = Base64StringEncoder.encode(arr)
      val decoded = Base64StringEncoder.decode(encoded)
      assert(decoded.length === f.length())
      assert(arr === decoded)
    }

    it("Should test DatatypeConverter from javax.xml.bind package") {
      val f = new File(dir, "mini-pdf-0.pdf")
      val arr = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(f))
      assert(arr.length === f.length())
      val encoded = DatatypeConverter.printBase64Binary(arr)
      val decoded = DatatypeConverter.parseBase64Binary(encoded)
      assert(decoded.length === f.length())
      assert(arr === decoded)
    }

    it("Should test Base64StringEncoder with Scala map (speed test 0)") {
      val f = new File(dir, "mini-pdf-1.pdf")
      val arr = scala.io.Source.fromFile(f)(Codec.ISO8859).map(_.toByte).toArray
      assert(arr.length === f.length())
      val encoded = Base64StringEncoder.encode(arr)
      val decoded = Base64StringEncoder.decode(encoded)
      assert(decoded.length === f.length())
      assert(arr === decoded)
    }

    it("Should test Base64StringEncoder (speed test 1)") {
      val f = new File(dir, "mini-pdf-1.pdf")
      val arr = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(f))
      assert(arr.length === f.length())
      val encoded = Base64StringEncoder.encode(arr)
      val decoded = Base64StringEncoder.decode(encoded)
      assert(decoded.length === f.length())
      assert(arr === decoded)
    }

    it("Should test DatatypeConverter (speed test 2)") {
      val f = new File(dir, "mini-pdf-1.pdf")
      val arr = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(f))
      assert(arr.length === f.length())
      val encoded = DatatypeConverter.printBase64Binary(arr)
      val decoded = DatatypeConverter.parseBase64Binary(encoded)
      assert(decoded.length === f.length())
      assert(arr === decoded)
    }

  }
}
