package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import jdbc.JDBC;

public class AjouterLigue extends JFrame {
    
    private JDBC jdbc;
    private JTextField champNom;
    private JButton boutonAjouter;
    private JButton boutonAnnuler;
    private JLabel messageErreur;
    
    public AjouterLigue() {
        jdbc = new JDBC();
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Configuration de la fenêtre
        String nomComplet = AccueilConnexion.UtilisateurConnecte.prenom + " " + AccueilConnexion.UtilisateurConnecte.nom;
        setTitle("Ajouter une Ligue - " + nomComplet);
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panneau principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("AJOUTER UNE NOUVELLE LIGUE", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        panelPrincipal.add(titre, BorderLayout.NORTH);

        // Panneau du formulaire
        JPanel panelFormulaire = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Label nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel labelNom = new JLabel("Nom de la ligue :");
        labelNom.setFont(new Font("Arial", Font.PLAIN, 14));
        panelFormulaire.add(labelNom, gbc);

        // Champ nom
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        champNom = new JTextField(20);
        champNom.setFont(new Font("Arial", Font.PLAIN, 14));
        panelFormulaire.add(champNom, gbc);

        // Message d'erreur
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        messageErreur = new JLabel("", SwingConstants.CENTER);
        messageErreur.setForeground(Color.RED);
        messageErreur.setFont(new Font("Arial", Font.ITALIC, 12));
        panelFormulaire.add(messageErreur, gbc);

        panelPrincipal.add(panelFormulaire, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        
        // Bouton Ajouter
        boutonAjouter = new JButton("Ajouter la ligue");
        boutonAjouter.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonAjouter.setBackground(new Color(144, 238, 144)); // Vert clair
        boutonAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterLigue();
            }
        });
        panelBoutons.add(boutonAjouter);
        
        // Bouton Annuler
        boutonAnnuler = new JButton("Annuler");
        boutonAnnuler.setFont(new Font("Arial", Font.PLAIN, 14));
        boutonAnnuler.setBackground(new Color(255, 182, 193)); // Rose clair
        boutonAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestionLigues();
                dispose();
            }
        });
        panelBoutons.add(boutonAnnuler);

        panelPrincipal.add(panelBoutons, BorderLayout.SOUTH);

        add(panelPrincipal);
        
        // Permettre d'ajouter en appuyant sur Entrée
        champNom.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ajouterLigue();
                }
            }
        });
        
        // Focus sur le champ nom
        champNom.requestFocus();
        
        setVisible(true);
    }
    
    private void ajouterLigue() {
        String nomLigue = champNom.getText().trim();
        
        // Validation
        if (nomLigue.isEmpty()) {
            messageErreur.setText("Veuillez saisir un nom pour la ligue !");
            return;
        }
        
        if (nomLigue.length() < 2) {
            messageErreur.setText("Le nom de la ligue doit contenir au moins 2 caractères !");
            return;
        }
        
        // Vérifier si la ligue existe déjà
        if (ligueExiste(nomLigue)) {
            messageErreur.setText("Une ligue avec ce nom existe déjà !");
            return;
        }
        
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO ligue (nom) VALUES (?)");
            pstmt.setString(1, nomLigue);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                    "La ligue '" + nomLigue + "' a été ajoutée avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                // Retour au menu de gestion des ligues
                new GestionLigues();
                dispose();
            } else {
                messageErreur.setText("Erreur lors de l'ajout de la ligue !");
            }
            
            pstmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            messageErreur.setText("Erreur base de données : " + e.getMessage());
        }
    }
    
    private boolean ligueExiste(String nom) {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM ligue WHERE LOWER(nom) = LOWER(?)");
            pstmt.setString(1, nom);
            
            ResultSet rs = pstmt.executeQuery();
            boolean existe = false;
            
            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }
            
            rs.close();
            pstmt.close();
            
            return existe;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}