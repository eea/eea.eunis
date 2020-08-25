package ro.finsiel.eunis.search;

import ro.finsiel.eunis.jrfTables.Chm62edtHabitatPersist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simple tree to handle habitat hierarchical view
 */
public class HabitatTree {
    private Integer idHabitat;
    private Integer idHabitatParent;
    private String name;
    private String code;
    private List<HabitatTree> children = new ArrayList<>();
    private boolean hasChildren;

    public Integer getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(Integer idHabitat) {
        this.idHabitat = idHabitat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HabitatTree> getChildren() {
        return children;
    }

    public void setChildren(List<HabitatTree> children) {
        this.children = children;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getIdHabitatParent() {
        return idHabitatParent;
    }

    public void setIdHabitatParent(Integer idHabitatParent) {
        this.idHabitatParent = idHabitatParent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HabitatTree)) return false;
        HabitatTree that = (HabitatTree) o;
        return getIdHabitat().equals(that.getIdHabitat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdHabitat());
    }
}
