package ro.finsiel.eunis.dataimport.legal;

/**
 * Created by miahi on 9/3/2015.
 */
public abstract class ExcelRow {

    /**
     * Cleans the row data, trimming the strings and removing unnecessary spaces
     */
    public abstract void cleanup();

    /**
     * Cleans the string, removing more than one space and trimming
     * @param s String to clean
     * @return Cleaned string
     */
    protected String cleanString(String s){
        if(s == null) return null;
        return s.replaceAll("\\s+", " ").trim();
    }
}
