package com.bitdubai.fermat_cbp_api.layer.stock_transactions.crypto_money_destock.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.crypto_money_destock.exceptions.CantCreateCryptoMoneyDestockException;


/**
 * Created by franklin on 16/11/15.
 */
public interface CryptoMoneyDestockManager {

    void createTransactionDestock(
            String publicKeyActor,
            CryptoCurrency cryptoCurrency,
            String cbpWalletPublicKey,
            String bankWalletPublicKey,
            String bankAccount,
            float amount,
            String memo
    ) throws CantCreateCryptoMoneyDestockException;
}
