package calculateuradressereseau;
import javax.swing.*;
import java.awt.event.*;

public class CalculateurReseauSwingApp extends UIComposant {
    private HistoriqueCalculs historique = new HistoriqueCalculs();

    @Override
    void initialiserUI() {
        JFrame frame = new JFrame("Calculateur de Réseau");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placerComposants(panel);

        frame.setVisible(true);
    }

    private void placerComposants(JPanel panel) {
        panel.setLayout(null);

        JLabel labelIP = new JLabel("Adresse IP :");
        labelIP.setBounds(10, 20, 80, 25);
        panel.add(labelIP);

        champIP = new JTextField(20);
        champIP.setBounds(100, 20, 165, 25);
        panel.add(champIP);

        JLabel labelMasque = new JLabel("Masque :");
        labelMasque.setBounds(10, 50, 80, 25);
        panel.add(labelMasque);

        champMasque = new JTextField(20);
        champMasque.setBounds(100, 50, 165, 25);
        panel.add(champMasque);

        JButton boutonCalculer = new JButton("Calculer");
        boutonCalculer.setBounds(10, 80, 150, 25);
        panel.add(boutonCalculer);

        boutonCalculer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculer();
            }
        });

        zoneResultat = new JTextArea();
        zoneResultat.setBounds(10, 110, 450, 200);
        panel.add(zoneResultat);
    }

    private void calculer() {
        try {
            String adresseIP = champIP.getText();
            String masque = champMasque.getText();

            Reseau reseau = new Reseau(adresseIP, masque);
            String resultats = "Classe : " + reseau.getClasse() + "\n"
                    + "Plage : " + reseau.getAdresseDebut() + " - " + reseau.getAdresseFin();

            mettreAJourResultats(resultats);

            historique.ajouterCalcul(resultats);
            historique.sauvegarderHistorique("historique.txt");
        } catch (Exception ex) {
            mettreAJourResultats("Erreur : " + ex.getMessage());
        }
    }

    @Override
    void mettreAJourResultats(String resultats) {
        zoneResultat.setText(resultats);
    }

    public static void main(String[] args) {
        new CalculateurReseauSwingApp().initialiserUI();
    }
}
