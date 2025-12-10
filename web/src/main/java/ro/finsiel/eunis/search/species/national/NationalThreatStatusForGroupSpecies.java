package ro.finsiel.eunis.search.species.national;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.jrfTables.species.national.NationalThreatStatusDomain;
import ro.finsiel.eunis.jrfTables.species.national.NationalThreatStatusPersist;

import java.util.Vector;
import java.util.List;


/**
 * Used to retrieve data in species-threat-national.
 * @author finsiel
 */
public class NationalThreatStatusForGroupSpecies {
    private Vector CountriesForAnyGroup = new Vector();
    private Vector CountriesForAGroup = new Vector();
    private Vector ThreatStatusForAnyGroupAndACountry = new Vector();
    private Vector ThreatStatusForAGroupAndACountry = new Vector();
    private Vector ThreatStatusForAnyGroupAndAnyCountry = new Vector();
    private Vector ThreatStatusForAGroupAndAnyCountry = new Vector();
    private String idGroup = null;
    private String idCountry = null;

    private static final Logger logger = Logger.getLogger(NationalThreatStatusForGroupSpecies.class);

    /**
     * Ctor.
     * @param idGroup ID of the species group (ID_GROUP)
     * @param idCountry ID of the country (ID_COUNTRY)
     */
    public NationalThreatStatusForGroupSpecies(String idGroup, String idCountry) {
        this.idGroup = idGroup;
        this.idCountry = idCountry;

        /*
         this.setCountriesForAGroup();
         this.setCountriesForAnyGroup();
         this.setThreatStatusForAGroupAndACountry();
         this.setThreatStatusForAnyGroupAndACountry();
         */
    }

