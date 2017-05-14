package clmarrThesis;

import java.util.*;

/**
 * LangLexicon
 * 
 * @author Clayton Marr This class represents the lexicon, in phonemic IPA
 *         notation, of a given language at a given time. This internal
 *         'lexicon' of words is represented as a list of word phons Also
 *         included are currently (some) productive rules that will immediately
 *         effect (therefore assimilating) new words added to the language at
 *         the time * Example: the <t> in "Putin" becomes glottalized in
 *         American English, and the <d> of "Vladimir" is similarly flapped
 *         These rules are hierarchically ordered by index (lower index --> are
 *         applied first) , and can be reordered. When/if rules are added, they
 *         are automatically applied on all words in the lexicon However
 *         "spontaneous" application rules can also be done with the method,
 *         applyRule, which doesn't permanently add the rule. When rules are
 *         removed, consistent with Historical Phonology theory, their effects
 *         are NOT reversed, rather they are simply no longer productive. Class
 *         also contains the ability to simultaneously activate a sound shift
 *         upon all words in the LangLexicon
 *
 */
public class LangLexicon {

	private Map<String, WordPhon> lexicon;
	// list of phonemic IPA representation of all relevant words in the language
	// (relevant -- the ones we care to deal with at the moment)
	// in practice, the String-class keys should be the original Latin script
	// for the words

	private List<Alteration> productiveRules;

	// Constructors : note there is no constructor with rules -- this is done to
	// force all rules to be applied when they are added.
	// default constructor : all class variables initialized as empty lists.
	public LangLexicon() {
		lexicon = new HashMap<String, WordPhon>();
		productiveRules = new ArrayList<Alteration>();
	}

	// clone constructor
	public LangLexicon(LangLexicon that) {
		lexicon = new HashMap<String, WordPhon>(that.lexicon);
		productiveRules = new ArrayList<Alteration>(that.productiveRules);
	}

	// constructor with initialized lexicon
	public LangLexicon(Map<String, WordPhon> lex) {
		lexicon = new HashMap<String, WordPhon>(lex); // clone.
		productiveRules = new ArrayList<Alteration>();
	}

	/**
	 * constructor with lexicon initialized through two lists
	 * 
	 * @param wordKeys
	 *            -- String form keys, typically using English translations
	 * @param phonVals
	 *            -- WordPhon values for those keys
	 * @precondition : wordKeys.size() == phonVals.size()
	 */
	public LangLexicon(List<String> wordKeys, List<WordPhon> phonVals) {
		assert wordKeys.size() == phonVals.size() : "Precondition violated: wordKeys.size() == phonVals.size()";
		lexicon = new HashMap<String, WordPhon>();
		productiveRules = new ArrayList<Alteration>();
		// now we fill lexicon.
		for (int i = 0; i < wordKeys.size(); i++)
			putWord(wordKeys.get(i), phonVals.get(i));
	}

	// mutators for lexicon follow:

	/**
	 * putWord -- puts single word in the lexicon with given key, after applying
	 * all in productiveRules to it if the key in the lexicon is already in use,
	 * value is replaced.
	 * 
	 * @param newWord
	 *            -- word to add
	 */
	public void putWord(String newKey, WordPhon newWord) {
		// first apply all productive rules -- we do so on a clone
		// note it is this clone that we add, to prevent outside access, and to
		// prevent modifying the outside variable as a side effect of this
		// method
		WordPhon verbum = new WordPhon(newWord); // the clone WordPhon declared

		// apply all productive rules to the clone
		if (productiveRules.size() > 0) {
			for (Alteration rule : productiveRules)
				verbum.applySoundShift(rule);
		}

		lexicon.put(newKey, verbum); // finally, add the clone.
	}

	/**
	 * putWords -- puts list of words to the lexicon, indexed as per
	 * corresponding list of keys any previously used keys have their values
	 * replaced.
	 * 
	 * @param newKeys
	 *            -- list of keys with which to index words at same position
	 * @param newWords
	 *            -- list of words to add
	 * @precondition : newKeys.size() == newWords.size();
	 * @precondition : for all Strings in newKeys, none are equal to each other.
	 */
	public void putWords(List<String> newKeys, List<WordPhon> newWords) {
		assert newKeys.size() == newWords.size() : "Error: mismatch between size of newKeys and newWords";
		for (int i = 0; i < newKeys.size(); i++) {
			WordPhon verbum = new WordPhon(newWords.get(i));
			for (Alteration rule : productiveRules)
				verbum.applySoundShift(rule);
			lexicon.put(new String(newKeys.get(i)), verbum);
		}
	}

