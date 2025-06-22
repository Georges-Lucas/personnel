package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import jdbc.JDBC;
import personnel.SauvegardeImpossible;

public class VoirLigues extends JFrame {
    private JDBC jdbc;
    private JTable tableLigues;
    private DefaultTableModel modeleTableLigues;
    private JTable tableUtilisateurs;
    private DefaultTableModel modeleTableUtilisateurs;
    private JButton btnRetour, btnRafraichir;
    private int ligueSelectionnee = -1;

    public VoirLigues() {
        setTitle("Visualisation des Ligues et leurs Utilisateurs");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        jdbc = new JDBC();
        creerInterface();
        chargerLigues();
        setVisible(true);
    }

    private void creerInterface() {
        setLayout(new BorderLayout());

        JLabel titre = new JLabel("GESTION DES LIGUES ET UTILISATEURS", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titre, BorderLayout.NORTH);

        // Panel principal avec split horizontal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        // Panel gauche - Ligues
        JPanel panelLigues = new JPanel(new BorderLayout());
        panelLigues.setBorder(BorderFactory.createTitledBorder("Ligues"));

        String[] colonnesLigues = {"ID", "Nom de la Ligue", "Nombre d'utilisateurs"};
        modeleTableLigues = new DefaultTableModel(colonnesLigues, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableLigues = new JTable(modeleTableLigues);
        tableLigues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLigues.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int ligneSelectionnee = tableLigues.getSelectedRow();
                if (ligneSelectionnee != -1) {
                    ligueSelectionnee = (Integer) modeleTableLigues.getValueAt(ligneSelectionnee, 0);
                    chargerUtilisateursLigue(ligueSelectionnee);
                }
            }
        });

        JScrollPane scrollPaneLigues = new JScrollPane(tableLigues);
        panelLigues.add(scrollPaneLigues, BorderLayout.CENTER);

        // Panel droit - Utilisateurs de la ligue sélectionnée
        JPanel panelUtilisateurs = new JPanel(new BorderLayout());
        panelUtilisateurs.setBorder(BorderFactory.createTitledBorder("Utilisateurs de la ligue sélectionnée"));

        String[] colonnesUtilisateurs = {"ID", "Nom", "Prénom", "Email", "Niveau d'accès"};
        modeleTableUtilisateurs = new DefaultTableModel(colonnesUtilisateurs, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableUtilisateurs = new JTable(modeleTableUtilisateurs);
        tableUtilisateurs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPaneUtilisateurs = new JScrollPane(tableUtilisateurs);
        panelUtilisateurs.add(scrollPaneUtilisateurs, BorderLayout.CENTER);

        splitPane.setLeftComponent(panelLigues);
        splitPane.setRightComponent(panelUtilisateurs);

        add(splitPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        btnRafraichir = new JButton("Rafraîchir");
        btnRetour = new JButton("Retour");

        panelBoutons.add(btnRafraichir);
        panelBoutons.add(btnRetour);

        add(panelBoutons, BorderLayout.SOUTH);

        ajouterEvenements();
    }

    private void ajouterEvenements() {
        btnRafraichir.addActionListener(e -> {
            chargerLigues();
            modeleTableUtilisateurs.setRowCount(0);
            ligueSelectionnee = -1;
        });

        btnRetour.addActionListener(e -> {
            dispose();
        });
    }

    private void chargerLigues() {
        try {
            Connection connection = jdbc.getConnection();
            modeleTableLigues.setRowCount(0);

            String query =
                "SELECT l.id, l.nom, " +
                "COUNT(el.id_employe) as nb_utilisateurs " +
                "FROM ligue l " +
                "LEFT JOIN employe_ligue el ON l.id = el.id_ligue " +
                "GROUP BY l.id, l.nom " +
                "ORDER BY l.nom";

            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> ligne = new Vector<>();
                ligne.add(rs.getInt("id"));
                ligne.add(rs.getString("nom"));
                ligne.add(rs.getInt("nb_utilisateurs"));
                modeleTableLigues.addRow(ligne);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des ligues : " + e.getMessage());
        }
    }

    private void chargerUtilisateursLigue(int idLigue) {
        try {
            Connection connection = jdbc.getConnection();
            modeleTableUtilisateurs.setRowCount(0);

            String query = 
                "SELECT e.id, e.nom, e.prenom, e.mail, na.niveau_acces " +
                "FROM employe e " +
                "INNER JOIN employe_ligue el ON e.id = el.id_employe " +
                "INNER JOIN niveau_acces na ON e.id_niveau_acces = na.id " +
                "WHERE el.id_ligue = ? " +
                "ORDER BY e.nom, e.prenom";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idLigue);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> ligne = new Vector<>();
                ligne.add(rs.getInt("id"));
                ligne.add(rs.getString("nom"));
                ligne.add(rs.getString("prenom"));
                ligne.add(rs.getString("mail"));
                ligne.add(rs.getString("niveau_acces"));
                modeleTableUtilisateurs.addRow(ligne);
            }

            // Mettre à jour le titre du panel
            String nomLigue = getNomLigue(idLigue);
            ((javax.swing.border.TitledBorder) ((JPanel) ((JSplitPane) getContentPane().getComponent(1)).getRightComponent()).getBorder())
                    .setTitle("Utilisateurs de la ligue : " + nomLigue);
            repaint();

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }
    private String getNomLigue(int idLigue) {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT nom_ligue FROM ligue WHERE id = ?");
            pstmt.setInt(1, idLigue);
            ResultSet rs = pstmt.executeQuery();

            String nom = "";
            if (rs.next()) {
                nom = rs.getString("nom_ligue");
            }

            rs.close();
            pstmt.close();
            return nom;

        } catch (SQLException e) {
            e.printStackTrace();
            return "Inconnue";
        }
    }

    public void fermerConnexion() {
        try {
            jdbc.close();
        } catch (SauvegardeImpossible e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        fermerConnexion();
        super.dispose();
    }
}