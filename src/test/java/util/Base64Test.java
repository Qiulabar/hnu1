package util;

import com.smtpmail.exception.WrongBase64DecodeException;
import com.smtpmail.util.Base64;
import org.junit.jupiter.api.Test;

public class Base64Test {

	@Test
	public void test01() {
		String des = Base64.encode("哈哈");
		System.out.println(des);
	}

	@Test
	public void test02() throws WrongBase64DecodeException {
		String src = Base64.decode("5ZOI5ZOI");
		System.out.println(new String(src));
	}

	@Test
	public void test03() {
		System.out.println(Base64.encode("1462749490@qq.com"));
		System.out.println(Base64.encode("hjxwionfpfjjhedg"));
	}

	@Test
	public void test04() throws WrongBase64DecodeException {
		System.out.println(Base64.decode("MTQ2Mjc0OTQ5MEBxcS5jb20="));
		System.out.println(Base64.decode("aGp4d2lvbmZwZmpqaGVkZw=="));
	}

	@Test
	public void test05() throws WrongBase64DecodeException {
		System.out.println(Base64.decode("哈"));
	}

}
