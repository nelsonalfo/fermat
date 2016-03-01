package com.bitdubai.fermat_ccp_plugin.layer.request.crypto_payment.developer.bitdubai.version_1.event_handlers;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.exceptions.UnexpectedEventException;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_ccp_api.all_definition.enums.EventType;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.exceptions.CantApproveCryptoPaymentRequestException;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.exceptions.CantUpdateRequestPaymentStateException;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.exceptions.CryptoPaymentRequestNotFoundException;
import com.bitdubai.fermat_ccp_api.layer.request.crypto_payment.exceptions.InsufficientFundsException;
import com.bitdubai.fermat_ccp_plugin.layer.request.crypto_payment.developer.bitdubai.version_1.CryptoPaymentRequestPluginRoot;
import com.bitdubai.fermat_ccp_plugin.layer.request.crypto_payment.developer.bitdubai.version_1.exceptions.CryptoPaymentCantHandleTransactionDebitException;
import com.bitdubai.fermat_ccp_api.layer.platform_service.event_manager.events.IncomingIntraUserTransactionDebitNotificationEvent;

/**
 * Created by natalia on 26/11/15.
 */
public class IncomingIntraUserTransactionDebitEventHandler implements FermatEventHandler {

    private final CryptoPaymentRequestPluginRoot cryptoPaymentRequestPluginRoot;

    public IncomingIntraUserTransactionDebitEventHandler(final CryptoPaymentRequestPluginRoot   cryptoPaymentRequestPluginRoot ) {

        this.cryptoPaymentRequestPluginRoot = cryptoPaymentRequestPluginRoot;

    }
    /**
     * FermatEventHandler interface implementation
     */
    @Override
    public void handleEvent(FermatEvent fermatEvent) throws FermatException {

        if (this.cryptoPaymentRequestPluginRoot.getStatus() == ServiceStatus.STARTED) {

            IncomingIntraUserTransactionDebitNotificationEvent  incomingDebitEvent = (IncomingIntraUserTransactionDebitNotificationEvent)fermatEvent;

                try
                {
                    cryptoPaymentRequestPluginRoot.getCryptoPaymentRegistry().acceptIncomingRequest(incomingDebitEvent.getRequestId());
                
                }catch(CantUpdateRequestPaymentStateException e){
                    throw new CryptoPaymentCantHandleTransactionDebitException("Can't Update CryptoPayment",e,"","");
                }
                catch (FermatException e) {
                    throw new CryptoPaymentCantHandleTransactionDebitException("An exception happened",e,"","");
                } catch (Exception e) {
                    throw new CryptoPaymentCantHandleTransactionDebitException("An unexpected exception happened",FermatException.wrapException(e),"","");
                }
                

            } else {
                EventType eventExpected = EventType.CRYPTO_PAYMENT_REQUEST_NEWS;
                String context = "Event received: " + fermatEvent.getEventType().toString() + " - " + fermatEvent.getEventType().getCode()+"\n"+
                        "Event expected: " + eventExpected.toString()              + " - " + eventExpected.getCode();
                throw new UnexpectedEventException(context);
            }

    }

}