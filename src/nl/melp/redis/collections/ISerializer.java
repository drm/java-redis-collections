package nl.melp.redis.collections;

public interface ISerializer<T> {
	byte[] serialize(T v);
	T deserialize(byte[] v);
}
