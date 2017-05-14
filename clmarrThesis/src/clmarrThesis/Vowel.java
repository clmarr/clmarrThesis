package clmarrThesis;

public class Vowel extends Phone {

	private int[] vowelPlaces = new int[] { 40, 42, 45, 48, 50 };

	/** default constructor */
	public Vowel() {
		super();
		type = "Vowel";
	}

	/** clone constructor */
	public Vowel(Vowel that) {
		type = "Vowel";
		this.place = that.getPlace();
		this.manner = that.getManner();
		this.coArtic = that.getCoArtic();
		this.rounded = that.isRounded();
		this.nasal = that.isNasal();
		this.lateral = false;
		this.rhotic = that.isRhotic();
		this.voiced = true;
		this.aspirated = false;
		this.stress = that.getStress();
		this.length = that.getLength();
	}

	/**
	 * caster from Phone
	 * 
	 * @precondition place, manner etc are correct
	 */
	public Vowel(Phone that) {
		super(that);
		type = "Vowel";
		assert validVowelPlace(
				place) : "Phone object cannot be cast to a Vowel because its articulation place is inappropriate! ";
		assert manner > 30 : "Phone object cannot be cast to a Vowel because its articulation manner is inappropriate!";
		this.lateral = false;
		this.voiced = true;
		this.aspirated = false;
	}

	public Vowel(Phonic that) {
		super(that);
		type = "Vowel";
		assert validVowelPlace(
				place) : "Phone object cannot be cast to a Vowel because its articulation place is inappropriate! ";
		assert manner > 30 : "Phone object cannot be cast to a Vowel because its articulation manner is inappropriate!";
		this.lateral = false;
		this.voiced = true;
		this.aspirated = false;
	}

	public Vowel(int newPlace, int newManner) {
		super(newPlace, newManner);
		assert validVowelPlace(newPlace) : "Error: new place is not valid for vowels";
		assert newManner > 30 : "Error: new manner is not valid for vowels";
		type = "Vowel";
		this.voiced = true;
	}

	public Vowel(int newPlace, int newManner, boolean round) {
		super(newPlace, newManner);
		assert validVowelPlace(newPlace) : "Error: new place is not valid for vowels";
		assert newManner > 30 : "Error: new manner is not valid for vowels";
		type = "Vowel";
		rounded = round;
		this.voiced = true;
	}

	public Vowel(int newPlace, int newManner, boolean round, int len) {
		super(newPlace, newManner);
		assert validVowelPlace(newPlace) : "Error: new place is not valid for vowels";
		assert newManner > 30 : "Error: new manner is not valid for vowels";
		type = "Vowel";
		rounded = round;
		length = len;
		this.voiced = true;
	}

	public Vowel(int newPlace, int newManner, int newCoArtic, boolean round, boolean naze, boolean lat, boolean rhote,
			boolean voice, boolean asp, int shtress, int len) {
		super(newPlace, newManner, newCoArtic, round, naze, lat, rhote, voice, asp, shtress, len);
		assert validVowelPlace(newPlace) : "Error: new place is not valid for vowels";
		assert newManner > 30 : "Error: new manner is not valid for vowels";
		type = "Vowel";
		coArtic = 0;
		lateral = false;
		voiced = true;
		aspirated = false;
	}

	@Override
	public int getCoArtic() {
		return 0;
	}

	public boolean isLateral() {
		return false;
	}

	public boolean isVoiced() {
		return true;
	} // all vowels are voiced.

	public boolean isAspirated() {
		return false;
	} // technically all vowels are mildly aspirated, but we don't care.

	public boolean isStressed() {
		return (stress == 1);
	}

	public int getStress() {
		return stress;
	}

	public int[][] getRise() {
		return null;
	} // overridden by Phthong subclass

	public int[][] getFall() {
		return null;
	} // overridden by Phthong subclass

