package ro.finsiel.eunis.dataimport.legal;

import org.apache.commons.lang.WordUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Extract the Species data from IUCN
 */
public class SpeciesImport {

    public static void main(String args[]) {
        SpeciesImport si = new SpeciesImport();

        try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            for(String line; (line = br.readLine()) != null; ) {
                try {
                    Species s = si.readSpecies(new URL(line));
                    if(s != null) {
                        System.out.println("# " + line);
                        System.out.println(s.toPythonScript());
                    }
                } catch (Exception e) {
                    System.out.println("# " + line);
//                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Species readSpecies(URL url) {

        // http://www.iucnredlist.org/details/13186352/0

        InputStream is = null;
        BufferedReader br;
        String line;
        Species result = null;

        try {
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder pageBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                pageBuilder.append(line);
            }
            String page = pageBuilder.toString();

            int famLocation = page.indexOf("<th>Family</th>");
            // get the fifth td after this

            for (int i = 0; i < 5; i++) {
                famLocation = page.indexOf("<td>", famLocation + 1);
            }

            String family = page.substring(famLocation + 4, page.indexOf("</td>", famLocation));

            int sciNameLocation = page.indexOf("<span class=\"sciname\">");
            // just after
            int lastSciNameLocation = page.indexOf("</span>", sciNameLocation);
            String sciName = page.substring(sciNameLocation + 22, lastSciNameLocation);

            int authLocation = page.indexOf("<td class=\"label\"><strong>Species Authority:</strong></td>");
            // next TD
            authLocation = page.indexOf("<td>", authLocation);

            String authority = page.substring(authLocation + 4, page.indexOf("</td>", authLocation));

            result = new Species(sciName, family, authority);
        } catch (MalformedURLException mue) {
//            mue.printStackTrace();
        } catch (IOException ioe) {
//            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }

        return result;
    }

    private class Species {
        private String scientificName;
        private String family;
        private String authority;

        private Species(String scientificName, String family, String authority) {
            this.scientificName = scientificName.trim();
            this.family = family.trim();
            this.authority = authority.trim().replace("&amp;", "&");
        }

        public String getScientificName() {
            return scientificName;
        }

        public void setScientificName(String scientificName) {
            this.scientificName = scientificName;
        }

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }

        public boolean isSubspecies(){
            int firstSpace = scientificName.indexOf(" ");
            int secondSpace = scientificName.indexOf(" ", firstSpace + 1);
            return secondSpace != -1;
        }

        @Override
        public String toString() {
            return "Species{" +
                    "scientificName='" + scientificName + '\'' +
                    ", authority='" + authority + '\'' +
                    ", family='" + family + '\'' +
                    '}';
        }
                   /*
                   ./addspecies.py -a "Blanford, 1874" -f Lacertidae -r Species "Timon princeps"
./addspecies.py -a "Lac & Kluch, 1968" -f Lacertidae -r Subspecies "Zootoca vivipara pannonica"
                    */
        public String toPythonScript(){
            StringBuilder result = new StringBuilder();
            result.append("./addspecies.py -a \"");
            result.append(authority);
            result.append("\" -f ");
            result.append(WordUtils.capitalizeFully(family));
            result.append(" -r ");
            if(isSubspecies()){
                result.append("Subspecies");
            } else {
                result.append("Species");
            }
            result.append(" \"");
            result.append(scientificName);
            result.append("\"");
            return result.toString();
        }
    }
}
