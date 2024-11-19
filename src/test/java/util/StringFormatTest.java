package util;

import org.junit.jupiter.api.Test;

public class StringFormatTest {
	@Test
	public void test01() {
		System.out.println(String.format("502 Invalid input from %s:%d to smtp.kid.com", "127.0.0.1", 8001));
	}
}
