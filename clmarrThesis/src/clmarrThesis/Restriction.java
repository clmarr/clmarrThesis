package clmarrThesis;

import java.util.*;

//class represents one phonemic restriction on a candidate phoneme that would be seen in teh context variables of a sound shift, for example
public class Restriction {

	private String var; // name of the variable that is being restricted

	// list of all the boolean-type variables
	private final List<String> boolVars = Arrays.asList("rounded,nasal,lateral,rhotic,voiced,aspirated".split(","));
	// private final List<String> nonBoolVars =
	// Arrays.asList("type,place,manner,coArtic,length,rise,fall".split(","));

	private boolean truth; // true if its a positive mapping -- otherwise it's
							// negative (false) . I.e. negative would mean it's
							// NOT the assigned value.

	private String value; // value assigned to that variable. Not strictly an
							// equation. Will be things like "consonant",
							// "velar", etc...

	// This is the only constructor: construct the restriction based on the
	// input.
	// Ex : If it says "consonant", var = "type", truth = true, value =
	// "consonant".
	// Ex : If it says "-consonant", same as above but truth = false
	// for boolean type variables, value is empty
	// @precondition : please start input with '+' or '-' and then the trait
	public Restriction(String input) throws Error {
		char firstChar = input.charAt(0);
		assert (firstChar == '+'
				|| firstChar == '-') : "You did not start the input string with either a + or a -. Please do so.";
		truth = (firstChar == '+');

		// the remaining part of the string may be either value or the variable,
		// depending on whether its a boolean variable.
		String trait = input.substring(1).replace(" ", ""); // strip any white
															// space.

		// now get what variable it refers to and what the value is.
		// note some values actually effect multiple variables, but when they
		// do, we only count the one that's not a boolean
		// ex "liquid" incorporates data from manner, lateralness, and
		// rhoticity, but we place it under manner.

		if (boolVars.contains(trait)) {
			var = new String(trait);
			value = "";
		} else if (isTypeVal(trait)) {
			var = "type";
			value = new String(trait);
		} else if (isPlaceVal(trait)) {
			var = "place";
			value = new String(trait);
		} else if (isMannerVal(trait)) {
			var = "manner";
			value = new String(trait);
		} else if (isCoArticVal(trait)) {
			var = "coArtic";
			value = new String(trait);
		} else if (isStressVal(trait)) {
			var = "stress";
			value = new String(trait);
		} else if (isLengthVal(trait)) {
			var = "length";
			value = new String(trait);
		} else if (isRiseVal(trait)) {
			var = "rise";
			value = new String(trait);
		} else if (isFallVal(trait)) {
			var = "fall";
			value = new String(trait);
		} else // TODO debugging
		{
			System.out.println("Something is wrong : " + trait + " is not a supported trait");
			throw new Error();
		}
	}

	// this class shall have no mutators. Once declared, everything is final.

	// accessors
	public String getVar() {
		return var;
	}

	public boolean getTruth() {
		return truth;
	}

	public String getValue() {
		return value;
	}

	// general methods follow

	// to see if a Phone object that meets the requirement specified by this
	// restriction
	public boolean compare(Phonic that) {

		if (boolVars.contains(var)) {
			switch (var) {
			case "rounded":
				return (truth == that.isRounded());
			case "nasal":
				return (truth == that.isNasal());
			case "lateral":
				return (truth == that.isLateral());
			case "rhotic":
				return (truth == that.isRhotic());
			case "voiced":
				return (truth == that.isVoiced());
			case "aspirated":
				return (truth == that.isAspirated());
			default:
				System.out.println("There has been some error, boolVars variable must be typed wrong");
				return false;
			}
		} else
			return (truth == valueTruthCondition(that));
	}

