package eionet.eunis.dao.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.displaytag.properties.SortOrderEnum;

import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.EunisUtil;
import eionet.eunis.dao.IDocumentsDao;
import eionet.eunis.dto.DcContributorDTO;
import eionet.eunis.dto.DcCoverageDTO;
import eionet.eunis.dto.DcCreatorDTO;
import eionet.eunis.dto.DcDateDTO;
import eionet.eunis.dto.DcDescriptionDTO;
import eionet.eunis.dto.DcFormatDTO;
import eionet.eunis.dto.DcIdentifierDTO;
import eionet.eunis.dto.DcIndexDTO;
import eionet.eunis.dto.DcLanguageDTO;
import eionet.eunis.dto.DcObjectDTO;
import eionet.eunis.dto.DcPublisherDTO;
import eionet.eunis.dto.DcRelationDTO;
import eionet.eunis.dto.DcRightsDTO;
import eionet.eunis.dto.DcSourceDTO;
import eionet.eunis.dto.DcSubjectDTO;
import eionet.eunis.dto.DcTitleDTO;
import eionet.eunis.dto.DcTypeDTO;
import eionet.eunis.dto.DesignationDcObjectDTO;
import eionet.eunis.dto.DocumentDTO;
import eionet.eunis.dto.PairDTO;
import eionet.eunis.dto.readers.DcObjectDTOReader;
import eionet.eunis.dto.readers.DocumentDTOReader;
import eionet.eunis.util.CustomPaginatedList;


/**
 * @author Risto Alt
 * <a href="mailto:risto.alt@tieto.com">contact</a>
 */
public class DocumentsDaoImpl extends MySqlBaseDao implements IDocumentsDao {

