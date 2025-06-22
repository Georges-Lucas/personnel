package affichage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Importez vos packages
import jdbc.JDBC;
import personnel.SauvegardeImpossible;

public class AccueilConnexion extends JFrame {
    private JTextField champUtilisateur;
    private JPasswordField champMotDePasse;
    private JButton boutonConnexion;
    private JLabel labelCompte;
    private JLabel messageErreur;
    
    // Liste pour stocker les identifiants
    private List<String[]> listeIdentifiants = new ArrayList<>();
    private int indexActuel = 0;
    
    // Instance de votre classe JDBC
    private JDBC jdbc;
    
    // Classe pour stocker les infos de l'utilisateur connecté
    public static class UtilisateurConnecte {
        public static int id;
        public static String nom;
        public static String prenom;
        public static String mail;
        public static int niveauAcces;
    }
    
    public AccueilConnexion() {
        // Configuration de la fenêtre
        setTitle("Connexion");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialisation de la connexion JDBC
        jdbc = new JDBC();
        
        // Chargement des identifiants depuis la base de données
        chargerIdentifiants();
        
        // Création des composants
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));
        
        champUtilisateur = new JTextField();
        champMotDePasse = new JPasswordField();
        boutonConnexion = new JButton("Se connecter");
        labelCompte = new JLabel("Compte 1/" + listeIdentifiants.size(), SwingConstants.CENTER);
        messageErreur = new JLabel("", SwingConstants.CENTER);
        messageErreur.setForeground(Color.RED);
        
        
        // Ajout des composants au panneau
        panel.add(new JLabel("Email de l'utilisateur :"));
        panel.add(champUtilisateur);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(champMotDePasse);
        panel.add(boutonConnexion);
        panel.add(labelCompte);
        panel.add(messageErreur);
        
        // Ajustement du layout
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        
        // Gestionnaires d'événements avec validation
        boutonConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String utilisateur = champUtilisateur.getText().trim(); // trim() pour enlever les espaces
                String motDePasse = new String(champMotDePasse.getPassword());
                
                // Validation des champs
                if (utilisateur.isEmpty()) {
                    messageErreur.setText("Veuillez saisir une adresse email !");
                    return;
                }
                
                if (motDePasse.isEmpty()) {
                    messageErreur.setText("Veuillez saisir un mot de passe !");
                    return;
                }
                
                // Validation du format email (optionnel mais recommandé)
                if (!isValidEmail(utilisateur)) {
                    messageErreur.setText("Format d'email invalide !");
                    return;
                }
                
                // Vérification de la connexion et récupération des infos utilisateur
                if (verifierConnexionEtRecupererInfos(utilisateur, motDePasse)) {
                    messageErreur.setText(""); // Effacer le message d'erreur
                    JOptionPane.showMessageDialog(null, "Connexion réussie !");
                    // Crée et affiche la nouvelle fenêtre Accueil
                    new Accueil();
                    dispose();
                } else {
                    messageErreur.setText("Identifiants incorrects !");
                }
            }
        });
        

        setVisible(true);
    }
    
    // Méthode pour valider le format email
    private boolean isValidEmail(String email) {
        // Expression régulière simple pour valider un email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    private void chargerIdentifiants() {
        try {
            // Utilisation de votre connexion JDBC existante
            Connection connection = jdbc.getConnection();
            Statement stmt = connection.createStatement();
            
            // D'après votre code JDBC, la table s'appelle "employe" et les colonnes 
            // pour l'email et le mot de passe sont "mail" et "mdp"
            ResultSet rs = stmt.executeQuery("SELECT mail, mdp FROM employe");
            
            while (rs.next()) {
                String email = rs.getString("mail");
                String motDePasse = rs.getString("mdp");
                listeIdentifiants.add(new String[]{email, motDePasse});
            }
            
            rs.close();
            stmt.close();
            // Ne fermez pas la connexion ici pour pouvoir l'utiliser plus tard
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la récupération des identifiants : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private boolean verifierConnexionEtRecupererInfos(String utilisateur, String motDePasse) {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT e.id, e.nom, e.prenom, e.mail, e.id_niveau_acces " +
                "FROM employe e " +
                "WHERE e.mail = ? AND e.mdp = ?");
            pstmt.setString(1, utilisateur);
            pstmt.setString(2, motDePasse);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Stocker les informations de l'utilisateur connecté
                UtilisateurConnecte.id = rs.getInt("id");
                UtilisateurConnecte.nom = rs.getString("nom");
                UtilisateurConnecte.prenom = rs.getString("prenom");
                UtilisateurConnecte.mail = rs.getString("mail");
                UtilisateurConnecte.niveauAcces = rs.getInt("id_niveau_acces");
                
                rs.close();
                pstmt.close();
                return true;
            }
            
            rs.close();
            pstmt.close();
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void fermerConnexion() {
        try {
            jdbc.close();
        } catch (SauvegardeImpossible e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la fermeture de la connexion : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AccueilConnexion();
            }
        });
    }
}