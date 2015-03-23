package com.juliasoft.service.pdf

import io.finch.HttpResponse
import io.finch.request.items.{BodyItem, ParamItem}
import io.finch.request.{NotParsed, NotPresent, NotValid}
import io.finch.response.{BadRequest, NotFound}
import io.finch.route.RouteNotFound

object Handlers {
  val handleRequestReaderErrors: PartialFunction[Throwable, HttpResponse] = {
    case NotPresent(ParamItem(p)) => BadRequest(
      JsonUtils.toJson("error" -> "param_not_present", "param" -> p)
    )

    case NotPresent(BodyItem) => BadRequest(
      JsonUtils.toJson("error" -> "body_not_present")
    )

    case NotParsed(ParamItem(p), _, _) => BadRequest(
      JsonUtils.toJson("error" -> "param_not_parsed", "param" -> p)
    )

    case NotParsed(BodyItem, _, _) => BadRequest(
      JsonUtils.toJson("error" -> "body_not_parsed")
    )

    case NotValid(ParamItem(p), rule) => BadRequest(
      JsonUtils.toJson("error" -> "param_not_valid", "param" -> p, "rule" -> rule)
    )
  }

  val handleRouterErrors: PartialFunction[Throwable, HttpResponse] = {
    case RouteNotFound(route) => NotFound(JsonUtils.toJson("error" -> "route_not_found", "route" -> route))
  }
}
