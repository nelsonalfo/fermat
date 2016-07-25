package com.bitdubai.fermat_csh_plugin.layer.wallet_module.cash_money.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.modules.ModuleManagerImpl;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_csh_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_csh_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_csh_api.all_definition.exceptions.CashMoneyWalletInsufficientFundsException;
import com.bitdubai.fermat_csh_api.all_definition.interfaces.CashTransactionParameters;
import com.bitdubai.fermat_csh_api.all_definition.interfaces.CashWalletBalances;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.deposit.exceptions.CantCreateDepositTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.deposit.interfaces.CashDepositTransaction;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.deposit.interfaces.CashDepositTransactionManager;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.withdrawal.exceptions.CantCreateWithdrawalTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.withdrawal.interfaces.CashWithdrawalTransaction;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.withdrawal.interfaces.CashWithdrawalTransactionManager;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantCreateCashMoneyWalletException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletBalanceException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletCurrencyException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletTransactionsException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantLoadCashMoneyWalletException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.interfaces.CashMoneyWallet;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.interfaces.CashMoneyWalletManager;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.interfaces.CashMoneyWalletTransaction;
import com.bitdubai.fermat_csh_api.layer.csh_wallet_module.CashMoneyWalletPreferenceSettings;
import com.bitdubai.fermat_csh_api.layer.csh_wallet_module.exceptions.CantGetCashMoneyWalletBalancesException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet_module.interfaces.CashMoneyWalletModuleManager;
import com.bitdubai.fermat_csh_plugin.layer.wallet_module.cash_money.developer.bitdubai.version_1.CashMoneyWalletModulePluginRoot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Alex on 21/1/2016.
 */
