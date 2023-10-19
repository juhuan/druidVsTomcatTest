package com.example.druidvstomcat;

import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;

import java.beans.PropertyVetoException;

import static com.example.druidvstomcat.TestConfig.DRIVER_CLASS;
import static com.example.druidvstomcat.TestConfig.INIT_SIZE;
import static com.example.druidvstomcat.TestConfig.JDBC_URL;
import static com.example.druidvstomcat.TestConfig.MAX_SIZE;
import static com.example.druidvstomcat.TestConfig.MAX_WAIT;
import static com.example.druidvstomcat.TestConfig.MIN_SIZE;
import static com.example.druidvstomcat.TestConfig.PASSWORD;
import static com.example.druidvstomcat.TestConfig.USER;
import static com.example.druidvstomcat.TestConfig.VALIDATION_QUERY;
import static com.example.druidvstomcat.TestConfig.VALIDATION_QUERY_INTERVAL;
import static com.example.druidvstomcat.TestConfig.VALIDATION_QUERY_TIME_OUT;

public class DataSourceBuilder {

    public static DruidDataSource defaultDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);

        dataSource.setInitialSize(INIT_SIZE);
        dataSource.setMaxActive(MAX_SIZE);
        dataSource.setMinIdle(MIN_SIZE);

        // 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
        dataSource.setMaxWait(MAX_WAIT);
        dataSource.setUseUnfairLock(true);

        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(20);

        dataSource.setValidationQuery(VALIDATION_QUERY);
        dataSource.setValidationQueryTimeout(VALIDATION_QUERY_TIME_OUT);

        dataSource.setKeepAlive(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(VALIDATION_QUERY_INTERVAL);
        return dataSource;
    }

    public static org.apache.tomcat.jdbc.pool.DataSource defaultTomcatDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);

        dataSource.setInitialSize(INIT_SIZE);
        dataSource.setMaxActive(MAX_SIZE);
        dataSource.setMinIdle(MIN_SIZE);
        dataSource.setMaxIdle(MAX_SIZE);

        dataSource.setMaxWait(MAX_WAIT);

        dataSource.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.StatementCache");

        dataSource.setValidationQuery(VALIDATION_QUERY);
        dataSource.setValidationQueryTimeout(VALIDATION_QUERY_TIME_OUT);
        dataSource.setValidationInterval(VALIDATION_QUERY_INTERVAL);

        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setMinEvictableIdleTimeMillis(VALIDATION_QUERY_INTERVAL);

        dataSource.setFairQueue(true);
        dataSource.setJmxEnabled(false);
        dataSource.setJdbcInterceptors("SlowQueryReport(threhold=500)");
        return dataSource;
    }

    public static BasicDataSource defaultDbcp2DataSrouce() {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setInitialSize(INIT_SIZE);
        dataSource.setMaxTotal(MAX_SIZE);
        dataSource.setMinIdle(MIN_SIZE);
        dataSource.setMaxIdle(MAX_SIZE);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(JDBC_URL);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setValidationQuery(VALIDATION_QUERY);
        dataSource.setTestOnBorrow(false);
        return dataSource;
    }

    public static BoneCPDataSource defaultBonecpDataSource() {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinConnectionsPerPartition(MIN_SIZE);
        dataSource.setMaxConnectionsPerPartition(MAX_SIZE);

        dataSource.setDriverClass(DRIVER_CLASS);
        dataSource.setJdbcUrl(JDBC_URL);
        dataSource.setStatementsCacheSize(100);
        dataSource.setServiceOrder("LIFO");
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        // dataSource.setConnectionTestStatement("SELECT 1");
        dataSource.setPartitionCount(1);
        dataSource.setAcquireIncrement(5);
        dataSource.setIdleConnectionTestPeriod(0L);
        // dataSource.setDisableConnectionTracking(true);
        return dataSource;
    }

    public static ComboPooledDataSource defaultC3p0DataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinPoolSize(MIN_SIZE);
        dataSource.setMaxPoolSize(MAX_SIZE);

        dataSource.setDriverClass(DRIVER_CLASS);
        dataSource.setJdbcUrl(JDBC_URL);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    public static HikariDataSource defaultHikariDataSource() throws PropertyVetoException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("HikariCPDataSource");
        hikariConfig.setDriverClassName(DRIVER_CLASS);
        hikariConfig.setJdbcUrl(JDBC_URL);
        hikariConfig.setUsername(USER);
        hikariConfig.setPassword(PASSWORD);
        hikariConfig.setMaximumPoolSize(MAX_SIZE);
        hikariConfig.setMinimumIdle(MIN_SIZE);

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }
}
