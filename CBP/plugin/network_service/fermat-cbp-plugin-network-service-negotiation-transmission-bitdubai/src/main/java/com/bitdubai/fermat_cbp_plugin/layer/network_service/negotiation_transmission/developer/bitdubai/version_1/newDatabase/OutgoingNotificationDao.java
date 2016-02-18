package com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase;

import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseTransactionFailedException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransactionType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransmissionState;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransmissionType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationType;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.enums.ActorProtocolState;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.interfaces.NegotiationTransmission;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.structure.NegotiationTransmissionImpl;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_user.exceptions.CantCreateNotificationException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.common.network_services.template.exceptions.CantUpdateRecordDataBaseException;

import java.util.List;
import java.util.UUID;

/**
 * Created by José Vilchez on 15/02/16.
 */
public class OutgoingNotificationDao {
    private Database database;

    private final PluginFileSystem pluginFileSystem;
    private final UUID pluginId;

    public OutgoingNotificationDao(Database database,
                                   final PluginFileSystem pluginFileSystem,
                                   final UUID pluginId) {

        this.database = database;
        this.pluginFileSystem = pluginFileSystem;
        this.pluginId = pluginId;
    }

    public DatabaseTable getDatabaseTable() {
        return database.getTable(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TABLE_NAME);
    }

    public void createNotification(NegotiationTransmission negotiationTransmission) throws CantCreateNotificationException {

        try {

            if (!existNotification(negotiationTransmission)) {
                DatabaseTable cryptoPaymentRequestTable = getDatabaseTable();

                DatabaseTableRecord entityRecord = cryptoPaymentRequestTable.getEmptyRecord();

                cryptoPaymentRequestTable.insertRecord(loadRecordAsSendNegotiationTransmission(entityRecord, negotiationTransmission));
            }


        } catch (CantInsertRecordException e) {
//            throw new CantCreateNotificationException( "",e, "Exception not handled by the plugin, there is a problem in database and i cannot insert the record.","");
        }
    }

    public void createNotification(NegotiationTransmission negotiationTransmission, NegotiationTransmissionState negotiationTransmissionState) throws CantCreateNotificationException {

        try {

//            if (!existNotification(negotiationTransmission)) {
                DatabaseTable cryptoPaymentRequestTable = getDatabaseTable();

                DatabaseTableRecord entityRecord = cryptoPaymentRequestTable.getEmptyRecord();

                cryptoPaymentRequestTable.insertRecord(loadRecordAsSendNegotiationTransmission(entityRecord, negotiationTransmission, negotiationTransmissionState));
//            }


        } catch (CantInsertRecordException e) {
//            throw new CantCreateNotificationException( "",e, "Exception not handled by the plugin, there is a problem in database and i cannot insert the record.","");
        }
    }

    public boolean existNotification(final NegotiationTransmission negotiationTransmission) {


        try {

            DatabaseTable cryptoPaymentRequestTable = getDatabaseTable();

            cryptoPaymentRequestTable.addUUIDFilter(NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSMISSION_ID_COLUMN_NAME, negotiationTransmission.getResponseToNotificationId(), DatabaseFilterType.EQUAL);
//            cryptoPaymentRequestTable.addUUIDFilter(NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSACTION_ID_COLUMN_NAME, negotiationTransmission.getTransactionId(), DatabaseFilterType.EQUAL);
//            cryptoPaymentRequestTable.addUUIDFilter(NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_NEGOTIATION_ID_COLUMN_NAME, negotiationTransmission.getNegotiationId(), DatabaseFilterType.EQUAL);

            cryptoPaymentRequestTable.loadToMemory();

            List<DatabaseTableRecord> records = cryptoPaymentRequestTable.getRecords();


            if (!records.isEmpty())
                return true;
            else
                return false;

        } catch (CantLoadTableToMemoryException exception) {

            return false;
//            throw new CantGetNotificationException( "",exception, "Exception not handled by the plugin, there is a problem in database and i cannot load the table.","");
        }

    }

    /*CONFIRM RECEPTION*/
    public void confirmReception(UUID transmissionId) {
        try {

            NegotiationTransmission negotiationTransmission = getNotificationById(transmissionId);
            negotiationTransmission.setTransmissionState(NegotiationTransmissionState.DONE);
            update(negotiationTransmission);
            System.out.print("\n\n**** 19.2.2) MOCK NEGOTIATION TRANSACTION - NEGOTIATION TRANSMISSION - DAO - REGISTER NEW EVENT, CONFIRM TRANSAMISSION ****\n");

        } catch (CantUpdateRecordDataBaseException e) {
            e.printStackTrace();
        }
    }

    public NegotiationTransmission getNotificationById(final UUID transmissionId) {
//
        if (transmissionId == null)
            return null;

        try {

            DatabaseTable cryptoPaymentRequestTable = getDatabaseTable();

            cryptoPaymentRequestTable.addUUIDFilter(NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSMISSION_ID_COLUMN_NAME, transmissionId, DatabaseFilterType.EQUAL);

            cryptoPaymentRequestTable.loadToMemory();

            List<DatabaseTableRecord> records = cryptoPaymentRequestTable.getRecords();


            if (!records.isEmpty())
                return buildNegotiationTransmission(records.get(0));
            else
                ;


        } catch (CantLoadTableToMemoryException exception) {

//            throw new CantGetNotificationException( "",exception, "Exception not handled by the plugin, there is a problem in database and i cannot load the table.","");
        } catch (InvalidParameterException exception) {

//            throw new CantGetNotificationException("",exception, "Check the cause."                                                                                ,"");
        }

        return null;

    }

