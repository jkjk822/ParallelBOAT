package parallelBOAT;

import java.util.Comparator;
import java.util.Objects;

public class CompareByAttribute implements Comparator<Article>{

    private static int index = 0;

    public CompareByAttribute(Attribute attribute) {
        this.index = attribute.ordinal();
    }

    public CompareByAttribute setAttribute(Attribute attribute) {
        this.index = attribute.ordinal();
        return this;
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
