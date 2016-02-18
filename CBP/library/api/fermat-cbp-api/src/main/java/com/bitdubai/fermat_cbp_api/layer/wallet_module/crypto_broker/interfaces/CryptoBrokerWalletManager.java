package com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.world.interfaces.Currency;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankAccountType;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.exceptions.CantAddNewAccountException;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.exceptions.CantLoadBankMoneyWalletException;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.interfaces.BankAccountNumber;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractDetailType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.MoneyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStepStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.identity.ActorIdentity;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.NegotiationBankAccount;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.NegotiationLocations;
import com.bitdubai.fermat_cbp_api.layer.actor.crypto_broker.exceptions.CantCreateNewBrokerIdentityWalletRelationshipException;
import com.bitdubai.fermat_cbp_api.layer.actor.crypto_broker.exceptions.CantGetListBrokerIdentityWalletRelationshipException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.exceptions.CantAckMerchandiseException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.exceptions.CantAckPaymentException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.exceptions.CantSendPaymentException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.exceptions.CantSubmitMerchandiseException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.exceptions.CantGetListCustomerBrokerContractSaleException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.interfaces.CustomerBrokerContractSale;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.exceptions.CantCreateCryptoBrokerIdentityException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.exceptions.CantListCryptoBrokerIdentitiesException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.exceptions.CryptoBrokerIdentityAlreadyExistsException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.interfaces.CryptoBrokerIdentity;
import com.bitdubai.fermat_cbp_api.layer.middleware.matching_engine.exceptions.CantAssociatePairException;
import com.bitdubai.fermat_cbp_api.layer.middleware.matching_engine.exceptions.CantLoadEarningSettingsException;
import com.bitdubai.fermat_cbp_api.layer.middleware.matching_engine.exceptions.PairAlreadyAssociatedException;
import com.bitdubai.fermat_cbp_api.layer.middleware.matching_engine.exceptions.PairNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.middleware.matching_engine.interfaces.EarningsPair;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantCreateBankAccountSaleException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantCreateLocationSaleException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantDeleteBankAccountSaleException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantDeleteLocationSaleException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantGetListSaleNegotiationsException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantUpdateLocationSaleException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.exceptions.CantGetListClauseException;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.bank_money_destock.exceptions.CantCreateBankMoneyDestockException;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.bank_money_restock.exceptions.CantCreateBankMoneyRestockException;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.cash_money_destock.exceptions.CantCreateCashMoneyDestockException;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.cash_money_restock.exceptions.CantCreateCashMoneyRestockException;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.crypto_money_destock.exceptions.CantCreateCryptoMoneyDestockException;
import com.bitdubai.fermat_cbp_api.layer.stock_transactions.crypto_money_restock.exceptions.CantCreateCryptoMoneyRestockException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetAvailableBalanceCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerMarketRateException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerStockTransactionException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetCryptoBrokerWalletSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetStockCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantSaveCryptoBrokerWalletSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CryptoBrokerWalletNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransaction;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.FiatIndex;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletAssociatedSetting;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletProviderSetting;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.setting.CryptoBrokerWalletSettingSpread;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetAssociatedIdentity;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantNewEmptyBankAccountException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantNewEmptyCryptoBrokerWalletAssociatedSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantNewEmptyCryptoBrokerWalletProviderSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantNewEmptyCryptoBrokerWalletSettingException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantNewEmptyNegotiationBankAccountException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.CustomerBrokerNegotiationInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.IndexInfoSummary;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.NegotiationStep;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.WalletManager;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.exceptions.CantGetCryptoBrokerIdentityListException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.exceptions.CantGetProvidersCurrentExchangeRatesException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantLoadWalletException;
import com.bitdubai.fermat_cer_api.all_definition.interfaces.CurrencyPair;
import com.bitdubai.fermat_cer_api.all_definition.interfaces.ExchangeRate;
import com.bitdubai.fermat_cer_api.layer.provider.exceptions.CantGetExchangeRateException;
import com.bitdubai.fermat_cer_api.layer.provider.exceptions.UnsupportedCurrencyPairException;
import com.bitdubai.fermat_cer_api.layer.provider.interfaces.CurrencyExchangeRateProviderManager;
import com.bitdubai.fermat_cer_api.layer.search.exceptions.CantGetProviderException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantCreateCashMoneyWalletException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletBalanceException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantGetCashMoneyWalletCurrencyException;
import com.bitdubai.fermat_csh_api.layer.csh_wallet.exceptions.CantLoadCashMoneyWalletException;
import com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_manager.exceptions.CantListWalletsException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nelson
 * on 22/09/15.
 */
