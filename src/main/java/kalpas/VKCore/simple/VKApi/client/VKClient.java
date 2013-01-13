package kalpas.VKCore.simple.VKApi.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

public abstract class VKClient {

    private Logger logger = Logger.getLogger(VKClient.class);

    public InputStream send(String request) {
        return handleResponseInternal(sendInternal(request));
    }

    public VKAsyncResult sendAsync(String request) {
        return new VKAsyncResult(sendAsyncInternal(request));
    }

    protected HttpResponse sendInternal(String request) {
        throw new UnsupportedOperationException();
    }

    protected Future<HttpResponse> sendAsyncInternal(String request) {
        throw new UnsupportedOperationException();
    }

    protected InputStream handleResponseInternal(HttpResponse response) {

        if (response == null) {
            logger.fatal("null http response");
            return null;
        }
        HttpEntity entity = response.getEntity();
        InputStream stream = null;
        if (entity != null) {
            try {
                stream = entity.getContent();
            } catch (IllegalStateException | IOException e) {
                logger.error("error ", e);
            }
        }
        return stream;
    }

    public class VKAsyncResult implements Future<InputStream> {

        private Future<HttpResponse> future;

        @SuppressWarnings("unused")
        private VKAsyncResult() {
        }

        public VKAsyncResult(Future<HttpResponse> future) {
            this.future = future;
        }

        public InputStream get() {
            InputStream result = null;
            try {
                result = handleResponseInternal(future.get());
            } catch (InterruptedException e) {
                logger.error("interrupted", e);
            } catch (ExecutionException e) {
                logger.error("exec exception", e);
            }
            return result;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }

        @Override
        public InputStream get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                TimeoutException {
            InputStream result = null;
            try {
                result = handleResponseInternal(future.get(timeout, unit));
            } catch (InterruptedException e) {
                logger.error("interrupted", e);
            } catch (ExecutionException e) {
                logger.error("exec exception", e);
            }
            return result;
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return future.isDone();
        }

    }
}
