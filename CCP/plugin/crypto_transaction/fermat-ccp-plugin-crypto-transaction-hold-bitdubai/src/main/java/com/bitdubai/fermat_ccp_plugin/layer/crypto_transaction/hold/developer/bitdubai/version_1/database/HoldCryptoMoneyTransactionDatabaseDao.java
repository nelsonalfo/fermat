package com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.hold.developer.bitdubai.version_1.database;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_ccp_api.all_definition.enums.CryptoTransactionStatus;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.hold.interfaces.CryptoHoldTransaction;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.hold.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.hold.developer.bitdubai.version_1.exceptions.MissingHoldCryptoDataException;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.hold.developer.bitdubai.version_1.utils.HoldCryptoMoneyTransactionImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Class <code>HoldCryptoMoneyTransactionDatabaseDao</code>
 * contains the logic for handling database the plugin
 * Created by Franklin Marcano on 23/11/15.
 */
public class HoldCryptoMoneyTransactionDatabaseDao {

    private final PluginDatabaseSystem pluginDatabaseSystem;
    private final UUID                 pluginId            ;

    private Database database;

    /**
     * Constructor
     */
    public HoldCryptoMoneyTransactionDatabaseDao(final PluginDatabaseSystem pluginDatabaseSystem,
                                                 final UUID                 pluginId            ) {

        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginId             = pluginId            ;
    }

    private DatabaseTable getDatabaseTable(final String tableName) {

        return database.getTable(tableName);
    }

    private Database openDatabase() throws CantOpenDatabaseException, CantCreateDatabaseException {

        if (database != null)
            return database;

        try {

            database = pluginDatabaseSystem.openDatabase(this.pluginId, HoldCryptoMoneyTransactionDatabaseConstants.HOLD_DATABASE_NAME);
            return database;

        } catch (DatabaseNotFoundException e) {
            HoldCryptoMoneyTransactionDatabaseFactory holdCryptoMoneyTransactionDatabaseFactory = new HoldCryptoMoneyTransactionDatabaseFactory(this.pluginDatabaseSystem);
            database = holdCryptoMoneyTransactionDatabaseFactory.createDatabase(this.pluginId, HoldCryptoMoneyTransactionDatabaseConstants.HOLD_DATABASE_NAME);
            return database;
        }

    }

