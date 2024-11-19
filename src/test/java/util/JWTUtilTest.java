package util;

import com.smtpmail.start.SmtpMailApplication;
import com.smtpmail.entity.UserToken;
import com.smtpmail.exception.TokenException;
import com.smtpmail.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmtpMailApplication.class)
public class JWTUtilTest {
	@Autowired
	JWTUtil jwtUtil;

	@Test
	public void validateToken() {
		String token = "Lunangangster-eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpc1B1Ymxpc2hlclwiOjAsXCJsZXZlbFwiOjAsXCJ1c2VybmFtZVwiOlwiMTQ2Mjc0OTQ5MEBsdW5hbmdhbmdzdGVyLnN0b3JlXCJ9IiwiZXhwIjoxNjUyNDUxODgzfQ.Bwe08t8uJ3VQmbjqUFFpkQ7P_ttBFvdA17xXfKbfK69W2GJqyT7lb65ghj9bZygCsjK6Y6cEgCNV5R3CqGuohw";
		try {
			String s = JWTUtil.validateToken(token);
			System.out.println(s);
		} catch (TokenException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void test01() {
		UserToken userToken = jwtUtil.getUserToken(
				"Lunangangster-eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpc1B1Ymxpc2hlclwiOjAsXCJsZXZlbFwiOjAsXCJ1c2VybmFtZVwiOlwiMTQ2Mjc0OTQ5MEBsdW5hbmdhbmdzdGVyLnN0b3JlXCJ9IiwiZXhwIjoxNjUyNDUxODgzfQ.Bwe08t8uJ3VQmbjqUFFpkQ7P_ttBFvdA17xXfKbfK69W2GJqyT7lb65ghj9bZygCsjK6Y6cEgCNV5R3CqGuohw");
		System.out.println(userToken);
	}
}
