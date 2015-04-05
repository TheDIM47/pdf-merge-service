package com.juliasoft.service.pdf

import io.finch.HttpResponse
import io.finch.route._
import io.finch.request._
import io.finch.request.items.ParamItem
import io.finch.response.{NotFound, BadRequest}

object Handlers {
  val handleRequestReaderErrors: PartialFunction[Throwable, HttpResponse] = {
    case NotPresent(ParamItem(p)) => BadRequest(
      JsonUtils.toJson(Map("error" -> "param_not_present", "param" -> p))
    )

    case NotPresent(body) => BadRequest(
      JsonUtils.toJson(Map("error" -> "body_not_present"))
    )

    case NotParsed(ParamItem(p), t, c) => BadRequest(
      JsonUtils.toJson(Map("error" -> "param_not_parsed", "param" -> p, "type" -> t, "cause" -> c))
    )

    case NotParsed(body, t, c) => BadRequest(
      JsonUtils.toJson(Map("error" -> "body_not_parsed", "type" -> t, "cause" -> c))
    )

    case NotValid(ParamItem(p), rule) => BadRequest(
      JsonUtils.toJson(Map("error" -> "param_not_valid", "param" -> p, "rule" -> rule))
    )
  }

  val handleRouterErrors: PartialFunction[Throwable, HttpResponse] = {
    case RouteNotFound(route) => NotFound(JsonUtils.toJson(Map("error" -> "route_not_found", "route" -> route)))
  }
}
