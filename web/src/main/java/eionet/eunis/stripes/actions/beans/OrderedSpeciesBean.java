package eionet.eunis.stripes.actions.beans;

import org.apache.poi.ss.formula.functions.T;

public class OrderedSpeciesBean extends SpeciesBean {
    private int order = 0;
    public OrderedSpeciesBean(SpeciesType speciesType, String scientificName, String commonName, String group, Object source, String natura2000Code, String url, Integer idNatureObject, int order) {
        super(speciesType, scientificName, commonName, group, source, natura2000Code, url, idNatureObject);
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(SpeciesBean o) {
        if(!(o instanceof OrderedSpeciesBean)) {
            return super.compareTo(o);
        }
        else {
            OrderedSpeciesBean osb = (OrderedSpeciesBean)o;

            if(osb.getOrder() == this.getOrder()){
                return super.compareTo(o);
            } else {
                return this.getOrder() > osb.getOrder() ? 1 : -1;
            }
        }
    }
}
