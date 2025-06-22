package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import jdbc.JDBC;

public class AfficherLigues extends JFrame {
    
    private JDBC jdbc;
    private JTable tableLigues;
    private DefaultTableModel modelTable;
    
    public AfficherLigues() {
        jdbc = new JDBC();
        initializeComponents();
        chargerLigues();
    }
    
    private void initializeComponents() {
        // Configuration de la fenêtre
        String nomComplet = AccueilConnexion.UtilisateurConnecte.prenom + " " + AccueilConnexion.UtilisateurConnecte.nom;
        setTitle("Affichage des Ligues - " + nomComplet);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panneau principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("LISTE DES LIGUES", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelPrincipal.add(titre, BorderLayout.NORTH);

        // Création du modèle de table
        String[] colonnes = {"ID", "Nom de la Ligue"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre la table non-éditable
            }
        };

        // Création de la table
        tableLigues = new JTable(modelTable);
        tableLigues.setFont(new Font("Arial", Font.PLAIN, 14));
        tableLigues.setRowHeight(25);
        tableLigues.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tableLigues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Scroll pane pour la table
        JScrollPane scrollPane = new JScrollPane(tableLigues);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        
        // Bouton Actualiser
        JButton boutonActualiser = new JButton("Actualiser");
        boutonActualiser.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonActualiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chargerLigues();
            }
        });
        panelBoutons.add(boutonActualiser);
        
        // Bouton Retour
        JButton boutonRetour = new JButton("Retour");
        boutonRetour.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestionLigues();
                dispose();
            }
        });
        panelBoutons.add(boutonRetour);

        panelPrincipal.add(panelBoutons, BorderLayout.SOUTH);

        add(panelPrincipal);
        setVisible(true);
    }
    
    private void chargerLigues() {
        try {
            // Vider la table
            modelTable.setRowCount(0);
            
            Connection connection = jdbc.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom FROM ligue ORDER BY nom");
            
            while (rs.next()) {
                Object[] ligne = {
                    rs.getInt("id"),
                    rs.getString("nom")
                };
                modelTable.addRow(ligne);
            }
            
            rs.close();
            stmt.close();
            
            // Afficher le nombre de ligues
            setTitle("Affichage des Ligues - " + modelTable.getRowCount() + " ligue(s) trouvée(s)");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des ligues : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}