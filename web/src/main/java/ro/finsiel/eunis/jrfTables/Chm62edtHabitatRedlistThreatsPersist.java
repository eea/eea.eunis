package ro.finsiel.eunis.jrfTables;

import net.sf.jrf.domain.PersistentObject;

public class Chm62edtHabitatRedlistThreatsPersist extends PersistentObject  {
    /**
     * create table chm62edt_threats
     * (
     * ID_THREAT int          default 0      not null
     * primary key,
     * THREAT_CODE_1        varchar(10)        null,
     * MAIN_THREAT          varchar(2500)       not null, -> code B, C, etc.
     * DESCRIPTION          varchar(2500)       not null,
     * ORDER                int(16)            not null
     * )
     * <p>
     * create table chm62edt_habitat_redlist_threats
     * (
     * ID_HABITAT int(16)                    not null,
     * ID_THREAT int                         not null
     * )
     */

    private Integer idThreat;
    private Integer idHabitat;
    private String mainThreat;
    private String description;
    private String order;

    public String getMainThreat() {
        return mainThreat;
    }

    public void setMainThreat(String mainThreat) {
        this.mainThreat = mainThreat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(Integer idHabitat) {
        this.idHabitat = idHabitat;
    }

    public Integer getIdThreat() {
        return idThreat;
    }

    public void setIdThreat(Integer idThreat) {
        this.idThreat = idThreat;
    }
}
