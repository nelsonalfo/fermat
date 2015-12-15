package com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_restock.developer.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.Agent;
import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_cbp_api.all_definition.business_transaction.CryptoMoneyTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionStatusRestockDestock;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddCreditCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddDebitCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CryptoBrokerWalletNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_restock.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_restock.developer.bitdubai.version_1.exceptions.MissingCryptoMoneyRestockDataException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_restock.developer.bitdubai.version_1.structure.StockTransactionCryptoMoneyRestockFactory;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_restock.developer.bitdubai.version_1.structure.StockTransactionCryptoMoneyRestockManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_restock.developer.bitdubai.version_1.utils.CryptoTransactionParametersWrapper;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_restock.developer.bitdubai.version_1.utils.WalletTransactionWrapper;
import com.bitdubai.fermat_ccp_api.all_definition.enums.CryptoTransactionStatus;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.Unhold.exceptions.CantCreateUnHoldTransactionException;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.Unhold.exceptions.CantGetUnHoldTransactionException;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.Unhold.interfaces.CryptoUnholdTransactionManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * The Class <code>BusinessTransactionBankMoneyRestockMonitorAgent</code>
 * contains the logic for handling agent transactional
 * Created by franklin on 17/11/15.
 */
public class StockTransactionsCryptoMoneyRestockMonitorAgent implements Agent{
    //TODO: Manejo de Eventos

    private Thread agentThread;

    private final ErrorManager errorManager;
    private final StockTransactionCryptoMoneyRestockManager stockTransactionCryptoMoneyRestockManager;
    private final CryptoBrokerWalletManager cryptoBrokerWalletManager;
    private final CryptoUnholdTransactionManager cryptoUnholdTransactionManager;
    private final StockTransactionCryptoMoneyRestockFactory stockTransactionCryptoMoneyRestockFactory;

    public StockTransactionsCryptoMoneyRestockMonitorAgent(ErrorManager errorManager,
                                                           StockTransactionCryptoMoneyRestockManager stockTransactionCryptoMoneyRestockManager,
                                                           CryptoBrokerWalletManager cryptoBrokerWalletManager,
                                                           CryptoUnholdTransactionManager cryptoUnholdTransactionManager,
                                                           PluginDatabaseSystem pluginDatabaseSystem,
                                                           UUID pluginId)
                                                           {

        this.errorManager                              = errorManager;
        this.stockTransactionCryptoMoneyRestockManager = stockTransactionCryptoMoneyRestockManager;
        this.cryptoBrokerWalletManager                 = cryptoBrokerWalletManager;
        this.cryptoUnholdTransactionManager            = cryptoUnholdTransactionManager;
        this.stockTransactionCryptoMoneyRestockFactory = new StockTransactionCryptoMoneyRestockFactory(pluginDatabaseSystem, pluginId);
    }
    @Override
    public void start() throws CantStartAgentException {
        Logger LOG = Logger.getGlobal();
        LOG.info("Bank Money Restock Transaction monitor agent starting");

        final MonitorAgent monitorAgent = new MonitorAgent(errorManager);

        this.agentThread = new Thread(monitorAgent);
        this.agentThread.start();
    }

    @Override
    public void stop() {
        this.agentThread.interrupt();
    }

    /**
     * Private class which implements runnable and is started by the Agent
     * Based on MonitorAgent created by Rodrigo Acosta
     */
    private final class MonitorAgent implements Runnable {

        private final ErrorManager errorManager;
        public final int SLEEP_TIME = 5000;
        int iterationNumber = 0;
        boolean threadWorking;

        public MonitorAgent(final ErrorManager errorManager) {

            this.errorManager = errorManager;
        }

