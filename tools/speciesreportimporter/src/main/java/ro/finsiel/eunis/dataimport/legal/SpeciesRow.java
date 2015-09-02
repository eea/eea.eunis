package ro.finsiel.eunis.dataimport.legal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean to keep the Species read from an Excel row
 */
public class SpeciesRow {
    private String speciesName;

    // single values, read from the Excel columns

    private String habitatsD = "";
    private String habitatsIIPriority = "";
    private String habitatsRestrictions = "";
    private String habitatsName = "";

    private String birdsD = "";
    private String birdsName = "";

    private String bernConvention = "";
    private String bernRestrictions = "";
    private String bernName = "";

    private String emeraldR6 = "";
    private String emeraldRestrictions = "";
    private String emeraldName = "";

    private String bonnConvention = "";
    private String bonnRestrictions = "";
    private String bonnName = "";

    private String cites = "";
    private String euTrade = "";
    private String aewa = "";
    private String eurobats = "";
    private String accobams = "";
    private String ascobans = "";
    private String wadden = "";
    private String spa = "";
    private String spaName = "";
    private String ospar = "";
    private String helcom = "";
    private String redList = "";
    private String redListName = "";

    // identification data, read from the database
    private String idSpecies;
    private String idNatureObject;

    // lists of annexes, where they can be more (populated when the columns are set)
    private String[] habitatsDAnnex = new String[0];
    private String[] birdsDAnnex = new String[0];
    private String[] bernConventionAnnex = new String[0];
    private String[] bonnConventionAnnex = new String[0];
    private String[] citesAnnex = new String[0];
    private String[] euTradeAnnex = new String[0];
    private String[] spaAnnex = new String[0];

    // restrictions mapped by legal document
    private Map<String, RestrictionsRow> restrictionsMap = new HashMap<String, RestrictionsRow>();

    // the Excel row number, for logging/debugging purpose
    private int excelRow;

    // flag if it is a synonym
    private String databaseName;
    // the group ID, to identify Birds (id group = 5)
    private String idGroup;
    private String citesName = "";
    private String euTradeName = "";
    private String aewaName = "";
    private String osparName = "";

    public Map<String, RestrictionsRow> getRestrictionsMap() {
        return restrictionsMap;
    }

    public String[] getSpaAnnex() {
        return spaAnnex;
    }

    public String[] getEuTradeAnnex() {
        return euTradeAnnex;
    }

    public String[] getCitesAnnex() {
        return citesAnnex;
    }

    public String[] getBonnConventionAnnex() {
        return bonnConventionAnnex;
    }

    public String[] getBernConventionAnnex() {
        return bernConventionAnnex;
    }

