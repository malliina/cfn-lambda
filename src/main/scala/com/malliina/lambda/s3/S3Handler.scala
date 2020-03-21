package com.malliina.lambda.s3

import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import scala.jdk.CollectionConverters.ListHasAsScala

class S3Handler extends RequestHandler[S3Event, String] {
  override def handleRequest(input: S3Event, context: Context): String = {
    val messages = input.getRecords.asScala.map { record =>
      val event = record.getEventName
      val bucket = record.getS3.getBucket.getName
      val key = record.getS3.getObject.getKey
      s"Event '$event' with key '$key' in '$bucket'."
    }
    messages.foreach(println)
    ""
  }
}
