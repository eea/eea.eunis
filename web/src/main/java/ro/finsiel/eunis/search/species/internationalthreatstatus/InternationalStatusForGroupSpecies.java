package ro.finsiel.eunis.search.species.internationalthreatstatus;


import ro.finsiel.eunis.jrfTables.species.internationalthreatstatus.InternationalThreatStatusDomain;
import ro.finsiel.eunis.jrfTables.species.internationalthreatstatus.InternationalThreatStatusPersist;

import java.util.List;
import java.util.Vector;


/**
 * Data factory for species->international-threat-status popup.
 *
 * @author finsiel
 */
public class InternationalStatusForGroupSpecies {
    private Vector countryForAnyGroup = new Vector();
    private Vector countryForAGroup = new Vector();
    private Vector statusForAnyGroupACountry = new Vector();
    private Vector statusForAGroupACountry = new Vector();

    /**
     * Constructor.
     */
    public InternationalStatusForGroupSpecies() {}

    /**
     * Getter.
     * @return countryForAnyGroup
     */
    public Vector getCountryForAnyGroup() {
        return countryForAnyGroup;
    }

    /**
     * Set new country if "any group" is selected.
     */
    public void setCountryForAnyGroup() {

        List countryListForAnyGroup = new InternationalThreatStatusDomain().findWhere(
                " trim(F.AREA_NAME_EN) not like 'ospar%' and (F.ISO_2L is null or trim(F.ISO_2L) = '') AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' GROUP BY F.AREA_NAME_EN");

        if (countryListForAnyGroup != null && countryListForAnyGroup.size() > 0) {
            for (int i = 0; i < countryListForAnyGroup.size(); i++) {
                countryForAnyGroup.addElement(((InternationalThreatStatusPersist) countryListForAnyGroup.get(i)));
            }
        }

    }

    /**
     * Getter.
     * @return countryForAGroup
     */
    public Vector getCountryForAGroup() {
        return countryForAGroup;
    }

    /**
     * Set country if an specific group is selected.
     * @param idGroup New group
     */
    public void setCountryForAGroup(String idGroup) {

        List countryListForAGroup = new InternationalThreatStatusDomain().findWhere(
                " D.ID_GROUP_SPECIES = " + idGroup
                + " AND trim(F.AREA_NAME_EN) not like 'ospar%' and (F.ISO_2L is null or trim(F.ISO_2L) = '') AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' GROUP BY F.AREA_NAME_EN");

        if (countryListForAGroup != null && countryListForAGroup.size() > 0) {
            for (int i = 0; i < countryListForAGroup.size(); i++) {
                countryForAGroup.addElement(((InternationalThreatStatusPersist) countryListForAGroup.get(i)));
            }
        }
    }

    /**
     * Getter.
     * @return statusForAnyGroupACountry
     */
    public Vector getStatusForAnyGroupACountry() {
        return statusForAnyGroupACountry;
    }

    /**
     * Set status for 'any group' and a specific country.
     * @param idCountry Id country
     */
    public void setStatusForAnyGroupACountry(String idCountry) {

        List statusListForAnyGroupACountry = new InternationalThreatStatusDomain().findWhere(
                " F.ID_COUNTRY = " + idCountry
                + " AND trim(F.AREA_NAME_EN) not like 'ospar%' and (F.ISO_2L is null or trim(F.ISO_2L) = '') AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' GROUP BY H.NAME");

        if (statusListForAnyGroupACountry != null && statusListForAnyGroupACountry.size() > 0) {
            for (int i = 0; i < statusListForAnyGroupACountry.size(); i++) {
                statusForAnyGroupACountry.addElement(((InternationalThreatStatusPersist) statusListForAnyGroupACountry.get(i)));
            }
        }
    }

    /**
     * Getter.
     * @return statusForAGroupACountry
     */
    public Vector getStatusForAGroupACountry() {
        return statusForAGroupACountry;
    }

    /**
     * Set new status for an specific group and a specific country.
     * @param idGroup group
     * @param idCountry country
     */
    public void setStatusForAGroupACountry(String idGroup, String idCountry) {

        List statusListForAGroupACountry = new InternationalThreatStatusDomain().findWhere(
                " D.ID_GROUP_SPECIES = " + idGroup + " AND F.ID_COUNTRY = " + idCountry
                + " AND trim(F.AREA_NAME_EN) not like 'ospar%' and (F.ISO_2L is null or trim(F.ISO_2L) = '') AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' GROUP BY H.NAME");

        if (statusListForAGroupACountry != null && statusListForAGroupACountry.size() > 0) {
            for (int i = 0; i < statusListForAGroupACountry.size(); i++) {
                statusForAGroupACountry.addElement(((InternationalThreatStatusPersist) statusListForAGroupACountry.get(i)));
            }
        }
    }

    // /**
    // * It used to obtain list with treath status for any group or a group identified by his id_group_species.
    // * @param id id group species
    // */
    // public InternationalStatusForGroupSpecies(String id) {
    // //any group
    // try {
    // //List statusListForAnyGroup = new InternationalThreatStatusDomain().findWhere("F.AREA_NAME_EN = 'EUROPE' AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' GROUP BY H.NAME");
    // List statusListForAnyGroup = new InternationalThreatStatusDomain().findWhere(" (F.ISO_2L is null or trim(F.ISO_2L) = '') AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' GROUP BY H.NAME");
    // if (statusListForAnyGroup != null && statusListForAnyGroup.size() > 0) {
    // for (int i = 0; i < statusListForAnyGroup.size(); i++) {
    // statusForAnyGroup.addElement(((InternationalThreatStatusPersist) statusListForAnyGroup.get(i)));
    // }
    // }
    // } catch (Exception e) {
    // logger.debug(e, e); 
    // }
    // // a group
    // try {
    // //List statusListForAGroup = new InternationalThreatStatusDomain().findWhere("F.AREA_NAME_EN = 'EUROPE' AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' AND D.ID_GROUP_SPECIES = '" + id + "' GROUP BY H.NAME");
    // List statusListForAGroup = new InternationalThreatStatusDomain().findWhere(" (F.ISO_2L is null or trim(F.ISO_2L) = '') AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' AND D.ID_GROUP_SPECIES = '" + id + "' GROUP BY H.NAME");
    // if (statusListForAGroup != null && statusListForAGroup.size() > 0) {
    // for (int i = 0; i < statusListForAGroup.size(); i++) {
    // System.out.println("===="+((InternationalThreatStatusPersist) statusListForAGroup.get(i)).getCommonName());
    // statusForAGroup.addElement(((InternationalThreatStatusPersist) statusListForAGroup.get(i)));
    // }
    // }
    // } catch (Exception e) {
    // logger.debug(e, e); 
    // }
    // }

}
