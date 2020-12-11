package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SerializedSet<V> implements Set<V>, Collection<V> {
	private final ByteArraySet innerSet;
	private final ISerializer<V> serializer;

	public SerializedSet(Redis redis, String keyName) {
		this(new Serializer.DefaultSerializer<>(), redis, keyName);
	}

	public SerializedSet(ISerializer<V> serializer, Redis redis, String keyName) {
		this.serializer = serializer;
		this.innerSet = new ByteArraySet(redis, keyName);
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return innerSet.size();
	}

	@Override
	public boolean add(V o) {
		return innerSet.add(serializer.serialize(o));
	}

	@Override
	public boolean addAll(java.util.Collection<? extends V> collection) {
		return innerSet.addAll(collection.stream().map(serializer::serialize).collect(Collectors.toSet()));
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean contains(Object o) {
		return innerSet.contains(serializer.serialize((V)o));
	}

	@Override
	public boolean remove(Object o) {
		return innerSet.remove(serializer.serialize((V)o));
	}

	@Override
	public Iterator<V> iterator() {
		Iterator<byte[]> innerIterator = innerSet.iterator();
		return new Iterator<V>() {
			@Override
			public boolean hasNext() {
				return innerIterator.hasNext();
			}

			@Override
			public V next() {
				return serializer.deserialize(innerIterator.next());
			}
		};
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		for (Object item : collection) {
			if (!contains(item)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		AtomicBoolean b = new AtomicBoolean(false);
		collection.forEach(item -> {
			if (this.remove(item)) {
				b.set(true);
			}
		});
		return b.get();
	}

	@Override
	public void clear() {
		innerSet.clear();
	}
}
