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

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;

import static com.example.druidvstomcat.TestConfig.LOOP_COUNT_X;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CaseAllGetConnections {

    @BeforeAll
    private void setUp() throws Exception {
        DriverManager.registerDriver(new Driver());
    }

    public void testDataSource(String dataSourceType, DataSource dataSource) throws Exception {
        List<Integer> threadCounts = Arrays.asList(1, 2, 5, 10, 20, 50);

        for (Integer threadCount : threadCounts) {
            TestConnectionResult result = TestUtil.doGetConnectionTest(dataSource, dataSourceType, threadCount, LOOP_COUNT_X);
            System.out.printf("thread %2d %12s millis total: %-10d YGC %-10d FGC %-10d blocked %-10d waited %-10d\n", threadCount,
                              dataSourceType, result.getMillis(),
                              result.getYgc(), result.getFgc(), result.getBlocked(), result.getWaited());
        }
    }

    @Test
    public void test_druid() throws Exception {
        DruidDataSource dataSource = DataSourceBuilder.defaultDruidDataSource();
        testDataSource("druid", dataSource);
    }

    @Test
    public void test_tomcat_jdbc() throws Exception {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = DataSourceBuilder.defaultTomcatDataSource();
        testDataSource("tomcat-jdbc", dataSource);
    }

    @Test
    public void test_dbcp2() throws Exception {
        BasicDataSource dataSource = DataSourceBuilder.defaultDbcp2DataSrouce();
        testDataSource("dbcp2", dataSource);
    }

    public void test_bonecp() throws Exception {
        BoneCPDataSource dataSource = DataSourceBuilder.defaultBonecpDataSource();
        testDataSource("boneCP", dataSource);
    }

    @Test
    public void test_c3p0() throws Exception {
        ComboPooledDataSource dataSource = DataSourceBuilder.defaultC3p0DataSource();
        testDataSource("c3p0", dataSource);
    }

    @Test
    public void test_HikariCP() throws Exception {
        HikariDataSource dataSource = DataSourceBuilder.defaultHikariDataSource();
        testDataSource("HikariCP", dataSource);
    }
}
