package com.bitdubai.fermat_cbp_api.layer.cbp_stock_transactions.cash_money_restock.interfaces;

import com.bitdubai.fermat_cbp_api.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_cbp_api.layer.cbp_stock_transactions.cash_money_restock.exceptions.CantCreateCashMoneyRestockException;


/**
 * Created by franklin on 16/11/15.
 */
public interface CashMoneyRestockManager {

    void createTransactionRestock(
            String publicKeyActor,
            FiatCurrency fiatCurrency,
            String cbpWalletPublicKey,
            String bankWalletPublicKey,
            String cashReference,
            float amount,
            String memo
    ) throws CantCreateCashMoneyRestockException;
}
