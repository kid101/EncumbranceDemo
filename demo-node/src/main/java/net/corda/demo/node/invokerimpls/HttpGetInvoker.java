package net.corda.demo.node.invokerimpls;

import net.corda.demo.node.exchange.GenericServiceResponse;
import net.corda.demo.node.interceptor.AuthenticationInterceptor;
import net.corda.demo.node.invokers.HttpInvoker;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class HttpGetInvoker implements HttpInvoker {
    private static final Logger logger = LoggerFactory.getLogger(HttpGetInvoker.class);

    private final OkHttpClient client;

    public HttpGetInvoker(File cacheDirectory) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(cacheDirectory, cacheSize);
        client = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor())
                .cache(cache)
                .readTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public GenericServiceResponse executeRequest(String URL) {
        GenericServiceResponse serviceResponse = new GenericServiceResponse();
        Response response = null;
        Request request = new Request.Builder().url(URL).build();
        try {
            response = client.newCall(request).execute();
            serviceResponse.setStatus(response.message());
            serviceResponse.setData(response.body().string());
            return serviceResponse;
        } catch (Exception e) {
            logger.error(e.getMessage());
            serviceResponse.setError(e.getMessage());
            serviceResponse.setStatus(response.message());
            return serviceResponse;
        }
    }
}
