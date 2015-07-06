package eionet.eunis.stripes.actions;

import java.util.*;

import eionet.eunis.dto.*;
import eionet.eunis.util.AlphanumComparator;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;

import ro.finsiel.eunis.exceptions.CriteriaMissingException;
import ro.finsiel.eunis.jrfTables.ReferencesDomain;
import ro.finsiel.eunis.search.AbstractSortCriteria;
import ro.finsiel.eunis.search.species.references.ReferencesSearchCriteria;
import ro.finsiel.eunis.utilities.EunisUtil;
import eionet.eunis.dao.DaoFactory;
import eionet.eunis.dao.IReferencesDao;
import eionet.eunis.util.Constants;
import eionet.eunis.util.CustomPaginatedList;

/**
 * Action bean for references.
 *
 * @author Risto Alt
 */
@UrlBinding("/references/{idref}/{section}")
public class ReferencesActionBean extends AbstractStripesAction {

    /** */
    public final static String DEFAULT_FILTER_VALUE = "Search reference by author or title here ...";

    private String idref;
    private String section;
    private CustomPaginatedList<ReferenceDTO> refs;
    private DcIndexDTO dcIndex;
    private DcIndexDTO parent;
    private List<DcIndexDTO> children;
    private List<AttributeDto> dcAttributes;
    private List<DcLinkDTO> links;

    /** tabs to display */
//    private List<Pair<String, String>> tabsWithData = new LinkedList<Pair<String, String>>();

    List<ReferenceSpeciesGroupDTO> speciesGrouped = new ArrayList<ReferenceSpeciesGroupDTO>();
    List<ReferenceSpeciesDTO> speciesByName = new ArrayList<ReferenceSpeciesDTO>();
    
    List<HabitatDTO> habitats = new ArrayList<HabitatDTO>();

    private int page = 1;
    private String sort;
    private String dir;

    /** References tab filter input and it's default phrase. */
    private String filterPhrase = DEFAULT_FILTER_VALUE;
    
    @DefaultHandler
    @DontValidate(ignoreBindingErrors = true)
    public Resolution defaultAction() {

        String forwardPage = "/stripes/references.jsp";

        String eeaHome = getContext().getInitParameter("EEA_HOME");
        String btrail = "";
        IReferencesDao dao = DaoFactory.getDaoFactory().getReferncesDao();

        String defaultPageSize = getContext().getApplicationProperty("default.page.size");
        
        if (!StringUtils.isBlank(idref) && EunisUtil.isNumber(idref)) {
            forwardPage = "/stripes/reference.jsp";

            dcIndex = dao.getDcIndex(idref);
            dcAttributes = dao.getDcAttributes(idref);

            if (dcIndex == null) {
                return new ErrorResolution(404);
            }

            // Get the parent reference
            if(dcIndex.getReference() != null) {
                parent = dao.getDcIndex(dcIndex.getReference());
            }

            links = dao.getLinks(dcIndex.getIdDc());

            // search for the children ; orders by alphanum
            children = dao.getChildren(dcIndex.getIdDc());
            Collections.sort(children, new ChildrenComparator());

            btrail = "eea#" + eeaHome
                    + ",home#index.jsp,references#references";
            if (dcIndex != null && dcIndex.getTitle() != null) {
                btrail += "," + dcIndex.getTitle();
            }

            try {
                ReferencesDomain refDomain = new ReferencesDomain(new ReferencesSearchCriteria[0], new AbstractSortCriteria[0]);
                speciesByName = refDomain.getSpeciesForAReference(idref);

                habitats = refDomain.getHabitatsForAReferences(idref);
            } catch (CriteriaMissingException e) {
                e.printStackTrace();
            }

            setMetaDescription("reference");
        } else if (!StringUtils.isBlank(idref) && !EunisUtil.isNumber(idref)) {
            handleEunisException("Reference ID has to be a number!", Constants.SEVERITY_ERROR);
            setMetaDescription("references");
        } else {
            btrail = "eea#" + eeaHome + ",home#index.jsp,references";
            refs = dao.getReferences(page, Integer.parseInt(defaultPageSize), sort, dir, filterPhrase);
            setMetaDescription("references");
        }
        setBtrail(btrail);

        if(section!=null){
            // check that the linked section actually exists
            if(section.equals("species") && speciesByName.size() == 0){
                section = "";
            }
            if(section.equals("habitats") && habitats.size() == 0){
                section = "";
            }
            if(!(section.equals("") || section.equals("species") || section.equals("habitats"))){
                section = "";
            }
        }

        return new ForwardResolution(forwardPage);
    }

    public String getIdref() {
        return idref;
    }

    public void setIdref(String idref) {
        this.idref = idref;
    }

    public CustomPaginatedList<ReferenceDTO> getRefs() {
        return refs;
    }

    public void setRefs(CustomPaginatedList<ReferenceDTO> refs) {
        this.refs = refs;
    }

    public DcIndexDTO getDcIndex() {
        return dcIndex;
    }

    public void setDcIndex(DcIndexDTO dcIndex) {
        this.dcIndex = dcIndex;
    }

    public List<ReferenceSpeciesGroupDTO> getSpeciesGrouped() {
        return speciesGrouped;
    }

    public void setSpeciesGrouped(List<ReferenceSpeciesGroupDTO> speciesGrouped) {
        this.speciesGrouped = speciesGrouped;
    }

    public List<HabitatDTO> getHabitats() {
        return habitats;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public List<AttributeDto> getDcAttributes() {
        return dcAttributes;
    }

    public String getFilterPhrase() {
        return filterPhrase;
    }

    public void setFilterPhrase(String filterPhrase) {
        this.filterPhrase = filterPhrase;
    }

    public String getDefaultFilterValue() {
        return DEFAULT_FILTER_VALUE;
    }

    public List<ReferenceSpeciesDTO> getSpeciesByName() {
        return speciesByName;
    }

    public void setSpeciesByName(List<ReferenceSpeciesDTO> speciesByName) {
        this.speciesByName = speciesByName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public DcIndexDTO getParent() {
        return parent;
    }

    public List<DcIndexDTO> getChildren() {
        return children;
    }

    private class ChildrenComparator implements Comparator {

        private AlphanumComparator ac = new AlphanumComparator();

        @Override
        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof DcIndexDTO) || !(o2 instanceof DcIndexDTO))
            {
                return 0;
            }
            return ac.compare(((DcIndexDTO) o1).getTitle(), ((DcIndexDTO) o2).getTitle());
        }
    }

    public List<DcLinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<DcLinkDTO> links) {
        this.links = links;
    }
}
