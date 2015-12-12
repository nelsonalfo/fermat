package com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.all_definition.network_service.interfaces.NetworkServiceLocal;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransmissionState;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventType;
import com.bitdubai.fermat_cbp_api.layer.network_service.NegotiationTransmission.events.IncomingNegotiationTransmissionUpdateEvent;
import com.bitdubai.fermat_cbp_api.layer.network_service.NegotiationTransmission.interfaces.NegotiationTransmission;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.NetworkServiceNegotiationTransmissionPluginRoot;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.communication.structure.CommunicationNetworkServiceConnectionManager;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.database.NegotiationTransmissionNetworkServiceDatabaseConstants;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.database.NegotiationTransmissionNetworkServiceConnectionsDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.database.NegotiationTransmissionNetworkServiceDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.exceptions.CantInitializeDatabaseException;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.exceptions.CantRegisterSendNegotiationTransmissionException;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.messages.NegotiationTransmissionResponseMessage;
import com.bitdubai.fermat_p2p_api.layer.all_definition.common.network_services.template.communications.CommunicationNetworkServiceLocal;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.WsCommunicationsCloudClientManager;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.contents.FermatMessage;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.exceptions.CantEstablishConnectionException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yordin Alayn on 30.11.15.
 */
public class NegotiationTransmissionAgent {

    //Represent the sleep time for the read or send (2000 milliseconds)
    private static final long SLEEP_TIME = 15000;
    private static final long RECEIVE_SLEEP_TIME = 15000;

    //DealsWithErrors Interface member variables.
    private ErrorManager errorManager;

    //Represent is the tread is running
    private Boolean running;

    //Represent the identity
    private ECCKeyPair identity;

    //Communication Service, Class to send the message
    //CryptoTransmissionNetworkServiceLocal communicationNetworkServiceLocal;

    //Communication manager, Class to obtain the connections
    CommunicationNetworkServiceConnectionManager communicationNetworkServiceConnectionManager;

    //Communication Cloud Client manager
    WsCommunicationsCloudClientManager wsCommunicationsCloudClientManager;

    //plugin root
    NetworkServiceNegotiationTransmissionPluginRoot networkServiceNegotiationTransmissionPluginRoot;

    //DAO NegotiationTransmission
    NegotiationTransmissionNetworkServiceConnectionsDatabaseDao databaseConnectionsDao;
    NegotiationTransmissionNetworkServiceDatabaseDao databaseDao;

    //Represent the remoteNetworkServicesRegisteredList
    private List<PlatformComponentProfile> remoteNetworkServicesRegisteredList;

    //PlatformComponentProfile platformComponentProfile
    PlatformComponentProfile platformComponentProfile;

    //Represent the send cycle tread of this NetworkService
    private Thread toSend;

    //Represent the send messages tread of this TemplateNetworkServiceRemoteAgent
    private Thread toReceive;

    //Cache de metadata con conexions leidas anteriormente. ActorPublicKey, metadata de respuesta
    Map<String, NegotiationTransmissionState> cacheResponseMetadataFromRemotes;

    //Map contains publicKey from componentProfile to connect and the number of connections intents.
    Map<String,Integer> connectionsCounters;

    //Cola de espera, a la cual van pasando las conecciones que no se pudieron conectar para que se hagan con más tiempo y no saturen el server
    Map<String, NegotiationTransmissionPlatformComponentProfilePlusWaitTime> waitingPlatformComponentProfile;

    //Pool connections requested waiting for peer or server response. PublicKey  and transaccion metadata waiting to be a response
    Map<String,NegotiationTransmission> poolConnectionsWaitingForResponse;

    //
    Map<String, FermatMessage> receiveMessage;
    private boolean flag=true;


    private EventManager eventManager;

