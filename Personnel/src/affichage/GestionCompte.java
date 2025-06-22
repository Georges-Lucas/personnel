package affichage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

import jdbc.JDBC;
import personnel.SauvegardeImpossible;

public class GestionCompte extends JFrame {
    private JDBC jdbc;
    private JTable tableEmployes;
    private DefaultTableModel modeleTable;
    private JTextField champNom, champPrenom, champMail, champMdp;
    private JComboBox<String> comboNiveauAcces;
    private JButton btnAfficher, btnChangerNom, btnChangerPrenom, btnChangerMail, btnChangerMdp, btnChangerNiveau, btnRetour;
    private int employeSelectionne = -1;
    
    public GestionCompte() {
        // Configuration de la fenêtre
        setTitle("Gestion des Comptes Utilisateurs");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialisation JDBC
        jdbc = new JDBC();
        
        // Création de l'interface
        creerInterface();
        
        // Adaptation du titre selon le niveau d'accès
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            setTitle("Gestion des Comptes Utilisateurs - Administrateur");
        } else {
            setTitle("Mon Compte - " + AccueilConnexion.UtilisateurConnecte.prenom + " " + 
                    AccueilConnexion.UtilisateurConnecte.nom);
        }
        
        // Chargement initial des données
        chargerEmployes();
        
