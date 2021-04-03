package com.sobachken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args)  {
        Set<Integer> ports = Set.of(7000, 7001, 7002, 7003);
        JedisConfiguration jedisConfiguration = new JedisConfiguration();
        Jedis jedis = jedisConfiguration.getJedis();

        RedisMap redisMap = new RedisMap(jedis, "jedis");
        redisMap.put("first", 1);
        System.out.println(redisMap.get("first"));

        jedis.close();
    }

    private static void jedisTest() {

    }

    private static void jedisClusterTest() {

    }
}
