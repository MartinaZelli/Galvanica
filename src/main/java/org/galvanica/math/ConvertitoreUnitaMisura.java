package org.galvanica.math;

public class ConvertitoreUnitaMisura {
    public Double convertiMisura(double misura, UnitaDiMisura unitaDiMisuraFROM,
                                 UnitaDiMisura unitaDiMisuraTO) {
        if (unitaDiMisuraFROM.isSonoVolume() != unitaDiMisuraTO.isSonoVolume()) {
            throw new RuntimeException(
                    "non si trasforma il volume in peso e viceversa, coglione.");
        }
        if (unitaDiMisuraFROM == UnitaDiMisura.ML || unitaDiMisuraFROM == UnitaDiMisura.G) {
            return misura / 1000;
        }
        return misura * 1000;
    }
}
