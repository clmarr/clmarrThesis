package clmarrThesis;

import java.util.Arrays;
import java.util.List;

/*
 * @author: Clayton Marr
 * @date: February 28 2017
 * Basic class of thesisReboot. Masterclass of Phone type hierarchy.
 * Essentially based off of "Phone2" in old project
 * However it is programmed using the Map class 
 */

abstract public class Phone extends Phonic {

	protected int place;
	/*
	 * Place enumeration: 0 : default. Should never be seen. 10 : Glottal
	 * Consonant 20 : Pharyngeal Consonant -- likely unused 30 : Uvular
	 * Consonant 40 : Velar Consonant or Back Vowel 42 : Near-back Vowel 45 :
	 * Central Vowel 48 : Near-front Vowel 50 : Palatal Consonant or Front Vowel
	 * 60 : Postalveolar Consonant, including Retroflex -- likely unused 70 :
	 * Alveolar Consonant 80 : Dental Consonant. May ultimately merge with
	 * alveolar 85 : Linguolabial Consonant. Likely unused for Albanian and
	 * French, obviously. 90 : Labiodental Consonant. 100 : Bilabial Consonant
	 */

	private final List<Integer> maxPlaces = Arrays
			.asList(new Integer[] { 10, 20, 30, 40, 42, 45, 48, 50, 60, 70, 80, 85, 90, 100 });

	protected int manner;
	/*
	 * Manner enumeration: 0 : Default. Should never be seen. 10 : Stop 13 :
	 * Sibilant Affricate 15 : Affricate 23 : Sibilant Fricative 25 : Fricative
	 * 27 : Trill 28 : Flap/Tap 30 : Approximant 40 : Close Vowel! 45 :
	 * Near-close vowel 50 : Close-mid vowel 55 : Middle vowel 60 : Open-mid
	 * vowel -- last set that can be interchanged w consonant 65 : Near-open
	 * vowel 70 : Open vowel
	 */

	private final List<Integer> maxManners = Arrays
			.asList(new Integer[] { 10, 13, 15, 23, 25, 27, 28, 30, 40, 45, 50, 55, 60, 65, 70 });

	protected int coArtic; // place of coArticulation, default 0 means no
							// coarticulation

	protected boolean rounded, nasal, lateral, rhotic, voiced, aspirated; // all
																			// denoting
																			// presence
																			// or
																			// absence
																			// of
																			// feature.

	protected int stress; // -1, 0 or 1. 1 -- stressed. 0 -- not stressed. -1 --
							// secondarily stressed.
	// TODO change this layout if need be

	protected int length; // 1, 2, or 3. 3 rarely if ever used.

	private final List<Integer> maxLengths = Arrays.asList(new Integer[] { 1, 2, 3 });

	protected int[][] rise, fall; // only implemented for phthongs, denote the
									// non-prominent constituent vowels in the
									// phthong
	// first dimension : consecutive order of vowels involved. Max 2 for either.
	// Max 3 total between rise and fall.
	// second dimension : three cells: place, manner and roundedness (1,0)

	/** default constructor */
	public Phone() {
		type = "Phone";
		place = 0;
		manner = 0;
		coArtic = 0;
		rounded = false;
		nasal = false;
		lateral = false;
		rhotic = false;
		voiced = false;
		aspirated = false;
		stress = 0;
		length = 1;
		// rise = null; false = null;
	}

	/** clone constructor */
	public Phone(Phone that) {
		type = "Phone";
		this.place = that.getPlace();
		this.manner = that.getManner();
		this.coArtic = (this.place == that.getCoArtic()) ? 0 : that.getCoArtic();
		this.rounded = that.isRounded();
		this.nasal = that.isNasal();
		this.lateral = that.isLateral();
		this.rhotic = that.isRhotic();
		this.voiced = that.isVoiced();
		this.aspirated = that.isAspirated();
		this.stress = that.getStress();
		this.length = that.getLength();
	}

	public Phone(Phonic that) {
		type = "Phone";
		this.place = that.getPlace();
		this.manner = that.getManner();
		this.coArtic = (this.place == that.getCoArtic()) ? 0 : that.getCoArtic();
		this.rounded = that.isRounded();
		this.nasal = that.isNasal();
		this.lateral = that.isLateral();
		this.rhotic = that.isRhotic();
		this.voiced = that.isVoiced();
		this.aspirated = that.isAspirated();
		this.stress = that.getStress();
		this.length = that.getLength();
	}

	public Phone(int newPlace, int newManner) {
		type = "Phone";
		place = newPlace;
		manner = newManner;
		coArtic = 0;
		rounded = false;
		nasal = false;
		lateral = false;
		rhotic = false;
		voiced = false;
		aspirated = false;
		stress = 0;
		length = 1;
		// rise = null; false = null;
	}

