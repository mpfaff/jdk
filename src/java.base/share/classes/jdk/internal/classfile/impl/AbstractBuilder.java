package jdk.internal.classfile.impl;

public abstract class AbstractBuilder<M> {
    protected final M original;

    protected AbstractBuilder(M original) {
        this.original = original;
    }
}
