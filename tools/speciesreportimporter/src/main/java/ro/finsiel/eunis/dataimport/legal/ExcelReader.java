package ro.finsiel.eunis.dataimport.legal;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.finsiel.eunis.search.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Reads an Excel file and parses the content into objects
 */
public class ExcelReader {

    private List<SpeciesRow> speciesRows;
    private List<RestrictionsRow> restrictionsRows;

    /**
     * The file type, identified by the column A1
     */
    public static enum FileType { VERTEBRATES, UNKNOWN, INVERTEBRATES, PLANTS }

    private FileType fileType = FileType.UNKNOWN ;

    /**
     * Reads an Excel file
     * @param filename The file name (and path)
     * @throws IOException
     */
    public ExcelReader(String filename) throws IOException {

        speciesRows = new ArrayList<SpeciesRow>();
        restrictionsRows = new ArrayList<RestrictionsRow>();

        FileInputStream file = new FileInputStream(new File(filename));

        XSSFWorkbook workbook = new XSSFWorkbook (file);

        // Species sheet
        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        ExcelRowFactory erf = new ExcelRowFactory();

        String type = sheet.getRow(0).getCell(0).getStringCellValue();
        if(type.equalsIgnoreCase("VERTEBRATES")){
            fileType = FileType.VERTEBRATES;
        } else if(type.equalsIgnoreCase("INVERTEBRATES")){
            fileType = FileType.INVERTEBRATES;
        } else if(type.equalsIgnoreCase("PLANTS")){
            fileType = FileType.PLANTS;
        } else {
            System.out.println("Unknown file type '" + type + "'! Please fill the cell A1 with 'Vertebrates' or 'Invertebrates'");
            return;
        }

        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            SpeciesRow sr;

            if(fileType == FileType.VERTEBRATES){
                sr = erf.getVertebratesRow(row);
            } else if(fileType == FileType.INVERTEBRATES) {
                sr = erf.getInvertebratesRow(row);
            } else if(fileType == FileType.PLANTS){
                sr = erf.getPlantsRow(row);
            } else {
                sr = null;
            }

            if(sr != null && sr.isSpecies()) {
//                System.out.println(sr);
                sr.cleanup();
                speciesRows.add(sr);
            }
        }

        // Geographic restrictions sheet

        if(fileType == FileType.VERTEBRATES){
            sheet = workbook.getSheetAt(2);
        } else if(fileType == FileType.INVERTEBRATES) {
            sheet = workbook.getSheetAt(2);
        } else {
            sheet = null;
        }

        if(sheet != null) {

            rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                RestrictionsRow restrictionsRow = erf.getRestrictionsRow(row);

                if (restrictionsRow != null) {
//                System.out.println(restrictionsRow);
                    restrictionsRow.cleanup();
                    restrictionsRows.add(restrictionsRow);
                }
            }

            populateRestrictions();
        }


        file.close();
    }

    /**
     * Populate the restriction maps of the species with the data read from the Geographic and other Restrictions tab
     * Also populates the priority data (Habitats "Priority in Annex II")
     * The used Restrictions are marked so we can identify the unused ones at the end
     */
    private void populateRestrictions(){


        for(SpeciesRow sr : speciesRows){
            for(RestrictionsRow r : restrictionsRows){
                if(sr.getSpeciesName().equalsIgnoreCase(r.getSpecies())){
                    sr.getRestrictionsMap().put(r.getLegalText(), r);
                    r.setUsed(true);
                } else if ((! Utilities.isEmptyString(r.getSpeciesValidName())) && sr.getSpeciesName().equalsIgnoreCase(r.getSpeciesValidName())){
                    sr.getRestrictionsMap().put(r.getLegalText(), r);
                    r.setRestriction(r.getRestriction() + " (the synonym name [" + r.getSpecies() + "] appears in the document)");
                    r.setUsed(true);
                }
            }

            // "priority in II" for habitats
            if(!sr.getHabitatsIIPriority().isEmpty()){
                RestrictionsRow rr = sr.getRestrictionsMap().get("HD II");
                if(rr == null) {
                    rr = new RestrictionsRow();
                    sr.getRestrictionsMap().put("HD II", rr);
                }
                rr.setPriority(1);
            }
        }

        // Log the unused restrictions
        for(RestrictionsRow r : restrictionsRows){
            if(!r.isUsed()){
                System.out.println("WARNING: Species not found for restriction " + r.getSpecies() + " (valid name " + r.getSpeciesValidName() + ")" + " / " + r.getLegalText());
            }
        }

    }

    /**
     * Returns the list of species
     * @return
     */
    public List<SpeciesRow> getSpeciesRows() {
        return speciesRows;
    }

    /**
     * Returns the list of restrictions
     * @return
     */
    public List<RestrictionsRow> getRestrictionsRows() {
        return restrictionsRows;
    }

    public FileType getFileType() {
        return fileType;
    }
}
