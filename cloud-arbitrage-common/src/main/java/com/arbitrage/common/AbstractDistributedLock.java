package com.arbitrage.common;


public abstract class AbstractDistributedLock implements DistributedLock {

    @Override
    public boolean lock(String key) {
        return lock(key, DEFAULT_TIMEOUT_MILLIS, DEFAULT_RETRY_TIMES, DEFAULT_SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes) {
        return lock(key, DEFAULT_TIMEOUT_MILLIS, retryTimes, DEFAULT_SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes, long sleepMillis) {
        return lock(key, DEFAULT_TIMEOUT_MILLIS, retryTimes, sleepMillis);
    }

    @Override
    public boolean lock(String key, long expire) {
        return lock(key, expire, DEFAULT_RETRY_TIMES, DEFAULT_SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes) {
        return lock(key, expire, retryTimes, DEFAULT_SLEEP_MILLIS);
    }
}

