package kalpas.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class VKClient {

    private Logger logger = Logger.getLogger(VKClient.class);

    public JSONObject send(String request) {
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

    protected JSONObject handleResponseInternal(HttpResponse response) {

        JSONObject result = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream stream = null;
            try {
                stream = entity.getContent();
                result = new JSONObject(IOUtils.toString(stream));
                stream.close();
            } catch (JSONException e) {
                logger.error("error parsing JSON ", e);
            } catch (IllegalStateException e) {
                logger.error("IO exception ", e);
            } catch (IOException e) {
                logger.error("cannot create stream from response ", e);
            } finally {
                try {
                    stream.close();
                } catch (Exception e) {
                    logger.fatal("check it. exeption in finally block", e);
                }
            }
        }
        return result;
    }

    public class VKAsyncResult implements Future<JSONObject> {

        private Future<HttpResponse> future;

        @SuppressWarnings("unused")
        private VKAsyncResult() {
        }

        public VKAsyncResult(Future<HttpResponse> future) {
            this.future = future;
        }

        public JSONObject get() {
            JSONObject result = null;
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
        public JSONObject get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                TimeoutException {
            JSONObject result = null;
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
