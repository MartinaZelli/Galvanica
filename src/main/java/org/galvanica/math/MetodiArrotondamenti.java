package org.galvanica.math;

public class MetodiArrotondamenti {
    public static ScattiMath alimentazioneScattiMath(int scattiAttuali,
                                                     int scattiAlimentazione,
                                                     boolean arrotondaValori,
                                                     Double primoValoreVolumetrico) {
        //risposta:  private long restoScatti;
        //           private double moltiplicatoreAlimentazione;
        ScattiMath risposta = new ScattiMath();
        double moltiplicatoreReale = ((double) scattiAttuali) / scattiAlimentazione;

        //se il rapporto scattiattuali/alimentazione non supera 0.9 allora esci con moltiplicatore 0;
        if (moltiplicatoreReale < 0.9) {
            risposta.setRestoScatti(scattiAttuali);
            risposta.setMoltiplicatoreAlimentazione(0D);
            return risposta;
        }
        //se non vi sono valori volumetrici ma solo valori di peso allora possiamo usare il moltiplicatore reale.
        if (primoValoreVolumetrico == null) {
            risposta.setMoltiplicatoreAlimentazione(moltiplicatoreReale);
            risposta.setRestoScatti(0);
            return risposta;
        }
        //se è richiesto dall'alimentazione di arrotondare i valori allora usando math.floor
        // (arrotonda al numero intero più piccolo) troviamo prima il moltiplicatore da usare poi lo riportiamo.
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
        //infine se l'aggiunta non è da approssimare ma vi sono valori volumetrici
        // approssimiamo l'aggiunta per l'approssimazione del volume.
        double aggiunta = moltiplicatoreReale * primoValoreVolumetrico;
        double aggiuntaApprossimata = moltiplicatoreApprossimatoPerAggiunta(aggiunta);

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

    public static double moltiplicatoreApprossimatoPerAggiunta(double aggiunta) {
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
