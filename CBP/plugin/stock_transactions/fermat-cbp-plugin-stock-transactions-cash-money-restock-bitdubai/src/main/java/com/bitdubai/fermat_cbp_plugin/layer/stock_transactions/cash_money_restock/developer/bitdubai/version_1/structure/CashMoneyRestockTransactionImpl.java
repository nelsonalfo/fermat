package com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.structure;


import com.bitdubai.fermat_cbp_api.all_definition.business_transaction.CashMoneyTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionStatusRestockDestock;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by franklin on 17/11/15.
 */
public class CashMoneyRestockTransactionImpl implements CashMoneyTransaction {
    UUID                            transactionId;
    String                          actorPublicKey;
    FiatCurrency                    fiatCurrency;
    String                          cbpWalletPublicKey;
    String                          bnkWalletPublicKey;
    String                          memo;
    String                          concept;
    String                          cashReference;
    BigDecimal amount;
    Timestamp                       timeStamp;
    TransactionStatusRestockDestock transactionStatus;
    BigDecimal                           priceReference;
    OriginTransaction               originTransaction;

    public CashMoneyRestockTransactionImpl(){

    };

    public CashMoneyRestockTransactionImpl(UUID transactionId,
                                           String actorPublicKey,
                                           FiatCurrency fiatCurrency,
                                           String cbpWalletPublicKey,
                                           String bnkWalletPublicKey,
                                           String memo,
                                           String concept,
                                           String cashReference,
                                           BigDecimal amount,
                                           Timestamp timeStamp,
                                           TransactionStatusRestockDestock transactionStatus,
                                           BigDecimal priceReference,
                                           OriginTransaction originTransaction){
        this.transactionId      = transactionId;
        this.actorPublicKey     = actorPublicKey;
        this.fiatCurrency       = fiatCurrency;
        this.cbpWalletPublicKey = cbpWalletPublicKey;
        this.bnkWalletPublicKey = bnkWalletPublicKey;
        this.memo               = memo;
        this.concept            = concept;
        this.cashReference        = cashReference;
        this.amount             = amount;
        this.timeStamp          = timeStamp;
        this.transactionStatus  = transactionStatus;
        this.priceReference     = priceReference;
        this.originTransaction  = originTransaction;
    }



    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getActorPublicKey() {
        return actorPublicKey;
    }

    @Override
    public void setActorPublicKey(String actorPublicKey) {
        this.actorPublicKey = actorPublicKey;
    }

    @Override
    public FiatCurrency getFiatCurrency() {
        return fiatCurrency;
    }

    @Override
    public void setFiatCurrency(FiatCurrency fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }

    @Override
    public String getCbpWalletPublicKey() {
        return cbpWalletPublicKey;
    }

    @Override
    public void setCbpWalletPublicKey(String cbpWalletPublicKey) {
        this.cbpWalletPublicKey = cbpWalletPublicKey;
    }

    @Override
    public String getCashWalletPublicKey() {
        return bnkWalletPublicKey;
    }

    @Override
    public void setCashWalletPublicKey(String bnkWalletPublicKey) {
        this.bnkWalletPublicKey = bnkWalletPublicKey;
    }

    @Override
    public String getCashReference() {
        return cashReference;
    }

    @Override
    public void setCashReference(String cashReference) {
        this.cashReference = cashReference;
    }

    @Override
    public String getConcept() {
        return concept;
    }

    @Override
    public void setConcept(String concept) {
        this.concept = concept;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String getMemo() {
        return memo;
    }

    @Override
    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public TransactionStatusRestockDestock getTransactionStatus() {
        return transactionStatus;
    }

    @Override
    public void setTransactionStatus(TransactionStatusRestockDestock transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    @Override
    public BigDecimal getPriceReference() {
        return priceReference;
    }

    @Override
    public void setPriceReference(BigDecimal priceReference) {
        this.priceReference = priceReference;
    }

    @Override
    public OriginTransaction getOriginTransaction() {
        return originTransaction;
    }

    @Override
    public void setOriginTransaction(OriginTransaction originTransaction) {
        this.originTransaction = originTransaction;
    }
}
