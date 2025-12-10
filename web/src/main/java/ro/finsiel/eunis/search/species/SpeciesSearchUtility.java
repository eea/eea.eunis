package ro.finsiel.eunis.search.species;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.factsheet.species.NationalThreatWrapper;
import ro.finsiel.eunis.jrfTables.Chm62edtAbundanceDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtAbundancePersist;
import ro.finsiel.eunis.jrfTables.Chm62edtConservationStatusDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtConservationStatusPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtDistributionStatusDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtDistributionStatusPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtGroupspeciesDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtGroupspeciesPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtInfoQualityDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtInfoQualityPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtLanguageDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtReportAttributesDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesStatusDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtSpeciesStatusPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtTrendDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtTrendPersist;
import ro.finsiel.eunis.jrfTables.species.VernacularNamesDomain;
import ro.finsiel.eunis.jrfTables.species.VernacularNamesPersist;
import ro.finsiel.eunis.jrfTables.species.legal.ScientificLegalDomain;
import ro.finsiel.eunis.jrfTables.species.taxonomy.Chm62edtTaxcodeDomain;
import ro.finsiel.eunis.jrfTables.species.taxonomy.Chm62edtTaxcodePersist;
import ro.finsiel.eunis.jrfTables.species.taxonomy.SpeciesGroupSpeciesDomain;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.SQLUtilities;
import ro.finsiel.eunis.utilities.TableColumns;


/**
 * This class contains utility methods used in species search module.
 * @author finsiel
 */
public class SpeciesSearchUtility {

    private static final Logger logger = Logger.getLogger(SpeciesSearchUtility.class);

    /**
     * Specifies that the search is used in species' scientific names.
     * @see SpeciesSearchUtility#findSpeciesWithCriteria
     */
    public static final Integer CRITERIA_SCIENTIFIC_NAME = 0;

    /**
     * Specifies that the search is used in species' comon names.
     * @see SpeciesSearchUtility#findSpeciesWithCriteria
     */
    public static final Integer CRITERIA_VERNACULAR_NAME = 1;

    /** Finds the common names for a given specie, returning a vector of pairs : language, name.
     * @param idNatureObject ID Nature object of that specie
     * @return A list of VernacularNameWrapper objects, containing common names for that specie.
     */
    public static Vector<VernacularNameWrapper> findVernacularNames(Integer idNatureObject) {
        Vector<VernacularNameWrapper> ret = new Vector<VernacularNameWrapper>(0);

        if (null == idNatureObject) {
            return ret;
        }
        try {
            // search also on synonyms
            Vector<Integer> synonyms = new Vector<Integer>();

            List<Integer> idList = new LinkedList<Integer>();
            List<Chm62edtSpeciesPersist> lstSpeciesIDs = new Chm62edtSpeciesDomain().findWhere("ID_NATURE_OBJECT=" + idNatureObject);

            if (lstSpeciesIDs.size() > 0) {
                for (Chm62edtSpeciesPersist speciesPersist : lstSpeciesIDs) {
                    idList.add(speciesPersist.getIdSpecies());
                }
            }

            synonyms.add(idNatureObject);
            List<Chm62edtSpeciesPersist> lstSynonyms = new Chm62edtSpeciesDomain().findWhere(
                    "TYPE_RELATED_SPECIES = 'Synonym' and ID_SPECIES_LINK IN (" + listToStringIds(idList) + ")");

            if (lstSynonyms.size() > 0) {
                for (Chm62edtSpeciesPersist speciesPersist : lstSynonyms) {
                    synonyms.add(speciesPersist.getIdNatureObject());
                }
            }

            List<VernacularNamesPersist> verNameList = new VernacularNamesDomain().findWhere(
                    "LOOKUP_TYPE='language' " + "AND ID_NATURE_OBJECT IN (" + listToStringIds(synonyms)
                    + ") AND F.NAME='vernacular_name' GROUP BY F.VALUE, NAME_EN");

            // old list
            // List verNameList = new VernacularNamesDomain().findWhere("LOOKUP_TYPE='language' AND ID_NATURE_OBJECT='" + idNatureObject + "' AND F.NAME='vernacular_name' GROUP BY F.VALUE, NAME_EN");
            for (VernacularNamesPersist vernName : verNameList) {
                ret.addElement(
                        new VernacularNameWrapper(vernName.getLanguageName(), vernName.getLanguageCode(), vernName.getValue(),
                                vernName.getIdDc()));
            }
        } catch (Exception ex) {
            // If exception occurrs, return an empty list!
            ex.printStackTrace(System.err);
        }
        return ret;
    }

