package ro.finsiel.eunis.dataimport.legal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

/**
 * Factory for Excel objects
 */
public class ExcelRowFactory {

    /**
     * Reads an Excel row as a Vertebrates row
     * @param row The Excel row
     * @return The populated SpeciesRow object
     */
    public SpeciesRow getVertebratesRow(Row row){
        if(row.getLastCellNum()<29){
            return null;
        }

        SpeciesRow r = new SpeciesRow();
        r.setSpeciesName(getCellValue(row, "A"));
        r.setHabitatsD(getCellValue(row, "B"));
        r.setHabitatsIIPriority(getCellValue(row, "C"));
        r.setHabitatsRestrictions(getCellValue(row, "D"));
        r.setHabitatsName(getCellValue(row, "E"));
        r.setBirdsD(getCellValue(row, "F"));
        r.setBirdsName(getCellValue(row, "G"));
        r.setBernConvention(getCellValue(row, "H"));
        r.setBernRestrictions(getCellValue(row, "I"));
        r.setBernName(getCellValue(row, "J"));
        r.setEmeraldR6(getCellValue(row, "K"));
        r.setEmeraldRestrictions(getCellValue(row, "L"));
        r.setEmeraldName(getCellValue(row, "M"));
        r.setBonnConvention(getCellValue(row, "N"));
        r.setBonnRestrictions(getCellValue(row, "O"));
        r.setBonnName(getCellValue(row, "P"));
        r.setCites(getCellValue(row, "Q"));
        r.setCitesName(getCellValue(row, "R"));
        r.setEuTrade(getCellValue(row, "S"));
        r.setEuTradeName(getCellValue(row, "T"));
        r.setAewa(getCellValue(row, "U"));
        r.setAewaName(getCellValue(row, "V"));
        r.setEurobats(getCellValue(row, "W"));
        r.setAccobams(getCellValue(row, "X"));
        r.setAscobans(getCellValue(row, "Y"));
        r.setWadden(getCellValue(row, "Z"));
        r.setSpa(getCellValue(row, "AA"));
        r.setSpaName(getCellValue(row, "AB"));
        r.setOspar(getCellValue(row, "AC"));
        r.setOsparName(getCellValue(row, "AD"));
        r.setHelcom(getCellValue(row, "AE"));
        r.setRedList(getCellValue(row, "AF"));
        r.setRedListName(getCellValue(row, "AG"));

        r.setExcelRow(row.getRowNum() + 1);

        return r;
    }

    /**
     * Reads an Excel row as an Invertebrates row
     * @param row The Excel row
     * @return The populated SpeciesRow object
     */
    public SpeciesRow getInvertebratesRow(Row row){
        if(row.getLastCellNum()<15){
            return null;
        }

        SpeciesRow r = new SpeciesRow();
        r.setSpeciesName(getCellValue(row, "A"));
        r.setHabitatsD(getCellValue(row, "B"));
        r.setHabitatsName(getCellValue(row, "C"));
        r.setBernConvention(getCellValue(row, "D"));
        r.setBernRestrictions(getCellValue(row, "E"));
        r.setBernName(getCellValue(row, "F"));
        r.setEmeraldR6(getCellValue(row, "G"));
        r.setEmeraldName(getCellValue(row, "H"));
        r.setCites(getCellValue(row, "I"));
        r.setEuTrade(getCellValue(row, "J"));
        r.setSpa(getCellValue(row, "K"));
        r.setSpaName(getCellValue(row, "L"));
        r.setOspar(getCellValue(row, "M"));
        r.setHelcom(getCellValue(row, "N"));
        r.setRedList(getCellValue(row, "O"));
        r.setRedListName(getCellValue(row, "P"));

        r.setExcelRow(row.getRowNum() + 1);

        return r;
    }

    /**
     * Reads an Excel row as a Restrictions table row
     * @param row
     * @return
     */
    public RestrictionsRow getRestrictionsRow(Row row){
        if(row.getLastCellNum()<3){
            return null;
        }
        RestrictionsRow r = new RestrictionsRow();
        r.setSpecies(getCellValue(row, "A"));
        r.setLegalText(getCellValue(row, "B"));
        r.setRestriction(getCellValue(row, "C"));

        if(r.getLegalText().isEmpty() || r.getLegalText().contains("Legal text"))
            return null;

        return r;
    }

    /**
     * Returns the trimmed, not-null value of a column given by its letter
     * @param row The row to read from
     * @param column The column (by its letter, like "B")
     * @return The String value, trimmed. Null values are returned as empty String.
     */
    private String getCellValue(Row row, String column){
        Cell c = row.getCell(CellReference.convertColStringToIndex(column));
        if(c!=null){
            String s = c.getStringCellValue();
            if(s == null) {
                s = "";
            }
            s = s.trim();
            return s;
        } else {
            return "";
        }
    }

}