public interface CryptoBrokerWalletManager extends WalletManager {
    //TODO: Documentar

    /**
     * associate an Identity to this wallet
     *
     * @param brokerPublicKey the Public Key of the Crypto Broker who is going to be associated with this wallet
     */
    boolean associateIdentity(ActorIdentity brokerPublicKey, String brokerWalletPublicKey) throws CantCreateNewBrokerIdentityWalletRelationshipException;

    /**
     * Return a list of exchange rates for the desired provider and currency pair, from the current date to the desired number of days back
     *
     * @param indexInfo    object with the necessary info: the currency pair, the provider ID
     * @param numberOfDays the number of days the list will cover
     * @return The list of Exchange Rates
     * @throws CantGetProviderException         Cant get the provider from the CER platform
     * @throws UnsupportedCurrencyPairException The Currency pair fot the selected provider is not supported
     * @throws CantGetExchangeRateException     Cant get current the exchange rate for the currency pair in the provider
     */
    Collection<ExchangeRate> getDailyExchangeRatesFromCurrentDate(IndexInfoSummary indexInfo, int numberOfDays) throws CantGetProviderException, UnsupportedCurrencyPairException, CantGetExchangeRateException;

    /**
     * @param brokerWalletPublicKey the wallet public key
     * @return A summary of the current market rate for the different selected providers
     * @throws CantGetProvidersCurrentExchangeRatesException Cant get current Index Summary for the selected providers
     * @throws CryptoBrokerWalletNotFoundException           Cant find the installed wallet data
     * @throws CantGetCryptoBrokerWalletSettingException     Cant find the settings for the wallet with the public key
     * @throws CantGetProviderException                      Cant get the provider from the CER platform
     * @throws UnsupportedCurrencyPairException              The Currency pair fot the selected provider is not supported
     * @throws CantGetExchangeRateException                  Cant get current the exchange rate for the currency pair in the provider
     * @throws InvalidParameterException                     Invalid parameters
     */
    Collection<IndexInfoSummary> getProvidersCurrentExchangeRates(String brokerWalletPublicKey) throws CantGetProvidersCurrentExchangeRatesException, CryptoBrokerWalletNotFoundException, CantGetCryptoBrokerWalletSettingException, CantGetProviderException, UnsupportedCurrencyPairException, CantGetExchangeRateException, InvalidParameterException;

    boolean haveAssociatedIdentity(String walletPublicKey) throws CantListCryptoBrokerIdentitiesException, CantGetListBrokerIdentityWalletRelationshipException;

    CryptoBrokerIdentity getAssociatedIdentity(String walletPublicKey) throws CantListCryptoBrokerIdentitiesException, CantGetListBrokerIdentityWalletRelationshipException, CantGetAssociatedIdentity;

    /**
     * @return list of identities associated with this wallet
     */
    List<CryptoBrokerIdentity> getListOfIdentities() throws CantGetCryptoBrokerIdentityListException, CantListCryptoBrokerIdentitiesException;

    List<String> getPaymentMethods(String currencyToSell, String brokerWalletPublicKey) throws CryptoBrokerWalletNotFoundException, CantGetCryptoBrokerWalletSettingException;

    List<NegotiationStep> getSteps(CustomerBrokerNegotiationInformation negotiationInfo);

    void modifyNegotiationStepValues(NegotiationStep step, NegotiationStepStatus status, String... newValues);

    boolean isNothingLeftToConfirm(List<NegotiationStep> dataSet);

    CustomerBrokerNegotiationInformation sendNegotiationSteps(CustomerBrokerNegotiationInformation data, List<NegotiationStep> dataSet);

    /**
     * This method list all wallet installed in device, start the transaction
     */
    List<com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_manager.interfaces.InstalledWallet> getInstallWallets() throws CantListWalletsException;

    CryptoBrokerWalletSettingSpread newEmptyCryptoBrokerWalletSetting() throws CantNewEmptyCryptoBrokerWalletSettingException;

