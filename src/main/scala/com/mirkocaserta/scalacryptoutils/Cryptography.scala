package com.mirkocaserta.scalacryptoutils

import java.security.{PrivateKey, PublicKey}

sealed case class privKeyId(id: String)
sealed case class pubKeyId(id: String)

trait Cryptography extends Configuration {

  def using(privKey: privKeyId)(f: (PrivateKey) => Unit) {
    f(privateKeys(privKey.id))
  }

  def using(pubKey: pubKeyId)(f: (PublicKey) => Unit) {
    f(publicKeys(pubKey.id))
  }

}

