package com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_purchase.developer.bitdubai.version_1.utils;

import com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus;
import com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.customer_broker_purchase.interfaces.CustomerBrokerPurchase;

import java.util.UUID;

/**
 * Created by franklin on 13/12/15.
 */
public class CustomerBrokerPurchaseImpl implements CustomerBrokerPurchase {
    private String transactionId;
    private String contractTransactionId;
    private long   timestamp;
    private String purchaseStatus;
    private String contractStatus;
    private TransactionStatus transactionStatus;
    private String currencyType;
    private String transactionType;
    private String memo;

    public CustomerBrokerPurchaseImpl(String transactionId,
                                      String contractTransactionId,
                                      long timestamp,
                                      String purchaseStatus,
                                      String contractStatus,
                                      TransactionStatus transactionStatus,
                                      String currencyType,
                                      String transactionType,
                                      String memo){
        this.transactionId = transactionId;
        this.contractTransactionId = contractTransactionId;
        this.timestamp             = timestamp;
        this.purchaseStatus        = purchaseStatus;
        this.contractStatus        = contractStatus;
        this.transactionStatus     = transactionStatus;
        this.currencyType          = currencyType;
        this.transactionType       = transactionType;
        this.memo                  = memo;
    };

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getContractTransactionId() {
        return contractTransactionId;
    }

    @Override
    public void setContractTransactionId(String contractTransactionId) {
        this.contractTransactionId = contractTransactionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPurchaseStatus() {
        return purchaseStatus;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    @Override
    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getMemo() {
        return memo;
    }
}
