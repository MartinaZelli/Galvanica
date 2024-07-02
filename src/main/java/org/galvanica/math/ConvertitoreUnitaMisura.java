package org.galvanica.math;

public class ConvertitoreUnitaMisura {


    public static Integer convertiQuantitaToDatabase(Double misura,
                                                     UnitaDiMisura originalUnit) {
        double convertedValue = switch (originalUnit) {
            case MG, ML -> misura;
            case KG -> misura * 1000000;
            default -> misura * 1000;
        };

        if (convertedValue > Integer.MAX_VALUE) {
            throw new ArithmeticException(
                    "Overflow: Il valore convertito supera la capacità massima di un intero.");
        }
        return (int) convertedValue;
    }

    public static Double convertiQuantitaGenerico(double misura,
                                                  UnitaDiMisura originalUnit,
                                                  UnitaDiMisura targetUnit) {

        if (!originalUnit.isSonoVolume() == targetUnit.isSonoVolume()) {
            throw new IllegalArgumentException(
                    "Impossibile convertire volume in peso e viceversa.");
        }

        double convertedValue = misura;

        switch (originalUnit) {
            case L -> {
                if (targetUnit == UnitaDiMisura.ML) {
                    convertedValue *= 1000;
                }
            }
            case ML -> {
                if (targetUnit == UnitaDiMisura.L) {
                    convertedValue /= 1000;
                }
            }
            case G -> convertedValue = switch (targetUnit) {
                case MG -> misura * 1000;
                case KG -> misura / 1000;
                default -> misura;
            };
            case MG -> convertedValue = switch (targetUnit) {
                case G -> misura / 1000;
                case KG -> misura / 1000000;
                default -> misura;
            };
            case KG -> convertedValue = switch (targetUnit) {
                case MG -> misura * 1000000;
                case G -> misura * 1000;
                default -> misura;
            };
            default -> throw new IllegalArgumentException(
                    "Unità di misura non supportata: " + originalUnit);
        }

        if (convertedValue > Integer.MAX_VALUE) {
            throw new ArithmeticException(
                    "Overflow: Il valore convertito supera la capacità massima di un intero.");
        }
        return convertedValue;
    }

    public static UnitaDiMisura convertiUnitaMisuraPerDto(Integer misura,
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

    public static Double convertiQuantitaPerDto(Integer quantita,
                                                Boolean sonoVolume) {
        if (sonoVolume) {
            if (quantita < 999) {
                return quantita.doubleValue();
            }
            return quantita.doubleValue() / 1000;
        }
        if (quantita < 999) {
            return quantita.doubleValue();
        }
        if (quantita < 999999) {
            return quantita.doubleValue() / 1000;
        }
        return quantita.doubleValue() / 1000000;
    }


}
