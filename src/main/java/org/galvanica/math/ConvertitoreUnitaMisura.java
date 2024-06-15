package org.galvanica.math;

public class ConvertitoreUnitaMisura {
    public Integer convertiQuantitaToDatabase(Double misura,
                                              UnitaDiMisura unitaDiMisuraFROM) {
        if (unitaDiMisuraFROM == UnitaDiMisura.MG || unitaDiMisuraFROM == UnitaDiMisura.ML) {
            return misura.intValue();
        }
        if (unitaDiMisuraFROM == UnitaDiMisura.KG) {
            double misuraCorretta = misura * 1000000;
            return (int) misuraCorretta;
        } else {
            double misuraCorretta = misura * 1000;
            return (int) misuraCorretta;
        }
    }


    public Double convertiQuantitaGenerico(Double misura,
                                           UnitaDiMisura unitaDiMisuraFROM,
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

    public UnitaDiMisura convertiUnitaMisuraPerDto(Integer misura,
                                                   boolean sonoVolume) {
        if (sonoVolume) {
            if (misura < 999) {
                return UnitaDiMisura.ML;
            } else return UnitaDiMisura.L;
        }
        if (misura < 999) {
            return UnitaDiMisura.MG;
        }
        if (misura < 999999) {
            return UnitaDiMisura.G;
        }
        return UnitaDiMisura.KG;
    }

}
