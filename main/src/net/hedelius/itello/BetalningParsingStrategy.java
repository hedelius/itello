package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of <code>ParsingStrategy</code> that handles files
 * that conform to specification in the document Betalningsservice.doc.
 */
public class BetalningParsingStrategy implements ParsingStrategy {

    private static final String CHARSET = "ISO8859-1";
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void handle(InputStream input, PaymentReceiver paymentReceiver) throws PaymentException {

        int expectedPaymentLineCount = 0;
        int actualPaymentLineCount = 0;
        BigDecimal actualPaymmentLineAmountTotal = new BigDecimal(0);
        BigDecimal expectedPaymentAmountTotal = new BigDecimal(0);

        try {

            String theOrderLine = null;

            String currentLine;
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(CHARSET)));
            while ((currentLine = reader.readLine()) != null) {

                // order line?
                if (currentLine.startsWith("O")) {

                    if (theOrderLine != null) {
                        throw new PaymentException("Multiple order lines!");
                    }

                    String accountNumber = currentLine.substring( 1, 16);
                    String sum           = currentLine.substring(16, 30);
                    String count         = currentLine.substring(30, 40);
                    String dateString    = currentLine.substring(40, 48);
                    String currency      = currentLine.substring(48, 51);

                    theOrderLine = currentLine;
                    expectedPaymentAmountTotal = bigDecimalFromString(sum);
                    expectedPaymentLineCount = Integer.parseInt(count.trim());

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
                } else {

                    if (theOrderLine == null) {
                        throw new PaymentException("First line is not an order line!");
                    }

                    // payment line?
                    if (currentLine.startsWith("B")) {

                        BigDecimal amount = bigDecimalFromString(currentLine.substring(1, 15));
                        actualPaymmentLineAmountTotal = actualPaymmentLineAmountTotal.add(amount);
                        ++actualPaymentLineCount;

                        paymentReceiver.payment(amount, currentLine.substring(15, 50).trim());

                    } else {
                        throw new PaymentException("Unknown line type!");
                    }
                }
            }

        } catch (IOException e) {
            throw new PaymentException("Failed to read file!");
        }

        if (actualPaymentLineCount != expectedPaymentLineCount) {
            throw new PaymentException("Incorrect number of payment records compared to header!");
        }

        if (!actualPaymmentLineAmountTotal.equals(expectedPaymentAmountTotal)) {
            throw new PaymentException("Incorrect sum of payments compared to header!");
        }

        paymentReceiver.endPaymentBundle();
    }

    private BigDecimal bigDecimalFromString(String s) {
        return new BigDecimal(s.trim().replace(',', '.'));
    }
}
