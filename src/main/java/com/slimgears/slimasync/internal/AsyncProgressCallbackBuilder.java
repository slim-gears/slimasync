// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;


import com.slimgears.slimasync.AsyncProgressCallback;
import com.slimgears.slimasync.Callback;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
public class AsyncProgressCallbackBuilder<P, R> extends AbstractAsyncCallbackBuilder<R, AsyncProgressCallback<P, R>, AsyncProgressCallbackBuilder<P, R>> {
    private Callback<P> progressCallback = arg -> {};

    @Override
    protected AsyncProgressCallbackBuilder<P, R> self() {
        return this;
    }

    public AsyncProgressCallbackBuilder<P, R> onProgressUpdate(Callback<P> progressCallback) {
        this.progressCallback = progressCallback;
        return this;
    }

    @Override
    protected AsyncProgressCallback<P, R> safeBuild() {
        return new AsyncProgressCallback<P, R>() {
            @Override
            public void onComplete(R response) {
                successCallback.call(response);
            }

            @Override
            public void onError(Throwable error) {
                errorCallback.call(error);
            }

            @Override
            public void onProgressUpdate(P progress) {
                progressCallback.call(progress);
            }
        };
    }
}
