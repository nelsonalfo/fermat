package com.bitdubai.fermat_dap_plugin.layer.wallet.asset.issuer.developer.bitdubai.version_1.structure.database;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.util.XMLParser;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterOrder;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.DealsWithPluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAsset;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.AssetCurrentStatus;
import com.bitdubai.fermat_dap_api.layer.all_definition.util.Validate;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_issuer.exceptions.CantGetAssetStatisticException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.RecordsNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantRegisterDebitException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.exceptions.CantSaveStatisticException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces.AssetIssuerWalletList;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces.AssetIssuerWalletTransaction;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces.AssetIssuerWalletTransactionRecord;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_issuer_wallet.interfaces.AssetIssuerWalletTransactionSummary;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.TransactionType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantGetActorTransactionSummaryException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantGetTransactionsException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantStoreMemoException;
import com.bitdubai.fermat_dap_plugin.layer.wallet.asset.issuer.developer.bitdubai.version_1.structure.AssetIssuerWalletBalance;
import com.bitdubai.fermat_dap_plugin.layer.wallet.asset.issuer.developer.bitdubai.version_1.structure.AssetIssuerWalletTransactionWrapper;
import com.bitdubai.fermat_dap_plugin.layer.wallet.asset.issuer.developer.bitdubai.version_1.structure.exceptions.CantExecuteAssetIssuerTransactionException;
import com.bitdubai.fermat_dap_plugin.layer.wallet.asset.issuer.developer.bitdubai.version_1.structure.exceptions.CantFindTransactionException;
import com.bitdubai.fermat_dap_plugin.layer.wallet.asset.issuer.developer.bitdubai.version_1.structure.exceptions.CantGetBalanceRecordException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by franklin on 28/09/15.
 */
public class AssetIssuerWalletDao implements DealsWithPluginFileSystem {
    //TODO: Manejo de excepciones
    public static final String PATH_DIRECTORY = "asset-issuer-swap/";//digital-asset-swap/"
    PluginFileSystem pluginFileSystem;
    UUID plugin;

    @Override
    public void setPluginFileSystem(PluginFileSystem pluginFileSystem) {
        this.pluginFileSystem = pluginFileSystem;
    }

    public void setPlugin(UUID plugin) {
        this.plugin = plugin;
    }

    private Database database;

    public AssetIssuerWalletDao(Database database) {
        this.database = database;
    }


