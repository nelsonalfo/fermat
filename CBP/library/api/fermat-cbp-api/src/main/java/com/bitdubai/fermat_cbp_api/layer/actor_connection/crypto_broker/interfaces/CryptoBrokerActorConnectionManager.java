package com.bitdubai.fermat_cbp_api.layer.actor_connection.crypto_broker.interfaces;

import com.bitdubai.fermat_api.layer.actor_connection.common.interfaces.ActorConnectionManager;
import com.bitdubai.fermat_cbp_api.layer.actor_connection.crypto_broker.utils.CryptoBrokerLinkedActorIdentity;
import com.bitdubai.fermat_cbp_api.layer.actor_connection.crypto_broker.utils.CryptoBrokerActorConnection;

/**
 * The interface <code>com.bitdubai.fermat_cbp_api.layer.actor_connection.crypto_broker.interfaces.CryptoCustomerActorConnectionManager</code>
 * provide the methods to manage actor connections for the crypto broker actors.
 * <p>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 20/11/2015.
 */
public interface CryptoBrokerActorConnectionManager extends ActorConnectionManager<CryptoBrokerLinkedActorIdentity, CryptoBrokerActorConnection, CryptoBrokerActorConnectionSearch> {

}
