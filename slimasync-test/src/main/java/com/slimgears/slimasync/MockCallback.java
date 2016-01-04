package com.slimgears.slimasync;

import junit.framework.Assert;

import java.util.List;
import java.util.Stack;

/**
 * Created by Denis on 19/10/2015
 * <File Description>
 */
public class MockCallback<_R> implements AsyncCallback<_R>, AsyncProgressCallback<Integer, _R> {
    private final Stack<_R> receivedResponses = new Stack<>();
    private final Stack<Throwable> exceptions = new Stack<>();
    private final Stack<Integer> receivedProgress = new Stack<>();

    public List<_R> responses() {
        return receivedResponses;
    }

    public boolean hasResponses() {
        return !receivedResponses.isEmpty();
    }

    public _R lastResponse() {
        if (receivedResponses.isEmpty()) return null;
        return receivedResponses.peek();
    }

    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }

    public boolean hasProgressUpdates() { return !receivedProgress.isEmpty(); }

    public int progressUpdateCount() {
        return receivedProgress.size();
    }

    public void assertHasProgressUpdates() {
        Assert.assertTrue(hasProgressUpdates());
    }

    public void assertHasResponses() {
        Assert.assertTrue(hasResponses());
    }

    public void assertHasResponses(int number) {
        Assert.assertEquals(number, responses().size());
    }

    public void assertNoExceptions() {
        if (hasExceptions()) {
            throw new RuntimeException(lastException());
        }
    }

    public Throwable lastException() {
        if (!hasExceptions()) return null;
        return exceptions.peek();
    }

    @Override
    public void onComplete(_R response) {
        receivedResponses.push(response);
    }

    @Override
    public void onError(Throwable error) {
        //noinspection ThrowableResultOfMethodCallIgnored
        exceptions.push(error);
    }

    @Override
    public void onProgressUpdate(Integer progress) {
        receivedProgress.push(progress);
    }
}
