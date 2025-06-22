package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import jdbc.JDBC;

public class RetirerUtilisateurLigue extends JFrame {
    
    private JDBC jdbc;
    private int idLigue;
    private String nomLigue;
    private EditerLigue parentWindow;
    
    private JTable tableUtilisateursLigue;
    private DefaultTableModel modeleTableUtilisateurs;
    private JButton btnRetirer;
    private JButton btnAnnuler;
    private JButton btnActualiser;
    
    public RetirerUtilisateurLigue(int idLigue, String nomLigue, EditerLigue parent) {
        this.idLigue = idLigue;
        this.nomLigue = nomLigue;
        this.parentWindow = parent;
        this.jdbc = new JDBC();
        
        initializeComponents();
        chargerUtilisateursLigue();
        setVisible(true);
    }
    
    private void initializeComponents() {
        setTitle("Retirer un utilisateur de la ligue : " + nomLigue);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentWindow);
        setLayout(new BorderLayout());
        
        // Panel d'information
        JPanel panelInfo = new JPanel();
        panelInfo.setBorder(BorderFactory.createTitledBorder("Sélectionner un utilisateur à retirer"));
        JLabel labelInfo = new JLabel("Utilisateurs actuellement dans la ligue :");
        labelInfo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(labelInfo);
        add(panelInfo, BorderLayout.NORTH);
        
        // Table des utilisateurs de la ligue
        String[] colonnes = {"ID", "Nom", "Prénom", "Email", "Niveau d'accès"};
        modeleTableUtilisateurs = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableUtilisateursLigue = new JTable(modeleTableUtilisateurs);
        tableUtilisateursLigue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUtilisateursLigue.setFillsViewportHeight(true);
        
        // Double-clic pour retirer
        tableUtilisateursLigue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    retirerUtilisateurSelectionne();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableUtilisateursLigue);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        
        btnActualiser = new JButton("Actualiser");
        btnActualiser.addActionListener(e -> chargerUtilisateursLigue());
        panelBoutons.add(btnActualiser);
        
        btnRetirer = new JButton("Retirer de la ligue");
        btnRetirer.setBackground(new Color(255, 200, 200)); // Couleur d'avertissement
        btnRetirer.addActionListener(e -> retirerUtilisateurSelectionne());
        panelBoutons.add(btnRetirer);
        
        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        panelBoutons.add(btnAnnuler);
        
        add(panelBoutons, BorderLayout.SOUTH);
    }
    
    private void chargerUtilisateursLigue() {
        modeleTableUtilisateurs.setRowCount(0);
        
        try {
            Connection connection = jdbc.getConnection();
            String sql = "SELECT e.id, e.nom, e.prenom, e.mail, n.niveau_acces " +
                        "FROM employe e " +
                        "INNER JOIN employe_ligue el ON e.id = el.id_employe " +
                        "INNER JOIN niveau_acces n ON e.id_niveau_acces = n.id " +
                        "WHERE el.id_ligue = ? " +
                        "ORDER BY e.nom, e.prenom";
            
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
            
            if (modeleTableUtilisateurs.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun utilisateur dans cette ligue.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des utilisateurs : " + e.getMessage(), 
                "Erreur BD", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void retirerUtilisateurSelectionne() {
        int ligneSelectionnee = tableUtilisateursLigue.getSelectedRow();
        
        if (ligneSelectionnee == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un utilisateur à retirer.", 
                "Aucune sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idEmploye = (Integer) modeleTableUtilisateurs.getValueAt(ligneSelectionnee, 0);
        String nom = (String) modeleTableUtilisateurs.getValueAt(ligneSelectionnee, 1);
        String prenom = (String) modeleTableUtilisateurs.getValueAt(ligneSelectionnee, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Retirer " + prenom + " " + nom + " de la ligue " + nomLigue + " ?\n" +
            "Cette action supprimera l'association mais ne supprimera pas l'employé.", 
            "Confirmer le retrait", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = jdbc.getConnection();
                String sql = "DELETE FROM employe_ligue WHERE id_employe = ? AND id_ligue = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, idEmploye);
                pstmt.setInt(2, idLigue);
                
                int deleted = pstmt.executeUpdate();
                pstmt.close();
                
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, 
                        prenom + " " + nom + " a été retiré(e) avec succès de la ligue !", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualiser la fenêtre parent et cette fenêtre
                    parentWindow.chargerUtilisateursLigue();
                    chargerUtilisateursLigue();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Échec du retrait de l'utilisateur de la ligue.", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erreur SQL lors du retrait : " + e.getMessage(), 
                    "Erreur BD", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}