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

        DruidDataSource dataSource = DataSourceBuilder.defaultDruidDataSource();
        //System.out.println(ToStringBuilder.reflectionToString(dataSource, ToStringStyle.JSON_STYLE));
        TestUtil.doGetConnectionTest(dataSource, "druid", THREAD_COUNT, LOOP_COUNT_X);
    }

    @Test
    public void test_tomcat_jdbc() throws Exception {

        org.apache.tomcat.jdbc.pool.DataSource dataSource = DataSourceBuilder.defaultTomcatDataSource();
        TestUtil.doGetConnectionTest(dataSource, "tomcat-jdbc", THREAD_COUNT, LOOP_COUNT_X);
    }

    @Test
    public void test_dbcp2() throws Exception {
        final BasicDataSource dataSource = DataSourceBuilder.defaultDbcp2DataSrouce();
        TestUtil.doGetConnectionTest(dataSource, "dbcp2", THREAD_COUNT, LOOP_COUNT_X);
    }

    @Test
    public void test_bonecp() throws Exception {
        BoneCPDataSource dataSource = DataSourceBuilder.defaultBonecpDataSource();
        TestUtil.doGetConnectionTest(dataSource, "boneCP", THREAD_COUNT, LOOP_COUNT_X);
    }

    @Test
    public void test_c3p0() throws Exception {
        ComboPooledDataSource dataSource = DataSourceBuilder.defaultC3p0DataSource();
        TestUtil.doGetConnectionTest(dataSource, "c3p0", THREAD_COUNT, LOOP_COUNT_X);
    }

    @Test
    public void test_HikariCP() throws Exception {
        HikariDataSource dataSource = DataSourceBuilder.defaultHikariDataSource();
        TestUtil.doGetConnectionTest(dataSource, "HikariCP", THREAD_COUNT, LOOP_COUNT_X);
    }
}
