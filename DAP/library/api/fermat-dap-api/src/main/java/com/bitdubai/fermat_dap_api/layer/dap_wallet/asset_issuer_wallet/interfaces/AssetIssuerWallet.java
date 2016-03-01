package com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces;

import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAsset;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAssetMetadata;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.AssetCurrentStatus;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.AssetMovementType;
import com.bitdubai.fermat_dap_api.layer.dap_actor.DAPActor;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_issuer.exceptions.CantGetAssetStatisticException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantGetDigitalAssetFromLocalStorageException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.RecordsNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantSaveStatisticException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.TransactionType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantFindTransactionException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantGetActorTransactionSummaryException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantGetTransactionsException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantStoreMemoException;

import java.util.List;
import java.util.UUID;

/**
 * Created by franklin on 07/09/15.
 */
public interface AssetIssuerWallet {

    //TODO:Documentar y manejo de excepciones

    AssetIssuerWalletBalance getBalance() throws CantGetTransactionsException;

    List<AssetIssuerWalletTransaction> getTransactionsAll(BalanceType balanceType,
                                                          TransactionType transactionType,
                                                          String assetPublicKey) throws CantGetTransactionsException;

    List<AssetIssuerWalletTransaction> getTransactions(BalanceType balanceType,
                                                       TransactionType transactionType,
                                                       int max,
                                                       int offset, String assetPublicKey) throws CantGetTransactionsException;

    List<AssetIssuerWalletTransaction> getAvailableTransactions(String assetPublicKey) throws CantGetTransactionsException;

    List<AssetIssuerWalletTransaction> getTransactionsByActor(String actorPublicKey,
                                                              BalanceType balanceType,
                                                              int max,
                                                              int offset) throws CantGetTransactionsException;

    List<AssetIssuerWalletTransaction> gettLastActorTransactionsByTransactionType(BalanceType balanceType,
                                                                                  TransactionType transactionType,
                                                                                  int max,
                                                                                  int offset) throws CantGetTransactionsException;

    void setTransactionDescription(UUID transactionID,
                                   String description) throws CantFindTransactionException, CantStoreMemoException;

    AssetIssuerWalletTransactionSummary getActorTransactionSummary(String actorPublicKey,
                                                                   BalanceType balanceType) throws CantGetActorTransactionSummaryException;

    List<AssetIssuerWalletTransaction> getTransactionsAssetAll(String assetPublicKey) throws CantGetTransactionsException;

    List<AssetIssuerWalletTransaction> getTransactionsForDisplay(String assetPublicKey) throws CantGetTransactionsException;

    DigitalAssetMetadata getDigitalAssetMetadata(String transactionHash) throws CantGetDigitalAssetFromLocalStorageException;

    DigitalAsset getAssetByPublicKey(String assetPublicKey);

    String getUserDeliveredToPublicKey(UUID transactionId) throws RecordsNotFoundException, CantGetAssetStatisticException;

    List<DigitalAssetMetadata> getAllUnusedAssets() throws CantGetDigitalAssetFromLocalStorageException;

    //ASSET STATISTIC METHODS

    void createdNewAsset(DigitalAssetMetadata assetMetadata) throws CantSaveStatisticException;

    void newMovement(DAPActor actorFrom, DAPActor actorTo, String assetPk, AssetMovementType type) throws CantSaveStatisticException;

    void assetDistributed(UUID transactionId, String actorAssetUserPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException;

    void assetRedeemed(UUID transactionId, String userPublicKey, String redeemPointPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException;

    void assetAppropriated(UUID transactionId, String userPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException;

    List<AssetStatistic> getAllStatisticForAllAssets() throws CantGetAssetStatisticException;

    List<AssetStatistic> getStatisticForAllAssetsByStatus(AssetCurrentStatus status) throws CantGetAssetStatisticException;

    List<AssetStatistic> getStatisticForGivenAssetByStatus(String assetName, AssetCurrentStatus status) throws CantGetAssetStatisticException;

    List<AssetStatistic> getAllStatisticForGivenAsset(String assetName) throws CantGetAssetStatisticException;

    int getUnusedAmountForAssetByStatus(AssetCurrentStatus status, String assetName) throws CantGetAssetStatisticException;
}
