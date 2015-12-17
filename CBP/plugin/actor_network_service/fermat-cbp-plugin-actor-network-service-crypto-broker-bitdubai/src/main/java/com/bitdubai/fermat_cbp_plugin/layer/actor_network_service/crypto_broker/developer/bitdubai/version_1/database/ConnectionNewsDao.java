package com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.database;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterOperator;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilterGroup;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ConnectionRequestAction;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ProtocolState;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.RequestType;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantAcceptConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantDenyConnectionRequestException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantListPendingConnectionRequestsException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.CantRequestConnectionException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.exceptions.ConnectionRequestNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerConnectionInformation;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerConnectionRequest;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.exceptions.CantChangeProtocolStateException;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.exceptions.CantConfirmConnectionRequestException;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.exceptions.CantInitializeDatabaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The class <code>com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.database.ConnectionNewsDao</code>
 * contains all the methods that interact with the database.
 * Manages the actor connection requests by storing them on a Database Table.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 23/11/2015.
 *
 * @version 1.0
 */
public final class ConnectionNewsDao {

    private final PluginDatabaseSystem pluginDatabaseSystem;
    private final UUID                 pluginId            ;

    private Database database;

    public ConnectionNewsDao(final PluginDatabaseSystem pluginDatabaseSystem,
                             final UUID                 pluginId            ) {

        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginId             = pluginId            ;
    }

    public void initialize() throws CantInitializeDatabaseException {

        try {

            database = this.pluginDatabaseSystem.openDatabase(
                    this.pluginId,
                    CryptoBrokerActorNetworkServiceDatabaseConstants.CRYPTO_BROKER_ACTOR_NETWORK_SERVICE_DATABASE_NAME
            );

        } catch (final DatabaseNotFoundException e) {

            try {

                CryptoBrokerActorNetworkServiceDatabaseFactory cryptoBrokerActorNetworkServiceDatabaseFactory = new CryptoBrokerActorNetworkServiceDatabaseFactory(pluginDatabaseSystem);
                database = cryptoBrokerActorNetworkServiceDatabaseFactory.createDatabase(
                        pluginId,
                        CryptoBrokerActorNetworkServiceDatabaseConstants.CRYPTO_BROKER_ACTOR_NETWORK_SERVICE_DATABASE_NAME
                );

            } catch (final CantCreateDatabaseException f) {

                throw new CantInitializeDatabaseException(f, "", "There was a problem and we cannot create the database.");
            } catch (final Exception z) {

                throw new CantInitializeDatabaseException(z, "", "Unhandled Exception.");
            }

        } catch (final CantOpenDatabaseException e) {

            throw new CantInitializeDatabaseException(e, "", "Exception not handled by the plugin, there was a problem and we cannot open the database.");
        } catch (final Exception e) {

            throw new CantInitializeDatabaseException(e, "", "Unhandled Exception.");
        }
    }

    /**
     * Return all the pending requests depending on the action informed through parameters.
     *
     * @param actions  the list of actions that we need to bring.
     *
     * @return a list of CryptoBrokerConnectionRequest instances.
     *
     * @throws CantListPendingConnectionRequestsException  if something goes wrong.
     */
    public final List<CryptoBrokerConnectionRequest> listAllPendingRequests(final List<ConnectionRequestAction> actions) throws CantListPendingConnectionRequestsException {

        try {

            final ProtocolState protocolState = ProtocolState.PENDING_LOCAL_ACTION;

            final DatabaseTable connectionNewsTable = database.getTable(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_TABLE_NAME);

            connectionNewsTable.addFermatEnumFilter(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME, protocolState, DatabaseFilterType.EQUAL);

            final List<DatabaseTableFilter> tableFilters = new ArrayList<>();

            for(final ConnectionRequestAction action : actions)
                connectionNewsTable.addFermatEnumFilter(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ACTION_COLUMN_NAME, action, DatabaseFilterType.EQUAL);

            final DatabaseTableFilterGroup filterGroup = connectionNewsTable.getNewFilterGroup(tableFilters, null, DatabaseFilterOperator.OR);

            connectionNewsTable.setFilterGroup(filterGroup);

            connectionNewsTable.loadToMemory();

            final List<DatabaseTableRecord> records = connectionNewsTable.getRecords();

            final List<CryptoBrokerConnectionRequest> cryptoAddressRequests = new ArrayList<>();

            for (final DatabaseTableRecord record : records)
                cryptoAddressRequests.add(buildConnectionNewRecord(record));

            return cryptoAddressRequests;

        } catch (final CantLoadTableToMemoryException e) {

            throw new CantListPendingConnectionRequestsException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot load the table.");
        } catch (final InvalidParameterException e) {

            throw new CantListPendingConnectionRequestsException(e, "", "There is a problem with some enum code."                                                                                );
        }
    }

