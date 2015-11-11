// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;

import com.slimgears.slimasync.Async;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
public class AsyncProgressTaskBuilder<P, R> extends AbstractAsyncProgressTaskBuilder<R, P, AsyncProgressTaskBuilder<P, R>> {
    @Override
    protected AsyncProgressTaskBuilder<P, R> self() {
        return this;
    }

    @Override
    protected AsyncProgressCallbackBuilder<R, P> createCallbackBuilder() {
        return Async.<R, P>progressCallbackBuilder();
    }
}
