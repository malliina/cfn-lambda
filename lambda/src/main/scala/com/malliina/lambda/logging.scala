package com.malliina.lambda

import ch.qos.logback.classic.pattern.{MessageConverter, ThrowableProxyConverter}
import ch.qos.logback.classic.spi.{ILoggingEvent, IThrowableProxy}
import org.slf4j.{Logger, LoggerFactory}

object AppLogger:
  def apply(cls: Class[?]): Logger =
    val name = cls.getName.reverse.dropWhile(_ == '$').reverse
    LoggerFactory.getLogger(name)

class StackTraceSingleLineConverter extends ThrowableProxyConverter:
  override def throwableProxyToString(tp: IThrowableProxy): String =
    super.throwableProxyToString(tp).trim.replaceAll("\\s+", " ")

class SingleLineConverter extends MessageConverter:
  override def convert(event: ILoggingEvent): String =
    super.convert(event).trim.replaceAll("\\s+", " ")
