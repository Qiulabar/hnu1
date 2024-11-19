package async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class TreadPoolTest {
	@Test
	public void test01() throws ExecutionException, InterruptedException {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("执行f1");
			return atomicInteger.getAndIncrement();
		});

		CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("执行f2");
			return atomicInteger.getAndIncrement();
		});

		CompletableFuture.allOf(f1, f2).get();
		System.out.println(f1.get());
		System.out.println(f2.get());
	}
}