public class CashMoneyWalletModuleManagerImpl extends ModuleManagerImpl<CashMoneyWalletPreferenceSettings>
        implements CashMoneyWalletModuleManager, Serializable {

    private final CashMoneyWalletModulePluginRoot pluginRoot;
    private final CashMoneyWalletManager cashMoneyWalletManager;
    private final CashDepositTransactionManager cashDepositTransactionManager;
    private final CashWithdrawalTransactionManager cashWithdrawalTransactionManager;
    private CashWalletAsyncTransactionAgent agent;



    public CashMoneyWalletModuleManagerImpl(final CashMoneyWalletManager cashMoneyWalletManager, final UUID pluginId, final PluginFileSystem pluginFileSystem,
                                           final CashMoneyWalletModulePluginRoot pluginRoot, final CashDepositTransactionManager cashDepositTransactionManager,
                                            final CashWithdrawalTransactionManager cashWithdrawalTransactionManager, final Broadcaster broadcaster) {
        super(pluginFileSystem, pluginId);
        
        this.pluginRoot = pluginRoot;
        this.cashMoneyWalletManager = cashMoneyWalletManager;
        this.cashDepositTransactionManager = cashDepositTransactionManager;
        this.cashWithdrawalTransactionManager = cashWithdrawalTransactionManager;

        agent = new CashWalletAsyncTransactionAgent(broadcaster, this);
    }


    /*
     * CashMoneyWalletModuleManager implementation
     */
    @Override
    public CashWalletBalances getWalletBalances(String walletPublicKey) throws CantGetCashMoneyWalletBalancesException {
        CashMoneyWallet wallet;
        BigDecimal availableBalance, bookBalance;

        try{
            wallet = cashMoneyWalletManager.loadCashMoneyWallet(walletPublicKey);
        } catch (CantLoadCashMoneyWalletException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletBalancesException(CantGetCashMoneyWalletBalancesException.DEFAULT_MESSAGE, e, "CashMoneyWalletModulePluginRoot", "Cannot load cash money wallet");
        }

        try {
            availableBalance = wallet.getAvailableBalance().getBalance();
            bookBalance = wallet.getBookBalance().getBalance();
        } catch(CantGetCashMoneyWalletBalanceException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletBalancesException(CantGetCashMoneyWalletBalancesException.DEFAULT_MESSAGE, e, "CashMoneyWalletModulePluginRoot", "Cannot get cash money wallet balances");

        }

        return new CashWalletBalancesImpl(availableBalance, bookBalance);
    }

    @Override
    public FiatCurrency getWalletCurrency(String walletPublicKey) throws CantGetCashMoneyWalletCurrencyException {
        CashMoneyWallet wallet;
        try{
            wallet = cashMoneyWalletManager.loadCashMoneyWallet(walletPublicKey);
        } catch (CantLoadCashMoneyWalletException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletCurrencyException(CantGetCashMoneyWalletCurrencyException.DEFAULT_MESSAGE, e, "CashMoneyWalletModulePluginRoot", "Cannot load cash money wallet");
        }

        try {
            return wallet.getCurrency();
        } catch(CantGetCashMoneyWalletCurrencyException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletCurrencyException(CantGetCashMoneyWalletCurrencyException.DEFAULT_MESSAGE, e, "CashMoneyWalletModulePluginRoot", "Cannot get cash money wallet currency");

        }
    }

    @Override
    public void createAsyncCashTransaction(CashTransactionParameters depositParameters) {
        agent.queueNewTransaction(depositParameters);
    }

    @Override
    public void cancelAsyncCashTransaction(CashMoneyWalletTransaction t)  throws Exception {
        CashTransactionParameters tp = new CashTransactionParametersImpl(t.getTransactionId(), t.getPublicKeyWallet(), t.getPublicKeyActor(), t.getPublicKeyPlugin(), t.getAmount(), t.getCurrency(), t.getMemo(), t.getTransactionType());
        agent.cancelTransaction(tp);
    }

    @Override
    public CashDepositTransaction doCreateCashDepositTransaction(CashTransactionParameters depositParameters) throws CantCreateDepositTransactionException {
        return cashDepositTransactionManager.createCashDepositTransaction(depositParameters);
    }

    @Override
    public CashWithdrawalTransaction doCreateCashWithdrawalTransaction(CashTransactionParameters withdrawalParameters) throws CantCreateWithdrawalTransactionException, CashMoneyWalletInsufficientFundsException {
        return cashWithdrawalTransactionManager.createCashWithdrawalTransaction(withdrawalParameters);
    }

    @Override
    public List<CashMoneyWalletTransaction> getPendingTransactions() {

        List<CashMoneyWalletTransaction> transactionList = new ArrayList<>();

        for(Map.Entry<Long, CashTransactionParameters> transaction : agent.getQueuedTransactions().entrySet()) {
            CashTransactionParameters tp = transaction.getValue();
            transactionList.add(new CashMoneyWalletTransactionImpl(tp.getTransactionId(), tp.getPublicKeyWallet(), tp.getPublicKeyActor(), tp.getPublicKeyPlugin(),
                    tp.getTransactionType(), null, tp.getAmount(), tp.getMemo(), transaction.getKey(), true));
        }
        Collections.reverse(transactionList);
        return transactionList;
    }

    @Override
    public List<CashMoneyWalletTransaction> getTransactions(String walletPublicKey, List<TransactionType> transactionTypes, List<BalanceType> balanceTypes,  int max, int offset) throws CantGetCashMoneyWalletTransactionsException {
        CashMoneyWallet wallet;
        try{
            wallet = cashMoneyWalletManager.loadCashMoneyWallet(walletPublicKey);
        } catch (CantLoadCashMoneyWalletException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletTransactionsException(CantGetCashMoneyWalletTransactionsException.DEFAULT_MESSAGE, e, "CashMoneyWalletModulePluginRoot", "Cannot load cash money wallet");
        }

        try {
            return wallet.getTransactions(transactionTypes, balanceTypes, max, offset);
        } catch(CantGetCashMoneyWalletTransactionsException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletTransactionsException(CantGetCashMoneyWalletTransactionsException.DEFAULT_MESSAGE, e, "CashMoneyWalletModulePluginRoot", "Cannot get cash money wallet currency");

        }
    }

    @Override
    public CashMoneyWalletTransaction getTransaction(String walletPublicKey, UUID transactionId) throws CantGetCashMoneyWalletTransactionsException {
        CashMoneyWallet wallet;
        try{
            wallet = cashMoneyWalletManager.loadCashMoneyWallet(walletPublicKey);
        } catch (CantLoadCashMoneyWalletException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantGetCashMoneyWalletTransactionsException(CantGetCashMoneyWalletTransactionsException.DEFAULT_MESSAGE, e, "CashMoneyWalletModulePluginRoot", "Cannot load cash money wallet");
        }

        return wallet.getTransaction(transactionId);

    }

    @Override
    public boolean cashMoneyWalletExists(String walletPublicKey) {
        return cashMoneyWalletManager.cashMoneyWalletExists(walletPublicKey);
    }

    @Override
    public void createCashMoneyWallet(String walletPublicKey, FiatCurrency fiatCurrency) throws CantCreateCashMoneyWalletException {
        cashMoneyWalletManager.createCashMoneyWallet(walletPublicKey, fiatCurrency);
    }


//    private SettingsManager<CashMoneyWalletPreferenceSettings> settingsManager;

//    @Override
//    public SettingsManager<CashMoneyWalletPreferenceSettings> getSettingsManager() {
//        if (this.settingsManager != null)
//            return this.settingsManager;
//
//        this.settingsManager = new SettingsManager<>(
//                pluginFileSystem,
//                pluginId
//        );
//
//        return this.settingsManager;    }

    @Override
    public ActiveActorIdentityInformation getSelectedActorIdentity() throws CantGetSelectedActorIdentityException {
        return null;
    }

    @Override
    public void createIdentity(String name, String phrase, byte[] profile_img) throws Exception {

    }

    @Override
    public void setAppPublicKey(String publicKey) {

    }

    @Override
    public int[] getMenuNotifications() {
        return new int[0];
    }
}
