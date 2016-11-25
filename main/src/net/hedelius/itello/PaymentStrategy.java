package net.hedelius.itello;

import se.itello.example.payments.PaymentReceiver;
import java.io.InputStream;

public interface PaymentStrategy {

    void handle(InputStream input, PaymentReceiver paymentReceiver) throws PaymentException;
}
