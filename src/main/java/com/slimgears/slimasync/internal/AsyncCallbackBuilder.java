// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;

import com.slimgears.slimasync.AsyncCallback;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
public class AsyncCallbackBuilder<R> extends AbstractAsyncCallbackBuilder<R, AsyncCallback<R>, AsyncCallbackBuilder<R>> {
    @Override
    protected AsyncCallbackBuilder<R> self() {
        return this;
    }

    @Override
    protected AsyncCallback<R> safeBuild() {
        return new AsyncCallback<R>() {
            @Override
            public void onComplete(R response) {
                successCallback.call(response);
            }

            @Override
            public void onError(Throwable e) {
                errorCallback.call(e);
            }
        };
    }
}
