package ro.finsiel.eunis;

import eionet.eunis.dto.ForeignDataQueryDTO;
import eionet.eunis.rdf.LinkedData;
import eionet.sparqlClient.helpers.ResultValue;
import org.apache.commons.lang.StringUtils;
import ro.finsiel.eunis.utilities.TheOneConnectionPool;

import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Servlet to initialize the settings (on load)
 */
public class InitServlet  extends HttpServlet {
    public void init() {
        Settings.loadSettings(this);
        refreshArt17();
    }

    /**
     * Refreshes the Article 17 local cache for species and habitats
     */
    private void refreshArt17() {

        List<ForeignDataQueryDTO> biogeoAssessment;

        try {

            Properties props = new Properties();
            props.loadFromXML(getClass().getClassLoader().getResourceAsStream("art17.xml"));
            LinkedData ld = new LinkedData(props, null, "force");
            biogeoAssessment = ld.getQueryObjects();

            for (ForeignDataQueryDTO aBiogeoAssessment : biogeoAssessment) {
                if(aBiogeoAssessment.getId().equals("art17species_full_eu") || aBiogeoAssessment.getId().equals("art17habitats_full_eu")){
                    String syntaxaQuery = aBiogeoAssessment.getId();
                    if (!StringUtils.isBlank(syntaxaQuery)) {
                        ld.executeQuery(syntaxaQuery, null);
                        saveArt17((aBiogeoAssessment.getId().equals("art17species_full_eu")?"S":"H"), ld.getRows());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Persists the Article 17 data
     * @param objectType Object type (S = species, H = habitat)
     * @param results List of assessments
     */
    private void saveArt17(String objectType, List<HashMap<String, ResultValue>> results) {
        try {
            Connection c = TheOneConnectionPool.getConnection();

            // cleanup
            PreparedStatement deletePs = c.prepareStatement("delete from chm62edt_art17cache where object_type=?");
            deletePs.setString(1, objectType);
            deletePs.executeUpdate();
            deletePs.close();

            // save
            PreparedStatement ps = c.prepareStatement("insert into chm62edt_art17cache(code2000, region, code, assessment, object_type) values(?, ?, ?, ?, ?)");
            for(HashMap<String, ResultValue> hm : results) {
                try {
                    if(hm.get("species_code") != null) {
                        ps.setString(1, hm.get("species_code").getValue());
                    } else {
                        ps.setString(1, hm.get("habitat_code").getValue());
                    }
                    ps.setString(2, hm.get("region").getValue());
                    ps.setString(3, hm.get("code").getValue());
                    ps.setString(4, hm.get("assessment").getValue());
                    ps.setString(5, objectType);
                    ps.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ps.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
