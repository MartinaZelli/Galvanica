package org.galvanica.math;

import lombok.Getter;

@Getter
public enum UnitaDiMisura {

    ML(true, "Millilitri"),
    L(true, "Litri"),
    MG(false, "Milligrammi"),
    G(false, "Grammi"),
    KG(false, "Chilogrammi");

    private final boolean sonoVolume;
    
    private final String descrizione;

    UnitaDiMisura(boolean sonoVolume, String descrizione) {
        this.sonoVolume = sonoVolume;
        this.descrizione = descrizione;
    }

}
