package org.galvanica.math;

public class MetodiArrotondamenti {
    public static ScattiMath alimentazioneScattiMath(int scattiAttuali,
                                                     int scattiAlimentazione,
                                                     boolean arrotondaValori,
                                                     Double primoValoreVolumetrico) {
        ScattiMath risposta = new ScattiMath();
        double moltiplicatoreReale = (double) scattiAttuali / scattiAlimentazione;

        if (moltiplicatoreReale < 0.9) {
            risposta.setRestoScatti(scattiAttuali);
            risposta.setMoltiplicatoreAlimentazione(0D);
            return risposta;
        }
        if (arrotondaValori) {
            double moltiplicatoreFloor = Math.floor(moltiplicatoreReale);
            double moltiplicatoreApprossimato = moltiplicatoreApprossimato(
                    moltiplicatoreReale,
                    moltiplicatoreFloor);
            double restoScatti = ((moltiplicatoreReale - moltiplicatoreApprossimato) * scattiAlimentazione);
            risposta.setRestoScatti(Math.round(restoScatti));
            risposta.setMoltiplicatoreAlimentazione(moltiplicatoreApprossimato);

            return risposta;
        }
        if (primoValoreVolumetrico == null) {
            risposta.setMoltiplicatoreAlimentazione(moltiplicatoreReale);
            risposta.setRestoScatti(0);
            return risposta;
        }
        double aggiunta = moltiplicatoreReale * primoValoreVolumetrico;
        double aggiuntaApprossimata = approssimazioneAggiunta(aggiunta);

        double moltiplicatoreApprossimato = aggiuntaApprossimata / primoValoreVolumetrico * 1;
        double scattiAggiunti = moltiplicatoreApprossimato * scattiAlimentazione;
        risposta.setRestoScatti(Math.round(scattiAttuali - scattiAggiunti));
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
