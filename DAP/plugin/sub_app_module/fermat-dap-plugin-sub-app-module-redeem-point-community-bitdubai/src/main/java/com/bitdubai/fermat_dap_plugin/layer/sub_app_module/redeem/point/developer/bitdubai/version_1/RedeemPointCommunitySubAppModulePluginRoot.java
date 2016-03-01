package com.bitdubai.fermat_dap_plugin.layer.sub_app_module.redeem.point.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededIndirectPluginReferences;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.settings.structure.SettingsManager;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.exceptions.ActorIdentityNotSelectedException;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPConnectionState;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantConnectToActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.RedeemPointActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantAssetRedeemPointActorNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantConnectToActorAssetRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantCreateActorRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantGetAssetRedeemPointActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantUpdateRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.RedeemPointNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPointManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.exceptions.CantRequestListActorAssetRedeemPointRegisteredException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.interfaces.AssetRedeemPointActorNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_identity.redeem_point.exceptions.CantGetRedeemPointIdentitiesException;
import com.bitdubai.fermat_dap_api.layer.dap_identity.redeem_point.interfaces.RedeemPointIdentity;
import com.bitdubai.fermat_dap_api.layer.dap_identity.redeem_point.interfaces.RedeemPointIdentityManager;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_redeem_point.RedeemPointSettings;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_user.interfaces.AssetUserWalletSubAppModuleManager;
import com.bitdubai.fermat_dap_api.layer.dap_sub_app_module.redeem_point_community.interfaces.RedeemPointCommunitySubAppModuleManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO explain here the main functionality of the plug-in.
 * <p/>
 * Created by Nerio on 13/10/15.
 */
