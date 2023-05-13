import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Classification2 {
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
            //float x = 0;
            FileWriter file = new FileWriter(nomFichier + ".txt");
            String maxCh;
            // création des compteurs (pour compter combien de classification juste a chaque catégorie) + rangement dans Arraylist juste
            ArrayList<PaireChaineEntier> juste = new ArrayList<>();
            ArrayList<PaireChaineEntier> max = new ArrayList<>();
            for (int i = 0; i < categories.size(); i++) {
                juste.add(new PaireChaineEntier(categories.get(i).getNom(), 0));
                max.add(new PaireChaineEntier(categories.get(i).getNom(), 0));
            }
            triFusion(juste, 0, juste.size()-1);

            for (int j = 0; j < depeches.size(); j++) {
                // pour chaque depeche, on range dans total ses scores pour chaques catégorie et on garde son max avec chainemax (dans maxCh)
                ArrayList<PaireChaineEntier> total = new ArrayList<>();
                for (int i = 0; i < categories.size(); i++) {
                    total.add(new PaireChaineEntier(categories.get(i).getNom(), (int) categories.get(i).score(depeches.get(j))));
                    // compter le nombre de depeche par catégorie
                    if(max.get(i).getChaine().compareTo(depeches.get(j).getCategorie()) == 0){
                        max.get(i).incremEntier();
                    }
                   // x += categories.get(i).score(depeches.get(j))*100 %100;
                }
                maxCh = UtilitairePaireChaineEntier.chaineMax(total);
                file.write((j + 1) + "\t:  " + maxCh + "\n");

                // on incrémente le bon compteur si la catégorie est la bonne
                if (depeches.get(j).getCategorie().compareTo(maxCh) == 0) {
                        juste.get(rechDichoRec(juste, maxCh)).incremEntier();
                }
            }
            //System.out.println("\n(score) : Nb de comparaisons en moyenne: "+ (int) x / 2500);
            file.write("\n");
            // pourcentage de réussite pour chaque catégorie -> moyenne
            for (int i = 0; i < juste.size(); i++) {
                juste.get(i).setEntier(juste.get(i).getEntier()*100/max.get(i).getEntier());
                file.write(juste.get(i).getChaine() + " : " + juste.get(i).getEntier() + "%\n");
                // nb de depeche par categorie
                // System.out.println(max.get(i).getEntier());
            }

            file.write("\nMOYENNE : " + UtilitairePaireChaineEntier.moyenne(juste) + "%");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) {
        // création de resultat contenant tous les mots des depeches d'une catégorie
        ArrayList<PaireChaineEntier> resultat = new ArrayList<>(Arrays.asList(new PaireChaineEntier(depeches.get(0).getMots().get(0), 0)));
        // on parcours depeches en vérifiant que la dépeche soit de la bonne catégorie
        for (int i = 0; i < depeches.size(); i++) {
            if (depeches.get(i).getCategorie().compareTo(categorie) == 0) {
                for (int j = 0; j < depeches.get(i).getMots().size(); j++) {
                    // pour chaque mot, on regarde si il n'appartient pas déjà à résultat et on réagi en fonction
                    // si le mot n'est pas dans resultat
                    if (rechDichoRec(resultat, depeches.get(i).getMots().get(j)) == -1){
                        // on l'ajoute à l'indice correspondant
                        resultat.add(rechDichoRecInsere(resultat, depeches.get(i).getMots().get(j)), new PaireChaineEntier(depeches.get(i).getMots().get(j), 0));
                    }
                }
            }
        }
        return resultat;
    }

    // renvoie l'indice où devrait se trouver mot dans resultat
    public static int rechDichoRecInsere(ArrayList<PaireChaineEntier> resultat, String mot) {
        // on test si le mot est plus petit que le dernier mot de dico
        if (resultat.get(resultat.size() - 1).getChaine().compareTo(mot) < 0) {
            return resultat.size();
        } else {
            return rechDichoWorkerInsere(resultat, mot, 0, resultat.size() - 1);
        }
    }
    public static int rechDichoWorkerInsere(ArrayList<PaireChaineEntier> resultat, String mot, int inf, int sup) {
        if (sup == inf) {
                return (inf);
        } else {
            int m = (inf + sup) / 2;
            if (resultat.get(m).getChaine().compareTo(mot) >= 0) {
                return rechDichoWorkerInsere(resultat, mot, inf, m);
            } else {
                return rechDichoWorkerInsere(resultat, mot, m + 1, sup);
            }
        }
    }

    public static int calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
        int indice, compteur = 0;
        // on parcours depeches en vérifiant que la depeche soit de la bonne catégorie
        for (int j = 0; j < depeches.size(); j++) {
            compteur ++;
            if (depeches.get(j).getCategorie().compareTo(categorie) == 0) {
                compteur ++;
                for (int k = 0; k < depeches.get(j).getMots().size(); k++) {
                    compteur ++;
                    // on cherche l'indice de chaque mot de chaque depeche dans le dictionnaire
                    indice = rechDichoRec(dictionnaire, depeches.get(j).getMots().get(k));
                    if (depeches.get(j).getCategorie().compareTo(categorie) != 0) {
                        dictionnaire.get(indice).decremEntier();
                    } else {
                        dictionnaire.get(indice).incremEntier();
                    }
                    compteur ++;
                }
            }
        }
        return compteur;
    }

    public static int poidsPourScore(int score) {
        if (score <= 1) {
            return 1;
        } else if (score <= 4) {
            return 2;
        } else {
            return 3;
        }
    }

    public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {
        try {
            FileWriter file = new FileWriter(nomFichier + ".txt");
            // création et tri de cat contenant tous les mots d'une catégorie donnée
            ArrayList<PaireChaineEntier> cat = initDico(depeches, categorie);
            triFusion(cat, 0, cat.size() - 1);
            calculScores(depeches, categorie, cat);
            //System.out.println("(calculScores) : Nb opération pour categorie : " + categorie + " : " + calculScores(depeches, categorie, cat));
            int occurence;
            // attribuer un poid à chaque mot de cat
            for (int i = 0; i < cat.size(); i++) {
                occurence = cat.get(i).getEntier();

                cat.set(i, new PaireChaineEntier(cat.get(i).getChaine(), poidsPourScore(occurence)));
                file.write(cat.get(i).getChaine() + ":" + cat.get(i).getEntier() + "\n");
            }
            System.out.println("lexique écrit avec succès dans " + nomFichier + ".txt");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void triFusion(ArrayList<PaireChaineEntier> dico, int inf, int sup) {
        if (inf < sup) {
            int m = (inf + sup) / 2;
            triFusion(dico, inf, m);
            triFusion(dico, m + 1, sup);
            fusionTabGTabD(dico, inf, m, sup);
        }
    }

    public static void fusionTabGTabD(ArrayList<PaireChaineEntier> dico, int inf, int m, int sup) {
        // étape 1 : déclaration
        ArrayList<PaireChaineEntier> temp = new ArrayList<>();
        int g = inf, d = m + 1, i;
        // étape 2 : ajouter les éléments triés
        while (g <= m && d <= sup) {
            if (dico.get(g).getChaine().compareTo(dico.get(d).getChaine()) < 0) {
                temp.add(dico.get(g));
                g++;
            } else {
                temp.add(dico.get(d));
                d++;
            }
        }
        // étape 3 : ajouter le reste des éléments
        int j = g;
        while (j <= m) {
            temp.add(dico.get(j));
            j++;
        }
        j = d;
        while (j <= sup) {
            temp.add(dico.get(j));
            j++;
        }
        // étape 4 : copie des éléments de temp vers vInt
        i = inf;
        for (j = 0; j < temp.size(); j++) {
            dico.set(i, temp.get(j));
            i++;
        }
    }

    public static int rechDichoRec(ArrayList<PaireChaineEntier> dico, String mot) {
        PaireChaineEntier recherche = new PaireChaineEntier(mot, 0);
        // on test si le mot est plus petit que le dernier mot de dico
        if (dico.get(dico.size() - 1).getChaine().compareTo(recherche.getChaine()) < 0) {
            return -1;
        } else {
            return rechDichoWorker(dico, mot, 0, dico.size() - 1);
        }
    }

    public static int rechDichoWorker(ArrayList<PaireChaineEntier> dico, String mot, int inf, int sup) {
        PaireChaineEntier recherche = new PaireChaineEntier(mot, 0);
        if (sup == inf) {
            if (dico.get(inf).getChaine().compareTo(recherche.getChaine()) == 0) {
                return inf;
            } else {
                return -1;
            }
        } else {
            int m = (inf + sup) / 2;
            if (dico.get(m).getChaine().compareTo(recherche.getChaine()) >= 0) {
                return rechDichoWorker(dico, mot, inf, m);
            } else {
                return rechDichoWorker(dico, mot, m + 1, sup);
            }
        }
    }

    public static void main(String[] args) {
        // Chargement des dépêches en mémoire
        long startTime1 = System.currentTimeMillis();
        System.out.println("chargement des dépêches\n");
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");
        ArrayList<Depeche> tests = lectureDepeches("./test.txt");

        long endTime1 = System.currentTimeMillis();

        // création catégories
        long startTime2 = System.currentTimeMillis();
        Categorie SCIENCE = new Categorie("ENVIRONNEMENT-SCIENCES");
        Categorie CULTURE = new Categorie("CULTURE");
        Categorie ECONOMIE = new Categorie("ECONOMIE");
        Categorie POLITIQUE = new Categorie("POLITIQUE");
        Categorie SPORT = new Categorie("SPORTS");


        // création arraylist catégoriestests
        ArrayList<Categorie> cat = new ArrayList<>(Arrays.asList(SCIENCE, CULTURE, ECONOMIE, POLITIQUE, SPORT));
        long endTime2 = System.currentTimeMillis();

        // création fichiers lexiques
        long startTime3 = System.currentTimeMillis();

        for(int i = 0; i < cat.size(); i++){
            generationLexique(depeches, cat.get(i).getNom(), cat.get(i).getNom());
            cat.get(i).initLexique(cat.get(i).getNom() + ".txt");
        }

        long endTime3 = System.currentTimeMillis();

        // choix du nom fichier
        System.out.print("\n*** Nom fichier : ");
        Scanner lecteur = new Scanner(System.in);
        String nom = lecteur.nextLine();

        long startTime4 = System.currentTimeMillis();
        //classementDepeches(depeches, cat, nom);
        classementDepeches(tests, cat, nom);
        long endTime4 = System.currentTimeMillis();

        System.out.println("\nChargement des dépêches en mémoire : " + (endTime1 - startTime1) + "ms");
        System.out.println("Création catégories : " + (endTime2 - startTime2) + "ms");
        System.out.println("Création fichiers lexiques : " + (endTime3 - startTime3) + "ms");
        System.out.println("Classement depeches : " + (endTime4 - startTime4) + "ms");

        System.out.println("\nTemps total : " + ((endTime1 - startTime1)+(endTime2 - startTime2)+(endTime3 - startTime3)+(endTime4 - startTime4)) + "ms");
    }
}

