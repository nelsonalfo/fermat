package com.bitdubai.fermat_cbp_plugin.layer.wallet_module.crypto_broker.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStepStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStepType;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.interfaces.CryptoBrokerIdentity;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetContractHistoryException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetContractsWaitingForBrokerException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetContractsWaitingForCustomerException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetNegotiationsWaitingForBrokerException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetNegotiationsWaitingForCustomerException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CouldNotCancelNegotiationException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CouldNotConfirmNegotiationException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.AmountToSellStep;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.ClauseInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.ContractBasicInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.CustomerBrokerNegotiationInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.ExchangeRateStep;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.IndexInfoSummary;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.NegotiationStep;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.SingleValueStep;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.exceptions.CantGetCryptoBrokerIdentityListException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.exceptions.CantGetCurrentIndexSummaryForStockCurrenciesException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.StockInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.StockStatistics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Module Manager of Crypto Broker Module Plugin
 *
 * @author Nelson Ramirez
 * @version 1.0
 * @since 05/11/15
 */
public class CryptoBrokerWalletModuleCryptoBrokerWalletManager implements CryptoBrokerWalletManager {
    private List<ContractBasicInformation> contractsHistory;
    private List<CustomerBrokerNegotiationInformation> openNegotiations;

    public static final String CASH_IN_HAND = "Cash on Hand";
    public static final String CASH_DELIVERY = "Cash Delivery";
    public static final String BANK_TRANSFER = "Bank Transfer";
    public static final String CRYPTO_TRANSFER = "Crypto Transfer";

    private static final List<String> BROKER_BANK_ACCOUNTS = new ArrayList<>();
    public static final String BROKER_BANK_ACCOUNT_1 = "Banco: BOD\nTipo de cuenta: Corriente\nNro: 0105-2255-2221548739\nCliente: Brokers Asociados";
    public static final String BROKER_BANK_ACCOUNT_2 = "Banco: Provincial\nTipo de cuenta: Ahorro\nNro: 0114-3154-268548735\nCliente: Brokers Asociados";
    public static final String BROKER_BANK_ACCOUNT_3 = "Banco: Banesco\nTipo de cuenta: Corriente\nNro: 1124-0235-9981548701\nCliente: Brokers Asociados";

    public static final String CUSTOMER_BANK_ACCOUNT_1 = "Banco: Venezuela\nTipo de cuenta: Ahorro\nNro: 0001-2051-2221548714\nCliente: Angel Lacret";

    private static final List<String> BROKER_LOCATIONS = new ArrayList<>();
    public static final String BROKER_LOCATION_1 = "C.C. Sambil Maracaibo, Piso 2, Local 5A, al lado de Farmatodo";
    public static final String BROKER_LOCATION_2 = "C.C. Galerias, Piso 3, Local 5A, cerca de la feria de la comida";
    public static final String BROKER_LOCATION_3 = "Av. Padilla, Residencias San Martin, Casa #4-5";

    public static final String CUSTOMER_LOCATION_1 = "Centro Empresarial Totuma, Piso 2, Local 5A";
    public static final String CUSTOMER_LOCATION_2 = "Instituto de Calculo Aplicado, LUZ Facultad de Ingenieria";

    private static final String BROKER_CRYPTO_ADDRESS = "jn384jfnqirfjqn4834232039dj";
    private static final String CUSTOMER_CRYPTO_ADDRESS = "ioajpviq3489f9r8fj208245nds";


    @Override
    public CustomerBrokerNegotiationInformation addClause(CustomerBrokerNegotiationInformation negotiation, ClauseInformation clause) {
        return null;
    }

    @Override
    public CustomerBrokerNegotiationInformation changeClause(CustomerBrokerNegotiationInformation negotiation, ClauseInformation clause) {
        return null;
    }