    /**
     * Checks that the row is actually a species or something else using the species name
     * A species name should have at least two names and not start with "Order", "Phylum", "Phyllum", "Class"
     * @return
     */
    public boolean isSpecies(){
        return speciesName != null
            && speciesName.contains(" ")
           // && !(speciesName.contains("("))  // https://taskman.eionet.europa.eu/issues/26690#note-74
            && !speciesName.startsWith("Order ")
            && !speciesName.startsWith("Phyllum ")
            && !speciesName.startsWith("Phylum ")
            && !speciesName.startsWith("Class ");
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getHabitatsD() {
        return habitatsD;
    }

    public void setHabitatsD(String habitatsD) {
        this.habitatsD = habitatsD;
        habitatsDAnnex = explode(habitatsD, ",");
    }

    public String[] getHabitatsDAnnex() {
        return habitatsDAnnex;
    }

    public String getHabitatsIIPriority() {
        return habitatsIIPriority;
    }

    public void setHabitatsIIPriority(String habitatsIIPriority) {
        this.habitatsIIPriority = habitatsIIPriority;
    }

    public String getHabitatsRestrictions() {
        return habitatsRestrictions;
    }

    public void setHabitatsRestrictions(String habitatsRestrictions) {
        this.habitatsRestrictions = habitatsRestrictions;
    }

    public String getHabitatsName() {
        return habitatsName;
    }

    public void setHabitatsName(String habitatsName) {
        this.habitatsName = habitatsName;
    }

    public String getBirdsD() {
        return birdsD;
    }

    public void setBirdsD(String birdsD) {
        this.birdsD = birdsD;
        birdsDAnnex = explode(birdsD,",");
    }

    public String[] getBirdsDAnnex() {
        return birdsDAnnex;
    }

    public String getBirdsName() {
        return birdsName;
    }

    public void setBirdsName(String birdsName) {
        this.birdsName = birdsName;
    }

    public String getBernConvention() {
        return bernConvention;
    }

    public void setBernConvention(String bernConvention) {
        this.bernConvention = bernConvention;
        bernConventionAnnex = explode(bernConvention, ",");
    }

    public String getBernRestrictions() {
        return bernRestrictions;
    }

    public void setBernRestrictions(String bernRestrictions) {
        this.bernRestrictions = bernRestrictions;
    }

    public String getBernName() {
        return bernName;
    }

    public void setBernName(String bernName) {
        this.bernName = bernName;
    }

    public String getEmeraldR6() {
        return emeraldR6;
    }

    public void setEmeraldR6(String emeraldR6) {
        this.emeraldR6 = emeraldR6;
    }

    public String getEmeraldRestrictions() {
        return emeraldRestrictions;
    }

    public void setEmeraldRestrictions(String emeraldRestrictions) {
        this.emeraldRestrictions = emeraldRestrictions;
    }

    public String getEmeraldName() {
        return emeraldName;
    }

    public void setEmeraldName(String emeraldName) {
        this.emeraldName = emeraldName;
    }

    public String getBonnConvention() {
        return bonnConvention;
    }

    public void setBonnConvention(String bonnConvention) {
        this.bonnConvention = bonnConvention;
        bonnConventionAnnex = explode(bonnConvention, ",");
    }

    public String getBonnRestrictions() {
        return bonnRestrictions;
    }

    public void setBonnRestrictions(String bonnRestrictions) {
        this.bonnRestrictions = bonnRestrictions;
    }

    public String getBonnName() {
        return bonnName;
    }

    public void setBonnName(String bonnName) {
        this.bonnName = bonnName;
    }

    public String getCites() {
        return cites;
    }

    public void setCites(String cites) {
        this.cites = cites;
        citesAnnex = explode(cites, ",");
    }

    public String getEuTrade() {
        return euTrade;
    }

    public void setEuTrade(String euTrade) {
        this.euTrade = euTrade;
        euTradeAnnex = explode(euTrade, ",");
    }

    public String getAewa() {
        return aewa;
    }

    public void setAewa(String aewa) {
        this.aewa = aewa;
    }

    public String getEurobats() {
        return eurobats;
    }

    public void setEurobats(String eurobats) {
        this.eurobats = eurobats;
    }

    public String getAccobams() {
        return accobams;
    }

    public void setAccobams(String accobams) {
        this.accobams = accobams;
    }

    public String getAscobans() {
        return ascobans;
    }

    public void setAscobans(String ascobans) {
        this.ascobans = ascobans;
    }

    public String getWadden() {
        return wadden;
    }

    public void setWadden(String wadden) {
        this.wadden = wadden;
    }

    public String getSpa() {
        return spa;
    }

    public void setSpa(String spa) {
        this.spa = spa;
        spaAnnex = explode(spa, ",");
    }

    public String getOspar() {
        return ospar;
    }

    public void setOspar(String ospar) {
        this.ospar = ospar;
    }

    public String getHelcom() {
        return helcom;
    }

    public void setHelcom(String helcom) {
        this.helcom = helcom;
    }

    public String getRedList() {
        return redList;
    }

    public void setRedList(String redList) {
        this.redList = redList;
    }

    public String getRedListName() {
        return redListName;
    }

    public void setRedListName(String redListName) {
        this.redListName = redListName;
    }

    public String getIdNatureObject() {
        return idNatureObject;
    }

    public void setIdNatureObject(String idNatureObject) {
        this.idNatureObject = idNatureObject;
    }

    public String getIdSpecies() {
        return idSpecies;
    }

    public void setIdSpecies(String idSpecies) {
        this.idSpecies = idSpecies;
    }

    public int getExcelRow() {
        return excelRow;
    }

    public void setExcelRow(int excelRow) {
        this.excelRow = excelRow;
    }

    public String getSpaName() {
        return spaName;
    }

    public void setSpaName(String spaName) {
        this.spaName = spaName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getIdGroup() {
        return idGroup;
    }

    /**
     * Cleans the row data, trimming the strings and removing unnecessary spaces
     */
    public void cleanup(){
        speciesName = cleanString(speciesName);
    }

    private String cleanString(String s){
        return s.replaceAll("\\s", " ").trim();
    }

    /**
     * Splits the list by the given separator, trimming the result. If the parameter is empty returns an empty array.
     * @param list The string to be split
     * @param separator The separator used for splitting
     * @return An array of the split values, trimmed.
     */
    private String[] explode(String list, String separator){
        if(list.isEmpty()){
            return new String[0];
        } else {
            String[] exploded = list.split(separator);

            for(int i=0;i<exploded.length;i++){
                exploded[i] = exploded[i].trim();
            }
            return exploded;
        }
    }

    @Override
    public String toString() {
        return "SpeciesRow{" +
                "speciesName='" + speciesName + '\'' +
                ", habitatsD='" + habitatsD + '\'' +
                ", habitatsIIPriority='" + habitatsIIPriority + '\'' +
                ", habitatsRestrictions='" + habitatsRestrictions + '\'' +
                ", habitatsName='" + habitatsName + '\'' +
                ", birdsD='" + birdsD + '\'' +
                ", birdsName='" + birdsName + '\'' +
                ", bernConvention='" + bernConvention + '\'' +
                ", bernRestrictions='" + bernRestrictions + '\'' +
                ", bernName='" + bernName + '\'' +
                ", emeraldR6='" + emeraldR6 + '\'' +
                ", emeraldRestrictions='" + emeraldRestrictions + '\'' +
                ", emeraldName='" + emeraldName + '\'' +
                ", bonnConvention='" + bonnConvention + '\'' +
                ", bonnRestrictions='" + bonnRestrictions + '\'' +
                ", bonnName='" + bonnName + '\'' +
                ", cites='" + cites + '\'' +
                ", euTrade='" + euTrade + '\'' +
                ", aewa='" + aewa + '\'' +
                ", eurobats='" + eurobats + '\'' +
                ", accobams='" + accobams + '\'' +
                ", ascobans='" + ascobans + '\'' +
                ", wadden='" + wadden + '\'' +
                ", spa='" + spa + '\'' +
                ", ospar='" + ospar + '\'' +
                ", helcom='" + helcom + '\'' +
                ", redList='" + redList + '\'' +
                ", redListName='" + redListName + '\'' +
                ", habitatsDAnnex=" + (habitatsDAnnex == null ? null : Arrays.asList(habitatsDAnnex)) +
                ", birdsDAnnex=" + (birdsDAnnex == null ? null : Arrays.asList(birdsDAnnex)) +
                ", bernConventionAnnex=" + (bernConventionAnnex == null ? null : Arrays.asList(bernConventionAnnex)) +
                ", bonnConventionAnnex=" + (bonnConventionAnnex == null ? null : Arrays.asList(bonnConventionAnnex)) +
                ", citesAnnex=" + (citesAnnex == null ? null : Arrays.asList(citesAnnex)) +
                ", euTradeAnnex=" + (euTradeAnnex == null ? null : Arrays.asList(euTradeAnnex)) +
                '}';
    }

    public void setCitesName(String citesName) {
        this.citesName = citesName;
    }

    public String getCitesName() {
        return citesName;
    }

    public void setEuTradeName(String euTradeName) {
        this.euTradeName = euTradeName;
    }

    public String getEuTradeName() {
        return euTradeName;
    }

    public void setAewaName(String aewaName) {
        this.aewaName = aewaName;
    }

    public String getAewaName() {
        return aewaName;
    }

    public void setOsparName(String osparName) {
        this.osparName = osparName;
    }

    public String getOsparName() {
        return osparName;
    }
}
