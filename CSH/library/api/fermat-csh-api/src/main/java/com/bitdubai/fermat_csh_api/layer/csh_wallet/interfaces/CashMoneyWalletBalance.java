package com.bitdubai.fermat_csh_api.layer.csh_wallet.interfaces;

import com.bitdubai.fermat_csh_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetBalanceException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletBalanceException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantRegisterDebitException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantRegisterHoldException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantRegisterUnholdException;

import java.util.UUID;

/**
 * Created by Yordin Alayn on 30.09.15.
 * Modified by Alejandro Bicelis on 23/11/2015
 */

public interface CashMoneyWalletBalance {


    /**
     * Returns the Balance this CashMoneyWalletBalance belongs to. (Can be available or book)
     *
     * @return A double, containing the balance.
     */
    double getBalance() throws CantGetCashMoneyWalletBalanceException;

    /**
     * Registers a debit on the Balance of this CashMoneyWalletBalance. (Can be available or book)
     *
     */

    void debit(UUID transactionId, String publicKeyActor, String publicKeyPlugin, float amount, String memo) throws CantRegisterDebitException;

    /**
     * Registers a credit on the Balance of his CashMoneyWalletBalance. (Can be available or book)
     *
     */
    void credit(UUID transactionId, String publicKeyActor, String publicKeyPlugin, float amount, String memo) throws CantRegisterCreditException;



}
