package me.howandev.itemmaker.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SchedulerUtil {
    private SchedulerUtil() {
        throw new IllegalStateException("SchedulerUtil class should not be instantiated!");
    }

    public static <T> CompletableFuture<T> future(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                }
                throw new CompletionException(ex);
            }
        });
    }

    public static CompletableFuture<Void> future(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }
}
