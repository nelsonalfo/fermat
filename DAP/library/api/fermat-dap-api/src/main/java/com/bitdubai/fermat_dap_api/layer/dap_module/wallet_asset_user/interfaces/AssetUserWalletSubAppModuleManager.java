package com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_user.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.resources_structure.Resource;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.interfaces.ModuleManager;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginBinaryFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.AssetNegotiation;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetAssetNegotiationsException;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserGroupException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserGroup;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantGetAssetRedeemPointActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_identity.asset_user.interfaces.IdentityAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_middleware.dap_asset_factory.exceptions.CantGetAssetFactoryException;
import com.bitdubai.fermat_dap_api.layer.dap_middleware.dap_asset_factory.interfaces.AssetFactory;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_user.AssetUserSettings;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.asset_buyer.exceptions.CantProcessBuyingTransactionException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.asset_seller.exceptions.CantStartAssetSellTransactionException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.asset_transfer.exceptions.CantTransferDigitalAssetsException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantExecuteAppropriationTransactionException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.user_redemption.exceptions.CantRedeemDigitalAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces.AssetUserWallet;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces.AssetUserWalletList;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantCreateWalletException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantLoadWalletException;
import com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_manager.exceptions.CantListWalletsException;

import java.util.List;
import java.util.UUID;

/**
 * Created by franklin on 16/10/15.
 */
public interface AssetUserWalletSubAppModuleManager extends ModuleManager<AssetUserSettings, ActiveActorIdentityInformation> {
    /**
     * (non-Javadoc)
     *
     * @see List<AssetUserWalletList> getAssetIssuerWalletBalancesBook(String publicKey)
     */
    List<AssetUserWalletList> getAssetUserWalletBalances(String publicKey) throws CantLoadWalletException;

    List<ActorAssetRedeemPoint> getAllActorAssetRedeemPointConnected() throws CantGetAssetRedeemPointActorsException;

    List<ActorAssetRedeemPoint> getRedeemPointsConnectedForAsset(String assetPublicKey) throws CantGetAssetRedeemPointActorsException;

    AssetUserWallet loadAssetUserWallet(String walletPublicKey) throws CantLoadWalletException;

    void createAssetUserWallet(String walletPublicKey) throws CantCreateWalletException;

    IdentityAssetUser getActiveAssetUserIdentity() throws CantGetIdentityAssetUserException;

    void redeemAssetToRedeemPoint(String digitalAssetPublicKey, String walletPublicKey, List<ActorAssetRedeemPoint> actorAssetRedeemPoints, int assetAmount) throws CantRedeemDigitalAssetException;

    void appropriateAsset(String digitalAssetPublicKey, String bitcoinWalletPublicKey) throws CantExecuteAppropriationTransactionException;

    AssetFactory getAssetFactory(final String publicKey) throws CantGetAssetFactoryException, CantCreateFileException;

    PluginBinaryFile getAssetFactoryResource(Resource resource) throws FileNotFoundException, CantCreateFileException;

    void changeNetworkType(BlockchainNetworkType networkType);

    BlockchainNetworkType getSelectedNetwork();

    // ASSET TRANSFER METHODS.

    List<ActorAssetUser> getAllActorUserRegistered() throws CantGetAssetUserActorsException;

    List<ActorAssetUser> getAllAssetUserActorConnected() throws CantGetAssetUserActorsException;

    List<ActorAssetUserGroup> getAssetUserGroupsList() throws CantGetAssetUserGroupException;

    List<ActorAssetUser> getListActorAssetUserByGroups(String groupName) throws CantGetAssetUserActorsException;

    void addUserToDeliver(ActorAssetUser user);

    void addGroupToDeliver(ActorAssetUserGroup group) throws CantGetAssetUserActorsException;

    void removeUserToDeliver(ActorAssetUser user);

    void removeGroupToDeliver(ActorAssetUserGroup group) throws CantGetAssetUserActorsException;

    void clearDeliverList();

    void addAllRegisteredUsersToDeliver() throws CantGetAssetUserActorsException;

    List<ActorAssetUser> getSelectedUsersToDeliver();

    void transferAssets(String assetPublicKey, String walletPublicKey, int assetsAmount) throws CantTransferDigitalAssetsException;

    ActorAssetUser getActorByPublicKey(String publicKey) throws CantGetAssetUserActorsException;

    //ASSET SELL METHODS
    void startSell(ActorAssetUser userToDeliver, long amountPerUnity, long totalAmount, int quantityToBuy, String assetToOffer) throws CantStartAssetSellTransactionException;


    //ASSET BUY METHODS

    /**
     * Gets the list of pending asset negotiations
     *
     * @return a list of pending asset negotiations
     * @throws CantGetAssetNegotiationsException
     */
    List<AssetNegotiation> getPendingAssetNegotiations() throws CantGetAssetNegotiationsException;

    /**
     * This method notifies the seller that we've accepted one of its asset and the transaction for this asset can proceed.
     *
     * @param negotiationId {@link UUID} instance that is the {@link AssetNegotiation} ID.
     * @throws CantProcessBuyingTransactionException
     */
    void acceptAsset(UUID negotiationId) throws CantProcessBuyingTransactionException;

    /**
     * This method notifies the seller that we've rejected one of its asset and the transaction for this asset won't proceed.
     *
     * @param negotiationId {@link UUID} instance that is the {@link AssetNegotiation} ID.
     * @throws CantProcessBuyingTransactionException
     */
    void declineAsset(UUID negotiationId) throws CantProcessBuyingTransactionException;

    /**
     * This method get the available balance of bitcoin wallet
     * @param walletPublicKey
     * @return
     * @throws com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantLoadWalletException
     * @throws CantCalculateBalanceException
     */
    long getBitcoinWalletBalance(String walletPublicKey) throws com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantLoadWalletException, CantCalculateBalanceException;

    List<com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_manager.interfaces.InstalledWallet> getInstallWallets() throws CantListWalletsException;

}
