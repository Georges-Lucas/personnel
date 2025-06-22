package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Accueil extends JFrame {
    
    public Accueil() {
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Configuration de la fenêtre
        String nomComplet = AccueilConnexion.UtilisateurConnecte.prenom + " " + AccueilConnexion.UtilisateurConnecte.nom;
        setTitle("Accueil - Gestion (" + nomComplet + ")");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("ACCUEIL", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Sous-titre avec informations utilisateur
        String niveauTexte = getNiveauTexte(AccueilConnexion.UtilisateurConnecte.niveauAcces);
        JLabel sousTitre = new JLabel("Bienvenue " + nomComplet + " (" + niveauTexte + ")", SwingConstants.CENTER);
        sousTitre.setFont(new Font("Arial", Font.ITALIC, 14));
        sousTitre.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panneau pour titre et sous-titre
        JPanel panelTitre = new JPanel();
        panelTitre.setLayout(new BorderLayout());
        panelTitre.add(titre, BorderLayout.NORTH);
        panelTitre.add(sousTitre, BorderLayout.SOUTH);

        // Panneau des boutons
        JPanel panelBoutons = new JPanel();
        panelBoutons.setLayout(new GridLayout(4, 1, 10, 10)); // 4 lignes au lieu de 3
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Création des boutons
        JButton boutonGererCompte = new JButton("Gérer le compte utilisateur");
        JButton boutonGererLigues = new JButton("Gérer les ligues");
        JButton boutonConnexion = new JButton("Connexion Utilisateur");
        JButton boutonQuitter = new JButton("Quitter");

        // Style des boutons
        Font fontBouton = new Font("Arial", Font.PLAIN, 16);
        boutonGererCompte.setFont(fontBouton);
        boutonGererLigues.setFont(fontBouton);
        boutonConnexion.setFont(fontBouton);
        boutonQuitter.setFont(fontBouton);

        // Hauteur des boutons
        Dimension tailleBouton = new Dimension(0, 50);
        boutonGererCompte.setPreferredSize(tailleBouton);
        boutonGererLigues.setPreferredSize(tailleBouton);
        boutonConnexion.setPreferredSize(tailleBouton);
        boutonQuitter.setPreferredSize(tailleBouton);

        // Gestionnaires d'événements
        boutonGererCompte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestionCompte();
                dispose();
            }
        });

        boutonGererLigues.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ouvrir la page de gestion des ligues
                new GestionLigues();
                dispose();
            }
        });

        boutonConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Retour à la connexion...");
                // Retour à la page de connexion
                new AccueilConnexion();
                dispose();
            }
        });

        boutonQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choix = JOptionPane.showConfirmDialog(
                    null,
                    "Êtes-vous sûr de vouloir quitter ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
                if (choix == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Ajout des boutons au panneau
        panelBoutons.add(boutonGererCompte);
        panelBoutons.add(boutonGererLigues);
        panelBoutons.add(boutonConnexion);
        panelBoutons.add(boutonQuitter);

        // Ajout des composants au panneau principal
        panelPrincipal.add(panelTitre, BorderLayout.NORTH);
        panelPrincipal.add(panelBoutons, BorderLayout.CENTER);

        // Ajout du panneau principal à la fenêtre
        add(panelPrincipal);

        // Affichage de la fenêtre
        setVisible(true);
    }
    
    private String getNiveauTexte(int niveau) {
        switch (niveau) {
            case 1: return "Administrateur Logiciel";
            case 2: return "Administrateur Ligue";
            case 3: return "Utilisateur";
            default: return "Non défini";
        }
    }
}