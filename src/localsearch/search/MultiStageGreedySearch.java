package localsearch.search;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;
import localsearch.selectors.MinMaxSelectorWithObj;
import localsearch.selectors.Res;

import java.util.HashMap;

public class MultiStageGreedySearch {

	/**
	 * @param
	 */
	
	public String name(){
		return "MultiStageGreedySearch";
	}
	public void search(IConstraint S, int maxTime, int maxIter, boolean verbose){
		VarIntLS[] x = S.getVariables();
		VarIntLS objective = null;
		//		San add code for objective
		for (int i = 0; i < x.length; i++) {
			if (x[i].getName().charAt(0) == 's') {
				objective = x[i];
				System.out.println(objective.getName() + "= " + i + " " + objective.getValue());
				break;
			}
		}

		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < x.length; i++) map.put(x[i], i);
		
		int it = 0;
		maxTime = maxTime * 1000;
		double t0 = System.currentTimeMillis();
		MinMaxSelectorWithObj mms = new MinMaxSelectorWithObj(S, objective);
		
		int best = S.violations() * 10000 + objective.getValue();
		int[] x_best = new int[x.length];
		while(it < maxIter && System.currentTimeMillis() - t0 < maxTime){
			// set s big
			objective.setValuePropagate(10000);

			Res res = mms._selectMostViolatingVariable();
			if (res.check) {
				int sel_v = mms.selectMostPromissingValue(res._sel_x);
				res._sel_x.setValuePropagate(sel_v);
			}
			int sel_v = mms.selectMostPromissingValue(objective, false);
			objective.setValuePropagate(sel_v);

			if(verbose)
				System.out.println(name() + "::search --> Step " + it + ", x[" + map.get(res._sel_x) + "] := " + sel_v + ", S = " + S.violations() + " -- " + objective.getValue());
			
			if(S.violations() * 10000 + objective.getValue() < best){
				best = S.violations() * 10000 + objective.getValue();
				for(int i = 0; i < x.length; i++)
					x_best[i] = x[i].getValue();
			}
			it++;
		}
		
		for(int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
