package clmarrThesis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Collection;

public class LatinParser {

	private final String latVowels = "aāeēiīoōuūyȳëü";

	private HashMap<String, String> latinVowelsStructure; // maps Latin vowels
															// to their simple
															// structure as per
															// this project's
															// apparatus
	private HashMap<String, String> latinConsonantsStructure; // same as above
																// for Latin
																// consonants.

	public LatinParser() {
		try {
			initializeLatinMaps();
		} catch (IOException e) {
			System.out.println("IOException while initializing simple vowel maps!");
			e.printStackTrace();
		}

		// TODO fill in more methods here once they are created
	}

	public void initializeLatinMaps() throws IOException {
		latinVowelsStructure = new HashMap<String, String>();
		latinConsonantsStructure = new HashMap<String, String>();

		try {
			BufferedReader inV = new BufferedReader(
					new InputStreamReader(new FileInputStream("LatinCharVowels.txt"), "UTF-8"));
			String currLine;

			// to deal with the invisible first character
			// TODO remove print statement once we can
			// System.out.println("Latin vowels, first char is
			// "+(char)inV.read());
			char dummy = (char) inV.read(); // here to catch residual characters

			while ((currLine = inV.readLine()) != null) {
				currLine = currLine.replaceAll("\\s+", "");
				String[] currPair = currLine.split(";");
				latinVowelsStructure.put(currPair[0], currPair[1]);
			}

			inV.close();
			currLine = "";
			BufferedReader inC = new BufferedReader(
					new InputStreamReader(new FileInputStream("LatinCharConsonants.txt"), "UTF-8"));

			// to deal with the invisible first character
			// TODO remove print statement once we can
			// System.out.println("Latin consonants, first char is
			// "+(char)inC.read());

			while ((currLine = inC.readLine()) != null) {
				currLine = currLine.replaceAll("\\s+", "");
				String[] currPair = currLine.split(";");
				latinConsonantsStructure.put(currPair[0], currPair[1]);
			}
			inC.close();

		} catch (UnsupportedEncodingException e) {
			System.out.println("Encoding unsupported!");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		}
	}

	/**
	 * parseLatinSegList
	 * 
	 * @param segList
	 *            -- list of Strings
	 * @return List<List<Phone>> of parses of these Latin strings
	 * @precondition all preconditions for auxiliary methods encorporated apply
	 *               -- i.e. enter Latin words in lower case, etc...
	 */

	public List<List<Phone>> parseSegList(List<String> segList) {
		List<List<Phone>> output = new ArrayList<List<Phone>>();
		for (String seg : segList)
			output.add(parseSegment(seg));
		return output;
	}

	// parseLatinSegList -- the String version, list is separated by commas

	public List<List<Phone>> parseSegList(String segList) {
		String[] segs = segList.split(",");
		// for (int h = 0; h < segs.length; h++) System.out.println("segs["+h+"]
		// :"+segs[h]);
		List<List<Phone>> output = new ArrayList<List<Phone>>();
		for (int i = 0; i < segs.length; i++)
			output.add(parseSegment(segs[i]));
		return output;
	}

	/**
	 * parseLatinWordPhon
	 * 
	 * @param verbum
	 *            -- Latin word, in Latin script
	 * @return WordPhon containing List of Phones the word is made up of, in
	 *         order, contained between word onset and word coda Note: in some
	 *         ways what we are parsing is the vulgar latin version Note: we
	 *         also ignore gemination and treat long consonants as two separate
	 *         consonants, for conveniency in finding syllabic stress
	 * @precondition PLEASE enter Latin words in lower case!!! * i.e. we ignore
	 *               the aspiration of Greek aspirated consonants * however in
	 *               others we are representing hte "classical" pronunciation
	 *               here, especially with the vowels * as we have not
	 *               incorporated nasalization, the loss of /y/, and various
	 *               other late classical phenomena at this stage
	 */
	public WordPhon parseWordPhon(String verbum) {
		return new WordPhon(setClassicStress(parseSegment(verbum)));
	}

