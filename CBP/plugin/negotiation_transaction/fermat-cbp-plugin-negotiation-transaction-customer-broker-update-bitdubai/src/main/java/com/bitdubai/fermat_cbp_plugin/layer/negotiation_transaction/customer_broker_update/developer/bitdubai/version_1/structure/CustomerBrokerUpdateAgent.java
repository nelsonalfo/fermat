package com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.DealsWithPluginIdentity;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Specialist;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.util.XMLParser;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.DealsWithLogger;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_cbp_api.all_definition.agent.CBPTransactionAgent;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransactionStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransactionType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationType;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventStatus;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventType;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.CantInitializeCBPAgent;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.UnexpectedResultReturnedFromDatabaseException;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation_transaction.NegotiationSaleRecord;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation_transaction.NegotiationPurchaseRecord;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation_transaction.NegotiationTransaction;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation_transaction.customer_broker_update.events.NewNegotiationTransactionUpdateEvent;
import com.bitdubai.fermat_cbp_api.layer.negotiation_transaction.customer_broker_update.interfaces.CustomerBrokerUpdate;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendConfirmToCryptoBrokerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendConfirmToCryptoCustomerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendNegotiationToCryptoBrokerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendNegotiationToCryptoCustomerException;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.interfaces.NegotiationTransmission;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.interfaces.NegotiationTransmissionManager;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.NegotiationTransactionCustomerBrokerUpdatePluginRoot;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.database.CustomerBrokerUpdateNegotiationTransactionDatabaseConstants;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.database.CustomerBrokerUpdateNegotiationTransactionDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.database.CustomerBrokerUpdateNegotiationTransactionDatabaseFactory;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantGetNegotiationTransactionListException;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.version_1.exceptions.CantSendCustomerBrokerUpdateNegotiationTransactionException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.DealsWithEvents;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

import java.util.List;
import java.util.UUID;

/**
 * Created by Yordin Alayn on 16.12.15.
 */