	// auxiliary function for use if we are not dealing with one of the
	// boolvars.
	// outputs the raw value of the truth condition based on if the phone
	// matches the restrictions its parameter specifies (note ultimately
	// sometimes this boolean is negated)
	public boolean valueTruthCondition(Phonic that) {
		String thatType = that.getType().toLowerCase();
		String phoneTypes = "phonevowelconsonantphthong";

		// handle first containment with the types: relevant for pseudophone
		// phonic, phone and vowel
		if (value.equals("phonic"))
			return isTypeVal(thatType);
		if (value.equals("phone"))
			return phoneTypes.contains(that.getType());
		if (value.equals("pseudophone"))
			return isTypeVal(thatType) && !phoneTypes.contains(that.getType());
		if (value.equals("vowel"))
			return "vowelphthong".contains(thatType);

		if (var.equals("type"))
			return value.equalsIgnoreCase(that.getType().replaceAll(" ", ""));
		// this also handles word onset, word coda and syll break, even though
		// these are types within PseudoPhone

		int lastFallIndex; // for convenience
		if (that.getFall() == null)
			lastFallIndex = -1;
		else
			lastFallIndex = that.getFall().length;

		switch (value) {
		case "laryngeal":
			return that.getPlace() <= 20; // TODO note in some languages these
											// include uvulars
		case "glottal":
			return that.getPlace() == 10;
		case "pharyngeal":
			return that.getPlace() == 20;
		case "dorsal":
			return Math.abs(that.getPlace() - 40) <= 10; // 30 <=
															// that.getPlace()
															// <= 50
		case "uvular":
			return that.getPlace() == 30;
		case "velar":
			return that.getPlace() == 40;
		case "back":
			return Math.abs(that.getPlace() - 41) == 1; // 40 or 42
		case "nearback":
			return that.getPlace() == 42;
		case "central":
			return that.getPlace() == 45;
		case "nearfront":
			return that.getPlace() == 48;
		case "front":
			return Math.abs(that.getPlace() - 49) == 1; // 48 or 50
		case "palatal":
			return that.getPlace() == 50;
		case "coronal":
			return Math.abs(that.getPlace() - 70) <= 10; // 60 to 80
		case "postalveolar":
			return that.getPlace() == 60;
		case "alveolar":
			return that.getPlace() == 70;
		case "dental":
			return that.getPlace() == 80;
		case "labial":
			return that.getPlace() > 81; // 90 or 100.
		case "labiodental":
			return that.getPlace() == 90;
		case "bilabial":
			return that.getPlace() == 100;
		case "obstruent":
			return that.getManner() <= 25;
		case "stop":
			return that.getManner() == 10;
		case "nasalstop":
			return that.getManner() == 10 && that.isNasal();
		case "affricate":
			return Math.abs(that.getManner() - 14) == 1; // 13 or 15
		case "sibilantaffricate":
			return that.getManner() == 13;
		case "spirantaffricate":
			return that.getManner() == 15;
		case "fricative":
			return Math.abs(that.getManner() - 24) == 1; // 23 or 25
		case "sibilant":
			return that.getManner() == 23;
		case "spirant":
			return that.getManner() == 25;
		case "sonorant":
			return that.getManner() > 25; // vowels and anything more open than
											// fricatives
		case "tap":
			return that.getManner() == 28;
		case "trill":
			return that.getManner() == 27;
		case "approximant":
			return that.getManner() == 30;
		case "liquid":
			return (that.isRhotic() || that.isLateral()) && that.getManner() >= 25; // TODO
																					// check
																					// this
																					// one.
		case "semivowel":
			return that.getManner() == 30 && Math.abs(that.getPlace() - 45) <= 5;
		case "close":
			return Math.abs(that.getManner() - 40) <= 10; // anywhere between 30
															// and 50
		case "nearclose":
			return that.getManner() == 45;
		case "midclose":
			return that.getManner() == 50;
		case "mid":
			return Math.abs(that.getManner() - 55) <= 5; // anywhere between 50
															// and 60
		case "schwa":
			return that.getManner() == 55 && Math.abs(that.getPlace() - 45) <= 3; // i.e.
																					// a
																					// vowel
																					// that
																					// could
																					// be
																					// a
																					// schwa
		case "midopen":
			return that.getManner() == 60;
		case "nearopen":
			return that.getManner() == 65;
		case "open":
			return that.getManner() >= 60;
		case "coglottal":
			return that.getCoArtic() == 10;
		case "copharyngeal":
			return that.getCoArtic() == 20;
		case "covelar":
			return that.getCoArtic() == 40;
		case "copalatal":
			return that.getCoArtic() == 50;
		case "cobilabial":
			return that.getCoArtic() == 100;
		case "stressed":
			return that.isStressed(); // i.e. stressed == 1 ; -stressed means
										// either 0 OR -1
		case "secondarystressed":
			return that.getStress() == -1;
		case "unstressed":
			return that.getStress() == 0;
		case "short":
			return that.getLength() == 1;
		case "long":
			return that.getLength() > 1;
		case "superlong":
			return that.getLength() == 3;
		case "rise":
			return that.getRise() != null;
		case "frontrise":
			return that.getRise()[0][0] > 47;
		case "centralrise":
			return Math.abs(that.getRise()[0][0] - 45) <= 3;
		case "backrise":
			return that.getRise()[0][0] < 43;
		case "roundrise":
			return that.getRise()[0][2] == 1;
		case "closerise":
			return that.getRise()[0][1] <= 50;
		case "midrise":
			return Math.abs(that.getRise()[0][1] - 55) <= 5;
		case "openrise":
			return that.getRise()[0][1] >= 60;
		case "fall":
			return that.getFall() != null;
		case "frontfall":
			return that.getFall()[lastFallIndex][0] > 47;
		case "centralfall":
			return Math.abs(that.getFall()[lastFallIndex][0] - 45) <= 3;
		case "backfall":
			return that.getFall()[lastFallIndex][0] < 43;
		case "roundfall":
			return that.getFall()[lastFallIndex][2] == 1;
		case "closefall":
			return that.getFall()[lastFallIndex][1] <= 50;
		case "midfall":
			return Math.abs(that.getFall()[lastFallIndex][1] - 55) <= 5;
		case "openfall":
			return that.getFall()[lastFallIndex][1] >= 60;
		case "frontonset":
			if (that.getRise() == null)
				return (that.getPlace() > 47);
			else
				return that.getRise()[0][0] > 47;
		case "frontoffset":
			if (that.getFall() == null)
				return (that.getPlace() > 47);
			else
				return that.getFall()[lastFallIndex][0] > 47;
		case "centralonset":
			if (that.getRise() == null)
				return Math.abs(that.getPlace() - 45) <= 3;
			else
				return Math.abs(that.getRise()[0][0] - 45) <= 3;
		case "centraloffset":
			if (that.getFall() == null)
				return Math.abs(that.getPlace() - 45) <= 3;
			else
				return Math.abs(that.getFall()[lastFallIndex][0] - 45) <= 3;
		case "backonset": // rise if there IS a rise otherwise the default (i.e.
							// the nucleus)
			if (that.getRise() == null)
				return (that.getPlace() < 43);
			else
				return that.getRise()[0][0] < 43;
		case "backoffset": // fall if there IS a fall otherwise the default
							// (i.e. the nucleus)
			if (that.getFall() == null)
				return (that.getPlace() < 43);
			else
				return that.getFall()[lastFallIndex][0] < 43;
		case "roundonset":
			if (that.getRise() == null)
				return that.isRounded();
			else
				return that.getRise()[0][2] == 1;
		case "roundoffset":
			if (that.getFall() == null)
				return that.isRounded();
			else
				return that.getFall()[lastFallIndex][2] == 1;
		case "closeonset":
			if (that.getRise() == null)
				return that.getManner() <= 50;
			else
				return that.getRise()[0][1] <= 50;
		case "closeoffset":
			if (that.getFall() == null)
				return that.getManner() <= 50;
			else
				return that.getFall()[lastFallIndex][1] <= 50;
		case "midonset":
			if (that.getRise() == null)
				return Math.abs(that.getManner() - 55) <= 5;
			else
				return Math.abs(that.getRise()[0][1] - 55) <= 5;
		case "midoffset":
			if (that.getFall() == null)
				return Math.abs(that.getManner() - 55) <= 5;
			else
				return Math.abs(that.getFall()[lastFallIndex][1] - 55) <= 5;
		case "openonset":
			if (that.getRise() == null)
				return that.getManner() >= 60;
			else
				return that.getRise()[0][1] >= 60;
		case "openoffset":
			if (that.getFall() == null)
				return that.getManner() >= 60;
			else
				return that.getFall()[lastFallIndex][1] >= 60;
		default:
			System.out.println("There is clearly an error -- " + value + " is not a supported value.");
			return false; // TODO debugging
		}

	}

