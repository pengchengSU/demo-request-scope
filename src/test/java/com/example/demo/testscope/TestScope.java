package com.example.demo.testscope;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.Test;

import com.example.demo.scope.Scope;
import com.example.demo.scope.ScopeKey;
import com.example.demo.scope.ScopeThreadPoolExecutor;
import com.example.demo.scope.ThrowableRunnable;

@Slf4j
public class TestScope {

    @Test
    public void testScopeKey() {
        ScopeKey<String> localThreadName = new ScopeKey<>();

        // 不同线程中执行时，开启独占的 Scope
        Runnable r = () -> {
            // 开启 Scope
            Scope.beginScope();
            try {
                String currentThreadName = Thread.currentThread().getName();
                localThreadName.set(currentThreadName);
                log.info("currentThread: {}", localThreadName.get());
            } finally {
                // 关闭 Scope
                Scope.endScope();
            }
        };

        new Thread(r, "thread-1").start();
        new Thread(r, "thread-2").start();

        /** 执行结果
         * [thread-1] INFO com.example.demo.testscope.TestScope - currentThread: thread-1
         * [thread-2] INFO com.example.demo.testscope.TestScope - currentThread: thread-2
         */
    }

    @Test
    public void testWithInitial() {
        ScopeKey<String> initValue = ScopeKey.withInitial(() -> "initVal");

        Runnable r = () -> {
            Scope.beginScope();
            try {
                log.info("initValue: {}", initValue.get());
            } finally {
                Scope.endScope();
            }
        };

        new Thread(r, "thread-1").start();
        new Thread(r, "thread-2").start();

        /** 执行结果
         * [thread-1] INFO com.example.demo.testscope.TestScope - initValue: initVal
         * [thread-2] INFO com.example.demo.testscope.TestScope - initValue: initVal
         */
    }

    @Test
    public void testRunWithNewScope() {
        ScopeKey<String> localThreadName = new ScopeKey<>();

        ThrowableRunnable r = () -> {
            String currentThreadName = Thread.currentThread().getName();
            localThreadName.set(currentThreadName);
            log.info("currentThread: {}", localThreadName.get());
        };

        // 不同线程中执行时，开启独占的 Scope
        new Thread(() -> Scope.runWithNewScope(r), "thread-1").start();
        new Thread(() -> Scope.runWithNewScope(r), "thread-2").start();

        /** 执行结果
         * [thread-2] INFO com.example.demo.TestScope.testscope - currentThread: thread-2
         * [thread-1] INFO com.example.demo.TestScope.testscope - currentThread: thread-1
         */
    }

    @Test
    public void testScopeThreadPoolExecutor() {
        ScopeKey<String> localVariable = new ScopeKey<>();
        Scope.beginScope();

        try {
            localVariable.set("value out of thread pool");
            Runnable r = () -> log.info("localVariable in thread pool: {}", localVariable.get());

            // 使用线程池执行，能获取到外部Scope中的数据
            ExecutorService executor = ScopeThreadPoolExecutor.newFixedThreadPool(10);
            executor.execute(r);
            executor.submit(r);

        } finally {
            Scope.endScope();
        }

        /** 执行结果
         * [pool-1-thread-1] INFO com.example.demo.testscope.TestScope - localVariable in thread pool: value out of thread pool
         * [pool-1-thread-2] INFO com.example.demo.testscope.TestScope - localVariable in thread pool: value out of thread pool
         */
    }

    @Test
    public void testScopeThreadPoolExecutor2() {
        ScopeKey<String> localVariable = new ScopeKey<>();
        Scope.runWithNewScope(() -> {
            localVariable.set("value out of thread pool");
            Runnable r = () -> log.info("localVariable in thread pool: {}", localVariable.get());

            // 使用线程池执行，能获取到外部Scope中的数据
            ExecutorService executor = ScopeThreadPoolExecutor.newFixedThreadPool(10);
            executor.execute(r);
            executor.submit(r);
        });

        /** 执行结果
         * [pool-1-thread-2] INFO com.example.demo.testscope.TestScope - localVariable in thread pool: value out of thread pool
         * [pool-1-thread-1] INFO com.example.demo.testscope.TestScope - localVariable in thread pool: value out of thread pool
         */
    }
}
