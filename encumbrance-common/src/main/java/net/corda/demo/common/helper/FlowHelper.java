package net.corda.demo.common.helper;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.demo.common.exception.AlreadyIssuedCakeIdException;
import net.corda.demo.common.exception.NoCakeFoundException;
import net.corda.demo.common.exception.NoExpiryFoundException;
import net.corda.demo.sc.state.Cake;
import net.corda.demo.sc.state.Expiry;

import java.util.Collections;

import static io.netty.util.internal.shaded.org.jctools.util.JvmInfo.PAGE_SIZE;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.DEFAULT_PAGE_NUM;

public class FlowHelper {
    public static StateAndRef<Cake> getCakeById(String cakeId, ServiceHub serviceHub) {
        Vault.Page<Cake> cakePage = serviceHub.getVaultService().queryBy(Cake.class);
        return cakePage.getStates().stream().filter(e -> e.getState().getData().getCakeId().equalsIgnoreCase(cakeId)).findAny().orElseThrow(() -> new NoCakeFoundException("No cake found with cakeId: " + cakeId));
    }

    public static StateAndRef<Expiry> getExpiryOfCake(String cakeId, ServiceHub serviceHub) {
        Vault.Page<Expiry> expiryPage = serviceHub.getVaultService().queryBy(Expiry.class);
        return expiryPage.getStates().stream().filter(e -> e.getState().getData().getCakeId().equalsIgnoreCase(cakeId)).findAny().orElseThrow(() -> new NoExpiryFoundException("No expiry found for cakeId:" + cakeId));
    }

    public static void checkIfCakeExists(String cakeId, ServiceHub serviceHub) {
        PageSpecification pageSpecification;
        Vault.Page<Cake> results;
        Integer pageNum = DEFAULT_PAGE_NUM;
        do {
            pageSpecification = new PageSpecification(pageNum, PAGE_SIZE);
            results = serviceHub.getVaultService().queryBy(Cake.class,
                    new QueryCriteria.VaultQueryCriteria(
                            Vault.StateStatus.ALL,
                            Collections.singleton(Cake.class)),
                    pageSpecification);
            if(results.getStates().stream().anyMatch(e -> e.getState().getData().getCakeId().equalsIgnoreCase(cakeId))) {
                throw new AlreadyIssuedCakeIdException(String.format("Cake already issued with cakeId: %s", cakeId));
            }
            pageNum++;
        } while ((pageSpecification.getPageSize() * (pageNum)) <= results.getTotalStatesAvailable());
    }
}
