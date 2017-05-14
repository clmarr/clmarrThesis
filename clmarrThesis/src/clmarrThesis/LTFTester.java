package clmarrThesis;

import java.util.*;

public class LTFTester {
	
	private final static int columnSize = 17;

	static LatinParser lParse = new LatinParser(); 
	static PhoneStructureTranslator ipaParse = new PhoneStructureTranslator(); 
	
	public static void main (String args[])
	{
		Scanner input = new Scanner(System.in); 
		
		LatinToFrench LTF = new LatinToFrench();
	
		System.out.println("Type in the Latin words you would like to test:"); 
		System.out.println("Mark long vowels with a following colon-- ex) a: ");
		System.out.println("Separate different words with a semicolon (;)");
		System.out.println("Now, please enter the Latin words you'd like to test: ");
		
		String latLine = input.nextLine(); 
		List<String> latWords = Arrays.asList(latLine.split(";"));
		
		System.out.println("Now, please enter their English meanings.");
		
		String engLine = input.nextLine(); 
		input.close();
		List<String> engWords = Arrays.asList(engLine.split(";")); 
		
		LangLexicon gaLexicon = new LangLexicon(engWords, getPhonForms(latWords)); 
		

		//to Vulgar Latin 
		gaLexicon = LTF.toVulgarLatin(gaLexicon); 
		
		System.out.println("Vulgar Latin");
		printPhonForms(gaLexicon.getLexiconClone().values()) ;
		
		//to (later) Gallian Vulgar Latin 
		gaLexicon = LTF.toGalloPopularLatin(gaLexicon);
		
		System.out.println("Later Vulgar Latin, with Gallian accentation: ");
		printPhonForms(gaLexicon.getLexiconClone().values());
		
		gaLexicon = LTF.toGalloRomance(gaLexicon);
		
		System.out.println("Gallo-Romance, circa 600: ");
		printPhonForms(gaLexicon.getLexiconClone().values());
		
		gaLexicon = LTF.toEarlyOldFrench(gaLexicon);
		
		System.out.println("Early Old French, circa 800: ");
		printPhonForms(gaLexicon.getLexiconClone().values());
		
		gaLexicon = LTF.toClassicOldFrench(gaLexicon);
		System.out.println("Classic Old French, circa 1100: ");
		printPhonForms(gaLexicon.getLexiconClone().values());
		
		gaLexicon = LTF.toLateOldFrench(gaLexicon);
		System.out.println("Late Old French, circa 1300: ");
		printPhonForms(gaLexicon.getLexiconClone().values());
		
		gaLexicon = LTF.toMiddleFrench(gaLexicon);
		System.out.println("Middle French, circa 1550: ");
		printPhonForms(gaLexicon.getLexiconClone().values());
		
		gaLexicon = LTF.toEarlyModernFrench(gaLexicon); 
		System.out.println("Early Modern French, circa 1780:");
		printPhonForms(gaLexicon.getLexiconClone().values());
		
		gaLexicon = LTF.toModernFrench(gaLexicon); 
		System.out.println("Modern French, circa 1930: ");
		printPhonForms(gaLexicon.getLexiconClone().values());
	}
	
	public static List<List<Phone>> nonEntityList(int n)
	{
		List<List<Phone>> output = new ArrayList<List<Phone>>(); 
		int i = 0 ;
		while (i < n)	
		{	
			output.add(new ArrayList<Phone>()); i++;  
		}
		return output; 
	}
	
	public static List<WordPhon> getPhonForms (List<String> wordList)
	{
		List<WordPhon> output = new ArrayList<WordPhon>(); 
		for(String word : wordList)	output.add(lParse.parseWordPhon(word));
		return output; 
	}
	
	public static void printPhonForms (Collection<WordPhon> wordList)
	{
		String outprint = ""; 
		
		for(WordPhon word : wordList)	{	
			String wordStr = "" + word; 
			outprint = outprint + wordStr; 
			for (int i = 0; i < columnSize - wordStr.length(); i++)	outprint = outprint+" ";
		}
		System.out.println(outprint);
	}
}