    /**
     * Return all the pending requests depending on the protocol state informed through parameters.
     *
     * @param protocolState  the protocol state that we need to bring.
     *
     * @return a list of CryptoBrokerConnectionRequest instances.
     *
     * @throws CantListPendingConnectionRequestsException  if something goes wrong.
     */
    public final List<CryptoBrokerConnectionRequest> listAllRequestByProtocolState(final ProtocolState protocolState) throws CantListPendingConnectionRequestsException {

        try {

            final DatabaseTable connectionNewsTable = database.getTable(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_TABLE_NAME);

            connectionNewsTable.addFermatEnumFilter(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME, protocolState, DatabaseFilterType.EQUAL);

            connectionNewsTable.loadToMemory();

            final List<DatabaseTableRecord> records = connectionNewsTable.getRecords();

            final List<CryptoBrokerConnectionRequest> cryptoAddressRequests = new ArrayList<>();

            for (final DatabaseTableRecord record : records)
                cryptoAddressRequests.add(buildConnectionNewRecord(record));

            return cryptoAddressRequests;

        } catch (final CantLoadTableToMemoryException e) {

            throw new CantListPendingConnectionRequestsException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot load the table.");
        } catch (final InvalidParameterException e) {

            throw new CantListPendingConnectionRequestsException(e, "", "There is a problem with some enum code."                                                                                );
        }
    }

    public final void createConnectionRequest(final UUID                              newId            ,
                                              final CryptoBrokerConnectionInformation brokerInformation,
                                              final ProtocolState                     state            ,
                                              final RequestType                       type             ,
                                              final ConnectionRequestAction           action           ) throws CantRequestConnectionException {

        try {

            final CryptoBrokerConnectionRequest connectionNew = new CryptoBrokerConnectionRequest(
                    newId                                      ,
                    brokerInformation.getSenderPublicKey()     ,
                    brokerInformation.getSenderActorType()     ,
                    brokerInformation.getSenderAlias()         ,
                    null                                       ,
                    brokerInformation.getDestinationPublicKey(),
                    type                                       ,
                    state                                      ,
                    action                                     ,
                    brokerInformation.getSendingTime()
            );

            final DatabaseTable addressExchangeRequestTable = database.getTable(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_TABLE_NAME);

            DatabaseTableRecord entityRecord = addressExchangeRequestTable.getEmptyRecord();

            entityRecord = buildDatabaseRecord(entityRecord, connectionNew);

            addressExchangeRequestTable.insertRecord(entityRecord);

        } catch (final CantInsertRecordException e) {

            throw new CantRequestConnectionException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot insert the record.");
        }
    }

    /**
     * Through this method you can save a denial for a connection request.
     * It can be LOCAL or REMOTE.
     * Possible states: PROCESSING_SEND, PROCESSING_RECEIVE.
     *
     * @param requestId id of the connection request.
     * @param state     PROCESSING_SEND, PROCESSING_RECEIVE
     *
     * @throws CantDenyConnectionRequestException    if something goes wrong.
     * @throws ConnectionRequestNotFoundException    if we cannot find the request.
     */
    public void denyConnection(final UUID          requestId,
                               final ProtocolState state    ) throws CantDenyConnectionRequestException ,
                                                                     ConnectionRequestNotFoundException {

        if (requestId == null)
            throw new CantDenyConnectionRequestException(null, "", "The requestId is required, can not be null");

        if (state == null)
            throw new CantDenyConnectionRequestException(null, "", "The state is required, can not be null");

        try {

            final ConnectionRequestAction action = ConnectionRequestAction.DENY;

            final DatabaseTable connectionNewsTable = database.getTable(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_TABLE_NAME);

            connectionNewsTable.addUUIDFilter(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ID_COLUMN_NAME, requestId, DatabaseFilterType.EQUAL);

            connectionNewsTable.loadToMemory();

            final List<DatabaseTableRecord> records = connectionNewsTable.getRecords();

            if (!records.isEmpty()) {

                final DatabaseTableRecord record = records.get(0);

                record.setFermatEnum(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME , state );
                record.setFermatEnum(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ACTION_COLUMN_NAME, action);

                connectionNewsTable.updateRecord(record);

            } else
                throw new ConnectionRequestNotFoundException(null, "requestId: "+requestId, "Cannot find an actor connection request with that requestId.");

        } catch (final CantUpdateRecordException e) {

            throw new CantDenyConnectionRequestException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot update the record.");
        } catch (final CantLoadTableToMemoryException e) {

            throw new CantDenyConnectionRequestException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot load the table.");
        }
    }

