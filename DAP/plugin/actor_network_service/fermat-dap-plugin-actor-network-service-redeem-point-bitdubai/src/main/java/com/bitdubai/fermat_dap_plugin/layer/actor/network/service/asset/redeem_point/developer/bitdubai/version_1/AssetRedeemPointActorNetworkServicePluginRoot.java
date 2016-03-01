/*
 * @#AssetTransmissionPluginRoot.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1;

import android.util.Base64;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.DiscoveryQueryParameters;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.developer.LogManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.all_definition.network_service.interfaces.NetworkServiceConnectionManager;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.all_definition.util.XMLParser;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogLevel;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPMessageSubject;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPMessageType;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventType;
import com.bitdubai.fermat_dap_api.layer.all_definition.events.ActorAssetRedeemPointCompleteRegistrationNotificationEvent;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantHandleDapNewMessagesException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.DAPMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantGetDAPMessagesException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantSendMessageException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantUpdateMessageStatusException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.message.NetworkServiceMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.message.NetworkServiceMessageAccept;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.message.NetworkServiceMessageDeny;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.message.NetworkServiceMessageRequest;
import com.bitdubai.fermat_dap_api.layer.dap_actor.DAPActor;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.RedeemPointActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.exceptions.CantRegisterActorAssetRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.exceptions.CantRequestListActorAssetRedeemPointRegisteredException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.interfaces.AssetRedeemPointActorNetworkServiceManager;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.agents.AssetRedeemPointActorNetworkServiceAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.communications.CommunicationNetworkServiceConnectionManager;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.communications.CommunicationNetworkServiceLocal;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.communications.CommunicationRegistrationProcessNetworkServiceAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.database.communications.CommunicationNetworkServiceDatabaseConstants;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.database.communications.CommunicationNetworkServiceDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.database.communications.CommunicationNetworkServiceDeveloperDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.database.communications.OutgoingMessageDao;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.ClientConnectionCloseNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.ClientConnectionLooseNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.ClientSuccessfullReconnectNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.CompleteComponentConnectionRequestNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.CompleteComponentRegistrationNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.CompleteRequestListComponentRegisteredNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.NewReceiveMessagesNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.NewSentMessagesNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.event_handlers.VPNConnectionCloseNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.exceptions.CantInitializeTemplateNetworkServiceDatabaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.common.network_services.abstract_classes.AbstractNetworkService;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatMessageCommunication;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatMessageCommunicationFactory;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.P2pEventType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.ClientConnectionCloseNotificationEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.VPNConnectionCloseNotificationEvent;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.WsCommunicationsCloudClientManager;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.client.CommunicationsClientConnection;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.contents.FermatMessage;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatMessageContentType;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatMessagesStatus;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.exceptions.CantRegisterComponentException;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.exceptions.CantRequestListException;
import com.bitdubai.fermat_pip_api.layer.actor.exception.CantGetLogTool;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * The Class <code>com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.AssetRedeemPointActorNetworkServicePluginRoot</code> is
 * the responsible to initialize all component to work together, and hold all resources they needed.
 * <p/>
 * Created by Roberto Requena - (rrequena) on 21/07/15.
 * Modified by franklin on 17/10/2015
 *
 * @version 1.0
 */
