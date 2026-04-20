package calculateuradressereseau;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class CalculateurReseauSwingApp extends UIComposant {
    private HistoriqueCalculs historique = new HistoriqueCalculs();
    private static final String HISTORIQUE_FICHIER = "historique.txt";

    private JFrame frame;
    private DefaultListModel<String> historiqueModel;
    private JList<String> historiqueList;

    @Override
    void initialiserUI() {
        appliquerTheme();

        frame = new JFrame("Calculateur de réseau IP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1200, 860));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0x0F0F10));
        root.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel titre = new JLabel("Calculateur de réseau IP");
        titre.setFont(new Font("SansSerif", Font.BOLD, 28));
        titre.setForeground(new Color(0xF2F2F2));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        root.add(titre, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel carteCalcul = creerCarte();
        carteCalcul.setLayout(new GridBagLayout());
        remplirCarteCalcul(carteCalcul);

        content.add(carteCalcul);
        content.add(Box.createVerticalStrut(18));

        JPanel carteHistorique = creerCarte();
        carteHistorique.setLayout(new BorderLayout(0, 12));
        remplirCarteHistorique(carteHistorique);

        content.add(carteHistorique);
        root.add(content, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        chargerHistoriqueInitial();
    }

    private void remplirCarteCalcul(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel sousTitre = new JLabel("Paramètres");
        sousTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        sousTitre.setForeground(new Color(0xF2F2F2));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(sousTitre, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(creerChampAvecLabel("Adresse IP", champIP = creerChampAvecPlaceholder("ex: 192.168.1.0")), gbc);

        gbc.gridx = 1;
        panel.add(creerChampAvecLabel("Masque de sous-réseau", champMasque = creerChampAvecPlaceholder("ex: 255.255.255.0")), gbc);

        JButton boutonCalculer = new JButton("Calculer");
        stylePrimaryButton(boutonCalculer);
        boutonCalculer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculer();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 6, 10);
        panel.add(boutonCalculer, gbc);

        JLabel labelResultats = new JLabel("Résultats");
        labelResultats.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelResultats.setForeground(new Color(0xF2F2F2));
        gbc.gridy = 3;
        gbc.insets = new Insets(16, 10, 8, 10);
        panel.add(labelResultats, gbc);

        zoneResultat = new JTextArea();
        zoneResultat.setEditable(false);
        zoneResultat.setLineWrap(true);
        zoneResultat.setWrapStyleWord(true);
        zoneResultat.setFont(new Font("SansSerif", Font.PLAIN, 14));
        zoneResultat.setForeground(new Color(0xE6E6E6));
        zoneResultat.setBackground(new Color(0x151517));
        zoneResultat.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        zoneResultat.setText("Entrez une adresse IP et un masque, puis cliquez sur Calculer.");

        JScrollPane scrollResultats = new JScrollPane(zoneResultat);
        scrollResultats.setBorder(BorderFactory.createLineBorder(new Color(0x2A2A2E), 1, true));
        scrollResultats.getViewport().setBackground(new Color(0x151517));
        scrollResultats.setPreferredSize(new Dimension(200, 360));

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollResultats, gbc);
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    private void remplirCarteHistorique(JPanel panel) {
        JLabel titre = new JLabel("Historique des calculs");
        titre.setFont(new Font("SansSerif", Font.BOLD, 18));
        titre.setForeground(new Color(0xF2F2F2));
        panel.add(titre, BorderLayout.NORTH);

        historiqueModel = new DefaultListModel<>();
        historiqueList = new JList<>(historiqueModel);
        historiqueList.setBackground(new Color(0x151517));
        historiqueList.setForeground(new Color(0xE6E6E6));
        historiqueList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        historiqueList.setSelectionBackground(new Color(0x232329));
        historiqueList.setSelectionForeground(new Color(0xFFFFFF));
        historiqueList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = value == null ? "" : String.valueOf(value);
                String html = "<html><div style='padding:6px 8px; line-height:1.35;'>" +
                        escapeHtml(text).replace(" | ", "<br>") +
                        "</div></html>";
                label.setText(html);
                label.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(historiqueList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x2A2A2E), 1, true));
        scroll.getViewport().setBackground(new Color(0x151517));
        scroll.setPreferredSize(new Dimension(200, 320));

        panel.add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("Aucun calcul effectué.");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hint.setForeground(new Color(0xA1A1AA));
        hint.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        panel.add(hint, BorderLayout.SOUTH);

        historiqueModel.addListDataListener(new javax.swing.event.ListDataListener() {
            @Override
            public void intervalAdded(javax.swing.event.ListDataEvent e) {
                hint.setVisible(historiqueModel.isEmpty());
            }

            @Override
            public void intervalRemoved(javax.swing.event.ListDataEvent e) {
                hint.setVisible(historiqueModel.isEmpty());
            }

            @Override
            public void contentsChanged(javax.swing.event.ListDataEvent e) {
                hint.setVisible(historiqueModel.isEmpty());
            }
        });
        hint.setVisible(historiqueModel.isEmpty());
    }

    private void calculer() {
        try {
            String adresseIP = lireValeurChamp(champIP);
            String masque = lireValeurChamp(champMasque);

            Reseau reseau = new Reseau(adresseIP, masque);
            String resultats = "Classe : " + reseau.getClasse() + "\n\n"
                    + "Adresse réseau : " + reseau.getAdresseReseau() + "\n\n"
                    + "Broadcast : " + reseau.getBroadcast() + "\n\n"
                    + "Masque CIDR : /" + reseau.getMasqueCIDR() + "\n\n"
                    + "Plage hôtes : " + reseau.getPlageHotesDebut() + " — " + reseau.getPlageHotesFin() + "\n\n"
                    + "Nb hôtes utiles : " + reseau.getNbHotesUtiles();

            String historiqueEntree = adresseIP + " / " + masque
                    + " | Classe : " + reseau.getClasse()
                    + " | Adresse réseau : " + reseau.getAdresseReseau()
                    + " | Broadcast : " + reseau.getBroadcast() + " (/" + reseau.getMasqueCIDR() + ")"
                    + " | Plage hôtes : " + reseau.getPlageHotesDebut() + " — " + reseau.getPlageHotesFin()
                    + " | Nb hôtes utiles : " + reseau.getNbHotesUtiles();

            mettreAJourResultats(resultats);

            historique.ajouterCalcul(historiqueEntree);
            historique.sauvegarderHistorique(HISTORIQUE_FICHIER);
            ajouterHistoriqueUI(historiqueEntree);
        } catch (Exception ex) {
            mettreAJourResultats("Erreur : " + ex.getMessage());
        }
    }

    @Override
    void mettreAJourResultats(String resultats) {
        zoneResultat.setText(resultats);
    }

    private void ajouterHistoriqueUI(String resultats) {
        if (historiqueModel == null) {
            return;
        }
        historiqueModel.addElement(resultats);
        if (historiqueList != null) {
            int last = historiqueModel.getSize() - 1;
            if (last >= 0) {
                historiqueList.ensureIndexIsVisible(last);
            }
        }
    }

    private void chargerHistoriqueInitial() {
        try {
            historique.chargerHistorique(HISTORIQUE_FICHIER);
            if (historiqueModel != null) {
                for (String item : historique.getHistorique()) {
                    historiqueModel.addElement(item);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void appliquerTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Panel.background", new Color(0x0F0F10));
        UIManager.put("Label.foreground", new Color(0xE6E6E6));
        UIManager.put("TextField.background", new Color(0x121214));
        UIManager.put("TextField.foreground", new Color(0xE6E6E6));
        UIManager.put("TextField.caretForeground", new Color(0xE6E6E6));
        UIManager.put("TextField.inactiveForeground", new Color(0xA1A1AA));
        UIManager.put("TextArea.background", new Color(0x151517));
        UIManager.put("TextArea.foreground", new Color(0xE6E6E6));
    }

    private JPanel creerCarte() {
        JPanel carte = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1A1A1D));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(0x2A2A2E));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
            }
        };
        carte.setOpaque(false);
        carte.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return carte;
    }

    private JPanel creerChampAvecLabel(String label, JTextField champ) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BorderLayout(0, 8));

        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(new Color(0xC9C9D1));
        p.add(l, BorderLayout.NORTH);

        p.add(champ, BorderLayout.CENTER);
        return p;
    }

    private JTextField creerChampAvecPlaceholder(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x2A2A2E), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        Color placeholderColor = new Color(0x7A7A85);
        Color textColor = new Color(0xE6E6E6);
        field.setForeground(placeholderColor);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder) && field.getForeground().equals(placeholderColor)) {
                    field.setText("");
                    field.setForeground(textColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(placeholderColor);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    private String lireValeurChamp(JTextField field) {
        if (field == null) {
            return "";
        }
        String value = field.getText();
        if (value == null) {
            return "";
        }
        value = value.trim();
        if (value.startsWith("ex:")) {
            return "";
        }
        return value;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(0x2C5DFD));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static void main(String[] args) {
        new CalculateurReseauSwingApp().initialiserUI();
    }
}
