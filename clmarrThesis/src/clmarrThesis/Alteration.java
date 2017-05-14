package clmarrThesis;

import java.util.*;

public class Alteration {

	private List<CandRestrictPhone> priorContext, postContext;
	// necessary prior and posterior context, programmed as candPhones.
	// if they are empty there is no context requirement in that ordinal
	// direction.

	// targets map onto the outputs, so indices must match!
	// we use actual Phone type objects here.
	private List<List<Phone>> targets, destinations;
	// TODO find a way to deal with the scenario for when either one of the
	// targets..
	// ... or one of the destinations is a non-entity
	// ANSWER : for hte moment we exclude the possibility of a non-entity target
	// while a non-entity destination is represented as an empty List<Phone>();

	private String type; // may ultimately not use.
	// TODO decide about hte use of "type" .

	// default constructor
	public Alteration() {
		priorContext = new ArrayList<CandRestrictPhone>();
		postContext = new ArrayList<CandRestrictPhone>();
		targets = new ArrayList<List<Phone>>();
		destinations = new ArrayList<List<Phone>>();
	}

	/**
	 * context-free constructor
	 * 
	 * @param priorCont
	 *            -- prior context
	 * @param postCont
	 *            -- posterior context
	 * @precondition: targs.size() == dests.size()
	 * @precondition: for all segments in targs, there is none that is a sublist
	 *                contained in another.
	 */
	public Alteration(List<List<Phone>> targs, List<List<Phone>> dests) {
		priorContext = new ArrayList<CandRestrictPhone>();
		postContext = new ArrayList<CandRestrictPhone>();
		targets = new ArrayList<List<Phone>>(targs);
		destinations = new ArrayList<List<Phone>>(dests);
		assert (targets.size() == destinations
				.size()) : "Precondition violated: (targs.size() == dests.size()) is false! ";
	}

	/**
	 * constructor with context
	 * 
	 * @param priorCont
	 *            -- prior context
	 * @param postCont
	 *            -- posterior context
	 * @param targs
	 *            -- list of target segments
	 * @param dests
	 *            -- list of destination segments
	 * @precondition: targ.size() == dests.size()
	 * @precondition: for all segments in targs, there is none that is a sublist
	 *                contained in another.
	 */
	public Alteration(List<CandRestrictPhone> priorCont, List<CandRestrictPhone> postCont, List<List<Phone>> targs,
			List<List<Phone>> dests) {
		priorContext = (priorCont == null) ? new ArrayList<CandRestrictPhone>()
				: new ArrayList<CandRestrictPhone>(priorCont);
		postContext = (postCont == null) ? new ArrayList<CandRestrictPhone>()
				: new ArrayList<CandRestrictPhone>(postCont);
		targets = new ArrayList<List<Phone>>(targs);
		destinations = new ArrayList<List<Phone>>(dests);
		assert (targets.size() == destinations
				.size()) : "Precondition violated: (targs.size() == dests.size()) is false! ";
	}

	/**
	 * method RESULT : given lexicophonological @param input, @return how that
	 * would be modified, if at all, by this Alteration
	 */
	public List<Phonic> result(List<Phonic> input) {
		/*
		 * //TODO debugging if (targets.size() == 0)
		 * System.out.println("Initiating shift for null target"); if
		 * (destinations.size() == 0 )
		 * System.out.println("Initiating shift for null destination");
		 */

		List<Phonic> res = new ArrayList<Phonic>(input);

		int inpSize = input.size();
		int priorSize = priorContext.size();
		int postSize = postContext.size();

		// calculate minimum phone-sequence target size
		int minTargSize = targets.get(0).size(), i = 1;
		while (i < targets.size()) {
			minTargSize = Math.min(minTargSize, targets.get(i).size());
			i++;
		}

		// quickly end process by returning unchanged input if input is too
		// small given the phonetic length of the context requirements.
		if (inpSize < postSize + priorSize)
			return input;

		int p = priorSize;

		/**
		 * check if the target occurs at each index. We iterate from the
		 * beginning to the end of the word. We acknowledge the decision to
		 * iterate this way (progressive) is arbitrary, and that it may not be
		 * technically correct as some changes happen regressively. However it
		 * is most convenient for the time being.
		 */
		while (p < inpSize - postSize + 1 - minTargSize) {
			if (priorMatch(res, p)) // check if the prior context is appropriate
									// -- posterior context is checked in
									// another method
			{
				int matchInd = whichMatch(res, p); // checking if there is a
													// match with one of hte
													// targets, if any

				if (matchInd != -1) {
					res = mutate(res, matchInd, p);
					p += destinations.get(matchInd).size(); // skip all the
															// output of this
															// shift so we don't
															// double shift!
				} else
					p++;
			} else
				p++;
		}

		return res;

	}