	/**
	 * auxiliary for parseLatinWordPhon -- parses the segment can also be used
	 * for parts that aren't whole words this does not add things like the
	 * PseudoPhones of word onset, word coda, and so on, whereas
	 * parseLatinWordPhon does. it laso returns a List<Phone>, not a WordPhon
	 * 
	 * @param verbum
	 * @return List of Phones the segment is made up of, in order.
	 */

	public List<Phone> parseSegment(String seg) {
		// troubleshoot : where seglist strings have been borrowed for the
		// segments.
		assert !seg.contains(";") : "';' character found in string for segment :/";

		List<Phone> latPhones = new ArrayList<Phone>();

		String remchars = preParseLatin(seg).replaceAll("\\s+", "");// parse and
																	// then
																	// remove
																	// white
																	// space,
																	// making
																	// proxy
																	// string to
																	// avoid
																	// destructive
																	// programming

		Set<String> vowelKeys = latinVowelsStructure.keySet();
		Set<String> consKeys = latinConsonantsStructure.keySet();

		// make phones out of the remaining characters until there are none
		// left.
		// if there are any we can't parse, throw an error!
		while (remchars.length() > 0) {
			// before we do anything we have to check if there are enough
			// characters left not to crash the method in the case of a
			// multi-character phoneme
			if (remchars.length() >= 2) {
				/*
				 * //TODO debugging
				 * System.out.println("First two characters of remchars : "
				 * +remchars.substring(0,2));
				 */

				// first check for a vowel
				// give precedence to diphthongs and other 2-character vowels
				// (note macrons are a character)

				if (vowelKeys.contains(remchars.substring(0, 2))) {

					// get the structure from the mapping
					String mapping = latinVowelsStructure.get(remchars.substring(0, 2));

					// are we dealing with a diphthong?
					// note: in Latin thankfully we do not have to deal wiht
					// triphthongs. Furthermore all Latin diphthongs are falling
					// diphthongs.
					// we detect a diphthong using the '|' delimiter used to
					// mark the different dimensions of hte non-prominent
					// component
					if (mapping.contains("/")) // diphthong
					{
						String[] struct = mapping.split(",");
						// since we only have to deal with falling diphthongs,
						// we can just split the 6th/last index by '|' to get
						// its characteristics.
						String[] fall = struct[5].split("/");

						// make an integer version of the above array for the
						// Phthong class to read
						int[][] fallstruct = new int[1][3];
						for (int i = 0; i < 3; i++)
							fallstruct[0][i] = Integer.parseInt(fall[i]);

						/*
						 * //TODO debugging for (int pikachu = 0; pikachu <
						 * fallstruct[0].length; pikachu++)
						 * System.out.println("fallstruct[0]["+pikachu+"] = "
						 * +fallstruct[0][pikachu]);
						 */

						// declare and add the new diphthong phoneme
						latPhones.add(new Phthong(Integer.parseInt(struct[0]), // place
								Integer.parseInt(struct[1]), // manner
								0, // coArtic
								(struct[2].equals("1")), // roundness, true if
															// we had "1" for
															// that slot
								false, false, false, true, false, // these five
																	// are
																	// constant
																	// for all
																	// Latin
																	// vowels if
																	// we ignore
																	// nasalization
																	// (which we
																	// are
																	// ignoring
																	// for the
																	// moment,
																	// considering
																	// it to be
																	// a sound
																	// shift)
								0, // we also make stress false for now, as it
									// will be decided later in this algorithm
								2 /* Integer.parseInt(struct[3]) */, // length,
																		// which
																		// is
																		// always
																		// 2 for
																		// a
																		// Latin
																		// diphthong
								null, // rise -- no Latin diphthong has a rise
								fallstruct)); // fall, which we assembled above.
					} else // then we are dealing with a simple vowel here.
						latPhones.add(new Vowel(getVowel(remchars.substring(0, 2))));

					remchars = remchars.substring(2); // remove the characters
														// so as not to double
														// count them.
				}
				// then check for a one-character vowel
				else if (vowelKeys.contains(remchars.substring(0, 1))) {
					latPhones.add(getVowel(remchars.substring(0, 1)));
					remchars = remchars.substring(1);
				}
				// next: check for two-character consonant
				else if (consKeys.contains(remchars.substring(0, 2))) {
					String[] struct = latinConsonantsStructure.get(remchars.substring(0, 2)).split(",");
					remchars = remchars.substring(2); // avoid double counting.

					latPhones.add(new Consonant(Integer.parseInt(struct[0]), Integer.parseInt(struct[1]), 0, // place,
																												// manner,
																												// and
																												// coartic,
																												// which
																												// doesn't
																												// exist
																												// in
																												// Latin
																												// aside
																												// from
																												// labiovelars
																												// (handled
																												// with
																												// rounding
																												// variable)
							(struct[2].equals("1")), false, false, // rounding,
																	// is
																	// decided
																	// by 1:0
																	// boolean
																	// ints.
																	// Nasality
																	// and
																	// lateralness
																	// don't
																	// occur for
																	// 2-character
																	// consonants
							false, // rhoticity -- no two letter phoneme is
									// rhotic in Latin (in preparsing, we
									// replaced word-initial 'rh' > 'r')
							(struct[5].equals("1")), // voicing
							false, 0, 1)); // at the moment we are ignoring
											// gemination and treating each long
											// consonant like two short
											// consonants.
				} else if (consKeys.contains(remchars.substring(0, 1))) {
					String chr = remchars.substring(0, 1);
					String[] struct = latinConsonantsStructure.get(chr).split(",");
					remchars = remchars.substring(1);
					latPhones.add(new Consonant(Integer.parseInt(struct[0]), Integer.parseInt(struct[1]), 0,
							(struct[2].equals("1")), (struct[3].equals("1")), (struct[4].equals("1")), // no
																										// 1-character
																										// consonant
																										// is
																										// rounded
							chr.equals("r"), // for rhoticity
							struct[5].equals("1"), false, 0, 1));
				} else // character unsupported
				{
					System.out.println("Unsupported character : " + remchars.charAt(0));
					throw new UnsupportedCharacterError();
				}

			} else if (remchars.length() == 1) {

				if (vowelKeys.contains(remchars))
					latPhones.add(getVowel(remchars));

				else if (consKeys.contains(remchars)) {
					String[] struct = latinConsonantsStructure.get(remchars).split(",");
					latPhones.add(new Consonant(Integer.parseInt(struct[0]), Integer.parseInt(struct[1]), 0, false,
							struct[3].equals("1"), struct[4].equals("1"), remchars.equals("r"), struct[5].equals("1"),
							false, 0, 1));
				} else
					throw new UnsupportedCharacterError();
				remchars = "";
			} else
				System.out.println("For some reason, remchars.length() = 0. Please investigate.");
		}

		// TODO now we set the stress according to Latin stress rules

		return latPhones;

	}

