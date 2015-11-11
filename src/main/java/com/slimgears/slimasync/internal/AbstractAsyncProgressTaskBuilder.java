// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;

import com.slimgears.slimasync.AsyncProgressCallback;
import com.slimgears.slimasync.Callback;
import com.slimgears.slimasync.ProgressCallable;
import com.slimgears.slimasync.SafeCallback;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
public abstract class AbstractAsyncProgressTaskBuilder<P, R, B extends AbstractAsyncProgressTaskBuilder<P, R, B>>
        extends AbstractAsyncTaskBuilder<P, R, AsyncProgressCallback<P, R>, AsyncProgressCallbackBuilder<P, R>, B> {
    private ProgressCallable<P, R> task;

    class AbstractAsyncProgressTask extends AbstractAsyncTask {
        AbstractAsyncProgressTask(AsyncProgressCallback<P, R> callback) {
            super(callback);
        }

        @Override
        protected R doSafeInBackground(Void... params) throws Throwable {
            //noinspection Convert2MethodRef,unchecked
            return task.call(p -> this.publishProgress(p));
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(P... progress) {
            callback.onProgressUpdate(progress.length > 0 ? progress[0] : null);
        }
    }

    public B doInBackground(ProgressCallable<P, R> task) {
        this.task = task;
        return self();
    }

    public B onProgressUpdate(SafeCallback<P> progressCallback) {
        callbackBuilder.onProgressUpdate(progressCallback);
        return self();
    }

    @Override
    protected AbstractAsyncProgressTask safeBuild(AsyncProgressCallback<P, R> callback) {
        return new AbstractAsyncProgressTask(callback);
    }
}
