package net.hedelius.itello;

/**
 * Created by MHES on 2016-11-22.
 */
public interface PaymentFileHandler {

    void handleFile(String fileName) throws PaymentException;
}
