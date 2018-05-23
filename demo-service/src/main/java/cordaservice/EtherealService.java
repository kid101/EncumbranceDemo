package cordaservice;

import constant.ServiceConstant;
import invokers.HttpInvoker;
import invokers.HttpInvokerFactory;
import net.corda.core.node.AppServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;
import net.corda.demo.node.constant.NodeConstant;
import net.corda.demo.node.exception.DemoFlowException;
import net.corda.demo.node.exchange.GenericServiceRequest;
import net.corda.demo.node.exchange.GenericServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

import static constant.ServiceConstant.CACHE_FOLDER_PATH;

// Can be Later Deployed as an Independent Oracle Service as well If required.
@CordaService
public final class EtherealService extends SingletonSerializeAsToken {
    private static final Logger logger = LoggerFactory.getLogger(EtherealService.class);
    private final AppServiceHub appServiceHub;
    private File cacheDir;
    private final Timer timer;

    public EtherealService(AppServiceHub appServiceHub) {
        this.timer = new Timer();
        this.appServiceHub = appServiceHub;
        File cacheDir = new File(CACHE_FOLDER_PATH);
        if (cacheDir.mkdir() || cacheDir.exists()) {
            this.cacheDir = cacheDir;
        }
        timer.schedule(new PeriodicTask(), 0);
    }

    public GenericServiceResponse executeRequest(GenericServiceRequest request) {
        HttpInvoker factory = HttpInvokerFactory.getHttpInvoker(request.getMethod(), cacheDir);
        return factory.executeRequest(request.getUrl());
    }

    private class PeriodicTask extends TimerTask {
        @Override
        public void run() {
            // Get Requisite Party List from the outside world and write to a file
            GenericServiceResponse genericServiceResponse = executeRequest(new GenericServiceRequest("", ServiceConstant.PARTY_LIST_URL, HttpMethod.GET));
            if (genericServiceResponse.getData() != null) {
                try {
                    File responseFile = new File(NodeConstant.PARTY_LIST_PATH);
                    File responseFolder = responseFile.getParentFile();
                    if (!responseFolder.exists() && !responseFolder.mkdirs()) {
                        throw new IllegalStateException("Couldn't create dir: " + responseFolder);
                    }
                    Files.write(responseFile.toPath(), genericServiceResponse.getData().getBytes());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    throw new DemoFlowException(e.getMessage(), e.getCause());
                }
            }
            timer.schedule(new PeriodicTask(), 10 * 60 * 1000); // Delay of 10 mins
        }
    }
}
