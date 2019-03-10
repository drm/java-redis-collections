package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.List;

public class BlockingByteArrayDeque extends ByteArrayList {
	private final int timeoutSecs;

	public BlockingByteArrayDeque(Redis redis, String keyName, int timeoutSecs) {
		super(redis, keyName);
		this.timeoutSecs = timeoutSecs;
	}

	@Override
	public byte[] removeLast() {
		return doPop("BRPOP");
	}

	@Override
	public byte[] removeFirst() {
		return doPop("BLPOP");
	}

	private byte[] doPop(String op) {
		List<byte[]> r = call(op, Integer.toString(timeoutSecs).getBytes());
		if (r == null) {
			return null;
		}
		return r.get(1);
	}
}
