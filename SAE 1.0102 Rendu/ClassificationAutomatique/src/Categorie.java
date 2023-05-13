import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Categorie {

    private String nom; // le nom de la catégorie p.ex : sport, politique,...
    private ArrayList<PaireChaineEntier> lexique; //le lexique de la catégorie

    // constructeur
    public Categorie(String nom) {
        this.nom = nom;
    }


    public String getNom() {
        return nom;
    }

    public  ArrayList<PaireChaineEntier> getLexique() {
        return lexique;
    }


    // initialisation du lexique de la catégorie à partir du contenu d'un fichier texte
    public void initLexique(String nomFichier) {
        this.lexique = new ArrayList<PaireChaineEntier>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String ligne = scanner.nextLine();
                String mot = ligne.substring(0, ligne.length()-2);
                int poids = Integer.parseInt(ligne.substring(ligne.length()-1));
                PaireChaineEntier env = new PaireChaineEntier(mot, poids);
                this.lexique.add(env);
            }
            scanner.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    //calcul du score d'une dépêche pour la catégorie
    public float score(Depeche d) {
        float resultat=0, compteur =0;
        for(int i = 0; i< d.getMots().size(); i++){
            compteur ++;
            resultat += UtilitairePaireChaineEntier.entierPourChaine(this.lexique, d.getMots().get(i));
        }
        return resultat + compteur/100;
    }


}
