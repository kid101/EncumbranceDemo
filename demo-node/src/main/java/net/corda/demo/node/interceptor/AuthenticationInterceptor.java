package net.corda.demo.node.interceptor;

import net.corda.demo.node.constant.ServiceConstant;
import net.corda.demo.node.util.NetworkUtil;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//Can be used to add any Authentication Headers later on.
public class AuthenticationInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        long t1 = System.nanoTime();
        builder.header(ServiceConstant.USER_AGENT, ServiceConstant.USER_AGENT_VALUE);
        if(!NetworkUtil.checkIfConnectionExists()){
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }
        Response response = chain.proceed(builder.build());
        long t2 = System.nanoTime();
        logger.info(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d,
                response.headers()));
        return response;
    }

}
