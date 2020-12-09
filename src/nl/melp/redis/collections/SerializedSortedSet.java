package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class SerializedSortedSet<V> implements Set<V>, Collection<V>, Queue<V> {
	private final ByteArraySortedSet innerSet;
	private final ISerializer<V> serializer;

	public SerializedSortedSet(Redis redis, String keyName) {
		this(new Serializer.DefaultSerializer<>(), redis, keyName);
	}

	public SerializedSortedSet(ISerializer<V> serializer, Redis redis, String keyName) {
		this.serializer = serializer;
		this.innerSet = new ByteArraySortedSet(redis, keyName);
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
	public boolean offer(V v) {
		return innerSet.offer(serializer.serialize(v));
	}

	@Override
	public V remove() {
		return serializer.deserialize(innerSet.remove());
	}

	@Override
	public V poll() {
		return serializer.deserialize(innerSet.poll());
	}

	@Override
	public V element() {
		return serializer.deserialize(innerSet.element());
	}

	@Override
	public V peek() {
		return serializer.deserialize(innerSet.peek());
	}

	@Override
	public boolean addAll(Collection<? extends V> collection) {
		return innerSet.addAll(collection.stream().map(serializer::serialize).collect(Collectors.toList()));
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
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void clear() {
		innerSet.clear();
	}
}
