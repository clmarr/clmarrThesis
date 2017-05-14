package clmarrThesis;

import java.util.*;
import java.io.*;

public class PhoneStructureTranslator {

	// TODO WARNING DO NOT EVER USE THE TREMA THAT IS A SINGLE CHARACTER BY
	// ITSELF FROM PHONETICS WEBSITES AS IT WILL CAUSE MASSIVE PROBLEMS.

	// private final String latVowels = "aāeēiīoōuūyȳëü";
	private final String simpleVowels = "uɯu̞ɯ̞oɤo̞ɤ̞ɔʌɒɑʊωʉɨɵɘəɞɜɐɒ̈äʏɪyiy̞i̞øeø̞e̞œɛæɶa";
	// private final String semiVowels = "wɰɥj";
	private final String rhoticConsonants = "ʀʁɽɻɭrɾɹ"; // note that this list
														// holds true for
														// French, Latin and
														// Albanian, but not
														// necessarily other
														// languages. The
														// alveolar tap, in
														// particular, is
														// certainly not rhotic
														// in American English,
														// and languages with
														// uvular consonants
														// typically don't
														// consider the uvular
														// fricative rhotic.
	// private final String latinOnsets = Arrays.asList(new
	// String[]{"bl","pl","gl","kl","br","pr","tr","gr","kr")

	private HashMap<String, String> IPAToSimpleVowelStructure; // maps IPA
																// symbols to
																// String
																// showing
																// place, manner
																// and roundness
	private HashMap<String, String> simpleVowelStructureToIPA; // maps string
																// showing
																// place, manner
																// and roundness
																// to IPA
																// symbols

	// both of the above actually include also semivowels, because they are used
	// in CandPhone to deal with semivowels that are components of Phthongs

	private HashMap<String, String> IPAToSimpleConsonantStructure; // maps IPA
																	// symbols
																	// to String
																	// showing
																	// place,
																	// manner,
																	// roundness,
																	// nasality,
																	// lateralness
																	// and
																	// voicing

	public PhoneStructureTranslator() {
		try {
			initializeVowelMaps();
			initializeConsonantMaps();
		} catch (IOException e) {
			System.out.println("IOException while initializing simple vowel maps!");
			e.printStackTrace();
		}

		// TODO fill in more methods here once they are created
	}

	public void initializeConsonantMaps() throws IOException {
		IPAToSimpleConsonantStructure = new HashMap<String, String>();
		try {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(new FileInputStream("SimpleConsonantsByIPA.txt"), "UTF-8"));
			String currLine;
			while ((currLine = in.readLine()) != null) {
				currLine = currLine.replaceAll("\\s+", ""); // strip white space
															// and invisible
															// characters
				String[] currPair = currLine.split(";"); // split IPA symbol
															// from parameter
															// values
				IPAToSimpleConsonantStructure.put(currPair[0], currPair[1]);
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Encoding unsupported!");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		}
	}

	public void initializeVowelMaps() throws IOException {
		IPAToSimpleVowelStructure = new HashMap<String, String>();

		try {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(new FileInputStream("SimpleVowelsByIPA.txt"), "UTF-8"));
			String currLine;
			while ((currLine = in.readLine()) != null) {
				currLine = currLine.replaceAll("\\s+", ""); // strip white space
															// and invisible
															// characters
				String[] currPair = currLine.split(";"); // split IPA symbol
															// from parameter
															// values
				IPAToSimpleVowelStructure.put(currPair[0], currPair[1]); // put
																			// the
																			// pair
																			// into
																			// the
																			// HashMap
			}
			in.close();
			simpleVowelStructureToIPA = inverseMap(IPAToSimpleVowelStructure);

		} catch (UnsupportedEncodingException e) {
			System.out.println("Encoding unsupported!");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		}
		// file input should be of the form -- IPA;place,manner,roundedness[1,0]

		// TODO this-- initialize using text file, simpleVowelsByIPA.txt
	}

	/**
	 * parseWordPhon -- declare new WordPhon containing given list of phones in
	 * order Please separate separate phones by commas!
	 * 
	 * @param ipaSegment
	 *            -- string of consecutive phones, each separated by commas
	 * @return WordPhon given these
	 */
	public WordPhon parseWordPhon(String ipaSegment) {
		String[] strPhones = ipaSegment.replaceAll(" ", "").split(",");
		List<Phone> seq = new ArrayList<Phone>();
		for (int i = 0; i < strPhones.length; i++)
			seq.add(getPhone(strPhones[i]));
		return new WordPhon(seq);
	}

