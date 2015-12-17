package com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util;

import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.interfaces.KeyPair;
import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransactionRecord;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Yordin Alayn on 19.10.15.
 * Modified by Franklin Marcano 01.12.2015
 */
public class CryptoBrokerStockTransactionRecordImpl implements CryptoBrokerStockTransactionRecord {
    private UUID transactionId;
    private KeyPair walletKeyPair;
    private String ownerPublicKey;
    private BalanceType balanceType;
    private TransactionType transactionType;
    private BigDecimal amount;
    private CurrencyType currencyType;
    private FermatEnum merchandise;
    private BigDecimal runningBookBalance;
    private BigDecimal runningAvailableBalance;
    private long timeStamp;
    private String memo;
    private OriginTransaction originTransaction;
    private BigDecimal priceReference;

    public CryptoBrokerStockTransactionRecordImpl(
            UUID transactionId,
            KeyPair walletKeyPair,
            String ownerPublicKey,
            BalanceType balanceType,
            TransactionType transactionType,
            CurrencyType currencyType,
            FermatEnum merchandise,
            BigDecimal amount,
            BigDecimal runningBookBalance,
            BigDecimal runningAvailableBalance,
            long timeStamp,
            String memo,
            OriginTransaction originTransaction,
            BigDecimal priceReference
    ){
        this.transactionId = transactionId;
        this.walletKeyPair = walletKeyPair;
        this.ownerPublicKey = ownerPublicKey;
        this.balanceType = balanceType;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currencyType = currencyType;
        this.merchandise = merchandise;
        this.runningBookBalance = runningBookBalance;
        this.runningAvailableBalance = runningAvailableBalance;
        this.timeStamp = timeStamp;
        this.memo = memo;
        this.priceReference = priceReference;
        this.originTransaction = originTransaction;
    }

    @Override
    public UUID getTransactionId() { return this.transactionId; }

    @Override
    public BalanceType getBalanceType() { return this.balanceType; }

    @Override
    public TransactionType getTransactionType() { return this.transactionType; }

    @Override
    public String getWalletPublicKey() { return this.walletKeyPair.getPublicKey(); }

    @Override
    public String getBrokerPublicKey() { return this.ownerPublicKey; }

    @Override
    public CurrencyType getCurrencyType() { return this.currencyType; }

    @Override
    public FermatEnum getMerchandise() { return this.merchandise; }

    @Override
    public BigDecimal getAmount() { return this.amount; }

    @Override
    public BigDecimal getRunningBookBalance() { return this.runningBookBalance; }

    @Override
    public BigDecimal getRunningAvailableBalance() { return this.runningAvailableBalance; }

    @Override
    public long getTimestamp() { return this.timeStamp; }

    @Override
    public String getMemo() { return this.memo; }

    @Override
    public BigDecimal getPriceReference() {
        return this.priceReference;
    }

    @Override
    public OriginTransaction getOriginTransaction() {
        return this.originTransaction;
    }

}