import java.util.ArrayList;

public class PaireChaineEntier {
    private String chaine;
    private int entier;

    public PaireChaineEntier(String chaine, int entier){
        this.chaine = chaine;
        this.entier = entier;
    }

    public String getChaine(){
        return chaine;
    }
    public int getEntier(){
        return entier;
    }

    // utiliser pour changer tous les set
    public void setEntier(int entier){
        this.entier = entier;
    }

    public void incremEntier(){ this.entier += 1;}
    public void decremEntier(){ this.entier -= 1;}
}
