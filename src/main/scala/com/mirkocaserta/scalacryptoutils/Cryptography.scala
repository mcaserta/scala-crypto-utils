package com.mirkocaserta.scalacryptoutils

import java.security._

sealed case class PrivateKeyException(msg: String) extends RuntimeException(msg)

sealed case class PublicKeyException(msg: String) extends RuntimeException(msg)

trait Cryptography {

  val privateKeyCache = collection.mutable.Map[String, PrivateKey]()

  def publicKey(alias: String)(implicit keystore: KeyStore): PublicKey =
    keystore.getCertificate(alias) match {
      case c: java.security.cert.Certificate => c.getPublicKey
      case _ => throw new PublicKeyException("public key not found: alias=%s" format alias)
    }

  def privateKey(alias: String, password: String)(implicit ks: KeyStore): PrivateKey = {
    privateKeyCache.get(alias) match {
      case p: Some[PrivateKey] => p.get
      case _ => {
        val pk =
          ks.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray)) match {
            case pkk: KeyStore.PrivateKeyEntry => pkk.getPrivateKey
            case _ => throw new PrivateKeyException("private key not found: alias=%s" format alias)
          }
        privateKeyCache.put(alias, pk)
        pk
      }
    }
  }

  def keystore(location: String, password: String, `type`: String = "JKS", provider: String = "SUN"): KeyStore = {
    val keystore = KeyStore.getInstance(`type`, provider)
    keystore.load(getClass.getClassLoader.getResourceAsStream(location), password.toCharArray)
    keystore
  }

}