package BankMoneyRestockTransactionImpl;

import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.bank_money_restock.developer.bitdubai.version_1.structure.BankMoneyRestockTransactionImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jose Vilchez on 18/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetCbpWalletPublicKeyTest {

    @Test
    public void getCbpWalletPublicKey() {
        BankMoneyRestockTransactionImpl bankMoneyRestockTransaction = mock(BankMoneyRestockTransactionImpl.class);
        when(bankMoneyRestockTransaction.getCbpWalletPublicKey()).thenReturn(new String());
        assertThat(bankMoneyRestockTransaction.getCbpWalletPublicKey()).isNotNull();
    }

}
