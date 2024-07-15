package org.galvanica.math;

import lombok.Getter;

@Getter
public enum UnitaDiMisura {

	ML(true), L(true), MG(false), G(false), KG(false);

	private final boolean sonoVolume;

	UnitaDiMisura(boolean sonoVolume) {
		this.sonoVolume = sonoVolume;
	}

}
