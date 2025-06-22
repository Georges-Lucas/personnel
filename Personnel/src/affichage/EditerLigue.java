package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

import jdbc.JDBC;

public class EditerLigue extends JFrame {

    private JDBC jdbc;
    private int idLigue;
    private String nomLigue;
    private int niveauAccesUtilisateur;

    // Déclarations de tous les composants d'interface graphique
    private JLabel labelNomLigue;
    private JTextField champNomLigue;
    private JButton btnRenommerLigue;
    private JButton btnSupprimerLigue;
    private JButton btnActualiserUtilisateurs; // Renommé pour correspondre à sa fonction
    private JTable tableUtilisateurs;
    private DefaultTableModel modeleTableUtilisateurs;
    private JButton btnAjouterUtilisateur; // Renommé pour correspondre à la demande
    // private JButton btnChangerRole; // Ce bouton est supprimé
    private JButton btnRetirerUtilisateur;
    private JButton btnRetour;


    public EditerLigue(int idLigue, String nomLigue, int niveauAccesUtilisateur) {
        this.idLigue = idLigue;
        this.nomLigue = nomLigue;
        this.niveauAccesUtilisateur = niveauAccesUtilisateur;
        this.jdbc = new JDBC();

        try {
            initializeComponents();
            chargerDetailsLigue();
            chargerUtilisateursLigue(); // Appel automatique pour charger les utilisateurs
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Une erreur est survenue lors du chargement de la ligue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
        
        setVisible(true);
    }

    private void initializeComponents() {
        // Configuration de la fenêtre EditerLigue
        setTitle("Détails de la Ligue : " + nomLigue);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panneau d'informations de la ligue ---
        JPanel panelInfoLigue = new JPanel(new GridBagLayout());
        panelInfoLigue.setBorder(BorderFactory.createTitledBorder("Informations de la ligue"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        labelNomLigue = new JLabel("Nom de la ligue : " + nomLigue);
        labelNomLigue.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelInfoLigue.add(labelNomLigue, gbc);

        // Champ pour renommer la ligue (visible seulement pour niveau 1 et 2)
        if (niveauAccesUtilisateur == 1 || niveauAccesUtilisateur == 2) {
            champNomLigue = new JTextField(nomLigue);
            gbc.gridy = 1; gbc.gridwidth = 1;
            panelInfoLigue.add(new JLabel("Nouveau nom :"), gbc);
            gbc.gridx = 1;
            panelInfoLigue.add(champNomLigue, gbc);

            btnRenommerLigue = new JButton("Renommer Ligue");
            btnRenommerLigue.addActionListener(e -> renommerLigue());
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            panelInfoLigue.add(btnRenommerLigue, gbc);

            btnSupprimerLigue = new JButton("Supprimer Ligue");
            btnSupprimerLigue.setBackground(new Color(255, 200, 200)); 
            btnSupprimerLigue.addActionListener(e -> supprimerLigue());
            gbc.gridy = 3;
            panelInfoLigue.add(btnSupprimerLigue, gbc);
        }

        add(panelInfoLigue, BorderLayout.NORTH); 


        // --- Panneau des utilisateurs de la ligue (Tableau) ---
        JPanel panelUtilisateurs = new JPanel(new BorderLayout());
        panelUtilisateurs.setBorder(BorderFactory.createTitledBorder("Utilisateurs de la ligue"));

        String[] colonnesUtilisateurs = {"ID Employé", "Nom", "Prénom", "Email", "Niveau d'accès"};
        modeleTableUtilisateurs = new DefaultTableModel(colonnesUtilisateurs, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableUtilisateurs = new JTable(modeleTableUtilisateurs);
        tableUtilisateurs.setFillsViewportHeight(true);
        JScrollPane scrollPaneUtilisateurs = new JScrollPane(tableUtilisateurs);
        panelUtilisateurs.add(scrollPaneUtilisateurs, BorderLayout.CENTER);

        // --- Boutons de gestion des utilisateurs ---
        JPanel panelBoutonsUtilisateurs = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnActualiserUtilisateurs = new JButton("Actualiser la liste des Utilisateurs"); 
        btnActualiserUtilisateurs.addActionListener(e -> chargerUtilisateursLigue());
        panelBoutonsUtilisateurs.add(btnActualiserUtilisateurs);

        // Boutons pour les niveaux 1 et 2 (Ajouter, Retirer Utilisateur)
        if (niveauAccesUtilisateur == 1 || niveauAccesUtilisateur == 2) {
            btnAjouterUtilisateur = new JButton("Ajouter un Utilisateur à la ligue"); // Nom changé ici
            btnAjouterUtilisateur.addActionListener(e -> ajouterUtilisateurALigue());
            panelBoutonsUtilisateurs.add(btnAjouterUtilisateur);

            // btnChangerRole = new JButton("Changer Rôle Utilisateur"); // Supprimé
            // btnChangerRole.addActionListener(e -> changerRoleUtilisateur()); // Supprimé
            // panelBoutonsUtilisateurs.add(btnChangerRole); // Supprimé

            btnRetirerUtilisateur = new JButton("Retirer Utilisateur");
            btnRetirerUtilisateur.addActionListener(e -> retirerUtilisateur());
            panelBoutonsUtilisateurs.add(btnRetirerUtilisateur);
        }


        panelUtilisateurs.add(panelBoutonsUtilisateurs, BorderLayout.SOUTH);
        add(panelUtilisateurs, BorderLayout.CENTER);


        // --- Bouton Retour ---
        JPanel panelRetour = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRetour = new JButton("Retour aux Ligues");
        btnRetour.addActionListener(e -> {
            new SelectionnerLigue(); 
            dispose();
        });
        panelRetour.add(btnRetour);
        add(panelRetour, BorderLayout.SOUTH);
    }

    private void chargerDetailsLigue() {
        if (labelNomLigue != null) { 
            labelNomLigue.setText("Nom de la ligue : " + nomLigue);
        }
    }

    // Méthode rendue publique pour permettre l'actualisation depuis les fenêtres enfants
    public void chargerUtilisateursLigue() {
        modeleTableUtilisateurs.setRowCount(0);

        try {
            Connection connection = jdbc.getConnection();
            String sql = "SELECT e.id, e.nom, e.prenom, e.mail, n.niveau_acces " +
                         "FROM employe e " +
                         "INNER JOIN employe_ligue el ON e.id = el.id_employe " + 
                         "INNER JOIN niveau_acces n ON e.id_niveau_acces = n.id " +
                         "WHERE el.id_ligue = ? ORDER BY e.nom"; 
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idLigue);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("mail"),
                    rs.getString("niveau_acces") 
                };
                modeleTableUtilisateurs.addRow(row);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs de la ligue : " + e.getMessage(), "Erreur BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renommerLigue() {
        String nouveauNom = champNomLigue.getText().trim();
        if (nouveauNom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom de la ligue ne peut pas être vide.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Connection connection = jdbc.getConnection();
            String sql = "UPDATE ligue SET nom = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nouveauNom);
            pstmt.setInt(2, idLigue);
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Ligue renommée avec succès !");
                this.nomLigue = nouveauNom; 
                labelNomLigue.setText("Nom de la ligue : " + nomLigue); 
                setTitle("Détails de la Ligue : " + nomLigue); 
            } else {
                JOptionPane.showMessageDialog(this, "Échec du renommage de la ligue.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL lors du renommage : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerLigue() {
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette ligue ? Cette action est irréversible et supprimera également les associations avec les employés.", "Confirmer suppression", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = jdbc.getConnection();
                // Supprimer les associations employe_ligue
                String sqlDeleteAssociations = "DELETE FROM employe_ligue WHERE id_ligue = ?";
                PreparedStatement pstmtAssoc = connection.prepareStatement(sqlDeleteAssociations);
                pstmtAssoc.setInt(1, idLigue);
                pstmtAssoc.executeUpdate();
                pstmtAssoc.close();

                // Supprimer la ligue elle-même
                String sqlDeleteLigue = "DELETE FROM ligue WHERE id = ?";
                PreparedStatement pstmtLigue = connection.prepareStatement(sqlDeleteLigue);
                pstmtLigue.setInt(1, idLigue);
                int deleted = pstmtLigue.executeUpdate();
                pstmtLigue.close();

                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Ligue supprimée avec succès !");
                    new SelectionnerLigue(); 
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de la suppression de la ligue.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur SQL lors de la suppression : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Méthode modifiée pour ouvrir la fenêtre d'ajout d'utilisateur
    private void ajouterUtilisateurALigue() {
        new AjouterUtilisateurLigue(idLigue, nomLigue, this);
    }

    // private void changerRoleUtilisateur() { // Cette méthode est supprimée
    //     JOptionPane.showMessageDialog(this, "Fonctionnalité 'Changer Rôle Utilisateur' à implémenter.");
    // }

    // Méthode modifiée pour ouvrir la fenêtre de retrait d'utilisateur
    private void retirerUtilisateur() {
        new RetirerUtilisateurLigue(idLigue, nomLigue, this);
    }

    public void fermerConnexion() {
        try {
            if (jdbc != null) {
                // jdbc.close(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}