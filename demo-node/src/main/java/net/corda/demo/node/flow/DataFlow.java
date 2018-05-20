package net.corda.demo.node.flow;

import net.corda.core.flows.FlowLogic;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.ArrayList;
import java.util.List;

public abstract class DataFlow extends FlowLogic<SignedTransaction> {
    // any generic function to be added in here.
    protected List<Party> getAllCounterParties(List<AbstractParty> abstractPartyList, Party party) {
        List<Party> parties = resolveIdentities(abstractPartyList);
        parties.remove(party);
        return parties;
    }

    protected List<Party> resolveIdentities(List<AbstractParty> abstractPartyList) {
        List<Party> allParties = new ArrayList();
        for (AbstractParty abstractParty : abstractPartyList) {
            allParties.add(resolveIdentity(abstractParty));
        }
        return allParties;
    }

    protected Party resolveIdentity(AbstractParty abstractParty) {
        return getServiceHub().getIdentityService().requireWellKnownPartyFromAnonymous(abstractParty);
    }
}