        setVisible(true);
    }
    
    private void creerInterface() {
        setLayout(new BorderLayout());
        
        // Titre
        JLabel titre;
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            titre = new JLabel("GESTION DES COMPTES UTILISATEURS - ADMINISTRATEUR", SwingConstants.CENTER);
        } else {
            titre = new JLabel("MON COMPTE UTILISATEUR", SwingConstants.CENTER);
        }
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titre, BorderLayout.NORTH);
        
        // Panneau principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        // Table des employés
        String[] colonnes;
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            colonnes = new String[]{"ID", "Nom", "Prénom", "Email", "Mot de passe", "Niveau d'accès"};
        } else {
            colonnes = new String[]{"ID", "Nom", "Prénom", "Email", "Mot de passe"};
        }
        modeleTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table non éditable directement
            }
        };
        
        tableEmployes = new JTable(modeleTable);
        tableEmployes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEmployes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int ligneSelectionnee = tableEmployes.getSelectedRow();
                if (ligneSelectionnee != -1) {
                    employeSelectionne = (Integer) modeleTable.getValueAt(ligneSelectionnee, 0);
                    remplirChamps(ligneSelectionnee);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableEmployes);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Panneau des boutons et champs
        JPanel panelBas = new JPanel(new BorderLayout());
        
        // Panneau des champs de modification
        JPanel panelChamps = new JPanel();
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            panelChamps.setLayout(new GridLayout(5, 2, 5, 5)); // 5 lignes pour inclure le niveau d'accès
        } else {
            panelChamps.setLayout(new GridLayout(4, 2, 5, 5)); // 4 lignes normalement
        }
        panelChamps.setBorder(BorderFactory.createTitledBorder("Modifier les informations"));
        
        panelChamps.add(new JLabel("Nom :"));
        champNom = new JTextField();
        panelChamps.add(champNom);
        
        panelChamps.add(new JLabel("Prénom :"));
        champPrenom = new JTextField();
        panelChamps.add(champPrenom);
        
        panelChamps.add(new JLabel("Email :"));
        champMail = new JTextField();
        panelChamps.add(champMail);
        
        panelChamps.add(new JLabel("Mot de passe :"));
        champMdp = new JPasswordField();
        panelChamps.add(champMdp);
        
        // Ajout du combo niveau d'accès seulement pour les administrateurs
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            panelChamps.add(new JLabel("Niveau d'accès :"));
            String[] niveaux = {"1 - Administrateur Logiciel", "2 - Administrateur Ligue", "3 - Utilisateur"};
            comboNiveauAcces = new JComboBox<>(niveaux);
            panelChamps.add(comboNiveauAcces);
        }
        
        panelBas.add(panelChamps, BorderLayout.CENTER);
        
        // Panneau des boutons
        JPanel panelBoutons = new JPanel();
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            panelBoutons.setLayout(new GridLayout(2, 3, 10, 10)); // 2 lignes, 3 colonnes
        } else {
            panelBoutons.setLayout(new GridLayout(2, 3, 10, 10)); // Même layout mais sans le bouton niveau
        }
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnAfficher = new JButton("Afficher l'employé");
        btnChangerNom = new JButton("Changer le nom");
        btnChangerPrenom = new JButton("Changer le prénom");
        btnChangerMail = new JButton("Changer le mail");
        btnChangerMdp = new JButton("Changer le MDP");
        btnRetour = new JButton("Retour");
        
        panelBoutons.add(btnAfficher);
        panelBoutons.add(btnChangerNom);
        panelBoutons.add(btnChangerPrenom);
        panelBoutons.add(btnChangerMail);
        panelBoutons.add(btnChangerMdp);
        
        // Ajout du bouton niveau d'accès seulement pour les administrateurs
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            btnChangerNiveau = new JButton("Changer le niveau");
            panelBoutons.add(btnChangerNiveau);
            panelBoutons.add(btnRetour);
        } else {
            panelBoutons.add(btnRetour);
        }
        
        panelBas.add(panelBoutons, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelBas, BorderLayout.SOUTH);
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Gestionnaires d'événements
        ajouterEvenements();
    }
    
    private void ajouterEvenements() {
        btnAfficher.addActionListener(e -> chargerEmployes());
        
        btnChangerNom.addActionListener(e -> {
            if (employeSelectionne != -1 && !champNom.getText().trim().isEmpty()) {
                modifierChamp("nom", champNom.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un employé et saisissez un nom !");
            }
        });
        
        btnChangerPrenom.addActionListener(e -> {
            if (employeSelectionne != -1 && !champPrenom.getText().trim().isEmpty()) {
                modifierChamp("prenom", champPrenom.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un employé et saisissez un prénom !");
            }
        });
        
        btnChangerMail.addActionListener(e -> {
            if (employeSelectionne != -1 && !champMail.getText().trim().isEmpty()) {
                if (isValidEmail(champMail.getText().trim())) {
                    modifierChamp("mail", champMail.getText().trim());
                } else {
                    JOptionPane.showMessageDialog(this, "Format d'email invalide !");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un employé et saisissez un email !");
            }
        });
        
        btnChangerMdp.addActionListener(e -> {
            if (employeSelectionne != -1 && !champMdp.getText().trim().isEmpty()) {
                modifierChamp("mdp", champMdp.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un employé et saisissez un mot de passe !");
            }
        });
        
        btnRetour.addActionListener(e -> {
            new Accueil();
            dispose();
        });
        
        // Gestionnaire pour le bouton changer niveau (seulement pour les administrateurs)
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 && btnChangerNiveau != null) {
            btnChangerNiveau.addActionListener(e -> {
                if (employeSelectionne != -1) {
                    // Vérifier que ce n'est pas un administrateur qui essaie de modifier un autre admin
                    if (verifierNiveauEmployeSelectionne() == 1) {
                        JOptionPane.showMessageDialog(this, "Impossible de modifier le niveau d'un autre administrateur !");
                        return;
                    }
                    
                    // Vérifier que l'admin ne modifie pas son propre niveau
                    if (employeSelectionne == AccueilConnexion.UtilisateurConnecte.id) {
                        JOptionPane.showMessageDialog(this, "Vous ne pouvez pas modifier votre propre niveau d'accès !");
                        return;
                    }
                    
                    int nouveauNiveau = comboNiveauAcces.getSelectedIndex() + 1; // +1 car les index commencent à 0
                    modifierNiveauAcces(nouveauNiveau);
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez un employé !");
                }
            });
        }
    }
    
    private void chargerEmployes() {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt;
            ResultSet rs;
            
            // Vider la table
            modeleTable.setRowCount(0);
            
            // Si l'utilisateur est administrateur (niveau 1), afficher tous les employés avec leur niveau
            if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
                pstmt = connection.prepareStatement(
                    "SELECT e.id, e.nom, e.prenom, e.mail, e.mdp, n.niveau_acces " +
                    "FROM employe e " +
                    "INNER JOIN niveau_acces n ON e.id_niveau_acces = n.id " +
                    "ORDER BY e.id");
                rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Vector<Object> ligne = new Vector<>();
                    ligne.add(rs.getInt("id"));
                    ligne.add(rs.getString("nom"));
                    ligne.add(rs.getString("prenom"));
                    ligne.add(rs.getString("mail"));
                    ligne.add(rs.getString("mdp"));
                    ligne.add(rs.getString("niveau_acces"));
                    modeleTable.addRow(ligne);
                }
            } else {
                // Sinon, afficher seulement ses propres informations
                pstmt = connection.prepareStatement("SELECT id, nom, prenom, mail, mdp FROM employe WHERE id = ?");
                pstmt.setInt(1, AccueilConnexion.UtilisateurConnecte.id);
                rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Vector<Object> ligne = new Vector<>();
                    ligne.add(rs.getInt("id"));
                    ligne.add(rs.getString("nom"));
                    ligne.add(rs.getString("prenom"));
                    ligne.add(rs.getString("mail"));
                    ligne.add(rs.getString("mdp"));
                    modeleTable.addRow(ligne);
                }
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des employés : " + e.getMessage());
        }
    }
    
    private void remplirChamps(int ligne) {
        champNom.setText((String) modeleTable.getValueAt(ligne, 1));
        champPrenom.setText((String) modeleTable.getValueAt(ligne, 2));
        champMail.setText((String) modeleTable.getValueAt(ligne, 3));
        champMdp.setText((String) modeleTable.getValueAt(ligne, 4));
        
        // Remplir le combo niveau d'accès seulement pour les administrateurs
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 && comboNiveauAcces != null && modeleTable.getColumnCount() > 5) {
            String niveauTexte = (String) modeleTable.getValueAt(ligne, 5);
            if (niveauTexte.contains("Administrateur Logiciel")) {
                comboNiveauAcces.setSelectedIndex(0);
            } else if (niveauTexte.contains("Administrateur Ligue")) {
                comboNiveauAcces.setSelectedIndex(1);
            } else if (niveauTexte.contains("Utilisateur")) {
                comboNiveauAcces.setSelectedIndex(2);
            }
        }
    }
    
    private void modifierChamp(String nomChamp, String nouvelleValeur) {
        try {
            Connection connection = jdbc.getConnection();
            String sql;
            PreparedStatement pstmt;
            
            // Vérification des droits : un utilisateur non-admin ne peut modifier que ses propres infos
            if (AccueilConnexion.UtilisateurConnecte.niveauAcces != 1 && 
                employeSelectionne != AccueilConnexion.UtilisateurConnecte.id) {
                JOptionPane.showMessageDialog(this, "Vous n'avez pas les droits pour modifier cet utilisateur !");
                return;
            }
            
            sql = "UPDATE employe SET " + nomChamp + " = ? WHERE id = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nouvelleValeur);
            pstmt.setInt(2, employeSelectionne);
            
            int lignesModifiees = pstmt.executeUpdate();
            
            if (lignesModifiees > 0) {
                JOptionPane.showMessageDialog(this, "Modification réussie !");
                
                // Mettre à jour les infos de l'utilisateur connecté si il modifie ses propres infos
                if (employeSelectionne == AccueilConnexion.UtilisateurConnecte.id) {
                    switch (nomChamp) {
                        case "nom":
                            AccueilConnexion.UtilisateurConnecte.nom = nouvelleValeur;
                            break;
                        case "prenom":
                            AccueilConnexion.UtilisateurConnecte.prenom = nouvelleValeur;
                            break;
                        case "mail":
                            AccueilConnexion.UtilisateurConnecte.mail = nouvelleValeur;
                            break;
                    }
                }
                
                chargerEmployes(); // Recharger la table
                viderChamps();
            } else {
                JOptionPane.showMessageDialog(this, "Aucune modification effectuée !");
            }
            
            pstmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + e.getMessage());
        }
    }
    
    private void viderChamps() {
        champNom.setText("");
        champPrenom.setText("");
        champMail.setText("");
        champMdp.setText("");
        if (comboNiveauAcces != null) {
            comboNiveauAcces.setSelectedIndex(0);
        }
        employeSelectionne = -1;
        tableEmployes.clearSelection();
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    private int verifierNiveauEmployeSelectionne() {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT id_niveau_acces FROM employe WHERE id = ?");
            pstmt.setInt(1, employeSelectionne);
            ResultSet rs = pstmt.executeQuery();
            
            int niveau = 0;
            if (rs.next()) {
                niveau = rs.getInt("id_niveau_acces");
            }
            
            rs.close();
            pstmt.close();
            return niveau;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private void modifierNiveauAcces(int nouveauNiveau) {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("UPDATE employe SET id_niveau_acces = ? WHERE id = ?");
            pstmt.setInt(1, nouveauNiveau);
            pstmt.setInt(2, employeSelectionne);
            
            int lignesModifiees = pstmt.executeUpdate();
            
            if (lignesModifiees > 0) {
                String[] niveauxTexte = {"Administrateur Logiciel", "Administrateur Ligue", "Utilisateur"};
                JOptionPane.showMessageDialog(this, "Niveau d'accès modifié vers : " + niveauxTexte[nouveauNiveau - 1]);
                chargerEmployes();
                viderChamps();
            } else {
                JOptionPane.showMessageDialog(this, "Aucune modification effectuée !");
            }
            
            pstmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du niveau d'accès : " + e.getMessage());
        }
    }
    
    public void fermerConnexion() {
        try {
            jdbc.close();
        } catch (SauvegardeImpossible e) {
            e.printStackTrace();
        }
    }
}