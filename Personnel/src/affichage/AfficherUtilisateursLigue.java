package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import jdbc.JDBC;

public class AfficherUtilisateursLigue extends JFrame {

    private JDBC jdbc;
    private int idLigue;
    private String nomLigue;
    private JTable tableUtilisateurs;
    private DefaultTableModel modelTable;

    public AfficherUtilisateursLigue(int idLigue, String nomLigue) {
        this.idLigue = idLigue;
        this.nomLigue = nomLigue;
        this.jdbc = new JDBC();

        initializeComponents();
        chargerUtilisateurs();
    }

    private void initializeComponents() {
        setTitle("Utilisateurs de la Ligue : " + nomLigue + " (ID: " + idLigue + ")");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titre = new JLabel("Utilisateurs de la ligue : " + nomLigue, SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titre, BorderLayout.NORTH);

        // Table des utilisateurs
        String[] colonnes = {"ID", "Nom", "Prénom", "Email", "Rôle"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table non éditable
            }
        };

        tableUtilisateurs = new JTable(modelTable);
        tableUtilisateurs.setFont(new Font("Arial", Font.PLAIN, 14));
        tableUtilisateurs.setRowHeight(30);
        tableUtilisateurs.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(tableUtilisateurs);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Bouton Fermer
        JButton boutonFermer = new JButton("Fermer");
        boutonFermer.addActionListener(e -> dispose());
        JPanel panelBas = new JPanel();
        panelBas.add(boutonFermer);

        add(panelBas, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void chargerUtilisateurs() {
        try {
            modelTable.setRowCount(0); // vider la table

            Connection connection = jdbc.getConnection();

            String sql = 
                "SELECT e.id, e.nom, e.prenom, e.mail, e.id_niveau_acces " +
                "FROM employe_ligue el " +
                "JOIN employe e ON e.id = el.id_employe " +
                "WHERE el.id_ligue = ? " +
                "ORDER BY e.nom, e.prenom";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idLigue);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String mail = rs.getString("mail");
                int niveauAcces = rs.getInt("id_niveau_acces");

                String role = (niveauAcces == 2) ? "Administrateur" : "Utilisateur";

                Object[] ligne = {id, nom, prenom, mail, role};
                modelTable.addRow(ligne);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des utilisateurs : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
