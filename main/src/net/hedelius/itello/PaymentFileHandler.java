package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PaymentFileHandler {

    private static final String BETALNING = "_betalningsservice.txt";
    private static final String INBETALNING = "_inbetalningstjansten.txt";

    private final PaymentReceiver paymentReceiver;
    private final Map<String, ParsingStrategy> strategies;

    /**
     * Constructs a file handler given a payment receiver.
     * @param paymentReceiver The Itello payment receiver service.
     */
    public PaymentFileHandler(PaymentReceiver paymentReceiver) {

        this.paymentReceiver = paymentReceiver;
        strategies = new HashMap<>();
        strategies.put(BETALNING, new BetalningPaymentStrategy());
        strategies.put(INBETALNING, new InbetalningPaymentStrategy());
    }

    /**
     * Handle a payments file by delegating to the appropriate <code>ParsingStrategy</code>
     * @param fileName The name of the file, which indicates the format .
     * @param stream The file contents as a byte stream.
     * @throws PaymentException
     */
    public void handleFile(String fileName, InputStream stream) throws PaymentException {

        String fileNameEnding = fileName.substring(fileName.lastIndexOf("_"));
        ParsingStrategy parsingStrategy = strategies.get(fileNameEnding);
        if (parsingStrategy == null)
            throw new IllegalArgumentException("Unsupported file type!");
        parsingStrategy.handle(stream, paymentReceiver);
    }
}
