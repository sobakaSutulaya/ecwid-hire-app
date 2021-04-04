package com.sobachken.collection;

import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sobachken.collection.Constant.SUCCESS_CODE;

public class RedisList implements List<Integer> {

    private final Jedis jedis;
    private final String hashKey;
    private final List<String> keys;

    public RedisList(Jedis jedis, String hashKey) {
        this.jedis = jedis;
        this.hashKey = hashKey;
        this.keys = new ArrayList<>();
    }

    @Override
    public int size() {
        return this.keys.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.jedis.hgetAll(this.hashKey)
                .values()
                .stream()
                .anyMatch(value -> value.equals(o));
    }

    @Override
    public Iterator<Integer> iterator() {
        return this.jedis.hgetAll(this.hashKey)
                .values()
                .stream()
                .map(Integer::valueOf)
                .iterator();
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
        String hash = this.getHashAsString(integer);
        if (SUCCESS_CODE == this.jedis.hset(this.hashKey, hash, String.valueOf(integer))) {
            this.keys.add(hash);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        String hash = this.getHashAsString(o);
        if (SUCCESS_CODE == this.jedis.hdel(this.hashKey, hash)) {
            this.keys.remove(hash);
            return true;
        }
        return false;
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
    public boolean addAll(int index, Collection<? extends Integer> c) {
        for (Integer integer : c) {
            this.add(index++, integer);
        }
        return true;//tbd
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        List<String> hashes = c.stream()
                .map(this::getHashAsString)
                .collect(Collectors.toList());
        if (SUCCESS_CODE == this.jedis.hdel(this.hashKey, hashes.toArray(String[]::new))) {
            this.keys.removeAll(hashes);
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<String> specifiedKeys = c.stream()
                .map(this::getHashAsString)
                .collect(Collectors.toList());

        Set<String> savedKeys = this.jedis.hkeys(this.hashKey);
        String[] keysToDelete = savedKeys.stream()
                .filter(key -> !specifiedKeys.contains(key))
                .distinct()
                .toArray(String[]::new);

        if (SUCCESS_CODE == this.jedis.hdel(this.hashKey, keysToDelete)) {
            this.keys.retainAll(specifiedKeys);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            this.jedis.hdel(this.hashKey, this.keys.toArray(String[]::new));
            this.keys.clear();
        }
    }

    @Override
    public Integer get(int index) {
        return Integer.valueOf(this.jedis.hget(this.hashKey, this.keys.get(index)));
    }

    @Override
    public Integer set(int index, Integer element) {
        String hash = this.getHashAsString(element);
        Integer oldValue = this.get(index);
        this.keys.set(index, hash);
        this.jedis.hset(this.hashKey, hash, String.valueOf(element));
        return oldValue;
    }

    @Override
    public void add(int index, Integer element) {
        String hash = this.getHashAsString(element);
        if (SUCCESS_CODE == this.jedis.hset(this.hashKey, hash, String.valueOf(element))) {
            this.keys.add(index, hash);
        }
    }

    @Override
    public Integer remove(int index) {
        Integer oldValue = this.get(index);
        if (SUCCESS_CODE == this.jedis.hdel(this.hashKey, this.keys.get(index))) {
            this.keys.remove(index);
        }
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        return this.keys.indexOf(getHashAsString(o));
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.keys.lastIndexOf(getHashAsString(o));
    }

    @Override
    public ListIterator<Integer> listIterator() {
        return this.jedis.hgetAll(this.hashKey)
                .values()
                .stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList())
                .listIterator();
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        return this.jedis.hgetAll(this.hashKey)
                .values()
                .stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList())
                .listIterator(index);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        List<String> hashesSubList = this.keys.subList(fromIndex, toIndex);
        List<Integer> valueSubList = new RedisList(this.jedis, buildSubListHashKey());
        Map<String, String> allValues = this.jedis.hgetAll(this.hashKey);

        for (String hash : hashesSubList) {
            if (allValues.get(hash) != null) {
                valueSubList.add(Integer.valueOf(allValues.get(hash)));
            }
        }
        return valueSubList;
    }

    private String buildSubListHashKey() {
        return "sub_list_" + this.hashKey + "_" + LocalDateTime.now().hashCode();
    }

    private String getHashAsString(Object obj) {
        return String.valueOf(obj.hashCode());
    }
}