    /*
     * getBookBalance must get actual Book Balance global of Asset Issuer wallet, select record from balances table
     */
    public long getBookBalance() throws CantCalculateBalanceException {
        try {
            return getCurrentBookBalance();
        } catch (CantGetBalanceRecordException exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, exception, null, "Check the cause");
        } catch (Exception exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");

        }
    }

    /*
     * getBookBalance must get actual Book Balance global of Asset Issuer wallet, select record from balances table
     */
    public List<AssetIssuerWalletList> getBalanceByAssets() throws CantCalculateBalanceException {
        try {
            return getCurrentBalanceByAssets();
        } catch (CantGetBalanceRecordException exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, exception, null, "Check the cause");
        } catch (Exception exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");

        }
    }

    /*
    * getBookBalance must get actual Book Balance global of Asset Issuer wallet, select record from balances table
    */
    public long getAvailableBalance() throws CantCalculateBalanceException {
        try {
            return getCurrentAvailableBalance();
        } catch (CantGetBalanceRecordException exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, exception, null, "Check the cause");
        } catch (Exception exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }
    }

    /*
    * getBalanceByAsset must get actual Balance global of Asset Issuer wallet, select record from balances table
    */
    public List<AssetIssuerWalletList> getBalanceByAsset() throws CantCalculateBalanceException {
        try {
            return getCurrentBalanceByAssets();
        } catch (CantGetBalanceRecordException exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, exception, null, "Check the cause");
        } catch (Exception exception) {
            throw new CantCalculateBalanceException(CantCalculateBalanceException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }
    }

    /*
    * Add a new debit transaction.
    */
    public void addDebit(final AssetIssuerWalletTransactionRecord assetIssuerWalletTransactionRecord, final BalanceType balanceType) throws CantRegisterDebitException {
        try {
            System.out.println("Agregando Debito-----------------------------------------------------------");
            if (isTransactionInTable(assetIssuerWalletTransactionRecord.getIdTransaction(), TransactionType.DEBIT, balanceType))
                throw new CantRegisterDebitException(CantRegisterDebitException.DEFAULT_MESSAGE, null, null, "The transaction is already in the database");

            long availableAmount = balanceType.equals(BalanceType.AVAILABLE) ? assetIssuerWalletTransactionRecord.getAmount() : 0L;
            long bookAmount = balanceType.equals(BalanceType.BOOK) ? assetIssuerWalletTransactionRecord.getAmount() : 0L;
            long availableRunningBalance = calculateAvailableRunningBalanceByAsset(-availableAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());
            long bookRunningBalance = calculateBookRunningBalanceByAsset(-bookAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());

            long quantityAvailableAmount = balanceType.equals(BalanceType.AVAILABLE) ? 1 : 0L;
            long quantityBookAmount = balanceType.equals(BalanceType.BOOK) ? 1 : 0L;
            long quantityAvailableRunningBalance = calculateQuantityAvailableRunningBalanceByAsset(-quantityAvailableAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());
            long quantityBookRunningBalance = calculateQuantityBookRunningBalanceByAsset(-quantityBookAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());

            executeTransaction(assetIssuerWalletTransactionRecord, TransactionType.DEBIT, balanceType, availableRunningBalance, bookRunningBalance, quantityAvailableRunningBalance, quantityBookRunningBalance);
        } catch (CantGetBalanceRecordException | CantLoadTableToMemoryException | CantExecuteAssetIssuerTransactionException exception) {
            throw new CantRegisterDebitException(CantRegisterDebitException.DEFAULT_MESSAGE, exception, null, "Check the cause");
        } catch (Exception exception) {
            throw new CantRegisterDebitException(CantRegisterDebitException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }
    }

    /*
    * Add a new credit transaction.
    */
    public void addCredit(final AssetIssuerWalletTransactionRecord assetIssuerWalletTransactionRecord, final BalanceType balanceType) throws CantRegisterCreditException {

        try {
            System.out.println("Agregando Credito-----------------------------------------------------------");
            if (isTransactionInTable(assetIssuerWalletTransactionRecord.getIdTransaction(), TransactionType.CREDIT, balanceType))
                throw new CantRegisterCreditException(CantRegisterCreditException.DEFAULT_MESSAGE, null, null, "The transaction is already in the database");

            long availableAmount = balanceType.equals(BalanceType.AVAILABLE) ? assetIssuerWalletTransactionRecord.getAmount() : 0L;
            long bookAmount = balanceType.equals(BalanceType.BOOK) ? assetIssuerWalletTransactionRecord.getAmount() : 0L;
            long availableRunningBalance = calculateAvailableRunningBalanceByAsset(availableAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());
            long bookRunningBalance = calculateBookRunningBalanceByAsset(bookAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());

            long quantityAvailableAmount = balanceType.equals(BalanceType.AVAILABLE) ? 1 : 0L;
            long quantityBookAmount = balanceType.equals(BalanceType.BOOK) ? 1 : 0L;
            long quantityAvailableRunningBalance = calculateQuantityAvailableRunningBalanceByAsset(quantityAvailableAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());
            long quantityBookRunningBalance = calculateQuantityBookRunningBalanceByAsset(quantityBookAmount, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());

            executeTransaction(assetIssuerWalletTransactionRecord, TransactionType.CREDIT, balanceType, availableRunningBalance, bookRunningBalance, quantityAvailableRunningBalance, quantityBookRunningBalance);
        } catch (CantGetBalanceRecordException | CantLoadTableToMemoryException | CantExecuteAssetIssuerTransactionException exception) {
            throw new CantRegisterCreditException(CantRegisterCreditException.DEFAULT_MESSAGE, exception, null, "Check the cause");
        } catch (Exception exception) {
            throw new CantRegisterCreditException(CantRegisterCreditException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }
    }

    public List<AssetIssuerWalletTransaction> listsTransactionsByAssetsAll(BalanceType balanceType, TransactionType transactionType, String assetPublicKey) throws CantGetTransactionsException {
        try {
            DatabaseTable databaseTableAssuerIssuerWallet = getAssetIssuerWalletTable();
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ASSET_PUBLIC_KEY_COLUMN_NAME, assetPublicKey, DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode(), DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TYPE_COLUMN_NAME, transactionType.getCode(), DatabaseFilterType.EQUAL);

            databaseTableAssuerIssuerWallet.loadToMemory();
            return createTransactionList(databaseTableAssuerIssuerWallet.getRecords());
        } catch (CantLoadTableToMemoryException cantLoadTableToMemory) {
            throw new CantGetTransactionsException("Get List of All Transactions", cantLoadTableToMemory, "Error load wallet table ", "");
        } catch (Exception exception) {
            throw new CantGetTransactionsException(CantGetTransactionsException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }
    }

    public List<AssetIssuerWalletTransaction> listsTransactionsByAssets(BalanceType balanceType, TransactionType transactionType, int max, int offset, String assetPublicKey) throws CantGetTransactionsException {
        try {
            DatabaseTable databaseTableAssuerIssuerWallet = getAssetIssuerWalletTable();
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ASSET_PUBLIC_KEY_COLUMN_NAME, assetPublicKey, DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode(), DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.setFilterTop(String.valueOf(max));
            databaseTableAssuerIssuerWallet.setFilterOffSet(String.valueOf(offset));

            databaseTableAssuerIssuerWallet.loadToMemory();
            return createTransactionList(databaseTableAssuerIssuerWallet.getRecords());
        } catch (CantLoadTableToMemoryException cantLoadTableToMemory) {
            throw new CantGetTransactionsException("Get List of Transactions", cantLoadTableToMemory, "Error load wallet table ", "");
        } catch (Exception exception) {
            throw new CantGetTransactionsException(CantGetTransactionsException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }
    }

    public List<AssetIssuerWalletTransaction> getTransactionsByActor(String actorPublicKey, BalanceType balanceType, int max, int offset) throws CantGetTransactionsException {
        try {
            DatabaseTable databaseTableAssuerIssuerWallet = getAssetIssuerWalletTable();

            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_FROM_COLUMN_NAME, actorPublicKey, DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_TO_COLUMN_NAME, actorPublicKey, DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode(), DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.addFilterOrder(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TIME_STAMP_COLUMN_NAME, DatabaseFilterOrder.DESCENDING);

            databaseTableAssuerIssuerWallet.setFilterTop(String.valueOf(max));
            databaseTableAssuerIssuerWallet.setFilterOffSet(String.valueOf(offset));

            databaseTableAssuerIssuerWallet.loadToMemory();

            return createTransactionList(databaseTableAssuerIssuerWallet.getRecords());
        } catch (CantLoadTableToMemoryException cantLoadTableToMemory) {
            throw new CantGetTransactionsException("Get List of Transactions", cantLoadTableToMemory, "Error load wallet table ", "");
        } catch (Exception exception) {
            throw new CantGetTransactionsException(CantGetTransactionsException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }

    }

    public List<AssetIssuerWalletTransaction> getTransactionsByTransactionType(TransactionType transactionType, int max, int offset) throws CantGetTransactionsException {
        try {
            DatabaseTable databaseTableAssuerIssuerWallet = getAssetIssuerWalletTable();

            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TYPE_COLUMN_NAME, transactionType.getCode(), DatabaseFilterType.EQUAL);
            databaseTableAssuerIssuerWallet.addFilterOrder(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TIME_STAMP_COLUMN_NAME, DatabaseFilterOrder.DESCENDING);

            databaseTableAssuerIssuerWallet.setFilterTop(String.valueOf(max));
            databaseTableAssuerIssuerWallet.setFilterOffSet(String.valueOf(offset));

            databaseTableAssuerIssuerWallet.loadToMemory();

            return createTransactionList(databaseTableAssuerIssuerWallet.getRecords());
        } catch (CantLoadTableToMemoryException cantLoadTableToMemory) {
            throw new CantGetTransactionsException("Get List of Transactions", cantLoadTableToMemory, "Error load wallet table ", "");
        } catch (Exception exception) {
            throw new CantGetTransactionsException(CantGetTransactionsException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }

    }

    public AssetIssuerWalletTransactionSummary getActorTransactionSummary(String actorPublicKey, BalanceType balanceType) throws CantGetActorTransactionSummaryException {
        return null;
    }

    public List<AssetIssuerWalletTransaction> distributeAssets(String assetPublicKey) throws CantGetTransactionsException {
        try {
            List<AssetIssuerWalletTransaction> assetIssuerWalletTransactions = listsTransactionsByAsset(assetPublicKey);
            return assetIssuerWalletTransactions;
        } catch (CantGetTransactionsException e) {
            throw new CantGetTransactionsException("Get List of Transactions", e, "Error load wallet table ", "Method: distributeAssets()");
        }

    }

    public void updateMemoField(UUID transactionID, String memo) throws CantStoreMemoException, CantFindTransactionException {
        try {
            DatabaseTable databaseTableAssuerIssuerWalletBalance = getBalancesTable();
            /**
             *  I will load the information of table into a memory structure, filter for transaction id
             */

            databaseTableAssuerIssuerWalletBalance.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TABLE_ID_COLUMN_NAME, transactionID.toString(), DatabaseFilterType.EQUAL);
            for (DatabaseTableRecord record : databaseTableAssuerIssuerWalletBalance.getRecords()) {
                record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_MEMO_COLUMN_NAME, memo);
                databaseTableAssuerIssuerWalletBalance.updateRecord(record);
            }
            databaseTableAssuerIssuerWalletBalance.loadToMemory();
        } catch (CantLoadTableToMemoryException cantLoadTableToMemory) {
            throw new CantFindTransactionException("Transaction Memo Update Error", cantLoadTableToMemory, "Error load Transaction table" + transactionID.toString(), "");

        } catch (CantUpdateRecordException cantUpdateRecord) {
            throw new CantStoreMemoException("Transaction Memo Update Error", cantUpdateRecord, "Error update memo of Transaction " + transactionID.toString(), "");
        } catch (Exception exception) {
            throw new CantStoreMemoException(CantStoreMemoException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, null);
        }
    }

    private List<AssetIssuerWalletTransaction> listsTransactionsByAsset(String assetPublicKey) throws CantGetTransactionsException {
        try {
            DatabaseTable databaseTableAssuerIssuerWallet = getAssetIssuerWalletTable();
            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ASSET_PUBLIC_KEY_COLUMN_NAME, assetPublicKey, DatabaseFilterType.EQUAL);

            databaseTableAssuerIssuerWallet.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME, BalanceType.AVAILABLE.getCode(), DatabaseFilterType.EQUAL);

            databaseTableAssuerIssuerWallet.loadToMemory();
            return createTransactionList(databaseTableAssuerIssuerWallet.getRecords());
        } catch (CantLoadTableToMemoryException cantLoadTableToMemory) {
            throw new CantGetTransactionsException("Get List of Transactions", cantLoadTableToMemory, "Error load wallet table ", "");
        } catch (Exception exception) {
            throw new CantGetTransactionsException(CantGetTransactionsException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, "Check the cause");
        }
    }

    // Read record data and create transactions list
    private List<AssetIssuerWalletTransaction> createTransactionList(final Collection<DatabaseTableRecord> records) {

        List<AssetIssuerWalletTransaction> transactions = new ArrayList<>();

        for (DatabaseTableRecord record : records)
            transactions.add(constructAssetIssuerWalletTransactionFromRecord(record));

        return transactions;
    }


    private AssetIssuerWalletTransaction constructAssetIssuerWalletTransactionFromRecord(DatabaseTableRecord record) {

        String transactionId = record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_VERIFICATION_ID_COLUMN_NAME);
        String assetPublicKey = record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_ASSET_PUBLIC_KEY_COLUMN_NAME);
        String transactionHash = record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TRANSACTION_HASH_COLUMN_NAME);
        TransactionType transactionType = TransactionType.getByCode(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME));
        CryptoAddress addressFrom = new CryptoAddress();
        addressFrom.setAddress(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ADDRESS_FROM_COLUMN_NAME));
        CryptoAddress addressTo = new CryptoAddress();
        addressTo.setAddress(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ADDRESS_TO_COLUMN_NAME));
        String actorFromPublicKey = record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_FROM_COLUMN_NAME);
        String actorToPublicKey = record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_TO_COLUMN_NAME);
        Actors actorFromType = Actors.getByCode(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_FROM_TYPE_COLUMN_NAME));
        Actors actorToType = Actors.getByCode(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_TO_TYPE_COLUMN_NAME));
        BalanceType balanceType = BalanceType.getByCode(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME));
        long amount = record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_AMOUNT_COLUMN_NAME);
        long runningBookBalance = record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_RUNNING_BOOK_BALANCE_COLUMN_NAME);
        long runningAvailableBalance = record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_RUNNING_AVAILABLE_BALANCE_COLUMN_NAME);
        long timeStamp = record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TIME_STAMP_COLUMN_NAME);
        String memo = record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_MEMO_COLUMN_NAME);
        return new AssetIssuerWalletTransactionWrapper(transactionId, transactionHash, assetPublicKey, transactionType, addressFrom, addressTo,
                actorFromPublicKey, actorToPublicKey, actorFromType, actorToType, balanceType, amount, runningBookBalance, runningAvailableBalance, timeStamp, memo);
    }

    private void executeTransaction(final AssetIssuerWalletTransactionRecord assetIssuerWalletTransactionRecord, final TransactionType transactionType, final BalanceType balanceType, final long availableRunningBalance, final long bookRunningBalance, final long quantityAvailableRunningBalance, final long quantityBookRunningBalance) throws CantExecuteAssetIssuerTransactionException {
        try {
            DatabaseTableRecord assetIssuerWalletRecord = constructAssetIssuerWalletRecord(assetIssuerWalletTransactionRecord, transactionType, balanceType, availableRunningBalance, bookRunningBalance);
            DatabaseTableRecord assetBalanceRecord = constructAssetBalanceRecord(assetIssuerWalletTransactionRecord.getDigitalAsset(), availableRunningBalance, bookRunningBalance, quantityAvailableRunningBalance, quantityBookRunningBalance);
            DatabaseTransaction transaction = database.newTransaction();
            transaction.addRecordToInsert(getAssetIssuerWalletTable(), assetIssuerWalletRecord);

            DatabaseTable databaseTable = getBalancesTable();
            databaseTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_ASSET_PUBLIC_KEY_COLUMN_NAME, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey(), DatabaseFilterType.EQUAL);
            databaseTable.loadToMemory();
            if (databaseTable.getRecords().isEmpty()) {
                transaction.addRecordToInsert(databaseTable, assetBalanceRecord);
                String digitalAssetInnerXML = assetIssuerWalletTransactionRecord.getDigitalAsset().toString();
                PluginTextFile pluginTextFile = pluginFileSystem.createTextFile(plugin, PATH_DIRECTORY, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey(), FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
                pluginTextFile.setContent(digitalAssetInnerXML);
                pluginTextFile.persistToMedia();

                /**
                 * I'm also saving to file the DigitalAssetMetadata of this digital Asset.
                 */
                String digitalAssetMetadataFilename = assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey() + "_metadata";
                String digitalAssetMetadataXML = assetIssuerWalletTransactionRecord.getDigitalAssetMetadata().toString();
                pluginTextFile = pluginFileSystem.createTextFile(plugin, PATH_DIRECTORY, digitalAssetMetadataFilename, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
                pluginTextFile.setContent(digitalAssetMetadataXML);
                pluginTextFile.persistToMedia();

            } else {
                transaction.addRecordToUpdate(databaseTable, assetBalanceRecord);
            }

            database.executeTransaction(transaction);
            database.closeDatabase();

        } catch (Exception e) {
            e.printStackTrace();
            throw new CantExecuteAssetIssuerTransactionException("Error to get balances record", e, "Can't load balance table", "");
        }
    }

    private DatabaseTableRecord constructAssetBalanceRecord(DigitalAsset digitalAsset, long availableRunningBalance, long bookRunningBalance, long quantityAvailableRunningBalance, long quantityBookRunningBalance) {

        DatabaseTableRecord record = getBalancesTable().getEmptyRecord();
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_ASSET_PUBLIC_KEY_COLUMN_NAME, digitalAsset.getPublicKey());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_NAME_COLUMN_NAME, digitalAsset.getName());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_DESCRIPTION_COLUMN_NAME, digitalAsset.getDescription());
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_AVAILABLE_BALANCE_COLUMN_NAME, availableRunningBalance);
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_BOOK_BALANCE_COLUMN_NAME, bookRunningBalance);
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_QUANTITY_AVAILABLE_BALANCE_COLUMN_NAME, quantityAvailableRunningBalance);
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_QUANTITY_BOOK_BALANCE_COLUMN_NAME, quantityBookRunningBalance);
        return record;

    }

    private DatabaseTableRecord constructAssetIssuerWalletRecord(final AssetIssuerWalletTransactionRecord assetIssuerWalletTransactionRecord, final TransactionType transactionType, final BalanceType balanceType, final long availableRunningBalance, final long bookRunningBalance) {
        DatabaseTableRecord record = getAssetIssuerWalletTable().getEmptyRecord();
        record.setUUIDValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TABLE_ID_COLUMN_NAME, UUID.randomUUID());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ASSET_PUBLIC_KEY_COLUMN_NAME, assetIssuerWalletTransactionRecord.getDigitalAsset().getPublicKey());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_VERIFICATION_ID_COLUMN_NAME, assetIssuerWalletTransactionRecord.getIdTransaction());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TYPE_COLUMN_NAME, transactionType.getCode());
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_AMOUNT_COLUMN_NAME, assetIssuerWalletTransactionRecord.getAmount());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_MEMO_COLUMN_NAME, assetIssuerWalletTransactionRecord.getMemo());
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TIME_STAMP_COLUMN_NAME, assetIssuerWalletTransactionRecord.getTimestamp());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TRANSACTION_HASH_COLUMN_NAME, assetIssuerWalletTransactionRecord.getDigitalAssetMetadataHash());

        if (assetIssuerWalletTransactionRecord.getAddressFrom() != null)
            record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ADDRESS_FROM_COLUMN_NAME, assetIssuerWalletTransactionRecord.getAddressFrom().getAddress());

        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ADDRESS_TO_COLUMN_NAME, assetIssuerWalletTransactionRecord.getAddressTo().getAddress());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode());
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_RUNNING_AVAILABLE_BALANCE_COLUMN_NAME, availableRunningBalance);
        record.setLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_RUNNING_BOOK_BALANCE_COLUMN_NAME, bookRunningBalance);
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_FROM_COLUMN_NAME, assetIssuerWalletTransactionRecord.getActorFromPublicKey());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_TO_COLUMN_NAME, assetIssuerWalletTransactionRecord.getActorToPublicKey());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_FROM_TYPE_COLUMN_NAME, assetIssuerWalletTransactionRecord.getActorFromType().getCode());
        record.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_ACTOR_TO_TYPE_COLUMN_NAME, assetIssuerWalletTransactionRecord.getActorToType().getCode());
        return record;
    }

    private long calculateAvailableRunningBalanceByAsset(final long transactionAmount, String assetPublicKey) throws CantGetBalanceRecordException {
        return getCurrentAvailableBalanceByAsset(assetPublicKey) + transactionAmount;
    }

    private long calculateQuantityAvailableRunningBalanceByAsset(final long transactionAmount, String assetPublicKey) throws CantGetBalanceRecordException {
        return getQuantityCurrentAvailableBalanceByAsset(assetPublicKey) + transactionAmount;
    }


    private long calculateBookRunningBalanceByAsset(final long transactionAmount, String assetPublicKey) throws CantGetBalanceRecordException {
        return getCurrentBookBalanceByAsset(assetPublicKey) + transactionAmount;
    }

    private long getCurrentBookBalanceByAsset(String assetPublicKey) throws CantGetBalanceRecordException {
        return getCurrentBalanceByAsset(BalanceType.BOOK, assetPublicKey);
    }

    private long calculateQuantityBookRunningBalanceByAsset(final long transactionAmount, String assetPublicKey) throws CantGetBalanceRecordException {
        return getCurrentQuantityBookBalanceByAsset(assetPublicKey) + transactionAmount;
    }

    private long getCurrentQuantityBookBalanceByAsset(String assetPublicKey) throws CantGetBalanceRecordException {
        return getQuantityCurrentBalanceByAsset(BalanceType.BOOK, assetPublicKey);
    }

    private boolean isTransactionInTable(final String transactionId, final TransactionType transactionType, final BalanceType balanceType) throws CantLoadTableToMemoryException {
        DatabaseTable assetIssuerWalletTable = getAssetIssuerWalletTable();
        assetIssuerWalletTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_VERIFICATION_ID_COLUMN_NAME, transactionId, DatabaseFilterType.EQUAL);
        assetIssuerWalletTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TYPE_COLUMN_NAME, transactionType.getCode(), DatabaseFilterType.EQUAL);
        assetIssuerWalletTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode(), DatabaseFilterType.EQUAL);
        assetIssuerWalletTable.loadToMemory();
        return !assetIssuerWalletTable.getRecords().isEmpty();
    }

    private DatabaseTable getAssetIssuerWalletTable() {
        DatabaseTable databaseTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_TABLE_NAME);
        return databaseTable;
    }

    private long getCurrentAvailableBalance() throws CantGetBalanceRecordException {
        return getCurrentBalance(BalanceType.AVAILABLE);
    }

    private long getCurrentAvailableBalanceByAsset(String assetPublicKey) throws CantGetBalanceRecordException {
        return getCurrentBalanceByAsset(BalanceType.AVAILABLE, assetPublicKey);
    }

    private long getQuantityCurrentAvailableBalanceByAsset(String assetPublicKey) throws CantGetBalanceRecordException {
        return getQuantityCurrentBalanceByAsset(BalanceType.AVAILABLE, assetPublicKey);
    }

    private long getCurrentBalanceByAsset(BalanceType balanceType, String assetPublicKey) {
        long balanceAmount = 0;
        try {

            if (balanceType == BalanceType.AVAILABLE)
                balanceAmount = getBalancesByAssetRecord(assetPublicKey).getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_AVAILABLE_BALANCE_COLUMN_NAME);
            else
                balanceAmount = getBalancesByAssetRecord(assetPublicKey).getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_BOOK_BALANCE_COLUMN_NAME);

            return balanceAmount;
        } catch (Exception exception) {
            return balanceAmount;
        }
    }

    private long getQuantityCurrentBalanceByAsset(BalanceType balanceType, String assetPublicKey) {
        long balanceAmount = 0;
        try {
            if (balanceType == BalanceType.AVAILABLE)
                balanceAmount = getBalancesByAssetRecord(assetPublicKey).getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_QUANTITY_AVAILABLE_BALANCE_COLUMN_NAME);
            else
                balanceAmount = getBalancesByAssetRecord(assetPublicKey).getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_QUANTITY_BOOK_BALANCE_COLUMN_NAME);
            return balanceAmount;
        } catch (Exception exception) {
            return balanceAmount;
        }
    }

    private long getCurrentBookBalance() throws CantGetBalanceRecordException {
        return getCurrentBalance(BalanceType.BOOK);
    }

    private List<AssetIssuerWalletList> getCurrentBalanceByAssets() throws CantGetBalanceRecordException {
        return getCurrentBalanceByAsset();
    }

    private long getCurrentBalance(final BalanceType balanceType) throws CantGetBalanceRecordException {
        long balanceAmount = 0;
        if (balanceType == BalanceType.AVAILABLE) {
            for (DatabaseTableRecord record : getBalancesRecord()) {
                balanceAmount += record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_AVAILABLE_BALANCE_COLUMN_NAME);
            }
        } else {
            for (DatabaseTableRecord record : getBalancesRecord()) {
                balanceAmount += record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_BOOK_BALANCE_COLUMN_NAME);
            }
        }
        return balanceAmount;
    }

    private List<AssetIssuerWalletList> getCurrentBalanceByAsset() throws CantGetBalanceRecordException {
        List<AssetIssuerWalletList> issuerWalletBalances = new ArrayList<>();
        for (DatabaseTableRecord record : getBalancesRecord()) {
            AssetIssuerWalletList assetIssuerWalletBalance = new AssetIssuerWalletBalance();
            assetIssuerWalletBalance.setName(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_NAME_COLUMN_NAME));
            assetIssuerWalletBalance.setDescription(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_DESCRIPTION_COLUMN_NAME));
            assetIssuerWalletBalance.setAssetPublicKey(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_ASSET_PUBLIC_KEY_COLUMN_NAME));
            assetIssuerWalletBalance.setBookBalance(record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_BOOK_BALANCE_COLUMN_NAME));
            assetIssuerWalletBalance.setAvailableBalance(record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_AVAILABLE_BALANCE_COLUMN_NAME));
            assetIssuerWalletBalance.setQuantityBookBalance(record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_QUANTITY_BOOK_BALANCE_COLUMN_NAME));
            assetIssuerWalletBalance.setQuantityAvailableBalance(record.getLongValue(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_QUANTITY_AVAILABLE_BALANCE_COLUMN_NAME));
            issuerWalletBalances.add(assetIssuerWalletBalance);
        }
        return issuerWalletBalances;
    }

    private List<DatabaseTableRecord> getBalancesRecord() throws CantGetBalanceRecordException {
        try {
            DatabaseTable balancesTable = getBalancesTable();
            balancesTable.loadToMemory();
            return balancesTable.getRecords();
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetBalanceRecordException("Error to get balances record", exception, "Can't load balance table", "");
        }
    }

    private DatabaseTableRecord getBalancesByAssetRecord(String assetPublicKey) throws CantGetBalanceRecordException {
        try {
            DatabaseTable balancesTable = getBalancesTable();//database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_NAME);;
            balancesTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_ASSET_PUBLIC_KEY_COLUMN_NAME, assetPublicKey, DatabaseFilterType.EQUAL);
            balancesTable.loadToMemory();
            if (!balancesTable.getRecords().isEmpty()) {
                return balancesTable.getRecords().get(0);
            } else {
                //return balancesTable.getEmptyRecord();
                return balancesTable.getRecords().get(0);
            }
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetBalanceRecordException("Error to get balances record", exception, "Can't load balance table", "");
        }
    }

    private DatabaseTable getBalancesTable() {
        DatabaseTable databaseTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_NAME);
        return databaseTable; //database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_WALLET_ISSUER_BALANCE_TABLE_NAME);
    }

    // ************** ASSET STATISTIC TABLE METHODS ***************

    // PUBLIC METHODS
    public void createdNewAsset(DigitalAsset asset) throws CantSaveStatisticException {
        String context = "Asset: " + asset;
        try {
            DatabaseTable databaseTable = this.database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            DatabaseTableRecord assetStatisticRecord = databaseTable.getEmptyRecord();

            assetStatisticRecord.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_PUBLIC_KEY_COLUMN_NAME, asset.getPublicKey());
            assetStatisticRecord.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_NAME_COLUMN_NAME, asset.getName());
            assetStatisticRecord.setStringValue(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_CURRENT_STATUS_COLUMN_NAME, AssetCurrentStatus.ASSET_CREATED.getCode());

            databaseTable.insertRecord(assetStatisticRecord);
        } catch (CantInsertRecordException exception) {
            throw new CantSaveStatisticException(exception, context, "Cannot insert a record in Asset Statistic Table");
        }
    }

    public void assetDistributed(String assetPublicKey, String actorAssetUserPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException {
        updateStringFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_CURRENT_STATUS_COLUMN_NAME, AssetCurrentStatus.ASSET_UNUSED.getCode(), assetPublicKey);
        updateStringFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ACTOR_USER_PUBLIC_KEY_COLUMN_NAME, actorAssetUserPublicKey, assetPublicKey);
        updateLongFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_DISTRIBUTION_DATE_COLUMN_NAME, System.currentTimeMillis(), assetPublicKey);
    }

    //Even when I already stored the user public key when I distributed the asset, there's a chance that the user that redeemed the asset is a different one.
    public void assetRedeemed(String assetPublicKey, String userPublicKey, String redeemPointPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException {
        updateStringFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_CURRENT_STATUS_COLUMN_NAME, AssetCurrentStatus.ASSET_REDEEMED.getCode(), assetPublicKey);
        updateStringFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_REDEEM_POINT_PUBLIC_KEY_COLUMN_NAME, redeemPointPublicKey, assetPublicKey);
        updateStringFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ACTOR_USER_PUBLIC_KEY_COLUMN_NAME, userPublicKey, assetPublicKey);
        updateLongFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_USAGE_DATE_COLUMN_NAME, System.currentTimeMillis(), assetPublicKey);
    }

    //Even when I already stored the user public key when I distributed the asset, there's a chance that the user that appropriated the asset is a different one.
    public void assetAppropriated(String assetPublicKey, String userPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException {
        updateStringFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_CURRENT_STATUS_COLUMN_NAME, AssetCurrentStatus.ASSET_APPROPRIATED.getCode(), assetPublicKey);
        updateStringFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ACTOR_USER_PUBLIC_KEY_COLUMN_NAME, userPublicKey, assetPublicKey);
        updateLongFieldByAssetPublicKey(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_USAGE_DATE_COLUMN_NAME, System.currentTimeMillis(), assetPublicKey);
    }

    public DigitalAsset getAssetByPublicKey(String assetPublicKey) {
        try {
            return (DigitalAsset) XMLParser.parseXML(pluginFileSystem.getTextFile(plugin, PATH_DIRECTORY, assetPublicKey, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT).getContent(), new DigitalAsset());
        } catch (FileNotFoundException | CantCreateFileException e) {
            return null;
        }
    }

    //Methods for construct the AssetStatistic object.
    public String getUserPublicKey(String assetPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException {
        return getAssetStatisticStringFieldByPk(assetPublicKey, AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ACTOR_USER_PUBLIC_KEY_COLUMN_NAME);
    }

    public String getRedeemPointPublicKey(String assetPublicKey) throws RecordsNotFoundException, CantGetAssetStatisticException {
        return getAssetStatisticStringFieldByPk(assetPublicKey, AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_REDEEM_POINT_PUBLIC_KEY_COLUMN_NAME);
    }

    public AssetCurrentStatus getStatus(String assetPublicKey) {
        try {
            return AssetCurrentStatus.getByCode(getAssetStatisticStringFieldByPk(assetPublicKey, AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_CURRENT_STATUS_COLUMN_NAME));
        } catch (InvalidParameterException | CantGetAssetStatisticException | RecordsNotFoundException e) {
            return AssetCurrentStatus.ASSET_CREATED;
        }
    }

    public String getAssetName(String assetPublicKey) {
        try {
            return getAssetStatisticStringFieldByPk(assetPublicKey, AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_NAME_COLUMN_NAME);
        } catch (CantGetAssetStatisticException | RecordsNotFoundException e) {
            return Validate.DEFAULT_STRING;
        }
    }

    public Date getDistributionDate(String assetPublicKey) {
        try {
            return new Date(getAssetStatisticLongFieldByPk(assetPublicKey, AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_DISTRIBUTION_DATE_COLUMN_NAME));
        } catch (CantGetAssetStatisticException | RecordsNotFoundException e) {
            return null;
        }
    }

    public Date getUsageDate(String assetPublicKey) {
        try {
            return new Date(getAssetStatisticLongFieldByPk(assetPublicKey, AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_USAGE_DATE_COLUMN_NAME));
        } catch (CantGetAssetStatisticException | RecordsNotFoundException e) {
            return null;
        }
    }

    //Query Methods.
    public List<String> getAllAssetPublicKeyForAssetName(String assetName) throws CantGetAssetStatisticException {
        String context = "Asset Name: " + assetName;
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            assetStatisticTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_NAME_COLUMN_NAME, assetName, DatabaseFilterType.EQUAL);
            assetStatisticTable.loadToMemory();

            if (assetStatisticTable.getRecords().isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            List<String> returnList = new ArrayList<>();

            for (DatabaseTableRecord record : assetStatisticTable.getRecords()) {
                returnList.add(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_PUBLIC_KEY_COLUMN_NAME));
            }
            return returnList;
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetAssetStatisticException(exception, context, "Cannot load table to memory.");
        }
    }

    public List<String> getAllAssetPublicKeyForAssetNameAndStatus(String assetName, AssetCurrentStatus status) throws CantGetAssetStatisticException {
        String context = "Asset Name: " + assetName;
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            assetStatisticTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_NAME_COLUMN_NAME, assetName, DatabaseFilterType.EQUAL);
            assetStatisticTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_CURRENT_STATUS_COLUMN_NAME, status.getCode(), DatabaseFilterType.EQUAL);
            assetStatisticTable.loadToMemory();

            if (assetStatisticTable.getRecords().isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            List<String> returnList = new ArrayList<>();

            for (DatabaseTableRecord record : assetStatisticTable.getRecords()) {
                returnList.add(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_PUBLIC_KEY_COLUMN_NAME));
            }
            return returnList;
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetAssetStatisticException(exception, context, "Cannot load table to memory.");
        }
    }

    public List<String> getAllAssetPublicKey() throws CantGetAssetStatisticException {
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            assetStatisticTable.loadToMemory();

            if (assetStatisticTable.getRecords().isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            List<String> returnList = new ArrayList<>();

            for (DatabaseTableRecord record : assetStatisticTable.getRecords()) {
                returnList.add(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_PUBLIC_KEY_COLUMN_NAME));
            }
            return returnList;
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetAssetStatisticException(exception, null, "Cannot load table to memory.");
        }
    }

    public List<String> getAllAssetPublicKeyForStatus(AssetCurrentStatus status) throws CantGetAssetStatisticException {
        String context = "Asset Status: " + status.getCode();
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            assetStatisticTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_CURRENT_STATUS_COLUMN_NAME, status.getCode(), DatabaseFilterType.EQUAL);
            assetStatisticTable.loadToMemory();

            if (assetStatisticTable.getRecords().isEmpty()) {
                return Collections.EMPTY_LIST;
            }

            List<String> returnList = new ArrayList<>();

            for (DatabaseTableRecord record : assetStatisticTable.getRecords()) {
                returnList.add(record.getStringValue(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_PUBLIC_KEY_COLUMN_NAME));
            }
            return returnList;
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetAssetStatisticException(exception, context, "Cannot load table to memory.");
        }
    }


    //PRIVATE METHODS

    private void updateLongFieldByAssetPublicKey(String columnName, long value, String assetPublicKey) throws CantGetAssetStatisticException, RecordsNotFoundException {
        String context = "Column Name: " + columnName + " - Asset Public Key: " + assetPublicKey + " - Value: " + value;
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            assetStatisticTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_PUBLIC_KEY_COLUMN_NAME, assetPublicKey, DatabaseFilterType.EQUAL);
            assetStatisticTable.loadToMemory();

            if (assetStatisticTable.getRecords().isEmpty()) {
                throw new RecordsNotFoundException(null, context, "");
            }

            for (DatabaseTableRecord record : assetStatisticTable.getRecords()) {
                record.setLongValue(columnName, value);
                assetStatisticTable.updateRecord(record);
            }
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetAssetStatisticException(exception, context, "Cannot load table to memory.");
        } catch (CantUpdateRecordException exception) {
            throw new CantGetAssetStatisticException(exception, context, "Cannot update record.");
        }
    }

    private String getAssetStatisticStringFieldByPk(String assetPublicKey, String column) throws CantGetAssetStatisticException, RecordsNotFoundException {
        String context = "assetPublicKey: " + assetPublicKey;
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            DatabaseTableRecord record = assetStatisticTable.getRecordFromPk(assetPublicKey);

            if (record == null) {
                throw new RecordsNotFoundException(null, context, "");
            }

            return record.getStringValue(column);
        } catch (RecordsNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CantGetAssetStatisticException(e, context, "Database error.");
        }
    }

    private long getAssetStatisticLongFieldByPk(String assetPublicKey, String column) throws CantGetAssetStatisticException, RecordsNotFoundException {
        String context = "assetPublicKey: " + assetPublicKey;
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            DatabaseTableRecord record = assetStatisticTable.getRecordFromPk(assetPublicKey);

            if (record == null) {
                throw new RecordsNotFoundException(null, context, "");
            }

            return record.getLongValue(column);
        } catch (RecordsNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CantGetAssetStatisticException(e, context, "Database error.");
        }
    }

    private void updateStringFieldByAssetPublicKey(String columnName, String value, String assetPublicKey) throws CantGetAssetStatisticException, RecordsNotFoundException {
        String context = "Column Name: " + columnName + " - Asset Public Key: " + assetPublicKey + " - Value: " + value;
        try {
            DatabaseTable assetStatisticTable;
            assetStatisticTable = database.getTable(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_TABLE_NAME);
            assetStatisticTable.addStringFilter(AssetWalletIssuerDatabaseConstant.ASSET_STATISTIC_ASSET_PUBLIC_KEY_COLUMN_NAME, assetPublicKey, DatabaseFilterType.EQUAL);
            assetStatisticTable.loadToMemory();

            if (assetStatisticTable.getRecords().isEmpty()) {
                throw new RecordsNotFoundException(null, context, "");
            }

            for (DatabaseTableRecord record : assetStatisticTable.getRecords()) {
                record.setStringValue(columnName, value);
                assetStatisticTable.updateRecord(record);
            }
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetAssetStatisticException(exception, context, "Cannot load table to memory.");
        } catch (CantUpdateRecordException exception) {
            throw new CantGetAssetStatisticException(exception, context, "Cannot update record.");
        }
    }
}