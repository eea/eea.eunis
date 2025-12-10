package ro.finsiel.eunis.search.save_criteria;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;
import ro.finsiel.eunis.jrfTables.save_criteria.*;
import ro.finsiel.eunis.jrfTables.habitats.habitatsByReferences.RefDomain;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.search.species.SpeciesSearchUtility;
import ro.finsiel.eunis.search.species.habitats.HabitateSearchCriteria;
import ro.finsiel.eunis.search.habitats.HabitatsSearchUtility;
import ro.finsiel.eunis.search.sites.size.SizeSearchCriteria;
import ro.finsiel.eunis.search.sites.habitats.HabitatSearchCriteria;

import java.util.Vector;
import java.util.List;
import java.util.Hashtable;
import java.sql.*;


/**
 * Date: Sep 19, 2003
 * Time: 4:26:10 PM
 */

/**
 * This class is used in save easy search criteria.
 * @author Finsiel.
 */
public class SaveSearchCriteria {
    // attributesNames vector contains attributes names values witch will be inserts into criteria_attribute table field
    // from eunis_group_search_criteria table
    private Vector attributesNames = new Vector();
    // formFieldAttributes vector contains form field attributes names values witch will be inserts into
    // criteria_form_field_attribute table field from eunis_group_search_criteria table
    private Vector formFieldAttributes = new Vector();
    // formFieldOperators vector contains form field operators names values witch will be inserts into
    // criteria_form_field_operator table field from eunis_group_search_criteria table
    private Vector formFieldOperators = new Vector();
    // booleans vector contains booleans values witch will be inserts into criteria_boolean table field
    // from eunis_group_search_criteria table
    private Vector booleans = new Vector();
    // operators vector contains operators names values witch will be inserts into criteria_operator table field
    // from eunis_group_search_criteria table
    private Vector operators = new Vector();
    // firstValues vector contains first value of attribute names witch will be inserts into
    // criteria_first_value table field from eunis_group_search_criteria table
    private Vector firstValues = new Vector();
    // lastValues vector contains last value of attribute names witch will be inserts into
    // criteria_last_value table field from eunis_group_search_criteria table
    private Vector lastValues = new Vector();

    // name of user who mande this save operation
    private String userName = null;
    // search criterion description
    private String description = null;
    // name of the jsp page where the search is done
    private String fromWhere = null;
    // number of form elements used in save criteria
    private int numberCriteria = 0;
    // criteria name
    private String criteriaName = "";
    // description of jsp pages
    private Hashtable fromWhereMapping = null;
    // habitat database
    private Vector database = new Vector();

    private static final Logger logger = Logger.getLogger(SaveSearchCriteria.class);

