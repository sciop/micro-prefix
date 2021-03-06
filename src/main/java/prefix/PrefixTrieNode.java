package prefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrefixTrieNode extends TrieNode {
	
	protected void add(String s, int index) {
		if(index == s.length()) EOW = true;
		else {
			if(!map.containsKey(s.charAt(index))) map.put(s.charAt(index), new PrefixTrieNode());
			map.get(s.charAt(index)).add(s, index + 1);
		}
	}
	
	// Returns a (sorted) list of all strings below the node that start with pre
	public List<String> prefixQuery(String pre) {
		List<String> ans = prefixQuery(pre, 0);
		Collections.sort(ans);
		return ans;
	}
	
	private List<String> prefixQuery(String pre, int index) {
		List<String> ans = new ArrayList<String>();
		if(index < pre.length()) {
			if(!map.containsKey(pre.charAt(index))) return ans;
			return ((PrefixTrieNode) map.get(pre.charAt(index))).prefixQuery(pre, index + 1);
		}
		for(char c: map.keySet()) {
			ans.addAll(((PrefixTrieNode) map.get(c)).prefixQuery(pre + c, index + 1));
		}
		if(EOW) ans.add(pre);
		return ans;
	}
	
	// Returns a (sorted) list of all strings below the node that match exp:
	//	'.' -> any character
	//	'*' -> any character any times (incl. 0)
	
	// Probably very bad optimization since we have to prune duplicates at the end...
	// 		...but with Sets instead of Lists, it somehow takes us more time.
	public List<String> expQuery(String exp) {
		List<String> ans = expQuery(exp, 0);
		Set<String> unique = new HashSet<String>(ans);
		ans = new ArrayList<String>(unique);
		Collections.sort(ans);
		return ans;
	}
	
	private List<String> expQuery(String exp, int index) {
		List<String> ans = new ArrayList<String>();
		if(index >= exp.length()) {
			if(EOW) ans.add("");
			return ans;
		}
		List<String> aux;
		switch(exp.charAt(index)) {
			case '.':
				for(char c: map.keySet()) {
					aux = ((PrefixTrieNode) map.get(c)).expQuery(exp, index + 1);
					for(int i = 0; i < aux.size(); i++) aux.set(i, c + aux.get(i));
					ans.addAll(aux);
				}
				break;
			case '*':
				ans.addAll(this.expQuery(exp, index + 1));
				for(char c: map.keySet()) {
					aux = ((PrefixTrieNode) map.get(c)).expQuery(exp, index);
					for(int i = 0; i < aux.size(); i++) aux.set(i, c + aux.get(i));
					ans.addAll(aux);
				}
				break;
			case '\\':
				if(map.containsKey(exp.charAt(index + 1))) {
					aux = ((PrefixTrieNode) map.get(exp.charAt(index + 1))).expQuery(exp, index + 2);
					for(int i = 0; i < aux.size(); i++) aux.set(i, exp.charAt(index + 1) + aux.get(i));
					ans.addAll(aux);
				}
				break;
			default:
				if(map.containsKey(exp.charAt(index))) {
					aux = ((PrefixTrieNode) map.get(exp.charAt(index))).expQuery(exp, index + 1);
					for(int i = 0; i < aux.size(); i++) aux.set(i, exp.charAt(index) + aux.get(i));
					ans.addAll(aux);
				}
		}
		return ans;
	}
	
	/* Version with sets:
	 public List<String> expQuery(String exp) {
		List<String> ans = new ArrayList<String>(expQuery(exp, 0, ""));
		Collections.sort(ans);
		return ans;
	}
	
	private Set<String> expQuery(String exp, int index, String back) {
		Set<String> ans = new HashSet<String>();
		if(index >= exp.length()) {
			if(EOW) ans.add(back);
			return ans;
		}
		switch(exp.charAt(index)) {
			case '.':
				for(char c: map.keySet()) {
					ans.addAll(map.get(c).expQuery(exp, index + 1, back + c));
				}
				break;
			case '*':
				ans.addAll(this.expQuery(exp, index + 1, back));
				for(char c: map.keySet()) {
					ans.addAll(map.get(c).expQuery(exp, index, back + c));
				}
				break;
			case '\\':
				if(map.containsKey(exp.charAt(index + 1))) {
					ans.addAll(map.get(exp.charAt(index + 1)).expQuery(exp, index + 2, back + exp.charAt(index + 1)));
				}
				break;
			default:
				if(map.containsKey(exp.charAt(index))) {
					ans.addAll(map.get(exp.charAt(index)).expQuery(exp, index + 1, back + exp.charAt(index)));
				}
		}
		return ans;
	}
	*/
}
