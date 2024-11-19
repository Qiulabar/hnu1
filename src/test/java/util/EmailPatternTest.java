package util;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class EmailPatternTest {
	private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_-]+@lunangangster.xyz$");

	@Test
	public void test01() {
		System.out.println(emailPattern.matcher("1@lunangangster.xyz").matches());
	}
}