    /**
     * This method maps page names with their automatic string description for save criterias
     * when user don't fill the description field.
     */
    private void FromWhereMappings() {
        if (null != fromWhereMapping) {
            return;
        }
        fromWhereMapping = new Hashtable(28);

        fromWhereMapping.put("habitats-legal.jsp", "EUNIS database habitat types protected by Legal Instruments".toUpperCase());
        fromWhereMapping.put("habitats-code.jsp", "habitat types by code / classifications".toUpperCase());
        fromWhereMapping.put("habitats-species.jsp", "Characteristic species in a habitat type".toUpperCase());
        fromWhereMapping.put("sites-names.jsp", "sites by name".toUpperCase());
        fromWhereMapping.put("sites-habitats.jsp", "sites with habitat types".toUpperCase());
        fromWhereMapping.put("sites-size.jsp", "sites after their size / length".toUpperCase());
        fromWhereMapping.put("species-names.jsp", "SPECIES BY SCIENTIFIC NAME / COMMON NAME".toUpperCase());
        fromWhereMapping.put("species-synonyms.jsp", "SPECIES BY SYNONYMS".toUpperCase());
        fromWhereMapping.put("species-groups.jsp", "SPECIES BY GROUP SPECIES NAME".toUpperCase());
        fromWhereMapping.put("species-legal.jsp", "SPECIES BY LEGAL INSTRUMENTS".toUpperCase());
        fromWhereMapping.put("species-country.jsp", "SPECIES BY COUNTRY / REGION".toUpperCase());
        fromWhereMapping.put("habitats-country.jsp", "HABITAT TYPES BY COUNTRY / REGION".toUpperCase());
        fromWhereMapping.put("habitats-names.jsp", "HABITAT TYPES BY NAMES AND DESCRIPTIONS".toUpperCase());
        fromWhereMapping.put("species-threat-international.jsp", "Species threatened at international level".toUpperCase());
        fromWhereMapping.put("species-threat-national.jsp", "Species threatened at COUNTRY level".toUpperCase());
        fromWhereMapping.put("species-habitats.jsp", "Species by habitat types".toUpperCase());
        fromWhereMapping.put("species-sites.jsp", "Species by sites".toUpperCase());
        fromWhereMapping.put("species-books.jsp", "References of Species selected by scientific name".toUpperCase());
        fromWhereMapping.put("sites-country.jsp", "Sites located within a particular country ".toUpperCase());
        fromWhereMapping.put("sites-year.jsp", "sites after their year of establishment ".toUpperCase());
        fromWhereMapping.put("habitats-sites.jsp", "Habitat types within Sites ".toUpperCase());
        fromWhereMapping.put("sites-designated-codes.jsp", "sites by designation types ".toUpperCase());
        fromWhereMapping.put("sites-designations.jsp", "designation types ".toUpperCase());
        fromWhereMapping.put("sites-statistical.jsp", "statistical data about sites ".toUpperCase());
        fromWhereMapping.put("habitats-books.jsp", "References for habitat types selected by scientific name ".toUpperCase());
        fromWhereMapping.put("habitats-references.jsp",
                "  Habitat types for which references are selected by author, date, title, editor and publisher ".toUpperCase());
        fromWhereMapping.put("sites-species.jsp", "sites with species ".toUpperCase());
        fromWhereMapping.put("sites-coordinates.jsp", "sites after theirs coordinates  ".toUpperCase());
        fromWhereMapping.put("species-references.jsp",
                "  Species for which references are selected by author, date, title, editor and publisher ".toUpperCase());
        fromWhereMapping.put("sites-neighborhood.jsp", "Sites neighborhoods".toUpperCase());

    }

    /**
     * Class constructor.
     * @param database numeric values of habitat database (EUNIS, ANNEX I, BOTH)
     * @param numberCriteria number of criterias witch will be insert into eunis_group_search_criteria table for that search
     * @param userName user name
     * @param description description of search
     * @param fromWhere name of page from where search was made
     * @param attributesNames vector with attributes names
     * @param formFieldAttributes vector with form fields for attributes
     * @param formFieldOperators vector with form fields for operators
     * @param booleans vector with booleans
     * @param operators vector with operators
     * @param firstValues vector with first values
     * @param lastValues vector with last values
     */

    public SaveSearchCriteria(Vector database,
                              int numberCriteria,
                              String userName,
                              String description,
                              String fromWhere,
                              Vector attributesNames,
                              Vector formFieldAttributes,
                              Vector formFieldOperators,
                              Vector booleans,
                              Vector operators,
                              Vector firstValues,
                              Vector lastValues) {

        FromWhereMappings();
        this.attributesNames = attributesNames;
        this.formFieldAttributes = formFieldAttributes;
        this.formFieldOperators = formFieldOperators;
        this.booleans = booleans;
        this.operators = operators;
        this.firstValues = firstValues;
        this.lastValues = lastValues;
        this.userName = userName;
        this.description = description;
        this.fromWhere = fromWhere;
        this.numberCriteria = numberCriteria;
        this.database = database;

    }

    /**
     * This method saves the search in eunis_group_search table and her criterias in eunis_group_search_criteria table.
     * @return true if operation was made with success.
     */

