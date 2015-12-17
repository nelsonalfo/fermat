package com.bitdubai.fermat_cbp_api.layer.stock_transactions.bank_money_destock.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.FermatManager;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.layer.network_service.TransactionTransmission.exceptions.CantSendContractNewStatusNotificationException;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.bank_money_destock.exceptions.CantCreateBankMoneyDestockException;

import java.math.BigDecimal;


/**
 * Created by franklin on 16/11/15.
 */
public interface BankMoneyDestockManager  extends FermatManager {
        /**
         * Method that create the transaction Destock
         *
         * @param publicKeyActor
         * @param fiatCurrency
         * @param cbpWalletPublicKey
         * @param cbpWalletPublicKey
         * @param bankWalletPublicKey
         * @param bankAccount
         * @param amount
         * @param memo
         * @param priceReference
         * @param originTransaction
         *
         * @throws CantCreateBankMoneyDestockException
         */
        void createTransactionDestock(
            String publicKeyActor,
            FiatCurrency fiatCurrency,
            String cbpWalletPublicKey,
            String bankWalletPublicKey,
            String bankAccount,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction
        ) throws CantCreateBankMoneyDestockException;
}
