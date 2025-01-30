package personnel;

public class InvalideDate extends Exception {
	public InvalideDate(InvalideDate arr) {
		System.out.println("La date renseigner n'est pas correcte");
	}
	
	public String toString() {
		return "La date renseigner n'est pas correcte";
	}
}
