package clmarrThesis;

public class TesterConsonant {

	public static void main(String args[]) {
		System.out.println("Testing class consonant");
		System.out.println("Testing the constructors ");

		System.out.println("Default constructor: ");
		Consonant defaultcons = new Consonant();
		System.out.println("toString() " + defaultcons);

		System.out.println("(int,int) constructor: trial case 'p' (place = 100, manner = 10");
		Consonant cons1 = new Consonant(100, 10);
		System.out.println("print() " + cons1.print());
		System.out.println("toString() " + cons1);

		System.out.println("(int,int,bool,bool) constructor: trial case 'b' (100,10,false,true) ");
		Consonant cons2 = new Consonant(100, 10, false, true);
		System.out.println("print() " + cons2.print());
		System.out.println("toString() " + cons2);

		System.out.println("Clone constructor: trial case 'b', from above");
		Consonant cons3 = new Consonant(cons2);
		System.out.println("print() " + cons3.print());
		System.out.println("toString() " + cons3);

		System.out.println("Maximum constructor : trial 'kᶣ', as in QUINQUE");
		Consonant cons4 = new Consonant(40, 10, 50, true, false, false, false, false, false, 1, 1);
		System.out.println("print() " + cons4.print());
		System.out.println("toString() " + cons4);

	}

}