    public boolean SaveCriterias() {
        boolean success = false;
        String SQL = "";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            Connection[] con1 = new Connection[numberCriteria];
            PreparedStatement[] ps1 = new PreparedStatement[numberCriteria];

            criteriaName = userName + (CriteriaMaxNumber(userName).toString());
      
            for (int i = 0; i < numberCriteria; i++) {
                con1[i] = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

                SQL = "INSERT INTO eunis_group_search_criteria "
                        + "(CRITERIA_NAME,ID_eunis_group_search_criteria,CRITERIA_ATTRIBUTE,CRITERIA_FORM_FIELD_ATTRIBUTE,"
                        + "CRITERIA_OPERATOR,CRITERIA_FORM_FIELD_OPERATOR,CRITERIA_FIRST_VALUE,CRITERIA_LAST_VALUE,CRITERIA_BOOLEAN)"
                        + "VALUES(?,?,?,?,?,?,?,?,?)";
                ps1[i] = con1[i].prepareStatement(SQL);
                ps1[i].setString(1, criteriaName);
                ps1[i].setString(2, new Integer(i + 1).toString());
                ps1[i].setString(3, (String) attributesNames.get(i));
                ps1[i].setString(4, (String) formFieldAttributes.get(i));
                ps1[i].setString(5, (String) operators.get(i));
                ps1[i].setString(6, (String) formFieldOperators.get(i));
                // String firstValue = Utilities.removeQuotes((String) firstValues.get(i));
                // String firstValue = (firstValues.get(i) == null ? "" : ((String)firstValues.get(i)).replaceAll("'","''"));
                ps1[i].setString(7, (String) firstValues.get(i));
                // String lastValue = Utilities.removeQuotes((String) lastValues.get(i));
                // String lastValue = (lastValues.get(i) == null ? "" : ((String)lastValues.get(i)).replaceAll("'","''"));
                ps1[i].setString(8, (String) lastValues.get(i));
                ps1[i].setString(9, (String) booleans.get(i));

                ps1[i].execute();
                ps1[i] = con1[i].prepareStatement("COMMIT");
                ps1[i].executeUpdate();
                ps1[i].close();
                con1[i].close();
            }

            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "INSERT INTO eunis_group_search (CRITERIA_NAME,DESCRIPTION,USERNAME,FROM_WHERE)VALUES(?,?,?,?)";

            ps = con.prepareStatement(SQL);

            ps.setString(1, criteriaName);
            if (description != null && description.trim().length() > 0) {
                // ps.setString(2, Utilities.removeQuotes(description));
                ps.setString(2, description);
            } else {
                // ps.setString(2, Utilities.removeQuotes(getStringDescription()));
                ps.setString(2, getStringDescription());
            }
            ps.setString(3, userName);
            ps.setString(4, fromWhere);

            ps.execute();
            con.commit();
            ps.close();
            con.close();

            if (!existEunisGroupSearchByCriteriaName() || !existEunisGroupSearchCriteriaByCriteriaName()) {
                deleteIfErrorToInsert();
            } else {
                success = true;
            }
        } catch (Exception e) {
            logger.debug(e, e); 
            deleteIfErrorToInsert();
        }

