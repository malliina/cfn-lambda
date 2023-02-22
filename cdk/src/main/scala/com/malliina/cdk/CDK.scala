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

    val prefix = "test"
    val lambda = LambdaStack(app, s"$prefix-stack")
    val lambdaPipeline =
      LambdaPipeline(LambdaConf(lambda.code, lambda.constructId), app, s"$prefix-pipe")
    val assembly = app.synth()
