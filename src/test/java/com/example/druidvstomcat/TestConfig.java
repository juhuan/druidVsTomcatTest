package com.example.druidvstomcat;

import java.util.concurrent.atomic.AtomicLong;

public class TestConfig {
    static       String     DRIVER_CLASS              = "com.mysql.cj.jdbc.Driver";
    static       String     JDBC_URL                  = "jdbc:mysql://localhost:3306/mysql";
    static       String     USER                      = "root";
    static       String     PASSWORD                  = "123456";
    static       int        INIT_SIZE                 = 10;
    static       int        MIN_SIZE                  = 10;
    static       int        MAX_SIZE                  = 50;
    static       int        MAX_WAIT                  = 30000;
    static       String     VALIDATION_QUERY          = "SELECT 1";
    static       int        VALIDATION_QUERY_TIME_OUT = 1;
    static       int        VALIDATION_QUERY_INTERVAL = 30000;
    static       int        THREAD_COUNT              = 1;
    static       int        LOOP_COUNT_X              = 10;
    static final int        LOOP_COUNT_Y              = 1000 * 1 * 1 / THREAD_COUNT;
    static       AtomicLong PHYSICAL_CONN_STAT        = new AtomicLong(0);
}
