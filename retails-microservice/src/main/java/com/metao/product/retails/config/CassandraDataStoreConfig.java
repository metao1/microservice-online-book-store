package com.metao.product.retails.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableAutoConfiguration
@EnableCassandraRepositories(basePackages = { "com.metao.product.retails.persistence" })
public class CassandraDataStoreConfig {

}
