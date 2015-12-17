package com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_restock.developer.bitdubai.version_1.utils;

import com.bitdubai.fermat_bnk_api.all_definition.bank_money_transaction.BankTransactionParameters;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by franklin on 18/11/15.
 */
public class BankTransactionParametersWrapper implements BankTransactionParameters {
    private final UUID            transactionId;
    private final FiatCurrency fiatCurrency;
    private final String          walletPublicKey;
    private final String          publicActorKey;
    private final String          account;
    private final BigDecimal amount;
    private final String          memo;
    private final String          publicKeyPlugin;

    public BankTransactionParametersWrapper(UUID            transactionId,
                                            FiatCurrency    fiatCurrency,
                                            String          walletPublicKey,
                                            String          publicActorKey,
                                            String          account,
                                            BigDecimal           amount,
                                            String          memo,
                                            String          publicKeyPlugin
    ){
        this.transactionId   = transactionId;
        this.fiatCurrency    = fiatCurrency;
        this.walletPublicKey = walletPublicKey;
        this.publicActorKey  = publicActorKey;
        this.account         = account;
        this.amount          = amount;
        this.memo            = memo;
        this.publicKeyPlugin = publicKeyPlugin;
    }
    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public String getPublicKeyPlugin() {
        return publicKeyPlugin;
    }

    @Override
    public String getPublicKeyWallet() {
        return walletPublicKey;
    }

    @Override
    public String getPublicKeyActor() {
        return publicActorKey;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency getCurrency() {
        return fiatCurrency;
    }

    @Override
    public String getMemo() {
        return memo;
    }
}
