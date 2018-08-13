package com.rbkmoney.provider.samsungpay;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.payment_tool_provider.*;
import com.rbkmoney.provider.samsungpay.service.SPayClient;
import com.rbkmoney.provider.samsungpay.store.SPKeyStore;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.rest_port=65432"}
)
public class PaymentToolProviderTest {

    @MockBean
    private SPayClient sPayClient;

    @MockBean
    private SPKeyStore spKeyStore;

    @LocalServerPort
    private int port;

    private PaymentToolProviderSrv.Iface client;

    @Before
    public void setup() throws URISyntaxException {
        client = new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:" + port + "/provider/samsung")).build(PaymentToolProviderSrv.Iface.class);
    }

    @Test
    public void testUnwrapPaymentTool() throws Exception {
        String serviceId = "bb12a068ebca4916a7c3c1";
        String referenceId = "abf930ba74fe4231b2e2cd";

        when(spKeyStore.getKey(eq(serviceId)))
                .thenReturn(SPKeyStore.getPrivateKey(IOUtils.toString(new ClassPathResource("stg-privkey.pem").getInputStream(), StandardCharsets.UTF_8)));
        when(sPayClient.requestCredentials(eq(serviceId), eq(referenceId)))
                .thenReturn("{\"resultCode\":\"0\",\"resultMessage\":\"SUCCESS\",\"method\":\"3DS\",\"card_brand\":\"MC\",\"card_last4digits\":\"4006\",\"3DS\":{\"type\":\"S\",\"version\":\"100\",\"data\":\"eyJhbGciOiJSU0ExXzUiLCJraWQiOiJzNEZXYjlrckdkQ1A2aVdxVmRCU3hqZVlsWHpqQVMvMjR3cWkxUmY2QWFBPSIsInR5cCI6IkpPU0UiLCJjaGFubmVsU2VjdXJpdHlDb250ZXh0IjoiUlNBX1BLSSIsImVuYyI6IkExMjhHQ00ifQ.dT5occmb4pWPTz0j8d0xMhjqII41cZ0sitjusZ1HB_hIdBi8ipI6wascn2QQU_fGEIx2UGnvIfLrORoJRKxZ6q12hDHaoPCeHAFi_Xp8R17frpbfrbtAhK-A543xIXHaGHm4uATQrWsM18w393P7XYwC8_37SPBobk_hH0QROWuT1A5ufFdpbRa2ZtWuNolseI9ZV4EA75zXP9V_twyBylwuX4-QIyoonMKx71AZp7k1rU1dXet3N0JXAG1v_HVk3zSzj_SQplhf1KGavDPC1Hgq5HFd1oZH8pDSQgDbmR2xVJkuv5baLOGhHPaXTBuU7JPNJlZEm92o604ARo8y1Q.WqlrrRZB7AtFibbz.pOEmRM2K3HUdNFF_6tb4PyhPgXybhj9hh8RcwVjGLcP31w0UKYYDnDdi5PdU8GbL9Aq9m2GVVK2TkG58P4Xn6Irus5jlR-RogFdZCZ6gTVlI_FTk5Kff2BoaptpUo47fUMgmBUuZMEN8vc-JvWnT6ueegKb1fsFbVxcTX7jkhRzzQMXCzhrN11EYtF5xK85mKZWJFB5Rpo5QB9kspJwVNTA_D6NAPDKaMFUm2Jz8d6jTw_gYuov6.HQ3crm4BZQFtsOUq0_NdIw\"},\"wallet_dm_id\":\"qlqdB4vVDcJkK7TPvhaRH88TpEVKj2F439lv5rgrNnM=\"}");

        UnwrappedPaymentTool unwrappedPaymentTool = client.unwrap(new WrappedPaymentTool(PaymentRequest.samsung(new SamsungPayRequest(serviceId, referenceId))));
        assertEquals(BankCardPaymentSystem.mastercard, unwrappedPaymentTool.getCardInfo().getPaymentSystem());
        assertEquals("4006", unwrappedPaymentTool.getCardInfo().getLast4Digits());
        assertEquals("5185731540006869", unwrappedPaymentTool.getPaymentData().getTokenizedCard().getDpan());
        assertEquals(new ExpDate((byte) 8, (short) 2021), unwrappedPaymentTool.getPaymentData().getTokenizedCard().getExpDate());
        assertEquals("AGsawxdVZiL5ACMPDVwSAoACFA==", unwrappedPaymentTool.getPaymentData().getTokenizedCard().getAuthData().getAuth3ds().getCryptogram());
        assertEquals("5", unwrappedPaymentTool.getPaymentData().getTokenizedCard().getAuthData().getAuth3ds().getEci());
        assertEquals("qlqdB4vVDcJkK7TPvhaRH88TpEVKj2F439lv5rgrNnM=", unwrappedPaymentTool.getDetails().getSamsung().getDeviceId());

        assertNull(unwrappedPaymentTool.getCardInfo().getCardholderName());
        assertNull(unwrappedPaymentTool.getCardInfo().getDisplayName());
        assertNull(unwrappedPaymentTool.getCardInfo().getCardClass());
    }

}
