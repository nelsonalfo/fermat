package com.bitdubai.fermat_cbp_api.layer.stock_transactions.crypto_money_destock.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.FermatManager;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.crypto_money_destock.exceptions.CantCreateCryptoMoneyDestockException;

import java.math.BigDecimal;


/**
 * Created by franklin on 16/11/15.
 */
public interface CryptoMoneyDestockManager  extends FermatManager {

    void createTransactionDestock(
            String publicKeyActor,
            CryptoCurrency cryptoCurrency,
            String cbpWalletPublicKey,
            String cryWalletPublicKey,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction
    ) throws CantCreateCryptoMoneyDestockException;
}
