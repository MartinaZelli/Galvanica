package org.galvanica.Math;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class ScattiMath {
    private int restoScatti;
    private double moltiplicatoreAlimentazione;

    //metodo per arrangiare a 1/1.5/2/2.5etcetc alimentazioni senza cifre. riporta
    private ScattiMath alimentazioneScattiApprossimata(int scattiAttuali,
                                                       int scattiAlimentazione) {
        ScattiMath risposta = new ScattiMath();
        double moltiplicatoreReale = (double) scattiAttuali / scattiAlimentazione;
        //Math.floor approssima per difetto o per eccesso il numero del moltiplicatore.
        double moltiplicatoreFloor = Math.floor(moltiplicatoreReale);
        System.out.println(moltiplicatoreFloor);
        double cifreDecimali = moltiplicatoreReale - moltiplicatoreFloor;
        double moltiplicatoreApprossimato = 0;
        double restoMoltiplicatore;
        if (cifreDecimali < 0.4) {
            moltiplicatoreApprossimato = moltiplicatoreFloor;
        }
        if (cifreDecimali <= 0.9 && cifreDecimali >= 0.4) {
            moltiplicatoreApprossimato = moltiplicatoreFloor + 0.5;
        }
        if (cifreDecimali > 0.9) {
            moltiplicatoreApprossimato = moltiplicatoreFloor + 1;
        }
        risposta.restoScatti = (int) ((moltiplicatoreReale - moltiplicatoreApprossimato) * scattiAlimentazione);
        risposta.moltiplicatoreAlimentazione = moltiplicatoreApprossimato;

        return risposta;
    }

    public static void main(String[] args) {
        ScattiMath prova = new ScattiMath();
        System.out.println(prova.alimentazioneScattiApprossimata(1899, 1000));

    }


}

