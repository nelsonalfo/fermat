package com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces;

import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAsset;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAssetMetadata;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantGetDigitalAssetFromLocalStorageException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.RecordsNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.TransactionType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantExecuteLockOperationException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantFindTransactionException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantGetActorTransactionSummaryException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantGetTransactionsException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantStoreMemoException;

import java.util.List;
import java.util.UUID;

/**
 * Created by franklin on 05/10/15.
 */
public interface AssetUserWallet {

    //TODO:Documentar y manejo de excepciones

    AssetUserWalletBalance getBalance() throws CantGetTransactionsException;

    List<AssetUserWalletTransaction> getAllTransactions(String assetPublicKey) throws CantGetTransactionsException;

    List<AssetUserWalletTransaction> getAllAvailableTransactions(String assetPublicKey) throws CantGetTransactionsException;

    List<AssetUserWalletTransaction> getTransactionsForDisplay(String assetPublicKey) throws CantGetTransactionsException;

    List<AssetUserWalletTransaction> getTransactions(BalanceType balanceType,
                                                     TransactionType transactionType,
                                                     String assetPublicKey) throws CantGetTransactionsException;

    List<AssetUserWalletTransaction> getTransactions(BalanceType balanceType,
                                                     TransactionType transactionType,
                                                     int max,
                                                     int offset, String assetPublicKey) throws CantGetTransactionsException;

    List<AssetUserWalletTransaction> getTransactionsByActor(String actorPublicKey,
                                                            BalanceType balanceType,
                                                            int max,
                                                            int offset) throws CantGetTransactionsException;

    List<AssetUserWalletTransaction> gettLastActorTransactionsByTransactionType(BalanceType balanceType,
                                                                                TransactionType transactionType,
                                                                                int max,
                                                                                int offset) throws CantGetTransactionsException;

    void lockFunds(DigitalAssetMetadata metadata) throws RecordsNotFoundException, CantExecuteLockOperationException;

    void unlockFunds(DigitalAssetMetadata metadata) throws RecordsNotFoundException, CantExecuteLockOperationException;

    void setTransactionDescription(UUID transactionID,
                                   String description) throws CantFindTransactionException, CantStoreMemoException;

    AssetUserWalletTransactionSummary getActorTransactionSummary(String actorPublicKey,
                                                                 BalanceType balanceType) throws CantGetActorTransactionSummaryException;

    DigitalAssetMetadata getDigitalAssetMetadata(String transactionHash) throws CantGetDigitalAssetFromLocalStorageException;

    DigitalAsset getDigitalAsset(String assetPublicKey) throws CantGetDigitalAssetFromLocalStorageException;
}
