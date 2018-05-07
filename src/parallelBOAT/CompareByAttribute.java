package parallelBOAT;

import java.util.Comparator;

// Class created in order to sort data (list of articles) based on a specific attribute value
public class CompareByAttribute implements Comparator<Article>{

    private int index;

    public CompareByAttribute(Attribute attribute) {
        this.index = attribute.getIndex();
    }

    public int compare(Article a, Article b) {
        if(a.getData()[index] instanceof Integer) {
            int x = (int) a.getData()[index];
            int y = (int) b.getData()[index];
            return Integer.compare(x,y);
        } else if (a.getData()[index] instanceof Double) {
            double x = (double) a.getData()[index];
            double y = (double) b.getData()[index];
            return Double.compare(x,y);
        }
        return 0;
    }
}