	/**
	 * getLatinVowel
	 * 
	 * @param sym
	 *            : Latin character(s) we are getting the computational
	 *            structure for
	 * @return Vowel object corresponding to the characters
	 * @precondition : latinVowelsStructure.keySet().contains(sym) note also--
	 *               this class should only be called for a single character
	 *               once we have negated the possibility of that character
	 *               being part of a group of characters that represents one
	 *               vowel phoneme!
	 */
	public Vowel getVowel(String sym) {
		String[] struct = latinVowelsStructure.get(sym).split(",");
		return new Vowel(Integer.parseInt(struct[0]), // manner
				Integer.parseInt(struct[1]), // place
				struct[2].equals("1"), // roundness
				Integer.parseInt(struct[3])); // length
	}

	/**
	 * setClassicStress
	 * 
	 * @param phonemes
	 *            : list of the in phonemes in the word, MAKE SURE LENGTH IS
	 *            ACCOUNTED FOR.
	 * @return same list of phonemes except with the correct vowel set to
	 *         stressed
	 * @precondition proper syllable structure is present in the list, phonemes
	 *               Note also that for hte moment we are ignoring the effects
	 *               of the Latin Enclitic Accent * which causes the
	 *               newly-second to last syllable to always be stressed when an
	 *               enclitic is added to the end of hte word (-que, -ne, -ve,
	 *               etc, but never -nam or -dem) Lastly, this stress setter
	 *               will indeed be incorrect for a small set of Latin words,
	 *               which, * due to contraction, have the stress on the last
	 *               syllable.
	 * @precondition : at least one of the phonemes is a vowel.
	 */
	public List<Phone> setClassicStress(List<Phone> phonemes) {
		List<Phone> output = new ArrayList<Phone>(phonemes);

		// TODO debugging
		/*
		 * System.out.println("Determining Classic Latin stressed vowels");
		 */

		int nVs = numVowels(phonemes);

		// there must be more than one vowel in this set of phones
		// -- otherwise calling this method is a colossal waste of time
		assert nVs > 0 : "violated precondition : there needs to be at least one vowel";

		/**
		 * we have two scenarios here-- the first is that we have a mono-, or
		 * di- syllabic word, /* * in which case the first syllable is stressed
		 * the other case is where we have a word with 3+ syllables, which means
		 * we revert to * Latin's trimoraic stress system (which is universal
		 * save a few morphologically-caused exceptions)
		 */

		// check for case one -- a mono, or di- syllabic word.
		// we know this is the case if we have only one or two nuclei (vowels)

		if (nVs < 3) // we only have to deal wiht primary stress, then.
		{
			// if there are less than three vowels, the first is always stressed
			// find the first vowel...
			// we know that there must be at least one vowel because the case of
			// nVs <= 0 is covered by an assertion above.
			int p = 0;
			while (!(phonemes.get(p) instanceof Vowel) && p < 3)
				p++;
			output.get(p).setStress(1);
		}
		// otherwise we clearly have a word
		else // we have to deal with both primary and secondary stress.
		{
			int[] nucArr = vowelIndices(phonemes); // array of syllabic nucleus
													// (vowel) locations,
													// indices

			boolean initStressed; // bool to track whether initial vowel gets
									// primary stress
			// if the initial vowel doesn't get the primary stress, then it gets
			// the secondary stress

			// TODO debugging
			/*
			 * System.out.print("\nnucArr : "); for (int i = 0 ; i <
			 * nucArr.length ; i++) System.out.print(nucArr[i]+",");
			 * System.out.println("\n");
			 */

			int[] onsetArr = getSyllableOnsets(phonemes); // array of onset
															// locations
															// (indices)
			int nS = nucArr.length; // number of syllables. Should equal also
									// onsetArr.length

			int p = nucArr[nS - 2]; // initialize p at the second-to-last vowel

			// TODO debugging
			/*
			 * System.out.print("\nonsetArr:	"); for (int oA : onsetArr)
			 * System.out.print(oA); System.out.println("\nnucArr: "); for (int
			 * nA : nucArr) System.out.print(nA);
			 */

			// check the second to last syllable -- is it heavy?
			// it is heavy if it has two or more morae, i.e. the vowel is long
			// and/or there are one or more consonants in the coda
			// all diphthongs also count as bimoraic here, for classic Latin
			// (not for Old English or Icelandic though). However this should
			// already be dealt with as they should have been assigned a length
			// of 2 elsewhere
			// we know if there are consonants in its coda iff there is one or
			// more phones between the nucleus and the next onset, i.e.
			// nucArr[nS - 2] + 1 < onsetArr [nS - 1]
			if (phonemes.get(p).getLength() > 1 || p + 1 < onsetArr[nS - 1])
				output.get(p).setStress(1);
			else // by the latin Trimoraic rule, then the third-to-last vowel is
					// always stressed, no matter the weight of its syllable.
				output.get(nucArr[nS - 3]).setStress(1);

			// check if the first vowel is stressed -- if not, then give it
			// secondary stress.
			if (output.get(nucArr[0]).isStressed() == false) {
				output.get(nucArr[0]).setStress(-1); // give secondary stress.
			}
		}
		return output;
	}

