package nl.melp.redis.collections;

import nl.melp.redis.Redis;

import java.io.IOException;

abstract class RedisVar {
	private final Redis redis;
	private final byte[] keyName;

	RedisVar(Redis redis, String keyName) {
		this.redis = redis;
		this.keyName = keyName.getBytes();
	}

	protected <T2> T2 call(String cmd, byte[]... args) {
		byte[][] rawArgs = new byte[args.length + 2][];
		rawArgs[0] = cmd.getBytes();
		rawArgs[1] = keyName;
		System.arraycopy(args, 0, rawArgs, 2, args.length);
		try {
			synchronized (redis) {
				return redis.call((Object[])rawArgs);
			}
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
