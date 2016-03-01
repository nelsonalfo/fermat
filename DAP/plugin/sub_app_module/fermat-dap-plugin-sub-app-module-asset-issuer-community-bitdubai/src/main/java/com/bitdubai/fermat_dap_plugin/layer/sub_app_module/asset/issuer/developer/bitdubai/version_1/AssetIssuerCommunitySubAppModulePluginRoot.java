package com.bitdubai.fermat_dap_plugin.layer.sub_app_module.asset.issuer.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededIndirectPluginReferences;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
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
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityAssetIssuerException;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.AssetIssuerActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantAssetIssuerActorNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantCreateActorAssetIssuerException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantGetAssetIssuerActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantUpdateActorAssetIssuerException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuerManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantConnectToActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantConnectToActorAssetRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantGetAssetRedeemPointActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPointManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_issuer.exceptions.CantRequestListActorAssetIssuerRegisteredException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_issuer.interfaces.AssetIssuerActorNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_user.exceptions.CantAskConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_user.exceptions.CantCancelConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_user.exceptions.CantDenyConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantAcceptActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantGetActorAssetNotificationException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantGetActorAssetWaitingException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantRequestAlreadySendActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_identity.asset_issuer.exceptions.CantGetAssetIssuerIdentitiesException;
import com.bitdubai.fermat_dap_api.layer.dap_identity.asset_issuer.interfaces.IdentityAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_identity.asset_issuer.interfaces.IdentityAssetIssuerManager;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_issuer.AssetIssuerSettings;
import com.bitdubai.fermat_dap_api.layer.dap_sub_app_module.asset_issuer_community.interfaces.AssetIssuerCommunitySubAppModuleManager;
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
public class AssetIssuerCommunitySubAppModulePluginRoot extends AbstractPlugin implements
        AssetIssuerCommunitySubAppModuleManager {

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.ASSET_ISSUER)
    ActorAssetIssuerManager actorAssetIssuerManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.REDEEM_POINT)
    ActorAssetRedeemPointManager actorAssetRedeemPointManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.ASSET_ISSUER)
    AssetIssuerActorNetworkServiceManager assetIssuerActorNetworkServiceManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    ErrorManager errorManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.IDENTITY, plugin = Plugins.ASSET_ISSUER)
    IdentityAssetIssuerManager identityAssetIssuerManager;

    private SettingsManager<AssetIssuerSettings> settingsManager;

    private String appPublicKey;

    public AssetIssuerCommunitySubAppModulePluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @Override
    public List<AssetIssuerActorRecord> getAllActorAssetIssuerRegistered() throws CantGetAssetIssuerActorsException {
        List<ActorAssetIssuer> list = null;
        List<AssetIssuerActorRecord> assetIssuerActorRecords = null;

        try {
            list = assetIssuerActorNetworkServiceManager.getListActorAssetIssuerRegistered();
            actorAssetIssuerManager.createActorAssetIssuerRegisterInNetworkService(list);
        } catch (CantRequestListActorAssetIssuerRegisteredException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantCreateActorAssetIssuerException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

        if (list != null) {
            assetIssuerActorRecords = new ArrayList<>();

            try {
                for (ActorAssetIssuer actorAssetIssuer : actorAssetIssuerManager.getAllAssetIssuerActorInTableRegistered()) {
                    AssetIssuerActorRecord assetIssuerActorRecord = (AssetIssuerActorRecord) actorAssetIssuer;
                    assetIssuerActorRecords.add(assetIssuerActorRecord);
                }

            } catch (CantGetAssetIssuerActorsException e) {
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                e.printStackTrace();
            }
        }
        return assetIssuerActorRecords;
    }

    @Override
    public DAPConnectionState getActorIssuerRegisteredDAPConnectionState(String actorAssetPublicKey) throws CantGetAssetIssuerActorsException {
        return actorAssetIssuerManager.getActorIssuerRegisteredDAPConnectionState(actorAssetPublicKey);
    }

    @Override
    public ActorAssetIssuer getActorIssuer(String actorPublicKey) throws CantGetAssetIssuerActorsException, CantAssetIssuerActorNotFoundException {
        try {
            return actorAssetIssuerManager.getActorByPublicKey(actorPublicKey);
        } catch (CantGetAssetIssuerActorsException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetAssetIssuerActorsException(CantGetAssetIssuerActorsException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR GETTING ACTOR ISSUER", null);
        } catch (CantAssetIssuerActorNotFoundException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantAssetIssuerActorNotFoundException(CantAssetIssuerActorNotFoundException.DEFAULT_MESSAGE, e, "ACTOR ISSUER NOT FOUND", null);
        }
    }

    @Override
    public void connectToActorAssetIssuer(ActorAssetRedeemPoint requester, List<ActorAssetIssuer> actorAssetIssuers) throws CantConnectToActorAssetRedeemPointException {

        ActorAssetRedeemPoint actorAssetRedeemPoint;
        //TODO Actor Asset Redeem Point de BD Local
        try {
            actorAssetRedeemPoint = actorAssetRedeemPointManager.getActorAssetRedeemPoint();

            if (actorAssetRedeemPoint != null) {
                actorAssetRedeemPointManager.sendMessage(actorAssetRedeemPoint, actorAssetIssuers);
                for (ActorAssetIssuer actorAssetIssuer : actorAssetIssuers) {
                    actorAssetIssuerManager.updateIssuerRegisteredDAPConnectionState(actorAssetIssuer.getActorPublicKey(), DAPConnectionState.CONNECTING);
                }
            } else
                throw new CantConnectToActorAssetRedeemPointException(CantConnectToActorAssetRedeemPointException.DEFAULT_MESSAGE, null, "THERE WAS AN ERROR GET ACTOR ASSET REDEEM POINT.", null);

        } catch (CantGetAssetRedeemPointActorsException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConnectToActorAssetRedeemPointException(CantConnectToActorAssetRedeemPointException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR GET ACTOR ASSET REDEEM POINT.", null);
        } catch (CantConnectToActorAssetUserException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConnectToActorAssetRedeemPointException(CantConnectToActorAssetRedeemPointException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR CONNECTING TO ASSET ISSUERS.", null);
        } catch (CantUpdateActorAssetIssuerException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConnectToActorAssetRedeemPointException(CantConnectToActorAssetRedeemPointException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR UPDATING TO ASSET ISSUERS.", null);
        }
    }

    @Override
    public IdentityAssetIssuer getActiveAssetIssuerIdentity() throws CantGetIdentityAssetIssuerException {
        try {
            return identityAssetIssuerManager.getIdentityAssetIssuer();
        } catch (CantGetAssetIssuerIdentitiesException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetIdentityAssetIssuerException(e);
        }
    }

    @Override
    public void askActorAssetIssuerForConnection(List<ActorAssetIssuer> actorAssetIssuers) throws CantAskConnectionActorAssetException, CantRequestAlreadySendActorAssetException {

    }

    @Override
    public void acceptActorAssetIssuer(String actorAssetIssuerPublicKey, String actorAssetIssuerAddPublicKey) throws CantAcceptActorAssetUserException {
        try {
            /**
             *Call Actor Intra User to accept request connection
             */
            this.actorAssetIssuerManager.acceptActorAssetIssuer(actorAssetIssuerPublicKey, actorAssetIssuerAddPublicKey);

            /**
             *Call Network Service User to accept request connection
             */
//            this.assetIssuerActorNetworkServiceManager.acceptConnectionActorAsset(actorAssetIssuerPublicKey, actorAssetIssuerAddPublicKey);

        } catch (CantAcceptActorAssetUserException e) {
            throw new CantAcceptActorAssetUserException("CAN'T ACCEPT ACTOR ASSET ISSUER CONNECTION - KEY " + actorAssetIssuerAddPublicKey, e, "", "");
        } catch (Exception e) {
            throw new CantAcceptActorAssetUserException("CAN'T ACCEPT ACTOR ASSET ISSUER CONNECTION - KEY " + actorAssetIssuerAddPublicKey, FermatException.wrapException(e), "", "unknown exception");
        }
    }

    @Override
    public void denyConnectionActorAssetIssuer(String actorAssetIssuerLoggedPublicKey, String actorAssetIssuerToRejectPublicKey) throws CantDenyConnectionActorAssetException {
        try {
            /**
             *Call Actor Intra User to denied request connection
             */
            this.actorAssetIssuerManager.denyConnectionActorAssetIssuer(actorAssetIssuerLoggedPublicKey, actorAssetIssuerToRejectPublicKey);

            /**
             *Call Network Service User to denied request connection
             */
//            this.assetIssuerActorNetworkServiceManager.denyConnectionActorAsset(actorAssetIssuerLoggedPublicKey, actorAssetIssuerToRejectPublicKey);

        } catch (CantDenyConnectionActorAssetException e) {
            throw new CantDenyConnectionActorAssetException("CAN'T DENY ACTOR ASSET ISSUER CONNECTION - KEY:" + actorAssetIssuerToRejectPublicKey, e, "", "");
        } catch (Exception e) {
            throw new CantDenyConnectionActorAssetException("CAN'T DENY ACTOR ASSET ISSUER CONNECTION - KEY:" + actorAssetIssuerToRejectPublicKey, FermatException.wrapException(e), "", "unknown exception");
        }
    }

    @Override
    public void cancelActorAssetIssuer(String actorAssetIssuerLoggedPublicKey, String actorAssetIssuerToCancelPublicKey) throws CantCancelConnectionActorAssetException {
        try {
            /**
             *Call Actor Intra User to cancel request connection
             */
            this.actorAssetIssuerManager.cancelActorAssetIssuer(actorAssetIssuerLoggedPublicKey, actorAssetIssuerToCancelPublicKey);

            /**
             *Call Network Service Intra User to cancel request connection
             */
//            this.assetIssuerActorNetworkServiceManager.cancelConnectionActorAsset(actorAssetIssuerLoggedPublicKey, actorAssetIssuerToCancelPublicKey);
        } catch (CantCancelConnectionActorAssetException e) {
            throw new CantCancelConnectionActorAssetException("CAN'T CANCEL ACTOR ASSET ISSUER CONNECTION - KEY:" + actorAssetIssuerToCancelPublicKey, e, "", "");
        } catch (Exception e) {
            throw new CantCancelConnectionActorAssetException("CAN'T CANCEL ACTOR ASSET ISSUER CONNECTION - KEY:" + actorAssetIssuerToCancelPublicKey, FermatException.wrapException(e), "", "unknown exception");
        }
    }

    @Override
    public List<ActorAssetIssuer> getWaitingYourConnectionActorAssetIssuer(String actorAssetUserLoggedPublicKey, int max, int offset) throws CantGetActorAssetWaitingException {
//        List<ActorAssetUser> intraUserList = new ArrayList<ActorAssetUser>();

        try {

//            for (ActorAssetUser intraUserActor : actorsList) {
//                intraUserList.add(new IntraUserModuleInformation(intraUserActor.getName(),"",intraUserActor.getPublicKey(),intraUserActor.getProfileImage(),intraUserActor.getContactState()));
//            }

            return this.actorAssetIssuerManager.getWaitingYourConnectionActorAssetIssuer(actorAssetUserLoggedPublicKey, max, offset);
        } catch (CantGetActorAssetWaitingException e) {
            throw new CantGetActorAssetWaitingException("CAN'T GET ACTOR ASSET ISSUER WAITING YOUR ACCEPTANCE", e, "", "");
        } catch (Exception e) {
            throw new CantGetActorAssetWaitingException("CAN'T GET ACTOR ASSET ISSUER WAITING YOUR ACCEPTANCE", FermatException.wrapException(e), "", "unknown exception");
        }
    }

    @Override
    public List<ActorAssetIssuer> getWaitingTheirConnectionActorAssetIssuer(String actorAssetUserLoggedPublicKey, int max, int offset) throws CantGetActorAssetWaitingException {
        try {
//            List<ActorAssetUser> intraUserList = new ArrayList<ActorAssetUser>();

            //            for (ActorAssetUser intraUserActor : actorsList) {
//                intraUserList.add(new IntraUserModuleInformation(intraUserActor.getName(),"",intraUserActor.getPublicKey(),intraUserActor.getProfileImage(),intraUserActor.getContactState()));
//            }
            return this.actorAssetIssuerManager.getWaitingTheirConnectionActorAssetIssuer(actorAssetUserLoggedPublicKey, max, offset);
        } catch (CantGetActorAssetWaitingException e) {
            throw new CantGetActorAssetWaitingException("CAN'T GET ACTOR ASSET ISSUER WAITING THEIR ACCEPTANCE", e, "", "Error on ACTOR ASSET ISSUER Manager");
        } catch (Exception e) {
            throw new CantGetActorAssetWaitingException("CAN'T GET ACTOR ASSET ISSUER WAITING THEIR ACCEPTANCE", FermatException.wrapException(e), "", "unknown exception");
        }
    }
    @Override
    public ActorAssetIssuer getLastNotification(String actorAssetIssuerPublicKey) throws CantGetActorAssetNotificationException {
        try {
            return this.actorAssetIssuerManager.getLastNotificationActorAssetIssuer(actorAssetIssuerPublicKey);
        } catch (CantGetActorAssetNotificationException e) {
            throw new CantGetActorAssetNotificationException("CAN'T GET ACTOR ASSET ISSUER LAST NOTIFICATION", e, "", "Error on ACTOR ISSUER MANAGER");
        }
    }

    @Override
    public int getWaitingYourConnectionActorAssetIssuerCount() {
        //TODO: falta que este metodo que devuelva la cantidad de request de conexion que tenes
        try {

            if (getActiveAssetIssuerIdentity() != null) {
                return getWaitingYourConnectionActorAssetIssuer(getActiveAssetIssuerIdentity().getPublicKey(), 100, 0).size();
            }

        } catch (CantGetActorAssetWaitingException e) {
            e.printStackTrace();
        } catch (CantGetIdentityAssetIssuerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public SettingsManager<AssetIssuerSettings> getSettingsManager() {
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
        return identityAssetIssuerManager.getSelectedActorIdentity();
    }

    @Override
    public void createIdentity(String name, String phrase, byte[] profile_img) throws Exception {
        identityAssetIssuerManager.createNewIdentityAssetIssuer(name, profile_img);
    }

    @Override
    public void setAppPublicKey(String publicKey) {
        this.appPublicKey = publicKey;
    }

    @Override
    public int[] getMenuNotifications() {
        int[] notifications = new int[4];
        try {
            if (getSelectedActorIdentity() != null)
                notifications[2] = actorAssetIssuerManager.getWaitingYourConnectionActorAssetIssuer(getSelectedActorIdentity().getPublicKey(), 99, 0).size();
            else
                notifications[2] = 0;
        } catch (CantGetActorAssetWaitingException | CantGetSelectedActorIdentityException | ActorIdentityNotSelectedException e) {
            e.printStackTrace();
        }
        return notifications;
    }
}
