package net.kalpas.VKCore.simple.VKApi.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public abstract class VKClient {

    private final String OK     = "OK";

    private Logger       logger = LogManager.getLogger(VKClient.class);

    private long         lastRequest = System.currentTimeMillis();
    private long         offset      = 500L;

    public Result send(String request) {
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

    protected Result handleResponseInternal(HttpResponse response) {

        if (response == null) {
            logger.fatal("null http response");
            return new Result(-1);
        } else if (!OK.equals(response.getStatusLine().getReasonPhrase())) {
            Result result = new Result(response.getStatusLine().getStatusCode());
            result.errMsg = response.getStatusLine().toString();
            logger.error(result.errMsg);
            return result;
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
        return new Result(stream);
    }

    protected void sleepIfNeeded() {
        long now = System.currentTimeMillis();
        long diff = now - lastRequest;
        if (diff < offset) {
            try {
                logger.debug("sleeping for {} ms", offset - diff);
                Thread.sleep(offset - diff);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        } else {
            logger.debug("no sleep");
        }
        lastRequest = System.currentTimeMillis();
    }

    public class VKAsyncResult implements Future<Result> {

        private Future<HttpResponse> future;

        @SuppressWarnings("unused")
        private VKAsyncResult() {
        }

        public VKAsyncResult(Future<HttpResponse> future) {
            this.future = future;
        }

        public Result get() {
            Result result = null;
            try {
                result = handleResponseInternal(future.get());
            } catch (InterruptedException e) {
                logger.error("interrupted", e);
            } catch (ExecutionException e) {
                logger.error("exec exception, possibly cause bu too many requests or network issue", e);
            } catch (Throwable e) {
                logger.fatal("SMTH REALLLY BAD HAPPENED", e);
            }
            return result;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }

        @Override
        public Result get(long timeout, TimeUnit unit) throws TimeoutException {
            Result result = null;
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
