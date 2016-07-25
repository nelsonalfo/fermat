package com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_purchase.developer.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.AbstractAgent;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.WalletsPublicKeys;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.Owner;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.BroadcasterType;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.FermatBundle;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.world.interfaces.Currency;
import com.bitdubai.fermat_cbp_api.all_definition.constants.CBPBroadcasterConstants;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStatus;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.CantGetUTCException;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.UnexpectedResultReturnedFromDatabaseException;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.Clause;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.Negotiation;
import com.bitdubai.fermat_cbp_api.all_definition.util.DateTimeZone;
import com.bitdubai.fermat_cbp_api.all_definition.util.NegotiationClauseHelper;
import com.bitdubai.fermat_cbp_api.all_definition.util.UniversalTime;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.close_contract.exceptions.CantCloseContractException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.close_contract.interfaces.CloseContractManager;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.open_contract.exceptions.CantOpenContractException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.open_contract.interfaces.OpenContractManager;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.exceptions.CantGetListCustomerBrokerContractPurchaseException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.exceptions.CantUpdateCustomerBrokerContractPurchaseException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.interfaces.CustomerBrokerContractPurchase;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_purchase.interfaces.CustomerBrokerContractPurchaseManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.exceptions.CantGetListPurchaseNegotiationsException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_purchase.interfaces.CustomerBrokerPurchaseNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.exceptions.CantGetListClauseException;
import com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus;
import com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.customer_broker_purchase.interfaces.CustomerBrokerPurchase;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWallet;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletProviderSetting;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletSetting;
import com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_purchase.developer.bitdubai.version_1.database.UserLevelBusinessTransactionCustomerBrokerPurchaseConstants;
import com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_purchase.developer.bitdubai.version_1.database.UserLevelBusinessTransactionCustomerBrokerPurchaseDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_purchase.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_purchase.developer.bitdubai.version_1.exceptions.MissingCustomerBrokerPurchaseDataException;
import com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_purchase.developer.bitdubai.version_1.utils.CustomerBrokerPurchaseImpl;
import com.bitdubai.fermat_cer_api.all_definition.interfaces.CurrencyPair;
import com.bitdubai.fermat_cer_api.all_definition.interfaces.ExchangeRate;
import com.bitdubai.fermat_cer_api.all_definition.utils.CurrencyPairImpl;
import com.bitdubai.fermat_cer_api.layer.provider.exceptions.CantGetExchangeRateException;
import com.bitdubai.fermat_cer_api.layer.provider.interfaces.CurrencyExchangeRateProviderManager;
import com.bitdubai.fermat_cer_api.layer.search.interfaces.CurrencyExchangeProviderFilterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.bitdubai.fermat_api.layer.osa_android.broadcaster.NotificationBundleConstants.APP_ACTIVITY_TO_OPEN_CODE;
import static com.bitdubai.fermat_api.layer.osa_android.broadcaster.NotificationBundleConstants.APP_NOTIFICATION_PAINTER_FROM;
import static com.bitdubai.fermat_api.layer.osa_android.broadcaster.NotificationBundleConstants.APP_TO_OPEN_PUBLIC_KEY;
import static com.bitdubai.fermat_api.layer.osa_android.broadcaster.NotificationBundleConstants.NOTIFICATION_ID;
import static com.bitdubai.fermat_api.layer.osa_android.broadcaster.NotificationBundleConstants.SOURCE_PLUGIN;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.CANCELLED;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.COMPLETED;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.IN_CONTRACT_SUBMIT;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.IN_MERCHANDISE_SUBMIT;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.IN_OPEN_CONTRACT;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.IN_PAYMENT_SUBMIT;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.IN_PENDING_MERCHANDISE;
import static com.bitdubai.fermat_cbp_api.layer.user_level_business_transaction.common.enums.TransactionStatus.IN_PROCESS;


/**
 * The Class <code>UserLevelBusinessTransactionCustomerBrokerPurchaseMonitorAgent</code>
 * contains the logic for handling agent transactional
 * Created by franklin on 02/07/16
 */
public class UserLevelBusinessTransactionCustomerBrokerPurchaseMonitorAgent2 extends AbstractAgent {
    private final AbstractPlugin pluginRoot;
    private final CustomerBrokerPurchaseNegotiationManager purchaseNegotiationManager;
    private final UserLevelBusinessTransactionCustomerBrokerPurchaseDatabaseDao dao;
    private final OpenContractManager openContractManager;
    private final CloseContractManager closeContractManager;
    private final CustomerBrokerContractPurchaseManager contractPurchaseManager;
    private final CurrencyExchangeProviderFilterManager currencyExchangeRateProviderFilter;
    private final CryptoBrokerWalletManager cryptoBrokerWalletManager;
    private final Broadcaster broadcaster;

    public final int DELAY_HOURS = 2;
    public final int TIME_BETWEEN_NOTIFICATIONS = 600000; //10min
    private long lastNotificationTime = 0;

