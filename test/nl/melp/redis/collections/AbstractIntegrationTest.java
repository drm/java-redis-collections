package nl.melp.redis.collections;

import nl.melp.redis.Redis;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

abstract public class AbstractIntegrationTest {
	protected Redis redis;
	private Socket socket;

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
}