    CryptoBrokerWalletAssociatedSetting newEmptyCryptoBrokerWalletAssociatedSetting() throws CantNewEmptyCryptoBrokerWalletAssociatedSettingException;

    CryptoBrokerWalletProviderSetting newEmptyCryptoBrokerWalletProviderSetting() throws CantNewEmptyCryptoBrokerWalletProviderSettingException;

    NegotiationBankAccount newEmptyNegotiationBankAccount(UUID bankAccountId, String bankAccount, FiatCurrency fiatCurrency) throws CantNewEmptyNegotiationBankAccountException;

    BankAccountNumber newEmptyBankAccountNumber(String bankName, BankAccountType bankAccountType, String alias, String account, FiatCurrency currencyType) throws CantNewEmptyBankAccountException;

    void addNewAccount(BankAccountNumber bankAccountNumber, String walletPublicKey) throws CantAddNewAccountException, CantLoadBankMoneyWalletException;

    /**
     * Returns an object which allows getting and modifying (credit/debit) the Book Balance
     *
     * @return A CashMoneyWalletBalance object
     */
    void createCashMoneyWallet(String walletPublicKey, FiatCurrency fiatCurrency) throws CantCreateCashMoneyWalletException;

    /**
     * Through the method <code>createCryptoBrokerIdentity</code> you can create a new crypto broker identity.
     *
     * @param alias the alias of the crypto broker that we want to create.
     * @param image an image that represents the crypto broker. it will be shown to other actors when they try to connect.
     * @return an instance of the recent created crypto broker identity.
     * @throws CantCreateCryptoBrokerIdentityException if something goes wrong.
     */
    CryptoBrokerIdentity createCryptoBrokerIdentity(final String alias,
                                                    final byte[] image) throws CantCreateCryptoBrokerIdentityException,
            CryptoBrokerIdentityAlreadyExistsException;

    void saveWalletSetting(CryptoBrokerWalletSettingSpread cryptoBrokerWalletSettingSpread, String publicKeyWalletCryptoBrokerInstall) throws CantSaveCryptoBrokerWalletSettingException, CryptoBrokerWalletNotFoundException, CantGetCryptoBrokerWalletSettingException;

    void saveWalletSettingAssociated(CryptoBrokerWalletAssociatedSetting cryptoBrokerWalletAssociatedSetting, String publicKeyWalletCryptoBrokerInstall) throws CantGetCryptoBrokerWalletSettingException, CryptoBrokerWalletNotFoundException, CantSaveCryptoBrokerWalletSettingException;

    boolean isWalletConfigured(String publicKeyWalletCryptoBrokerInstall) throws CryptoBrokerWalletNotFoundException, CantGetCryptoBrokerWalletSettingException;

    /**
     * @param location
     * @param uri
     * @throws CantCreateLocationSaleException
     */
    void createNewLocation(String location, String uri) throws CantCreateLocationSaleException;

    /**
     * @param location
     * @throws CantUpdateLocationSaleException
     */
    void updateLocation(NegotiationLocations location) throws CantUpdateLocationSaleException;

    /**
     * @param location
     * @throws CantDeleteLocationSaleException
     */
    void deleteLocation(NegotiationLocations location) throws CantDeleteLocationSaleException;

    List<BankAccountNumber> getAccounts(String walletPublicKey) throws CantLoadBankMoneyWalletException;

    /**
     * Returns an object which allows getting and modifying (credit/debit) the Book Balance
     *
     * @return A FiatCurrency object
     */
    FiatCurrency getCashCurrency(String walletPublicKey) throws CantGetCashMoneyWalletCurrencyException, CantLoadCashMoneyWalletException;

    /**
     * Method that create the transaction Restock
     *
     * @param publicKeyActor
     * @param fiatCurrency
     * @param cbpWalletPublicKey
     * @param cbpWalletPublicKey
     * @param bankWalletPublicKey
     * @param bankAccount
     * @param amount
     * @param memo
     * @param priceReference
     * @param originTransaction
     * @throws CantCreateBankMoneyRestockException
     */

    void createTransactionRestockBank(
            String publicKeyActor,
            FiatCurrency fiatCurrency,
            String cbpWalletPublicKey,
            String bankWalletPublicKey,
            String bankAccount,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction,
            String originTransactionId
    ) throws com.bitdubai.fermat_cbp_api.layer.stock_transactions.bank_money_restock.exceptions.CantCreateBankMoneyRestockException;

