package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import personnel.*;

class testLigue 
{
	GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
	
	@Test
	void createLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		assertEquals("Fléchettes", ligue.getNom());
	}

	@Test
	void addEmploye() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty",LocalDate.now(),null); 
		assertEquals(employe, ligue.getEmployes().first());
	}
	
	@Test 
	void arriveIncorrecte() throws SauvegardeImpossible, Exception
	{
		Ligue ligue = gestionPersonnel.addLigue("Course");
		Employe employe = ligue.addEmploye("Babski", "Florian", "f@gmail.com", "azerty",LocalDate.now(),LocalDate.of(2022, 10, 1));
		employe.setarrive(LocalDate.of(2023,5,23));
		assertEquals(employe, ligue.getEmployes().first());
	}
	
	@Test
	void departIncorrecte() throws Exception, SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Course");
		Employe employe = ligue.addEmploye("Grondin", "Lucas", "gl@gmail.com", "azerty",LocalDate.now(),null);
		employe.setdepart(LocalDate.of(2023,5,23));
		assertEquals(employe, ligue.getEmployes().first());
	}
	
	@Test
	void gatsetLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Course");
		ligue.setNom("Lancé");
		assertEquals("Lancé", ligue.getNom());
	}
	
	@Test
	void getsetEmploye() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty",LocalDate.now(),null);
		employe.setNom("Bruno");
		employe.setPrenom("Brunié");
		employe.setMail("b.brunod@gmail.com");
		employe.setPassword("12345");
		assertEquals("Bruno", employe.getNom());
		assertEquals("Brunié", employe.getPrenom());
		assertEquals("b.brunod@gmail.com", employe.getMail());
	}
	
	@Test 
	void supLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		ligue.remove();
		assertEquals(ligue, gestionPersonnel.getLigues().first());
	}
	
	@Test 
	void supemploye() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty",LocalDate.now(),null);
		ligue.getEmployes().first().remove();
		assertEquals(employe, ligue.getEmployes().first());
	}
	
	@Test 
	void changementAdmin() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty",LocalDate.now(),null);
		ligue.setAdministrateur(employe);
		assertEquals(true, ligue.getEmployes().first().estAdmin(ligue));
	}
	
	@Test 
	void supAdmin() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty",LocalDate.now(),null);
		ligue.setAdministrateur(employe);
		employe.remove();
		assertEquals(employe, ligue.getEmployes().first());
	}
}
	