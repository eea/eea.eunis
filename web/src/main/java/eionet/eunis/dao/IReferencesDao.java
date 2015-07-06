package eionet.eunis.dao;


import java.util.List;

import eionet.eunis.dto.*;
import eionet.eunis.stripes.actions.ReferencesActionBean;
import eionet.eunis.util.CustomPaginatedList;


/**
 * Helper Dao interface for {@link ReferencesActionBean}.
 *
 * @author Risto Alt
 */
public interface IReferencesDao {

    /**
     * Returns list of dublin core elements
     * @param page nr
     * @param defaltPageSize
     * @param sort order
     * @param dir - sort direction
     * @return list
     */
    CustomPaginatedList<ReferenceDTO> getReferences(int page, int defaltPageSize, String sort, String dir, String likeSearch);

    /**
     * Returns all attributes from dc_attributes table for given ID_DC
     * @param idDc
     * @return List<AttributeDto>
     */
    List<AttributeDto> getDcAttributes(String idDc);

    /**
     * Returns content of table dc_index
     * @param id_dc
     * @return DcIndexDTO
     */
    DcIndexDTO getDcIndex(String id);

    /**
     * Returns all Dublin Core elements
     * @return list of DcIndexDTO elements
     */
    List<DcIndexDTO> getDcObjects();

    /**
     * Insert new source
     * @param title
     * @param source
     * @param publisher
     * @param editor
     * @param url
     * @param year
     * @return ID_DC
     */
    Integer insertSource(String title, String source, String publisher, String editor, String url, String year) throws Exception;

    /**
     * Get DC object for designation factsheet
     * @param idDesig
     * @param idGeo
     * @return DesignationDcObjectDTO
     */
    DesignationDcObjectDTO getDesignationDcObject(String idDesig, String idGeo) throws Exception;

    /**
     * Get list of known sources for red list import
     * @return List
     */
    public List<PairDTO> getRedListSources() throws Exception;

    /**
     * Returns a list of DC objects that are children of the given DC. Searches by the REFERENCE DB field.
     * @param idDc The parent document
     * @return List of DC objects
     */
    public List<DcIndexDTO> getChildren(String idDc);

    /**
     * Reads the links of the reference (for More Info section)
     * @param idDc The reference ID
     * @return List of links
     */
    public List<DcLinkDTO> getLinks(String idDc);

}
