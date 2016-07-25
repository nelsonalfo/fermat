package com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_ack_online_merchandise.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.EventManager;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Specialist;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.exceptions.CantConfirmTransactionException;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.exceptions.CantDeliverPendingTransactionsException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractTransactionStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.MoneyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.PaymentType;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventStatus;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventType;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.ObjectNotSetException;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.UnexpectedResultReturnedFromDatabaseException;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.Clause;
import com.bitdubai.fermat_cbp_api.all_definition.util.NegotiationClauseHelper;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.events.CustomerAckMerchandiseConfirmed;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.interfaces.AbstractBusinessTransactionAgent;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.interfaces.BusinessTransactionRecord;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.interfaces.IncomingMoneyEventWrapper;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.interfaces.ObjectChecker;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.exceptions.CantGetListCustomerBrokerContractPurchaseException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.exceptions.CantUpdateCustomerBrokerContractPurchaseException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.interfaces.CustomerBrokerContractPurchase;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.interfaces.CustomerBrokerContractPurchaseManager;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.exceptions.CantGetListCustomerBrokerContractSaleException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.exceptions.CantUpdateCustomerBrokerContractSaleException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.interfaces.CustomerBrokerContractSale;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.interfaces.CustomerBrokerContractSaleManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.network_service.transaction_transmission.interfaces.BusinessTransactionMetadata;
import com.bitdubai.fermat_cbp_api.layer.network_service.transaction_transmission.interfaces.TransactionTransmissionManager;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_ack_online_merchandise.developer.bitdubai.version_1.CustomerAckOnlineMerchandisePluginRoot;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_ack_online_merchandise.developer.bitdubai.version_1.database.CustomerAckOnlineMerchandiseBusinessTransactionDao;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_ack_online_merchandise.developer.bitdubai.version_1.exceptions.IncomingOnlineMerchandiseException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 03/07/16.
 */