    /** Finds the English common name for a given species
     * @param idNatureObject ID Nature object of that species
     * @return English common name of the species
     */
    public static String findEnglishName(Integer idNatureObject) {
        String ret = "";

        if (null == idNatureObject) {
            return ret;
        }
        try {
            // search also on synonyms
            Vector<Integer> synonyms = new Vector<Integer>();

            List<Integer> idList = new LinkedList<Integer>();
            List<Chm62edtSpeciesPersist> lstSpeciesIDs = new Chm62edtSpeciesDomain().findWhere("ID_NATURE_OBJECT=" + idNatureObject);

            if (lstSpeciesIDs.size() > 0) {
                for (Chm62edtSpeciesPersist speciesPersist : lstSpeciesIDs) {
                    idList.add(speciesPersist.getIdSpecies());
                }
            }

            synonyms.add(idNatureObject);
            List<Chm62edtSpeciesPersist> lstSynonyms = new Chm62edtSpeciesDomain().findWhere(
                    "TYPE_RELATED_SPECIES = 'Synonym' and ID_SPECIES_LINK IN (" + listToStringIds(idList) + ")");

            if (lstSynonyms.size() > 0) {
                for (Chm62edtSpeciesPersist speciesPersist : lstSynonyms) {
                    synonyms.add(speciesPersist.getIdNatureObject());
                }
            }

            List<VernacularNamesPersist> verNameList = new VernacularNamesDomain().findWhere(
                    "LOOKUP_TYPE='language' " + "AND ID_NATURE_OBJECT IN (" + listToStringIds(synonyms)
                            + ") AND F.NAME='vernacular_name' AND CODE='en' GROUP BY F.VALUE, NAME_EN");

            for (VernacularNamesPersist vernName : verNameList) {
                ret = vernName.getValue();
            }
        } catch (Exception ex) {
            // If exception occurrs, return an empty list!
            ex.printStackTrace(System.err);
        }
        return ret;
    }

    private static String listToStringIds(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("argument cannot be empty");
        }
        StringBuilder sb = new StringBuilder();