    @Override
    public Collection<ContractBasicInformation> getContractsHistory(ContractStatus status, int max, int offset) throws CantGetContractHistoryException {
        try {
            List<ContractBasicInformation> contractsHistory;

            contractsHistory = getContractHistoryTestData();

            if (status != null) {
                List<ContractBasicInformation> filteredList = new ArrayList<>();
                for (ContractBasicInformation item : contractsHistory) {
                    if (item.getStatus().equals(status))
                        filteredList.add(item);
                }
                contractsHistory = filteredList;
            }

            return contractsHistory;

        } catch (Exception ex) {
            throw new CantGetContractHistoryException(ex);
        }
    }

    @Override
    public Collection<ContractBasicInformation> getContractsWaitingForBroker(int max, int offset) throws CantGetContractsWaitingForBrokerException {
        try {
            ContractBasicInformation contract;
            Collection<ContractBasicInformation> waitingForBroker = new ArrayList<>();

            contract = new CryptoBrokerWalletModuleContractBasicInformation("adrianasupernova", "USD", "Crypto Transfer", "BTC", ContractStatus.PAUSED);
            waitingForBroker.add(contract);

            return waitingForBroker;

        } catch (Exception ex) {
            throw new CantGetContractsWaitingForBrokerException("Cant get contracts waiting for the broker", ex);
        }
    }

    @Override
    public Collection<ContractBasicInformation> getContractsWaitingForCustomer(int max, int offset) throws CantGetContractsWaitingForCustomerException {
        try {
            ContractBasicInformation contract;
            Collection<ContractBasicInformation> waitingForCustomer = new ArrayList<>();

            contract = new CryptoBrokerWalletModuleContractBasicInformation("yalayn", "BTC", "Bank Transfer", "USD", ContractStatus.PENDING_PAYMENT);
            waitingForCustomer.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("vzlangel", "BsF", "Cash Delivery", "BsF", ContractStatus.PENDING_PAYMENT);
            waitingForCustomer.add(contract);

            return waitingForCustomer;

        } catch (Exception ex) {
            throw new CantGetContractsWaitingForCustomerException("Cant get contracts waiting for the customers", ex);
        }


    }

    @Override
    public Collection<CustomerBrokerNegotiationInformation> getNegotiationsWaitingForBroker(int max, int offset) throws CantGetNegotiationsWaitingForBrokerException {
        try {
            List<CustomerBrokerNegotiationInformation> testData = getOpenNegotiationsTestData();
            Collection<CustomerBrokerNegotiationInformation> waitingForBroker = new ArrayList<>();

            for (CustomerBrokerNegotiationInformation item : testData) {
                if (item.getStatus().equals(NegotiationStatus.WAITING_FOR_BROKER))
                    waitingForBroker.add(item);
            }

            return waitingForBroker;

        } catch (Exception ex) {
            throw new CantGetNegotiationsWaitingForBrokerException("Cant get negotiations waiting for the broker", ex, "", "");
        }
    }

    @Override
    public Collection<CustomerBrokerNegotiationInformation> getNegotiationsWaitingForCustomer(int max, int offset) throws CantGetNegotiationsWaitingForCustomerException {
        try {
            List<CustomerBrokerNegotiationInformation> testData = getOpenNegotiationsTestData();
            Collection<CustomerBrokerNegotiationInformation> waitingForCustomer = new ArrayList<>();

            for (CustomerBrokerNegotiationInformation item : testData) {
                if (item.getStatus().equals(NegotiationStatus.WAITING_FOR_CUSTOMER))
                    waitingForCustomer.add(item);
            }

            return waitingForCustomer;

        } catch (Exception ex) {
            throw new CantGetNegotiationsWaitingForCustomerException("Cant get negotiations waiting for the customers", ex, "", "");
        }
    }

    @Override
    public boolean associateIdentity(UUID brokerId) {
        return false;
    }

    @Override
    public CustomerBrokerNegotiationInformation cancelNegotiation(CustomerBrokerNegotiationInformation negotiation, String reason) throws CouldNotCancelNegotiationException {
        return null;
    }

