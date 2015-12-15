package com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.Agent;
import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankTransactionStatus;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.unhold.exceptions.CantGetUnholdTransactionException;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.unhold.exceptions.CantMakeUnholdTransactionException;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.unhold.interfaces.UnholdManager;
import com.bitdubai.fermat_cbp_api.all_definition.business_transaction.BankMoneyTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.CurrencyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionStatusRestockDestock;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddCreditCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantPerformTransactionException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CryptoBrokerWalletNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.exceptions.MissingBankMoneyDestockDataException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.structure.StockTransactionBankMoneyDestockFactory;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.structure.StockTransactionBankMoneyDestockManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.utils.BankTransactionParametersWrapper;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.utils.WalletTransactionWrapper;
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
public class BusinessTransactionBankMoneyDestockMonitorAgent implements Agent{
    //TODO: Documentar y manejo de excepciones.

    private Thread agentThread;

    private final ErrorManager errorManager;
    private final StockTransactionBankMoneyDestockManager stockTransactionBankMoneyDestockManager;
    private final CryptoBrokerWalletManager cryptoBrokerWalletManager;
    private final UnholdManager unHoldManager;
    StockTransactionBankMoneyDestockFactory stockTransactionBankMoneyDestockFactory;

    public BusinessTransactionBankMoneyDestockMonitorAgent(ErrorManager errorManager,
                                                           StockTransactionBankMoneyDestockManager stockTransactionBankMoneyDestockManager,
                                                           CryptoBrokerWalletManager cryptoBrokerWalletManager,
                                                           UnholdManager unHoldManager,
                                                           PluginDatabaseSystem pluginDatabaseSystem,
                                                           UUID pluginId) {

        this.errorManager                            = errorManager;
        this.stockTransactionBankMoneyDestockManager = stockTransactionBankMoneyDestockManager;
        this.cryptoBrokerWalletManager               = cryptoBrokerWalletManager;
        this.unHoldManager                           = unHoldManager;
        this.stockTransactionBankMoneyDestockFactory = new StockTransactionBankMoneyDestockFactory(pluginDatabaseSystem, pluginId);
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
                    errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
                }

            }
        }
    }

    private void doTheMainTask(){
        try {
            // I define the filter to null for all
            DatabaseTableFilter filter = null;
            for(BankMoneyTransaction bankMoneyTransaction : stockTransactionBankMoneyDestockFactory.getBankMoneyTransactionList(filter))
            {
                switch(bankMoneyTransaction.getTransactionStatus()) {
                    case INIT_TRANSACTION:
                        //Llamar al metodo de la interfaz public del manager de Bank Hold
                        //Luego cambiar el status al registro de la transaccion leido
                        bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_WALLET);
                        stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        break;
                    case IN_WALLET:
                        //Llamar al metodo de la interfaz public del manager de la wallet CBP
                        //Luego cambiar el status al registro de la transaccion leido
                        //Buscar el regsitro de la transaccion en manager de la wallet si lo consigue entonces le cambia el status de COMPLETED
                        try {
                            WalletTransactionWrapper walletTransactionRecord = new WalletTransactionWrapper(
                                    bankMoneyTransaction.getTransactionId(),
                                    null,
                                    BalanceType.AVAILABLE,
                                    TransactionType.CREDIT,
                                    CurrencyType.BANK_MONEY,
                                    bankMoneyTransaction.getCbpWalletPublicKey(),
                                    bankMoneyTransaction.getActorPublicKey(),
                                    bankMoneyTransaction.getAmount(),
                                    new Date().getTime() / 1000,
                                    bankMoneyTransaction.getConcept(),
                                    bankMoneyTransaction.getPriceReference(),
                                    bankMoneyTransaction.getOriginTransaction());

                            cryptoBrokerWalletManager.loadCryptoBrokerWallet(bankMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().credit(walletTransactionRecord, BalanceType.BOOK);
                            cryptoBrokerWalletManager.loadCryptoBrokerWallet(bankMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().credit(walletTransactionRecord, BalanceType.AVAILABLE);
                            bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_UNHOLD);
                            stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);

                        } catch (CryptoBrokerWalletNotFoundException e) {
                            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
                        } catch (CantGetStockCryptoBrokerWalletException e) {
                            e.printStackTrace();
                        } catch (CantAddCreditCryptoBrokerWalletException e) {
                            e.printStackTrace();
                        }
                        break;
                    case IN_UNHOLD:
                        //Llamar al metodo de la interfaz public del manager de la wallet CBP
                        //Luego cambiar el status al registro de la transaccion leido
                        //Buscar el regsitro de la transaccion en manager de Bank Hold y si lo consigue entonces le cambia el status de IN_WALLET y hace el credito
                        BankTransactionParametersWrapper bankTransactionParametersWrapper = new BankTransactionParametersWrapper(

                                bankMoneyTransaction.getTransactionId(),
                                bankMoneyTransaction.getFiatCurrency(),
                                bankMoneyTransaction.getBnkWalletPublicKey(),
                                bankMoneyTransaction.getActorPublicKey(),
                                bankMoneyTransaction.getBankAccount(),
                                bankMoneyTransaction.getAmount(),
                                bankMoneyTransaction.getMemo(),
                                "pluginId");
                        unHoldManager.unHold(bankTransactionParametersWrapper);
                        BankTransactionStatus bankTransactionStatus =  unHoldManager.getUnholdTransactionsStatus(bankMoneyTransaction.getTransactionId());
                        if (BankTransactionStatus.CONFIRMED.getCode() == bankTransactionStatus.getCode())
                        {
                            bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.COMPLETED);
                            stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        }
                        if (BankTransactionStatus.REJECTED.getCode() == bankTransactionStatus.getCode())
                        {
                            bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.REJECTED);
                            stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        }
                        break;
                }
            }
        } catch (DatabaseOperationException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
        } catch (InvalidParameterException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
        } catch (MissingBankMoneyDestockDataException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
        } catch (CantGetUnholdTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
        } catch (CantMakeUnholdTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);;
        }
    }
}
