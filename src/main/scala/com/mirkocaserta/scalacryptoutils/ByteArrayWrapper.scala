package com.mirkocaserta.scalacryptoutils

import java.security.{MessageDigest, PublicKey, Signature, PrivateKey}

sealed case class ByteArrayWrapper(bytes: Array[Byte]) {

  def hex: String = hex(bytes)

  def base64: String = Base64.encode(bytes)

  override def toString = hex

  def digest: ByteArrayWrapper = digest("sha1")

  def digest(algorithm: String): ByteArrayWrapper =
  // TODO: pool digest instances
    ByteArrayWrapper(MessageDigest.getInstance(algorithm).digest(this.bytes))

  def verify(signature: ByteArrayWrapper, algorithm: String = "SHA1withRSA", provider: String = "SunRsaSign")(implicit publicKey: PublicKey): Boolean = {
    val s = Signature.getInstance(algorithm, provider)
    s.initVerify(publicKey)
    s.update(this.bytes)

    try {
      s.verify(signature.bytes)
    } catch {
      case _: java.lang.Exception => false
    }
  }

  def sign(implicit privateKey: PrivateKey): ByteArrayWrapper =
    sign("SHA1withRSA", "SunRsaSign")

  def sign(algorithm: String)(implicit privateKey: PrivateKey): ByteArrayWrapper =
    sign(algorithm, "SunRsaSign")

  def sign(algorithm: String, provider: String)(implicit privateKey: PrivateKey): ByteArrayWrapper = {
    // TODO: pool these instances
    val s = Signature.getInstance(algorithm, provider)
    s.initSign(privateKey)
    s.update(this.bytes)
    ByteArrayWrapper(s.sign())
  }

  private def hex(input: Array[Byte]): String = {
    val sb = new StringBuilder
    val len = input.length
    def addDigit(in: Array[Byte], pos: Int, len: Int, sb: StringBuilder) {
      if (pos < len) {
        val b: Int = in(pos)
        val msb = (b & 0xf0) >> 4
        val lsb = (b & 0x0f)
        sb.append((if (msb < 10) ('0' + msb).asInstanceOf[Char] else ('a' + (msb - 10)).asInstanceOf[Char]))
        sb.append((if (lsb < 10) ('0' + lsb).asInstanceOf[Char] else ('a' + (lsb - 10)).asInstanceOf[Char]))
        addDigit(in, pos + 1, len, sb)
      }
    }
    addDigit(input, 0, len, sb)
    sb.toString()
  }
}
