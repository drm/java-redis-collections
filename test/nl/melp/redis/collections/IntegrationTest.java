package nl.melp.redis.collections;

import nl.melp.redis.Redis;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {
	private final String keyName = IntegrationTest.class.getCanonicalName();
	private Redis redis;
	private Socket socket;

	@Test
	public void tmp () throws IOException {
		redis.call("SELECT", "0");
		System.out.println(
			new SerializedHashMap<>(redis, "nl.melp.linkchecker.LinkChecker.report.statuses").keySet().size()
		);
	}

	@Before
	public void init() throws IOException {
		socket = new Socket("localhost", 6379);
		redis = new Redis(socket);
		redis.call("SELECT", "15");
		Assert.assertEquals("Refusing to run on non-empty database", 0, ((List<?>) redis.call("KEYS", "*")).size());
	}

	@After
	public void cleanup() throws IOException {
		int size = ((List<?>) redis.call("KEYS", "*")).size();
		redis.call("QUIT");
		if (!socket.isClosed()) {
			socket.close();
		}
		if (size > 0) {
			Assert.fail("This test is littering");
		}
	}


	@Test
	public void testList() throws IOException {
		ByteArrayList s = new ByteArrayList(redis, keyName);

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

		s.clear();
		assertEquals(0, s.size());
		assertTrue(s.isEmpty());
	}

	@Test
	public void testMap() throws IOException {
		Map<byte[], byte[]> t = new ByteArrayMap(redis, keyName);
		assertEquals(0, t.size());
		t.put("Key".getBytes(), "Value".getBytes());
		assertEquals(1, t.size());
		assertTrue(t.containsKey("Key".getBytes()));
		assertEquals("Value", new String(t.get("Key".getBytes())));
		t.clear();
		assertEquals(0, t.size());
	}

	@Test
	public void testSerializedMap() throws IOException {
		Map<String, String> t = new SerializedHashMap<>(redis, keyName);
		assertEquals(0, t.size());
		t.put("Key", "Value");
		assertEquals(1, t.size());
		assertTrue(t.containsKey("Key"));
		assertEquals("Value", t.get("Key"));
		t.clear();
		assertEquals(0, t.size());
	}

	@Test
	public void testSet() throws Exception {
		ByteArraySet t = new ByteArraySet(redis, keyName);

		Iterator<byte[]> iterator = t.iterator();
		assertFalse(iterator.hasNext());
		assertFalse(iterator.hasNext());
		assertNull(iterator.next());

		// test empty set iteration
		for (byte[] b : t) {
			throw new Exception("Should be empty");
		}

		assertEquals(0, t.size());
		t.add("Hello".getBytes());
		assertEquals(1, t.size());

		// test single item iteration
		for (byte[] b : t) {
			assertEquals(new String(b), "Hello");
		}
		t.clear();

		assertEquals(0, t.size());
		t.add("Val1".getBytes());
		t.add("Val2".getBytes());
		t.add("Val2".getBytes());
		assertEquals(2, t.size());
		assertTrue(t.contains("Val1".getBytes()));
		assertTrue(t.contains("Val2".getBytes()));
		assertFalse(t.contains("Val3".getBytes()));
		assertTrue(t.remove("Val1".getBytes()));
		assertFalse(t.remove("Val1".getBytes()));
		t.clear();
		assertEquals(0, t.size());

		Set<String> s = new HashSet<>();
		for (int i = 0; i < 1000; i++) {
			String str = Integer.toString(i);
			s.add(str);
			t.add(str.getBytes());
		}

		List<String> l = new LinkedList<>();
		for (byte[] b : t) {
			l.add(new String(b));
		}
		assertTrue(s.containsAll(l));
		assertEquals(s.size(), l.size());

		t.clear();
	}

	@Test
	public void testSerializedSet() {
		SerializedSet<String> t = new SerializedSet<>(redis, keyName);

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

		t.clear();
		assertEquals(0, t.size());
		assertTrue(t.isEmpty());
	}

	@Test
	public void testSerializedSortedSet() {
		SerializedSortedSet<String> t = new SerializedSortedSet<>(redis, keyName);

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

		String b;
		while (null != (b = t.remove())) {
			l.add(b);
		}

		assertEquals("1234512345", String.join("", l));

		assertEquals(0, t.size());
		assertTrue(t.isEmpty());
	}

	@Test
	public void testBlockinDeque() {
		List<String> results = new LinkedList<>();
		Runnable listener = () -> {
			Deque<String> q = new SerializedBlockingDeque<>(redis, keyName, 1);
			results.add(q.remove());
		};

		SerializedList<String> input = new SerializedList<>(redis, keyName);

		input.add("1");
		input.add("2");
		input.add("3");
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);

		List<Future<?>> futures = new LinkedList<>();

		futures.add(pool.schedule(listener, 100, TimeUnit.MILLISECONDS));
		futures.add(pool.schedule(listener, 200, TimeUnit.MILLISECONDS));
		futures.add(pool.schedule(listener, 300, TimeUnit.MILLISECONDS));

		futures.forEach(future -> {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});

		assertTrue(results.contains("1"));
		assertTrue(results.contains("2"));
		assertTrue(results.contains("3"));

		assertEquals(3, results.size());
		assertEquals(0, input.size());

		results.clear();
		input.clear();
	}

	@Test
	public void testSerializedMappedSet() throws IOException {
		Map<String, Set<String>> values = new SerializedMappedSet<>(Serializers.of(String.class), Serializers.of(String.class), redis, keyName);
		Map<String, Set<String>> secondary = new SerializedMappedSet<>(Serializers.of(String.class), Serializers.of(String.class), redis, keyName);
		values.clear();

		assertFalse(values.get("foo").contains("bar"));
		values.get("foo").add("bar");
		assertTrue(values.get("foo").contains("bar"));
		assertTrue(secondary.get("foo").contains("bar"));
		values.get("foo").remove("bar");
		assertEquals(1, values.size());
		assertEquals(1, secondary.size());
		values.clear();
		assertEquals(0, values.size());
		assertEquals(0, secondary.size());
		assertFalse(values.get("foo").contains("bar"));
		assertFalse(secondary.get("foo").contains("bar"));

		values.get("A").add("1");
		values.get("A").add("2");
		values.get("A").add("3");
		values.get("B").add("1");
		values.get("B").add("2");
		values.get("B").add("3");

		List<String> l = new ArrayList<>();
		for (Map.Entry<String, Set<String>> e : values.entrySet()) {
			l.add(e.getKey() + "." + e.getValue());
		}

		assertFalse(values.isEmpty());

		values.clear();
		assertEquals(0, values.size());
		assertTrue(values.isEmpty());

		assertEquals(0, secondary.size());
		assertTrue(secondary.isEmpty());
	}

	@Test
	public void testSerializedMappedSetForInForEach() throws IOException {
		Map<String, Set<String>> map = new SerializedMappedSet<>(Serializers.of(String.class), Serializers.of(String.class), redis, keyName);
		map.clear();

		map.get("A").add("1");
		map.get("A").add("2");
		map.get("A").add("3");
		map.get("B").add("1");
		map.get("B").add("2");
		map.get("B").add("3");

		Set<String> pairs = new HashSet<>();
		Set<String> pairs2 = new HashSet<>();

		for (Entry<String, Set<String>> entry : map.entrySet()) {
			for (String s : entry.getValue()) {
				pairs.add(entry.getKey() + "." + s);
			}
		}

		map.forEach((k, v) -> v.forEach(s -> pairs2.add(k + "." + s)));

		Assert.assertEquals(6, pairs.size());
		for (String expected : new String[]{"A.1", "A.2", "A.3", "B.1", "B.2", "B.3",}) {
			Assert.assertTrue(pairs.contains(expected));
			Assert.assertTrue(pairs2.contains(expected));
		}

		map.clear();
	}


	@Test
	public void testSerializedMappedSetKeySynchronization() throws IOException {
		Map<String, Set<String>> map = new SerializedMappedSet<>(Serializers.of(String.class), Serializers.of(String.class), redis, keyName);
		Assert.assertEquals(0, map.get("A").size());
		Assert.assertEquals(0, map.size());
		map.remove("A");
		Assert.assertEquals(0, map.size());
	}
}
