package net.hedelius.itello;

import java.util.Arrays;

/**
 * Created by MHES on 2016-11-22.
 */
public class PaymentFileHandler implements IPaymentFileHandler {

    private String[] supportedFileNameEndings =
            new String[] {"_betalningsservice.txt", "inbetalningstjansten.txt"};

    @Override
    public void handleFile(String fileName) {

        if (Arrays.stream(supportedFileNameEndings).noneMatch(i -> fileName.endsWith(i)))
            throw new IllegalArgumentException("");
    }

}
