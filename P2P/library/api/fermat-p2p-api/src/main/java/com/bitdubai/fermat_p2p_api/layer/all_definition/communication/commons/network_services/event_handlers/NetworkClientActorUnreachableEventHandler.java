package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientActorUnreachableEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractNetworkService;

/**
 * The Class <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientActorUnreachableEventHandler</code>
 * implements the handling of the event <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientActorUnreachableEvent</code>
 * reference: <code>P2pEventType.NETWORK_CLIENT_ACTOR_UNREACHABLE</code>
 * <p/>
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 14/06/2016.
 *
 * @author  lnacosta
 * @version 1.0
 * @since   Java JDK 1.7
 */
public class NetworkClientActorUnreachableEventHandler implements FermatEventHandler<NetworkClientActorUnreachableEvent> {

    /**
     * Represent the networkService
     */
    private AbstractNetworkService networkService;

    /**
     * Constructor with parameter
     *
     * @param networkService
     */
    public NetworkClientActorUnreachableEventHandler(AbstractNetworkService networkService) {
        this.networkService = networkService;
    }

    /**
     * (non-Javadoc)
     *
     * @see FermatEventHandler#handleEvent(FermatEvent)
     *
     * @param fermatEvent instance of NetworkClientActorFoundEvent
     *
     * @throws FermatException if something goes wrong.
     */
    @Override
    public void handleEvent(NetworkClientActorUnreachableEvent fermatEvent) throws FermatException {

        if (this.networkService.isStarted() &&
                this.networkService.getProfile().getNetworkServiceType() == fermatEvent.getNetworkServiceType()) {

            this.networkService.handleActorUnreachable(fermatEvent.getActorProfile());
        }

    }

}
