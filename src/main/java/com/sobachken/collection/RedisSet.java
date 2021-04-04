package com.sobachken.collection;

import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sobachken.collection.Constant.SUCCESS_CODE;

public class RedisSet implements Set<Integer> {

    private final String hashKey;
    private final Jedis jedis;

    public RedisSet(Jedis jedis, String hashKey) {
        this.hashKey = hashKey;
        this.jedis = jedis;
    }

    @Override
    public int size() {
        return this.jedis.hkeys(this.hashKey).size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.jedis.hexists(this.hashKey, this.getHashAsString(o));
    }

    @Override
    public Iterator<Integer> iterator() {
        Set<Integer> values = this.jedis.hgetAll(this.hashKey)
                .values()
                .stream()
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.jedis.hgetAll(this.hashKey).values().toArray();
    }

    @Override
    public <Integer> Integer[] toArray(Integer[] a) {
        Integer[] values = (Integer[]) this.toArray();
        if (a == null || a.length != values.length) {
            return values;
        } else {
            System.arraycopy(values, 0, a, 0, values.length);
            return a;
        }
    }

    @Override
    public boolean add(Integer integer) {
        if (this.contains(integer)) {
            return false;
        }
        this.jedis.hset(this.hashKey, getHashAsString(integer), integer.toString());
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return SUCCESS_CODE == this.jedis.hdel(this.hashKey, getHashAsString(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream()
                .map(this::getHashAsString)
                .map(this::contains)
                .reduce((x, y) -> x && y)
                .orElse(false);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        return c.stream()
                .map(this::add)
                .reduce((x, y) -> x && y)
                .orElse(false);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Set<String> specifiedKeys = c.stream()
                .map(this::getHashAsString)
                .collect(Collectors.toSet());

        Set<String> savedKeys = this.jedis.hkeys(this.hashKey);
        String[] keysToDelete = savedKeys.stream()
                .filter(key -> !specifiedKeys.contains(key))
                .distinct()
                .toArray(String[]::new);
        return SUCCESS_CODE == this.jedis.hdel(this.hashKey, keysToDelete);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        String[] keys = c.stream().map(this::getHashAsString).toArray(String[]::new);
        return SUCCESS_CODE == this.jedis.hdel(this.hashKey, keys);
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            Set<String> keys = this.jedis.hkeys(this.hashKey);
            this.jedis.hdel(this.hashKey, keys.toArray(new String[0]));
        }
    }

    private String getHashAsString(Object obj) {
        return String.valueOf(obj.hashCode());
    }
}
