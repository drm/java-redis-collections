package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

// NX: Don't update already existing elements. Always add new elements.
public class ByteArraySortedSet extends Collection<byte[]> implements Set<byte[]>, Queue<byte[]> {
	public ByteArraySortedSet(Redis redis, String keyName) {
		super(redis, keyName);
	}

	@Override
	public Iterator<byte[]> iterator() {
		AtomicInteger i = new AtomicInteger(0);
		return new Iterator<byte[]>() {
			@Override
			public boolean hasNext() {
				return i.get() < size();
			}

			@Override
			public byte[] next() {
				final String offset = Integer.toString(i.getAndIncrement());
				return ByteArraySortedSet.this.<List<byte[]>>call("ZRANGE", offset, offset).get(0);
			}
		};
	}

	@Override
	public int size() {
		return this.<Long>call("ZCARD").intValue();
	}

	@Override
	public boolean add(byte[] o) {
		return this.<Long>call("ZADD", "NX", Long.toString(System.nanoTime()), o) > 0;
	}

	@Override
	public boolean offer(byte[] bytes) {
		return add(bytes);
	}

	@Override
	public byte[] remove() {
		final List<?> response = call("ZPOPMIN");
		if (response.size() > 0) {
			return (byte[]) response.get(0);
		}
		return null;
	}

	@Override
	public byte[] poll() {
		return remove();
	}

	@Override
	public byte[] element() {
		return peek();
	}

	@Override
	public byte[] peek() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean addAll(java.util.Collection<? extends byte[]> collection) {
		boolean ret = false;
		for (byte[] obj : collection) {
			ret |= add(obj);
		}
		return ret;
	}

	@Override
	public Object[] toArray() {
		return this.<List<?>>call("ZRANGE", 0, -1).toArray();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		return this.<List<?>>call("ZRANGE", 0, -1).toArray(ts);
	}

	@Override
	public boolean contains(Object o) {
		return this.<Long>call("ZRANK", o) != null;
	}

	@Override
	public boolean remove(Object o) {
		return this.<Long>call("ZREM", o) > 0;
	}
}
