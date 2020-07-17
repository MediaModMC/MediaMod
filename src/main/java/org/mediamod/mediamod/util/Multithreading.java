package org.mediamod.mediamod.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A utility that uses Java's concurrency for multithreading
 */
public class Multithreading {

    /**
     * The executor service, which uses a thread pool of 50 threads
     */
    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(50, Thread::new);

    /**
     * Runs a task asynchronously on the thread pool
     *
     * @param task Task to run
     */
    public static void runAsync(Runnable task) {
        SERVICE.execute(task);
    }
}