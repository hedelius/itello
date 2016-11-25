package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class InbetalningPaymentStrategy implements PaymentStrategy {

    private static final String CHARSET = "ISO8859-1";
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void handle(InputStream input, PaymentReceiver paymentReceiver) throws PaymentException {

        // TODO dpn't read whole file!
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(CHARSET)));
        String[] lines = reader.lines().toArray(x -> new String[x]);

        String header = Arrays.stream(lines).filter(x -> x.startsWith("00")).findFirst().get();
        String accountNumber = header.substring(10, 14) + '-' + header.substring(14, 24);
        paymentReceiver.startPaymentBundle(accountNumber, new Date(), "SEK");

        Arrays.stream(lines).
                filter(x -> x.startsWith("30")).
                forEach(x -> {
                    BigDecimal amount = bigDecimalFromString(x.substring(2, 22));
                    String reference = x.substring(40, 65).trim();
                    paymentReceiver.payment(amount, reference);
                });

        paymentReceiver.endPaymentBundle();
    }

    private BigDecimal bigDecimalFromString(String s) {
        return new BigDecimal(s.trim().replace(',', '.'));
    }
}
