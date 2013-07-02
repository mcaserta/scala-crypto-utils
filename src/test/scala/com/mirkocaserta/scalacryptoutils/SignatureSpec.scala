package com.mirkocaserta.scalacryptoutils

import org.specs2.mutable.Specification

class SignatureSpec extends Specification with Cryptography {

  println(privateKeys)
  println(publicKeys)

  "The verifier" should {
    "return true with a good signature" in {
      val message = "Hello scala cryptographic world"

      using(privKeyId("privateKey2")) {
        implicit x =>
          using(pubKeyId("publicKey1")) {
            implicit y =>
              val signature = message sign
              val verified = message verify signature
              verified must_== (true)
          }
      }
    }
  }

  "The verifier" should {
    "return false with a bad signature" in {
      implicit val ks = keystore("keystore.jks", "password")
      implicit val pubKey = publicKey("test")
      val message = "Hello scala cryptographic world"
      val signature = "badbadbad"
      val verified = message verify signature
      verified must_== (false)
    }
  }

  "The verifier" should {
    "return false with a modified message" in {
      implicit val ks = keystore("keystore.jks", "password")
      implicit val privKey = privateKey("test", "password")
      implicit val pubKey = publicKey("test")
      val message = "Hello scala cryptographic world"
      val modifiedMessage = "XHello scala cryptographic world"
      val signature = message sign
      val verified = modifiedMessage verify signature
      verified must_== (false)
    }
  }

}
