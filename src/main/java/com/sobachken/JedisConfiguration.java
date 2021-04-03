package com.sobachken;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class JedisConfiguration {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PASSWORD = "bitnami";

    public JedisCluster getJedisCluster(Set<Integer> nodePorts) {
        Set<HostAndPort> nodes = new HashSet<>();
        nodePorts.stream()
                .map(port -> new HostAndPort(DEFAULT_HOST, port))
                .forEach(nodes::add);
        GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(3);
        poolConfig.setMaxWaitMillis(3000);
        poolConfig.setTestOnBorrow(true);

        return new JedisCluster(nodes, 1000, 500, 10, DEFAULT_PASSWORD, poolConfig);
    }

    public Jedis getJedis() {
        return new Jedis(DEFAULT_HOST, 6379);
    }
}
