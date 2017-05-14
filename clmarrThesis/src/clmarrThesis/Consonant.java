package clmarrThesis;

public class Consonant extends Phone {

	private final int[] consPlaces = new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 85, 90, 100 };

	// Constructors
	public Consonant() {
		super();
		type = "Consonant";
	}

	/** clone constructor */
	public Consonant(Consonant that) {
		type = "Consonant";
		this.place = that.getPlace();
		this.manner = that.getManner();
		this.coArtic = (that.getCoArtic() == this.place) ? 0 : that.getCoArtic(); // cannot
																					// have
																					// same
																					// coArtic
																					// as
																					// place
		this.rounded = that.isRounded();
		this.nasal = that.isNasal();
		this.lateral = that.isLateral();
		this.rhotic = that.isRhotic();
		this.voiced = that.isVoiced();
		this.aspirated = that.isAspirated();
		this.stress = 0;
		this.length = that.getLength();
	}

	/**
	 * casting from Phone-type
	 * 
	 * @param that
	 *            -- Phone we are casting to a consonant-structure object
	 * @precondition : consPlaces.contains(that.getPlace())
	 * @precondition : that.getManner() <= 30 //i.e. a valid manner for
	 *               consonants
	 */
	public Consonant(Phone that) {
		super(that);
		assert validConsPlace(that
				.getPlace()) : "Phone object cannot be casted to a Consonant as its place is not valid for Consonants!";
		assert that
				.getManner() <= 30 : "Phone object cannot be casted to Consonant as its manner is not valid for Consonants!";
		type = "Consonant";
		stress = 0;
	}

	public Consonant(int newPlace, int newManner) {
		super(newPlace, newManner);
		assert validConsPlace(newPlace) : "Error : new place is not valid for a consonant ";
		assert newManner > 30 : "Error : new manner is not valid for a consonant";
		type = "Consonant";
	}

	// TODO explain why for consonants, not just for vowels, the one-bool
	// constructor specifies round, not, say, voicing or nasality
	public Consonant(int newPlace, int newManner, boolean round) {
		super(newPlace, newManner);
		assert validConsPlace(newPlace) : "Error : new place is not valid for a consonant ";
		assert newManner > 30 : "Error : new manner is not valid for a consonant";
		type = "Consonant";
		rounded = round;
	}

	public Consonant(int newPlace, int newManner, boolean round, boolean voice) {
		super(newPlace, newManner);
		assert validConsPlace(newPlace) : "Error : new place is not valid for a consonant ";
		assert newManner > 30 : "Error : new manner is not valid for a consonant";
		type = "Consonant";
		rounded = round;
		voiced = voice;
	}

	public Consonant(int newPlace, int newManner, int newCoArtic, boolean round, boolean naze, boolean lat,
			boolean rhote, boolean voice, boolean asp, int shtress, int len) {
		super(newPlace, newManner, newCoArtic, round, naze, lat, rhote, voice, asp, shtress, len);
		assert validConsPlace(newPlace) : "Error : new place is not valid for a consonant ";
		assert newManner > 30 : "Error : new manner is not valid for a consonant";
		type = "Consonant";
		stress = 0;
	}

	// @Override accessors.
	public boolean isLateral() {
		return lateral;
	}

	public boolean isVoiced() {
		return voiced;
	}

	public boolean isAspirated() {
		return aspirated;
	}

	public boolean isStressed() {
		return false;
	}

	public int getStress() {
		return 0;
	}

	public int[][] getRise() {
		return null;
	}

	public int[][] getFall() {
		return null;
	}

	// Mutators all coded in master class

	public String print() {
		String outprint = "";

		if (place == 10) // Glottal
		{
			if (manner == 10) // Stop
			{
				if (!voiced && noCoMan())
					outprint = "ʔ"; // "Ê�?";
			} else if (manner == 25) // nonsibilant fricative
			{
				if (!voiced && noCoMan())
					outprint = "h";
			}

		}
		// pharyngeals excluded for now as we won't be using them here.
		else if (place == 30) // Uvular
		{
			if (manner == 10) // Stop
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ɢ"; // "É¢";
					else
						outprint = "q";
				}
			} else if (manner == 25 || manner == 30) // Fricative or
														// Approximant.
			{
				if (voiced)
					if (!nasal && !lateral)
						outprint = "ʁ";// "Ê�"; //note: could be rhotic.
					else if (noCoMan())
						outprint = "χ"; // "�?‡";

			}
			if (manner == 27) // Trill
			{
				outprint = "ʀ"; // "Ê€";
			}
		} else if (place == 40) // Velar
		{
			if (manner == 10) // Stop
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ɡ";
					else
						outprint = "k";
				} else if (nasal) {
					if (voiced)
						outprint = "ŋ"; // "Å‹";
					else
						outprint = "MOO";
				}
			} else if (manner == 25) // fricative
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ɣ";
					else
						outprint = "x"; // É£;
				}
			} else if (manner == 30) // approximant
			{
				if (noCoMan()) {
					if (rounded) {
						if (voiced)
							outprint = "w";
					} else {
						if (voiced)
							outprint = "ɰ";
					}
				} else if (lateral)
					if (voiced)
						outprint = "ʟ"; // "ÊŸ";
			}
		} else if (place == 50) // Palatal
		{
			if (manner == 10) // Stop
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ɟ"; // "ÉŸ";
					else
						outprint = "c";
				} else if (nasal) {
					if (voiced)
						outprint = "ɲ"; // "É²"; }
				}
			} else if (manner == 13) // Sibilant Affricate
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "dʑ";// "dÊ‘";
					else
						outprint = "tɕ"; // "tÉ•";
				}
			} else if (manner == 15) // Nonsibilant Affricate
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ɟʝ";
					else
						outprint = "cç";
				}
			} else if (manner == 23) // Sibilant
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ʑ"; // "Ê‘";
					else
						outprint = "ɕ"; // "tÉ•";
				}
			} else if (manner == 25) // Nonsibilant Fricative
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ʝ"; // "Ê�";
					else
						outprint = "ç"; // "Ã§";
				}
			} else if (manner == 30) // Approximant
			{
				if (noCoMan()) {
					if (rounded) {
						if (voiced)
							outprint = "ɥ";
					} else if (voiced)
						outprint = "j";
				} else if (lateral)
					if (voiced)
						outprint = "ʎ";
			}
		} else if (place == 60) // Postalveolar
		{
			if (manner == 13) // Sibilant Affricate
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "d͡ʒ";// "Ê’";
					else
						outprint = "t͡ʃ";// "Êƒ";
				}
			} else if (manner == 23) // Sibilant Fricative
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "ʒ";// "Ê’";
					else
						outprint = "ʃ";// "Êƒ";
				}
			}
		} else if (place == 70) // Alveolar
		{
			if (manner == 10) // Stop
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "d";
					else
						outprint = "t";
				} else if (nasal)
					if (voiced)
						outprint = "n";
			} else if (manner == 13) // Sibilant Affricate
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "d͡z";
					else
						outprint = "t͡s";
				}
			} else if (manner == 23) // Sibilant Fricative
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "z";
					else
						outprint = "s";
				}
			} else if (manner == 27) // trill
			{
				if (!nasal && !lateral)
					if (voiced)
						outprint = "r"; // rhotic
			} else if (manner == 28) // tap
			{
				if (!nasal && !lateral)
					if (voiced)
						outprint = "ɾ";// "É¾"; // rhotic
			} else if (manner == 30) // Approximant
			{
				if (!nasal) {
					if (!lateral && rhotic)
						outprint = "ɹ";// "É¹";
					else if (lateral && !rhotic) {
						if (coArtic == 40)
							outprint = "ɫ";
						outprint = "l";
					}
				}
			}
		} else if (place == 80) // Dental
		{
			if (manner == 10) // Stop
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "d̪";
					else
						outprint = "t̪";
				} else if (nasal)
					outprint = "n̪";
			} else if (manner == 23) {
				if (noCoMan()) {
					if (voiced)
						outprint = "z̪";
					else
						outprint = "s̪";
				}
			} else if (manner == 25) {
				if (noCoMan()) {
					if (voiced)
						outprint = "ð"; // Ã°
					else
						outprint = "θ"; // tÎ¸
				}
			} else if (manner == 27) {
				if (!lateral && !nasal && rhotic)
					if (voiced)
						outprint = "r̪";
			} else if (manner == 30) {
				if (noCoMan())
					if (voiced)
						outprint = "ð̞";
					else if (lateral)
						if (voiced)
							outprint = "l̪";
			}
		} // ignore linguolabial
		else if (place == 90) // Labiodental
		{
			if (manner == 10) {
				if (nasal && !lateral && !rhotic)
					if (voiced)
						outprint = "ɱ"; // ðÉ±";
			} else if (manner == 15) {
				if (noCoMan()) {
					if (voiced)
						outprint = "bv";
					else
						outprint = "pf";
				}
			} else if (manner == 25) // Nonsibilant Fricative
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "v";
					else
						outprint = "f";
				}
			} else if (manner == 30) {
				if (noCoMan())
					if (voiced)
						outprint = "ʋ"; // Ê‹";
			}
		} else if (place == 100) // Bilabial
		{
			if (manner == 10) // Stop
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "b";
					else
						outprint = "p";
				} else if (nasal)
					if (voiced)
						outprint = "m";
			} else if (manner == 25 || manner == 30) // same symbols used...
														// should fix this
														// maybe?
			{
				if (noCoMan()) {
					if (voiced)
						outprint = "β"; // "Î²";
					else
						outprint = "ɸ";// "É¸";

				}
			}
		}
		if (outprint == "")
			return "#";

		/*
		 * Markings for articulation: Those that are unlikely to be of any use
		 * for this project are excluded
		 */
		if (rounded) {
			if ((coArtic == 50 || place == 50) && manner != 30) // palatal
																// rounded
																// coartic
				outprint = outprint + "ᶣ";
			else if ((coArtic == 40 || place == 40) && manner != 30)
				outprint = outprint + "ʷ";
		} else if (coArtic == 50)
			outprint = outprint + "ʲ"; // palatalization
		else if (coArtic == 40)
			outprint = outprint + "ᶭ"; // velarization
		if (coArtic == 10)
			outprint = outprint + "ˀ"; // glottalization
		else if (aspirated)
			outprint = outprint + "ʰ"; // aspiration -- glottal fricative
										// aspect.

		// return and deal with length
		if (length == 1)
			return outprint;
		else if (length == 2)
			return outprint + "ː";
		else /* length == 3, hopefully */ return outprint + "ːː";

	}

	// auxiliary method used for assertions
	private boolean validConsPlace(int pInt) {
		for (int p = 0; p < consPlaces.length; p++) {
			if (consPlaces[p] == pInt)
				return true;
		}
		return false;
	}

}
