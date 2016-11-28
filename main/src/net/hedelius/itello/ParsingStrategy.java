package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;
import java.io.InputStream;

public interface ParsingStrategy {

    void handle(InputStream input, PaymentReceiver paymentReceiver) throws PaymentException;
}
