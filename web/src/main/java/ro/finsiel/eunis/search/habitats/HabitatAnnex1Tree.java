/*
 * HabitatAnnex1Tree.java
 *
 * Created on August 26, 2002, 5:46 PM
 */

package ro.finsiel.eunis.search.habitats;


/**
 *
 * @author  root
 */

import java.util.*;

import eionet.eunis.util.Constants;
import ro.finsiel.eunis.jrfTables.*;
import ro.finsiel.eunis.search.Utilities;


/**
 * Class used in habitat-annex1-browser.
 * @author finsiel
 */
public class HabitatAnnex1Tree {

    private int nodeID;
    private int habID;

    /**
     * Javascript tree.
     */
    public StringBuffer tree;

    /**
     * Maximum level.
     */
    public int maxlevel;

    /**
     * Level.
     */
    public String level1;
    List coduriL1;

    /**
     * Creates a new instance of HabitatAnnex1Tree.
     */
    public HabitatAnnex1Tree() {
        coduriL1 = new Chm62edtHabitatDomain().findWhereOrderBy("ID_HABITAT>" + Constants.HABITAT_ANNEX1_ROOT + " AND LEVEL=1", "CODE_2000");
    }

    /**
     * Iterator for list generated by SELECT * FROM chm62edt_habitat WHERE HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "'  AND LEVEL=1 ORDER BY CODE_2000.
     * @return List of Chm62edtHabitatPersist objects.
     */

    public Iterator getIterator() {
        return coduriL1.iterator();
    }

    /** Generate 'arrayName' tree string beginning with first level.
     * @param arrayName name of tree string
     */

    public void getTree(String arrayName) {
        tree = new StringBuffer();
        nodeID = 0;
        level1 = getTree(arrayName, 1, 0, "");
    }

    /**
     * Generate 'arrayName' tree string beginning with level = 'level' and parent code = 'parentCode'.
     * @param arrayName name of tree string
     * @param level level
     * @param parentCode parent code
     */

    public void getTree(String arrayName, int level, String parentCode) {
        tree = new StringBuffer();
        nodeID = 0;
        level1 = getTree(arrayName, level, 0, parentCode);
    }

    /**
     * Generate 'arrayName' tree string beginning with level = 'level', parent node id = 'parentNodID'
     * and parent code = 'parentCode'.
     * @param arrayName name of tree string
     * @param level level
     * @param parentNodID parent node id
     * @param parentCode parent code
     * @return Tree (Javascript definition of the tree array).
     */