	/**
	 * getLatinSyllableOnsets -- auxiliary for setClassicStress()
	 * 
	 * @param phones
	 *            -- list of phones
	 * @return array of all the onset places of syllables. Note that geminate
	 *         consonants are split up in this algorithm Note we are ignoring
	 *         the later, primarily Western Latin, e-prosthesis for illegal
	 *         s-cluster onsets (which interestingly did not effect f-cluster
	 *         onsets)
	 */
	public int[] getSyllableOnsets(List<Phone> phones) {
		int[] nuclei = vowelIndices(phones); // nuclei i.e. vowels (incl
												// Pthongs) of the syllables
		int[] onsets = new int[numVowels(phones)]; // we will fill and return
													// this array

		// get the last onsets first.

		for (int n = nuclei.length - 1; n >= 0; n--) // this loop will not
														// affect the first
														// onset, which is dealt
														// with afterward
		{
			// largest possible size of hte onset, not counting its validity
			// (checked later)...
			// is the number of consonants before, with a maximum of 3.
			// we know the number of consonants preceding hte given nucleus
			// because it is the number of phones
			// between this and the previous nucleus.
			int currNucInd = nuclei[n];

			// we want to check validity of the onset with teh largest possible
			// size first,
			// if it is not valid, then check next largest possible size
			// (decreasing onsetSize in the process)

			// the last phone that cannot by any means (for any language) be
			// incorporated into the onset is either the last vowel before the
			// previous one,
			// or the first phone in the word if we are dealing with the first
			// vowel already, which would be the case iff n==0
			int prevBoundary = (n == 0) ? 0 : nuclei[n - 1];
			int onsetSize = Math.min(currNucInd - prevBoundary - 1, 3);

			for (boolean foundCorrectSize = false; onsetSize > 1 && !foundCorrectSize; onsetSize--) {
				// make and fill onsetCluster to pass to method to check
				// validity of onset as per Latin phonology.
				List<Phone> onsetCluster = new ArrayList<Phone>();
				for (int i = 0; i < onsetSize; i++)
					onsetCluster.add(phones.get(currNucInd - onsetSize + i));

				foundCorrectSize = isValidOnset(onsetCluster);

			}

			onsets[n] = nuclei[n] - onsetSize; // if onsetSize = 0, this is a
												// case of a syllable break
												// between two vowels
			// and we should end up adding the same index of the nucleus as the
			// index of hte onset

		}

		return onsets;
	}

