package com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.database;

import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseTransactionFailedException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.DealsWithPluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetAvailableBalanceCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetBookedBalanceCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerMarketRateException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerQuoteException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerStockTransactionException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerWalletSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantSaveCryptoBrokerWalletSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransaction;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransactionRecord;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletBalanceRecord;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.FiatIndex;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.Quote;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletAssociatedSetting;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletProviderSetting;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletSettingSpread;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantGetBalanceRecordException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantExecuteCryptoBrokerTransactionException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantAddCreditException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantAddDebitException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerWalletAssociatedSettingImpl;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerWalletBalanceRecordImpl;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerWalletProviderSettingImpl;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerWalletSettingSpreadImpl;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerStockTransactionImpl;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.FiatIndexImpl;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.QuoteImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yordin Alayn on 17.10.15.
 * Modified by Franklin Marcano
 */
public class CryptoBrokerWalletDatabaseDao implements DealsWithPluginFileSystem {
    //TODO: Documentar y Manejo de excepciones
    public static final String PATH_DIRECTORY = "cryptobrokerwallet-swap/";
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

    public CryptoBrokerWalletDatabaseDao(Database database) {
        this.database = database;
    }

    public float getBookedBalance(FermatEnum merchandise) throws CantGetBookedBalanceCryptoBrokerWalletException {
        return getCurrentBalanceByMerchandise(BalanceType.BOOK, merchandise.getCode());
    }

    public float geAvailableBalance(FermatEnum merchandise) throws CantGetAvailableBalanceCryptoBrokerWalletException {
        return getCurrentBalanceByMerchandise(BalanceType.AVAILABLE, merchandise.getCode());
    }

    public float getAvailableBalanceFrozen(FermatEnum merchandise) throws CantGetAvailableBalanceCryptoBrokerWalletException {
        return getCurrentBalanceByMerchandise(BalanceType.AVAILABLE, merchandise.getCode());
    }

    public List<CryptoBrokerWalletBalanceRecord> getAvailableBalanceByMerchandise() throws CantCalculateBalanceException, CantGetBalanceRecordException {
        return getCurrentAvailableBalanceByMerchandise();
    }

    public List<CryptoBrokerWalletBalanceRecord> getBookBalanceByMerchandise() throws CantCalculateBalanceException, CantGetBalanceRecordException {
        return getCurrentBookBalanceByMerchandise();
    }

    public List<CryptoBrokerWalletBalanceRecord> getAvailableBalanceByMerchandiseFrozen() throws CantCalculateBalanceException {
        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_TABLE_NAME);
        CryptoBrokerWalletBalanceRecordImpl cryptoBrokerWalletBalanceRecord = null;
        List<CryptoBrokerWalletBalanceRecord> cryptoBrokerWalletBalanceRecords = new ArrayList<>();

