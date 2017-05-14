package clmarrThesis;

import java.util.*;

public class LatinToFrench {

	// TODO place somewhere : loss of stress distinctions

	/**
	 * assumptions made here: 1) most of these alterations, except for a few
	 * otherwise specified, stayed as productive rules for assimilating new
	 * words for awhile 2) however most of those were no longer active in the
	 * next major period (unless otherwise specified).
	 */

	public LatinParser lParse; // TODO make this again private
	private PhoneStructureTranslator ipaParse;

	// easy references for dental n, t and d -- the articulation in Latin,
	// Gaulish and French
	private Consonant nDent, tDent, dDent;
	private String nDentCh, tDentCh, dDentCh;

	// internal use only : all possible variable types
	private final List<String> possVars = Arrays
			.asList("type,place,manner,coArtic,rounded,nasal,lateral,rhotic,voiced,aspirated,stress,length,rise,fall"
					.split(","));
	// internal use only : all boolean variable types
	private final List<String> boolVars = Arrays.asList("rounded,nasal,lateral,rhotic,voiced,aspirated".split(","));

	public LatinToFrench() {
		lParse = new LatinParser();
		ipaParse = new PhoneStructureTranslator();
		nDent = new Consonant(lParse.parseSegment("n").get(0));
		tDent = new Consonant(lParse.parseSegment("t").get(0));
		dDent = new Consonant(lParse.parseSegment("d").get(0));
		nDentCh = nDent.print();
		tDentCh = tDent.print();
		dDentCh = dDent.print();

	}

	public LangLexicon toVulgarLatin(LangLexicon CL) {
		LangLexicon VL = new LangLexicon(CL.getLexiconClone());

		// TODO currently unchecked -- check
		// unrounding of y: and y
		List<Phone> fUTargets = lParse.parseSegment("ȳy");
		List<Phone> fUDests = ipaParse.parseSegment("iː,i");

		Alteration frontUnrounding = new Alteration(includeStressedVariants(capsulate(fUTargets)),
				includeStressedVariants(capsulate(fUDests)));
		VL.addRule(frontUnrounding);

		// TODO currently unchecked -- check
		// deaspiration
		Alteration deAspiration = new Alteration(capsulate(mapFeatShift(lParse.parseSegment("ctp"), "aspirated", 0)),
				lParse.parseSegList("c,t,p"));
		VL.addRule(deAspiration);

		// TODO currently unchecked -- check
		// first vowel nasalization, stage 1 -- vowels before nasals that
		// precede fricatives get a nasal articulation, nasals at first are
		// unchanged
		// possibly originated as a Celtic substrate effect but ultimately
		// spread throughout almost all Latin dialects
		List<Phone> latinVowels = lParse.parseSegment("aāeēiīoōuū");
		latinVowels.addAll(mapFeatShift(latinVowels, "stress", 1)); // include
																	// stressed
																	// versions
																	// too.

		Alteration firstVowelNasalizationPreFric = new Alteration(null, parseCandRestricts("+nasal;+fricative"),
				includeStressedVariants(capsulate(latinVowels)),
				includeStressedVariants(capsulate(mapFeatShift(latinVowels, "nasal", 1))));
		VL.addRule(firstVowelNasalizationPreFric);

		// first vowel nasalization also affects vowels preceding "m" and then
		// the word coda...
		// possibly originated as a Celtic substrate effect but ultimately
		// spread throughout almost all Latin dialects
		Alteration firstVowelNasalizationPreCoda = new Alteration(null, parseCandRestricts("+nasal,+labial;+wordcoda"),
				includeStressedVariants(capsulate(latinVowels)),
				includeStressedVariants(capsulate(mapFeatShift(latinVowels, "nasal", 1))));
		VL.addRule(firstVowelNasalizationPreCoda);

		// TODO currently unchecked -- check
		// TODO -- possibly delete this as it may have been bled in Celtic
		// substrate areas by a g > gamma shift
		// nasalization of g before n
		Alteration velarNasalization = new Alteration(null, parseCandRestricts("+coronal,+nasal"),
				lParse.parseSegList("g"), ipaParse.parseSegList("ŋ"));
		VL.addRule(velarNasalization);

		// nasalization of n before velars, including labiovelars
		Alteration nasalVelarization = new Alteration(null, parseCandRestricts("+velar"), lParse.parseSegList("n"),
				capsulate(mapFeatShift(ipaParse.parseSegment("n"), "place", 40)));
		VL.addRule(nasalVelarization);

		// closing of short front vowels that precede other vowels
		List<Phone> pVCTargets = ipaParse.parseSegment("ɪ,ɛ,ʊ,ɔ");
		pVCTargets.addAll(mapFeatShift(pVCTargets, "stress", 1)); // include
																	// also the
																	// stressed
																	// versions
		List<Phone> pVCDests = ipaParse.parseSegment("i,e,u,o");
		pVCDests.addAll(mapFeatShift(pVCTargets, "stress", 1));
		Alteration preVowelClosing = new Alteration(null, // no prior context
				parseCandRestricts("+Vowel"), // posterior context: that its a
												// vowel
				includeStressedVariants(capsulate(pVCTargets)), includeStressedVariants(capsulate(pVCDests)));
		VL.addRule(preVowelClosing);

		// TODO currently unchecked -- check
		// monophthongization of /ae/-diphthong to /ɛ:/, and /oe/-diphthong to
		// /e:/
		List<Phone> monTargets = lParse.parseSegment("aeoe");
		monTargets.addAll(mapFeatShift(monTargets, "stress", 1));
		List<Phone> monDests = ipaParse.parseSegment("ɛː,eː");
		monDests.addAll(mapFeatShift(monDests, "stress", 1));

		Alteration monophthongization = new Alteration(includeStressedVariants(capsulate(monTargets)),
				includeStressedVariants(capsulate(monDests)));
		VL.addRule(monophthongization);

		// loss of h
		// TODO make sure null destination is covered using this example.
		Alteration hLoss = new Alteration(capsulate(lParse.parseSegment("h")), nonEntityList(1));
		VL.addRule(hLoss);

		return VL;
	}

	public LangLexicon toGalloPopularLatin(LangLexicon preGPL) // i.e. around
																// 1st century
																// BC to 2nd
																// century AD,
																// ish.
	{

		// TODO add? au > a in unstressed intertonic syllables (ex bagautai >
		// baghadai)

		// TODO for developments of kw intervocalic, consult Pope: sections 187;
		// 327-330

		LangLexicon GPL = new LangLexicon(preGPL.getLexiconClone()); // pass
																		// lexicon
																		// from
																		// last
																		// period
																		// but
																		// not
																		// the
																		// list
																		// of
																		// productive
																		// rules.

		// TODO debugging
		System.out.println("At beginning of GPL: ");
		printPhonForms(GPL.getLexiconClone().values());

		// ɸ > f everywhere. This happens in all Vulg Latin dialects except
		// Spanish, Gascon, Aragonese
		Alteration toLabDentFric = new Alteration(ipaParse.parseSegList("ɸ"), ipaParse.parseSegList("f"));
		GPL.addRule(toLabDentFric);

		// TODO currently unchecked -- check
		// loss of length distinction -- happened in all Latin dialects, around
		// 2nd century CE
		// however WE DO NOT effect stressed vowels in this shift, as stressed
		// vowels remain long in Gaul.
		List<Phone> longVowels = ipaParse.parseSegment("iː,eː,ɛː,äː,uː,oː");
		longVowels.addAll(mapFeatShift(longVowels, "nasal", 1)); // neutralize
																	// also the
																	// nasalized
																	// longvowels

		Alteration neutralizeLength = new Alteration(includeStressedVariants(capsulate(longVowels)),
				includeStressedVariants(capsulate(mapFeatShift(longVowels, "length", 1))));
		GPL.addRule(neutralizeLength);

		// NOTE it is at this point that Latin's trimoraic stress system became
		// no longer productive, due to hte loss in length distinctions
		// as a result, stress becomes PHONOLOGIZED, with minimal pairs emerging
		// HOWEVER in the case of French (North Gallo-Latin), Latin's trimoraic
		// stress system might have never been productive
		// as evidenced by the fact that loanwords from the Gaulish substrate
		// seem to follow Gaulish, rather than Latin, stress
		// and so we never add that rule as productive.

		// TODO currently unchecked -- check
		// denasalization of vowels before nasal consonant then f: bleeds
		// subsequent nasal consonant loss in Gallo-Romance areas
		List<Phone> bPFTargets = mapFeatShift(ipaParse.parseSegment("i,ɪ,e,ɛ,ä,u,ʊ,o,ɔ"), "nasal", 1);

		Alteration bleedPreF = new Alteration(null, // no prior context
				parseCandRestricts("+nasal,+consonant;+spirant"), // context is
																	// nasal
																	// cons then
																	// [f], the
																	// only
																	// spirant
																	// at this
																	// time
				includeStressedVariants(capsulate(bPFTargets)),
				includeStressedVariants(ipaParse.parseSegList("i;ɪ;e;ɛ;ä;u;ʊ;o;ɔ")));
		GPL.addRule(bleedPreF);

		// TODO fix -- currently disfunctional
		// nasal consonants absorbed after nasal vowels and before either s or a
		// for simplicity's sake, we're going to ignore the fact that for awhile
		// nasal vowels were longer,
		// as this was ultimately lost as well and will not change the out come
		// of the simulation
		Alteration nasalConsLoss = new Alteration(parseCandRestricts("+vowel,+nasal"), // prior
																						// context
																						// is
																						// a
																						// nasal
																						// vowel,
																						// which
																						// absorbs
																						// the
																						// consonant
				null, lParse.parseSegList("n,m"), nonEntityList(2)); // map to
																		// null
																		// segment
		GPL.addRule(nasalConsLoss);

		// TODO check -- currently unchecked
		// reinsertion of an m after nasal vowels in monosyllabic words.
		// we can tell they're mono syllabic as they have a stressed vowel
		// before a coda!
		Alteration nasConsRetention = new Alteration(parseCandRestricts("+vowel,+nasal,+stressed"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), ipaParse.parseSegList("m"));
		GPL.addRule(nasConsRetention);

		// TODO check -- currently unchecked
		// i-prosthesis
		Alteration iProsthesis = new Alteration(parseCandRestricts("+wordonset"), // prior
																					// context
																					// is
																					// only
																					// at
																					// the
																					// beginning
																					// of
																					// words
				parseCandRestricts("+consonant"), // posterior context : s +
													// consonant
				lParse.parseSegList("s"), lParse.parseSegList("is"));
		GPL.addRule(iProsthesis);

		// TODO debugging
		System.out.println("Middle of GPL: ");
		printPhonForms(GPL.getLexiconClone().values());

		// nasal vowels denasalized, nasalization not productive for a time
		List<Phone> nasVowels = mapFeatShift(lParse.parseSegment("aāaueēiīoōuū"), "nasal", 1);

		nasVowels.addAll(mapFeatShift(nasVowels, "stress", 1));

		Alteration Denasalization = new Alteration(includeStressedVariants(capsulate(nasVowels)),
				includeStressedVariants(capsulate(mapFeatShift(nasVowels, "nasal", 0))));
		GPL.addRule(Denasalization);

		// vowel shifts ɪ > e and ʊ > o
		List<Phone> nCVSTargs = ipaParse.parseSegment("ɪ,ʊ");
		nCVSTargs.addAll(mapFeatShift(nCVSTargs, "stress", 1));
		List<Phone> nCVSDests = ipaParse.parseSegment("e,o");
		nCVSDests.addAll(mapFeatShift(nCVSDests, "stress", 1));

		Alteration nearCloseVowelShift = new Alteration(includeStressedVariants(capsulate(nCVSTargs)),
				includeStressedVariants(capsulate(nCVSDests)));
		GPL.addRule(nearCloseVowelShift);

		// West Latin r-metathesis : at the ends of words er > re, or>ro (in
		// Spanish, all become -ro)
		Alteration finalRhoticMetathesis = new Alteration(null, parseCandRestricts("+wordcoda"),
				lParse.parseSegList("er,or"), lParse.parseSegList("re,ro"));
		GPL.addRule(finalRhoticMetathesis);

		// West Latin unstressed e,i before another vowel ("in hiatus") > j :
		// feeds later palatalization via yod absorption
		// we don't have to deal with any stressed or long variants as they are
		// never affected
		Alteration hiatusToYod = new Alteration(null, // no prior context
				parseCandRestricts("+vowel"), ipaParse.parseSegList("i;e;u;o"), // we
																				// dont
																				// have
																				// to
																				// deal
																				// with
																				// ɛ
																				// as
																				// it
																				// has
																				// shifted
																				// to
																				// e
																				// pre-vowels.
				ipaParse.parseSegList("j;j;w;w"));
		GPL.addRule(hiatusToYod);

		// Wikipedia here overruled by Pope + data
		// note that although Wikipedia claims this only happened with
		// unstressed prelateral vowels after velars,
		// Pope seems to disagree, and there are plenty of counterexamples (ex
		// tabula, fabula, vetulum > veclum)
		// West Latin intertonic (i.e. unstressed and in interior syllable)
		// vowel loss : in some cases bleeds palatalization
		// source: Pope page 115, section 262
		Alteration WLVowelLoss = new Alteration(parseCandRestricts("-wordonset;+stop,-rounded,-nasal"),
				parseCandRestricts("+coronal,-rhotic,-sibilant,-nasal;-wordcoda"), // in
																					// practice
																					// this
																					// means:
																					// k,t,l.
																					// does
																					// it
																					// really
																					// need
																					// to
																					// not
																					// be
																					// sibilant?
				ipaParse.parseSegList("i;e;ɛ;ä;u;o;ɔ"), nonEntityList(7));
		GPL.addRule(WLVowelLoss);

		// TODO currently unchecked -- check
		// ps,pt > ks,kt : a Celtic substrate effect on Gallo-Latin (and some
		// Ibero-Latin)
		Alteration paraxsidiPart1 = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+coronal,-sonorant,-nasal"), capsulate(lParse.parseSegment("p")),
				capsulate(lParse.parseSegment("k")));
		GPL.addRule(paraxsidiPart1);