public class CustomerBrokerUpdateAgent implements
        CBPTransactionAgent,
        DealsWithLogger,
        DealsWithEvents,
        DealsWithErrors,
        DealsWithPluginDatabaseSystem,
        DealsWithPluginIdentity {

    private Database                                    database;

    private Thread                                      agentThread;

    private LogManager                                  logManager;

    private EventManager                                eventManager;

    private ErrorManager                                errorManager;

    private PluginDatabaseSystem                        pluginDatabaseSystem;

    private UUID                                        pluginId;

    /*Represent the Network Service*/
    private NegotiationTransmissionManager              negotiationTransmissionManager;

    /*Represent the Negotiation Purchase*/
    private CustomerBrokerPurchaseNegotiation           customerBrokerPurchaseNegotiation;

    /*Represent the Negotiation Purchase*/
    private CustomerBrokerPurchaseNegotiationManager    customerBrokerPurchaseNegotiationManager;

    /*Represent the Negotiation Sale*/
    private CustomerBrokerSaleNegotiation               customerBrokerSaleNegotiation;

    /*Represent the Negotiation Sale*/
    private CustomerBrokerSaleNegotiationManager        customerBrokerSaleNegotiationManager;

    /*Represent the Monitor Agent*/
    private MonitorAgentTransaction                     monitorAgentTransaction;

    public CustomerBrokerUpdateAgent(
        PluginDatabaseSystem                        pluginDatabaseSystem,
        LogManager                                  logManager,
        ErrorManager                                errorManager,
        EventManager                                eventManager,
        UUID                                        pluginId,
        NegotiationTransmissionManager              negotiationTransmissionManager,
        CustomerBrokerPurchaseNegotiation           customerBrokerPurchaseNegotiation,
        CustomerBrokerSaleNegotiation               customerBrokerSaleNegotiation,
        CustomerBrokerPurchaseNegotiationManager    customerBrokerPurchaseNegotiationManager,
        CustomerBrokerSaleNegotiationManager        customerBrokerSaleNegotiationManager
    ){
        this.pluginDatabaseSystem                       = pluginDatabaseSystem;
        this.logManager                                 = logManager;
        this.errorManager                               = errorManager;
        this.eventManager                               = eventManager;
        this.pluginId                                   = pluginId;
        this.negotiationTransmissionManager             = negotiationTransmissionManager;
        this.customerBrokerPurchaseNegotiation          = customerBrokerPurchaseNegotiation;
        this.customerBrokerSaleNegotiation              = customerBrokerSaleNegotiation;
        this.customerBrokerPurchaseNegotiationManager   = customerBrokerPurchaseNegotiationManager;
        this.customerBrokerSaleNegotiationManager       = customerBrokerSaleNegotiationManager;
    }

    /*IMPLEMENTATION CBPTransactionAgent*/
    @Override
    public void start() throws CantStartAgentException {

//        Logger LOG = Logger.getGlobal();
//        LOG.info("CUSTMER BROKER NEW AGENT STARTING...");
        monitorAgentTransaction = new MonitorAgentTransaction();

        this.monitorAgentTransaction.setPluginDatabaseSystem(this.pluginDatabaseSystem);
        this.monitorAgentTransaction.setErrorManager(this.errorManager);

        try {
            this.monitorAgentTransaction.Initialize();
        } catch (Exception exception) {
            errorManager.reportUnexpectedPluginException(Plugins.CUSTOMER_BROKER_UPDATE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, exception);
        }

        this.agentThread = new Thread(monitorAgentTransaction);
        this.agentThread.start();
//        System.out.print("-----------------------\n CUSTOMER BROKER UPDATE AGENT: SUCCESSFUL START \n-----------------------\n");
    }

    @Override
    public void stop() { this.agentThread.interrupt(); }

    /*IMPLEMENTATION DealsWithErrors*/
    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager=errorManager;
    }

    /*IMPLEMENTATION DealsWithEvents*/
    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager=eventManager;
    }

    /*IMPLEMENTATION DealsWithLogger*/
    @Override
    public void setLogManager(LogManager logManager) {
        this.logManager=logManager;
    }

    /*IMPLEMENTATION DealsWithPluginDatabaseSystem*/
    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) { this.pluginDatabaseSystem=pluginDatabaseSystem; }

    /*IMPLEMENTATION DealsWithPluginIdentity*/
    @Override
    public void setPluginId(UUID pluginId) {
        this.pluginId=pluginId;
    }

    /*INNER CLASS*/
    private class MonitorAgentTransaction implements DealsWithPluginDatabaseSystem, DealsWithErrors, Runnable {

        private CustomerBrokerUpdateSaleNegotiationTransaction       customerBrokerUpdateSaleNegotiationTransaction;

        private CustomerBrokerUpdatePurchaseNegotiationTransaction   customerBrokerUpdatePurchaseNegotiationTransaction;

        private volatile boolean                                    agentRunning;

        ErrorManager                                                errorManager;

        PluginDatabaseSystem                                        pluginDatabaseSystem;

        CustomerBrokerUpdateNegotiationTransactionDatabaseDao       customerBrokerUpdateNegotiationTransactionDatabaseDao;

        boolean                                                     threadWorking;

        public final int                                            SLEEP_TIME = 5000;

        int                                                         iterationNumber = 0;

        /*IMPLEMENTATION DealsWithPluginIdentity*/
        @Override
        public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) { this.pluginDatabaseSystem=pluginDatabaseSystem; }

        /*IMPLEMENTATION DealsWithErrors*/
        @Override
        public void setErrorManager(ErrorManager errorManager) {
            this.errorManager=errorManager;
        }

        /*IMPLEMENTATION Runnable*/
        @Override
        public void run() {

            threadWorking=true;
            logManager.log(NegotiationTransactionCustomerBrokerUpdatePluginRoot.getLogLevelByClass(this.getClass().getName()),"Customer Broker Update Monitor Agent: running...", null, null);

            while(threadWorking){
                //Increase the iteration counter
                iterationNumber++;
                try {

                    Thread.sleep(SLEEP_TIME);

                } catch (InterruptedException interruptedException) {
                    return;
                }

                //now I will check if there are pending transactions to raise the event
                try {

                    logManager.log(NegotiationTransactionCustomerBrokerUpdatePluginRoot.getLogLevelByClass(this.getClass().getName()), "Iteration number " + iterationNumber, null, null);
                    doTheMainTask();

                } catch (CantSendCustomerBrokerUpdateNegotiationTransactionException | CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException | CantUpdateRecordException e) {
                    errorManager.reportUnexpectedPluginException(Plugins.CUSTOMER_BROKER_UPDATE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                }
            }
        }

        /*INNER CLASS PUBLIC METHOD*/
        public void Initialize() throws CantInitializeCBPAgent {

            try {

                database = this.pluginDatabaseSystem.openDatabase(pluginId, CustomerBrokerUpdateNegotiationTransactionDatabaseConstants.DATABASE_NAME);

            }
            catch (DatabaseNotFoundException databaseNotFoundException) {

                try {
                    CustomerBrokerUpdateNegotiationTransactionDatabaseFactory databaseFactory = new CustomerBrokerUpdateNegotiationTransactionDatabaseFactory(this.pluginDatabaseSystem);
                    database = databaseFactory.createDatabase(pluginId,CustomerBrokerUpdateNegotiationTransactionDatabaseConstants.DATABASE_NAME);
                } catch (CantCreateDatabaseException cantCreateDatabaseException) {
                    errorManager.reportUnexpectedPluginException(Plugins.CUSTOMER_BROKER_UPDATE,UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,cantCreateDatabaseException);
                    throw new CantInitializeCBPAgent(cantCreateDatabaseException,"Customer Broker Update Initialize Monitor Agent - trying to create the plugin database","Please, check the cause");
                }

            } catch (CantOpenDatabaseException exception) {
                errorManager.reportUnexpectedPluginException(Plugins.CUSTOMER_BROKER_UPDATE,UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,exception);
                throw new CantInitializeCBPAgent(exception,"Customer Broker Update Initialize Monitor Agent - trying to open the plugin database","Please, check the cause");
            }

        }

        public void stopAgent() { agentRunning = false; }

        public void startAgent() { agentRunning = true; }

        public boolean isAgentRunning() { return agentRunning; }
        /*END INNER CLASS PUBLIC METHOD*/

        /*INNER CLASS PRIVATE METHOD*/
        private void doTheMainTask() throws
            CantSendCustomerBrokerUpdateNegotiationTransactionException,
            CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException,
            CantUpdateRecordException
        {

            try {

                customerBrokerUpdateNegotiationTransactionDatabaseDao = new CustomerBrokerUpdateNegotiationTransactionDatabaseDao(pluginDatabaseSystem, pluginId, database);

                String negotiationXML;
                NegotiationType negotiationType;
                UUID transactionId;
                List<CustomerBrokerUpdate> negotiationPendingToSubmitList;
                CustomerBrokerPurchaseNegotiation purchaseNegotiation = new NegotiationPurchaseRecord();
                CustomerBrokerSaleNegotiation saleNegotiation = new NegotiationSaleRecord();

                //SEND NEGOTIATION PENDING (CUSTOMER_BROKER_NEW_STATUS_NEGOTIATION_COLUMN_NAME = NegotiationTransactionStatus.PENDING_SUBMIT)
                negotiationPendingToSubmitList = customerBrokerUpdateNegotiationTransactionDatabaseDao.getPendingToSubmitNegotiation();
                if (!negotiationPendingToSubmitList.isEmpty()) {
                    for (CustomerBrokerUpdate negotiationTransaction : negotiationPendingToSubmitList) {

                        System.out.print("\n\n**** 5) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - NEGOTIATION FOR SEND transactionId: " + negotiationTransaction.getTransactionId() + " ****\n");

                        negotiationXML  = negotiationTransaction.getNegotiationXML();
                        negotiationType = negotiationTransaction.getNegotiationType();
                        transactionId   = negotiationTransaction.getTransactionId();

                        switch (negotiationType) {
                            case PURCHASE:
                                purchaseNegotiation = (CustomerBrokerPurchaseNegotiation) XMLParser.parseXML(negotiationXML, purchaseNegotiation);
                                System.out.print("\n\n**** 6) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - PURCHASE NEGOTIATION SEND negotiationId(XML): " + purchaseNegotiation.getNegotiationId() + " ****\n" +
                                        "\n - Status :"+purchaseNegotiation.getStatus().getCode());
                                //SEND NEGOTIATION TO BROKER
                                negotiationTransmissionManager.sendNegotiatioToCryptoBroker(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);
                                //CHANGE STATUS PURCHASE NEGOTIATION. SEND_TO_BROKER: send negotiation to broker, waiting confirm
//                                customerBrokerPurchaseNegotiationManager.waitForBroker(purchaseNegotiation);
                                break;

                            case SALE:
                                saleNegotiation = (CustomerBrokerSaleNegotiation) XMLParser.parseXML(negotiationXML, saleNegotiation);
                                System.out.print("\n\n**** 6) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - SALE NEGOTIATION SEND negotiationId(XML): " + saleNegotiation.getNegotiationId() + " ****\n" +
                                        "\n - Status :"+saleNegotiation.getStatus().getCode());
                                //SEND NEGOTIATION TO CUSTOMER
                                negotiationTransmissionManager.sendNegotiatioToCryptoCustomer(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);
                                //CHANGE STATUS PURCHASE NEGOTIATION. SEND_TO_CUSTOMER: send negotiation to customer, waiting confirm
//                                customerBrokerSaleNegotiationManager.waitForBroker(saleNegotiation);
                                break;
                        }

                        //Update the Negotiation Transaction
//                        System.out.print("\n\n**** 7) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE STATUS SALE NEGOTIATION STATUS : " + NegotiationTransactionStatus.SENDING_NEGOTIATION.getCode() + " ****\n");
                        customerBrokerUpdateNegotiationTransactionDatabaseDao.updateStatusRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId, NegotiationTransactionStatus.SENDING_NEGOTIATION);

                    }
                }

                //SEND CONFIRM PENDING (CUSTOMER_BROKER_NEW_STATUS_NEGOTIATION_COLUMN_NAME = NegotiationTransactionStatus.PENDING_CONFIRMATION)
                negotiationPendingToSubmitList = customerBrokerUpdateNegotiationTransactionDatabaseDao.getPendingToConfirmtNegotiation();
                if (!negotiationPendingToSubmitList.isEmpty()) {
                    for (CustomerBrokerUpdate negotiationTransaction : negotiationPendingToSubmitList) {

//                        System.out.print("\n\n**** 22) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CONFIRMATION FOR SEND transactionId: " + negotiationToSubmit.getTransactionId() + " ****\n");

                        transactionId   = negotiationTransaction.getTransactionId();
                        negotiationType = negotiationTransaction.getNegotiationType();

                        switch (negotiationType) {
                            case PURCHASE:
                                System.out.print("\n**** 23) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CONFIRMATION SEND PURCHASE NEGOTIATION negotiationId(XML): " + negotiationTransaction.getTransactionId() + " ****\n");
                                //SEND CONFIRM NEGOTIATION TO BROKER
                                negotiationTransmissionManager.sendConfirmNegotiatioToCryptoBroker(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);
                                break;
                            case SALE:
                                System.out.print("\n**** 23) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CONFIRMATION SEND SALE NEGOTIATION negotiationId(XML): " + negotiationTransaction.getTransactionId() + " ****\n");
                                //SEND NEGOTIATION TO CUSTOMER
                                negotiationTransmissionManager.sendConfirmNegotiatioToCryptoCustomer(negotiationTransaction, NegotiationTransactionType.CUSTOMER_BROKER_UPDATE);
                                break;
                        }

                        //Update the Negotiation Transaction
                        customerBrokerUpdateNegotiationTransactionDatabaseDao.updateStatusRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId, NegotiationTransactionStatus.CONFIRM_NEGOTIATION);
                    }
                }

                //Check if pending events
                List<UUID> pendingEventsIdList = customerBrokerUpdateNegotiationTransactionDatabaseDao.getPendingEvents();
                for (UUID eventId : pendingEventsIdList) {
                    checkPendingEvent(eventId);
                }

            } catch (CantSendNegotiationToCryptoBrokerException e) {
                throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(CantSendCustomerBrokerUpdateNegotiationTransactionException.DEFAULT_MESSAGE, e,"Sending Purchase Negotiation","Error in Negotiation Transmission Network Service");
            } catch (CantSendNegotiationToCryptoCustomerException e) {
                throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(CantSendCustomerBrokerUpdateNegotiationTransactionException.DEFAULT_MESSAGE, e,"Sending Sale Negotiation","Error in Negotiation Transmission Network Service");
            } catch (CantSendConfirmToCryptoBrokerException e) {
                throw new CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException(CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException.DEFAULT_MESSAGE, e,"Sending Confirm Purchase Negotiation", "Error in Negotiation Transmission Network Service");
            } catch (CantSendConfirmToCryptoCustomerException e){
                throw new CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException(CantSendCustomerBrokerUpdateConfirmationNegotiationTransactionException.DEFAULT_MESSAGE, e,"Sending Confirm Sale Negotiation", "Error in Negotiation Transmission Network Service");
            } catch (CantGetNegotiationTransactionListException e) {
                throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(e.getMessage(), FermatException.wrapException(e),"Sending Negotiation","Cannot get the Negotiation list from database");
            } catch (Exception e) {
                throw new CantSendCustomerBrokerUpdateNegotiationTransactionException(e.getMessage(), FermatException.wrapException(e),"Sending Negotiation","UNKNOWN FAILURE.");
            }

        }

        //CHECK PENDING EVEN
        private void checkPendingEvent(UUID eventId) throws UnexpectedResultReturnedFromDatabaseException {

            try {
                UUID                                transactionId;
                UUID                                transmissionId;
                NegotiationTransmission negotiationTransmission;
                NegotiationTransaction              negotiationTransaction;
                NegotiationType                     negotiationType;
                String                              negotiationXML;
                CustomerBrokerPurchaseNegotiation   purchaseNegotiation = new NegotiationPurchaseRecord();
                CustomerBrokerSaleNegotiation       saleNegotiation     = new NegotiationSaleRecord();

                String eventTypeCode = customerBrokerUpdateNegotiationTransactionDatabaseDao.getEventType(eventId);

                //EVENT - RECEIVE NEGOTIATION
                if (eventTypeCode.equals(EventType.INCOMING_NEGOTIATION_TRANSMISSION_TRANSACTION_UPDATE.getCode())) {
                    List<Transaction<NegotiationTransmission>> pendingTransactionList = negotiationTransmissionManager.getPendingTransactions(Specialist.UNKNOWN_SPECIALIST);
                    for (Transaction<NegotiationTransmission> record : pendingTransactionList) {

                        negotiationTransmission = record.getInformation();

                        if(negotiationTransmission.getNegotiationTransactionType().getCode().equals(NegotiationTransactionType.CUSTOMER_BROKER_UPDATE.getCode())) {

                            negotiationXML = negotiationTransmission.getNegotiationXML();
                            transmissionId = negotiationTransmission.getTransmissionId();
                            transactionId = negotiationTransmission.getTransactionId();
                            negotiationType = negotiationTransmission.getNegotiationType();

                            System.out.print("\n**** 18) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - LIST EVENT PENDING ****\n" +
                                            "\n - NEGOTIATION TRANSACTION TYPE = "+negotiationTransmission.getNegotiationTransactionType().getCode()+
                                            "\n - TRANSACTION ID = " +transactionId+
                                            "\n - TRANSMISSION ID = " +transmissionId+
                                            "\n - TRANSMISSION STATE = "+negotiationTransmission.getTransmissionState().getCode()+
                                            "\n - NEGOTIATION TYPE = "+negotiationType+
                                            "\n - NEGOTIATION XML = "+negotiationXML
                            );

                            if (negotiationXML != null) {

                                //TODO FOR TEST
//                                if(customerBrokerUpdateNegotiationTransactionDatabaseDao.getRegisterCustomerBrokerUpdateNegotiationTranasctionFromNegotiationId(negotiationTransmission.getNegotiationId()) == null) {
//                                System.out.print("\n**** 18.2) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - LIST EVENT ID "+ eventId +" PENDING ****\n");
                                    switch (negotiationType) {
                                        case PURCHASE:
                                            System.out.print("\n**** 19) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CREATE PURCHASE NEGOTIATION TRANSACTION  ****\n");
                                            //UPDATE PURCHASE NEGOTIATION
                                            purchaseNegotiation = (CustomerBrokerPurchaseNegotiation) XMLParser.parseXML(negotiationXML, purchaseNegotiation);
                                            customerBrokerUpdatePurchaseNegotiationTransaction = new CustomerBrokerUpdatePurchaseNegotiationTransaction(
                                                    customerBrokerPurchaseNegotiationManager,
                                                    customerBrokerUpdateNegotiationTransactionDatabaseDao

                                            );

                                            if (purchaseNegotiation.getStatus().getCode().equals(NegotiationStatus.CANCELLED.getCode())) {
                                                System.out.print("\n**** 20) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CANCEL PURCHASE NEGOTIATION TRANSACTION  ****\n");
                                                //CANCEL NEGOTIATION
                                                customerBrokerUpdatePurchaseNegotiationTransaction.receiveCancelPurchaseNegotiationTranasction(transactionId, purchaseNegotiation);
                                            } else {
                                                System.out.print("\n**** 20) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE PURCHASE NEGOTIATION TRANSACTION  ****\n");
                                                //UPDATE NEGOTIATION
                                                customerBrokerUpdatePurchaseNegotiationTransaction.receivePurchaseNegotiationTranasction(transactionId, purchaseNegotiation);
                                            }
                                            break;

                                        case SALE:
                                            System.out.print("\n**** 19) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CREATE SALE NEGOTIATION TRANSACTION  ****\n");
                                            //UPDATE SALE NEGOTIATION
                                            saleNegotiation = (CustomerBrokerSaleNegotiation) XMLParser.parseXML(negotiationXML, saleNegotiation);
                                            customerBrokerUpdateSaleNegotiationTransaction = new CustomerBrokerUpdateSaleNegotiationTransaction(
                                                    customerBrokerSaleNegotiationManager,
                                                    customerBrokerUpdateNegotiationTransactionDatabaseDao

                                            );

                                            if (saleNegotiation.getStatus().getCode().equals(NegotiationStatus.CANCELLED.getCode())) {
                                                System.out.print("\n**** 20) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - CANCEL SALE NEGOTIATION TRANSACTION  ****\n");
                                                //CANCEL NEGOTIATION
                                                customerBrokerUpdateSaleNegotiationTransaction.receiveCancelSaleNegotiationTranasction(transactionId, saleNegotiation);
                                            } else {
                                                System.out.print("\n**** 20) MOCK NEGOTIATION TRANSACTION - CUSTOMER BROKER UPDATE - AGENT - UPDATE SALE NEGOTIATION TRANSACTION  ****\n");
                                                //UPDATE NEGOTIATION
                                                customerBrokerUpdateSaleNegotiationTransaction.receiveSaleNegotiationTranasction(transactionId, saleNegotiation);
                                            }
                                            break;
                                    }
//                                }

                                //TODO. UNA EXCEPCION OCURRIDA ANTES DE ESTO, HARA QUE LA TRANSMISSION EN CUESTION SE REPITA EN EL CICLO, POR NO CAMBIAR DE ESTADO.
                                //NOTIFIED EVENT
                                customerBrokerUpdateNegotiationTransactionDatabaseDao.updateEventTansactionStatus(eventId, EventStatus.NOTIFIED);
                                //CONFIRM TRANSMISSION
                                negotiationTransmissionManager.confirmReception(transmissionId);

                            }
                        }
                    }
                }

                //EVENT - RECEIVE CONFIRM NEGOTIATION
                if (eventTypeCode.equals(EventType.INCOMING_NEGOTIATION_TRANSMISSION_CONFIRM_UPDATE.getCode())) {
                    List<Transaction<NegotiationTransmission>> pendingTransactionList = negotiationTransmissionManager.getPendingTransactions(Specialist.UNKNOWN_SPECIALIST);
                    for (Transaction<NegotiationTransmission> record : pendingTransactionList) {

                        negotiationTransmission = record.getInformation();
                        transmissionId          = negotiationTransmission.getTransmissionId();
                        transactionId           = negotiationTransmission.getTransactionId();
                        negotiationTransaction  = customerBrokerUpdateNegotiationTransactionDatabaseDao.getRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId);

                        if (negotiationTransaction.getStatusTransaction().equals(NegotiationTransactionStatus.PENDING_CONFIRMATION.getCode())) {

                            //CONFIRM TRANSACTION
                            customerBrokerUpdateNegotiationTransactionDatabaseDao.updateStatusRegisterCustomerBrokerUpdateNegotiationTranasction(transactionId, NegotiationTransactionStatus.CONFIRM_NEGOTIATION);
                            //NOTIFIED EVENT
                            customerBrokerUpdateNegotiationTransactionDatabaseDao.updateEventTansactionStatus(transactionId, EventStatus.NOTIFIED);
                            //CONFIRM TRANSMISSION
                            negotiationTransmissionManager.confirmReception(transmissionId);

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*END INNER CLASS PRIVATE METHOD*/
    }
    /*END INNER CLASS*/
}
