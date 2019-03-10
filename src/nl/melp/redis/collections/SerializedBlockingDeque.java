package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.Serializable;

public class SerializedBlockingDeque<V extends Serializable> extends SerializedList<V> {
	public SerializedBlockingDeque(ISerializer<V> serializer, Redis redis, String keyName, int timeoutSecs) {
		super(serializer, redis, keyName, new BlockingByteArrayDeque(redis, keyName, timeoutSecs));
	}

	public SerializedBlockingDeque(Redis redis, String keyName, int timeoutSecs) {
		this(new Serializer<V>(), redis, keyName, timeoutSecs);
	}
}
