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

import static com.example.druidvstomcat.TestConfig.THREAD_COUNT;
import static com.example.druidvstomcat.TestConfig.LOOP_COUNT_X;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CaseAllGetConnections {

    @BeforeAll
    private void setUp() throws Exception {
        DriverManager.registerDriver(new Driver());
    }

    @Test
    public void test_druid() throws Exception {

        DruidDataSource      dataSource = DataSourceBuilder.defaultDruidDataSource();
        TestConnectionResult result     = TestUtil.doGetConnectionTest(dataSource, "druid", THREAD_COUNT, LOOP_COUNT_X);
        dataSource.close();
        System.out.println(
                String.format("thread %2d %12s millis total: %-10d YGC %-10d FGC %-10d blocked %-10d waited %-10d", THREAD_COUNT, "druid", result.getMillis(),
                              result.getYgc(), result.getFullGC(), result.getBlocked(), result.getWaited()));
    }

    @Test
    public void test_tomcat_jdbc() throws Exception {

        org.apache.tomcat.jdbc.pool.DataSource dataSource  = DataSourceBuilder.defaultTomcatDataSource();
        TestConnectionResult result     =  TestUtil.doGetConnectionTest(dataSource, "tomcat-jdbc", THREAD_COUNT, LOOP_COUNT_X);
        dataSource.close();
        System.out.println(
                String.format("thread %2d %12s millis total: %-10d YGC %-10d FGC %-10d blocked %-10d waited %-10d", THREAD_COUNT, "tomcat-jdbc", result.getMillis(),
                              result.getYgc(), result.getFullGC(), result.getBlocked(), result.getWaited()));
    }

    @Test
    public void test_dbcp2() throws Exception {
        final BasicDataSource dataSource  = DataSourceBuilder.defaultDbcp2DataSrouce();
        TestConnectionResult result     =   TestUtil.doGetConnectionTest(dataSource, "dbcp2", THREAD_COUNT, LOOP_COUNT_X);
        dataSource.close();
        System.out.println(
                String.format("thread %2d %12s millis total: %-10d YGC %-10d FGC %-10d blocked %-10d waited %-10d", THREAD_COUNT, "dbcp2", result.getMillis(),
                              result.getYgc(), result.getFullGC(), result.getBlocked(), result.getWaited()));
    }

    @Test
    public void test_bonecp() throws Exception {
        BoneCPDataSource dataSource  = DataSourceBuilder.defaultBonecpDataSource();
        TestConnectionResult result     =   TestUtil.doGetConnectionTest(dataSource, "boneCP", THREAD_COUNT, LOOP_COUNT_X);
        dataSource.close();
        System.out.println(
                String.format("thread %2d %12s millis total: %-10d YGC %-10d FGC %-10d blocked %-10d waited %-10d", THREAD_COUNT, "boneCP", result.getMillis(),
                              result.getYgc(), result.getFullGC(), result.getBlocked(), result.getWaited()));
    }

    @Test
    public void test_c3p0() throws Exception {
        ComboPooledDataSource dataSource  = DataSourceBuilder.defaultC3p0DataSource();
        TestConnectionResult result     =   TestUtil.doGetConnectionTest(dataSource, "c3p0", THREAD_COUNT, LOOP_COUNT_X);
        dataSource.close();
        System.out.println(
                String.format("thread %2d %12s millis total: %-10d YGC %-10d FGC %-10d blocked %-10d waited %-10d", THREAD_COUNT, "c3p0", result.getMillis(),
                              result.getYgc(), result.getFullGC(), result.getBlocked(), result.getWaited()));
    }

    @Test
    public void test_HikariCP() throws Exception {
        HikariDataSource dataSource  = DataSourceBuilder.defaultHikariDataSource();
        TestConnectionResult result     =   TestUtil.doGetConnectionTest(dataSource, "HikariCP", THREAD_COUNT, LOOP_COUNT_X);
        dataSource.close();
        System.out.println(
                String.format("thread %2d %12s millis total: %-10d YGC %-10d FGC %-10d blocked %-10d waited %-10d", THREAD_COUNT, "HikariCP", result.getMillis(),
                              result.getYgc(), result.getFullGC(), result.getBlocked(), result.getWaited()));
    }
}
