package net.hedelius.itello;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import se.itello.example.payments.PaymentReceiver;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;

public class ItelloTest {

    private IPaymentFileHandler sut;

    @Before
    public void setup() {
        sut = new PaymentFileHandler();
    }

    @Test
    public void testMocking() {

        // arrange
        String accountNumber = "account-number";
        Date now = Date.from(Instant.now());
        String currency = "SEK";
        PaymentReceiver paymentReceiver = mock(PaymentReceiver.class);

        // act
        paymentReceiver.startPaymentBundle(accountNumber, now, currency);

        // assert
        verify(paymentReceiver).startPaymentBundle(anyString(), any(Date.class), anyString());
    }

    @Test
    public void testStreams() {

        Stream.of(1, 2, 3, 4, 5, 6, 7).map(x -> x + 1).forEach(x -> System.out.println(x));
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

        // assert - should not throw
    }

    @Test
    public void testInbetalningFileName() {

        // arrange
        String paymentServiceFile = "A_legitimate_file_inbetalningstjansten.txt";

        // act
        sut.handleFile(paymentServiceFile);

        // assert - should not throw
    }
}