    public DocumentsDaoImpl() {}

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDocuments(int page, int defaltPageSize, String sort, String dir)
     * {@inheritDoc}
     */
    public CustomPaginatedList<DocumentDTO> getDocuments(int page, int defaltPageSize, String sort, String dir) {

        CustomPaginatedList<DocumentDTO> ret = new CustomPaginatedList<DocumentDTO>();

        int offset = 0;
        if (page > 1) {
            offset = page * defaltPageSize;
        }

        String order = "";
        if (sort != null) {
            if (sort.equals("idDoc")) {
                order = "ID_DC";
            } else if (sort.equals("docTitle")) {
                order = "TITLE";
            } else if (sort.equals("author")) {
                order = "SOURCE";
            } else if (sort.equals("docYear")) {
                order = "CREATED";
            }
        }

        if (order != null && order.length() > 0 && dir != null && dir.length() > 0) {
            order = order + " " + dir.toUpperCase();
        }

        String query = "SELECT ID_DC, ID_TITLE, TITLE, ALTERNATIVE, SOURCE, CREATED FROM DC_INDEX "
            + "LEFT JOIN DC_TITLE USING (ID_DC) "
            + "LEFT JOIN DC_SOURCE USING (ID_DC) "
            + "LEFT JOIN DC_DATE USING (ID_DC)";
        if (order != null && order.length() > 0) {
            query = query + " ORDER BY " + order;
        }
        query = query + (defaltPageSize > 0 ? " LIMIT " + (offset > 0 ? offset + "," : "") + defaltPageSize : "");
        List<Object> values = new ArrayList<Object>();
        DocumentDTOReader rsReader = new DocumentDTOReader();

        try {

            executeQuery(query, values, rsReader);
            List<DocumentDTO> docs = rsReader.getResultList();

            int listSize = getDocumentsCnt();
            ret.setList(docs);
            ret.setFullListSize(listSize);
            ret.setObjectsPerPage(defaltPageSize);
            if (page == 0) {
                page = 1;
            }
            ret.setPageNumber(page);
            ret.setSortCriterion(sort);
            if (dir != null && dir.equals("asc")) {
                ret.setSortDirection(SortOrderEnum.ASCENDING);
            } else if (dir != null && dir.equals("desc")) {
                ret.setSortDirection(SortOrderEnum.DESCENDING);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDocumentsCnt()
     * {@inheritDoc}
     */
    private int getDocumentsCnt() {

        int ret = 0;

        String query = "SELECT COUNT(*) FROM DC_INDEX LEFT JOIN DC_TITLE USING (ID_DC)";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ret = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcContributor(String id)
     * {@inheritDoc}
     */
    public DcContributorDTO getDcContributor(String id) {

        DcContributorDTO object = null;

        String query = "SELECT * FROM DC_CONTRIBUTOR WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcContributorDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdContributor(rs.getString("ID_CONTRIBUTOR"));
                object.setContributor(rs.getString("CONTRIBUTOR"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcCoverage(String id)
     * {@inheritDoc}
     */
    public DcCoverageDTO getDcCoverage(String id) {

        DcCoverageDTO object = null;

        String query = "SELECT * FROM DC_COVERAGE WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcCoverageDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdCoverage(rs.getString("ID_COVERAGE"));
                object.setCoverage(rs.getString("COVERAGE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcCreator(String id)
     * {@inheritDoc}
     */
    public DcCreatorDTO getDcCreator(String id) {

        DcCreatorDTO object = null;

        String query = "SELECT * FROM DC_CREATOR WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcCreatorDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdCreator(rs.getString("ID_CREATOR"));
                object.setCreator(rs.getString("CREATOR"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcDate(String id)
     * {@inheritDoc}
     */
    public DcDateDTO getDcDate(String id) {

        DcDateDTO object = null;

        String query = "SELECT * FROM DC_DATE WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcDateDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdDate(rs.getString("ID_DATE"));
                object.setAvailable(rs.getString("AVAILABLE"));
                object.setCreated(rs.getString("CREATED"));
                object.setIssued(rs.getString("ISSUED"));
                object.setMdate(rs.getString("MDATE"));
                object.setModified(rs.getString("MODIFIED"));
                object.setValid(rs.getString("VALID"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcDescription(String id)
     * {@inheritDoc}
     */
    public DcDescriptionDTO getDcDescription(String id) {

        DcDescriptionDTO object = null;

        String query = "SELECT * FROM DC_DESCRIPTION WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcDescriptionDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdDescription(rs.getString("ID_DESCRIPTION"));
                object.setDescription(rs.getString("DESCRIPTION"));
                object.setAbstr(rs.getString("ABSTRACT"));
                object.setToc(rs.getString("TOC"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcFormat(String id)
     * {@inheritDoc}
     */
    public DcFormatDTO getDcFormat(String id) {

        DcFormatDTO object = null;

        String query = "SELECT * FROM DC_FORMAT WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcFormatDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdFormat(rs.getString("ID_FORMAT"));
                object.setFormat(rs.getString("FORMAT"));
                object.setExtent(rs.getString("EXTENT"));
                object.setMedium(rs.getString("MEDIUM"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcIdentifier(String id)
     * {@inheritDoc}
     */
    public DcIdentifierDTO getDcIdentifier(String id) {

        DcIdentifierDTO object = null;

        String query = "SELECT * FROM DC_IDENTIFIER WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcIdentifierDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdIdentifier(rs.getString("ID_IDENTIFIER"));
                object.setIdentifier(rs.getString("IDENTIFIER"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcIndex(String id)
     * {@inheritDoc}
     */
    public DcIndexDTO getDcIndex(String id) {

        DcIndexDTO object = null;

        String query = "SELECT * FROM DC_INDEX WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcIndexDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setComment(rs.getString("COMMENT"));
                object.setRefCd(rs.getString("REFCD"));
                object.setReference(rs.getString("REFERENCE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcLanguage(String id)
     * {@inheritDoc}
     */
    public DcLanguageDTO getDcLanguage(String id) {

        DcLanguageDTO object = null;

        String query = "SELECT * FROM DC_LANGUAGE WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcLanguageDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdLanguage(rs.getString("ID_LANGUAGE"));
                object.setLanguage(rs.getString("LANGUAGE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcPublisher(String id)
     * {@inheritDoc}
     */
    public DcPublisherDTO getDcPublisher(String id) {

        DcPublisherDTO object = null;

        String query = "SELECT * FROM DC_PUBLISHER WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcPublisherDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdPublisher(rs.getString("ID_PUBLISHER"));
                object.setPublisher(rs.getString("PUBLISHER"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcRelation(String id)
     * {@inheritDoc}
     */
    public DcRelationDTO getDcRelation(String id) {

        DcRelationDTO object = null;

        String query = "SELECT * FROM DC_RELATION WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcRelationDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdRelation(rs.getString("ID_RELATION"));
                object.setRelation(rs.getString("RELATION"));
                object.setHasFormat(rs.getString("HAS_FORMAT"));
                object.setHasPart(rs.getString("HAS_PART"));
                object.setHasVersion(rs.getString("HAS_VERSION"));
                object.setIsFormatOf(rs.getString("IS_FORMAT_OF"));
                object.setIsPartOf(rs.getString("IS_PART_OF"));
                object.setIsReferencedBy(rs.getString("IS_REFERENCED_BY"));
                object.setIsReplacedBy(rs.getString("IS_REPLACED_BY"));
                object.setIsRequiredBy(rs.getString("IS_REQUIRED_BY"));
                object.setIsVersionOf(rs.getString("IS_VERSION_OF"));
                object.setReferences(rs.getString("REFERENCES"));
                object.setRequires(rs.getString("REQUIRES"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcRights(String id)
     * {@inheritDoc}
     */
    public DcRightsDTO getDcRights(String id) {

        DcRightsDTO object = null;

        String query = "SELECT * FROM DC_RIGHTS WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcRightsDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdRights(rs.getString("ID_RIGHTS"));
                object.setRights(rs.getString("RIGHTS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcSubject(String id)
     * {@inheritDoc}
     */
    public DcSubjectDTO getDcSubject(String id) {

        DcSubjectDTO object = null;

        String query = "SELECT * FROM DC_SUBJECT WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcSubjectDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdSubject(rs.getString("ID_SUBJECT"));
                object.setSubject(rs.getString("SUBJECT"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcType(String id)
     * {@inheritDoc}
     */
    public DcTypeDTO getDcType(String id) {

        DcTypeDTO object = null;

        String query = "SELECT * FROM DC_TYPE WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                object = new DcTypeDTO();
                object.setIdDc(rs.getString("ID_DC"));
                object.setIdType(rs.getString("ID_TYPE"));
                object.setType(rs.getString("TYPE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return object;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcTitle(String id)
     * {@inheritDoc}
     */
    public DcTitleDTO getDcTitle(String id) {

        DcTitleDTO doc = null;

        String query = "SELECT * FROM DC_TITLE WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                doc = new DcTitleDTO();
                doc.setIdDoc(rs.getString("ID_DC"));
                doc.setIdTitle(rs.getString("ID_TITLE"));
                doc.setTitle(rs.getString("TITLE"));
                doc.setAlternative(rs.getString("ALTERNATIVE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return doc;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcSource(String id)
     * {@inheritDoc}
     */
    public DcSourceDTO getDcSource(String id) {

        DcSourceDTO source = null;

        String query = "SELECT * FROM DC_SOURCE WHERE ID_DC = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                source = new DcSourceDTO();
                source.setIdDc(rs.getString("ID_DC"));
                source.setIdSource(rs.getString("ID_SOURCE"));
                source.setSource(rs.getString("SOURCE"));
                source.setEditor(rs.getString("EDITOR"));
                source.setJournalTitle(rs.getString("JOURNAL_TITLE"));
                source.setBookTitle(rs.getString("BOOK_TITLE"));
                source.setJournalIssue(rs.getString("JOURNAL_ISSUE"));
                source.setIsbn(rs.getString("ISBN"));
                source.setGeoLevel(rs.getString("GEO_LEVEL"));
                source.setUrl(rs.getString("URL"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return source;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcObjects()
     * {@inheritDoc}
     */
    public List<DcObjectDTO> getDcObjects() {

        List<DcObjectDTO> ret = new ArrayList<DcObjectDTO>();

        String query = "SELECT ID_DC, TITLE, SOURCE, URL, CONTRIBUTOR, COVERAGE, CREATOR, CREATED, DESCRIPTION, FORMAT, "
            + "IDENTIFIER, LANGUAGE, PUBLISHER, RELATION, RIGHTS, SUBJECT, TYPE "
            + "FROM DC_INDEX " + "LEFT JOIN DC_TITLE USING (ID_DC) "
            + "LEFT JOIN DC_SOURCE USING (ID_DC) "
            + "LEFT JOIN DC_CONTRIBUTOR USING (ID_DC) "
            + "LEFT JOIN DC_CREATOR USING (ID_DC) "
            + "LEFT JOIN DC_COVERAGE USING (ID_DC) "
            + "LEFT JOIN DC_DATE USING (ID_DC) "
            + "LEFT JOIN DC_DESCRIPTION USING (ID_DC) "
            + "LEFT JOIN DC_FORMAT USING (ID_DC) "
            + "LEFT JOIN DC_IDENTIFIER USING (ID_DC) "
            + "LEFT JOIN DC_LANGUAGE USING (ID_DC) "
            + "LEFT JOIN DC_PUBLISHER USING (ID_DC) "
            + "LEFT JOIN DC_RELATION USING (ID_DC) "
            + "LEFT JOIN DC_RIGHTS USING (ID_DC) "
            + "LEFT JOIN DC_SUBJECT USING (ID_DC) "
            + "LEFT JOIN DC_TYPE USING (ID_DC)";

        List<Object> values = new ArrayList<Object>();
        DcObjectDTOReader rsReader = new DcObjectDTOReader();

        try {

            executeQuery(query, values, rsReader);
            ret = rsReader.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * @see eionet.eunis.dao.IDocumentsDao#getDcObject(String id)
     * {@inheritDoc}
     */
    public DcObjectDTO getDcObject(String id) {

        DcObjectDTO ret = new DcObjectDTO();

        String query = "SELECT ID_DC, TITLE, SOURCE, URL, CONTRIBUTOR, COVERAGE, CREATOR, CREATED, DESCRIPTION, FORMAT, "
            + "IDENTIFIER, LANGUAGE, PUBLISHER, RELATION, RIGHTS, SUBJECT, TYPE "
            + "FROM DC_INDEX " + "LEFT JOIN DC_TITLE USING (ID_DC) "
            + "LEFT JOIN DC_SOURCE USING (ID_DC) "
            + "LEFT JOIN DC_CONTRIBUTOR USING (ID_DC) "
            + "LEFT JOIN DC_CREATOR USING (ID_DC) "
            + "LEFT JOIN DC_COVERAGE USING (ID_DC) "
            + "LEFT JOIN DC_DATE USING (ID_DC) "
            + "LEFT JOIN DC_DESCRIPTION USING (ID_DC) "
            + "LEFT JOIN DC_FORMAT USING (ID_DC) "
            + "LEFT JOIN DC_IDENTIFIER USING (ID_DC) "
            + "LEFT JOIN DC_LANGUAGE USING (ID_DC) "
            + "LEFT JOIN DC_PUBLISHER USING (ID_DC) "
            + "LEFT JOIN DC_RELATION USING (ID_DC) "
            + "LEFT JOIN DC_RIGHTS USING (ID_DC) "
            + "LEFT JOIN DC_SUBJECT USING (ID_DC) "
            + "LEFT JOIN DC_TYPE USING (ID_DC) " + "WHERE ID_DC = ?";

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ret.setId(rs.getString("ID_DC"));
                ret.setTitle(rs.getString("TITLE"));
                ret.setSource(rs.getString("SOURCE"));
                ret.setSourceUrl(rs.getString("URL"));
                ret.setContributor(rs.getString("CONTRIBUTOR"));
                ret.setCoverage(rs.getString("COVERAGE"));
                ret.setCreator(rs.getString("CREATOR"));
                ret.setDate(rs.getString("CREATED"));
                ret.setDescription(rs.getString("DESCRIPTION"));
                ret.setFormat(rs.getString("FORMAT"));
                ret.setIdentifier(rs.getString("IDENTIFIER"));
                ret.setLanguage(rs.getString("LANGUAGE"));
                ret.setPublisher(rs.getString("PUBLISHER"));
                ret.setRelation(rs.getString("RELATION"));
                ret.setRights(rs.getString("RIGHTS"));
                ret.setSubject(rs.getString("SUBJECT"));
                ret.setType(rs.getString("TYPE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return ret;
    }

    public Integer insertSource(String title, String source, String publisher, String editor, String url, String year) throws Exception {
        Integer ret = null;

        if (source != null) {
            int id_dc = getId("SELECT MAX(ID_DC) FROM DC_INDEX");

            id_dc++;
            ret = new Integer(id_dc);

            String insertIndex = "INSERT INTO dc_index (ID_DC, REFERENCE, COMMENT, REFCD) VALUES (?,-1,'RED_LIST',0)";
            String insertTitle = "INSERT INTO dc_title (ID_DC, ID_TITLE, TITLE) VALUES (?,1,?)";
            String insertPublisher = "INSERT INTO dc_publisher (ID_DC, ID_PUBLISHER, PUBLISHER) VALUES (?,1,?)";
            String insertDate = "INSERT INTO dc_date (ID_DC, ID_DATE, CREATED) VALUES (?,1,?)";
            String insertSource = "INSERT INTO dc_source (ID_DC, ID_SOURCE, SOURCE, EDITOR, URL) VALUES (?,1,?,?,?)";

            Connection con = null;
            PreparedStatement psIndex = null;
            PreparedStatement psTitle = null;
            PreparedStatement psPublisher = null;
            PreparedStatement psDate = null;
            PreparedStatement psSource = null;

            try {
                con = getConnection();
                psIndex = con.prepareStatement(insertIndex);
                psIndex.setInt(1, id_dc);
                psIndex.executeUpdate();

                psTitle = con.prepareStatement(insertTitle);
                psTitle.setInt(1, id_dc);
                psTitle.setString(2, title);
                psTitle.executeUpdate();

                psPublisher = con.prepareStatement(insertPublisher);
                psPublisher.setInt(1, id_dc);
                psPublisher.setString(2, publisher);
                psPublisher.executeUpdate();

                if (year != null && year.length() == 4) {
                    psDate = con.prepareStatement(insertDate);
                    psDate.setInt(1, id_dc);
                    psDate.setInt(2, new Integer(year).intValue());
                    psDate.executeUpdate();
                }

                psSource = con.prepareStatement(insertSource);
                psSource.setInt(1, id_dc);
                psSource.setString(2, source);
                psSource.setString(3, editor);
                psSource.setString(4, url);
                psSource.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                con.close();
                psIndex.close();
                psTitle.close();
                psPublisher.close();
                psDate.close();
                psSource.close();
            }
        }
        return ret;
    }

    private int getId(String query) throws ParseException {
        String maxId = ExecuteSQL(query);
        int maxIdInt = 0;

        if (maxId != null && maxId.length() > 0) {
            maxIdInt = new Integer(maxId).intValue();
        }

        return maxIdInt;
    }

    public DesignationDcObjectDTO getDesignationDcObject(String idDesig, String idGeo) throws Exception {

        DesignationDcObjectDTO ret = null;

        String query = "SELECT " + "`DC_SOURCE`.`SOURCE`, "
        + "`DC_SOURCE`.`EDITOR`, " + "`DC_DATE`.`CREATED`, "
        + "`DC_TITLE`.`TITLE`, " + "`DC_PUBLISHER`.`PUBLISHER` "
        + "FROM " + "`CHM62EDT_DESIGNATIONS` "
        + "INNER JOIN `DC_INDEX` ON (`CHM62EDT_DESIGNATIONS`.`ID_DC` = `DC_INDEX`.`ID_DC`) "
        + "INNER JOIN `DC_PUBLISHER` ON (`DC_INDEX`.`ID_DC` = `DC_PUBLISHER`.`ID_DC`) "
        + "INNER JOIN `DC_TITLE` ON (`DC_INDEX`.`ID_DC` = `DC_TITLE`.`ID_DC`) "
        + "INNER JOIN `DC_SOURCE` ON (`DC_INDEX`.`ID_DC` = `DC_SOURCE`.`ID_DC`) "
        + "INNER JOIN `DC_DATE` ON (`DC_INDEX`.`ID_DC` = `DC_DATE`.`ID_DC`) "
        + "WHERE "
        + "`CHM62EDT_DESIGNATIONS`.ID_DESIGNATION = ? AND `CHM62EDT_DESIGNATIONS`.`ID_GEOSCOPE` = ?";

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, idDesig);
            preparedStatement.setString(2, idGeo);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {

                ret = new DesignationDcObjectDTO();

                ret.setAuthor(
                        Utilities.formatString(
                                Utilities.FormatDatabaseFieldName(
                                        rs.getString(1)),
                        ""));
                ret.setEditor(
                        Utilities.formatString(
                                Utilities.FormatDatabaseFieldName(
                                        rs.getString(2)),
                        ""));
                ret.setDate(
                        Utilities.formatString(
                                Utilities.formatReferencesDate(rs.getDate(3)),
                        ""));
                ret.setTitle(
                        Utilities.formatString(
                                Utilities.FormatDatabaseFieldName(
                                        rs.getString(4)),
                        ""));
                ret.setPublisher(
                        Utilities.formatString(
                                Utilities.FormatDatabaseFieldName(
                                        rs.getString(5)),
                        ""));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }

        return ret;
    }

    public List<PairDTO> getRedListSources() throws Exception {

        List<PairDTO> ret = new ArrayList<PairDTO>();

        String query = "SELECT S.ID_DC, S.SOURCE, TITLE.TITLE "
            + "FROM chm62edt_reports AS R, chm62edt_report_type AS T, DC_SOURCE AS S, DC_TITLE AS TITLE "
            + "WHERE R.ID_REPORT_TYPE = T.ID_REPORT_TYPE AND T.LOOKUP_TYPE = 'CONSERVATION_STATUS' "
            + "AND R.ID_DC = S.ID_DC AND R.ID_DC = TITLE.ID_DC GROUP BY R.ID_DC ORDER BY TITLE.TITLE";

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {

                String idDc = rs.getString("ID_DC");
                String title = rs.getString("TITLE");
                String source = rs.getString("SOURCE");

                String heading = EunisUtil.threeDots(title, 50) + " (" + source
                + ")";
                PairDTO pair = new PairDTO();

                pair.setKey(idDc);
                pair.setValue(heading);
                ret.add(pair);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllResources(con, preparedStatement, rs);
        }
        return ret;
    }

}