    public UserLevelBusinessTransactionCustomerBrokerPurchaseMonitorAgent2(long sleepTime,
                                                                           TimeUnit timeUnit,
                                                                           long initDelayTime,
                                                                           AbstractPlugin pluginRoot,
                                                                           CustomerBrokerPurchaseNegotiationManager customerBrokerPurchaseNegotiationManager,
                                                                           PluginDatabaseSystem pluginDatabaseSystem,
                                                                           UUID pluginId,
                                                                           OpenContractManager openContractManager,
                                                                           CloseContractManager closeContractManager,
                                                                           CustomerBrokerContractPurchaseManager customerBrokerContractPurchaseManager,
                                                                           CurrencyExchangeProviderFilterManager currencyExchangeRateProviderFilter,
                                                                           CryptoBrokerWalletManager cryptoBrokerWalletManager,
                                                                           Broadcaster broadcaster) {

        super(sleepTime, timeUnit, initDelayTime);
        this.pluginRoot = pluginRoot;
        this.purchaseNegotiationManager = customerBrokerPurchaseNegotiationManager;
        this.openContractManager = openContractManager;
        this.closeContractManager = closeContractManager;
        this.contractPurchaseManager = customerBrokerContractPurchaseManager;
        this.currencyExchangeRateProviderFilter = currencyExchangeRateProviderFilter;
        this.cryptoBrokerWalletManager = cryptoBrokerWalletManager;
        this.broadcaster = broadcaster;

        this.dao = new UserLevelBusinessTransactionCustomerBrokerPurchaseDatabaseDao(pluginDatabaseSystem, pluginId);

    }

    @Override
    protected Runnable agentJob() {
        return new Runnable() {
            @Override
            public void run() {
                doTheMainTask();
            }
        };
    }

    @Override
    protected void onErrorOccur() {
        pluginRoot.reportError(
                UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,
                new Exception("UserLevelBusinessTransactionCustomerBrokerPurchaseMonitorAgent2 Error"));
    }


    /**
     * Contain the main task of this agent, which is to check the contract status and make
     * the correspondent updates and raise the correspondent notifications
     */
    private void doTheMainTask() {
        try {
            final String transactionStatusColumnName = UserLevelBusinessTransactionCustomerBrokerPurchaseConstants.
                    CUSTOMER_BROKER_PURCHASE_TRANSACTION_STATUS_COLUMN_NAME;

            // NegotiationStatus.CLOSED -> TransactionStatus.IN_PROCESS
            takeCloseNegotiationsAndCreateTransactionInProgress();

            // IN_PROCESS -> IN_OPEN_CONTRACT
            changeTransactionStatusFromInProcessToInOpenContract(transactionStatusColumnName);

            // IN_OPEN_CONTRACT -> IN_CONTRACT_SUBMIT
            changeTransactionStatusFromInOpenContractToInContractSubmitted();

            // IN_CONTRACT_SUBMIT -> Update Contract Expiration Time and notify
            updateContractExpirationDateWhitStatusInContractSubmitAndNotify();

            // IN_CONTRACT_SUBMIT -> Update Contract Status to CANCELLED for expiration time in payment submit
            changeTransactionStatusFromInContractSubmitToCancelledIfExpirationTimeReached();

            // IN_CONTRACT_SUBMIT -> IN_PAYMENT_SUBMIT
            changeTransactionStatusFromInContractSubmitToInPaymentSubmit();

            // IN_PAYMENT_SUBMIT -> Update Contract Status to CANCELLED for expiration time in merchandise
            changeTransactionStatusFromInPaymentSubmitToCancelledIfExpirationTimeReached();

            // IN_PAYMENT_SUBMIT -> IN_PENDING_MERCHANDISE
            changeTransactionStatusFromInPaymentSubmitToInPendingMerchandise();

            // IN_PENDING_MERCHANDISE -> Update Contract Expiration Time and notify
            updateContractExpirationDateWhitInPendingMerchandiseStatusAndNotify();

            // IN_PENDING_MERCHANDISE -> IN_MERCHANDISE_SUBMIT
            changeTransactionStatusFromInPendingMerchandiseToInMerchandiseSubmitted();

            // IN_MERCHANDISE_SUBMIT -> COMPLETED
            changeTransactionStatusFromInMerchandiseSubmitToCompleted();

        } catch (Exception e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }

    }

    /* Private methods */

    /**
     * ContractStatus.READY_TO_CLOSE -> TransactionStatus.COMPLETED
     * <p/>
     * Se debe enviar un Broadcast para actualizar la UI
     */
    private void changeTransactionStatusFromInMerchandiseSubmitToCompleted() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, CantCloseContractException, MissingCustomerBrokerPurchaseDataException {
        final List<CustomerBrokerPurchase> userLevelTransactions = dao.getCustomerBrokerPurchases(null);

        final Collection<CustomerBrokerContractPurchase> readyToCloseContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.READY_TO_CLOSE);