	/**
	 * isValidLatinOnset Auxiliary class for getLatinSyllablePhones
	 * 
	 * @param onsPhones
	 * @return true iff this is a valid Latin onset
	 * @precondition only consonants are involved here
	 */
	public boolean isValidOnset(List<Phone> onsPhones) {
		int onsSize = onsPhones.size();
		if (onsSize == 1 || onsSize == 0)
			return true; // TODO revise this-- only some consonants are valid
							// onsets (i.e. not a velar nasal)
		if (onsSize < 0)
			return false;

		// now we know size > 1 , so the following line won't cause errors
		Phone last = onsPhones.get(onsSize - 1);

		String finalPrint = last.print(); // print of the last onset consonant
		if (finalPrint.equals("u"))
			finalPrint = "w";

		Phone secondLast = onsPhones.get(onsSize - 2);

		if (onsSize == 2) {
			if (secondLast.getManner() == 10) // Stop
			{
				// nasal or oral?
				if (secondLast.isNasal()) // then it should definitely be "gn"
											// /ŋn/
					return (secondLast.print().equals("ŋ") && finalPrint.equals("n̪"));
				else // oral
				{
					int firstPlace = secondLast.getPlace();

					// velar or labial -- valid regardless of voicing for /l/ or
					// /r/ following
					if (firstPlace == 40 || firstPlace == 100)
						return (finalPrint.equals("l") || finalPrint.equals("r"));

					// dental. Count alveolar too just to be sure we didn't
					// encode it wrong (English-ly)
					if (firstPlace == 70 || firstPlace == 80) {
						// these can only be valid for 'r' following
						if (!(finalPrint.equals("r")))
							return false;

						// if voiceless -- valid
						if (secondLast.isVoiced())
							return true;
						else // if not is a special case, as 'd' exists but only
								// in Gaulish and Greek loanwords. Since we're
								// dealing with French and Albanian, we count
								// these as true for now. TODO change if
								// alternative evidence arises
							return true;
					}

					// anything else -- invalid
					return false;
				}

			} else if (secondLast.print() == "s") {
				if (last.isNasal()) // then
				{
					// the case of 'sm-' : attested only for Greek loans, but
					// there are a quite a few of them
					// we're putting true for these for now TODO change if new
					// evidence arises
					if (finalPrint.equals("m"))
						return true;

					// the case of 'sn-' : unattested. However may have been
					// valid for Gaulish words.
					// false for now, TODO change if new evidence arises
					if (finalPrint.equals("n̪"))
						return false;

					// other cases -- return false;
					return false;
				} else // oral-- then valid iff followed by a voiceless stop.
					return (last.getManner() == 10 && !last.isVoiced());

			} else if (secondLast.print() == "f") // valid iff followed by l or
													// r
				return (finalPrint.equals("l") || finalPrint.equals("r"));
			else
				return false;
		}
		if (onsSize == 3) {
			Phone secondCons = onsPhones.get(1);

			// all three consonant onset clusters must have s as their first
			// consonant
			if (!onsPhones.get(0).print().equals("s"))
				return false;

			// second consonant must be a voiceless stop -- which ones it can be
			// depends on the third.
			if (!(secondCons.getManner() == 10) && !secondCons.isVoiced())
				return false;

			// for their third-- must be r or l, and which one it is decides
			// what hte second can be.
			if (finalPrint.equals("r")) {
				// if we reached this point, we already know that secondCons is
				// a voiceless stop.

				// initial 'skr' -- true, and this one is quite common
				// ("scribo", etc)
				if (secondCons.getPlace() == 40)
					return true;

				// initial 'str' -- true, also fairly common ("stringo" ,etc)
				if (secondCons.getPlace() == 80)
					return true;

				// initial 'spr' -- unattested except for one loan from German
				// ("Sprea", a river)
				// unclear if this is a valid onset.
				// leave it as a specific false case for now, TODO change if new
				// evidence arises
				if (secondCons.getPlace() == 100)
					return false;

				// if reached this point, no valid second consonants remain
				return false;
			} else if (finalPrint.equals("l")) {
				// if we reached this point, we already know that secondCons is
				// a voiceless stop-- which ones can it be

				// initial 'skl' -- attested but only for rare Greek loans
				// tentatively include for now
				// TODO change if evidence of the exclusion of this onset arises
				if (secondCons.getPlace() == 40)
					return true;

				// initial 'stl' -- occurs for a small set of native Latin words
				// ('stlatta", etc.)
				// tentatively include for now
				// TODO change if evidence of the exclusion of this onset arises
				if (secondCons.getPlace() == 80)
					return true;

				// initial 'spl' -- valid and very common ('splendo', etc.)
				if (secondCons.getPlace() == 100)
					return true;

				// everything else is invalid
				return false;
			} else
				return false;
		}
		return false;
	}

