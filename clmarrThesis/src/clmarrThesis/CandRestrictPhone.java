package clmarrThesis;

import java.util.*;

//an alternative to CandPhone, which utilizes the Restriction class and allows for easy printing 
// on the other hand, it is much more difficult and costly if we ever need to check the equality of two of these objects, than it would be for checking two CandPhones
public class CandRestrictPhone {

	private List<Restriction> specs;
	// list of all restrictions on what the Phonic object that gets compared to
	// can be

	// the number of POSITIVE restrictions that exist for each variable, indexed
	// by the name of the variable itself.
	// note negative restrictions are not counted, because we edo not have to
	// deal with calculating a margin for safety for them...
	// ... as if they return false, it is automatically considered to be a
	// mismatch between the CandRestrictPhone and the Phone
	// very important -- STRICTLY internal use ONLY
	private Map<String, Integer> varCount;

	// internal use only : all possible variable types
	private final List<String> possVars = Arrays
			.asList("type,place,manner,coArtic,rounded,nasal,lateral,rhotic,voiced,aspirated,stress,length,rise,fall"
					.split(","));

	// constructors follow :

	// default empty constructor-- default construction of varCount, specs as
	// empty list.
	public CandRestrictPhone() {
		specs = new ArrayList<Restriction>();
		varCount = new HashMap<String, Integer>();

		// initialize varCount:
		for (String possVar : possVars)
			varCount.put(possVar, 0);
	}

	// clone constructor
	public CandRestrictPhone(CandRestrictPhone that) {
		specs = new ArrayList<Restriction>(that.specs);
		varCount = new HashMap<String, Integer>(that.varCount);
		// TODO hopefully this doesn't have any long term side effects of things
		// getting modified outside their class. Make sure this is true though.
	}

	public CandRestrictPhone(Collection<Restriction> newSpecs) {
		specs = new ArrayList<Restriction>(newSpecs);
		varCount = new HashMap<String, Integer>();

		// initialize varCount
		for (String possVar : possVars)
			varCount.put(possVar, 0);

		// count up in varCount for all the specs we already have
		for (Restriction spec : newSpecs) {
			String varble = spec.getVar();
			if (spec.getTruth()) // increment varCount if and only of the truth
									// value is positive, as varCount is only
									// used for hte positives
			{
				varCount.put(varble, varCount.get(varble) + 1);
			}
		}

	}

	// String version of the above constructor, with different new specs
	// separated by COMMAS --- IMPORTANT THAT COMMAS ARE USED.
	public CandRestrictPhone(String newSpecs) {
		varCount = new HashMap<String, Integer>();

		// initialize varCount
		for (String possVar : possVars) {
			varCount.put(possVar, 0);
		}

		specs = new ArrayList<Restriction>();
		String[] specInputs = newSpecs.replaceAll(" ", "").split(",");

		for (int i = 0; i < specInputs.length; i++) {
			Restriction newSpec = new Restriction(specInputs[i]);
			String varble = newSpec.getVar();

			if (newSpec.getTruth()) {
				varCount.put(varble, ((varCount.containsKey(varble) ? varCount.get(varble).intValue() : 0)) + 1); // the
																													// heavy
																													// text
																													// on
																													// the
																													// left
																													// is
																													// to
																													// prevent
																													// us
																													// trying
																													// to
																													// access
																													// a
																													// null
																													// value
																													// because
																													// the
																													// count
																													// is
																													// zero
																													// and
																													// it
																													// hadn't
																													// yet
																													// been
																													// added
			}
			specs.add(newSpec);
		}
	}

	// mutators-- all are only for specs
	// add one spec and increment varCount
	public void addSpec(Restriction newSpec) {
		String varble = newSpec.getVar();
		if (newSpec.getTruth())
			varCount.put(varble, varCount.get(varble).intValue() + 1);
		specs.add(newSpec);
	}

	// same as above for multiple specs, in case we want to cut down on running
	// time with fewer function calls.
	public void addSpecs(Collection<Restriction> newSpecs) {
		for (Restriction newSpec : newSpecs) {
			String varble = newSpec.getVar();
			if (newSpec.getTruth())
				varCount.put(varble, varCount.get(varble).intValue() + 1);
			specs.add(newSpec);
		}
	}

	// remove and return the spec at a given index
	public Restriction removeSpec(int ind) {
		String varble = specs.get(ind).getVar();
		if (specs.get(ind).getTruth())
			varCount.put(varble, varCount.get(varble).intValue() + 1);
		return specs.remove(ind);
	}

	// one accessor, probably for minimal use
	public List<Restriction> getSpecs() {
		return specs;
	}

	// generic methods:

	// note we have NO equals method -- may need to make some sort of hybrid
	// between this and the normal candPhone to do that.

	// for comparing to a Phonic
	public boolean compare(Phonic candidate) {
		// to compare, we make a dummy of varCount called safetyMargins
		// if a negative constraint is violated (like "not velar") we obviously
		// immediately return false
		// but if, for a variable with multiple Restrictions, a positive
		// constraint is violated (i.e. "is velar"),
		// we technically could still end up returning true if another one of
		// hte same varialbe type is fulfilled (i.e. "is dental", if that is the
		// other)
		// so instead we decrease our "safety margin", only returning false for
		// the failure of a positive constraint once this margin hits 0.

		// initialize safetyMargins:
		Map<String, Integer> safetyMargins = new HashMap<String, Integer>(varCount);

		// now check each Restriction on the candidate
		for (Restriction spec : specs) {

			if (spec.compare(candidate) == false) {
				if (spec.getTruth()) // i.e. if this a positive constraint
				{
					String varble = spec.getVar();
					int prevMargin = safetyMargins.get(varble);
					if (prevMargin == 1)
						return false;
					else if (prevMargin > 1)
						safetyMargins.put(varble, prevMargin - 1);
					else
						System.out.println("ERROR: Safety margin of less than 1 found!");
				} else // we have a negative constraint and we violated it --
						// immediately return false.
					return false;
			}
		}

		// if we reached this point...
		return true;
	}

	public String toString() {
		String output = "[Specs: ";
		for (Restriction spec : specs)
			output = output + spec + "|"; // using toString method of
											// Restriction
		return output.substring(0, output.length() - 1) + "]";
	}

}
