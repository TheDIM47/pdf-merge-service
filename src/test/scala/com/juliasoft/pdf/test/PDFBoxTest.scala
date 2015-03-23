package com.juliasoft.pdf.test

import java.io.File
import java.net.URL

import com.juliasoft.service.pdf.Server
import org.apache.pdfbox.util.PDFMergerUtility
import org.scalatest.FunSpec

import scala.sys.process._

class PDFBoxTest extends FunSpec {
  val dir = new File("./samples/")

  val fileList = List("./samples/mini-pdf-0.pdf", "./samples/mini-pdf-1.pdf", "./samples/mini-pdf-0.pdf")

  def fileDownloader(url: String, filename: String = File.createTempFile("tmp", ".pdf").getCanonicalPath) = {
    new URL(url) #> new File(filename) !!
  }

  val urlList = List("http://www.calendarpedia.com/download/2015-calendar-landscape-in-color.pdf",
    "http://www.calendarpedia.com/download/2015-calendar-landscape.pdf",
    "http://www.calendarpedia.com/download/2015-calendar-landscape-year-overview-in-color.pdf"
  )

  val brokenList = List("http://www.calendarpedia.com/download/2015-calendar-landscape-in-color.pdf",
    "http://www.calendarpedia.com/download/BROKEN/LINK/FAKE-2015-calendar-landscape.pdf",
    "http://www.calendarpedia.com/download/2015-calendar-landscape-year-overview-in-color.pdf"
  )

  describe("Merge PDF files") {

    it("Should merge list of local files") {
      val f = new File(dir, "test-local.pdf")
      val m = new PDFMergerUtility()
      m.setDestinationFileName(f.getCanonicalPath)
      fileList.foreach(m.addSource(_))
      m.mergeDocuments()
      assert(f.exists)
      assert(f.length > 0)
      assert(f.delete)
    }

    it("Should merge URLs of AWS files") {
      val f = new File(dir, "test-local.pdf")
      val m = new PDFMergerUtility()
      m.setDestinationFileName(f.getCanonicalPath)
      urlList.par.foreach(x => m.addSource(new URL(x).openStream()))
      m.mergeDocuments()
      assert(f.exists)
      assert(f.length > 0)
      assert(f.delete)
    }

    it("Should merge URLs of BROKEN AWS files") {
      val f = new File(dir, "test-local.pdf")
      val m = new PDFMergerUtility()
      m.setDestinationFileName(f.getCanonicalPath)
      brokenList.par.foreach { x =>
        try {
          m.addSource(new URL(x).openStream())
        } catch {
          case e: Exception => println(e);
        }
      }
      m.mergeDocuments()
      assert(f.exists)
      assert(f.length > 0)
      assert(f.delete)
    }

  }
}
