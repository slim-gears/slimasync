package com.slimgears.slimasync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
    private Callback<Throwable> onErrorMock;
    private static final String BACKGROUND_TASK_RESULT = "Success";

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        doInBackgroundMock = mock(Callable.class);
        when(doInBackgroundMock.call()).thenReturn(BACKGROUND_TASK_RESULT);

        onCompleteMock = mock(Callback.class);
        onErrorMock = mock(Callback.class);

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();
    }

    @Test
    public void buildAndExecuteAsyncTask_noError_shouldCallOnComplete() {
        Async.<String>asyncTaskBuilder()
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
    public void buildAndExecuteAsyncTask_withError_shouldCallOnError() {
        when(doInBackgroundMock.call()).thenThrow(new RuntimeException("Error"));

        Async.<String>asyncTaskBuilder()
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
}
