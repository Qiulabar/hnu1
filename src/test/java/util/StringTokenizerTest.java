package util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.junit.jupiter.api.Test;

public class StringTokenizerTest {
	@Test
	public void test01() {
		String str = "HELO kid abc";
		StringTokenizer tokenizer = new StringTokenizer(str);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			System.out.println(token);
		}
	}

	@Test
	public void test02() {
		String commandString = "MAIL TO:<1462749490@qq.com>";
		StringTokenizer tokenizer = new StringTokenizer(commandString, ":", false);
		while (tokenizer.hasMoreTokens()) {
			System.out.println(tokenizer.nextToken());
		}
	}

	@Test
	public void test03() {
		String data = "from:123\nto:aa\nsubject:cc\n\nhello\n.\n";
		StringTokenizer tokenizer = new StringTokenizer(data);
		while (tokenizer.hasMoreTokens()) {
			System.out.println(tokenizer.nextToken());
		}
	}

	@Test
	public void test04() {
		String data = "from:123\nto:aa\nsubject:cc\n\nhello\n.\n";
		int i = data.indexOf("\n\n");
		System.out.println(data.substring(0, i));
	}

	@Test
	public void test05() {
		Map<String, String> map = new HashMap<>();
		map.put("FROM", "a");
		System.out.println(map.get("FROM"));
		System.out.println(map.get("TO"));
	}
}
