package clmarrThesis;

public class Phthong extends Vowel {

	// default constructor -- note rise and fall are still null by default!
	public Phthong() {
		super();
		type = "Phthong";
	}

	/** clone constructor */
	public Phthong(Phthong that) {
		super(that.getNucleus());
		type = "Phthong";
		rise = that.getRise();
		fall = that.getFall();
		length = that.getLength();
	}

	public Phthong(Phonic that) {
		super(that);
		type = "Phthong";
		rise = that.getRise();
		fall = that.getFall();
		length = that.getLength();
	}

	public Phthong(Phone that) {
		super(that);
		type = "Phthong";
		rise = that.rise;
		fall = that.fall;
		length = that.length;
	}

	public Phthong(Vowel nucleus, int[][] newRise, int[][] newFall) {
		super(nucleus); // essentially clone the nucleus, then add the phthong
						// elements onto it.
		type = "Phthong";
		rise = (newRise == null) ? null : newRise.clone();
		fall = (newFall == null) ? null : newFall.clone();
		// by default, unless otherwise specified, length is number of
		// components -- note this isn't how languages like Old English and
		// Icelandic worked
		length = 1 + ((rise == null) ? 0 : rise.length) + ((fall == null) ? 0 : fall.length);
	}

	public Phthong(Vowel nucleus, int[][] newRise, int[][] newFall, int newLength) {
		super(nucleus);
		type = "Phthong";
		rise = (newRise == null) ? null : newRise.clone();
		fall = (newFall == null) ? null : newFall.clone();
		length = newLength;
	}

	public Phthong(int newPlace, int newManner, int newCoArtic, boolean round, boolean naze, boolean lat, boolean rhote,
			boolean voice, boolean asp, int shtress, int len, int[][] newRise, int[][] newFall) {
		super(newPlace, newManner, newCoArtic, round, naze, lat, rhote, voice, asp, shtress, len);
		assert validVowelPlace(newPlace) : "Error: new place is not valid for vowels";
		assert newManner > 30 : "Error: new manner is not valid for vowels";
		type = "Phthong";
		rise = (newRise == null) ? null : newRise.clone();
		fall = (newFall == null) ? null : newFall.clone();
	}

	public Vowel getNucleus() {
		return new Vowel(place, manner, coArtic, rounded, nasal, lateral, rhotic, voiced, aspirated, stress, length);
	}

	public Phone getOnset() {
		if (rise == null) // then return same as getNucleus()
			return new Vowel(place, manner, coArtic, rounded, nasal, lateral, rhotic, voiced, aspirated, stress,
					length);
		return new Vowel(rise[0][0], rise[0][1], (rise[0][2] == 1)); // return
																		// first
																		// part
																		// of
																		// the
																		// rise;
																		// considered
																		// to be
																		// rounded
																		// iff
																		// rise[0][2]
																		// == 1.
	}

	public Phone getOffset() {
		if (fall == null) // then return same as getNucleus()
			return new Vowel(place, manner, coArtic, rounded, nasal, lateral, rhotic, voiced, aspirated, stress,
					length);
		int last = fall.length - 1;
		return new Vowel(fall[last][0], fall[last][1], (fall[last][2] == 1));
	}

	// @Override accessors
	public int[][] getRise() {
		return (rise == null) ? null : (rise.length > 0) ? null : rise.clone();
	}

	public int[][] getFall() {
		return (fall == null) ? null : (fall.length > 0) ? null : fall.clone();
	}

	// @Override
	public String print() {
		String outprint = "";

		// add contours of nonprominent rise vowels each time
		if (rise != null) {
			for (int i = 0; i < rise.length; i++) { // add contours of
													// nonprominent rise vowels
													// each time
				if (manner < 4.0) // in practice all of these are approximants
									// and all that matters is place, manner and
									// roundedness.
					outprint = outprint + (new Consonant(rise[i][0], rise[i][1], (rise[i][2] == 1))).print();
				else // we're dealing with a vowel, not an approximant
						// (consonant)
				{
					outprint = outprint + (new Vowel(rise[i][0], rise[i][1], (rise[i][2] == 1))).print();// third
																											// part
																											// is
																											// to
																											// convert
																											// int
																											// form
																											// of
																											// rounded
																											// to
																											// boolean.
					// if (nasal) outprint = outprint + "̃"; //nasal diphthongs
					// mean all parts are nasal . However for character encoding
					// reasons we do not print this on the non-prominent parts
					// because in the current encoding, if the nasal tilde and
					// the asyllabic underscore are on the same character, one
					// jumps back 2-3 slots, which is bad.
					outprint = outprint + "̯̯";
				}
			}
		}

		// print prominent vowel
		Vowel promVowel = new Vowel(this.place, this.manner, this.rounded);
		promVowel.setNasal(this.nasal);
		promVowel.setStress(this.stress);
		outprint = outprint + promVowel.print();

		if (fall != null) {
			for (int i = 0; i < fall.length; i++) { // add contours of
													// nonprominent rise vowels
													// each time
				if (manner < 4.0) // in practice all of these are approximants
									// and all that matters is place, manner and
									// roundedness.
					outprint = outprint + (new Consonant(fall[i][0], fall[i][1], (fall[i][2] == 1))).print();
				else // we're dealing with a vowel, not an approximant.
				{
					outprint = outprint + (new Vowel(fall[i][0], fall[i][1], (fall[i][2] == 1))).print() + "̯"; // third
																												// part
																												// is
																												// to
																												// convert
																												// int
																												// form
																												// of
																												// rounded
																												// to
																												// boolean.
					// if (nasal) outprint = outprint + "̃"; //nasal diphthongs
					// mean all parts are nasal . However for encoding reasons
					// we do not print this on the non-prominent parts.
				}
			}
		}

		if (rhotic)
			outprint = outprint + "˞";
		if (length == 3 && this.numComps() != 3)
			outprint = outprint + ":"; // we assume there are no four-vowel
										// phthongs

		if (outprint == "")
			return "&#&";

		return outprint;
	}

	// returns total number of components : nucleus plus the sizes of fall and
	// rise
	public int numComps() {
		return 1 + ((rise == null) ? 0 : rise.length) + ((fall == null) ? 0 : fall.length);
	}

}
