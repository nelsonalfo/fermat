package com.bitdubai.fermat_cbp_plugin.layer.actor.crypto_customer.developer.bitdubai.version_1.database;

import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.world.interfaces.Currency;
import com.bitdubai.fermat_cbp_api.all_definition.identity.ActorIdentity;
import com.bitdubai.fermat_cbp_api.layer.actor.crypto_customer.exceptions.CantCreateNewActorExtraDataException;
import com.bitdubai.fermat_cbp_api.layer.actor.crypto_customer.interfaces.ActorExtraData;
import com.bitdubai.fermat_cbp_api.layer.actor.crypto_customer.interfaces.QuotesExtraData;
import com.bitdubai.fermat_cbp_plugin.layer.actor.crypto_customer.developer.bitdubai.version_1.structure.ActorExtraDataIdentity;
import com.bitdubai.fermat_cbp_plugin.layer.actor.crypto_customer.developer.bitdubai.version_1.structure.ActorExtraDataInformation;
import com.bitdubai.fermat_cbp_plugin.layer.actor.crypto_customer.developer.bitdubai.version_1.structure.QuotesExtraDataInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by angel on 18/01/16.
 */
public class pruebaExtraData {

    CryptoCustomerActorDao dao;

    public pruebaExtraData(CryptoCustomerActorDao dao){
        this.dao = dao;
        test();
    }

    public void test(){
        ActorIdentity Customer = new ActorExtraDataIdentity("Angel", UUID.randomUUID().toString());

        Collection<QuotesExtraData> quotes = new ArrayList<>();

        QuotesExtraData quote = new QuotesExtraDataInformation(
            UUID.randomUUID(),
            CryptoCurrency.BITCOIN,
            FiatCurrency.VENEZUELAN_BOLIVAR,
            348f
        );

        quotes.add(quote);

        Map<Currency, Collection<Platforms>> currencies = new HashMap<Currency, Collection<Platforms>>();

        Collection<Platforms> pla = new ArrayList<>();
        pla.add(Platforms.BANKING_PLATFORM);
        pla.add(Platforms.CASH_PLATFORM);

        currencies.put(FiatCurrency.VENEZUELAN_BOLIVAR, pla);

        ActorExtraData actorExtraData = new ActorExtraDataInformation(Customer, quotes, currencies);

        try {
            this.dao.createCustomerExtraData(actorExtraData);
        } catch (CantCreateNewActorExtraDataException e) {
            System.out.println("Error creando el registro");
        }
    }

}
