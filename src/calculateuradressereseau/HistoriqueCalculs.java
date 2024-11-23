/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calculateuradressereseau;

import java.io.*;
import java.util.ArrayList;

class HistoriqueCalculs {
    private ArrayList<String> historique = new ArrayList<>();

    public void ajouterCalcul(String calcul) {
        historique.add(calcul);
    }

    public void sauvegarderHistorique(String fichier) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fichier, true));
        for (String calcul : historique) {
            writer.write(calcul);
            writer.newLine();
        }
        writer.close();
    }
}
