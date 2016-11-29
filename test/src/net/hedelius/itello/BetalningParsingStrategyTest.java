package net.hedelius.itello;

import static junit.framework.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import org.mockito.InOrder;
import se.itello.example.payments.PaymentReceiver;
import javax.xml.bind.DatatypeConverter;

public class BetalningParsingStrategyTest {

    private BetalningParsingStrategy sut;
    private PaymentReceiver paymentReceiver;

    @Before
    public void setup() {

        paymentReceiver = mock(PaymentReceiver.class);
        sut = new BetalningParsingStrategy();
    }

    @Test(expected = PaymentException.class)
    public void testOfficialTestData()
    {
        // arrange
        String testDataHex =
                        "4f35353535203535353535353535353520202020202020343731312c3137" +
                        "20202020202020202034323031313033313553454b0d0a42202020202020" +
                        "202020203330303031323334353637383930202020202020202020202020" +
                        "202020202020202020202020200d0a422020202020202020202031303030" +
                        "323334353637383930312020202020202020202020202020202020202020" +
                        "20202020200d0a4220202020202020203330302c31303334353637383930" +
                        "3132202020202020202020202020202020202020202020202020200d0a42" +
                        "20202020202020203430302c303734353637383930313233202020202020" +
                        "202020202020202020202020202020202020200d0a";
        byte[] testDataBytes = DatatypeConverter.parseHexBinary(testDataHex);
        InputStream testDataStream = new ByteArrayInputStream(testDataBytes);

        // act
        sut.handle(testDataStream, paymentReceiver);

        // assert - throws, should not get here
        fail();
    }

    @Test
    public void testOfficialTestDataCorrected()
    {
        // arrange
        String testDataHex =
                "4f35353535203535353535353535353520202020202020343730302c3137" +
             // "4f35353535203535353535353535353520202020202020343731312c3137" + // not correct!
                "20202020202020202034323031313033313553454b0d0a42202020202020" +
                "202020203330303031323334353637383930202020202020202020202020" +
                "202020202020202020202020200d0a422020202020202020202031303030" +
                "323334353637383930312020202020202020202020202020202020202020" +
                "20202020200d0a4220202020202020203330302c31303334353637383930" +
                "3132202020202020202020202020202020202020202020202020200d0a42" +
                "20202020202020203430302c303734353637383930313233202020202020" +
                "202020202020202020202020202020202020200d0a";
        byte[] testDataBytes = DatatypeConverter.parseHexBinary(testDataHex);
        InputStream testDataStream = new ByteArrayInputStream(testDataBytes);

        // act
        sut.handle(testDataStream, paymentReceiver);

        // assert
        String expectedAccount = "5555-5555555555";
        Date expectedDate = new GregorianCalendar(2011, 2, 15).getTime();
        String expectedCurrency = "SEK";
        InOrder inOrder = inOrder(paymentReceiver);
        inOrder.verify(paymentReceiver).startPaymentBundle(expectedAccount, expectedDate, expectedCurrency);
        inOrder.verify(paymentReceiver).payment(new BigDecimal("3000"), "1234567890");
        inOrder.verify(paymentReceiver).payment(new BigDecimal("1000"), "2345678901");
        inOrder.verify(paymentReceiver).payment(new BigDecimal("300.10"), "3456789012");
        inOrder.verify(paymentReceiver).payment(new BigDecimal("400.07"), "4567890123");
        inOrder.verify(paymentReceiver).endPaymentBundle();
    }

    @Test(expected = PaymentException.class)
    public void testMultipleHeaderLines()
    {
        // arrange
        String testDataHex =
            "4f3535353520353535353535353535352020202020202020202020202030" +
            "20202020202020202031323031313033313553454b0d0a" +
            "4f35353535203535353535353535353520202020202020343730302c3137" +
            "20202020202020202034323031313033313553454b0d0a" +
            "4220202020202020202020303030303031323334353637383930202020202020202020202020202020202020202020202020200d0a";
        byte[] testDataBytes = DatatypeConverter.parseHexBinary(testDataHex);
        InputStream testDataStream = new ByteArrayInputStream(testDataBytes);

        // act
        sut.handle(testDataStream, paymentReceiver);

        // assert - should throw and not get here
        fail();
    }
}