        @Override
        public void run() {
            threadWorking = true;
            while (threadWorking) {
                /**
                 * Increase the iteration counter
                 */
                iterationNumber++;
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException interruptedException) {
                    return;
                }

                /**
                 * now I will check if there are pending transactions to raise the event
                 */
                try {
                    doTheMainTask();
                } catch (Exception e) {
                    errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                }

            }
        }
    }

    private void doTheMainTask(){
        try {
            // I define the filter to null for all
            DatabaseTableFilter filter = null;
            for(CryptoMoneyTransaction cryptoMoneyTransaction : stockTransactionCryptoMoneyRestockFactory.getCryptoMoneyTransactionList(filter))
            {
                switch(cryptoMoneyTransaction.getTransactionStatus()) {
                    case INIT_TRANSACTION:
                        //Llamar al metodo de la interfaz public del manager de Bank Hold
                        //Luego cambiar el status al registro de la transaccion leido
                        CryptoTransactionParametersWrapper cryptoTransactionParametersWrapper = new CryptoTransactionParametersWrapper(

                                cryptoMoneyTransaction.getTransactionId(),
                                cryptoMoneyTransaction.getCryptoCurrency(),
                                cryptoMoneyTransaction.getCryWalletPublicKey(),
                                cryptoMoneyTransaction.getActorPublicKey(),
                                cryptoMoneyTransaction.getAmount(),
                                cryptoMoneyTransaction.getMemo(),
                                "pluginId");
                        cryptoUnholdTransactionManager.createCryptoUnholdTransaction(cryptoTransactionParametersWrapper);

                        cryptoMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_HOLD);
                        stockTransactionCryptoMoneyRestockFactory.saveCryptoMoneyRestockTransactionData(cryptoMoneyTransaction);
                        break;
                    case IN_HOLD:
                        //Llamar al metodo de la interfaz public del manager de la wallet CBP
                        //Luego cambiar el status al registro de la transaccion leido
                        //Buscar el regsitro de la transaccion en manager de la wallet si lo consigue entonces le cambia el status de COMPLETED
                        CryptoTransactionStatus cryptoTransactionStatus = cryptoUnholdTransactionManager.getCryptoUnholdTransactionStatus(cryptoMoneyTransaction.getTransactionId());
                        if (CryptoTransactionStatus.CONFIRMED.getCode() == cryptoTransactionStatus.getCode()) {
                            cryptoMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.COMPLETED);
                            try {
                                WalletTransactionWrapper walletTransactionRecord = new WalletTransactionWrapper(

                                        cryptoMoneyTransaction.getTransactionId(),
                                        null,
                                        BalanceType.AVAILABLE,
                                        TransactionType.CREDIT,
                                        CurrencyType.BANK_MONEY,
                                        cryptoMoneyTransaction.getCbpWalletPublicKey(),
                                        cryptoMoneyTransaction.getActorPublicKey(),
                                        cryptoMoneyTransaction.getAmount(),
                                        new Date().getTime() / 1000,
                                        cryptoMoneyTransaction.getConcept(),
                                        cryptoMoneyTransaction.getPriceReference(),
                                        cryptoMoneyTransaction.getOriginTransaction());

                                cryptoBrokerWalletManager.loadCryptoBrokerWallet(cryptoMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().debit(walletTransactionRecord, BalanceType.BOOK);
                                cryptoBrokerWalletManager.loadCryptoBrokerWallet(cryptoMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().debit(walletTransactionRecord, BalanceType.AVAILABLE);
                            } catch (CryptoBrokerWalletNotFoundException e) {
                                errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
                            } catch (CantGetStockCryptoBrokerWalletException e) {
                                errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
                            } catch (CantAddDebitCryptoBrokerWalletException e) {
                                errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
                            }
                            cryptoMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_WALLET);
                            stockTransactionCryptoMoneyRestockFactory.saveCryptoMoneyRestockTransactionData(cryptoMoneyTransaction);
                        }
                        break;
                    case IN_WALLET:
                        cryptoMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.COMPLETED);
                        stockTransactionCryptoMoneyRestockFactory.saveCryptoMoneyRestockTransactionData(cryptoMoneyTransaction);
                        break;
                }
            }
        } catch (DatabaseOperationException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (InvalidParameterException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (MissingCryptoMoneyRestockDataException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantCreateUnHoldTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantGetUnHoldTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CRYPTO_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
    }
}
