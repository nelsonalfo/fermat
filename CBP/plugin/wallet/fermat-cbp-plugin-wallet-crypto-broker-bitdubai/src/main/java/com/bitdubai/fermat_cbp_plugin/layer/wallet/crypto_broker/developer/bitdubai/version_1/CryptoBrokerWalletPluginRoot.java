package com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantLoadFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantPersistFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddCreditCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantCreateCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CryptoBrokerWalletNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransactionRecord;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWallet;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.database.CryptoBrokerWalletDeveloperDatabaseFactory;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.exceptions.CantDeliverDatabaseException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerWalletImpl;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yordin Alayn on 19.10.15.
 * Modified by Franklin Marcano 30.11.2015
 */
public class CryptoBrokerWalletPluginRoot extends AbstractPlugin implements
        CryptoBrokerWalletManager,
        DatabaseManagerForDevelopers
        {

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM   , layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER         )
    private ErrorManager errorManager;

    public static final String CRYPTO_BROKER_WALLET_PRIVATE_KEYS_FILE_NAME = "cryptoBrokerWalletPrivateKeyWallet";

    private static final String CRYPTO_BROKER_WALLET_FILE_NAME = "walletsIds";
    private Map<String, UUID> cryptoBrokerWallet = new HashMap<>();
    boolean existWallet = false;
    String walletPublicKey = "walletPublicKeyTest";
    private CryptoBrokerWallet cryptoBrokerWalletImpl;

    public CryptoBrokerWalletPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        List<String> databasesNames = new ArrayList<>();
        Collection<UUID> ids = this.cryptoBrokerWallet.values();
        for (UUID id : ids)
            databasesNames.add(id.toString());
        CryptoBrokerWalletDeveloperDatabaseFactory dbFactory = new CryptoBrokerWalletDeveloperDatabaseFactory(this.pluginId.toString(), databasesNames);
        return dbFactory.getDatabaseList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        return CryptoBrokerWalletDeveloperDatabaseFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        List<DeveloperDatabaseTableRecord> databaseTableRecords = new ArrayList<>();
        UUID walletId = null;
        try {

            loadWalletMap();
            walletId = cryptoBrokerWallet.get(walletPublicKey);
            Database database = this.pluginDatabaseSystem.openDatabase(this.pluginId, walletId.toString());
            databaseTableRecords.addAll(CryptoBrokerWalletDeveloperDatabaseFactory.getDatabaseTableContent(developerObjectFactory, database, developerDatabaseTable));
            database.closeDatabase();
        } catch (CantOpenDatabaseException cantOpenDatabaseException) {
            /**
             * The database exists but cannot be open. I can not handle this situation.
             */
            FermatException e = new CantDeliverDatabaseException("I can't open database", cantOpenDatabaseException, "WalletId: " + walletId, "");
            this.errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (DatabaseNotFoundException databaseNotFoundException) {
            FermatException e = new CantDeliverDatabaseException("Database does not exists", databaseNotFoundException, "WalletId: " + walletId, "");
            this.errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (Exception exception) {
            FermatException e = new CantDeliverDatabaseException(CantDeliverDatabaseException.DEFAULT_MESSAGE, FermatException.wrapException(exception), "WalletId: " + walletId, "");
            this.errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
        // If we are here the database could not be opened, so we return an empry list
        return databaseTableRecords;
    }

    @Override
    public void start() throws CantStartPluginException {
        try{
            loadWalletMap();

            if(!existWallet) {
                createCryptoBrokerWallet(walletPublicKey);
            }

            cryptoBrokerWalletImpl = loadCryptoBrokerWallet(walletPublicKey);
            System.out.println("Start Plugin Crypto Broker Wallet ");
            this.serviceStatus = ServiceStatus.STARTED;
            //testAddCredito();
        }catch(CantStartPluginException exception){
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, exception);
            throw exception;
        }catch(Exception exception){
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, FermatException.wrapException(exception));
            throw new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, null);
        }
    }

    @Override
    public void pause() {
        this.serviceStatus = ServiceStatus.PAUSED;
    }

    @Override
    public void resume() {
        this.serviceStatus = ServiceStatus.STARTED;
    }

    @Override
    public void stop() {
        this.serviceStatus = ServiceStatus.STOPPED;
    }

    @Override
    public void createCryptoBrokerWallet(String walletPublicKey) throws CantCreateCryptoBrokerWalletException {
        try {
            CryptoBrokerWalletImpl cryptoBrokerWalletImpl = new CryptoBrokerWalletImpl(errorManager, pluginDatabaseSystem, pluginFileSystem, pluginId);

            UUID internalWalletId = cryptoBrokerWalletImpl.create(walletPublicKey);

            cryptoBrokerWallet.put(walletPublicKey, internalWalletId);
        }catch (CantCreateCryptoBrokerWalletException exception) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, FermatException.wrapException(exception));
            throw new CantCreateCryptoBrokerWalletException("Wallet Creation Failed", exception, "walletId: " + walletPublicKey, "");
        } catch (Exception exception) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, FermatException.wrapException(exception));
            throw new CantCreateCryptoBrokerWalletException("Wallet Creation Failed", FermatException.wrapException(exception), "walletId: " + walletPublicKey, "");
        }
    }

    @Override
    public CryptoBrokerWallet loadCryptoBrokerWallet(String walletPublicKey) throws CryptoBrokerWalletNotFoundException {

        try {
            CryptoBrokerWalletImpl cryptoBrokerWalletImpl = new CryptoBrokerWalletImpl(errorManager, pluginDatabaseSystem, pluginFileSystem, pluginId);

            UUID internalWalletId = cryptoBrokerWallet.get(walletPublicKey);
            cryptoBrokerWalletImpl.initialize(internalWalletId);
            return cryptoBrokerWalletImpl;

        }catch (CryptoBrokerWalletNotFoundException exception) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, FermatException.wrapException(exception));
            throw new CryptoBrokerWalletNotFoundException("I can't initialize wallet", exception, "", "");
        } catch (Exception exception) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_BROKER_WALLET, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, FermatException.wrapException(exception));
            throw new CryptoBrokerWalletNotFoundException(CryptoBrokerWalletNotFoundException.DEFAULT_MESSAGE, FermatException.wrapException(exception), "", "");
        }
    }

