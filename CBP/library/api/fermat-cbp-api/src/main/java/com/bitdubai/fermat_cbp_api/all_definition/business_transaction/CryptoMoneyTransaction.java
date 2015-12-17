package com.bitdubai.fermat_cbp_api.all_definition.business_transaction;


import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_cbp_api.all_definition.enums.OriginTransaction;
import com.bitdubai.fermat_cbp_api.all_definition.enums.TransactionStatusRestockDestock;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by franklin on 16/11/15.
 */
public interface CryptoMoneyTransaction {
    //TODO: Documentar
    /**
     * The property <code>TransactionId</code>  represented identifier the record
     *
     * @return the UUID
     */
    UUID getTransactionId();
    void setTransactionId(UUID transactionId);

    /**
     * The property <code>ActorPublicKey</code>  represented the public key Actor
     *
     * @return the Sting
     */
    String getActorPublicKey();
    void setActorPublicKey(String actorPublicKey);

    /**
     * The property <code>CryptoCurrency</code>  represented the Crypto Currency
     *
     * @return the CryptoCurrency
     */
    CryptoCurrency getCryptoCurrency();
    void setCryptoCurrency(CryptoCurrency cryptoCurrency);

    /**
     * The property <code>WalletPublicKey</code>  represented the public key Wallet
     *
     * @return the Sting
     */
    String getCbpWalletPublicKey();
    void setCbpWalletPublicKey(String cbpWalletPublicKey);

    /**
     * The property <code>getCryWalletPublicKey</code>  represented the public key Bitcoin Wallet
     *
     * @return the Sting
     */
    String getCryWalletPublicKey();
    void setCryWalletPublicKey(String cryWalletPublicKey);

    /**
     * The property <code>Concept</code>  represented the public key Bank
     *
     * @return the Sting
     */
    String getConcept();
    void setConcept(String concept);

    /**
     * The property <code>Amount</code>  represented the amount in transaction
     *
     * @return the BigDecimal
     */
    BigDecimal getAmount();
    void setAmount(BigDecimal amount);

    /**
     * The property <code>Timestamp</code>  represented the Date in transaction
     *
     * @return the Timestamp
     */
    Timestamp getTimeStamp();
    void setTimeStamp(Timestamp timeStamp);

    /**
     * The property <code>Memo</code>  represented the Memo transaction
     *
     * @return the Sting
     */
    String getMemo();
    void setMemo(String memo);

    /**
     * The property <code>TransactionStatusRestockDestock</code>  represented the Status in transaction
     *
     * @return the Sting
     */
    TransactionStatusRestockDestock getTransactionStatus();
    void setTransactionStatus(TransactionStatusRestockDestock transactionStatus);


    /**
     * The property <code>PriceReference</code>  represented the Price Reference
     * @return BigDecimal
     */
    BigDecimal getPriceReference();
    void setPriceReference(BigDecimal priceReference);

    /**
     * The property <code>OriginTransaction</code>  represented the Origin Transaction
     *
     * @return the OriginTransaction
     */
    OriginTransaction getOriginTransaction();
    void setOriginTransaction(OriginTransaction originTransaction);

}