	/**
	 * parseSegmentList -- parse list of strings of consecutive phones Please
	 * abide by the following! : segments should be separated by semicolons <;>
	 * phones within segments should be separated by commas <,>
	 * 
	 * @param ipaSegmentList
	 *            -- list of strings of consecutive phones
	 * @return list of lists of phones in each segment
	 */
	public List<List<Phone>> parseSegList(String ipaSegmentList) {
		String[] segments = ipaSegmentList.replaceAll(" ", "").split(";");
		List<List<Phone>> out = new ArrayList<List<Phone>>();

		for (int j = 0; j < segments.length; j++)
			out.add(parseSegment(segments[j]));

		return out;
	}

	/**
	 * parseSegment -- parse string of consecutive phones To make things simple,
	 * we say that separate phones are separated by commas here (semicolons will
	 * ultimatelly separate diff segments) PLEASE USE THE COMMAS
	 * 
	 * @param ipaSegment
	 *            -- string of consecutive phones
	 * @return list of phones involved
	 */
	public List<Phone> parseSegment(String ipaSegment) {
		String[] strPhones = ipaSegment.replaceAll(" ", "").split(",");
		List<Phone> output = new ArrayList<Phone>();

		for (int i = 0; i < strPhones.length; i++) {
			output.add(getPhone(strPhones[i]));
		}

		return output;
	}

	/**
	 * getPhone
	 * 
	 * @param ipa
	 *            : IPA representation of the phone
	 * @return Phone as an object
	 * @precondition ipa is a legitimate IPA symbol Branches into three
	 *               auxiliary methods : getVowel, getPhthong and getConsonant
	 */
	public Phone getPhone(String ipa0) {
		String ipa = ipa0.replaceAll("g", "ɡ").replaceAll("ä", "ä");

		// first we need to quickly determine if we are dealing with a Phthong
		// -- we'll use if it contains the nonsyllabic marker, the inverted
		// breve, that all Phthongs have
		if (ipa.contains("̯"))
			return getPhthong(ipa);

		// TODO debugging
		/*
		 * System.out.println("Before line 155 error, ipa is... "+ipa);
		 */

		// are we dealing with a simple vowel?
		if (ipa.length() >= 2) {
			if (simpleVowels.contains(ipa.substring(0, 2)))
				return getVowel(ipa);
			else if (simpleVowels.contains(ipa.substring(0, 1)))
				return getVowel(ipa);
			else
				return getConsonant(ipa);
		}

		else if (simpleVowels.contains(ipa.substring(0, 1)) || ipa.contains("̈") || ipa.contains("̞"))
			return getVowel(ipa);
		// we also do the first 2 characters not just the first alone because
		// this way we can catch the 8 phones with lowering diacritics

		// if we reach this point, we know it must be a consonant
		return getConsonant(ipa);
	}

