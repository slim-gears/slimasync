// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimasync;

/**
 * Created by ditskovi on 11/10/2015.
 *
 */
public interface SafeCallback<T> {
    void call(T arg);
}
