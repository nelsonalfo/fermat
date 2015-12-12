package com.bitdubai.fermat_cbp_api.layer.network_service.NegotiationTransmission.events;

import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_cbp_api.all_definition.events.AbstractCBPFermatEvent;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventType;

/**
 * Created by Yordin Alayn on 04.12.15.
 */
public class IncomingNegotiationTransactionEvent extends AbstractCBPFermatEvent {

    PlatformComponentType destinationPlatformComponentType;

    public IncomingNegotiationTransactionEvent(EventType eventType) {
        super(eventType);
    }

    public PlatformComponentType getDestinationPlatformComponentType(){
        return destinationPlatformComponentType;
    }

    public void setDestinationPlatformComponentType(PlatformComponentType destinationPlatformComponentType){
        this.destinationPlatformComponentType = destinationPlatformComponentType;
    }
}
