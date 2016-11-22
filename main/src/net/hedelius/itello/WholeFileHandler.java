package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.stream.Stream;

public class WholeFileHandler implements IPaymentFileHandler {

    private static final String CHARSET = "ISO8859-1";
    private final PaymentReceiver paymentReceiver;
    private final FileReader fileReader;
    private String[] supportedFileNameEndings =
            new String[] {"_betalningsservice.txt", "_inbetalningstjansten.txt"};
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public WholeFileHandler(FileReader fileReader, PaymentReceiver paymentReceiver) {
        this.fileReader = fileReader;
        this.paymentReceiver = paymentReceiver;
    }

    @Override
    public void handleFile(String fileName) throws PaymentException {

        if (Arrays.stream(supportedFileNameEndings).noneMatch(i -> fileName.endsWith(i)))
            throw new IllegalArgumentException("Unsupported file type!");

        // TODO handle both types
        // and then, handle any type

        byte[] fileContentBytes = fileReader.read(fileName);
        String fileContent = null;
        try {
            fileContent = new String(fileContentBytes, CHARSET);
        } catch (UnsupportedEncodingException e) {
            // not going to happen
        }

        String[] lines = fileContent.split("\\r\\n");
        String orderLine = lines[0];

        if (!orderLine.startsWith("O")) {
            throw new PaymentException("Incorrect file format, multiple header lines!");
        }

        String accountNumber = orderLine.substring( 1, 16);
        String sum           = orderLine.substring(16, 30);
        String count         = orderLine.substring(30, 40);
        String dateString    = orderLine.substring(40, 48);
        String currency      = orderLine.substring(48, 51);

        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new PaymentException(e);
        }

        paymentReceiver.startPaymentBundle(
                accountNumber, // TODO good enough?
                date,
                currency);
    }
}
