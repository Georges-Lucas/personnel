package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Assurez-vous que cette importation est correcte selon la localisation de votre classe
// Si tout est dans le même package "affichage", cette ligne n'est pas nécessaire.
// Si elle pose problème, supprimez-la si AccueilConnexion est aussi dans le package "affichage".
// import AccueilConnexion.UtilisateurConnecte; 

public class GestionLigues extends JFrame {
    
    public GestionLigues() {
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Configuration de la fenêtre
        String prenom = AccueilConnexion.UtilisateurConnecte.prenom != null ? AccueilConnexion.UtilisateurConnecte.prenom : "";
        String nom = AccueilConnexion.UtilisateurConnecte.nom != null ? AccueilConnexion.UtilisateurConnecte.nom : "";
        String nomComplet = (prenom + " " + nom).trim();

        setTitle("Gestion des Ligues - " + nomComplet);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panneau principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        // Titre et sous-titre
        JLabel titre = new JLabel("GESTION DES LIGUES", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        String niveauTexte = getNiveauTexte(AccueilConnexion.UtilisateurConnecte.niveauAcces);
        JLabel sousTitre = new JLabel("Niveau d'accès : " + niveauTexte, SwingConstants.CENTER);
        sousTitre.setFont(new Font("Arial", Font.ITALIC, 14));
        sousTitre.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel panelTitre = new JPanel();
        panelTitre.setLayout(new BorderLayout());
        panelTitre.add(titre, BorderLayout.NORTH);
        panelTitre.add(sousTitre, BorderLayout.SOUTH);

        JPanel panelBoutons = new JPanel();
        panelBoutons.setLayout(new GridLayout(0, 1, 10, 10)); // 0 lignes, 1 colonne
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

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

        // Bouton "Sélectionner une ligue" - maintenant accessible à TOUS
        JButton boutonSelectionner = new JButton("Sélectionner une ligue");
        boutonSelectionner.setFont(fontBouton);
        boutonSelectionner.setPreferredSize(tailleBouton);
        boutonSelectionner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionnerLigue();
            }
        });
        panelBoutons.add(boutonSelectionner); // Ajouté inconditionnellement

        // Bouton "Ajouter une ligue" - Visible uniquement pour les niveaux 1 et 2
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 || AccueilConnexion.UtilisateurConnecte.niveauAcces == 2) {
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
        }
            
        // Bouton "Retour" - accessible à tous
        JButton boutonRetour = new JButton("Retour à l'accueil");
        boutonRetour.setFont(fontBouton);
        boutonRetour.setPreferredSize(tailleBouton);
        boutonRetour.setBackground(new Color(255, 230, 230));
        boutonRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retourAccueil();
            }
        });
        panelBoutons.add(boutonRetour);

        panelPrincipal.add(panelTitre, BorderLayout.NORTH);
        panelPrincipal.add(panelBoutons, BorderLayout.CENTER);

        // Information sur les droits d'accès en bas
        JLabel infoAcces = new JLabel(getInfoAcces(), SwingConstants.CENTER);
        infoAcces.setFont(new Font("Arial", Font.PLAIN, 12));
        infoAcces.setForeground(Color.GRAY);
        infoAcces.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(infoAcces, BorderLayout.SOUTH);

        add(panelPrincipal);
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
        new AfficherLigues();
        dispose();
    }

    private void ajouterLigue() {
        new AjouterLigue();
        dispose();
    }
    
    private void selectionnerLigue() {
        // C'est ici que SelectionnerLigue est appelée.
        // SelectionnerLigue sera chargée de masquer/activer ses propres boutons d'édition.
        new SelectionnerLigue(); 
        dispose();
    }
    
    private void retourAccueil() {
        new Accueil();
        dispose();
    }
}