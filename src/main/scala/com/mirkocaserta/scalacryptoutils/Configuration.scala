package com.mirkocaserta.scalacryptoutils

import com.typesafe.config.{ConfigException, ConfigFactory}
import collection.JavaConversions._

trait Configuration {

  val mainConfig = ConfigFactory.load

  val ksConfig = mainConfig.getObject("keystores")
  val pubKeysConfig = mainConfig.getObject("publicKeys")
  val privKeysConfig = mainConfig.getObject("privateKeys")

  val keystores = (for {
    key <- ksConfig.keys
    c = ksConfig.toConfig.getObject(key).toConfig
    (location, password, t, provider) = (
      c.getString("location"),
      c.getString("password"),
      readValue("type", c.getString("type")).getOrElse("JKS"),
      readValue("provider", c.getString("provider")).getOrElse("SUN")
      )
    ks = keystore(location, password, t, provider)
  } yield key -> ks).toMap

  val privateKeys = (for {
    key <- privKeysConfig.keys
    c = privKeysConfig.toConfig.getObject(key).toConfig
    (alias, password, keystore) = (
      c.getString("alias"),
      c.getString("password"),
      c.getString("keystore")
      )
    ks = keystores(keystore)
  } yield key -> privateKey(alias, password)(ks)).toMap

  val publicKeys = (for {
    key <- pubKeysConfig.keys
    c = pubKeysConfig.toConfig.getObject(key).toConfig
    (alias, keystore) = (c.getString("alias"), c.getString("keystore"))
    ks = keystores(keystore)
  } yield key -> publicKey(alias)(ks)).toMap

  private def readValue[A](path: String, v: => A): Option[A] = {
    try {
      Option(v)
    } catch {
      case e: ConfigException.Missing => None
    }
  }
}
