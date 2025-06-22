package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import jdbc.JDBC;

public class SelectionnerLigue extends JFrame {
    
    private JDBC jdbc;
    private JTable tableLigues;
    private DefaultTableModel modelTable;
    private JButton boutonSelectionner;
    private JButton boutonRetour;
    
    public SelectionnerLigue() {
        jdbc = new JDBC();
        initializeComponents();
        chargerLigues();
    }
    
    private void initializeComponents() {
        // Configuration de la fenêtre
        String nomComplet = AccueilConnexion.UtilisateurConnecte.prenom + " " + AccueilConnexion.UtilisateurConnecte.nom;
        setTitle("Sélectionner une Ligue - " + nomComplet);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panneau principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("SÉLECTIONNER UNE LIGUE À MODIFIER", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelPrincipal.add(titre, BorderLayout.NORTH);

        // Instructions
        JLabel instructions = new JLabel("Double-cliquez sur une ligue ou sélectionnez-la et cliquez sur 'Éditer'", SwingConstants.CENTER);
        instructions.setFont(new Font("Arial", Font.ITALIC, 12));
        instructions.setForeground(Color.GRAY);
        
        JPanel panelTitre = new JPanel(new BorderLayout());
        panelTitre.add(titre, BorderLayout.NORTH);
        panelTitre.add(instructions, BorderLayout.SOUTH);
        panelPrincipal.add(panelTitre, BorderLayout.NORTH);

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
        tableLigues.setRowHeight(30);
        tableLigues.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tableLigues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener pour la sélection
        tableLigues.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boutonSelectionner.setEnabled(tableLigues.getSelectedRow() != -1);
            }
        });
        
        // Double-clic pour éditer
        tableLigues.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tableLigues.getSelectedRow();
                    if (selectedRow != -1) {
                        editerLigueSelectionnee();
                    }
                }
            }
        });

        // Scroll pane pour la table
        JScrollPane scrollPane = new JScrollPane(tableLigues);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        
        // Bouton Sélectionner/Éditer
        boutonSelectionner = new JButton("Éditer la ligue sélectionnée");
        boutonSelectionner.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonSelectionner.setBackground(new Color(173, 216, 230)); // Bleu clair
        boutonSelectionner.setEnabled(false); // Désactivé par défaut
        boutonSelectionner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editerLigueSelectionnee();
            }
        });
        panelBoutons.add(boutonSelectionner);
        
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
        boutonRetour = new JButton("Retour");
        boutonRetour.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonRetour.setBackground(new Color(255, 182, 193)); // Rose clair
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
            
            // Mettre à jour le titre avec le nombre de ligues
            String nomComplet = AccueilConnexion.UtilisateurConnecte.prenom + " " + AccueilConnexion.UtilisateurConnecte.nom;
            setTitle("Sélectionner une Ligue - " + modelTable.getRowCount() + " ligue(s) disponible(s)");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des ligues : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editerLigueSelectionnee() {
        int selectedRow = tableLigues.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une ligue à éditer.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Récupérer les données de la ligue sélectionnée
        int idLigue = (Integer) modelTable.getValueAt(selectedRow, 0);
        String nomLigue = (String) modelTable.getValueAt(selectedRow, 1);
        
        // Ouvrir la page d'édition de la ligue
        new EditerLigue(idLigue, nomLigue);
        dispose();
    }
}