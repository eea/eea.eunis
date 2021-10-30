package ro.finsiel.eunis.search.habitats;


import eionet.eunis.util.Constants;
import ro.finsiel.eunis.jrfTables.habitats.names.NamesDomain;
import ro.finsiel.eunis.jrfTables.habitats.legal.EUNISLegalDomain;
import ro.finsiel.eunis.jrfTables.habitats.code.CodeDomain;
import ro.finsiel.eunis.jrfTables.*;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.habitats.code.OtherClassWrapper;
import ro.finsiel.eunis.search.habitats.advanced.DictionaryBean;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;


/**
 * This class offers utility methods for the habitats searches.
 * @author finsiel
 */
public class HabitatsSearchUtility {

    /**
     * This method finds all the habitats with scientific name matching criterias imposed by name and operator params.
     * @param name Scientific name of the habitats
     * @param relationOp How name is related to search.<br />
     * Possible values are:<br />
     * <UL>
     *  <LI>Utilities.OPERATOR_CONTAINS
     *  <LI>Utilities.OPERATOR_IS;
     *  <LI>Utilities.OPERATOR_STARTS
     * </UL>
     * @param database Specifies database where to search:<br />
     * Possible values:<br />
     * <UL>
     *  <LI>null - Search both in EUNIS &amp; ANNEX I
     *  <LI>ro.finsiel.eunis.jrfTables.habitats.names.NamesDomain.SEARCH_EUNIS - Search in EUNIS
     *  <LI>ro.finsiel.eunis.jrfTables.habitats.names.NamesDomain.SEARCH_ANNEX_I - Search in ANNEX I
     *  <LI>any other values different of those specified above, defaults to SEARCH_EUNIS (second)
     * </UL>
     * @param useScientific Specifies that name parameter is searched in scientific names of habitats. At least one of
     * these three boolean parameters must be set to true in order to do some search
     * @param useVernacular Specifies that name parameter is searched in common names of habitats. At least one of
     * these three boolean parameters must be set to true in order to do some search
     * @param useDescription Specifies that name parameter is searched in descriptions of habitats. At least one of
     * these three boolean parameters must be set to true in order to do some search
     * @return A List of NamesPersist objects, one for each habitat, or an empty list if params were null or
     * exception occurred.
     */
    public static List findHabitatsWithCriteria(String name, Integer relationOp, Integer database,
            boolean useScientific,
            boolean useVernacular,
            boolean useDescription) {
        if (null == name || null == relationOp) {
            return new Vector();
        }
        if (useScientific == false && useVernacular == false && useDescription == false) {
            return new Vector();
        }
        StringBuffer whereSQL = new StringBuffer();

        if (null != database) {
            if (0 != database.compareTo(NamesDomain.SEARCH_BOTH)) {
                if (database.intValue() == NamesDomain.SEARCH_ANNEX_I.intValue()) { // ANNEX I
                    whereSQL.append(" A.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "' AND ");
                } else { // If EUNIS (or other wrong value - defaults to EUNIS)
                    whereSQL.append(" A.ID_HABITAT >= 1 AND A.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%' AND ");
                }
            } else {
                whereSQL.append(" A.ID_HABITAT <>'-1' AND A.ID_HABITAT <> '" + Constants.HABITAT_ANNEX1_ROOT + "' AND ");
            }
        }
        if (useScientific) {
            whereSQL.append(Utilities.prepareSQLOperator("A.SCIENTIFIC_NAME", name, relationOp));
            whereSQL.append(" ");
        }
        if (useVernacular) {
            if (useScientific) {
                whereSQL.append(" OR ");
            }
            whereSQL.append(Utilities.prepareSQLOperator("A.DESCRIPTION", name, relationOp));
            whereSQL.append(" ");
        }
        if (useDescription) {
            if (useScientific || useVernacular) {
                whereSQL.append(" OR ");
            }
            whereSQL.append(Utilities.prepareSQLOperator("B.DESCRIPTION", name, relationOp));
            whereSQL.append(" ");
        }
        whereSQL.append("GROUP BY A.SCIENTIFIC_NAME ORDER BY A.SCIENTIFIC_NAME");
        whereSQL.append(" LIMIT 0, " + Utilities.MAX_POPUP_RESULTS);
        List results;

        try {
            results = new NamesDomain().findWhere(whereSQL.toString());
        } catch (Exception ex) {
            // Return an empty list if exception occurred
            ex.printStackTrace(System.err);
            results = new Vector();
        }
        return results;
    }

    /**
     * Find habitats matching a specified name.
     * @param name Name to search.
     * @param relationOp Relation operator (is / contains / starts with).
     * @param database Database to search (eunis / annex).
     * @param useScientific Search in scientific names of habitats.
     * @param useVernacular Search in common names of habitats.
     * @param useDescription Search in description of habitats.
     * @param limit Limit the output of results to a specified value.
     * @return List of ro.finsiel.eunis.jrfTables.habitats.names.NamesPersist objects.
     */
    public static List findHabitatsWithCriteriaWithLimit(String name, Integer relationOp, Integer database,
            boolean useScientific,
            boolean useVernacular,
            boolean useDescription, int limit) {
        if (null == name || null == relationOp) {
            return new Vector();
        }
        if (useScientific == false && useVernacular == false && useDescription == false) {
            return new Vector();
        }
        StringBuffer whereSQL = new StringBuffer();

        if (null != database) {
            if (database.intValue() != NamesDomain.SEARCH_BOTH.intValue()) {
                if (database.intValue() == NamesDomain.SEARCH_ANNEX_I.intValue()) { // ANNEX I
                    whereSQL.append(" A.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "' AND ");
                } else { // If EUNIS (or other wrong value - defaults to EUNIS)
                    whereSQL.append(" A.ID_HABITAT>=1 and A.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%' AND ");
                }
            } else {
                whereSQL.append(" A.ID_HABITAT<>'-1' and A.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "' AND ");
            }
        }
        if (useScientific) {
            whereSQL.append(Utilities.prepareSQLOperator("A.SCIENTIFIC_NAME", name, relationOp));
            whereSQL.append(" ");
        }
        if (useVernacular) {
            if (useScientific) {
                whereSQL.append(" OR ");
            }
            whereSQL.append(Utilities.prepareSQLOperator("A.DESCRIPTION", name, relationOp));
            whereSQL.append(" ");
        }
        if (useDescription) {
            if (useScientific || useVernacular) {
                whereSQL.append(" OR ");
            }
            whereSQL.append(Utilities.prepareSQLOperator("B.DESCRIPTION", name, relationOp));
            whereSQL.append(" ");
        }
        whereSQL.append(" GROUP BY A.SCIENTIFIC_NAME ORDER BY A.SCIENTIFIC_NAME ");
        whereSQL.append(" LIMIT 0, " + limit);

        List results = new Vector();

        try {
            results = new NamesDomain().findWhere(whereSQL.toString());
        } catch (Exception ex) {
            // Return an empty list if exception occurred
            ex.printStackTrace(System.err);
            results = new Vector();
        }
        return results;
    }

    /**
     * This method finds all the EUNIS Habitat type (A, B, C, D ...).
     * In particular, this method is used in Habitats -> Legal Instruments.
     * @return An list of Chm62edtHabitatPersist objects, one for each habitat, or an empty list if error occurred.
     */
    public static List findEUNISHabitatTypes() {
        List results = new Vector();

        try {
            results = new Chm62edtHabitatDomain().findWhereOrderBy("LEVEL=1 and ID_HABITAT>=1 and HABITAT_TYPE like '"+ Constants.HABITAT_EUNIS +"%'" ,
                    "EUNIS_HABITAT_CODE");
            if (null == results) {
                results = new Vector();
            }
        } catch (Exception ex) {
            // Return an empty list if exception occurrs.
            ex.printStackTrace(System.err);
            results = new Vector();
        }
        return results;
    }

    /**
     * Finds all the legal texts available. In particular, this method is used in Habitats -> Legal Instruments.
     * @return An list of Chm62edtHabitatDesignatedCodesPersist objects, one for each legal text or an empty list if
     * error occurred.
     */
    public static List findLegalTexts() {
        List results = new Vector();

        try {
            results = new Chm62edtClassCodeDomain().findCustom("SELECT * FROM chm62edt_class_code WHERE LEGAL = 1 GROUP BY NAME");
            if (null == results) {
                results = new Vector();
            }
        } catch (Exception ex) {
            // Return an empty list if exception occurrs
            ex.printStackTrace(System.err);
            results = new Vector();
        }
        return results;
    }


    /**
     * Return the name of the language by its ID from the chm62edt_language.
     * @param id ID of the language.
     * @return String with the name of the language in english (NAME_EN from chm62edt_language).
     */
    public static String GetLanguage(Integer id) {
        String lang = "";
        List l = new Chm62edtLanguageDomain().findWhere("ID_LANGUAGE = " + id);

        if (l != null && l.size() > 0) {
            lang = ((Chm62edtLanguagePersist) l.get(0)).getNameEn();
        }
        return lang;
    }

    /**
     * Find all the clasifications available in database (from chm62edt_class_code table).
     * @return An non-null list with all the classif objects (Chm62edtClassCodePersist objects)
     */
    public static List getDatabaseClassifications() {
        List result = new Vector();

        try {
            // result = new Chm62edtClassCodeDomain().findWhere(" LEGAL = 0 OR (LEGAL=1 AND NAME LIKE '%EU Habitats Directive Annex I%') ORDER BY CURRENT_CLASSIFICATION DESC, SORT_ORDER ASC");
            result = new Chm62edtClassCodeDomain().findWhere(" LEGAL = 0 ORDER BY CURRENT_CLASSIFICATION DESC, SORT_ORDER ASC");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            result = new Vector();
        }
        if (null == result) {
            result = new Vector();
        }
        return result;
    }

    /**
     * Find all the clasifications available in database (from chm62edt_class_code table).
     * @param id ID_CLASS_CODE
     * @return An non-null list with all the classif objects (Chm62edtClassCodePersist objects)
     */
    public static String getClassificationName(String id) {
        String result;

        try {
            result = ((Chm62edtClassCodePersist) (new Chm62edtClassCodeDomain().findWhere(" ID_CLASS_CODE = '" + id + "'").get(0))).getClassName();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            result = "";
        }

        return result;
    }

    /**
     * Try to find some examples of codes from a given classification.
     * @param idClassification The classification code to retrieve (chm62edt_class_code.ID_CLASS_CODE). If idClassCode = -1
     * it means that the search is done in current classification.
     * @param codePartSQL - Code parameter (for example CODE LIKE 'B2%')
     * @param database Possible values are: CodeDomain.SEARCH_BOTH/CodeDomain.SEARCH_EUNIS/CodeDomain.SEARCH_ANNEX).
     * @return If idClassCode == -1 (current classification) the result will contain list of
     * <code>ro.finsiel.eunis.eunis.jrfTables.Chm62edtHabiatatPersist</code> objects, otherwise the list will contain
     * objects of type <code>ro.finsiel.eunis.jrfTables.Chm62edtHabitatClassCodePersist objects</code>.
     */
    public static List findDatabaseClassificationsExamples(int idClassification, String codePartSQL, int database) {
        List results = new ArrayList();
        String sql;

        if (-1 == idClassification) // Current classification
        {
            sql = "SELECT * FROM chm62edt_habitat WHERE (1 = 1) ";
            sql += " AND IF(TRIM(chm62edt_habitat.CODE_2000) <> '',RIGHT(chm62edt_habitat.CODE_2000,2),1) <> IF(TRIM(chm62edt_habitat.CODE_2000) <> '','00',2) AND IF(TRIM(chm62edt_habitat.CODE_2000) <> '',LENGTH(chm62edt_habitat.CODE_2000),1) = IF(TRIM(chm62edt_habitat.CODE_2000) <> '',4,1) ";
            if (database != CodeDomain.SEARCH_BOTH.intValue()) {
                if (database == CodeDomain.SEARCH_EUNIS.intValue()) {
                    sql += " AND EUNIS_HABITAT_CODE " + codePartSQL;
                    sql += " AND EUNIS_HABITAT_CODE IS NOT NULL";
                    sql += " AND EUNIS_HABITAT_CODE <> '0' ";
                    sql += " AND TRIM(EUNIS_HABITAT_CODE) <> '' ";
                    sql += " AND ID_HABITAT>=1 AND HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%'  ";
                    sql += " GROUP BY EUNIS_HABITAT_CODE ";
                }
                if (database == CodeDomain.SEARCH_ANNEX.intValue()) {
                    sql += " AND CODE_ANNEX1 " + codePartSQL;
                    sql += " AND CODE_ANNEX1 IS NOT NULL";
                    sql += " AND CODE_ANNEX1 <> '0' ";
                    sql += " AND TRIM(CODE_ANNEX1) <> '' ";
                    sql += " AND HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  ";
                    sql += " GROUP BY CODE_ANNEX1 ";
                }
            } else {
                sql += " AND ((EUNIS_HABITAT_CODE " + codePartSQL
                        + " AND EUNIS_HABITAT_CODE <> '0' AND EUNIS_HABITAT_CODE IS NOT NULL AND TRIM(EUNIS_HABITAT_CODE)<>'') "
                        + " OR (CODE_ANNEX1 " + codePartSQL
                        + " AND CODE_ANNEX1 <> '0' AND CODE_ANNEX1 IS NOT NULL AND TRIM(CODE_ANNEX1)<>'')) ";
                sql += " AND ID_HABITAT <> '-1' AND ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "'  ";
                sql += " GROUP BY EUNIS_HABITAT_CODE,CODE_ANNEX1 ";
            }
            try {
                results = new Chm62edtHabitatDomain().findCustom(sql);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else {
            sql = "SELECT A.ID_HABITAT,A.ID_NATURE_OBJECT,A.SCIENTIFIC_NAME,A.DESCRIPTION,A.CODE_2000, "
                    + "A.CODE_ANNEX1,A.PRIORITY,A.EUNIS_HABITAT_CODE,A.CLASS_REF,A.CODE_PART_2,A.LEVEL,B.CODE "
                    + " FROM chm62edt_habitat AS A ";
            sql += " INNER JOIN chm62edt_habitat_class_code AS B ON A.ID_HABITAT = B.ID_HABITAT WHERE (1 = 1) ";
            sql += " AND IF(TRIM(A.CODE_2000) <> '',RIGHT(A.CODE_2000,2),1) <> IF(TRIM(A.CODE_2000) <> '','00',2) AND IF(TRIM(A.CODE_2000) <> '',LENGTH(A.CODE_2000),1) = IF(TRIM(A.CODE_2000) <> '',4,1) ";
            if (database != CodeDomain.SEARCH_BOTH.intValue()) {
                if (database == CodeDomain.SEARCH_EUNIS.intValue()) {
                    sql += " AND A.EUNIS_HABITAT_CODE IS NOT NULL";
                    sql += " AND A.EUNIS_HABITAT_CODE <> '0' ";
                    sql += " AND TRIM(A.EUNIS_HABITAT_CODE) <> '' ";
                    sql += " AND A.ID_HABITAT > '1' AND A.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%' ";
                    sql += " AND ((A.EUNIS_HABITAT_CODE " + codePartSQL + ")";
                    sql += " OR (B.CODE " + codePartSQL + " AND B.CODE IS NOT NULL AND TRIM(B.CODE)<>''))";
                }
                if (database == CodeDomain.SEARCH_ANNEX.intValue()) {
                    sql += " AND A.CODE_ANNEX1 IS NOT NULL";
                    sql += " AND A.CODE_ANNEX1 <> '0' ";
                    sql += " AND TRIM(A.CODE_ANNEX1) <> '' ";
                    sql += " AND A.HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  ";
                    sql += " AND ((A.CODE_ANNEX1 " + codePartSQL + ")";
                    sql += " OR (B.CODE " + codePartSQL + "  AND B.CODE IS NOT NULL AND TRIM(B.CODE)<>''))";
                }
            } else {
                sql += " AND ((A.EUNIS_HABITAT_CODE " + codePartSQL
                        + " AND A.EUNIS_HABITAT_CODE <> '0' AND A.EUNIS_HABITAT_CODE IS NOT NULL AND TRIM(A.EUNIS_HABITAT_CODE)<>'') "
                        + " OR (A.CODE_ANNEX1 " + codePartSQL
                        + " AND A.CODE_ANNEX1 <> '0' AND A.CODE_ANNEX1 IS NOT NULL AND TRIM(A.CODE_ANNEX1)<>'')" + " OR (B.CODE "
                        + codePartSQL + "  AND B.CODE IS NOT NULL AND TRIM(B.CODE)<>''))";
                sql += " AND A.ID_HABITAT <> '-1' AND A.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "'  ";
            }
            sql += " AND B.ID_CLASS_CODE='" + idClassification + "' ";
            sql += " GROUP BY A.EUNIS_HABITAT_CODE, A.CODE_ANNEX1, B.CODE ";
            try {
                results = new CodeDomain().findCustom(sql);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        if (null == results) {
            results = new ArrayList();
        }
        return results;
    }

    /**
     * Count the number of available habitats belonging to a classification.
     * @param habClassCode The class code of habitat.
     * @return -1 or the number of habitats in a
     */
    public static long countHabitatsInClassification(Integer habClassCode) {
        long result = -1;

        try {
            result = new Chm62edtHabitatClassCodeDomain().countWhere("ID_CLASS_CODE='" + habClassCode + "'").longValue();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            result = -1;
        }
        return result;
    }

    /**
     * This method retrieves the name of the habitat based on its code (eg A -> Marine habitat). ONLY FOR EUNIS.
     * @param habCode Code of the habitat.
     * @return non-null string with habitat's name.
     */
    public static String getHabitatLevelName(String habCode) {
        if (null == habCode) {
            return "n/a";
        }
        if (habCode.equalsIgnoreCase("any")) {
            return " all habitats ";
        }
        String ret = "";

        try {
            List list = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE='" + habCode + "'");

            if (!list.isEmpty()) {
                Chm62edtHabitatPersist hab = (Chm62edtHabitatPersist) list.get(0);

                ret = (null != hab) ? hab.getScientificName() : "n/a";
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            ret = "n/a";
        }
        return ret;
    }

    /**
     * Search other classification where a habitat is specified.
     * @param habitatID ID of the habitat whose references are computed.
     * @return An non-null list of OtherClassWrapper objects with references for this habitat.
     */
    public static List findOtherClassifications(Integer habitatID) {
        Vector result = new Vector();
        // Find in HABITAT_CLASS_CODE WHERE ID_HABITAT = habitatID
        List list = new Chm62edtHabitatClassCodeDomain().findWhere("ID_HABITAT=" + habitatID);
        Iterator it = list.iterator();

        while (it.hasNext()) {
            Chm62edtHabitatClassCodePersist aHabitat = (Chm62edtHabitatClassCodePersist) it.next();
            String relationType = (null == aHabitat.getRelationType() || aHabitat.getRelationType().equalsIgnoreCase(""))
                    ? "not specified"
                    : aHabitat.getRelationType();
            List l1 = new Chm62edtClassCodeDomain().findWhere("ID_CLASS_CODE = " + aHabitat.getIdClassCode());

            if (l1.size() > 0) {
                Chm62edtClassCodePersist cd = (Chm62edtClassCodePersist) l1.get(0);

                result.add(new OtherClassWrapper(aHabitat.getCode(), cd.getClassName(), relationType));
            } else {
                // Add the data to the row
                result.add(new OtherClassWrapper(aHabitat.getCode(), "unknown", relationType));
            }
        }
        return result;
    }

    /**
     * Find an the equivalent of a habitat from a classification in other classifications.
     * Used in habitats-code-result.jsp.<br />
     * This search must be interpreted like this:<br />
     * Find the all the habitats with 'code relationOp searchString' (ex. code LIKE A1)
     * equivalent with the habitat with ID=idHabitat in all the classifications.
     * @param habitatID ID of the current habitat
     * @param relationOp Relation with other habitats (IS, CONTAINS etc)
     * @param searchString The code which was base of search (ex. A1)
     * @return A list of habitats equivalent with this habitat
     */
    public static List findOtherClassifications(int habitatID, Integer relationOp, String searchString) {
        Vector result = new Vector();
        // Find in HABITAT_CLASS_CODE WHERE ID_HABITAT = habitatID
        List list = new Chm62edtHabitatClassCodeDomain().findWhere(
                "ID_HABITAT=" + habitatID + " AND " + Utilities.prepareSQLOperator("CODE", searchString, relationOp));
        Iterator it = list.iterator();

        while (it.hasNext()) {
            Chm62edtHabitatClassCodePersist aHabitat = (Chm62edtHabitatClassCodePersist) it.next();

            String relationType = (null == aHabitat.getRelationType() || aHabitat.getRelationType().equalsIgnoreCase(""))
                    ? "not specified"
                    : aHabitat.getRelationType();

            List l1 = new Chm62edtClassCodeDomain().findWhere("ID_CLASS_CODE = " + aHabitat.getIdClassCode());

            if (l1.size() > 0) {
                Chm62edtClassCodePersist cd = (Chm62edtClassCodePersist) l1.get(0);

                result.add(
                        new OtherClassWrapper(aHabitat.getCode(), cd.getClassName(), relationType, cd.getIdClassCode().toString(),
                        aHabitat.getTitle()));
            } else {
                // Add the data to the row
                result.add(new OtherClassWrapper(aHabitat.getCode(), "unknown", relationType, "", aHabitat.getTitle()));
            }

        }
        return result;
    }

    /**
     * This method translates a value from a dictionary.
     * @param dictionary Possible values are defined in ro.finsiel.eunis.search.habitats.advanced.DICT_XXX
     * @param name Name of the term to be translated (column NAME from Dictionary)
     * @return Returns the ID corresponding to that NAME.
     */
    public static String mapDictionaryTermToID(Integer dictionary, String name) {
        Integer result = null;
        List list;

        try {
            switch (dictionary.intValue()) {
            case DictionaryBean.DICT_ALTITUDE:
                list = new Chm62edtAltitudeDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtAltitudePersist) list.get(0)).getIdAltzone();
                }
                break;

            case DictionaryBean.DICT_CHEMISTRY:
                list = new Chm62edtChemistryDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtChemistryPersist) list.get(0)).getIdChemistry();
                }
                break;

            case DictionaryBean.DICT_CLIMATE:
                list = new Chm62edtClimateDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtClimatePersist) list.get(0)).getIdClimate();
                }
                break;

            case DictionaryBean.DICT_COVERAGE:
                list = new Chm62edtCoverDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtCoverPersist) list.get(0)).getIdCover();
                }
                break;

            case DictionaryBean.DICT_HUMIDITY:
                list = new Chm62edtHumidityDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtHumidityPersist) list.get(0)).getIdHumidity();
                }
                break;

            case DictionaryBean.DICT_IMPACT:
                list = new Chm62edtImpactDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtImpactPersist) list.get(0)).getIdImpact();
                }
                break;

            case DictionaryBean.DICT_LIGHT:
                list = new Chm62edtLightIntensityDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtLightIntensityPersist) list.get(0)).getIdLightIntensity();
                }
                break;

            case DictionaryBean.DICT_PH:
                list = new Chm62edtPhDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtPhPersist) list.get(0)).getIdPh();
                }
                break;

            case DictionaryBean.DICT_LIFEFORM:
                list = new Chm62edtLifeFormDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtLifeFormPersist) list.get(0)).getIdLifeForm();
                }
                break;

            case DictionaryBean.DICT_TEMPERATURE:
                list = new Chm62edtTemperatureDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtTemperaturePersist) list.get(0)).getIdTemperature();
                }
                break;

            case DictionaryBean.DICT_USAGE:
                list = new Chm62edtUsageDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtUsagePersist) list.get(0)).getIdUsage();
                }
                break;

            case DictionaryBean.DICT_WATER:
                list = new Chm62edtWaterDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtWaterPersist) list.get(0)).getIdWater();
                }
                break;

            case DictionaryBean.DICT_SUBSTRATE:
                list = new Chm62edtSubstrateDomain().findWhere("NAME='" + name + "'");
                if (null != list && list.size() > 0) {
                    result = ((Chm62edtSubstratePersist) list.get(0)).getIdSubstrate();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            result = new Integer(-1);
        }
        if (null == result) {
            result = new Integer(-1);
        }
        return result.toString();
    }

    /**
     * Find the name of a classification (from chm62edt_class_code) giving its id (ID_CLASS_CODE).
     * @param idClassification ID_CLASS_CODE.
     * @return Associated name (or "" if not found).
     */
    public static String findClassificationName(String idClassification) {
        String name = "";

        try {
            List results = new Chm62edtClassCodeDomain().findWhere("ID_CLASS_CODE='" + idClassification + "'");

            if (null != results && results.size() > 0) {
                name = (((Chm62edtClassCodePersist) results.get(0)).getClassName() == null
                        ? ""
                        : ((Chm62edtClassCodePersist) results.get(0)).getClassName());
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return name;
    }

    /**
     * Find a habitat's name by giving its code (EUNIS_HABITAT_CODE from chm62edt_habitat).
     * @param eunisCode EUNIS_HABITAT_CODE.
     * @return Scientific name (or "" if not found).
     */
    public static String findHabitatNameByEunisCode(String eunisCode) {
        String name = "";

        try {
            List results = new Chm62edtHabitatDomain().findWhere("EUNIS_HABITAT_CODE='" + eunisCode + "'");

            if (null != results && results.size() > 0) {
                name = (((Chm62edtHabitatPersist) results.get(0)).getScientificName() == null
                        ? ""
                        : ((Chm62edtHabitatPersist) results.get(0)).getScientificName());
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return name;
    }

    /**
     * Find a habitat's name by giving its id (ID_HABITAT from chm62edt_habitat).
     * @param habId ID_HABITAT.
     * @return Scientific name (or "" if not found).
     */
    public static String findHabitatNameById(String habId) {
        String name = "";

        try {
            List results = new Chm62edtHabitatDomain().findWhere("ID_HABITAT='" + habId + "'");

            if (null != results && results.size() > 0) {
                name = (((Chm62edtHabitatPersist) results.get(0)).getScientificName() == null
                        ? ""
                        : ((Chm62edtHabitatPersist) results.get(0)).getScientificName());
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return name;
    }

    /**
     * Find the legal instrument reffering a habitat.
     * @param idHabitat ID_HABITAT.
     * @return A list of ro.finsiel.eunis.jrfTables.habitats.legal.EUNISLegalPersist objects.
     */
    public static List findHabitatLegalInstrument(String idHabitat) {
        List results = new Vector();

        try {
            results = new EUNISLegalDomain().findWhere("A.ID_HABITAT='" + idHabitat + "' AND C.LEGAL='1' GROUP BY C.NAME");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            results = new Vector();
        }
        return results;
    }

    /**
     * This method finds the code of the current classification from <code>chm62edt_class_code</code> table.
     * It takes all the rows that have <code>CURRENT_CLASSIFICATION = 1</code> and does a match on the name of the
     * classification so it contains the <code>classification</code> string parameter. For example if you want to find
     * the current classification for EUNIS database you would write:<br />
     * <code>
     * int classificationCode = findCurrentClassification("eunis");
     * </code>.
     * @param classification The name of the classification. Recommended values: "eunis" - EUNIS or "annex" - ANNEX I
     * @return Code of the classification or -1 if error ocurred.
     */
    public static int findCurrentClassificationID(String classification) {
        int ret = -1;

        try {
            List results = new Chm62edtClassCodeDomain().findWhere("CURRENT_CLASSIFICATION='1'"); // Return 2 results

            for (int i = 0; i < results.size(); i++) {
                Chm62edtClassCodePersist code = (Chm62edtClassCodePersist) results.get(i);
                String classificationName = code.getClassName().toLowerCase();

                if (classificationName.indexOf(classification.toLowerCase()) != -1) {
                    ret = code.getIdClassCode().intValue();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (-1 == ret) {
            System.out.println(
                    "findCurrentClassification(" + classification + ") = -1. It is possible that classification code is incorrect.");
        }
        return ret;
    }

    /**
     * Find a habitat from chm62edt_habitat table after its ID.
     * @param idHabitat ID of the habitat.
     * @return English name or empty string if exception ocurred or habitat not found.
     */

    public static String findHabitatByID(Integer idHabitat) {
        String ret = "";

        try {
            List habitats = new Chm62edtHabitatDomain().findWhere("ID_HABITAT=" + idHabitat);

            if (habitats.size() > 0) {
                Chm62edtHabitatPersist habitat = (Chm62edtHabitatPersist) habitats.get(0);

                ret = habitat.getDescription();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * Test method.
     * @param args Command line arguments
     */
    public static void main(String[] args) {}
}