        for (CustomerBrokerContractPurchase contract : readyToCloseContracts) {
            for (CustomerBrokerPurchase userLevelTransaction : userLevelTransactions) {
                String transactionId = userLevelTransaction.getTransactionId();
                String negotiationId = contract.getNegotiatiotId();
                TransactionStatus transactionStatus = userLevelTransaction.getTransactionStatus();

                if (transactionId.equals(negotiationId) && transactionStatus != COMPLETED) {
                    closeContractManager.closePurchaseContract(contract.getContractId());

                    userLevelTransaction.setTransactionStatus(COMPLETED);
                    userLevelTransaction.setContractStatus(ContractStatus.READY_TO_CLOSE.getCode());

                    dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                    FermatBundle fermatBundle = new FermatBundle();
                    fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                    broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);

                    fermatBundle = new FermatBundle();
                    fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                    fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                    fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_CONTRACT_COMPLETED_NOTIFICATION);
                    fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_CONTRACTS_HISTORY.getCode());
                    broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);
                }
            }
        }
    }

    /**
     * ContractStatus.MERCHANDISE_SUBMIT -> TransactionStatus.IN_MERCHANDISE_SUBMIT
     * <p/>
     * Se confirma que se recibio la mercancia
     * <p/>
     * Se debe enviar un Broadcast para actualizar la UI
     */
    private void changeTransactionStatusFromInPendingMerchandiseToInMerchandiseSubmitted() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, MissingCustomerBrokerPurchaseDataException {
        final List<CustomerBrokerPurchase> userLevelTransactions = dao.getCustomerBrokerPurchases(null);

        final Collection<CustomerBrokerContractPurchase> merchandiseSubmittedContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.MERCHANDISE_SUBMIT);

        for (CustomerBrokerContractPurchase contract : merchandiseSubmittedContracts) {
            for (CustomerBrokerPurchase userLevelTransaction : userLevelTransactions) {
                String transactionId = userLevelTransaction.getTransactionId();
                String negotiationId = contract.getNegotiatiotId();
                TransactionStatus transactionStatus = userLevelTransaction.getTransactionStatus();

                if (transactionId.equals(negotiationId) && transactionStatus != IN_MERCHANDISE_SUBMIT) {
                    userLevelTransaction.setTransactionStatus(IN_MERCHANDISE_SUBMIT);
                    userLevelTransaction.setContractStatus(ContractStatus.MERCHANDISE_SUBMIT.getCode());

                    dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                    FermatBundle fermatBundle = new FermatBundle();
                    fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                    broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);

                    fermatBundle = new FermatBundle();
                    fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                    fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                    fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_CONTRACT_BROKER_SUBMITED_MERCHANDISE);
                    fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_HOME.getCode());
                    broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);
                }
            }
        }
    }

    /**
     * ContractStatus.PENDING_MERCHANDISE -> Update Contract Expiration Time and notify:
     * <p/>
     * Si se acerca la tiempo límite para recibir la mercadería y esta no ha sido registrada como recibida,
     * Se notifica si la confirmacion de la recepcion de la mercancia del contrato esta proxima a expirar
     */
    private void updateContractExpirationDateWhitInPendingMerchandiseStatusAndNotify() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, CantUpdateCustomerBrokerContractPurchaseException {

        final Collection<CustomerBrokerContractPurchase> pendingMerchandiseContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.PENDING_MERCHANDISE);

        for (CustomerBrokerContractPurchase contract : pendingMerchandiseContracts) {

            long timeStampToday = ((contract.getDateTime() - getDateTimeUTC()) / 3600000);

//            System.out.print("\n *** TIME ZONE NEGOTIATION: " + contract.getNegotiatiotId() + " ***" +
//                    "\n - Compare alert: " + contract.getDateTime()+" - "+getDateTimeUTC()+"\n" +
//                    "\n - timeStampToday: "+timeStampToday+" <= "+DELAY_HOURS);

            if (timeStampToday <= DELAY_HOURS) {
                contractPurchaseManager.updateContractNearExpirationDatetime(contract.getContractId(), true);

                if (new Date().getTime() - lastNotificationTime > TIME_BETWEEN_NOTIFICATIONS) {
                    lastNotificationTime = new Date().getTime();

                    FermatBundle fermatBundle = new FermatBundle();
                    fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                    fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                    fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_CONTRACT_EXPIRATION_NOTIFICATION);
                    fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_HOME.getCode());
                    broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);

                    fermatBundle = new FermatBundle();
                    fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                    broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);
                }
            }
        }
    }

    /**
     * ContractStatus.PENDING_MERCHANDISE -> TransactionStatus.IN_PENDING_MERCHANDISE
     * <p/>
     * Se debe enviar un Broadcast para actualizar la UI
     */
    private void changeTransactionStatusFromInPaymentSubmitToInPendingMerchandise() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, MissingCustomerBrokerPurchaseDataException {
        final List<CustomerBrokerPurchase> userLevelTransactions = dao.getCustomerBrokerPurchases(null);

        final Collection<CustomerBrokerContractPurchase> pendingMerchandiseContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.PENDING_MERCHANDISE);

        for (CustomerBrokerContractPurchase contract : pendingMerchandiseContracts) {
            for (CustomerBrokerPurchase userLevelTransaction : userLevelTransactions) {
                final String transactionId = userLevelTransaction.getTransactionId();
                final String negotiationId = contract.getNegotiatiotId();
                final TransactionStatus transactionStatus = userLevelTransaction.getTransactionStatus();

                if (transactionId.equals(negotiationId) && transactionStatus != IN_PENDING_MERCHANDISE) {
                    userLevelTransaction.setTransactionStatus(IN_PENDING_MERCHANDISE);
                    userLevelTransaction.setContractStatus(ContractStatus.PENDING_MERCHANDISE.getCode());

                    dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                    FermatBundle fermatBundle = new FermatBundle();
                    fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                    broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);

                    fermatBundle = new FermatBundle();
                    fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                    fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                    fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_CONTRACT_BROKER_ACK_PAYMENT_NOTIFICATION);
                    fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_HOME.getCode());
                    broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);
                }
            }
        }
    }

    /**
     * ContractStatus.PAYMENT_SUBMIT -> TransactionStatus.IN_PAYMENT_SUBMIT
     * <p/>
     * Se verifica el estatus del contrato hasta que se consiga la realización de un pago
     * <p/>
     * Se debe enviar un Broadcast para actualizar la UI
     */
    private void changeTransactionStatusFromInContractSubmitToInPaymentSubmit() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, MissingCustomerBrokerPurchaseDataException {
        final List<CustomerBrokerPurchase> userLevelTransactions = dao.getCustomerBrokerPurchases(null);

        final Collection<CustomerBrokerContractPurchase> paymentSubmittedContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.PAYMENT_SUBMIT);

        for (CustomerBrokerContractPurchase contract : paymentSubmittedContracts) {
            for (CustomerBrokerPurchase userLevelTransaction : userLevelTransactions) {
                String negotiationId = contract.getNegotiatiotId();
                String transactionId = userLevelTransaction.getTransactionId();
                TransactionStatus transactionStatus = userLevelTransaction.getTransactionStatus();

                if (transactionId.equals(negotiationId) && transactionStatus != IN_PAYMENT_SUBMIT) {
                    userLevelTransaction.setTransactionStatus(IN_PAYMENT_SUBMIT);
                    userLevelTransaction.setContractStatus(ContractStatus.PAYMENT_SUBMIT.getCode());
                    dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                    FermatBundle fermatBundle = new FermatBundle();
                    fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                    broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);
                }
            }
        }
    }

    /**
     * ContractStatus.PENDING_MERCHANDISE -> TransactionStatus.CANCELLED
     * <p/>
     * Update Contract Status to CANCELLED for expiration time in merchandise.
     * If Expiration Time is done, Update the contract status to CANCELLED.
     */
    private void changeTransactionStatusFromInPaymentSubmitToCancelledIfExpirationTimeReached() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, CantGetListPurchaseNegotiationsException, CantGetListClauseException, CantUpdateCustomerBrokerContractPurchaseException, MissingCustomerBrokerPurchaseDataException {
        final List<CustomerBrokerPurchase> userLevelTransactions = dao.getCustomerBrokerPurchases(null);

        final Collection<CustomerBrokerContractPurchase> pendingMerchandiseContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.PENDING_MERCHANDISE);

        for (CustomerBrokerContractPurchase contract : pendingMerchandiseContracts) {
            for (CustomerBrokerPurchase userLevelTransaction : userLevelTransactions) {
                String negotiationId = contract.getNegotiatiotId();
                String transactionId = userLevelTransaction.getTransactionId();
                TransactionStatus transactionStatus = userLevelTransaction.getTransactionStatus();

                if (transactionId.equals(negotiationId) && transactionStatus != CANCELLED) {

                    long timeToDelivery = 0;
//                    long timeStampToday = new Date().getTime();
                    Negotiation negotiation = purchaseNegotiationManager.getNegotiationsByNegotiationId(UUID.fromString(negotiationId));
                    Collection<Clause> clauses = negotiation.getClauses();
                    String clauseValue = NegotiationClauseHelper.getNegotiationClauseValue(clauses, ClauseType.BROKER_DATE_TIME_TO_DELIVER);

                    if (clauseValue != null)
                        timeToDelivery = Long.parseLong(clauseValue);

                    DateTimeZone dateTimeZoneDelivery = new DateTimeZone(TimeZone.getDefault().getID(), timeToDelivery, "MM/dd/yyyy hh:mm a");
                    String dateDelivery = dateTimeZoneDelivery.getDate();

                    DateTimeZone dateTimeZoneToday = new DateTimeZone(TimeZone.getDefault().getID(), getDateTimeUTC(), "MM/dd/yyyy hh:mm a");
                    String dateToday = dateTimeZoneToday.getDate(TimeZone.getTimeZone("UTC"));

//                    System.out.print("\n *** TIME ZONE NEGOTIATION: " + negotiationId + " ***" +
//                            "\n - " + dateDelivery + " compareTo " + dateToday);

                    if (dateDelivery.compareTo(dateToday) == 0) {
//                    System.out.print("\n *** TIME ZONE NEGOTIATION: " + negotiationId + " ***" +getDateTimeUTC()+" >= "+timeToDelivery);
//                    if (getDateTimeUTC() >= timeToDelivery) {

                        //UPDATE CONTRACT STATUS
                        contractPurchaseManager.cancelContract(contract.getContractId(),
                                "CANCELLATION CONTRACT BY EXPIRATION IN DATE OF SUBMIT MERCHANDISE.");

                        //UPDATE STATUS USER LEVEL BUSINESS TRANSACTION
                        userLevelTransaction.setTransactionStatus(CANCELLED);
                        userLevelTransaction.setContractStatus(ContractStatus.CANCELLED.getCode());
                        dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                        //BROADCASTER
                        FermatBundle fermatBundle = new FermatBundle();
                        fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                        fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                        fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                        fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_CONTRACT_CANCELLED_NOTIFICATION);
                        fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_CONTRACTS_HISTORY.getCode());
                        broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);

                        fermatBundle = new FermatBundle();
                        fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                        fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                        broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);

                    }
                }
            }
        }
    }

    /**
     * ContractStatus.PENDING_PAYMENT -> TransactionStatus.CANCELLED
     * <p/>
     * Update Contract Status to CANCELLED for expiration time in payment submit.
     * If Expiration Time is done, Update the contract status to CANCELLED.
     */
    private void changeTransactionStatusFromInContractSubmitToCancelledIfExpirationTimeReached() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, CantGetListPurchaseNegotiationsException, CantGetListClauseException, CantUpdateCustomerBrokerContractPurchaseException, MissingCustomerBrokerPurchaseDataException {
        final List<CustomerBrokerPurchase> userLevelTransactions = dao.getCustomerBrokerPurchases(null);

        final Collection<CustomerBrokerContractPurchase> pendingPaymentContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.PENDING_PAYMENT);