	/**
	 * method WHICHMATCH
	 * 
	 * @param inpWord
	 *            -- phonological representation of the input
	 * @param inpInd
	 *            -- index for which we are checking if one of our target
	 *            segments starts at
	 * @return index of the targ segment, in our list targets, that starts at
	 *         this index; -1 if none of our target segments do (most common
	 *         output)
	 * @precondition : only one targ segment starts at the index. This should be
	 *               true if no targ segment is contained by another as a
	 *               sublist.
	 */
	public int whichMatch(List<Phonic> inpWord, int inpInd) {
		// if we have prior context requirements, check if they are found before
		// the input. If not, abortively return -1 (false, essentially).
		if (priorContext.size() != 0)
			if (!priorMatch(inpWord, inpInd))
				return -1;

		// check each of the possible targets. We arbitrarily go by the order
		// they are placed in the targets list.
		for (int it = 0; it < targets.size(); it++) {
			if (isMatch(inpWord, targets.get(it), inpInd))
				return it;
		}

		// if we have reached this point, none of hte potential targets have
		// been found to match.
		return -1;
	}

	/**
	 * method ISMATCH
	 * 
	 * @param inpWord
	 *            -- phonological representation of the input,
	 * @param targSegment
	 *            -- which of our (List<Phone) target segments we are checking
	 *            for, if we have multiple in the List<List<Phone>> targets.
	 * @param ind
	 *            -- start index for the target segment for which we are
	 *            checking
	 * @precondition : priorMatch(inpWord, ind) == true
	 * @return true iff a proper target lies at this index and it's posterior
	 *         context reqs are fulfilled (prior reqs should have already been
	 *         checked for.)
	 */
	public boolean isMatch(List<Phonic> inpWord, List<Phone> targSegment, int ind) {
		// return false for the case of an invalid index:
		if (ind >= inpWord.size() || ind < 0)
			return false;

		int indAfter = ind + targSegment.size(); // first index after the end of
													// the target segment. For
													// frequent use later

		// abortively return false in the case that the number of phones in the
		// word after ind is less than the phone-length of the targSegment
		if (indAfter - 1 >= inpWord.size())
			return false;

		// and return false if the target segment is not found at this index
		if (!foundTarget(inpWord.subList(ind, indAfter), targSegment))
			return false;

		// at this point we know the phone at the relevant index is indeed a
		// target

		// note that since they are already checked for in methods calling this
		// one, we should be confident the prior context requirements are met.
		// we do all the same things to check the posterior context.

		int postSize = postContext.size();

		if (postSize != 0) {
			if (postSize > inpWord.size() - indAfter)
				return false;

			for (int j = 0; j < postSize; j++) {
				CandRestrictPhone currReq = postContext.get(j);
				Phonic toCheck = inpWord.get(indAfter + j);

				if (!currReq.compare(toCheck))
					return false;
			}
		}

		// at this point all the requirements have been met, so we can return
		// true.

		return true;
	}

	/**
	 * method PRIORMATCH : checks if prior context is consistent wiht a given
	 * index
	 * 
	 * @param :
	 *            inpWord -- phonological representation of the input,
	 * @param :
	 *            ind -- starting index for which we are checking potential
	 *            matches with target segments
	 */
	public boolean priorMatch(List<Phonic> inpWord, int ind) {
		// abortively return true

		int priorSize = priorContext.size();

		// abortively return true if there is no specification on prior context
		if (priorSize == 0)
			return true;

		// abortively return false if the prior context is longer in phonetic
		// length than all of hte input word before ind
		if (priorSize > ind)
			return false;

		for (int i = 0; i < priorSize; i++) {
			CandRestrictPhone currReq = priorContext.get(priorSize - 1 - i);
			Phonic toCheck = inpWord.get(ind - 1 - i);
			if (!currReq.compare(toCheck))
				return false;
		}

		// if we made it to this point, we have a prior context match

		return true;
	}

