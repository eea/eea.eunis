package ro.finsiel.eunis.dataimport.legal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Writes the import result to the Excel file (to have a better view of the import problems)
 * Created by miahi on 9/17/2015.
 */
public class ExcelWriter {

    private String filename;
    private ExcelReader.FileType fileType;
    private List<SpeciesRow> speciesRows;

    /**
     * Create the writer
     * @param filename The input file; the output file will have "_result.xlsx" appended to the filename
     * @param type The detected file type
     * @param speciesRows The list of rows, as it was read by the ExcelReader; the list must be ordered by row number
     */
    public ExcelWriter(String filename, ExcelReader.FileType type, List<SpeciesRow> speciesRows) {
        this.filename = filename;
        this.fileType = type;
        this.speciesRows = speciesRows;
    }

    /**
     * Write the problems to file
     * @throws IOException
     */
    public void writeToFile() throws IOException {

        FileInputStream file = new FileInputStream(new File(filename));
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        // Species sheet
        XSSFSheet sheet = workbook.getSheetAt(0);

        String column = "ZZ";
        if (fileType == ExcelReader.FileType.VERTEBRATES) {
            column = "AL";
        } else if (fileType == ExcelReader.FileType.PLANTS) {
            column = "AH";
        }

        int col = CellReference.convertColStringToIndex(column);

        CellStyle yellowBackground = workbook.createCellStyle();
        yellowBackground.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowBackground.setFillPattern(CellStyle.SOLID_FOREGROUND);

        if (sheet != null) {

            Iterator<Row> rowIterator = sheet.iterator();
            int index = 0;  // list position

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    SpeciesRow sr = speciesRows.get(index);
                    if (sr.getExcelRow() == row.getRowNum()+1) {
                        if(sr.getResult() != null && sr.getResult().length()>1) {
                            Cell c = row.getCell(col);
                            if(c==null){
                                c = row.createCell(col);
                            }
                            c.setCellValue(sr.getResult());
                            c.setCellStyle(yellowBackground);
                        }
                        index++;
                        if(index>=speciesRows.size()) break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        file.close();

        // the actual write
        FileOutputStream fileOut = new FileOutputStream(filename + "_result.xlsx");
        workbook.write(fileOut);
        fileOut.close();
    }
}
