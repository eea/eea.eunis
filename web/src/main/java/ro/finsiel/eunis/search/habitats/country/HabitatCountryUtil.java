package ro.finsiel.eunis.search.habitats.country;


import eionet.eunis.dao.impl.MySqlBaseDao;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * This utility class extracts from the HTTP request the pairs of country-region which comes as:<br />
 * _country === _region<br />
 * _country === _region<br />
 * ...<br />
 * ...<br />.
 * @author finsiel.
 */
public class HabitatCountryUtil {

    /* Hashtable to keep pairs of country-region values.*/
    private Vector _countries = new Vector(); // Countries
    private Vector _regions = new Vector(); // Regions. _regions.size() = _countries.size()!
    private HttpServletRequest _request = null;
    private static final Logger logger = Logger.getLogger(HabitatCountryUtil.class);

    /**
     * Ctor.
     */
    public HabitatCountryUtil() {}

    /** Constructor which takes the HTTP request and generates the pairs of country-region.
     * @param request HTTP request
     */
    public HabitatCountryUtil(HttpServletRequest request) {
        _request = request;
        Vector countries = extractCountryParams(_request);

        setCountryRegionPairs(_request, countries);
    }

    /**
     * Number of pairs country-region stored.
     * @return Pairs stored
     */
    public int countItems() {
        return _countries.size();
    }

    /**
     * Return a country at a specified index.
     * @param index Index.
     * @return Country name.
     */
    public String getCountry(int index) {
        if (index < _countries.size()) {
            return (String) _countries.get(index);
        } else {
            System.out.println(
                    HabitatCountryUtil.class.getName() + "::getCountry(" + index
                    + ") - Warning: element not removed. Array out of bounds.");
        }
        return null;
    }

    /**
     * Get the region from a specified index.
     * @param index Index.
     * @return Region name.
     */
    public String getRegion(int index) {
        if (index < _regions.size()) {
            return (String) _regions.get(index);
        } else {
            System.out.println(
                    HabitatCountryUtil.class.getName() + "::getRegionIDGeoscope(" + index
                    + ") - Warning: element not removed. Array out of bounds");
        }
        return null;
    }

    /**
     * Getter for _countries property.
     * @return _countries.
     */
    public Vector getCountry() {
        return _countries;
    }

    /**
     * Getter for _regions property.
     * @return _regions.
     */
    public Vector getRegion() {
        return _regions;
    }

    /**
     * Remove a country from a specified index.
     * @param index Index.
     */
    public void removeCountryRegion(int index) {
        if (index < 0) {
            return;
        }
        if (index < _countries.size() && index < _regions.size()) {
            _countries.removeElementAt(index);
            _regions.removeElementAt(index);
        }
    }

    /**
     * Extract country names from request.
     * @param request HTTP request.
     * @return Vector of Strings.
     */
    public Vector extractCountryParams(HttpServletRequest request) {
        Vector ret = new Vector();

        if (null == request) {
            return ret;
        }
        Enumeration en = request.getParameterNames();

        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();

            if (-1 != paramName.lastIndexOf("country")) {
                ret.addElement(paramName);
            }
        }
        SortVector sorter = new SortVector();

