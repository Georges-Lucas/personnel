package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import jdbc.JDBC;

public class AffectationLigue extends JFrame {

    private JDBC jdbc;
    private JComboBox<String> comboEmployes;
    private JComboBox<String> comboLigues;
    private JButton btnAffecter, btnFermer;

    public AffectationLigue() {
        jdbc = new JDBC();

        setTitle("Affectation Employé à une Ligue");
        setSize(500, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        creerInterface();
        chargerEmployes();
        chargerLigues();

        setVisible(true);
    }

    private void creerInterface() {
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Sélectionnez un employé :"));
        comboEmployes = new JComboBox<>();
        add(comboEmployes);

        add(new JLabel("Sélectionnez une ligue :"));
        comboLigues = new JComboBox<>();
        add(comboLigues);

        btnAffecter = new JButton("Affecter");
        btnFermer = new JButton("Fermer");

        add(btnAffecter);
        add(btnFermer);

        btnAffecter.addActionListener(e -> affecterEmployeLigue());
        btnFermer.addActionListener(e -> dispose());
    }

    private void chargerEmployes() {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT id, nom, prenom FROM employe ORDER BY nom, prenom"
            );
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                comboEmployes.addItem(id + " - " + prenom + " " + nom);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des employés : " + e.getMessage());
        }
    }

    private void chargerLigues() {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT id, nom FROM ligue ORDER BY nom"
            );
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                comboLigues.addItem(id + " - " + nom);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des ligues : " + e.getMessage());
        }
    }

    private void affecterEmployeLigue() {
        if (comboEmployes.getSelectedItem() == null || comboLigues.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un employé et une ligue !");
            return;
        }

        String employeSelectionne = (String) comboEmployes.getSelectedItem();
        int employeId = Integer.parseInt(employeSelectionne.split(" - ")[0]);

        String ligueSelectionnee = (String) comboLigues.getSelectedItem();
        int ligueId = Integer.parseInt(ligueSelectionnee.split(" - ")[0]);

        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE employe SET id_ligue = ? WHERE id = ?"
            );
            pstmt.setInt(1, ligueId);
            pstmt.setInt(2, employeId);

            int res = pstmt.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(this, "Affectation réussie !");
            } else {
                JOptionPane.showMessageDialog(this, "Aucune modification effectuée.");
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'affectation : " + e.getMessage());
        }
    }

    public void fermerConnexion() {
        try {
            jdbc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
