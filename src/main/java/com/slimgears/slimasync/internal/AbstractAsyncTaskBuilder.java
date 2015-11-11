// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;

import android.os.AsyncTask;

import com.slimgears.slimasync.AsyncCallback;
import com.slimgears.slimasync.Callback;

import java.util.concurrent.Callable;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
public abstract class AbstractAsyncTaskBuilder<
        _Progress,
        _Response,
        _Callback extends AsyncCallback<_Response>,
        _CallbackBuilder extends AbstractAsyncCallbackBuilder<_Response, _Callback, _CallbackBuilder>,
        _Builder extends AbstractAsyncTaskBuilder<_Progress, _Response, _Callback, _CallbackBuilder, _Builder>>
        extends AbstractBuilder<AsyncTask<Void, _Progress, _Response>, _Builder> {

    protected _CallbackBuilder callbackBuilder;
    private Callable<_Callback> callbackProvider;

    abstract class AbstractAsyncTask extends ErrorHandlingAsyncTask<Void, _Progress, _Response> {
        _Callback callback;

        AbstractAsyncTask(_Callback callback) {
            this.callback = callback;
        }

        @Override
        protected void onComplete(_Response response) throws Exception {
            callback.onComplete(response);
        }

        @Override
        protected void onError(Throwable e) {
            callback.onError(e);
        }
    }

    public _Builder onComplete(Callback<_Response> successCallback) {
        callbackBuilder = callbackBuilder().onComplete(successCallback);
        return self();
    }

    public _Builder onError(Callback<Throwable> errorCallback) {
        callbackBuilder = callbackBuilder().onError(errorCallback);
        return self();
    }

    public _Builder callback(_Callback callback) {
        callbackProvider = () -> callback;
        return self();
    }

    @Override
    protected AsyncTask<Void, _Progress, _Response> safeBuild() {
        validate();
        try {
            return safeBuild(callbackProvider.call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AsyncTask<Void, _Progress, _Response> execute() {
        return build().execute();
    }

    protected _CallbackBuilder callbackBuilder() {
        if (callbackBuilder != null) return callbackBuilder;
        callbackBuilder = createCallbackBuilder();
        callbackProvider = callbackBuilder::build;
        return callbackBuilder;
    }

    @Override
    protected void validate() {
        if (callbackProvider == null)
            throw new IllegalArgumentException("Asynchronous callback was not specified");
    }

    protected abstract AbstractAsyncTask safeBuild(_Callback callback);

    protected abstract _CallbackBuilder createCallbackBuilder();
}
