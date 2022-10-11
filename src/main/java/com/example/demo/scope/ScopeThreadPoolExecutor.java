package com.example.demo.scope;

import static com.example.demo.scope.Scope.getCurrentScope;
import static com.example.demo.scope.Scope.runWithExistScope;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScopeThreadPoolExecutor extends ThreadPoolExecutor {

    ScopeThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                            TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public static ScopeThreadPoolExecutor newFixedThreadPool(int nThreads) {
        return new ScopeThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 只要override这一个方法就可以
     * 所有submit, invokeAll等方法都会代理到这里来
     */
    @Override
    public void execute(Runnable command) {
        Scope scope = getCurrentScope();
        super.execute(() -> runWithExistScope(scope, command::run));
    }
}