    @Override
    public CustomerBrokerNegotiationInformation confirmNegotiation(CustomerBrokerNegotiationInformation negotiation) throws CouldNotConfirmNegotiationException {
        return null;
    }

    @Override
    public Collection<IndexInfoSummary> getCurrentIndexSummaryForStockCurrencies() throws CantGetCurrentIndexSummaryForStockCurrenciesException {
        try {
            IndexInfoSummary indexInfoSummary;
            Collection<IndexInfoSummary> summaryList = new ArrayList<>();

            indexInfoSummary = new CryptoBrokerWalletModuleIndexInfoSummary(CryptoCurrency.BITCOIN, FiatCurrency.US_DOLLAR, 240.62, 235.87);
            summaryList.add(indexInfoSummary);
            indexInfoSummary = new CryptoBrokerWalletModuleIndexInfoSummary(FiatCurrency.VENEZUELAN_BOLIVAR, CryptoCurrency.BITCOIN, 245000, 240000);
            summaryList.add(indexInfoSummary);
            indexInfoSummary = new CryptoBrokerWalletModuleIndexInfoSummary(FiatCurrency.VENEZUELAN_BOLIVAR, FiatCurrency.US_DOLLAR, 840, 800);
            summaryList.add(indexInfoSummary);
            indexInfoSummary = new CryptoBrokerWalletModuleIndexInfoSummary(FiatCurrency.US_DOLLAR, FiatCurrency.EURO, 1.2, 1.1);
            summaryList.add(indexInfoSummary);

            return summaryList;

        } catch (Exception ex) {
            throw new CantGetCurrentIndexSummaryForStockCurrenciesException(ex);
        }
    }

    @Override
    public StockInformation getCurrentStock(String stockCurrency) {
        return null;
    }

    @Override
    public List<CryptoBrokerIdentity> getListOfIdentities() throws CantGetCryptoBrokerIdentityListException {
        return null;
    }

    @Override
    public StockStatistics getStockStatistics(String stockCurrency) {
        return null;
    }

    @Override
    public List<String> getBrokerLocations() {
        // TODO esto viene de los settings de la negociacion que guarda Angel en su plugin Customer Broker Purchase Negotiation
        if (BROKER_LOCATIONS.isEmpty()) {
            BROKER_LOCATIONS.add(BROKER_LOCATION_1);
            BROKER_LOCATIONS.add(BROKER_LOCATION_2);
            BROKER_LOCATIONS.add(BROKER_LOCATION_3);
        }

        return BROKER_LOCATIONS;
    }

    @Override
    public List<String> getBrokerBankAccounts() {
        // TODO esto viene de los settings de la negociacion que guarda Angel en su plugin Customer Broker Purchase Negotiation
        if(BROKER_BANK_ACCOUNTS.isEmpty()){
            BROKER_BANK_ACCOUNTS.add(BROKER_BANK_ACCOUNT_1);
            BROKER_BANK_ACCOUNTS.add(BROKER_BANK_ACCOUNT_2);
            BROKER_BANK_ACCOUNTS.add(BROKER_BANK_ACCOUNT_3);
        }
        return BROKER_BANK_ACCOUNTS;
    }

    @Override
    public List<String> getPaymentMethods(String currencyToSell) {
        List<String> paymentMethod = new ArrayList<>();
        CryptoCurrency currency = null;

        try {
            currency = CryptoCurrency.getByCode(currencyToSell);
        } catch (InvalidParameterException ignored) {
        }

        if (currency != null) {
            paymentMethod.add(CRYPTO_TRANSFER);
        } else {
            paymentMethod.add(BANK_TRANSFER);
            paymentMethod.add(CASH_IN_HAND);
            paymentMethod.add(CASH_DELIVERY);
        }

        return paymentMethod;
    }

