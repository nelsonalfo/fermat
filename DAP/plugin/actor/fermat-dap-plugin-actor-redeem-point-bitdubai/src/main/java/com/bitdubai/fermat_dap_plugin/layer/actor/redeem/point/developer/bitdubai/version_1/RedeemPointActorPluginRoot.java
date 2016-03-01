package com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.BroadcasterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.ExtendedPublicKey;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.exceptions.CantInitializeWatchOnlyVaultException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.interfaces.WatchOnlyVaultManager;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.enums.CryptoAddressDealers;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.enums.RequestAction;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions.CantConfirmAddressExchangeRequestException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions.CantListPendingCryptoAddressRequestsException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions.PendingRequestNotFoundException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.interfaces.CryptoAddressRequest;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.interfaces.CryptoAddressesManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.DAPConstants;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPConnectionState;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPPublicKeys;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventType;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.DAPMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.content_message.AssetExtendedPublicKeyContentMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantSendMessageException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantUpdateActorAssetIssuerException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuerManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantConnectToActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.RedeemPointActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantAssetRedeemPointActorNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantCreateActorRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantGetAssetRedeemPointActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPointManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.Address;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_issuer.interfaces.AssetIssuerActorNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.exceptions.CantRegisterActorAssetRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.interfaces.AssetRedeemPointActorNetworkServiceManager;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.agent.ActorAssetRedeemPointMonitorAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.developerUtils.RedeemPointActorDeveloperDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.event_handlers.ActorAssetRedeemPointCompleteRegistrationNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.event_handlers.CryptoAddressRequestedEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.event_handlers.NewReceiveMessageEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantAddPendingRedeemPointException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantGetRedeemPointsListException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantHandleCryptoAddressReceivedActionException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantHandleCryptoAddressesNewsEventException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantInitializeRedeemPointActorDatabaseException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantUpdateRedeemPointException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.RedeemPointNotFoundException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.structure.RedeemPointActorAddress;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.structure.RedeemPointActorDao;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;
import com.bitdubai.fermat_pip_api.layer.user.device_user.exceptions.CantGetLoggedInDeviceUserException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Nerio on 09/09/15.
 */
