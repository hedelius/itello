package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of <code>PaymentStrategy</code> that handles files
 * that conform to specification in the document Betalningsservice.doc.
 * Do not use this class for large files - it reads the entire file into memory
 * before processing it!
 */
public class BetalningPaymentStrategy implements PaymentStrategy {

    private static final String CHARSET = "ISO8859-1";
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void handle(InputStream input, PaymentReceiver paymentReceiver) throws PaymentException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(CHARSET)));
        String[] lines = reader.lines().toArray(x -> new String[x]);

        String orderLine = lines[0];

        if (!orderLine.startsWith("O")) {
            throw new PaymentException("First line is not a header line!");
        }

        String accountNumber = orderLine.substring( 1, 16);
        String sum           = orderLine.substring(16, 30);
        String count         = orderLine.substring(30, 40);
        String dateString    = orderLine.substring(40, 48);
        String currency      = orderLine.substring(48, 51);

        Date date;
        try {
            date = DATEFORMAT.parse(dateString);
        } catch (ParseException e) {
            throw new PaymentException(e);
        }

        paymentReceiver.startPaymentBundle(
                accountNumber.trim().replace(' ', '-'),
                date,
                currency);

        List<String> payments =
                Arrays.stream(lines, 1, lines.length).filter(x -> x.startsWith("B")).collect(Collectors.toList());

        if (payments.size() != Integer.parseInt(count.trim())) {
            throw new PaymentException("Incorrect number of payment records compared to header!");
        }

        BigDecimal recordSum = payments.stream().
                map(x -> bigDecimalFromString(x.substring(1, 15))).
                reduce((x, y) -> x.add(y)).get();

        if (!recordSum.equals(bigDecimalFromString(sum))){
            throw new PaymentException("Incorrect sum of payments compared to header!");
        }

        payments.forEach(x -> {
            BigDecimal amount = bigDecimalFromString(x.substring(1, 15));
            paymentReceiver.payment(amount, x.substring(15, 50).trim());
        });

        paymentReceiver.endPaymentBundle();
    }

    private BigDecimal bigDecimalFromString(String s) {
        return new BigDecimal(s.trim().replace(',', '.'));
    }
}