    /**
     * Method that create the transaction Destock
     *
     * @param publicKeyActor
     * @param fiatCurrency
     * @param cbpWalletPublicKey
     * @param cbpWalletPublicKey
     * @param bankWalletPublicKey
     * @param bankAccount
     * @param amount
     * @param memo
     * @param priceReference
     * @param originTransaction
     * @throws CantCreateBankMoneyDestockException
     */
    void createTransactionDestockBank(
            String publicKeyActor,
            FiatCurrency fiatCurrency,
            String cbpWalletPublicKey,
            String bankWalletPublicKey,
            String bankAccount,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction,
            String originTransactionId
    ) throws CantCreateBankMoneyDestockException;

    /**
     * Method that create the transaction Destock
     *
     * @param publicKeyActor
     * @param fiatCurrency
     * @param cbpWalletPublicKey
     * @param cshWalletPublicKey
     * @param cashReference
     * @param amount
     * @param memo
     * @param priceReference
     * @param originTransaction
     * @throws CantCreateCashMoneyRestockException
     */
    void createTransactionRestockCash(
            String publicKeyActor,
            FiatCurrency fiatCurrency,
            String cbpWalletPublicKey,
            String cshWalletPublicKey,
            String cashReference,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction,
            String originTransactionId
    ) throws com.bitdubai.fermat_cbp_api.layer.stock_transactions.cash_money_restock.exceptions.CantCreateCashMoneyRestockException;

    /**
     * Method that create the transaction Destock
     *
     * @param publicKeyActor
     * @param fiatCurrency
     * @param cbpWalletPublicKey
     * @param cshWalletPublicKey
     * @param cashReference
     * @param amount
     * @param memo
     * @param priceReference
     * @param originTransaction
     * @throws CantCreateCashMoneyDestockException
     */
    void createTransactionDestockCash(
            String publicKeyActor,
            FiatCurrency fiatCurrency,
            String cbpWalletPublicKey,
            String cshWalletPublicKey,
            String cashReference,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction,
            String originTransactionId
    ) throws CantCreateCashMoneyDestockException;

    /**
     * Method that create the transaction Destock
     *
     * @param publicKeyActor
     * @param cryptoCurrency
     * @param cbpWalletPublicKey
     * @param cryWalletPublicKey
     * @param amount
     * @param memo
     * @param priceReference
     * @param originTransaction
     * @throws CantCreateCryptoMoneyRestockException
     */
    void createTransactionRestockCrypto(
            String publicKeyActor,
            CryptoCurrency cryptoCurrency,
            String cbpWalletPublicKey,
            String cryWalletPublicKey,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction,
            String originTransactionId
    ) throws CantCreateCryptoMoneyRestockException;


    /**
     * Method that create the transaction Destock
     *
     * @param publicKeyActor
     * @param cryptoCurrency
     * @param cbpWalletPublicKey
     * @param cryWalletPublicKey
     * @param amount
     * @param memo
     * @param priceReference
     * @param originTransaction
     * @throws CantCreateCryptoMoneyDestockException
     */
    void createTransactionDestockCrypto(
            String publicKeyActor,
            CryptoCurrency cryptoCurrency,
            String cbpWalletPublicKey,
            String cryWalletPublicKey,
            BigDecimal amount,
            String memo,
            BigDecimal priceReference,
            OriginTransaction originTransaction,
            String originTransactionId,
            BlockchainNetworkType blockchainNetworkType
    ) throws CantCreateCryptoMoneyDestockException;

    /**
     * This method load the list CryptoBrokerStockTransaction
     *
     * @param merchandise
     * @param fiatCurrency
     * @param moneyType
     * @return FiatIndex
     * @throws CantGetCryptoBrokerMarketRateException
     */
    FiatIndex getMarketRate(Currency merchandise, FiatCurrency fiatCurrency, MoneyType moneyType, String walletPublicKey) throws CantGetCryptoBrokerMarketRateException, CryptoBrokerWalletNotFoundException;

    CustomerBrokerNegotiationInformation setMemo(String memo, CustomerBrokerNegotiationInformation data);