    /**
     * Used to find countries for any species group with species with threat status.
     */
    public void setCountriesForAnyGroup() {
        Vector results = new Vector();

        try {
            List listPersistentObject = new NationalThreatStatusDomain().findWhere(
                    "F.ISO_2L<>'' " + "AND F.ISO_2L<>'null' " + "AND F.ISO_2L IS NOT NULL "
                    + "AND F.SELECTION <> 0 AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' GROUP BY F.AREA_NAME_EN");

            if (listPersistentObject != null && listPersistentObject.size() > 0) {
                for (int i = 0; i < listPersistentObject.size(); i++) {
                    NationalThreatStatusPersist obj = (NationalThreatStatusPersist) listPersistentObject.get(i);

                    results.addElement(obj);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        CountriesForAnyGroup = results;
    }

    /**
     * Used to obtain countries for a group species with species with threat status.
     */
    public void setCountriesForAGroup() {
        Vector results = new Vector();

        try {
            List listPersistentObject = new NationalThreatStatusDomain().findWhere(
                    "F.ISO_2L<>'' " + "AND F.ISO_2L<>'null' " + "AND F.ISO_2L IS NOT NULL " + "AND F.SELECTION <> 0 "
                    + "AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' " + "AND D.ID_GROUP_SPECIES='" + idGroup
                    + "' GROUP BY F.AREA_NAME_EN");

            if (listPersistentObject != null && listPersistentObject.size() > 0) {
                for (int i = 0; i < listPersistentObject.size(); i++) {
                    NationalThreatStatusPersist obj = (NationalThreatStatusPersist) listPersistentObject.get(i);

                    results.addElement(obj);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        CountriesForAGroup = results;
    }

    /**
     * Used to obtain threat status list for pair (any group species, country).
     */
    public void setThreatStatusForAnyGroupAndACountry() {
        Vector results = new Vector();

        try {
            List listPersistentObject = new NationalThreatStatusDomain().findWhere(
                    "F.ISO_2L<>'' " + "AND F.ISO_2L<>'null' " + "AND F.ISO_2L IS NOT NULL " + "AND F.SELECTION <> 0 "
                    + "AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' " + "AND F.ID_COUNTRY='" + idCountry + "' GROUP BY H.NAME");

            if (listPersistentObject != null && listPersistentObject.size() > 0) {
                for (int i = 0; i < listPersistentObject.size(); i++) {
                    NationalThreatStatusPersist obj = (NationalThreatStatusPersist) listPersistentObject.get(i);

                    results.addElement(obj);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        ThreatStatusForAnyGroupAndACountry = results;
    }

    /**
     * Used to obtain threat status list for pair ( group species, country).
     */
    public void setThreatStatusForAGroupAndACountry() {
        Vector results = new Vector();

        try {
            List listPersistentObject = new NationalThreatStatusDomain().findWhere(
                    "F.ISO_2L<>'' " + "AND F.ISO_2L<>'null' " + "AND F.ISO_2L IS NOT NULL " + "AND F.SELECTION <> 0 "
                    + "AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' " + "AND F.ID_COUNTRY='" + idCountry + "' "
                    + "AND D.ID_GROUP_SPECIES='" + idGroup + "' GROUP BY H.NAME");

            if (listPersistentObject != null && listPersistentObject.size() > 0) {
                for (int i = 0; i < listPersistentObject.size(); i++) {
                    NationalThreatStatusPersist obj = (NationalThreatStatusPersist) listPersistentObject.get(i);

                    results.addElement(obj);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        ThreatStatusForAGroupAndACountry = results;
    }
  
    /**
     * Used to obtain threat status list for pair (any group species, any country).
     */
    public void setThreatStatusForAnyGroupAndAnyCountry() {
        Vector results = new Vector();

        try {
            List listPersistentObject = new NationalThreatStatusDomain().findWhere(
                    "F.ISO_2L<>'' " + "AND F.ISO_2L<>'null' " + "AND F.ISO_2L IS NOT NULL " + "AND F.SELECTION <> 0 "
                    + "AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' " + "GROUP BY H.NAME");

            if (listPersistentObject != null && listPersistentObject.size() > 0) {
                for (int i = 0; i < listPersistentObject.size(); i++) {
                    NationalThreatStatusPersist obj = (NationalThreatStatusPersist) listPersistentObject.get(i);

                    results.addElement(obj);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        ThreatStatusForAnyGroupAndAnyCountry = results;
    }

    /**
     * Used to obtain threat status list for pair ( group species, any country).
     */
    public void setThreatStatusForAGroupAndAnyCountry() {
        Vector results = new Vector();

        try {
            List listPersistentObject = new NationalThreatStatusDomain().findWhere(
                    "F.ISO_2L<>'' " + "AND F.ISO_2L<>'null' " + "AND F.ISO_2L IS NOT NULL " + "AND F.SELECTION <> 0 "
                    + "AND G.LOOKUP_TYPE = 'CONSERVATION_STATUS' " + "AND D.ID_GROUP_SPECIES='" + idGroup + "' GROUP BY H.NAME");

            if (listPersistentObject != null && listPersistentObject.size() > 0) {
                for (int i = 0; i < listPersistentObject.size(); i++) {
                    NationalThreatStatusPersist obj = (NationalThreatStatusPersist) listPersistentObject.get(i);

                    results.addElement(obj);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        ThreatStatusForAGroupAndAnyCountry = results;
    }

    /**
     * Getter for CountriesForAnyGroup property.
     * @return CountriesForAnyGroup.
     */
    public Vector getCountriesForAnyGroup() {
        return CountriesForAnyGroup;
    }

    /**
     * Getter for CountriesForAGroup property.
     * @return CountriesForAGroup.
     */
    public Vector getCountriesForAGroup() {
        return CountriesForAGroup;
    }

    /**
     * Getter for ThreatStatusForAnyGroupAndACountry property.
     * @return ThreatStatusForAnyGroupAndACountry.
     */
    public Vector getThreatStatusForAnyGroupAndACountry() {
        return ThreatStatusForAnyGroupAndACountry;
    }

    /**
     * Getter for CountriesForAnyGroup property.
     * @return CountriesForAnyGroup.
     */
    public Vector getThreatStatusForAGroupAndACountry() {
        return ThreatStatusForAGroupAndACountry;
    }
 
    /**
     * Getter for ThreatStatusForAnyGroupAndAnyCountry property.
     * @return ThreatStatusForAnyGroupAndAnyCountry.
     */
    public Vector getThreatStatusForAnyGroupAndAnyCountry() {
        return ThreatStatusForAnyGroupAndAnyCountry;
    }

    /**
     * Getter for ThreatStatusForAGroupAndAnyCountry property.
     * @return ThreatStatusForAGroupAndAnyCountry.
     */
    public Vector getThreatStatusForAGroupAndAnyCountry() {
        return ThreatStatusForAGroupAndAnyCountry;
    }
}
