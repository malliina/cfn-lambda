package com.malliina.lambda

import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.jdk.CollectionConverters.ListHasAsScala

object S3EventHandler:
  // event name when an object has been uploaded from the AWS Console
  val ObjectCreatedPut = "ObjectCreated:Put"

class S3EventHandler extends RequestHandler[S3Event, String]:
  override def handleRequest(input: S3Event, context: Context): String =
    val messages = input.getRecords.asScala.map { record =>
      val event = record.getEventName
      val bucket = record.getS3.getBucket.getName
      val key = record.getS3.getObject.getKey
      s"Event '$event' with key '$key' in '$bucket'."
    }
    messages.foreach(println)
    ""
