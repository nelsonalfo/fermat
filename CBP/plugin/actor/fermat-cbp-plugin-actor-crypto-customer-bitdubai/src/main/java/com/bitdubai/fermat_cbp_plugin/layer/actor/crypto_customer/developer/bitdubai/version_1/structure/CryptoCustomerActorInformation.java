package com.bitdubai.fermat_cbp_plugin.layer.actor.crypto_customer.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_cbp_api.all_definition.identity.ActorIdentity;
import com.bitdubai.fermat_cbp_api.layer.actor.crypto_broker.interfaces.CryptoBrokerActor;

/**
 * Created by Angel on 31/01/2016.
 */

public class CryptoCustomerActorInformation implements CryptoBrokerActor {

    private static final int HASH_PRIME_NUMBER_PRODUCT = 1117;
    private static final int HASH_PRIME_NUMBER_ADD = 3001;

    private final ActorIdentity identity;

    public CryptoCustomerActorInformation(final ActorIdentity identity){
        this.identity = identity;
    }

    @Override
    public ActorIdentity getIdentity() {
        return identity;
    }

    @Override
    public boolean equals(final Object o){
        if(!(o instanceof CryptoBrokerActor))
            return false;
        CryptoBrokerActor compare = (CryptoBrokerActor) o;
        return this.identity.equals(compare.getIdentity());
    }

    @Override
    public int hashCode(){
        int c = 0;
        c += identity.hashCode();
        return 	HASH_PRIME_NUMBER_PRODUCT * HASH_PRIME_NUMBER_ADD + c;
    }
}