        float availableBalanceFrozen = 0;
        try {
            table.loadToMemory();
            for (DatabaseTableRecord records : table.getRecords()) {
                availableBalanceFrozen = getCurrentBalanceByMerchandise(BalanceType.AVAILABLE, records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME));
                cryptoBrokerWalletBalanceRecord = new CryptoBrokerWalletBalanceRecordImpl();
                cryptoBrokerWalletBalanceRecord.setAvilableBalance(new BigDecimal(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_AVAILABLE_BALANCE_COLUMN_NAME)).subtract(new BigDecimal(availableBalanceFrozen)));
                cryptoBrokerWalletBalanceRecord.setCurrencyType(CurrencyType.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)));
                if (CurrencyType.CRYPTO_MONEY.getCode() != CurrencyType.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)).getCode()) {
                    cryptoBrokerWalletBalanceRecord.setMerchandise(FiatCurrency.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                } else {
                    cryptoBrokerWalletBalanceRecord.setMerchandise(CryptoCurrency.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                }
                cryptoBrokerWalletBalanceRecord.setBookBalance(new BigDecimal(0));
                cryptoBrokerWalletBalanceRecord.setBrokerPublicKey(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BROKER_PUBLIC_KEY_COLUMN_NAME));

                cryptoBrokerWalletBalanceRecords.add(cryptoBrokerWalletBalanceRecord);


            }
        } catch (CantLoadTableToMemoryException e) {
            throw new CantCalculateBalanceException("Cant Load Table Memory", e, "", "");
        } catch (InvalidParameterException e) {
            throw new CantCalculateBalanceException("Invalid Parameter", e, "", "");
        }
        return cryptoBrokerWalletBalanceRecords;
    }

    public List<CryptoBrokerWalletBalanceRecord> getBookBalanceByMerchandiseFrozen() throws CantCalculateBalanceException {
        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_TABLE_NAME);
        CryptoBrokerWalletBalanceRecordImpl cryptoBrokerWalletBalanceRecord = null;
        List<CryptoBrokerWalletBalanceRecord> cryptoBrokerWalletBalanceRecords = new ArrayList<>();

        float bookBalanceFrozen = 0;
        try {
            table.loadToMemory();
            for (DatabaseTableRecord records : table.getRecords()) {
                bookBalanceFrozen = getCurrentBalanceByMerchandise(BalanceType.BOOK, records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME));
                cryptoBrokerWalletBalanceRecord = new CryptoBrokerWalletBalanceRecordImpl();
                cryptoBrokerWalletBalanceRecord.setBookBalance(new BigDecimal(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BOOK_BALANCE_COLUMN_NAME)).subtract(new BigDecimal(bookBalanceFrozen)));
                cryptoBrokerWalletBalanceRecord.setCurrencyType(CurrencyType.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)));
                if (CurrencyType.CRYPTO_MONEY.getCode() != CurrencyType.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)).getCode()) {
                    cryptoBrokerWalletBalanceRecord.setMerchandise(FiatCurrency.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                } else {
                    cryptoBrokerWalletBalanceRecord.setMerchandise(CryptoCurrency.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                }
                cryptoBrokerWalletBalanceRecord.setAvilableBalance( new BigDecimal(0));
                cryptoBrokerWalletBalanceRecord.setBrokerPublicKey(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BROKER_PUBLIC_KEY_COLUMN_NAME));

                cryptoBrokerWalletBalanceRecords.add(cryptoBrokerWalletBalanceRecord);


            }
        } catch (CantLoadTableToMemoryException e) {
            throw new CantCalculateBalanceException("Cant Load Table Memory", e, "", "");
        } catch (InvalidParameterException e) {
            throw new CantCalculateBalanceException("Invalid Parameter", e, "", "");
        }
        return cryptoBrokerWalletBalanceRecords;
    }

    //TODO Implementar:
    public Quote getQuote(final FermatEnum merchandise, final float quantity, final FiatCurrency payment) throws CantGetCryptoBrokerQuoteException {
        //Debemos de conocer el valor AvailableBalance menos los congelado, de esa forma tengo lo que puedo vender
        //Tambien podemos determinar devolver segun la volatilidad del mercado
        //Determinar mediante el precio del mercado a como esta esa mercancia
        float priceReference = 0;
        float availableBalanceFroze = 0;
        try {
            availableBalanceFroze = getCurrentBalanceByMerchandise(BalanceType.AVAILABLE, merchandise.getCode()) - getBalanceFrozenByMerchandise(merchandise, null, BalanceType.AVAILABLE, priceReference);
        } catch (CantLoadTableToMemoryException e) {
            throw new CantGetCryptoBrokerQuoteException("Cant Load Table", e, "", "");
        }

        QuoteImpl quote = new QuoteImpl(
                merchandise,
                payment,
                priceReference,
                availableBalanceFroze
        );

        return quote;
    }

    //TODO Implementar:
    public FiatIndex getMarketRate(final FermatEnum merchandise, FiatCurrency fiatCurrency, CurrencyType currencyType) throws CantGetCryptoBrokerMarketRateException {
        //Buscar el Spread en el setting de la wallet
        FiatIndexImpl fiatIndex = null;
        float spread = 0;
        try {
            spread = getSpread();
            //Determinar luego si ya se vendio o compro esa mercaderia para calcular el rate sobre el precio inicial y final
            //Tambien podemos determinar devolver segun la volatilidad del mercado
            float volatility = getVolatilityCalculation(merchandise, currencyType);
            //Determinar mediante el precio del mercado a como esta esa mercancia
            final float priceReference = 0; //Precio en dolar devuelto al pedir el precio del mercado
            final float priceRateSale = 0;
            final float priceRatePurchase = 0;
            final float priceSaleUp = (priceRateSale * ((spread / 2) / 100)) + priceRateSale;
            final float priceSaleDown = (priceRateSale * ((spread / 2) / 100)) - priceRateSale;
            final float pricePurchaseUp = (priceRatePurchase * ((spread / 2) / 100)) + priceRatePurchase;
            final float pricePurchaseDown = (priceRatePurchase * ((spread / 2) / 100)) + priceRatePurchase;

            fiatIndex = new FiatIndexImpl(
                    merchandise,
                    priceRateSale,
                    priceRatePurchase,
                    priceSaleUp,
                    priceSaleDown,
                    pricePurchaseUp,
                    pricePurchaseDown
            );
        } catch (CantLoadTableToMemoryException e) {
            throw new CantGetCryptoBrokerMarketRateException("Cant Load Table Memory", e, "", "");
        } catch (CantGetCryptoBrokerStockTransactionException e) {
            throw new CantGetCryptoBrokerMarketRateException("Cant Get Broker Stock Transaction", e, "", "");
        }
        return fiatIndex;
    }


    public List<CryptoBrokerStockTransaction> getCryptoBrokerStockTransactionsByMerchandise(FermatEnum merchandise, CurrencyType currencyType, TransactionType transactionType, BalanceType balanceType) throws CantGetCryptoBrokerStockTransactionException {
        DatabaseTable databaseTable = getStockWalletTransactionTable();
        FiatCurrency fiatCurrency = null;
        CryptoCurrency cryptoCurrency = null;
        FermatEnum fermatEnum = null;
        if (CurrencyType.CRYPTO_MONEY.getCode() != currencyType.getCode()) {
            try {
                fiatCurrency = FiatCurrency.getByCode(merchandise.getCode());
            } catch (InvalidParameterException e) {
                throw new CantGetCryptoBrokerStockTransactionException("Invalid Parameter", e, "", "");
            }
            fermatEnum = fiatCurrency;
        } else {
            try {
                cryptoCurrency = CryptoCurrency.getByCode(merchandise.getCode());
            } catch (InvalidParameterException e) {
                throw new CantGetCryptoBrokerStockTransactionException("Invalid Parameter", e, "", "");
            }
            fermatEnum = cryptoCurrency;
        }
        String query = "SELECT * FROM " +
                CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TABLE_NAME +
                " WHERE " +
                CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_MERCHANDISE_COLUMN_NAME +
                " = '" +
                fermatEnum.getCode() +
                "' AND " +
                CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME +
                " = '" +
                transactionType.getCode() +
                "' AND " +
                CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME +
                " = '" +
                balanceType.getCode() +
                "' ORDER BY " +
                CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TIMESTAMP_COLUMN_NAME +
                "' DESC";

        List<CryptoBrokerStockTransaction> transactions = new ArrayList<>();

        try {
            Collection<DatabaseTableRecord> records = databaseTable.customQuery(query, true);
            for (DatabaseTableRecord record : records) {
                if (CurrencyType.CRYPTO_MONEY != CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_CURRENCY_TYPE_COLUMN_NAME))) {
                    fiatCurrency = FiatCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_MERCHANDISE_COLUMN_NAME));
                    fermatEnum = fiatCurrency;
                } else {
                    cryptoCurrency = CryptoCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_MERCHANDISE_COLUMN_NAME));
                    fermatEnum = cryptoCurrency;
                }

                CryptoBrokerStockTransactionImpl cryptoBrokerStockTransaction = null;

                cryptoBrokerStockTransaction = new CryptoBrokerStockTransactionImpl(
                        new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_RUNNING_BOOK_BALANCE_COLUMN_NAME)),
                        new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_RUNNING_AVAILABLE_BALANCE_COLUMN_NAME)),
                        new BigDecimal(0),
                        new BigDecimal(0),
                        record.getUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_ID_COLUMN_NAME),
                        BalanceType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME)),
                        TransactionType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME)),
                        CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_CURRENCY_TYPE_COLUMN_NAME)),
                        fermatEnum,
                        record.getStringValue(""),
                        record.getStringValue(""),
                        new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_AMOUNT_COLUMN_NAME)),
                        record.getLongValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TIMESTAMP_COLUMN_NAME),
                        record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_MEMO_COLUMN_NAME),
                        new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_PRICE_REFERENCE_COLUMN_NAME)),
                        OriginTransaction.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_ORIGIN_TRANSACTION_COLUMN_NAME))
                );

                transactions.add(cryptoBrokerStockTransaction);

            }

        } catch (CantLoadTableToMemoryException e) {
            throw new CantGetCryptoBrokerStockTransactionException("Cant Load Table", e, "", "");
        } catch (InvalidParameterException e) {
            throw new CantGetCryptoBrokerStockTransactionException("Invalid Parameter", e, "", "");
        }

        return transactions;
    }

    /*
    * Add a new debit transaction.
    */
    public void addDebit(final CryptoBrokerStockTransactionRecord cryptoBrokerStockTransactionRecord, final BalanceType balanceType) throws CantAddDebitException {
        System.out.println("Agregando Debito-----------------------------------------------------------");
        try {
            if (isTransactionInTable(cryptoBrokerStockTransactionRecord.getTransactionId().toString(), TransactionType.DEBIT, balanceType))
                throw new CantAddDebitException(CantAddDebitException.DEFAULT_MESSAGE, null, null, "The transaction is already in the database");

            float value1 = cryptoBrokerStockTransactionRecord.getAmount().floatValue();
            float availableAmount = balanceType.equals(BalanceType.AVAILABLE) ? cryptoBrokerStockTransactionRecord.getAmount().floatValue() : 0L;
            float bookAmount = balanceType.equals(BalanceType.BOOK) ? cryptoBrokerStockTransactionRecord.getAmount().floatValue() : 0L;

            float availableRunningBalance = calculateAvailableRunningBalanceByMerchandise(-availableAmount, cryptoBrokerStockTransactionRecord.getMerchandise().getCode());
            float bookRunningBalance = calculateBookRunningBalanceByMerchandise(-bookAmount, cryptoBrokerStockTransactionRecord.getMerchandise().getCode());

            executeTransaction(cryptoBrokerStockTransactionRecord, TransactionType.DEBIT, balanceType, availableRunningBalance, bookRunningBalance);
        } catch (CantLoadTableToMemoryException e) {
            throw new CantAddDebitException("Error Add Debit", e, "", "");
        } catch (CantGetBalanceRecordException e) {
            throw new CantAddDebitException("Error Get Balance Record", e, "", "");
        } catch (CantExecuteCryptoBrokerTransactionException e) {
            throw new CantAddDebitException("Error Execute Transaction", e, "", "");
        }
    }

    /*
    * Add a new credit transaction.
    */
    public void addCredit(final CryptoBrokerStockTransactionRecord cryptoBrokerStockTransactionRecord, final BalanceType balanceType) throws CantAddCreditException {
        System.out.println("Agregando Credito-----------------------------------------------------------");
        try {
            if (isTransactionInTable(cryptoBrokerStockTransactionRecord.getTransactionId().toString(), TransactionType.CREDIT, balanceType))
                throw new CantAddCreditException(CantAddCreditException.DEFAULT_MESSAGE, null, null, "The transaction is already in the database");

            float availableAmount = balanceType.equals(BalanceType.AVAILABLE) ? cryptoBrokerStockTransactionRecord.getAmount().floatValue() : 0L;
            float bookAmount = balanceType.equals(BalanceType.BOOK) ? cryptoBrokerStockTransactionRecord.getAmount().floatValue() : 0L;

            float availableRunningBalance = calculateAvailableRunningBalanceByMerchandise(availableAmount, cryptoBrokerStockTransactionRecord.getMerchandise().getCode());
            float bookRunningBalance = calculateBookRunningBalanceByMerchandise(bookAmount, cryptoBrokerStockTransactionRecord.getMerchandise().getCode());

            executeTransaction(cryptoBrokerStockTransactionRecord, TransactionType.CREDIT, balanceType, availableRunningBalance, bookRunningBalance);
        } catch (CantLoadTableToMemoryException e) {
            throw new CantAddCreditException("Error Add Debit", e, "", "");
        } catch (CantGetBalanceRecordException e) {
            throw new CantAddCreditException("Error Get Balance Record", e, "", "");
        } catch (CantExecuteCryptoBrokerTransactionException e) {
            throw new CantAddCreditException("Error Execute Transaction", e, "", "");
        }
    }

    public void saveCryptoBrokerWalletSpreadSetting(CryptoBrokerWalletSettingSpread cryptoBrokerWalletSettingSpread) throws CantSaveCryptoBrokerWalletSettingException {
        DatabaseTransaction transaction = database.newTransaction();

        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_TABLE_NAME);
        try {
            DatabaseTableRecord Record = getCryptoBrokerWalletSpreadSettingRecord(cryptoBrokerWalletSettingSpread);

            DatabaseTableFilter filter = table.getEmptyTableFilter();
            filter.setType(DatabaseFilterType.EQUAL);
            filter.setValue(cryptoBrokerWalletSettingSpread.getId().toString());
            filter.setColumn(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_ID_COLUMN_NAME);

            if (isNewRecord(table, filter))
                transaction.addRecordToInsert(table, Record);
            else {
                table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                transaction.addRecordToUpdate(table, Record);
            }
            //I execute the transaction and persist the database
            database.executeTransaction(transaction);

        } catch (Exception e) {
            if (database != null)
                database.closeDatabase();
            throw new CantSaveCryptoBrokerWalletSettingException(CantSaveCryptoBrokerWalletSettingException.DEFAULT_MESSAGE, e, "Error trying to save the Crypto Broker Wallet Setting Spread in the database.", null);
        }
    }

    public CryptoBrokerWalletSettingSpread getCryptoBrokerWalletSpreadSetting() throws CantGetCryptoBrokerWalletSettingException {
        CryptoBrokerWalletSettingSpread cryptoBrokerWalletSettingSpread = null;
        try {
            for (DatabaseTableRecord record : getCryptoBrokerWalletSpreadSettingData()) {
                cryptoBrokerWalletSettingSpread = getCryptoBrokerWalletSpreadSetting(record);
            }
        } catch (CantLoadTableToMemoryException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Cant Load Table Memory", e, "", "");
        } catch (DatabaseOperationException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Database Operation", e, "", "");
        } catch (InvalidParameterException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Invalid Parameter", e, "", "");
        }
        return cryptoBrokerWalletSettingSpread;
    }

    public void saveCryptoBrokerWalletAssociatedSetting(CryptoBrokerWalletAssociatedSetting cryptoBrokerWalletAssociatedSetting) throws CantSaveCryptoBrokerWalletSettingException {
        DatabaseTransaction transaction = database.newTransaction();

        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_TABLE_NAME);
        try {
            DatabaseTableRecord Record = getCryptoBrokerWalletAssociatedSettingRecord(cryptoBrokerWalletAssociatedSetting);

            DatabaseTableFilter filter = table.getEmptyTableFilter();
            filter.setType(DatabaseFilterType.EQUAL);
            filter.setValue(cryptoBrokerWalletAssociatedSetting.getId().toString());
            filter.setColumn(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_ID_COLUMN_NAME);

            if (isNewRecord(table, filter))
                transaction.addRecordToInsert(table, Record);
            else {
                table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                transaction.addRecordToUpdate(table, Record);
            }
            //I execute the transaction and persist the database
            database.executeTransaction(transaction);

        } catch (Exception e) {
            if (database != null)
                database.closeDatabase();
            throw new CantSaveCryptoBrokerWalletSettingException(CantSaveCryptoBrokerWalletSettingException.DEFAULT_MESSAGE, e, "Error trying to save the Crypto Broker Wallet Setting Associated in the database.", null);
        }
    }

    public List<CryptoBrokerWalletAssociatedSetting> getCryptoBrokerWalletAssociatedSettings() throws CantGetCryptoBrokerWalletSettingException {
        List<CryptoBrokerWalletAssociatedSetting> cryptoBrokerWalletAssociatedSettings = new ArrayList<>();
        try {
            for (DatabaseTableRecord record : getCryptoBrokerWalletAssociatedSettingData()) {
                CryptoBrokerWalletAssociatedSetting cryptoBrokerWalletAssociatedSetting = getCryptoBrokerWalletAssociatedSetting(record);
                cryptoBrokerWalletAssociatedSettings.add(cryptoBrokerWalletAssociatedSetting);
            }
        } catch (CantLoadTableToMemoryException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Cant Load Table Memory", e, "", "");
        } catch (DatabaseOperationException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Database Operation", e, "", "");
        } catch (InvalidParameterException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Invalid Parameter", e, "", "");
        }
        return cryptoBrokerWalletAssociatedSettings;
    }

    public void saveCryptoBrokerWalletProviderSetting(CryptoBrokerWalletProviderSetting cryptoBrokerWalletProviderSetting) throws CantSaveCryptoBrokerWalletSettingException {
        DatabaseTransaction transaction = database.newTransaction();

        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_TABLE_NAME);
        try {
            DatabaseTableRecord Record = getCryptoBrokerWalletProviderSettingRecord(cryptoBrokerWalletProviderSetting);

            DatabaseTableFilter filter = table.getEmptyTableFilter();
            filter.setType(DatabaseFilterType.EQUAL);
            filter.setValue(cryptoBrokerWalletProviderSetting.getId().toString());
            filter.setColumn(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_ID_COLUMN_NAME);

            if (isNewRecord(table, filter))
                transaction.addRecordToInsert(table, Record);
            else {
                table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                transaction.addRecordToUpdate(table, Record);
            }
            //I execute the transaction and persist the database
            database.executeTransaction(transaction);

        } catch (Exception e) {
            if (database != null)
                database.closeDatabase();
            throw new CantSaveCryptoBrokerWalletSettingException(CantSaveCryptoBrokerWalletSettingException.DEFAULT_MESSAGE, e, "Error trying to save the Crypto Broker Wallet Setting Provider in the database.", null);
        }
    }

    public List<CryptoBrokerWalletProviderSetting> getCryptoBrokerWalletProviderSettings() throws CantGetCryptoBrokerWalletSettingException {
        List<CryptoBrokerWalletProviderSetting> cryptoBrokerWalletProviderSettings = new ArrayList<>();
        try {
            for (DatabaseTableRecord record : getCryptoBrokerWalletProviderSettingData()) {
                CryptoBrokerWalletProviderSetting cryptoBrokerWalletProviderSetting = getCryptoBrokerWalletProviderSetting(record);
                cryptoBrokerWalletProviderSettings.add(cryptoBrokerWalletProviderSetting);
            }
        } catch (CantLoadTableToMemoryException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Cant Load Table Memory", e, "", "");
        } catch (DatabaseOperationException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Database Operation", e, "", "");
        } catch (InvalidParameterException e) {
            throw new CantGetCryptoBrokerWalletSettingException("Invalid Parameter", e, "", "");
        }
        return cryptoBrokerWalletProviderSettings;
    }

    private float getVolatilityCalculation(final FermatEnum merchandise, CurrencyType currencyType) throws CantGetCryptoBrokerStockTransactionException
    {
        float volatility = 0, priceMinimun = 0, priceMaximum = 0;

        boolean sw = true;
        try {
            for(CryptoBrokerStockTransaction cryptoBrokerStockTransaction : getCryptoBrokerStockTransactionsByMerchandise(merchandise, currencyType, TransactionType.CREDIT, BalanceType.AVAILABLE))
            {
                if(sw) {
                    priceMaximum =  cryptoBrokerStockTransaction.getPriceReference().floatValue();
                    sw = false;
                }
                priceMinimun = cryptoBrokerStockTransaction.getPriceReference().floatValue();
            }
        } catch (CantGetCryptoBrokerStockTransactionException e) {
            throw new CantGetCryptoBrokerStockTransactionException("Cant Get Crypto Broker Stock Transaction", e, "", "");
        }

        volatility = (priceMaximum - priceMinimun)/(priceMaximum + priceMinimun);

        return volatility;
    }

    private float getBalanceFrozenByMerchandise(FermatEnum merchandise, CurrencyType currencyType, BalanceType balanceType, float priceReference) throws CantLoadTableToMemoryException
    {
        float rateFrozen    = 0;
        long  countRecords  = 0;

        //if (CurrencyType.CRYPTO_MONEY.getCode() != currencyType.getCode())
        //{
        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TABLE_NAME);
        table.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_MERCHANDISE_COLUMN_NAME, merchandise.getCode(), DatabaseFilterType.EQUAL);
        table.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode(), DatabaseFilterType.EQUAL);

        table.loadToMemory();

        for (DatabaseTableRecord records : table.getRecords())
        {
            records.getFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_AVAILABLE_BALANCE_COLUMN_NAME);

            try {
                if (TransactionType.CREDIT.getCode() ==  TransactionType.getByCode(records.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME)).getCode())
                {
                    if (records.getFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_AMOUNT_COLUMN_NAME) <= priceReference)
                    {
                        rateFrozen+=records.getFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_AMOUNT_COLUMN_NAME);
                        countRecords++;
                    }
                }
            } catch (InvalidParameterException e) {
                throw new CantLoadTableToMemoryException("Invalid Parameter", e, "", "");
            }
        }
        //}
        return rateFrozen;
    }
    private float getSpread() throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_TABLE_NAME);

        table.loadToMemory();
        float spread = table.getRecords().get(0).getFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_VALUE_COLUMN_NAME);

        return spread;
    }

    private CryptoBrokerWalletSettingSpread getCryptoBrokerWalletSpreadSetting(DatabaseTableRecord record) throws CantLoadTableToMemoryException, DatabaseOperationException, InvalidParameterException {
        CryptoBrokerWalletSettingSpreadImpl cryptoBrokerWalletSettingSpread = new CryptoBrokerWalletSettingSpreadImpl();

        cryptoBrokerWalletSettingSpread.setId(record.getUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_ID_COLUMN_NAME));
        cryptoBrokerWalletSettingSpread.setBrokerPublicKey(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_BROKER_PUBLIC_KEY_COLUMN_NAME));
        cryptoBrokerWalletSettingSpread.setSpread(record.getFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_VALUE_COLUMN_NAME));
        cryptoBrokerWalletSettingSpread.setRestockAutomatic(Boolean.valueOf(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_RESTOCK_AUTOMATIC)));

        return cryptoBrokerWalletSettingSpread;
    }

    private CryptoBrokerWalletProviderSetting getCryptoBrokerWalletProviderSetting(DatabaseTableRecord record) throws CantLoadTableToMemoryException, DatabaseOperationException, InvalidParameterException {
        CryptoBrokerWalletProviderSettingImpl cryptoBrokerWalletProviderSetting = new CryptoBrokerWalletProviderSettingImpl();

        cryptoBrokerWalletProviderSetting.setId(record.getUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_ID_COLUMN_NAME));
        cryptoBrokerWalletProviderSetting.setBrokerPublicKey(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_BROKER_PUBLIC_KEY_COLUMN_NAME));
        cryptoBrokerWalletProviderSetting.setDescription(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_DESCRIPTION_COLUMN_NAME));
        cryptoBrokerWalletProviderSetting.setPlugin(record.getUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_PLUGIN_COLUMN_NAME));

        return cryptoBrokerWalletProviderSetting;
    }

    private CryptoBrokerWalletAssociatedSetting getCryptoBrokerWalletAssociatedSetting(DatabaseTableRecord record) throws CantLoadTableToMemoryException, DatabaseOperationException, InvalidParameterException {
        CryptoBrokerWalletAssociatedSettingImpl cryptoBrokerWalletAssociatedSetting = new CryptoBrokerWalletAssociatedSettingImpl();

        cryptoBrokerWalletAssociatedSetting.setId(record.getUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_ID_COLUMN_NAME));
        cryptoBrokerWalletAssociatedSetting.setBrokerPublicKey(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_BROKER_PUBLIC_KEY_COLUMN_NAME));
        cryptoBrokerWalletAssociatedSetting.setWalletPublicKey(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_PUBLIC_KEY_COLUMN_NAME));
        cryptoBrokerWalletAssociatedSetting.setCurrencyType(CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_CURRENCY_TYPE_COLUMN_NAME)));
        if (CurrencyType.CRYPTO_MONEY != CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_CURRENCY_TYPE_COLUMN_NAME)))
            cryptoBrokerWalletAssociatedSetting.setMerchandise(FiatCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_MERCHANDISE_COLUMN_NAME)));
        else
            cryptoBrokerWalletAssociatedSetting.setMerchandise(CryptoCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_MERCHANDISE_COLUMN_NAME)));

        return cryptoBrokerWalletAssociatedSetting;
    }

    private List<DatabaseTableRecord> getCryptoBrokerWalletSpreadSettingData() throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_TABLE_NAME);

        //table.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_ID_COLUMN_NAME, Id, DatabaseFilterType.EQUAL);
        table.loadToMemory();

        return table.getRecords();
    }

    private List<DatabaseTableRecord> getCryptoBrokerWalletAssociatedSettingData() throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_TABLE_NAME);

        table.loadToMemory();

        return table.getRecords();
    }

    private List<DatabaseTableRecord> getCryptoBrokerWalletProviderSettingData() throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_TABLE_NAME);

        table.loadToMemory();

        return table.getRecords();
    }

    private DatabaseTable getDatabaseTable(String tableName) {

        return database.getTable(tableName);
    }

    private boolean isNewRecord(DatabaseTable table, DatabaseTableFilter filter) throws CantLoadTableToMemoryException {
        table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
        table.loadToMemory();
        if (table.getRecords().isEmpty())
            return true;
        else
            return false;
    }

    private DatabaseTableRecord getCryptoBrokerWalletSpreadSettingRecord(CryptoBrokerWalletSettingSpread cryptoBrokerWalletSettingSpread) throws DatabaseOperationException {
        DatabaseTable databaseTable = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_ID_COLUMN_NAME, cryptoBrokerWalletSettingSpread.getId());
        record.setFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_VALUE_COLUMN_NAME, cryptoBrokerWalletSettingSpread.getSpread());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_BROKER_PUBLIC_KEY_COLUMN_NAME, cryptoBrokerWalletSettingSpread.getBrokerPublicKey());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_SPREAD_RESTOCK_AUTOMATIC, String.valueOf(cryptoBrokerWalletSettingSpread.getRestockAutomatic()));

        return record;
    }

    private DatabaseTableRecord getCryptoBrokerWalletAssociatedSettingRecord(CryptoBrokerWalletAssociatedSetting cryptoBrokerWalletAssociatedSetting) throws DatabaseOperationException {
        DatabaseTable databaseTable = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_ID_COLUMN_NAME, cryptoBrokerWalletAssociatedSetting.getId());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_PUBLIC_KEY_COLUMN_NAME, cryptoBrokerWalletAssociatedSetting.getWalletPublicKey());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_BROKER_PUBLIC_KEY_COLUMN_NAME, cryptoBrokerWalletAssociatedSetting.getBrokerPublicKey());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_PLATFORM_COLUMN_NAME, cryptoBrokerWalletAssociatedSetting.getPlatform().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_BANK_ACCOUNT_COLUMN_NAME, cryptoBrokerWalletAssociatedSetting.getBankAccount());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_MERCHANDISE_COLUMN_NAME, cryptoBrokerWalletAssociatedSetting.getMerchandise().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_ASSOCIATED_CURRENCY_TYPE_COLUMN_NAME, cryptoBrokerWalletAssociatedSetting.getCurrencyType().getCode());

        return record;
    }

    private DatabaseTableRecord getCryptoBrokerWalletProviderSettingRecord(CryptoBrokerWalletProviderSetting cryptoBrokerWalletProviderSetting) throws DatabaseOperationException {
        DatabaseTable databaseTable = getDatabaseTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_ID_COLUMN_NAME, cryptoBrokerWalletProviderSetting.getId());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_PLUGIN_COLUMN_NAME, cryptoBrokerWalletProviderSetting.getPlugin().toString());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_BROKER_PUBLIC_KEY_COLUMN_NAME, cryptoBrokerWalletProviderSetting.getBrokerPublicKey());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_WALLET_PROVIDER_DESCRIPTION_COLUMN_NAME, cryptoBrokerWalletProviderSetting.getDescription());

        return record;
    }

    private void executeTransaction(final CryptoBrokerStockTransactionRecord stockTransaction, final TransactionType transactionType, final BalanceType balanceType, final float runningBookBalance, final float runningAvailableBalance) throws CantExecuteCryptoBrokerTransactionException {
        try {
            DatabaseTableRecord stockWalletTransactionRecord = constructStockWalletTransactionRecord(stockTransaction, transactionType, balanceType, runningAvailableBalance, runningBookBalance);
            DatabaseTableRecord stockWalletBalance = constructStockWalletBalanceRecord(stockTransaction, runningAvailableBalance, runningBookBalance);
            DatabaseTransaction transaction = database.newTransaction();
            transaction.addRecordToInsert(getStockWalletTransactionTable(), stockWalletTransactionRecord);

            DatabaseTable databaseTable = getBalancesTable();
            //if (CurrencyType.CRYPTO_MONEY != CurrencyType.getByCode(stockTransaction.getMerchandise().getCode()))
            databaseTable.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME, stockTransaction.getMerchandise().getCode().toString(), DatabaseFilterType.EQUAL);
            databaseTable.loadToMemory();
            if (databaseTable.getRecords().isEmpty()){
                transaction.addRecordToInsert(databaseTable, stockWalletBalance);
            }else{
                transaction.addRecordToUpdate(databaseTable, stockWalletBalance);
            }

            database.executeTransaction(transaction);

        } catch (CantLoadTableToMemoryException e) {
            throw new CantExecuteCryptoBrokerTransactionException("Cant Load Table To Memory", e, "", "");
        } catch (DatabaseTransactionFailedException e) {
            throw new CantExecuteCryptoBrokerTransactionException("Database Transaction Failed", e, "", "");
        }
    }

    private DatabaseTableRecord constructStockWalletTransactionRecord(final CryptoBrokerStockTransactionRecord cryptoBrokerStockTransactionRecord, final TransactionType transactionType, final BalanceType balanceType, final float availableRunningBalance, final float bookRunningBalance)
    {
        DatabaseTableRecord record = getStockWalletTransactionTable().getEmptyRecord();

        record.setUUIDValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_ID_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getTransactionId());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getBalanceType().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getTransactionType().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_AMOUNT_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getAmount().toPlainString());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_MERCHANDISE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getMerchandise().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_CURRENCY_TYPE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getCurrencyType().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getTransactionType().getCode());
        record.setFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_RUNNING_AVAILABLE_BALANCE_COLUMN_NAME, availableRunningBalance);
        record.setFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_RUNNING_BOOK_BALANCE_COLUMN_NAME, bookRunningBalance);
        record.setLongValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TIMESTAMP_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getTimestamp());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_MEMO_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getMemo());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_ORIGIN_TRANSACTION_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getOriginTransaction().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_PRICE_REFERENCE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getPriceReference().toPlainString());

        return record;
    }

    private DatabaseTableRecord constructStockWalletBalanceRecord(final CryptoBrokerStockTransactionRecord cryptoBrokerStockTransactionRecord, float  availableRunningBalance, float bookRunningBalance)
    {
        DatabaseTableRecord record = getBalancesTable().getEmptyRecord();

        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getMerchandise().getCode());
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getCurrencyType().getCode());
        record.setFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_AVAILABLE_BALANCE_COLUMN_NAME, availableRunningBalance);
        record.setFloatValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BOOK_BALANCE_COLUMN_NAME, bookRunningBalance);
        record.setStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BROKER_PUBLIC_KEY_COLUMN_NAME, cryptoBrokerStockTransactionRecord.getBrokerPublicKey());

        return record;

    }

    private float calculateAvailableRunningBalanceByMerchandise(final float transactionAmount, String merchandise) throws CantGetBalanceRecordException{
        return  getCurrentAvailableBalanceByMerchandise(merchandise) + transactionAmount;
    }

    private float calculateBookRunningBalanceByMerchandise(final float transactionAmount, String merchandise) throws CantGetBalanceRecordException{
        return  getCurrentBookBalanceByMerchandise(merchandise) + transactionAmount;
    }

    private float getCurrentAvailableBalanceByMerchandise(String merchandise) throws CantGetBalanceRecordException{
        return getCurrentBalanceByMerchandise(BalanceType.AVAILABLE, merchandise);
    }

    private float getCurrentBookBalanceByMerchandise(String merchandise) throws CantGetBalanceRecordException{
        return getCurrentBalanceByMerchandise(BalanceType.BOOK, merchandise);
    }

    private float getCurrentBalanceByMerchandise(BalanceType balanceType, String merchandise)
    {
        float balanceAmount = 0;
        try {

            if (balanceType == BalanceType.AVAILABLE)
                balanceAmount = getBalancesByMerchandiseRecord(merchandise).getLongValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_AVAILABLE_BALANCE_COLUMN_NAME);
            else
                balanceAmount = getBalancesByMerchandiseRecord(merchandise).getLongValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BOOK_BALANCE_COLUMN_NAME);

            return balanceAmount;
        }
        catch (Exception exception){
            return balanceAmount;
        }
    }

    private DatabaseTableRecord getBalancesByMerchandiseRecord(String merchandise) throws CantGetBalanceRecordException{
        try {
            DatabaseTable balancesTable = getBalancesTable();
            balancesTable.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME, merchandise, DatabaseFilterType.EQUAL);
            balancesTable.loadToMemory();
            if (!balancesTable.getRecords().isEmpty() ) {
                return balancesTable.getRecords().get(0);
            }
            else
            {
                return balancesTable.getRecords().get(0);
            }
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetBalanceRecordException("Error to get balances record",exception,"Can't load balance table" , "");
        }
    }

    private boolean isTransactionInTable(final String transactionId, final TransactionType transactionType, final BalanceType balanceType) throws CantLoadTableToMemoryException {
        DatabaseTable assetIssuerWalletTable = getStockWalletTransactionTable();
        assetIssuerWalletTable.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_ID_COLUMN_NAME, transactionId, DatabaseFilterType.EQUAL);
        assetIssuerWalletTable.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TRANSACTION_TYPE_COLUMN_NAME, transactionType.getCode(), DatabaseFilterType.EQUAL);
        assetIssuerWalletTable.addStringFilter(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_BALANCE_TYPE_COLUMN_NAME, balanceType.getCode(), DatabaseFilterType.EQUAL);
        assetIssuerWalletTable.loadToMemory();
        return !assetIssuerWalletTable.getRecords().isEmpty();
    }

    private DatabaseTable getStockWalletTransactionTable(){
        DatabaseTable databaseTable = database.getTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_TRANSACTIONS_TABLE_NAME);
        return databaseTable;
    }

    private List<CryptoBrokerWalletBalanceRecord> getCurrentBookBalanceByMerchandise() throws CantGetBalanceRecordException{
        return getCurrentBalanceByMerchandise(BalanceType.BOOK);
    }

    private List<CryptoBrokerWalletBalanceRecord> getCurrentAvailableBalanceByMerchandise() throws CantGetBalanceRecordException{
        return getCurrentBalanceByMerchandise(BalanceType.AVAILABLE);
    }

    private List<CryptoBrokerWalletBalanceRecord> getCurrentBalanceByMerchandise(final BalanceType balanceType) throws CantGetBalanceRecordException {
        List<CryptoBrokerWalletBalanceRecord> stockWalletBalances= new ArrayList<>();
        CryptoBrokerWalletBalanceRecordImpl cryptoBrokerWalletBalanceRecord = new CryptoBrokerWalletBalanceRecordImpl();
        if (balanceType == BalanceType.AVAILABLE){
            for (DatabaseTableRecord record : getBalancesRecord())
            {
                try {
                    if (CurrencyType.CRYPTO_MONEY != CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)))
                    {
                        cryptoBrokerWalletBalanceRecord.setMerchandise(FiatCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                    }else{
                        cryptoBrokerWalletBalanceRecord.setMerchandise(CryptoCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                    }
                    cryptoBrokerWalletBalanceRecord.setAvilableBalance(new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_AVAILABLE_BALANCE_COLUMN_NAME)));
                    cryptoBrokerWalletBalanceRecord.setBookBalance(new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BOOK_BALANCE_COLUMN_NAME)));
                    cryptoBrokerWalletBalanceRecord.setCurrencyType(CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)));
                    cryptoBrokerWalletBalanceRecord.setBrokerPublicKey(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BROKER_PUBLIC_KEY_COLUMN_NAME));
                    stockWalletBalances.add(cryptoBrokerWalletBalanceRecord);

                } catch (InvalidParameterException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            for (DatabaseTableRecord record : getBalancesRecord())
            {
                try {
                    if (CurrencyType.CRYPTO_MONEY != CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)))
                    {
                        cryptoBrokerWalletBalanceRecord.setMerchandise(FiatCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                    }else{
                        cryptoBrokerWalletBalanceRecord.setMerchandise(CryptoCurrency.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_MERCHANDISE_COLUMN_NAME)));
                    }
                    cryptoBrokerWalletBalanceRecord.setAvilableBalance(new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_AVAILABLE_BALANCE_COLUMN_NAME)));
                    cryptoBrokerWalletBalanceRecord.setBookBalance(new BigDecimal(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BOOK_BALANCE_COLUMN_NAME)));
                    cryptoBrokerWalletBalanceRecord.setCurrencyType(CurrencyType.getByCode(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_CURRENCY_TYPE_COLUMN_NAME)));
                    cryptoBrokerWalletBalanceRecord.setBrokerPublicKey(record.getStringValue(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_BROKER_PUBLIC_KEY_COLUMN_NAME));
                    stockWalletBalances.add(cryptoBrokerWalletBalanceRecord);

                } catch (InvalidParameterException e) {
                    e.printStackTrace();
                }
            }

        }
        return stockWalletBalances;
    }

    private List<DatabaseTableRecord> getBalancesRecord() throws CantGetBalanceRecordException{
        try {
            DatabaseTable balancesTable = getBalancesTable();
            balancesTable.loadToMemory();
            return balancesTable.getRecords();
        } catch (CantLoadTableToMemoryException exception) {
            throw new CantGetBalanceRecordException("Error to get balances record",exception,"Can't load balance table" , "");
        }
    }

    private DatabaseTable getBalancesTable(){
        DatabaseTable databaseTable = database.getTable(CryptoBrokerWalletDatabaseConstants.CRYPTO_BROKER_STOCK_BALANCE_TABLE_NAME);
        return databaseTable;
    }
}