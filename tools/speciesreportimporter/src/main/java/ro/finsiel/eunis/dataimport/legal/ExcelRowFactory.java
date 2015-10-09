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
// todo restriction
        r.setBirdsName(getCellValue(row, "H"));
        r.setBernConvention(getCellValue(row, "I"));
        r.setBernRestrictions(getCellValue(row, "J"));
        r.setBernName(getCellValue(row, "K"));
        r.setEmeraldR6(getCellValue(row, "L"));
        r.setEmeraldRestrictions(getCellValue(row, "M"));
        r.setEmeraldName(getCellValue(row, "N"));
        r.setBonnConvention(getCellValue(row, "O"));
        r.setBonnRestrictions(getCellValue(row, "P"));
        r.setBonnName(getCellValue(row, "Q"));
        r.setCites(getCellValue(row, "R"));
        r.setCitesName(getCellValue(row, "S"));
        r.setEuTrade(getCellValue(row, "T"));
        r.setEuTradeName(getCellValue(row, "U"));
        r.setAewa(getCellValue(row, "V"));
        r.setAewaName(getCellValue(row, "W"));

        //ACAP  X
        //raptors Y
        //anme in raptors Z

        r.setEurobats(getCellValue(row, "AA"));
        r.setAccobams(getCellValue(row, "AB"));
        r.setAscobans(getCellValue(row, "AC"));
        r.setWadden(getCellValue(row, "AD"));
        r.setSpa(getCellValue(row, "AE"));
        r.setSpaName(getCellValue(row, "AF"));
        r.setOspar(getCellValue(row, "AG"));
        r.setOsparName(getCellValue(row, "AH"));
        r.setHelcom(getCellValue(row, "AI"));
        r.setRedList(getCellValue(row, "AJ"));
        r.setRedListName(getCellValue(row, "AK"));

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
     * Reads Plants row (https://taskman.eionet.europa.eu/issues/22176)
     * @param row
     * @return
     */
    public SpeciesRow getPlantsRow(Row row) {
        if(row.getLastCellNum()<15){
            return null;
        }

        SpeciesRow r = new SpeciesRow();
        r.setSpeciesName(getCellValue(row, "A"));
        r.setHabitatsD(getCellValue(row, "K"));
        r.setHabitatsName(getCellValue(row, "L"));
        r.setBernConvention(getCellValue(row, "M"));
//        r.setBernRestrictions(getCellValue(row, "E"));
        r.setBernName(getCellValue(row, "N"));
        r.setEmeraldR6(getCellValue(row, "O"));
        r.setEmeraldName(getCellValue(row, "P"));
        r.setCites(getCellValue(row, "Q"));
        // todo: name in CITES
        r.setEuTrade(getCellValue(row, "S"));
        // todo: name in EU Trade
        r.setSpa(getCellValue(row, "U")); // Barcelona
        r.setSpaName(getCellValue(row, "V"));
//        r.setOspar(getCellValue(row, "M"));
        r.setHelcom(getCellValue(row, "N"));

        r.setRedList(getCellValue(row, "B"));
//        r.setRedListName(getCellValue(row, "P"));

        r.setRedListEU27(getCellValue(row, "D"));
        r.setHelcom("");

        // todo: HELCOM?

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
        r.setLegalText(getCellValue(row, "D"));
        r.setRestriction(getCellValue(row, "C"));
        r.setSpeciesValidName(getCellValue(row, "B"));

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
            String s = null;
            try{
                s = c.getStringCellValue();
            } catch (IllegalStateException ise){
             ise.printStackTrace();
               System.out.println("ERROR: Illegal stat on cell " + row.getRowNum() + ", " + column);
            }
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