    /**
     * @param ContractId
     * @return a CustomerBrokerContractSale with information of contract with ContractId
     * @throws CantGetListCustomerBrokerContractSaleException
     */
    CustomerBrokerContractSale getCustomerBrokerContractSaleForContractId(final String ContractId) throws CantGetListCustomerBrokerContractSaleException;

    /**
     * This method load the list CryptoBrokerWalletProviderSetting
     *
     * @param
     * @return List<CryptoBrokerWalletProviderSetting>
     * @throws CantGetCryptoBrokerWalletSettingException
     */
    List<CryptoBrokerWalletProviderSetting> getCryptoBrokerWalletProviderSettings(String walletPublicKey) throws CantGetCryptoBrokerWalletSettingException, CryptoBrokerWalletNotFoundException;

    /**
     * Returns a list of Providers able to obtain the  CurrencyPair for providers wallet associated
     *
     * @return a map containing both the ProviderID and the CurrencyPair for providers wallet associated
     */
    Map<String, CurrencyPair> getWalletProviderAssociatedCurrencyPairs(CurrencyPair currencyPair, String walletPublicKey) throws CryptoBrokerWalletNotFoundException, CantGetCryptoBrokerWalletSettingException;

    /**
     * This method load the list CryptoBrokerWalletProviderSetting
     *
     * @param
     * @return List<CryptoBrokerWalletAssociatedSetting>
     * @throws CantGetCryptoBrokerWalletSettingException
     */
    List<CryptoBrokerWalletAssociatedSetting> getCryptoBrokerWalletAssociatedSettings(String walletPublicKey) throws CantGetCryptoBrokerWalletSettingException, CryptoBrokerWalletNotFoundException;

    /**
     * This method load the instance saveCryptoBrokerWalletSpreadSetting
     *
     * @param
     * @return CryptoBrokerWalletSettingSpread
     * @throws CantSaveCryptoBrokerWalletSettingException
     */
    CryptoBrokerWalletSettingSpread getCryptoBrokerWalletSpreadSetting(String walletPublicKey) throws CantGetCryptoBrokerWalletSettingException, CryptoBrokerWalletNotFoundException;

    /**
     * Returns an exchange rate of a given date, for a specific currencyPair
     *
     * @return an exchangeRate object
     */
    ExchangeRate getExchangeRateFromDate(Currency currencyFrom, Currency currencyTo, long timestamp, UUID providerId) throws UnsupportedCurrencyPairException, CantGetExchangeRateException, CantGetProviderException;

    /**
     * Given a start and end timestamp and a currencyPair, returns a list of DAILY ExchangeRates
     *
     * @return a list of exchangeRate objects
     */
    Collection<ExchangeRate> getDailyExchangeRatesForPeriod(Currency currencyFrom, Currency currencyTo, long startTimestamp, long endTimestamp, UUID providerId) throws UnsupportedCurrencyPairException, CantGetExchangeRateException, CantGetProviderException;

    /**
     * This method load the list CryptoBrokerStockTransaction
     *
     * @param merchandise
     * @param moneyType
     * @param offset
     * @param timeStamp
     * @return List<CryptoBrokerStockTransaction>
     * @throws CantGetCryptoBrokerStockTransactionException
     */
    List<CryptoBrokerStockTransaction> getStockHistory(Currency merchandise, MoneyType moneyType, int offset, long timeStamp, String walletPublicKey) throws CantGetCryptoBrokerStockTransactionException;

    float getAvailableBalance(Currency merchandise, String walletPublicKey) throws CantGetAvailableBalanceCryptoBrokerWalletException, CryptoBrokerWalletNotFoundException, CantGetStockCryptoBrokerWalletException;

    /**
     * Returns a list of provider references which can obtain the ExchangeRate of the given CurrencyPair
     *
     * @param currencyFrom
     * @param currencyTo
     * @return a Collection of provider reference pairs
     */
    Collection<CurrencyExchangeRateProviderManager> getProviderReferencesFromCurrencyPair(Currency currencyFrom, Currency currencyTo) throws CantGetProviderException;

    /**
     * This method save the instance CryptoBrokerWalletProviderSetting
     *
     * @param cryptoBrokerWalletProviderSetting
     * @return
     * @throws CantSaveCryptoBrokerWalletSettingException
     */
    void saveCryptoBrokerWalletProviderSetting(CryptoBrokerWalletProviderSetting cryptoBrokerWalletProviderSetting, String walletPublicKey) throws CantSaveCryptoBrokerWalletSettingException, CryptoBrokerWalletNotFoundException, CantGetCryptoBrokerWalletSettingException;