public class RedeemPointActorPluginRoot extends AbstractPlugin implements
        ActorAssetRedeemPointManager,
        DatabaseManagerForDevelopers {

    public RedeemPointActorPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    private ErrorManager errorManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    private EventManager eventManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_BROADCASTER_SYSTEM)
    private Broadcaster broadcaster;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.ASSET_ISSUER)
    private AssetIssuerActorNetworkServiceManager assetIssuerActorNetworkServiceManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.REDEEM_POINT)
    private AssetRedeemPointActorNetworkServiceManager assetRedeemPointActorNetworkServiceManager;

    @NeededPluginReference(platform = Platforms.CRYPTO_CURRENCY_PLATFORM, layer = Layers.NETWORK_SERVICE, plugin = Plugins.CRYPTO_ADDRESSES)
    private CryptoAddressesManager cryptoAddressesNetworkServiceManager;

    @NeededPluginReference(platform = Platforms.BLOCKCHAINS, layer = Layers.CRYPTO_VAULT, plugin = Plugins.BITCOIN_WATCH_ONLY_VAULT)
    private WatchOnlyVaultManager watchOnlyVaultManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.ASSET_ISSUER)
    private ActorAssetIssuerManager actorAssetIssuerManager;

    private RedeemPointActorDao redeemPointActorDao;

    private ActorAssetRedeemPointMonitorAgent actorAssetRedeemPointMonitorAgent;

    List<FermatEventListener> listenersAdded = new ArrayList<>();

    @Override
    public void start() throws CantStartPluginException {
        try {
            /**
             * Created instance of AssetUserActorDao and initialize Database
             */
            this.redeemPointActorDao = new RedeemPointActorDao(this.pluginDatabaseSystem, this.pluginFileSystem, this.pluginId);

            initializeListener();

            /**
             * Agent for Search Actor Asset User REGISTERED in Actor Network Service User
             */
//            startMonitorAgent();

            this.serviceStatus = ServiceStatus.STARTED;

        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_DAP_REDEEM_POINT_ACTOR);
        }
    }

    @Override
    public void stop() {
        actorAssetRedeemPointMonitorAgent.stop();
        this.serviceStatus = ServiceStatus.STOPPED;
    }

    @Override
    public ActorAssetRedeemPoint getActorByPublicKey(String actorPublicKey) throws CantGetAssetRedeemPointActorsException,
            CantAssetRedeemPointActorNotFoundException {
        try {
            ActorAssetRedeemPoint currentRedeem = getActorAssetRedeemPoint();
            if (currentRedeem != null && currentRedeem.getActorPublicKey().equals(actorPublicKey)) {
                return currentRedeem;
            } else {
                return this.redeemPointActorDao.getActorRegisteredByPublicKey(actorPublicKey);
            }
        } catch (CantGetAssetRedeemPointActorsException e) {
            throw new CantGetAssetRedeemPointActorsException("", FermatException.wrapException(e), "Cant Get Actor Asset User from Data Base", null);
        }
    }

    @Override
    public void createActorAssetRedeemPointFactory(String assetRedeemPointActorPublicKey, String assetRedeemPointActorName, byte[] assetRedeemPointActorprofileImage,
                    String contactInformation, String countryName, String cityName) throws CantCreateActorRedeemPointException {
        try {
            ActorAssetRedeemPoint actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetRedeemPoint();

            if (actorAssetRedeemPoint == null) {

                Double locationLatitude = new Random().nextDouble();
                Double locationLongitude = new Random().nextDouble();

                RedeemPointActorRecord record = new RedeemPointActorRecord(
                        assetRedeemPointActorPublicKey,
                        assetRedeemPointActorName,
                        DAPConnectionState.REGISTERED_LOCALLY,
                        locationLatitude,
                        locationLongitude,
                        null,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        Actors.DAP_ASSET_REDEEM_POINT,
                        null,
                        assetRedeemPointActorprofileImage);

                RedeemPointActorAddress address = new RedeemPointActorAddress();
                address.setCountryName(countryName);
                address.setCityName(cityName);
                record.setAddress(address);
                record.setContactInformation(contactInformation);

//                record.setCryptoAddress(cryptoAddress);
//                record.setHoursOfOperation("08:00 am a 05:30pm");

                redeemPointActorDao.createNewRedeemPoint(record);

                actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetRedeemPoint();

                assetRedeemPointActorNetworkServiceManager.registerActorAssetRedeemPoint(actorAssetRedeemPoint);
            } else {
                Double locationLatitude = new Random().nextDouble();
                Double locationLongitude = new Random().nextDouble();

                RedeemPointActorRecord record = new RedeemPointActorRecord(
                        actorAssetRedeemPoint.getActorPublicKey(),
                        assetRedeemPointActorName,
                        actorAssetRedeemPoint.getDapConnectionState(),
                        locationLatitude,
                        locationLongitude,
                        actorAssetRedeemPoint.getCryptoAddress(),
                        actorAssetRedeemPoint.getRegistrationDate(),
                        System.currentTimeMillis(),
                        actorAssetRedeemPoint.getType(),
                        null,
                        assetRedeemPointActorprofileImage,
                        actorAssetRedeemPoint.getRegisteredIssuers());

                RedeemPointActorAddress address = new RedeemPointActorAddress();
                address.setCountryName(countryName);
                address.setCityName(cityName);
                record.setAddress(address);
                record.setContactInformation(contactInformation);

//                RedeemPointActorAddress address = new RedeemPointActorAddress();
//                address.setCountryName("Venezuela");
//                address.setProvinceName("Zulia");
//                address.setPostalCode("4019");
//                address.setCityName("Ciudad Ojeda");
//                address.setStreetName("Avenida 8");
//                address.setHouseNumber("#712");
//                record.setAddress(address);
//                record.setCryptoAddress(cryptoAddress);
//                record.setHoursOfOperation("08:00 am a 05:30pm");
//                record.setContactInformation("marsvicam@gmail.com");
                redeemPointActorDao.updateRedeemPoint(record);

                actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetRedeemPoint();

                assetRedeemPointActorNetworkServiceManager.updateActorAssetRedeemPoint(actorAssetRedeemPoint);
            }

            if (actorAssetRedeemPoint != null) {
                System.out.println("*********************Actor Asset Redeem Point************************");
                System.out.println("Actor Asset PublicKey: " + actorAssetRedeemPoint.getActorPublicKey());
                System.out.println("Actor Asset Name: " + actorAssetRedeemPoint.getName());
                System.out.println("***************************************************************");
            }
        } catch (CantAddPendingRedeemPointException e) {
            throw new CantCreateActorRedeemPointException("CAN'T ADD NEW ACTOR ASSET REDEEM POINT", e, "", "");
        } catch (CantGetAssetRedeemPointActorsException e) {
            throw new CantCreateActorRedeemPointException("CAN'T GET ACTOR ASSET REDEEM POINT", FermatException.wrapException(e), "", "");
        } catch (Exception e) {
            throw new CantCreateActorRedeemPointException("CAN'T ADD NEW ACTOR ASSET REDEEM POINT", FermatException.wrapException(e), "", "");
        }
    }

    /**
     * This method saves an already existing redeem point in the registered redeem point database,
     * usually uses when the redeem point request the issuer an extended public key, we save in
     * the issuer side this redeem point so we can retrieve its information on future uses.
     *
     * @param redeemPoint The already existing redeem point with all its information
     * @throws CantCreateActorRedeemPointException
     */
    @Override
    public void saveRegisteredActorRedeemPoint(ActorAssetRedeemPoint redeemPoint) throws CantCreateActorRedeemPointException {
        try {
            redeemPointActorDao.createNewRedeemPointRegisterInNetworkService(redeemPoint);
        } catch (CantAddPendingRedeemPointException e) {
            throw new CantCreateActorRedeemPointException();
        }
    }

    @Override
    public void createActorAssetRedeemPointRegisterInNetworkService(List<ActorAssetRedeemPoint> actorAssetRedeemPoints) throws CantCreateActorRedeemPointException {
        try {
            redeemPointActorDao.createNewAssetRedeemPointRegisterInNetworkServiceByList(actorAssetRedeemPoints);
        } catch (CantAddPendingRedeemPointException e) {
            throw new CantCreateActorRedeemPointException("CAN'T ADD NEW ACTOR ASSET REDEEM POINT REGISTERED", e, "", "");
        }
    }

    @Override
    public ActorAssetRedeemPoint getActorAssetRedeemPoint() throws CantGetAssetRedeemPointActorsException {

        ActorAssetRedeemPoint actorAssetRedeemPoint;
        try {
            actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetRedeemPoint();
        } catch (Exception e) {
            throw new CantGetAssetRedeemPointActorsException("", FermatException.wrapException(e), "There is a problem I can't identify.", null);
        }

        return actorAssetRedeemPoint;
    }

    @Override
    public DAPConnectionState getActorRedeemPointRegisteredDAPConnectionState(String actorAssetPublicKey, BlockchainNetworkType blockchainNetworkType) throws CantGetAssetRedeemPointActorsException {
        try {
            ActorAssetRedeemPoint actorAssetRedeemPoint;
//
            actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetUserRegisteredByPublicKey(actorAssetPublicKey, blockchainNetworkType);
//
            if(actorAssetRedeemPoint != null)
                return actorAssetRedeemPoint.getDapConnectionState();
            else
                return DAPConnectionState.ERROR_UNKNOWN;
        } catch (CantGetAssetRedeemPointActorsException e) {
            throw new CantGetAssetRedeemPointActorsException("CAN'T GET ACTOR REDEEM POINT STATE", e, "Error get database info", "");
        } catch (Exception e) {
            throw new CantGetAssetRedeemPointActorsException("CAN'T GET ACTOR REDEEM POINT STATE", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public List<ActorAssetRedeemPoint> getAllAssetRedeemPointActorInTableRegistered() throws CantGetAssetRedeemPointActorsException {
        List<ActorAssetRedeemPoint> list; // Asset User Actor list.
        try {
            list = this.redeemPointActorDao.getAllAssetRedeemPointActorRegistered();
        } catch (CantGetRedeemPointsListException e) {
            throw new CantGetAssetRedeemPointActorsException("CAN'T GET REDEEM POINT ACTOR REGISTERED", e, "", "");
        }

        return list;
    }

    @Override
    public List<ActorAssetRedeemPoint> getAllRedeemPointActorConnected() throws CantGetAssetRedeemPointActorsException {
        List<ActorAssetRedeemPoint> list; // Asset User Actor list.
        try {
            list = this.redeemPointActorDao.getAllAssetRedeemPointActorConnected();
        } catch (CantGetRedeemPointsListException e) {
            throw new CantGetAssetRedeemPointActorsException("CAN'T GET REDEEM POINT ACTOR CONNECTED ", e, "", "");
        }

        return list;
    }

    @Override
    public List<ActorAssetRedeemPoint> getAllRedeemPointActorConnectedForIssuer(String issuerPublicKey) throws CantGetAssetRedeemPointActorsException {
        List<ActorAssetRedeemPoint> list; // Asset User Actor list.
        try {
            list = this.redeemPointActorDao.getRedeemPointsConnectedForIssuer(issuerPublicKey);
        } catch (CantGetRedeemPointsListException e) {
            throw new CantGetAssetRedeemPointActorsException("CAN'T GET REDEEM POINT ACTOR CONNECTED ", e, "", "");
        }

        return list;
    }


    public void registerActorInActorNetworkService() throws CantRegisterActorAssetRedeemPointException {
        try {
            /*
             * Send the Actor Asset Redeem Point Local for Register in Actor Network Service
             */
            ActorAssetRedeemPoint actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetRedeemPoint();

            if (actorAssetRedeemPoint != null)
                assetRedeemPointActorNetworkServiceManager.registerActorAssetRedeemPoint(actorAssetRedeemPoint);

        } catch (CantRegisterActorAssetRedeemPointException e) {
            throw new CantRegisterActorAssetRedeemPointException("CAN'T Register Actor Redeem Poiint in Actor Network Service", e, "", "");
        } catch (CantGetAssetRedeemPointActorsException e) {
            throw new CantRegisterActorAssetRedeemPointException("CAN'T GET ACTOR Redeem Point", e, "", "");
        }
    }

    @Override
    public void sendMessage(ActorAssetRedeemPoint requester, List<ActorAssetIssuer> actorAssetIssuers) throws CantConnectToActorAssetUserException {
        for (ActorAssetIssuer actorAssetIssuer : actorAssetIssuers) {
            try {
                AssetExtendedPublicKeyContentMessage assetExtendedPublickKeyContentMessage = new AssetExtendedPublicKeyContentMessage();
                DAPMessage dapMessage = new DAPMessage(
                        assetExtendedPublickKeyContentMessage,
                        requester,
                        actorAssetIssuer);
                assetIssuerActorNetworkServiceManager.sendMessage(dapMessage);
//                this.redeemPointActorDao.updateRedeemPointRegisteredDAPConnectionState(actorAssetRedeemPoint.getActorPublicKey(), DAPConnectionState.CONNECTING);
            } catch (CantSendMessageException e) {
                throw new CantConnectToActorAssetUserException("CAN'T SEND MESSAGE TO ACTOR ASSET ISSUER", e, "", "");
            } catch (CantSetObjectException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disconnectToActorAssetRedeemPoint(ActorAssetRedeemPoint redeemPoint) throws com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantUpdateRedeemPointException, com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.RedeemPointNotFoundException {
        try {
            this.redeemPointActorDao.deleteCryptoCurrencyFromRedeemPointRegistered(redeemPoint);
        } catch (CantUpdateRedeemPointException e) {
            throw new com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantUpdateRedeemPointException("CAN'T UPDATE REDEEM POINT", e, "", "");
        } catch (RedeemPointNotFoundException e) {
            throw new com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.RedeemPointNotFoundException("REDEEM POINT NOT FOUND", e, "", "");
        }
    }

    @Override
    public void updateRedeemPointDAPConnectionStateActorNetworService(String actorPublicKey, DAPConnectionState state) throws com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantUpdateRedeemPointException{
        try {
            this.redeemPointActorDao.updateRedeemPointRegisteredDAPConnectionState(actorPublicKey,state);
        } catch (CantUpdateRedeemPointException e) {
            throw new com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantUpdateRedeemPointException("CAN'T UPDATE REDEEM POINT", e, "", "");
        }
    }

    public void handleCompleteActorAssetRedeemPointRegistrationNotificationEvent(ActorAssetRedeemPoint actorAssetRedeemPoint) {
        System.out.println("***************************************************************");
        System.out.println("Actor Asset Redeem Point se Registro " + actorAssetRedeemPoint.getName());
        try {
            //TODO Cambiar luego por la publicKey Linked proveniente de Identity
            this.redeemPointActorDao.updateRedeemPointDAPConnectionState(actorAssetRedeemPoint.getActorPublicKey(),
                    DAPConnectionState.CONNECTED_ONLINE);
        } catch (CantUpdateRedeemPointException e) {
            e.printStackTrace();
        }
        System.out.println("***************************************************************");
    }

    public void handleCryptoAddressesNewsEvent() throws CantHandleCryptoAddressesNewsEventException {
        final List<CryptoAddressRequest> list;

        try {
            list = cryptoAddressesNetworkServiceManager.listAllPendingRequests();

            System.out.println("----------------------------\n" +
                    "Actor Redeem Point: handleCryptoAddressesNewsEvent " + list.size()
                    + "\n-------------------------------------------------");
            for (final CryptoAddressRequest request : list) {

                if (request.getCryptoAddressDealer() == CryptoAddressDealers.DAP_WATCH_ONLY || request.getCryptoAddressDealer() == CryptoAddressDealers.DAP_ASSET) {

                    if (request.getAction().equals(RequestAction.ACCEPT) || request.getAction().equals(RequestAction.NONE) || request.getAction().equals(RequestAction.RECEIVED))
                        this.handleCryptoAddressReceivedEvent(request);
                    try {
                        cryptoAddressesNetworkServiceManager.markReceivedRequest(request.getRequestId());
                    } catch (CantConfirmAddressExchangeRequestException e) {
                        throw new CantHandleCryptoAddressesNewsEventException(e, "Error marking request as received.", null);
                    }

//                if (request.getAction().equals(RequestAction.DENY))
//                    this.handleCryptoAddressDeniedEvent(request);
                }
            }
        } catch (CantListPendingCryptoAddressRequestsException |
//                CantHandleCryptoAddressDeniedActionException |
                CantHandleCryptoAddressReceivedActionException e) {

            throw new CantHandleCryptoAddressesNewsEventException(e, "", "Error handling Crypto Addresses News Event.");
        }
    }

    public void handleCryptoAddressReceivedEvent(final CryptoAddressRequest request) throws CantHandleCryptoAddressReceivedActionException {

        try {
            if (request.getCryptoAddress() != null) {
                System.out.println("*****Actor Redeem Point Recibiendo Crypto Localmente*****");

                this.redeemPointActorDao.updateAssetRedeemPointPConnectionStateCryptoAddress(request.getIdentityPublicKeyResponding(), DAPConnectionState.CONNECTED_ONLINE, request.getCryptoAddress());

                List<ActorAssetRedeemPoint> actorAssetRedeemPoints = this.redeemPointActorDao.getAssetRedeemPointRegistered(request.getIdentityPublicKeyResponding());

                if (!actorAssetRedeemPoints.isEmpty()) {
                    for (ActorAssetRedeemPoint ActorAssetRedeemPoint1 : actorAssetRedeemPoints) {
                        System.out.println("Actor Redeem Point: " + ActorAssetRedeemPoint1.getActorPublicKey());
                        System.out.println("Actor Redeem Point: " + ActorAssetRedeemPoint1.getName());
                        if (ActorAssetRedeemPoint1.getCryptoAddress() != null) {
                            System.out.println("Actor Redeem Point: " + ActorAssetRedeemPoint1.getCryptoAddress().getAddress());
                            System.out.println("Actor Redeem Point: " + ActorAssetRedeemPoint1.getCryptoAddress().getCryptoCurrency());
                            System.out.println("Actor Redeem Point: " + ActorAssetRedeemPoint1.getDapConnectionState());
                        } else {
                            System.out.println("Actor Redeem Point FALLO Recepcion CryptoAddress: " + ActorAssetRedeemPoint1.getName());
                        }
                    }
                } else {
                    System.out.println("Actor Redeem Point NO se Encontro PublicKey: " + request.getIdentityPublicKeyResponding());
                    System.out.println("Actor Redeem Point NO se Encontro: " + request.getIdentityTypeResponding());
                }

                cryptoAddressesNetworkServiceManager.confirmAddressExchangeRequest(request.getRequestId());
            }
        } catch (PendingRequestNotFoundException e) {
            e.printStackTrace();
        } catch (CantConfirmAddressExchangeRequestException e) {
            e.printStackTrace();
        } catch (CantGetAssetRedeemPointActorsException e) {
            e.printStackTrace();
        } catch (CantUpdateRedeemPointException e) {
            e.printStackTrace();
        }
    }

    public void handleNewReceiveMessageActorNotificationEvent(DAPMessage dapMessage) {
        ActorAssetIssuer dapActorSender = (ActorAssetIssuer) dapMessage.getActorSender();
        System.out.println("*****Actor Asset Redeem Point Recibe*****");
        System.out.println("Actor Asset Redeem Point Sender name: " + dapActorSender.getName());
        System.out.println("Actor Asset Redeem Point message: " + dapMessage.getMessageContent().messageType());
        System.out.println("***************************************************************");

        /**
         * we will extract the ExtendedPublicKey from the message
         */
        ExtendedPublicKey extendedPublicKey = null;
        try {
            AssetExtendedPublicKeyContentMessage assetExtendedPublickKeyContentMessage = (AssetExtendedPublicKeyContentMessage) dapMessage.getMessageContent();
            extendedPublicKey = assetExtendedPublickKeyContentMessage.getExtendedPublicKey();

        } catch (Exception e) {
            //handle this. I might have a Class Cast exception
        }

        if (extendedPublicKey == null) {
            System.out.println("*** Actor Asset Redeem Point  *** The extended public Key received by " + dapActorSender.getName() + " is null.");
        } else {
            /**
             * I will start the Bitcoin Watch only Vault on the redeem Point.
             */

            try {
                watchOnlyVaultManager.initialize(extendedPublicKey);
                redeemPointActorDao.newExtendedPublicKeyRegistered(getActorAssetRedeemPoint().getActorPublicKey(), dapActorSender.getActorPublicKey());
                assetRedeemPointActorNetworkServiceManager.updateActorAssetRedeemPoint(redeemPointActorDao.getActorAssetRedeemPoint());
                actorAssetIssuerManager.updateExtendedPublicKey(dapActorSender.getActorPublicKey(), extendedPublicKey.toString());
                actorAssetIssuerManager.updateIssuerRegisteredDAPConnectionState(dapActorSender.getActorPublicKey(), DAPConnectionState.CONNECTED_ONLINE);
            } catch (CantInitializeWatchOnlyVaultException | CantInsertRecordException | CantGetAssetRedeemPointActorsException | CantUpdateActorAssetIssuerException | CantRegisterActorAssetRedeemPointException e) {
                //handle this.
                e.printStackTrace();
            }
            broadcaster.publish(BroadcasterType.UPDATE_VIEW, DAPConstants.DAP_UPDATE_VIEW_ANDROID);
            broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, DAPPublicKeys.DAP_COMMUNITY_ISSUER.getCode(), "EXTENDED-RECEIVE_" + dapActorSender.getName());

        }
    }

    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        RedeemPointActorDeveloperDatabaseFactory dbFactory = new RedeemPointActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return dbFactory.getDatabaseList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        RedeemPointActorDeveloperDatabaseFactory dbFactory = new RedeemPointActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return dbFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        try {
            RedeemPointActorDeveloperDatabaseFactory dbFactory = new RedeemPointActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
            dbFactory.initializeDatabase();
            return dbFactory.getDatabaseTableContent(developerObjectFactory, developerDatabaseTable);
        } catch (CantInitializeRedeemPointActorDatabaseException e) {
            /**
             * The database exists but cannot be open. I can not handle this situation.
             */
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (Exception e) {
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
        // If we are here the database could not be opened, so we return an empty list
        return Collections.EMPTY_LIST;
    }

    /**
     * Private methods
     */
    private void startMonitorAgent() throws CantGetLoggedInDeviceUserException, CantStartAgentException {
        if (this.actorAssetRedeemPointMonitorAgent == null) {
//            String userPublicKey = this.deviceUserManager.getLoggedInDeviceUser().getPublicKey();
            this.actorAssetRedeemPointMonitorAgent = new ActorAssetRedeemPointMonitorAgent(this.eventManager,
                    this.pluginDatabaseSystem,
                    this.errorManager,
                    this.pluginId,
                    this.assetRedeemPointActorNetworkServiceManager,
                    this.redeemPointActorDao,
                    this);
//            this.assetUserActorMonitorAgent.setLogManager(this.logManager);
            this.actorAssetRedeemPointMonitorAgent.start();
        } else {
            this.actorAssetRedeemPointMonitorAgent.start();
        }
    }

    private void initializeListener() {
        /**
         * I will initialize the handling of com.bitdubai.platform events.
         */
        FermatEventListener fermatEventListener;
        fermatEventListener = eventManager.getNewListener(EventType.COMPLETE_ASSET_REDEEM_POINT_REGISTRATION_NOTIFICATION);
        fermatEventListener.setEventHandler(new ActorAssetRedeemPointCompleteRegistrationNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        fermatEventListener = eventManager.getNewListener(EventType.NEW_RECEIVE_MESSAGE_ACTOR);
        fermatEventListener.setEventHandler(new NewReceiveMessageEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        fermatEventListener = eventManager.getNewListener(com.bitdubai.fermat_ccp_api.all_definition.enums.EventType.CRYPTO_ADDRESSES_NEWS);
        fermatEventListener.setEventHandler(new CryptoAddressRequestedEventHandler(this, cryptoAddressesNetworkServiceManager));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);
    }
}