//        long timeToDeliveryTest = 1467235072;
//        DateTimeZone dateTimeZoneDelivery = new DateTimeZone(TimeZone.getDefault().getID(),timeToDeliveryTest,"MM/dd/yyyy hh:mm a");
//        String dateTimeDelivery     = dateTimeZoneDelivery.getDate();
//        String dateTimeDeliveryUTC  = dateTimeZoneDelivery.getDateUTC();
//        String dateTimeToday        = dateTimeZoneDelivery.getDateTodayUTC();
//
//        System.out.print("\n *** TIME ZONE " +
//                "\n - Date:" +dateTimeDelivery+
//                "\n - Date UTC:" +dateTimeDeliveryUTC+
//                "\n - DateToday: "+dateTimeToday+
//                "\n - CompareTo: " + dateTimeDelivery.compareTo(dateTimeToday)+"\n");

//        System.out.print("\n *** USER LEVEL changeTransactionStatusFromInContractSubmitToCancelledIfExpirationTimeReached *** \n");
        for (CustomerBrokerContractPurchase contract : pendingPaymentContracts) {
            for (CustomerBrokerPurchase userLevelTransaction : userLevelTransactions) {
                String negotiationId = contract.getNegotiatiotId();
                String transactionId = userLevelTransaction.getTransactionId();
                TransactionStatus transactionStatus = userLevelTransaction.getTransactionStatus();

                if (transactionId.equals(negotiationId) && transactionStatus != CANCELLED) {

                    long timeToDelivery = 0;
//                    long timeStampToday = new Date().getTime();

                    Negotiation negotiation = purchaseNegotiationManager.getNegotiationsByNegotiationId(UUID.fromString(negotiationId));
                    Collection<Clause> clauses = negotiation.getClauses();
                    String clauseValue = NegotiationClauseHelper.getNegotiationClauseValue(clauses, ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER);

                    if (clauseValue != null)
                        timeToDelivery = Long.parseLong(clauseValue);


                    DateTimeZone dateTimeZoneDelivery = new DateTimeZone(TimeZone.getDefault().getID(), timeToDelivery, "MM/dd/yyyy hh:mm a");
                    String dateDelivery = dateTimeZoneDelivery.getDate();

                    DateTimeZone dateTimeZoneToday = new DateTimeZone(TimeZone.getDefault().getID(), getDateTimeUTC(), "MM/dd/yyyy hh:mm a");
                    String dateToday = dateTimeZoneToday.getDate(TimeZone.getTimeZone("UTC"));

//                    System.out.print("\n *** TIME ZONE NEGOTIATION: " + negotiationId + " ***" +
//                            "\n - " +dateDelivery+" compareTo "+dateToday);

                    if (dateDelivery.compareTo(dateToday) == 0) {
//                    System.out.print("\n *** TIME ZONE NEGOTIATION: " + negotiationId + " ***" +getDateTimeUTC()+" >= "+timeToDelivery);
//                    if (getDateTimeUTC() >= timeToDelivery) {
//                    if(dateTimeDeliveryUTC.compareTo(dateTimeToday) == 0){

                        contractPurchaseManager.cancelContract(contract.getContractId(),
                                "CANCELLATION CONTRACT BY EXPIRATION IN DATE OF SUBMIT PAYMENT.");

                        userLevelTransaction.setTransactionStatus(CANCELLED);
                        userLevelTransaction.setContractStatus(ContractStatus.CANCELLED.getCode());
                        dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                        //BROADCASTER
                        FermatBundle fermatBundle = new FermatBundle();
                        fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                        fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                        fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                        fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_CONTRACT_CANCELLED_NOTIFICATION);
                        fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_CONTRACTS_HISTORY.getCode());
                        broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);

                        fermatBundle = new FermatBundle();
                        fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                        fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                        broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);
                    }
                }
            }
        }
    }

    /**
     * ContractStatus.PENDING_PAYMENT -> Update Contract Expiration Time and notify:
     * <p/>
     * Si la fecha del contracto se acerca al dia y 2 horas antes de vencerse debo de elevar un evento de notificacion
     * siempre y cuando el ContractStatus sea igual a PENDING_PAYMENT
     */
    private void updateContractExpirationDateWhitStatusInContractSubmitAndNotify() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, CantUpdateCustomerBrokerContractPurchaseException {

        final Collection<CustomerBrokerContractPurchase> pendingPaymentContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.PENDING_PAYMENT);

        for (CustomerBrokerContractPurchase contract : pendingPaymentContracts) {

//            long timeStampToday = ((contract.getDateTime() - new Date().getTime()) / 3600000);
            long timeStampToday = ((contract.getDateTime() - getDateTimeUTC()) / 3600000);
//            System.out.print("\n *** TIME ZONE NEGOTIATION: " + contract.getNegotiatiotId() + " ***" +
//                    "\n - Compare alert: " + contract.getDateTime()+" - "+getDateTimeUTC()+"\n" +
//                    "\n - timeStampToday: "+timeStampToday+" <= "+DELAY_HOURS);

            if (timeStampToday <= DELAY_HOURS) {
                contractPurchaseManager.updateContractNearExpirationDatetime(contract.getContractId(), true);

                if (new Date().getTime() - lastNotificationTime > TIME_BETWEEN_NOTIFICATIONS) {
                    lastNotificationTime = new Date().getTime();

                    FermatBundle fermatBundle = new FermatBundle();
                    fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                    fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                    fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_CONTRACT_EXPIRATION_NOTIFICATION);
                    fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_HOME.getCode());
                    broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);

                    fermatBundle = new FermatBundle();
                    fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                    broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);

                }
            }
        }
    }

    /**
     * ContractStatus.PENDING_PAYMENT -> TransactionStatus.IN_CONTRACT_SUBMIT
     * <p/>
     * Se debe enviar un Broadcast para actualizar la UI
     */
    private void changeTransactionStatusFromInOpenContractToInContractSubmitted() throws DatabaseOperationException, InvalidParameterException, CantGetListCustomerBrokerContractPurchaseException, MissingCustomerBrokerPurchaseDataException {
        final List<CustomerBrokerPurchase> userLevelTransactions = dao.getCustomerBrokerPurchases(null);

        final Collection<CustomerBrokerContractPurchase> pendingPaymentContracts = contractPurchaseManager.
                getCustomerBrokerContractPurchaseForStatus(ContractStatus.PENDING_PAYMENT);

        for (CustomerBrokerContractPurchase contract : pendingPaymentContracts) {
            for (CustomerBrokerPurchase userLevelTransaction : userLevelTransactions) {
                String transactionId = userLevelTransaction.getTransactionId();
                String negotiationId = contract.getNegotiatiotId();
                TransactionStatus transactionStatus = userLevelTransaction.getTransactionStatus();

                if (transactionId.equals(negotiationId) && transactionStatus != IN_CONTRACT_SUBMIT) {
                    userLevelTransaction.setTransactionStatus(IN_CONTRACT_SUBMIT);
                    userLevelTransaction.setContractStatus(ContractStatus.PENDING_PAYMENT.getCode());
                    dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                    FermatBundle fermatBundle = new FermatBundle();
                    fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                    fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                    broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);
                }
            }
        }
    }

    /**
     * IN_PROCESS -> IN_OPEN_CONTRACT:
     * <p/>
     * Registra el Open Contract siempre y cuando el Transaction Status de la CustomerBrokerSale este IN_PROCESS
     * Se obtiene el customerCurrency de la negociacion para obtener el marketExchangeRate de ese currency vs. USD
     * <p/>
     * Se envia un Broadcast para actualizar la UI y enviar una notificacion
     */
    private void changeTransactionStatusFromInProcessToInOpenContract(String transactionStatusColumnName) throws DatabaseOperationException, InvalidParameterException, CantGetListPurchaseNegotiationsException, CantGetListClauseException, CantOpenContractException, MissingCustomerBrokerPurchaseDataException, UnexpectedResultReturnedFromDatabaseException {
        final DatabaseTableFilter tableFilter = getFilterTable(IN_PROCESS.getCode(), transactionStatusColumnName);

        final List<CustomerBrokerPurchase> inProgressTransactions = dao.getCustomerBrokerPurchases(tableFilter);

        for (CustomerBrokerPurchase userLevelTransaction : inProgressTransactions) {
            UUID transactionId = UUID.fromString(userLevelTransaction.getTransactionId());
            CustomerBrokerPurchaseNegotiation transactionInfo = purchaseNegotiationManager.getNegotiationsByNegotiationId(transactionId);

            //Registra el Open Contract siempre y cuando el Transaction_Status de la Transaction Customer Broker Purchase este IN_PROCESS
            //Find the negotiation's customerCurrency, to find the marketExchangeRate of that currency vs. USD

            Collection<Clause> clauses = null;
            try {
                clauses = transactionInfo.getClauses();
            } catch (CantGetListClauseException e) {
                e.printStackTrace();
            }
//            final Collection<Clause> clauses = transactionInfo.getClauses();
            final String customerCurrency = NegotiationClauseHelper.getNegotiationClauseValue(clauses, ClauseType.CUSTOMER_CURRENCY);

            float marketExchangeRate = 1;
            if (customerCurrency != null) {
                try {
                    marketExchangeRate = getMarketExchangeRate(customerCurrency);
                } catch (CantGetExchangeRateException e) {
                    marketExchangeRate = 1;
                }
            }

            boolean isContract = openContractManager.isOpenContract(transactionInfo.getNegotiationId().toString());
            if (transactionInfo.getStatus().equals(NegotiationStatus.WAITING_FOR_CLOSING) && (!isContract)) {

                openContractManager.openPurchaseContract(transactionInfo, marketExchangeRate);

                //Actualiza el Transaction_Status de la Transaction Customer Broker Purchase a IN_OPEN_CONTRACT
                userLevelTransaction.setTransactionStatus(IN_OPEN_CONTRACT);
                dao.saveCustomerBrokerPurchaseTransactionData(userLevelTransaction);

                /*FermatBundle fermatBundle = new FermatBundle();
                fermatBundle.put(SOURCE_PLUGIN, Plugins.CUSTOMER_BROKER_PURCHASE.getCode());
                fermatBundle.put(APP_NOTIFICATION_PAINTER_FROM, new Owner(WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode()));
                fermatBundle.put(APP_TO_OPEN_PUBLIC_KEY, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                fermatBundle.put(NOTIFICATION_ID, CBPBroadcasterConstants.CCW_NEW_CONTRACT_NOTIFICATION);
                fermatBundle.put(APP_ACTIVITY_TO_OPEN_CODE, Activities.CBP_CRYPTO_CUSTOMER_WALLET_HOME.getCode());
                broadcaster.publish(BroadcasterType.NOTIFICATION_SERVICE, fermatBundle);

<<<<<<< HEAD
                broadcaster.publish(BroadcasterType.UPDATE_VIEW, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);*/
/*=======
                fermatBundle = new FermatBundle();
                fermatBundle.put(Broadcaster.PUBLISH_ID, WalletsPublicKeys.CBP_CRYPTO_CUSTOMER_WALLET.getCode());
                fermatBundle.put(Broadcaster.NOTIFICATION_TYPE, CBPBroadcasterConstants.CCW_CONTRACT_UPDATE_VIEW);
                broadcaster.publish(BroadcasterType.UPDATE_VIEW, fermatBundle);
>>>>>>> 0b77e4ac982dacacaff5541307b362f1affcafad*/
            }
        }
    }

    /**
     * NegotiationStatus.CLOSED -> TransactionStatus.IN_PROCESS
     * <p/>
     * Verifica que la transaccion no se encuentre ya registrada para agregarla a la BD
     */
    private void takeCloseNegotiationsAndCreateTransactionInProgress() throws CantGetListPurchaseNegotiationsException, DatabaseOperationException, InvalidParameterException, MissingCustomerBrokerPurchaseDataException {
        Collection<CustomerBrokerPurchaseNegotiation> closedNegotiations = purchaseNegotiationManager.
                getNegotiationsByStatus(NegotiationStatus.WAITING_FOR_CLOSING);

        for (CustomerBrokerPurchaseNegotiation negotiation : closedNegotiations) {
            UUID negotiationId = negotiation.getNegotiationId();

            if (isNegotiationNoRegisteredInUserLevelDatabase(negotiationId)) {
                CustomerBrokerPurchaseImpl customerBrokerPurchase = new CustomerBrokerPurchaseImpl(
                        negotiationId.toString(),
                        negotiationId.toString(),
                        0,
                        null,
                        null,
                        IN_PROCESS,
                        null,
                        null,
                        null);

                dao.saveCustomerBrokerPurchaseTransactionData(customerBrokerPurchase);
            }
        }
    }

    /**
     * check if the negotiation is registered in the database of this plugin
     *
     * @param negotiationId the negotiation ID
     * @return <code>true</code> if the negotiation is registerd. <code>false</code> otherwise
     * @throws DatabaseOperationException
     * @throws InvalidParameterException
     */
    private boolean isNegotiationNoRegisteredInUserLevelDatabase(UUID negotiationId) throws DatabaseOperationException, InvalidParameterException {
        final DatabaseTableFilter tableFilter = getFilterTable(
                negotiationId.toString(),
                UserLevelBusinessTransactionCustomerBrokerPurchaseConstants.CUSTOMER_BROKER_PURCHASE_TRANSACTION_ID_COLUMN_NAME);

        final List<CustomerBrokerPurchase> transactions = dao.getCustomerBrokerPurchases(tableFilter);

        return transactions.isEmpty();
    }

    /**
     * Return a database filter object filled with the given parameters
     *
     * @param valueFilter the value of the filter
     * @param columnValue the column to filter
     * @return the database filter object
     */
    private DatabaseTableFilter getFilterTable(final String valueFilter, final String columnValue) {
        // I define the filter to search for the public Key
        return new DatabaseTableFilter() {
            @Override
            public void setColumn(String column) {

            }

            @Override
            public void setType(DatabaseFilterType type) {

            }

            @Override
            public void setValue(String value) {

            }

            @Override
            public String getColumn() {
                return columnValue;
            }

            @Override
            public String getValue() {
                return valueFilter;
            }

            @Override
            public DatabaseFilterType getType() {
                return DatabaseFilterType.EQUAL;
            }
        };
    }

    /**
     * Return the market exchange rate for the given currency
     *
     * @param customerCurrency the currency
     * @return the market rate
     * @throws CantGetExchangeRateException
     */
    private float getMarketExchangeRate(String customerCurrency) throws CantGetExchangeRateException {
        //Find out if customerCurrency parameter is a FiatCurrency or a CryptoCurrency
        Currency currency = null;
        try {
            if (FiatCurrency.codeExists(customerCurrency))
                currency = FiatCurrency.getByCode(customerCurrency);
            else if (CryptoCurrency.codeExists(customerCurrency))
                currency = CryptoCurrency.getByCode(customerCurrency);
        } catch (Exception e) {
            throw new CantGetExchangeRateException();
        }

        if (currency == null)
            throw new CantGetExchangeRateException();


        CurrencyPair currencyPair = new CurrencyPairImpl(currency, FiatCurrency.US_DOLLAR);


        //Get saved CER providers in broker wallet
        final String publicKeyWalletCryptoBrokerInstall = "walletPublicKeyTest"; //TODO: Quitar este hardcode luego que se implemente la instalacion de la wallet

        try {
            final CryptoBrokerWallet cryptoBrokerWallet = cryptoBrokerWalletManager.loadCryptoBrokerWallet(publicKeyWalletCryptoBrokerInstall);
            final CryptoBrokerWalletSetting cryptoWalletSetting = cryptoBrokerWallet.getCryptoWalletSetting();
            final List<CryptoBrokerWalletProviderSetting> providerSettings = cryptoWalletSetting.getCryptoBrokerWalletProviderSettings();

            for (CryptoBrokerWalletProviderSetting providerSetting : providerSettings) {

                UUID providerId = providerSetting.getPlugin();
                CurrencyExchangeRateProviderManager providerReference = currencyExchangeRateProviderFilter.getProviderReference(providerId);
                if (providerReference.isCurrencyPairSupported(currencyPair)) {
                    ExchangeRate currentExchangeRate = providerReference.getCurrentExchangeRate(currencyPair);
                    return (float) currentExchangeRate.getPurchasePrice();
                }
            }
        } catch (Exception e) { /*Continue*/ }

        //Find any CER provider which can obtain the needed currencyPair, regardless of it not being set up in the broker wallet
        try {
            List<CurrencyExchangeRateProviderManager> providers = new ArrayList<>();
            providers.addAll(currencyExchangeRateProviderFilter.getProviderReferencesFromCurrencyPair(currencyPair));

            CurrencyExchangeRateProviderManager providerReference = providers.get(0);
            ExchangeRate currentExchangeRate = providerReference.getCurrentExchangeRate(currencyPair);

            return (float) currentExchangeRate.getPurchasePrice();

        } catch (Exception e) { /*Continue*/ }

        //Can't do nothing more
        throw new CantGetExchangeRateException();
    }

    private long getDateTimeUTC() {

        try {

            return UniversalTime.getUTC().getTime();

        } catch (CantGetUTCException e) {
            System.out.print("Cant get Date UTC in User Level Business Transaction Customer Broker Purchase");
        }

        return 0;
    }
}
