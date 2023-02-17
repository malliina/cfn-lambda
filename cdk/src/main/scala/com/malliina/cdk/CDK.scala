package com.malliina.cdk

import software.amazon.awscdk.{App as AWSApp, Environment, StackProps}

object CDK:
  val stackProps =
    StackProps
      .builder()
      .env(Environment.builder().account("297686094835").region("eu-west-1").build())
      .build()

  def main(args: Array[String]): Unit =
    val app = new AWSApp()

    val lambda = LambdaStack(app, "lambda-stack")
    val lambdaPipeline =
      LambdaPipeline(LambdaConf(lambda.code, lambda.constructId), app, "lambda-pipe")

    val assembly = app.synth()
