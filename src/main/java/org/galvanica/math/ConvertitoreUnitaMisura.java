package org.galvanica.math;

public class ConvertitoreUnitaMisura {
    public Double convertiMisura(double misura, UnitaDiMisura unitaDiMisuraFROM,
                                 UnitaDiMisura unitaDiMisuraTO) {
        if (unitaDiMisuraFROM.isSonoVolume() != unitaDiMisuraTO.isSonoVolume()) {
            throw new RuntimeException(
                    "non si trasforma il volume in peso e viceversa, la matematica non è un opinione.");
        }
        if (unitaDiMisuraFROM == unitaDiMisuraTO) {
            return misura;
        }
        if (unitaDiMisuraFROM.isSonoVolume()) {
            if (unitaDiMisuraFROM == UnitaDiMisura.L && unitaDiMisuraTO == UnitaDiMisura.ML) {
                return misura / 1000;
            }
            return misura * 1000;
        }
        if ((unitaDiMisuraFROM == UnitaDiMisura.KG && unitaDiMisuraTO == UnitaDiMisura.G) ||
                (unitaDiMisuraFROM == UnitaDiMisura.G && unitaDiMisuraTO == UnitaDiMisura.MG)) {
            return misura / 1000;
        }
        if ((unitaDiMisuraFROM == UnitaDiMisura.MG && unitaDiMisuraTO == UnitaDiMisura.G) ||
                (unitaDiMisuraFROM == UnitaDiMisura.G && unitaDiMisuraTO == UnitaDiMisura.KG)) {
            return misura * 1000;
        }
        if (unitaDiMisuraFROM == UnitaDiMisura.MG && unitaDiMisuraTO == UnitaDiMisura.KG) {
            return misura * 1000000;
        }
        //if (unitaDiMisuraFROM == UnitaDiMisura.KG && unitaDiMisuraTO == UnitaDiMisura.MG) {
        return misura / 1000000;
    }
}