    private DatabaseTableRecord getHoldCryptoRecord(CryptoHoldTransaction cryptoHoldTransaction) throws DatabaseOperationException {
        DatabaseTable databaseTable = getDatabaseTable(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setUUIDValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TRANSACTION_ID_COLUMN_NAME, cryptoHoldTransaction.getTransactionId());
        record.setStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_ACTOR_PUBLIC_KEY_COLUMN_NAME, cryptoHoldTransaction.getPublicKeyActor());
        record.setStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_WALLET_PUBLIC_KEY_COLUMN_NAME, cryptoHoldTransaction.getPublicKeyWallet());
        record.setStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_PLUGIN_PUBLIC_KEY_COLUMN_NAME, cryptoHoldTransaction.getPublicKeyPlugin());
        record.setFloatValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_AMOUNT_COLUMN_NAME, cryptoHoldTransaction.getAmount());
        record.setStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_CURRENCY_COLUMN_NAME, cryptoHoldTransaction.getCurrency().getCode());
        record.setStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_MEMO_COLUMN_NAME, cryptoHoldTransaction.getMemo());
        record.setLongValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TIMESTAMP_ACKNOWLEDGE_COLUMN_NAME, cryptoHoldTransaction.getTimestampAcknowledged());
        record.setLongValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TIMESTAMP_CONFIRM_REJECT_COLUMN_NAME, cryptoHoldTransaction.getTimestampConfirmedRejected());
        record.setStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_STATUS_COLUMN_NAME, cryptoHoldTransaction.getStatus().getCode());
        record.setStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_BLOCK_CHAIN_NETWORK_TYPE_COLUMN_NAME, cryptoHoldTransaction.getBlockchainNetworkType().getCode());

        return record;
    }

    private boolean isNewRecord(DatabaseTable table, DatabaseTableFilter filter) throws CantLoadTableToMemoryException {
        table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
        table.loadToMemory();

        return table.getRecords().isEmpty();
    }

    private List<DatabaseTableRecord> getHoldCryptoData(DatabaseTableFilter filter) throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TABLE_NAME);

        if (filter != null)
            table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());

        table.loadToMemory();

        return table.getRecords();
    }

    private CryptoHoldTransaction getCryptoHoldTransaction(final DatabaseTableRecord crtyptoHoldTransactionRecord) throws CantLoadTableToMemoryException, DatabaseOperationException, InvalidParameterException {

        HoldCryptoMoneyTransactionImpl holdCryptoMoneyTransaction = new HoldCryptoMoneyTransactionImpl();

        holdCryptoMoneyTransaction.setTransactionId(crtyptoHoldTransactionRecord.getUUIDValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TRANSACTION_ID_COLUMN_NAME));
        holdCryptoMoneyTransaction.setPublicKeyWallet(crtyptoHoldTransactionRecord.getStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_WALLET_PUBLIC_KEY_COLUMN_NAME));
        holdCryptoMoneyTransaction.setPublicKeyActor(crtyptoHoldTransactionRecord.getStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_ACTOR_PUBLIC_KEY_COLUMN_NAME));
        holdCryptoMoneyTransaction.setPublicKeyPlugin(crtyptoHoldTransactionRecord.getStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_PLUGIN_PUBLIC_KEY_COLUMN_NAME));
        holdCryptoMoneyTransaction.setAmount(crtyptoHoldTransactionRecord.getFloatValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_AMOUNT_COLUMN_NAME));
        holdCryptoMoneyTransaction.setCurrency(CryptoCurrency.getByCode(crtyptoHoldTransactionRecord.getStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_CURRENCY_COLUMN_NAME)));
        holdCryptoMoneyTransaction.setMemo(crtyptoHoldTransactionRecord.getStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_MEMO_COLUMN_NAME));
        holdCryptoMoneyTransaction.setTimestampAcknowledged(crtyptoHoldTransactionRecord.getLongValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TIMESTAMP_ACKNOWLEDGE_COLUMN_NAME));
        holdCryptoMoneyTransaction.setTimestampConfirmedRejected(crtyptoHoldTransactionRecord.getLongValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TIMESTAMP_ACKNOWLEDGE_COLUMN_NAME));
        holdCryptoMoneyTransaction.setStatus(CryptoTransactionStatus.getByCode(crtyptoHoldTransactionRecord.getStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_STATUS_COLUMN_NAME)));
        holdCryptoMoneyTransaction.setBlockchainNetworkType(BlockchainNetworkType.getByCode(crtyptoHoldTransactionRecord.getStringValue(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_BLOCK_CHAIN_NETWORK_TYPE_COLUMN_NAME)));

        return holdCryptoMoneyTransaction;
    }

    public void saveHoldCryptoTransactionData(CryptoHoldTransaction cryptoHoldTransaction) throws DatabaseOperationException, MissingHoldCryptoDataException {

        try
        {
            database = openDatabase();
            DatabaseTransaction transaction = database.newTransaction();

            DatabaseTable table = getDatabaseTable(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TABLE_NAME);
            DatabaseTableRecord holdCryptoRecord = getHoldCryptoRecord(cryptoHoldTransaction);
            DatabaseTableFilter filter = table.getEmptyTableFilter();
            filter.setType(DatabaseFilterType.EQUAL);
            filter.setValue(cryptoHoldTransaction.getTransactionId().toString());
            filter.setColumn(HoldCryptoMoneyTransactionDatabaseConstants.HOLD_TRANSACTION_ID_COLUMN_NAME);

            if (isNewRecord(table, filter))
                transaction.addRecordToInsert(table, holdCryptoRecord);
            else {
                table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                transaction.addRecordToUpdate(table, holdCryptoRecord);
            }

            //I execute the transaction and persist the database side of the asset.
            database.executeTransaction(transaction);

        }catch (Exception e) {
            throw new DatabaseOperationException(DatabaseOperationException.DEFAULT_MESSAGE, e, "Error trying to save the Hold Crypto Transaction in the database.", null);
        }
    }

    public List<CryptoHoldTransaction> getHoldCryptoTransactionList(DatabaseTableFilter filter) throws DatabaseOperationException, InvalidParameterException
    {
        try {
            database = openDatabase();
            List<CryptoHoldTransaction> cryptoHoldTransactions = new ArrayList<>();
            // I will add the Asset Factory information from the database
            for (DatabaseTableRecord holdCryptokRecord : getHoldCryptoData(filter)) {
                final CryptoHoldTransaction cryptoHoldTransaction = getCryptoHoldTransaction(holdCryptokRecord);

                cryptoHoldTransactions.add(cryptoHoldTransaction);
            }

            return cryptoHoldTransactions;
        }
        catch (Exception e) {
            throw new DatabaseOperationException(DatabaseOperationException.DEFAULT_MESSAGE, e, "error trying to get Hold Crypto Transaction from the database with filter: " + filter.toString(), null);
        }
    }
}
