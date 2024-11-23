/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calculateuradressereseau;

import javax.swing.*;

abstract class UIComposant {
    protected JTextField champIP;
    protected JTextField champMasque;
    protected JTextArea zoneResultat;

    abstract void initialiserUI();

    abstract void mettreAJourResultats(String resultats);
}
