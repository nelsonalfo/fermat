package CryptoBrokerWalletModuleContractBasicInformation;

import com.bitdubai.fermat_cbp_plugin.layer.wallet_module.crypto_customer.developer.bitdubai.version_1.structure.CryptoBrokerWalletModuleContractBasicInformation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by roy on 5/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetMerchandiseTest {
    @Test
    public void getMerchandise() {
        CryptoBrokerWalletModuleContractBasicInformation cryptoBrokerWalletModuleContractBasicInformation = mock(CryptoBrokerWalletModuleContractBasicInformation.class);
        when(cryptoBrokerWalletModuleContractBasicInformation.getMerchandise()).thenReturn(new String());
        assertThat(cryptoBrokerWalletModuleContractBasicInformation.getMerchandise()).isNotNull();
    }

}
