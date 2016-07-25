package com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransactionStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationType;
import com.bitdubai.fermat_cbp_api.layer.negotiation_transaction.customer_broker_update.interfaces.CustomerBrokerUpdate;

import java.util.UUID;

/**
 * Created by Yordin Alayn on 16.12.15.
 */
public class CustomerBrokerUpdateImpl implements CustomerBrokerUpdate {

    private static final int HASH_PRIME_NUMBER_PRODUCT = 1019;
    private static final int HASH_PRIME_NUMBER_ADD = 1811;

    private UUID transactionId;
    private UUID negotiationId;
    private String publicKeyBroker;
    private String publicKeyCustomer;
    private NegotiationTransactionStatus negotiationTransactionStatus;
    private NegotiationType negotiationType;
    private String negotiationXML;
    private long timestamp;

    public CustomerBrokerUpdateImpl(
            UUID transactionId,
            UUID negotiationId,
            String publicKeyBroker,
            String publicKeyCustomer,
            NegotiationTransactionStatus negotiationTransactionStatus,
            NegotiationType negotiationType,
            String negotiationXML,
            long timestamp
    ) {
        this.transactionId = transactionId;
        this.negotiationId = negotiationId;
        this.publicKeyBroker = publicKeyBroker;
        this.publicKeyCustomer = publicKeyCustomer;
        this.negotiationTransactionStatus = negotiationTransactionStatus;
        this.negotiationType = negotiationType;
        this.negotiationXML = negotiationXML;
        this.timestamp = timestamp;
    }

    public UUID getTransactionId() {
        return this.transactionId;
    }

    public UUID getNegotiationId() {
        return this.negotiationId;
    }

    public String getPublicKeyBroker() {
        return this.publicKeyBroker;
    }

    public String getPublicKeyCustomer() {
        return this.publicKeyCustomer;
    }

    public NegotiationTransactionStatus getStatusTransaction() {
        return this.negotiationTransactionStatus;
    }

    public NegotiationType getNegotiationType() {
        return this.negotiationType;
    }

    public String getNegotiationXML() {
        return this.negotiationXML;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean equals(Object o) {
        if (!(o instanceof CustomerBrokerUpdateImpl))
            return false;
        CustomerBrokerUpdateImpl compare = (CustomerBrokerUpdateImpl) o;
        return publicKeyBroker.equals(compare.getPublicKeyBroker()) && publicKeyCustomer.equals(compare.getPublicKeyCustomer());
    }

    @Override
    public int hashCode() {
        int c = 0;
        c += publicKeyBroker.hashCode();
        c += publicKeyCustomer.hashCode();
        return HASH_PRIME_NUMBER_PRODUCT * HASH_PRIME_NUMBER_ADD + c;
    }
}
