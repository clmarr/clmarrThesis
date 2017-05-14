package clmarrThesis;

import java.util.*;
import java.io.*;

public class TesterPhoneStructureTranslator {

	public static void main(String args[]) {

		List<Phone> phoneyphones = new ArrayList<Phone>();
		phoneyphones.add(new Vowel());
		System.out.println("phoneyphones.get(0) instanceof Vowel? " + (phoneyphones.get(0) instanceof Vowel));
		phoneyphones.add(new Phthong());
		System.out.println("phoneyphones.get(1) instanceof Vowel? " + (phoneyphones.get(1) instanceof Vowel));

		System.out.println("c.equals('c')?" + ('c' == 'c'));

		// what directory are we in?
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		// new instance
		PhoneStructureTranslator hidad = new PhoneStructureTranslator();

		System.out.println("Testing class PhoneStructureTranslator");

		// testing PhoneStructureTranslator, and Vowel now.
		Vowel basicVowel = (Vowel) hidad.getPhone("a");
		System.out.print("For the vowel, 'a'");
		System.out.println(" print() = " + basicVowel.print());
		System.out.println("toString() = " + basicVowel);

		Vowel lessBasicVowel = (Vowel) hidad.getPhone("ẽ̞");
		System.out.println("For the vowel, 'ẽ̞'");
		System.out.println("print() = " + lessBasicVowel.print());
		System.out.println("toString() = " + lessBasicVowel);

		// testing PhoneStructureTranslator and Consonant now
		Phone basicConsonant = hidad.getPhone("ɡ");
		System.out.println("Translating the consonant, 'ɡ'");
		System.out.println("print() = " + basicConsonant.print());
		System.out.println("toString() = " + basicConsonant);

		Phone lessBasicConsonant = hidad.getPhone("lʲ");
		System.out.println("Translating the consonant, lʲ");
		System.out.println("print() " + lessBasicConsonant.print());
		System.out.println("toString() = " + lessBasicConsonant);

		LatinParser himom = new LatinParser();

		System.out.println("Is 'a' a key in latinVowelsStructure?");
		System.out.println(himom.getVowel("a").print());

		String latWord = "lingua";
		System.out.println("Now parsing the phonemes of this Latin word : " + latWord);
		WordPhon phonRep = himom.parseWordPhon(latWord);
		List<Phonic> latPhons = phonRep.getAllPhones();
		for (int p = 0; p < latPhons.size(); p++)
			System.out.print(latPhons.get(p).print() + " " + latPhons.get(p) + "\n ");

	}

}