    public void update(NegotiationTransmission entity) throws CantUpdateRecordDataBaseException {

        if (entity == null) {
            throw new IllegalArgumentException("The entity is required, can not be null");
        }

        try {

            DatabaseTable incomingNotificationtable = getDatabaseTable();

            DatabaseTableRecord emptyRecord = getDatabaseTable().getEmptyRecord();
            /*
             * 1- Create the record to the entity
             */
            DatabaseTableRecord entityRecord = loadRecordAsSendNegotiationTransmission(emptyRecord, entity);

            /**
             * 2.- Create a new transaction and execute
             */
            DatabaseTransaction transaction = database.newTransaction();

            //Set filter
            incomingNotificationtable.addUUIDFilter(NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSMISSION_ID_COLUMN_NAME, entity.getResponseToNotificationId(), DatabaseFilterType.EQUAL);
//            incomingNotificationtable.addUUIDFilter(NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSACTION_ID_COLUMN_NAME, entity.getTransactionId(), DatabaseFilterType.EQUAL);
//            incomingNotificationtable.addUUIDFilter(NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_NEGOTIATION_ID_COLUMN_NAME, entity.getNegotiationId(), DatabaseFilterType.EQUAL);

            transaction.addRecordToUpdate(incomingNotificationtable, entityRecord);
            database.executeTransaction(transaction);

        } catch (DatabaseTransactionFailedException databaseTransactionFailedException) {
            // Register the failure.
            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Database Name: " + NegotiationTransmissionNetworkServiceDatabaseConstants.DATA_BASE_NAME);

            String context = contextBuffer.toString();
            String possibleCause = "The record do not exist";
            CantUpdateRecordDataBaseException cantUpdateRecordDataBaseException = new CantUpdateRecordDataBaseException(CantUpdateRecordDataBaseException.DEFAULT_MESSAGE, databaseTransactionFailedException, context, possibleCause);
            throw cantUpdateRecordDataBaseException;
//        } catch (CantBuildDataBaseRecordException e) {
//            CantUpdateRecordDataBaseException cantUpdateRecordDataBaseException = new CantUpdateRecordDataBaseException(CantUpdateRecordDataBaseException.DEFAULT_MESSAGE, e, "", "");
//            throw cantUpdateRecordDataBaseException;
        }
    }

    private NegotiationTransmission buildNegotiationTransmission(DatabaseTableRecord record) throws InvalidParameterException {
        try {

            UUID transmissionId = record.getUUIDValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSMISSION_ID_COLUMN_NAME);
            UUID transactionId = record.getUUIDValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSACTION_ID_COLUMN_NAME);
            UUID negotiationId = record.getUUIDValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_NEGOTIATION_ID_COLUMN_NAME);
            String negotiationTransactionType = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_NEGOTIATION_TRANSACTION_TYPE_COLUMN_NAME);
            String publicKeyActorSend = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_PUBLIC_KEY_ACTOR_SEND_COLUMN_NAME);
            String actorSendType = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_ACTOR_SEND_TYPE_COLUMN_NAME);
            String publicKeyActorReceive = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_PUBLIC_KEY_ACTOR_RECEIVE_COLUMN_NAME);
            String actorReceiveType = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_ACTOR_RECEIVE_TYPE_COLUMN_NAME);
            String transmissionType = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSMISSION_TYPE_COLUMN_NAME);
            String transmissionState = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TRANSMISSION_STATE_COLUMN_NAME);
            String negotiationType = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_NEGOTIATION_TYPE_COLUMN_NAME);
            String negotiationXML = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_NEGOTIATION_XML_COLUMN_NAME);
            long timestamp = record.getLongValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_TIMESTAMP_COLUMN_NAME);
            String pendingFlag = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_NETWORK_SERVICE_PENDING_FLAG_COLUMN_NAME);
            String flagRead = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_READ_MARK_COLUMN_NAME);
            String protocolState = record.getStringValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_PROTOCOL_STATE_COLUMN_NAME);
            int sentCount = 0;
            UUID responseToNotificationId = record.getUUIDValue(com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.newDatabase.NegotiationTransmissionNetworkServiceDatabaseConstants.INCOMING_NOTIFICATION_RESPONSE_TO_NOTIFICATION_ID_COLUMN_NAME);

            return new NegotiationTransmissionImpl(
                    transmissionId,
                    transactionId,
                    negotiationId,
                    NegotiationTransactionType.getByCode(negotiationTransactionType),
                    publicKeyActorSend,
                    PlatformComponentType.getByCode(actorSendType),
                    publicKeyActorReceive,
                    PlatformComponentType.getByCode(actorReceiveType),
                    NegotiationTransmissionType.getByCode(transmissionType),
                    NegotiationTransmissionState.getByCode(transmissionState),
                    NegotiationType.getByCode(negotiationType),
                    negotiationXML,
                    timestamp,
                    Boolean.valueOf(pendingFlag),
                    Boolean.valueOf(flagRead),
                    ActorProtocolState.getByCode(protocolState),
                    sentCount,
                    responseToNotificationId
            );
        } catch (Exception e) {
            throw new InvalidParameterException();
        }
    }

    private DatabaseTableRecord loadRecordAsSendNegotiationTransmission(DatabaseTableRecord record, NegotiationTransmission negotiationTransmission, NegotiationTransmissionState negotiationTransmissionState) {
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSMISSION_ID_COLUMN_NAME, negotiationTransmission.getTransmissionId());
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSACTION_ID_COLUMN_NAME, negotiationTransmission.getTransactionId());
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_ID_COLUMN_NAME, negotiationTransmission.getNegotiationId());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_TRANSACTION_TYPE_COLUMN_NAME, negotiationTransmission.getNegotiationTransactionType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_PUBLIC_KEY_ACTOR_SEND_COLUMN_NAME, negotiationTransmission.getPublicKeyActorSend());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_ACTOR_SEND_TYPE_COLUMN_NAME, negotiationTransmission.getActorSendType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_PUBLIC_KEY_ACTOR_RECEIVE_COLUMN_NAME, negotiationTransmission.getPublicKeyActorReceive());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_ACTOR_RECEIVE_TYPE_COLUMN_NAME, negotiationTransmission.getActorReceiveType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSMISSION_TYPE_COLUMN_NAME, negotiationTransmission.getTransmissionType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSMISSION_STATE_COLUMN_NAME, negotiationTransmissionState.getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_TYPE_COLUMN_NAME, negotiationTransmission.getNegotiationType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_XML_COLUMN_NAME, negotiationTransmission.getNegotiationXML());
        record.setLongValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TIMESTAMP_COLUMN_NAME, negotiationTransmission.getTimestamp());
