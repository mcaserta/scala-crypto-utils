package com.mirkocaserta

import java.nio.charset.Charset

package object scalacryptoutils {

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

}