	public Phone(int newPlace, int newManner, boolean round) {
		type = "Phone";
		place = newPlace;
		manner = newManner;
		coArtic = 0;
		rounded = round;
		nasal = false;
		lateral = false;
		rhotic = false;
		voiced = false;
		aspirated = false;
		stress = 0;
		length = 1;
		// rise = null; false = null;
	}

	public Phone(int newPlace, int newManner, int newCoArtic) {
		type = "Phone";
		place = newPlace;
		manner = newManner;
		coArtic = (place == newCoArtic) ? 0 : newCoArtic;
		rounded = false;
		nasal = false;
		lateral = false;
		rhotic = false;
		voiced = false;
		aspirated = false;
		stress = 0;
		length = 1;
		// rise = null; false = null;
	}

	public Phone(int newPlace, int newManner, int newCoArtic, boolean round, boolean naze, boolean lat, boolean rhote,
			boolean voice, boolean asp, int shtress, int len) {
		type = "Phone";
		place = newPlace;
		manner = newManner;
		coArtic = (place == newCoArtic) ? 0 : newCoArtic;
		rounded = round;
		nasal = naze;
		lateral = lat;
		rhotic = rhote;
		voiced = voice;
		aspirated = asp;
		stress = shtress;
		length = len;
		// rise=null; fall=null;
	}

	// accessors
	public int getPlace() {
		return place;
	}

	public int getManner() {
		return manner;
	}

	public int getCoArtic() {
		return coArtic;
	}

	public boolean isRounded() {
		return rounded;
	}

	public boolean isNasal() {
		return nasal;
	}

	abstract public boolean isLateral(); // auto-false for vowels

	public boolean isRhotic() {
		return rhotic;
	}

	abstract public boolean isVoiced(); // auto-true for vowels

	abstract public boolean isAspirated(); // auto-true for vowels

	abstract public boolean isStressed(); // auto-false for consonants.

	public abstract int getStress(); // auto-zero for consonants

	public int getLength() {
		return length;
	}

	abstract public int[][] getRise();

	abstract public int[][] getFall();

	// mutators
	public void setPlace(int newPlace) {
		assert maxPlaces.contains(newPlace) : "Cannot set place to " + newPlace;
		place = newPlace;
	}

	public void setManner(int newManner) {
		assert maxManners.contains(newManner) : "Cannot set manner to " + newManner;
		manner = newManner;
	}

	public void setCoArtic(int newCoArtic) {
		assert maxPlaces.contains(newCoArtic) : "Cannot set place to " + newCoArtic;
		coArtic = (place == newCoArtic) ? 0 : newCoArtic;
	}

	public void setRounded(boolean round) {
		rounded = round;
	}

	public void setNasal(boolean naze) {
		nasal = naze;
	}

	public void setLateral(boolean lat) {
		lateral = lat;
	}

	public void setRhotic(boolean rhote) {
		rhotic = rhote;
	}

	public void setVoiced(boolean voice) {
		voiced = voice;
	}

	public void setAspirated(boolean asp) {
		aspirated = asp;
	}

	public void setStress(int stres) {
		stress = stres;
	}

	public void setLength(int len) {
		assert maxLengths.contains(len) : "Cannot set length to " + len;
		length = len;
	}

	public void setRise(int[][] rajze) {
		rise = rajze.clone();
	}

	public void setFall(int[][] falle) {
		fall = falle.clone();
	}

	/**
	 * @return if all the "coManner" features are false-- i.e. nothing that
	 *         would change the IPA symbol for consonants or add a diacritic for
	 *         vowels is here.
	 */
	public boolean noCoMan() {
		return (!nasal && !lateral && !rhotic);
	}

	/**
	 * 
	 * @return IPA transcription.
	 */
	abstract public String print(); // different for phthongs

	/**
	 * @return vector string form for Machine Learning algorithm.
	 */

	// @Override
	public String toString() {
		String out = "";
		out = out + type + "," + place + "," + manner + ", " + coArtic + "," + rounded + "," + nasal + "," + lateral
				+ "," + rhotic + "," + voiced + "," + aspirated + "," + stress + "," + length + ","
				+ printRiseFall(true) + "," + printRiseFall(false);
		return out;
	}

	/**
	 * @return true if they are the exact same phone.
	 */
	public boolean equals(Object that) {
		if (that instanceof Phone)
			return (this.toString().equals(that.toString()));
		return false;
	}

	/**
	 * convenient string form for rise or fall
	 * 
	 * @param isRise
	 *            -- true to get the form for rise, false for fall
	 * @return
	 */
	protected String printRiseFall(boolean isRise) {
		String output = "{";
		if (isRise) {
			if (rise != null) {
				for (int i = 0; i < rise.length; i++) {
					output = output + "[" + rise[i][0] + "," + rise[i][1] + "," + rise[i][2] + "]";
				}
			}
		} else {
			if (fall != null) {
				for (int i = 0; i < fall.length; i++) {
					output = output + "[" + fall[i][0] + "," + fall[i][1] + "," + fall[i][2] + "]";
				}
			}
		}
		output = output + "}";
		return output;
	}

}
