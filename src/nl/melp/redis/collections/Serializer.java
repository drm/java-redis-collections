package nl.melp.redis.collections;

import java.io.*;

public class Serializer<V extends Serializable> implements ISerializer<V> {
	@Override
	public byte[] serialize(Serializable v) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(buf).writeObject(v);
			return buf.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public V deserialize(byte[] v) {
		if (v == null) {
			return null;
		}
		ByteArrayInputStream buf = new ByteArrayInputStream(v);
		try {
			return (V) new ObjectInputStream(buf).readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(e);
		}
	}
}
