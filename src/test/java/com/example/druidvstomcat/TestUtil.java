package com.example.druidvstomcat;

import com.alibaba.druid.util.Utils;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

import static com.example.druidvstomcat.TestConfig.LOOP_COUNT_Y;
import static com.example.druidvstomcat.TestConfig.PHYSICAL_CONN_STAT;

public class TestUtil {
    public static long getYoungGC() {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName  objectName;
            if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ParNew"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=ParNew");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=Copy"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=Copy");
            } else {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS Scavenge");
            }

            return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
        } catch (Exception e) {
            throw new RuntimeException("error");
        }
    }

    public static long getFullGC() {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName  objectName;

            if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep");
            } else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact"))) {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact");
            } else {
                objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS MarkSweep");
            }

            return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
        } catch (Exception e) {
            throw new RuntimeException("error");
        }
    }

    public static String getResource(String path) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (Reader reader = new InputStreamReader(contextClassLoader.getResourceAsStream(path), "UTF-8")) {
            return Utils.read(reader);
        } catch (IOException ignored) {
            return null;
        }
    }

    public static TestConnectionResult doGetConnectionTest(final DataSource dataSource, String name, int threadCount, int loopCount)
            throws Exception {
        TestConnectionResult result = new TestConnectionResult();
        for (int i = 0; i < loopCount; ++i) {
            TestConnectionResult singleResult = doGetConnectionTest(dataSource, name, threadCount);
            result.setMillis(result.getMillis() + singleResult.getMillis());
            result.setYgc(result.getYgc() + singleResult.getYgc());
            result.setFullGC(result.getFullGC() + singleResult.getFullGC());
            result.setBlocked(result.getBlocked() + singleResult.getBlocked());
            result.setWaited(result.getWaited() + singleResult.getWaited());
        }
        System.out.println();
        return result;
    }

    private static TestConnectionResult doGetConnectionTest(final DataSource dataSource, String name, int threadCount) throws Exception {
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
                + " waited " + NumberFormat.getInstance().format(waitedCount) + " physicalConn " + PHYSICAL_CONN_STAT.get());

        TestConnectionResult result = new TestConnectionResult();
        result.setMillis(millis);
        result.setYgc(ygc);
        result.setFullGC(fullGC);
        result.setBlocked(blockedCount);
        result.setWaited(waitedCount);
        return result;
    }
}
