// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;

import com.slimgears.slimasync.Async;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
public class AsyncProgressTaskBuilder<P, R> extends AbstractAsyncProgressTaskBuilder<P, R, AsyncProgressTaskBuilder<P, R>> {
    @Override
    protected AsyncProgressTaskBuilder<P, R> self() {
        return this;
    }

    @Override
    protected AsyncProgressCallbackBuilder<P, R> createCallbackBuilder() {
        return Async.<P, R>progressCallbackBuilder();
    }
}
