package com.sobachken.map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisMap implements Map<String, Integer> {

    private final Jedis jedis;
    private final String key;

    public RedisMap(Jedis jedis, String key) {
        this.jedis = jedis;
        this.key = key;
    }

    @Override
    public int size() {
        return jedis.hkeys(key).size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return jedis.hexists(this.key, (String) key);
    }

    @Override
    public boolean containsValue(Object value) {
        return jedis.hvals(this.key).contains(value.toString());
    }

    @Override
    public Integer get(Object key) {
        return Optional.ofNullable(jedis.hget(this.key, key.toString()))
                .map(Integer::parseInt)
                .orElse(null);
    }

    @Override
    public Integer put(String key, Integer value) {
        Integer oldValue = this.get(key);
        this.jedis.hset(this.key, key, value.toString());
        return oldValue;
    }

    @Override
    public Integer remove(Object key) {
        Integer oldValue = Integer.parseInt(jedis.hget(this.key, key.toString()));
        jedis.hdel(this.key, key.toString());
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Integer> m) {
        m.forEach((key, value) -> jedis.hset(this.key, key, value.toString()));
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            Map<String, String> allValues = this.jedis.hgetAll(this.key);
            jedis.hdel(this.key, allValues.keySet().toArray(new String[0]));
        }
    }

    @Override
    public Set<String> keySet() {
        return this.jedis.hgetAll(this.key)
                .keySet();
    }

    @Override
    public Collection<Integer> values() {
        return this.jedis.hgetAll(this.key)
                .values()
                .stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Entry<String, Integer>> entrySet() {
        return this.jedis.hgetAll(this.key)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, (entry) -> Integer.parseInt(entry.getValue())))
                .entrySet();
    }
}
