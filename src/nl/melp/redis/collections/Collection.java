package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.Iterator;
import java.util.List;

public abstract class Collection<T> extends RedisVar implements java.util.Collection<T> {
	public Collection(Redis redis, String keyName) {
		super(redis, keyName);
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean containsAll(java.util.Collection<?> collection) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean addAll(java.util.Collection<? extends T> collection) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean removeAll(java.util.Collection<?> collection) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean retainAll(java.util.Collection<?> collection) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void clear() {
		call("DEL");
	}
}
