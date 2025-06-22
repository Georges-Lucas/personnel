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
    private JButton boutonAjouterLigue;

    public SelectionnerLigue() {
        jdbc = new JDBC();
        initializeComponents();
        chargerLigues();
    }

    private void initializeComponents() {
        String prenom = AccueilConnexion.UtilisateurConnecte.prenom != null ? AccueilConnexion.UtilisateurConnecte.prenom : "";
        String nom = AccueilConnexion.UtilisateurConnecte.nom != null ? AccueilConnexion.UtilisateurConnecte.nom : "";
        String nomComplet = (prenom + " " + nom).trim();

        setTitle("Sélectionner une Ligue - " + nomComplet);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JLabel titre = new JLabel("SÉLECTIONNER UNE LIGUE", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JLabel instructions = new JLabel("", SwingConstants.CENTER);
        instructions.setFont(new Font("Arial", Font.ITALIC, 12));
        instructions.setForeground(Color.GRAY);

        JPanel panelTitre = new JPanel(new BorderLayout());
        panelTitre.add(titre, BorderLayout.NORTH);
        panelTitre.add(instructions, BorderLayout.SOUTH);
        panelPrincipal.add(panelTitre, BorderLayout.NORTH);

        int niveauAcces = AccueilConnexion.UtilisateurConnecte.niveauAcces;

        if (niveauAcces == 1 || niveauAcces == 2) {
            titre.setText("GÉRER LES LIGUES");
            instructions.setText("Double-cliquez sur une ligue ou sélectionnez-la et cliquez sur 'Éditer'");
        } else { // Niveau d'accès 3 (employé)
            titre.setText("CONSULTER LES LIGUES");
            instructions.setText("Double-cliquez sur une ligue ou sélectionnez-la pour voir ses détails");
        }

        String[] colonnes = {"ID", "Nom de la Ligue"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tableLigues = new JTable(modelTable);
        tableLigues.setFont(new Font("Arial", Font.PLAIN, 14));
        tableLigues.setRowHeight(30);
        tableLigues.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tableLigues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tableLigues.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableLigues.getSelectedRow();
                boolean enableSelectButton = (selectedRow != -1) &&
                                             (niveauAcces == 1 || niveauAcces == 2);
                
                if (boutonSelectionner != null) { 
                    boutonSelectionner.setEnabled(enableSelectButton);
                }
            }
        });
        
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

        JScrollPane scrollPane = new JScrollPane(tableLigues);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        boutonSelectionner = new JButton("Éditer la ligue sélectionnée");
        boutonSelectionner.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonSelectionner.setBackground(new Color(173, 216, 230)); 
        boutonSelectionner.setEnabled(false); 
        boutonSelectionner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editerLigueSelectionnee();
            }
        });

        if (niveauAcces == 1 || niveauAcces == 2) {
            panelBoutons.add(boutonSelectionner);
        }
        
        JButton boutonActualiser = new JButton("Actualiser");
        boutonActualiser.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonActualiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chargerLigues();
            }
        });
        panelBoutons.add(boutonActualiser);
        
        boutonAjouterLigue = new JButton("Ajouter une nouvelle ligue");
        boutonAjouterLigue.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonAjouterLigue.setBackground(new Color(144, 238, 144)); 
        boutonAjouterLigue.addActionListener(e -> {
            ajouterLigue();
        });
        
        if (niveauAcces == 1 || niveauAcces == 2) {
            panelBoutons.add(boutonAjouterLigue);
        }

        boutonRetour = new JButton("Retour");
        boutonRetour.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonRetour.setBackground(new Color(255, 182, 193)); 
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
            modelTable.setRowCount(0); 
            
            Connection connection = jdbc.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom FROM ligue ORDER BY nom");
            
            int rowCount = 0;
            while (rs.next()) {
                Object[] ligne = {
                    rs.getInt("id"),
                    rs.getString("nom")
                };
                modelTable.addRow(ligne);
                rowCount++;
            }
            
            rs.close();
            stmt.close();
            
            String currentTitle = getTitle();
            if (currentTitle.contains(" - ")) {
                 currentTitle = currentTitle.substring(0, currentTitle.indexOf(" - "));
            }
            setTitle(currentTitle + " - " + modelTable.getRowCount() + " ligue(s) disponible(s)");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des ligues : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Assurez-vous de fermer la connexion JDBC si vous avez une méthode close()
        }
    }
    
    private void editerLigueSelectionnee() {
        int selectedRow = tableLigues.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une ligue.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idLigue = (Integer) modelTable.getValueAt(selectedRow, 0);
        String nomLigue = (String) modelTable.getValueAt(selectedRow, 1);
        int niveauAccesUtilisateur = AccueilConnexion.UtilisateurConnecte.niveauAcces;

        try {
            new EditerLigue(idLigue, nomLigue, niveauAccesUtilisateur);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Une erreur inattendue est survenue lors de l'ouverture de la page d'édition : " + ex.getMessage() + "\nConsultez la console pour plus de détails.", "Erreur Critique", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterLigue() {
        String nomNouvelleLigue = JOptionPane.showInputDialog(this, "Nom de la nouvelle ligue :", "Ajouter Ligue", JOptionPane.PLAIN_MESSAGE);
        
        if (nomNouvelleLigue != null && !nomNouvelleLigue.trim().isEmpty()) {
            try {
                Connection connection = jdbc.getConnection();
                String sql = "INSERT INTO ligue (nom) VALUES (?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nomNouvelleLigue.trim());
                
                int rowsInserted = pstmt.executeUpdate();
                pstmt.close();
                
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Ligue '" + nomNouvelleLigue + "' ajoutée avec succès !");
                    chargerLigues();
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de l'ajout de la ligue.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur SQL lors de l'ajout : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
            }
        }
    }
}