    /**
     * @param bankAccount
     * @throws CantCreateBankAccountSaleException
     */
    void createNewBankAccount(NegotiationBankAccount bankAccount) throws CantCreateBankAccountSaleException;

    /**
     * @param bankAccount
     * @throws CantDeleteBankAccountSaleException
     */
    void deleteBankAccount(NegotiationBankAccount bankAccount) throws CantDeleteBankAccountSaleException;

    /**
     * Returns the Balance this BankMoneyWalletBalance belongs to. (Can be available or book)
     *
     * @return A double, containing the balance.
     */
    double getBalanceBankWallet(String walletPublicKey, String accountNumber) throws CantCalculateBalanceException, CantLoadBankMoneyWalletException;

    /**
     * Returns the Balance this CashMoneyWalletBalance belongs to. (Can be available or book)
     *
     * @return A BigDecimal, containing the balance.
     */
    BigDecimal getBalanceCashWallet(String walletPublicKey) throws CantGetCashMoneyWalletBalanceException, CantLoadCashMoneyWalletException;

    /**
     * Checks if wallet exists in wallet database
     */
    boolean cashMoneyWalletExists(String walletPublicKey);

    /**
     * Returns the Balance this BitcoinWalletBalance belongs to. (Can be available or book)
     *
     * @return A BigDecimal, containing the balance.
     */
    public long getBalanceBitcoinWallet(String walletPublicKey) throws com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantCalculateBalanceException, CantLoadWalletException;

    /**
     * This method returns the CustomerBrokerContractSale associated to a negotiationId
     *
     * @param negotiationId
     * @return
     * @throws CantGetListCustomerBrokerContractSaleException
     */
    CustomerBrokerContractSale getCustomerBrokerContractSaleByNegotiationId(
            String negotiationId) throws CantGetListCustomerBrokerContractSaleException;

    /**
     * This method returns the currency type from a contract
     *
     * @param customerBrokerContractSale
     * @param contractDetailType
     * @return
     * @throws CantGetListSaleNegotiationsException
     */
    MoneyType getMoneyTypeFromContract(
            CustomerBrokerContractSale customerBrokerContractSale,
            ContractDetailType contractDetailType) throws
            CantGetListSaleNegotiationsException;

    /**
     * This method returns the ContractStatus by contractHash/Id
     *
     * @param contractHash
     * @return
     * @throws CantGetListCustomerBrokerContractSaleException
     */
    ContractStatus getContractStatus(String contractHash) throws
            CantGetListCustomerBrokerContractSaleException;

    /**
     * This method send a merchandise according the contract elements.
     *
     * @param contractHash
     */
    void submitMerchandise(String contractHash) throws CantSubmitMerchandiseException;

    /**
     * This method execute a Broker Ack payment Business Transaction
     *
     * @param contractHash
     * @throws CantAckPaymentException
     */
    ContractStatus ackPayment(String contractHash) throws CantAckPaymentException;

    /**
     * Through the method <code>associatePair</code> you can associate an earnings pair to the wallet in which we're working.
     * Automatically will be generated an UUID to the earnings pair, with it, we can identify the same.
     *
     * @param earningCurrency        the currency which we decided to extract the earnings.
     * @param linkedCurrency         the currency that we're linking to the previous selected currency to conform the pair.
     * @param earningWalletPublicKey the wallet's public key that we're associating and where we will deposit the earnings.
     * @param brokerWalletPublicKey  the broker wallet's public key who we're associating this earning setting
     * @return an instance of the well formed associated EarningPair-
     * @throws CantAssociatePairException     if something goes wrong.
     * @throws PairAlreadyAssociatedException if the pair is already associated.
     * @throws CantLoadEarningSettingsException if something goes wrong trying to get the earning settings.
     */
    EarningsPair addEarningsPairToEarningSettings(Currency earningCurrency, Currency linkedCurrency, String earningWalletPublicKey, String brokerWalletPublicKey) throws CantLoadEarningSettingsException, CantAssociatePairException, PairAlreadyAssociatedException;
}
