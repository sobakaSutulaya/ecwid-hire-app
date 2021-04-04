package com.sobachken.config;

import redis.clients.jedis.Jedis;

public class JedisConfiguration {

    private static final String DEFAULT_HOST = "localhost";

    public Jedis getJedis() {
        return new Jedis(DEFAULT_HOST, 6380);
    }
}
