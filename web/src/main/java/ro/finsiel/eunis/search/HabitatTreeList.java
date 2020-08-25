package ro.finsiel.eunis.search;

public class HabitatTreeList {
    private int idHabitat;
    private int level;
    private String code;
    private String name;
    private boolean open;
    private boolean children;

    public HabitatTreeList() {
    }

    public HabitatTreeList(HabitatTree h) {
        this.idHabitat = h.getIdHabitat();
        this.code = h.getCode();
        this.name = h.getName();
        this.children = h.isHasChildren();
    }

    public int getIdHabitat() {
        return idHabitat;
    }

    public void setIdHabitat(int idHabitat) {
        this.idHabitat = idHabitat;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean hasChildren() {
        return children;
    }
}
