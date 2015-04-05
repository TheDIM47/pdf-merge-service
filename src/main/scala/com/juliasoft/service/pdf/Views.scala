package com.juliasoft.service.pdf

import scalajs._
import scalatags.Text.all._
import scalatags._

object Views {
    
  val json = """[
               | "http://www.calendarpedia.com/download/2015-calendar-landscape-in-color.pdf",
               | "http://www.calendarpedia.com/download/2015-calendar-landscape.pdf",
               | "http://www.calendarpedia.com/download/2015-calendar-landscape-year-overview-in-color.pdf"
               |]""".stripMargin
        
  val homePage = html(
    head(
      title := "PDF Merge service"
    ),
    body(
      h3("Hello from PDF merge application!"),
      div("Enter your json to process"),
      form(
        action := "/merges", method := "post",
        div(
          textarea( name := "data", rows := 10, cols := 100, json )
        ),
        input( tpe := "submit", value := "submit" )
      )
    )
  ).render
               
//  val hp = s"""<html>
//                    |<title>PDF Merge service</title>
//                    |<body>
//                    |<p>
//                    |<h3>Hello from PDF merge application!</h3>
//                    |Enter your json to process
//                    |</p>
//                    |<form action="/merges" method="post">
//                    |<textarea name="data" rows="10" cols="100">${json}
//      |</textarea>
//      |<p>
//      |<input type="submit" value="Submit" />
//      |</p>
//      |</form>
//      |</body>
//      |</html>""".stripMargin
}