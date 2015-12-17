package com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.database;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterOperator;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_csh_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_csh_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.exceptions.CantCreateHoldTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantCreateCashMoneyWalletTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CashMoneyWalletDoesNotExistException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletBalanceException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletCurrencyException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletTransactionsException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetHeldFundsException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantRegisterDebitException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantTransactionCashMoneyException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.interfaces.CashMoneyWalletTransaction;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CantAddCashMoneyTotalBalanceException;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CantAddCreditException;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CantAddDebitException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantCreateCashMoneyWalletException;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CantGetBalancesRecord;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CantGetCashMoneyBalance;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CantInitializeCashMoneyWalletDatabaseException;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CantRegisterCashMoneyWalletTransactionException;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.exceptions.CashMoneyWalletInconsistentTableStateException;
import com.bitdubai.fermat_csh_plugin.layer.wallet.cash_money.developer.bitdubai.version_1.structure.CashMoneyWalletTransactionImpl;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alejandro Bicelis on 11/23/2015.
 */
public class CashMoneyWalletDao {

    private final ErrorManager errorManager;
    private final PluginDatabaseSystem pluginDatabaseSystem;
    private final UUID pluginId;

    private Database database;

    public CashMoneyWalletDao(final PluginDatabaseSystem pluginDatabaseSystem, final UUID pluginId, final ErrorManager errorManager) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginId = pluginId;
        this.errorManager = errorManager;
    }

    public void initialize() throws CantInitializeCashMoneyWalletDatabaseException {
        try {
            database = this.pluginDatabaseSystem.openDatabase(pluginId, pluginId.toString());
        } catch (DatabaseNotFoundException e) {
            CashMoneyWalletDatabaseFactory databaseFactory = new CashMoneyWalletDatabaseFactory(pluginDatabaseSystem);
            try {
                database = databaseFactory.createDatabase(pluginId, pluginId.toString());
            } catch (CantCreateDatabaseException cantCreateDatabaseException) {
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantCreateDatabaseException);
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantCreateDatabaseException);
                throw new CantInitializeCashMoneyWalletDatabaseException("Database could not be opened", cantCreateDatabaseException, "Database Name: " + pluginId.toString(), "");
            }
        } catch (CantOpenDatabaseException cantOpenDatabaseException) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantOpenDatabaseException);
            throw new CantInitializeCashMoneyWalletDatabaseException("Database could not be opened", cantOpenDatabaseException, "Database Name: " + pluginId.toString(), "");
        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantInitializeCashMoneyWalletDatabaseException("Database could not be opened", FermatException.wrapException(e), "Database Name: " + pluginId.toString(), "");
        }
    }


    public void createCashMoneyWallet(String walletPublicKey, FiatCurrency fiatCurrency) throws CantCreateCashMoneyWalletException {

        if(walletExists(walletPublicKey))
            throw new CantCreateCashMoneyWalletException(CantCreateCashMoneyWalletException.DEFAULT_MESSAGE, null, "Cant create Cash Money Wallet", "Cash Wallet already exists! publicKey:" + walletPublicKey);

        try {
            DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.WALLETS_TABLE_NAME);
            DatabaseTableRecord record = table.getEmptyRecord();

            record.setStringValue(CashMoneyWalletDatabaseConstants.WALLETS_WALLET_PUBLIC_KEY_COLUMN_NAME, walletPublicKey);
            record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME, 0);
            record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME, 0);
            record.setStringValue(CashMoneyWalletDatabaseConstants.WALLETS_CURRENCY_COLUMN_NAME, fiatCurrency.getCode());
            record.setLongValue(CashMoneyWalletDatabaseConstants.WALLETS_TIMESTAMP_WALLET_CREATION_COLUMN_NAME, (new Date().getTime() / 1000));

            table.insertRecord(record);
        } catch (CantInsertRecordException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantCreateCashMoneyWalletException(CantCreateCashMoneyWalletException.DEFAULT_MESSAGE, e, "Cant create Cash Money Wallet", "Cant insert record into database");
        }
    }

    public void registerTransaction(CashMoneyWalletTransaction cashMoneyWalletTransaction) throws CantRegisterCashMoneyWalletTransactionException {
        try {
            DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME);
            DatabaseTableRecord record = table.getEmptyRecord();
            constructRecordFromCashMoneyWalletTransaction(record, cashMoneyWalletTransaction);

            table.insertRecord(record);
        } catch (CantInsertRecordException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantRegisterCashMoneyWalletTransactionException(CantRegisterCashMoneyWalletTransactionException.DEFAULT_MESSAGE, e, "Cant insert transaction record into database", null);
        }
    }


    public void credit(String walletPublicKey, BalanceType balanceType, float creditAmount) throws CantRegisterCreditException {
        DatabaseTable table;
        DatabaseTableRecord record;
        try {
            table = this.database.getTable(CashMoneyWalletDatabaseConstants.WALLETS_TABLE_NAME);
            record = this.getWalletRecordByPublicKey(walletPublicKey);
            if (balanceType == BalanceType.AVAILABLE) {
                float available = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME);
                available = available + creditAmount;

                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME, available);

            } else if (balanceType == BalanceType.BOOK) {
                float book = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME);
                book = book + creditAmount;

                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME, book);

            } else if (balanceType == BalanceType.ALL) {
                float available = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME);
                float book = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME);

                available = available + creditAmount;
                book = book + creditAmount;

                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME, available);
                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME, book);
            }

            table.updateRecord(record);

        } catch (CashMoneyWalletInconsistentTableStateException e) {
            throw new CantRegisterCreditException(e.getMessage(), e, "Credit in Cash wallet", "Cant credit balance. Inconsistent Table State");
        } catch (CantUpdateRecordException e) {
            throw new CantRegisterCreditException(e.getMessage(), e, "Credit in Cash wallet", "Cant credit balance. Cant update record");
        } catch (CantLoadTableToMemoryException e) {
            throw new CantRegisterCreditException(e.getMessage(), e, "Credit in Cash wallet", "Cant credit balance. Cant load table to memory");
        } catch (CashMoneyWalletDoesNotExistException e) {
        throw new CantRegisterCreditException(e.getMessage(), e, "Credit in Cash wallet", "Cant credit balance. Wallet does not exist");
        }
    }

    public void debit(String walletPublicKey, BalanceType balanceType, float debitAmount) throws CantRegisterDebitException {
        DatabaseTable table;
        DatabaseTableRecord record;
        try {
            table = this.database.getTable(CashMoneyWalletDatabaseConstants.WALLETS_TABLE_NAME);
            record = this.getWalletRecordByPublicKey(walletPublicKey);
            if (balanceType == BalanceType.AVAILABLE) {
                float available = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME);
                available = available - debitAmount;

                if (available < 0)
                    throw new CantRegisterDebitException(CantRegisterDebitException.DEFAULT_MESSAGE, null, "Debit in Cash wallet", "Cant debit available balance by this amount, insufficient funds.");

                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME, available);

            } else if (balanceType == BalanceType.BOOK) {
                float book = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME);
                book = book - debitAmount;

                if (book < 0)
                    throw new CantRegisterDebitException(CantRegisterDebitException.DEFAULT_MESSAGE, null, "Debit in Cash wallet", "Cant debit book balance by this amount, insufficient funds.");

                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME, book);

            } else if (balanceType == BalanceType.ALL) {
                float available = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME);
                float book = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME);

                available = available - debitAmount;
                book = book - debitAmount;

                if (available < 0)
                    throw new CantRegisterDebitException(CantRegisterDebitException.DEFAULT_MESSAGE, null, "Debit in Cash wallet", "Cant debit available balance by this amount, insufficient funds.");

                if (book < 0)
                    throw new CantRegisterDebitException(CantRegisterDebitException.DEFAULT_MESSAGE, null, "Debit in Cash wallet", "Cant debit book balance by this amount, insufficient funds.");

                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME, available);
                record.setFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME, book);
            }


            table.updateRecord(record);

        } catch (CashMoneyWalletInconsistentTableStateException e) {
            throw new CantRegisterDebitException(e.getMessage(), e, "Debit in Cash wallet", "Cant debit balance. Inconsistent Table State");
        } catch (CantUpdateRecordException e) {
            throw new CantRegisterDebitException(e.getMessage(), e, "Debit in Cash wallet", "Cant debit balance. Cant update record");
        } catch (CantLoadTableToMemoryException e) {
            throw new CantRegisterDebitException(e.getMessage(), e, "Debit in Cash wallet", "Cant debit balance. Cant load table to memory");
        } catch (CashMoneyWalletDoesNotExistException e) {
            throw new CantRegisterDebitException(e.getMessage(), e, "Debit in Cash wallet", "Cant debit balance. Wallet does not exist");
        }
    }

    public FiatCurrency getWalletCurrency(String walletPublicKey) throws CantGetCashMoneyWalletCurrencyException {
        FiatCurrency currency;
        try {
            DatabaseTableRecord record = this.getWalletRecordByPublicKey(walletPublicKey);
            currency = FiatCurrency.getByCode(record.getStringValue(CashMoneyWalletDatabaseConstants.WALLETS_CURRENCY_COLUMN_NAME));
        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletCurrencyException(CantGetCashMoneyWalletCurrencyException.DEFAULT_MESSAGE, e, "Cant get wallet currency", null);
        }

        return currency;
    }

    public double getWalletBalance(String walletPublicKey, BalanceType balanceType) throws CantGetCashMoneyWalletBalanceException {
        double balance;
        try {
            DatabaseTableRecord record = this.getWalletRecordByPublicKey(walletPublicKey);
            if (balanceType == BalanceType.AVAILABLE)
                balance = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_AVAILABLE_BALANCE_COLUMN_NAME);
            else if (balanceType == BalanceType.BOOK)
                balance = record.getFloatValue(CashMoneyWalletDatabaseConstants.WALLETS_BOOK_BALANCE_COLUMN_NAME);
            else throw new InvalidParameterException();
        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletBalanceException(CantGetCashMoneyWalletBalanceException.DEFAULT_MESSAGE, e, "Cant get wallet balance", null);
        }

        return balance;
    }

    public boolean walletExists(String walletPublicKey) {
        DatabaseTableRecord record = null;
        try {
            record = this.getWalletRecordByPublicKey(walletPublicKey);
        } catch (CashMoneyWalletDoesNotExistException e) {
            return false;
        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            return false;
        }

        if (record.getStringValue(CashMoneyWalletDatabaseConstants.WALLETS_WALLET_PUBLIC_KEY_COLUMN_NAME).equals(walletPublicKey))
            return true;
        else return false;
    }


    public List<CashMoneyWalletTransaction> getTransactions(String walletPublicKey, List<TransactionType> transactionTypes, List<BalanceType> balanceTypes, int max, int offset) throws CantGetCashMoneyWalletTransactionsException {
        List<CashMoneyWalletTransaction> transactions = new ArrayList<>();

        List<String> transactionTypesString = new ArrayList<>();
        for(TransactionType t : transactionTypes)
            transactionTypesString.add(t.getCode());

        List<String> balanceTypesString = new ArrayList<>();
        for(BalanceType b : balanceTypes)
            balanceTypesString.add(b.getCode());

        String query = "SELECT * FROM " +
                CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME +
                " WHERE ( " +
                CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME +
                " = '" +
                StringUtils.join(transactionTypesString, "' OR " + CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME + " = '") +
                "') AND (" +
                CashMoneyWalletDatabaseConstants.TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME +
                " = '" +
                StringUtils.join(balanceTypesString, "' OR " + CashMoneyWalletDatabaseConstants.TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME + " = '") +
                "') AND " +
                CashMoneyWalletDatabaseConstants.TRANSACTIONS_WALLET_PUBLIC_KEY_COLUMN_NAME +
                " = '" +
                walletPublicKey +
                "' ORDER BY " +
                CashMoneyWalletDatabaseConstants.TRANSACTIONS_TIMESTAMP_COLUMN_NAME +
                " DESC " +
                " LIMIT " + max +
                " OFFSET " + offset;


        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME);

        try {
            Collection<DatabaseTableRecord> records = table.customQuery(query, false);

            for (DatabaseTableRecord record : records) {
                CashMoneyWalletTransaction transaction = constructCashMoneyWalletTransactionFromRecord(record);
                transactions.add(transaction);
            }
        } catch (CantCreateCashMoneyWalletTransactionException e) {
            throw new CantGetCashMoneyWalletTransactionsException(CantGetCashMoneyWalletTransactionsException.DEFAULT_MESSAGE, e, "Failed to get Cash Money Wallet Transactions.", "CantCreateCashMoneyWalletTransactionException");
        } catch (CantLoadTableToMemoryException e) {
            throw new CantGetCashMoneyWalletTransactionsException(CantGetCashMoneyWalletTransactionsException.DEFAULT_MESSAGE, e, "Failed to get Cash Money Wallet Transactions.", "CantLoadTableToMemoryException");
        }
        return transactions;
    }


    public double getHeldFunds(String walletPublicKey, String actorPublicKey) throws CantGetHeldFundsException {
        List<DatabaseTableRecord> records;
        double heldFunds = 0, unheldFunds = 0;

        List<DatabaseTableFilter> filtersTable = new ArrayList<>();
        DatabaseTableFilter walletFilter, actorFilter;
        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME);

        walletFilter = getEmptyTransactionsTableFilter();
        walletFilter.setColumn(CashMoneyWalletDatabaseConstants.TRANSACTIONS_WALLET_PUBLIC_KEY_COLUMN_NAME);
        walletFilter.setValue(walletPublicKey);
        walletFilter.setType(DatabaseFilterType.EQUAL);
        filtersTable.add(walletFilter);

        actorFilter = getEmptyTransactionsTableFilter();
        actorFilter.setColumn(CashMoneyWalletDatabaseConstants.TRANSACTIONS_ACTOR_PUBLIC_KEY_COLUMN_NAME);
        actorFilter.setValue(actorPublicKey);
        actorFilter.setType(DatabaseFilterType.EQUAL);
        filtersTable.add(actorFilter);

        table.setFilterGroup(filtersTable, null, DatabaseFilterOperator.AND);

        try {
            table.loadToMemory();
            records = table.getRecords();
        } catch (CantLoadTableToMemoryException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetHeldFundsException(CantGetHeldFundsException.DEFAULT_MESSAGE, e, "getHeldFunds", "Cant load table into memory");
        }

        for (DatabaseTableRecord record : records) {
            if (record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME).equals(TransactionType.HOLD.getCode()))
                heldFunds += record.getFloatValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_AMOUNT_COLUMN_NAME);
            else if (record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME).equals(TransactionType.UNHOLD.getCode()))
                unheldFunds += record.getFloatValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_AMOUNT_COLUMN_NAME);
        }
        heldFunds = heldFunds - unheldFunds;

        if (heldFunds < 0)
            throw new CantGetHeldFundsException(CantGetHeldFundsException.DEFAULT_MESSAGE, null, "Held funds calculates to a negative value", "Unheld funds are higher than held funds, invalid table state");

        return heldFunds;
    }


    /* INTERNAL HELPER FUNCTIONS */
    private DatabaseTableFilter getEmptyTransactionsTableFilter() {
        return this.database.getTable(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME).getEmptyTableFilter();
    }

    private DatabaseTableRecord getWalletRecordByPublicKey(String walletPublicKey) throws CantLoadTableToMemoryException, CashMoneyWalletInconsistentTableStateException, CashMoneyWalletDoesNotExistException {
        List<DatabaseTableRecord> records;
        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.WALLETS_TABLE_NAME);
        table.setStringFilter(CashMoneyWalletDatabaseConstants.WALLETS_WALLET_PUBLIC_KEY_COLUMN_NAME, walletPublicKey, DatabaseFilterType.EQUAL);
        try {
            table.loadToMemory();
        } catch (CantLoadTableToMemoryException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_WALLET_CASH_MONEY, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantLoadTableToMemoryException(CantLoadTableToMemoryException.DEFAULT_MESSAGE, e, "getWalletRecordByPublicKey", "Cant load table into memory");
        }

        records = table.getRecords();

        if (records.size() == 0)
            throw new CashMoneyWalletDoesNotExistException(CashMoneyWalletDoesNotExistException.DEFAULT_MESSAGE);
        if (records.size() != 1)
            throw new CashMoneyWalletInconsistentTableStateException("Inconsistent (" + records.size() + ") number of fetched records, should be between 0 and 1.", null, "The id is: " + walletPublicKey, "");

        return records.get(0);
    }


    private List<DatabaseTableRecord> getTransactionRecordsByFilter(DatabaseTableFilter filter, int max, int offset) throws CantLoadTableToMemoryException {
        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME);

        if (filter != null)
            table.setStringFilter(filter.getColumn(), filter.getValue(), filter.getType());

        if (offset >= 0)
            table.setFilterOffSet(String.valueOf(offset));

        if (max >= 0)
            table.setFilterTop(String.valueOf(max));

        table.loadToMemory();
        return table.getRecords();
    }


    private void constructRecordFromCashMoneyWalletTransaction(DatabaseTableRecord newRecord, CashMoneyWalletTransaction cashMoneyWalletTransaction) {

        newRecord.setUUIDValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_ID_COLUMN_NAME, cashMoneyWalletTransaction.getTransactionId());
        newRecord.setStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_WALLET_PUBLIC_KEY_COLUMN_NAME, cashMoneyWalletTransaction.getPublicKeyWallet());
        newRecord.setStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_ACTOR_PUBLIC_KEY_COLUMN_NAME, cashMoneyWalletTransaction.getPublicKeyActor());
        newRecord.setStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_PLUGIN_PUBLIC_KEY_COLUMN_NAME, cashMoneyWalletTransaction.getPublicKeyPlugin());
        newRecord.setStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME, cashMoneyWalletTransaction.getTransactionType().getCode());
        newRecord.setStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME, cashMoneyWalletTransaction.getBalanceType().getCode());
        newRecord.setFloatValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_AMOUNT_COLUMN_NAME, cashMoneyWalletTransaction.getAmount());
        newRecord.setStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_MEMO_COLUMN_NAME, cashMoneyWalletTransaction.getMemo());
        newRecord.setLongValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TIMESTAMP_COLUMN_NAME, cashMoneyWalletTransaction.getTimestamp());
    }

    private CashMoneyWalletTransaction constructCashMoneyWalletTransactionFromRecord(DatabaseTableRecord record) throws CantCreateCashMoneyWalletTransactionException {

        UUID transactionId = record.getUUIDValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_ID_COLUMN_NAME);
        String publicKeyWallet = record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_WALLET_PUBLIC_KEY_COLUMN_NAME);
        String publicKeyActor = record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_ACTOR_PUBLIC_KEY_COLUMN_NAME);
        String publicKeyPlugin = record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_PLUGIN_PUBLIC_KEY_COLUMN_NAME);
        float amount = record.getFloatValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_AMOUNT_COLUMN_NAME);
        String memo = record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_MEMO_COLUMN_NAME);
        long timestamp = record.getLongValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TIMESTAMP_COLUMN_NAME);

        TransactionType transactionType;
        try {
            transactionType = TransactionType.getByCode(record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME));
        } catch (InvalidParameterException e) {
            throw new CantCreateCashMoneyWalletTransactionException(e.getMessage(), e, "Cash Money Wallet", "Invalid TransactionType value stored in table"
                    + CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME + " for id " + transactionId);
        }

        BalanceType balanceType;
        try {
            balanceType = BalanceType.getByCode(record.getStringValue(CashMoneyWalletDatabaseConstants.TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME));
        } catch (InvalidParameterException e) {
            throw new CantCreateCashMoneyWalletTransactionException(e.getMessage(), e, "Cash Money Wallet", "Invalid BalanceType value stored in table"
                    + CashMoneyWalletDatabaseConstants.TRANSACTIONS_TABLE_NAME + " for id " + transactionId);
        }

        return new CashMoneyWalletTransactionImpl(transactionId, publicKeyWallet, publicKeyActor, publicKeyPlugin, transactionType, balanceType, amount, memo, timestamp);
    }















   /* public CashMoneyWallet registerCashMoney(
            String cashTransactionId,
            String publicKeyActorFrom,
            String publicKeyActorTo,
            String status,
            String balanceType,
            String transactionType,
            double amount,
            String cashCurrencyType,
            String cashReference,
            long runningBookBalance,
            long runningAvailableBalance,
            long timestamp,
            String memo) throws CantCreateCashMoneyException {
        try {
        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.CASH_MONEY_TABLE_NAME);
        DatabaseTableRecord record =  table.getEmptyRecord();

            CashMoneyWallet cashMoney = cashMoney(record,
                cashTransactionId,
                publicKeyActorFrom,
                publicKeyActorTo,
                status,
                balanceType,
                transactionType,
                amount,
                cashCurrencyType,
                cashReference,
                runningBookBalance,
                runningAvailableBalance,
                timestamp,
                memo);

            table.insertRecord(record);

            database.closeDatabase();
            return  cashMoney;
        } catch (CantInsertRecordException e) {
            throw new CantCreateCashMoneyException(CantCreateCashMoneyException.DEFAULT_MESSAGE,e,"Cant Create CashMoneyWallet Exception","Cant Insert Record Exception");
        }
    }*/
    /**
     *
     * @param cashMoneyTransaction
     * @param name
     * @param description
     * @throws CantAddCashMoneyTotalBalanceException
     */
  /*  public void addCashMoneyTotalBalance(CashMoneyWalletTransaction cashMoneyTransaction,
                                         String name,
                                         String description) throws CantAddCashMoneyTotalBalanceException
    {
        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_TABLE_NAME);
        DatabaseTableRecord record =  table.getEmptyRecord();

        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_WALLET_KEY_BROKER_COLUMN_NAME, cashMoneyTransaction.getPublicKeyActorFrom());
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_PUBLIC_KEY_BROKER_COLUMN_NAME, cashMoneyTransaction.getPublicKeyActorTo());
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_CASH_CURRENCY_TYPE_COLUMN_NAME, cashMoneyTransaction.getCashCurrencyType().getCode());
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_NAME_COLUMN_NAME,name);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_DESCRIPTION_COLUMN_NAME, description);
        record.setLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_AVAILABLE_BALANCE_COLUMN_NAME, cashMoneyTransaction.getRunningAvailableBalance());
        record.setLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_BOOK_BALANCE_COLUMN_NAME, cashMoneyTransaction.getRunningBookBalance());

        try {
            table.insertRecord(record);
            database.closeDatabase();
        } catch (CantInsertRecordException e) {
           throw new CantAddCashMoneyTotalBalanceException(CantAddCashMoneyTotalBalanceException.DEFAULT_MESSAGE,e,"Cant Add CashMoneyConstructor Total Balance","Cant Insert Record Exception");
        }
    }*/
    /**
     *
     * @param cashMoneyBalanceRecord
     * @param balanceType
     * @throws CantAddDebitException
     */
    /*public void addDebit(CashMoneyBalanceRecord cashMoneyBalanceRecord, BalanceType balanceType) throws CantAddDebitException {
        try {
        double availableAmount;
        double bookAmount;
        long runningAvailableBalance = 0;
        long runningBookBalance = 0;
        if (balanceType == BalanceType.AVAILABLE){
            availableAmount  = cashMoneyBalanceRecord.getAmount();
            runningAvailableBalance = (long) (getCurrentBalance(BalanceType.AVAILABLE) + (-availableAmount));
            addCashMoney(cashMoneyBalanceRecord, balanceType, runningBookBalance,runningAvailableBalance);

        }
        if (balanceType == BalanceType.BOOK){
            bookAmount  = cashMoneyBalanceRecord.getAmount();
            runningBookBalance = (long) (getCurrentBalance(BalanceType.BOOK) + (-bookAmount));
            addCashMoney(cashMoneyBalanceRecord, balanceType, runningBookBalance, runningAvailableBalance);
        }

        } catch (CantGetBalancesRecord cantGetBalancesRecord) {
            throw new CantAddDebitException(CantAddDebitException.DEFAULT_MESSAGE,cantGetBalancesRecord,"Cant Add Debit Exception","Cant Get Balances Record");
        } catch (CantGetCurrentBalanceException e) {
            throw new CantAddDebitException(CantAddDebitException.DEFAULT_MESSAGE,e,"Cant Add Debit Exception","Cant Get Current Balance Exception");
        } catch (CantAddCashMoney cantAddCashMoney) {
           throw new CantAddDebitException(CantAddDebitException.DEFAULT_MESSAGE,cantAddCashMoney,"Cant Add Debit Exception","Cant Add CashMoneyConstructor");
        }
    }*/
    /**
     *
     * @param cashMoneyBalanceRecord
     * @param balanceType
     * @throws CantAddCreditException
     */
    /*public void addCredit(CashMoneyBalanceRecord cashMoneyBalanceRecord, BalanceType balanceType) throws CantAddCreditException {
        try {
            double availableAmount;
            double bookAmount;
            long runningAvailableBalance = 0;
            long runningBookBalance = 0;
            if (balanceType == BalanceType.AVAILABLE){
                availableAmount  = cashMoneyBalanceRecord.getAmount();
                runningAvailableBalance = (long) (getCurrentBalance(BalanceType.AVAILABLE) + (-availableAmount));
                addCashMoney(cashMoneyBalanceRecord, balanceType, runningBookBalance, runningAvailableBalance);

            }
            if (balanceType == BalanceType.BOOK){
                bookAmount  = cashMoneyBalanceRecord.getAmount();
                runningBookBalance = (long) (getCurrentBalance(BalanceType.BOOK) + (-bookAmount));
                addCashMoney(cashMoneyBalanceRecord, balanceType, runningBookBalance, runningAvailableBalance);
            }

        }catch (CantGetBalancesRecord cantGetBalancesRecord) {
            throw new CantAddCreditException(CantAddCreditException.DEFAULT_MESSAGE,cantGetBalancesRecord,"Cant Add Debit Exception","Cant Get Balances Record");
        } catch (CantGetCurrentBalanceException e) {
            throw new CantAddCreditException(CantAddCreditException.DEFAULT_MESSAGE,e,"Cant Add Credit Exception","Cant Get Current Balance Exception");
        } catch (CantAddCashMoney cantAddCashMoney) {
           throw  new CantAddCreditException(CantAddCreditException.DEFAULT_MESSAGE,cantAddCashMoney,"Cant Add Credit Exception","Cant Add Cash Money");
        }
    }*/
    /**
     *
     * @param balanceType
     * @param max
     * @param offset
     * @return
     * @throws CantTransactionCashMoneyException
     */
   /* public List<CashMoneyWalletTransaction> getTransactions(BalanceType balanceType, int max, int offset )throws CantTransactionCashMoneyException {

        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.CASH_MONEY_BALANCE_RECORD_TABLE_NAME);

        table.setStringFilter(CashMoneyWalletDatabaseConstants.CASH_MONEY_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode(), DatabaseFilterType.EQUAL);
        table.setFilterTop(String.valueOf(max));
        table.setFilterOffSet(String.valueOf(offset));

        try {
            table.loadToMemory();
            return createTransactionList(table.getRecords());
        } catch (CantLoadTableToMemoryException e) {
           throw new CantTransactionCashMoneyException(
                   CantTransactionCashMoneyException.DEFAULT_MESSAGE,
                   e,
                   "Cant Transaction CashMoneyConstructor Exception",
                   "Cant Load Table To Memory Exception"
           );
        }
    }*/
   /* public List<CashMoneyWallet> getTransactionsCashMoney() throws CantCreateCashMoneyException {
        DatabaseTable table = this.database.getTable(CashMoneyWalletDatabaseConstants.CASH_MONEY_BALANCE_RECORD_TABLE_NAME);

        try {
            table.loadToMemory();
            return createTransactionCashMoneyList(table.getRecords());
        } catch (CantLoadTableToMemoryException e) {
           throw new CantCreateCashMoneyException(
                   CantCreateCashMoneyException.DEFAULT_MESSAGE,
                   e,
                   "Cant Create Cash Money Exception",
                   "Cant Load Table To Memory Exception");
        }

    }*/
    /**
     *
     * @return
     * @throws CantGetCashMoneyBalance
     */
   /* public double getAmauntCashMoneyTotalBalance(BalanceType balanceType) throws CantGetCashMoneyTotalBalanceRecordList, CantGetCashMoneyListException {
        double balanceAmount = 0;
        if (balanceType == BalanceType.AVAILABLE){
            for (DatabaseTableRecord record : getCashMoneyTotalBalancesRecordList()){
                String stringAmununt = record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_AVAILABLE_BALANCE_COLUMN_NAME);
                balanceAmount += Double.valueOf(stringAmununt);
            }
        } else {
            for (DatabaseTableRecord record : getCashMoneyTotalBalancesRecordList()){
                String stringAmununt = record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_BOOK_BALANCE_COLUMN_NAME);
                balanceAmount += Double.valueOf(stringAmununt);
            }
        }
        return balanceAmount;
    }
    public double getAmaunt() throws CantGetCashMoneyListException {
            double balanceAmount = 0;
            for (DatabaseTableRecord record : getCashMoneyList()){
                balanceAmount= record.getDoubleValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_AMOUNT_COLUMN_NAME);
                balanceAmount +=balanceAmount;
            }
        return balanceAmount;
    }*/
    /**
     *
     * @param balanceType
     * @return
     * @throws CantGetBalancesRecord
     */
   /* public double getCurrentBalance(BalanceType balanceType) throws CantGetCurrentBalanceException, CantGetBalancesRecord {
        if (balanceType == balanceType.AVAILABLE)
            return Double.valueOf(getCashMoneyTotalBalance().getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_AVAILABLE_BALANCE_COLUMN_NAME));
        else
            return Double.valueOf(getCashMoneyTotalBalance().getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_BOOK_BALANCE_COLUMN_NAME));
        }*/

    /**
     *
     * @return
     * @throws CantGetBalancesRecord
     */
      /*   private DatabaseTableRecord getCashMoneyTotalBalance() throws CantGetBalancesRecord {
        try {
            DatabaseTable balancesTable = database.getTable(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_TABLE_NAME);
            balancesTable.loadToMemory();
            return balancesTable.getRecords().get(0);
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetBalancesRecord(CantGetBalancesRecord.DEFAULT_MESSAGE,exception,"Cant Get Balances Record","Cant Load Table To Memory Exception");
        }
    }*/
    /**
     *
     * @return
     */
   /* private List<DatabaseTableRecord> getCashMoneyTotalBalancesRecordList() throws CantGetCashMoneyTotalBalanceRecordList, CantGetCashMoneyListException {
        try {
            DatabaseTable totalBalancesTable;
            totalBalancesTable = this.database.getTable(CashMoneyWalletDatabaseConstants.CASH_MONEY_TOTAL_BALANCES_TABLE_NAME);
            totalBalancesTable.loadToMemory();
            return totalBalancesTable.getRecords();
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetCashMoneyTotalBalanceRecordList(CantGetCashMoneyTotalBalanceRecordList.DEFAULT_MESSAGE, exception, "Cant Get CashMoneyConstructor Total Balance Record List", "Cant Load Table T oMemory Exception");
        }
    }*/
    /**
     *
     */
       /* private List<DatabaseTableRecord> getCashMoneyList() throws CantGetCashMoneyListException{
            try {
                DatabaseTable totalBalancesTable;
                totalBalancesTable = this.database.getTable(CashMoneyWalletDatabaseConstants.CASH_MONEY_TABLE_NAME);
                totalBalancesTable.loadToMemory();
                return totalBalancesTable.getRecords();
            } catch (CantLoadTableToMemoryException e) {
              throw new CantGetCashMoneyListException(CantGetCashMoneyListException.DEFAULT_MESSAGE,e,"Cant Get CashMoneyConstructor List Exception","Cant Load Table To Memory Exception");
            }
        }

    private List<CashMoneyWalletTransaction> createTransactionList(Collection<DatabaseTableRecord> records){
        List<CashMoneyWalletTransaction> cashMoneyTransactionsList = new ArrayList<>();

        for(DatabaseTableRecord record : records)
            cashMoneyTransactionsList.add(constructCashMoneyTransactionFromRecord(record));

        return cashMoneyTransactionsList;
    }
    private List<CashMoneyWallet> createTransactionCashMoneyList(Collection<DatabaseTableRecord> records){
        List<CashMoneyWallet> cashMoneyConstructorTransactionsList = new ArrayList<>();

        for(DatabaseTableRecord record : records)
            cashMoneyConstructorTransactionsList.add(constructTransactionCashMoney(record));

        return cashMoneyConstructorTransactionsList;
    }*/

    /**
     *
     * @param record
     * @return
     */
    /*private CashMoneyWalletTransaction constructCashMoneyTransactionFromRecord(DatabaseTableRecord record){

        UUID cashTransactionId=record.getUUIDValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_TRANSACTION_ID_COLUMN_NAME);
        String walletKeyBroker=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_WALLET_KEY_BROKER_COLUMN_NAME);
        String publicKeyCustomer=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_PUBLIC_KEY_CUSTOMER_COLUMN_NAME);
        String publicKeyBroker=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_PUBLIC_KEY_BROKER_COLUMN_NAME);
        String balanceType=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_BALANCE_TYPE_COLUMN_NAME);
        String transactionType=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TRANSACTION_TYPE_COLUMN_NAME);
        double amount=record.getDoubleValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_AMOUNT_COLUMN_NAME);
        String cashCurrencyType=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_CURRENCY_TYPE_COLUMN_NAME);
        String cashReference=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_REFERENCE_COLUMN_NAME);
        long runningBookBalance=record.getLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_RUNNING_BOOK_BALANCE_COLUMN_NAME);
        long runningAvailableBalance=record.getLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_RUNNING_AVAILABLE_BALANCE_COLUMN_NAME);
        long timeStamp=record.getLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TIMESTAMP_COLUMN_NAME);
        String memo=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_MEMO_COLUMN_NAME);
        String status=record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_STATUS_COLUMN_NAME);

        return new TransactionCashMoney(
                cashTransactionId,
                walletKeyBroker,
                publicKeyCustomer,
                publicKeyBroker,
                balanceType,
                transactionType,
                amount,
                cashCurrencyType,
                cashReference,
                runningBookBalance,
                runningAvailableBalance,
                timeStamp,
                memo,
                status);
    }
    private CashMoneyWallet constructTransactionCashMoney(DatabaseTableRecord record){

        UUID cashTransactionId          =record.getUUIDValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_TRANSACTION_ID_COLUMN_NAME);
        String walletKeyBroker          =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_WALLET_KEY_BROKER_COLUMN_NAME);
        String publicKeyCustomer        =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_PUBLIC_KEY_CUSTOMER_COLUMN_NAME);
        String publicKeyBroker          =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_PUBLIC_KEY_BROKER_COLUMN_NAME);
        String balanceType              =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_BALANCE_TYPE_COLUMN_NAME);
        String transactionType          =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TRANSACTION_TYPE_COLUMN_NAME);
        double amount                   =record.getDoubleValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_AMOUNT_COLUMN_NAME);
        String cashCurrencyType         =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_CURRENCY_TYPE_COLUMN_NAME);
        String cashReference            =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_REFERENCE_COLUMN_NAME);
        long runningBookBalance         =record.getLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_RUNNING_BOOK_BALANCE_COLUMN_NAME);
        long runningAvailableBalance    =record.getLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_RUNNING_AVAILABLE_BALANCE_COLUMN_NAME);
        long timeStamp                  =record.getLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TIMESTAMP_COLUMN_NAME);
        String memo                     =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_MEMO_COLUMN_NAME);
        String status                   =record.getStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_STATUS_COLUMN_NAME);

        return (CashMoneyWallet) new CashMoneyConstructor(
                cashTransactionId,
                walletKeyBroker,
                publicKeyCustomer,
                publicKeyBroker,
                balanceType,
                transactionType,
                amount,
                cashCurrencyType,
                cashReference,
                runningBookBalance,
                runningAvailableBalance,
                timeStamp,
                memo,
                status);
    }
    private CashMoneyWallet cashMoney(DatabaseTableRecord record,
                                String cashTransactionId,
                                String publicKeyActorFrom,
                                String publicKeyActorTo,
                                String status,
                                String balanceType,
                                String transactionType,
                                double amount,
                                String cashCurrencyType,
                                String cashReference,
                                long runningBookBalance,
                                long runningAvailableBalance,
                                long timestamp,
                                String memo){

        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_TRANSACTION_ID_COLUMN_NAME, cashTransactionId);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_PUBLIC_KEY_CUSTOMER_COLUMN_NAME, publicKeyActorFrom);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_PUBLIC_KEY_BROKER_COLUMN_NAME, publicKeyActorTo);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_BALANCE_TYPE_COLUMN_NAME, balanceType);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TRANSACTION_TYPE_COLUMN_NAME, transactionType);
        record.setDoubleValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_AMOUNT_COLUMN_NAME, amount);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_CURRENCY_TYPE_COLUMN_NAME, cashCurrencyType);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_CASH_REFERENCE_COLUMN_NAME, cashReference);
        record.setLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_RUNNING_BOOK_BALANCE_COLUMN_NAME, runningBookBalance);
        record.setLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_RUNNING_AVAILABLE_BALANCE_COLUMN_NAME,runningAvailableBalance);
        record.setLongValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_TIMESTAMP_COLUMN_NAME, timestamp);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_MEMO_COLUMN_NAME, memo);
        record.setStringValue(CashMoneyWalletDatabaseConstants.CASH_MONEY_STATUS_COLUMN_NAME,status);

        return (CashMoneyWallet) new CashMoneyManagerImp(
                 cashTransactionId,
                 publicKeyActorFrom,
                 publicKeyActorTo,
                 status,
                 balanceType,
                 transactionType,
                 amount,
                 cashCurrencyType,
                 cashReference,
                 runningBookBalance,
                 runningAvailableBalance,
                 timestamp,
                 memo);
    }*/

}
