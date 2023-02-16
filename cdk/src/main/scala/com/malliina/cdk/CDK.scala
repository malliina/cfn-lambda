package com.malliina.cdk

import com.malliina.cdk.LambdaPipeline.LambdaConf
import software.amazon.awscdk.core.{Environment, StackProps, App as AWSApp}

object CDK:
  val stackProps =
    StackProps
      .builder()
      .env(Environment.builder().account("297686094835").region("eu-west-1").build())
      .build()

  def main(args: Array[String]): Unit =
    val app = new AWSApp()

    val lambda = LambdaStack(app, "LambdaStack")
    val lambdaPipeline = LambdaPipeline(LambdaConf(lambda.code), app, "mylambda2")

    val assembly = app.synth()
