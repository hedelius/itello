package net.hedelius.itello;

import java.io.InputStream;

public interface FileFinderService {

    InputStream find(String fileName);
}
