package net.hedelius.itello;

import static junit.framework.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;
import se.itello.example.payments.PaymentReceiver;

public class ItelloTest {

    private IPaymentFileHandler sut;
    private PaymentReceiver paymentReceiver;

    @Before
    public void setup() {

        paymentReceiver = mock(PaymentReceiver.class);
        sut = new PaymentFileHandler(paymentReceiver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownFileType() {

        // arrange
        String unknownFile = "Some_random_file_unsupported.txt";

        // act
        sut.handleFile(unknownFile);

        // assert - should not get here
        fail();
    }

    @Test
    public void testBetalningFileName() {

        // arrange
        String paymentServiceFile = "A_legitimate_file_betalningsservice.txt";

        // act
        sut.handleFile(paymentServiceFile);

        // assert
        verify(paymentReceiver).startPaymentBundle(anyString(), any(Date.class), anyString());
    }

    @Test
    public void testInbetalningFileName() {

        // arrange
        String paymentServiceFile = "A_legitimate_file_inbetalningstjansten.txt";

        // act
        sut.handleFile(paymentServiceFile);

        // assert
        verify(paymentReceiver).startPaymentBundle(anyString(), any(Date.class), anyString());
    }
}
