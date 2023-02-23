package com.malliina.lambda

object MainApp:
  val log = AppLogger(getClass)
  def main(args: Array[String]): Unit =
    log.error(
      """Matrix X:
        |A B
        |C D
        |""".stripMargin,
      Exception("Kaboom!")
    )
