package com.slimgears.slimasync;

import com.slimgears.slimasync.internal.AbstractAsyncProgressTaskBuilder;
import com.slimgears.slimasync.internal.AsyncProgressCallbackBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Denis on 19/10/2015
 * <File Description>
 */
public class DownloadTask {
    abstract static class Downloader<Result, Stream extends OutputStream> extends CopyStreamTask.Copier<URLConnection, Result, Stream> {
        private final URL url;

        Downloader(URL url, int chunkSize) {
            super(chunkSize);
            this.url = url;
        }

        @Override
        protected URLConnection createParam() throws Exception {
            URLConnection connection = url.openConnection();
            connection.connect();
            return connection;
        }

        @Override
        protected InputStream createInputStream(URLConnection urlConnection) throws IOException {
            return urlConnection.getInputStream();
        }

        @Override
        protected void close(URLConnection urlConnection) throws IOException {
        }

        @Override
        protected long getSize(URLConnection urlConnection) throws IOException {
            return urlConnection.getContentLength();
        }
    }

    public static class DownloadTaskBuilder<R> extends AbstractAsyncProgressTaskBuilder<Integer, R, DownloadTaskBuilder<R>> {

        private URL url;
        private int chunkSize = 4096;
        private CopyStreamTask.ByteArrayDecoder<R> decoder;

        public DownloadTaskBuilder<R> decodeWith(CopyStreamTask.ByteArrayDecoder<R> decoder) {
            this.decoder = decoder;
            return this;
        }

        public DownloadTaskBuilder<R> url(String url) {
            try {
                return url(new URL(url));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        public DownloadTaskBuilder<R> url(URL url) {
            this.url = url;
            return this;
        }

        @Override
        protected void validate() {
            if (url == null) throw new IllegalArgumentException("URL not specified");
            if (decoder == null) throw new IllegalArgumentException("Decoder was not provided");

            Downloader<R, ByteArrayOutputStream> downloader = new Downloader<R, ByteArrayOutputStream>(url, chunkSize) {
                @Override
                protected ByteArrayOutputStream createOutputStream() throws IOException {
                    return new ByteArrayOutputStream();
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
            return Async.progressCallbackBuilder();
        }

        @Override
        protected DownloadTaskBuilder<R> self() {
            return this;
        }
    }

    public static <R> DownloadTaskBuilder<R> downloadTask() {
        return new DownloadTaskBuilder<>();
    }

    public static DownloadTaskBuilder<byte[]> binaryDownloadTask() {
        return DownloadTask.<byte[]>downloadTask().decodeWith(b -> b);
    }
}
