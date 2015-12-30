package com.bitdubai.fermat_cht_plugin.layer.network_service.chat.version_1.database;

import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseDataType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFactory;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFactory;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateTableException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.InvalidOwnerIdException;

import java.util.UUID;

/**
 *  The Class  <code>com.bitdubai.fermat_cht_plugin.layer.network_service.chat.version_1.layer.network_service.chat.developer.bitdubai.version_1.database.ChatNetworkServiceDatabaseFactory</code>
 * is responsible for creating the tables in the database where it is to keep the information.
 * <p/>
 *
 * Created by Gabriel Araujo - (gabe_512@hotmail.com) on 30/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ChatNetworkServiceDatabaseFactory implements DealsWithPluginDatabaseSystem {

    /**
     * DealsWithPluginDatabaseSystem Interface member variables.
     */
    private PluginDatabaseSystem pluginDatabaseSystem;

    /**
     * Constructor with parameters to instantiate class
     * .
     *
     * @param pluginDatabaseSystem DealsWithPluginDatabaseSystem
     */
    public ChatNetworkServiceDatabaseFactory(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
    }

    /**
     * Create the database
     *
     * @param ownerId      the owner id
     * @param databaseName the database name
     * @return Database
     * @throws CantCreateDatabaseException
     */
    protected Database createDatabase(UUID ownerId, String databaseName) throws CantCreateDatabaseException {
        Database database;

        /**
         * I will create the database where I am going to store the information of this wallet.
         */
        try {
            database = this.pluginDatabaseSystem.createDatabase(ownerId, databaseName);
        } catch (CantCreateDatabaseException cantCreateDatabaseException) {
            /**
             * I can not handle this situation.
             */
            throw new CantCreateDatabaseException(CantCreateDatabaseException.DEFAULT_MESSAGE, cantCreateDatabaseException, "", "Exception not handled by the plugin, There is a problem and i cannot create the database.");
        }

        /**
         * Next, I will add the needed tables.
         */
        try {
            DatabaseTableFactory table;
            DatabaseFactory databaseFactory = database.getDatabaseFactory();

            /**
             * Create incoming messages table.
             */
            table = databaseFactory.newTableFactory(ownerId, ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_TABLE_NAME);

            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_ID_COLUMN_NAME, DatabaseDataType.STRING, 36, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_SENDER_ID_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_RECEIVER_ID_COLUMN_NAME, DatabaseDataType.STRING, 10, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_TEXT_CONTENT_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_TYPE_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_SHIPPING_TIMESTAMP_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_DELIVERY_TIMESTAMP_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_STATUS_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);

            table.addIndex(ChatNetworkServiceDatabaseConstants.INCOMING_MESSAGES_FIRST_KEY_COLUMN);

            try {
                //Create the table
                databaseFactory.createTable(ownerId, table);
            } catch (CantCreateTableException cantCreateTableException) {
                throw new CantCreateDatabaseException(CantCreateDatabaseException.DEFAULT_MESSAGE, cantCreateTableException, "", "Exception not handled by the plugin, There is a problem and i cannot create the table.");
            }           /**
             * Create outgoing messages table.
             */
            table = databaseFactory.newTableFactory(ownerId, ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_TABLE_NAME);

            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_ID_COLUMN_NAME, DatabaseDataType.STRING, 36, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_SENDER_ID_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_RECEIVER_ID_COLUMN_NAME, DatabaseDataType.STRING, 10, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_TEXT_CONTENT_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_TYPE_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_SHIPPING_TIMESTAMP_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_DELIVERY_TIMESTAMP_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_STATUS_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);

            table.addIndex(ChatNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_FIRST_KEY_COLUMN);

            try {
                //Create the table
                databaseFactory.createTable(ownerId, table);
            } catch (CantCreateTableException cantCreateTableException) {
                throw new CantCreateDatabaseException(CantCreateDatabaseException.DEFAULT_MESSAGE, cantCreateTableException, "", "Exception not handled by the plugin, There is a problem and i cannot create the table.");
            }           /**
             * Create chat_metadata_transaction table.
             */
            table = databaseFactory.newTableFactory(ownerId, ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_TABLE_NAME);

            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_CHAT_ID_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_SENDER_ID_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_SENDER_TYPE_COLUMN_NAME, DatabaseDataType.STRING, 30, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_RECEIVER_ID_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_RECEIVER_TYPE_COLUMN_NAME, DatabaseDataType.STRING, 30, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_META_DATA_XML_COLUMN_NAME, DatabaseDataType.STRING, 1000, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_TYPE_COLUMN_NAME, DatabaseDataType.STRING, 20, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_DISTRIBUTION_STATUS_COLUMN_NAME, DatabaseDataType.STRING, 100, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_TIMESTAMP_COLUMN_NAME, DatabaseDataType.STRING, 255, Boolean.FALSE);
            table.addColumn(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_PROCESSED_COLUMN_NAME, DatabaseDataType.STRING, 1, Boolean.FALSE);

            table.addIndex(ChatNetworkServiceDatabaseConstants.CHAT_METADATA_TRANSACTION_FIRST_KEY_COLUMN);

            try {
                //Create the table
                databaseFactory.createTable(ownerId, table);
            } catch (CantCreateTableException cantCreateTableException) {
                throw new CantCreateDatabaseException(CantCreateDatabaseException.DEFAULT_MESSAGE, cantCreateTableException, "", "Exception not handled by the plugin, There is a problem and i cannot create the table.");
            }
        } catch (InvalidOwnerIdException invalidOwnerId) {
            /**
             * This shouldn't happen here because I was the one who gave the owner id to the database file system,
             * but anyway, if this happens, I can not continue.
             */
            throw new CantCreateDatabaseException(CantCreateDatabaseException.DEFAULT_MESSAGE, invalidOwnerId, "", "There is a problem with the ownerId of the database.");
        }
        return database;
    }

    /**
     * DealsWithPluginDatabaseSystem Interface implementation.
     */
    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
    }
}
