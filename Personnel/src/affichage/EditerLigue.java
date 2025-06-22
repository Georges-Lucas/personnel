package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import jdbc.JDBC;
import java.sql.*;

public class EditerLigue extends JFrame {

    private JDBC jdbc;
    private int idLigue;
    private String nomLigue;

    public EditerLigue(int idLigue, String nomLigue) {
        this.idLigue = idLigue;
        this.nomLigue = nomLigue;
        this.jdbc = new JDBC();

        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Éditer la Ligue : " + nomLigue + " (ID: " + idLigue + ")");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Titre principal
        JLabel labelTitre = new JLabel("Gestion de la ligue : " + nomLigue, SwingConstants.CENTER);
        labelTitre.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitre.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(labelTitre, BorderLayout.NORTH);

        // Panneau central pour les boutons
        JPanel panelBoutons = new JPanel();
        panelBoutons.setLayout(new GridLayout(5, 1, 10, 10));
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Bouton 1 : Afficher utilisateurs et administrateurs
        JButton boutonAfficherUtilisateurs = new JButton("Afficher les utilisateurs et administrateurs");
        boutonAfficherUtilisateurs.addActionListener(e -> afficherUtilisateurs());
        panelBoutons.add(boutonAfficherUtilisateurs);

        // Bouton 2 : Ajouter un employé
        JButton boutonAjouterEmploye = new JButton("Ajouter un employé à la ligue");
        boutonAjouterEmploye.addActionListener(e -> ajouterEmploye());
        panelBoutons.add(boutonAjouterEmploye);

        // Bouton 3 : Renommer la ligue
        JButton boutonRenommerLigue = new JButton("Renommer la ligue");
        boutonRenommerLigue.addActionListener(e -> renommerLigue());
        panelBoutons.add(boutonRenommerLigue);

        // Bouton 4 : Supprimer la ligue
        JButton boutonSupprimerLigue = new JButton("Supprimer la ligue");
        boutonSupprimerLigue.setForeground(Color.RED);
        boutonSupprimerLigue.addActionListener(e -> supprimerLigue());
        panelBoutons.add(boutonSupprimerLigue);

        // Bouton 5 : Retour
        JButton boutonRetour = new JButton("Retour");
        boutonRetour.addActionListener(e -> {
            new SelectionnerLigue(); // Retourner à la sélection des ligues
            dispose();
        });
        panelBoutons.add(boutonRetour);

        add(panelBoutons, BorderLayout.CENTER);

        setVisible(true);
    }

    // ACTIONS DES BOUTONS :

    private void afficherUtilisateurs() {
        new AfficherUtilisateursLigue(idLigue, nomLigue);
    }


    private void ajouterEmploye() {
        // Ouvre le formulaire d'ajout d'employé pour cette ligue
        new AjouterEmploye(idLigue);
    }

    private void renommerLigue() {
        String nouveauNom = JOptionPane.showInputDialog(this, 
            "Nouveau nom pour la ligue :", nomLigue);

        if (nouveauNom != null && !nouveauNom.trim().isEmpty()) {
            try {
                Connection connection = jdbc.getConnection();
                String sql = "UPDATE ligue SET nom = ? WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nouveauNom.trim());
                pstmt.setInt(2, idLigue);

                int rowsUpdated = pstmt.executeUpdate();
                pstmt.close();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Le nom de la ligue a été mis à jour avec succès.");
                    nomLigue = nouveauNom.trim(); // Mettre à jour le titre
                    setTitle("Éditer la Ligue : " + nomLigue + " (ID: " + idLigue + ")");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la mise à jour.", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erreur SQL : " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerLigue() {
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer cette ligue ?", 
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                Connection connection = jdbc.getConnection();

                // Supprimer d'abord les liaisons dans employe_ligue (sinon FK ERROR)
                String sqlDeleteLiaisons = "DELETE FROM employe_ligue WHERE id_ligue = ?";
                PreparedStatement pstmt1 = connection.prepareStatement(sqlDeleteLiaisons);
                pstmt1.setInt(1, idLigue);
                pstmt1.executeUpdate();
                pstmt1.close();

                // Ensuite supprimer la ligue
                String sqlDeleteLigue = "DELETE FROM ligue WHERE id = ?";
                PreparedStatement pstmt2 = connection.prepareStatement(sqlDeleteLigue);
                pstmt2.setInt(1, idLigue);

                int rowsDeleted = pstmt2.executeUpdate();
                pstmt2.close();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "La ligue a été supprimée avec succès.");
                    new SelectionnerLigue(); // Retour à la liste
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression.", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erreur SQL : " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