public class AssetRedeemPointActorNetworkServicePluginRoot extends AbstractNetworkService implements
        AssetRedeemPointActorNetworkServiceManager,
        DatabaseManagerForDevelopers,
        LogManagerForDevelopers {

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    protected PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.LOG_MANAGER)
    private LogManager logManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    private ErrorManager errorManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    private EventManager eventManager;

    @NeededPluginReference(platform = Platforms.COMMUNICATION_PLATFORM, layer = Layers.COMMUNICATION, plugin = Plugins.WS_CLOUD_CLIENT)
    private WsCommunicationsCloudClientManager wsCommunicationsCloudClientManager;

    /**
     * Represent the dataBase
     */
    private Database dataBase;

    /**
     * Represent the EVENT_SOURCE
     */
    public final static EventSource EVENT_SOURCE = EventSource.NETWORK_SERVICE_ACTOR_ASSET_REDEEM_POINT;

    protected final static String DAP_IMG_REDEEM_POINT = "DAP_IMG_REDEEM_POINT";
    protected final static String DAP_REGISTERED_ISSUERS = "DAP_REGISTERED_ISSUERS";

    List<FermatEventListener> listenersAdded = new ArrayList<>();

    /**
     * DealsWithLogger interface member variable
     */
    static Map<String, LogLevel> newLoggingLevel = new HashMap<>();

    /**
     * Represent the communicationNetworkServiceConnectionManager
     */
    private CommunicationNetworkServiceConnectionManager communicationNetworkServiceConnectionManager;

    /**
     * Represent the remoteNetworkServicesRegisteredList
     */
    private List<PlatformComponentProfile> remoteNetworkServicesRegisteredList;

    /**
     * Represent the actorAssetUserPendingToRegistration
     */
    private List<PlatformComponentProfile> actorAssetRedeemPointPendingToRegistration;

    /**
     * Represent the communicationNetworkServiceDeveloperDatabaseFactory
     */
    private CommunicationNetworkServiceDeveloperDatabaseFactory communicationNetworkServiceDeveloperDatabaseFactory;

    /**
     * Represent the communicationRegistrationProcessNetworkServiceAgent
     */
    private CommunicationRegistrationProcessNetworkServiceAgent communicationRegistrationProcessNetworkServiceAgent;

    /**
     * Represent the actorAssetUserRegisteredList
     */
    private List<ActorAssetRedeemPoint> actorAssetRedeemPointRegisteredList;

    private AssetRedeemPointActorNetworkServiceAgent assetRedeemPointActorNetworkServiceAgent;

    private int createactorAgentnum = 0;
    /**
     * Represent the flag to start only once
     */
    private AtomicBoolean flag = new AtomicBoolean(false);

    /**
     * Constructor
     */
    public AssetRedeemPointActorNetworkServicePluginRoot() {
        super(
                new PluginVersionReference(new Version()),
                PlatformComponentType.NETWORK_SERVICE,
                NetworkServiceType.ASSET_REDEEM_POINT_ACTOR,
                "Actor Network Service Asset RedeemPoint",
                "ActorNetworkServiceAssetRedeemPoint",
                null,
                EventSource.NETWORK_SERVICE_ACTOR_ASSET_REDEEM_POINT
        );
        this.listenersAdded = new ArrayList<>();

        this.actorAssetRedeemPointRegisteredList = new ArrayList<>();
        this.actorAssetRedeemPointPendingToRegistration = new ArrayList<>();
    }

    /**
     * Get the IdentityPublicKey
     *
     * @return String
     */
    public String getIdentityPublicKey() {
        return this.identity.getPublicKey();
    }

    /**
     * This method initialize the communicationNetworkServiceConnectionManager.
     * IMPORTANT: Call this method only in the CommunicationRegistrationProcessNetworkServiceAgent, when execute the registration process
     * because at this moment, is create the platformComponentProfile for this component
     */
    public void initializeCommunicationNetworkServiceConnectionManager() {
        this.communicationNetworkServiceConnectionManager = new CommunicationNetworkServiceConnectionManager(
                getPlatformComponentProfilePluginRoot(),
                identity,
                wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection(),
                dataBase,
                errorManager,
                eventManager);
    }

    private void initializeActorRedeemPointAgent() {
        try {
            assetRedeemPointActorNetworkServiceAgent = new AssetRedeemPointActorNetworkServiceAgent(
                    this,
                    wsCommunicationsCloudClientManager,
                    communicationNetworkServiceConnectionManager,
                    getPlatformComponentProfilePluginRoot(),
                    errorManager,
                    identity,
                    dataBase);
            assetRedeemPointActorNetworkServiceAgent.start();

        } catch (CantStartAgentException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
        }
    }

    @Override
    public List<String> getClassesFullPath() {
        List<String> returnedClasses = new ArrayList<String>();
        returnedClasses.add("com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.redeem_point.developer.bitdubai.version_1.AssetRedeemPointActorNetworkServicePluginRoot");
        /**
         * I return the values.
         */
        return returnedClasses;
    }

    @Override
    public void setLoggingLevelPerClass(Map<String, LogLevel> newLoggingLevel) {
        /**
         * Modify by Manuel on 25/07/2015
         * I will wrap all this method within a try, I need to catch any generic java Exception
         */
        try {

            /**
             * I will check the current values and update the LogLevel in those which is different
             */
            for (Map.Entry<String, LogLevel> pluginPair : newLoggingLevel.entrySet()) {
                /**
                 * if this path already exists in the Root.bewLoggingLevel I'll update the value, else, I will put as new
                 */
                if (AssetRedeemPointActorNetworkServicePluginRoot.newLoggingLevel.containsKey(pluginPair.getKey())) {
                    AssetRedeemPointActorNetworkServicePluginRoot.newLoggingLevel.remove(pluginPair.getKey());
                    AssetRedeemPointActorNetworkServicePluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
                } else {
                    AssetRedeemPointActorNetworkServicePluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
                }
            }

        } catch (Exception exception) {
            FermatException e = new CantGetLogTool(CantGetLogTool.DEFAULT_MESSAGE, FermatException.wrapException(exception), "setLoggingLevelPerClass: " + AssetRedeemPointActorNetworkServicePluginRoot.newLoggingLevel, "Check the cause");
            // this.errorManager.reportUnexpectedAddonsException(Addons.EXTRA_USER, UnexpectedAddonsExceptionSeverity.DISABLES_THIS_ADDONS, e);
        }
    }

    @Override
    public synchronized void start() throws CantStartPluginException {

        if (!flag.getAndSet(true)) {
            if (this.serviceStatus != ServiceStatus.STARTING) {
                serviceStatus = ServiceStatus.STARTING;

                System.out.println("Start Plugin Asset Redeem Point ActorNetworkService");
                logManager.log(AssetRedeemPointActorNetworkServicePluginRoot.getLogLevelByClass(this.getClass().getName()), "AssetRedeemPointActorNetworkService - Starting", "AssetRedeemPointActorNetworkServicePluginRoot - Starting", "AssetRedeemPointActorNetworkServicePluginRoot - Starting");

        /*
         * Validate required resources
         */
                validateInjectedResources();

                try {
        /*
         * Create a new key pair for this execution
         */
                    initializeClientIdentity();
           /*
            * Initialize the data base
            */
                    initializeDb();

            /*
             * Initialize Developer Database Factory
             */
                    communicationNetworkServiceDeveloperDatabaseFactory = new CommunicationNetworkServiceDeveloperDatabaseFactory(pluginDatabaseSystem, pluginId);
                    communicationNetworkServiceDeveloperDatabaseFactory.initializeDatabase();
                        /*
             * Initialize listeners
             */
                    initializeListener();

           /*
            * Initialize connection manager
            */
                    initializeCommunicationNetworkServiceConnectionManager();

            /*
             * Verify if the communication cloud client is active
             */
                    if (!wsCommunicationsCloudClientManager.isDisable()) {

                /*
                 * Construct my profile and register me
                 */
                        PlatformComponentProfile platformComponentProfilePluginRoot = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructPlatformComponentProfileFactory(getIdentityPublicKey(),
                                getAlias().toLowerCase(),
                                getName(),
                                getNetworkServiceType(),
                                getPlatformComponentType(),
                                getExtraData());

                        setPlatformComponentProfilePluginRoot(platformComponentProfilePluginRoot);

                /*
                 * Initialize the agent and start
                 */
                        communicationRegistrationProcessNetworkServiceAgent = new CommunicationRegistrationProcessNetworkServiceAgent(this, wsCommunicationsCloudClientManager);
                        communicationRegistrationProcessNetworkServiceAgent.start();
                    }

                    this.serviceStatus = ServiceStatus.STARTED;

                } catch (CantInitializeTemplateNetworkServiceDatabaseException exception) {

                    StringBuffer contextBuffer = new StringBuffer();
                    contextBuffer.append("Plugin ID: " + pluginId);
                    contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
                    contextBuffer.append("Database Name: " + CommunicationNetworkServiceDatabaseConstants.DATA_BASE_NAME);

                    String context = contextBuffer.toString();
                    String possibleCause = "The Asset RedeemPoint Actor Network Service Database triggered an unexpected problem that wasn't able to solve by itself";
                    CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, exception, context, possibleCause);

                    errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

                    throw pluginStartException;
                }
            }
        }
    }

    private void initializeClientIdentity() throws CantStartPluginException {

        System.out.println("Calling the method - initializeClientIdentity() ");

        try {

            System.out.println("Loading clientIdentity");

             /*
              * Load the file with the clientIdentity
              */
            PluginTextFile pluginTextFile = pluginFileSystem.getTextFile(pluginId, "private", "clientIdentity", FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            String content = pluginTextFile.getContent();

            //System.out.println("content = "+content);

            identity = new ECCKeyPair(content);

        } catch (FileNotFoundException e) {

            /*
             * The file no exist may be the first time the plugin is running on this device,
             * We need to create the new clientIdentity
             */
            try {

                System.out.println("No previous clientIdentity finder - Proceed to create new one");

                /*
                 * Create the new clientIdentity
                 */
                identity = new ECCKeyPair();

                /*
                 * save into the file
                 */
                PluginTextFile pluginTextFile = pluginFileSystem.createTextFile(pluginId, "private", "clientIdentity", FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
                pluginTextFile.setContent(identity.getPrivateKey());
                pluginTextFile.persistToMedia();

            } catch (Exception exception) {
                /*
                 * The file cannot be created. I can not handle this situation.
                 */
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, exception);
                throw new CantStartPluginException(exception.getLocalizedMessage());
            }


        } catch (CantCreateFileException cantCreateFileException) {

            /*
             * The file cannot be load. I can not handle this situation.
             */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantCreateFileException);
            throw new CantStartPluginException(cantCreateFileException.getLocalizedMessage());

        }

    }

    @Override
    public void pause() {
        /*
         * Pause
         */
        communicationNetworkServiceConnectionManager.pause();

        /*
         * Set the new status
         */
        this.serviceStatus = ServiceStatus.PAUSED;
    }

    @Override
    public void resume() {
        /*
         * resume the managers
         */
        communicationNetworkServiceConnectionManager.resume();

        /*
         * Set the new status
         */
        this.serviceStatus = ServiceStatus.STARTED;
    }

    @Override
    public void stop() {
        //Clear all references of the event listeners registered with the event manager.
        listenersAdded.clear();

        /*
         * Stop all connection on the managers
         */
        communicationNetworkServiceConnectionManager.closeAllConnection();

        //set to not register
        register = Boolean.FALSE;

        /*
         * Set the new status
         */
        this.serviceStatus = ServiceStatus.STOPPED;
    }

    @Override
    public void registerActorAssetRedeemPoint(ActorAssetRedeemPoint actorAssetRedeemPointToRegister) throws CantRegisterActorAssetRedeemPointException {
        try {

            final CommunicationsClientConnection communicationsClientConnection = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection();

            /*
             * If register
             */
            if (true) {
                /*
                 * Construct the profile
                 */

                final PlatformComponentProfile platformComponentProfileAssetRedeemPoint = constructPlatformComponentProfile(actorAssetRedeemPointToRegister, communicationsClientConnection);
                /*
                 * ask to the communication cloud client to register
                 */
                /**
                 * I need to add this in a new thread other than the main android thread
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            communicationsClientConnection.registerComponentForCommunication(getNetworkServiceType(), platformComponentProfileAssetRedeemPoint);
                        } catch (CantRegisterComponentException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } else {
                /*
                 * Construct the profile
                 */
                PlatformComponentProfile platformComponentProfileAssetRedeemPoint = constructPlatformComponentProfile(
                        actorAssetRedeemPointToRegister,
                        communicationsClientConnection);
                /*
                 * Add to the list of pending to register
                 */

                actorAssetRedeemPointPendingToRegistration.add(platformComponentProfileAssetRedeemPoint);
            }
        } catch (Exception e) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Plugin was not registered";

            CantRegisterActorAssetRedeemPointException pluginStartException = new CantRegisterActorAssetRedeemPointException(CantStartPluginException.DEFAULT_MESSAGE, e, context, possibleCause);
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

            throw pluginStartException;
        }
    }

    @Override
    public void updateActorAssetRedeemPoint(ActorAssetRedeemPoint actorAssetRedeemPointToRegister) throws CantRegisterActorAssetRedeemPointException {
        try {

            final CommunicationsClientConnection communicationsClientConnection = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection();

            /*
             * If register
             */
            if (true) {
                /*
                 * Construct the profile
                 */
                final PlatformComponentProfile platformComponentProfileAssetRedeemPoint = constructPlatformComponentProfile(actorAssetRedeemPointToRegister, communicationsClientConnection);
                /*
                 * ask to the communication cloud client to register
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            communicationsClientConnection.updateRegisterActorProfile(getNetworkServiceType(), platformComponentProfileAssetRedeemPoint);
                        } catch (CantRegisterComponentException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
//            else {
//                /*
//                 * Construct the profile
//                 */
//                PlatformComponentProfile platformComponentProfileAssetRedeemPoint = constructPlatformComponentProfile(actorAssetRedeemPointToRegister, communicationsClientConnection);
//                /*
//                 * Add to the list of pending to register
//                 */
//                actorAssetRedeemPointPendingToRegistration.add(platformComponentProfileAssetRedeemPoint);
//            }
        } catch (Exception e) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Plugin was not registered";

            CantRegisterActorAssetRedeemPointException pluginStartException = new CantRegisterActorAssetRedeemPointException(CantStartPluginException.DEFAULT_MESSAGE, e, context, possibleCause);
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

            throw pluginStartException;
        }
    }

    @Override
    public void sendMessage(DAPMessage dapMessage) throws CantSendMessageException {
        receivePublicKeyExtended(dapMessage);
    }

    //TODO HANDLE THESE!!!!

    /**
     * This method retrieves the list of new incoming and unread DAP Messages for a specific type.
     *
     * @param type The {@link DAPMessageType} of message to search for.
     * @return {@link List} instance filled with all the {@link DAPMessage} that were found.
     * @throws CantGetDAPMessagesException If there was an error while querying for the list.
     */
    @Override
    public List<DAPMessage> getUnreadDAPMessagesByType(DAPMessageType type) throws CantGetDAPMessagesException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<DAPMessage> getUnreadDAPMessageBySubject(DAPMessageSubject subject) throws CantGetDAPMessagesException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void confirmReception(DAPMessage message) throws CantUpdateMessageStatusException {

    }

    private void receivePublicKeyExtended(DAPMessage dapMessage) {
        DAPActor actorAssetIssuerSender = dapMessage.getActorSender();
        DAPActor actorRedeemPointDestination = dapMessage.getActorReceiver();
        try {

            if (true) {

                CommunicationNetworkServiceLocal communicationNetworkServiceLocal = communicationNetworkServiceConnectionManager.getNetworkServiceLocalInstance(actorRedeemPointDestination.getActorPublicKey());

                String messageContentIntoJson = XMLParser.parseObject(dapMessage);

                if (communicationNetworkServiceLocal != null) {

                    //Send the message
                    communicationNetworkServiceLocal.sendMessage(actorAssetIssuerSender.getActorPublicKey(), actorRedeemPointDestination.getActorPublicKey(), messageContentIntoJson);

                } else {

                    /*
                     * Created the message
                     */
                    FermatMessage fermatMessage = FermatMessageCommunicationFactory.constructFermatMessage(
                            actorAssetIssuerSender.getActorPublicKey(),//Sender
                            actorRedeemPointDestination.getActorPublicKey(), //Receiver
                            messageContentIntoJson,                //Message Content
                            FermatMessageContentType.TEXT);//Type

                    /*
                     * Configure the correct status
                     */
                    ((FermatMessageCommunication) fermatMessage).setFermatMessagesStatus(FermatMessagesStatus.PENDING_TO_SEND);

                    /*
                     * Save to the data base table
                     */
                    OutgoingMessageDao outgoingMessageDao = communicationNetworkServiceConnectionManager.getOutgoingMessageDao();
                    outgoingMessageDao.create(fermatMessage);

                    /*
                     * Create the sender basic profile
                     */
                    PlatformComponentProfile sender = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().
                            constructBasicPlatformComponentProfileFactory(
                                    actorAssetIssuerSender.getActorPublicKey(),
                                    NetworkServiceType.UNDEFINED,
                                    PlatformComponentType.ACTOR_ASSET_ISSUER);

                    /*
                     * Create the receiver basic profile
                     */
                    PlatformComponentProfile receiver = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().
                            constructBasicPlatformComponentProfileFactory(
                                    actorRedeemPointDestination.getActorPublicKey(),
                                    NetworkServiceType.UNDEFINED,
                                    PlatformComponentType.ACTOR_ASSET_REDEEM_POINT);

                    /*
                     * Ask the client to connect
                     */
                    communicationNetworkServiceConnectionManager.connectTo(sender, getPlatformComponentProfilePluginRoot(), receiver);
                }


            } else {

                StringBuffer contextBuffer = new StringBuffer();
                contextBuffer.append("Plugin ID: " + pluginId);
                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
                contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
                contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
                contextBuffer.append("errorManager: " + errorManager);
                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
                contextBuffer.append("eventManager: " + eventManager);

                String context = contextBuffer.toString();
                String possibleCause = "Asset Redeem Point Actor Network Service Not Registered";
//
//                CantRequestCryptoAddressException pluginStartException = new CantRequestCryptoAddressException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);
//
//                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
//
//                throw pluginStartException;

            }

        } catch (Exception e) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Cant Request Crypto Address";

//            CantRequestCryptoAddressException pluginStartException = new CantRequestCryptoAddressException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);

            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

//            throw pluginStartException;
        }
    }

    @Override
    public List<ActorAssetRedeemPoint> getListActorAssetRedeemPointRegistered() throws CantRequestListActorAssetRedeemPointRegisteredException {

        try {
//            if (true) {
            if (actorAssetRedeemPointRegisteredList != null && !actorAssetRedeemPointRegisteredList.isEmpty()) {
                actorAssetRedeemPointRegisteredList.clear();
            }
            DiscoveryQueryParameters discoveryQueryParametersAssetUser = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().
                    constructDiscoveryQueryParamsFactory(PlatformComponentType.ACTOR_ASSET_REDEEM_POINT, //applicant = who made the request
                            NetworkServiceType.UNDEFINED,
                            null,                     // alias
                            null,                     // identityPublicKey
                            null,                     // location
                            null,                     // distance
                            null,                     // name
                            null,                     // extraData
                            null,                     // offset
                            null,                     // max
                            null,                     // fromOtherPlatformComponentType, when use this filter apply the identityPublicKey
                            null);

            List<PlatformComponentProfile> platformComponentProfileRegisteredListRemote = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().requestListComponentRegistered(discoveryQueryParametersAssetUser);

            if (platformComponentProfileRegisteredListRemote != null && !platformComponentProfileRegisteredListRemote.isEmpty()) {

                for (PlatformComponentProfile platformComponentProfile : platformComponentProfileRegisteredListRemote) {

                    String profileImage = "";
                    List<String> registeredIssuers = new ArrayList<>();
                    if (!platformComponentProfile.getExtraData().equals("")) {
                        try {
                            JsonParser jParser = new JsonParser();
                            JsonObject jsonObject = jParser.parse(platformComponentProfile.getExtraData()).getAsJsonObject();

                            profileImage = jsonObject.get(DAP_IMG_REDEEM_POINT).getAsString();
                            Type type = new TypeToken<List<String>>() {
                            }.getType();
                            registeredIssuers = new Gson().fromJson(jsonObject.get(DAP_REGISTERED_ISSUERS).getAsString(), type);
                        } catch (Exception e) {
                            profileImage = platformComponentProfile.getExtraData();
                        }
                    }

                    byte[] imageByte = Base64.decode(profileImage, Base64.DEFAULT);

                    ActorAssetRedeemPoint actorAssetRedeemPoint = new RedeemPointActorRecord(
                            platformComponentProfile.getIdentityPublicKey(),
                            platformComponentProfile.getName(),
                            imageByte,
                            platformComponentProfile.getLocation(),
                            registeredIssuers);

                    actorAssetRedeemPointRegisteredList.add(actorAssetRedeemPoint);
                }
            } else {
                return actorAssetRedeemPointRegisteredList;
            }

        } catch (CantRequestListException e) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Cant Request List Actor Asset Redeem Point Registered";

            CantRequestListActorAssetRedeemPointRegisteredException pluginStartException = new CantRequestListActorAssetRedeemPointRegisteredException(CantRequestListActorAssetRedeemPointRegisteredException.DEFAULT_MESSAGE, null, context, possibleCause);

            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

            throw pluginStartException;
        }
        return actorAssetRedeemPointRegisteredList;
    }

    @Override
    public List<PlatformComponentProfile> getRemoteNetworkServicesRegisteredList() {
        return remoteNetworkServicesRegisteredList;
    }

    @Override
    public void requestRemoteNetworkServicesRegisteredList(DiscoveryQueryParameters discoveryQueryParameters) {
        System.out.println("Asset Redeem Point ActorNetworkServicePluginRoot - requestRemoteNetworkServicesRegisteredList");

        /*
         * Request the list of component registers
         */
        try {
            wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().requestListComponentRegistered(getPlatformComponentProfilePluginRoot(), discoveryQueryParameters);
        } catch (CantRequestListException e) {
            e.printStackTrace();
        }
    }

    @Override
    public NetworkServiceConnectionManager getNetworkServiceConnectionManager() {
        return communicationNetworkServiceConnectionManager;
    }

    @Override
    public DiscoveryQueryParameters constructDiscoveryQueryParamsFactory(PlatformComponentType platformComponentType, NetworkServiceType networkServiceType, String alias, String identityPublicKey, Location location, Double distance, String name, String extraData, Integer firstRecord, Integer numRegister, PlatformComponentType fromOtherPlatformComponentType, NetworkServiceType fromOtherNetworkServiceType) {
        return wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructDiscoveryQueryParamsFactory(platformComponentType,
                networkServiceType, alias, identityPublicKey, location, distance, name, extraData, firstRecord, numRegister, fromOtherPlatformComponentType, fromOtherNetworkServiceType);

    }

    @Override
    public void handleCompleteComponentRegistrationNotificationEvent(PlatformComponentProfile platformComponentProfileRegistered) {
        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.COMMUNICATION_CLOUD_CLIENT && this.register) {
            if (communicationRegistrationProcessNetworkServiceAgent.isAlive()) {
                communicationRegistrationProcessNetworkServiceAgent.interrupt();
                communicationRegistrationProcessNetworkServiceAgent = null;
            }

            /*
             * Construct my profile and register me
             */
            PlatformComponentProfile platformComponentProfileToReconnect = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructPlatformComponentProfileFactory(this.getIdentityPublicKey(),
                    this.getAlias().toLowerCase(),
                    this.getName(),
                    this.getNetworkServiceType(),
                    this.getPlatformComponentType(),
                    this.getExtraData());

            try {
                    /*
                     * Register me
                     */
                wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().registerComponentForCommunication(this.getNetworkServiceType(), platformComponentProfileToReconnect);

            } catch (CantRegisterComponentException e) {
                e.printStackTrace();
            }
        }

        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.ACTOR_ASSET_REDEEM_POINT) {
            System.out.print("ACTOR REDEEM POINT REGISTRADO!! -----------------------\n" +
                    "-----------------------\n REDEEM: " + platformComponentProfileRegistered.getAlias());
        }

        /*
         * If the component registered have my profile and my identity public key
         */
        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.NETWORK_SERVICE &&
                platformComponentProfileRegistered.getNetworkServiceType() == NetworkServiceType.ASSET_REDEEM_POINT_ACTOR &&
                platformComponentProfileRegistered.getIdentityPublicKey().equals(identity.getPublicKey())) {

            /*
             * Mark as register
             */
            this.register = Boolean.TRUE;

            initializeActorRedeemPointAgent();

            /*
             * If exist actor asset redeem point pending to registration
             */
            if (actorAssetRedeemPointPendingToRegistration != null && !actorAssetRedeemPointPendingToRegistration.isEmpty()) {

                CommunicationsClientConnection communicationsClientConnection = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection();

                for (PlatformComponentProfile platformComponentProfileAssetRedeemPoint : actorAssetRedeemPointPendingToRegistration) {

                     /*
                     * ask to the communication cloud client to register
                     */
                    try {
                        communicationsClientConnection.registerComponentForCommunication(
                                this.getNetworkServiceType(),
                                platformComponentProfileAssetRedeemPoint);
                    } catch (CantRegisterComponentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /*
         * If is a actor registered
         */
        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.ACTOR_ASSET_REDEEM_POINT &&
                platformComponentProfileRegistered.getNetworkServiceType() == NetworkServiceType.UNDEFINED) {

            Location loca = null;

            ActorAssetRedeemPoint actorAssetUserNewRegistered = new RedeemPointActorRecord(
                    platformComponentProfileRegistered.getIdentityPublicKey(),
                    platformComponentProfileRegistered.getName(),
                    convertoByteArrayfromString(platformComponentProfileRegistered.getExtraData()),
                    loca,
                    new ArrayList<String>());

            if (communicationNetworkServiceConnectionManager == null) {

                this.communicationNetworkServiceConnectionManager = new CommunicationNetworkServiceConnectionManager(
                        getPlatformComponentProfilePluginRoot(),
                        identity,
                        wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection(),
                        dataBase,
                        errorManager,
                        eventManager);
            }

//            if (createactorAgentnum == 0) {
//
//                assetRedeemPointActorNetworkServiceAgent = new AssetRedeemPointActorNetworkServiceAgent(
//                        this,
//                        wsCommunicationsCloudClientManager,
//                        communicationNetworkServiceConnectionManager,
//                        getPlatformComponentProfilePluginRoot(),
//                        errorManager,
//                        identity,
//                        dataBase);
//
//                // start main threads
//                assetRedeemPointActorNetworkServiceAgent.start();
//
//                createactorAgentnum = 1;
//            }

            System.out.println("Actor Asset Redeem Point REGISTRADO en A.N.S - Enviando Evento de Notificacion");

            FermatEvent event = eventManager.getNewEvent(EventType.COMPLETE_ASSET_REDEEM_POINT_REGISTRATION_NOTIFICATION);
            event.setSource(EventSource.ACTOR_ASSET_REDEEM_POINT);

            ((ActorAssetRedeemPointCompleteRegistrationNotificationEvent) event).setActorAssetRedeemPoint(actorAssetUserNewRegistered);

            eventManager.raiseEvent(event);
        }
    }

    @Override
    public void handleFailureComponentRegistrationNotificationEvent(PlatformComponentProfile networkServiceApplicant, PlatformComponentProfile remoteParticipant) {

        System.out.println(" Failure ConnecTo() with " + remoteParticipant.getIdentityPublicKey());

        assetRedeemPointActorNetworkServiceAgent.connectionFailure(remoteParticipant.getIdentityPublicKey());
    }

    @Override
    public void handleCompleteRequestListComponentRegisteredNotificationEvent(List<PlatformComponentProfile> platformComponentProfileRegisteredList) {
        System.out.println(" CommunicationNetworkServiceConnectionManager - Starting method handleCompleteComponentRegistrationNotificationEvent");
        /*
         * if have result create a ActorAssetUser
         */
        if (platformComponentProfileRegisteredList != null && !platformComponentProfileRegisteredList.isEmpty()) {
            /*
             * Get a remote network service registered from the list requested
             */
            PlatformComponentProfile remoteNetworkServiceToConnect = platformComponentProfileRegisteredList.get(0);

            /*
             * tell to the manager to connect to this remote network service
             */
            if (remoteNetworkServiceToConnect.getNetworkServiceType() == NetworkServiceType.UNDEFINED && remoteNetworkServiceToConnect.getPlatformComponentType() == PlatformComponentType.ACTOR_ASSET_REDEEM_POINT) {
                for (PlatformComponentProfile p : platformComponentProfileRegisteredList) {
                    Location loca = null;
                    ActorAssetRedeemPoint actorAssetUserNew = new RedeemPointActorRecord(p.getIdentityPublicKey(), p.getName(), convertoByteArrayfromString(p.getExtraData()), loca, new ArrayList<String>());

                    actorAssetRedeemPointRegisteredList.add(actorAssetUserNew);
                }

                FermatEvent event = eventManager.getNewEvent(EventType.COMPLETE_REQUEST_LIST_ASSET_REDEEM_POINT_REGISTERED_NOTIFICATION);
                event.setSource(EventSource.ACTOR_ASSET_REDEEM_POINT);

                //((AssetUserActorRequestListRegisteredNetworkServiceNotificationEvent) event).setActorAssetUserList(actorAssetUserRegisteredList);
                eventManager.raiseEvent(event);
            } else if (remoteNetworkServiceToConnect.getNetworkServiceType() == NetworkServiceType.ASSET_REDEEM_POINT_ACTOR && remoteNetworkServiceToConnect.getPlatformComponentType() == PlatformComponentType.NETWORK_SERVICE) {
                /*
                 * save into the cache
                 */
                remoteNetworkServicesRegisteredList = platformComponentProfileRegisteredList;
            }
        }
    }


    @Override
    public void handleCompleteComponentConnectionRequestNotificationEvent(PlatformComponentProfile applicantComponentProfile, PlatformComponentProfile remoteComponentProfile) {
        System.out.println(" TemplateNetworkServiceRoot - Starting method handleCompleteComponentConnectionRequestNotificationEvent");

        /*
         * Tell the manager to handler the new connection stablished
         */
        communicationNetworkServiceConnectionManager.handleEstablishedRequestedNetworkServiceConnection(remoteComponentProfile);
    }

    /**
     * Handles the events VPNConnectionCloseNotificationEvent
     *
     * @param fermatEvent
     */
    @Override
    public void handleVpnConnectionCloseNotificationEvent(FermatEvent fermatEvent) {

        if (fermatEvent instanceof VPNConnectionCloseNotificationEvent) {

            VPNConnectionCloseNotificationEvent vpnConnectionCloseNotificationEvent = (VPNConnectionCloseNotificationEvent) fermatEvent;

            if (vpnConnectionCloseNotificationEvent.getNetworkServiceApplicant() == getNetworkServiceType()) {
                String remotePublicKey = vpnConnectionCloseNotificationEvent.getRemoteParticipant().getIdentityPublicKey();

                if (communicationNetworkServiceConnectionManager != null)
                    communicationNetworkServiceConnectionManager.closeConnection(remotePublicKey);

                if (assetRedeemPointActorNetworkServiceAgent != null)
                    assetRedeemPointActorNetworkServiceAgent.getPoolConnectionsWaitingForResponse().remove(remotePublicKey);
            }
        }
    }

    /**
     * Handles the events ClientConnectionCloseNotificationEvent
     *
     * @param fermatEvent
     */
    @Override
    public void handleClientConnectionCloseNotificationEvent(FermatEvent fermatEvent) {

        if (fermatEvent instanceof ClientConnectionCloseNotificationEvent) {
            this.register = false;

            if (communicationNetworkServiceConnectionManager != null)
                communicationNetworkServiceConnectionManager.closeAllConnection();
        }

    }

    /*
     * Handles the events ClientConnectionLooseNotificationEvent
     */
    @Override
    public void handleClientConnectionLooseNotificationEvent(FermatEvent fermatEvent) {

        if (communicationNetworkServiceConnectionManager != null)
            communicationNetworkServiceConnectionManager.stop();

    }

    /*
     * Handles the events ClientSuccessfullReconnectNotificationEvent
     */
    @Override
    public void handleClientSuccessfullReconnectNotificationEvent(FermatEvent fermatEvent) {

        if (communicationNetworkServiceConnectionManager != null) {
            communicationNetworkServiceConnectionManager.restart();
        }


        if (communicationRegistrationProcessNetworkServiceAgent != null && !this.register) {

            if (communicationRegistrationProcessNetworkServiceAgent.isAlive()) {
                communicationRegistrationProcessNetworkServiceAgent.interrupt();
                communicationRegistrationProcessNetworkServiceAgent = null;
            }

                   /*
                 * Construct my profile and register me
                 */
            PlatformComponentProfile platformComponentProfileToReconnect = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().
                    constructPlatformComponentProfileFactory(this.getIdentityPublicKey(),
                            this.getAlias().toLowerCase(),
                            this.getName(),
                            this.getNetworkServiceType(),
                            this.getPlatformComponentType(),
                            this.getExtraData());

            try {
                    /*
                     * Register me
                     */
                wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().registerComponentForCommunication(
                        this.getNetworkServiceType(),
                        platformComponentProfileToReconnect);

            } catch (CantRegisterComponentException e) {
                e.printStackTrace();
            }

                /*
                 * Configure my new profile
                 */
            this.setPlatformComponentProfilePluginRoot(platformComponentProfileToReconnect);

                /*
                 * Initialize the connection manager
                 */
            this.initializeCommunicationNetworkServiceConnectionManager();
        }

        /*
         * Mark as register
         */
        this.register = Boolean.TRUE;

        if (assetRedeemPointActorNetworkServiceAgent != null) {
            try {
                assetRedeemPointActorNetworkServiceAgent.start();
            } catch (CantStartAgentException e) {
                e.printStackTrace();
            }
        } else {
            initializeActorRedeemPointAgent();
        }
    }

    /**
     * Static method to get the logging level from any class under root.
     *
     * @param className
     * @return
     */
    public static LogLevel getLogLevelByClass(String className) {
        try {
            /**
             * sometimes the classname may be passed dinamically with an $moretext
             * I need to ignore whats after this.
             */
            String[] correctedClass = className.split((Pattern.quote("$")));
            return AssetRedeemPointActorNetworkServicePluginRoot.newLoggingLevel.get(correctedClass[0]);
        } catch (Exception e) {
            /**
             * If I couldn't get the correct loggin level, then I will set it to minimal.
             */
            return DEFAULT_LOG_LEVEL;
        }
    }

    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        return communicationNetworkServiceDeveloperDatabaseFactory.getDatabaseList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        return communicationNetworkServiceDeveloperDatabaseFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        return communicationNetworkServiceDeveloperDatabaseFactory.getDatabaseTableContent(developerObjectFactory, developerDatabaseTable);
    }

    /**
     * This method initialize the database
     *
     * @throws CantInitializeTemplateNetworkServiceDatabaseException
     */
    private void initializeDb() throws CantInitializeTemplateNetworkServiceDatabaseException {
        try {
            /*
             * Open new database connection
             */
            this.dataBase = this.pluginDatabaseSystem.openDatabase(pluginId, CommunicationNetworkServiceDatabaseConstants.DATA_BASE_NAME);

        } catch (CantOpenDatabaseException cantOpenDatabaseException) {
            /*
             * The database exists but cannot be open. I can not handle this situation.
             */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantOpenDatabaseException);
            throw new CantInitializeTemplateNetworkServiceDatabaseException(cantOpenDatabaseException.getLocalizedMessage());

        } catch (DatabaseNotFoundException e) {
            /*
             * The database no exist may be the first time the plugin is running on this device,
             * We need to create the new database
             */
            CommunicationNetworkServiceDatabaseFactory communicationNetworkServiceDatabaseFactory = new CommunicationNetworkServiceDatabaseFactory(pluginDatabaseSystem);

            try {
                /*
                 * We create the new database
                 */
                this.dataBase = communicationNetworkServiceDatabaseFactory.createDatabase(pluginId, CommunicationNetworkServiceDatabaseConstants.DATA_BASE_NAME);

            } catch (CantCreateDatabaseException cantOpenDatabaseException) {
                /*
                 * The database cannot be created. I can not handle this situation.
                 */
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantOpenDatabaseException);
                throw new CantInitializeTemplateNetworkServiceDatabaseException(cantOpenDatabaseException.getLocalizedMessage());
            }
        }
    }

    /*
     * get a string and convert it into array bytes
     */
    private static byte[] convertoByteArrayfromString(String arrengebytes) {

        if (arrengebytes != null) {

            try {

                String[] byteValues = arrengebytes.substring(1, arrengebytes.length() - 1).split(",");
                byte[] bytes = new byte[byteValues.length];
                if (bytes.length > 0) {
                    for (int i = 0, len = bytes.length; i < len; i++) {
                        bytes[i] = Byte.parseByte(byteValues[i].trim());
                    }
                    return bytes;
                } else {
                    return new byte[]{};
                }
            } catch (Exception e) {
                return new byte[]{};
            }
        } else {
            return new byte[]{};
        }
    }

    private PlatformComponentProfile constructPlatformComponentProfile(ActorAssetRedeemPoint redeemPoint, CommunicationsClientConnection communicationsClientConnection) {
   /*
    * Construct the profile
    */
        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DAP_IMG_REDEEM_POINT, Base64.encodeToString(redeemPoint.getProfileImage(), Base64.DEFAULT));
        jsonObject.addProperty(DAP_REGISTERED_ISSUERS, gson.toJson(redeemPoint.getRegisteredIssuers()));

        String extraData = gson.toJson(jsonObject);

        return communicationsClientConnection.constructPlatformComponentProfileFactory(
                redeemPoint.getActorPublicKey(),
                redeemPoint.getName().toLowerCase().trim(),
                redeemPoint.getName(),
                NetworkServiceType.UNDEFINED,
                PlatformComponentType.ACTOR_ASSET_REDEEM_POINT,
                extraData);
    }

    /**
     * This method validate is all required resource are injected into
     * the plugin root by the platform
     *
     * @throws CantStartPluginException
     */
    private void validateInjectedResources() throws CantStartPluginException {

         /*
         * If all resources are inject
         */
        if (wsCommunicationsCloudClientManager == null ||
                pluginDatabaseSystem == null ||
                errorManager == null ||
                eventManager == null) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "No all required resource are injected";
            CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);

            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_REDEEM_POINT_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
            throw pluginStartException;
        }
    }

    @Override
    public void handleNewMessages(FermatMessage fermatMessage) {

        try {

            Gson gson = new Gson();
            String jsonMessage = fermatMessage.getContent();
            NetworkServiceMessage networkServiceDaoMessage = gson.fromJson(jsonMessage, NetworkServiceMessage.class);

            switch (networkServiceDaoMessage.getMessageType()) {

                case ACCEPT:
                    NetworkServiceMessageAccept acceptMessage = gson.fromJson(jsonMessage, NetworkServiceMessageAccept.class);
//                    receiveAcceptance(acceptMessage);
                    break;

                case DENY:
                    NetworkServiceMessageDeny denyMessage = gson.fromJson(jsonMessage, NetworkServiceMessageDeny.class);
//                    receiveDenial(denyMessage);
                    break;

                case REQUEST:
                    // update the request to processing receive state with the given action.
                    NetworkServiceMessageRequest requestMessage = gson.fromJson(jsonMessage, NetworkServiceMessageRequest.class);
//                    receiveRequest(requestMessage);
                    break;

                default:
                    throw new CantHandleDapNewMessagesException(
                            "message type: " + networkServiceDaoMessage.getMessageType().name(),
                            "Message type not handled."
                    );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleNewSentMessageNotificationEvent(FermatMessage data) {

    }

    /**
     * Initialize the event listener and configure
     */
    private void initializeListener() {
        /*
         * Listen and handle Complete Component Registration Notification Event
         */
        FermatEventListener fermatEventListener = eventManager.getNewListener(P2pEventType.COMPLETE_COMPONENT_REGISTRATION_NOTIFICATION);
        fermatEventListener.setEventHandler(new CompleteComponentRegistrationNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        /*
         * Listen and handle Complete Request List Component Registered Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.COMPLETE_REQUEST_LIST_COMPONENT_REGISTERED_NOTIFICATION);
        fermatEventListener.setEventHandler(new CompleteRequestListComponentRegisteredNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        /*
         * Listen and handle Complete Request List Component Registered Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.COMPLETE_COMPONENT_CONNECTION_REQUEST_NOTIFICATION);
        fermatEventListener.setEventHandler(new CompleteComponentConnectionRequestNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);


        /*
         * Listen and handle new message sent
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.NEW_NETWORK_SERVICE_MESSAGE_SENT_NOTIFICATION);
        fermatEventListener.setEventHandler(new NewSentMessagesNotificationEventHandler());
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        /*
         * Listen and handle New Message Receive Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.NEW_NETWORK_SERVICE_MESSAGE_RECEIVE_NOTIFICATION);
        if (communicationNetworkServiceConnectionManager == null)
            initializeCommunicationNetworkServiceConnectionManager();
        fermatEventListener.setEventHandler(new NewReceiveMessagesNotificationEventHandler(communicationNetworkServiceConnectionManager, eventManager));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        /*
         * Listen and handle VPN Connection Close Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.VPN_CONNECTION_CLOSE);
        fermatEventListener.setEventHandler(new VPNConnectionCloseNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        /*
         * Listen and handle Client Connection Close Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.CLIENT_CONNECTION_CLOSE);
        fermatEventListener.setEventHandler(new ClientConnectionCloseNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

          /*
         * Listen and handle Client Connection Loose Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.CLIENT_CONNECTION_LOOSE);
        fermatEventListener.setEventHandler(new ClientConnectionLooseNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);


        /*
         * Listen and handle Client Connection Success Reconnect Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.CLIENT_SUCCESS_RECONNECT);
        fermatEventListener.setEventHandler(new ClientSuccessfullReconnectNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);
    }
}