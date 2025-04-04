package affichage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Importez vos packages
import jdbc.JDBC;
import personnel.SauvegardeImpossible;

public class Accueil extends JFrame {
    
    public Accueil() {
        // Configuration de la fenêtre
        this.setTitle("Ma Fenêtre");  // Définit le titre de la fenêtre
        this.setSize(500, 400);       // Définit la taille (largeur, hauteur) en pixels
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);  // Centre la fenêtre sur l'écran
        
        // Création d'un panneau pour contenir les composants
        JPanel panel = new JPanel();
        
        // Vous pouvez définir un gestionnaire de mise en page (layout manager)
        panel.setLayout(new FlowLayout());
        
        // Ajout de composants au panneau
        JLabel label = new JLabel("Bienvenue dans ma fenêtre Swing!");
        JButton bouton = new JButton("Cliquez-moi");
        
        panel.add(label);
        panel.add(bouton);
        
        // Ajout du panneau à la fenêtre
        this.add(panel);
        
        // Rendre la fenêtre visible
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        // Pour éviter les problèmes de threading avec Swing, il est recommandé
        // d'utiliser SwingUtilities.invokeLater
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Accueil();
            }
        });
    }
}