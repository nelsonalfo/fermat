package unit.com.bitdubai.fermat_cbp_plugin.layer.network_service.transaction_transmission.developer.bitdubai.version_1.structure.BusinessTransactionMetadataRecord;

import com.bitdubai.fermat_cbp_api.layer.network_service.TransactionTransmission.enums.BusinessTransactionTransactionType;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.transaction_transmission.developer.bitdubai.version_1.structure.BusinessTransactionMetadataRecord;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;


/**
 * Created by root on 14/12/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class SetBusinessTransactionTransactionTypeTest {

    @Test
    public void setBusinessTransactionTransactionType() throws Exception{

        BusinessTransactionMetadataRecord businessTransactionMetadataRecord = mock(BusinessTransactionMetadataRecord.class,Mockito.RETURNS_DEEP_STUBS);
        doCallRealMethod().when(businessTransactionMetadataRecord).setBusinessTransactionTransactionType(Mockito.any(BusinessTransactionTransactionType.class));

    }
}
