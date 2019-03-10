package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ByteArraySet extends Collection<byte[]> implements Set<byte[]> {
	public ByteArraySet(Redis redis, String keyName) {
		super(redis, keyName);
	}

	@Override
	public Iterator<byte[]> iterator() {
		return this.<List<byte[]>>call("SMEMBERS").iterator();
	}

	@Override
	public int size() {
		return this.<Long>call("SCARD").intValue();
	}

	@Override
	public boolean add(byte[] o) {
		return this.<Long>call("SADD", o) > 0;
	}

	@Override
	public boolean addAll(java.util.Collection<? extends byte[]> collection) {
		return this.<Long>call("SADD", collection.toArray(new byte[0][])) > 0;
	}

	@Override
	public Object[] toArray() {
		return this.<List<?>>call("SMEMBERS").toArray();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		return this.<List<?>>call("SMEMBERS").toArray(ts);
	}

	@Override
	public boolean contains(Object o) {
		return this.<Long>call("SISMEMBER", (byte[]) o) > 0;
	}

	@Override
	public boolean remove(Object o) {
		return this.<Long>call("SREM", (byte[]) o) > 0;
	}
}
