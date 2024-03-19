package org.galvanica.math;

public class MetodiArrotondamenti {
    public static ScattiMath alimentazioneScattiApprossimata(int scattiAttuali,
                                                             int scattiAlimentazione) {
        ScattiMath risposta = new ScattiMath();
        double moltiplicatoreReale = (double) scattiAttuali / scattiAlimentazione;
        double moltiplicatoreFloor = Math.floor(moltiplicatoreReale);
        double moltiplicatoreApprossimato = moltiplicatoreApprossimato(
                moltiplicatoreReale,
                moltiplicatoreFloor);
        risposta.setRestoScatti((int) ((moltiplicatoreReale - moltiplicatoreApprossimato) * scattiAlimentazione));
        risposta.setMoltiplicatoreAlimentazione(moltiplicatoreApprossimato);

        return risposta;
    }

    private static double moltiplicatoreApprossimato(double moltiplicatoreReale,
                                                     double moltiplicatoreFloor) {
        double cifreDecimali = moltiplicatoreReale - moltiplicatoreFloor;
        if (cifreDecimali < 0.4) {
            return moltiplicatoreFloor;
        }
        if (cifreDecimali <= 0.9) {
            return moltiplicatoreFloor + 0.5;
        }
        if (cifreDecimali > 0.9) {
            return moltiplicatoreFloor + 1;
        }
        return 0;
    }

    public static double approssimazioneAggiunta(double aggiunta) {
        if (aggiunta < 100) {
            double resto = aggiunta % 5;
            return aggiunta - resto;
        }
        if (aggiunta < 500) {
            double resto = aggiunta % 10;
            return aggiunta - resto;
        }
        if (aggiunta < 1000) {
            double resto = aggiunta % 50;
            return aggiunta - resto;
        }
        double resto = aggiunta % 250;
        return aggiunta - resto;
    }
}
