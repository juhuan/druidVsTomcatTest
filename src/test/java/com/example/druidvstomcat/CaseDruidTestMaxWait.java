package com.example.druidvstomcat;

import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.cj.jdbc.Driver;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.DriverManager;

import static com.example.druidvstomcat.TestConfig.LOOP_COUNT_X;
import static com.example.druidvstomcat.TestConfig.THREAD_COUNT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CaseDruidTestMaxWait {

    @BeforeAll
    private void setUp() throws Exception {
        DriverManager.registerDriver(new Driver());
    }

    @Test
    public void test_druid() throws Exception {

        DruidDataSource dataSource = DataSourceBuilder.defaultDruidDataSource();
        TestUtil.doGetConnectionTest(dataSource, "druid", THREAD_COUNT, LOOP_COUNT_X);
    }
    @Test
    public void test_druid_max_wait() throws Exception {

        DruidDataSource dataSource = DataSourceBuilder.defaultDruidDataSource();
        dataSource.setMaxWait(1000);
        dataSource.setUseUnfairLock(false);
        TestUtil.doGetConnectionTest(dataSource, "druid", THREAD_COUNT, LOOP_COUNT_X);
    }
}
