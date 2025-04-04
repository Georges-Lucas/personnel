package affichage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccueilConnexion extends JFrame {
    private JTextField champUtilisateur;
    private JPasswordField champMotDePasse;
    private JButton boutonConnexion;
    private JLabel messageErreur;

    public AccueilConnexion() {
        // Configuration de la fenêtre
        setTitle("Connexion");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Création des composants
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        champUtilisateur = new JTextField();
        champMotDePasse = new JPasswordField();
        boutonConnexion = new JButton("Se connecter");
        messageErreur = new JLabel("", SwingConstants.CENTER);
        messageErreur.setForeground(Color.RED);

        // Ajout des composants au panneau
        panel.add(new JLabel("Email de l'utilisatejur :"));
        panel.add(champUtilisateur);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(champMotDePasse);
        panel.add(boutonConnexion);
        panel.add(messageErreur);

        add(panel);

        // Gestion de l'événement du bouton
        boutonConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String utilisateur = champUtilisateur.getText();
                String motDePasse = new String(champMotDePasse.getPassword());

                if (verifierConnexion(utilisateur, motDePasse)) {
                    JOptionPane.showMessageDialog(null, "Connexion réussie !");
                    dispose(); // Ferme la fenêtre après connexion réussie
                } else {
                    messageErreur.setText("Identifiants incorrects !");
                }
            }
        });

        setVisible(true);
    }

    // Méthode de vérification des identifiants (exemple simple)
    private boolean verifierConnexion(String utilisateur, String motDePasse) {
        return utilisateur.equals(mail) && motDePasse.equals(password);
    }

    public static void main(String[] args) {
        new AccueilConnexion();
    }
}
