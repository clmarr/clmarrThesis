package clmarrThesis;

// Master class for all things that can hold the place of a phone(me) in a word -- to include pseudoPhones
public abstract class Phonic {

	protected String type; // type of the phone (vowel, consonant, phthong
							// [under vowel] or PseudoPhone).. but this isn't
							// implemented here!

	abstract public String print();

	abstract public boolean equals(Object other);

	public String getType() {
		return type;
	}

	abstract public int getPlace();

	abstract public int getManner();

	abstract public int getCoArtic();

	abstract public boolean isRounded();

	abstract public boolean isNasal();

	abstract public boolean isLateral();

	abstract public boolean isRhotic();

	abstract public boolean isVoiced();

	abstract public boolean isAspirated();

	abstract public boolean isStressed(); // true iff 1, else false.

	abstract public int getStress();

	abstract public int getLength();

	abstract public int[][] getRise();

	abstract public int[][] getFall();

}
