package eionet.eunis.stripes.actions;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import ro.finsiel.eunis.factsheet.species.SpeciesFactsheet;
import ro.finsiel.eunis.jrfTables.Chm62edtNatureObjectPictureDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtNatureObjectPicturePersist;
import ro.finsiel.eunis.search.species.SpeciesSearchUtility;
import ro.finsiel.eunis.search.species.VernacularNameWrapper;
import ro.finsiel.eunis.utilities.SQLUtilities;
import eionet.eunis.dto.SpeciesFactsheetDto;
import eionet.eunis.dto.SpeciesSynonymDto;
import eionet.eunis.dto.VernacularNameDto;
import eionet.eunis.util.Pair;

/**
 * ActionBean to replace old /species-factsheet.jsp.
 * 
 * @author Aleksandr Ivanov
 * <a href="mailto:aleksandr.ivanov@tietoenator.com">contact</a>
 */
@UrlBinding("/species/{idSpecies}/{tab}")
public class SpeciesFactsheetActionBean extends AbstractStripesAction implements RdfAware {
	
	private static final String[][] allTypes = new String[][]{
		{"GENERAL_INFORMATION","general"},
		{"VERNACULAR_NAMES","vernacular"},
		{"GEOGRAPHICAL_DISTRIBUTION","countries"},
		{"POPULATION","population"},
		{"TRENDS","trends"},
		{"REFERENCES","references"},
		{"GRID_DISTRIBUTION","grid"},
		{"THREAT_STATUS","threatstatus"},
		{"LEGAL_INSTRUMENTS","legal"},
		{"HABITATS","habitats"},
		{"SITES","sites"},
		{"GBIF","gbif"}};

	private String idSpecies;
	private int idSpeciesLink;
	
	private SpeciesFactsheet factsheet;
	private String scientificName = "";
	private String author = "";
	
	
	//selected tab
	private String tab;
	//tabs to display
	private List<Pair<String,String>> tabsWithData = new LinkedList<Pair<String,String>>();
	//refered from name
	private String referedFromName;

	private String mainPictureFilename;

	
	@DefaultHandler
	@SuppressWarnings("unchecked")
	public Resolution index(){
		String idSpeciesText = null;
		if(tab == null || tab.length() == 0){
			tab = "general";
		}
		
		//sanity checks
		if(StringUtils.isBlank(idSpecies) && idSpeciesLink == 0) {
			factsheet = new SpeciesFactsheet(0, 0);
		}
		int tempIdSpecies = 0;
		
		//get idSpecies based on the request param.
		if (StringUtils.isNumeric(idSpecies)) {
			tempIdSpecies = new Integer(idSpecies);
		} else if (!StringUtils.isBlank(idSpecies)) {
			idSpeciesText = idSpecies;
			tempIdSpecies = getContext().getSpeciesFactsheetDao().getIdSpeciesForScientificName(this.idSpecies);
		}
		
		
		int mainIdSpecies = getContext().getSpeciesFactsheetDao().getCanonicalIdSpecies(tempIdSpecies);
		//it is not a synonym, check the referer
		if (mainIdSpecies == tempIdSpecies) {
			Integer referedFrom = (Integer) getContext().getFromSession("referer");
			if (referedFrom != null && referedFrom > 0) {
				getContext().removeFromSession("referer");
				referedFromName = getContext().getSpeciesFactsheetDao().getScientificName(referedFrom);
			}
			factsheet = new SpeciesFactsheet(mainIdSpecies, mainIdSpecies);
		}
		//it's a synonym for another specie, redirect.
		else {
			getContext().addToSession("referer", tempIdSpecies);
			return new RedirectResolution(getUrlBinding()).addParameter("idSpecies", mainIdSpecies);
		}
		
		if (StringUtils.isNotBlank(idSpeciesText) && !factsheet.exists()) {
			//redirecting to more general search in case user tried text based search
			String redirectUrl = "/species-names-result.jsp?pageSize=10" +
					"&relationOp=2&typeForm=0&showGroup=true&showOrder=true" +
					"&showFamily=true&showScientificName=true&showVernacularNames=true" +
					"&showValidName=true&searchSynonyms=true&sort=2&ascendency=0" +
					"&scientificName=" + idSpeciesText;
			return new RedirectResolution(redirectUrl);
		}
		
		if (factsheet.exists()) {
			//set up some vars used in the presentation layer
			setMetaDescription(factsheet.getSpeciesDescription());
			scientificName = StringEscapeUtils.escapeHtml(
					factsheet.getSpeciesNatureObject().getScientificName());
			author  = StringEscapeUtils.escapeHtml(
					factsheet.getSpeciesNatureObject().getAuthor());
			
			SQLUtilities sqlUtil = getContext().getSqlUtilities();
			for (int i = 0; i< allTypes.length; i++) {
				if (!sqlUtil.TabPageIsEmpy(factsheet.getSpeciesNatureObject().getIdNatureObject().toString(), "SPECIES", allTypes[i][0])) {
					tabsWithData.add(new Pair<String, String>(allTypes[i][1], getContentManagement().cms(allTypes[i][0].toLowerCase())));
				}
			}
			List<Chm62edtNatureObjectPicturePersist> pictures = new Chm62edtNatureObjectPictureDomain()
					.findWhere("MAIN_PIC = 1 AND ID_OBJECT = " + mainIdSpecies );
			if (pictures != null && !pictures.isEmpty()) {
				mainPictureFilename = pictures.get(0).getFileName();
			}
		} 
		String eeaHome = getContext().getInitParameter("EEA_HOME");
		String btrail = "eea#" + eeaHome + ",home#index.jsp,species#species.jsp,factsheet";
		setBtrail(btrail);
		return new ForwardResolution("/stripes/species-factsheet.layout.jsp");
	}

