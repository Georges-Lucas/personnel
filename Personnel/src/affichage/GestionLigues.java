package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GestionLigues extends JFrame {
    
    public GestionLigues() {
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Configuration de la fenêtre
        String nomComplet = AccueilConnexion.UtilisateurConnecte.prenom + " " + AccueilConnexion.UtilisateurConnecte.nom;
        setTitle("Gestion des Ligues - " + nomComplet);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("GESTION DES LIGUES", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Sous-titre avec niveau d'accès
        String niveauTexte = getNiveauTexte(AccueilConnexion.UtilisateurConnecte.niveauAcces);
        JLabel sousTitre = new JLabel("Niveau d'accès : " + niveauTexte, SwingConstants.CENTER);
        sousTitre.setFont(new Font("Arial", Font.ITALIC, 14));
        sousTitre.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panneau pour titre et sous-titre
        JPanel panelTitre = new JPanel();
        panelTitre.setLayout(new BorderLayout());
        panelTitre.add(titre, BorderLayout.NORTH);
        panelTitre.add(sousTitre, BorderLayout.SOUTH);

        // Panneau des boutons - le nombre de lignes dépend du niveau d'accès
        int nombreBoutons = (AccueilConnexion.UtilisateurConnecte.niveauAcces == 3) ? 2 : 4; // Utilisateur simple : 2 boutons, Admin/Ligue : 4 boutons
        JPanel panelBoutons = new JPanel();
        panelBoutons.setLayout(new GridLayout(nombreBoutons, 1, 10, 10));
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        // Style des boutons
        Font fontBouton = new Font("Arial", Font.PLAIN, 16);
        Dimension tailleBouton = new Dimension(0, 60);

        // Bouton "Afficher les ligues" - accessible à tous
        JButton boutonAfficher = new JButton("Afficher les ligues");
        boutonAfficher.setFont(fontBouton);
        boutonAfficher.setPreferredSize(tailleBouton);
        boutonAfficher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherLigues();
            }
        });
        panelBoutons.add(boutonAfficher);

        // Boutons pour les niveaux 1 et 2 (Administrateur et Administrateur Ligue)
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 || AccueilConnexion.UtilisateurConnecte.niveauAcces == 2) {
            // Bouton "Ajouter une ligue"
            JButton boutonAjouter = new JButton("Ajouter une ligue");
            boutonAjouter.setFont(fontBouton);
            boutonAjouter.setPreferredSize(tailleBouton);
            boutonAjouter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ajouterLigue();
                }
            });
            panelBoutons.add(boutonAjouter);

            // Bouton "Sélectionner une ligue"
            JButton boutonSelectionner = new JButton("Sélectionner une ligue");
            boutonSelectionner.setFont(fontBouton);
            boutonSelectionner.setPreferredSize(tailleBouton);
            boutonSelectionner.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectionnerLigue();
                }
            });
            panelBoutons.add(boutonSelectionner);
        }

        // Bouton "Retour" - accessible à tous
        JButton boutonRetour = new JButton("Retour à l'accueil");
        boutonRetour.setFont(fontBouton);
        boutonRetour.setPreferredSize(tailleBouton);
        boutonRetour.setBackground(new Color(255, 230, 230)); // Couleur légèrement différente
        boutonRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retourAccueil();
            }
        });
        panelBoutons.add(boutonRetour);

        // Ajout des composants au panneau principal
        panelPrincipal.add(panelTitre, BorderLayout.NORTH);
        panelPrincipal.add(panelBoutons, BorderLayout.CENTER);

        // Information sur les droits d'accès en bas
        JLabel infoAcces = new JLabel(getInfoAcces(), SwingConstants.CENTER);
        infoAcces.setFont(new Font("Arial", Font.PLAIN, 12));
        infoAcces.setForeground(Color.GRAY);
        infoAcces.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(infoAcces, BorderLayout.SOUTH);

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
    
    private String getInfoAcces() {
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 3) {
            return "Accès limité : Consultation uniquement";
        } else {
            return "Accès complet : Consultation et modification";
        }
    }
    
    private void afficherLigues() {
        // Création et affichage de la fenêtre AfficherLigues
        AfficherLigues fenetreLigues = new AfficherLigues();
        fenetreLigues.setVisible(true);
    }

    
    private void ajouterLigue() {
        // Création et affichage de la fenêtre AfficherLigues
    	AjouterLigue fenetreLigues = new AjouterLigue();
        fenetreLigues.setVisible(true);
    }
    
    private void selectionnerLigue() {
        // Création et affichage de la fenêtre AfficherLigues
    	SelectionnerLigue fenetreLigues = new SelectionnerLigue();
        fenetreLigues.setVisible(true);
    }
    
    private void retourAccueil() {
        // Retour à la page d'accueil
        new Accueil();
        dispose();
    }
}