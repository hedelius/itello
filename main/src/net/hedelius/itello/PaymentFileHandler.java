package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;

import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;

/**
 * Created by MHES on 2016-11-22.
 */
public class PaymentFileHandler implements IPaymentFileHandler {

    private final PaymentReceiver paymentReceiver;
    private final FileReader fileReader;
    private String[] supportedFileNameEndings =
            new String[] {"_betalningsservice.txt", "_inbetalningstjansten.txt"};

    public PaymentFileHandler(FileReader fileReader, PaymentReceiver paymentReceiver) {
        this.fileReader = fileReader;
        this.paymentReceiver = paymentReceiver;
    }

    @Override
    public void handleFile(String fileName) {

        if (Arrays.stream(supportedFileNameEndings).noneMatch(i -> fileName.endsWith(i)))
            throw new IllegalArgumentException("");

        byte[] fileContents = fileReader.read(fileName);
        paymentReceiver.startPaymentBundle("foo", Date.from(Instant.now()), "bar");
    }
}
