package affichage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import jdbc.JDBC;
import personnel.SauvegardeImpossible;

public class CreationUtilisateur extends JFrame {
    private JDBC jdbc;
    private JTextField champNom, champPrenom, champMail, champMdp;
    private JComboBox<String> comboNiveauAcces;
    private JButton btnCreer, btnAnnuler, btnEffacer;

    public CreationUtilisateur() {
        setTitle("Création d'un Nouvel Utilisateur");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        jdbc = new JDBC();
        creerInterface();
        setVisible(true);
    }

    private void creerInterface() {
        setLayout(new BorderLayout());

        JLabel titre = new JLabel("CRÉATION D'UN NOUVEL UTILISATEUR", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        titre.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titre, BorderLayout.NORTH);

        // Panel principal avec les champs
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Panel des champs de saisie
        JPanel panelChamps = new JPanel(new GridLayout(5, 2, 10, 15));
        panelChamps.setBorder(BorderFactory.createTitledBorder("Informations de l'utilisateur"));

        champNom = new JTextField();
        champPrenom = new JTextField();
        champMail = new JTextField();
        champMdp = new JPasswordField();

        // Restriction du niveau d'accès selon l'utilisateur connecté
        String[] niveaux;
        if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
            // Administrateur logiciel peut créer tous les niveaux
            niveaux = new String[]{"1 - Administrateur Logiciel", "2 - Administrateur Ligue", "3 - Utilisateur"};
        } else {
            // Administrateur ligue ne peut créer que des utilisateurs normaux
            niveaux = new String[]{"3 - Utilisateur"};
        }
        comboNiveauAcces = new JComboBox<>(niveaux);

        panelChamps.add(new JLabel("Nom * :"));
        panelChamps.add(champNom);

        panelChamps.add(new JLabel("Prénom * :"));
        panelChamps.add(champPrenom);

        panelChamps.add(new JLabel("Email * :"));
        panelChamps.add(champMail);

        panelChamps.add(new JLabel("Mot de passe * :"));
        panelChamps.add(champMdp);

        panelChamps.add(new JLabel("Niveau d'accès * :"));
        panelChamps.add(comboNiveauAcces);

        panelPrincipal.add(panelChamps, BorderLayout.CENTER);

        // Note d'information
        JLabel noteInfo = new JLabel("<html><i>* Champs obligatoires<br/>L'utilisateur pourra modifier ses informations après création<br/>L'affectation à une ligue se fait depuis la page de gestion des ligues</i></html>");
        noteInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        noteInfo.setForeground(Color.GRAY);
        noteInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelPrincipal.add(noteInfo, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);

        // Panel des boutons
        JPanel panelBoutons = new JPanel(new FlowLayout());
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        btnCreer = new JButton("Créer l'utilisateur");
        btnCreer.setBackground(new Color(46, 125, 50));
        btnCreer.setForeground(Color.WHITE);
        btnCreer.setFont(new Font("Arial", Font.BOLD, 12));

        btnEffacer = new JButton("Effacer les champs");
        btnEffacer.setBackground(new Color(251, 140, 0));
        btnEffacer.setForeground(Color.WHITE);

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setBackground(new Color(198, 40, 40));
        btnAnnuler.setForeground(Color.WHITE);

        panelBoutons.add(btnCreer);
        panelBoutons.add(btnEffacer);
        panelBoutons.add(btnAnnuler);

        add(panelBoutons, BorderLayout.SOUTH);

