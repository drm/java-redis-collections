package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SerializedMappedSet<K, V> implements Map<K, Set<V>> {
	private final byte[] prefix;
	private final int prefixLength;

	private final ISerializer<V> valueSerializer;
	private final ISerializer<K> keySerializer;
	private final Redis redis;
	private final SerializedSet<K> keys;

	public SerializedMappedSet(ISerializer<K> keySerializer, ISerializer<V> valueSerializer, Redis redis, String prefix) {
		this.redis = redis;

		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
		this.prefix = prefix.getBytes();
		this.prefixLength = this.prefix.length;

		this.keys = new SerializedSet<K>(keySerializer, redis, new String(this.withPrefix("_keys".getBytes())));
	}


	private byte[] withPrefix(byte[] value) {
		byte[] prefixedValue = new byte[this.prefixLength + value.length + 1];

		System.arraycopy(prefix, 0, prefixedValue, 0, prefixLength);
		System.arraycopy(value, 0, prefixedValue, prefixLength + 1, value.length);
		prefixedValue[prefixLength] = ':';
		return prefixedValue;
	}

	private <T> T call(String command, byte[]... args) {
		byte[][] rawArgs = new byte[args.length + 1][];
		rawArgs[0] = command.getBytes();
		System.arraycopy(args, 0, rawArgs, 1, args.length);
		try {
			return redis.call((Object[])rawArgs);
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public int size() {
		return keys.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object o) {
		return keys.contains(o);
	}

	@Override
	public boolean containsValue(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<V> get(Object o) {
		synchronized (redis) {
			if (!this.keys.contains(o)) {
				this.keys.add((K)o);
			}
			return new SerializedSet<>(this.valueSerializer, this.redis, new String(this.keySerializer.serialize((K)o)));
		}
	}

	@Override
	public Set<V> put(K k, Set<V> vs) {
		synchronized (redis) {
			Set<V> set = this.get(k);
			set.clear();
			set.addAll(vs);
			return set;
		}
	}

	@Override
	public Set<V> remove(Object o) {
		synchronized (redis) {
			Set<V> vs = this.get(o);
			vs.clear();
			return vs;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends Set<V>> map) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void clear() {
		synchronized (redis) {
			for (K key : keys) {
				call("DEL", keySerializer.serialize(key));
			}
			keys.clear();
		}
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(keys);
	}

	@Override
	public Collection<Set<V>> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<K, Set<V>>> entrySet() {
		throw new UnsupportedOperationException();
	}
}