	// auxiliary method -- return true if the String given is a possible name of
	// a value for type
	// note that since hte getType() method for PseudoPhones never returns
	// "PseudoPhone", that should never be what we enter.
	public boolean isTypeVal(String valu) {
		return (Arrays.asList(new String[] { "phone", "pseudophone", "consonant", "vowel", "phthong", "phonic",
				"wordonset", "wordcoda", "syllbreak" })).contains(valu.toLowerCase().replaceAll(" ", ""));
	}

	// auxiliary method -- return true if the String given is a possible name of
	// a value for place
	public boolean isPlaceVal(String valu) {
		return (Arrays.asList(new String[] { "laryngeal", "glottal", "pharyngeal", "dorsal", "uvular", "velar",
				"palatal", "back", "nearback", "central", "nearfront", "front", "postalveolar", "coronal", "alveolar",
				"dental", "labial", "labiodental", "bilabial" })).contains(valu);
	}

	// auxiliary method -- return true if the String given is a possible name of
	// a value for manner
	public boolean isMannerVal(String valu) {
		return (Arrays.asList(new String[] { "obstruent", "nasalstop", "stop", "affricate", "fricative", "spirant",
				"sibilant", "sonorant", "tap", "trill", "approximant", "liquid", "semivowel", "close", "nearclose",
				"midclose", "mid", "midopen", "nearopen", "open", "schwa" })).contains(valu);
	}

