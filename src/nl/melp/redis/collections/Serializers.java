package nl.melp.redis.collections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Serializers {
	private static Map<Class, Class> serializers = new HashMap<>();
	static {
		serializers.put(Integer.class, Serializer.IntegerSerializer.class);
		serializers.put(Long.class, Serializer.LongSerializer.class);
		serializers.put(String.class, Serializer.StringSerializer.class);
	}


	public static <T> ISerializer<T> of(Class<T> t) {
		if (serializers.containsKey(t)) {
			try {
				return (ISerializer<T>) serializers.get(t).getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		return new Serializer.DefaultSerializer();
	}
}
