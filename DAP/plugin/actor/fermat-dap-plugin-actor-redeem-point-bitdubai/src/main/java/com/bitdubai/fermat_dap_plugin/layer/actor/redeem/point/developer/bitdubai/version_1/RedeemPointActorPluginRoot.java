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
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.enums.CryptoAddressDealers;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.enums.RequestAction;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions.CantConfirmAddressExchangeRequestException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions.CantListPendingCryptoAddressRequestsException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions.PendingRequestNotFoundException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.interfaces.CryptoAddressRequest;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.interfaces.CryptoAddressesManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPConnectionState;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPMessageType;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventType;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.DAPMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.content_message.AssetExtendedPublickKeyContentMessage;
import com.bitdubai.fermat_dap_api.layer.dap_actor.DAPActor;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantConnectToActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.RedeemPointActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantAssetRedeemPointActorNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantCreateActorRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantGetAssetRedeemPointActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPointManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_issuer.exceptions.CantSendMessageException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_issuer.interfaces.AssetIssuerActorNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.exceptions.CantRegisterActorAssetRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.interfaces.AssetRedeemPointActorNetworkServiceManager;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.agent.ActorAssetRedeemPointMonitorAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.developerUtils.RedeemPointActorDeveloperDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.event_handlers.ActorAssetRedeemPointCompleteRegistrationNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.event_handlers.CryptoAddressRequestedEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.event_handlers.NewReceiveMessageActorRedeemPointNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantAddPendingRedeemPointException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantGetRedeemPointsListException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantHandleCryptoAddressReceivedActionException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantHandleCryptoAddressesNewsEventException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantInitializeRedeemPointActorDatabaseException;
import com.bitdubai.fermat_dap_plugin.layer.actor.redeem.point.developer.bitdubai.version_1.exceptions.CantUpdateRedeemPointException;
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

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.ASSET_ISSUER)
    private AssetIssuerActorNetworkServiceManager assetIssuerActorNetworkServiceManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.REDEEM_POINT)
    private AssetRedeemPointActorNetworkServiceManager assetRedeemPointActorNetworkServiceManager;

    @NeededPluginReference(platform = Platforms.CRYPTO_CURRENCY_PLATFORM, layer = Layers.NETWORK_SERVICE, plugin = Plugins.CRYPTO_ADDRESSES)
    private CryptoAddressesManager cryptoAddressesNetworkServiceManager;

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
            return this.redeemPointActorDao.getActorByPublicKey(actorPublicKey);
        } catch (CantGetAssetRedeemPointActorsException e) {
            throw new CantGetAssetRedeemPointActorsException("", FermatException.wrapException(e), "Cant Get Actor Asset User from Data Base", null);
        }
    }

    @Override
    public void createActorAssetRedeemPointFactory(String assetRedeemPointActorPublicKey, String assetRedeemPointActorName, byte[] assetRedeemPointActorprofileImage) throws CantCreateActorRedeemPointException {
        try {
            ActorAssetRedeemPoint actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetRedeemPoint();

            Double locationLatitude = new Random().nextDouble();
            Double locationLongitude = new Random().nextDouble();

            if (actorAssetRedeemPoint == null) {

                RedeemPointActorRecord record = new RedeemPointActorRecord(
                        assetRedeemPointActorPublicKey,
                        assetRedeemPointActorName,
                        DAPConnectionState.REGISTERED_LOCALLY,
                        locationLatitude,
                        locationLongitude,
                        System.currentTimeMillis(),
                        assetRedeemPointActorprofileImage);

//                RedeemPointActorRecord record = new RedeemPointActorRecord("RedeemP_" + i, publicKey);
//                record.setDAPConnectionState(DAPConnectionState.CONNECTED);
//                record.setProfileImage(new byte[0]);
//                record.setLocation(location);
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
                redeemPointActorDao.createNewRedeemPoint(record);
            }

            registerActorInActorNetowrkSerice();

            actorAssetRedeemPoint = this.redeemPointActorDao.getActorAssetRedeemPoint();

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

    public void registerActorInActorNetowrkSerice() throws CantRegisterActorAssetRedeemPointException {
        try {
            /*
             * Send the Actor Asset Issuer Local for Register in Actor Network Service
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
                AssetExtendedPublickKeyContentMessage assetExtendedPublickKeyContentMessage = new AssetExtendedPublickKeyContentMessage();
                DAPMessage dapMessage = new DAPMessage(
                        DAPMessageType.EXTENDED_PUBLIC_KEY,
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

                if (request.getCryptoAddressDealer().equals(CryptoAddressDealers.DAP_ASSET)) {

                    if (request.getAction().equals(RequestAction.ACCEPT))
                        this.handleCryptoAddressReceivedEvent(request);

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

                this.redeemPointActorDao.updateAssetRedeemPointDAPConnectionStateActorNetworService(request.getIdentityPublicKeyResponding(), DAPConnectionState.CONNECTED_ONLINE, request.getCryptoAddress());

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

    public void handleNewReceiveMessageActorNotificationEvent(DAPActor dapActorSender, DAPActor dapActorDestination, DAPMessage dapMessage) {
        System.out.println("*****Actor Asset Redeem Point Recibe*****");
        System.out.println("Actor Asset Redeem Point name: " + dapActorSender.getName());
        System.out.println("Actor Asset Redeem Point message: " + dapMessage.getMessageType());
        System.out.println("***************************************************************");
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
        FermatEventHandler fermatEventHandler;

        fermatEventListener = eventManager.getNewListener(EventType.COMPLETE_ASSET_REDEEM_POINT_REGISTRATION_NOTIFICATION);
        fermatEventListener.setEventHandler(new ActorAssetRedeemPointCompleteRegistrationNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        fermatEventListener = eventManager.getNewListener(EventType.NEW_RECEIVE_MESSAGE_ACTOR);
        fermatEventListener.setEventHandler(new NewReceiveMessageActorRedeemPointNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        fermatEventListener = eventManager.getNewListener(com.bitdubai.fermat_ccp_api.all_definition.enums.EventType.CRYPTO_ADDRESSES_NEWS);
        fermatEventListener.setEventHandler(new CryptoAddressRequestedEventHandler(this, cryptoAddressesNetworkServiceManager));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);
    }
}
