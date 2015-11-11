// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync.internal;

import com.slimgears.slimasync.Async;
import com.slimgears.slimasync.AsyncCallback;

import java.util.concurrent.Callable;

/**
 * Created by ditskovi on 11/4/2015.
 *
 */
public class AsyncTaskBuilder<R> extends AbstractAsyncTaskBuilder<Void, R, AsyncCallback<R>, AsyncCallbackBuilder<R>, AsyncTaskBuilder<R>> {
    private Callable<R> task;

    public AsyncTaskBuilder<R> doInBackground(Callable<R> task) {
        this.task = task;
        return this;
    }

    @Override
    protected AsyncTaskBuilder<R> self() {
        return this;
    }

    @Override
    protected void validate() {
        super.validate();
        if (task == null)
            throw new IllegalArgumentException("Nothing to do - no background task was specified. Use doInBackground() to define.");
    }

    @Override
    protected AbstractAsyncTask safeBuild(AsyncCallback<R> callback) {
        return new AbstractAsyncTask(callback) {
            @Override
            protected R doSafeInBackground(Void... voids) throws Throwable {
                return task.call();
            }
        };
    }

    @Override
    protected AsyncCallbackBuilder<R> createCallbackBuilder() {
        return Async.<R>callbackBuilder();
    }
}