        sorter.sort(ret, SortVector.SORT_DESCENDING);
        return ret;
    }

    /**
     * Compute the pairs of country-region from request.
     * @param request HTTP request.
     * @param countries List of countries as Strings.
     */
    public void setCountryRegionPairs(HttpServletRequest request, Vector countries) {
        try {
            // Vector countries = extractCountryParams(request);
            for (int i = 0; i < countries.size(); i++) {
                String country = (String) countries.get(i);
                char prefix = country.charAt(1);

                _countries.addElement(request.getParameter(country)); // Add country
                _regions.addElement(request.getParameter("_" + prefix + "region")); // Add region here.
            }
        } catch (Exception e) {
            logger.debug(e, e);
        }
    }

    /** Class to support the sorting of an given array of Objects<br />
     * In order to do a sorting, you class will  provide a valid
     * <code>toString()</code>
     * method in order to extract the criteria after which sorting is done.
     *
     * @author  finsiel
     */
    public static class SortVector {

        /** Elements in the list are sorted in <i>reverse natural sorting order</i>. */
        public static final boolean SORT_ASCENDING = false;

        /** Elements in the list that are sorted in <i>natural sorting order</i>. */
        public static final boolean SORT_DESCENDING = true;

        /** Creates a new instance of Compare. */
        public SortVector() {}

        /** Method used to sort a given Vector.
         * @param data Vector of objects to be sorted. Remember that
         * their toString method must return a valid comparison
         * @param order Must be either <code>SortList.SORT_ASCENDIG</code>
         * or <code>SortList.SORT_DESCENDING</code>If other specified,
         * no sorting is done
         * @return The sorted Vector. Warning, this functions alters the
         * content of the input <code>data</code> parameter, so if you
         * want to preserve the original order, then do a copy before calling
         * this method.
         * @throws java.lang.ClassCastException - if the list contains elements that are not
         * mutually comparable (i.e. compare a String with an Int)
         * @throws java.lang.UnsupportedOperationException - if the specified list's list-iterator
         * does not support the set operation.
         */
        public Vector sort(Vector data, boolean order) throws ClassCastException, UnsupportedOperationException {
            StringComparator comp = new StringComparator();

            if (SORT_DESCENDING == order) {
                comp.reverseOrder();
            }
            Collections.sort(data, comp);
            return data;
        }

        /**
         * Comparator implementation.
         */
        public static class StringComparator implements Comparator {
            boolean ascending = true;

            /**
             * Reverse the order of sorting.
             */
            public void reverseOrder() {
                ascending = false;
            }

            public int compare(Object element1, Object element2) {
                try {
                    String lower1 = ((String) element1).toLowerCase();
                    String lower2 = ((String) element2).toLowerCase();
                    int res = lower1.compareTo(lower2);

                    res = (!ascending) ? res : (-1) * res;
                    return res;
                } catch (ClassCastException ex) {
                    ex.printStackTrace();
                    return 0;
                }
            }
        }
    }

    /**
     * Transform string to int.
     * @param s string.
     * @param defaultValue a default value.
     * @return int.
     */
    public static int checkedStringToInt(String s, int defaultValue) {
        int p = defaultValue;

        try {
            p = Integer.parseInt(s);
        } catch (NumberFormatException e) {} finally {
            return p;
        }
    }

    /**
     * Set the request.
     * @param request HTTP request.
     */
    public void set_request(HttpServletRequest request) {
        this._request = request;
    }

    /**
     * Return country representation as URL.
     * @return URL string.
     */
    public String getCountryRegAsURL() {
        String url = "";

        // System.out.println("size1="+_regions.size()+","+_countries.size());
        url = "&country=" + (String) _countries.get(0) + "&region=" + (String) _regions.get(0);
        // System.out.println("size="+_regions.size()+","+_countries.size());
        for (int i = 1; i < _regions.size(); i++) {
            // System.out.println("i="+i);
            url += "&country=" + (String) _countries.get(i) + "&region=" + (String) _regions.get(i);
        }
        return url;
    }

    /**
     * Return country representation as URL. Used to save search criteria.
     * @return URL string.
     */
    public String getCountryRegAsURLSave() {
        String url = "";

        // System.out.println("size="+_regions.size()+","+_countries.size());
        for (int i = 1; i < _regions.size(); i++) {
            // System.out.println("i="+i);
            url += "&country=" + (String) _countries.get(i) + "&region=" + (String) _regions.get(i);
        }
        return url;
    }

    /** Test method.
     * @param args Cmd line.
     */
    public static void main(String[] args) {
        Vector testV = new Vector();

        testV.addElement("_1country");
        testV.addElement("_0country");
        testV.addElement("_2country");
        SortVector sorter = new SortVector();

        sorter.sort(testV, SortVector.SORT_DESCENDING);
        for (int i = 0; i < testV.size(); i++) {// System.out.println(testV.get(i));
        }
    }
}
