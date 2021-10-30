package ro.finsiel.eunis.search;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import eionet.eunis.util.Constants;
import ro.finsiel.eunis.exceptions.RecordNotFoundException;
import ro.finsiel.eunis.jrfTables.Chm62edtBiogeoregionDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtBiogeoregionPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtCountryBiogeoregionDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtCountryBiogeoregionPersist;
import ro.finsiel.eunis.jrfTables.Chm62edtCountryDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtCountryPersist;
import ro.finsiel.eunis.jrfTables.habitats.country.CountryDomain;
import ro.finsiel.eunis.search.species.country.CountryWrapper;
import ro.finsiel.eunis.search.species.country.RegionWrapper;
import ro.finsiel.eunis.utilities.SQLUtilities;
import ro.finsiel.eunis.utilities.TableColumns;


/**
 * This class is used to support species-country.jsp file.
 * @author finsiel
 */
public class CountryUtil {

    /**
     * Cache for the countries by , to improve performance
     */
    private static final Map<String, Chm62edtCountryPersist> COUNTRY_PERSIST_BY_EN_NAME = new ConcurrentHashMap<String, Chm62edtCountryPersist>();


    /**
     * This method finds the bioregions located within a country.
     * @param countryCode Country area code. Note: If you specify 'any' as the country string, it will return all of the
     * biogeographic regions available.
     * @return An non-null List object containing the regions as Country.
     */
    public static Vector findRegionsFromCountry(String countryCode) {
        List _regionsCodes = new Vector();

        if (countryCode.equals("any")) {
            // Do the find for any country -> this means return all bioregions
            _regionsCodes = findAllRegionsCodes();
        } else { // or for a specified country
            try {
                _regionsCodes = new Chm62edtCountryBiogeoregionDomain().findWhere(
                        "CODE_COUNTRY='" + countryCode
                        + "' AND CODE_BIOGEOREGION<>'nd'");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Vector _regions = new Vector();

        for (Object _regionsCode : _regionsCodes) {
            Chm62edtCountryBiogeoregionPersist _aRegion = (Chm62edtCountryBiogeoregionPersist) _regionsCode;

            _regions.addElement(
                    new RegionWrapper(
                            regionCode2Name(_aRegion.getCodeBiogeoregion()),
                            _aRegion.getCodeBiogeoregion(), "idGeoscope",
                            _aRegion.getPercentage()));
        }
        new SortList().sort(_regions, SortList.SORT_ASCENDING);
        return _regions;
    }

    /**
     * This method finds all the countries which contains a given region.
     * @param regionCode Region code. Note: If you specify 'any' as the regions string, it will return all of the countries
     * available.
     * @return A non-null List object containing the list of countries.
     */
    public Iterator findCountriesForRegion(String regionCode) {
        List _countryCodes = null;

        if (regionCode.equals("any")) { // Do the find for any region -> this means return all countries
            _countryCodes = findAllCountriesCodes();
        } else {
            try {
                _countryCodes = new Chm62edtCountryBiogeoregionDomain().findWhere(
                        "CODE_BIOGEOREGION='" + regionCode + "'");
            } catch (Exception e) {
                e.printStackTrace();
                _countryCodes = new ArrayList();
            }
        }
        Vector _countries = new Vector();
        Iterator _it = _countryCodes.iterator();

        try {
            while (_it.hasNext()) {
                Chm62edtCountryBiogeoregionPersist _aRegion = (Chm62edtCountryBiogeoregionPersist) _it.next();

                _countries.addElement(
                        new CountryWrapper(
                                countryCode2Name(_aRegion.getCodeCountry()),
                                _aRegion.getCodeCountry(), ""));
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
            return _it;
        }
        new SortList().sort(_countries, SortList.SORT_ASCENDING);
        return _countries.iterator();
    }

    /**
     * This method finds all country codes which contains at least one bioregion (from chm62edt_country_BIOREGION).
     * @return A non-null list of Chm62edtCountryBiogeoregionPersist objects, one for each country
     */
    protected List findAllCountriesCodes() {
        List results = new Vector();

        try {
            results = new Chm62edtCountryBiogeoregionDomain().findWhere(
                    "1=1 GROUP BY CODE_COUNTRY");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * This method finds all the available regions from the database.
     * @return A non-null list of Chm62edtCountryBiogeoregionPersist objects, one for each region.
     */
    protected static List findAllRegionsCodes() {
        List results = new Vector();

        try {
            results = new Chm62edtCountryBiogeoregionDomain().findWhere(
                    "1=1 GROUP BY CODE_BIOGEOREGION");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Transform region code into region name (mapping got from chm62edt_biogeoregion).
     * @param regionCode Code of the region e.g. AT
     * @return The name of that region e.g. Atlantic
     */
    public static String regionCode2Name(String regionCode) {
        if (null == regionCode) {
            return "null";
        }
        String result = "";
        List _list = new Vector();

        try {
            _list = new ro.finsiel.eunis.jrfTables.Chm62edtBiogeoregionDomain().findWhere(
                    "CODE='" + regionCode + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!_list.isEmpty()) {
            result = ((Chm62edtBiogeoregionPersist) _list.get(0)).getBiogeoregionName();
        } else {
            result = (regionCode.equals("any")) ? "any" : "N/A";
        }
        return result;
    }

    /**
     * Transform a country code into a country name by looking to chm62edt_country table in EUNIS_AREA_CODE column.
     * @param code Country code.
     * @return Country name or "N/A" if not found or "null" of code was null.
     */
    public String countryCode2Name(String code) {
        if (null == code) {
            return "null";
        }
        String result = "";
        List _list = new Vector();

        try {
            _list = new Chm62edtCountryDomain().findWhere(
                    "EUNIS_AREA_CODE='" + code + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!_list.isEmpty()) {
            result = ((Chm62edtCountryPersist) _list.get(0)).getAreaNameEnglish();
        } else {
            result = (code.equals("any")) ? "any" : "N/A";
        }
        return result;
    }

    /**
     * This method finds the ID_GEOSCOPE for a region (taken from chm62edt_biogeoregion).
     * @param regionName Name of the region (NAME)
     * @return An integer with ID of the region.
     * @throws ro.finsiel.eunis.exceptions.RecordNotFoundException If no matching records were found.
     */
    public String findRegionIdGeoscope(String regionName) throws RecordNotFoundException {
        if (regionName.equals("any")) {
            return "any";
        }
        Integer ret;
        List _list = new Vector();

        try {
            _list = new Chm62edtBiogeoregionDomain().findWhere(
                    "NAME='" + regionName + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!_list.isEmpty()) {
            ret = ((Chm62edtBiogeoregionPersist) _list.get(0)).getIdGeoscope();
        } else {
            throw new RecordNotFoundException(
                    "No region with name " + regionName + " has been found.");
        }
        return ret.toString();
    }

    /**
     * This method finds the ID_GEOSCOPE for a country (taken from chm62edt_country) by using its AREA_NAME_ENGLISH.
     * @param countryName Name of the country (AREA_NAME_ENGLISH).
     * @return An string with ID of the country.
     * @throws ro.finsiel.eunis.exceptions.RecordNotFoundException If no matching records were found.
     */
    public String findCountryIdGeoscope(String countryName) throws RecordNotFoundException {
        if (countryName.equals("any")) {
            return "any";
        }
        Integer ret;
        List _list = new Vector();

        try {
            _list = new Chm62edtCountryDomain().findWhere(
                    "AREA_NAME_EN='" + countryName + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!_list.isEmpty()) {
            ret = ((Chm62edtCountryPersist) _list.get(0)).getIdGeoscope();
        } else {
            throw new RecordNotFoundException(
                    "findCountryIdGeoscope(String): No country with name "
                            + countryName + " has been found.");
        }
        return ret.toString();
    }

    /**
     * This method finds the ID_GEOSCOPE for a country (taken from chm62edt_country) by using its EUNIS_AREA_CODE.
     * @param countryCode Country code (EUNIS_AREA_CODE).
     * @return A string with ID of the country.
     * @throws ro.finsiel.eunis.exceptions.RecordNotFoundException If no matching records were found.
     */
    public String findCountryIdGeoscope(Object countryCode) throws RecordNotFoundException {
        String code = (String) countryCode;

        if (code.equals("any")) {
            return "any";
        }
        Integer ret;
        List _list = new Vector();

        try {
            _list = new Chm62edtCountryDomain().findWhere(
                    "EUNIS_AREA_CODE='" + code + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!_list.isEmpty()) {
            ret = ((Chm62edtCountryPersist) _list.get(0)).getIdGeoscope();
        } else {
            throw new RecordNotFoundException(
                    "findCountryIdGeoscope(Object): No country with code "
                            + code + " has been found.");
        }
        return ret.toString();
    }

    /**
     * Find country information by giving its english name (AREA_NAME_EN from chm62edt_country).
     * Cached for better performance. The cache entries DO NOT REFRESH!
     * @param englishName Name of the country in english (EXACT MATCH)
     * @return null if nothing found, or the country object with data.
     */
    public static Chm62edtCountryPersist findCountry(String englishName) {

        if(englishName == null) return null;

        if(COUNTRY_PERSIST_BY_EN_NAME.containsKey(englishName)){
            // found in cache
            return COUNTRY_PERSIST_BY_EN_NAME.get(englishName);
        } else {

            Chm62edtCountryPersist country = null;

            try {
                List contries = new Chm62edtCountryDomain().findWhere(
                        "AREA_NAME_EN='" + englishName + "'");

                if (null != contries && contries.size() > 0) {
                    country = (Chm62edtCountryPersist) contries.get(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

            if(country != null){
                // cache it
                COUNTRY_PERSIST_BY_EN_NAME.put(englishName, country);
            }

            return country;
        }
    }

    /**
     * Finds country by its ID.
     *
     * @param countryId
     * @return object containing country data.
     */
    @SuppressWarnings("rawtypes")
    public static Chm62edtCountryPersist findCountry(int countryId) {

        try {
            List contries = new Chm62edtCountryDomain().findWhere("ID_COUNTRY=" + countryId);
            return contries !=null && !contries.isEmpty() ? (Chm62edtCountryPersist) contries.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Find country information by the given EUNIS area code (EUNIS_AREA_CODE column in database).
     *
     * @param areaCode
     * @return null if not found
     */
    public static Chm62edtCountryPersist findCountryByAreaCode(String areaCode) {

        try {
            List countries = new Chm62edtCountryDomain().findWhere("EUNIS_AREA_CODE='" + areaCode + "'");
            return countries == null || countries.isEmpty() ? null : (Chm62edtCountryPersist) countries.iterator().next();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Find a region (from chm62edt_biogeoregion).
     * @param Name Region name.
     * @return Region JRF object.
     */
    public static Chm62edtBiogeoregionPersist findRegion(String Name) {
        Chm62edtBiogeoregionPersist region = null;

        try {
            List regions = new Chm62edtBiogeoregionDomain().findWhere(
                    "NAME='" + Name + "'");

            if (null != regions && regions.size() > 0) {
                region = (Chm62edtBiogeoregionPersist) regions.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return region;
    }

    /**
     * Find ID_GEOSCOPE_LINK for a biogeoregion.
     * @param regionName Name of the region in english.
     * @return null if not found or exception ocurred or the report object associated. Extract idgeoscopelink from there.
     */
    public static Chm62edtBiogeoregionPersist findRegionIDGeoscope(String regionName) {
        Chm62edtBiogeoregionPersist region = null;

        try {
            List regions = new Chm62edtBiogeoregionDomain().findWhere(
                    "NAME='" + regionName + "'");

            if (null != regions && regions.size() > 0) {
                region = (Chm62edtBiogeoregionPersist) regions.get(0);
            }
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
        }
        return region;
    }

    /**
     * Find all objects that are countries, from (from chm62edt_country).
     * @return A list of Chm62edtCountryPersist objects.
     */
    public static List findAllCountries() {
        List ret = null;

        try {
            ret = new Chm62edtCountryDomain().findWhereOrderBy(
                    "ISO_2L<>'' AND ISO_2L<>'null' AND ISO_2L IS NOT NULL AND SELECTION <> 0",
                    "AREA_NAME_EN");
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
            ret = new Vector();
        }
        return ret;
    }

    public static List findAllRegions() {
        List ret;

        try {
            ret = new Chm62edtBiogeoregionDomain().findOrderBy("NAME");
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
            ret = new Vector();
        }
        return ret;
    }

    /**
     * All countries containing string.
     * @param name match string.
     * @return A list of Chm62edtCountryPersist objects.
     */
    public static List findAllCountriesMatchingName(String name) {
        List ret;

        if (null == name) {
            return findAllCountries();
        }
        try {
            ret = new Chm62edtCountryDomain().findCustom(
                    "SELECT * FROM chm62edt_country WHERE AREA_NAME_EN LIKE '%"
                            + name
                            + "%' AND (ISO_2L<>'' AND ISO_2L<>'null' AND ISO_2L IS NOT NULL AND SELECTION <> 0) GROUP BY AREA_NAME_EN ORDER BY AREA_NAME_EN");
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
            ret = new Vector();
        }
        return ret;
    }

    /**
     * Find ISO2L for a country.
     * @param areaNameEn Name of the country.
     * @return ISO2L or null if not found.
     */
    public static String areaNameEn2ISO2L(String areaNameEn) {
        String result = null;

        if (null == areaNameEn) {
            return result;
        }
        try {
            List ret = new Chm62edtCountryDomain().findWhere(
                    "AREA_NAME_EN='" + areaNameEn + "'");

            if (null != ret && ret.size() > 0) {
                result = ((Chm62edtCountryPersist) ret.get(0)).getIso2l();
            } else {
                result = null;
            }
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
            result = null;
        }
        return result;
    }

    /**
     * Find a country by his ID_GEOSCOPE from chm62edt_country.
     * @param idGeoscope Country's ID_GEOSCOPE.
     * @return Country name or '' if not found.
     */
    public static String findCountryByIDGeoscope(Integer idGeoscope) {
        String result = "";

        if (null == idGeoscope) {
            return "";
        }
        try {
            List ret = new Chm62edtCountryDomain().findWhere(
                    "ID_GEOSCOPE='" + idGeoscope + "'");

            if (null != ret && ret.size() > 0) {
                result = ((Chm62edtCountryPersist) ret.get(0)).getAreaNameEnglish();
            } else {
                result = "";
            }
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
            result = "";
        }
        return result;
    }

    /**
     * Find ISO3L for a country (ISO_3L from chm62edt_country).
     * @param name country name.
     * @return ISO_3L or null if not found.
     */
    public static String getIso3LByCountryName(String name) {
        String ISO = "";

        try {
            List country = new Chm62edtCountryDomain().findWhere(
                    "AREA_NAME_EN='" + name + "'");

            if (country != null && country.size() > 0) {
                ISO = (((Chm62edtCountryPersist) country.get(0)).getIso3l()
                        == null
                        ? ""
                                : ((Chm62edtCountryPersist) country.get(0)).getIso3l());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ISO;
    }

    /**
     * Find biogeoregion by its ID Geoscope.
     * @param idGeoscope ID_GEOSCOPE (from chm62edt_biogeoregion).
     * @return Region name.
     */
    public static String findBiogeoregionByIDGeoscope(Integer idGeoscope) {
        // System.out.println( "findBiogeoregionByIDGeoscope::idGeoscope = " + idGeoscope );
        String result;

        try {
            List results = new Chm62edtBiogeoregionDomain().findWhere(
                    "ID_GEOSCOPE='" + idGeoscope + "'");

            if (null != results && results.size() > 0) {
                result = ((Chm62edtBiogeoregionPersist) results.get(0)).getBiogeoregionName();
            } else {
                result = "";
            }
        } catch (Exception _ex) {
            _ex.printStackTrace(System.err);
            result = "";
        }

        return result;
    }

    /**
     * Put list of nature objects in where condition format
     * @param NOList
     * @param alias
     * @return string condition
     */

    public static String getNOListString(List NOList, String alias) {
        if (NOList == null || NOList.size() <= 0) {
            return "";
        }
        StringBuffer result = new StringBuffer();

        result.append(alias);
        result.append(".ID_NATURE_OBJECT IN ( ");
        for (int i = 0; i < NOList.size(); i++) {
            result.append("'");
            result.append(NOList.get(i));
            result.append("'");
            if (i < NOList.size() - 1) {
                result.append(",");
            } else {
                result.append(" ) ");
            }
        }
        return result.toString();
    }

    public static String listIDNOForHabitatCountryLOV(HttpServletRequest request, SQLUtilities sqlc, Integer database) {

        // country name or region name must be not null
        String whereCond = "C.AREA_NAME_EN is not null and trim(C.AREA_NAME_EN) != '' and C.AREA_NAME_EN != 'null' and "
                + " D.NAME is not null and trim(D.NAME) != '' and D.NAME != 'null' ";

        // search depends by habitat database type
        String dbCond = "";

        if (0 != database.compareTo(CountryDomain.SEARCH_BOTH)) {
            if (0 == database.compareTo(CountryDomain.SEARCH_EUNIS)) {
                dbCond += " AND A.ID_HABITAT >=1 AND A.HABITAT_TYPE like '" + Constants.HABITAT_EUNIS + "%' ";
            }
            if (0 == database.compareTo(CountryDomain.SEARCH_ANNEX_I)) {
                dbCond += " AND A.HABITAT_TYPE = '" + Constants.HABITAT_ANNEX1 + "' ";
            }
        } else {
            dbCond += " AND A.ID_HABITAT <>'-1' AND A.ID_HABITAT <> '" + Constants.HABITAT_ANNEX1_ROOT + "' ";
        }

        // conditions concern habitats
        dbCond += " AND IF(TRIM(A.CODE_2000) <> '',RIGHT(A.CODE_2000,2),1) <> IF(TRIM(A.CODE_2000) <> '','00',2) "
                + " AND IF(TRIM(A.CODE_2000) <> '',LENGTH(A.CODE_2000),1) = IF(TRIM(A.CODE_2000) <> '',4,1) ";

        whereCond += dbCond;

        // extract list of habitat nature objects for the first country/region pair from the form(which has the grather index)
        // this was for (coutry-region) and (country-region)
        // Enumeration en = request.getParameterNames();
        // int lenCountryList = 0;
        // while (en.hasMoreElements())
        // {
        // String param = (String) en.nextElement();
        // if(param.indexOf("country") >= 0) lenCountryList ++;
        // }
        //
        // String country = request.getParameter("_" + (lenCountryList == 0 ? 0 : lenCountryList -1) + "country");
        // String region = request.getParameter("_" + (lenCountryList == 0 ? 0 : lenCountryList -1) + "region");

        // this is for (coutry-region) or (country-region)
        String country = request.getParameter("_0country");
        String region = request.getParameter("_0region");

        String sql = "";

        if ((null != country && country.trim().length() > 0)
                || (null != region && region.trim().length() > 0)) {
            if (null != country && country.trim().length() > 0) {
                whereCond += " and c.area_name_en = '" + country + "' ";
            }
            if (null != region && region.trim().length() > 0) {
                whereCond += " and d.name = '" + region + "' ";
            }

            sql = " select distinct a.id_nature_object FROM chm62edt_habitat AS A "
                    + " INNER JOIN chm62edt_reports AS B ON  A.ID_NATURE_OBJECT = B.ID_NATURE_OBJECT ";
            if (null != country && country.trim().length() > 0) {
                sql += " INNER JOIN chm62edt_country AS C ON B.ID_GEOSCOPE = C.ID_GEOSCOPE "
                        + (null != region && region.trim().length() > 0
                        ? "INNER"
                                : "LEFT OUTER")
                                + " JOIN chm62edt_biogeoregion AS D ON B.ID_GEOSCOPE_LINK = D.ID_GEOSCOPE where "
                                + whereCond;
            } else {
                sql += " INNER JOIN chm62edt_biogeoregion AS D ON B.ID_GEOSCOPE_LINK = D.ID_GEOSCOPE "
                        + " LEFT OUTER JOIN chm62edt_country AS C ON B.ID_GEOSCOPE = C.ID_GEOSCOPE where "
                        + whereCond;
            }
        } else {
            // if is not country or region yet selected make un union

            sql = "select distinct a.id_nature_object "
                    + "FROM chm62edt_habitat AS A "
                    + "INNER JOIN chm62edt_reports AS B ON  A.ID_NATURE_OBJECT = B.ID_NATURE_OBJECT "
                    + "INNER JOIN chm62edt_country AS C ON B.ID_GEOSCOPE = C.ID_GEOSCOPE "
                    + "where C.AREA_NAME_EN is not null and trim(C.AREA_NAME_EN) != '' and C.AREA_NAME_EN != 'null' "
                    + dbCond + "union " + "select distinct a.id_nature_object "
                    + "FROM chm62edt_habitat AS A "
                    + "INNER JOIN chm62edt_reports AS B ON  A.ID_NATURE_OBJECT = B.ID_NATURE_OBJECT "
                    + "INNER JOIN chm62edt_biogeoregion AS D ON B.ID_GEOSCOPE_LINK = D.ID_GEOSCOPE "
                    + "where  D.NAME is not null and trim(D.NAME) != '' and D.NAME != 'null' "
                    + dbCond;
        }

        List results = sqlc.ExecuteSQLReturnList(sql, 1);
        List resultsAsStrings = new ArrayList();

        if (results != null && results.size() > 0) {
            for (Object result : results) {
                resultsAsStrings.add(
                        ((TableColumns) result).getColumnsValues().get(
                                0));
            }
        }
        return getNOListString(resultsAsStrings, "B");
    }
}
