package org.galvanica.math;

import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.List;


public class DayOfWeekConverter implements Converter<String, List<String>> {

    @Override
    public List<String> convert(String source) {
        return Arrays.asList(source.split(","));
    }

    /*
    i numeri della settimana saranno:
    0 domenica
    1 lunedì
    2 martedì
    3 mercoledì
    4 giovedì
    5 venerdì
    6 sabato

     */
}
