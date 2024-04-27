package org.galvanica.service.mathService;

import org.galvanica.math.UnitaDiMisura;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

@Service
public class MathService {
    private List<DayOfWeek> dayOfWeekList() {
        return Arrays.stream(DayOfWeek.values()).toList();
    }

    private List<UnitaDiMisura> unitaDiMisuraList() {
        return Arrays.stream(UnitaDiMisura.values()).toList();
    }
}
