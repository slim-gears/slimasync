package com.slimgears.slimasync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.Callable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16, manifest = Config.NONE)
public class AsyncTest {
    private Callable<String> doInBackgroundMock;
    private Callback<String> onCompleteMock;
    private SafeCallback<Throwable> onErrorMock;
    private SafeCallback<Integer> onProgressMock;
    private ProgressCallable<Integer, String> doInBackgroundWithProgressMock;
    private static final String BACKGROUND_TASK_RESULT = "Success";

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        doInBackgroundMock = mock(Callable.class);
        onCompleteMock = mock(Callback.class);
        onErrorMock = mock(SafeCallback.class);
        onProgressMock = mock(SafeCallback.class);

        doInBackgroundWithProgressMock = mock(ProgressCallable.class);

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();
    }

    @Test
    public void buildAndExecuteAsyncTask_noError_shouldCallOnComplete() throws Exception {
        when(doInBackgroundMock.call()).thenReturn(BACKGROUND_TASK_RESULT);

        Async.<String>taskBuilder()
                .doInBackground(doInBackgroundMock)
                .onComplete(onCompleteMock)
                .onError(onErrorMock)
                .execute();

        verify(doInBackgroundMock, never()).call();

        Robolectric.flushBackgroundThreadScheduler();
        verify(doInBackgroundMock, times(1)).call();
        verify(onCompleteMock, never()).call(any());

        Robolectric.flushForegroundThreadScheduler();
        verify(doInBackgroundMock, times(1)).call();
        verify(onCompleteMock, times(1)).call(BACKGROUND_TASK_RESULT);
        verify(onErrorMock, never()).call(any());
    }

    @Test
    public void buildAndExecuteAsyncTask_withError_shouldCallOnError() throws Exception {
        when(doInBackgroundMock.call()).thenThrow(new RuntimeException("Error"));

        Async.<String>taskBuilder()
                .doInBackground(doInBackgroundMock)
                .onComplete(onCompleteMock)
                .onError(onErrorMock)
                .execute();

        verify(doInBackgroundMock, never()).call();

        Robolectric.flushBackgroundThreadScheduler();
        verify(doInBackgroundMock, times(1)).call();
        verify(onCompleteMock, never()).call(any());

        Robolectric.flushForegroundThreadScheduler();
        verify(doInBackgroundMock, times(1)).call();
        verify(onCompleteMock, never()).call(any());
        verify(onErrorMock, times(1)).call(any(RuntimeException.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void buildAndExceuteAsyncProgressTask_noError_shouldCallOnCompleteAndOnProgress() throws Exception {
        when(doInBackgroundWithProgressMock.call(any(ProgressObserver.class))).then(invocation -> {
            ((ProgressObserver)invocation.getArguments()[0]).onProgressUpdate(1);
            ((ProgressObserver)invocation.getArguments()[0]).onProgressUpdate(2);
            return BACKGROUND_TASK_RESULT;
        });

        Async.<Integer, String>progressTaskBuilder()
                .doInBackground(doInBackgroundWithProgressMock)
                .onComplete(onCompleteMock)
                .onError(onErrorMock)
                .onProgressUpdate(onProgressMock)
                .execute();

        verify(doInBackgroundWithProgressMock, never()).call(any());

        Robolectric.flushBackgroundThreadScheduler();
        verify(doInBackgroundWithProgressMock, times(1)).call(Matchers.any());
        verify(onCompleteMock, never()).call(any());
        verify(onProgressMock, never()).call(any());

        Robolectric.flushForegroundThreadScheduler();
        verify(doInBackgroundWithProgressMock, times(1)).call(any());
        verify(onProgressMock, times(1)).call(1);
        verify(onProgressMock, times(1)).call(2);
        verify(onCompleteMock, times(1)).call(BACKGROUND_TASK_RESULT);
        verify(onErrorMock, never()).call(any());
    }

    @Test
    public void buildAndExceuteAsyncProgressTask_withError_shouldCallOnCompleteAndOnProgress() throws Exception {
        when(doInBackgroundWithProgressMock.call(any())).then(invocation -> {
            //noinspection unchecked
            ((ProgressObserver) invocation.getArguments()[0]).onProgressUpdate(1);
            throw new RuntimeException("Error");
        });

        Async.<Integer, String>progressTaskBuilder()
                .doInBackground(doInBackgroundWithProgressMock)
                .onComplete(onCompleteMock)
                .onError(onErrorMock)
                .onProgressUpdate(onProgressMock)
                .execute();

        verify(doInBackgroundWithProgressMock, never()).call(any());

        Robolectric.flushBackgroundThreadScheduler();
        verify(doInBackgroundWithProgressMock, times(1)).call(Matchers.any());
        verify(onCompleteMock, never()).call(any());
        verify(onProgressMock, never()).call(any());

        Robolectric.flushForegroundThreadScheduler();
        verify(doInBackgroundWithProgressMock, times(1)).call(any());
        verify(onProgressMock, times(1)).call(1);
        verify(onCompleteMock, never()).call(any());
        verify(onErrorMock, times(1)).call(any(RuntimeException.class));
    }
}
