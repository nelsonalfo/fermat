package com.bitdubai.fermat_ccp_api.layer.basic_wallet.bitcoin_wallet.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantRegisterDebitException;

/**
 * Created by ciencias on 7/6/15.
 */
public interface BitcoinWalletBalance {

   /*
    * Get the balance of the wallet, the result represents the
    * amount of satoshis the user has.
   */

    long getBalance() throws CantCalculateBalanceException;

    long getBalance(BlockchainNetworkType blockchainNetworkType) throws CantCalculateBalanceException;


    void debit(BitcoinWalletTransactionRecord cryptoTransaction) throws CantRegisterDebitException;

    void credit(BitcoinWalletTransactionRecord cryptoTransaction) throws CantRegisterCreditException;

    void revertCredit(BitcoinWalletTransactionRecord cryptoTransaction) throws CantRegisterCreditException;


}
