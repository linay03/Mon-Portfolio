import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Classification {
    private static ArrayList<Depeche> lectureDepeches(String nomFichier) {
        //creation d'un tableau de dépêches
        ArrayList<Depeche> depeches = new ArrayList<>();
        try {
            // lecture du fichier d'entrée
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                String id = ligne.substring(3);
                ligne = scanner.nextLine();
                String date = ligne.substring(3);
                ligne = scanner.nextLine();
                String categorie = ligne.substring(3);
                ligne = scanner.nextLine();
                String lignes = ligne.substring(3);
                while (scanner.hasNextLine() && !ligne.equals("")) {
                    ligne = scanner.nextLine();
                    if (!ligne.equals("")) {
                        lignes = lignes + '\n' + ligne;
                    }
                }
                Depeche uneDepeche = new Depeche(id, date, categorie, lignes);
                depeches.add(uneDepeche);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return depeches;
    }


    public static void classementDepeches(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {

        try {
            FileWriter file = new FileWriter(nomFichier +".txt");
            float x = 0;
            int compteurSc = 0, compteurC = 0, compteurE = 0, compteurP = 0, compteurSp = 0;
            ArrayList<PaireChaineEntier> juste = new ArrayList<>();
            ArrayList<Integer> compteur = new ArrayList<>(Arrays.asList(compteurSc, compteurC, compteurE, compteurP, compteurSp));
            for (int i = 0; i < 5; i++) {
                juste.add(new PaireChaineEntier(categories.get(i).getNom(), compteur.get(i)));
            }
            for (int j = 0; j < depeches.size(); j++) {
                ArrayList<PaireChaineEntier> total = new ArrayList<PaireChaineEntier>();
                for (int i = 0; i < categories.size(); i++) {
                    total.add(new PaireChaineEntier(categories.get(i).getNom(), (int) categories.get(i).score(depeches.get(j))));

                    x += categories.get(i).score(depeches.get(j))*100 %100;
                }

                file.write((j + 1) + "\t:  " + UtilitairePaireChaineEntier.chaineMax(total)+ "\n");

                if (depeches.get(j).getCategorie().compareTo(UtilitairePaireChaineEntier.chaineMax(total)) == 0) {
                    for (int i = 0; i < juste.size(); i++) {
                        if (UtilitairePaireChaineEntier.chaineMax(total).compareTo(juste.get(i).getChaine()) == 0) {
                            juste.set(i, new PaireChaineEntier(juste.get(i).getChaine(), juste.get(i).getEntier() + 1));
                        }
                    }
                }
            }
            System.out.println( "\n(score) : Nb de comparaisons en moyenne: "+(int) x / 2500);
            file.write("\n");
            for (int i = 0; i < juste.size(); i++) {
                file.write(juste.get(i).getChaine() + " : " + juste.get(i).getEntier() + "%"+ "\n");
            }

            file.write("MOYENNE : " + UtilitairePaireChaineEntier.moyenne(juste) + "%");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        boolean ajout;
        int k;
        for(int i = 0; i< depeches.size(); i++){
            if(depeches.get(i).getCategorie().compareTo(categorie) == 0){
                for(int j=0; j< depeches.get(i).getMots().size();j++) {
                    ajout = false;
                    // si resultat vide
                    if(resultat.size() == 0){
                        resultat.add(new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                        ajout = true;
                    }
                    // si mot deja dans resultat
                    for(int l = 0; l< resultat.size() && !ajout; l++){
                        if(depeches.get(i).getMots().get(j).compareTo(resultat.get(l).getChaine()) == 0){
                            ajout = true;
                        }
                    }
                    // si le mot n'est pas dans resultat
                    if(!ajout){
                        resultat.add(new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                    }

                }
            }
        }
        return resultat;
    }
    public static ArrayList<PaireChaineEntier> initDicoTotal(ArrayList<Depeche> depeches) {
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
        boolean ajout;
        int k;
        for(int i = 0; i< depeches.size(); i++){
            for(int j=0; j< depeches.get(i).getMots().size();j++) {
                ajout = false;
                // si resultat vide
                if(resultat.size() == 0){
                    resultat.add(new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                    ajout = true;
                }
                // si mot deja dans resultat
                for(int l = 0; l< resultat.size() && !ajout; l++){
                    if(depeches.get(i).getMots().get(j).compareTo(resultat.get(l).getChaine()) == 0){
                        ajout = true;
                    }
                }
                // si le mot n'est pas dans resultat
                if(!ajout){
                    resultat.add(new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                }

            }
        }

        return resultat;
    }
    public static int calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
        int compteur =0;
        for (int i = 0; i < dictionnaire.size(); i++) {     //parcours de dictionnaire
            compteur++;
            for (int j = 0; j < depeches.size(); j++) {    // parcours des depeches
                compteur++;
                if(depeches.get(j).getCategorie().compareTo(categorie) == 0) {
                    for (int k = 0; k < depeches.get(j).getMots().size(); k++) {    //parcours des mots d'un depeches
                        compteur++;
                        if (depeches.get(j).getMots().get(k).compareTo(dictionnaire.get(i).getChaine()) == 0) {
                            dictionnaire.set(i, new PaireChaineEntier(depeches.get(j).getMots().get(k), dictionnaire.get(i).getEntier() + 1));
                        }
                    }
                }
            }
        }
        return compteur;
    }

    public static void calculScoresTotal(ArrayList<Depeche> depeches, ArrayList<PaireChaineEntier> dictionnaire) {
        for (int i = 0; i < dictionnaire.size(); i++) {     //parcours de dictionnaire
            for (int j = 0; j < depeches.size(); j++) {    // parcours des depeches
                for (int k = 0; k < depeches.get(j).getMots().size(); k++) {    //parcours des mots d'un depeches
                    if (depeches.get(j).getMots().get(k).compareTo(dictionnaire.get(i).getChaine()) == 0) {
                        dictionnaire.set(i, new PaireChaineEntier(depeches.get(j).getMots().get(k), dictionnaire.get(i).getEntier() + 1));
                    }
                }

            }
        }
    }
    public static int poidsPourScore(int score) {
        if(score <= 2) {
            return 1;
        } else if(score <= 8){
            return 2;
        } else{
            return 3;
        }
    }

    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {
        try {
            FileWriter file = new FileWriter(nomFichier + ".txt");
            ArrayList<PaireChaineEntier> dicoTotal = new ArrayList<>();
            dicoTotal = initDicoTotal(depeches);
            calculScoresTotal(depeches, dicoTotal);

            ArrayList<PaireChaineEntier> cat = new ArrayList<>();
            cat = initDico(depeches, categorie);
            calculScores(depeches, categorie, cat);
            System.out.print("(calculScores) : Nb opération pour categorie : ");
            System.out.print(categorie);
            System.out.print(" : ");
            System.out.println(calculScores(depeches, categorie, cat));
            int score, scoreTotal=0;
            for (int i = 0; i < cat.size(); i++) {
                score = cat.get(i).getEntier();
                for (int j = 0; j < dicoTotal.size(); j++) {
                    if (cat.get(i).getChaine().compareTo(dicoTotal.get(j).getChaine()) == 0) {
                        scoreTotal = dicoTotal.get(j).getEntier();
                    }
                }
                cat.set(i, new PaireChaineEntier(cat.get(i).getChaine(), poidsPourScore(score - (scoreTotal - score))));
                file.write(cat.get(i).getChaine() + ":" + cat.get(i).getEntier()+"\n");
            }
            //System.out.println("lexique écrit avec succès dans "+ nomFichier+".txt");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long startTime1 = System.currentTimeMillis();
        //Chargement des dépêches en mémoire
        System.out.println("chargement des dépêches");
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");
        ArrayList<Depeche> tests = lectureDepeches("./test.txt");
        long endTime1 = System.currentTimeMillis();

        long startTime2 = System.currentTimeMillis();
        ArrayList<PaireChaineEntier> lex = new ArrayList<PaireChaineEntier>();
        lex.add(0,new PaireChaineEntier("0", 0));

        Categorie SCIENCE = new Categorie("ENVIRONNEMENT-SCIENCES");
        Categorie CULTURE = new Categorie("CULTURE");
        Categorie ECONOMIE = new Categorie("ECONOMIE");
        Categorie POLITIQUE = new Categorie("POLITIQUE");
        Categorie SPORT = new Categorie("SPORTS");

        ArrayList<Categorie> cat = new ArrayList<Categorie>(Arrays.asList(SCIENCE, CULTURE, ECONOMIE, POLITIQUE, SPORT));
        long endTime2 = System.currentTimeMillis();
        long startTime3 = System.currentTimeMillis();
        for(int i =0; i< cat.size(); i++) {
            generationLexique(depeches, cat.get(i).getNom(), cat.get(i).getNom());
            cat.get(i).initLexique(cat.get(i).getNom()+ ".txt");
        }

        long endTime3 = System.currentTimeMillis();

        System.out.print("\n*** Nom fichier : ");
        Scanner lecteur = new Scanner(System.in);
        String nom = lecteur.nextLine();

        long startTime4 = System.currentTimeMillis();
        classementDepeches(tests, cat, nom);
        //classementDepeches(depeches, cat, nom);
        long endTime4 = System.currentTimeMillis();

        System.out.println("\nChargement des dépêches en mémoire : " + (endTime1 - startTime1) + "ms");
        System.out.println("Création catégories : " + (endTime2 - startTime2) + "ms");
        System.out.println("Création fichiers lexiques : " + (endTime3 - startTime3) + "ms");
        System.out.println("Classement depeches : " + (endTime4 - startTime4) + "ms");

        System.out.println("\nTemps total : " + ((endTime1 - startTime1)+(endTime2 - startTime2)+(endTime3 - startTime3)+(endTime4 - startTime4)) + "ms");
    }
}