@NeededIndirectPluginReferences(indirectReferences = {
        @NeededPluginReference(platform = Platforms.CRYPTO_CURRENCY_PLATFORM, layer = Layers.TRANSACTION, plugin = Plugins.INCOMING_EXTRA_USER),
        @NeededPluginReference(platform = Platforms.CRYPTO_CURRENCY_PLATFORM, layer = Layers.TRANSACTION, plugin = Plugins.INCOMING_INTRA_USER)
})
public class RedeemPointCommunitySubAppModulePluginRoot extends AbstractPlugin implements
        RedeemPointCommunitySubAppModuleManager {

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.ASSET_USER)
    ActorAssetUserManager actorAssetUserManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.WALLET_MODULE, plugin = Plugins.ASSET_USER)
    AssetUserWalletSubAppModuleManager assetUserWalletSubAppModuleManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.REDEEM_POINT)
    ActorAssetRedeemPointManager actorAssetRedeemPointManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.REDEEM_POINT)
    AssetRedeemPointActorNetworkServiceManager assetRedeemPointActorNetworkServiceManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    ErrorManager errorManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.IDENTITY, plugin = Plugins.REDEEM_POINT)
    RedeemPointIdentityManager redeemPointIdentityManager;

    BlockchainNetworkType blockchainNetworkType;
    private SettingsManager<RedeemPointSettings> settingsManager;

    public RedeemPointCommunitySubAppModulePluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @Override
    public List<RedeemPointActorRecord> getAllActorAssetRedeemPointRegistered() throws CantGetAssetRedeemPointActorsException {
        List<ActorAssetRedeemPoint> list = null;
        List<RedeemPointActorRecord> actorAssetRedeemPoints = null;

        try {
            list = assetRedeemPointActorNetworkServiceManager.getListActorAssetRedeemPointRegistered();
            actorAssetRedeemPointManager.createActorAssetRedeemPointRegisterInNetworkService(list);
        } catch (CantRequestListActorAssetRedeemPointRegisteredException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantCreateActorRedeemPointException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

        if (list != null) {
            actorAssetRedeemPoints = new ArrayList<>();

            try {
                for (ActorAssetRedeemPoint actorAssetRedeemPoint : actorAssetRedeemPointManager.getAllAssetRedeemPointActorInTableRegistered()) {
                    RedeemPointActorRecord redeemPointActorRecord = (RedeemPointActorRecord) actorAssetRedeemPoint;
                    actorAssetRedeemPoints.add(redeemPointActorRecord);
                }
            } catch (CantGetAssetRedeemPointActorsException e) {
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                e.printStackTrace();
            }
        }
        return actorAssetRedeemPoints;
    }

    @Override
    public DAPConnectionState getActorRedeemRegisteredDAPConnectionState(String actorAssetPublicKey) throws CantGetAssetRedeemPointActorsException {
        blockchainNetworkType = assetUserWalletSubAppModuleManager.getSelectedNetwork();

        return actorAssetRedeemPointManager.getActorRedeemPointRegisteredDAPConnectionState(actorAssetPublicKey, blockchainNetworkType);
    }

    @Override
    public void connectToActorAssetRedeemPoint(ActorAssetUser requester, List<ActorAssetRedeemPoint> actorAssetRedeemPoint) throws CantConnectToActorAssetUserException {
        blockchainNetworkType = assetUserWalletSubAppModuleManager.getSelectedNetwork();

        ActorAssetUser actorAssetUser;
        //TODO Actor Asset User de BD Local
        try {
            actorAssetUser = actorAssetUserManager.getActorAssetUser();

            if (actorAssetUser != null)
                actorAssetUserManager.connectToActorAssetRedeemPoint(actorAssetUser, actorAssetRedeemPoint);
            else
                throw new CantConnectToActorAssetUserException(CantConnectToActorAssetUserException.DEFAULT_MESSAGE, null, "THERE WAS AN ERROR GET ACTOR ASSET USER.", null);

        } catch (CantGetAssetUserActorsException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConnectToActorAssetUserException(CantConnectToActorAssetUserException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR GET ACTOR ASSET USER.", null);
        } catch (CantConnectToActorAssetRedeemPointException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConnectToActorAssetUserException(CantConnectToActorAssetUserException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR CONNECTING TO REDEEM POINT.", null);
        }
    }

    @Override
    public RedeemPointIdentity getActiveAssetRedeemPointIdentity() throws CantGetIdentityRedeemPointException {
        try {
            return redeemPointIdentityManager.getIdentityAssetRedeemPoint();
        } catch (CantGetRedeemPointIdentitiesException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetIdentityRedeemPointException(e);
        }
    }

    @Override
    public ActorAssetRedeemPoint getActorRedeemPoint(String actorPublicKey) throws CantGetAssetRedeemPointActorsException, CantAssetRedeemPointActorNotFoundException {
        try {
            return actorAssetRedeemPointManager.getActorByPublicKey(actorPublicKey);
        } catch (CantGetAssetRedeemPointActorsException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetAssetRedeemPointActorsException(CantGetAssetRedeemPointActorsException.DEFAULT_MESSAGE,e,"THERE WAS AN ERROR GETTING ACTOR REDEEM POINT",null);
        } catch (CantAssetRedeemPointActorNotFoundException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantAssetRedeemPointActorNotFoundException(CantAssetRedeemPointActorNotFoundException.DEFAULT_MESSAGE,e,"ACTOR REDEEM POINT NOT FOUND",null);
        }
    }

    @Override
    public void disconnectToActorAssetRedeemPoint(ActorAssetUser requester, ActorAssetRedeemPoint redeemPoint) throws CantAssetRedeemPointActorNotFoundException, CantUpdateRedeemPointException {
        try {
            actorAssetRedeemPointManager.disconnectToActorAssetRedeemPoint(redeemPoint);
        } catch (CantUpdateRedeemPointException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantUpdateRedeemPointException(CantUpdateRedeemPointException.DEFAULT_MESSAGE,e,"ACTOR REDEEM CANT BE UPDATED",null);
        } catch (RedeemPointNotFoundException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantAssetRedeemPointActorNotFoundException(CantAssetRedeemPointActorNotFoundException.DEFAULT_MESSAGE,e,"ACTOR REDEEM POINT NOT FOUND",null);
        }
    }

    @Override
    public SettingsManager<RedeemPointSettings> getSettingsManager() {
        if (this.settingsManager != null)
            return this.settingsManager;

        this.settingsManager = new SettingsManager<>(
                pluginFileSystem,
                pluginId
        );

        return this.settingsManager;
    }

    @Override
    public ActiveActorIdentityInformation getSelectedActorIdentity() throws CantGetSelectedActorIdentityException, ActorIdentityNotSelectedException {
        return redeemPointIdentityManager.getSelectedActorIdentity();
    }

    @Override
    public void createIdentity(String name, String phrase, byte[] profile_img) throws Exception {

    }


    @Override
    public void setAppPublicKey(String publicKey) {

    }

    @Override
    public int[] getMenuNotifications() {
        return new int[0];
    }
}
