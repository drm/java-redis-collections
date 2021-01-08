package nl.melp.redis.collections;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;


public class ScanIteratorTest extends AbstractIntegrationTest {
	@Test
	public void testScan() throws IOException {
		for (int i = 0; i < 1000; i ++) {
			redis.call("SET", "foo:" + i, "Test");
		}

		ScanIterator it = new ScanIterator(redis, "SCAN".getBytes(), "foo:*".getBytes(), null);
		while (it.hasNext()) {
			byte[] r = it.next();
			redis.call("DEL", r);
		}

		Assert.assertEquals(0, ((List)redis.call("KEYS", "foo:*")).size());
	}

	@Test
	public void testScanEmptyPages() throws IOException {
		for (int i = 0; i < 1000; i ++) {
			redis.call("SET", "foo:" + i, "Test");
		}

		ScanIterator it = new ScanIterator(redis, 1, "SCAN".getBytes(), "foo:9*".getBytes(), null);
		int i = 0;
		while (it.hasNext()) {
			it.next();
			i ++;
		}
		Assert.assertEquals(111, i);

		redis.call("FLUSHDB");
	}

	@Test
	public void testHscan() throws IOException {
		for (int i = 0; i < 1000; i ++) {
			redis.call("HSET", "foo", Integer.toString(i), "Test");
		}

		Assert.assertEquals(Long.valueOf(1000L), redis.call("HLEN", "foo"));

		ScanIterator it = new ScanIterator(redis, "HSCAN".getBytes(), "foo".getBytes());
		while (it.hasNext()) {
			byte[] r = it.next();
			redis.call("HDEL", "foo", r);
		}

		Assert.assertEquals(Long.valueOf(0), redis.call("HLEN", "foo"));
	}
}
