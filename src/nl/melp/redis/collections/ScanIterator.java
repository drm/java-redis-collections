package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ScanIterator implements Iterator<byte[]> {
	private final Redis redis;
	private final byte[] operation;
	private final byte[] keyName;
	private final byte[] match;
	private int cursor;
	protected int localCursor;
	protected List<byte[]> buffer;

	public ScanIterator(Redis redis, byte[]operation, byte[] keyName) {
		this(redis, operation, null, keyName);
	}

	public ScanIterator(Redis redis, byte[]operation, byte[] match, byte[] keyName) {
		this(redis, 1000, operation, match, keyName);
	}

	public ScanIterator(Redis redis, int bufferSize, byte[]operation, byte[] match, byte[] keyName) {
		this.redis = redis;
		this.operation = operation;
		this.keyName = keyName;
		this.cursor = 0;
		this.localCursor = 0;
		this.match = match;
	}

	@Override
	public boolean hasNext() {
		if (this.buffer == null || this.localCursor >= this.buffer.size()) {
			if (this.buffer != null && this.cursor == 0) {
				// the previous command return 0 as cursor, which means there are no more pages left.
				return false;
			}
			try {
				List<Object> result;
				List<Object> args = new ArrayList<>();
				args.add(this.operation);
				if (keyName != null) {
					args.add(this.keyName);
				}
				int cursorPos = args.size();
				args.add(null);
				if (match != null) {
					args.add("MATCH");
					args.add(match);
				}
				do {
					args.set(cursorPos, Integer.toString(this.cursor));
					synchronized (redis) {
						result = redis.call(args.toArray());
					}
					this.cursor = Integer.parseInt(new String((byte[]) result.get(0)));
					this.buffer = (List<byte[]>) result.get(1);
					// skip over empty pages
				} while (this.cursor != 0 && this.buffer.size() == 0);
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