        return success;
    }

    /**
     * Return max number from values of CRITERIA_NAME fields for a user (Ex : for 'root0','root1','root2' return 2 for user 'root').
     * @param user user name
     * @return max number
     */
    private Long CriteriaMaxNumber(String user) {
        String SQL1 = "";
        ResultSet rs = null;
        Connection con1 = null;
        Statement st = null;

        Long result = new Long(0);

        try {
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL1 = " SELECT MAX(CAST(SUBSTRING(CRITERIA_NAME,LENGTH('" + user + "')+1,LENGTH(CRITERIA_NAME)) AS SIGNED))"
                    + " FROM eunis_group_search" + " WHERE USERNAME = '" + user + "'";

            st = con1.createStatement();
            rs = st.executeQuery(SQL1);

            if (rs.isBeforeFirst()) {
                while (!rs.isLast()) {
                    rs.next();
                    result = new Long(rs.getLong(1) + 1);
                }
            }

            rs.close();
            st.close();
            con1.close();
        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * This method return a automatic description of search if user hasn't insert something in description field.
     * @return string description.
     */

    public String getStringDescription() {
        String result = "";
        boolean allStringNull = true;
        String beginString = "";

        try {
            List criteriaSearch = new EunisGroupSearchCriteriaDomain().findWhere("CRITERIA_NAME ='" + criteriaName + "'");

            if (criteriaSearch != null && criteriaSearch.size() > 0) {
                for (int j = 0; j < criteriaSearch.size(); j++) {
                    EunisGroupSearchCriteriaPersist criteria = (EunisGroupSearchCriteriaPersist) criteriaSearch.get(j);

                    // check boxes forms for witch criteria form field attribute from database table contains ',' like sourceDB from sites search
                    if (criteria.getFormFieldAttribute() != null && criteria.getFormFieldAttribute().lastIndexOf(",") != -1) {
                        // source database from sites(only to sites searches)
                        if (criteria.getAttribute() != null && criteria.getAttribute().equalsIgnoreCase("sourceDB")
                                && criteria.getFirstValue() != null) {
                            result += " Source data set is " + getSourceDBString(criteria.getFirstValue());
                            result += (criteria.getCriteriaBoolean() == null ? " " : " " + criteria.getCriteriaBoolean() + " ");
                        }
                        // 'search in ' from habitats names search
                        if (criteria.getFormFieldAttribute().equalsIgnoreCase("useScientific,useVernacular,useDescription")
                                && criteria.getFirstValue() != null) {
                            result += " search will be made by "
                                    + criteria.getFirstValue().replaceAll("use", "").replaceAll("=true", "").substring(0, criteria.getFirstValue().replaceAll("use", "").replaceAll("=true", "").length() - 1).replaceAll(
                                            ",", " and by ")
                                            + " name ";
                            result += (criteria.getCriteriaBoolean() == null ? " " : " " + criteria.getCriteriaBoolean() + " ");
                        }
                        continue;
                    }
                    // Select database from habitats(only to habitats searches)
                    if (criteria.getFormFieldAttribute() != null && criteria.getFormFieldAttribute().equalsIgnoreCase("database")
                            && criteria.getFirstValue() != null) {
                        result += criteria.getAttribute() + " " + criteria.getOperator() + " ";

                        if (criteria.getFirstValue().equalsIgnoreCase((String) database.get(0))) {
                            result += "EUNIS";
                        }
                        if (criteria.getFirstValue().equalsIgnoreCase((String) database.get(1))) {
                            result += "ANNEX I";
                        }
                        if (criteria.getFirstValue().equalsIgnoreCase((String) database.get(2))) {
                            result += "BOTH";
                        }

                        result += (criteria.getCriteriaBoolean() == null ? " " : " " + criteria.getCriteriaBoolean() + " ");
                        continue;
                    }
                    // source reference from habitats references (only there)
                    if (criteria.getFormFieldAttribute() != null && criteria.getFormFieldAttribute().equalsIgnoreCase("source")
                            && criteria.getFirstValue() != null) {
                        result += criteria.getAttribute() + " " + criteria.getOperator() + " ";

                        if (criteria.getFirstValue().equalsIgnoreCase(RefDomain.SOURCE.toString())) {
                            result += "Source information";
                        }
                        if (criteria.getFirstValue().equalsIgnoreCase(RefDomain.OTHER_INFO.toString())) {
                            result += "Other information";
                        }

                        result += (criteria.getCriteriaBoolean() == null ? " " : " " + criteria.getCriteriaBoolean() + " ");
                        continue;
                    }
                    // ordinary criteria
                    if (criteria.getFirstValue() != null && !criteria.getFirstValue().equalsIgnoreCase("")
                            && criteria.getAttribute() != null && !criteria.getAttribute().equalsIgnoreCase("")) {
                        allStringNull = false;
                        // if this criterion has operator in form page
                        if (criteria.getFormFieldOperator() != null && !criteria.getFormFieldOperator().equalsIgnoreCase("")) {
                            result += Utilities.prepareHumanString(parseAttributeNameIfNumber(criteria.getAttribute(), fromWhere),
                                    criteria.getFirstValue(), criteria.getFirstValue(), criteria.getLastValue(),
                                    Utilities.checkedStringToInt(criteria.getOperator(), new Integer(-1)));
                        } else {
                            if (criteria.getOperator() != null) {
                                if (!criteria.getOperator().equalsIgnoreCase("between")) {
                                    if (criteria.getFormFieldAttribute() != null
                                            && criteria.getFormFieldAttribute().lastIndexOf("/") != -1) {
                                        result += parseAttributeNameIfNumber(criteria.getAttribute(), fromWhere) + " "
                                                + criteria.getOperator() + " "
                                                + parseFirstValue(criteria.getFormFieldAttribute(), criteria.getFirstValue(),
                                                fromWhere)
                                                + " / "
                                                + criteria.getLastValue();
                                    } else {
                                        result += parseAttributeNameIfNumber(criteria.getAttribute(), fromWhere) + " "
                                                + criteria.getOperator() + " "
                                                + parseFirstValue(criteria.getFormFieldAttribute(), criteria.getFirstValue(),
                                                fromWhere);
                                    }
                                } else {
                                    result += parseAttributeNameIfNumber(criteria.getAttribute(), fromWhere) + " "
                                            + criteria.getOperator() + " "
                                            + (criteria.getFirstValue() == null || criteria.getFirstValue().trim().length() <= 0
                                            ? "-"
                                            : criteria.getFirstValue())
                                            + " and "
                                            + (criteria.getLastValue() == null || criteria.getLastValue().trim().length() <= 0
                                                    ? "-"
                                                    : criteria.getLastValue());
                                }
                            }
                        }
                        result += (criteria.getCriteriaBoolean() == null ? " " : " " + criteria.getCriteriaBoolean() + " ");
                    } else {
                        if (criteria.getOperator().equalsIgnoreCase("between")) {
                            result += parseAttributeNameIfNumber(criteria.getAttribute(), fromWhere) + " " + criteria.getOperator()
                                    + " "
                                    + (criteria.getFirstValue() == null || criteria.getFirstValue().trim().length() <= 0
                                    ? "-"
                                    : criteria.getFirstValue())
                                    + " and "
                                    + (criteria.getLastValue() == null || criteria.getLastValue().trim().length() <= 0
                                            ? "-"
                                            : criteria.getLastValue());
                            result += (criteria.getCriteriaBoolean() == null ? " " : " " + criteria.getCriteriaBoolean() + " ");
                        }
                    }
                }
            }
            if (allStringNull) {
                beginString = "You searched ALL " + (String) fromWhereMapping.get(fromWhere.trim())
                        + (result.length() > 0 ? " having next information :  " : "");
            } else {
                beginString = "You searched " + (String) fromWhereMapping.get(fromWhere.trim()) + " having next information : ";
            }

        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return beginString + result;
    }

    /**
     * This method parse attribute value if this is a number (like in sites-size search where value of attribute name
     *  of search, area/length, is a number (4 for area and 5 for length)).
     * @param attributeName name of attribute
     * @param fromWhere page name
     * @return attribute name if this is not a number and right string if it is
     */

    private String parseAttributeNameIfNumber(String attributeName, String fromWhere) {
        String result = "";

        try {
            if (Utilities.checkedStringToInt(attributeName, -1) == -1) {
                result = attributeName;
            } else {
                if (fromWhere != null && fromWhere.equalsIgnoreCase("sites-size.jsp")) {
                    if (SizeSearchCriteria.SEARCH_AREA.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "area";
                    }
                    if (SizeSearchCriteria.SEARCH_LENGTH.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "length";
                    }
                }
                if (fromWhere != null && fromWhere.equalsIgnoreCase("habitats-species.jsp")) {
                    if (ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Species scientific name";
                    }
                    if (ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria.SEARCH_COUNTRY.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Country name";
                    }
                    if (ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria.SEARCH_GROUP.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Group name";
                    }
                    if (ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Legal instrument name";
                    }
                    if (ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria.SEARCH_REGION.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Region name";
                    }
                    if (ro.finsiel.eunis.search.habitats.species.SpeciesSearchCriteria.SEARCH_VERNACULAR.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Common name";
                    }
                }
                if (fromWhere != null && fromWhere.equalsIgnoreCase("habitats-sites.jsp")) {
                    if (ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria.SEARCH_NAME.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Site name";
                    }
                    if (ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria.SEARCH_SIZE.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Site size";
                    }
                    if (ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria.SEARCH_LENGTH.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Site length";
                    }
                    if (ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria.SEARCH_COUNTRY.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Country name";
                    }
                    if (ro.finsiel.eunis.search.habitats.sites.SitesSearchCriteria.SEARCH_REGION.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Biogeographic region name";
                    }
                }
                if (fromWhere != null && fromWhere.equalsIgnoreCase("species-habitats.jsp")) {
                    if (HabitateSearchCriteria.SEARCH_CODE.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Habitat type code";
                    }
                    if (HabitateSearchCriteria.SEARCH_COUNTRY.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Country name";
                    }
                    if (HabitateSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Legal instrument name";
                    }
                    if (HabitateSearchCriteria.SEARCH_REGION.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Biogeographic region name";
                    }
                    if (HabitateSearchCriteria.SEARCH_NAME.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "English name or description";
                    }
                }
                if (fromWhere != null && fromWhere.equalsIgnoreCase("species-sites.jsp")) {
                    if (ro.finsiel.eunis.search.species.sites.SitesSearchCriteria.SEARCH_NAME.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Site name";
                    }
                    if (ro.finsiel.eunis.search.species.sites.SitesSearchCriteria.SEARCH_SIZE.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Site size";
                    }
                    if (ro.finsiel.eunis.search.species.sites.SitesSearchCriteria.SEARCH_LENGTH.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Site length";
                    }
                    if (ro.finsiel.eunis.search.species.sites.SitesSearchCriteria.SEARCH_COUNTRY.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Country name";
                    }
                    if (ro.finsiel.eunis.search.species.sites.SitesSearchCriteria.SEARCH_REGION.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Biogeographic region name";
                    }
                }
                if (fromWhere != null && fromWhere.equalsIgnoreCase("sites-species.jsp")) {
                    if (ro.finsiel.eunis.search.sites.species.SpeciesSearchCriteria.SEARCH_SCIENTIFIC_NAME.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Species scientific name";
                    }
                    if (ro.finsiel.eunis.search.sites.species.SpeciesSearchCriteria.SEARCH_COUNTRY.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Country name";
                    }
                    if (ro.finsiel.eunis.search.sites.species.SpeciesSearchCriteria.SEARCH_GROUP.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Group name";
                    }
                    if (ro.finsiel.eunis.search.sites.species.SpeciesSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Legal instrument name";
                    }
                    if (ro.finsiel.eunis.search.sites.species.SpeciesSearchCriteria.SEARCH_REGION.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Region name";
                    }
                    if (ro.finsiel.eunis.search.sites.species.SpeciesSearchCriteria.SEARCH_VERNACULAR.intValue()
                            == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Common name";
                    }
                }
                if (fromWhere != null && fromWhere.equalsIgnoreCase("sites-habitats.jsp")) {
                    if (HabitatSearchCriteria.SEARCH_CODE.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Habitat type code";
                    }
                    if (HabitatSearchCriteria.SEARCH_COUNTRY.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Country name";
                    }
                    if (HabitatSearchCriteria.SEARCH_LEGAL_INSTRUMENTS.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Legal instrument name";
                    }
                    if (HabitatSearchCriteria.SEARCH_REGION.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "Biogeographic region name";
                    }
                    if (HabitatSearchCriteria.SEARCH_NAME.intValue() == Utilities.checkedStringToInt(attributeName, -1)) {
                        result = "English name or description";
                    }
                }
            }

        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * This method parse attribute name in habitats-country search where attribute name is like '_0country' and it must
     * be 'country'.
     * @param attributeName name of attribute
     * @param fromWhere page name
     * @return attribute name if search is not habitats-country search and right string if it is
     */

    private static String parseAttributeName(String attributeName, String fromWhere) {
        String result = "";

        try {
            if (fromWhere != null && fromWhere.equalsIgnoreCase("habitats-country.jsp")) {
                if (attributeName != null) {
                    result = attributeName.replaceAll("_", "");
                    for (int l = 0; l <= 5; l++) {
                        result = result.replaceAll(new Integer(l).toString(), "");
                    }
                }
            } else {
                result = attributeName;
            }
        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * This method change name of form field attribute from a value in another value like in species-group search
     * ("ID" is changed in "groupID").
     * @param name name of attribute
     * @param pageName page name
     * @return right string
     */

    private static String parseFormFieldAttribute(String name, String pageName) {
        String result = "";

        try {
            if (pageName != null && pageName.equalsIgnoreCase("species-groups.jsp")) {
                if (name != null && name.equalsIgnoreCase("ID")) {
                    result = "groupID";
                    return result;
                }
            }
            result = name;
        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * In some searches like habitats codes drop down elements have one value and another text is displayed.
     * @param attribute name of attribute
     * @param firstValue first value
     * @param pageName page name
     * @return right string
     */


    private String parseFirstValue(String attribute, String firstValue, String pageName) {
        String result = "";

        try {
            if (pageName != null && pageName.equalsIgnoreCase("habitats-code.jsp")) {
                if (attribute != null && attribute.equalsIgnoreCase("classificationCode")) {
                    result = HabitatsSearchUtility.findClassificationName(firstValue);
                    return result;
                }
            }
            if (pageName != null && pageName.equalsIgnoreCase("habitats-legal.jsp")) {
                if (attribute != null && attribute.equalsIgnoreCase("habitatType")) {
                    result = firstValue + " - " + HabitatsSearchUtility.findHabitatNameByEunisCode(firstValue);
                    return result;
                }
            }
            if (pageName != null && pageName.equalsIgnoreCase("species-synonyms.jsp")) {
                if (attribute != null && attribute.equalsIgnoreCase("groupName")) {
                    result = SpeciesSearchUtility.findGroupName(firstValue);
                    return result;
                }
            }

            result = firstValue;

        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * This method parse sourceDB string.
     * @param value string with sourceDB values
     * @return right string
     */

    private String getSourceDBString(String value) {
        String result = "";

        try {
            if (value != null && value.length() > 0) {
                if (value.lastIndexOf("NATURA2000") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "Natura 2000";
                }
                if (value.lastIndexOf("CDDA_NATIONAL") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "CDDA  National";
                }
                if (value.lastIndexOf("DB_NATURE_NET") != -1 || value.lastIndexOf("NATURENET") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "NatureNet";
                }
                if (value.lastIndexOf("CORINE") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "Corine Biotopes";
                }
                if (value.lastIndexOf("CDDA_INTERNATIONAL") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "CDDA International";
                }
                if (value.lastIndexOf("DIPLOMA") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "European Diploma";
                }
                if (value.lastIndexOf("BIOGENETIC") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "Biogenetic Reserve";
                }
                if (value.lastIndexOf("EMERALD") != -1) {
                    if (result.length() > 0) {
                        result += " or ";
                    }
                    result += "Emerald";
                }
            }
        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * This method return true if exist a search in eunis_group_search table with a given criteria name, and false
     * otherwise.
     * @return true or false
     */

    private boolean existEunisGroupSearchByCriteriaName() {
        boolean result = false;

        try {
            List criterias = new EunisGroupSearchDomain().findWhere("CRITERIA_NAME ='" + criteriaName + "'");

            if (criterias != null && criterias.size() > 0) {
                result = true;
            }
        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * This method return true if exist a search in eunis_group_search_criteria table with a given criteria name, and
     * false otherwise.
     * @return true or false
     */

    private boolean existEunisGroupSearchCriteriaByCriteriaName() {
        boolean result = false;

        try {
            List criterias = new EunisGroupSearchCriteriaDomain().findWhere("CRITERIA_NAME ='" + criteriaName + "'");

            if (criterias != null && criterias.size() == numberCriteria) {
                result = true;
            }
        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return result;
    }

    /**
     * This method delete records from eunis_group_search and eunis_group_search_criteria with a given criteria name
     * if it was errors at insert process.
     */

    private void deleteIfErrorToInsert() {
        try {
            String SQL = "";
            Connection con = null;
            PreparedStatement ps = null;
            Connection con1 = null;
            PreparedStatement ps1 = null;

            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
            con1 = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            SQL = "DELETE FROM eunis_group_search WHERE CRITERIA_NAME='" + criteriaName + "'";
            ps = con.prepareStatement(SQL);
            ps.execute();
            ps.close();
            con.close();

            SQL = "DELETE FROM eunis_group_search_criteria WHERE CRITERIA_NAME='" + criteriaName + "'";
            ps1 = con1.prepareStatement(SQL);
            ps1.execute();
            ps1.close();
            con1.close();

        } catch (Exception e) {
            logger.debug(e, e); 
        }
    }

    /**
     * This method return the url string for each search that is dispayed on a web page when user expand "Show saved
     * search criterias".
     * @param userName user name
     * @param pageName page name
     * @param criteriaName criteria name
     * @return url string
     */

    public static String getURLForUserNameAndPageName(String userName, String pageName, String criteriaName) {
        String url = "";

        try {

            List criterias = new CriteriasForUsersDomain().findWhere(
                    "USERNAME='" + userName + "' AND FROM_WHERE='" + pageName + "' AND eunis_group_search.CRITERIA_NAME='"
                    + criteriaName + "'");

            if (criterias != null && criterias.size() > 0) {
                for (int i = 0; i < criterias.size(); i++) {
                    CriteriasForUsersPersist criteria = (CriteriasForUsersPersist) criterias.get(i);
                    String firstValue = criteria.getCriteriaFirstValue();
                    String lastValue = criteria.getCriteriaLastValue();

                    // for check boxes like sourceDB on sites searches or 'search in' on habitats names search
                    if (criteria.getCriteriaAttribute() != null
                            && (criteria.getCriteriaAttribute().equalsIgnoreCase("sourceDB")
                                    || criteria.getCriteriaFormFieldAttribute().equalsIgnoreCase(
                                            "useScientific,useVernacular,useDescription"))) {
                        if (criteria.getCriteriaFirstValue() != null && criteria.getCriteriaFirstValue().length() > 0) {
                            url += "&"
                                    + criteria.getCriteriaFirstValue().replace(',', '&').substring(0,
                                    criteria.getCriteriaFirstValue().length() - 1);
                        }
                        continue;
                    }

                    // criteria with operator = between
                    if (criteria.getCriteriaFormFieldAttribute() != null
                            && criteria.getCriteriaFormFieldAttribute().lastIndexOf('/') != -1) {
                        Vector elem = Utilities.tokenizeString(criteria.getCriteriaFormFieldAttribute(), "/");

                        if (elem != null && elem.size() > 1) {
                            if (elem.size() == 2) {
                                if (criteria.getCriteriaFirstValue() != null && criteria.getCriteriaLastValue() != null
                                        && (!criteria.getCriteriaFirstValue().equalsIgnoreCase("")
                                        || !criteria.getCriteriaLastValue().equalsIgnoreCase(""))) {
                                    url += "&" + parseAttributeName((String) elem.get(0), pageName) + "=" + firstValue;
                                    url += "&" + parseAttributeName((String) elem.get(1), pageName) + "=" + lastValue;
                                }
                            }
                            if (elem.size() == 3) {
                                if (criteria.getCriteriaOperator().equalsIgnoreCase(Utilities.OPERATOR_BETWEEN.toString())
                                        || criteria.getCriteriaOperator().trim().equalsIgnoreCase("between")) {
                                    url += "&" + (String) elem.get(1) + "=" + firstValue;
                                    url += "&" + (String) elem.get(2) + "=" + lastValue;
                                } else {
                                    url += "&" + (String) elem.get(0) + "=" + firstValue;
                                }
                            }
                        }
                        // if criteria_attribute is number
                        if (criteria.getCriteriaAttribute() != null
                                && Utilities.checkedStringToInt(criteria.getCriteriaAttribute(), -1) != -1) {
                            if (pageName != null && pageName.equalsIgnoreCase("sites-size.jsp")) {
                                url += "&searchType=" + criteria.getCriteriaAttribute();
                            }
                        }

                        // if criteria has relationOp form field
                        if (criteria.getCriteriaFormFieldOperator() != null
                                && !criteria.getCriteriaFormFieldOperator().equalsIgnoreCase("")) {
                            if (pageName != null && pageName.equalsIgnoreCase("sites-size.jsp")) {
                                url += "&" + criteria.getCriteriaFormFieldOperator() + "=" + criteria.getCriteriaFormFieldOperator()
                                        + criteria.getCriteriaOperator();
                            } else {
                                url += "&" + criteria.getCriteriaFormFieldOperator() + "=" + criteria.getCriteriaOperator();
                            }
                        }

                        continue;
                    }

                    // ordinary criteria
                    if (criteria.getCriteriaFormFieldAttribute() != null
                            && criteria.getCriteriaFormFieldAttribute().lastIndexOf('/') == -1
                            && criteria.getCriteriaFormFieldAttribute().lastIndexOf(',') == -1) {
                        url += "&" + parseFormFieldAttribute(criteria.getCriteriaFormFieldAttribute(), pageName) + "=" + firstValue;
                    }

                    // if criteria_attribute is number
                    if (criteria.getCriteriaAttribute() != null
                            && Utilities.checkedStringToInt(criteria.getCriteriaAttribute(), -1) != -1) {
                        if (pageName != null
                                && (pageName.equalsIgnoreCase("habitats-species.jsp")
                                        || pageName.equalsIgnoreCase("habitats-sites.jsp")
                                        || pageName.equalsIgnoreCase("species-habitats.jsp")
                                        || pageName.equalsIgnoreCase("species-sites.jsp")
                                        || pageName.equalsIgnoreCase("sites-species.jsp")
                                        || pageName.equalsIgnoreCase("sites-habitats.jsp"))) {
                            url += "&searchAttribute=" + criteria.getCriteriaAttribute();
                        }
                    }

                    // if criteria has relation form field
                    if (criteria.getCriteriaFormFieldOperator() != null
                            && !criteria.getCriteriaFormFieldOperator().equalsIgnoreCase("")) {
                        if (pageName != null && pageName.equalsIgnoreCase("sites-size.jsp")) {
                            url += "&" + criteria.getCriteriaFormFieldOperator() + "=" + criteria.getCriteriaFormFieldOperator()
                                    + criteria.getCriteriaOperator();
                        } else {
                            url += "&" + criteria.getCriteriaFormFieldOperator() + "=" + criteria.getCriteriaOperator();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e, e); 
        }
        return url;
    }
}
