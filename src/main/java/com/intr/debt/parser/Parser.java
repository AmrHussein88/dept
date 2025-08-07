package com.intr.debt.parser;

import com.intr.debt.dto.PayoutDto;

import java.io.File;
import java.util.List;

public interface Parser {
    static final String SEMI_COLON_DELIMITER = ";";
    static final String ISO_ENCODING = "ISO-8859-1";
    List<PayoutDto> parseFile (File file);

}
