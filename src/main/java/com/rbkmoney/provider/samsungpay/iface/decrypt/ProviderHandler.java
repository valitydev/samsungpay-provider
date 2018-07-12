package com.rbkmoney.provider.samsungpay.iface.decrypt;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.payment_tool_provider.*;
import com.rbkmoney.provider.samsungpay.domain.CardBrand;
import com.rbkmoney.provider.samsungpay.domain.CredentialsResponse;
import com.rbkmoney.provider.samsungpay.domain.PData3DS;
import com.rbkmoney.provider.samsungpay.service.SPayService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by vpankrashkin on 04.07.18.
 */
public class ProviderHandler implements PaymentToolProviderSrv.Iface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SPayService service;

    public ProviderHandler(SPayService service) {
        this.service = service;
    }

    @Override
    public UnwrappedPaymentTool unwrap(WrappedPaymentTool paymentTool) throws InvalidRequest, TException {
        log.info("New unwrap request: {}", paymentTool);
        if (!paymentTool.getRequest().isSetSamsung()) {
            throw new InvalidRequest(Arrays.asList("Received request type is not SamsungPay"));
        }
        String refId = paymentTool.getRequest().getSamsung().getReferenceId();
        String srvId = paymentTool.getRequest().getSamsung().getServiceId();
        try {
            Map.Entry<CredentialsResponse, PData3DS> responseWithPData3DS = service.getCredentials(srvId, refId);

            CredentialsResponse credentialsResponse = responseWithPData3DS.getKey();
            PData3DS pData = responseWithPData3DS.getValue();
            log.info("Payment data decrypted: {}", pData);

            UnwrappedPaymentTool result = new UnwrappedPaymentTool();
            SamsungPayDetails samsungPayDetails = new SamsungPayDetails();
            samsungPayDetails.setDeviceId(credentialsResponse.deviceId);
            result.setDetails(PaymentDetails.samsung(samsungPayDetails));

            CardPaymentData cardPaymentData = new CardPaymentData();
            TokenizedCard tokenizedCard = new TokenizedCard();
            tokenizedCard.setDpan(pData.dpan);
            ExpDate expDate = new ExpDate();
            expDate.setMonth((byte) pData.expirationDate.getMonth().getValue());
            expDate.setYear((short) pData.expirationDate.getYear());
            tokenizedCard.setExpDate(expDate);
            AuthData authData = new AuthData();
            Auth3DS auth3DS = new Auth3DS();
            auth3DS.setCryptogram(pData.cryptogram);
            auth3DS.setEci(pData.eci);
            authData.setAuth3ds(auth3DS);
            tokenizedCard.setAuthData(authData);
            cardPaymentData.setTokenizedCard(tokenizedCard);
            result.setPaymentData(cardPaymentData);

            CardInfo cardInfo = new CardInfo();
//            cardInfo.setCardClass(); //?
//            cardInfo.setDisplayName(); //?
            cardInfo.setLast4Digits(credentialsResponse.last4digits);
            cardInfo.setPaymentSystem(CardBrand.findPaymentSystemById(credentialsResponse.cardBrand));
            cardInfo.setCardholderName(pData.cardholder);
            result.setCardInfo(cardInfo);

            return result;
        } catch (IOException e) {
            //log.error("Failed to read json data: {}", filterPan(e.getMessage()));
            throw new InvalidRequest(Arrays.asList("Failed to read json data"));
        } catch (Exception e) {
            log.error("Failed to get credentials", e);
            throw new InvalidRequest(Arrays.asList(e.getMessage()));
        }
    }
}
