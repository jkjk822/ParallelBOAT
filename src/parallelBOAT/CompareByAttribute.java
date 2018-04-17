package parallelBOAT;

import java.util.Comparator;

public class CompareByAttribute implements Comparator<Article>{

    private int index;

    public CompareByAttribute(Attribute attribute) {
        this.index = attribute.getIndex();
    }

    public int compare(Article a, Article b) {
        if(a.getData()[index] instanceof Integer) {
            Integer aData = (Integer) a.getData()[index];
            Integer bData = (Integer) b.getData()[index];
            return aData.compareTo(bData);
        } else if (a.getData()[index] instanceof Double) {
            Double aData = (Double) a.getData()[index];
            Double bData = (Double) b.getData()[index];
            return aData.compareTo(bData);
        }
        return 0;
    }
}
