package clmarrThesis;

//this class does not actually represent any real linguistic feature
//however it is used for various programming purposes
//most importantly, marking the ends and beginnings of words and/or syllables with "pseudo Phones"
//designed so that they can fit into a list of phones for analysis. 
public class PseudoPhone extends Phonic {

	/*
	 * Note that this class uses the protected parameter type differently than
	 * the other subclasses of Phonic, for which it is simply a class marker:
	 * "default" -- bullshit type occurring by default that should never, ever
	 * be seen "word onset" -- marks the beginning of a word. By definition also
	 * a syllable onset "word coda" -- marks the end of a word. By definition
	 * also a syllable coda "syll break" -- not yet used. May be used if we ever
	 * want to mark syllable breaks. anything else -- should hopefully never be
	 * seen.
	 */

	// Constructors:
	public PseudoPhone() {
		type = "default";
	} // a bullshit type that should never be seen.

	public PseudoPhone(String t) {
		type = t;
	}

	// Accessors implemented for the sake of candPhone compiling.
	public int getPlace() {
		return 0;
	} // this should really never be called.

	public int getManner() {
		return 0;
	} // same as above.

	public int getCoArtic() {
		return 0;
	} // same as above.

	public boolean isRounded() {
		return false;
	}

	public boolean isNasal() {
		return false;
	}

	public boolean isLateral() {
		return false;
	}

	public boolean isRhotic() {
		return false;
	}

	public boolean isVoiced() {
		return false;
	}

	public boolean isAspirated() {
		return false;
	}

	public boolean isStressed() {
		return false;
	}

	public int getStress() {
		return 0;
	}

	public int getLength() {
		return 0;
	}

	public int[][] getRise() {
		return null;
	}

	public int[][] getFall() {
		return null;
	}

	// Mutator
	public void setType(String newType) {
		type = newType;
	}

	public String print() {
		if (type == "word onset")
			return "<";
		if (type == "word coda")
			return ">";
		if (type == "syll break")
			return "$"; // unlikely to be used. Instead a syllable division
						// method will be used somewhere.
		return "%"; // default -- should never be seen.
	}

	public boolean equals(Object other) {
		if (other instanceof PseudoPhone)
			return (this.toString() == other.toString());
		return false; // i.e. not a pseudophone.
	}

}
