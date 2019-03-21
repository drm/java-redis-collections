package nl.melp.redis.collections;

import nl.melp.redis.Redis;

public class SerializedBlockingDeque<V> extends SerializedList<V> {
	public SerializedBlockingDeque(ISerializer<V> serializer, Redis redis, String keyName, int timeoutSecs) {
		super(serializer, new BlockingByteArrayDeque(redis, keyName, timeoutSecs));
	}

	public SerializedBlockingDeque(Redis redis, String keyName, int timeoutSecs) {
		this(new Serializer.DefaultSerializer<>(), redis, keyName, timeoutSecs);
	}
}
