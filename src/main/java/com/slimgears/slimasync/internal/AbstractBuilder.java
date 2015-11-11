package com.slimgears.slimasync.internal;

/**
 * Created by Denis on 14/10/2015
 * <File Description>
 */
public abstract class AbstractBuilder<T, B extends AbstractBuilder<T, B>> {
    protected abstract B self();

    public final T build() {
        validate();
        return safeBuild();
    }

    protected abstract T safeBuild();
    protected abstract void validate();
}