        ajouterEvenements();
    }

    private void ajouterEvenements() {
        btnCreer.addActionListener(e -> creerUtilisateur());

        btnEffacer.addActionListener(e -> {
            champNom.setText("");
            champPrenom.setText("");
            champMail.setText("");
            champMdp.setText("");
            comboNiveauAcces.setSelectedIndex(0);
        });

        btnAnnuler.addActionListener(e -> dispose());

        // Validation en temps réel de l'email
        champMail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String email = champMail.getText().trim();
                if (!email.isEmpty() && !isValidEmail(email)) {
                    champMail.setBackground(new Color(255, 235, 235));
                } else {
                    champMail.setBackground(Color.WHITE);
                }
            }
        });
    }

    private void creerUtilisateur() {
        // Validation des champs
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        String mail = champMail.getText().trim();
        String mdp = champMdp.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || mdp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(mail)) {
            JOptionPane.showMessageDialog(this, "L'adresse email n'est pas valide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            champMail.requestFocus();
            return;
        }

        if (mdp.length() < 4) {
            JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 4 caractères !", "Erreur", JOptionPane.ERROR_MESSAGE);
            champMdp.requestFocus();
            return;
        }

        // Vérification de l'unicité de l'email
        if (emailExiste(mail)) {
            JOptionPane.showMessageDialog(this, "Cette adresse email est déjà utilisée !", "Erreur", JOptionPane.ERROR_MESSAGE);
            champMail.requestFocus();
            return;
        }

        // Création de l'utilisateur
        Connection connection = null;
        try {
            connection = jdbc.getConnection();
            
            // Détermination du niveau d'accès selon l'utilisateur connecté
            int niveauAcces;
            if (AccueilConnexion.UtilisateurConnecte.niveauAcces == 1) {
                // Administrateur logiciel peut créer tous les niveaux
                niveauAcces = comboNiveauAcces.getSelectedIndex() + 1;
            } else {
                // Administrateur ligue ne peut créer que des utilisateurs (niveau 3)
                niveauAcces = 3;
            }

            // Créer l'employé (sans affectation de ligue)
            PreparedStatement pstmtEmploye = connection.prepareStatement(
                    "INSERT INTO employe (nom, prenom, mail, mdp, id_niveau_acces) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            
            pstmtEmploye.setString(1, nom);
            pstmtEmploye.setString(2, prenom);
            pstmtEmploye.setString(3, mail);
            pstmtEmploye.setString(4, mdp);
            pstmtEmploye.setInt(5, niveauAcces);

            int resultat = pstmtEmploye.executeUpdate();
            int nouvelEmployeId = -1;
            
            if (resultat > 0) {
                ResultSet generatedKeys = pstmtEmploye.getGeneratedKeys();
                if (generatedKeys.next()) {
                    nouvelEmployeId = generatedKeys.getInt(1);
                }
                generatedKeys.close();
            }
            pstmtEmploye.close();

            if (nouvelEmployeId > 0) {
                // Message de succès
                String[] niveauxTexte = {"Administrateur Logiciel", "Administrateur Ligue", "Utilisateur"};
                
                String message = String.format(
                    "Utilisateur créé avec succès !\n\n" +
                    "ID : %d\n" +
                    "Nom : %s %s\n" +
                    "Email : %s\n" +
                    "Niveau d'accès : %s\n\n" +
                    "L'affectation à une ligue peut être effectuée depuis la page de gestion des ligues.",
                    nouvelEmployeId, prenom, nom, mail, niveauxTexte[niveauAcces - 1]
                );

                JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);

                // Proposer de créer un autre utilisateur ou fermer
                int choix = JOptionPane.showConfirmDialog(this, 
                    "Voulez-vous créer un autre utilisateur ?", 
                    "Confirmation", 
                    JOptionPane.YES_NO_OPTION);

                if (choix == JOptionPane.YES_OPTION) {
                    // Effacer les champs pour une nouvelle création
                    champNom.setText("");
                    champPrenom.setText("");
                    champMail.setText("");
                    champMdp.setText("");
                    comboNiveauAcces.setSelectedIndex(0);
                    champNom.requestFocus();
                } else {
                    dispose();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création de l'utilisateur !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur base de données : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean emailExiste(String email) {
        try {
            Connection connection = jdbc.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM employe WHERE mail = ?");
            pstmt.setString(1, email);
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

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
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