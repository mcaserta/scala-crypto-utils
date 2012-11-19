package com.mirkocaserta.scalacryptoutils

import org.specs2.mutable.Specification

class Base64Spec extends Specification {

  implicit def stringToSeqOfBytes(in: String): Seq[Byte] =
    in.getBytes("UTF-8").toSeq

  "The encoder" should {
    "return a certain string with 'Hello' as input" in {
      "Hello".encodeBase64 must_== "SGVsbG8="
    }
    "return a certain string with 'Hello specs2 world' as input" in {
      "Hello specs2 world".encodeBase64 must_== "SGVsbG8gc3BlY3MyIHdvcmxk"
    }
  }

  "The decoder" should {
    "return 'Hello' with the right input" in {
      "SGVsbG8=".decodeBase64 must_== stringToSeqOfBytes("Hello")
    }
    "return 'Hello specs2 world' with the right input" in {
      "SGVsbG8gc3BlY3MyIHdvcmxk".decodeBase64 must_== stringToSeqOfBytes("Hello specs2 world")
    }
  }
}
