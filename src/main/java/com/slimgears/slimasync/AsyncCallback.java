// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync;

/**
 * Created by Denis Itskovich on 11/10/2015.
 *
 */
public interface AsyncCallback<T> {
    void onComplete(T result);
    void onError(Throwable e);
}
