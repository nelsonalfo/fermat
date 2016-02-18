package com.bitdubai.fermat_cbp_plugin.layer.network_service.negotiation_transmission.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransactionType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransmissionState;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationTransmissionType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationType;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.enums.ActorProtocolState;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.interfaces.NegotiationTransmission;

import java.util.UUID;

/**
 * Created by Yordin Alayn on 29.11.15.
 */
public class NegotiationTransmissionImpl implements NegotiationTransmission {

    private final UUID transmissionId;
    private final UUID transactionId;
    private final UUID negotiationId;
    private NegotiationTransactionType negotiationTransactionType;
    private final String publicKeyActorSend;
    private final PlatformComponentType actorSendType;
    private final String publicKeyActorReceive;
    private final PlatformComponentType actorReceiveType;
    private NegotiationTransmissionType transmissionType;
    private NegotiationTransmissionState transmissionState;
    private final NegotiationType negotiationType;
    private final String negotiationXML;
    private final long timestamp;
    private boolean pendingFlag;
    private boolean flagRead;
    private int sentCount;

    private UUID responseToNotificationId;

    //OLD ONE
    public NegotiationTransmissionImpl(
            final UUID transmissionId,
            final UUID transactionId,
            final UUID negotiationId,
            final NegotiationTransactionType negotiationTransactionType,
            final String publicKeyActorSend,
            final PlatformComponentType actorSendType,
            final String publicKeyActorReceive,
            final PlatformComponentType actorReceiveType,
            final NegotiationTransmissionType transmissionType,
            final NegotiationTransmissionState transmissionState,
            final NegotiationType negotiationType,
            final String negotiationXML,
            final long timestamp
    ) {
        this.transmissionId = transmissionId;
        this.transactionId = transactionId;
        this.negotiationId = negotiationId;
        this.negotiationTransactionType = negotiationTransactionType;
        this.publicKeyActorSend = publicKeyActorSend;
        this.actorSendType = actorSendType;
        this.publicKeyActorReceive = publicKeyActorReceive;
        this.actorReceiveType = actorReceiveType;
        this.transmissionType = transmissionType;
        this.transmissionState = transmissionState;
        this.negotiationType = negotiationType;
        this.negotiationXML = negotiationXML;
        this.timestamp = timestamp;
        this.pendingFlag = false;
    }

    public NegotiationTransmissionImpl(
            final UUID transmissionId,
            final UUID transactionId,
            final UUID negotiationId,
            final NegotiationTransactionType negotiationTransactionType,
            final String publicKeyActorSend,
            final PlatformComponentType actorSendType,
            final String publicKeyActorReceive,
            final PlatformComponentType actorReceiveType,
            final NegotiationTransmissionType transmissionType,
            final NegotiationTransmissionState transmissionState,
            final NegotiationType negotiationType,
            final String negotiationXML,
            final long timestamp,
            final boolean pendingFlag,
            final boolean flagRead,
            final ActorProtocolState actorProtocolState,
            final int sentCount,
            final UUID responseToNotificationId
    ) {
        this.transmissionId = transmissionId;
        this.transactionId = transactionId;
        this.negotiationId = negotiationId;
        this.negotiationTransactionType = negotiationTransactionType;
        this.publicKeyActorSend = publicKeyActorSend;
        this.actorSendType = actorSendType;
        this.publicKeyActorReceive = publicKeyActorReceive;
        this.actorReceiveType = actorReceiveType;
        this.transmissionType = transmissionType;
        this.transmissionState = transmissionState;
        this.negotiationType = negotiationType;
        this.negotiationXML = negotiationXML;
        this.timestamp = timestamp;
        this.pendingFlag = false;
        this.pendingFlag = pendingFlag;
        this.flagRead = flagRead;
        this.sentCount = sentCount;
        this.responseToNotificationId = responseToNotificationId;
    }

    @Override
    public UUID getTransmissionId() {
        return transmissionId;
    }

    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public UUID getNegotiationId() {
        return negotiationId;
    }

    @Override
    public NegotiationTransactionType getNegotiationTransactionType() {
        return negotiationTransactionType;
    }

    @Override
    public String getPublicKeyActorSend() {
        return publicKeyActorSend;
    }

    @Override
    public PlatformComponentType getActorSendType() {
        return actorSendType;
    }

    @Override
    public String getPublicKeyActorReceive() {
        return publicKeyActorReceive;
    }

    @Override
    public PlatformComponentType getActorReceiveType() {
        return actorReceiveType;
    }

    @Override
    public NegotiationTransmissionType getTransmissionType() {
        return transmissionType;
    }

    @Override
    public NegotiationTransmissionState getTransmissionState() {
        return transmissionState;
    }

    @Override
    public NegotiationType getNegotiationType() {
        return negotiationType;
    }

    @Override
    public String getNegotiationXML() {
        return negotiationXML;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean isPendingToRead() {
        return this.pendingFlag;
    }

    @Override
    public void confirmRead() {
        this.pendingFlag = true;
    }

    @Override
    public void setNegotiationTransactionType(NegotiationTransactionType type) {
        this.negotiationTransactionType = type;
    }

    @Override
    public void setTransmissionType(NegotiationTransmissionType type) {
        this.transmissionType = type;
    }

    @Override
    public void setTransmissionState(NegotiationTransmissionState state) {
        this.transmissionState = state;
    }

    public boolean isFlagRead() {
        return flagRead;
    }

    public void setFlagRead(boolean flagRead) {
        this.flagRead = flagRead;
    }

    public int getSentCount() {
        return sentCount;
    }

    public void setSentCount(int sentCount) {
        this.sentCount = sentCount;
    }

    public UUID getResponseToNotificationId() {
        return responseToNotificationId;
    }

    public void setResponseToNotificationId(UUID responseToNotificationId) {
        this.responseToNotificationId = responseToNotificationId;
    }

    public boolean isPendingFlag() {
        return pendingFlag;
    }

    public void setPendingFlag(boolean pendingFlag) {
        this.pendingFlag = pendingFlag;
    }
}
