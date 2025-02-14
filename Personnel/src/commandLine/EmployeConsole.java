package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.time.LocalDate;

import commandLineMenus.ListOption;
import commandLineMenus.Menu;
import commandLineMenus.Option;
import personnel.Employe;
import personnel.InvalideDate;

public class EmployeConsole 
{
	private Option afficher(final Employe employe)
	{
		return new Option("Afficher l'employé", "l", () -> {System.out.println(employe);});
	}

	ListOption<Employe> editerEmploye()
	{
		return (employe) -> editerEmploye(employe);		
	}

	Option editerEmploye(Employe employe)
	{
			Menu menu = new Menu("Gérer le compte " + employe.getNom(), "c");
			menu.add(afficher(employe));
			menu.add(changerNom(employe));
			menu.add(changerPrenom(employe));
			menu.add(changerMail(employe));
			menu.add(changerPassword(employe));
			menu.add(modifierDateArrivee(employe));
			menu.add(modifierDateDepart(employe));
			menu.add(supEmploye(employe));
			menu.add(abilitationLigue(employe));
			menu.add(supressionAbilitation(employe));
			menu.addBack("q");
			return menu;
	}

	private Option changerNom(final Employe employe)
	{
		return new Option("Changer le nom", "n", 
				() -> {employe.setNom(getString("Nouveau nom : "));}
			);
	}
	
	private Option changerPrenom(final Employe employe)
	{
		return new Option("Changer le prénom", "p", () -> {employe.setPrenom(getString("Nouveau prénom : "));});
	}
	
	private Option changerMail(final Employe employe)
	{
		return new Option("Changer le mail", "e", () -> {employe.setMail(getString("Nouveau mail : "));});
	}
	
	private Option changerPassword(final Employe employe)
	{
		return new Option("Changer le password", "x", () -> {employe.setPassword(getString("Nouveau password : "));});
	}
	
	private Option supEmploye(final Employe employe)
	{
		return new Option("Supprimer l'employé", "d", () -> {employe.remove();});
	}
	private Option abilitationLigue(final Employe employe)
	{
		return new Option("Donner le rôle d'administrateur de la ligue", "a", () -> {employe.getLigue().setAdministrateur(employe);});
	}
	private Option supressionAbilitation(final Employe employe)
	{
		return new Option("Réfuter les droits administrateurs de l'utilisateur", "r", () -> {employe.getLigue().deleteAdministrateur(employe);});
	}
	private Option modifierDateArrivee(final Employe employe) 
	{
	    return new Option("Modifier la date d'arrivée", "m", () -> {
	        LocalDate nouvelleDateArrivee = LocalDate.of(
	            Integer.parseInt(getString("Année : ")),
	            Integer.parseInt(getString("Mois : ")),
	            Integer.parseInt(getString("Jour : "))
	        );
	        try {
	            employe.setarrive(nouvelleDateArrivee); // Assurez-vous que la méthode s'appelle bien "setarrive"
	        } catch (InvalideDate e) {
	            e.printStackTrace();
	        }
	        System.out.println("Date d'arrivée modifiée avec succès.");
	    });
	}
	private Option modifierDateDepart(final Employe employe) 
	{
	    return new Option("Modifier la date de départ", "z", () -> {
	        LocalDate nouvelleDateDepart = LocalDate.of(
	            Integer.parseInt(getString("Année : ")),
	            Integer.parseInt(getString("Mois : ")),
	            Integer.parseInt(getString("Jour : "))
	        );
	        try {
	            employe.setdepart(nouvelleDateDepart); // Assurez-vous que la méthode s'appelle bien "setarrive"
	        } catch (InvalideDate e) {
	            e.printStackTrace();
	        }
	        System.out.println("Date de départ modifiée avec succès.");
	    });
	}
}
