package com.juliasoft.service.pdf

import io.finch.HttpResponse
import io.finch.request.items.{BodyItem, ParamItem}
import io.finch.request.{NotParsed, NotPresent, NotValid}
import io.finch.response.{BadRequest, NotFound}
import io.finch.route.RouteNotFound

object Handlers {
  val handleRequestReaderErrors: PartialFunction[Throwable, HttpResponse] = {
    case NotPresent(ParamItem(p)) => BadRequest(
      JsonUtils.toJson(Map("error" -> "param_not_present", "param" -> p))
    )

    case NotPresent(BodyItem) => BadRequest(
      JsonUtils.toJson(Map("error" -> "body_not_present"))
    )

    case NotParsed(ParamItem(p), t, c) => BadRequest(
      JsonUtils.toJson(Map("error" -> "param_not_parsed", "param" -> p, "type" -> t, "cause" -> c))
    )

    case NotParsed(BodyItem, t, c) => BadRequest(
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