        for (Integer integer : list) {
            sb.append(integer).append(',');
        }
        sb.deleteCharAt(sb.length() - 1); // delete last comma
        return sb.toString();
    }

    /**
     * This method finds all the species belonging to a group (Algae, Fishes, duh!s).
     *
     * @param groupID The ID of the group species belongs to. Can also be 'any' and will return all the species.
     * @param scientificName The scientific name of the species searched. Note that the SQL is executing as
     *                       "... LIKE '%" + scientificName + "%' ..." so could find all the species matching criteria.
     * @param expandAll Apply limit or not in results (Utilities.MAX_POPUP_RESULTS)
     * @return A list with species belonging to the specified group.<br />
     * The returned list can contain the following possible values:<br />
     * <UL>
     *  <LI> If groupID or scientificName is null or Exception occurrs, an empty list will be returned
     *  <LI> A list of Chm62edtSpeciesPersist objects if the query was successfully.
     * </UL>
     */
    public static List findSpeciesFromGroup(String groupID, String scientificName, boolean expandAll) {
        if (null == groupID || null == scientificName) {
            return new Vector(0);
        }
        List results = new Vector(0);

        try {

            SQLUtilities sqlc = new SQLUtilities();

            sqlc.Init();

            String SQL = "SELECT distinct e.scientific_name " + "FROM chm62edt_legal_status AS D "
            + "INNER JOIN chm62edt_report_type AS C ON (D.ID_LEGAL_STATUS = C.ID_LOOKUP and C.LOOKUP_TYPE='LEGAL_STATUS') "
            + "INNER JOIN chm62edt_reports AS B ON C.ID_REPORT_TYPE = B.ID_REPORT_TYPE "
            + "INNER JOIN dc_index AS A ON B.ID_DC = A.ID_DC "
            + "INNER JOIN chm62edt_species AS E ON B.ID_NATURE_OBJECT = E.ID_NATURE_OBJECT ";

            if (groupID.equalsIgnoreCase("any")) {
                SQL += " WHERE E.SCIENTIFIC_NAME LIKE '%" + scientificName + "%'";
                if (!expandAll) {
                    SQL += " LIMIT 0, " + Utilities.MAX_POPUP_RESULTS;
                }
            } else {
                SQL += " WHERE E.ID_GROUP_SPECIES = '" + groupID + "' AND E.SCIENTIFIC_NAME LIKE '%" + scientificName + "%'";
                if (!expandAll) {
                    SQL += " LIMIT 0, " + Utilities.MAX_POPUP_RESULTS;
                }
            }
            results = sqlc.ExecuteSQLReturnList(SQL, 1);
        } catch (Exception ex) {
            // If exception occurrs, return an empty list!
            ex.printStackTrace(System.err);
        }
        return results;
    }

    /**
     * This method finds all the texts which are reffered by a group (Algae, Fishes etc).
     * @param groupID Group for which you want the legal texts to be found. You can pass 'any' here to find all the
     *                legal texts.
     * @return A list of legal texts associated with this groupID.<br />
     * The list contains ScientificLegalPersist objects. If groupID is null, list size will be 0.
     */
    public static List findLegalTextsForGroup(String groupID) {
        if (null == groupID) {
            return new Vector(0);
        }
        List results = new Vector(0);

        try {
            if (!groupID.equalsIgnoreCase("any")) {
                ScientificLegalDomain finder = new ScientificLegalDomain();

                results = finder.findWhere(
                        "E.ID_GROUP_SPECIES = " + groupID + " AND B.LOOKUP_TYPE = 'LEGAL_STATUS' AND E.VALID_NAME>0 GROUP BY D.ALTERNATIVE");
            } else {
                // Any group
                ScientificLegalDomain finder = new ScientificLegalDomain();

                results = finder.findWhere("B.LOOKUP_TYPE = 'LEGAL_STATUS' AND E.VALID_NAME>0 GROUP BY D.ALTERNATIVE");
            }
        } catch (Exception ex) {
            // If exception occurrs, return an empty list!
            ex.printStackTrace(System.err);
        }
        return results;
    }

    /**
     * This method find a group ID after its name (COMMON_NAME). Consult chm62edt_group_species table for this matter.
     * @param groupName Group name (fishes, invertebrates etc.)
     * @return An group ID or new Integer(-1) if that group is not found or exception occurrs
     */
    public static Integer findGroupID(String groupName) {
        Integer ret = -1;

        if (null == groupName) {
            return ret;
        }
        try {
            Chm62edtGroupspeciesDomain finder = new Chm62edtGroupspeciesDomain();
            List resList = finder.findWhere("COMMON_NAME LIKE '%" + groupName + "%'");

            if (resList.size() > 0) {
                Chm62edtGroupspeciesPersist group = (Chm62edtGroupspeciesPersist) resList.get(0);

                if (null != group) {
                    ret = group.getIdGroupspecies();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findGroupID(" + groupName + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
            ret = -1;
        }
        return ret;
    }

    /**
     * Find an id of an attribute from chm62edt_conservation_status table.
     * @param name Name of the abundance.
     * @return ID of that abundance attribute or -1 if not found.
     */
    public static Integer findIDConservationStatus(String name) {
        Integer ret = -1;

        if (null == name) {
            return ret;
        }
        try {
            Chm62edtConservationStatusDomain finder = new Chm62edtConservationStatusDomain();
            List resList = finder.findWhere("DESCRIPTION='" + name + "'");

            if (resList.size() > 0) {
                Chm62edtConservationStatusPersist conservation = (Chm62edtConservationStatusPersist) resList.get(0);

                if (null != conservation) {
                    ret = conservation.getIdConsStatus();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findIDConservationStatus(" + name + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
            ret = -1;
        }
        return ret;
    }

    /**
     * Find an id of an attribute from chm62edt_abundance table.
     * @param name Name of the attribute.
     * @return ID of that abundance attribute or -1 if not found.
     */
    public static Integer findIDAbundance(String name) {
        Integer ret = -1;

        if (null == name) {
            return ret;
        }
        try {
            Chm62edtAbundanceDomain finder = new Chm62edtAbundanceDomain();
            List resList = finder.findWhere("DESCRIPTION='" + name + "'");

            if (resList.size() > 0) {
                Chm62edtAbundancePersist conservation = (Chm62edtAbundancePersist) resList.get(0);

                if (null != conservation) {
                    ret = conservation.getIdAbundance();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findIDAbundance(" + name + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
            ret = -1;
        }
        return ret;
    }

    /**
     * Find an id of an attribute from chm62edt_trendS table.
     * @param name Name of the abundance.
     * @return ID of that abundance attribute or -1 if not found.
     */
    public static Integer findIDTrend(String name) {
        Integer ret = -1;

        if (null == name) {
            return ret;
        }
        try {
            Chm62edtTrendDomain finder = new Chm62edtTrendDomain();
            List resList = finder.findWhere("DESCRIPTION='" + name + "'");

            if (resList.size() > 0) {
                Chm62edtTrendPersist conservation = (Chm62edtTrendPersist) resList.get(0);

                if (null != conservation) {
                    ret = conservation.getIdTrend();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findIDTrend(" + name + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
            ret = -1;
        }
        return ret;
    }

    /**
     * Find an id of an attribute from chm62edt_distribution_status table.
     * @param name Name of the attribute.
     * @return ID of that abundance attribute or -1 if not found.
     */
    public static Integer findIDDistributionStatus(String name) {
        Integer ret = -1;

        if (null == name) {
            return ret;
        }
        try {
            Chm62edtDistributionStatusDomain finder = new Chm62edtDistributionStatusDomain();
            List resList = finder.findWhere("DESCRIPTION='" + name + "'");

            if (resList.size() > 0) {
                Chm62edtDistributionStatusPersist conservation = (Chm62edtDistributionStatusPersist) resList.get(0);

                if (null != conservation) {
                    ret = conservation.getId();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findIDDistributionStatus(" + name + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
            ret = -1;
        }
        return ret;
    }

    /**
     * Find an id of an attribute from chm62edt_species_status table.
     * @param name Name of the abundance.
     * @return ID of that abundance attribute or -1 if not found.
     */
    public static Integer findIDSpeciesStatus(String name) {
        Integer ret = -1;

        if (null == name) {
            return ret;
        }
        try {
            Chm62edtSpeciesStatusDomain finder = new Chm62edtSpeciesStatusDomain();
            List resList = finder.findWhere("DESCRIPTION='" + name + "'");

            if (resList.size() > 0) {
                Chm62edtSpeciesStatusPersist conservation = (Chm62edtSpeciesStatusPersist) resList.get(0);

                if (null != conservation) {
                    ret = conservation.getIdSpeciesStatus();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findIDSpeciesStatus(" + name + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
        }
        return ret;
    }

    /**
     * Find an id of an attribute from chm62edt_info_quality table.
     * @param name Name of the abundance.
     * @return ID of that abundance attribute or -1 if not found.
     */
    public static Integer findIDInfoQuality(String name) {
        Integer ret = -1;

        if (null == name) {
            return ret;
        }
        try {
            Chm62edtInfoQualityDomain finder = new Chm62edtInfoQualityDomain();
            List resList = finder.findWhere("DESCRIPTION='" + name + "'");

            if (resList.size() > 0) {
                Chm62edtInfoQualityPersist conservation = (Chm62edtInfoQualityPersist) resList.get(0);

                if (null != conservation) {
                    ret = conservation.getIdInfoQuality();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findIDInfoQuality(" + name + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
        }
        return ret;
    }

    /**
     * This method is used to find a group's common name (english) name giving it's group id. Consult chm62edt_group_species
     * table for this
     * @param groupID ID_GROUP_SPECIES (group id)
     * @return The COMMON_NAME column coresponding to this id or "" if an invalid parameter was passed to this method
     * If exception ocurrent or parameter was null, "Exception!" string is returned
     */
    public static String findGroupName(String groupID) {
        String ret = "any";

        if (null == groupID) {
            return ret;
        }
        if (groupID.equalsIgnoreCase("any")) {
            return "any";
        }
        try {
            Chm62edtGroupspeciesDomain finder = new Chm62edtGroupspeciesDomain();
            List resList = finder.findWhere("ID_GROUP_SPECIES  ='" + groupID + "'");

            if (resList.size() > 0) {
                Chm62edtGroupspeciesPersist group = (Chm62edtGroupspeciesPersist) resList.get(0);

                if (null != group) {
                    ret = group.getCommonName();
                }
            } else {
                System.err.println(SpeciesSearchUtility.class.getName() + "::findGroupName(" + groupID + "): List is empty!");
            }
        } catch (Exception ex) {
            // If exception occurrs, return "Exception!" string
            ex.printStackTrace(System.err);
            ret = "any";
        }
        return ret;
    }

    /**
     * This method is used to find a group's common name (english) name giving a part of its name.
     * @param namePart Part of the group name
     * @param relationOp relation operator for name (Utilites.OPERATOR_XXX)
     * @return The List from COMMON_NAME column coresponding to this id or "" if an invalid parameter was passed to this method
     * If exception ocurrent or parameter was null, "Exception!" string is returned
     */
    public static List findGroupCommonName(String namePart, Integer relationOp) {
        List results = new Vector();

        if (null == namePart) {
            namePart = "%";
            relationOp = Utilities.OPERATOR_CONTAINS;
        }
        try {
            Chm62edtGroupspeciesDomain finder = new Chm62edtGroupspeciesDomain();
            List resList = finder.findWhere(Utilities.prepareSQLOperator("COMMON_NAME", namePart, relationOp).toString());

            for (Object aResList : resList) {
                Chm62edtGroupspeciesPersist group = (Chm62edtGroupspeciesPersist) aResList;

                results.add(group.getCommonName());
            }
        } catch (Exception ex) {
            // If exception occurrs, return "Exception!" string
            ex.printStackTrace(System.err);
        }
        return results;
    }

    /**
     * This method is used to cound the real number of species & subspecies which belongs within a group.
     * Real number means that species doesn't contain synonyms, hybrids etc
     * @param groupID ID of the group species belongs to
     * @param showInvalidatedSpecies Count the invalidated species also.
     * @return Count number of species. -1 is returned if groupID is null or an exception occurrs
     */
    public static Long countUniqueSpecies(Integer groupID, boolean showInvalidatedSpecies) {
        Long ret = (long) -1;

        if (null == groupID) {
            return ret;
        }
        try {
            Chm62edtSpeciesDomain finder = new Chm62edtSpeciesDomain();

            if (showInvalidatedSpecies) {
                ret = finder.countWhere(
                        "SELECT count(*) FROM chm62edt_species WHERE ID_GROUP_SPECIES = " + groupID
                        + " AND (TYPE_RELATED_SPECIES='species' OR TYPE_RELATED_SPECIES='subspecies')");
            } else {
                ret = finder.countWhere(
                        "SELECT count(*) FROM chm62edt_species WHERE ID_GROUP_SPECIES = " + groupID
                        + " AND VALID_NAME > 0 AND (TYPE_RELATED_SPECIES='species' OR TYPE_RELATED_SPECIES='subspecies')");
            }
        } catch (Exception ex) {
            // If exception occurrs, return -1!
            ex.printStackTrace(System.err);
            ret = (long) -1;
        }
        return ret;
    }

    /**
     * Find species with their scientific or common name matching a given criteria.
     * @param criteria Where to search. Possible values: SpeciesSearchUtility.CRITERIA_SCIENTIFIC_NAME[CRITERIA_VERNACULAR_NAME]
     * @param name Name to be searched for
     * @param relationOp Relation between criteria and name parameters.
     * Possible values: Utilities.OPERATOR_IS[OPERATORS_CONTAINS / OPERATOR_STARTS]
     * @param langName The name of the language we are searching in (i.e. English). This parameters is used only for
     * common names search (CRITERIA_VERNACULAR_NAME).<br />
     * Possible values are:<br />
     * <UL>
     *  <LI> <I>null</I> - this means that any language is selected, search is done for that name in all available languages
     *  <LI> <I>any</I> - Also, like <I>null</I> does the search for that name in all available languages
     *  <LI> <I>a language</I> - A particular language. Names will be searched only in that language
     * </UL>
     * @param showNonValidated If true Non-validated species will also be included in results
     * @param expandAll Apply limit or not in results (Utilities.MAX_POPUP_RESULTS).
     * @return A list of species matching criterias. Otherwise an non-null 0-size empty list<br />
     * <UL>
     *  <LI>If criteria = CRITERIA_SCIENTIFIC_NAME, then returned list contains Chm62edtSpeciesPersist objects
     *  <LI>If criteria = CRITERIA_VERNACULAR_NAME, then returned list contains Chm62edtReportsPersist objects
     * </UL>
     * @see SpeciesSearchUtility#CRITERIA_SCIENTIFIC_NAME
     * @see SpeciesSearchUtility#CRITERIA_VERNACULAR_NAME
     */
    public static List findSpeciesWithCriteria(Integer criteria,
            String name,
            Integer relationOp,
            String langName,
            boolean showNonValidated,
            boolean expandAll) {
        if (null == criteria || null == name || null == relationOp) {
            return new Vector(0);
        }
        List results = new Vector();

        try {
            if (0 == criteria.compareTo(CRITERIA_SCIENTIFIC_NAME)) {
                String sql = Utilities.prepareSQLOperator("SCIENTIFIC_NAME", name, relationOp).toString();

                if (!showNonValidated) {
                    sql += " AND VALID_NAME > 0 ";
                }
                sql += " GROUP BY SCIENTIFIC_NAME";
                if (!expandAll) {
                    sql += " LIMIT 0, " + Utilities.MAX_POPUP_RESULTS;
                }
                results = new Chm62edtSpeciesDomain().findWhere(sql);
            }
            if (0 == criteria.compareTo(CRITERIA_VERNACULAR_NAME)) {
                if (null == langName || (langName.equalsIgnoreCase("any"))) {
                    String sql = Utilities.prepareSQLOperator("VALUE", name, relationOp)
                    + " AND NAME='vernacular_name' GROUP BY VALUE";

                    if (!expandAll) {
                        sql += " LIMIT 0, " + Utilities.MAX_POPUP_RESULTS;
                    }
                    results = new Chm62edtReportAttributesDomain().findWhere(sql);
                } else {
                    String sql = Utilities.prepareSQLOperator("VALUE", name, relationOp)
                    + " AND LOOKUP_TYPE='language' AND NAME='vernacular_name' AND NAME_EN='" + langName + "'";

                    if (!expandAll) {
                        sql += " LIMIT 0, " + Utilities.MAX_POPUP_RESULTS;
                    }
                    results = new VernacularNamesDomain().findWhere(sql);
                }
            }
            if (null == results) {
                results = new Vector(0);
            }
        } catch (Exception ex) {
            // If exception occurrs, return an empty list!
            ex.printStackTrace(System.err);
            results = new Vector(0);
        }
        return results;
    }

    /**
     * Finds all the languages available (records from Chm62edtLanguageDomain).<br />
     * Implements the query: SELECT * FROM chm62edt_language ORDER BY NAME.
     * @return A list of Chm62edtLanguagePersist objects, encapsulating the languages
     */
    public static List findAllLanguages() {
        List results = null;

        try {
            results = new Chm62edtLanguageDomain().findOrderBy("NAME_EN");
            if (null == results) {
                return new Vector(0);
            }
        } catch (Exception ex) {
            // If exception occurrs, return an empty list!
            ex.printStackTrace(System.err);
            results = new Vector(0);
        }
        return results;
    }

    /**
     * Find all the groups available to display in Species -> Groups table.
     * @return A list of groups. The following possible values are returned:<br />
     * <UL>
     *  <LI> An empty list if no group was found or exception occurred
     *  <LI> A list of Chm62edtGroupspeciesPersist objects, one for each group.
     * </UL>
     */
    public static List findAllGroups() {
        List results = new Vector(0);

        try {
            results = new Chm62edtGroupspeciesDomain().findOrderBy("COMMON_NAME");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return results;
    }

    /**
     * Return the national threat status information for a specified species in the specified country.
     * @param specieID ID of the species (ID_SPECIES from chm62edt_species)
     * @param countryName Name of the country (AREA_NANE_EN from chm62edt_country)
     * @return List of Chm62edtConservationStatusPersist objects with threat information.
     */
    public static List getThreatNational(Integer specieID, String countryName) {
        List result = new Vector();
        String sql = "SELECT H.* FROM chm62edt_species C INNER JOIN chm62edt_reports E ON C.ID_NATURE_OBJECT=E.ID_NATURE_OBJECT";

        sql += " INNER JOIN chm62edt_country F ON E.ID_GEOSCOPE=F.ID_GEOSCOPE ";
        sql += " INNER JOIN chm62edt_report_type G ON E.ID_REPORT_TYPE=G.ID_REPORT_TYPE ";
        sql += " INNER JOIN chm62edt_conservation_status H ON G.ID_LOOKUP=H.ID_CONSERVATION_STATUS ";
        sql += " WHERE (G.LOOKUP_TYPE = 'CONSERVATION_STATUS') ";
        sql += " AND (C.ID_SPECIES=" + specieID + " AND F.AREA_NAME_EN='" + countryName + "') ";
        sql += " GROUP BY F.AREA_NAME_EN,H.NAME,H.CODE";
        try {
            result = new Chm62edtConservationStatusDomain().findGeneral(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Return true if taxonomy has children (ex: order has families).
     * @param idTaxonomy ID of taxonomy
     * @return true or false.
     */
    public static boolean IdTaxonomyHasChildren(String idTaxonomy) {
        boolean result = false;

        if (idTaxonomy == null) {
            return false;
        }
        try {
            List taxonomies = new Chm62edtTaxcodeDomain().findWhere(
                    "ID_TAXONOMY != ID_TAXONOMY_PARENT and ID_TAXONOMY_PARENT ='" + idTaxonomy + "'");

            if (taxonomies != null && taxonomies.size() > 0) {
                result = true;
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Return species and their group species for a taxonomy( given by his ID_TAXONMY).
     * @param idTaxonomy ID of taxonomy
     * @param showEUNISInvalidatedSpecies true or false
     * @return list of species.
     */
    public static List FindSpeciesforIdTaxonomy(String idTaxonomy, boolean showEUNISInvalidatedSpecies) {
        List result = new ArrayList();

        if (idTaxonomy == null) {
            return new ArrayList();
        }

        try {
            String tree = getTaxonomicTree(idTaxonomy);
            StringTokenizer st = new StringTokenizer(tree, ",");
            String whereC = " A.ID_TAXONOMY in ('" + idTaxonomy + "' ";

            while (st.hasMoreTokens()) {
                StringTokenizer sts = new StringTokenizer(st.nextToken(), "*");
                String classification_id = sts.nextToken();

                whereC += ",'" + classification_id + "'";
            }
            whereC += ")";

            String sql = Utilities.showEUNISInvalidatedSpecies("AND A.VALID_NAME", showEUNISInvalidatedSpecies);

            List species = new SpeciesGroupSpeciesDomain().findCustom(
                    "SELECT A.ID_SPECIES,A.ID_SPECIES_LINK, " + "A.SCIENTIFIC_NAME,B.COMMON_NAME " + "FROM chm62edt_species AS A "
                    + "LEFT JOIN chm62edt_group_species AS B ON A.ID_GROUP_SPECIES = B.ID_GROUP_SPECIES " + // "WHERE A.ID_TAXONOMY LIKE '" + findRadicalOfIdTaxonomy(idTaxonomy) + "%' " + sql +
                    // "WHERE A.ID_TAXONOMY = '" + idTaxonomy + "' " + sql +
                    "WHERE " + whereC + sql + " GROUP BY A.ID_SPECIES");

            if (species != null && species.size() > 0) {
                result = species;
            }

        } catch (Exception e) {
            logger.debug(e, e);
        }
        return result;
    }

    /**
     * Return radical of an ID_TAXONOMY (Ex: for "1GA40000000" return "1GA4", delete zero by final).
     * @param idTaxonomy ID of taxonomy
     * @return radical of taxonomy.
     */
    public static String findRadicalOfIdTaxonomy(String idTaxonomy) {
        if (idTaxonomy == null) {
            return "";
        }
        String result = idTaxonomy;
        int lgn = idTaxonomy.length();

        for (int i = lgn - 1; i >= 0; i--) {
            if (idTaxonomy.charAt(i) != '0') {
                break;
            } else {
                result = idTaxonomy.substring(0, i);
            }
        }
        return result;
    }

    /**
     * Remove duplicates from the list of threats. Duplicates means that two countries have threat status information,
     * different statuses from different year. Only the most recent threat is considered, others are considered duplicates
     * and removed from list.
     * @param threats List of NationalThreatWrapper threats.
     * @return List of NationalThreatWrapper.
     */
    public static List processThreats(List threats) {
        Vector res = new Vector();

        try {
            for (Object threat : threats) {
                NationalThreatWrapper tw = (NationalThreatWrapper) threat;

                if (duplicate(res, tw)) {
                    replace(res, tw);
                } else {
                    res.addElement(tw);
                }
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
        return res;
    }

    private static boolean duplicate(List threats, NationalThreatWrapper threat) {
        for (Object threat1 : threats) {
            NationalThreatWrapper tw = (NationalThreatWrapper) threat1;

            if (tw.getCountry().equalsIgnoreCase(threat.getCountry())) {
                if (tw.getReferenceYear() <= threat.getReferenceYear()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void replace(List threats, NationalThreatWrapper newThreat) {
        int i = 0;

        for (i = 0; i < threats.size(); i++) {
            NationalThreatWrapper tw = (NationalThreatWrapper) threats.get(i);

            if (tw.getCountry().equalsIgnoreCase(newThreat.getCountry())) {
                break;
            }
        }
        threats.remove(i);
        threats.add(i, newThreat);
    }

    /**
     * Test method.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Vector v = new Vector();

        v.add(new NationalThreatWrapper("Greece", "Data deficient", 1992));
        v.add(new NationalThreatWrapper("Germany, Federal Republic of", "Vulnerable", 1994));
        v.add(new NationalThreatWrapper("Sweden", "Care-demanding", 1996));
        v.add(new NationalThreatWrapper("Norway", "Endangered", 1992));
        v.add(new NationalThreatWrapper("France", "Vulnerable", 1994));
        v.add(new NationalThreatWrapper("Netherlands", "Vulnerable", 1996));
        v.add(new NationalThreatWrapper("Denmark", "Decreasing species", 1991));
        v.add(new NationalThreatWrapper("Switzerland", "Vulnerable", 1994));
        v.add(new NationalThreatWrapper("Finland", "Vulnerable", 1992));
        v.add(new NationalThreatWrapper("Denmark", "Decreasing species", 1998));
        v.add(new NationalThreatWrapper("Norway", "Endangered", 1999));
        v.add(new NationalThreatWrapper("Germany, Federal Republic of", "Rare", 1998));
        v.add(new NationalThreatWrapper("Poland", "Lower Risk", 2002));
        v.add(new NationalThreatWrapper("Lithuania", "Data deficient", 2000));
        v.add(new NationalThreatWrapper("Latvia", "Vulnerable", 2002));
        v.add(new NationalThreatWrapper("Finland", "Vulnerable", 2000));

        List v1 = SpeciesSearchUtility.processThreats(v);

        for (Object aV1 : v1) {
            NationalThreatWrapper tw = (NationalThreatWrapper) aV1;
            // System.out.println(tw.getCountry() + "   |   " + tw.getStatus()  + "  |   " + tw.getReferenceYear());
        }

    }

    /**
     * Compute taxonomic tree.
     * @param idTaxonomy
     * @return Taxtree
     */
    public static String getTaxonomicTree(String idTaxonomy) {
        if (idTaxonomy == null || idTaxonomy.trim().length() <= 0) {
            return "";
        }
        List list = new ArrayList();
        String result = "";

        try {
            list = new Chm62edtTaxcodeDomain().findWhere("ID_TAXONOMY = '" + idTaxonomy + "'");

            if (list != null && list.size() > 0) {
                Chm62edtTaxcodePersist t = (Chm62edtTaxcodePersist) list.get(0);

                result = t.getTaxonomyTree();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Finds all the languages with species common names.
     * @return A list of languages
     */
    public static List findAllLanguagesWithVernacularNames() {
        List results = new ArrayList();

        try {

            SQLUtilities sqlc = new SQLUtilities();

            sqlc.Init();
            String sql = "SELECT DISTINCT NAME_EN " + "FROM chm62edt_language AS A "
            + "INNER JOIN chm62edt_report_type AS B ON (A.ID_LANGUAGE = B.ID_LOOKUP AND B.LOOKUP_TYPE = 'LANGUAGE') "
            + "INNER JOIN chm62edt_reports AS C ON B.ID_REPORT_TYPE = C.ID_REPORT_TYPE "
            + "INNER JOIN `chm62edt_report_attributes` AS D ON (C.ID_REPORT_ATTRIBUTES = D.ID_REPORT_ATTRIBUTES AND D.NAME='VERNACULAR_NAME') "
            + "ORDER BY NAME_EN";
            List columns = sqlc.ExecuteSQLReturnList(sql, 1);

            if (columns != null && columns.size() > 0) {
                for (Object column : columns) {
                    results.add(((TableColumns) column).getColumnsValues().get(0));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return results;
    }

}
