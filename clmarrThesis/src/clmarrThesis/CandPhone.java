package clmarrThesis;

import java.util.*;

//ABROGATED! 

public class CandPhone {

	private List<String> types; // allowable types
	private List<Integer> places; // allowable places
	private List<Integer> manners; // allowable manners
	private List<Integer> coArtics; // allowable coArtics
	private int roundness; // 1 -- must be round. 0 -- doesn't matter. -1 --
							// must be unround
	private int nasality, lateralness, rhoticity, voicedness, aspiration, stress; // same
																					// dichotomy
																					// as
																					// above.
	private int lengths;
	/*
	 * for lengths: 0: length must equal 1 1: length can be 1 or 2 2 length can
	 * be anything 3 lenght must be 2 or 3 4 length must be 3
	 */
	private String rises, falls;
	/*
	 * rises and falls may each be either: * (a) "!" -- meaning there must be no
	 * rise/fall * (b) each is a list of all possible rises/falls, separated by
	 * commas hopefully each is of 2 or fewer characters Alternatively, either
	 * rise or fall for each slot could be : * "V" -- there must be a vowel
	 * here, but it can be any vowel. * "@" -- any vowel/semivowel allowed here,
	 * including no semivowel * "!" -- no vowel/semivowel allowed in this spot
	 * (note one should never have a rise of {"V","!"} or a fall of the reverse
	 * as that is impossible, * * given that rises and falls are constructed
	 * growing out from the position of the prominent vowel) * otherwise it is a
	 * string of the IPA of all possible vowels in that slot. * we use the IPA
	 * translator in the PhoneStructureTranslator class to "translate" this,
	 * using our instance, IPAhelp.
	 */

	// TODO currently no way to handle the nonprominent parts of phthongs here
	// as of yet.
	// But we probably won't need to.

	// for when all are allowed:
	private final List<String> maxTypes = Arrays.asList(
			new String[] { "Consonant", "Vowel", "Phthong", "PseudoPhone", "word onset", "word coda", "syll break" });
	private final List<Integer> maxPlaces = Arrays
			.asList(new Integer[] { 10, 20, 30, 40, 42, 45, 48, 50, 60, 70, 80, 85, 90, 100 });
	// private final List<Integer> maxVowelPlaces = Arrays.asList(new
	// Integer[]{40,42,45,48,50});
	// private final List<Integer> maxConsonantPlaces = Arrays.asList(new
	// Integer[]
	// {10,20,30,40,50,60,70,80,85,90,100});
	private final List<Integer> maxManners = Arrays
			.asList(new Integer[] { 10, 13, 15, 23, 25, 27, 28, 30, 40, 45, 50, 55, 60, 65, 70 });
	// private final List<Integer> maxVowelManners = Arrays.asList(new
	// Integer[]{40,45,50,55,60,65,70});
	// private final List<Integer> maxConsonantManners = Arrays.asList(new
	// Integer[]{10,13,15,23,25,27,28,30});
	// maxConsonantPlaces doubles for maxCoArtics (although technically it
	// shouldn't have 85)
	// default "max" for all the boolean proxies is that it doesn't matter--
	// i.e. 0.
	// private final List<Integer> maxLengths = Arrays.asList(new Integer[]
	// {1,2,3});

	/*
	 * ABROGATED: private final List<String> maxRiseFall = Arrays.asList(new
	 * String[] {"@","@"}); private final List<String> noRiseFall =
	 * Arrays.asList(new String[] {"!","!"});
	 */

	private final String maxRiseFall = "@";
	private final String noRiseFall = "!";

	private final List<String> paramList = Arrays.asList(new String[] { "type", "place", "manner", "coArtic",
			"roundness", "nasality", "lateralness", "rhoticity", "voicedness", "aspiration", "stress", "length" });

	// Default constructor: make everything allowed.
	public CandPhone() {
		types = new ArrayList<String>(maxTypes);
		places = new ArrayList<Integer>(maxPlaces);
		manners = new ArrayList<Integer>(maxManners);
		coArtics = new ArrayList<Integer>(maxPlaces);
		roundness = 0;
		nasality = 0;
		lateralness = 0;
		rhoticity = 0;
		voicedness = 0;
		aspiration = 0;
		stress = 0;
		lengths = 1;
		rises = new String(maxRiseFall);
		falls = new String(maxRiseFall);
	}

