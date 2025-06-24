package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import jdbc.JDBC;

public class EditerLigue extends JFrame {

    private JDBC jdbc;
    private int idLigue;
    private String nomLigue;
    private int niveauAccesUtilisateur;

    private JLabel labelNomLigue;
    private JButton btnAffecterResponsable;
    private JTable tableUtilisateurs;
    private DefaultTableModel modeleTableUtilisateurs;

    public EditerLigue(int idLigue, String nomLigue, int niveauAccesUtilisateur) {
        this.idLigue = idLigue;
        this.nomLigue = nomLigue;
        this.niveauAccesUtilisateur = niveauAccesUtilisateur;
        this.jdbc = new JDBC();

        initializeComponents();
        chargerUtilisateursLigue();

        setTitle("Détails de la Ligue : " + nomLigue);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // --- Haut : Informations ligue ---
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Informations de la ligue"));

        labelNomLigue = new JLabel("Nom de la ligue : " + nomLigue);
        labelNomLigue.setFont(new Font("Arial", Font.BOLD, 20));
        labelNomLigue.setHorizontalAlignment(JLabel.CENTER);

        panelInfo.add(labelNomLigue, BorderLayout.CENTER);

        // Si niveau 1, afficher le bouton affecter responsable
        if (niveauAccesUtilisateur == 1) {
            btnAffecterResponsable = new JButton("Affecter / Changer Responsable de Ligue");
            btnAffecterResponsable.addActionListener(e -> ouvrirFenetreAffecterResponsable());
            panelInfo.add(btnAffecterResponsable, BorderLayout.SOUTH);
        }

        add(panelInfo, BorderLayout.NORTH);

        // --- Centre : Table utilisateurs ---
        JPanel panelUtilisateurs = new JPanel(new BorderLayout());
        panelUtilisateurs.setBorder(BorderFactory.createTitledBorder("Utilisateurs de la ligue"));

        String[] colonnes = {"ID Employé", "Nom", "Prénom", "Email", "Niveau d'accès"};
        modeleTableUtilisateurs = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableUtilisateurs = new JTable(modeleTableUtilisateurs);
        JScrollPane scrollPane = new JScrollPane(tableUtilisateurs);

        panelUtilisateurs.add(scrollPane, BorderLayout.CENTER);

        // --- Bas : Boutons ---
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnActualiser = new JButton("Actualiser la liste des Utilisateurs");
        btnActualiser.addActionListener(e -> chargerUtilisateursLigue());
        panelBoutons.add(btnActualiser);

        // Boutons pour niveau 1 et 2
        if (niveauAccesUtilisateur == 1 || niveauAccesUtilisateur == 2) {
            JButton btnAjouter = new JButton("Ajouter un Utilisateur");
            btnAjouter.addActionListener(e -> ajouterUtilisateurALigue());
            panelBoutons.add(btnAjouter);

            JButton btnRetirer = new JButton("Retirer Utilisateur");
            btnRetirer.addActionListener(e -> retirerUtilisateur());
            panelBoutons.add(btnRetirer);
        }

        // Bouton supprimer ligue pour niveau 1
        if (niveauAccesUtilisateur == 1) {
            JButton btnSupprimer = new JButton("Supprimer la Ligue");
            btnSupprimer.setBackground(new Color(255, 100, 100));
            btnSupprimer.addActionListener(e -> supprimerLigue());
            panelBoutons.add(btnSupprimer);
        }

        panelUtilisateurs.add(panelBoutons, BorderLayout.SOUTH);
        add(panelUtilisateurs, BorderLayout.CENTER);

        // --- Bas : Retour ---
        JPanel panelRetour = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRetour = new JButton("Retour aux Ligues");
        btnRetour.addActionListener(e -> {
            new SelectionnerLigue();
            dispose();
        });
        panelRetour.add(btnRetour);
        add(panelRetour, BorderLayout.SOUTH);
    }

    // --- Méthodes BDD ---

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
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ouvrirFenetreAffecterResponsable() {
        JDialog dialog = new JDialog(this, "Affecter / Changer Responsable", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JComboBox<String> comboResponsables = new JComboBox<>();
        try {
            Connection connection = jdbc.getConnection();
            String sql = "SELECT id, nom, prenom, mail, id_ligue FROM employe WHERE id_niveau_acces = 2 ORDER BY nom, prenom";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String mail = rs.getString("mail");
                int idLigueAffectee = rs.getInt("id_ligue");

                String ligueInfo = (idLigueAffectee == 0) ? "Aucune ligue" : "Ligue " + idLigueAffectee;
                comboResponsables.addItem(id + " - " + prenom + " " + nom + " (" + mail + ") - " + ligueInfo);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dialog.add(comboResponsables, BorderLayout.CENTER);

        JButton btnValider = new JButton("Valider l'affectation");
        btnValider.addActionListener(e -> {
            if (comboResponsables.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(dialog, "Sélectionnez un utilisateur.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selection = (String) comboResponsables.getSelectedItem();
            int idEmploye = Integer.parseInt(selection.split(" - ")[0]);

            try {
                Connection connection = jdbc.getConnection();

                // Mettre à jour id_ligue
                String sqlUpdate = "UPDATE employe SET id_ligue = ? WHERE id = ?";
                PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdate);
                pstmtUpdate.setInt(1, idLigue);
                pstmtUpdate.setInt(2, idEmploye);

                int updated = pstmtUpdate.executeUpdate();
                pstmtUpdate.close();

                if (updated > 0) {
                    JOptionPane.showMessageDialog(dialog, "Responsable affecté avec succès.");
                    chargerUtilisateursLigue();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'affectation.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Erreur SQL : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(btnValider, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void ajouterUtilisateurALigue() {
        // Ici tu peux ouvrir ta classe AjouterUtilisateurLigue (existant)
        new AjouterUtilisateurLigue(idLigue, nomLigue, this);
    }

    private void retirerUtilisateur() {
        new RetirerUtilisateurLigue(idLigue, nomLigue, this);
    }

    private void supprimerLigue() {
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cette ligue ? Cette action est irréversible.", "Confirmer", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = jdbc.getConnection();

                // 1. Libérer les responsables / employés liés à cette ligue
                String sqlUpdateEmployes = "UPDATE employe SET id_ligue = NULL WHERE id_ligue = ?";
                PreparedStatement pstmtEmployes = connection.prepareStatement(sqlUpdateEmployes);
                pstmtEmployes.setInt(1, idLigue);
                pstmtEmployes.executeUpdate();
                pstmtEmployes.close();

                // 2. Supprimer les associations dans employe_ligue
                String sqlDeleteAssociations = "DELETE FROM employe_ligue WHERE id_ligue = ?";
                PreparedStatement pstmtAssoc = connection.prepareStatement(sqlDeleteAssociations);
                pstmtAssoc.setInt(1, idLigue);
                pstmtAssoc.executeUpdate();
                pstmtAssoc.close();

                // 3. Supprimer la ligue
                String sqlDeleteLigue = "DELETE FROM ligue WHERE id = ?";
                PreparedStatement pstmtLigue = connection.prepareStatement(sqlDeleteLigue);
                pstmtLigue.setInt(1, idLigue);
                int deleted = pstmtLigue.executeUpdate();
                pstmtLigue.close();

                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Ligue supprimée.");
                    new SelectionnerLigue();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur SQL : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
