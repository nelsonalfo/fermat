package unit.CryptoBrokerWalletSettingSpreadImpl;

import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerWalletSettingSpreadImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by José Vilchez on 21/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetIdTest {

    @Test
    public void getId() {
        CryptoBrokerWalletSettingSpreadImpl cryptoBrokerWalletSettingSpread = mock(CryptoBrokerWalletSettingSpreadImpl.class);
        when(cryptoBrokerWalletSettingSpread.getId()).thenReturn(UUID.randomUUID());
        assertThat(cryptoBrokerWalletSettingSpread.getId()).isNotNull();
    }

}
