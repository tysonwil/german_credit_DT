import java.util.*;
import java.lang.Math;

/**
 * Fill in the implementation details of the class DecisionTree
 * using this file. Any methods or secondary classes
 * that you want are fine but we will only interact
 * with those methods in the DecisionTree framework.
 * 
 * You must add code for the 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 *
 * Usage: java HW2 <modeFlag> <trainFilename> <tuneFilename> <testFilename>
 */
public class DecisionTreeImpl extends DecisionTree {
	public static final String[] attr0 = {"A11", "A12", "A13", "A14"};
	public static final String[] attr1 = {"A20", "A21", "A22", "A23", "A24"};
	public static final String[] attr2 = {"A30", "A31", "A32", "A33", "A34", "A35", "A36", "A37", "A38", "A39", "A310"};
	public static final String[] attr3 = {"A41", "A42", "A43", "A44", "A45"};
	public static final String[] attr4 = {"A", "B"};
	public static final String[] attr5 = {"A", "B"};
	public static final String[] attr6 = {"A71", "A72"};
	Map<Integer,String[]> attributes = new HashMap<Integer,String[]>();
	Map<String,String> attr_text = new HashMap<String,String>();
	DecTreeNodeImpl root = null;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary
		// this is void purposefully
	}
	
	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train 
	 * 			the training set
	 */
	DecisionTreeImpl(DataSet train) {
		attributes.put(0, attr0);
		attributes.put(1, attr1);
		attributes.put(2, attr2);
		attributes.put(3, attr3);
		attributes.put(4, attr4);
		attributes.put(5, attr5);
		attributes.put(6, attr6);
		attr_text.put("0", "Status of existing checking account");
		attr_text.put("1", "Credit history");
		attr_text.put("2", "Purpose");
		attr_text.put("3", "Savings account/bonds");
		attr_text.put("4", "Duration in month");
		attr_text.put("5", "Credit amount");
		attr_text.put("6", "foreign worker");
		int one_count = 0; int two_count = 0;
		String maj_vote = null;
		List<String> valid_attr = new ArrayList<String>();
		for (int z=0; z<7; z++)
			valid_attr.add(Integer.toString(z));
		for (Instance i : train.instances) {
			if (i.label.equals("1")) one_count++;
			else two_count++;
		}
		if (one_count >= two_count) maj_vote = "1";
		else maj_vote = "2";

		root = buildTree(train.instances, valid_attr, maj_vote, "ROOT");	
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * 
	 * @param train 
	 * 			the training set
	 * @param tune 
	 * 			the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		attributes.put(0, attr0);
		attributes.put(1, attr1);
		attributes.put(2, attr2);
		attributes.put(3, attr3);
		attributes.put(4, attr4);
		attributes.put(5, attr5);
		attributes.put(6, attr6);
		attr_text.put("0", "Status of existing checking account");
		attr_text.put("1", "Credit history");
		attr_text.put("2", "Purpose");
		attr_text.put("3", "Savings account/bonds");
		attr_text.put("4", "Duration in month");
		attr_text.put("5", "Credit amount");
		attr_text.put("6", "foreign worker");
		int one_count = 0; int two_count = 0;
		String maj_vote = null;
		List<String> valid_attr = new ArrayList<String>();
		for (int z=0; z<7; z++)
			valid_attr.add(Integer.toString(z));

		root = buildTree(train.instances, valid_attr, "1", "ROOT");

		Map<DecTreeNodeImpl,Double> accuracy = new HashMap<DecTreeNodeImpl,Double>();

		prune(root, root, tune.instances, accuracy);
		
	}

	@Override
  /**
   * Evaluates the learned decision tree on a test set.
   * @return the label predictions for each test instance 
   * 	according to the order in data set list
   */
	public String[] classify(DataSet test) {
		int count = 0;
		String[] labels = new String[test.instances.size()];

		for (Instance i : test.instances) {
			String l = traverse(root, i.attributes);
			//System.out.println("-------" + l);
			labels[count] = l;
			count++;
		}
		return labels;
	}

	@Override
	/**
	 * Prints the tree in specified format. It is recommended, but not
	 * necessary, that you use the print method of DecTreeNode.
	 * 
	 * Example:
	 * Root {Existing checking account?}
	 *   A11 (2)
	 *   A12 {Foreign worker?}
	 *     A71 {Credit Amount?}
	 *       A (1)
	 *       B (2)
	 *     A72 (1)
	 *   A13 (1)
	 *   A14 (1)
	 *         
	 */
	public void print() {
		
		root.print(1);
		
	}

	public DecTreeNodeImpl buildTree(List<Instance> instances, List<String> valid_attr, String maj_vote, String prev_attr) {

		int num_instances = instances.size();
		int one_count = 0; int two_count = 0;
		double midpoint = 0.0;

		if (num_instances == 0) {
			//System.out.println("NONE LEFT");
			return new DecTreeNodeImpl(maj_vote, null, prev_attr, true, 0.0, 0.0);
		}

		for (Instance i : instances) {
			if (i.label.equals("1")) one_count++;
			else two_count++;
			if (one_count >= two_count) maj_vote = "1";
			else maj_vote = "2";
		}
		if (one_count == num_instances || two_count == num_instances) {
			//System.out.println("PURE");
			return new DecTreeNodeImpl(maj_vote, null, prev_attr, true, 0.0, 0.0);
		}
		if (valid_attr.size() == 0) {
			//System.out.println("NO Q");
			return new DecTreeNodeImpl(maj_vote, null, prev_attr, true, 0.0, 0.0);
		}

		int q = findInfoGain(instances, valid_attr);
		//System.out.println("The best question is attr: " + q);
		String q_attr = Integer.toString(q);
		List<String> valid_attr_cpy = new ArrayList<String>(valid_attr);
		valid_attr_cpy.remove(q_attr);
		
		String[] children = attributes.get(q);

		DecTreeNodeImpl my_node = new DecTreeNodeImpl(maj_vote, attr_text.get(q_attr), prev_attr, false, 0.0, 0.0);

		for (String child : children) {
			List<Instance> new_list = new ArrayList<Instance>();
			
			if (child.equals("A")) {
				midpoint = findMidpoint(instances, q);
				if (q==4) my_node.midpoint1 = midpoint;
				else my_node.midpoint2 = midpoint;
				for (Instance j : instances) {
					if (Double.parseDouble(j.attributes.get(q)) <= midpoint) {
						new_list.add(j);
					}
				}
			}
			
			else if (child.equals("B")) {
				midpoint = findMidpoint(instances, q);
				if (q==4) my_node.midpoint1 = midpoint;
				else my_node.midpoint2 = midpoint;
				for (Instance k : instances) {
					if (Double.parseDouble(k.attributes.get(q)) > midpoint) {
						new_list.add(k);
					}
				}
			}
			
			else {
				for (Instance l : instances) {
					if (l.attributes.get(q).equals(child)) {
						new_list.add(l);
					}
				}
			}
			DecTreeNodeImpl c = buildTree(new_list, valid_attr_cpy, maj_vote, child);
			my_node.addChild(c);
		}
		return my_node;
	}

	/**
	* Find information gain for a given attribute
	* I(T;a) = H(T) - H(T|a)
	*/
	public int findInfoGain(List<Instance> instances, List<String> valid_attr) {
		Map<String,Double> info_gain = new HashMap<String,Double>();

		// All of our needed probabilities
		for (String a : valid_attr) {
			int num_attr = Integer.parseInt(a);
			Map<String,Double> probabilities = new HashMap<String,Double>();
			String[] curr_attr = attributes.get(num_attr);

			probabilities = findProb(instances, num_attr);
			for (String s : curr_attr) {
				if (!probabilities.containsKey(s)) probabilities.put(s,0.0);
				if (!probabilities.containsKey("one|"+s)) probabilities.put("one|"+s,0.0);
				if (!probabilities.containsKey("two|"+s)) probabilities.put("two|"+s,0.0);
			}
			//System.out.println("\n------------\nATTRIBUTE " + num_attr + "\n------------");
			for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
	    		//System.out.println(entry.getKey() + " " + entry.getValue());
	    	}

	    	// H(one) = -p_one*log2(p_one) - p_two*log2(p_two)
	    	double p_one = probabilities.get("one");
	    	double p_two = probabilities.get("two");
	    	double h_one = (-1 * p_one * logTwo(p_one)) - (p_two * logTwo(p_two));

	    	// H(one|a) = sum(all_v)[ p_v * ((-p_one_v*log2(p_one_v)) - (p_two_v*log2(p_two_v))) ]
	    	double h_one_v = 0;
		   	for (String curr : curr_attr) {
	    		if (!probabilities.containsKey(curr)) continue;
	    		double p_v = probabilities.get(curr);
	    		double p_one_v = probabilities.get("one|" + curr);
	    		double p_two_v = probabilities.get("two|" + curr);
	    		h_one_v += p_v * ((-1 * p_one_v * logTwo(p_one_v)) - (p_two_v * logTwo(p_two_v)));
	    	}
	    	double ig = h_one - h_one_v;
	    	info_gain.put(a, ig);
	    	//System.out.println("Info gain: " + ig);
	    }

	    Map.Entry<String, Double> maxEntry = null;
		for (Map.Entry<String, Double> entry : info_gain.entrySet()) {
			//System.out.println("Key: "+entry.getKey() + "value "+entry.getValue());
    		if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
        		maxEntry = entry;
    		}
		}
	    //System.out.println("MAX KEY " + maxEntry.getKey());
	    return Integer.parseInt(maxEntry.getKey());
	}

	/**
	 * Finds all needed probabilities
	 */
	public Map<String,Double> findProb(List<Instance> instances, int attr) {

		// All of our needed probabilities
		Map<String,Double> probabilities = new HashMap<String,Double>();
		// Count number of occurances of each feature
		Map<String,Integer> counts = new HashMap<String,Integer>();
		List<String> numerical = null;

		boolean to_discrete = false;
		if (attr == 4 || attr == 5) {
			numerical = toDiscrete(instances, attr);
			to_discrete = true;
		}

		int num_instances = instances.size();		//total number of instances to analyze
		int z = 1;
		int j = 0;
		int count = 0;
		int one_count = 0; int two_count = 0;		//labels = 1, 2

		for (Instance i : instances) {
			//System.out.println(z + ": " + i.label);
			String a = null;
			if (to_discrete) {
				a = numerical.get(j);
				j++;
			}
			else {
				a = i.attributes.get(attr);
			}
			if (i.label.equals("1")) {
				one_count++;
				String conditional = "one|" + a;
				count = counts.containsKey(conditional) ? counts.get(conditional) : 0;
				counts.put(conditional, count + 1);
			}
			else {
				two_count++;
				String conditional = "two|" + a;
				count = counts.containsKey(conditional) ? counts.get(conditional) : 0;
				counts.put(conditional, count + 1);
			}
			count = counts.containsKey(a) ? counts.get(a) : 0;
			counts.put(a, count + 1);
			z++;
		}
		probabilities.put("one", (double)one_count/num_instances);
		probabilities.put("two", (double)two_count/num_instances);

		for (Map.Entry<String, Integer> entry : counts.entrySet()) {
			double prob = 0;
    		String key = entry.getKey();
    		Integer value = entry.getValue();
    		if (key.contains("|")) {
    			String first = key.split("\\|")[1];
    			prob = (double)value/counts.get(first);
    			probabilities.put(key, prob);
    		}
    		else {
    			prob = (double)value/num_instances;
    			probabilities.put(key, prob);
    		}
		}
		return probabilities;
	}

	// Convert continuous attributes to discrete
	public List<String> toDiscrete(List<Instance> instances, int attr) {
		List<String> discrete = new ArrayList<String>();
		int max = Integer.parseInt(instances.get(0).attributes.get(attr));
		int min = max;
		for (Instance i : instances) {
			int a = Integer.parseInt(i.attributes.get(attr));
			if (a > max)
				max = a;
			if (a < min)
				min = a;
		}
		double midpoint = .5 * (max+min);

		for (Instance j : instances) {
			int v = Integer.parseInt(j.attributes.get(attr));
			if (v <= midpoint)
				discrete.add("A");
			else
				discrete.add("B");
		}
		return discrete;
	}

	// Finds log base 2 of a double
	public double logTwo(double a) {
		if (a == 0.0) return 0.0;
		double b = Math.log(a)/Math.log(2);
		return b;
	}

	public double findMidpoint(List<Instance> instances, int attr) {
		int max = Integer.parseInt(instances.get(0).attributes.get(attr));
		int min = max;
		for (Instance i : instances) {
			int a = Integer.parseInt(i.attributes.get(attr));
			if (a > max)
				max = a;
			if (a < min)
				min = a;
		}
		return .5 * (max+min);
	}

	public String traverse(DecTreeNodeImpl root, List<String> instances) {

		String final_label = null;

		if (root.terminal) {
			//	System.out.println("DEAD : label: " + root.label +" "+root.parentAttributeValue);
			return root.label;
		}

		for (DecTreeNodeImpl child : root.children) {
			double mdpnt = 0;
			double aa = 0;
			//System.out.println("child PAV: "+child.parentAttributeValue);
			//System.out.println("child : "+child.attribute);
			if (child.attribute != null) {
				if (child.attribute.equals("Duration in month")) {
					mdpnt = child.midpoint1;
					//System.out.println("MIDPOINT: " +mdpnt);
					aa = Integer.parseInt(instances.get(4));
					if (mdpnt >= aa) {
						//System.out.println("DOWN A");
						return traverse(child.children.get(0), instances);
					}
					else {
						//System.out.println("DOWN B");
						return traverse(child.children.get(1), instances);
					}
				}
				else if (child.attribute.equals("Credit amount")) {
					mdpnt = child.midpoint2;
					//System.out.println("MIDPOINT: " +mdpnt);
					aa = Integer.parseInt(instances.get(5));
					if (mdpnt >= aa) {
						//System.out.println("DOWN A");
						return traverse(child.children.get(0), instances);
					}
					else {
						//System.out.println("DOWN B");
						return traverse(child.children.get(1), instances);
					}
				}
			}
			if (instances.contains(child.parentAttributeValue)) {
				//System.out.println("children"+child.children.get(0).parentAttributeValue);
				//System.out.println("traverse "+child.parentAttributeValue);
				return traverse(child, instances);
			}
		}
		return root.label;
	}

	public DecTreeNodeImpl prune(DecTreeNodeImpl root, DecTreeNodeImpl curr, List<Instance> instances, Map<DecTreeNodeImpl,Double> accuracy) {

		int one = 0;
		int two = 0;
		int yes = 0;
		int count = 0;
		int count2 = 0;
		String maj_vote = null;
		String[] labels = new String[instances.size()];
		String[] lab = new String[instances.size()];
		String b_label = null;
		List<DecTreeNodeImpl> b_children = null;

		if (curr.terminal) return curr;

		for (Instance i : instances) {
			String label = traverse(curr, i.attributes);
			labels[count] = label;
			count++;
		}
		for (String l : labels) {
			if (l.equals("1")) one++;
			else two++;
		}
		if (one >= two) maj_vote = "1";
		else maj_vote = "2";

		b_label = curr.label;
		b_children = curr.children;
		curr.label = maj_vote;
		curr.children = null;
		curr.terminal = true;

		for (Instance j : instances) {
			String l = traverse(root, j.attributes);
			lab[count2] = l;
			String real = instances.get(count2).label;
			count2++;
			if (l.equals(real)) yes++;
		}

		accuracy.put(curr, (double)yes/count);

		curr.label = b_label;
		curr.children = b_children;
		curr.terminal = false;
		
		for (DecTreeNodeImpl c : curr.children) {
			return prune(root, c, instances, accuracy);
		}
		return root;
	}
}

