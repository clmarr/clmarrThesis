package clmarrThesis;

import java.util.*;

public class MerciJacquesDemo {

	private static LatinParser lParse = new LatinParser();

	// Nouns to test : lit 'bed', table 'table', neige 'snow', mer 'sea', chien
	// 'dog', boeuf 'cow', legume 'veggie',
	private static List<String> merciJacquesEnglish = Arrays.asList(new String[] { "bed", "tongue", "four", "ice",
			"cow", "dog", "table", "sea"/* ,"vegetable","water" */ }); 
	private static List<String> merciJacquesLatin = Arrays.asList(new String[] { "lectum", "lingua", "quattuor",
			"glacia", "bovem", "canem", "tabula", "mare"/* ,"legu:mem","aqua" */ });

	private final static int columnSize = 17;

	public static void main(String args[]) {
		LatinToFrench LTF = new LatinToFrench();

		// System.out.println("Merci Jacques Demo, in operation ");

		System.out.println("English meanings: ");
		printWordList(merciJacquesEnglish);
		System.out.println("Latin written forms: ");
		printWordList(merciJacquesLatin);

		// get Latin phonemic forms
		List<WordPhon> latPhonForms = getPhonForms(merciJacquesLatin);
		// and print them.
		System.out.println("Latin phonemic forms: ");
		printPhonForms(latPhonForms);

		// make word lexicon:
		LangLexicon gaLexicon = new LangLexicon(merciJacquesEnglish, latPhonForms);

		// to Vulgar Latin
		gaLexicon = LTF.toVulgarLatin(gaLexicon);

		System.out.println("Vulgar Latin");
		printPhonForms(gaLexicon.getLexiconClone().values());

		// to (later) Gallian Vulgar Latin
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
		System.out.println("Early Modern French, circa 1750: ");
		printPhonForms(gaLexicon.getLexiconClone().values());

		gaLexicon = LTF.toModernFrench(gaLexicon);
		System.out.println("Modern French, circa 1930: ");
		printPhonForms(gaLexicon.getLexiconClone().values());

	}

	public static void printWordList(List<String> wordList) {
		String outprint = "";

		for (String word : wordList) {
			outprint = outprint + word;
			for (int i = 0; i < columnSize - word.length(); i++)
				outprint = outprint + " ";
		}

		System.out.println(outprint);
	}

	public static void printPhonForms(Collection<WordPhon> wordList) {
		String outprint = "";

		for (WordPhon word : wordList) {
			String wordStr = "" + word;
			outprint = outprint + wordStr;
			for (int i = 0; i < columnSize - wordStr.length(); i++)
				outprint = outprint + " ";
		}
		System.out.println(outprint);
	}

	public static List<WordPhon> getPhonForms(List<String> wordList) {
		List<WordPhon> output = new ArrayList<WordPhon>();
		for (String word : wordList)
			output.add(lParse.parseWordPhon(word));
		return output;
	}

}
