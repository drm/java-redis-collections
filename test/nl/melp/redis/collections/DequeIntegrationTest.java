package nl.melp.redis.collections;

import junit.framework.TestCase;
import nl.melp.redis.Redis;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 *
 */
public class DequeIntegrationTest extends TestCase {
	private AtomicInteger incr = new AtomicInteger();

	private Supplier<ByteArrayList> factory = () -> {
		try {
			String keyName = DequeIntegrationTest.class.getCanonicalName() + "-" + incr.getAndIncrement();
			Redis.run((redis) -> redis.call("DEL", keyName), "localhost", 6379);
			return new ByteArrayList(new Redis(new Socket("localhost", 6379)), keyName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	};

	@Test
	public void testListBehaviour() {
		List<byte[]> list = factory.get();

		list.add("A".getBytes());
		list.add("B".getBytes());
		list.add("C".getBytes());

		assertEquals(3, list.size());

		assertEquals("A", new String(list.get(0)));
		assertEquals("B", new String(list.get(1)));
		assertEquals("C", new String(list.get(2)));

		try {
			assertEquals("C", new String(list.get(3)));
		} catch (NoSuchElementException e) {
			return;
		}
		fail("Expected NoSuchElementException");
	}

	@Test
	public void testQueueBehaviour() {
		ByteArrayList queue = factory.get();

		queue.add("A".getBytes());
		queue.add("B".getBytes());
		queue.add("C".getBytes());

		assertEquals(3, queue.size());

		assertEquals("A", new String(queue.element()));
		assertEquals("A", new String(queue.remove()));
		assertEquals("B", new String(queue.element()));
		assertEquals("B", new String(queue.remove()));
		assertEquals("C", new String(queue.element()));
		assertEquals("C", new String(queue.remove()));

		try {
			queue.remove();
		} catch (NoSuchElementException e) {
			return;
		}
		fail("Expected NoSuchElementException");
	}

	@Test
	public void testStackBehaviour() {
		Deque<byte[]> queue = factory.get();

		queue.push("A".getBytes());
		queue.push("B".getBytes());
		queue.push("C".getBytes());

		assertEquals(3, queue.size());

		assertEquals("C", new String(queue.pop()));
		assertEquals("B", new String(queue.pop()));
		assertEquals("A", new String(queue.pop()));

		try {
			queue.pop();
		} catch (NoSuchElementException e) {
			return;
		}
		fail("Expected NoSuchElementException");
	}


}
