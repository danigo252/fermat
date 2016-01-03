package unit.com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_close.developer.bitdubai.version_1.structure.CustomerBrokerCloseManagerImpl;

import com.bitdubai.fermat_cbp_api.layer.negotiation_transaction.customer_broker_new.interfaces.CustomerBrokerNew;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_new.developer.bitdubai.version_1.structure.CustomerBrokerNewManagerImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Yordin Alayn on 02.01.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetCustomerBrokerCloseNegotiationTranasctionTest {

    private UUID transactionId = UUID.randomUUID();

    @Test
    public void getCustomerBrokerNewNegotiationTranasction() throws Exception{

        CustomerBrokerNew customerBrokerNew = null;

        CustomerBrokerNewManagerImpl customerBrokerNewManagerImpl = mock(CustomerBrokerNewManagerImpl.class, Mockito.RETURNS_DEEP_STUBS);
        when(customerBrokerNewManagerImpl.getCustomerBrokerNewNegotiationTranasction(transactionId)).thenReturn(customerBrokerNew).thenCallRealMethod();
        assertThat(customerBrokerNewManagerImpl.getCustomerBrokerNewNegotiationTranasction(transactionId)).isNotNull();

    }
}
