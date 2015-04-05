package com.juliasoft.service.pdf

import java.io.File
import java.net.URL

import org.apache.pdfbox.util.PDFMergerUtility

object Model {
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

}