	public CandPhone(String newTypes) {
		if (newTypes.contains(","))
			types = Arrays.asList(newTypes.split(","));
		else {
			types = new ArrayList<String>();
			types.add(newTypes); // i.e. there is just one type
			assert maxTypes.contains(newTypes) : "Type entry not recognized for CandPhone";
		}
	}

	// TODO more constructors
	// TODO constructors that include all except for certain phones
	// TODO constructors that EXclude all except for certain phones.

	// accessors
	public List<String> getTypes() {
		return new ArrayList<String>(types);
	}

	public List<Integer> getPlaces() {
		return new ArrayList<Integer>(places);
	}

	public List<Integer> getManners() {
		return new ArrayList<Integer>(manners);
	}

	public List<Integer> getCoArtics() {
		return new ArrayList<Integer>(coArtics);
	}

	public int getRoundness() {
		return roundness;
	}

	public int getNasality() {
		return nasality;
	}

	public int getLateralness() {
		return lateralness;
	}

	public int getRhoticity() {
		return rhoticity;
	}

	public int getVoicedness() {
		return voicedness;
	}

	public int getAspiration() {
		return aspiration;
	}

	public int getStress() {
		return stress;
	}

	public int getLengths() {
		return lengths;
	}

	public String getRises() {
		return rises;
	}

	public String getFalls() {
		return falls;
	}

	// mutators
	public void setTypes(List<String> newTypes) {
		this.types = new ArrayList<String>(newTypes);
		if (!types.contains("Phthong")) {
			rises = new String(noRiseFall);
			falls = new String(noRiseFall);
		}

	}

	public void setPlaces(List<Integer> newPlaces) {
		this.places = new ArrayList<Integer>(newPlaces);
	}

	public void setManners(List<Integer> newManners) {
		this.manners = new ArrayList<Integer>(newManners);
	}

	public void setCoArtics(List<Integer> newCoArtics) {
		this.coArtics = new ArrayList<Integer>(newCoArtics);
	}

	public void setRoundness(int newRoundness) {
		this.roundness = newRoundness;
	}

	public void setNasality(int newNasality) {
		this.nasality = newNasality;
	}

	public void setLateralness(int newLateralness) {
		this.lateralness = newLateralness;
	}

	public void setRhoticity(int newRhoticity) {
		this.rhoticity = newRhoticity;
	}

	public void setVoicedness(int newVoicedness) {
		this.voicedness = newVoicedness;
	}

	public void setAspiration(int newAspiration) {
		this.aspiration = newAspiration;
	}

	public void setStress(int newStress) {
		this.stress = newStress;
	}

	public void setLengths(int newLengths) {
		this.lengths = newLengths;
	}

	public void setRises(String newRises) {
		this.rises = newRises;
	}

	public void setFalls(String newFalls) {
		this.falls = newFalls;
	}

	public boolean compare(Phonic that) {
		String thatType = that.getType();

		// quickly end with returning "false" if the type/class is wrong:
		if (!types.contains(thatType))
			return false;

		// case that we are dealing with a PseudoPhone
		if (that instanceof PseudoPhone)
			return (thatType != "default"); // since we already know that types
											// contains thatType

		// from here on we can assume a proper Phone is in play.
		if (!places.contains(that.getPlace()))
			return false;
		if (!manners.contains(that.getManner()))
			return false;
		if (!coArtics.contains(that.getCoArtic()))
			return false;
		if (roundness == (that.isRounded() ? -1 : 1))
			return false; // i.e. can't be 1 and false, or -1 and true
		if (nasality == (that.isNasal() ? -1 : 1))
			return false; // same as above, for the next six lines
		if (lateralness == (that.isLateral() ? -1 : 1))
			return false;
		if (rhoticity == (that.isRhotic() ? -1 : 1))
			return false;
		if (voicedness == (that.isVoiced() ? -1 : 1))
			return false;
		if (aspiration == (that.isAspirated() ? -1 : 1))
			return false;
		if (stress == (that.isStressed() ? -1 : 1))
			return false;
		if (Math.abs(that.getLength() - lengths) > 1)
			return false;

		if (thatType == "Phthong") // deal with rises and falls only in the case
									// that we are handling a Phthong
		{
			// first check rise
			if (rises.equals(noRiseFall)) // case that there is a requirement of
											// no rise.
			{ // Then rise should be its default-- null. Otherwise it is not a
				// match.
				if (that.getRise() != null)
					return false;
			}

		}
		// TODO something for diphthongs to cover rise and fall...

		// if reached this point, it should fulfill all the requirements, so...
		return true;
	}