	// TODO make sure this can handle the case where the either the dest is
	// NOTHING or targ is (starts as) NOTHING ~~ null phone question .
	/**
	 * method MUTATE :
	 * 
	 * @param :
	 *            inpWord -- phonological representation of the input
	 * @param :
	 *            targInd -- index of the matching target
	 * @param :
	 *            wordInd -- starting index of the target segment within the
	 *            input word
	 * @precondition : isMatch(inpWord,targets.get(targInd()),wordInd) == true
	 *               // VERY IMPORTANT!
	 * @precondition : destinations.get(targInd) is indeed the corresponding
	 *               destination for our target here
	 * @return : inpWord with the target segment replaced with it's
	 *         corresponding destination
	 */
	public List<Phonic> mutate(List<Phonic> inpWord, int targInd, int wordInd) {
		// TODO debugging
		/*
		 * System.out.println("inpWord"); for (Phonic iP : inpWord)
		 * System.out.print(iP.print()); System.out.print("\n");
		 */

		List<Phonic> fjale = new ArrayList<Phonic>(inpWord.subList(0, wordInd));
		fjale.addAll(inpWord.subList(wordInd + targets.get(targInd).size(), inpWord.size()));

		// TODO debugging
		/*
		 * System.out.println("Alteration, mutate : Destinations : ");
		 * for(List<Phone> dest : destinations) { for (Phone d : dest)
		 * System.out.print(d.print()); System.out.print(","); }
		 */
		// System.out.println("Alteration, mutate : targInd = "+targInd);

		List<Phone> destsToAdd = destinations.get(targInd);

		if (destsToAdd.isEmpty()) // i.e. null destination
			return fjale;
		else {
			fjale.addAll(wordInd, destsToAdd);
			return fjale;
		}

	}

	// override toString with vaguely historical phonology theoretical notation
	public String toString() {
		String output = "\n" + type + " Shift:\n";
		output += printSegmentList(targets) + "\n-->\n" + printSegmentList(destinations);
		int priorSize = priorContext.size(), postSize = postContext.size();
		if (priorSize > 0 || postSize > 0) {
			output += "\n/";
			// print each prior candRestrictPhone, wiht the number of places it
			// comes before the target noted, using the CandRestrictPhone
			// toString method.
			for (int i = 0; i < priorSize; i++)
				output += "\n" + (i - priorSize) + ": " + priorContext.get(i);
			// and do the same for each posterior candRestrictPhone
			for (int j = 0; j < postSize; j++)
				output += "\n" + (j + 1) + ": " + postContext.get(j);
		}

		return output;
	}

	// auxiliary method for toString -- print each segment in a list of segments
	// of phones.
	public String printSegmentList(List<List<Phone>> segmentList) {
		String toPrint = "(";
		for (List<Phone> segment : segmentList) {
			for (Phone phonePart : segment)
				toPrint += phonePart.print();
			toPrint += ",";
		}
		return toPrint.substring(0, toPrint.length() - 1) + ")";
	}

	// auxiliary method to check equality of a List<Phonic>, the input, and a
	// List<Phone> the target
	public boolean foundTarget(List<Phonic> input, List<Phone> targSeg) {
		// TODO debugging
		/*
		 * System.out.println("input: "); for (int i = 0; i <input.size(); i++)
		 * System.out.print(input.get(i).print()+",");
		 * System.out.println("\ntargSeg: "); for (int j = 0; j <targSeg.size();
		 * j++) System.out.print(targSeg.get(j).print()+",");
		 * System.out.print("\n");
		 */

		// abortively return false if their sizes do not match
		if (input.size() != targSeg.size())
			return false;

		// check equality of each index
		for (int i = 0; i < input.size(); i++) {

			// TODO debugging
			/*
			 * if (targSeg.get(i).getType().equals("Phthong") &&
			 * input.get(i).getType().equals("Phthong")) {
			 * System.out.println("checking index "+i+" of target for match..."
			 * +targSeg.get(i).equals(input.get(i)) );
			 * System.out.println("targSeg.get(i): "+targSeg.get(i).toString());
			 * System.out.println("input.get(i): "+input.get(i).toString());
			 * System.out.println("------\n"); }
			 */

			if (!targSeg.get(i).equals(input.get(i)))
				return false;
		}

		// if reached this point...
		return true;
	}

	// TODO note that this class has no equals() method. DO NOT TRY TO CALL
	// THESE IN OTHER CLASSES>
	// TODO one solution to this is to make a Restriction class which CandPhone
	// would be based on an aggregation of (i.e. [place = 80], [rounded = true]
	// etc)... maybe implement that.
	// TODO note this class also cannot be called with a contains() method if it
	// is in a list.
	// TODO if we ever do make a toString() method, it should probably be based
	// on the standard A -> B \ C_D etc.
}
