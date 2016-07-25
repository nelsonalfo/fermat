package unit.CryptoBrokerStockTransactionImpl;

import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.CryptoBrokerStockTransactionImpl;

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
public class GetTransactionIdTest {

    @Test
    public void getTransactionId() {
        CryptoBrokerStockTransactionImpl cryptoBrokerStockTransaction = mock(CryptoBrokerStockTransactionImpl.class);
        when(cryptoBrokerStockTransaction.getTransactionId()).thenReturn(UUID.randomUUID());
        assertThat(cryptoBrokerStockTransaction.getTransactionId()).isNotNull();
    }

}
