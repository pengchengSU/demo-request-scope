package com.example.demo.scope;

@FunctionalInterface
public interface ThrowableRunnable<X extends Throwable> {
    void run() throws X;
}