	/**
	 * getConsonant -- auxiliary method of getPhone
	 * 
	 * @param sym
	 *            : IPA representation
	 * @return Consonant as an object
	 * @precondition sym is a legitimate IPA representation
	 */
	public Consonant getConsonant(String sym) {
		String[] baseStructure = new String[6];
		Set<String> consKeys = IPAToSimpleConsonantStructure.keySet();

		/*
		 * because some consonants contain characters of other consonants in
		 * them, we have to check for the longer ones in the key list first.
		 * However, to avoid an IndexOutOfBoundsException, we also have to make
		 * sure indices exist before checking them
		 */

		for (int c = (sym.length() > 2) ? 3 : sym.length(); c > 0 && baseStructure[0] == null; c--) {
			if (consKeys.contains(sym.substring(0, c)))
				baseStructure = IPAToSimpleConsonantStructure.get(sym.substring(0, c)).split(",");
		}
		if (baseStructure[0] == null)
			throw new UnsupportedCharacterError(sym);

		// deal with place modifiers -- the most likely culprit here is
		// dentalness, as t and d are dental for all three relevant languages
		// here.
		if (sym.contains("̪"))// dental
			baseStructure[0] = "80"; // change to dental

		if (sym.contains("̠")) // retracted; we ignore the advanced notation as
								// we don't expect to deal with it
			baseStructure[0] += -10; // retract to next position backwards

		// ignoring raised and lowered markings as we don't expect to deal with
		// them.

		// deal with rounding and coArtic, for hte moment excluding the
		// possibility of (other) multiple coarticulation.
		// because except for two languages in the world, rounding of
		// non-palatal consonants always implies velarization, we consider it so
		// here as well.

		int coArtic = 0;
		// the various methods of noting velarization:

		if (sym.contains("ʷ")) {
			if (!baseStructure[0].equals("40"))
				coArtic = 40;
			baseStructure[2] = "1";
		} else if (sym.contains("̴") || sym.contains("ˠ") || sym.contains("ᶭ")) /* velarize */ coArtic = 40;
		else if (sym.contains("ᶣ")) {
			if (!baseStructure[0].equals("50"))
				coArtic = 50;
			baseStructure[2] = "1";
		} else if (sym.contains("ʲ")) /* palatalize */ coArtic = 50;
		else if (sym.contains("ˀ")) /* glottalize */ coArtic = 10;
		// we exclude other forms of coarticulation (ex pharyngealization) as we
		// don't expect to deal with them

		// devoicing marking -- we assume we will never have to deal with teh
		// voicing marking
		if (sym.contains("̥"))
			baseStructure[5] = "0";

		// length -- for the moment we exclude the possibility of triply long
		// consonants
		int len = (sym.contains("ː")) ? 2 : 1;

		return new Consonant(Integer.parseInt(baseStructure[0]), // place
				Integer.parseInt(baseStructure[1]), // manner~closeness
				coArtic, (baseStructure[2].equals("1")), // roundness
				(baseStructure[3].equals("1")), // nasality
				(baseStructure[4].equals("1")), // lateralness
				rhoticConsonants.contains(sym.substring(0, 1)), // rhoticity
				(baseStructure[5].equals("1")), // voicedness
				sym.contains("ʰ"), // aspiration
				0, // stress
				len);
	}

	/**
	 * getVowel -- auxiliary method of getPhone
	 * 
	 * @param sym
	 *            : IPA representation
	 * @return Vowel as an object
	 * @precondition sym is a legitimate IPA representation
	 */
	public Vowel getVowel(String sym) {
		String struct[] = new String[3];
		if (sym.length() >= 2) {
			if (simpleVowels.contains(sym.substring(0, 2))) {
				struct = IPAToSimpleVowelStructure.get(sym.substring(0, 2)).split(",");
			} else
				struct = IPAToSimpleVowelStructure.get(sym.substring(0, 1)).split(",");
		} else
			struct = IPAToSimpleVowelStructure.get(sym.substring(0, 1)).split(",");

		int len;
		int indLen1 = sym.indexOf('ː'); // index of first length marker. -1 if
										// it doesn't exist.
		if (indLen1 != -1)
			len = 2 + ((sym.substring(indLen1 + 1).contains("ː")) ? 1 : 0);
		else
			len = 1;

		return new Vowel(Integer.parseInt(struct[0]), Integer.parseInt(struct[1]),
				0 /* vowels have no coArtic */, (struct[2].equals("1")), // rounded
																			// iff
																			// it
																			// is
																			// 1.
				(sym.contains("̃")), // i.e. it contains the nasal above tilde,
										// although it is difficult to see here
				false, false, true, false, (sym.contains("`")) ? 1 : (sym.contains("'") ? -1 : 0), // stress
				len);
	}

