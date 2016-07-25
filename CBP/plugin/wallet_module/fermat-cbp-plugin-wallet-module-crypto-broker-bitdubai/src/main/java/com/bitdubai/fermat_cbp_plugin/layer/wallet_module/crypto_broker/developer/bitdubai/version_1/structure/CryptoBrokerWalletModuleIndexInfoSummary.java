package com.bitdubai.fermat_cbp_plugin.layer.wallet_module.crypto_broker.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.world.interfaces.Currency;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.IndexInfoSummary;
import com.bitdubai.fermat_cer_api.all_definition.interfaces.ExchangeRate;
import com.bitdubai.fermat_cer_api.layer.provider.exceptions.CantGetProviderInfoException;
import com.bitdubai.fermat_cer_api.layer.provider.interfaces.CurrencyExchangeRateProviderManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;


/**
 * Created by nelson on 14/11/15.
 */
public class CryptoBrokerWalletModuleIndexInfoSummary implements IndexInfoSummary {
    private String currencyAndReferenceCurrency;
    private String salePriceAndCurrency;
    private String purchasePriceAndCurrency;
    private String providerName;
    private UUID providerId;
    private ExchangeRate exchangeRateData;

    public CryptoBrokerWalletModuleIndexInfoSummary(ExchangeRate exchangeRate, CurrencyExchangeRateProviderManager provider) {
        try {
            providerName = provider.getProviderName();
        } catch (CantGetProviderInfoException e) {
            providerName = "Unknown Provider";
        }

        try {
            providerId = provider.getProviderId();
        } catch (CantGetProviderInfoException e) {
            providerId = null;
        }

        this.exchangeRateData = exchangeRate;

        Currency toCurrency = exchangeRate.getToCurrency();
        currencyAndReferenceCurrency = new StringBuilder().append(exchangeRate.getFromCurrency().getCode()).append(" / ").append(toCurrency.getCode()).toString();

        NumberFormat numberFormat = DecimalFormat.getInstance();
        numberFormat.setMaximumFractionDigits(8);
        purchasePriceAndCurrency = new StringBuilder().append(toCurrency.getCode()).append(" ").append(numberFormat.format(exchangeRate.getPurchasePrice())).toString();

        numberFormat = DecimalFormat.getInstance();
        numberFormat.setMaximumFractionDigits(8);
        salePriceAndCurrency = new StringBuilder().append(toCurrency.getCode()).append(" ").append(numberFormat.format(exchangeRate.getSalePrice())).toString();
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public ExchangeRate getExchangeRateData() {
        return exchangeRateData;
    }

    @Override
    public String getCurrencyAndReferenceCurrency() {
        return currencyAndReferenceCurrency;
    }

    @Override
    public String getSalePriceAndCurrency() {
        return salePriceAndCurrency;
    }

    @Override
    public String getPurchasePriceAndCurrency() {
        return purchasePriceAndCurrency;
    }

    @Override
    public UUID getProviderId() {
        return providerId;
    }
}
