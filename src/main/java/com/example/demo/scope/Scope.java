package com.example.demo.scope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Scope {

    // 维护不同线程的上下文Scope
    private static final ThreadLocal<Scope> SCOPE_THREAD_LOCAL = new ThreadLocal<>();

    // 维护每个上下文中所有的状态数据，为了区分不同的状态数据，使用ScopeKey类型的实例作为key
    private final ConcurrentMap<ScopeKey<?>, Object> values = new ConcurrentHashMap<>();

    // 获取当前上下文

    public static Scope getCurrentScope() {
        return SCOPE_THREAD_LOCAL.get();
    }

    // 在当前上下文设置一个状态数据
    public <T> void set(ScopeKey<T> key, T value) {
        if (value != null) {
            values.put(key, value);
        } else {
            values.remove(key);
        }
    }

    // 在当前上下文读取一个状态数据
    public <T> T get(ScopeKey<T> key) {
        T value = (T) values.get(key);
        if (value == null && key.initializer() != null) {
            value = key.initializer().get();
        }
        return value;
    }


    // 开启一个上下文
    public static Scope beginScope() {
        Scope scope = SCOPE_THREAD_LOCAL.get();
        if (scope != null) {
            throw new IllegalStateException("start a scope in an exist scope.");
        }
        scope = new Scope();
        SCOPE_THREAD_LOCAL.set(scope);
        return scope;
    }

    // 关闭当前上下文
    public static void endScope() {
        SCOPE_THREAD_LOCAL.remove();
    }

    //开启一个新的 Scope 执行 Runnable
    public static <X extends Throwable> void runWithNewScope(ThrowableRunnable<X> runnable)
            throws X {
        supplyWithNewScope(() -> {
            runnable.run();
            return null;
        });
    }

    //开启一个新的 Scope 执行 Supplier
    public static <T, X extends Throwable> T
    supplyWithNewScope(ThrowableSupplier<T, X> supplier) throws X {
        beginScope();
        try {
            return supplier.get();
        } finally {
            endScope();
        }
    }

    // 以给定的上下文执行 Runnable
    public static <X extends Throwable> void runWithExistScope(Scope scope, ThrowableRunnable<X> runnable) throws X {
        supplyWithExistScope(scope, () -> {
            runnable.run();
            return null;
        });
    }

    // 以给定的上下文执行 Supplier
    public static <T, X extends Throwable> T supplyWithExistScope(Scope scope, ThrowableSupplier<T, X> supplier) throws X {
        // 保留现场
        Scope oldScope = SCOPE_THREAD_LOCAL.get();
        // 替换成外部传入的 Scope
        SCOPE_THREAD_LOCAL.set(scope);
        try {
            return supplier.get();
        } finally {
            if (oldScope != null) {
                // 恢复线程
                SCOPE_THREAD_LOCAL.set(oldScope);
            } else {
                SCOPE_THREAD_LOCAL.remove();
            }
        }
    }
}
