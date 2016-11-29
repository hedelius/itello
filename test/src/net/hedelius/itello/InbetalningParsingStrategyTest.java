package net.hedelius.itello;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import se.itello.example.payments.PaymentReceiver;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

public class InbetalningParsingStrategyTest {

    private InbetalningParsingStrategy sut;
    private PaymentReceiver paymentReceiver;

    @Before
    public void setup() {

        paymentReceiver = mock(PaymentReceiver.class);
        sut = new InbetalningParsingStrategy();
    }

    @Test
    public void testInbetalningOfficialTestData() {

        // arrange
        String testDataHex =
                "303030303030303030303132333431323334353637383937303030303030" +
                "303030303030303030303030303030303030303030303030303030303030" +
                "30303030303030303030303030303030303030300d0a3330303030303030" +
                "303030303030303034303030303030303030303030303030303030303030" +
                "303039383736353433323130202020202020202020202020202020202020" +
                "2020202020202020202020200d0a33303030303030303030303030303030" +
                "313030303030303030303030303030303030303030303030393837363534" +
                "333231302020202020202020202020202020202020202020202020202020" +
                "202020200d0a333030303030303030303030303030313033303030303030" +
                "303030303030303030303030303030303938373635343332313020202020" +
                "20202020202020202020202020202020202020202020202020200d0a3939" +
                "303030303030303030303030303135333030303030303030303030303030" +
                "303030303033303030303030303030303030303030303030303030303030" +
                "3030303030303030303030303030303030300d0a";
        byte[] testDataBytes = DatatypeConverter.parseHexBinary(testDataHex);
        InputStream testDataStream = new ByteArrayInputStream(testDataBytes);

        // act
        sut.handle(testDataStream, paymentReceiver);

        // assert
        String expectedAccount = "1234-1234567897";
        String expectedCurrency = "SEK";
        InOrder inOrder = inOrder(paymentReceiver);
        inOrder.verify(paymentReceiver).startPaymentBundle(eq(expectedAccount), any(Date.class), eq(expectedCurrency));
        inOrder.verify(paymentReceiver).payment(new BigDecimal("400000"), "9876543210");
        inOrder.verify(paymentReceiver).payment(new BigDecimal("100000"), "9876543210");
        inOrder.verify(paymentReceiver).payment(new BigDecimal("1030000"), "9876543210");
        inOrder.verify(paymentReceiver).endPaymentBundle();
    }
}
