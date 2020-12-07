package localsearch.selectors;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class MinMaxSelectorWithObj extends MinMaxSelector {

    /**
     * @param args
     */

    protected VarIntLS _objective;

    public MinMaxSelectorWithObj (IConstraint S, VarIntLS objective){
        super(S);
        _objective = objective;
    }
    public Res _selectMostViolatingVariable(){
        int sel_i = -1;
        int maxV = -1;
        _L.clear();
        boolean check = true;
        for(int i = 0; i < _vars.length; i++){
//            if (_vars[i].getName().charAt(0) == 's') {
//                check = false;
//            }
            int v = _S.violations(_vars[i]);
            if(maxV < v){
                maxV = v;
                _L.clear();
                _L.add(i);
            }else if(maxV == v){
                _L.add(i);
            }
        }
        sel_i = _L.get(_R.nextInt(_L.size()));
        System.out.println(_L.size());
        if (maxV == 1) {
            System.out.println(_L);
        }
        if (_vars[sel_i].getName().charAt(0) == 's') {
            check = false;
        }
        return new Res(_vars[sel_i], check);
    }
    public int selectMostPromissingValue(VarIntLS x, boolean check){
        int sel_v = -1;
        int minD = 10000000;
        _L.clear();
        if (check) {
            for (int v = x.getMinValue(); v <= x.getMaxValue(); v++) {
                int d = _S.getAssignDelta(x, v);
                if (minD > d) {
                    minD = d;
                    _L.clear();
                    _L.add(v);
                } else if (minD == d) {
                    _L.add(v);
                }
            }
        } else {
            for (int v = x.getMinValue(); v <= x.getMaxValue(); v++) {
                int d = _S.getAssignDelta(x, v) * 10000 - x.getValue() + v;
                if (minD > d) {
                    minD = d;
                    _L.clear();
                    _L.add(v);
                } else if (minD == d) {
                    _L.add(v);
                }
            }
        }
        sel_v = _L.get(_R.nextInt(_L.size()));
        System.out.println("san " + _L.size());
        return sel_v;
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

}