	@Override
	public String print() {
		String outprint = "";

		switch (place) {
		case 40: // Velar AKA Back
			switch (manner) {
			case 40: // Close
				if (rounded)
					outprint = "u";
				else
					outprint = "ɯ"; // "É¯";
				break;
			case 50: // Close-Mid
				if (rounded)
					outprint = "o";
				else
					outprint = "ɤ"; // "É¤";
				break;
			case 55: // Mid
				if (rounded)
					outprint = "o̞";
				else
					outprint = "ɤ̞";
				break;
			case 60: // Open-Mid
				if (rounded)
					outprint = "ɔ"; // "É�?";
				else
					outprint = "ʌ"; // "ÊŒ";}
				break;
			case 70: // Open
				if (rounded)
					outprint = "ɒ"; // "É’";
				else
					outprint = "α"; // "Î±";
				break;
			default:
				break;
			}
			break;
		case 42: // Near-Back
			if (manner == 45) {
				if (rounded)
					outprint = "ʊ"; // "ÊŠ";
				else
					outprint = "ω"; // "�?‰";
			}
			break;
		case 45: // Central
			switch (manner) {
			case 40: // Close
				if (rounded)
					outprint = "ʉ"; // "Ê‰";
				else
					outprint = "ɨ"; // "É¨";
				break;
			case 45: // Near-Close
				if (rounded)
					outprint = "ʊ̈"; // "ÊŠÌˆ";
				else
					outprint = "ɪ̈"; // "ÉªÌˆ";
				break;
			case 50:
				if (rounded)
					outprint = "ɵ"; // "Éµ";
				else
					outprint = "ɘ"; // "É˜";
				break;
			case 55:
				if (rounded)
					outprint = "ɵ̞"; // "ÉµÌž";
				else
					outprint = "ə"; // "É™";
				break;
			case 60:
				if (rounded)
					outprint = "ɞ"; // "Éž";
				else
					outprint = "ɜ"; // "Éœ";
				break;
			case 65:
				if (rounded)
					outprint = "ɞ̞"; // "ÉžÌž";
				else
					outprint = "ɐ"; // "É�";
				break;
			case 70:
				if (rounded)
					outprint = "ɒ̈"; // "É’Ìˆ";
				else
					outprint = "ä"; // "Ã¤";
				break;
			default:
				break;
			}
			break;
		case 48: // Near-Front
			if (manner == 45) {
				if (rounded)
					outprint = "ʏ"; // "Ê�";
				else
					outprint = "ɪ"; // "Éª";
			}
			break;
		case 50: // Palatal AKA Front
			switch (manner) {
			case 40: // Close
				if (rounded)
					outprint = "y";
				else
					outprint = "i";
				break;
			case 50: // Close-Mid
				if (rounded)
					outprint = "ø"; // "Ã¸";
				else
					outprint = "e";
				break;
			case 55: // Central
				if (rounded)
					outprint = "ø̞";
				else
					outprint = "e̞";
				break;
			case 60: // Open-Mid
				if (rounded)
					outprint = "œ"; // "Å“";
				else
					outprint = "ɛ"; // "É›"; }
				break;
			case 65: // Near-Open
				if (!rounded)
					outprint = "æ"; // "Ã¦";
				break;
			case 70: // Open
				if (rounded)
					outprint = "ɶ"; // "�?°";
				else
					outprint = "a";
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		if (outprint == "")
			return "#";

		if (nasal)
			outprint = outprint + "̃";
		if (rhotic)
			outprint = outprint + "˞";

		if (stress == 1)
			outprint = outprint + "`";
		else if (stress == -1)
			outprint = outprint + "'";

		if (length == 1)
			return outprint;
		else if (length == 2)
			return outprint + ":";
		else /* length == 3, hopefully */ return outprint + "::";

	}

	// auxiliary method for a few assertions -- return if place given is valid
	// given that this is a vowel.
	public boolean validVowelPlace(int pInt) {
		for (int p = 0; p < vowelPlaces.length; p++)
			if (vowelPlaces[p] == pInt)
				return true;

		return false;
	}

}
