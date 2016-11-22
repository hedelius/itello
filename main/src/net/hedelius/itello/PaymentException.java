package net.hedelius.itello;

/**
 * Created by MHES on 2016-11-22.
 */
public class PaymentException extends RuntimeException {
    PaymentException(String message) {
        super(message);
    }
    PaymentException(Exception e) {
        super(e);
    }
}
