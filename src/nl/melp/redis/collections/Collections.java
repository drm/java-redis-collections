package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.util.Map;
import java.util.Set;

public class Collections {
	public static <K, V> Map<K, V> mapOf(String keyName, Class<K> k, Class<V> v) {
		ISerializer<K> keySerializer = Serializers.of(k);
		ISerializer<V> valueSerializer = Serializers.of(v);

		return new SerializedHashMap<K, V>(keySerializer, valueSerializer, new Redis(), keyName);
	}

	public static <V> Set<V> setOf(String keyName, Class<V> v) {
		return new SerializedSet<V>(Serializers.of(v), new Redis(), keyName);
	}
}
