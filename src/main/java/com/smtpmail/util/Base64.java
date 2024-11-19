package com.smtpmail.util;

import com.smtpmail.exception.WrongBase64DecodeException;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class Base64 {
	private static final Encoder encoder = java.util.Base64.getMimeEncoder();
	private static final Decoder decoder = java.util.Base64.getMimeDecoder();

	public static String encode(byte[] bytes) {
		return new String(encoder.encode(bytes));
	}

	public static String encode(String str) {
		return Base64.encode(str.getBytes(StandardCharsets.UTF_8));
	}

	public static String encode(ByteBuf byteBuf) {
		return Base64.encode(byteBuf.array());
	}

	public static String decode(String str) throws WrongBase64DecodeException {
		byte[] decode = new byte[0];
		try {
			decode = decoder.decode(str.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new WrongBase64DecodeException();
		}
		return new String(decode);
	}
}
