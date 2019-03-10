package nl.melp.redis.collections;

import nl.melp.redis.Redis;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {
	private final String keyName = IntegrationTest.class.getCanonicalName();

	private void clear() {
		try {
			Redis.run((redis) -> redis.call("DEL", keyName), "localhost", 6379);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testList() throws IOException {
		clear();
		try (Socket socket = new Socket("localhost", 6379)) {
			ByteArrayList s = new ByteArrayList(new Redis(socket), keyName);

			s.push("A".getBytes());
			s.push("B".getBytes());
			s.push("C".getBytes());

			assertEquals(3, s.size());
			assertEquals("C", new String(s.pop()));
			assertEquals("B", new String(s.pop()));
			assertEquals("A", new String(s.pop()));

			assertEquals(0, s.size());
			assertTrue(s.isEmpty());

			s.push("A".getBytes());
			s.push("B".getBytes());
			assertEquals("A", new String(s.get(0)));
		}
	}

	@Test
	public void testMap() throws IOException {
		clear();

		try (Socket socket = new Socket("localhost", 6379)) {
			Map<byte[], byte[]> t = new ByteArrayMap(new Redis(socket), keyName);
			assertEquals(0, t.size());
			t.put("Key".getBytes(), "Value".getBytes());
			assertEquals(1, t.size());
			assertTrue(t.containsKey("Key".getBytes()));
			assertEquals("Value", new String(t.get("Key".getBytes())));
			t.clear();
			assertEquals(0, t.size());
		}

		clear();
	}

	@Test
	public void testSerializedMap() throws IOException {
		clear();

		try (Socket socket = new Socket("localhost", 6379)) {
			Map<String, String> t = new SerializedHashMap<>(new Redis(socket), keyName);
			assertEquals(0, t.size());
			t.put("Key", "Value");
			assertEquals(1, t.size());
			assertTrue(t.containsKey("Key"));
			assertEquals("Value", t.get("Key"));
			t.clear();
			assertEquals(0, t.size());
		}
	}

	@Test
	public void testSet() throws IOException {
		clear();

		try (Socket socket = new Socket("localhost", 6379)) {
			ByteArraySet t = new ByteArraySet(new Redis(socket), keyName);

			assertEquals(0, t.size());
			t.add("Val1".getBytes());
			t.add("Val2".getBytes());
			t.add("Val2".getBytes());
			assertEquals(2, t.size());
			assertTrue(t.contains("Val1".getBytes()));
			assertTrue(t.contains("Val2".getBytes()));
			assertTrue(!t.contains("Val3".getBytes()));
			assertTrue(t.remove("Val1".getBytes()));
			assertTrue(!t.remove("Val1".getBytes()));
			t.clear();
			assertEquals(0, t.size());

			t.add("1".getBytes());
			t.add("2".getBytes());
			t.add("3".getBytes());
			t.add("4".getBytes());
			t.add("5".getBytes());

			List<String> l = new LinkedList<>();
			for (byte[] b : t) {
				l.add(new String(b));
			}
			l.sort(Comparator.naturalOrder());

			assertEquals("12345", String.join("", l));
		}

		clear();
	}

	@Test
	public void testSerializedSet() throws IOException {
		clear();

		try (Socket socket = new Socket("localhost", 6379)) {
			SerializedSet<String> t = new SerializedSet<String>(new Redis(socket), keyName);

			assertEquals(0, t.size());
			t.add("Val1");
			t.add("Val2");
			t.add("Val2");
			assertEquals(2, t.size());
			assertTrue(t.contains("Val1"));
			assertTrue(t.contains("Val2"));
			assertTrue(!t.contains("Val3"));
			assertTrue(t.remove("Val1"));
			assertTrue(!t.remove("Val1"));
			t.clear();
			assertEquals(0, t.size());

			t.add("1");
			t.add("2");
			t.add("3");
			t.add("4");
			t.add("5");

			List<String> l = new LinkedList<>();
			for (String b : t) {
				l.add(b);
			}
			l.sort(Comparator.naturalOrder());

			assertEquals("12345", String.join("", l));
		}

		clear();
	}
}