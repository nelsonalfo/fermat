package com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatAgent;
import com.bitdubai.fermat_api.layer.all_definition.enums.AgentStatus;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankTransactionStatus;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.unhold.exceptions.CantGetUnholdTransactionException;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.unhold.exceptions.CantMakeUnholdTransactionException;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.unhold.interfaces.UnholdManager;
import com.bitdubai.fermat_cbp_api.all_definition.business_transaction.BankMoneyTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.MoneyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionStatusRestockDestock;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantAddDebitCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CryptoBrokerWalletNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.exceptions.MissingBankMoneyDestockDataException;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.structure.StockTransactionBankMoneyDestockFactory;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.structure.StockTransactionBankMoneyDestockManager;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.utils.BankTransactionParametersWrapper;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_destock.developer.bitdubai.version_1.utils.WalletTransactionWrapper;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * The Class <code>BusinessTransactionBankMoneyRestockMonitorAgent</code>
 * contains the logic for handling agent transactional
 * Created by franklin on 17/11/15.
 */
public class BusinessTransactionBankMoneyDestockMonitorAgent extends FermatAgent {
    //TODO: Documentar y manejo de excepciones.

    private Thread agentThread;

    private final ErrorManager errorManager;
    private final StockTransactionBankMoneyDestockManager stockTransactionBankMoneyDestockManager;
    private final CryptoBrokerWalletManager cryptoBrokerWalletManager;
    private final UnholdManager unHoldManager;
    StockTransactionBankMoneyDestockFactory stockTransactionBankMoneyDestockFactory;
    private UUID pluginId;
    public final int SLEEP_TIME = 5000;
    int iterationNumber = 0;
    boolean threadWorking;


    /**
     * Constructor for BusinessTransactionBankMoneyDestockMonitorAgent
     *
     * @param errorManager
     * @param stockTransactionBankMoneyDestockManager
     * @param cryptoBrokerWalletManager
     * @param unHoldManager
     * @param pluginDatabaseSystem
     * @param pluginId
     */
    public BusinessTransactionBankMoneyDestockMonitorAgent(ErrorManager errorManager,
                                                           StockTransactionBankMoneyDestockManager stockTransactionBankMoneyDestockManager,
                                                           CryptoBrokerWalletManager cryptoBrokerWalletManager,
                                                           UnholdManager unHoldManager,
                                                           PluginDatabaseSystem pluginDatabaseSystem,
                                                           UUID pluginId) {

        this.errorManager = errorManager;
        this.stockTransactionBankMoneyDestockManager = stockTransactionBankMoneyDestockManager;
        this.cryptoBrokerWalletManager = cryptoBrokerWalletManager;
        this.unHoldManager = unHoldManager;
        this.stockTransactionBankMoneyDestockFactory = new StockTransactionBankMoneyDestockFactory(pluginDatabaseSystem, pluginId);
        this.pluginId = pluginId;

        this.agentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning())
                    process();
            }
        }, this.getClass().getSimpleName());
    }

    /**
     * Starts the agent
     *
     * @throws CantStartAgentException
     */
    @Override
    public void start() throws CantStartAgentException {
        Logger LOG = Logger.getGlobal();
        LOG.info("Bank Money Destock Transaction monitor agent starting");

        //final MonitorAgent monitorAgent = new MonitorAgent(errorManager);

        //this.agentThread = new Thread(monitorAgent);
        this.agentThread.start();
        this.status = AgentStatus.STARTED;
    }

    @Override
    public void stop() {
        if (isRunning())
            this.agentThread.interrupt();
        this.status = AgentStatus.STOPPED;
    }

    public void process() {

        while (isRunning()) {

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException interruptedException) {
                cleanResources();
                return;
            }

            doTheMainTask();

            if (agentThread.isInterrupted()) {
                cleanResources();
                return;
            }
        }
    }

    /**
     * Private class which implements runnable and is started by the Agent
     * Based on MonitorAgent created by Rodrigo Acosta
     */
