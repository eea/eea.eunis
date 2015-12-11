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
        r.setBirdsDRestrictions(getCellValue(row, "G"));
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

        r.setAcap(getCellValue(row, "R"));
        r.setAWarbler(getCellValue(row, "Y"));
        r.setGBustard(getCellValue(row, "Z"));

        r.setMSeal(getCellValue(row, "AA"));
        r.setMouRaptors(getCellValue(row, "AB"));
        r.setMouRaptorsName(getCellValue(row, "AC"));
        r.setSbCurlew(getCellValue(row, "AD"));
        r.setSharksMou(getCellValue(row, "AE"));

        r.setCites(getCellValue(row, "AG"));
        r.setCitesName(getCellValue(row, "AH"));
        r.setEuTrade(getCellValue(row, "AI"));
        r.setEuTradeName(getCellValue(row, "AJ"));
        r.setAewa(getCellValue(row, "T"));
        r.setAewaName(getCellValue(row, "U"));

        r.setEurobats(getCellValue(row, "W"));
        r.setEurobatsName(getCellValue(row, "X"));

        r.setAccobams(getCellValue(row, "S"));
        r.setAscobans(getCellValue(row, "V"));
        r.setWadden(getCellValue(row, "AF"));
        r.setSpa(getCellValue(row, "AK"));
        r.setSpaName(getCellValue(row, "AL"));
        r.setOspar(getCellValue(row, "AM"));
        r.setOsparName(getCellValue(row, "AN"));
        r.setHelcom(getCellValue(row, "AO"));
        r.setHelcomRestrictions(getCellValue(row, "AP"));
        r.setHelcomName(getCellValue(row, "AQ"));
        r.setRedList(getCellValue(row, "AR"));
        r.setRedListName(getCellValue(row, "AS"));

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
        r.setSpeciesValidName(getCellValue(row, "B"));
        r.setLegalText(getCellValue(row, "C"));
        r.setRestriction(getCellValue(row, "D"));


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
    private String getCellValue(Row row, String column) {
        Cell c = row.getCell(CellReference.convertColStringToIndex(column));
        if (c != null) {
            String s = null;
            try {
                s = c.getStringCellValue();
            } catch (IllegalStateException ise) {
                try {
                    int value = ((int) c.getNumericCellValue());
                    if(value != 0){
                        s = value + "";
                    }
                } catch (IllegalStateException ise2) {
                    System.out.println("ERROR: Illegal state on cell " + row.getRowNum() + ", " + column + " (" + ise2.getMessage() + ")");
                }
            }
            if (s == null) {
                s = "";
            }
            s = s.trim();
            return s;
        } else {
            return "";
        }
    }

}