	/**
	 * vowelIndices auxiliary class for getLatinSyllableOnsets
	 * 
	 * @param phoneList
	 *            -- list of phones
	 * @return array of indices of vowels and phthongs, in order.
	 */
	public int[] vowelIndices(List<Phone> phoneList) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int p = 0; p < phoneList.size(); p++) {
			if (phoneList.get(p) instanceof Vowel)
				indices.add(p);
		}

		// TODO debugging
		/*
		 * System.out.println("\n Vowel indices in List<Integer> form: "); for
		 * (int i = 0; i < indices.size(); i++ )
		 * System.out.print(indices.get(i));
		 */

		int[] output = new int[indices.size()];
		for (int j = 0; j < indices.size(); j++)
			output[j] = indices.get(j);

		// TODO debugging
		/*
		 * System.out.println("\noutput for method vowelIndices, as array ");
		 * for (int j = 0; j < indices.size(); j++) System.out.print(output[j]);
		 */

		return output;
	}

	// numVowels -- auxiliary method for getSyllableOnsets. Seems pretty
	// self-explanatory
	// will count for both Phthong objects and proper Vowel objects
	public int numVowels(List<Phone> phoneList) {
		int n = 0;
		for (int p = 0; p < phoneList.size(); p++)
			if (phoneList.get(p) instanceof Vowel)
				n++;
		return n;
	}

	/**
	 * 
	 * @param origLatin
	 *            -- original Latin word
	 * @return preprocessed version so that IPA of Latin word can be outputted
	 *         (i.e. x > cs, ngua > ngva, iam > jam and other preprocessing
	 *         operations)
	 */
	private String preParseLatin(String origLatin) {
		/*
		 * //TODO debugging
		 * System.out.println("input to preParseLatin : "+origLatin);
		 */

		String output = origLatin.replace("x", "ks"); // "x" > "ks" everywhere
		int olen = output.length();

		List<String> skaDiphthongs = Arrays
				.asList(new String[] { "aum", "eum", "oum", "aus", "eus", "ous", "aem", "oem", "aes", "oes" });
		// All not diphthongs in word endings: -aum,-eum,-oum,-aus,-eus,-ous
		if (olen > 2) {
			if (skaDiphthongs.contains(output.substring(olen - 3))) {
				char secondLast = output.charAt(olen - 2);
				assert "eu".contains("" + secondLast) : "There is a problem with skaDiphthongs";
				if (secondLast == 'u')
					output = output.substring(0, olen - 2) + 'ü' + output.substring(olen - 1);
				else /* secondLast == 'e' */ output = output.substring(0, olen - 2) + 'ë' + output.substring(olen - 1);
			}
		}

		// g > gw \ n _ (vowel)
		int nguLoc = output.indexOf("ngu");
		while (nguLoc != -1) {
			if (nguLoc + 3 < origLatin.length()) // make sure string doesn't end
													// in -ngu itself. Given
													// that this is Latin where
													// almost every masc noun
													// ends in -s, this is
													// excessively unlikely, but
													// we must check to avoid
													// crashes anyhow
			{
				if (latVowels.contains(output.substring(nguLoc + 3, nguLoc + 4))) {
					// then replace the 'u' with a 'v' so that the algorithm can
					// instantly recognize it as the labiovelar phoneme
					output = output.substring(0, nguLoc + 2) + "v" + output.substring(nguLoc + 3);
					nguLoc = output.indexOf("ngu"); // in case we (very
													// non-probably) have a word
													// with two instances of
													// "ngu" (to hte best of my
													// knowledge no such Latin
													// verb exists)
				}
			}
			// we also have to check if we have a two character vowel.
			// but in order to check this we need
			else if (nguLoc + 4 < origLatin.length()) {
				if (latVowels.contains(output.substring(nguLoc + 3, nguLoc + 5))) {
					output = output.substring(0, nguLoc + 2) + "v" + output.substring(nguLoc + 3);
					nguLoc = output.indexOf("ngu");
				}
			} else
				nguLoc = -1;
		}

		if (output.length() > 1) {

			// word-initial i before another vowel > j
			if (output.substring(0, 1).equalsIgnoreCase("i")) {
				String nextChar = output.substring(1, 2);
				if (latVowels.contains(nextChar) && !nextChar.equals("̄"))
					output = "j" + output.substring(1);
				else if (output.length() >= 3)
					if (latVowels.contains(output.substring(1, 3)))
						output = "j" + output.substring(1);
			} else if (output.length() > 2) {
				// word-initial rh > r ; another Greek spelling peculiarity that
				// doesn't seem to have had any meaningful difference in native
				// Latin pronunciation
				if (output.substring(0, 2).equalsIgnoreCase("rh"))
					output = "r" + output.substring(2);
			}

		}

		return output;
	}

}
