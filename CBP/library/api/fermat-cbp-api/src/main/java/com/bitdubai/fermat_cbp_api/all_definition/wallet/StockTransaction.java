package com.bitdubai.fermat_cbp_api.all_definition.wallet;

import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by jorge on 30-09-2015.
 */
public interface StockTransaction {
    //TODO: Documentar

    UUID getTransactionId();

    BalanceType getBalanceType();

    TransactionType getTransactionType();

    CurrencyType getCurrencyType();

    FermatEnum getMerchandise();

    String getWalletPublicKey();

    String getBrokerPublicKey();

    BigDecimal getAmount();

    long getTimestamp();

    String getMemo();

    BigDecimal getPriceReference();

    OriginTransaction getOriginTransaction();
}
