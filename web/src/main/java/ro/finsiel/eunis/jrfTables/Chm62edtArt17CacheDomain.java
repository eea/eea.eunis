package ro.finsiel.eunis.jrfTables;

import net.sf.jrf.column.columnspecs.CompoundPrimaryKeyColumnSpec;
import net.sf.jrf.column.columnspecs.IntegerColumnSpec;
import net.sf.jrf.column.columnspecs.StringColumnSpec;
import net.sf.jrf.domain.AbstractDomain;
import net.sf.jrf.domain.PersistentObject;

public class Chm62edtArt17CacheDomain  extends AbstractDomain {
    /**
     * Implements newPersistentObject from AbstractDomain.
     * @return New persistent object (table row).
     */
    public PersistentObject newPersistentObject() {
        return new Chm62edtArt17CachePersist();
    }

    /**
     * Implements setup from AbstractDomain.
     */
    public void setup() {
        this.setTableName("chm62edt_art17cache");
        this.addColumnSpec(
                new CompoundPrimaryKeyColumnSpec(
                    new StringColumnSpec("code2000", "getCode2000", "setCode2000", DEFAULT_TO_EMPTY_STRING),
                    new StringColumnSpec("object_type", "getObjectType", "setObjectType", DEFAULT_TO_EMPTY_STRING),
                    new StringColumnSpec("CODE", "getCode", "setCode", DEFAULT_TO_EMPTY_STRING),
                    new StringColumnSpec("assessment", "getAssessment","setAssessment", DEFAULT_TO_EMPTY_STRING),
                    new StringColumnSpec("region", "getRegion","setRegion", DEFAULT_TO_EMPTY_STRING)
                )
        );

    }

}
