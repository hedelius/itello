package net.hedelius.itello;

import org.junit.Before;
import org.junit.Test;
import se.itello.example.payments.PaymentReceiver;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

public class PaymentFileHandlerTest {

    private PaymentFileHandler sut;
    private ParsingStrategy betalningParsingStrategy;
    private ParsingStrategy inbetalningParsingStrategy;
    private PaymentReceiver paymentReceiver;

    @Before
    public void setup() {

        betalningParsingStrategy = mock(ParsingStrategy.class);
        inbetalningParsingStrategy = mock(ParsingStrategy.class);
        Map<String, ParsingStrategy> fileNameMapping = new HashMap<>();
        fileNameMapping.put("_betalningsservice.txt", betalningParsingStrategy);
        fileNameMapping.put("_inbetalningstjansten.txt", inbetalningParsingStrategy);
        sut = new PaymentFileHandler(fileNameMapping, paymentReceiver);
    }

    @Test(expected = PaymentException.class)
    public void testUnknownFileType() {

        // arrange
        String unknownFile = "Some_random_file_unsupported.txt";

        // act
        sut.handleFile(unknownFile, null);

        // assert - should not get here
        fail();
    }

    @Test
    public void testBetalningFileType()
    {
        // arrange
        String fileName = "foobar_betalningsservice.txt";
        InputStream stream = mock(InputStream.class);

        // act
        sut.handleFile(fileName, stream);

        // assert
        verify(betalningParsingStrategy).handle(stream, paymentReceiver);
        verifyZeroInteractions(inbetalningParsingStrategy);
    }

    @Test
    public void testInbetalningFileType()
    {
        // arrange
        String fileName = "foobar_inbetalningstjansten.txt";
        InputStream stream = mock(InputStream.class);

        // act
        sut.handleFile(fileName, stream);

        // assert
        verify(inbetalningParsingStrategy).handle(stream, paymentReceiver);
        verifyZeroInteractions(betalningParsingStrategy);
    }
}
