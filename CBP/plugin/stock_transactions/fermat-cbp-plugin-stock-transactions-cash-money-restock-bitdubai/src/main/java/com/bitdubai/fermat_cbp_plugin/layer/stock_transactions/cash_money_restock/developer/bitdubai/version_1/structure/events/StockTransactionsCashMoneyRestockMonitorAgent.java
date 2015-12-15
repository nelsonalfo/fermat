package com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.Agent;
import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_cbp_api.all_definition.business_transaction.CashMoneyTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionStatusRestockDestock;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddCreditCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddDebitCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CryptoBrokerWalletNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.exceptions.MissingCashMoneyRestockDataException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.structure.StockTransactionCashMoneyRestockFactory;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.structure.StockTransactionCashMoneyRestockManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.utils.CashTransactionParametersWrapper;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.cash_money_restock.developer.bitdubai.version_1.utils.WalletTransactionWrapper;
import com.bitdubai.fermat_csh_api.all_definition.enums.CashTransactionStatus;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.exceptions.CantCreateHoldTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.exceptions.CantGetHoldTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.interfaces.CashHoldTransactionManager;
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
public class StockTransactionsCashMoneyRestockMonitorAgent implements Agent{
    //TODO: Manejo de Eventos

    private Thread agentThread;

    private final ErrorManager errorManager;
    private final StockTransactionCashMoneyRestockManager stockTransactionCashMoneyRestockManager;
    private final CryptoBrokerWalletManager cryptoBrokerWalletManager;
    private final CashHoldTransactionManager cashHoldTransactionManager;
    private final StockTransactionCashMoneyRestockFactory stockTransactionCashMoneyRestockFactory;

    public StockTransactionsCashMoneyRestockMonitorAgent(ErrorManager errorManager,
                                                         StockTransactionCashMoneyRestockManager stockTransactionCashMoneyRestockManager,
                                                         CryptoBrokerWalletManager cryptoBrokerWalletManager,
                                                         CashHoldTransactionManager cashHoldTransactionManager,
                                                         PluginDatabaseSystem pluginDatabaseSystem,
                                                         UUID pluginId) {

        this.errorManager                            = errorManager;
        this.stockTransactionCashMoneyRestockManager = stockTransactionCashMoneyRestockManager;
        this.cryptoBrokerWalletManager               = cryptoBrokerWalletManager;
        this.cashHoldTransactionManager              = cashHoldTransactionManager;
        this.stockTransactionCashMoneyRestockFactory = new StockTransactionCashMoneyRestockFactory(pluginDatabaseSystem, pluginId);
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
                    errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                }

            }
        }
    }

    private void doTheMainTask(){
        try {
            // I define the filter to null for all
            DatabaseTableFilter filter = null;
            for(CashMoneyTransaction cashMoneyTransaction : stockTransactionCashMoneyRestockFactory.getCashMoneyTransactionList(filter))
            {
                switch(cashMoneyTransaction.getTransactionStatus()) {
                    case INIT_TRANSACTION:
                        //Llamar al metodo de la interfaz public del manager de Bank Hold
                        //Luego cambiar el status al registro de la transaccion leido
                        //Llamar al metodo de la interfaz public del manager de la wallet CBP
                        //Luego cambiar el status al registro de la transaccion leido
                        //Buscar el regsitro de la transaccion en manager de Bank Hold y si lo consigue entonces le cambia el status de IN_WALLET y hace el credito
                        CashTransactionParametersWrapper cashTransactionParametersWrapper = new CashTransactionParametersWrapper(

                                cashMoneyTransaction.getTransactionId(),
                                cashMoneyTransaction.getFiatCurrency(),
                                cashMoneyTransaction.getCashWalletPublicKey(),
                                cashMoneyTransaction.getActorPublicKey(),
                                cashMoneyTransaction.getAmount(),
                                cashMoneyTransaction.getCashReference(),
                                cashMoneyTransaction.getMemo());
                        //"pluginId");
                        cashHoldTransactionManager.createCashHoldTransaction(cashTransactionParametersWrapper);
                        CashTransactionStatus castTransactionStatus =  cashHoldTransactionManager.getCashHoldTransactionStatus(cashMoneyTransaction.getTransactionId());
                        if (castTransactionStatus.CONFIRMED.getCode() == castTransactionStatus.getCode())
                        {
                            cashMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_HOLD);
                            stockTransactionCashMoneyRestockFactory.saveCashMoneyRestockTransactionData(cashMoneyTransaction);
                        }
                        if (castTransactionStatus.REJECTED.getCode() == castTransactionStatus.getCode())
                        {
                            cashMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.REJECTED);
                            stockTransactionCashMoneyRestockFactory.saveCashMoneyRestockTransactionData(cashMoneyTransaction);
                        }
                        break;
                    case IN_HOLD:
                        //Llamar al metodo de la interfaz public del manager de la wallet CBP
                        //Luego cambiar el status al registro de la transaccion leido
                        //Buscar el regsitro de la transaccion en manager de la wallet si lo consigue entonces le cambia el status de COMPLETED
                        try {
                            WalletTransactionWrapper walletTransactionRecord = new WalletTransactionWrapper(
                                    cashMoneyTransaction.getTransactionId(),
                                    null,
                                    BalanceType.AVAILABLE,
                                    TransactionType.CREDIT,
                                    CurrencyType.BANK_MONEY,
                                    cashMoneyTransaction.getCbpWalletPublicKey(),
                                    cashMoneyTransaction.getActorPublicKey(),
                                    cashMoneyTransaction.getAmount(),
                                    new Date().getTime() / 1000,
                                    cashMoneyTransaction.getConcept(),
                                    cashMoneyTransaction.getPriceReference(),
                                    cashMoneyTransaction.getOriginTransaction());

                            cryptoBrokerWalletManager.loadCryptoBrokerWallet(cashMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().debit(walletTransactionRecord, BalanceType.BOOK);
                            cryptoBrokerWalletManager.loadCryptoBrokerWallet(cashMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().debit(walletTransactionRecord, BalanceType.AVAILABLE);
                            cashMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_WALLET);
                            stockTransactionCashMoneyRestockFactory.saveCashMoneyRestockTransactionData(cashMoneyTransaction);

                        } catch (CryptoBrokerWalletNotFoundException e) {
                            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
                        } catch (CantGetStockCryptoBrokerWalletException e) {
                            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                        } catch (CantAddDebitCryptoBrokerWalletException e) {
                            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                        }

                        break;
                    case IN_WALLET:
                        cashMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.COMPLETED);
                        stockTransactionCashMoneyRestockFactory.saveCashMoneyRestockTransactionData(cashMoneyTransaction);
                        break;
                }
            }
        } catch (DatabaseOperationException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (InvalidParameterException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (MissingCashMoneyRestockDataException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantGetHoldTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantCreateHoldTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.CASH_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
    }
}
