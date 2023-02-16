package com.malliina.lambda

import java.time.Instant
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import org.slf4j.LoggerFactory

class LambdaHandler extends RequestHandler[ScheduledEvent, String]:
  val log = LoggerFactory.getLogger(getClass)

  override def handleRequest(event: ScheduledEvent, context: Context): String =
    val now = Instant.now()
    log.info(s"Hello, Lambda! The time is now $now.")
    log.warn("This is your final warning!")
    log.error("Boom")
    s"Handled event '${event.getId}'."
