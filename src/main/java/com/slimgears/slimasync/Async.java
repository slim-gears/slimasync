package com.slimgears.slimasync;

import com.slimgears.slimasync.internal.AsyncCallbackBuilder;
import com.slimgears.slimasync.internal.AsyncProgressCallbackBuilder;
import com.slimgears.slimasync.internal.AsyncProgressTaskBuilder;
import com.slimgears.slimasync.internal.AsyncTaskBuilder;

public class Async {
    private final static AsyncCallback NO_CALLBACK = new AsyncCallback() {
        @Override
        public void onComplete(Object result) {

        }

        @Override
        public void onError(Throwable e) {
            throw new RuntimeException(e);
        }
    };

    public static <R> AsyncCallback<R> noCallback() {
        //noinspection unchecked
        return (AsyncCallback<R>)NO_CALLBACK;
    }

    public static <P, R> AsyncProgressCallbackBuilder<P, R> progressCallbackBuilder() {
        return new AsyncProgressCallbackBuilder<>();
    }

    public static <R>AsyncCallbackBuilder<R> callbackBuilder() {
        return new AsyncCallbackBuilder<>();
    }

    public static <P, R> AsyncProgressTaskBuilder<P, R> progressTaskBuilder() {
        return new AsyncProgressTaskBuilder<>();
    }

    public static <R>AsyncTaskBuilder<R> taskBuilder() {
        return new AsyncTaskBuilder<>();
    }

    public static <P, R> AsyncProgressProvider<P, R> fromAsyncProvider(AsyncProvider<R> provider, P finalProgress) {
        return callback -> provider.get(new AsyncCallback<R>() {
            @Override
            public void onComplete(R result) throws Exception {
                callback.onProgressUpdate(finalProgress);
                callback.onComplete(result);
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }
        });
    }
}
