package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ByteArrayMap extends RedisVar implements Map<byte[], byte[]> {
	public ByteArrayMap(Redis redis, String keyName) {
		super(redis, keyName);
	}

	@Override
	public boolean isEmpty() {
		return size() > 0;
	}

	@Override
	public int size() {
		return (int)(long)call("HLEN");
	}

	@Override
	public boolean containsKey(Object o) {
		return this.<Long>call("HEXISTS", (byte[])o) > 0;
	}

	@Override
	public boolean containsValue(Object o) {
		return values().contains(o);
	}

	@Override
	public byte[] get(Object o) {
		return call("HGET", (byte[])o);
	}

	@Override
	public byte[] put(byte[] s, byte[] s2) {
		// TODO implement a pipe for this, so it is transaction safe.
		call("HSET", s, s2);
		return null;
	}

	@Override
	public byte[] remove(Object o) {
		// TODO implement a pipe for this, so it is transaction safe.
		byte[] r = this.get(o);
		call("HDEL", (byte[])o);
		return r;
	}

	@Override
	public void putAll(Map<? extends byte[], ? extends byte[]> map) {
		map.forEach(this::put);
	}

	@Override
	public void clear() {
		call("DEL");
	}

	@Override
	public Set<byte[]> keySet() {
		return new HashSet<>(call("HKEYS"));
	}

	@Override
	public Collection<byte[]> values() {
		return call("HVALS");
	}

	@Override
	public Set<Entry<byte[], byte[]>> entrySet() {
		Set<Entry<byte[], byte[]>> set = new HashSet<>();
		for (byte[] k : keySet()) {
			set.add(new Entry<>() {
				@Override
				public byte[] getKey() {
					return k;
				}

				@Override
				public byte[] getValue() {
					return get(k);
				}

				@Override
				public byte[] setValue(byte[] o) {
					throw new UnsupportedOperationException("Not implemented");
				}
			});
		}
		return set;
	}


}