//    private final class MonitorAgent implements Runnable {
//
//        private final ErrorManager errorManager;
//        public final int SLEEP_TIME = 5000;
//        int iterationNumber = 0;
//        boolean threadWorking;
//
//        public MonitorAgent(final ErrorManager errorManager) {
//
//            this.errorManager = errorManager;
//        }
//
//        @Override
//        public void run() {
//            threadWorking = true;
//            while (threadWorking) {
//                /**
//                 * Increase the iteration counter
//                 */
//                iterationNumber++;
//                try {
//                    Thread.sleep(SLEEP_TIME);
//
//                    /**
//                     * now I will check if there are pending transactions to raise the event
//                     */
//                    doTheMainTask();
//                } catch (InterruptedException e) {
//                    errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_RESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
//                }
//
//            }
//        }
//    }
    private void doTheMainTask() {
        try {
            // I define the filter to null for all
            DatabaseTableFilter filter = null;
            for (BankMoneyTransaction bankMoneyTransaction : stockTransactionBankMoneyDestockFactory.getBankMoneyTransactionList(filter)) {
                switch (bankMoneyTransaction.getTransactionStatus()) {
                    case INIT_TRANSACTION:
                        //Llamar al metodo de la interfaz public del manager de Bank Hold
                        //Luego cambiar el status al registro de la transaccion leido
                        bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_WALLET);
                        stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        break;
                    case IN_WALLET: {
                        //Llamar al metodo de la interfaz public del manager de la wallet CBP
                        //Luego cambiar el status al registro de la transaccion leido
                        //Buscar el regsitro de la transaccion en manager de la wallet si lo consigue entonces le cambia el status de COMPLETED
                        WalletTransactionWrapper walletTransactionRecordBook = new WalletTransactionWrapper(
                                bankMoneyTransaction.getTransactionId(),
                                bankMoneyTransaction.getFiatCurrency(),
                                BalanceType.BOOK,
                                TransactionType.DEBIT,
                                MoneyType.BANK,
                                bankMoneyTransaction.getCbpWalletPublicKey(),
                                bankMoneyTransaction.getActorPublicKey(),
                                bankMoneyTransaction.getAmount(),
                                new Date().getTime() / 1000,
                                bankMoneyTransaction.getConcept(),
                                bankMoneyTransaction.getPriceReference(),
                                bankMoneyTransaction.getOriginTransaction(),
                                bankMoneyTransaction.getOriginTransactionId(),
                                false);

                        WalletTransactionWrapper walletTransactionRecordAvailable = new WalletTransactionWrapper(
                                bankMoneyTransaction.getTransactionId(),
                                bankMoneyTransaction.getFiatCurrency(),
                                BalanceType.AVAILABLE,
                                TransactionType.DEBIT,
                                MoneyType.BANK,
                                bankMoneyTransaction.getCbpWalletPublicKey(),
                                bankMoneyTransaction.getActorPublicKey(),
                                bankMoneyTransaction.getAmount(),
                                new Date().getTime() / 1000,
                                bankMoneyTransaction.getConcept(),
                                bankMoneyTransaction.getPriceReference(),
                                bankMoneyTransaction.getOriginTransaction(),
                                bankMoneyTransaction.getOriginTransactionId(),
                                false);
                        //TODO:Solo para testear
                        bankMoneyTransaction.setCbpWalletPublicKey("walletPublicKeyTest");
                        cryptoBrokerWalletManager.loadCryptoBrokerWallet(bankMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().debit(walletTransactionRecordBook, BalanceType.BOOK);
                        cryptoBrokerWalletManager.loadCryptoBrokerWallet(bankMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().debit(walletTransactionRecordAvailable, BalanceType.AVAILABLE);
                        bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_UNHOLD);
                        stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
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
                                pluginId.toString());

                        if (!unHoldManager.isTransactionRegistered(bankMoneyTransaction.getTransactionId()))
                            unHoldManager.unHold(bankTransactionParametersWrapper);

                        bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.IN_EJECUTION);
                        stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        break;
                    case IN_EJECUTION:
                        BankTransactionStatus bankTransactionStatus = unHoldManager.getUnholdTransactionsStatus(bankMoneyTransaction.getTransactionId());
                        if (BankTransactionStatus.CONFIRMED.getCode() == bankTransactionStatus.getCode()) {
                            bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.COMPLETED);
                            stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        }
                        if (BankTransactionStatus.REJECTED.getCode() == bankTransactionStatus.getCode()) {
                            bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.REJECTED);
                            stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        }
                        break;
                    case REJECTED:{
                        WalletTransactionWrapper walletTransactionRecordBook = new WalletTransactionWrapper(
                                bankMoneyTransaction.getTransactionId(),
                                bankMoneyTransaction.getFiatCurrency(),
                                BalanceType.BOOK,
                                TransactionType.CREDIT,
                                MoneyType.BANK,
                                bankMoneyTransaction.getCbpWalletPublicKey(),
                                bankMoneyTransaction.getActorPublicKey(),
                                bankMoneyTransaction.getAmount(),
                                new Date().getTime() / 1000,
                                bankMoneyTransaction.getConcept(),
                                bankMoneyTransaction.getPriceReference(),
                                bankMoneyTransaction.getOriginTransaction(),
                                bankMoneyTransaction.getOriginTransactionId(),
                                false);

                        WalletTransactionWrapper walletTransactionRecordAvailable = new WalletTransactionWrapper(
                                bankMoneyTransaction.getTransactionId(),
                                bankMoneyTransaction.getFiatCurrency(),
                                BalanceType.AVAILABLE,
                                TransactionType.CREDIT,
                                MoneyType.BANK,
                                bankMoneyTransaction.getCbpWalletPublicKey(),
                                bankMoneyTransaction.getActorPublicKey(),
                                bankMoneyTransaction.getAmount(),
                                new Date().getTime() / 1000,
                                bankMoneyTransaction.getConcept(),
                                bankMoneyTransaction.getPriceReference(),
                                bankMoneyTransaction.getOriginTransaction(),
                                bankMoneyTransaction.getOriginTransactionId(),
                                false);
                        //TODO:Solo para testear
                        bankMoneyTransaction.setCbpWalletPublicKey("walletPublicKeyTest");
                        cryptoBrokerWalletManager.loadCryptoBrokerWallet(bankMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().credit(walletTransactionRecordBook, BalanceType.BOOK);
                        cryptoBrokerWalletManager.loadCryptoBrokerWallet(bankMoneyTransaction.getCbpWalletPublicKey()).getStockBalance().credit(walletTransactionRecordAvailable, BalanceType.AVAILABLE);
                        bankMoneyTransaction.setTransactionStatus(TransactionStatusRestockDestock.COMPLETED);
                        stockTransactionBankMoneyDestockFactory.saveBankMoneyDestockTransactionData(bankMoneyTransaction);
                        break;
                    }
                }
            }
        } catch (CryptoBrokerWalletNotFoundException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantGetStockCryptoBrokerWalletException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantAddDebitCryptoBrokerWalletException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (DatabaseOperationException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (InvalidParameterException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (MissingBankMoneyDestockDataException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantGetUnholdTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (CantMakeUnholdTransactionException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BANK_MONEY_DESTOCK, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
    }

    private void cleanResources() {
        /**
         * Disconnect from database and explicitly set all references to null.
         */
    }
}
