package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.*;

public class ByteArrayList extends Collection<byte[]> implements List<byte[]>, Deque<byte[]> {
	public ByteArrayList(Redis redis, String keyName) {
		super(redis, keyName);
	}

	@Override
	public int size() {
		return this.<Long>call("LLEN").intValue();
	}

	@Override
	public boolean addAll(int i, java.util.Collection<? extends byte[]> collection) {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException("Not implemented");
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
	public boolean add(byte[] bytes) {
		addLast(bytes);
		// TODO what should this return?
		return true;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Not implemented");
	}


	@Override
	public byte[] get(int i) {
		if (i > size() -1) {
			throw new NoSuchElementException();
		}
		return call("LINDEX", Integer.toString(i).getBytes());
	}

	@Override
	public byte[] set(int i, byte[] bytes) {
		call("LSET", Integer.toString(i).getBytes(), bytes);
		return null; // TODO implement replacement value
	}

	@Override
	public void add(int i, byte[] bytes) {
		set(i, bytes);
	}

	@Override
	public byte[] remove(int i) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public int lastIndexOf(Object o) {
		return 0;
	}

	@Override
	public ListIterator<byte[]> listIterator() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public ListIterator<byte[]> listIterator(int i) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public List<byte[]> subList(int i, int i1) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void addFirst(byte[] bytes) {
		call("LPUSH", bytes);
	}

	@Override
	public byte[] removeFirst() {
		if (size() > 0) {
			return call("LPOP");
		}
		throw new NoSuchElementException("List is empty");
	}

	@Override
	public void addLast(byte[] bytes) {
		call("RPUSH", bytes);
	}

	@Override
	public byte[] removeLast() {
		if (size() > 0) {
			return call("RPOP");
		}
		throw new NoSuchElementException("List is empty");
	}

	@Override
	public boolean offerFirst(byte[] bytes) {
		addFirst(bytes);
		return true;
	}

	@Override
	public boolean offerLast(byte[] bytes) {
		addLast(bytes);
		return true;
	}

	@Override
	public byte[] pollFirst() {
		return call("LINDEX", "0".getBytes());
	}

	@Override
	public byte[] pollLast() {
		return call("LINDEX", "-1".getBytes());
	}

	@Override
	public byte[] getFirst() {
		byte[] ret = peekFirst();
		if (ret == null) {
			throw new NoSuchElementException();
		}
		return ret;
	}

	@Override
	public byte[] getLast() {
		byte[] ret = peekLast();
		if (ret == null) {
			throw new NoSuchElementException();
		}
		return ret;
	}

	@Override
	public byte[] peekFirst() {
		return this.get(0);
	}

	@Override
	public byte[] peekLast() {
		// TODO transactionalize
		return call("LINDEX", Integer.toString(size() -1).getBytes());
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return call("LREM", (byte[])o, "-1".getBytes());
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return call("LREM", (byte[])o, "1".getBytes());
	}

	@Override
	public boolean offer(byte[] bytes) {
		offerLast(bytes);
		return true;
	}

	@Override
	public byte[] remove() {
		return removeFirst();
	}

	@Override
	public byte[] poll() {
		return pollFirst();
	}

	@Override
	public byte[] element() {
		return get(0);
	}

	@Override
	public byte[] peek() {
		return peekFirst();
	}

	@Override
	public void push(byte[] bytes) {
		addLast(bytes);
	}

	@Override
	public byte[] pop() {
		return removeLast();
	}

	@Override
	public Iterator<byte[]> descendingIterator() {
		throw new UnsupportedOperationException();
	}
}