	private int getSpeciesId() {
		int tempIdSpecies = 0;
		//get idSpecies based on the request param.
		if (StringUtils.isNumeric(idSpecies)) {
			tempIdSpecies = new Integer(idSpecies);
		} else if (!StringUtils.isBlank(idSpecies)) {
			tempIdSpecies = getContext().getSpeciesFactsheetDao().getIdSpeciesForScientificName(this.idSpecies);
		}
		return tempIdSpecies;
	}
	
	public Resolution generateRdf() {
		int speciesId = getSpeciesId();
		if (speciesId == 0) {
			return new ErrorResolution(404);
		}
		factsheet = new SpeciesFactsheet(speciesId,speciesId);
		if (!factsheet.exists()) {
			return new ErrorResolution(404);
		}
		
		Persister persister = new Persister(new AnnotationStrategy(), new Format(4));
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		SpeciesFactsheetDto dto = new SpeciesFactsheetDto();
		dto.setScientificName(factsheet.getSpeciesObject().getScientificName());
		dto.setGenus(factsheet.getSpeciesObject().getGenus());
		dto.setAuthor(factsheet.getSpeciesObject().getAuthor());
		dto.setDwcScientificName(dto.getScientificName() + ' ' + dto.getAuthor());
		
		dto.setAttributes(
				getContext().getSpeciesFactsheetDao().getAttributesForNatureObject(
						factsheet.getSpeciesObject().getIdNatureObject()));
		
		List<VernacularNameWrapper> vernacularNames = SpeciesSearchUtility.findVernacularNames(
						factsheet.getSpeciesObject().getIdNatureObject());
		if (factsheet.getIdSpeciesLink() != null 
					&& !factsheet.getIdSpeciesLink().equals(factsheet.getIdSpecies())) {
			dto.setSynonymFor(new SpeciesSynonymDto(factsheet.getIdSpeciesLink()));
		}
		List<Integer> isSynonymFor = getContext()
				.getSpeciesFactsheetDao()
				.getSynonyms(factsheet.getIdSpecies());
		List<SpeciesSynonymDto> speciesSynonym = new LinkedList<SpeciesSynonymDto>();
		if (isSynonymFor != null && !isSynonymFor.isEmpty()) {
			for(Integer idSpecies : isSynonymFor) {
				speciesSynonym.add(new SpeciesSynonymDto(idSpecies));
			}
			dto.setHasSynonyms(speciesSynonym);
		}
		
		
		if (vernacularNames != null) {
			List<VernacularNameDto> vernacularDtos = new LinkedList<VernacularNameDto>();
			for (VernacularNameWrapper wrapper : vernacularNames) {
				vernacularDtos.add(new VernacularNameDto(wrapper));
			}
			dto.setVernacularNames(vernacularDtos);
		}
		
		try {
			buffer.write(SpeciesFactsheetDto.HEADER.getBytes("UTF-8"));
			persister.write(dto, buffer, "UTF-8");
			buffer.write(SpeciesFactsheetDto.FOOTER.getBytes("UTF-8"));
			buffer.flush();
			return new StreamingResolution(
					"application/rdf+xml",
					buffer.toString("UTF-8"));
		} catch (Exception ignored) {
			logger.warn("exception while generating rdf for species, " + ignored);
			throw new RuntimeException(ignored);
		}
//		return new ErrorResolution(404);
	}

	/**
	 * @return the factsheet
	 */
	public SpeciesFactsheet getFactsheet() {
		return factsheet;
	}

	/**
	 * @param factsheet the factsheet to set
	 */
	public void setFactsheet(SpeciesFactsheet factsheet) {
		this.factsheet = factsheet;
	}

	/**
	 * @return the currentTab
	 */
	public String getTab() {
		return tab;
	}

	/**
	 * @param currentTab the currentTab to set
	 */
	public void setTab(String tab) {
		this.tab = tab;
	}

	/**
	 * @return the tabsWithData
	 */
	public List<Pair<String, String>> getTabsWithData() {
		return tabsWithData;
	}

	/**
	 * @return the idSpecies
	 */
	public String getIdSpecies() {
		return idSpecies;
	}

	/**
	 * @param idSpecies the idSpecies to set
	 */
	public void setIdSpecies(String idSpecies) {
		this.idSpecies = idSpecies;
	}

	/**
	 * @return the referedFromName
	 */
	public String getReferedFromName() {
		return referedFromName;
	}

	/**
	 * @return the idSpeciesLink
	 */
	public int getIdSpeciesLink() {
		return idSpeciesLink;
	}

	/**
	 * @param idSpeciesLink the idSpeciesLink to set
	 */
	public void setIdSpeciesLink(int idSpeciesLink) {
		this.idSpeciesLink = idSpeciesLink;
	}


	/**
	 * @return the scientificName
	 */
	public String getScientificName() {
		return scientificName;
	}


	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	public String getMainPictureFilename() {
		return mainPictureFilename;
	}

}