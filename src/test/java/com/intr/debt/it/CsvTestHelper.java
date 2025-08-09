package com.intr.debt.it;

import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CsvTestHelper {
    private CsvTestHelper() {}

    public static Path writeWakandaCsv(Path dir, String yyyymmdd, String hhmmss, String... rows) throws Exception {
        Path file = dir.resolve("WK_payouts_" + yyyymmdd + "_" + hhmmss + ".csv");
        try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(file), Charset.forName("ISO-8859-1"))) {
            w.write("\"Company name\";\"Company tax number\";\"Status\";\"Payment Date\";\"Amount\"\r\n");
            for (String r : rows) {
                w.write(r);
                if (!r.endsWith("\r\n")) w.write("\r\n");
            }
        }
        return file;
    }

    public static String row(String name, String tax, String status, String date, String amount) {
        return "\"" + name + "\";\"" + tax + "\";\"" + status + "\";\"" + date + "\";\"" + amount + "\"";
    }
}