	/**
	 * setIncludeForAll -- remakes the candPhone to include everything that fits
	 * stated requirements, single parameter version
	 * 
	 * @param param
	 *            -- the parameter restriction for which we are including
	 *            everything else. Must be name of a variable here.
	 * @param value
	 *            -- value of that parameter, in int form. ( for booleans, 1 is
	 *            true, 0 is false)
	 * @precondition values set are indeed valid given candPhones status as
	 *               consonant or vowel, etc.
	 * @precondition values set are valid for the parameter they are set for.
	 *               i.e. maxPlaces.contains(place), etc.
	 * @precondition paramList.contains(param.toLowerCase()) Note also: one
	 *               cannot change status as Vowel or Consonant this way
	 */

	public void setIncludeForAll(String param, int value) {
		assert paramList.contains(param.toLowerCase()) : "Your stated parameter is not among the supported parameters";
		// TODO when writing this method, you must take into account type
		// restriction (vowel, consonant, etc...)
		if (!param.equalsIgnoreCase("place"))
			places = new ArrayList<Integer>(maxPlaces);
		else {
			places = new ArrayList<Integer>();
			places.add(value);
		}
		if (!param.equalsIgnoreCase("manner"))
			manners = new ArrayList<Integer>(maxManners);
		else {
			manners = new ArrayList<Integer>();
			manners.add(value);
		}
		if (!param.equalsIgnoreCase("coArtic"))
			coArtics = new ArrayList<Integer>(maxPlaces);
		else {
			coArtics = new ArrayList<Integer>();
			coArtics.add(value);
		}
		if (!param.equalsIgnoreCase("roundness"))
			roundness = 0;
		else
			roundness = value;
		if (!param.equalsIgnoreCase("nasality"))
			nasality = 0;
		else
			nasality = value;
		if (!param.equalsIgnoreCase("lateralness"))
			lateralness = 0;
		else
			lateralness = value;
		if (!param.equalsIgnoreCase("rhoticity"))
			rhoticity = 0;
		else
			rhoticity = value;
		if (!param.equalsIgnoreCase("voicedness"))
			voicedness = 0;
		else
			voicedness = value;
		if (!param.equalsIgnoreCase("aspiration"))
			aspiration = 0;
		else
			aspiration = value;
		if (!param.equalsIgnoreCase("stress"))
			stress = 0;
		else
			stress = value;
		if (!param.equalsIgnoreCase("lengths"))
			lengths = 1;
		else
			lengths = value;
	}

	/**
	 * setIncludeForAll -- multiple parameter version of above method,
	 * setIncludeForAll (String param, int value)
	 * 
	 * @param params
	 *            -- parameters we are declaring restrictions on. Each must be
	 *            the name of a variable here
	 * @param values
	 *            -- integer values of restriction for their respective
	 *            paramters
	 * @precondition values set are valid given status as a consonant or vowel,
	 *               etc
	 * @precondition params.size() == values.length Note also: one cannot change
	 *               status as Vowel or Consonant this way
	 */
	public void setIncludeForAll(List<String> params, int[] values) {
		// assertion block
		assert params
				.size() == values.length : "Error: Number of parameters given does not match number of values given!";
		for (int i = 0; i < params.size(); i++)
			assert paramList.contains(params.get(i)) : "Error : parameter unsupported";

		// actual execution block
		int[] paramInds = new int[paramList.size()]; // array for the location,
														// or lack thereof, of
														// each possible
														// parameter in the list
														// given

		for (int j = 0; j < paramList.size(); j++) // fill array
			paramInds[j] = params.indexOf(paramList.get(j));

		// act on each parameter as per its presence and associated value
		// place: (note that index 0 on paramList is type)
		// TODO decide what to do about (non-numeric) type
		if (paramInds[1] == -1) // i.e. wasn't found.
			places = new ArrayList<Integer>(maxPlaces);
		else {
			places = new ArrayList<Integer>();
			places.add(values[paramInds[1]]);
		}

		// manner
		if (paramInds[2] == -1)
			manners = new ArrayList<Integer>(maxManners);
		else {
			manners = new ArrayList<Integer>();
			manners.add(values[paramInds[2]]);
		}

		// coArtic
		if (paramInds[3] == -1)
			coArtics = new ArrayList<Integer>(maxPlaces);
		else {
			coArtics = new ArrayList<Integer>();
			coArtics.add(values[paramInds[3]]);
		}

		// roundness
		if (paramInds[4] == -1)
			roundness = 0;
		else
			roundness = values[paramInds[4]];

		// nasality
		if (paramInds[5] == -1)
			nasality = 0;
		else
			nasality = values[paramInds[5]];

		// lateralness
		if (paramInds[6] == -1)
			lateralness = 0;
		else
			lateralness = values[paramInds[6]];

		// rhoticity
		if (paramInds[7] == -1)
			rhoticity = 0;
		else
			rhoticity = values[paramInds[7]];

		// voicedness
		if (paramInds[8] == -1)
			voicedness = 0;
		else
			voicedness = values[paramInds[8]];

		// aspiration
		if (paramInds[9] == -1)
			aspiration = 0;
		else
			aspiration = values[paramInds[9]];

		// stress
		if (paramInds[10] == -1)
			stress = 0;
		else
			stress = values[paramInds[10]];

		// length
		if (paramInds[11] == -1)
			lengths = 2;
		else
			lengths = values[paramInds[11]];

		// rise and fall are not handled by this method as they are in string
		// form
		// since the method is to be inclusive, we set these also as inclusive
		rises = "@";
		falls = "@";
	}