    @Override
    public List<NegotiationStep> getSteps(CustomerBrokerNegotiationInformation negotiationInfo) {
        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
        List<NegotiationStep> data = new ArrayList<>();
        int stepNumber = 0;

        // exchange rate step
        Map<ClauseType, ClauseInformation> clauses = negotiationInfo.getClauses();
        String currencyToSell = clauses.get(ClauseType.CUSTOMER_CURRENCY).getValue();
        String currencyToReceive = clauses.get(ClauseType.BROKER_CURRENCY).getValue();
        String exchangeRate = clauses.get(ClauseType.EXCHANGE_RATE).getValue();
        String suggestedExchangeRate = decimalFormat.format(215.25); // TODO este valor me lo da la wallet
        data.add(new ExchangeRateStepImp(++stepNumber, currencyToSell, currencyToReceive, suggestedExchangeRate, exchangeRate));

        // amount to sell step
        String amountToSell = clauses.get(ClauseType.CUSTOMER_CURRENCY_QUANTITY).getValue();
        String amountToReceive = clauses.get(ClauseType.BROKER_CURRENCY_QUANTITY).getValue();
        data.add(new AmountToSellStepImp(++stepNumber, currencyToSell, currencyToReceive, amountToSell, amountToReceive, exchangeRate));

        // Payment Method
        String paymentMethod = clauses.get(ClauseType.CUSTOMER_PAYMENT_METHOD).getValue();
        data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.PAYMENT_METHOD, paymentMethod));

        // Broker Bank Account
        ClauseInformation clauseInformation = clauses.get(ClauseType.BROKER_BANK_ACCOUNT);
        if (clauseInformation != null) {
            String brokerBankAccount = clauseInformation.getValue();
            data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.BROKER_BANK_ACCOUNT, brokerBankAccount));
        }

        // Broker Locations
        clauseInformation = clauses.get(ClauseType.BROKER_PLACE_TO_DELIVER);
        if (clauseInformation != null) {
            String brokerBankAccount = clauseInformation.getValue();
            data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.BROKER_LOCATION, brokerBankAccount));
        }

        // Customer Bank Account
        clauseInformation = clauses.get(ClauseType.CUSTOMER_BANK_ACCOUNT);
        if (clauseInformation != null) {
            String brokerBankAccount = clauseInformation.getValue();
            data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.CUSTOMER_BANK_ACCOUNT, brokerBankAccount));
        }

        // Customer Location
        clauseInformation = clauses.get(ClauseType.CUSTOMER_PLACE_TO_DELIVER);
        if (clauseInformation != null) {
            String brokerBankAccount = clauseInformation.getValue();
            data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.CUSTOMER_LOCATION, brokerBankAccount));
        }

        // Datetime to Pay
        String datetimeToPay = clauses.get(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER).getValue();
        data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.DATE_TIME_TO_PAY, datetimeToPay));

        // Datetime to Deliver
        String datetimeToDeliver = clauses.get(ClauseType.BROKER_DATE_TIME_TO_DELIVER).getValue();
        data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.DATE_TIME_TO_DELIVER, datetimeToDeliver));

        // Datetime to Deliver
        String expirationDatetime = String.valueOf(negotiationInfo.getNegotiationExpirationDate());
        data.add(new SingleValueStepImp(++stepNumber, NegotiationStepType.EXPIRATION_DATE_TIME, expirationDatetime));

        return data;
    }

    @Override
    public void modifyNegotiationStepValues(NegotiationStep step, NegotiationStepStatus status, String... newValues) {
        NegotiationStepType negotiationStepType = step.getType();

        switch (negotiationStepType) {
            case EXCHANGE_RATE:
                ExchangeRateStepImp exchangeRateStep = (ExchangeRateStepImp) step;
                exchangeRateStep.setExchangeRate(newValues[0]);
                exchangeRateStep.setStatus(status);
                break;
            case AMOUNT_TO_SALE:
                AmountToSellStepImp amountToSellStep = (AmountToSellStepImp) step;
                amountToSellStep.setAmountToSell(newValues[0]);
                amountToSellStep.setAmountToReceive(newValues[1]);
                amountToSellStep.setExchangeRateValue(newValues[2]);
                amountToSellStep.setStatus(status);
                break;
            default:
                SingleValueStepImp singleValueStep = (SingleValueStepImp) step;
                singleValueStep.setValue(newValues[0]);
                singleValueStep.setStatus(status);
                break;
        }
    }

    @Override
    public boolean isNothingLeftToConfirm(List<NegotiationStep> dataSet) {
        for (NegotiationStep step : dataSet) {
            if (step.getStatus() == NegotiationStepStatus.CONFIRM) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CustomerBrokerNegotiationInformation sendNegotiationSteps(CustomerBrokerNegotiationInformation data, List<NegotiationStep> dataSet) {

        CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation wrapper = new CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation(data);
        CryptoBrokerWalletModuleClauseInformation clause;
        ClauseInformation clauseInfo;
        Map<ClauseType, ClauseInformation> clauses = wrapper.getClauses();

        wrapper.setStatus(NegotiationStatus.WAITING_FOR_CUSTOMER);
        wrapper.setLastNegotiationUpdateDate(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());

        for (NegotiationStep step : dataSet) {
            NegotiationStepType type = step.getType();
            switch (type) {
                case AMOUNT_TO_SALE:
                    AmountToSellStep amountToSellStep = (AmountToSellStep) step;

                    clauseInfo = clauses.get(ClauseType.BROKER_CURRENCY_QUANTITY);
                    clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), amountToSellStep.getAmountToReceive(), clauseInfo.getStatus());
                    clauses.put(ClauseType.BROKER_CURRENCY_QUANTITY, clause);

                    clauseInfo = clauses.get(ClauseType.CUSTOMER_CURRENCY_QUANTITY);
                    clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), amountToSellStep.getAmountToSell(), clauseInfo.getStatus());
                    clauses.put(ClauseType.CUSTOMER_CURRENCY_QUANTITY, clause);
                    break;

                case BROKER_BANK_ACCOUNT:
                    SingleValueStep singleValueStep = (SingleValueStep) step;
                    clauseInfo = clauses.get(ClauseType.BROKER_BANK_ACCOUNT);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), singleValueStep.getValue(), clauseInfo.getStatus());
                        clauses.put(ClauseType.BROKER_BANK_ACCOUNT, clause);
                    }
                    break;

                case BROKER_LOCATION:
                    singleValueStep = (SingleValueStep) step;
                    clauseInfo = clauses.get(ClauseType.BROKER_PLACE_TO_DELIVER);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), singleValueStep.getValue(), clauseInfo.getStatus());
                        clauses.put(ClauseType.BROKER_PLACE_TO_DELIVER, clause);
                    }
                    break;

                case CUSTOMER_BANK_ACCOUNT:
                    singleValueStep = (SingleValueStep) step;
                    clauseInfo = clauses.get(ClauseType.CUSTOMER_BANK_ACCOUNT);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), singleValueStep.getValue(), clauseInfo.getStatus());
                        clauses.put(ClauseType.CUSTOMER_BANK_ACCOUNT, clause);
                    }
                    break;

                case CUSTOMER_LOCATION:
                    singleValueStep = (SingleValueStep) step;
                    clauseInfo = clauses.get(ClauseType.CUSTOMER_PLACE_TO_DELIVER);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), singleValueStep.getValue(), clauseInfo.getStatus());
                        clauses.put(ClauseType.CUSTOMER_PLACE_TO_DELIVER, clause);
                    }
                    break;

                case DATE_TIME_TO_DELIVER:
                    singleValueStep = (SingleValueStep) step;
                    clauseInfo = clauses.get(ClauseType.BROKER_DATE_TIME_TO_DELIVER);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), singleValueStep.getValue(), clauseInfo.getStatus());
                        clauses.put(ClauseType.BROKER_DATE_TIME_TO_DELIVER, clause);
                    }
                    break;

                case DATE_TIME_TO_PAY:
                    singleValueStep = (SingleValueStep) step;

                    clauseInfo = clauses.get(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), singleValueStep.getValue(), clauseInfo.getStatus());
                        clauses.put(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, clause);
                    }
                    break;

                case EXCHANGE_RATE:
                    ExchangeRateStep exchangeRateStep = (ExchangeRateStep) step;
                    clauseInfo = clauses.get(ClauseType.EXCHANGE_RATE);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), exchangeRateStep.getExchangeRate(), clauseInfo.getStatus());
                        clauses.put(ClauseType.EXCHANGE_RATE, clause);
                    }
                    break;

                case PAYMENT_METHOD:
                    singleValueStep = (SingleValueStep) step;
                    clauseInfo = clauses.get(ClauseType.CUSTOMER_PAYMENT_METHOD);
                    if (clauseInfo != null) {
                        clause = new CryptoBrokerWalletModuleClauseInformation(clauseInfo.getType(), singleValueStep.getValue(), clauseInfo.getStatus());
                        clauses.put(ClauseType.CUSTOMER_PAYMENT_METHOD, clause);
                    }
                    break;

                case EXPIRATION_DATE_TIME:
                    singleValueStep = (SingleValueStep) step;
                    wrapper.setExpirationDatetime(Long.valueOf(singleValueStep.getValue()));
                    break;
            }
        }

        // TODO el wrapper se le va a pasar al plugin de Yordin "Customer Broker Update Negotiation Transaction"
        for (int i = 0; i < openNegotiations.size(); i++) {
            CustomerBrokerNegotiationInformation item = openNegotiations.get(i);
            if (item.getNegotiationId().equals(wrapper.getNegotiationId())) {
                openNegotiations.set(i, wrapper);
                break;
            }
        }

        return wrapper;
    }

    private List<ContractBasicInformation> getContractHistoryTestData() {
        if (contractsHistory == null) {
            ContractBasicInformation contract;
            contractsHistory = new ArrayList<>();

            contract = new CryptoBrokerWalletModuleContractBasicInformation("adrianasupernova", "USD", "Crypto Transfer", "BTC", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("nelsoanlfo", "BTC", "Bank Transfer", "Arg $", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("neoperol", "USD", "Cash in Hand", "BsF", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("nairovene", "USD", "Cash Delivery", "BsF", ContractStatus.CANCELLED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("Luis Pineda", "USD", "Crypto Transfer", "BTC", ContractStatus.PENDING_PAYMENT);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("Carlos Ruiz", "USD", "Bank Transfer", "Col $", ContractStatus.CANCELLED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("josePres", "USD", "Crypto Transfer", "BTC", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("nairo300", "USD", "Crypto Transfer", "BTC", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("dbz_brokers", "USD", "Crypto Transfer", "BTC", ContractStatus.CANCELLED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("Mirian Margarita Noguera", "USD", "Crypto Transfer", "BTC", ContractStatus.CANCELLED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("adrianasupernova", "USD", "Crypto Transfer", "BTC", ContractStatus.PENDING_PAYMENT);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("nelsoanlfo", "BTC", "Bank Transfer", "Arg $", ContractStatus.CANCELLED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("neoperol", "USD", "Cash in Hand", "BsF", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("nairovene", "USD", "Cash Delivery", "BsF", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("Luis Pineda", "USD", "Crypto Transfer", "BTC", ContractStatus.PENDING_PAYMENT);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("Carlos Ruiz", "USD", "Crypto Transfer", "BTC", ContractStatus.CANCELLED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("josePres", "USD", "Crypto Transfer", "BTC", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("nairo300", "USD", "Crypto Transfer", "BTC", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("dbz_brokers", "USD", "Crypto Transfer", "BTC", ContractStatus.PENDING_PAYMENT);
            contractsHistory.add(contract);
            contract = new CryptoBrokerWalletModuleContractBasicInformation("Mirian Margarita Noguera", "USD", "Crypto Transfer", "BTC", ContractStatus.COMPLETED);
            contractsHistory.add(contract);
        }

        return contractsHistory;
    }

    private List<CustomerBrokerNegotiationInformation> getOpenNegotiationsTestData() {
        if (openNegotiations != null) {
            return openNegotiations;
        }

        Random random = new Random(321515131);
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();

        float currencyQtyVal = random.nextFloat() * 100;
        float exchangeRateVal = random.nextFloat();
        String customerCurrencyQty = decimalFormat.format(currencyQtyVal);
        String exchangeRate = decimalFormat.format(exchangeRateVal);
        String brokerCurrencyQty = decimalFormat.format(currencyQtyVal * exchangeRateVal);
        long timeInMillisVal = System.currentTimeMillis();
        String timeInMillisStr = String.valueOf(timeInMillisVal);

        openNegotiations = new ArrayList<>();
        CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation item;

        item = new CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation("nelsonalfo", NegotiationStatus.WAITING_FOR_BROKER);
        item.setLastNegotiationUpdateDate(timeInMillisVal);
        item.setExpirationDatetime(timeInMillisVal);
        item.setNote("Le dices al portero que vas a nombre del señor Bastidas");
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, FiatCurrency.VENEZUELAN_BOLIVAR.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY_QUANTITY, brokerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, CryptoCurrency.BITCOIN.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, customerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_PAYMENT_METHOD, CASH_DELIVERY, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PLACE_TO_DELIVER, BROKER_LOCATION_1, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, CRYPTO_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CRYPTO_ADDRESS, CUSTOMER_CRYPTO_ADDRESS, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        openNegotiations.add(item);

        currencyQtyVal = random.nextFloat() * 100;
        exchangeRateVal = random.nextFloat();
        customerCurrencyQty = decimalFormat.format(currencyQtyVal);
        exchangeRate = decimalFormat.format(exchangeRateVal);
        brokerCurrencyQty = decimalFormat.format(currencyQtyVal * exchangeRateVal);
        timeInMillisVal = System.currentTimeMillis();
        timeInMillisStr = String.valueOf(timeInMillisVal);

        item = new CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation("jorgeegonzalez", NegotiationStatus.WAITING_FOR_BROKER);
        item.setLastNegotiationUpdateDate(timeInMillisVal);
        item.setExpirationDatetime(timeInMillisVal);
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, FiatCurrency.VENEZUELAN_BOLIVAR.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY_QUANTITY, brokerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, FiatCurrency.US_DOLLAR.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, customerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_PAYMENT_METHOD, CASH_IN_HAND, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PLACE_TO_DELIVER, BROKER_LOCATION_1, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, BANK_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_BANK_ACCOUNT, CUSTOMER_BANK_ACCOUNT_1, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        openNegotiations.add(item);

        currencyQtyVal = random.nextFloat() * 100;
        exchangeRateVal = random.nextFloat();
        customerCurrencyQty = decimalFormat.format(currencyQtyVal);
        exchangeRate = decimalFormat.format(exchangeRateVal);
        brokerCurrencyQty = decimalFormat.format(currencyQtyVal * exchangeRateVal);
        timeInMillisVal = System.currentTimeMillis();
        timeInMillisStr = String.valueOf(timeInMillisVal);

        item = new CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation("Matias Furzyfer", NegotiationStatus.WAITING_FOR_BROKER);
        item.setLastNegotiationUpdateDate(timeInMillisVal);
        item.setExpirationDatetime(timeInMillisVal);
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, FiatCurrency.ARGENTINE_PESO.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY_QUANTITY, brokerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, FiatCurrency.US_DOLLAR.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, customerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_PAYMENT_METHOD, BANK_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_BANK_ACCOUNT, BROKER_BANK_ACCOUNT_1, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, CASH_DELIVERY, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_PLACE_TO_DELIVER, CUSTOMER_LOCATION_2, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        openNegotiations.add(item);


        currencyQtyVal = random.nextFloat() * 100;
        exchangeRateVal = random.nextFloat();
        customerCurrencyQty = decimalFormat.format(currencyQtyVal);
        exchangeRate = decimalFormat.format(exchangeRateVal);
        brokerCurrencyQty = decimalFormat.format(currencyQtyVal * exchangeRateVal);
        timeInMillisVal = System.currentTimeMillis();
        timeInMillisStr = String.valueOf(timeInMillisVal);

        item = new CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation("neoperol", NegotiationStatus.WAITING_FOR_BROKER);
        item.setLastNegotiationUpdateDate(timeInMillisVal);
        item.setExpirationDatetime(timeInMillisVal);
        item.setNote("Nos vemos cerca de la entrada principal. Voy vestido de franela amarílla y pantalón de Jean");
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, CryptoCurrency.BITCOIN.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY_QUANTITY, brokerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, FiatCurrency.US_DOLLAR.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, customerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_PAYMENT_METHOD, CRYPTO_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CRYPTO_ADDRESS, BROKER_CRYPTO_ADDRESS, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, BANK_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_BANK_ACCOUNT, CUSTOMER_BANK_ACCOUNT_1, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        openNegotiations.add(item);


        currencyQtyVal = random.nextFloat() * 100;
        exchangeRateVal = random.nextFloat();
        customerCurrencyQty = decimalFormat.format(currencyQtyVal);
        exchangeRate = decimalFormat.format(exchangeRateVal);
        brokerCurrencyQty = decimalFormat.format(currencyQtyVal * exchangeRateVal);
        timeInMillisVal = System.currentTimeMillis();
        timeInMillisStr = String.valueOf(timeInMillisVal);

        item = new CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation("Nelson Orlando", NegotiationStatus.WAITING_FOR_CUSTOMER);
        item.setLastNegotiationUpdateDate(timeInMillisVal);
        item.setExpirationDatetime(timeInMillisVal);
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, CryptoCurrency.BITCOIN.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY_QUANTITY, brokerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, FiatCurrency.VENEZUELAN_BOLIVAR.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, customerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_PAYMENT_METHOD, CRYPTO_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CRYPTO_ADDRESS, BROKER_CRYPTO_ADDRESS, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, CASH_DELIVERY, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_BANK_ACCOUNT, CUSTOMER_LOCATION_1, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        openNegotiations.add(item);

        currencyQtyVal = random.nextFloat() * 100;
        exchangeRateVal = random.nextFloat();
        customerCurrencyQty = decimalFormat.format(currencyQtyVal);
        exchangeRate = decimalFormat.format(exchangeRateVal);
        brokerCurrencyQty = decimalFormat.format(currencyQtyVal * exchangeRateVal);
        timeInMillisVal = System.currentTimeMillis();
        timeInMillisStr = String.valueOf(timeInMillisVal);

        item = new CryptoBrokerWalletModuleCustomerBrokerNegotiationInformation("Customer 5", NegotiationStatus.WAITING_FOR_CUSTOMER);
        item.setLastNegotiationUpdateDate(timeInMillisVal);
        item.setExpirationDatetime(timeInMillisVal);
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY, CryptoCurrency.LITECOIN.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CURRENCY_QUANTITY, brokerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY, CryptoCurrency.BITCOIN.getCode(), ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CURRENCY_QUANTITY, customerCurrencyQty, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.EXCHANGE_RATE, exchangeRate, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_PAYMENT_METHOD, CRYPTO_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_CRYPTO_ADDRESS, BROKER_CRYPTO_ADDRESS, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_PAYMENT_METHOD, CRYPTO_TRANSFER, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_CRYPTO_ADDRESS, CUSTOMER_CRYPTO_ADDRESS, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.BROKER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        item.addClause(new CryptoBrokerWalletModuleClauseInformation(ClauseType.CUSTOMER_DATE_TIME_TO_DELIVER, timeInMillisStr, ClauseStatus.DRAFT));
        openNegotiations.add(item);

        return openNegotiations;
    }
}
