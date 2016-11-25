package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class PaymentFileHandler {

    private static final String BETALNING = "_betalningsservice.txt";
    private static final String INBETALNING = "_inbetalningstjansten.txt";

    private final PaymentReceiver paymentReceiver;
    private final FileFinderService fileReader;
    private final Map<String, PaymentStrategy> strategies;

    /**
     * Constructs an object with the default payment handler strategies.
     * @param fileReader The I/O service to find files from.
     * @param paymentReceiver The Itello payment receiver service.
     */
    public PaymentFileHandler(FileFinderService fileReader, PaymentReceiver paymentReceiver) {

        this.fileReader = fileReader;
        this.paymentReceiver = paymentReceiver;

        strategies = new HashMap<>();
        strategies.put(BETALNING, new BetalningPaymentStrategy());
        strategies.put(INBETALNING, new InbetalningPaymentStrategy());
    }

    /**
     * Constructs an object with a custom set of payment strategies.
     * @param fileReader The I/O service to find files from.
     * @param paymentReceiver The Itello payment receiver service.
     * @param strategies The mapping from file name ending to content handling strategy.
     */
    public PaymentFileHandler(FileFinderService fileReader,
                              PaymentReceiver paymentReceiver,
                              Map<String, PaymentStrategy> strategies) {

        this.fileReader = fileReader;
        this.paymentReceiver = paymentReceiver;
        this.strategies = strategies;
    }

    /**
     * Handle a payments file by invoking <code>PaymentReceiver</code>
     * for the records in the file.
     * @param fileName The name of the file handle.
     * @throws PaymentException
     */
    public void handleFile(String fileName) throws PaymentException {

        String fileNameEnding = fileName.substring(fileName.lastIndexOf("_"));
        PaymentStrategy strategy = strategies.get(fileNameEnding);
        if (strategy == null)
            throw new IllegalArgumentException("Unsupported file type!");

        InputStream stream = fileReader.find(fileName);
        strategy.handle(stream, paymentReceiver);
    }
}
