package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.Collection;
import java.util.*;

public class SerializedList<V> implements List<V>, Deque<V> {
	private final ISerializer<V> serializer;
	private final ByteArrayList innerList;

	protected SerializedList(ISerializer<V> serializer, ByteArrayList innerList) {
		this.serializer = serializer;
		this.innerList = innerList;
	}

	public SerializedList(ISerializer<V> serializer, Redis redis, String keyName) {
		this(serializer, new ByteArrayList(redis, keyName));
	}

	public SerializedList(Redis redis, String keyName) {
		this(new Serializer.DefaultSerializer<>(), redis, keyName);
	}

	@Override
	public void addFirst(V v) {
		innerList.addFirst(serializer.serialize(v));
	}

	@Override
	public void addLast(V v) {
		innerList.addLast(serializer.serialize(v));

	}

	@Override
	public boolean offerFirst(V v) {
		return innerList.offerFirst(serializer.serialize(v));
	}

	@Override
	public boolean offerLast(V v) {
		return innerList.offerLast(serializer.serialize(v));
	}

	@Override
	public V removeFirst() {
		return serializer.deserialize(innerList.removeFirst());
	}

	@Override
	public V removeLast() {
		return serializer.deserialize(innerList.removeLast());
	}

	@Override
	public V pollFirst() {
		return serializer.deserialize(innerList.pollFirst());
	}

	@Override
	public V pollLast() {
		return serializer.deserialize(innerList.pollLast());
	}

	@Override
	public V getFirst() {
		return serializer.deserialize(innerList.getFirst());
	}

	@Override
	public V getLast() {
		return serializer.deserialize(innerList.getLast());
	}

	@Override
	public V peekFirst() {
		return serializer.deserialize(innerList.peekFirst());
	}

	@Override
	public V peekLast() {
		return serializer.deserialize(innerList.peekLast());
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return innerList.removeFirstOccurrence(serializer.serialize((V)o));
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return innerList.removeLastOccurrence(serializer.serialize((V)o));
	}

	@Override
	public boolean offer(V v) {
		return innerList.offer(serializer.serialize((V)v));
	}

	@Override
	public V remove() {
		return serializer.deserialize(innerList.remove());
	}

	@Override
	public V poll() {
		return serializer.deserialize(innerList.poll());
	}

	@Override
	public V element() {
		return serializer.deserialize(innerList.element());
	}

	@Override
	public V peek() {
		return serializer.deserialize(innerList.peek());
	}

	@Override
	public void push(V v) {
		innerList.push(serializer.serialize(v));
	}

	@Override
	public V pop() {
		return serializer.deserialize(innerList.pop());
	}

	@Override
	public Iterator<V> descendingIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return innerList.size();
	}

	@Override
	public boolean isEmpty() {
		return innerList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return innerList.contains(o);
	}

	@Override
	public Iterator<V> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(V v) {
		return innerList.add(serializer.serialize(v));
	}

	@Override
	public boolean remove(Object o) {
		return innerList.remove(serializer.serialize((V)o));
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends V> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int i, Collection<? extends V> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		innerList.clear();
	}

	@Override
	public V get(int i) {
		return serializer.deserialize(innerList.get(i));
	}

	@Override
	public V set(int i, V v) {
		return serializer.deserialize(innerList.set(i, serializer.serialize(v)));
	}

	@Override
	public void add(int i, V v) {
		innerList.add(i, serializer.serialize(v));
	}

	@Override
	public V remove(int i) {
		return serializer.deserialize(innerList.remove(i));
	}

	@Override
	public int indexOf(Object o) {
		return innerList.indexOf(serializer.serialize((V)o));
	}

	@Override
	public int lastIndexOf(Object o) {
		return innerList.lastIndexOf(serializer.serialize((V)o));
	}

	@Override
	public ListIterator<V> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<V> listIterator(int i) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<V> subList(int i, int i1) {
		throw new UnsupportedOperationException();
	}
}
