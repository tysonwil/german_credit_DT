import java.util.ArrayList;
import java.util.List;

/**
 * Possible class for internal organization of a decision tree.
 * Included to show standardized output method, print().
 * 
 * Do not modify. If you use,
 * create child class DecTreeNodeImpl that inherits the methods.
 * 
 */
public class DecTreeNodeImpl {
	String label;
	String attribute;
	String parentAttributeValue; // if is the root, set to "ROOT"
	boolean terminal;
	List<DecTreeNodeImpl> children;
	double midpoint1;
	double midpoint2;

	DecTreeNodeImpl(String _label, String _attribute, String _parentAttributeValue, boolean _terminal, double _midpoint1, double _midpoint2) {
		label = _label;
		attribute = _attribute;
		parentAttributeValue = _parentAttributeValue;
		terminal = _terminal;
		midpoint1 = _midpoint1;
		midpoint2 = _midpoint2;
		if (_terminal) {
			children = null;
		} else {
			children = new ArrayList<DecTreeNodeImpl>();
		}
	}

	/**
	 * Add child to the node.
	 * 
	 * For printing to be consistent, children should be added
	 * in order of the attribute values as specified in the
	 * dataset.
	 */
	public void addChild(DecTreeNodeImpl child) {
		if (children != null) {
			children.add(child);
		}
	}
	
	/**
	 * Prints the subtree of the node
	 * with each line prefixed by k * 4 blank spaces.
	 */
	public void print(int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		sb.append(parentAttributeValue);
		if (terminal) {
			sb.append(" (" + label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + attribute + "?}");
			System.out.println(sb.toString());
			for(DecTreeNodeImpl child: children) {
				child.print(k+1);
			}
		}
	}
}