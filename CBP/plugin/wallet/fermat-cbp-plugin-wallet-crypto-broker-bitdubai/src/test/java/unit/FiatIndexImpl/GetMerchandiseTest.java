package unit.FiatIndexImpl;

import com.bitdubai.fermat_cbp_api.all_definition.enums.MoneyType;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.FiatIndexImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by José Vilchez on 21/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetMerchandiseTest {

    @Test
    public void getMerchandise() {
        FiatIndexImpl fiatIndex = mock(FiatIndexImpl.class);
        when(fiatIndex.getMerchandise()).thenReturn(MoneyType.CRYPTO);
        assertThat(fiatIndex.getMerchandise()).isNotNull();
    }

}