	// auxiliary method -- return true if the String given is a possible name of
	// a value for coArtic
	public boolean isCoArticVal(String valu) {
		return (valu.substring(0, 2).equals("co")) && isPlaceVal(valu.substring(2));
	}

	public boolean isStressVal(String valu) {
		return Arrays.asList("stressed,secondarystressed,unstressed".split(",")).contains(valu);
	}

	public boolean isLengthVal(String valu) {
		return Arrays.asList("short,long,superlong".split(",")).contains(valu);
	}

	public boolean isRiseVal(String valu) {
		// TODO add more variables here if necessary;
		// +rise if must have rise, -rise if can't have rise etc...
		return (Arrays.asList(new String[] { "rise", "frontrise", "centralrise", "backrise", "roundrise", "closerise",
				"midrise", "openrise", "frontonset", "centralonset", "backonset", "closeonset", "midonset", "openonset",
				"roundonset" })).contains(valu);
	}

	public boolean isFallVal(String valu) {
		// TODO add more variables here if necessary;
		// +rise if must have rise, -rise if can't have rise etc...
		return (Arrays.asList(new String[] { "fall", "frontfall", "centralfall", "backfall", "roundfall", "closefall",
				"midfall", "openfall", "frontoffset", "centraloffset", "backoffset", "roundoffset", "closeoffset",
				"midoffset", "openoffset" })).contains(valu);
	}

	// TODO figure out how to define rise and fall

	public String toString() {
		String output = "";
		if (truth)
			output = output + "+";
		else
			output = output + "-";
		return output + value;
	}

	public boolean equals(Object other) {
		if (other instanceof Restriction)
			return this.toString().equals(other.toString());
		else
			return false;
	}
}
