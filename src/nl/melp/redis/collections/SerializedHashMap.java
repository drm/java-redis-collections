package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SerializedHashMap<K extends Serializable, V extends Serializable> implements Map<K, V> {
	private final ByteArrayMap innerMap;
	private final ISerializer<K> keySerializer;
	private final ISerializer<V> valueSerializer;

	public SerializedHashMap(Redis redis, String keyName) {
		this(new Serializer.DefaultSerializer<>(), new Serializer.DefaultSerializer<>(), redis, keyName);
	}

	public SerializedHashMap(ISerializer<K> keySerializer, ISerializer<V> valueSerializer, Redis redis, String keyName) {
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
		this.innerMap = new ByteArrayMap(redis, keyName);
	}

	@Override
	public int size() {
		return innerMap.size();
	}

	@Override
	public boolean isEmpty() {
		return innerMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return innerMap.containsKey(keySerializer.serialize((K)o));
	}

	@Override
	public boolean containsValue(Object o) {
		return innerMap.containsValue(valueSerializer.serialize((V)o));
	}

	@Override
	public V get(Object o) {
		return (V)valueSerializer.deserialize(innerMap.get(keySerializer.serialize((K)o)));
	}

	@Override
	public V put(K k, V v) {
		return (V) innerMap.put(keySerializer.serialize(k), valueSerializer.serialize(v));
	}

	@Override
	public V remove(Object o) {
		return (V) innerMap.remove(keySerializer.serialize((K)o));
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		map.forEach(this::put);
	}

	@Override
	public void clear() {
		innerMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return innerMap.keySet().stream().map(keySerializer::deserialize).collect(Collectors.toSet());
	}
	@Override
	public Collection<V> values() {
		return innerMap.keySet().stream().map(valueSerializer::deserialize).collect(Collectors.toList());
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> set = new HashSet<>();
		for (K k : keySet()) {
			set.add(new Entry<K, V>() {
				@Override
				public K getKey() {
					return k;
				}

				@Override
				public V getValue() {
					return get(k);
				}

				@Override
				public V setValue(V o) {
					throw new UnsupportedOperationException("Not implemented");
				}
			});
		}
		return set;
	}
}
