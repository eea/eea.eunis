/*
 * $Id
 */

package ro.finsiel.eunis.jrfTables;


import net.sf.jrf.domain.PersistentObject;


/**
 *
 * @version $Revision$ $Date$
 **/
public class Chm62edtHabitatMapsPersist extends PersistentObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String HabitatCode = null;
    private String HabitatName = null;
    private boolean DistributionMap = null;
    private boolean SuitabilityMap = null;
    private boolean ProbabilityMap = null;

    public String getHabitatCode() {
        return HabitatCode;
    }

    public void setHabitatCode(HabitatCode habitatCode) {
        HabitatCode = habitatCode;
    }

    public String getHabitatName() {
        return HabitatName;
    }

    public void setHabitatName(String name) {
        HabitatName = name;
    }

    public boolean getDistributionMap() {
        return DistributionMap;
    }

    public void setDistributionMap(boolean distributionMap) {
        DistributionMap = distributionMap;
    }

     public boolean getSuitabilityMap() {
        return SuitabilityMap;
    }

    public void setSuitabilityMap(boolean suitabilityMap) {
        SuitabilityMap = suitabilityMap;
    }

    public boolean getProbabilityMap() {
        return ProbabilityMap;
    }

    public void setProbabilityMap(boolean probabilityMap) {
        ProbabilityMap = probabilityMap;
    }

}
