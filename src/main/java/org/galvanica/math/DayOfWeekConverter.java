package org.galvanica.math;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


enum GiorniDellaSettimana {
    LUNEDI,
    MARREDI,
    MERCOLEDI,
    GIOVEDI,
    VENERDI,
    SABATO,
    DOMENICA
}

@Component
public class DayOfWeekConverter implements AttributeConverter<List<GiorniDellaSettimana>, String> {

    @Override
    public String convertToDatabaseColumn(List<GiorniDellaSettimana> giorniDellaSettimana) {
        return giorniDellaSettimana.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<GiorniDellaSettimana> convertToEntityAttribute(String source) {
        return Arrays.stream(source.split(","))
                .map(GiorniDellaSettimana::valueOf)
                .collect(Collectors.toList());
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
