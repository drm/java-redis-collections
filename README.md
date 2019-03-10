# Java Redis Collections

Store your collections in Redis and make your application resumable and stateful.

## Implemented collections

* `nl.melp.redis.collections.ByteArraySet` - a Set implementation that stores raw `byte[]` values as a Redis Set;
* `nl.melp.redis.collections.SerializedSet` - a Set implementation that serializes and deserializes java objects, 
   backed by the ByteArraySet;
* `nl.melp.redis.collections.ByteArrayMap` - a Map implementation that stores raw `byte[]` key/value pairs as a Redis Hash;
* `nl.melp.redis.collections.SerializedSet` - a Map implementation that serializes and deserializes key and value objects, 
   backed by the ByteArrayMap;
* `nl.melp.redis.collections.ByteArrayList` - a List and Deque implementation that stores raw byte values as a Redis list;
* `nl.melp.redis.collections.ByteArrayList` - a List and Deque implementation that serializes and deserializes objects, 
  backed by the ByteArrayList;
* `nl.melp.redis.collections.BlockingByteArrayDeque` - a Deque implementation based on `ByteArrayList` that does blocking reads with `BRPOP` and `BLPOP`.
* `nl.melp.redis.collections.SerializedBlockingDeque` - a Deque implementation that serializes and deserializes objects, 
  backed by the BlockingByteArrayDeque;

## Usage

You will only need a recent version (> 2.0.1) of https://github.com/drm/java-redis-client. 

Otherwise there are no dependencies, but java >= 8.

## Bugs, questions, etc?

Feel free to report them at Github: https://github.com/drm/java-redis-collections

## Copyright & license

See [LICENSE](LICENSE).