	/**
	 * replaceWord -- replace word linked a given key, while maintaining the key
	 * in the lexicon the only real use of this class is forcing a replacement
	 * or else causing assertion error.
	 * 
	 * @param wordKey
	 *            -- key whose word is getting replaced
	 * @param newWord
	 *            -- the replacement
	 * @precondition lexicon.containsKey(wordKey);
	 */
	// TODO probably delete this class
	public void replaceWord(String wordKey, WordPhon newWord) {
		assert lexicon.containsKey(wordKey) : "Error: The given key does not exist in the lexicon!";
		// make clone, modify it as per productiveRules, and replace wordKey's
		// value with it.
		WordPhon verbum = new WordPhon(newWord);
		for (Alteration rule : productiveRules)
			verbum.applySoundShift(rule);
		lexicon.put(wordKey, verbum);
	}
	// TODO check that this method above actually does what it says does.

	/**
	 * removeWord -- removes and returns the WordPhon of the mapped pair with
	 * key, wordKey
	 * 
	 * @param wordKey
	 *            -- key to remove with
	 * @return wordKey's associated WordPhon value.
	 */
	public WordPhon removeWord(String wordKey) {
		return lexicon.remove(wordKey);
	}

	// mutators on productiveRules follow (all the adders also modify lexicon)

	/**
	 * addRule -- adds rule to productive rules and applies to all words in the
	 * lexicon
	 * 
	 * @param newRule
	 */
	public void addRule(Alteration newRule) {
		productiveRules.add(newRule); // add to productiveRules
		// apply to all words in the lexicon :

		for (WordPhon verbum : lexicon.values())
			verbum.applySoundShift(newRule);

	}

	/**
	 * applyRule -- apply rule to all words in the lexicon, without adding it to
	 * productiveRules (in this method)
	 * 
	 * @param newRule
	 */
	public void applyRule(Alteration newRule) {
		for (WordPhon verbum : lexicon.values())
			verbum.applySoundShift(newRule);
	}

	/**
	 * reorderRule -- change the position of a rule this will affect how rules
	 * are applied to new words added to the lexicon.
	 * 
	 * @param oldInd
	 *            -- old position
	 * @param newInd
	 *            -- new position
	 */
	public void reorderRule(int oldInd, int newInd) {
		productiveRules.add(newInd, productiveRules.remove(oldInd));
	}

	// seems pretty straightforward
	public void switchRules(int firstInd, int secondInd) {
		assert firstInd < productiveRules.size() : "First ind is greater than size of productiveRules!";
		assert secondInd < productiveRules.size() : "Second ind is greater than size of productiveRules!";
		int pare = Math.min(firstInd, secondInd);
		int pas = Math.max(firstInd, secondInd);
		Alteration newFirst = productiveRules.remove(pas);
		productiveRules.add(pas, productiveRules.remove(pare));
		productiveRules.add(pare, newFirst);
	}

	// remove rule at given ind
	// @precondition : given ind is legitimate
	// note we can never remove rules based on what they are as they don't have
	// an equals() method
	public Alteration removeRule(int ind) {
		assert ind < productiveRules.size() : "Given index is greater than the size of the productive rule list!";
		return productiveRules.remove(ind);
	}

	// TODO we really need to write some sort of toString for Alterations, which
	// requires one for candPhones...

	// other methods

	// print all words in the lexicon
	public String printWords() {
		String output = "\nPrinting all keys:words in lexicon";
		for (String key : lexicon.keySet())
			output = output + "\n" + key + " : " + lexicon.get(key);
		return output;
	}

	// return a clone of the lexicon
	public Map<String, WordPhon> getLexiconClone() {
		return new HashMap<String, WordPhon>(lexicon);
	}

	// return a clone of productiveRules
	public List<Alteration> getProductiveRulesClone() {
		return new ArrayList<Alteration>(productiveRules);
	}

	// TODO this class has no toString() or equals() -- maybe we need to make
	// these...
	// note that to make these, we may need to make a Restriction class, and to
	// remake CandPhone to be based on aggregation of objects of that class.
}
