// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync;

/**
 * Created by ditskovi on 11/11/2015.
 *
 */
public interface AsyncProvider<R> {
    void get(AsyncCallback<R> callback);
}
