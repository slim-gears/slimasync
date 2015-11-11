// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;

import com.slimgears.slimasync.Async;
import com.slimgears.slimasync.AsyncCallback;
import com.slimgears.slimasync.Callback;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
abstract class AbstractAsyncCallbackBuilder<R, T extends AsyncCallback<R>, B extends AbstractAsyncCallbackBuilder<R, T, B>> extends AbstractBuilder<T, B> {
    protected Callback<R> successCallback = null;
    protected Callback<Throwable> errorCallback = Async.noCallback()::onError;

    public B onComplete(Callback<R> successCallback) {
        this.successCallback = successCallback;
        return self();
    }

    public B onError(Callback<Throwable> errorCallback) {
        this.errorCallback = errorCallback;
        return self();
    }

    @Override
    protected void validate() {
        if (successCallback == null)
            throw new IllegalArgumentException("Success callback was not specified");
    }
}
