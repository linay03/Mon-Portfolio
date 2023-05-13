import java.util.ArrayList;

public class UtilitairePaireChaineEntier {


    public static int indicePourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        int i =0;
        PaireChaineEntier p = new PaireChaineEntier(chaine,0);
        while (i < listePaires.size() && listePaires.get(i).getChaine().compareTo(chaine) != 0){
            i++;
        }
        if (i < listePaires.size()) {
            return i;
        } else {
            return -1;
        }
    }


    public static int entierPourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        int i = 0;
        PaireChaineEntier p = new PaireChaineEntier(chaine, 0);
        while (i < listePaires.size() && listePaires.get(i).getChaine().compareTo(chaine) != 0) {
            i++;
        }
        if (i < listePaires.size()) {
            return listePaires.get(i).getEntier();
        } else {
            return 0;
        }
    }
    public static String chaineMax(ArrayList<PaireChaineEntier> listePaires) {
        PaireChaineEntier pairemax = listePaires.get(0);
        for(int i = 1; i< listePaires.size(); i++){
            if(listePaires.get(i).getEntier() > pairemax.getEntier()){
                pairemax = listePaires.get(i);
            }
        }
        return pairemax.getChaine();
    }


    public static float moyenne(ArrayList<PaireChaineEntier> listePaires) {
        float moyenne =  0;
        int nb_ent = listePaires.size();
        int i = 0;
        while (i< nb_ent){
            moyenne += listePaires.get(i).getEntier();
            i++;
        }
        return moyenne/nb_ent;
    }


}
