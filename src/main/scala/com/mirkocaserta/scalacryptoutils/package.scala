package com.mirkocaserta

import java.nio.charset.Charset
import java.security.{PrivateKey, PublicKey, KeyStore}

package object scalacryptoutils {

  sealed case class PrivateKeyException(msg: String) extends RuntimeException(msg)

  sealed case class PublicKeyException(msg: String) extends RuntimeException(msg)

  val privateKeyCache = collection.mutable.Map[String, PrivateKey]()

  implicit val charset: Charset = "UTF-8"

  implicit def stringToArrayOfBytes(input: String)(implicit charset: Charset): Array[Byte] =
    input.getBytes(charset)

  implicit def stringToByteArrayWrapper(input: String): ByteArrayWrapper =
    ByteArrayWrapper(input)

  implicit def byteArrayToByteArrayWrapper(input: Array[Byte]): ByteArrayWrapper =
    ByteArrayWrapper(input)

  implicit def byteArrayWrapperToArrayOfBytes(input: ByteArrayWrapper): Array[Byte] = input.bytes

  implicit def charsetNameToCharset(charsetName: String): Charset = Charset.forName(charsetName)

  implicit def stringToEncodableString(input: String): EncodableString =
    EncodableString(input)

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