    //Constructor
    public NegotiationTransmissionAgent(
            NetworkServiceNegotiationTransmissionPluginRoot networkServiceNegotiationTransmissionPluginRoot,
            NegotiationTransmissionNetworkServiceConnectionsDatabaseDao databaseConnectionsDao,
            NegotiationTransmissionNetworkServiceDatabaseDao databaseDao,
            CommunicationNetworkServiceConnectionManager communicationNetworkServiceConnectionManager,
            WsCommunicationsCloudClientManager wsCommunicationsCloudClientManager,
            PlatformComponentProfile platformComponentProfile,
            ErrorManager errorManager,
            List<PlatformComponentProfile> remoteNetworkServicesRegisteredList,
            ECCKeyPair identity,
            EventManager eventManager
    ){

        this.networkServiceNegotiationTransmissionPluginRoot = networkServiceNegotiationTransmissionPluginRoot;
        this.databaseConnectionsDao = databaseConnectionsDao;
        this.databaseDao = databaseDao;
        this.communicationNetworkServiceConnectionManager = communicationNetworkServiceConnectionManager;
        this.wsCommunicationsCloudClientManager =wsCommunicationsCloudClientManager;
        this.errorManager = errorManager;
        this.remoteNetworkServicesRegisteredList = remoteNetworkServicesRegisteredList;
        this.identity = identity;
        this.platformComponentProfile = platformComponentProfile;
        this.eventManager = eventManager;


        cacheResponseMetadataFromRemotes = new HashMap<String, NegotiationTransmissionState>();
        waitingPlatformComponentProfile = new HashMap<>();


        poolConnectionsWaitingForResponse = new HashMap<> ();

        //Create a thread to send the messages
        this.toSend = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running)
                    sendCycle();
            }
        });

        //Create a thread to receive the messages
        this.toReceive = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running)
                    receiveCycle();
            }
        });

    }

    /*SERVICE*/
    public void start(){
        //Set to running
        this.running  = Boolean.TRUE;
        System.out.println("NEGOTIATION TRANSMISSION AGEN");
        try {
            databaseDao.initialize();
        } catch (CantInitializeDatabaseException e) {
            e.printStackTrace();
            System.out.println("ERROR NEGOTIATION TRANSMISSION AGEN, DATABASE DAO NOT STARTED");
        }

        //Start the Thread
        toSend.start();
        toReceive.start();

        System.out.println("NegotiationTransmissionAgent - started ");
    }

    public void pause(){
        this.running  = Boolean.FALSE;
    }

    public void resume(){
        this.running  = Boolean.TRUE;
    }

    public void stop(){
        //Stop the Thread
        toSend.interrupt();
        toReceive.interrupt();
        this.running  = Boolean.FALSE;
        //Disconnect from the service
    }
    /*END SERVICE*/

    /*PUBLIC*/
    private void processMetadata() {

        try {
            Map<String, Object> filters = new HashMap<>();
//            filters.put(NegotiationTransmissionNetworkServiceDatabaseConstants.NEGOTIATION_TRANSMISSION_NETWORK_SERVICE_TRANSMISSION_ID_COLUMN_NAME, "23");
            filters.put(NegotiationTransmissionNetworkServiceDatabaseConstants.NEGOTIATION_TRANSMISSION_NETWORK_SERVICE_TRANSMISSION_STATE_COLUMN_NAME, NegotiationTransmissionState.PROCESSING_SEND.getCode());

            //Read all pending NegotiationTransmission from database
            List<NegotiationTransmission> negotiationTransmissionList = databaseDao.findAll(filters);

            for (NegotiationTransmission negotiationTransmission : negotiationTransmissionList) {
                String receiverPublicKey= negotiationTransmission.getPublicKeyActorReceive();
                if(!poolConnectionsWaitingForResponse.containsKey(receiverPublicKey)) {
                    //TODO: hacer un filtro por aquellas que se encuentran conectadas
                    if (communicationNetworkServiceConnectionManager.getNetworkServiceLocalInstance(receiverPublicKey) == null) {
                        if (wsCommunicationsCloudClientManager != null) {
                            if (platformComponentProfile != null) {
                                PlatformComponentProfile applicantParticipant = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructBasicPlatformComponentProfileFactory(negotiationTransmission.getPublicKeyActorSend(), NetworkServiceType.UNDEFINED, PlatformComponentType.ACTOR_INTRA_USER);
                                PlatformComponentProfile remoteParticipant = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructBasicPlatformComponentProfileFactory(negotiationTransmission.getPublicKeyActorReceive(), NetworkServiceType.UNDEFINED, PlatformComponentType.ACTOR_INTRA_USER);
                                communicationNetworkServiceConnectionManager.connectTo(applicantParticipant, platformComponentProfile, remoteParticipant);
                                // pass the negotiationTransmission to a pool waiting for the response of the other peer or server failure
                                poolConnectionsWaitingForResponse.put(receiverPublicKey, negotiationTransmission);
                            }
                        }
                    }
                }else{
                    NetworkServiceLocal communicationNetworkServiceLocal = networkServiceNegotiationTransmissionPluginRoot.getNetworkServiceConnectionManager().getNetworkServiceLocalInstance(receiverPublicKey);
                    if (communicationNetworkServiceLocal != null) {
                        try {
                            negotiationTransmission.setTransmissionState(NegotiationTransmissionState.DONE);
                            System.out.print("-----------------------\n" +
                                    "SENDING NEGOTIATION TRANSACTION RECORD -----------------------\n" +
                                    "-----------------------\n To: " + receiverPublicKey);

                            // Si se encuentra conectado paso la metadata al dao de la capa de comunicacion para que lo envie
                            Gson gson = new Gson();
                            String jsonBusinessTransaction =gson.toJson(negotiationTransmission);
                            // Envio el mensaje a la capa de comunicacion
                            communicationNetworkServiceLocal.sendMessage(identity.getPublicKey(),receiverPublicKey,jsonBusinessTransaction);
                            databaseDao.changeState(negotiationTransmission);
                            System.out.print("-----------------------\n" +
                                    "NEGOTIATION TRANSACTION -----------------------\n" +
                                    "-----------------------\n STATE: " + negotiationTransmission.getTransmissionState());
                        } catch (CantRegisterSendNegotiationTransmissionException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.exceptions.CantReadRecordDataBaseException e) {
            e.printStackTrace();
        } catch (CantEstablishConnectionException e) {
            e.printStackTrace();
        }
    }

    public void addRemoteNetworkServicesRegisteredList(List<PlatformComponentProfile> list){
        remoteNetworkServicesRegisteredList = list;
    }

    public void setPlatformComponentProfile(PlatformComponentProfile platformComponentProfile){
        this.platformComponentProfile = platformComponentProfile;
    }

    public void connectionFailure(String identityPublicKey){
        this.poolConnectionsWaitingForResponse.remove(identityPublicKey);
    }

    public boolean isConnection(String publicKey){
        return this.poolConnectionsWaitingForResponse.containsKey(publicKey);
    }

    public boolean isRunning(){
        return running;
    }

    public void sendCycle(){
        try {

            if(networkServiceNegotiationTransmissionPluginRoot.isRegister()) {
                // function to process and send metadata to remote
                processMetadata();
                //Discount from the the waiting list
                discountWaitTime();
            }
            //Sleep for a time
            toSend.sleep(SLEEP_TIME);

        } catch (InterruptedException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_TEMPLATE_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, new Exception("Can not sleep"));
        }
    }
    /*END PUBLIC*/

    /*PRIVATE*/
    private void processReceive() {

        try {
            //communicationNetworkServiceConnectionManager.
            Map<String, Object> filters = new HashMap<>();
            filters.put(NegotiationTransmissionNetworkServiceDatabaseConstants.NEGOTIATION_TRANSMISSION_NETWORK_SERVICE_PENDING_FLAG_COLUMN_NAME, "false");

            //Read all pending CryptoTransmissionMetadata from database
            List<NegotiationTransmission> negotiationTransmissionList = databaseDao.findAll(filters);
            for(NegotiationTransmission negotiationTransmission : negotiationTransmissionList){
                CommunicationNetworkServiceLocal communicationNetworkServiceLocal = communicationNetworkServiceConnectionManager.getNetworkServiceLocalInstance(negotiationTransmission.getPublicKeyActorSend());
                if(communicationNetworkServiceLocal!=null){
                    System.out.print("-----------------------\n" +
                            "RECEIVING NEGOTIATION TRANSACTION-----------------------\n" +
                            "-----------------------\n STATE: " + negotiationTransmission.getTransmissionState());
                    // si no contiene la metadata, la tengo que guardar en la bd y notificar que llegó, tambien debería cargar ese caché cuando se lanza el evento de que llega la metadata de respuesta
                    // if( ! cacheResponseMetadataFromRemotes.containsKey(cryptoTransmissionMetadata.getDestinationPublicKey())){
                    try {
                        switch (negotiationTransmission.getTransmissionState()) {
                            case SEEN_BY_DESTINATION_NETWORK_SERVICE:

                                System.out.println("Negotiation Transmission SEEN_BY_DESTINATION_NETWORK_SERVICE---to implement");
                                break;

                            case CONFIRM_RESPONSE:

                                System.out.print(negotiationTransmission.getPublicKeyActorSend()+" Negotiation Transmission CONFIRM_RESPONSE");
                                launchNotification();
                                this.poolConnectionsWaitingForResponse.remove(negotiationTransmission.getPublicKeyActorReceive());
                                break;

                            case CONFIRM_NEGOTIATION:

                                System.out.print(negotiationTransmission.getPublicKeyActorSend()+" Negotiation Transmission CONFIRM_NEGOTIATION");
                                //this.poolConnectionsWaitingForResponse.remove(negotiationTransmission.getReceiverId());
                                launchNotification();
                                this.poolConnectionsWaitingForResponse.remove(negotiationTransmission.getPublicKeyActorReceive());
                                break;

                            // si el mensaje viene con un estado de SENT es porque es la primera vez que llega, por lo que tengo que guardarlo en la bd y responder
                            case SENT:

                                negotiationTransmission.setTransmissionState(NegotiationTransmissionState.SEEN_BY_OWN_NETWORK_SERVICE);
                                negotiationTransmission.setTransmissionType(negotiationTransmission.getTransmissionType());
                                databaseDao.updateRegisterSendNegotiatioTransmission(negotiationTransmission);

                                System.out.print("-----------------------\n" +
                                        "RECEIVING NEGOTIATION TRANSACTION -----------------------\n" +
                                        "-----------------------\n STATE: " + negotiationTransmission.getPublicKeyActorSend());

                                launchNotification();

                                NegotiationTransmissionResponseMessage transmissionResponseMessage = new NegotiationTransmissionResponseMessage(
                                        negotiationTransmission.getTransactionId(),
                                        NegotiationTransmissionState.SEEN_BY_DESTINATION_NETWORK_SERVICE,
                                        negotiationTransmission.getNegotiationTransactionType());

                                Gson gson = new Gson();
                                String message = gson.toJson(transmissionResponseMessage);

                                // El destination soy yo porque me lo estan enviando
                                // El sender es el otro y es a quien le voy a responder
                                communicationNetworkServiceLocal.sendMessage(negotiationTransmission.getPublicKeyActorReceive(), negotiationTransmission.getPublicKeyActorSend(), message);
                                System.out.print("-----------------------\n" +
                                        "SENDING ANSWER -----------------------\n" +
                                        "-----------------------\n STATE: " + negotiationTransmission.getTransmissionState());
                                break;

                            default:
                                //TODO: handle with an exception
                                break;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.exceptions.CantReadRecordDataBaseException e) {
            e.printStackTrace();
        }
    }

    private void launchNotification(){
        FermatEvent fermatEvent = eventManager.getNewEvent(EventType.INCOMING_NEGOTIATION_TRANSMISSION_UPDATE);
        IncomingNegotiationTransmissionUpdateEvent incomingNegotiationTransmissionUpdateEvent = (IncomingNegotiationTransmissionUpdateEvent) fermatEvent;
        incomingNegotiationTransmissionUpdateEvent.setSource(EventSource.NETWORK_SERVICE_NEGOTIATION_TRANSMISSION);
        eventManager.raiseEvent(incomingNegotiationTransmissionUpdateEvent);
    }

    private void discountWaitTime(){
        if(!waitingPlatformComponentProfile.isEmpty()) {
            for (NegotiationTransmissionPlatformComponentProfilePlusWaitTime negotiationTransmissionPlatformComponentProfilePlusWaitTime : waitingPlatformComponentProfile.values()) {
                negotiationTransmissionPlatformComponentProfilePlusWaitTime.WaitTimeDown();
                if (negotiationTransmissionPlatformComponentProfilePlusWaitTime.getWaitTime() <= 0) {
                    remoteNetworkServicesRegisteredList.add(negotiationTransmissionPlatformComponentProfilePlusWaitTime.getPlatformComponentProfile());
                    waitingPlatformComponentProfile.remove(negotiationTransmissionPlatformComponentProfilePlusWaitTime);
                }
            }
        }
    }

    private void receiveCycle(){
        try {
            // function to process metadata received
            processReceive();
            //Sleep for a time
            toSend.sleep(RECEIVE_SLEEP_TIME);

        } catch (InterruptedException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_TEMPLATE_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, new Exception("Can not sleep"));
        }
    }
    /*PRIVATE*/
}
