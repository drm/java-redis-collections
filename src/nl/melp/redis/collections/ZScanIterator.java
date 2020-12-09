package nl.melp.redis.collections;

import nl.melp.redis.Redis;

public class ZScanIterator extends ScanIterator {
	public ZScanIterator(Redis redis, byte[] operation, byte[] keyName) {
		super(redis, operation, keyName);
	}

	@Override
	public byte[] next() {
		if (buffer.size() > localCursor) {
			byte[] ret = buffer.get(localCursor);
			localCursor += 2;
			return ret;
		}
		return null;
	}
}
