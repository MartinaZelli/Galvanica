package org.galvanica.math;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class DayOfWeekConverter implements AttributeConverter<List<DayOfWeek>, String> {

    @Override
    public String convertToDatabaseColumn(List<DayOfWeek> giorniDellaSettimana) {
        if (giorniDellaSettimana == null) return null;
        return giorniDellaSettimana.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<DayOfWeek> convertToEntityAttribute(String source) {
        if (source == null) return null;
        return Arrays.stream(source.split(","))
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toList());
    }

}
