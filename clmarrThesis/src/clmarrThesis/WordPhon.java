package clmarrThesis;

import java.util.*;

public class WordPhon {

	private List<Phonic> phoneSeq;

	// constructors
	public WordPhon() {
		phoneSeq = new ArrayList<Phonic>();
		phoneSeq.add(new PseudoPhone("word onset"));
		phoneSeq.add(new PseudoPhone("word coda"));
	}

	// clone constructor
	public WordPhon(WordPhon that) {
		phoneSeq = new ArrayList<Phonic>(that.getWordRep());
	}

	public WordPhon(List<Phone> phones) // input parameter assumed not to
										// already have the pseudoPhones!
	{
		phoneSeq = new ArrayList<Phonic>();
		phoneSeq.add(new PseudoPhone("word onset"));
		phoneSeq.addAll(phones);
		phoneSeq.add(new PseudoPhone("word coda"));
	}

	// mutators

	public void replacePhone(int index, Phone newPhone) throws IndexOutOfWordBoundsException // index
																								// must
																								// be
																								// in
																								// [1,
																								// phoneSeq.size()-1).
	{
		if (index == 0 || index == phoneSeq.size() - 1) // can't editing of the
														// word onset or coda
														// markers!
			throw new IndexOutOfWordBoundsException();
		else
			phoneSeq.set(index, newPhone);
	}

	public void replacePhoneSeq(int startIndex, int endIndex, List<Phone> newPhones)
			throws IndexOutOfWordBoundsException {
		if (startIndex == 0 || endIndex == phoneSeq.size() - 1)
			throw new IndexOutOfWordBoundsException();
		else {
			for (int i = startIndex; i < endIndex + 1; i++)
				phoneSeq.remove(i);
			phoneSeq.addAll(startIndex, newPhones);
		}
	}

	/**
	 * change the mutable Phonics (i.e. not the onset or word coda) as according
	 * to sound shift
	 * 
	 * @param shift
	 *            -- the sound shift currently acting upon the word NOTE :
	 *            DESTRUCTIVE (!!)
	 */
	public void applySoundShift(Alteration shift) {
		phoneSeq = shift.result(phoneSeq);
	}

	// accessors
	public List<Phonic> getWordRep() {
		return phoneSeq;
	}

	public Phonic getPhone(int index) // NOT modified-- can end up getting the
										// onset or coda if not careful.
	{
		return phoneSeq.get(index);
	}

	public List<Phonic> subSeq(int startInd, int endInd) {
		List<Phonic> seq = new ArrayList<Phonic>();
		if (startInd >= endInd)
			return seq; // which is currently empty.
		for (int i = startInd; i < endInd; i++)
			seq.add(phoneSeq.get(i));
		return seq;
	}

	// get all phones not including the pseudophones
	public List<Phonic> getAllPhones() {
		return this.subSeq(1, phoneSeq.size() - 1);
		// TODO modify this method if we ever start using the syllable break
		// pseudophones.
	}

	public int findPhone(Phone p) {
		for (int i = 1; i < phoneSeq.size() - 1; i++)
			if (p.equals(phoneSeq.get(i)))
				return i;
		return -1;
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < phoneSeq.size(); i++)
			s = s + phoneSeq.get(i).print();
		return s;
	}

	public boolean equals(Object other) {
		if (other instanceof WordPhon)
			return (this.toString() == other.toString());
		return false; // not an instance.
	}

}