//    private void testAddCredito(){
//        CryptoBrokerStockTransactionRecord cryptoBrokerStockTransactionRecordBook = new CryptoBrokerStockTransactionRecord() {
//            @Override
//            public float getRunningBookBalance() {
//                return 0;
//            }
//
//            @Override
//            public float getRunningAvailableBalance() {
//                return 0;
//            }
//
//            @Override
//            public UUID getTransactionId() {
//                return UUID.randomUUID();
//            }
//
//            @Override
//            public BalanceType getBalanceType() {
//                return BalanceType.BOOK;
//            }
//
//            @Override
//            public TransactionType getTransactionType() {
//                return TransactionType.CREDIT;
//            }
//
//            @Override
//            public CurrencyType getCurrencyType() {
//                return CurrencyType.BANK_MONEY;
//            }
//
//            @Override
//            public FermatEnum getMerchandise() {
//                return FiatCurrency.US_DOLLAR;
//            }
//
//            @Override
//            public String getWalletPublicKey() {
//                return "walletPublicKeyTest";
//            }
//
//            @Override
//            public String getBrokerPublicKey() {
//                return "brokerPublicKeyTest";
//            }
//
//            @Override
//            public float getAmount() {
//                return 250;
//            }
//
//            @Override
//            public long getTimestamp() {
//                return new Date().getTime() / 1000;
//            }
//
//            @Override
//            public String getMemo() {
//                return "STOCK: compra";
//            }
//
//            @Override
//            public float getPriceReference() {
//                return 350;
//            }
//
//            @Override
//            public OriginTransaction getOriginTransaction() {
//                return OriginTransaction.STOCK_INITIAL;
//            }
//        };
//        CryptoBrokerStockTransactionRecord cryptoBrokerStockTransactionRecordAvailable = new CryptoBrokerStockTransactionRecord() {
//            @Override
//            public float getRunningBookBalance() {
//                return 0;
//            }
//
//            @Override
//            public float getRunningAvailableBalance() {
//                return 0;
//            }
//
//            @Override
//            public UUID getTransactionId() {
//                return UUID.randomUUID();
//            }
//
//            @Override
//            public BalanceType getBalanceType() {
//                return BalanceType.AVAILABLE;
//            }
//
//            @Override
//            public TransactionType getTransactionType() {
//                return TransactionType.CREDIT;
//            }
//
//            @Override
//            public CurrencyType getCurrencyType() {
//                return CurrencyType.BANK_MONEY;
//            }
//
//            @Override
//            public FermatEnum getMerchandise() {
//                return FiatCurrency.US_DOLLAR;
//            }
//
//            @Override
//            public String getWalletPublicKey() {
//                return "walletPublicKeyTest";
//            }
//
//            @Override
//            public String getBrokerPublicKey() {
//                return "brokerPublicKeyTest";
//            }
//
//            @Override
//            public float getAmount() {
//                return 250;
//            }
//
//            @Override
//            public long getTimestamp() {
//                return new Date().getTime() / 1000;
//            }
//
//            @Override
//            public String getMemo() {
//                return "STOCK: compra";
//            }
//
//            @Override
//            public float getPriceReference() {
//                return 350;
//            }
//
//            @Override
//            public OriginTransaction getOriginTransaction() {
//                return OriginTransaction.STOCK_INITIAL;
//            }
//        };
//        try {
//            loadCryptoBrokerWallet(walletPublicKey).getStockBalance().credit(cryptoBrokerStockTransactionRecordBook, BalanceType.BOOK);
//            loadCryptoBrokerWallet(walletPublicKey).getStockBalance().credit(cryptoBrokerStockTransactionRecordAvailable, BalanceType.AVAILABLE);
//        } catch (CantAddCreditCryptoBrokerWalletException e) {
//            e.printStackTrace();
//        } catch (CantGetStockCryptoBrokerWalletException e) {
//            e.printStackTrace();
//        } catch (CryptoBrokerWalletNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    private boolean loadWalletMap() throws CantStartPluginException {
        PluginTextFile walletFile = getWalletFile();
        String[] stringWallet = walletFile.getContent().split(";", -1);

        for (String stringWalletId : stringWallet)
            if (!stringWalletId.equals("")) {
                String[] idPair = stringWalletId.split(",", -1);
                cryptoBrokerWallet.put(idPair[0], UUID.fromString(idPair[1]));
                existWallet = true;
            }
        return existWallet;
    }

    private PluginTextFile getWalletFile() throws CantStartPluginException {
        try {
            PluginTextFile walletFile = pluginFileSystem.getTextFile(pluginId, "", CRYPTO_BROKER_WALLET_FILE_NAME, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            walletFile.loadFromMedia();
            return walletFile;
        } catch (FileNotFoundException | CantCreateFileException exception) {
            return createWalletFile();
        } catch (CantLoadFileException exception) {
            throw new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, exception, null, null);
        }
    }

    private PluginTextFile createWalletFile() throws CantStartPluginException {
        try {
            PluginTextFile walletFile = pluginFileSystem.createTextFile(pluginId, "", CRYPTO_BROKER_WALLET_FILE_NAME, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            walletFile.persistToMedia();
            return walletFile;
        } catch (CantCreateFileException | CantPersistFileException exception) {
            throw new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, exception, null, null);
        }
    }
}
