package com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces;

import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStepStatus;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.interfaces.CryptoBrokerIdentity;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.CustomerBrokerNegotiationInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.IndexInfoSummary;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.NegotiationStep;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.WalletManager;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.exceptions.CantGetCryptoBrokerIdentityListException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.exceptions.CantGetCurrentIndexSummaryForStockCurrenciesException;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by nelson on 22/09/15.
 */
public interface CryptoBrokerWalletManager extends WalletManager {

    /**
     * associate an Identity to this wallet
     *
     * @param brokerId the Crypto Broker ID who is going to be associated with this wallet
     */
    boolean associateIdentity(UUID brokerId);

    /**
     * @return a summary of the current market rate for the different currencies the broker have as stock
     */
    Collection<IndexInfoSummary> getCurrentIndexSummaryForStockCurrencies() throws CantGetCurrentIndexSummaryForStockCurrenciesException;

    /**
     * Get information about the current stock
     *
     * @param stockCurrency the stock currency
     * @return information about the current stock
     */
    StockInformation getCurrentStock(String stockCurrency);

    /**
     * @return list of identities associated with this wallet
     */
    List<CryptoBrokerIdentity> getListOfIdentities() throws CantGetCryptoBrokerIdentityListException;

    /**
     * Get stock staticstics data about the given stock currency
     *
     * @param stockCurrency the stock currency
     * @return stock statistics data
     */
    StockStatistics getStockStatistics(String stockCurrency);

    List<String> getBrokerLocations();

    List<String> getBrokerBankAccounts();

    List<String> getPaymentMethods(String currencyToSell);

    List<NegotiationStep> getSteps(CustomerBrokerNegotiationInformation negotiationInfo);

    void modifyNegotiationStepValues(NegotiationStep step, NegotiationStepStatus status, String... newValues);

    boolean isNothingLeftToConfirm(List<NegotiationStep> dataSet);

    CustomerBrokerNegotiationInformation sendNegotiationSteps(CustomerBrokerNegotiationInformation data, List<NegotiationStep> dataSet);
}