public class CustomerAckOnlineMerchandiseMonitorAgent2
        extends AbstractBusinessTransactionAgent<CustomerAckOnlineMerchandisePluginRoot> {

    private final CustomerAckOnlineMerchandiseBusinessTransactionDao dao;
    private final TransactionTransmissionManager transactionTransmissionManager;
    private final CustomerBrokerContractPurchaseManager customerBrokerContractPurchaseManager;
    private final CustomerBrokerContractSaleManager customerBrokerContractSaleManager;
    private final CustomerBrokerPurchaseNegotiationManager customerBrokerPurchaseNegotiationManager;

    /**
     * Default constructor with parameters
     *
     * @param sleepTime
     * @param timeUnit
     * @param initDelayTime
     * @param pluginRoot
     * @param eventManager
     * @param dao
     * @param transactionTransmissionManager
     * @param customerBrokerContractPurchaseManager
     * @param customerBrokerContractSaleManager
     * @param customerBrokerPurchaseNegotiationManager
     */
    public CustomerAckOnlineMerchandiseMonitorAgent2(
            long sleepTime,
            TimeUnit timeUnit,
            long initDelayTime,
            CustomerAckOnlineMerchandisePluginRoot pluginRoot,
            EventManager eventManager,
            CustomerAckOnlineMerchandiseBusinessTransactionDao dao,
            TransactionTransmissionManager transactionTransmissionManager,
            CustomerBrokerContractPurchaseManager customerBrokerContractPurchaseManager,
            CustomerBrokerContractSaleManager customerBrokerContractSaleManager,
            CustomerBrokerPurchaseNegotiationManager customerBrokerPurchaseNegotiationManager) {
        super(sleepTime, timeUnit, initDelayTime, pluginRoot, eventManager);
        this.dao = dao;
        this.transactionTransmissionManager = transactionTransmissionManager;
        this.customerBrokerContractPurchaseManager = customerBrokerContractPurchaseManager;
        this.customerBrokerContractSaleManager = customerBrokerContractSaleManager;
        this.customerBrokerPurchaseNegotiationManager = customerBrokerPurchaseNegotiationManager;
    }

    @Override
    protected void doTheMainTask() {
        try {
            String contractHash;

            /**
             * Check if pending incoming money events
             */
            List<String> pendingMoneyEventIdList = dao.getPendingIncomingMoneyEvents();
            for (String eventId : pendingMoneyEventIdList) {
                try {
                    checkPendingIncomingMoneyEvents(eventId);
                } catch (Exception e) {
                    reportError(e);
                }

            }

            /**
             * Check contract status to send. - Customer Side
             * The status to verify is PENDING_ACK_ONLINE_MERCHANDISE_NOTIFICATION, it represents that the merchandise is
             * acknowledge by the customer.
             */
            List<BusinessTransactionRecord> pendingToSubmitNotificationList = dao.getPendingToSubmitNotificationList();
            for (BusinessTransactionRecord pendingToSubmitNotificationRecord : pendingToSubmitNotificationList) {
                try {
                    System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - getting Pending To Submit Notification Record");

                    contractHash = pendingToSubmitNotificationRecord.getContractHash();

                    transactionTransmissionManager.sendContractStatusNotification(
                            pendingToSubmitNotificationRecord.getCustomerPublicKey(),
                            pendingToSubmitNotificationRecord.getBrokerPublicKey(),
                            contractHash,
                            pendingToSubmitNotificationRecord.getTransactionId(),
                            ContractTransactionStatus.ONLINE_MERCHANDISE_ACK,
                            Plugins.CUSTOMER_ACK_ONLINE_MERCHANDISE,
                            PlatformComponentType.ACTOR_CRYPTO_CUSTOMER,
                            PlatformComponentType.ACTOR_CRYPTO_BROKER);

                    System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - sent Contract Status Notification Message");

                    dao.updateContractTransactionStatus(contractHash, ContractTransactionStatus.ONLINE_MERCHANDISE_ACK);
                    System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - ContractTransactionStatus updated to ONLINE_MERCHANDISE_ACK");
                } catch (Exception e) {
                    reportError(e);
                }

            }

            /**
             * Check pending confirmation notifications to be submitted - Broker side
             * The status to verify is PENDING_ACK_ONLINE_MERCHANDISE
             */
            List<BusinessTransactionRecord> pendingToSubmitConfirmationList = dao.getPendingToSubmitConfirmationList();
            for (BusinessTransactionRecord pendingToSubmitConfirmationRecord : pendingToSubmitConfirmationList) {
                try {
                    System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - getting Pending To Submit Confirmation Record");
                    contractHash = pendingToSubmitConfirmationRecord.getContractHash();

                    transactionTransmissionManager.confirmNotificationReception(
                            pendingToSubmitConfirmationRecord.getBrokerPublicKey(),
                            pendingToSubmitConfirmationRecord.getCustomerPublicKey(),
                            contractHash,
                            pendingToSubmitConfirmationRecord.getTransactionId(),
                            Plugins.CUSTOMER_ACK_ONLINE_MERCHANDISE,
                            PlatformComponentType.ACTOR_CRYPTO_BROKER,
                            PlatformComponentType.ACTOR_CRYPTO_CUSTOMER);

                    System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - sent Confirm Notification Reception Message");

                    dao.updateContractTransactionStatus(contractHash, ContractTransactionStatus.CONFIRM_ONLINE_ACK_MERCHANDISE);
                    System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - ContractTransactionStatus updated to CONFIRM_ONLINE_ACK_MERCHANDISE");
                } catch (Exception e) {
                    reportError(e);
                }

            }

            /**
             * Check if pending events
             */
            List<String> pendingEventsIdList = dao.getPendingEvents();
            for (String eventId : pendingEventsIdList) {
                checkPendingEvent(eventId);
            }

        } catch (Exception e) {
            reportError(e);
        }
    }

    @Override
    protected void checkPendingEvent(String eventId) throws UnexpectedResultReturnedFromDatabaseException {
        try {
            String eventTypeCode = dao.getEventType(eventId);
            String contractHash;
            BusinessTransactionMetadata businessTransactionMetadata;
            ContractTransactionStatus contractTransactionStatus;
            BusinessTransactionRecord businessTransactionRecord;

            //This will happen in broker side
            if (eventTypeCode.equals(EventType.INCOMING_NEW_CONTRACT_STATUS_UPDATE.getCode())) {
                System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE");
                List<Transaction<BusinessTransactionMetadata>> pendingTransactionList = transactionTransmissionManager.getPendingTransactions(Specialist.UNKNOWN_SPECIALIST);
                for (Transaction<BusinessTransactionMetadata> record : pendingTransactionList) {
                    businessTransactionMetadata = record.getInformation();
                    contractHash = businessTransactionMetadata.getContractHash();
                    Plugins remoteBusinessTransaction = businessTransactionMetadata.getRemoteBusinessTransaction();

                    System.out.println("CUSTOMER_ACK_ONLINE_MERCHANDISE - remoteBusinessTransaction = " + remoteBusinessTransaction);
                    if (remoteBusinessTransaction != Plugins.CUSTOMER_ACK_ONLINE_MERCHANDISE)
                        continue;

                    System.out.println("CUSTOMER_ACK_ONLINE_MERCHANDISE - PASS remoteBusinessTransaction = " + remoteBusinessTransaction);

                    if (!dao.isContractHashInDatabase(contractHash)) {
                        CustomerBrokerContractSale saleContract = customerBrokerContractSaleManager.getCustomerBrokerContractSaleForContractId(contractHash);
                        ObjectChecker.checkArgument(saleContract); //If the contract is null, I cannot handle with this situation
                        System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - getting saleContract");
                        System.out.println(new StringBuilder().append("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - saleContract.getStatus() = ").append(saleContract.getStatus()).toString());

                        if (saleContract.getStatus() != ContractStatus.COMPLETED) {
                            dao.persistContractInDatabase(saleContract);
                            System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - saleContract persisted");

                            dao.setCompletionDateByContractHash(contractHash, (new Date()).getTime());
                            System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - settled completion date");

                            customerBrokerContractSaleManager.updateStatusCustomerBrokerSaleContractStatus(contractHash, ContractStatus.READY_TO_CLOSE);
                            System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - ContractStatus updated to READY_TO_CLOSE");

                            raiseAckConfirmationEvent(contractHash);
                            System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - raise Ack Confirmation Event");
                        }
                    }
                    transactionTransmissionManager.confirmReception(record.getTransactionID());
                    System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - reception confirmed");
                }
                dao.updateEventStatus(eventId, EventStatus.NOTIFIED);
                System.out.println("ACK_ONLINE_MERCHANDISE [Broker] - INCOMING_NEW_CONTRACT_STATUS_UPDATE - EventStatus update to NOTIFIED");
            }

            //This will happen in customer side
            if (eventTypeCode.equals(EventType.INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE.getCode())) {
                System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE");
                List<Transaction<BusinessTransactionMetadata>> pendingTransactionList = transactionTransmissionManager.getPendingTransactions(Specialist.UNKNOWN_SPECIALIST);
                for (Transaction<BusinessTransactionMetadata> record : pendingTransactionList) {
                    businessTransactionMetadata = record.getInformation();
                    contractHash = businessTransactionMetadata.getContractHash();
                    Plugins remoteBusinessTransaction = businessTransactionMetadata.getRemoteBusinessTransaction();

                    System.out.println("CUSTOMER_ACK_ONLINE_MERCHANDISE - remoteBusinessTransaction = " + remoteBusinessTransaction);
                    if (remoteBusinessTransaction != Plugins.CUSTOMER_ACK_ONLINE_MERCHANDISE)
                        continue;

                    System.out.println("CUSTOMER_ACK_ONLINE_MERCHANDISE - PASS remoteBusinessTransaction = " + remoteBusinessTransaction);

                    if (dao.isContractHashInDatabase(contractHash)) {
                        businessTransactionRecord = dao.getBusinessTransactionRecordByContractHash(contractHash);
                        contractTransactionStatus = businessTransactionRecord.getContractTransactionStatus();

                        if (contractTransactionStatus.getCode().equals(ContractTransactionStatus.ONLINE_MERCHANDISE_ACK.getCode())) {
                            CustomerBrokerContractPurchase contractPurchase = customerBrokerContractPurchaseManager.getCustomerBrokerContractPurchaseForContractId(contractHash);
                            ObjectChecker.checkArgument(contractPurchase);

                            if (contractPurchase.getStatus() != ContractStatus.COMPLETED) {
                                customerBrokerContractPurchaseManager.updateStatusCustomerBrokerPurchaseContractStatus(contractHash, ContractStatus.READY_TO_CLOSE);
                                System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE - ContractStatus updated to READY_TO_CLOSE");

                                dao.setCompletionDateByContractHash(contractHash, (new Date()).getTime());
                                System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE - settled completion date");

                                raiseAckConfirmationEvent(contractHash);
                                System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE - raise Ack Confirmation Event");
                            }
                        }
                    }
                    transactionTransmissionManager.confirmReception(record.getTransactionID());
                    System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE - reception confirmed");
                }
                dao.updateEventStatus(eventId, EventStatus.NOTIFIED);
                System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE - EventStatus updated to NOTIFIED");
            }

            if (eventTypeCode.equals(EventType.BROKER_ACK_PAYMENT_CONFIRMED.getCode())) {
                System.out.println("ACK_ONLINE_MERCHANDISE [Customer] - BROKER_ACK_PAYMENT_CONFIRMED");

                try {
                    //the eventId from this event is the contractId - Customer side
                    CustomerBrokerContractPurchase purchaseContract = customerBrokerContractPurchaseManager.
                            getCustomerBrokerContractPurchaseForContractId(eventId);

                    String negotiationId = purchaseContract.getNegotiatiotId();
                    CustomerBrokerPurchaseNegotiation purchaseNegotiation = customerBrokerPurchaseNegotiationManager.
                            getNegotiationsByNegotiationId(UUID.fromString(negotiationId));

                    Collection<Clause> clauses = purchaseNegotiation.getClauses();
                    String receptionMethodCode = NegotiationClauseHelper.getNegotiationClauseValue(clauses, ClauseType.BROKER_PAYMENT_METHOD);

                    if (MoneyType.CRYPTO.getCode().equals(receptionMethodCode)) {
                        String merchandiseCurrencyCode = NegotiationClauseHelper.getNegotiationClauseValue(clauses, ClauseType.CUSTOMER_CURRENCY);

                        String amountStr = NegotiationClauseHelper.getNegotiationClauseValue(clauses, ClauseType.CUSTOMER_CURRENCY_QUANTITY);
                        long cryptoAmount = getCryptoAmount(amountStr, merchandiseCurrencyCode);

                        dao.persistContractInDatabase(purchaseContract, cryptoAmount, merchandiseCurrencyCode);
                        System.out.println(new StringBuilder().append("ACK_ONLINE_MERCHANDISE [Customer] - BROKER_ACK_PAYMENT_CONFIRMED - persisted purchase contract. cryptoAmount = ").append(cryptoAmount).append(" - merchandiseCurrency = ").append(merchandiseCurrencyCode).toString());
                    }
                } catch (Exception e) {
                    System.out.println("ACK_ONLINE_MERCHANDISE - BROKER_ACK_PAYMENT_CONFIRMED - EXCEPTION!! Probably this is been executed in the Broker Side");
                }

                dao.updateEventStatus(eventId, EventStatus.NOTIFIED);
            }

        } catch (CantGetListCustomerBrokerContractPurchaseException | CantUpdateRecordException exception) {
            throw new UnexpectedResultReturnedFromDatabaseException(exception,
                    "Checking pending events", "Cannot update the database");
        } catch (CantConfirmTransactionException exception) {
            throw new UnexpectedResultReturnedFromDatabaseException(exception,
                    "Checking pending events", "Cannot confirm the transaction");
        } catch (CantUpdateCustomerBrokerContractSaleException | CantGetListCustomerBrokerContractSaleException exception) {
            throw new UnexpectedResultReturnedFromDatabaseException(exception,
                    "Checking pending events", "Cannot update the contract sale status");
        } catch (CantDeliverPendingTransactionsException exception) {
            throw new UnexpectedResultReturnedFromDatabaseException(exception,
                    "Checking pending events", "Cannot get the pending transactions from transaction transmission plugin");
        } catch (CantInsertRecordException exception) {
            throw new UnexpectedResultReturnedFromDatabaseException(exception,
                    "Checking pending events", "Cannot insert a record in database");
        } catch (CantUpdateCustomerBrokerContractPurchaseException exception) {
            throw new UnexpectedResultReturnedFromDatabaseException(exception,
                    "Checking pending events", "Cannot update the contract purchase status");
        } catch (ObjectNotSetException exception) {
            throw new UnexpectedResultReturnedFromDatabaseException(exception,
                    "Checking pending events", "The customerBrokerContractSale is null");
        }
    }

    private void raiseAckConfirmationEvent(String contractHash) {
        FermatEvent fermatEvent = eventManager.getNewEvent(EventType.CUSTOMER_ACK_MERCHANDISE_CONFIRMED);
        CustomerAckMerchandiseConfirmed customerAckMerchandiseConfirmed = (CustomerAckMerchandiseConfirmed) fermatEvent;
        customerAckMerchandiseConfirmed.setSource(EventSource.CUSTOMER_ACK_ONLINE_MERCHANDISE);
        customerAckMerchandiseConfirmed.setContractHash(contractHash);
        customerAckMerchandiseConfirmed.setPaymentType(PaymentType.CRYPTO_MONEY);
        eventManager.raiseEvent(customerAckMerchandiseConfirmed);
    }

    private void checkPendingIncomingMoneyEvents(String eventId) throws IncomingOnlineMerchandiseException, CantUpdateRecordException {
        System.out.println("ACK_ONLINE_MERCHANDISE - checkPendingIncomingMoneyEvents");

        try {
            IncomingMoneyEventWrapper incomingMoneyEventWrapper = dao.getIncomingMoneyEventWrapper(eventId);

            String contractHash = incomingMoneyEventWrapper.getTransactionHash();
            BusinessTransactionRecord businessTransactionRecord = dao.getBusinessTransactionRecordByContractHash(contractHash);

            if (businessTransactionRecord == null)
                return; //Case: the contract event is not processed or the incoming money is not link to a contract.

            //TODO verificar esto
            long incomingCryptoAmount = incomingMoneyEventWrapper.getCryptoAmount();
            long contractCryptoAmount = businessTransactionRecord.getCryptoAmount();
            if (incomingCryptoAmount != contractCryptoAmount)
                throw new IncomingOnlineMerchandiseException(
                        new StringBuilder().append("The incoming crypto amount received is ").append(incomingCryptoAmount).append("\nThe excepted amount in contract ").append(contractHash).append("\nis ").append(contractCryptoAmount).toString());

            // TODO probar esto
            final CryptoCurrency incomingCryptoCurrency = incomingMoneyEventWrapper.getCryptoCurrency();
            final CryptoCurrency contractCryptoCurrency = businessTransactionRecord.getCryptoCurrency();
            if (incomingCryptoCurrency != contractCryptoCurrency)
                throw new IncomingOnlineMerchandiseException(
                        new StringBuilder().append("The incoming crypto currency received is ").append(incomingCryptoCurrency.getFriendlyName()).append("\nThe excepted crypto currency in contract ").append(contractHash).append("\nis ").append(contractCryptoCurrency.getFriendlyName()).toString());

            String receiverActorPublicKey = incomingMoneyEventWrapper.getReceiverPublicKey();
            String expectedActorPublicKey = businessTransactionRecord.getCustomerPublicKey();
            if (!receiverActorPublicKey.equals(expectedActorPublicKey))
                throw new IncomingOnlineMerchandiseException(
                        new StringBuilder().append("The actor public key that receive the money is ").append(receiverActorPublicKey).append("\nThe broker public key in contract ").append(contractHash).append("\nis ").append(expectedActorPublicKey).toString());

            businessTransactionRecord.setContractTransactionStatus(ContractTransactionStatus.PENDING_ACK_ONLINE_MERCHANDISE_NOTIFICATION);
            dao.updateBusinessTransactionRecord(businessTransactionRecord);
            System.out.println("ACK_ONLINE_MERCHANDISE - checkPendingIncomingMoneyEvents - ContractTransactionStatus updated to PENDING_ACK_ONLINE_MERCHANDISE_NOTIFICATION");

            dao.updateIncomingEventStatus(eventId, EventStatus.NOTIFIED);
            System.out.println("ACK_ONLINE_MERCHANDISE - checkPendingIncomingMoneyEvents - Incoming Money EventStatus updated to NOTIFIED");

        } catch (UnexpectedResultReturnedFromDatabaseException e) {
            throw new IncomingOnlineMerchandiseException(e, "Checking the incoming payment", "The database return an unexpected result");
        }
    }

}
