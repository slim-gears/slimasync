package com.slimgears.slimasync.internal;

import android.os.AsyncTask;

/**
 * Created by Denis on 14/10/2015
 * <File Description>
 */
@SuppressWarnings("unchecked")
public abstract class ErrorHandlingAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private Throwable exception = null;

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doSafeInBackground(params);
        } catch (Throwable e) {
            exception = e;
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (exception != null) onError(exception);
        else {
            try {
                onComplete(result);
            } catch (Exception e) {
                onError(e);
            }
        }
    }

    protected abstract Result doSafeInBackground(Params... params) throws Throwable;

    protected abstract void onComplete(Result result) throws Exception;

    protected abstract void onError(Throwable e);
}
