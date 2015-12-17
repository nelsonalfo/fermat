package com.bitdubai.reference_wallet.cash_money_wallet.common;

import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.deposit.interfaces.CashDepositTransactionParameters;

import java.util.UUID;

/**
 * Created by Alejandro Bicelis on 11/27/2015.
 */
public class CashDepositTransactionParametersImpl implements CashDepositTransactionParameters {

    private UUID transactionId;
    private String publicKeyWallet;
    private String publicKeyActor;
    private String publicKeyPlugin;
    private float amount;
    FiatCurrency currency;
    private String memo;


    public CashDepositTransactionParametersImpl(UUID transactionId, String publicKeyWallet, String publicKeyActor,
                                                String publicKeyPlugin, float amount, FiatCurrency currency, String memo)
    {
        this.transactionId = transactionId;
        this.publicKeyWallet = publicKeyWallet;
        this.publicKeyActor = publicKeyActor;
        this.publicKeyPlugin = publicKeyPlugin;
        this.amount = amount;
        this.currency = currency;
        this.memo = memo;
    }


    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public String getPublicKeyWallet() {
        return publicKeyWallet;
    }

    @Override
    public String getPublicKeyActor() {
        return publicKeyActor;
    }

    @Override
    public String getPublicKeyPlugin() { return this.publicKeyPlugin; }

    @Override
    public float getAmount() {
        return amount;
    }

    @Override
    public FiatCurrency getCurrency() {
        return currency;
    }

    @Override
    public String getMemo() {
        return memo;
    }
}