    /**
     * change the protocol state
     *
     * @param requestId id of the address exchange request we want to confirm.
     * @param state     protocol state to change
     *
     * @throws CantChangeProtocolStateException      if something goes wrong.
     * @throws ConnectionRequestNotFoundException    if i can't find the record.
     */
    public void changeProtocolState(final UUID          requestId,
                                    final ProtocolState state    ) throws CantChangeProtocolStateException,
                                                                          ConnectionRequestNotFoundException  {

        if (requestId == null)
            throw new CantChangeProtocolStateException(null, "", "The requestId is required, can not be null");

        if (state == null)
            throw new CantChangeProtocolStateException(null, "", "The state is required, can not be null");

        try {

            DatabaseTable actorConnectionRequestTable = database.getTable(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_TABLE_NAME);

            actorConnectionRequestTable.addUUIDFilter(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ID_COLUMN_NAME, requestId, DatabaseFilterType.EQUAL);

            actorConnectionRequestTable.loadToMemory();

            List<DatabaseTableRecord> records = actorConnectionRequestTable.getRecords();

            if (!records.isEmpty()) {
                DatabaseTableRecord record = records.get(0);

                record.setStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME, state.getCode());

                actorConnectionRequestTable.updateRecord(record);

            } else
                throw new ConnectionRequestNotFoundException(null, "requestId: "+requestId, "Cannot find an actor Connection request with that requestId.");

        } catch (CantUpdateRecordException e) {

            throw new CantChangeProtocolStateException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot update the record.");
        } catch (CantLoadTableToMemoryException e) {

            throw new CantChangeProtocolStateException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot load the table.");

        }
    }

    /**
     * when i confirm a request i put it in the final state, indicating:
     * State : DONE.
     * Action: NONE.
     *
     * @param requestId id of the address exchange request we want to confirm.
     *
     * @throws CantConfirmConnectionRequestException   if something goes wrong.
     * @throws ConnectionRequestNotFoundException      if i can't find the record.
     */
    public void confirmActorConnectionRequest(final UUID requestId) throws CantConfirmConnectionRequestException,
                                                                           ConnectionRequestNotFoundException   {

        if (requestId == null) {
            throw new CantConfirmConnectionRequestException(null, "", "The requestId is required, can not be null");
        }

        try {

            ProtocolState           state  = ProtocolState          .DONE;
            ConnectionRequestAction action = ConnectionRequestAction.NONE;

            DatabaseTable actorConnectionRequestTable = database.getTable(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_TABLE_NAME);

            actorConnectionRequestTable.addUUIDFilter(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ID_COLUMN_NAME, requestId, DatabaseFilterType.EQUAL);

            actorConnectionRequestTable.loadToMemory();

            List<DatabaseTableRecord> records = actorConnectionRequestTable.getRecords();

            if (!records.isEmpty()) {
                DatabaseTableRecord record = records.get(0);

                record.setStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME , state .getCode());
                record.setStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ACTION_COLUMN_NAME, action.getCode());

                actorConnectionRequestTable.updateRecord(record);

            } else
                throw new ConnectionRequestNotFoundException(null, "requestId: "+requestId, "Cannot find an address exchange request with that requestId.");

        } catch (CantUpdateRecordException e) {

            throw new CantConfirmConnectionRequestException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot update the record.");
        } catch (CantLoadTableToMemoryException e) {

            throw new CantConfirmConnectionRequestException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot load the table.");

        }
    }

    public boolean isPendingUpdates() throws CantListPendingConnectionRequestsException {

        List<ConnectionRequestAction> actions = new ArrayList<>();

        actions.add(ConnectionRequestAction.ACCEPT);
        actions.add(ConnectionRequestAction.DENY  );

        return this.listAllPendingRequests(actions) != null && !(this.listAllPendingRequests(actions).isEmpty());
    }

    public boolean isPendingRequests() throws CantListPendingConnectionRequestsException {

        List<ConnectionRequestAction> actions = new ArrayList<>();

        actions.add(ConnectionRequestAction.CANCEL    );
        actions.add(ConnectionRequestAction.DISCONNECT);
        actions.add(ConnectionRequestAction.REQUEST   );

        return this.listAllPendingRequests(actions) != null && !(this.listAllPendingRequests(actions).isEmpty());
    }

    /**
     * Through this method you can save an acceptation for a connection request.
     * It can be LOCAL or REMOTE.
     * Possible states: PROCESSING_SEND, PROCESSING_RECEIVE.
     *
     * @param requestId id of the connection request.
     * @param state     PROCESSING_SEND, PROCESSING_RECEIVE
     *
     * @throws CantAcceptConnectionRequestException  if something goes wrong.
     * @throws ConnectionRequestNotFoundException    if we cannot find the request.
     */
    public void acceptConnection(final UUID          requestId,
                                 final ProtocolState state    ) throws CantAcceptConnectionRequestException,
                                                                       ConnectionRequestNotFoundException  {

        if (requestId == null)
            throw new CantAcceptConnectionRequestException(null, "", "The requestId is required, can not be null");

        if (state == null)
            throw new CantAcceptConnectionRequestException(null, "", "The state is required, can not be null");

        try {

            final ConnectionRequestAction action = ConnectionRequestAction.ACCEPT;

            final DatabaseTable connectionNewsTable = database.getTable(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_TABLE_NAME);

            connectionNewsTable.addUUIDFilter(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ID_COLUMN_NAME, requestId, DatabaseFilterType.EQUAL);

            connectionNewsTable.loadToMemory();

            final List<DatabaseTableRecord> records = connectionNewsTable.getRecords();

            if (!records.isEmpty()) {

                final DatabaseTableRecord record = records.get(0);

                record.setFermatEnum(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME , state );
                record.setFermatEnum(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ACTION_COLUMN_NAME, action);

                connectionNewsTable.updateRecord(record);

            } else
                throw new ConnectionRequestNotFoundException(null, "requestId: "+requestId, "Cannot find an actor connection request with that requestId.");

        } catch (final CantUpdateRecordException e) {

            throw new CantAcceptConnectionRequestException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot update the record.");
        } catch (final CantLoadTableToMemoryException e) {

            throw new CantAcceptConnectionRequestException(e, "", "Exception not handled by the plugin, there is a problem in database and i cannot load the table.");
        }
    }

    private DatabaseTableRecord buildDatabaseRecord(final DatabaseTableRecord       record       ,
                                                    final CryptoBrokerConnectionRequest connectionNew) {

        record.setUUIDValue  (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ID_COLUMN_NAME            , connectionNew.getRequestId()           );
        record.setStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENDER_PUBLIC_KEY_COLUMN_NAME     , connectionNew.getSenderPublicKey()     );
        record.setFermatEnum (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENDER_ACTOR_TYPE_COLUMN_NAME     , connectionNew.getSenderActorType()     );
        record.setStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENDER_ALIAS_COLUMN_NAME          , connectionNew.getSenderAlias()         );
        record.setStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_DESTINATION_PUBLIC_KEY_COLUMN_NAME, connectionNew.getDestinationPublicKey());
        record.setFermatEnum (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_TYPE_COLUMN_NAME          , connectionNew.getRequestType()         );
        record.setFermatEnum (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME         , connectionNew.getProtocolState()       );
        record.setFermatEnum (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ACTION_COLUMN_NAME        , connectionNew.getRequestAction()       );
        record.setLongValue  (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENT_TIME_COLUMN_NAME             , connectionNew.getSentTime()            );

        return record;
    }

    private CryptoBrokerConnectionRequest buildConnectionNewRecord(final DatabaseTableRecord record) throws InvalidParameterException {

        UUID   requestId                    = record.getUUIDValue  (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ID_COLUMN_NAME            );
        String senderPublicKey              = record.getStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENDER_PUBLIC_KEY_COLUMN_NAME     );
        String senderActorTypeString        = record.getStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENDER_ACTOR_TYPE_COLUMN_NAME     );
        String senderAlias                  = record.getStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENDER_ALIAS_COLUMN_NAME          );
        String destinationPublicKey         = record.getStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_DESTINATION_PUBLIC_KEY_COLUMN_NAME);
        String requestTypeString            = record.getStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_TYPE_COLUMN_NAME          );
        String protocolStateString          = record.getStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_STATE_COLUMN_NAME         );
        String requestActionString          = record.getStringValue(CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_REQUEST_ACTION_COLUMN_NAME        );
        Long   sentTime                     = record.getLongValue  (CryptoBrokerActorNetworkServiceDatabaseConstants.CONNECTION_NEWS_SENT_TIME_COLUMN_NAME             );

        Actors                  senderActorType  = Actors                 .getByCode(senderActorTypeString);
        RequestType             requestType      = RequestType            .getByCode(requestTypeString    );
        ProtocolState           state            = ProtocolState          .getByCode(protocolStateString  );
        ConnectionRequestAction action           = ConnectionRequestAction.getByCode(requestActionString  );

        return new CryptoBrokerConnectionRequest(
                requestId           ,
                senderPublicKey     ,
                senderActorType     ,
                senderAlias         ,
                null                ,
                destinationPublicKey,
                requestType         ,
                state               ,
                action              ,
                sentTime
        );
    }
}
