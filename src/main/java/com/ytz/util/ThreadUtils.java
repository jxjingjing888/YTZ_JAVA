package com.ytz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 线程工具方法
 *
 * @author Bob
 */
public class ThreadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);

    private static ExecutorService service = null;

    static {
        int processors = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool(processors*4);
    }

    /**
     * 执行线程
     * Bob
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        service.execute(runnable);
    }

    /**
     * Thread Sleep的工具方法，不会抛异常
     *
     * @param sleepTime 休眠的时长
     * @param unit      时间的单位
     * @param logger    日志记录实例
     */
    public static void sleep(long sleepTime, TimeUnit unit, Logger logger) {
        try {
            unit.sleep(sleepTime);
        } catch (InterruptedException e) {
            if (null == logger) {
                logger = LOGGER;
            }
            logger.warn("线程休眠过程中引发错误。休眠信息为：sleepTime:{};timeUnit:{}.Cause:{}", sleepTime, unit, e.getMessage());
            logger.debug("Error:{}", e);
        }
    }

    /**
     * Thread Sleep的工具方法，不会抛异常
     *
     * @param sleepTime 休眠的时长
     * @param unit      时间的单位
     */
    public static void sleep(long sleepTime, TimeUnit unit) {
        sleep(sleepTime, unit, null);
    }

    /**
     * Thread Sleep的工具方法，单位：毫秒
     *
     * @param sleepTime 休眠的时间
     * @param logger    日志实例
     */
    public static void sleep(long sleepTime, Logger logger) {
        sleep(sleepTime, TimeUnit.MILLISECONDS, logger);
    }

    /**
     * Thread Sleep的工具方法，单位：毫秒
     *
     * @param sleepTime 休眠的时间
     */
    public static void sleep(long sleepTime) {
        sleep(sleepTime, (Logger) null);
    }

}
