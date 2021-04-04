package com.sobachken;

import com.sobachken.collection.RedisList;
import com.sobachken.collection.RedisSet;
import com.sobachken.config.JedisConfiguration;
import com.sobachken.map.RedisMap;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        jedisMapTest();
        jedisSetTest();
        jedisListTest();
    }

    private static void jedisListTest() {
        System.out.println("###############-Jedis-List-Test-###########");
        JedisConfiguration jedisConfiguration = new JedisConfiguration();
        Jedis jedis = jedisConfiguration.getJedis();

        RedisList redisList = new RedisList(jedis, "jedis-list");
        listTest(redisList);
        jedis.close();
    }

    private static void jedisSetTest() {
        System.out.println("###############-Jedis-Set-Test-############");
        JedisConfiguration jedisConfiguration = new JedisConfiguration();
        Jedis jedis = jedisConfiguration.getJedis();

        RedisSet redisSet = new RedisSet(jedis, "jedis-set");
        setTest(redisSet);
        jedis.close();
    }

    private static void jedisMapTest() {
        System.out.println("###############-Jedis-Map-Test-############");
        JedisConfiguration jedisConfiguration = new JedisConfiguration();
        Jedis jedis = jedisConfiguration.getJedis();

        RedisMap redisMap = new RedisMap(jedis, "jedis-map");
        mapTest(redisMap);
        jedis.close();
    }

    private static void listTest(List<Integer> list) {
        System.out.println("is empty : " + list.isEmpty());
        System.out.println("add element '1' : " + list.add(1));
        System.out.println("add element '2' : " + list.add(2));
        System.out.println("get element by index '1' : " + list.get(1));
        System.out.println("remove element by index '0' : " + list.remove(0));
        list.clear();
    }

    private static void setTest(Set<Integer> set) {
        System.out.println("is empty : " + set.isEmpty());
        System.out.println("add element '1' : " + set.add(1));
        System.out.println("is empty after add : " + set.isEmpty());
        System.out.println("contains element '1' : " + set.contains(1));
        System.out.println("remove element '1' : " + set.remove(1));
        System.out.println("is empty : " + set.isEmpty());
        System.out.println("###########################################");
    }

    private static void mapTest(Map<String, Integer> redisMap) {
        System.out.println("is map empty : " + redisMap.isEmpty());
        System.out.println("put '1' by key 'KEY'");
        redisMap.put("KEY", 1);
        System.out.println("is map empty after put : " + redisMap.isEmpty());
        System.out.println("contains by 'KEY' : " + redisMap.containsKey("KEY"));
        System.out.println("contains by 'NOT-EXISTING-KEY' : " + redisMap.containsKey("NOT-EXISTING-KEY"));
        System.out.println("get by 'KEY' : " + redisMap.get("KEY"));
        System.out.println("get by 'NOT-EXISTING-KEY' : " + redisMap.get("NOT-EXISTING-KEY"));
        System.out.println("removed by 'KEY' : " + redisMap.remove("KEY"));
        System.out.println("###########################################");
    }
}