//        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_PROTOCOL_STATE_COLUMN_NAME, negotiationTransmission.getActorProtocolState().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_READ_MARK_COLUMN_NAME, String.valueOf(negotiationTransmission.isFlagRead()));
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NETWORK_SERVICE_PENDING_FLAG_COLUMN_NAME, String.valueOf(negotiationTransmission.isPendingFlag()));
        record.setIntegerValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_SENT_COUNT_COLUMN_NAME, negotiationTransmission.getSentCount());
        if(negotiationTransmission.getResponseToNotificationId() != null)
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_RESPONSE_TO_NOTIFICATION_ID_COLUMN_NAME, negotiationTransmission.getResponseToNotificationId());
        return record;
    }

    private DatabaseTableRecord loadRecordAsSendNegotiationTransmission(DatabaseTableRecord record, NegotiationTransmission negotiationTransmission) {
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSMISSION_ID_COLUMN_NAME, negotiationTransmission.getTransmissionId());
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSACTION_ID_COLUMN_NAME, negotiationTransmission.getTransactionId());
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_ID_COLUMN_NAME, negotiationTransmission.getNegotiationId());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_TRANSACTION_TYPE_COLUMN_NAME, negotiationTransmission.getNegotiationTransactionType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_PUBLIC_KEY_ACTOR_SEND_COLUMN_NAME, negotiationTransmission.getPublicKeyActorSend());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_ACTOR_SEND_TYPE_COLUMN_NAME, negotiationTransmission.getActorSendType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_PUBLIC_KEY_ACTOR_RECEIVE_COLUMN_NAME, negotiationTransmission.getPublicKeyActorReceive());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_ACTOR_RECEIVE_TYPE_COLUMN_NAME, negotiationTransmission.getActorReceiveType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSMISSION_TYPE_COLUMN_NAME, negotiationTransmission.getTransmissionType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TRANSMISSION_STATE_COLUMN_NAME, negotiationTransmission.getTransmissionState().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_TYPE_COLUMN_NAME, negotiationTransmission.getNegotiationType().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NEGOTIATION_XML_COLUMN_NAME, negotiationTransmission.getNegotiationXML());
        record.setLongValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_TIMESTAMP_COLUMN_NAME, negotiationTransmission.getTimestamp());
//        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_PROTOCOL_STATE_COLUMN_NAME, negotiationTransmission.getActorProtocolState().getCode());
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_READ_MARK_COLUMN_NAME, String.valueOf(negotiationTransmission.isFlagRead()));
        record.setStringValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_NETWORK_SERVICE_PENDING_FLAG_COLUMN_NAME, String.valueOf(negotiationTransmission.isPendingFlag()));
        record.setIntegerValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_SENT_COUNT_COLUMN_NAME, negotiationTransmission.getSentCount());
        record.setUUIDValue(NegotiationTransmissionNetworkServiceDatabaseConstants.OUTGOING_NOTIFICATION_RESPONSE_TO_NOTIFICATION_ID_COLUMN_NAME, negotiationTransmission.getResponseToNotificationId());
        return record;
    }
}
