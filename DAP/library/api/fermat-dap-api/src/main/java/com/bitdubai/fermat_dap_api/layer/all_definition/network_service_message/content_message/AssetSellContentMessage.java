package com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.content_message;

import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAssetMetadata;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.AssetSellStatus;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPMessageType;

import java.util.UUID;

/**
 * Created by Víctor A. Mars M. (marsvicam@gmail.com) on 11/02/16.
 */
public class AssetSellContentMessage implements DAPContentMessage {
    //VARIABLE DECLARATION
    private UUID sellingId;
    private byte[] serializedTransaction;
    private long transactionValue;
    private AssetSellStatus sellStatus;
    private DigitalAssetMetadata assetMetadata;
    private UUID negotiationId;
    private CryptoAddress cryptoVaultAddress;
    //CONSTRUCTORS

    public AssetSellContentMessage() {
    }

    public AssetSellContentMessage(UUID sellingId, byte[] serializedTransaction, AssetSellStatus sellStatus, DigitalAssetMetadata assetMetadata, UUID negotiationId, long transactionValue, CryptoAddress cryptoVaultAddress) {
        this.sellingId = sellingId;
        this.serializedTransaction = serializedTransaction;
        this.sellStatus = sellStatus;
        this.assetMetadata = assetMetadata;
        this.negotiationId = negotiationId;
        this.transactionValue = transactionValue;
        this.cryptoVaultAddress = cryptoVaultAddress;
    }

    //PUBLIC METHODS

    /**
     * Every content message should have a unique type associate to it.
     *
     * @return {@link DAPMessageType} The message type that corresponds to this content message.
     */
    @Override
    public DAPMessageType messageType() {
        return DAPMessageType.ASSET_SELL;
    }

    //PRIVATE METHODS

    //GETTER AND SETTERS

    public byte[] getSerializedTransaction() {
        return serializedTransaction;
    }

    public AssetSellStatus getSellStatus() {
        return sellStatus;
    }

    public UUID getSellingId() {
        return sellingId;
    }

    public DigitalAssetMetadata getAssetMetadata() {
        return assetMetadata;
    }

    public UUID getNegotiationId() {
        return negotiationId;
    }

    public long getTransactionValue() {
        return transactionValue;
    }

    public CryptoAddress getCryptoVaultAddress() {
        return cryptoVaultAddress;
    }

    //INNER CLASSES
}
