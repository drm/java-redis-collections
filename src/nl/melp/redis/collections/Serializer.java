package nl.melp.redis.collections;

import java.io.*;
import java.nio.charset.Charset;

public class Serializer {
	public static class DefaultSerializer<V extends Serializable> implements ISerializer<V> {
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


	public static class StringSerializer implements ISerializer<String> {
		private final Charset charset;

		public StringSerializer(Charset charset) {
			this.charset = charset;
		}

		public StringSerializer() {
			this(Charset.defaultCharset());
		}

		@Override
		public byte[] serialize(String v) {
			return v.getBytes();
		}

		@Override
		public String deserialize(byte[] v) {
			return new String(v, charset);
		}
	}

	public static class LongSerializer implements ISerializer<Long> {
		@Override
		public byte[] serialize(Long v) {
			long unboxed = v;
			byte[] result = new byte[Long.BYTES];
			for (int i = Long.BYTES -1; i >= 0; i--) {
				result[i] = (byte)(unboxed & 0xFF);
				unboxed >>= 8;
			}
			return result;
		}

		@Override
		public Long deserialize(byte[] b) {
			long result = 0;
			for (int i = 0; i < Long.BYTES; i++) {
				result <<= 8;
				result |= (b[i] & 0xFF);
			}
			return result;
		}
	}

	public static class IntegerSerializer implements ISerializer<Integer> {
		@Override
		public byte[] serialize(Integer v) {
			int unboxed = v;
			byte[] result = new byte[Integer.BYTES];
			for (int i = result.length -1; i >= 0; i--) {
				result[i] = (byte)(unboxed & 0xFF);
				unboxed >>= 8;
			}
			return result;
		}

		@Override
		public Integer deserialize(byte[] b) {
			int result = 0;
			for (int i = 0; i < Integer.BYTES; i++) {
				result <<= 8;
				result |= (b[i] & 0xFF);
			}
			return result;
		}
	}
}
