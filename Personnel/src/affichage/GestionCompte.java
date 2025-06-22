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

    private JButton btnAffectationLigue, btnAfficher, btnValiderModifications, btnRetour, btnVoirLigues, btnCreerUtilisateur;
    private int employeSelectionne = -1;

    public GestionCompte() {
        setTitle("Gestion des Comptes Utilisateurs");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        jdbc = new JDBC();
        creerInterface();

        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            setTitle("Gestion des Comptes Utilisateurs - Administrateur");
        } else {
            setTitle("Mon Compte - " + AccueilConnexion.UtilisateurConnecte.prenom + " " + AccueilConnexion.UtilisateurConnecte.nom);
        }

        chargerEmployes();
        setVisible(true);
    }

    private void creerInterface() {
        setLayout(new BorderLayout());

        JLabel titre = new JLabel(
                AccueilConnexion.UtilisateurConnecte.niveauAcces == 1
                        ? "GESTION DES COMPTES UTILISATEURS - ADMINISTRATEUR"
                        : "MON COMPTE UTILISATEUR",
                SwingConstants.CENTER
        );
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titre, BorderLayout.NORTH);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        String[] colonnes = AccueilConnexion.UtilisateurConnecte.niveauAcces == 1
                ? new String[]{"ID", "Nom", "Prénom", "Email", "Mot de passe", "Niveau d'accès"}
                : new String[]{"ID", "Nom", "Prénom", "Email", "Mot de passe"};

        modeleTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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

        JPanel panelBas = new JPanel(new BorderLayout());
        JPanel panelChamps = new JPanel();
        panelChamps.setLayout(new GridLayout(
                AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 ? 5 : 4,
                2, 5, 5
        ));
        panelChamps.setBorder(BorderFactory.createTitledBorder("Modifier les informations"));

        champNom = new JTextField();
        champPrenom = new JTextField();
        champMail = new JTextField();
        champMdp = new JPasswordField();

        panelChamps.add(new JLabel("Nom :"));
        panelChamps.add(champNom);

        panelChamps.add(new JLabel("Prénom :"));
        panelChamps.add(champPrenom);

        panelChamps.add(new JLabel("Email :"));
        panelChamps.add(champMail);

        panelChamps.add(new JLabel("Mot de passe :"));
        panelChamps.add(champMdp);

        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            panelChamps.add(new JLabel("Niveau d'accès :"));
            String[] niveaux = {"1 - Administrateur Logiciel", "2 - Administrateur Ligue", "3 - Utilisateur"};
            comboNiveauAcces = new JComboBox<>(niveaux);
            panelChamps.add(comboNiveauAcces);
        }

        panelBas.add(panelChamps, BorderLayout.CENTER);

        JPanel panelBoutons = new JPanel();
        
        // Calcul du nombre de lignes nécessaires selon le niveau d'accès
        int nombreBoutons = 2; // btnAfficher + btnRetour (base)
        nombreBoutons += 1; // btnValiderModifications
        
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 || AccueilConnexion.UtilisateurConnecte.niveauAcces == 2) {
            nombreBoutons += 3; // btnAffectationLigue + btnVoirLigues + btnCreerUtilisateur
        }
        
        int lignes = (int) Math.ceil(nombreBoutons / 3.0);
        panelBoutons.setLayout(new GridLayout(lignes, 3, 10, 10));
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnAfficher = new JButton("Afficher l'employé");
        btnValiderModifications = new JButton("Valider les modifications");
        btnRetour = new JButton("Retour");
        btnAffectationLigue = new JButton("Affectation Ligue");
        btnVoirLigues = new JButton("Voir les Ligues");
        btnCreerUtilisateur = new JButton("Créer Utilisateur");

        panelBoutons.add(btnAfficher);
        panelBoutons.add(btnValiderModifications);
        
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 || AccueilConnexion.UtilisateurConnecte.niveauAcces == 2) {
            panelBoutons.add(btnAffectationLigue);
            panelBoutons.add(btnVoirLigues);
            panelBoutons.add(btnCreerUtilisateur);
        }
        
        panelBoutons.add(btnRetour);

        panelBas.add(panelBoutons, BorderLayout.SOUTH);

        panelPrincipal.add(panelBas, BorderLayout.SOUTH);
        add(panelPrincipal, BorderLayout.CENTER);

        ajouterEvenements();
    }

    private void ajouterEvenements() {
        btnAfficher.addActionListener(e -> chargerEmployes());

        btnValiderModifications.addActionListener(e -> validerToutesModifications());

        btnRetour.addActionListener(e -> {
            new Accueil();
            dispose();
        });

        btnAffectationLigue.addActionListener(e -> {
            new AffectationLigue();
        });
        
        btnVoirLigues.addActionListener(e -> {
            new VoirLigues();
        });
        
        btnCreerUtilisateur.addActionListener(e -> {
            new CreationUtilisateur();
        });
    }

    private void validerToutesModifications() {
        if (employeSelectionne == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un employé !");
            return;
        }

        // Vérification des permissions
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces != 1 && employeSelectionne != AccueilConnexion.UtilisateurConnecte.id) {
            JOptionPane.showMessageDialog(this, "Vous ne pouvez modifier que vos informations !");
            return;
        }

        // Validation des champs
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        String mail = champMail.getText().trim();
        String mdp = champMdp.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || mdp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis !");
            return;
        }

        if (!isValidEmail(mail)) {
            JOptionPane.showMessageDialog(this, "Email invalide !");
            return;
        }

        // Vérification spéciale pour les administrateurs
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            int niveauSelectionne = verifierNiveauEmployeSelectionne();
            int nouveauNiveau = comboNiveauAcces.getSelectedIndex() + 1;
            
            if ((niveauSelectionne == 1 || employeSelectionne == AccueilConnexion.UtilisateurConnecte.id) && nouveauNiveau != niveauSelectionne) {
                JOptionPane.showMessageDialog(this, "Modification du niveau d'accès impossible pour cet utilisateur !");
                return;
            }
        }

        // Effectuer toutes les modifications
        try {
            Connection connection = jdbc.getConnection();
            connection.setAutoCommit(false); // Transaction

            // Mise à jour des informations de base
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE employe SET nom = ?, prenom = ?, mail = ?, mdp = ? WHERE id = ?");
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, mail);
            pstmt.setString(4, mdp);
            pstmt.setInt(5, employeSelectionne);

            int res = pstmt.executeUpdate();
            pstmt.close();

            // Mise à jour du niveau d'accès (si administrateur)
            if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 && comboNiveauAcces != null) {
                int nouveauNiveau = comboNiveauAcces.getSelectedIndex() + 1;
                PreparedStatement pstmtNiveau = connection.prepareStatement(
                        "UPDATE employe SET id_niveau_acces = ? WHERE id = ?");
                pstmtNiveau.setInt(1, nouveauNiveau);
                pstmtNiveau.setInt(2, employeSelectionne);
                pstmtNiveau.executeUpdate();
                pstmtNiveau.close();
            }

            connection.commit(); // Valider la transaction
            connection.setAutoCommit(true);

            if (res > 0) {
                JOptionPane.showMessageDialog(this, "Modifications effectuées avec succès !");
                chargerEmployes();
            } else {
                JOptionPane.showMessageDialog(this, "Aucune modification effectuée !");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void chargerEmployes() {
        try {
            Connection connection = jdbc.getConnection();
            modeleTable.setRowCount(0);
            PreparedStatement pstmt;
            ResultSet rs;

            if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
                pstmt = connection.prepareStatement(
                        "SELECT e.id, e.nom, e.prenom, e.mail, e.mdp, n.niveau_acces FROM employe e INNER JOIN niveau_acces n ON e.id_niveau_acces = n.id ORDER BY e.id");
            } else {
                pstmt = connection.prepareStatement(
                        "SELECT id, nom, prenom, mail, mdp FROM employe WHERE id = ?");
                pstmt.setInt(1, AccueilConnexion.UtilisateurConnecte.id);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<Object> ligne = new Vector<>();
                ligne.add(rs.getInt("id"));
                ligne.add(rs.getString("nom"));
                ligne.add(rs.getString("prenom"));
                ligne.add(rs.getString("mail"));
                ligne.add(rs.getString("mdp"));
                if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1 && rs.getMetaData().getColumnCount() > 5) {
                    ligne.add(rs.getString("niveau_acces"));
                }
                modeleTable.addRow(ligne);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur chargement employés : " + e.getMessage());
        }
    }

    private void remplirChamps(int ligne) {
        champNom.setText((String) modeleTable.getValueAt(ligne, 1));
        champPrenom.setText((String) modeleTable.getValueAt(ligne, 2));
        champMail.setText((String) modeleTable.getValueAt(ligne, 3));
        champMdp.setText((String) modeleTable.getValueAt(ligne, 4));

        if (comboNiveauAcces != null && modeleTable.getColumnCount() > 5) {
            String niveauTexte = (String) modeleTable.getValueAt(ligne, 5);
            comboNiveauAcces.setSelectedIndex(
                    niveauTexte.contains("Administrateur Logiciel") ? 0 :
                    niveauTexte.contains("Administrateur Ligue") ? 1 : 2
            );
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private int verifierNiveauEmployeSelectionne() {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT id_niveau_acces FROM employe WHERE id = ?");
            pstmt.setInt(1, employeSelectionne);

            ResultSet rs = pstmt.executeQuery();
            int niveau = rs.next() ? rs.getInt("id_niveau_acces") : 0;

            rs.close();
            pstmt.close();
            return niveau;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
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