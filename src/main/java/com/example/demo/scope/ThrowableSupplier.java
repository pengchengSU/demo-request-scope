package com.example.demo.scope;

@FunctionalInterface
public interface ThrowableSupplier<T, X extends Throwable> {
    T get() throws X;
}
