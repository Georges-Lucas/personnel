package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import jdbc.JDBC;

public class AjouterUtilisateurLigue extends JFrame {
    
    private JDBC jdbc;
    private int idLigue;
    private String nomLigue;
    private EditerLigue parentWindow;
    
    private JTable tableEmployesDisponibles;
    private DefaultTableModel modeleTableEmployes;
    private JButton btnAjouter;
    private JButton btnAnnuler;
    private JButton btnActualiser;
    
    public AjouterUtilisateurLigue(int idLigue, String nomLigue, EditerLigue parent) {
        this.idLigue = idLigue;
        this.nomLigue = nomLigue;
        this.parentWindow = parent;
        this.jdbc = new JDBC();
        
        initializeComponents();
        chargerEmployesDisponibles();
        setVisible(true);
    }
    
    private void initializeComponents() {
        setTitle("Ajouter un utilisateur à la ligue : " + nomLigue);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentWindow);
        setLayout(new BorderLayout());
        
        // Panel d'information
        JPanel panelInfo = new JPanel();
        panelInfo.setBorder(BorderFactory.createTitledBorder("Sélectionner un employé à ajouter"));
        JLabel labelInfo = new JLabel("Employés disponibles (non encore dans la ligue) :");
        labelInfo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(labelInfo);
        add(panelInfo, BorderLayout.NORTH);
        
        // Table des employés disponibles
        String[] colonnes = {"ID", "Nom", "Prénom", "Email", "Niveau d'accès"};
        modeleTableEmployes = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableEmployesDisponibles = new JTable(modeleTableEmployes);
        tableEmployesDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEmployesDisponibles.setFillsViewportHeight(true);
        
        // Double-clic pour ajouter
        tableEmployesDisponibles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ajouterEmployeSelectionne();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableEmployesDisponibles);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        
        btnActualiser = new JButton("Actualiser");
        btnActualiser.addActionListener(e -> chargerEmployesDisponibles());
        panelBoutons.add(btnActualiser);
        
        btnAjouter = new JButton("Ajouter à la ligue");
        btnAjouter.addActionListener(e -> ajouterEmployeSelectionne());
        panelBoutons.add(btnAjouter);
        
        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        panelBoutons.add(btnAnnuler);
        
        add(panelBoutons, BorderLayout.SOUTH);
    }
    
    private void chargerEmployesDisponibles() {
        modeleTableEmployes.setRowCount(0);
        
        try {
            Connection connection = jdbc.getConnection();
            String sql = "SELECT e.id, e.nom, e.prenom, e.mail, n.niveau_acces " +
                        "FROM employe e " +
                        "INNER JOIN niveau_acces n ON e.id_niveau_acces = n.id " +
                        "WHERE e.id NOT IN (SELECT el.id_employe FROM employe_ligue el WHERE el.id_ligue = ?) " +
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
                modeleTableEmployes.addRow(row);
            }
            
            rs.close();
            pstmt.close();
            
            if (modeleTableEmployes.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun employé disponible à ajouter à cette ligue.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des employés : " + e.getMessage(), 
                "Erreur BD", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ajouterEmployeSelectionne() {
        int ligneSelectionnee = tableEmployesDisponibles.getSelectedRow();
        
        if (ligneSelectionnee == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un employé à ajouter.", 
                "Aucune sélection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idEmploye = (Integer) modeleTableEmployes.getValueAt(ligneSelectionnee, 0);
        String nom = (String) modeleTableEmployes.getValueAt(ligneSelectionnee, 1);
        String prenom = (String) modeleTableEmployes.getValueAt(ligneSelectionnee, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Ajouter " + prenom + " " + nom + " à la ligue " + nomLigue + " ?", 
            "Confirmer l'ajout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = jdbc.getConnection();
                String sql = "INSERT INTO employe_ligue (id_employe, id_ligue) VALUES (?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, idEmploye);
                pstmt.setInt(2, idLigue);
                
                int inserted = pstmt.executeUpdate();
                pstmt.close();
                
                if (inserted > 0) {
                    JOptionPane.showMessageDialog(this, 
                        prenom + " " + nom + " a été ajouté(e) avec succès à la ligue !", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualiser la fenêtre parent et cette fenêtre
                    parentWindow.chargerUtilisateursLigue();
                    chargerEmployesDisponibles();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Échec de l'ajout de l'employé à la ligue.", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erreur SQL lors de l'ajout : " + e.getMessage(), 
                    "Erreur BD", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}