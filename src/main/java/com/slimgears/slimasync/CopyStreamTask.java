package com.slimgears.slimasync;

import com.slimgears.slimasync.internal.AbstractAsyncProgressTaskBuilder;
import com.slimgears.slimasync.internal.AsyncProgressCallbackBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Denis on 14/10/2015
 * <File Description>
 */
public class CopyStreamTask {
    public interface Finilizer<T> {
        void close(T arg) throws IOException;
    }

    public interface InputStreamProvider<T> {
        InputStream createStream(T param) throws IOException;
    }

    public interface ContentSizeProvider<T> {
        long getSize(T param);
    }

    static abstract class Copier<Param, Result, Stream extends OutputStream> implements ProgressCallable<Integer, Result> {
        private final int chunkSize;

        Copier(int chunkSize) {
            this.chunkSize = chunkSize;
        }

        protected abstract Param createParam() throws Exception;
        protected abstract Stream createOutputStream() throws IOException;
        protected abstract InputStream createInputStream(Param param) throws IOException;
        protected abstract void close(Param param) throws IOException;
        protected abstract long getSize(Param param) throws IOException;

        private Result copyAll(InputStream input, long totalSize, ProgressObserver<Integer> progressObserver) throws IOException {
            long copiedTotal = 0;
            long copiedChunk;

            try (Stream output = createOutputStream()) {
                progressObserver.onProgressUpdate(0);

                do {
                    copiedChunk = copyChunk(input, output);
                    copiedTotal += copiedChunk;

                    int progress = (copiedChunk >= 0) ? getProgress(totalSize, copiedTotal) : 100;
                    progressObserver.onProgressUpdate(progress);
                } while (copiedChunk > 0);

                return onCompleted(output);
            }
        }

        private int getProgress(long totalSize, long copiedSize) {
            if (totalSize <= 0) totalSize = copiedSize + 3 * chunkSize;
            return (int)(copiedSize * 100 / totalSize);
        }

        private int copyChunk(InputStream input, OutputStream output) throws IOException {
            byte data[] = new byte[chunkSize];
            int size = input.read(data);
            if (size <= 0) return size;

            output.write(data, 0, size);
            return size;
        }

        protected abstract Result onCompleted(Stream stream);

        @Override
        public Result call(ProgressObserver<Integer> progressObserver) {
            try {
                Param param = createParam();
                try {
                    long fileLength = getSize(param);

                    try (InputStream input = createInputStream(param)) {
                        return copyAll(input, fileLength, progressObserver);
                    }

                } finally {
                    close(param);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface ByteArrayDecoder<T> {
        T decode(byte[] bytes);
    }

    public static class CopyTaskBuilder<P, R> extends AbstractAsyncProgressTaskBuilder<Integer, R, CopyTaskBuilder<P, R>> {
        private int chunkSize = 4096;
        private ByteArrayDecoder<R> decoder;
        private InputStreamProvider<P> streamProvider;
        private Finilizer<P> finalizer = p -> {};
        private ContentSizeProvider<P> sizeProvider = p -> -1;
        private Callable<P> paramProvider = () -> null;

        @Override
        protected CopyTaskBuilder<P, R> self() {
            return this;
        }

        public CopyTaskBuilder<P, R> from(Callable<P> paramProvider) {
            this.paramProvider = paramProvider;
            return this;
        }

        public CopyTaskBuilder<P, R> withStream(InputStreamProvider<P> streamProvider) {
            this.streamProvider = streamProvider;
            return this;
        }

        public CopyTaskBuilder<P, R> withSize(ContentSizeProvider<P> sizeProvider) {
            this.sizeProvider = sizeProvider;
            return this;
        }

        public CopyTaskBuilder<P, R> decodeWith(ByteArrayDecoder<R> decoder) {
            this.decoder = decoder;
            return this;
        }


        public CopyTaskBuilder<P, R> finilizer(Finilizer<P> finalizer) {
            this.finalizer = finalizer;
            return this;
        }

        public CopyTaskBuilder<P, R> chunkSize(int size) {
            chunkSize = size;
            return this;
        }

        @Override
        protected void validate() {
            if (streamProvider == null) throw new RuntimeException("Stream provider was not specified");
            if (decoder == null) throw new RuntimeException("Decoder was not specified");

            ProgressCallable<Integer, R> downloader = new Copier<P, R, ByteArrayOutputStream>(chunkSize) {
                @Override
                protected P createParam() throws Exception {
                    return paramProvider.call();
                }

                @Override
                protected ByteArrayOutputStream createOutputStream() {
                    return new ByteArrayOutputStream();
                }

                @Override
                protected InputStream createInputStream(P p) throws IOException {
                    return streamProvider.createStream(p);
                }

                @Override
                protected void close(P p) throws IOException {
                    finalizer.close(p);
                }

                @Override
                protected long getSize(P p) throws IOException {
                    return sizeProvider.getSize(p);
                }

                @Override
                protected R onCompleted(ByteArrayOutputStream stream) {
                    return decoder.decode(stream.toByteArray());
                }
            };

            super.doInBackground(downloader);
            super.validate();
        }

        @Override
        protected AsyncProgressCallbackBuilder<Integer, R> createCallbackBuilder() {
            return Async.<Integer, R>progressCallbackBuilder();
        }
    }

    public static <P> CopyTaskBuilder<P, byte[]> binaryCopyBuilder() {
        return CopyStreamTask.<P, byte[]>copyBuilder().decodeWith(bytes -> bytes);
    }

    public static <P, R> CopyTaskBuilder<P, R> copyBuilder() {
        return new CopyTaskBuilder<>();
    }
}
