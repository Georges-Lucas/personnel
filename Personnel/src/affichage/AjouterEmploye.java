package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import jdbc.JDBC;

public class AjouterEmploye extends JFrame {

    private JDBC jdbc;
    private int idLigue;
    private JTable tableEmployes;
    private DefaultTableModel modelTable;
    private JButton boutonAjouter;

    public AjouterEmploye(int idLigue) {
        this.idLigue = idLigue;
        this.jdbc = new JDBC();

        initializeComponents();
        chargerEmployes();
    }

    private void initializeComponents() {
        setTitle("Ajouter un employé à la ligue (ID: " + idLigue + ")");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titre = new JLabel("Sélectionnez un employé à ajouter à la ligue", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titre, BorderLayout.NORTH);

        // Table des employés
        String[] colonnes = {"ID", "Nom", "Prénom", "Email"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table non éditable
            }
        };

        tableEmployes = new JTable(modelTable);
        tableEmployes.setFont(new Font("Arial", Font.PLAIN, 14));
        tableEmployes.setRowHeight(30);
        tableEmployes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tableEmployes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableEmployes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());

        boutonAjouter = new JButton("Ajouter à la ligue");
        boutonAjouter.setEnabled(false);
        boutonAjouter.addActionListener(e -> ajouterEmployeALigue());
        panelBoutons.add(boutonAjouter);

        JButton boutonAnnuler = new JButton("Annuler");
        boutonAnnuler.addActionListener(e -> dispose());
        panelBoutons.add(boutonAnnuler);

        add(panelBoutons, BorderLayout.SOUTH);

        // Activer bouton "Ajouter" quand une ligne est sélectionnée
        tableEmployes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boutonAjouter.setEnabled(tableEmployes.getSelectedRow() != -1);
            }
        });

        setVisible(true);
    }

    private void chargerEmployes() {
        try {
            modelTable.setRowCount(0); // vider la table

            Connection connection = jdbc.getConnection();

            // On va lister les employés qui ne sont PAS déjà dans cette ligue
            String sql = 
            	    "SELECT e.id, e.nom, e.prenom, e.mail " +
            	    "FROM employe e " +
            	    "WHERE NOT EXISTS ( " +
            	    "    SELECT 1 FROM employe_ligue el " +
            	    "    WHERE el.id_employe = e.id AND el.id_ligue = ? " +
            	    ") " +
            	    "ORDER BY e.nom, e.prenom";


            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idLigue);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] ligne = {
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("mail")
                };
                modelTable.addRow(ligne);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des employés : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterEmployeALigue() {
        int selectedRow = tableEmployes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un employé.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmploye = (Integer) modelTable.getValueAt(selectedRow, 0);

        try {
            Connection connection = jdbc.getConnection();

            String sql = "INSERT INTO employe_ligue (id_employe, id_ligue) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idEmploye);
            pstmt.setInt(2, idLigue);

            int rowsInserted = pstmt.executeUpdate();
            pstmt.close();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, 
                    "L'employé a été ajouté à la ligue !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'ajout.", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, 
                    "Cet employé fait déjà partie de cette ligue.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erreur SQL : " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
