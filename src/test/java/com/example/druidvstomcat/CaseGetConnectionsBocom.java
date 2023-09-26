/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.druidvstomcat;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.mysql.cj.jdbc.Driver;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CaseGetConnectionsBocom {
    private static String DRIVER_CLASS              = "com.mysql.cj.jdbc.Driver";
    private        String jdbcUrl;
    private        String user;
    private        String password;
    private static int    INIT_SIZE                 = 10;
    private static int    MIN_SIZE                  = 10;
    private static int    MAX_SIZE                  = 50;
    private static int    MAX_WAIT                  = 30000;
    private static String VALIDATION_QUERY          = "SELECT 1";
    private static int    VALIDATION_QUERY_TIME_OUT = 1;
    private static int    VALIDATION_QUERY_INTERVAL = 30000;
    private        int    threadCount               = 5;
    private        int    loopCount                 = 10;
    final          int    LOOP_COUNT                = 10000 * 1 * 1 / threadCount;

    private static AtomicLong physicalConnStat = new AtomicLong();

    @BeforeAll
    private void setUp() throws Exception {
        DriverManager.registerDriver(new Driver());

        user = "root";
        password = "123456";
        jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
        physicalConnStat.set(0);
    }

    private DruidDataSource buildDefaultDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        dataSource.setInitialSize(INIT_SIZE);
        dataSource.setMaxActive(MAX_SIZE);
        dataSource.setMinIdle(MIN_SIZE);
        dataSource.setMaxWait(MAX_WAIT);

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

    private org.apache.tomcat.jdbc.pool.DataSource buildDefaultTomcatDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        dataSource.setInitialSize(INIT_SIZE);
        dataSource.setMaxActive(MAX_SIZE);
        dataSource.setMinIdle(MIN_SIZE);
        dataSource.setMaxIdle(MAX_SIZE);
        dataSource.setMaxWait(MAX_WAIT);

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

    @Test
    public void test_druid_bocom_default() throws Exception {

        DruidDataSource dataSource = buildDefaultDruidDataSource();
        //System.out.println(ToStringBuilder.reflectionToString(dataSource, ToStringStyle.JSON_STYLE));
        doGetConnectionTest(dataSource, "druid", threadCount, loopCount);
    }
    @Test
    public void test_druid_bocom_simple() throws Exception {

        DruidDataSource dataSource = buildDefaultDruidDataSource();
        dataSource.setKeepAlive(false);
        dataSource.setMaxWait(-1);
        dataSource.setValidationQueryTimeout(-1);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMaxOpenPreparedStatements(10);
        //System.out.println(ToStringBuilder.reflectionToString(dataSource, ToStringStyle.JSON_STYLE));
        doGetConnectionTest(dataSource, "druid", threadCount, loopCount);
    }

    @Test
    public void test_druid_simple() throws Exception {

        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setInitialSize(INIT_SIZE);
        dataSource.setMaxActive(MAX_SIZE);
        dataSource.setMinIdle(MIN_SIZE);
        //dataSource.setMaxIdle(maxPoolSize);
        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(VALIDATION_QUERY);
        dataSource.setTestOnBorrow(false);

        //System.out.println(ToStringBuilder.reflectionToString(dataSource, ToStringStyle.JSON_STYLE));

        doGetConnectionTest(dataSource, "druid", threadCount, loopCount);
    }

    @Test
    public void test_tomcat_jdbc_bocom() throws Exception {

        org.apache.tomcat.jdbc.pool.DataSource dataSource = buildDefaultTomcatDataSource();
        doGetConnectionTest(dataSource, "tomcat-jdbc", threadCount, loopCount);
    }

    private void doGetConnectionTest(final DataSource dataSource, String name, int threadCount, int loopCount) throws Exception {
        for (int i = 0; i < loopCount; ++i) {
            doGetConnectionTest(dataSource, name, threadCount);
        }
        System.out.println();
    }

    private void doGetConnectionTest(final DataSource dataSource, String name, int threadCount) throws Exception {
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch   = new CountDownLatch(threadCount);
        final CountDownLatch dumpLatch  = new CountDownLatch(1);

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    endLatch.countDown();

                    try {
                        dumpLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threads[i] = thread;
            thread.start();
        }
        long startMillis = System.currentTimeMillis();
        long startYGC    = TestUtil.getYoungGC();
        long startFullGC = TestUtil.getFullGC();
        startLatch.countDown();
        endLatch.await();

        long[] threadIdArray = new long[threads.length];
        for (int i = 0; i < threads.length; ++i) {
            threadIdArray[i] = threads[i].getId();
        }
        ThreadInfo[] threadInfoArray = ManagementFactory.getThreadMXBean().getThreadInfo(threadIdArray);

        dumpLatch.countDown();

        long blockedCount = 0;
        long waitedCount  = 0;
        for (int i = 0; i < threadInfoArray.length; ++i) {
            ThreadInfo threadInfo = threadInfoArray[i];
            blockedCount += threadInfo.getBlockedCount();
            waitedCount += threadInfo.getWaitedCount();
        }

        long millis = System.currentTimeMillis() - startMillis;
        long ygc    = TestUtil.getYoungGC() - startYGC;
        long fullGC = TestUtil.getFullGC() - startFullGC;

        System.out.println(
                "thread " + threadCount + " " + name + " millis : " + NumberFormat.getInstance().format(millis) + "; YGC " + ygc + " FGC "
                + fullGC + " blocked " + NumberFormat.getInstance().format(blockedCount) //
                + " waited " + NumberFormat.getInstance().format(waitedCount) + " physicalConn " + physicalConnStat.get());

    }
}
