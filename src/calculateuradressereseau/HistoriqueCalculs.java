/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calculateuradressereseau;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class HistoriqueCalculs {
    private ArrayList<String> historique = new ArrayList<>();
    private int lastSavedIndex = 0;

    public void ajouterCalcul(String calcul) {
        historique.add(calcul);
    }

    public void sauvegarderHistorique(String fichier) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fichier, true));
        for (int i = lastSavedIndex; i < historique.size(); i++) {
            String calcul = historique.get(i);
            writer.write(calcul);
            writer.newLine();
        }
        writer.close();
        lastSavedIndex = historique.size();
    }

    public void chargerHistorique(String fichier) throws IOException {
        File f = new File(fichier);
        if (!f.exists()) {
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                historique.add(line);
            }
        }
        reader.close();
        lastSavedIndex = historique.size();
    }

    public List<String> getHistorique() {
        return Collections.unmodifiableList(historique);
    }
}
