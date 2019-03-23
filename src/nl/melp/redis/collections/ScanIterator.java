package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ScanIterator implements Iterator<byte[]> {
	private final Redis redis;
	private final byte[] operation;
	private final byte[] keyName;
	private int cursor;
	private int localCursor;
	private List<byte[]> buffer;

	public ScanIterator(Redis redis, byte[]operation, byte[] keyName) {
		this.redis = redis;
		this.operation = operation;
		this.keyName = keyName;
		this.cursor = 0;
		this.localCursor = 0;
	}


	@Override
	public boolean hasNext() {
		if (this.buffer == null || this.localCursor >= this.buffer.size()) {
			if (this.buffer != null && this.cursor == 0) {
				// the previous command return 0 as cursor, which means there are no more pages left.
				return false;
			}
			try {
				List<Object> result = redis.call(this.operation, this.keyName, Integer.toString(this.cursor));
				this.cursor = Integer.valueOf(new String((byte[]) result.get(0)));
				this.buffer = (List<byte[]>) result.get(1);
				this.localCursor = 0;
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return this.buffer.size() > localCursor;
	}

	@Override
	public byte[] next() {
		if (buffer.size() > localCursor) {
			byte[] ret = buffer.get(localCursor);
			localCursor ++;
			return ret;
		}
		return null;
	}
}