	/**
	 * setIncludeForAll-- multiple parameter, multiple value version
	 * 
	 * @param params
	 *            -- parameters we are declaring restrictions on. Each must be
	 *            the name of a variable here
	 * @param values
	 *            -- lists of integer values of restriction for their respective
	 *            parameters (if only one is possible, one-item list, otherwise
	 *            we have a multiple-item list)
	 * @precondition values set are valid given status as a consonant or vowel,
	 *               etc
	 * @precondition params.size() == values.length
	 */
	public void setIncludeForAll(List<String> params, List<List<Integer>> values) {
		// assertion block
		assert params.size() == values
				.size() : "Error: Number of parameters given does not match number of value-lists given!";
		for (int i = 0; i < params.size(); i++)
			assert paramList.contains(params.get(i)) : "Error : parameter unsupported";

		// actual execution block
		int[] paramInds = new int[paramList.size()]; // array for the location,
														// or lack thereof, of
														// each possible
														// parameter in the
														// parameter list given
														// as the first method
														// parameter
		// ordered by the order of parameters in class variable paramList

		// fill the array paramInds
		for (int j = 0; j < params.size(); j++) {
			paramInds[j] = params.indexOf(paramList.get(j));
		}

		// act on each parameter as per its presence and associated value(s)

		// first, place, which has index 1
		// (note that index 0 on paramList is type, which we are ignoring for
		// now)
		// TODO figure out what we are doing for type.
		if (paramInds[1] == -1) {

		}
	}

	public boolean equals(CandPhone that) {
		for (String type : types)
			if (!that.getTypes().contains(type))
				return false;
		for (Integer place : places)
			if (!that.getPlaces().contains(place))
				return false;
		for (Integer manner : manners)
			if (!that.getManners().contains(manner))
				return false;
		for (Integer coArtic : coArtics)
			if (!that.getCoArtics().contains(coArtic))
				return false;
		if (roundness != that.getRoundness())
			return false;
		if (nasality != that.getNasality())
			return false;
		if (lateralness != that.getLateralness())
			return false;
		if (rhoticity != that.getRhoticity())
			return false;
		if (voicedness != that.getVoicedness())
			return false;
		if (aspiration != that.getAspiration())
			return false;
		if (stress != that.getStress())
			return false;
		if (lengths != that.getLengths())
			return false;
		List<String> theseRises = Arrays.asList(rises.split(","));
		List<String> thoseRises = Arrays.asList(that.getRises().split(","));
		if (theseRises.size() != thoseRises.size())
			return false;
		for (String thisRise : theseRises)
			if (!thoseRises.contains(thisRise))
				return false;
		List<String> theseFalls = Arrays.asList(falls.split(","));
		List<String> thoseFalls = Arrays.asList(that.getFalls().split(","));
		if (theseFalls.size() != thoseFalls.size())
			return false;
		for (String thisFall : theseFalls)
			if (!thoseFalls.contains(thisFall))
				return false;

		// if reached this point...
		return true;
	}

	// TODO at the moment there is no toString() and also no generic
	// equals(Object) method-- do NOT try to use these in other classes for this
	// class.
}