		// another Celtic substrate effect: k> x \ __s,t,n
		Alteration paraxsidiPart2 = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+coronal,-sonorant,-nasal"), capsulate(lParse.parseSegment("k")),
				capsulate(ipaParse.parseSegment("x")));
		GPL.addRule(paraxsidiPart2);

		// tl as formed by intertonic vowel losses > kl
		Alteration tlToKl = new Alteration(null, parseCandRestricts("+consonant,+lateral"), // i.e.
																							// before
																							// l.
				lParse.parseSegList("t"), ipaParse.parseSegList("k"));
		GPL.addRule(tlToKl);

		// around this time, as I understand/infer it from the sources, Gallian
		// Latin seems
		// ... to develop a rule stating that w can not be the initial consonant
		// in a syllable
		// ... however, it CAN be in the syllable coda (still). For more info
		// see Pope p91, for example (tho not all there)

		// thus in certain syllable final areas, it was able to remain w for a
		// time, while elsewhere it universely became β

		// to implement this, we need to use a prop consonant for w first,
		// then shift all instances to either w or β
		// for our prop, we use the labiodental approximant ʋ (which in fairness
		// actually commonly is a destination of shifts targeting w)

		List<Phone> propCapsule = new ArrayList<Phone>(ipaParse.parseSegment("ʋ"));
		Alteration makeWProp = new Alteration(ipaParse.parseSegList("w"), capsulate(propCapsule));
		GPL.addRule(makeWProp);

		// formerly intervocalic w that becomes preconsonantal due to
		// WLVowelLoss becomes short u and ultimately diphthongizes prior vowel
		// -- Pope p91 sect 187? See also Pope section 254
		// here we just shift it to /w/ and we'll deal with it later .
		Alteration glidePreConsonant = new Alteration(parseCandRestricts("+vowel"), parseCandRestricts("+consonant"),
				capsulate(propCapsule), ipaParse.parseSegList("w"));
		GPL.addRule(glidePreConsonant);

		// not in Pope but must be so for the case of baua, graua etc : w is
		// retained between two as
		Alteration retainWBetweenAs = new Alteration(parseCandRestricts("+vowel,+open,-mid"),
				parseCandRestricts("+vowel,+open,-mid"), capsulate(propCapsule), ipaParse.parseSegList("w"));
		GPL.addRule(retainWBetweenAs);

		// See Pope 150, section 374 -- labiovelarization roughly coincident
		// with palatalization
		// I'd argue it targeted every consonant except r and the palatals (most
		// of which do not yet exist at this point).
		// However we deal with the velars elsewhere as labiovelars behave
		// differently
		List<Phone> labVelTargets = new ArrayList<Phone>(lParse.parseSegment("pbmtdnrlsz"));
		List<Phone> labVelDests = mapFeatShift(mapFeatShift(labVelTargets, "rounded", 1), "coArtic", 40);

		Alteration labVelarize = new Alteration(appendToAll(capsulate(labVelTargets), propCapsule.get(0), true),
				capsulate(labVelDests));
		GPL.addRule(labVelarize);
		// TODO these are simplified massively later -- see Pope 150 section 374

		// TODO deletion of s must be somewhere before this.

		// would-be intervocalic labiovelarized sonorants/nasals, remain two
		// consonant phonemes -- n/m and w;
		// Pope page 150, section 374 --- note that only n is cited as attested
		// in this book.
		List<Phone> sonors = lParse.parseSegment("nmlr");

		Alteration retainIntSonors = new Alteration(parseCandRestricts("+sonorant"), parseCandRestricts("+vowel"),
				capsulate(mapFeatShift(mapFeatShift(sonors, "coArtic", 40), "rounded", 1)),
				appendToAll(capsulate(sonors), propCapsule.get(0), true));
		GPL.addRule(retainIntSonors);

		// however in the case of l, the w is assimilated
		Alteration assimilateWtoL = new Alteration(parseCandRestricts("+lateral"), // i.e.
																					// l
				null, capsulate(propCapsule), lParse.parseSegList("l"));
		GPL.addRule(assimilateWtoL);

		// analogous to previous labVelarization shift and also Pope 91 sect 187
		// -- however I infer kw > kʷ not k, due to Old French spellings
		// quailler (<Late Latin coagulare)
		List<Phone> velars = new ArrayList<Phone>(ipaParse.parseSegment("k,g"));
		List<List<Phone>> velarTargets = appendToAll(capsulate(velars), ipaParse.getPhone("w"), true);
		Alteration roundVelars = new Alteration(null, parseCandRestricts("+vowel"), velarTargets,
				capsulate(mapFeatShift(velars, "rounded", 1)));
		GPL.addRule(roundVelars);

		// Also Pope 91 sect 187 : w absorbed by following labial ("avus non
		// aus" in Latin spelling corrections)
		// may have been retained for awhile as /w/ but it seems unnecessary to
		// model that.
		Alteration absorbW = new Alteration(parseCandRestricts("+vowel"), parseCandRestricts("+vowel,+back,+rounded"),
				capsulate(propCapsule), nonEntityList(1));
		GPL.addRule(absorbW);

		// Also Pope 91 sect 187 : w dropped before j. However this shift was
		// often reversed by analogy
		Alteration dropWBeforeJ = new Alteration(null, parseCandRestricts("+approximant,+palatal"), // i.e.
																									// j
				ipaParse.parseSegList("w"), nonEntityList(1));
		// GPL.addRule(dropWBeforeJ); //currently not included due to large
		// numbers of cases of reversion.

		// must come after Vulgar West Latin consonant loss, as seen by
		// developments like involare > embler
		// betacism: Latin /w/ begins developing into /β/ around the 1st century
		// CE
		Alteration betacism = new Alteration(capsulate(propCapsule), // in this
																		// way,
																		// with
																		// the
																		// prop,
																		// all
																		// the
																		// w-s
																		// not
																		// otherwise
																		// shifted
																		// become
																		// beta.
				ipaParse.parseSegList("β"));
		GPL.addRule(betacism);

		// all stressed vowels become long.
		// later those that are closed become re-shortened.
		List<Phone> stressedVowels = mapFeatShift(ipaParse.parseSegment("i,e,ɛ,ä,u,o,ɔ"), "stress", 1);
		Alteration relongateStressedVowels = new Alteration(capsulate(stressedVowels),
				capsulate(mapFeatShift(stressedVowels, "length", 2)));
		GPL.addRule(relongateStressedVowels);

		// palatalize velars before front vowels (which are at this point i, e,
		// ɛ) and j
		// although this did affect a few labiovelars (ex quinque > cinq) there
		// doesn't seem to be a pattern so I'm leaving them out for now
		// TODO add a pattern for these if there is one.
		Alteration velarPalatalization = new Alteration(null, // no prior
																// context
				parseCandRestricts("+front"), // should hit i,e,ɛ,j
				lParse.parseSegList("c,g"), // i.e. the plain velars, not the
											// labiovelars
				capsulate(mapFeatShift(lParse.parseSegment("cg"), "coArtic", 50)));
		GPL.addRule(velarPalatalization);

		// merge nonpalatal consonant and yod to make palatalized
		// non-palatalized consonant -- happens to all consonants that can occur
		// in syllable onsets, except labiovelars
		// should affect ALL nonpalatal consonants, except for k and g, as per
		// Pope sections 683-684
		List<Phone> onsetConsonants = lParse.parseSegment("tdslrtdnfpbm");
		onsetConsonants.addAll(ipaParse.parseSegment("ɣ,z,θ,ð,β")); // include
																	// also new
																	// non-Latin
																	// fricatives,
																	// all
																	// either
																	// newly
																	// developed
																	// or those
																	// that
																	// developed
																	// from
																	// Gaulish
																	// words

		Alteration mergeWithYod = new Alteration(appendToAll(capsulate(onsetConsonants), ipaParse.getPhone("j"), true),
				capsulate(mapFeatShift(onsetConsonants, "coArtic", 50)));
		GPL.addRule(mergeWithYod);

		// also absorb yod into consonants that are already palatalized
		// also excludes velars as per Pope 683,684
		Alteration absorbYod = new Alteration(parseCandRestricts("+copalatal,+consonant,-velar"), null,
				ipaParse.parseSegList("j"), nonEntityList(1));
		GPL.addRule(absorbYod);

		// TODO find a source for this that isn't Wikipedia.
		// bizarre shift that revelarizes the onset of kj when coming after a
		// vowel (or sonorant? TODO check this),
		// causing it to ultimately separate and make a two-phone segment, /kkʲ/
		Alteration reduplicatePalK = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+sonorant,-liquid"), // vowel, or
															// approximant as in
															// the case of j
				ipaParse.parseSegList("kʲ"), ipaParse.parseSegList("k,kʲ"));
		GPL.addRule(reduplicatePalK);

		// TODO note this following rule is not actually "canon" as per Frenchc
		// phonological "historiography", so to speak
		// however functionally it is necessary (and also seems logical) in
		// order to ensure that ultimately,
		// as per Operstein, pj > tsh, rather than pj > bj > dzh which would
		// occur under lenition unless pj is reduplicated.
		// and also to ensure mj > ndzh (also as per Operstein)
		Alteration reduplicatePalP = new Alteration(parseCandRestricts("-wordonset"), // internal
				parseCandRestricts("-wordcoda"), // internal
				ipaParse.parseSegList("pʲ;mʲ;bʲ;βʲ"), ipaParse.parseSegList("p,pʲ;m,bʲ;b,bʲ;β,bʲ"));
		GPL.addRule(reduplicatePalP);

		// as per Pope 185, section 488 : in Gallian Late Latin o > ɔ before
		// labials
		// however, not mentioned in pope, but my observation is that this
		// doesn't occur if a palatal comes after
		List<Phone> closeOs = ipaParse.parseSegment("o"), openOs = ipaParse.parseSegment("ɔ");

		Alteration openOPreLabial = new Alteration(null, parseCandRestricts("+bilabial;-copalatal"), // TODO
																										// check
																										// if
																										// /f/
																										// is
																										// included,
																										// in
																										// which
																										// case
																										// we
																										// should
																										// use
																										// +labial
				includeStressedVariants(capsulate(closeOs)), includeStressedVariants(capsulate(openOs)));
		GPL.addRule(openOPreLabial);

		// merge tj, kj > c, dj, gj > jh; soon to shift elsewhere.

		List<List<Phone>> palatalMergerTargets = ipaParse.parseSegList("kʲ;k,kʲ;gʲ;" + tDentCh + "ʲ;" + dDentCh + "ʲ");
		List<List<Phone>> palatalMergerDests = ipaParse.parseSegList("c;c,c;ɟ;c;ɟ");
		Alteration palatalMerger = new Alteration(palatalMergerTargets, palatalMergerDests);
		GPL.addRule(palatalMerger);

		// see, on the one hand, Pope page 126-127, sections 293-297
		// ... and on the other, Pope page 131, sections 311-312
		// the formation of enya and elya and their absorption of following
		// palatal consonants...
		// ... is different when the pair are followed by an unstressed vowel
		// then a consonant
		// in which case "enya" and "elya" are still formed, but the palatal
		// consonant is maintained (later becoming dental due to the following
		// consonant after the fall of the vowel)
		// Pope only lists some of these for the palatalized velars,
		// indeed when it is dentals it seems that for n the result is
		// different: plangere> plaiNDre but verecundea > vergoGNe
		Alteration Pope293a = new Alteration(null, parseCandRestricts("+stop,+palatal;+vowel,+unstressed;+consonant"),
				ipaParse.parseSegList("ŋ;l;lˠ;s"), ipaParse.parseSegList("ɲ;ʎ;ʎ;sʲ"));
		GPL.addRule(Pope293a);
		// as per Pope 293: unstressed vowels lost between palatal and r
		List<List<Phone>> unstressedVowels = ipaParse.parseSegList("ä;ɛ;e;i;u;o;ɔ");
		Alteration secondVowelLoss = new Alteration(parseCandRestricts("+palatal,+stop"), parseCandRestricts("+rhotic"),
				unstressedVowels, nonEntityList(unstressedVowels.size()));
		GPL.addRule(secondVowelLoss);
		// Pope 293, plus some of my own deduction : ɟ+r > dr always.
		// Whereas c+r > tr only if preceded by a coronal (tortre, but vaincre)
		Alteration Pope293c = new Alteration(ipaParse.parseSegList("ɟ,r"), ipaParse.parseSegList("" + dDentCh + ",r"));
		GPL.addRule(Pope293c);
		Alteration Pope293d = new Alteration(parseCandRestricts("+coronal"), null, ipaParse.parseSegList("c,r"),
				ipaParse.parseSegList("" + tDentCh + ",r"));
		GPL.addRule(Pope293d);
		Alteration Pope293e = new Alteration(ipaParse.parseSegList("c,r"), ipaParse.parseSegList("k,r"));
		GPL.addRule(Pope293e);
		// Pope also lists between palatal and t, in which case the palatal is
		// assimilated to t and deleted.
		// ... however this can be handled in the next section with the deletion
		// of middle stops, by adding to delete middle affricates

		// ds > s as per Pope page 147 section 367
		Alteration dsToS = new Alteration(lParse.parseSegList("ds"), ipaParse.parseSegList("s,s"));
		GPL.addRule(dsToS);

		return GPL;
	}

	/**
	 * implement shifts from Vulgar Latin to Gallo-Romance
	 * 
	 */

	// general overview though possibly obselete on some points : Pope pp 77-78
	// TODO make sure I haven't missed anything, using source above
	public LangLexicon toGalloRomance(LangLexicon preGR) // this stage
															// represents
															// roughly around
															// the 500s .
	{
		LangLexicon GR = new LangLexicon(preGR.getLexiconClone());

		// Pope page 131 sections 311 and 312
		// n + yod, n+ɟ, l+yod, ll+yod > enya, enya, elya, elya
		// note l + c or ɟ doesn't exist anymore, while n + c remains as it is,
		// ultimately becoming nts- (typically written nc or nc, in modern
		// French)
		Alteration Pope311a = new Alteration(ipaParse.parseSegList("l,l,j;" + nDentCh + "," + nDentCh + ",j"),
				ipaParse.parseSegList("l,j;" + nDentCh + ",j")); // deal with
																	// double l
																	// and
																	// double n
		GR.addRule(Pope311a);
		Alteration Pope311b = new Alteration(
				ipaParse.parseSegList("l,j;lː,j;n̪ʲ;" + nDentCh + ",j;" + nDentCh + "ː,j;" + nDentCh + ",ɟ"),
				ipaParse.parseSegList("ʎ;ʎ;ɲ;ɲ;ɲ;ɲ"));
		GR.addRule(Pope311b);

		// beta to v
		// -- effectively temporarily eliminates bilabial as a place of
		// articulation for fricatives
		// by moving it to hte interdental spot that is consistent with /f/
		// after this point, whenever bilabial fricatives arise from lenition,
		// they tend to either disappear or become labiodental (v or, more
		// rarely, f)
		// note this must come before the First Celtic Lenition, because former
		// w and lenited b
		/// ... do not have hte exact same results at this stage, so they must
		// at first be differentiated
		// Source: comparing Pope p139 section 343 (for beta) and Pope p91 sect
		// 187 (for w)
		Alteration veta = new Alteration(null, parseCandRestricts("-consonant"), // doesn't
																					// really
																					// seem
																					// to
																					// happen
																					// before
																					// consonants.
				ipaParse.parseSegList("β"), ipaParse.parseSegList("v"));
		GR.addRule(veta);

		// action on labialized consonants, from Pope 150 section 174
		// Pope p150, section 374 : labiovelarized consonants preceded by
		// another consonant are simplified to lose their coarticulation
		List<Phone> labVelTargets = new ArrayList<Phone>(lParse.parseSegment("pbmtdnrlsz"));
		Alteration deLabializeClusters = new Alteration(parseCandRestricts("+consonant"), null,
				capsulate(mapFeatShift(mapFeatShift(labVelTargets, "rounded", 1), "coArtic", 40)),
				capsulate(labVelTargets));
		GR.addRule(deLabializeClusters);

		// furthermore, sibilants and sibilant affricates lose labiovelar
		// articulation everywhere
		List<Phone> latSibs = new ArrayList<Phone>(lParse.parseSegment("sz"));
		Alteration simplifySibilants = new Alteration(
				capsulate(mapFeatShift(mapFeatShift(lParse.parseSegment("sz"), "rounded", 1), "coArtic", 40)),
				capsulate(latSibs));
		GR.addRule(simplifySibilants);

		/**
		 * palatal stops change in either place and/or manner : the voiceless
		 * one becomes an alveolar sibilant affricate, tsj (ttsj
		 * intervocalically) the voiced one becomes a simple yod, a palatal
		 * approximant
		 */
		Alteration palStopLoss = new Alteration(ipaParse.parseSegList("c,c;c;ɟ"),
				ipaParse.parseSegList("t,t͡sʲ;t͡sʲ;j"));
		GR.addRule(palStopLoss);

		// j + unstressed vowel + t,d -> j + PALATALIZED t,d -- early apocope?
		// I believe the palatalization is dealt with elsewhere
		// acts before lenition: (not sure if Pope is saying this explicitly,
		// it's a bit ambiguous, but necessary for medietatem > moitie not
		// moidie
		// see Pope page 132 section 316
		List<List<Phone>> unstressedVowels = ipaParse.parseSegList("i;e;ɛ;ä;u;o;ɔ"); // au
																						// never
																						// occurs
																						// unstressed.
		Alteration earlyApocopePostPalatal = new Alteration(parseCandRestricts("+approximant,+palatal"),
				parseCandRestricts("+stop,+dental,-nasal"), unstressedVowels, nonEntityList(unstressedVowels.size()));
		GR.addRule(earlyApocopePostPalatal);

		// prior to action of first Celtic lenition, intervocalic labiovelarized
		// stops open --- "the zeroeth Celtic lenition", so to speak
		// citation -- Pope page 134-135, sections 327-330
		List<Phone> roundStops = mapFeatShift(mapFeatShift(lParse.parseSegment("bpdtgc"), "rounded", 1), "coArtic", 40);

		Alteration openIntervocalicLabVelStops = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+vowel"), capsulate(roundStops), capsulate(mapFeatShift(roundStops, "manner", 25)));
		GR.addRule(openIntervocalicLabVelStops);

		// Celtic substrate effect: First lenition -- source is Arteaga
		// It is also Arteaga who notes that in original central French,
		// lenition preceded vowel apocope,
		// ... but a number of words were dialectally loaned from dialects where
		// the pattern was the opposite
		// these were the more Frankish influenced dialects of Lorrain, Normandy
		// and Champagne.

		// NOTE : tts (< c intervocalic) is geminate and not lenited. However it
		// IS hit in the second lenition.

		// intervocalic voiced consonants become fricatives, as part of first
		// lenition
		List<Phone> lenition1Targets = ipaParse.parseSegment("ɡ,ɡʷ,d̪,b");
		List<Phone> lenition1Dests = mapFeatShift(lenition1Targets, "manner", 25);
		lenition1Targets.addAll(ipaParse.parseSegment("x,xʷ,s,θ,θʷ,f,fʷ,ɸʷ")); // add
																				// voiceless
																				// fricatives
		lenition1Dests.addAll(ipaParse.parseSegment("ɣ,ɣʷ,z,ð,ðʷ,v,vʷ,βʷ")); // add
																				// their
																				// voiced
																				// counterparts
		lenition1Targets.addAll(ipaParse.parseSegment("k,kʷ,t̪,p"));
		lenition1Dests.addAll(ipaParse.parseSegment("ɡ,ɡʷ,d̪,b"));

		Alteration firstLenition = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+sonorant,-lateral"), // i.e. vowels and
															// rhotics
				capsulate(lenition1Targets), capsulate(lenition1Dests)); // make
																			// them
																			// voiced
																			// fricatives
		GR.addRule(firstLenition);

		// pl > bl also under lenition... and also gl and kl which later become
		// lambda
		// pl > bl > wl does however happen in the West of teh French speaking
		// area
		Alteration firstLenitionPlus = new Alteration(parseCandRestricts("+vowel"), parseCandRestricts("+lateral"),
				ipaParse.parseSegList("p;g;k"), ipaParse.parseSegList("b;ɣ;g"));
		GR.addRule(firstLenitionPlus);

		// Can't find a citation for this, but it seems that around roughly the
		// same time as the first lenition,
		// ... intervocalic n between two unstressed vowels passes to the tap ɾ
		// and ultimately to hte trill r. (Lingonos>Langres, Londines>Londres,
		// etc...)
		// This occurs mainly in Gaulish words, due to their stress system
		// having a lot more cases of this occurring
		// note we do know this CAN effect r-s after countertonic vowels :
		// a'nima`lia > armaille
		// Pope has a different interpretation of this phenomenon -- see Pope
		// page 230, section 643
		// ... although her analysis only covers learned loanwords
		Alteration isFrenchTosk = new Alteration(parseCandRestricts("+vowel,-stressed"),
				parseCandRestricts("+vowel,-stressed"), ipaParse.parseSegList("n"), ipaParse.parseSegList("ɾ"));
		GR.addRule(isFrenchTosk);

		// Note Pope 145 section 372 -- in Central French, b before l is not
		// affected by lenition.
		// However in northern dialects (Picard, Norman, Walloon), it was, ultimately creating doublets like taule for table and pueule or peuple
		// Pope page p139 section 343 : beta deleted when coming after u or o.
		Alteration absorbBeta1 = new Alteration(parseCandRestricts("+vowel,+velar,+close,+rounded"), // i.e. u or o
				null, ipaParse.parseSegList("β"), nonEntityList(1));
		GR.addRule(absorbBeta1);

		// also Pope 139 section 343 : beta deleted when preceding tonic u --
		// not in West French however, where it passes to w
		Alteration absorbBeta2 = new Alteration(null,
				parseCandRestricts("+vowel,+velar,+rounded,+stressed,+close,-midclose"), // i.e.
																							// u
				ipaParse.parseSegList("β"), nonEntityList(1));
		GR.addRule(absorbBeta2);

		// note dialectal (northern, western): bh > bhw > w when followed by a
		// close labial? Unsure on this one but see dialectal tabula > taule,
		// to^le

		// Pope page 139, section 342 : gamma deleted after a vowel and before
		// specifically tonic (stressed) u or o
		Alteration absorbGamma1 = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+vowel,+stressed,+close,+velar,+rounded"), ipaParse.parseSegList("ɣ"),
				nonEntityList(1));
		GR.addRule(absorbGamma1);

		// Pope page 139, section 342 : gamma deleted between u and a, ex ruuga
		// > rue etc.
		Alteration absorbGamma2 = new Alteration(parseCandRestricts("+vowel,+rounded,+close,-midclose,+velar"), // i.e.
																												// u
				parseCandRestricts("+vowel,+open,-midopen"), // a or ash
				ipaParse.parseSegList("ɣ"), nonEntityList(1));
		GR.addRule(absorbGamma2);

		// ɣʷ, the round velar voiced fricative, is also dealt wiht at this
		// time, but it doesn't disappear as simply as the others.

		// when it's preceded by a secondarily stressed vowel, becomes ww, and
		// first part combines to round the previous vowel,
		// Source -- see Pope p135 section 329
		Alteration waForGhwa1 = new Alteration(parseCandRestricts("+secondarystressed"), null,
				ipaParse.parseSegList("ɣʷ"), ipaParse.parseSegList("w,w"));
		GR.addRule(waForGhwa1);

		// Pope p134 sect 327 : when preceded by tonic palatal vowel (including
		// ash), becomes jw > jv -- this happens after a-fronting
		Alteration waForGhwa2 = new Alteration(parseCandRestricts("+stressed,-velar"), // i,e,eh
																						// or
																						// a
																						// .
																						// Note
																						// the
																						// fronting
																						// of
																						// u
																						// must
																						// start
																						// after
																						// this,
																						// otherwise
																						// we
																						// have
																						// a
																						// problem
																						// as
																						// it
																						// ughwa
																						// segments
																						// are
																						// unaffected.
																						// Though
																						// possibly
																						// also
																						// unattested?
				null, ipaParse.parseSegList("ɣʷ"), ipaParse.parseSegList("j,w"));
		GR.addRule(waForGhwa2);

		// TODO Pope p134 sect 327 everywhere else, becomes w -- as do all other
		// rounded voiced fricatives
		// all the other labiovelarized voiced fricatives are also shifted to w,
		// after which point most will soon disappear
		Alteration roundFricsToW = new Alteration(ipaParse.parseSegList("βʷ;vʷ;ðʷ;ɣʷ"),
				ipaParse.parseSegList("w;w;w;w"));
		GR.addRule(roundFricsToW);

		// CASE OF LEUCA : lɛ-uCA > le-uca > ljoga > ljoe > lieue

		// TODO this was actually much more complicated, see Pope 343-345

		// x and gamma modifieed variously to j
		// x > j everywhere . This results of course in xs,xt > j, infamously,
		// but also other things, such as changing the final results of velar
		// lenition
		// for sourcing, see Pope pp122-128, 134 ( page 134 explicitly
		// references the fate of Gallo-Roman /xs/, and notes that later /ks/
		// from Latin proper become /ss/ not /xs/ -- perhaps due to the
		// termination of Gaulish influence?)
		Alteration palX = new Alteration(ipaParse.parseSegList("x"), ipaParse.parseSegList("j"));
		GR.addRule(palX);

		// gamma palatalizes if it is both after a~ash,e,e or i and a (probs
		// would palatalize also before i and e, but it will not occur in these
		// positions as it palatalized earlier)
		// probably passed through a stage of a palatal fricative, because at
		// this stage the language has palatal fricatives but no velar
		// fricatives.
		Alteration palGamma1 = new Alteration(parseCandRestricts("+vowel,-velar,-rounded"),
				parseCandRestricts("+vowel,-velar,-rounded"), ipaParse.parseSegList("ɣ"), ipaParse.parseSegList("j"));
		GR.addRule(palGamma1);

		// unstressed vowels are shortened
		List<Phone> longVowels = new ArrayList<Phone>(ipaParse.parseSegment("äː,ɛː,eː,iː,uː,oː,ɔː")); // these
																										// are,
																										// by
																										// default,
																										// unstressed.
		List<Phone> shortVowels = new ArrayList<Phone>(ipaParse.parseSegment("ä,ɛ,e,i,u,o,ɔ"));
		Alteration shortenUnstressedVowels = new Alteration(capsulate(longVowels), capsulate(shortVowels));
		GR.addRule(shortenUnstressedVowels);

		// Celtic substrate effect: First diphthongization -- see Posner,
		// Romance Languages, 159
		// affects all long (stressed) low vowels which at this point happen to
		// all also be stressed (because all stressed vowels recently became
		// long -- see Grandgent & Moll 1991)
		// later closed vowels among these become shortened and (in most but not
		// all cases) monophthongs
		// UPDATE æ ~ a removed : Occam's razor, going with what the majority of
		// readings seem to say, that a was not diphthongized to /ea/ at this
		// point
		// UPDATE for similar reasons, closed vowels are also removed from teh
		// analysis here, though at the moment per programming only open vowels
		// should be long at this point.
		// TODO email either Professor Honeybone or Professor Iosad about this
		// as they support the a:>ea theory ?
		List<Phone> fDTargets = mapFeatShift(ipaParse.parseSegment("ɛː,ɔː"), "stress", 1);
		List<Phone> fDDests = new ArrayList<Phone>();
		// int[][] ehStruct = new int[][] {{50,60,0}};
		int[][] iStruct = new int[][] { { 50, 40, 0 } };
		int[][] uStruct = new int[][] { { 40, 40, 1 } };
		// fDDests.add(new Phthong (new Vowel(50,70), ehStruct, null));
		fDDests.add(new Phthong(new Vowel(50, 60), iStruct, null));
		fDDests.add(new Phthong(new Vowel(40, 60, true), uStruct, null));
		for (Phone fDDest : fDDests)
			fDDest.setStress(1); // all outcomes retain their stress.
		// fD1 -- to cover open syllables where it's followed by a consonant
		// then another vowel
		Alteration firstDiphthongization1 = new Alteration(null, parseCandRestricts("+consonant;+sonorant,-lateral"),
				capsulate(fDTargets), capsulate(fDDests));
		GR.addRule(firstDiphthongization1);
		// fD2 -- to cover open syllables wehre the vowel is followed by eihter
		// a word coda or another vowel
		Alteration firstDiphthongization2 = new Alteration(null, parseCandRestricts("+vowel,+wordcoda"),
				capsulate(fDTargets), capsulate(fDDests));
		GR.addRule(firstDiphthongization2);
		// fD3 -- diphthongization also occurred before a palatal consonant then
		// another consonant or wordcoda
		Alteration firstDiphthongization3 = new Alteration(null,
				parseCandRestricts("+consonant,+palatal;+consonant,+wordcoda"), capsulate(fDTargets),
				capsulate(fDDests));
		GR.addRule(firstDiphthongization3);

		// beginning of second palatalization : velar stops become coPalatal if
		// preceding a vowel with some degree of palatal onset
		// i.e. including i,e,eh,ash,a, au... technically a and au have a
		// fronted position at this point and this is the trigger for the shift
		// ... for info see Buckley. However it makes more ''programming'' sense
		// to put it first because also affected are a,au + w > ou sets
		// note also -- the palatalization of velar fricatives x and gamma
		// occurs near this time too
		// ... and is probably also related. But once again for programming
		// reasons we program them separately
		// note furthermore-- this must occur before the beginning of
		// u-fronting, as otherwise we'd see these effects before u as well.
		Alteration secondPalatalization1 = new Alteration(null, parseCandRestricts("+vowel,-back"),
				ipaParse.parseSegList("g;k"), ipaParse.parseSegList("gʲ;kʲ"));
		GR.addRule(secondPalatalization1);
		// see also: Pope's arguments that lenition of g,k preceded vowel
		// apocope...
		// ... but that that of d,t did not -- Pope 140-141 sections 348-350

		// diphthongization of othr vowels + u~w also occurs. ??

		// Germanic superstrate effect : advent of a strong expiratory stress
		// system
		// ... causes, among other things to come, the shortening of long closed
		// vowels.
		// They do, however, retain their stress.
		// NOTE: this is programmed in a way that might suggest that it bled
		// diphthongization
		// ultimately, in terms of results that is very well what it
		// functionally (and thus algorithmically) did
		// However, the reality at the time of its effect was probably much more
		// complicated, as its effects were likely seen mostly with the Frankish
		// ruling class first.
		// it is merely programmed this way as it is convenient
		// we also don't have to deal with secondarily stressed variants here as
		// they were never long.
		List<Phone> longStressedVowels = mapFeatShift(longVowels, "stress", 1);
		List<Phone> shortStressedVowels = mapFeatShift(shortVowels, "stress", 1);
		Alteration shortenClosedVowels1 = new Alteration(null, parseCandRestricts("+consonant;+consonant,-rhotic"), // rhotics
																													// count
																													// as
																													// sonarants
																													// here
																													// --
																													// laterals
																													// dont
																													// though
																													// (see
																													// table,
																													// not
																													// teble)
				capsulate(longStressedVowels), capsulate(shortStressedVowels));
		GR.addRule(shortenClosedVowels1);
		Alteration shortenClosedVowels2 = new Alteration(null, parseCandRestricts("+consonant;+wordcoda"),
				capsulate(longStressedVowels), capsulate(shortStressedVowels));
		GR.addRule(shortenClosedVowels2);

		// [a] gets a more fronted articulation (may have been also raised
		// later-- see Buckley, Pope)
		List<Phone> oldACapsule = new ArrayList<Phone>(lParse.parseSegment("aā"));
		oldACapsule.addAll(mapFeatShift(oldACapsule, "nasal", 1));
		List<Phone> newACapsule = new ArrayList<Phone>(ipaParse.parseSegment("a,aː"));
		newACapsule.addAll(mapFeatShift(newACapsule, "nasal", 1));

		Alteration aFronting = new Alteration(includeStressedVariants(capsulate(oldACapsule)),
				includeStressedVariants(capsulate(newACapsule)));
		GR.addRule(aFronting);

		// the AU diphthong's first element also gets similarly fronted (may
		// have been also raised later -- see Buckley, Pope)
		Phthong newAU = new Phthong(lParse.getVowel("au"));
		newAU.setPlace(50);
		List<Phone> newAUCapsule = new ArrayList<Phone>();
		newAUCapsule.add(newAU);
		newAUCapsule.addAll(mapFeatShift(newAUCapsule, "stress", 1));
		List<Phone> oldAUCapsule = lParse.parseSegment("au");
		oldAUCapsule.addAll(mapFeatShift(oldAUCapsule, "stress", 1));
		Alteration auFronting = new Alteration(includeStressedVariants(lParse.parseSegList("au")),
				includeStressedVariants(ipaParse.parseSegList("au̯")));
		GR.addRule(auFronting);

		// as per Buckley 2003 : a > æ in stressed, open syllables. æ eventually
		// gets raised furhter to e but not until the 12th century
		// this does NOT affect countertonics (ex maritu > mari, amicu > ami )
		// at the moment only open stressed vowels are long-- which are exactly
		// the targets here
		// so functionally it makes sense to simply directly target them rather
		// than specify context
		// TODO add in specificaiton for the open syllable stressed context if
		// the ordering of alterations is ever changed
		Alteration aeGenesis = new Alteration(capsulate(mapFeatShift(ipaParse.parseSegment("aː"), "stress", 1)),
				capsulate(mapFeatShift(ipaParse.parseSegment("æː"), "stress", 1)));
		GR.addRule(aeGenesis);

		/*
		 * REDACTED old rule where we originally had a > ea > ash . //note on
		 * this one: everyone agrees that ɛː,ɔː were targeted. //However the
		 * inclusion of a: is disputed. Some argue that it shifted later, //, at
		 * the same time as /o:/ and /e:/, to /ae/. Still others argue it was
		 * instead... //... immediatedly fronted to ash. See Buckley's paper for
		 * more details on the controversy // I take a midway approach -- an
		 * earlier diphthongization to /ɛa/, followed quickly // by
		 * monophthongization to ash. // Additionally, see above for the
		 * discussion of how the shortening of closed vowels // ... relates to
		 * this shift. // TODO note Spanish and Romansch provide evidence that
		 * not just open syllables were affected, as ue and ie appear elsewhere
		 * too // TODO change these last two shifts, as well as possibly the
		 * time of a-fronting if more evidence arises.
		 * 
		 * //ea to ash: List<Phone> eaMTargets = new ArrayList<Phone>();
		 * eaMTargets.add(new Phthong (fDDests.get(0))); //TODO change this if
		 * we ever change fDDests List<Phone> eaMDests = new ArrayList<Phone>();
		 * eaMDests.add(new
		 * Vowel(50,65,0,false,false,false,false,true,false,true, 2));
		 * 
		 * Alteration eaMonophthongization = new Alteration (
		 * capsulate(eaMTargets), capsulate(eaMDests));
		 */

		// jn, nj > palatal nasal stop; jl, gl to mouille intervocalically
		List<List<Phone>> nLPTargets = ipaParse.parseSegList("jn;ŋ,j;n,j;j,l;g,l".replaceAll("n", nDentCh)); // TODO
																												// find
																												// more
																												// efficient
																												// way
																												// to
																												// do
																												// this?
		List<List<Phone>> nLPDests = ipaParse.parseSegList("ɲ;ɲ;ɲ;ʎ;ʎ");
		Alteration nasalLateralPalatalization = new Alteration(parseCandRestricts("+sonorant"), // TODO
																								// check
																								// this
																								// --
																								// I
																								// know
																								// for
																								// sure
																								// initials
																								// are
																								// nto
																								// targeted
				parseCandRestricts("+vowel"), // TODO check this
				nLPTargets, nLPDests);
		GR.addRule(nasalLateralPalatalization);

		// TODO work hereNOW
		// countertonic and intertonic(/atonic?) vowels followed by yod are
		// influenced by it
		// alluded to in Pope section 518, various other places
		// explains the case of moitie, and others
		// Alteration yodInfluenceUnstressed = new Alteration( null,
		// parseCandRestricts("+approximant,+palatal"),

		// first unstressed vowel apocope. See note from Arteaga above on the
		// granica > grange, granche case
		// ... ^ concerning the temporal and dialectal relations between
		// lenition and apocope
		// note that before this rule, all unstressed long vowels are shortened,
		// so we don't have to deal with them here.
		// we do however have to deal with the /au/ diphthong .
		// for convenience I am currently working under the assumption that the
		// rare case of vowels next to vowels are unaffected
		// TODO fix this (somehow) if that is not true
		// TODO also currently using very inefficient method of determining
		// which is not the first vowel based on the maximum size of consonant
		// clusters at this time.
		// TODO fix this.

		// TODO we are assuming the maximum size of a word-final consonant
		// cluster is 1,
		// ...so anything coming after a posterior consonant besides a word coda
		// is proves that there is a vowel after the target
		// TODO fix if this is found to be wrong.

		int numShortVowels = shortVowels.size();

		Alteration firstAtonicVowelApocope1 = new Alteration(parseCandRestricts("+vowel;+consonant"), // TODO
																										// complete
																										// the
																										// prior
																										// restrictions...
				parseCandRestricts("+consonant;-wordcoda"), capsulate(shortVowels), // all
																					// short,
																					// unstressed
																					// vowels.
				nonEntityList(numShortVowels));
		GR.addRule(firstAtonicVowelApocope1);

		// REDACTEDTODO we assume at this stage of French, maximum size of an
		// initial consonant
		// cluster at is 2 so anything before a 2 consonant cluster in prior
		// context
		// besides a word onset indicates that the target is an internal vowel
		Alteration firstAtonicVowelApocope2 = new Alteration(null, // TODO note
																	// we don't
																	// have to
																	// specify a
																	// prior
																	// context
																	// solely
																	// because
																	// initial
																	// vowels
																	// are all
																	// either
																	// stressed
																	// or
																	// secondarily
																	// stressed
																	// -- and
																	// therefore
																	// not in
																	// the
																	// target
																	// list .
				parseCandRestricts("+consonant;-wordcoda"), capsulate(shortVowels), nonEntityList(numShortVowels));
		GR.addRule(firstAtonicVowelApocope2);

		// no sourcing for this in Pope however it seems necessary: otherwise
		// spata>espata becomes spada or even xpada... not remaining espada
		Alteration reinsertSupportingE = new Alteration(parseCandRestricts("+wordOnset"),
				parseCandRestricts("+sibilant;+consonant,-approximant"), nonEntityList(1), ipaParse.parseSegList("e'"));
		GR.addRule(reinsertSupportingE);

		// j + d or t -> palatal d or t -> j + the palatal

		// self inferred based on data; see also : Pope page 230
		// r > l when after unstressed a and before another consonant (see
		// armaille > aumaille, Arvernia > Auvergne etc)
		Alteration reverseJapanese = new Alteration(parseCandRestricts("+vowel,+open,-nearopen,-stressed"), // i.e.
																											// some
																											// A-variety
																											// that
																											// isn't
																											// stressed
				parseCandRestricts("+consonant"), ipaParse.parseSegList("ɾ"), ipaParse.parseSegList("l"));
		GR.addRule(reverseJapanese);

		// all remaining taps go to trills
		Alteration tapsToTrills = new Alteration(ipaParse.parseSegList("ɾ"), ipaParse.parseSegList("r"));
		GR.addRule(tapsToTrills);

		// as per Pope p145, section 359 :
		// g+m > w+m
		Alteration gmToWm = new Alteration(ipaParse.parseSegList("g,m"), ipaParse.parseSegList("w,m"));
		GR.addRule(gmToWm);

		// TODO work here
		// TODO do it for nawikella -- perhaps it is cuz n does not like to have
		// a w after it?

		// same source as above, Pope p145 section 359 : ex fabrica > faurge
		Alteration toFaurge = new Alteration(null, parseCandRestricts("+consonant,-stop;+consonant"),
				ipaParse.parseSegList("β"), ipaParse.parseSegList("w"));
		GR.addRule(toFaurge);

		// Pope pages 183-184 sections 480-483
		// a,a:,au to ɔ before ghw or w or u --- move this all to Gallo Romance
		// section
		// note this shift has to come after lenition and then shift of ghw > w
		// also must come after first diphthongization, otherwise it would have
		// been diphthongized
		// NOTE we do not have to worry about hte case of PAVCVM > peu as this
		// is in fact a case of a dialect loan
		// * The Old French form followed standard shifts : pou, not peu.
		Alteration bauaBoue = new Alteration(null, parseCandRestricts("+velar,+voiced,-stop,+rounded"),
				includeStressedVariants(ipaParse.parseSegList("a;aː;æ;æː;" + newAU.print())),
				includeStressedVariants(ipaParse.parseSegList("ɔ;ɔː;ɔ;ɔː;ɔː")));
		GR.addRule(bauaBoue);

		// all the other betas go to "veta", v
		Alteration veta2 = new Alteration(ipaParse.parseSegList("β"), ipaParse.parseSegList("v"));
		GR.addRule(veta2);

		// assimilate voiced stops and fricatives in clusters to voiceless if
		// next to a voiceless consonant
		// note : although listed, gamma and edh really shouldn't be hit by
		// this...
		List<Phone> voicedObstruents = new ArrayList<Phone>(ipaParse.parseSegment("b,v," + dDentCh + ",ð,d͡z,g,ɣ"));
		voicedObstruents.addAll(mapFeatShift(voicedObstruents, "coArtic", 50)); // palatal
																				// forms
																				// included;
																				// we
																				// assume
																				// labiodentalized
																				// forms
																				// are
																				// eliminated
																				// by
																				// this
																				// point.
		List<Phone> devoicedObstruents = mapFeatShift(voicedObstruents, "voiced", 0);
		Alteration voicingAssimilation1 = new Alteration(null, parseCandRestricts("+consonant,-voiced"),
				capsulate(voicedObstruents), capsulate(devoicedObstruents));
		GR.addRule(voicingAssimilation1);

		Alteration voicingAssimilation2 = new Alteration(parseCandRestricts("+consonant,-voiced"), null,
				capsulate(voicedObstruents), capsulate(devoicedObstruents));
		GR.addRule(voicingAssimilation2);

		// u everywhere, whether stressed or not, and whether long or not,
		// begins to become fronted...
		List<Phone> uF1Targets = new ArrayList<Phone>(ipaParse.parseSegment("u,uː"));
		List<Phone> uF1Dests = new ArrayList<Phone>(ipaParse.parseSegment("ʉ,ʉː"));
		Alteration uFronting1 = new Alteration(includeStressedVariants(capsulate(uF1Targets)),
				includeStressedVariants(capsulate(uF1Dests)));
		GR.addRule(uFronting1);

		// consonant cluster simplification as per Pope pp145-150 sections
		// 362-373

		// first, certain clusters are specifically modified:
		// s,t,s > t,s ; t,s> ts; d,z>dz

		Alteration simplifyClusters1 = new Alteration(
				ipaParse.parseSegList("s," + tDentCh + ",s;" + tDentCh + ",s;" + dDentCh + ",z;k,s," + tDentCh),
				ipaParse.parseSegList("t͡s;t͡s;d͡z;s,t"));
		GR.addRule(simplifyClusters1);

		// except for clusters ending in r or l (liquids), plosives surrounded
		// by two consonants are deleted
		// v also targeted -- see Pope p146
		// palatals affected too: culcita > coilte, coute
		Alteration deleteMiddleStops = new Alteration(parseCandRestricts("+consonant"),
				parseCandRestricts("+consonant,-liquid"), // all consonants
															// except for l and
															// r
				lParse.parseSegList("p,b,t,d,c,g,v"), nonEntityList(6));
		GR.addRule(deleteMiddleStops);

		/// VsklV > VslV , VrglV > rl ,
		Alteration deleteMoreMiddleStops = new Alteration(ipaParse.parseSegList("s,k,l;r,g,l"),
				ipaParse.parseSegList("s,l;r,l"));
		GR.addRule(deleteMoreMiddleStops);

		// denasalization ml>mbl, mr>mbr, nr >ndr. -- Pope p148 section 369

		Alteration clusterDenasalization = new Alteration(lParse.parseSegList("ml,mr,ndr"),
				lParse.parseSegList("mbr,mbl,ndr"));
		GR.addRule(clusterDenasalization);

		// Sources : Pope pages 149-150 section 373
		// Assimilation of labial stops to following dentals (and alveolar s )
		Alteration labDentAssimilation = new Alteration(null, // this one for
																// assimilating
																// to the stops
				parseCandRestricts("+dental,+stop"), lParse.parseSegList("pt,ps,bt,bd,bs"),
				lParse.parseSegList("tt,ss,tt,dd,ss"));
		GR.addRule(labDentAssimilation);
		// reduction of the geminate if another non-sonorant consonant cluster
		// follows -- not cited anywhere, just clearly obvious (obscura >
		// oscure, not *osscure, etc...)
		Alteration noNewTriples = new Alteration(null, parseCandRestricts("+consonant,-sonorant"),
				lParse.parseSegList("tt,dd,ss"), lParse.parseSegList("t,d,s"));
		GR.addRule(noNewTriples);

		// Assimilation of velar + labial to velar + labiovelar
		// No source, but it seems obvious to me given some rare but well-known
		// cases like Iakobos > Jacques
		Alteration velLabAssimilation = new Alteration(ipaParse.parseSegList("k,p;g,b"), // note
																							// kb
																							// and
																							// gp
																							// should
																							// have
																							// gone
																							// to
																							// kp
																							// because
																							// of
																							// earlier
																							// voicing
																							// assimilation
				ipaParse.parseSegList("k,kʷ;ɡ,ɡʷ"));
		GR.addRule(velLabAssimilation);

		// nasal assimilation -- Pope p148 section 371
		Alteration nasalAssimilation1 = new Alteration(lParse.parseSegList("mn,nm,mb"),
				ipaParse.parseSegList("m,m;m,m;m,m"));
		GR.addRule(nasalAssimilation1);

		Alteration nasalAssimilation2 = new Alteration(null, parseCandRestricts("+bilabial"), lParse.parseSegList("n"),
				ipaParse.parseSegList("m"));
		GR.addRule(nasalAssimilation2);

		Alteration nasalAssimilation3 = new Alteration(null, parseCandRestricts("+dental"), // i.e.
																							// a
																							// dental
																							// stop
																							// or
																							// fricative
																							// like
																							// edh
																							// or
																							// theta
				ipaParse.parseSegList("m"), lParse.parseSegList("n"));
		GR.addRule(nasalAssimilation3);

		// bbj > bgj, vbj > vgj, ppj > pkj, mbj > mgj -- but no clear evidence
		// of the assimilation of the previous element, although all are effaced
		// in the course of Old French
		Alteration labioPalatalBreaking = new Alteration(ipaParse.parseSegList("pʲ;bʲ"),
				ipaParse.parseSegList("kʲ;gʲ"));
		GR.addRule(labioPalatalBreaking);

		return GR;
	}

	// TODO make sure auits as in nawikella > nacele bleeds the shift of
	// awikellum > oiseau

	// general overview although possibly obselete on some points -- Pope
	// pp78-79
	// TODO make sure I haven't missed anything judging by source above.
	public LangLexicon toEarlyOldFrench(LangLexicon preOF) // aka "Later
															// Gallo-Roman",
															// represents
															// roughly around
															// 700-800
	{
		LangLexicon EOF = new LangLexicon(preOF.getLexiconClone());

		// TODO place
		// Source : Pope 184 section 484
		// influence of double ww or following tonic u on counter tonic e,
		// turning it to o
		// logically must come before the merging of adjacent vowels into new
		// diphthongs
		Alteration roundEPreW = new Alteration( // this one deals with the
												// effects of w~ww on previosu
												// countertonic e
				null, parseCandRestricts("+rounded,+velar,+approximant"), // i.e.
																			// w
				ipaParse.parseSegList("e'"), ipaParse.parseSegList("o'"));
		EOF.addRule(roundEPreW);

		Alteration roundEPreU = new Alteration(null, parseCandRestricts("+rounded,-palatal,+close,-midclose"), // i.e.
																												// u
																												// or
																												// cross
																												// u
				ipaParse.parseSegList("e'"), ipaParse.parseSegList("o'"));
		EOF.addRule(roundEPreU);

		// subsequent rounding effects of e do not seem to be Neogrammarian in
		// nature, and are thus excluded

		// TODO place this somewhere if we get more info
		// Delabialize all rounded consonants except for the velar stops. At
		// this same time, w disappears?
		// TODO we gotta explain the doublet of soventre and soentre somehow...
		// however it doesn't disappear in the jw cluser : aqua > ajwa > aiwa >
		// ɛwə > ɛau(ə) > o (water)
		Alteration delabializeNonVelarLabs = new Alteration(ipaParse.parseSegList("pʷ;bʷ;vʷ;θʷ;ðʷ;zʷ;t͡sʷ;d͡zʷ"), // all
																													// the
																													// labialized
																													// consonants
																													// that
																													// could
																													// theoretically
																													// exist
																													// at
																													// this
																													// time
				ipaParse.parseSegList("p;b;v;θ;ð;z;t͡s;d͡z"));
		EOF.addRule(delabializeNonVelarLabs);

		// TODO influence of retained j on prior vowels --
		// absorbed into new diphthongs, see note on that and 'absorb' it there.

		// gj, kj become simple palatals... and then ultimately affricates
		Alteration palatalizeVelars = new Alteration(ipaParse.parseSegList("kʲ;gʲ"), ipaParse.parseSegList("c;ɟ"));
		EOF.addRule(palatalizeVelars);
		Alteration palatalizeVelars2 = new Alteration( // in order to get those
														// velars that are
														// before palatals
				null, parseCandRestricts("+stop,+palatal"), ipaParse.parseSegList("k;g"), ipaParse.parseSegList("c;ɟ"));
		EOF.addRule(palatalizeVelars2);
		// reduplicate new palatals if they are intervocalic
		Alteration palatalizeVelars3 = new Alteration( // we also need to mkae
														// any that are single
														// and between vowels
														// geminate-- and
														// therefore won't eject
														// j before when
														// depalatalizing
				parseCandRestricts("+sonorant"), parseCandRestricts("+vowel"), ipaParse.parseSegList("c;ɟ"),
				ipaParse.parseSegList("c,c;ɟ,ɟ"));
		EOF.addRule(palatalizeVelars3);

		// ks > ss (including before palatalized s)
		Alteration ksToSs = new Alteration(null, parseCandRestricts("+sibilant"), // i.e.
																					// could
																					// be
																					// a
																					// palatal,
																					// or
																					// labial,
																					// s
																					// for
																					// all
																					// we
																					// care.
				ipaParse.parseSegList("k"), ipaParse.parseSegList("s"));
		EOF.addRule(ksToSs);

		// "third palatalization" of consonants following j --
		// According to Pope this only happened with dentals and r -- see Pope
		// page 132 section 316
		List<Phone> pDPPTargets = lParse.parseSegment("tdnr");
		Alteration palDentalsPostPal1 = new Alteration(parseCandRestricts("+consonant,+palatal"), null,
				capsulate(pDPPTargets), capsulate(mapFeatShift(pDPPTargets, "coArtic", 50)));
		EOF.addRule(palDentalsPostPal1);
		Alteration palDentalsPostPal2 = new Alteration(parseCandRestricts("+consonant,+copalatal"), null,
				capsulate(pDPPTargets), capsulate(mapFeatShift(pDPPTargets, "coArtic", 50)));
		EOF.addRule(palDentalsPostPal2);

		// tts^j is "degeminated" i.e. the t is lost iff it does not precede a
		// palatal glide, in order to realize the split described in Pope 683
		Alteration degeminateTS = new Alteration(null, parseCandRestricts("-approximant"),
				ipaParse.parseSegList("t,t͡sʲ"), ipaParse.parseSegList("t͡sʲ"));
		EOF.addRule(degeminateTS);
		// now delete yod after tsj so htat it doesn't harden
		Alteration deleteYodPostTs = new Alteration(ipaParse.parseSegList("t͡sʲ,j"), ipaParse.parseSegList("t͡sʲ"));
		EOF.addRule(deleteYodPostTs);

		// glides harden: protected (i.e. not preceded by a vowel) j > ɟ , w >
		// gw // TODO check if this is the right time for w > gw
		Alteration hardenGlides = new Alteration(parseCandRestricts("+consonant,-palatal,+wordonset"), // note
																										// that
																										// w
																										// is
																										// not
																										// affected
																										// if
																										// coming
																										// after
																										// a
																										// palatal.
																										// This
																										// is
																										// probably
																										// also
																										// true
																										// of
																										// j
																										// but
																										// TODO
																										// check
																										// this.
				parseCandRestricts("-consonant,-wordcoda"), ipaParse.parseSegList("j;w"),
				ipaParse.parseSegList("ɟ;gʷ"));
		EOF.addRule(hardenGlides);

		// certain stressed front vowels, namely e and ash, become more closed
		// if preceded by palatals.
		// Source : See Buckley, and also Pope pp244-245, section 666 for ash,
		// section 663 for /e/
		Alteration closePostPalatal1 = new Alteration(parseCandRestricts("+consonant,+palatal"), null,
				ipaParse.parseSegList("æ`ː;e`ː"), ipaParse.parseSegList("ɛ`ː;i`ː"));
		EOF.addRule(closePostPalatal1);
		Alteration closePostPalatal2 = new Alteration(parseCandRestricts("+consonant,+copalatal"), null,
				ipaParse.parseSegList("æ`ː;e`ː"), ipaParse.parseSegList("ɛ`ː;i`ː"));
		EOF.addRule(closePostPalatal2);

		// As per Pope p247 section 667; but see also Pope page 164 section 417
		// :
		// FREE countertonic a > ɛ when following palatals but not if preceding
		// l or r or tonic
		Alteration closeFreePostPalatal1 = new Alteration(parseCandRestricts("+consonant,+palatal"),
				parseCandRestricts("+consonant,-liquid;+vowel"), ipaParse.parseSegList("a'"),
				ipaParse.parseSegList("ɛ'"));
		EOF.addRule(closeFreePostPalatal1);
		Alteration blockPreTonicE = new Alteration(null, parseCandRestricts("+stressed,+midclose,+front"),
				ipaParse.parseSegList("a'"), ipaParse.parseSegList("q"));
		EOF.addRule(blockPreTonicE);
		Alteration closeFreePostPalatal2 = new Alteration(parseCandRestricts("+consonant,+palatal"),
				parseCandRestricts("+vowel"), ipaParse.parseSegList("q"), ipaParse.parseSegList("a'"));
		EOF.addRule(closeFreePostPalatal2);
		Alteration unblockPreTonicE = new Alteration(ipaParse.parseSegList("q"), ipaParse.parseSegList("a'"));
		EOF.addRule(unblockPreTonicE);

		// as far as I know, this rule does NOT operate after copalatals, only
		// actually palatal consonants
		// s regressively palatalized by copalatal s so that ssj ejects backward
		Alteration regPalSBySj = new Alteration(ipaParse.parseSegList("s,sʲ"), ipaParse.parseSegList("sʲ,sʲ"));
		EOF.addRule(regPalSBySj);

		// source is wiki, yet to TODO check in Pope
		// eject a j before copalatal consonants, excluding palatal n and l
		Alteration ejectJPrePalatal = new Alteration(parseCandRestricts("+vowel"), parseCandRestricts("+copalatal"),
				nonEntityList(1), ipaParse.parseSegList("j"));
		EOF.addRule(ejectJPrePalatal);

		// metathesis of j around a before palatal r
		Alteration preRMetathesis = new Alteration(null, parseCandRestricts("+rhotic,+copalatal"),
				ipaParse.parseSegList("a,j;a',j;æ`ː,j"), // æ should only exist
															// in long and fully
															// stressed form;
															// logical
															// conjunction with
															// the distribution
															// of /a/ variants
				ipaParse.parseSegList("j,a;j,a';j,æ`ː"));
		EOF.addRule(preRMetathesis);

		// loss of palatalization of feature.
		// TODO move this later if necessary
		List<Phone> cpLTargets = ipaParse
				.parseSegment("" + tDentCh + "," + dDentCh + "," + nDentCh + ",s" + ",z,t͡s,d͡z,l,r");
		Alteration copalatalizationLoss = new Alteration(capsulate(mapFeatShift(cpLTargets, "coArtic", 50)),
				capsulate(cpLTargets));
		EOF.addRule(copalatalizationLoss);

		// second vowel breaking -- in stressed open syllables not followed by
		// palatal sound
		// stressed open e,o,a > ei, ou, ae
		List<Phone> phthongDestList = new ArrayList<Phone>();

		// add ei
		phthongDestList.add(new Phthong(new Vowel(50, 50, false)/* e */, null, new int[][] { { 50, 40, 0 } } /* i */));
		// add ou
		phthongDestList.add(new Phthong(new Vowel(40, 50, true), null, new int[][] { { 40, 40, 1 } }));
		// add ae
		phthongDestList.add(new Phthong(new Vowel(50, 70, false), null, new int[][] { { 50, 50, 0 } }));
		// add ie -- there was another round of ɛ: > iɛ , as per Pope section
		// 225 (not on wiki tho)
		phthongDestList.add(new Phthong(new Vowel(50, 60, false), new int[][] { { 50, 40, 0 } }, null));

		// TODO modify this so that it doesn't occur after a palatal
		Alteration secondDiphthongization1 = new Alteration(null, null, // because
																		// only
																		// open
																		// syllables
																		// can
																		// be
																		// long
																		// at
																		// this
																		// time,
																		// so
																		// posterior
																		// context
																		// is
																		// covered
																		// by
																		// length
																		// TODO
																		// check
																		// this
																		// is
																		// true
																		// and
																		// change
																		// the
																		// way
																		// the
																		// rule
																		// works
																		// if it
																		// is
																		// not.
				capsulate(mapFeatShift(ipaParse.parseSegment("eː,oː,æː,ɛː"), "stress", 1)),
				capsulate(mapFeatShift(phthongDestList, "stress", 1)));
		EOF.addRule(secondDiphthongization1);

		// degeminate ɟɟ > ɟ so that it may be affected by lenition (i.e. :
		// theca > taie)
		Alteration degemGj = new Alteration(ipaParse.parseSegList("ɟ,ɟ"), ipaParse.parseSegList("ɟ"));
		EOF.addRule(degemGj);

		// second lenition -- palatals also effected with ɟ > j
		// note unless I'm wrong the open syllables at this time are
		// specifically the long ones... so we can just use that
		// TODO make sure htis is actually true and fix it if it isn't.

		List<Phone> lenition2Targets = ipaParse.parseSegment("k,ɡ,ɟ,t̪,d̪,p,b");
		List<Phone> lenition2Dests = mapFeatShift(lenition2Targets, "manner", 25);
		lenition2Targets.addAll(ipaParse.parseSegment("x,s,t͡s,θ,f")); // add
																		// voiceless
																		// fricatives
		lenition2Dests.addAll(ipaParse.parseSegment("ɣ,z,d͡z,ð,v,")); // add
																		// their
																		// voiced
																		// counterparts

		Alteration secondLenition = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+sonorant,-lateral"), // i.e. vowels and
															// rhotics
				capsulate(lenition2Targets), capsulate(lenition2Dests)); // make
																			// them
																			// voiced
																			// fricatives
		EOF.addRule(secondLenition);

		// pl > bl also under lenition... and also gl and kl which later become
		// lambda
		// pl > bl > wl does however happen in the West of teh French speaking
		// area
		Alteration secondLenitionPlus = new Alteration(parseCandRestricts("+vowel"), parseCandRestricts("+lateral"),
				ipaParse.parseSegList("p;g;k"), ipaParse.parseSegList("b;ɣ;g"));
		EOF.addRule(secondLenitionPlus);

		// TODO gamma, beta to null by various means, see Pope
		// TODO also send palatal fricative to j when gamma does .
		// as per Pope page 139 section 342
		Alteration effaceGamma1 = new Alteration(parseCandRestricts("+vowel"), // although
																				// shouldn't
																				// this
																				// be
																				// redundant
																				// as
																				// gamma
																				// only
																				// apears
																				// after
																				// vowels?
				parseCandRestricts("+vowel,+close,+rounded,-palatal,+stressed"), // i.e.
																					// stressed
																					// u
																					// or
																					// o
				ipaParse.parseSegList("ɣ"), nonEntityList(1));
		EOF.addRule(effaceGamma1);

		Alteration effaceGamma2 = new Alteration(parseCandRestricts("+velar,+vowel"),
				parseCandRestricts("-stressed,+rounded,+close"), // atonic u or
																	// high o
				ipaParse.parseSegList("ɣ"), nonEntityList(1));
		EOF.addRule(effaceGamma2);

		Alteration effaceGamma3 = new Alteration(parseCandRestricts("+roundoffset"), // i.e.
																						// velar
																						// offsets
																						// +
																						// u
																						// which
																						// is
																						// not
																						// totally
																						// velar
																						// at
																						// this
																						// point
				parseCandRestricts("+open,-midopen"), // i.e. some variant of
														// /a/ or ash
				ipaParse.parseSegList("ɣ"), nonEntityList(1));
		EOF.addRule(effaceGamma3);

		// now send all the remaining velar and palatal fricatives to j
		Alteration dorsalFricsToYod = new Alteration(ipaParse.parseSegList("ɣ;ʝ"), ipaParse.parseSegList("j;j"));
		EOF.addRule(dorsalFricsToYod);

		// efface betas in contexts specified by Pope page 139 section 343
		Alteration effaceBetas = new Alteration(parseCandRestricts("+phthong,+roundoffset,+closeoffset,+backoffset"), // diphthong
																														// ending
																														// in
																														// u
																														// //TODO
																														// work
																														// here
				parseCandRestricts("+vowel,+rounded,+close,-palatal;+wordcoda"), // final
																					// u
																					// or
																					// o
				ipaParse.parseSegList("β"), nonEntityList(1));
		EOF.addRule(effaceBetas);

		// send all other betas to v
		Alteration betaToV = new Alteration(ipaParse.parseSegList("β"), ipaParse.parseSegList("v"));
		EOF.addRule(betaToV);

		// palatal stops, geminate or not, become postalveolar affricates (with
		// no explicit copalatalization feature)
		Alteration affrPalatalStops = new Alteration(ipaParse.parseSegList("c;c,c;ɟ;ɟ,ɟ"),
				ipaParse.parseSegList("t͡ʃ;t͡ʃ;d͡ʒ;d͡ʒ"));
		EOF.addRule(affrPalatalStops);

		// second vowel loss -- unstressed intertonic and now also unstressed
		// final, with a reduced to schwa
		Alteration secondVowelLoss1 = new Alteration(ipaParse.parseSegList("e;ɛ;i;ɔ;o;u;ʉ"), // all
																								// of
																								// these
																								// are
																								// by
																								// default
																								// unstressed
				nonEntityList(7)); // TODO make sure this works...
		EOF.addRule(secondVowelLoss1);
		Alteration secondVowelLoss2 = new Alteration(ipaParse.parseSegList("a"), ipaParse.parseSegList("ə"));
		EOF.addRule(secondVowelLoss2);

		// not in Pope, but deduced by common sense given that caballos >
		// cheval, not *chevalle
		// also final ss > s : bassum > bas, not *basse
		Alteration degeminateSomeFinals = new Alteration(null, parseCandRestricts("+wordcoda"),
				ipaParse.parseSegList("l,l;s,s"), ipaParse.parseSegList("l;s"));
		EOF.addRule(degeminateSomeFinals);

		// consonant cluster simplification
		// like in Gallo-Roman, delete middle stops in 3 consonant clusters
		Alteration deleteMiddleStops = new Alteration(parseCandRestricts("+consonant"),
				parseCandRestricts("+consonant,-liquid"), // all consonants
															// except for l and
															// r
				lParse.parseSegList("p,b,t,d,c,g,v"), nonEntityList(6));
		EOF.addRule(deleteMiddleStops);

		// insert schwa after labiovelars if they precede either a consonant or
		// a word coda -- i.e. Iacobos > Jakkws > Jacques
		Alteration schwaPostLabVel = new Alteration(parseCandRestricts("+consonant,+velar,+rounded"),
				parseCandRestricts("+consonant,+wordcoda"), nonEntityList(1), ipaParse.parseSegList("ə"));
		EOF.addRule(schwaPostLabVel);

		// nowhere does it say this but it has become painfully obvious in the
		// testing
		// final schwa is not inserted after final geminate consonants.
		ArrayList<List<Phone>> geminates = new ArrayList<List<Phone>>(
				ipaParse.parseSegList("p,p;b,b;m,m;f,f;v,v;" + tDentCh + "," + tDentCh + ";" + dDentCh + "," + dDentCh
						+ ";" + nDentCh + "," + nDentCh + ";l,l;s,s;j,j;k,k;g,g;h,h;k,kʷ;ɡ,ɡʷ"));
		Alteration blockFinalSchwaPostGeminates = new Alteration(null, parseCandRestricts("+wordcoda"), geminates,
				appendToAll(geminates, ipaParse.getPhone("y") /* dummy */, true));
		EOF.addRule(blockFinalSchwaPostGeminates);

		// insertion of final schwa to prevent a few impossible final clusters
		// -- might functionally need to place this before the previous shift.
		// first one of these will deal with all 3-consonant clusters-- all of
		// which are illegal except some lingering glide formations
		Alteration insertFinalSchwa1 = new Alteration(
				parseCandRestricts("+consonant,-approximant;+consonant;+consonant"), parseCandRestricts("+wordcoda"),
				nonEntityList(1), ipaParse.parseSegList("ə"));
		EOF.addRule(insertFinalSchwa1);

		// insertion of final schwa to prevent illegal ending of anything but a
		// fricative or sonorant (or vowel) before a final stop consonant
		Alteration insertFinalSchwa2 = new Alteration(parseCandRestricts("-nasalstop,-sonorant,-fricative;+stop"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), ipaParse.parseSegList("ə"));
		EOF.addRule(insertFinalSchwa2);

		// prevent affricate or fricative-final 2-cons clusters where first is
		// anything but a sonorant (or vowel)
		Alteration insertFinalSchwa3 = new Alteration(parseCandRestricts("-nasalstop,-sonorant;+affricate,+fricative"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), ipaParse.parseSegList("ə"));
		EOF.addRule(insertFinalSchwa3);

		// prevent final 2 cons clusters ending with a liquid or nasal stop,
		// where teh first is anything but a glide
		Alteration insertFinalSchwa4 = new Alteration(parseCandRestricts("+consonant;+nasalstop,+liquid"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), ipaParse.parseSegList("ə"));
		EOF.addRule(insertFinalSchwa4);

		// prevent any final 2-cons clusters ending with glides
		Alteration insertFinalSchwa5 = new Alteration(parseCandRestricts("+consonant;+approximant"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), ipaParse.parseSegList("ə"));
		EOF.addRule(insertFinalSchwa5);

		// unblock final geminates
		Alteration deleteDummy = new Alteration(ipaParse.parseSegList("y"), nonEntityList(1));
		EOF.addRule(deleteDummy);

		// TODO are there any other contexts that trigger schwa insertion ?

		// See Pope : some unsupported final stops are opened (t, d, k? p??)
		// this must precede both degemination(?) and the absorption of nasal
		// stops as part of nasalization
		Alteration openUnsupportedFinals = new Alteration(parseCandRestricts("+vowel,+phthong"), // may
																									// in
																									// fact
																									// be
																									// more
																									// complicated
				parseCandRestricts("+wordcoda"), ipaParse.parseSegList("" + tDentCh + ";" + dDentCh + ";k"),
				ipaParse.parseSegList("θ;ð;x"));
		EOF.addRule(openUnsupportedFinals);

		// labial + coronal (dental, alveolar or postalveolar ) > dental +
		// coronal
		// not stated in Pope, apparent to me : ex rubeum > roddzha > rouge,
		// also cases of mj > mdhza > ndzha
		Alteration assimLabsToCoronals = new Alteration(null, parseCandRestricts("+coronal,-sonorant"),
				ipaParse.parseSegList("p;b;m;f;v"),
				capsulate(mapFeatShift(ipaParse.parseSegment("p,b,m,f,v"), "place", 80)));
		EOF.addRule(assimLabsToCoronals);

		// degemination: all duplicate consonants reduced to one (except for r--
		// this happens later) -- must be after final stops are opened --
		Alteration degemination = new Alteration(geminates,
				ipaParse.parseSegList("p;b;m;f;v;" + tDentCh + ";" + dDentCh + ";" + nDentCh + ";l;s;j;k;g;h;kʷ;ɡʷ"));
		EOF.addRule(degemination);
		Alteration degemination2 = new Alteration( // to get other clusters of
													// dental + nonsonorant
													// coronal, often coming
													// from labials or former
													// palatals
				null, parseCandRestricts("+coronal,-sonorant"),
				ipaParse.parseSegList("t;d;" + tDentCh + ";" + dDentCh + ";θ;ð"), nonEntityList(6));
		EOF.addRule(degemination2);

		// devoicing of final stops and fricatives -- TODO decide whether this
		// should be placed before of after the latest round of consonant
		// cluster simplifications
		// comes after gemination? final dd > t
		List<Phone> devoicingTargets = ipaParse.parseSegment("b,v," + dDentCh + "ð,d͡z,g,ɣ");
		Alteration finalDevoicing = new Alteration(null, parseCandRestricts("+wordcoda"), capsulate(devoicingTargets),
				capsulate(mapFeatShift(devoicingTargets, "voiced", 0)));
		EOF.addRule(finalDevoicing);

		// x -- either effaced or shifts to j -- see Pope section 357
		// 1: efface after unstressed vowels
		Alteration effaceXpostNonstressed = new Alteration(null, parseCandRestricts("+vowel,-stressed"),
				ipaParse.parseSegList("x"), nonEntityList(1));
		EOF.addRule(effaceXpostNonstressed);

		// 2: open to j after a and i. Absorbed after i .
		Alteration xToJPostA = new Alteration(parseCandRestricts("+open,-midopen"), null, ipaParse.parseSegList("x"),
				ipaParse.parseSegList("j"));
		EOF.addRule(xToJPostA);

		// 3 : efface after i
		Alteration effaceXPostI = new Alteration(parseCandRestricts("+vowel,+palatal,+close,-midclose"), null,
				ipaParse.parseSegList("x"), nonEntityList(1));
		EOF.addRule(effaceXPostI);

		// 4: shift back to c elsewhere
		Alteration closeX = new Alteration(ipaParse.parseSegList("x"), ipaParse.parseSegList("k"));
		EOF.addRule(closeX);

		// TODO place somewhere near or after degemination (which must be after
		// the second lenition)
		// some place after second lenition : new mutataions of gammma see pope
		// 342
		// shift of labials goes to beta at first ... but all betas are either
		// effaced or passed to v
		// after these, gamma becomes "jamma"~j everywhere

		// another round of consonant cluster simplifications and assimilations
		// -- see Pope.

		// dz > z unless final
		Alteration deaffricateZ = new Alteration(null, parseCandRestricts("-wordcoda"), lParse.parseSegList("z"), // should
																													// be
																													// the
																													// affricate.
																													// TODO
																													// check
																													// this.
				ipaParse.parseSegList("z"));
		EOF.addRule(deaffricateZ);

		// TODO drastically reorganize the following sections

		// ae > ai before a nasal -- as per Pope, Buckley
		List<Phone> rAEpNTargets = new ArrayList<Phone>(), rAEpNDests = new ArrayList<Phone>();
		rAEpNTargets.add(new Phthong(new Vowel(50, 70), null, new int[][] { { 50, 50, 0 } }));
		rAEpNDests.add(new Phthong(new Vowel(50, 70), null, new int[][] { { 50, 40, 0 } }));
		Alteration raiseAEpreNasal = new Alteration(null, parseCandRestricts("+consonant,+nasal"),
				includeStressedVariants(capsulate(rAEpNTargets)), includeStressedVariants(capsulate(rAEpNDests)));
		EOF.addRule(raiseAEpreNasal);

		// ae > a: when it is before j, so that it can ultimately be reabsorbed
		// to ai
		Alteration toLongApreJ = new Alteration(null, parseCandRestricts("+approximant,+palatal"),
				includeStressedVariants(capsulate(rAEpNTargets)), includeStressedVariants(ipaParse.parseSegList("aː")));
		EOF.addRule(toLongApreJ);

		// ash to long eh everywhere else, thus restoring long eh to the vowel
		// inventory
		Alteration AEtoOpenE = new Alteration(includeStressedVariants(capsulate(rAEpNTargets)),
				includeStressedVariants(ipaParse.parseSegList("ɛː")));
		EOF.addRule(AEtoOpenE);

		// After palatals /ɲ/, /ʎ/ a following /s/ becomes affricated -- note
		// these are the only palatals left in the language at this point except
		// for j
		// ... and syllable final j is dealt with in the previous shift.
		// need to specify consonant-- otherwise vowels do the shift too.
		Alteration affrSpostPal = new Alteration(parseCandRestricts("+palatal,+consonant,-approximant"), null,
				ipaParse.parseSegList("s"), ipaParse.parseSegList("t͡s"));
		EOF.addRule(affrSpostPal);

		// if final or following a consonant, /ɲ/, /ʎ/ are depalatalized,
		// becoming jn and l respectively
		// * however first person verb forms remain palatal because of analogy
		// enya and elya lose their palatal articulation and become coronal if
		// pre-consonant or pre-wordcoda
		// enya becomes jn when it does so if following a vowel. elya simply
		// becomes l.
		// neither should be occurring between two consonants to begin with-- if
		// htey are something is pretty badly wrong.
		Alteration dePalatalizeFinalSonors = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+consonant,+wordcoda"), ipaParse.parseSegList("ɲ;ʎ"),
				ipaParse.parseSegList("j," + nDentCh + ";l"));
		EOF.addRule(dePalatalizeFinalSonors);

		// development of prae-consonantal and word-final allophone of l, dark l
		// as per Pope page 153-154, section 182-- l does NOT darken before the
		// word coda
		// except in the SouthEastern dialects after a-- resulting in some
		// loanwards

		Alteration darkenLpreConsonant = new Alteration(null, parseCandRestricts("+consonant,-lateral"),
				ipaParse.parseSegList("l;ʎ"), ipaParse.parseSegList("lˠ;lˠ"));
		EOF.addRule(darkenLpreConsonant);

		// source: see Pope vowel destination charts, in sections 661-674, on
		// pages 236-249
		// SHIFT: formation of new diphthongs of long vowels + j
		// the first parts of targets targets are all long and stressed vowels
		// -- including diphthongs formed by the previous palatalization
		// note we have i̯ɛ and uɔ instead of stressed ɛ and ɔ because by
		// definition, before palatals in stressed syllables they should have
		// been diphthongized earlier, in the first diphthongization/vowel
		// breaking
		// furthermore there is no unstressed au as that became a in Latin and
		// Gaulish -- TODO maybe add that in somewhere. Or not, we probalby
		// don't have to deal with it.
		// countertonic open e and open o are closed to e and o when
		// diphthongized as seen in Pope s 665 (for e) and 669 (for o)
		// countertonic au > o in the diphthong as well as per Pope s 674
		List<List<Phone>> fPDTargets = appendToAll(
				ipaParse.parseSegList(
						"i;i';i`ː;i`;e;e';e`ː;e`;ɛ;ɛ';i̯̯ɛ`;ɛ`;a;a';a`;a`i̯;æ`ː;a'u̯;a`u̯;ɔ;ɔ';u̯ɔ`;ɔ`;o;o';o`ː;o`;o'u̯;o`u̯;ʉ;ʉ';ʉ`;ʉ`ː"),
				new Consonant(50, 30, false, true), true);
		List<List<Phone>> fPDDests = ipaParse.parseSegList(
				"iː;i'ː;i`ː;i`ː;ei̯;e'i̯;e`i̯;e`i̯;ei̯;e'i̯;i̯ɛ`i̯;e`i̯;ai̯;a'i̯;a`i̯;a`i̯;a`i̯;ɔ'i̯;ɔ`i̯;ɔi̯;ɔ'i̯;u̯ɔ`i̯;o`i̯;oi̯;o'i̯;o`i̯;o`i̯;o'i̯;o`i̯;ʉi̯;ʉ'i̯;ʉ`i̯;ʉ`i̯");

		Alteration formPalatalDiphthongs = new Alteration(fPDTargets, fPDDests);
		EOF.addRule(formPalatalDiphthongs);

		// TODO wait a minute pope says this happens much later.. check this. Oh
		// wait it does happen AGAIN later
		// au > ɔ
		Alteration monophthongizeAu = new Alteration(includeStressedVariants(ipaParse.parseSegList("au̯")),
				includeStressedVariants(ipaParse.parseSegList("ɔː"))); // TODO
																		// --
																		// check,
																		// is it
																		// long?
		EOF.addRule(monophthongizeAu);

		// simplification of triphthongs -- this also sees the shift of uoi to
		// yi
		Alteration elimTriphthongs = new Alteration(ipaParse.parseSegList("i̯ɛ`i̯;u̯ɔ`i̯"),
				ipaParse.parseSegList("i`ː;y`i̯"));
		EOF.addRule(elimTriphthongs);

		// all rising diphthongs become falling diphthongs. The second,
		// unstressed element is fronted and unrounded if not already so.
		Alteration elimRisePhthongs = new Alteration(ipaParse.parseSegList("i̯ɛ`;u̯ɔ`"),
				ipaParse.parseSegList("i`ɛ̯;u`ɛ̯"));
		EOF.addRule(elimRisePhthongs);

		// the beginnings of nasalization : a,e,o and their various derived
		// diphthongs begin to be nasalized
		// ... by following nasal consonants, regardless of what follows them.

		// TODO place somewhere far before nasalization: all o and ɔ
		// varieties-including the diphthongs-- become u before nasals
		// Sourcing: Pope page 176 section 459 : she asserts it occurs in
		// "Gallo-Roman", although her Gallo-Roman includes some
		// of what is termed "Early Old French" here. However one could make a
		// good case that it belongs in teh Gallo-Romance section,
		// as this would allow it to bleed diphthongization, which
		// diachronically it appears to do (despite some attested, on wiki tho,
		// cases of uen , i.e. buen which became bon, likely through a stage of
		// bun)

		// TODO place the initiation of nasalization. MUST BE BEFORE THIS POINT.
		// first nasalization only nasalizes a,e,o and variants, i.e. diphthongs
		// based on these (not high vowels i,u,etc. )
		// source for above is Wikipedia's Phonological history of French page
		// -- if have time, track down the real source (not cited there at the
		// moment)
		// this happens early enough to block the Later old French shifts of ei
		// > oi and ou > eu
		// Note Pope page 176 section 459: all o and ɔ varieties-including the
		// diphthongs-- become nasal u before nasal consonants
		// -- thus restoring u to the langauge (as original u > y)
		// she asserts it occurs in "Gallo-Roman", although her Gallo-Roman
		// includes some
		// of what is termed "Early Old French" here. However one could make a
		// good case that it belongs in teh Gallo-Romance section,
		// as this would allow it to bleed diphthongization, which
		// diachronically it appears to do (despite some attested, on wiki tho,
		// cases of uen , i.e. buen which became bon, likely through a stage of
		// bun)
		// however here we keep it in the Old French stage rather than
		// Gallo-Roman, simply for programming efficiency (and laziness haha)

		// first stage : a, e and o variants all nasalized with various
		// destinations as described by Pope's chapter on nasalization, and on
		// wiki

		// first add all the ones that can occur regardless of stress
		List<List<Phone>> fNTargets = includeStressedVariants(
				ipaParse.parseSegList("e;ei̯;ɛ;ɛː;a;aː;ai̯;ɔː;ɔ;ɔi̯;o;oi̯"));
		List<List<Phone>> fNDests = includeStressedVariants(
				capsulate(mapFeatShift(ipaParse.parseSegment("e,e,e,ɛː,a,aː,ai̯,uː,u,oi̯,u,oi̯"), "nasal", 1)));
		// then add the ones that ONLY occur in certain positions depending on
		// stress:
		fNTargets.addAll(ipaParse.parseSegList("i`ɛ̯;ə;u`ɛ̯;o`u̯"));
		fNDests.addAll(capsulate(mapFeatShift(ipaParse.parseSegment("i̯e`,a,uː,uː"), "nasal", 1)));

		Alteration firstNasalization = new Alteration(null, parseCandRestricts("+consonant,+nasal"), fNTargets,
				fNDests);
		EOF.addRule(firstNasalization);

		// nasalization -- note that Old Occitan doesn't nasalize vowels like
		// Old French does,
		// and some Rhaeto-Romance languages do, so its origins belong in this
		// time period, Early Old French

		// s > z if before a voiced consonant or f -- see Pope page 151 section
		// 377. All of these new /z/ instances are ultimately deleted
		Alteration voiceSPreVoiced = new Alteration(null, parseCandRestricts("+consonant,+voiced"),
				ipaParse.parseSegList("s"), ipaParse.parseSegList("z"));
		EOF.addRule(voiceSPreVoiced);

		Alteration voiceSPreF = new Alteration(null, parseCandRestricts("+labiodental"), ipaParse.parseSegList("s"),
				ipaParse.parseSegList("z"));
		EOF.addRule(voiceSPreF);

		return EOF;
	}

	public LangLexicon toClassicOldFrench(LangLexicon earlyOF) // circa 1100 --
																// this is the
																// one that the
																// script best
																// represents
	{
		LangLexicon COF = new LangLexicon(earlyOF.getLexiconClone());

		// u > y completed
		Alteration uFronting2 = new Alteration(includeStressedVariants(ipaParse.parseSegList("ʉ;ʉː;ʉi̯")),
				includeStressedVariants(ipaParse.parseSegList("y;yː;yi̯")));
		COF.addRule(uFronting2);
		// Pope argues o>u occurred immediately after, but it seems later
		// sources place it as happening in Late Old French.
		// see Pope

		// f,p,k lost before final s or t
		Alteration loseFPKpreST = new Alteration(null, parseCandRestricts("+alveolar,+dental,+sibilant,+stop"), // this
																												// could
																												// technically
																												// also
																												// hit
																												// theta,
																												// but
																												// theta
																												// doesn't
																												// occur
																												// after
																												// another
																												// consonant
																												// in
																												// final
																												// position
																												// anyways
				ipaParse.parseSegList("f;p;k"), nonEntityList(3));
		COF.addRule(loseFPKpreST);

		// ei > oi and ou>eu -- source: Pope pp104-106, sections 226-230.
		// ei > oi was blocked by nasals, however (probably because it was
		// already nasalized at this point, but we block it here also to be
		// sure.)
		Alteration EiToOi = new Alteration(null, parseCandRestricts("-nasalstop"),
				includeStressedVariants(ipaParse.parseSegList("ei̯")),
				includeStressedVariants(ipaParse.parseSegList("oi̯")));
		COF.addRule(EiToOi);
		// per Pope 185, section 489 : ou > eu shift was blocked by subsequent
		// labial consonants
		Alteration OuToEu = new Alteration(null, parseCandRestricts("-labial"),
				includeStressedVariants(ipaParse.parseSegList("ou̯")),
				includeStressedVariants(ipaParse.parseSegList("eu̯")));
		COF.addRule(OuToEu);

		// stressed ash to open e, if not earlier than here??? -- currently it's
		// earlier.

		// note countertonic ash > eh > e had occurred earlier -- Pope section
		// 121 and also referenced in section 231 (p 107)
		// ... but we include it here for convenience
		// countertonic open/free e > schwa, o > u

		// closing of eh > e . Actually occurs earlier, but programmed here for
		// convenience as the previous section has gotten quite convoluted.
		// as per Pope p 107, this was blocked if before r or l and then another
		// consonant (or word coda, implicitly, given the syllabic rules at the
		// time)
		List<List<Phone>> dummy = ipaParse.parseSegList("ɘ;ɘ'");
		Alteration blockCtClosing = new Alteration(null, parseCandRestricts("+liquid;+consonant,+wordCoda"),
				ipaParse.parseSegList("ɛ;ɛ'"), dummy);
		COF.addRule(blockCtClosing);
		Alteration closeCtEh = new Alteration(ipaParse.parseSegList("ɛ;ɛ'"), ipaParse.parseSegList("e;e'"));
		COF.addRule(closeCtEh);
		Alteration unblockEh = new Alteration(dummy, ipaParse.parseSegList("ɛ;ɛ'"));
		COF.addRule(unblockEh);

		// now e closes to schwa countertonically. This is however blocked in
		// closed contexts.
		Alteration blockCtReduction = new Alteration(null, parseCandRestricts("+consonant;+consonant,+wordcoda"),
				ipaParse.parseSegList("e;e'"), dummy);
		COF.addRule(blockCtReduction);
		Alteration CtEReduction = new Alteration(ipaParse.parseSegList("e;e'"), ipaParse.parseSegList("ə;ə'"));
		COF.addRule(CtEReduction);
		Alteration unblockCtE = new Alteration(dummy, ipaParse.parseSegList("e;e'"));
		COF.addRule(unblockCtE);

		// Source: Pope page 107-108, sections 107-108
		Alteration counterTonicOToU = new Alteration(ipaParse.parseSegList("o;o';ɔ;ɔ"),
				ipaParse.parseSegList("u;u';u;u'"));
		COF.addRule(counterTonicOToU);

		// loss of interdental fricatives in the early 1100s -- simple deletion
		// everywhere they occur
		Alteration loseInterdentals = new Alteration(ipaParse.parseSegList("θ;ð"), nonEntityList(2));
		COF.addRule(loseInterdentals);

		// where, as a result of the previous rule, we now have unstressed (or
		// countertonic) /a/ and then another vowel, it becomes a schwa
		Alteration reduceHiatusA = new Alteration(null, parseCandRestricts("+vowel"), ipaParse.parseSegList("a;a'"),
				ipaParse.parseSegList("ə;ə"));
		COF.addRule(reduceHiatusA);

		// o (and ou) > u elsewhere will happen in teh 12th (as per Pope) or
		// 13th century (as per others), putting it in the "Late Old French"
		// category.
		// Pope believes it happened earlier but it seems that later research
		// places it slightly later, although she does make a pretty convincing
		// case based on spellings.
		// this level of nitpicking however is really not necessary for our
		// purposes.

		// Before vowels are lengthened before s,z:
		// a,ɔ,ɔ: (<au) > ɑ,o,o: before s,z .
		// e > eh also happened slightly later but seems to have different
		// conditioning .
		// source for this is Wiki. Pope's (older) view is different, arguing
		// that this shift occurred once they were already lengthened
		// however it seems that some uvular or laryngeal aspect of s during the
		// period may have been response.
		// for Pope's view of it happening in the Later Old French period, see
		// Pope pp209-210,212 sections (575-6,?) 578-580,584 -- note Pope does
		// admit that these changes happened even when a,o,e were NOT long
		// wiki has o > u before s, while Pope (pp210-1, sect 581) argues this
		// only occurrred in "Southern dialects", whatever that means
		// for now, I'm going with wiki, to avoid a merger of o and ɔ that I
		// don't yet know of evidence for.
		// also note however that au > long oh seems to remain long oh when not
		// after s/z
		Alteration modifyPreSib = new Alteration(null, parseCandRestricts("+sibilant,+alveolar"), // for
																									// edh,
																									// x
																									// too
				includeStressedVariants(ipaParse.parseSegList("a;ɔ;ɔː")),
				includeStressedVariants(ipaParse.parseSegList("ɑ;o;oː"))); // effectiely
																			// a
																			// chian
																			// shift
																			// before
																			// sibilants.
		COF.addRule(modifyPreSib);

		// lengthen all short vowels pre /s/ [s~x~h] and z [similar phonemes.]
		// --- also edh and x
		// a list of all short (and pure) vowels that are capable of occurring
		// before sibilants at this time:
		// schwa is intentionally excluded from the list of targets
		List<Phone> preSibShortVowels = ipaParse.parseSegment("i,e,ɛ,ɑ,o,y");
		Alteration lengthenPreSib = new Alteration(null, parseCandRestricts("+sibilant,+alveolar;+consonant,+wordcoda"),
				includeStressedVariants(capsulate(preSibShortVowels)),
				includeStressedVariants(capsulate(mapFeatShift(preSibShortVowels, "length", 2))));
		COF.addRule(lengthenPreSib);

		// z in all these positions passes to edh -- and is deleted at a time
		// before original s in similar positions:
		// this is reflected in English loanwards : blastumare>blazmer> blame;
		// but escapare > exchaper > escape but echaper in French
		Alteration spirantizePreCons = new Alteration(null, parseCandRestricts("+consonant"),
				ipaParse.parseSegList("z;s"), ipaParse.parseSegList("ð;x"));
		COF.addRule(spirantizePreCons);

		// dark l vocalization becomes complete -- early 1100s
		Alteration vocalizeDarkL = new Alteration(capsulate(mapFeatShift(ipaParse.parseSegment("l"), "coArtic", 40)),
				ipaParse.parseSegList("w"));
		COF.addRule(vocalizeDarkL);

		// Lowering of nasal e to nasal a . According to Pope this occurs in teh
		// late 11th century
		// Source: Pope page 170 section 439
		Alteration lowerNasalE = new Alteration(includeStressedVariants(ipaParse.parseSegList("ẽ")),
				includeStressedVariants(ipaParse.parseSegList("ã")));
		COF.addRule(lowerNasalE);
		// it is intentional and correct that this does not effect diphthongs
		// like ei and ie, or ai

		// ɛ before w > ɛa -- Pope page 200 sections 538-539, this occurred
		// slightly before the 12th century.
		Alteration mutateEsPreW = new Alteration(null, parseCandRestricts("+consonant,-stop,+rounded"),
				includeStressedVariants(ipaParse.parseSegList("ɛ;ɛː")),
				includeStressedVariants(ipaParse.parseSegList("ɛa̯;ɛa̯")));
		COF.addRule(mutateEsPreW);

		// ue > ø in the late 1100s : Pope page 202-3 sections 550-554 , later
		// parts on divergent dialects that ultimately contributed words to the
		// standard
		// note however the fate of ueu is slightly different.
		Alteration UeToFrontO = new Alteration(includeStressedVariants(ipaParse.parseSegList("uɛ̯")),
				includeStressedVariants(ipaParse.parseSegList("œu̯̯")));
		COF.addRule(UeToFrontO);

		return COF;
	}

	public LangLexicon toLateOldFrench(LangLexicon classicOF) // circa 1300
	{
		// Pope cahpter 9 part 4 -- r in all positions but final often lost its
		// trill and went to z (after original z had been lost?)
		// TODO place these^

		LangLexicon LOF = new LangLexicon(classicOF.getLexiconClone());

		// as per Pope
		// s > h, if before a consonant or word coda
		Alteration debuccalizeH = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+consonant,+wordcoda"), ipaParse.parseSegList("x"), ipaParse.parseSegList("h"));
		LOF.addRule(debuccalizeH);

		// before deleting edh (ð) preconsonantal, before dentals (in practice
		// only n), ð went to z : hence
		// mansionata>masjonedha>majdhnjedha>maizniedhe>maiSnier
		// no source for this in Pope -- I found it was necessary to get the
		// system to work for words maisniee
		Alteration dissimilateEdhPreDental = new Alteration(null, parseCandRestricts("+dental"),
				ipaParse.parseSegList("ð"), ipaParse.parseSegList("z"));
		LOF.addRule(dissimilateEdhPreDental);

		// per Pope -- edh is lost before h is lost
		Alteration deleteEdhPraeconsonantal = new Alteration(parseCandRestricts("+vowel"),
				parseCandRestricts("+consonant"), ipaParse.parseSegList("ð"), nonEntityList(1));
		LOF.addRule(deleteEdhPraeconsonantal);

		// delabialization of labialized consonants (i.e. labiovelars), at last,
		// as per pope, and Buckley, occurred in 1200s.
		Alteration deLabializeVelars = new Alteration(ipaParse.parseSegList("kʷ;gʷ"), ipaParse.parseSegList("k;g"));
		LOF.addRule(deLabializeVelars);

		// hte first round of oeu becomes ø: in the late 1100s : Pope page 202-3
		// sections 550-554 , later parts on divergent dialects that ultimately
		// contributed words to the standard
		// note however the fate of ueu is slightly different.
		// note that there is no qualitative difference between the mid front
		// rounded vowels in French at this time.
		Alteration UeToFrontO = new Alteration(includeStressedVariants(ipaParse.parseSegList("œu̯̯")),
				includeStressedVariants(ipaParse.parseSegList("øː")));
		LOF.addRule(UeToFrontO);

		// rising diphthongs develop where first element is u,y or i : Ie > jE;
		// Yi > ɥI
		// oi and ɔi > we
		// these are analyzed as glide + vowel sequences for our convenience
		// here (as that is what they ultimately become), rather htan "real"
		// diphthongs.

		Alteration toRisingDiphthongs = new Alteration(
				includeStressedVariants(ipaParse.parseSegList("iɛ̯;yi̯̯;ɔi̯̯;oi̯̯;i̯̯ẽ;ɔ̃i̯;õi̯")),
				includeStressedVariants(ipaParse.parseSegList("j,e;ɥ,i;w,e;w,e;j,ẽ;w,ẽ;w,ẽ")));
		LOF.addRule(toRisingDiphthongs);
		// note iɛ̯ must have the second component go to e not ɛ, so that it is
		// properly labialized under the influence of labiovelarized dark l
		// this provides support to the idea that it went to high e not low ɛ

		// then the second elements, and many other segments, are affected by
		// nasalization :
		// nasalization of i,u,y and derived diphthongs.
		// I consider this to be a second nasalization that also would have
		// effected other vowels loaned into hte language at the time
		// with this in mind, note that it must come before the loss of h
		// it is also bled by l
		List<List<Phone>> sNTargets = ipaParse
				.parseSegList("i;iː;y;yː;e;eː;ei̯̯;eu̯̯;ɛ;ɛː;a;aː;ai̯̯;ɔ;ɔː;o;oː;ou̯̯;u;uː;øː");
		List<List<Phone>> sNDests = capsulate(mapFeatShift(
				ipaParse.parseSegment("i,iː,y,yː,e,eː,ei̯̯,eu̯̯,e,eː,a,aː,ai̯̯,u,uː,u,uː,uː,u,uː,øː"), "nasal", 1));
		Alteration secondNasalization = new Alteration(null, parseCandRestricts("+nasalstop"),
				includeStressedVariants(sNTargets), includeStressedVariants(sNDests));
		LOF.addRule(secondNasalization);

		// ai > a: before palatals (now just enya and mouille)
		// this bleeds the next shift
		// no actual sourcing for this but it is apparent givne hte modern
		// pronounciation of aille and aigne clusters.
		// effects also nasals.
		List<Phone> aiTAPPTargets = ipaParse.parseSegment("ai̯̯");
		List<Phone> aiTAPPDests = ipaParse.parseSegment("aː");
		aiTAPPTargets.addAll(mapFeatShift(aiTAPPTargets, "nasal", 1));
		aiTAPPDests.addAll(mapFeatShift(aiTAPPDests, "nasal", 1));
		Alteration aiToAPrePal = new Alteration(null, parseCandRestricts("+consonant,+palatal"),
				includeStressedVariants(capsulate(aiTAPPTargets)), includeStressedVariants(capsulate(aiTAPPDests)));
		LOF.addRule(aiToAPrePal);

		// ai > long eh -- blocked by nasalization though.
		// this must come before the formation of diphthongs with w segments --
		// see eau, which is not of lateral origin.
		// also note per Pope page 198 section 4331 -- when ai was in hiatus
		// with the tonic vowel or with final schwa, leveling was later (also
		// later for final)
		Alteration aiToE = new Alteration(includeStressedVariants(ipaParse.parseSegList("ai̯̯")),
				includeStressedVariants(ipaParse.parseSegList("ɛː")));
		LOF.addRule(aiToE);

		// efface h -- both in germanic loans and where it was originally a
		// sibilant
		Alteration effaceH = new Alteration(ipaParse.parseSegList("h"), nonEntityList(1));
		LOF.addRule(effaceH);

		// source: wiki, and Pope pages 154-156 sections 383-393
		// combination of w with preceding vowels to form a host of new phthongs
		// new diphthongs arise because of dark L
		// note : Pope seems to argue that vocalization of l only occurred
		// normally before CONSONANTS, not word codas
		// ... , and where it did occur in word codas, it was because of analogy
		// from plural forms (i.e. bels > beaux , bel > bel, but beau by
		// analogy)
		// BUT! : this shift only effects places where l has already been
		// vocalized to w, so whether a consonant follows or not is irrelevant
		// furthermore, w from other sources is also effeced
		List<List<Phone>> mWWTargets = includeStressedVariants(
				ipaParse.parseSegList("i;iː;e;eː;eu̯̯;ɛ;ɛː;ɛa̯;a;aː;ɔ;ɔː;o;oː;ou̯̯;uɛ̯;u;uː;y;yː"));
		mWWTargets = appendToAll(mWWTargets, ipaParse.getPhone("w"), true);
		List<List<Phone>> mWWDests = includeStressedVariants(ipaParse.parseSegList(
				"iː;iː;eu̯̯;eu̯̯;eu̯̯;ə̯au̯;ə̯au̯;ə̯au̯;au̯̯;au̯̯;ou̯̯;ou̯̯;ou̯̯;ou̯̯;ou̯̯;u̯̯ɛu̯̯;uː;uː;yː;yː"));
		Alteration mergeWithW = new Alteration(mWWTargets, mWWDests);
		LOF.addRule(mergeWithW);

		// TODO note that technically at this time ieu and ueu (later jeu and
		// weu) were actually falling triphthongs
		// for now they are programmed as glide + diphthong ( rather than
		// triphthong)

		// o > u . Also ou, ɔw>ɔu>u, including those that were newly formed
		// diphthongs
		// source for ou̯̯;ɔu̯̯ > u:,u: at this stage -- Pope page 202 sectiosn
		// 547-548
		Alteration oToU = new Alteration(includeStressedVariants(ipaParse.parseSegList("o;oː;ou̯̯;ɔu̯̯")),
				includeStressedVariants(ipaParse.parseSegList("u;uː;uː;uː")));
		LOF.addRule(oToU);

		// at last, degemination of r-- see Pope
		Alteration degemR = new Alteration(ipaParse.parseSegList("r,r"), ipaParse.parseSegList("r"));
		LOF.addRule(degemR);

		// e > ɛ in closed syllables
		Alteration closedEShift = new Alteration(null, parseCandRestricts("+consonant;+consonant,+wordcoda"),
				includeStressedVariants(ipaParse.parseSegList("e;eː")), // i.e.
																		// I
																		// don't
																		// believe
																		// there
																		// are
																		// any
																		// cases
																		// of
																		// long
																		// e in
																		// closed
																		// syllables.
																		// TODO
																		// Could
																		// be
																		// wrong
																		// though
																		// .
				includeStressedVariants(ipaParse.parseSegList("ɛ;ɛː")));
		LOF.addRule(closedEShift);

		// long low o > high o
		// Pope page 210 section 510 -- open oh also closed to closed o "...
		// [where] it had become free and long owing to the effacement of
		// prae-consonantal or final s or z
		Alteration raiseLongO = new Alteration(includeStressedVariants(ipaParse.parseSegList("ɔː")),
				includeStressedVariants(ipaParse.parseSegList("oː")));
		LOF.addRule(raiseLongO);

		// deaffrication: ts, tsh and dzh to s, sh and zh
		// * (this causes phonemicization of the difference between a and ɑ,
		// with rise of minimal pairs)

		Alteration deaffrication = new Alteration(ipaParse.parseSegList("t͡s;d͡z;t͡ʃ;d͡ʒ"),
				ipaParse.parseSegList("s;z;ʃ;ʒ"));
		LOF.addRule(deaffrication);

		// in eu diphthong, first element rounds to oe/o: : Pope page 201
		// section 541-542. resulting œu diphthong does not pass to ø until the
		// 13th century
		Alteration euToOeu = new Alteration(includeStressedVariants(ipaParse.parseSegList("eu̯̯")),
				includeStressedVariants(ipaParse.parseSegList("œu̯̯"))); // note
																			// that
																			// Pope
																			// holds
																			// this
																			// to
																			// be
																			// higher,
																			// a
																			// /ø/
		LOF.addRule(euToOeu);

		// for ueu, Pope pages 203-204, it is modified as follows in the 11th
		// and 12th centuries:
		// regularly to jø:
		// jø: > ø: after dzh
		// shift to jø also blocked by preceding labial, velar, or labiodental
		// consonants
		Alteration levelUeu1 = new Alteration(parseCandRestricts("-labial,-labiodental,-velar"), null,
				includeStressedVariants(ipaParse.parseSegList("u̯̯eu̯̯")),
				includeStressedVariants(ipaParse.parseSegList("j,øː")));
		LOF.addRule(levelUeu1);
		Alteration levelUeu2 = new Alteration(parseCandRestricts("+postalveolar,+voiced"), null,
				includeStressedVariants(ipaParse.parseSegList("j,ø")),
				includeStressedVariants(ipaParse.parseSegList("ø")));
		LOF.addRule(levelUeu2);
		Alteration levelUeu3 = new Alteration(includeStressedVariants(ipaParse.parseSegList("u̯̯eu̯̯")),
				includeStressedVariants(ipaParse.parseSegList("øː")));
		LOF.addRule(levelUeu3);

		// open tonic, only tonic, e before l,r, occurs in 12th century-- Pope
		// page 186 section 493
		Alteration openEBeforeLiquids = new Alteration(null, parseCandRestricts("+liquid,+consonant"),
				ipaParse.parseSegList("e`;e`ː"), ipaParse.parseSegList("ɛ`;ɛ`ː"));
		LOF.addRule(openEBeforeLiquids);

		// raising of ae to e-- note its descendants were not affected by the
		// rule above: mare>maer>mer does not ultimately descend to *mar
		Alteration raiseAe = new Alteration(includeStressedVariants(ipaParse.parseSegList("æ;æː")),
				includeStressedVariants(ipaParse.parseSegList("e;eː")));
		LOF.addRule(raiseAe);

		// Pope page 118 section 273 : drop schwa if both after n,r,l and before
		// other cons (then simplify resulting cluster)
		Alteration dropSchwaPostCoronalSonorant = new Alteration(parseCandRestricts("+coronal,+liquid,+nasalstop"),
				parseCandRestricts("+consonant,-sonorant"), ipaParse.parseSegList("ə,ə'"), nonEntityList(2));
		LOF.addRule(dropSchwaPostCoronalSonorant);

		Alteration simplifyNewClusters1 = new Alteration(null, parseCandRestricts("+consonant"),
				ipaParse.parseSegList("r,r"), ipaParse.parseSegList("r"));
		LOF.addRule(simplifyNewClusters1);
		// n before liquid to r
		Alteration simplifyNewClusters2 = new Alteration(null, parseCandRestricts("+liquid"),
				ipaParse.parseSegList("" + nDentCh), ipaParse.parseSegList("r"));
		LOF.addRule(simplifyNewClusters2);

		return LOF;

	}

	public LangLexicon toMiddleFrench(LangLexicon oF) // circa 1500
	{

		// include also : shifts among nasal diphthongs
		// - nasal consonant loss?
		// - r > z "buzzing"? Pope chapter 9 section 4

		LangLexicon MF = new LangLexicon(oF.getLexiconClone());

		// THE BEGINNING OF LIAISON IN THIS ERA (and enchainement, etc.) --
		// likely already a feature due to Celtic influence and weakness of word
		// boundaries
		// the only thing blocking the phonological effects of this, however,
		// was the Germanic heavy stress accent-- which is lost by the beginning
		// of the Middle French period

		// this era is also marked by increasing differentiation of the Latinate
		// speech of the upper class
		// which still used Latin in written communication
		// ... and the speech of the peasantry which showed many lenition and
		// enchainement characteristics still

		List<Phone> midFrenchShortVowels = ipaParse.parseSegment("a,ɛ,e,i,ø,y,ɑ,ɔ,o,u"); // although
																							// I
																							// believe
																							// some
																							// of
																							// these
																							// --
																							// i.e.
																							// u,
																							// ɑ,
																							// ø...
		midFrenchShortVowels.addAll(mapFeatShift(ipaParse.parseSegment("a,e,i,ø,y,ɔ,u"), "nasal", 1));

		/**
		 * various things happen to r in "weak" positions (intervocalic,
		 * unsupported final, VOILA LENITION) in various dialects and many of
		 * these do have degrees of influence in the Parisian dialect as they
		 * slowly climbed their way up the social ladder and these weak boundary
		 * characteristics slowly found there way into upper class dialects,
		 * starting from the lower class dialects, over time example : chaire >
		 * chaise, due to the "buzzing" of r in intervocalic position. But this
		 * did not become canon for all words I am only dealing with the ones
		 * that DID attain consistency across the lexicon
		 * 
		 * for a full summary see Pope chapter 9 part 4 -- i.e. pages 156-159
		 *
		 * not including at the moment : r > null before s,z,l, the alveolars
		 * this "provoked a reaction", i.e. the insertion of r where it did not
		 * exist previously, especially in some placenames : //
		 *
		 * not included at the moment : r > z intervocalically i.e.
		 * chaire>chaise, bericles > besicles (intervocalic s has become z at
		 * this time) also "provoked a reaction
		 * 
		 * will be included later : DELETION of r final, as ultimately changed
		 * the ending of almost every verb for this one as Pope notes, the
		 * opposition of grammarians was "vehement and persistent" but
		 * ultimately they couldn't stop it as it gained more and more ground
		 */

		// Pope page 110-111 : sections 244-245
		// effacement of schwa next to a vowel, lengthening that vowel if it is
		// not already long
		Alteration elongateNextToSchwa1 = new Alteration(parseCandRestricts("+schwa"), null,
				includeStressedVariants(capsulate(midFrenchShortVowels)),
				includeStressedVariants(capsulate(mapFeatShift(midFrenchShortVowels, "length", 2))));
		MF.addRule(elongateNextToSchwa1);
		Alteration elongateNextToSchwa2 = new Alteration(null, parseCandRestricts("+schwa"),
				includeStressedVariants(capsulate(midFrenchShortVowels)),
				includeStressedVariants(capsulate(mapFeatShift(midFrenchShortVowels, "length", 2))));
		MF.addRule(elongateNextToSchwa2);

		// au to o; oeu to ø; ei > ɛ ; nasal variant > nasal e
		Alteration levelPhthongs = new Alteration(
				includeStressedVariants(ipaParse.parseSegList("œu̯̯;au̯̯;ə̯̯au̯;ei̯̯")),
				includeStressedVariants(ipaParse.parseSegList("øː;oː;ə̯̯o;ɛː")));
		MF.addRule(levelPhthongs);
		Alteration levelNasalPhthongs = new Alteration(
				includeStressedVariants(capsulate(mapFeatShift(ipaParse.parseSegment("ei̯̯,ai̯̯"), "nasal", 1))),
				includeStressedVariants(ipaParse.parseSegList("ẽː;ẽː")));
		MF.addRule(levelNasalPhthongs);

		// Pope: page 82-3, section 171 part 3
		// voicing of labiodental and alveolar fricatives intervocalically and
		// unsupported finally
		Alteration thirdLenition = new Alteration(parseCandRestricts("+sonorant,-liquid"), // handle
																							// intervocalically
				parseCandRestricts("+sonorant,-liquid"), ipaParse.parseSegList("f,s"), ipaParse.parseSegList("v,z"));
		MF.addRule(thirdLenition);

		// note :
		// Pope page 210 section 509: raise ɔ to o before v in Middle French,
		// and also before s,z -- i.e. alveolar or labiodental fricative except
		// for f
		// i.e. so this effect happened before roughly half of the Middle French
		// fricatives: s|z|v yes, f|sh|zh no
		// actually we will never know if s was part of hte context as in all
		// hte relevant places it is either deleted causing prior lengthening
		// (and indeed closing) of o,
		/// .... or it is now been lenited to z
		Alteration blockF = new Alteration(ipaParse.parseSegList("f"), ipaParse.parseSegList("q"));
		MF.addRule(blockF);
		Alteration raiseRoundBeforeSomeFrics = new Alteration(null,
				parseCandRestricts("+fricative,-postalveolar,+voiced"),
				includeStressedVariants(ipaParse.parseSegList("ɔ;ɔː")),
				includeStressedVariants(ipaParse.parseSegList("o;oː")));
		MF.addRule(raiseRoundBeforeSomeFrics);
		Alteration unblockF = new Alteration(ipaParse.parseSegList("q"), ipaParse.parseSegList("f"));
		MF.addRule(unblockF);

		// e varieties open before palatal l -- Pope p186 sections 494
		// if I'm interpreting Pope right, this only occurs in tonic (i.e. fully
		// stressed) positions
		Alteration openEPreLateral = new Alteration(null, parseCandRestricts("+lateral"),
				ipaParse.parseSegList("e`;e`ː"), ipaParse.parseSegList("ɛ`;ɛ`ː"));
		MF.addRule(openEPreLateral);

		// open e > a before r -- Pope p187-8 sections 496-498
		// note however section 498-- ultimate results are inconsistent.
		// for the purposes of programming, since in my interpretation of hte
		// reading at least,
		// ..., this originated as a regular shift, it is treated as such
		// and the places where er>ar reverted to er are treated as
		// non-Neogrammarian stylistic restoration
		// at one point I thought this occurred only in countertonic positions
		// but I can't seem to find this
		// additionally, although Pope argues that this affected oi>we (>wa) as
		// well
		// ..., see also Mireille, "Histoire de la langue francaise", pp214 and
		// 223.
		// a much more recent source which argues that oi only passed to we much
		// later, in early Modern French.
		// note: this should also come after the effacement of final r, because
		// french verbs don't end in r
		// TODO this shift is causing problems with words with ɛ where it was
		// originally open a (ex. MARE > mer, not *mar)
		// note that Pope actually holds that ae did not merge with open e
		/*
		 * Alteration loweringPreRhotic = new Alteration (null,
		 * parseCandRestricts("+rhotic"),
		 * includeStressedVariants(ipaParse.parseSegList("ɛ;ɛː;w,ɛ;w,ɛː")),
		 * includeStressedVariants(ipaParse.parseSegList("a;aː;w,a;w,aː")));
		 * MF.addRule(loweringPreRhotic);
		 */
		// TODO note that above is removed for now due to its inconsistency

		// lowering countertonic u and y to oh and oe before r -- Pope page 188
		// sectiosn 499
		Alteration lowerRoundVowelsBeforeR = new Alteration(null, parseCandRestricts("+rhotic"),
				ipaParse.parseSegList("u';y';u'ː;y'ː"), ipaParse.parseSegList("ɔ';œ';ɔ'ː;œ'ː;"));
		MF.addRule(lowerRoundVowelsBeforeR);

		// ɔu diphthong > u ??
		int[][] uStruct = new int[][] { { 40, 40, 1 } };
		List<Phone> mRFDTargs = new ArrayList<Phone>();
		mRFDTargs.add(new Phthong(new Vowel(50, 70, false), null, uStruct)); // au
		mRFDTargs.add(new Phthong(new Vowel(40, 70, false), null, uStruct)); // ɑu
		mRFDTargs.add(new Phthong(new Vowel(40, 60, true), null, uStruct)); // ɔu

		Alteration monophthongizeRoundFallingDiphths = new Alteration(capsulate(mRFDTargs),
				ipaParse.parseSegList("ɔː;oː;uː"));
		MF.addRule(monophthongizeRoundFallingDiphths);

		// /ũ/ > /õ/ > /ɔ̃/. -- remember to use proper symbols, i.e. not these,
		// but the ones from the file.
		Alteration openNasalU = new Alteration(
				includeStressedVariants(capsulate(mapFeatShift(ipaParse.parseSegment("u,uː"), "nasal", 1))),
				includeStressedVariants(capsulate(mapFeatShift(ipaParse.parseSegment("ɔ,ɔː"), "nasal", 1))));
		MF.addRule(openNasalU);

		// after nasal u-lowering , denasalization of nasal vowels if preceding
		// a nasal stop which is in turn followed by a vowel
		// TODO check to make sure all nasal vowels are hit here!
		Alteration denasalizeVowelsPreNasCons = new Alteration(null,
				parseCandRestricts("+consonant,+nasal;+sonorant,-liquid,-trill,-tap"),
				includeStressedVariants(
						capsulate(mapFeatShift(ipaParse.parseSegment("i,iː,y,yː,e,eː,ɛ,ɛː,a,aː,ɔ,ɔː"), "nasal", 1))), // TODO
																														// make
																														// sure
																														// I
																														// got
																														// the
																														// list
																														// of
																														// possible
																														// nasal
																														// vowels
																														// correct.
				includeStressedVariants(capsulate(ipaParse.parseSegment("i,iː,y,yː,e,eː,ɛ,ɛː,a,aː,ɔ,ɔː"))));
		MF.addRule(denasalizeVowelsPreNasCons);

		// lengthen vowels before the nasals that are about to be deleted...
		// i.e. all the ones that haven't already been denasalized
		Alteration lengthenRemainingNasals = new Alteration(
				includeStressedVariants(ipaParse.parseSegList("ĩ;ẽ;ɛ̃;ã;ỹ;ɔ̃")),
				includeStressedVariants(ipaParse.parseSegList("ĩː;ẽː;ɛ̃ː;ãː;ỹː;ɔ̃ː")));
		MF.addRule(lengthenRemainingNasals);

		// ^after denasalizaton of nasal vowels before n,m in open syllables :
		// * * absorption of (closed syllable) nasal stops after nasal vowels
		// TODO decide if eng and eny should be included in this!
		// we don't need to deal with possibility that next is a vowel as this
		// is already handled by the shift above
		Alteration absorbNasalStops = new Alteration(parseCandRestricts("+vowel,+nasal"), null,
				ipaParse.parseSegList("m;" + nDentCh + ";ŋ;ɲ"), nonEntityList(4));
		MF.addRule(absorbNasalStops);

		// oe to y if countertonic ("secondary stressed stressed") -- Pope page
		// 203 section 543
		Alteration counterTonicFrontRoundClosing = new Alteration(ipaParse.parseSegList("ø';ø'ː"),
				ipaParse.parseSegList("y';y'ː"));
		MF.addRule(counterTonicFrontRoundClosing);

		// shifts to reduce hiatus follow -- see Pope p109

		// Pope page 109 section 241
		// consonantalization of countertonic (or atonic?) i, u, y before a
		// lower vowel -- accepted by the 16th century
		Alteration ConsonantalizationPreVowel = new Alteration(null, parseCandRestricts("+vowel"),
				ipaParse.parseSegList("i;i';u;u';y;y'"), ipaParse.parseSegList("j;j;w;w;ɥ;ɥ"));
		MF.addRule(ConsonantalizationPreVowel);

		// Pope page 110 section 242
		// contraction ~ coalescence ("syneresis")
		// of the countertonic vowel with the tonic occurred when...
		// * ... a) the two vowels were homophonous or nearly so.
		// * ... b) when countertonic a stood in hiatus with the tonic vowel
		// the result was always long -- results for each pair described on page
		// 110

		// before merging of vowels, schwa becomes a before a or a-like vowels,
		// so that it can merge just the way a does
		Alteration openSchwaPreA = new Alteration(null, parseCandRestricts("+vowel,+open,-midopen"),
				ipaParse.parseSegList("ə;ə'"), ipaParse.parseSegList("a',a'")); // we
																				// make
																				// it
																				// countertonic
																				// because
																				// the
																				// merging
																				// shift
																				// only
																				// effects
																				// countertonics
		MF.addRule(openSchwaPreA);

		// type a -- when the countertonic and following tonic are homophonous
		// or nearly homophonous
		// we're assuming, for the sake of convenience, that ɑ is not going to
		// be the countertonic vowel
		// ... nor will any other value that would have been elongated and/or
		// changed in quality due to the loss of following s>h
		// justified on the grounds that its unlikely that such s-affected
		// vowels, due to distributional properties, would end up in hiatus
		// (as otherwise htere wouldn't have been a following s to absorb)
		Alteration mergeHiatusHomophones1 = new Alteration(null, null,
				ipaParse.parseSegList("a',a`;a',a`ː;a',ã`;a',ã`ː;a',ɑ`;a',ɑ`ː;"
						+ "ɛ',ɛ`;ɛ',ɛ`ː;ɛ',ɛ̃`;ɛ',ɛ̃`ː;ɛ',e`;ɛ',e`ː;ɛ',ẽ`;ɛ',ẽ`ː;"
						+ "e',ɛ`;e',ɛ`ː;e',ɛ̃`;e',ɛ̃`ː;e',e`;e',e`ː;e',ẽ`;e',ẽ`ː;" + "i',i`;i',i`ː;i',ĩ`;i',ĩ`ː;"
						+ "ø',œ`;ø',œ`ː;ø',ø`;ø',ø`ː;ø',ø̃`;ø',ø̃`ː;" + "y',y`;y',y`ː;y',ỹ`;y',ỹ`ː;"
						+ "ɔ',ɔ`;ɔ',ɔ`ː;ɔ',ɔ̃`;ɔ',ɔ̃`ː;ɔ',o`;ɔ',o`ː;ɔ',õ`;ɔ',õ`ː;"
						+ "o',ɔ`;o',ɔ`ː;o',ɔ̃`;o',ɔ̃`ː;o',o`;o',o`ː;o',õ`;o',õ`ː;" + "u',u`;u',u`ː;u',ũ`;u',ũ`ː"),
				ipaParse.parseSegList("a`ː;a`ː;ã`ː;ã`ː;a`ː;a`ː;" + "ɛ`ː;ɛ`ː;ɛ̃`ː;ɛ̃`ː;e`ː;e`ː;ẽ`ː;ẽ`ː;"
						+ "ɛ`ː;ɛ`ː;ɛ̃`ː;" + "e`ː;e`ː;ẽ`ː;ẽ`ː;" + "i`ː;i`ː;ĩ`ː;ĩ`ː;" + "œ`ː;œ`ː;ø`ː;ø`ː;ø̃`ː;ø̃`ː;"
						+ "y`ː;y`ː;ỹ`ː;ỹ`ː;" + "ɔ`ː;ɔ`ː;ɔ̃`ː;ɔ̃`ː;o`ː;o`ː;õ`ː;õ`ː;"
						+ "ɔ`ː;ɔ`ː;ɔ̃`ː;ɔ̃`ː;o`ː;o`ː;õ`ː;õ`ː;" + "u`ː;u`ː;ũ`ː;ũ`ː"));
		MF.addRule(mergeHiatusHomophones1);

		// SPECIAL CASE: before dealing iwth the other a + vowel clusters, deal
		// with the case of a+u+n+WORDCODA > a, not u
		// partial reasoning (but wiht different interpretation on my part) --
		// Pope page 110
		// I'm assuming the word coda must follow hte nasal consonant, because
		// while taon is effected, Saone is not .
		Alteration mergeHiatusPhones2 = new Alteration(null, parseCandRestricts("+nasal;+wordcoda"),
				ipaParse.parseSegList("a',ũ`;a',ũ`ː;a',õ`;a',õ`ː;a',ɔ̃`;a',ɔ̃`ː"),
				ipaParse.parseSegList("ã`ː;ã`ː;ã`ː;ã`ː;ã`ː;ã`ː;ã`ː;ã`ː"));
		MF.addRule(mergeHiatusPhones2);

		// now the default treatments as according to Pope page 110, section 242
		// note: assimilates to oral or toher nasal (i.e. not covered above)
		// round vowels (i.e. Saone is /so:n/, aoust is /u:t/ )
		// all in all : merge A in most of the places where it is not next to
		// another A
		Alteration mergeHiatusPhones3 = new Alteration(null, null,
				ipaParse.parseSegList("a',i`;a',i`ː;a',ĩ`;a',ĩ`ː;" + "a',e`;a',e`ː;a',ẽ`;a',ẽ`ː;"
						+ "a',ɛ`;a',ɛ`ː;a',ɛ̃`;a',ɛ̃`ː;" + "a',y`;a',y`ː;a,ỹ`;a',ỹ`ː;"
						+ "a',ø`;a',ø`ː;a',ø̃`;a',ø̃`ː;a',œ`;a',œ`ː;" + "a',u`;a',u`ː;a',ũ`ː;a',ũ`ː;"
						+ "a',o`;a',o`ː;a',õ`;a',õ`ː;" + "a',ɔ`;a',ɔ`ː;a',ɔ̃`;a',ɔ̃`ː"),
				ipaParse.parseSegList("ɛ`ː;ɛ`ː;ẽ`ː;ẽ`ː;" + "ɛ`ː;ɛ`ː;ẽ`ː;ẽ`ː;" + "ɛ`ː;ɛ`ː;ɛ̃`ː;ɛ̃`ː;"
						+ "y`ː;y`ː;ỹ`ː;ỹ`ː;" + "ø`ː;ø`ː;ø̃`ː;ø̃`ː;œ`ː;œ`ː;" + "u`ː;u`ː;ũ`ː;ũ`ː;"
						+ "o`ː;o`ː;õ`ː;õ`ː;" + "ɔ`ː;ɔ`ː;ɔ̃`ː;ɔ̃`ː"));
		MF.addRule(mergeHiatusPhones3);

		// further reductions of atonic vowels in Period 2 -- Pope pages
		// 116-118, also 110-111ish

		// drop schwa in hiatus, without lengthening (only exception is covered
		// above, before the merging)
		// note that schwa is never stressed/tonic, but can be either atonic or
		// countertonic
		// it is also always short
		/// Pope p110 s243-245
		Alteration dropHiatusSchwa = new Alteration(null, parseCandRestricts("+vowel"), ipaParse.parseSegList("ə;ə'"),
				nonEntityList(2));
		MF.addRule(dropHiatusSchwa);

		// some exceptiosn: see Pope p111 s45

		// Pope p111 s247 :
		// countertonic (or atonic) shwa after an oral stop or fric (not
		// affricate)
		// ... and before a liquid r or l ... is deleted
		// likely exceptions not cited in Pope but inferred by me: t-l, d-l and
		// s-r
		// deal with the non-dentals first
		Alteration dropIntSchwa1 = new Alteration(parseCandRestricts("+stop,+fricative,-coronal,-nasal"),
				parseCandRestricts("+liquid"), ipaParse.parseSegList("ə;ə'"), nonEntityList(2));
		MF.addRule(dropIntSchwa1);
		// then s-l only (no s-r)
		Alteration dropIntSchwa2 = new Alteration(ipaParse.parseSegList("s,ə,l;s,ə',l"),
				ipaParse.parseSegList("s,l;s,l"));
		MF.addRule(dropIntSchwa2);
		// and finally the oral dental stops
		Alteration dropIntSchwa3 = new Alteration(parseCandRestricts("+dental,-nasal,+stop"),
				parseCandRestricts("+rhotic"), ipaParse.parseSegList("ə;ə'"), nonEntityList(2));
		MF.addRule(dropIntSchwa3);

		// Pope p117 sections 270: schwa dropped intertonically after preceding
		// vowel
		Alteration dropSchwaIntPostVowel = new Alteration(parseCandRestricts("+vowel"), parseCandRestricts("-wordcoda"),
				ipaParse.parseSegList("ə;ə'"), nonEntityList(2));
		MF.addRule(dropSchwaIntPostVowel);

		// Pope p117 section 271: schwa dropped finally after preceding TONIC
		// vowel
		Alteration dropSchwaFinalPostTonic = new Alteration(parseCandRestricts("+vowel,+stressed"), null,
				ipaParse.parseSegList("ə;ə'"), nonEntityList(2));
		MF.addRule(dropSchwaFinalPostTonic);

		// vowel lengthening in mid french? pages 205- 207 ~~~ ignore for now

		return MF;
	}

	public LangLexicon toEarlyModernFrench(LangLexicon midF) // circa 1700
	{
		LangLexicon EMF = new LangLexicon(midF.getLexiconClone());

		// Pope p200 s538-- əo diphthong becomes simple long o
		Alteration levelSchwaDiphth = new Alteration(includeStressedVariants(ipaParse.parseSegList("ə̯o")),
				includeStressedVariants(ipaParse.parseSegList("oː")));
		EMF.addRule(levelSchwaDiphth);

		// Pope p109 s509 : je > e after palatals or postalveolars
		Alteration dropYodPostPal = new Alteration(parseCandRestricts("+consonant,+palatal,+postalveolar"),
				parseCandRestricts("+vowel,+midclose,+front,-rounded,-nasal") /* e */ , ipaParse.parseSegList("j"),
				nonEntityList(1));
		EMF.addRule(dropYodPostPal);
		// note by analogy also dropped in all verb forms.

		// final -ai to eh only accepted in 17th century -- pope 199 ~~ but in
		// this program it is dealt with earlier.

		// final canonization of r-final buzzing and then effacement: 16th
		// century r final like r elsewhere is buzzed and then increasingly
		// effaced
		// 1625 -- grammarians are still condemning it
		// by 1700 -- not canon and possibly no longer productive in other
		// "weak" positions, but canonized for r-final after certain vowels
		// "Later on in the 17th century, the precepts of the gramarians
		// together with the influence of spelling, Latin, the numerous
		// loanwords and the analogical influences of the infinitive sin -ire
		// and the feminines in -eure led to the restoration iof the
		// pronunciation of fianl r in all positions in the terminations -irl,
		// -uer, -oer and to its retention
		// ... and its retention in monosyllabic words with -er and -ier and
		// (for a time only) in the terminations in polysyllabic woreds in
		// prae-vocalic position
		// tldr: result is that its only effaced in polysyllabic words ending in
		// (high) -er or -ier
		Alteration effaceSomeRFinal = new Alteration(null, parseCandRestricts("+wordcoda"),
				ipaParse.parseSegList("e,r;ɛ,r"), // INTENTIONALLY UNSTRESSED
													// ONLY.
				ipaParse.parseSegList("e;ɛ"));
		EMF.addRule(effaceSomeRFinal);

		// opening of e before r where it was not effaced, in the 16th century
		// source: Pope p187 section 495
		Alteration openEPreRhotic = new Alteration(null, parseCandRestricts("+rhotic"),
				includeStressedVariants(ipaParse.parseSegList("e;eː")),
				includeStressedVariants(ipaParse.parseSegList("ɛ;ɛː")));
		EMF.addRule(openEPreRhotic);

		Alteration openEPostW = new Alteration(parseCandRestricts("+approximant,+velar,+rounded"), null,
				includeStressedVariants(ipaParse.parseSegList("e;eː")),
				includeStressedVariants(ipaParse.parseSegList("ɛ;ɛː")));
		EMF.addRule(openEPostW);

		// Pope page 109 section 239
		// if countertonic we, wɛ (<oi) is before a tonic vowel, a palatal glide
		// j develops between them
		// also ("more variably") after countertonic ɛ and a tonic vowel
		// (typically where there had previously been a /ai/
		// in words of this (second) type, ai had instead leveled to e and
		// remained in hiatus
		Alteration insertYodPostCountertonicEps = new Alteration(null, parseCandRestricts("+vowel,+stressed"),
				ipaParse.parseSegList("ɛ';ɛ'ː"), ipaParse.parseSegList("ɛ',j;ɛ'ː,j"));
		EMF.addRule(insertYodPostCountertonicEps);

		// this must come after yod insertion after epsilon!
		// we > wa -- but not the nasalized variants
		// wait but does this handle open e TODO get more info on the quality of
		// e in this segment.
		Alteration weWa = new Alteration(includeStressedVariants(ipaParse.parseSegList("w,e;w,eː;w,ɛ;w,ɛː")),
				includeStressedVariants(ipaParse.parseSegList("w,a;w,aː;w,a;w,aː")));
		EMF.addRule(weWa);

		// loss of final consonants when standing alone (i.e. except by liaison)
		// the phenomenon did not affect any sonorants.
		// note : all the (oral) non-sonorant consonants that are possible in
		// final position at this time are : p,f,t,s,z,k. All devoiced except
		// for z from plural adjectives
		// no data for any cases of final postalveolars
		// Pope pages 219-223, sections 611-623
		// it seems overall that f and k were generally preserved, while t,z and
		// p weren't
		// s meanwhile, along with z was one of hte first to go, but was
		// successfully reinstated in numerous lemmas (i.e. not plural nominal
		// or adjective forms, where it may have been z to begin with)
		// it seems high vowels may have helped preserve /s/ : dix /dis/, tous
		// /tus/ (noun)
		Alteration effaceFinalConsonants1 = new Alteration(null, parseCandRestricts("+wordcoda"),
				ipaParse.parseSegList("p;" + tDentCh + ";z"), nonEntityList(3));
		EMF.addRule(effaceFinalConsonants1);
		Alteration effaceFinalConsonants2 = new Alteration(parseCandRestricts("-close"),
				parseCandRestricts("+wordcoda"), ipaParse.parseSegList("s"), nonEntityList(1));
		EMF.addRule(effaceFinalConsonants2);

		// r becomes uvular trill
		Alteration uvularizeR = new Alteration(ipaParse.parseSegList("r"), ipaParse.parseSegList("ʀ"));
		EMF.addRule(uvularizeR);

		// as per Pope section 585(?) -- differentiation of ø and oe happens at
		// this time, in the late sixteenth century
		// ultimately: o/ is hte only one that can be long one usually, and also
		// comes before z,t and wordcoda, while oe predominates elsewhere
		// first merge before controlling the diffferentiation .
		Alteration mergeFirst = new Alteration(includeStressedVariants(ipaParse.parseSegList("œ;œː")),
				includeStressedVariants(ipaParse.parseSegList("ø;øː")));
		EMF.addRule(mergeFirst);
		Alteration nonFinalShortsToOe = new Alteration(null, parseCandRestricts("-wordcoda"),
				includeStressedVariants(ipaParse.parseSegList("ø")),
				includeStressedVariants(ipaParse.parseSegList("œ")));
		EMF.addRule(nonFinalShortsToOe);
		Alteration closeBeforeSomeCoronals1 = new Alteration(null, parseCandRestricts("+alveolar,+fricative,+voiced"),
				includeStressedVariants(ipaParse.parseSegList("ø")),
				includeStressedVariants(ipaParse.parseSegList("œ")));
		EMF.addRule(closeBeforeSomeCoronals1);
		Alteration closeBeforeSomeCoronals2 = new Alteration(null, parseCandRestricts("+dental,-nasal,+stop,-voiced"),
				includeStressedVariants(ipaParse.parseSegList("ø")),
				includeStressedVariants(ipaParse.parseSegList("œ")));
		EMF.addRule(closeBeforeSomeCoronals2);

		return EMF;
	}

	public LangLexicon toModernFrench(LangLexicon earlyMF) // circa 1930
	{
		LangLexicon modF = new LangLexicon(earlyMF.getLexiconClone());

		// I believe(?) that at this point all diphthongs have been lost.
		// loss of phonemic vowel length -- all long vowels become short
		// (technically, PHONETIC, though not PHONEMIC, length differences
		// remain)
		List<Phone> longVowels = ipaParse
				.parseSegment("aː,ãː,ɛː,ɛ̃ː,eː,ẽː,iː,ĩː," + "œː,øː,ø̃`ː,yː,ỹː,ɑː,ɔː,ɔ̃ː,oː,õː,uː,ũː");
		Alteration endPhonemicLength = new Alteration(includeStressedVariants(capsulate(longVowels)),
				includeStressedVariants(capsulate(mapFeatShift(longVowels, "length", 1))));
		modF.addRule(endPhonemicLength);

		// but now vowels are lengthened pre r -- TODO find the original source
		// for this? Until then, no implementation

		// nasal i to nasal ɛ or even nasal ash ; and nasal y to nasal
		Alteration openNasalIY = new Alteration(
				includeStressedVariants(capsulate(mapFeatShift(ipaParse.parseSegment("i,iː,y,yː"), "nasal", 1))),
				includeStressedVariants(capsulate(mapFeatShift(ipaParse.parseSegment("ɛ,ɛː,œ,œː"), "nasal", 1))));
		modF.addRule(openNasalIY);

		// affected nasal diphthongs too : as /ĩẽ/ > /jẽ/ > /jɛ̃/ (bien /bjɛ̃/
		// "well" < bene);// this one is already done earlier
		// /ỹĩ/ > /ɥĩ/ > /ɥɛ̃/, (juin /ʒɥɛ̃/ "June" < iv̄nivm); also covered
		// elsewhere
		// /õĩ/ > /wẽ/ > /wɛ̃/, (coin /kwɛ̃/ "corner" < cvnevm). also covered
		// elsewhere
		// Also, /ãĩ/ > /ɛ̃/, (pain /pɛ̃/ "bread" < pānem); /ẽĩ/ > /ɛ̃/, (plein
		// /plɛ̃/ "full" < plēnvm). and these are currently covered elsewhere
		// too.

		// nasal a to nasal ɑ
		Alteration backNasalA = new Alteration(
				includeStressedVariants(capsulate(mapFeatShift(ipaParse.parseSegment("a,aː"), "nasal", 1))),
				includeStressedVariants(capsulate(mapFeatShift(ipaParse.parseSegment("ɑ,ɑː"), "nasal", 1))));
		modF.addRule(backNasalA);

		// lambda > j (loss of mouille)
		Alteration mouilleLoss = new Alteration(ipaParse.parseSegList("ʎ"), ipaParse.parseSegList("j"));
		modF.addRule(mouilleLoss);

		List<List<Phone>> schwaCaps = ipaParse.parseSegList("ə");

		// loss of final schwa except in monosyllabic words and where a
		// three-consonant cluster would be produced
		// my addition: or any other impermissible cluster
		// for convenience : first we remove it, then we readd it where it is
		// still needed
		Alteration deleteFinalSchwa = new Alteration(null, parseCandRestricts("+wordcoda"), ipaParse.parseSegList("ə"),
				nonEntityList(1));
		modF.addRule(deleteFinalSchwa);

		// readd final schwa if a sequence of three consonants is produced
		Alteration reviveFinalSchwa1 = new Alteration(parseCandRestricts("+consonant;+consonant;+consonant"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), schwaCaps);
		modF.addRule(reviveFinalSchwa1);

		// illegal : 2 consonant cluster ending in a lateral, rhotic or nasal
		// stop that doesn't start with a glide (there are none ending in a
		// glide in French)
		Alteration reviveFinalSchwa2 = new Alteration( // revive for l
				parseCandRestricts("+consonant,-approximant;+consonant,+lateral"), parseCandRestricts("+wordcoda"),
				nonEntityList(1), schwaCaps);
		modF.addRule(reviveFinalSchwa2);

		Alteration reviveFinalSchwa3 = new Alteration( // revive for r --
														// technically uvular
														// trill or fricative at
														// this point
				parseCandRestricts("+consonant,-approximant;+consonant,+rhotic,+uvular"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), schwaCaps);
		modF.addRule(reviveFinalSchwa3);

		Alteration reviveFinalSchwa4 = new Alteration( // revive for the nasals
														// m and n
				parseCandRestricts("+consonant,-approximant;+consonant,+nasal"), parseCandRestricts("+wordcoda"),
				nonEntityList(1), schwaCaps);
		modF.addRule(reviveFinalSchwa4);

		// illegal : 2 consonant cluster ending in an occlusive that isn't
		// dental/alveolar, unless the first element is a sibilant
		Alteration reviveFinalSchwa5 = new Alteration(
				parseCandRestricts("+consonant,-sibilant;+consonant,-dental,-alveolar"),
				parseCandRestricts("+wordcoda"), nonEntityList(1), schwaCaps);
		modF.addRule(reviveFinalSchwa5);

		// loss of schwa intervocalically unless a sequence of three vowels
		// would be produced
		// we assume at this point that schwas next to vowels have been somehow
		// absorbed (as they all should be, I think?)

		// make dummy vowel to protect initial schwas -- choose ɘ
		List<List<Phone>> guardSchwa = ipaParse.parseSegList("ɘ");
		Alteration guardInitSchwa1 = new Alteration(parseCandRestricts("+wordonset;+consonant"), null, schwaCaps,
				guardSchwa);
		modF.addRule(guardInitSchwa1);

		Alteration deleteInternalSchwa1 = new Alteration(parseCandRestricts("-consonant;+consonant"),
				parseCandRestricts("+consonant;-consonant"), schwaCaps, nonEntityList(1));
		modF.addRule(deleteInternalSchwa1);

		Alteration restoreFromDummy = new Alteration(guardSchwa, schwaCaps);
		modF.addRule(restoreFromDummy);

		// uvular trill becomes voiced uvular fricative
		Alteration toUvularFric = new Alteration(ipaParse.parseSegList("ʀ"), ipaParse.parseSegList("ʁ"));
		modF.addRule(toUvularFric);

		return modF;

	}

	public static void printPhonForms(Collection<WordPhon> wordList) {
		for (WordPhon word : wordList)
			System.out.print(word + "\t");
		System.out.print("\n");
	}

	/**
	 * parseCandRestricts -- auxiliary method in many places here
	 * 
	 * @param CRs
	 *            String of CandRestrictPhones. Different CandRestrictPhones
	 *            separated by semicolons. Different Restrictions within them
	 *            separated by commas VERY IMPORTANT THAT SEMICOLONS AND COMMAS
	 *            ARE USED PROPERLY
	 * @return
	 */
	public List<CandRestrictPhone> parseCandRestricts(String CRstr) {
		String dummy = (new String(CRstr)).replaceAll(" ", "");
		if (dummy.equals(""))
			return new ArrayList<CandRestrictPhone>();

		List<CandRestrictPhone> output = new ArrayList<CandRestrictPhone>();
		String[] CRs = dummy.split(";");
		for (int c = 0; c < CRs.length; c++)
			output.add(new CandRestrictPhone(CRs[c]));

		return output;
	}

	/**
	 * auxiliary method for purposes of attaining compatibility with the
	 * constraints of the Alteration constructor
	 * 
	 * @param phList
	 *            plain phone list we are trying to make compatible .
	 * @return
	 */
	public List<List<Phone>> capsulate(List<Phone> phList) {
		List<List<Phone>> output = new ArrayList<List<Phone>>();

		for (Phone ph : phList) {
			List<Phone> capsule = new ArrayList<Phone>();
			capsule.add(ph);
			output.add(capsule);
		}
		return output;
	}

	/**
	 * appendToAll -- auxiliary method to append a phone to each segment in a
	 * list of phone segments
	 * 
	 * @param segList
	 *            - List<List<Phone>> list of segments (phone sequences) to
	 *            append onto
	 * @param ph
	 *            -- the phone we are appending
	 * @param atBack
	 *            -- if true, we append at the back, otherwise we append at the
	 *            front
	 * @return a new list wiht each of hte segments of seglist with ph appended
	 *         onto them
	 */
	public List<List<Phone>> appendToAll(List<List<Phone>> segList, Phone ph, boolean atBack) {
		List<List<Phone>> output = new ArrayList<List<Phone>>();
		for (List<Phone> seg : segList)
			output.add(new ArrayList<Phone>(seg)); // to avoid destructive
													// programming.
		if (atBack)
			for (List<Phone> seg : output)
				seg.add(ph);
		else
			for (List<Phone> seg : output)
				seg.add(0, ph);
		return output;
	}

	/**
	 * mapFeatureShift -- helpful auxiliary method, for coding-side efficiency,
	 * to map shift of feature on set of phones, producing a new set of phones
	 * with the newly assigned value for that feature NOTE : we can't use this
	 * on rise or fall.
	 * 
	 * @param inpPhones
	 *            -- List<Phone> of phones of which to produce corresponding
	 *            shifted forms
	 * @param feature
	 *            -- the feature we are manipulating ("manner", "coArtic",
	 *            "rounded", etc)
	 * @param newValue
	 *            -- new value assigned to the feature (for booleans, 1 -- true
	 *            and 0 -- false)
	 * @precondition possVars.contains(feature)
	 * @return
	 */
	public List<Phone> mapFeatShift(List<Phone> inpPhones, String feature, int newValue) {
		assert possVars
				.contains(feature) : "Error : feature that is not a possible variable entered for mapFeatureShift()";

		List<Phone> output = new ArrayList<Phone>(); // "dummy variable" at
														// first

		// each index of output is filled with a clone of the corresponding
		// input phone
		// to avoid side effects.
		for (Phone iP : inpPhones) {
			String klase = iP.getType();
			switch (klase) {
			case "Consonant":
				output.add(new Consonant(iP));
				break;
			case "Vowel":
				output.add(new Vowel(iP));
				break;
			case "Phthong":
				output.add(new Phthong(iP));
				break;
			default:
				System.out.println("Uh oh, invalid type in mapFeatShift");
				throw new Error();
			}
		}

		switch (feature) {
		case "place":
			for (Phone outPh : output)
				outPh.setPlace(newValue);
			return output;
		case "manner":
			for (Phone outPh : output)
				outPh.setManner(newValue);
			return output;
		case "coArtic":
			for (Phone outPh : output)
				outPh.setCoArtic(newValue);
			return output;
		case "rounded":
			for (Phone outPh : output)
				outPh.setRounded(newValue == 1);
			return output;
		case "nasal":
			for (Phone outPh : output)
				outPh.setNasal(newValue == 1);
			return output;
		case "lateral":
			for (Phone outPh : output)
				outPh.setLateral(newValue == 1);
			return output;
		case "rhotic":
			for (Phone outPh : output)
				outPh.setRhotic(newValue == 1);
			return output;
		case "voiced":
			for (Phone outPh : output)
				outPh.setVoiced(newValue == 1);
			return output;
		case "aspirated":
			for (Phone outPh : output)
				outPh.setAspirated(newValue == 1);
			return output;
		case "stress":
			for (Phone outPh : output)
				outPh.setStress(newValue);
			return output;
		case "length":
			for (Phone outPh : output)
				outPh.setLength(newValue);
			return output;
		default: // note this is actually the case of rise and fall, as all the
					// others are hit with the assertion
			System.out.println("Error : tried to map to unsupported or illegitimate feature");
			return null;
		}
	}

	/**
	 * auxiliary function -- call in case we have a single, non-entity
	 * destination corresponding to a singular target
	 * 
	 * @param n
	 *            : how many non entities (i.e. empty List<Phone>()) we want
	 * @return a List<List<Phone>> containing a single, empty List<Phone>
	 */
	public static List<List<Phone>> nonEntityList(int n) {
		List<List<Phone>> output = new ArrayList<List<Phone>>();
		int i = 0;
		while (i < n) {
			output.add(new ArrayList<Phone>());
			i++;
		}
		return output;
	}

	// TODO this
	/**
	 * auxiliary method of to add all stressed variants of phones to a list of
	 * phones note : will not work on segments with more than one vowel for
	 * these, it will add stressed variants for only the first vowel
	 * 
	 * @return
	 */
	public static List<List<Phone>> includeStressedVariants(List<List<Phone>> unstressedPhones) {
		List<List<Phone>> output = new ArrayList<List<Phone>>(unstressedPhones);

		int[] stressVals = new int[] { 1, -1 };

		for (int c = 0; c < stressVals.length; c++) {
			for (List<Phone> uSeg : unstressedPhones) {
				List<Phone> newSeg = new ArrayList<Phone>();
				int i = 0;
				boolean foundVow = false;
				while (!foundVow && i < uSeg.size()) {
					Phone uP = uSeg.get(i);
					String type = uP.getType();
					if (type.equals("Vowel")) {
						foundVow = true;
						newSeg.add(new Vowel(uP));
						newSeg.get(newSeg.size() - 1).setStress(stressVals[c]); // set
																				// this
																				// newest
																				// vowel's
																				// stress
																				// to
																				// either
																				// 1
																				// or
																				// -1,
																				// depending
																				// on
																				// c
					} else if (type.equals("Phthong")) {
						foundVow = true;
						newSeg.add(new Phthong(uP));
						newSeg.get(newSeg.size() - 1).setStress(stressVals[c]);
					} else {
						newSeg.add(new Consonant(uP));
					}
					i++;
				}
				output.add(newSeg);
			}
		}

		return output;
	}

}
