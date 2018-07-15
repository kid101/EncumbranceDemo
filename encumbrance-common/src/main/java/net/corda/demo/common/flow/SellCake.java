package net.corda.demo.common.flow;

import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@InitiatingFlow
public abstract class SellCake extends FlowLogic<SignedTransaction> {
    protected static final Logger logger = LoggerFactory.getLogger(SellCake.class);
}