    public String getTree(String arrayName, int level, int parentNodID, String parentCode) {
        String r = null;
        StringBuffer localTree = new StringBuffer();
        StringBuffer childs = null;

        if (level < maxlevel) {
            List lHabitats = null;

            switch (level) {
            case 1: {
                    lHabitats = getListResults(1, parentCode, findLevel(parentCode));

                }
                break;

            case 2: {
                    lHabitats = getListResults(2, parentCode, findLevel(parentCode));
                }
                break;

            case 3: {
                    lHabitats = getListResults(3, parentCode, findLevel(parentCode));

                }
                break;

            default:
                lHabitats = new Vector();
            }
            Iterator it = lHabitats.iterator();

            if (it.hasNext()) {
                childs = new StringBuffer();
            }
            while (it.hasNext()) {
                Chm62edtHabitatPersist h = (Chm62edtHabitatPersist) it.next();

                localTree.append(arrayName);
                localTree.append("[");
                localTree.append(nodeID++); // elementId
                localTree.append("]=\"");
                localTree.append(nodeID); // nodeId
                childs.append(nodeID);
                if (it.hasNext()) {
                    childs.append(",");
                }
                localTree.append("|");
                localTree.append(parentNodID);
                localTree.append("|");
                String sn = h.getScientificName();

                while (sn.indexOf("\"") != -1) {
                    String aux = sn.substring(0, sn.indexOf("\"")) + "\\$";

                    sn = aux + sn.substring(sn.indexOf("\"") + 1);
                }
                localTree.append(h.getCode2000() + " - " + sn.replace('$', '\"'));
                localTree.append("|");

                List lParentHabitats = null;

                lParentHabitats = new Chm62edtHabitatDomain().findWhereOrderBy("CODE_2000 = '" + h.getCode2000() + "'", "CODE_2000");
                Iterator hit = lParentHabitats.iterator();

                if (hit.hasNext()) {
                    Chm62edtHabitatPersist hh = (Chm62edtHabitatPersist) hit.next();

                    habID = Utilities.checkedStringToInt(hh.getIdHabitat(), 0);
                } else {
                    habID = 0;
                }
                if (habID != 0) {
                    localTree.append("habitats-annex1-browser.jsp?habID=" + habID + "&openNode=" + nodeID);
                } else {
                    localTree.append("habitats-annex1-browser.jsp?habID=&openNode=" + nodeID);
                }
                // localTree.append("habitats-annex1-browser.jsp?habID=&openNode=" + nodeID);
                localTree.append("|");
                String nextlevel = this.getTree(arrayName, level + 1, nodeID, h.getCode2000());

                localTree.append(nextlevel != null);
                localTree.append("|");
                localTree.append(!it.hasNext());
                localTree.append("|");
                localTree.append(nextlevel);
                localTree.append("|");
                localTree.append(h.getHabLevel());
                localTree.append("\";\n");

            }
            tree.append(localTree);
        }
        if (childs != null) {
            r = childs.toString();
        }
        return r;
    }

    /**
     * Return the max level of three for a habitat(with CODE_2000 = codParent).
     * @param codParent habitat code2000
     * @return max level.
     */
    public int maxLevel(String codParent) {
        List lHabitats = null;

        lHabitats = new Chm62edtHabitatDomain().findWhere("HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "' AND CODE_2000 LIKE '" + codParent + "%'");
        int max = 0;

        if (lHabitats != null) {
            Iterator hit = lHabitats.iterator();

            while (hit.hasNext()) {
                Chm62edtHabitatPersist hh = (Chm62edtHabitatPersist) hit.next();

                if (max < hh.getHabLevel().intValue()) {
                    max = hh.getHabLevel().intValue();
                }
            }
        }
        return max;
    }

    /**
     * Return tree level for a habitat.
     * @param code habitat code2000
     * @return tree level.
     */
    public int findLevel(String code) {
        if (code != null) {
            List l = new Chm62edtHabitatDomain().findWhere("CODE_2000='" + code + "'");

            if (l != null && l.size() > 0) {
                return (((Chm62edtHabitatPersist) l.get(0)).getHabLevel() != null
                        ? ((Chm62edtHabitatPersist) l.get(0)).getHabLevel().intValue()
                        : -1);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Return children from level = 'level' for habitat with code2000 = 'codParent'.
     * @param level level
     * @param codParent habitat code2000
     * @param no characters number
     * @return list of children.
     */
    private List getListResults(int level, String codParent, int no) {
        Vector v = new Vector();
        boolean wasFind = false;
        int levelChild = level;

        while (wasFind == false && levelChild <= 3) {
            List l1 = new Chm62edtHabitatDomain().findWhere(
                    "HABITAT_TYPE='" + Constants.HABITAT_ANNEX1 + "' AND CODE_2000 <> '" + codParent + "' AND CODE_2000 LIKE '" + codParent.substring(0, no)
                    + "%' AND LEVEL=" + levelChild);

            if (l1 != null && l1.size() > 0) {
                wasFind = true;
                for (int i = 0; i < l1.size(); i++) {
                    Chm62edtHabitatPersist h = (Chm62edtHabitatPersist) l1.get(i);

                    v.addElement(h);
                }
            }
            levelChild++;
        }

        return v;
    }

}