	/**
	 * getPhthong -- auxiliary method of getPhone
	 * 
	 * @param sym
	 *            : IPA representation
	 * @return Phthong as an object
	 * @precondition only one nucleus is marked, i.e. only one vowel here lacks
	 *               the inverted breve note this method also assumes that all
	 *               Phthongs have a lenght of 2 or more. * this is true of
	 *               French, Latin and Albanian, but not true in some other
	 *               cases -- Old English, Icelandic, etc.
	 */
	public Phthong getPhthong(String sym) {
		List<String> comps = splitPhthongParts(sym);
		int csize = comps.size();
		int indNuc = findNucleus(comps);

		int[][] theRise = (indNuc == 0) ? null : new int[indNuc][3];
		int[][] theFall = (indNuc == csize - 1) ? null : new int[csize - indNuc - 1][3];
		if (theRise != null) {
			for (int i = 0; i < indNuc; i++) {
				String currComp = comps.get(i);
				String[] currStruct = new String[3];
				if (currComp.contains("̞"))
					currStruct = IPAToSimpleVowelStructure.get(currComp.substring(0, 2)).split(",");
				else
					currStruct = IPAToSimpleVowelStructure.get(currComp.substring(0, 1)).split(",");
				if (theRise != null) {
					theRise[i][0] = Integer.parseInt(currStruct[0]);
					theRise[i][1] = Integer.parseInt(currStruct[1]);
					theRise[i][2] = Integer.parseInt(currStruct[2]);
				}
			}
		}
		if (theFall != null) {
			for (int j = 0; j + indNuc + 1 < csize; j++) {
				String currComp = comps.get(j + indNuc + 1);
				String[] currStruct = new String[3];
				if (currComp.contains("̞"))
					currStruct = IPAToSimpleVowelStructure.get(currComp.substring(0, 2)).split(",");
				else
					currStruct = IPAToSimpleVowelStructure.get(currComp.substring(0, 1)).split(",");
				theFall[j][0] = Integer.parseInt(currStruct[0]);
				theFall[j][1] = Integer.parseInt(currStruct[1]);
				theFall[j][2] = Integer.parseInt(currStruct[2]);
			}
		}
		Vowel nuc = getVowel(comps.get(indNuc));
		if (nuc.getLength() < csize)
			return new Phthong(nuc, theRise, theFall); // note here that this
														// means csize will be
														// the length : this is
														// false for languages
														// such as Old English
														// and Icelandic, and if
														// this system is ever
														// used for them, will
														// likely have to be
														// changed.

		return new Phthong(getVowel(comps.get(indNuc)), theRise, theFall, nuc.getLength());
	}

	/**
	 * splitPhthongParts -- auxiliary method of getPhthong
	 * 
	 * @param phthipa
	 *            -- correct IPA for a Phthong
	 * @return array of the different vowels making up that Phthong
	 * @precondition all present symbols are legitimate IPA symbols and only one
	 *               is written as prominent, i.e. lacking the inverted breve
	 */
	private List<String> splitPhthongParts(String phthipa) {
		List<String> vowels = new ArrayList<String>();

		// note we are assuming that minor part diacritic is placed AFTER the
		// character it modifies, as is typical

		assert simpleVowels.contains(phthipa.substring(0,
				1)) : "Initial character of phthong IPA is not a legitimate vowel -- something must be wrong! Encoding?";

		// iterate through string, declaring a new vowel if a new legit IPA
		// vowel symbol appears, and otherwise adding the likely diacritics onto
		// the last vowel.
		String currVowel = phthipa.substring(0, 1);
		String syms; // we will manipulate what is left of phthipa at each
						// iteration.
		for (syms = phthipa.substring(1); syms != ""; syms = (syms.length() == 1) ? "" : syms.substring(1)) {
			String nextChar = syms.substring(0, 1);
			if (simpleVowels.contains(nextChar) && nextChar != "̞") {
				vowels.add(currVowel);
				currVowel = new String(nextChar);
			} else
				currVowel = currVowel + nextChar; // should add phthong
													// diacritics
		}
		vowels.add(currVowel); // add the last one.
		return vowels;
	}

	/**
	 * findNucleus -- auxiliary method of getPhthong
	 * 
	 * @param parts
	 *            -- each vowel component as a String symbol in a list
	 * @return index of the prominent vowel within the above list
	 * @precondition only one component vowel (i.e. String within parts) lacks
	 *               the inverted breve marking non-prominence
	 */
	private int findNucleus(List<String> parts) {
		for (int i = 0; i < parts.size(); i++) {
			if (!parts.get(i).contains("̯"))
				return i;
		}
		return -1;
	}

	/**
	 * quickVowel if we know the four necessary variables this can quickly be
	 * called, assuming a simple vowel
	 */

	/**
	 * inverseMap
	 * 
	 * @param sourceMap:
	 *            map which we are inverting
	 * @return a new map with the values of sourceMap mapped to its keys
	 * @precondition no two values of sourceMap are the same.
	 */
	private <V, K> HashMap<V, K> inverseMap(Map<K, V> sourceMap) {
		HashMap<V, K> inverse = new HashMap<V, K>();
		for (K k : sourceMap.keySet())
			inverse.put(sourceMap.get(k), k);
		return inverse;
	}

}
