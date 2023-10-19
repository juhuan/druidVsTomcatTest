package com.example.druidvstomcat;

import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

import static com.example.druidvstomcat.TestConfig.LOOP_COUNT_X;
import static com.example.druidvstomcat.TestConfig.LOOP_COUNT_Y;
import static com.example.druidvstomcat.TestConfig.PHYSICAL_CONN_STAT;
import static com.example.druidvstomcat.TestConfig.THREAD_COUNT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CaseDruidTestPSCache {

    @BeforeAll
    private void setUp() throws Exception {
        DriverManager.registerDriver(new Driver());
    }

    @Test
    public void test_pscache_on() throws Exception {

        DruidDataSource dataSource = DataSourceBuilder.defaultDruidDataSource();
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(200);

        dodoPrepareStmtTest(dataSource, "druid", THREAD_COUNT, LOOP_COUNT_X);
    }

    @Test
    public void test_pscache_off() throws Exception {

        DruidDataSource dataSource = DataSourceBuilder.defaultDruidDataSource();
        dataSource.setPoolPreparedStatements(false);

        dodoPrepareStmtTest(dataSource, "druid", THREAD_COUNT, LOOP_COUNT_X);
    }

    public static void dodoPrepareStmtTest(final DataSource dataSource, String name, int threadCount, int loopCount) throws Exception {
        for (int i = 0; i < loopCount; ++i) {
            dodoPrepareStmtTest(dataSource, name, threadCount);
        }
        System.out.println();
    }

    private static void dodoPrepareStmtTest(final DataSource dataSource, String name, int threadCount) throws Exception {
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch   = new CountDownLatch(threadCount);
        final CountDownLatch dumpLatch  = new CountDownLatch(1);

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT_Y; ++i) {
                            doPrepareStmt(dataSource, i);
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
                + " waited " + NumberFormat.getInstance().format(waitedCount) + " physicalConn " + PHYSICAL_CONN_STAT.get());

    }

    private static void doPrepareStmt(final DataSource dataSource, int i) throws SQLException {
        Connection        conn = dataSource.getConnection();
        String            sql  = "SELECT " + i;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();
        stmt.close();
        conn.close();
    }
}
