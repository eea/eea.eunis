package eionet.eunis.stripes.actions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import ro.finsiel.eunis.dataimport.parsers.RedListsImportParser;
import ro.finsiel.eunis.utilities.SQLUtilities;
import eionet.eunis.dao.DaoFactory;
import eionet.eunis.dto.PairDTO;
import eionet.eunis.util.Constants;

/**
 * Action bean to handle RDF export.
 *
 * @author Risto Alt
 * <a href="mailto:risto.alt@tieto.com">contact</a>
 */
@UrlBinding("/dataimport/importredlist")
public class RedListImporterActionBean extends AbstractStripesAction {

    private FileBean[] files = new FileBean[5];
    private boolean delete = false;

    private String title;
    private String source;
    private String publisher;
    private String editor;
    private String url;
    private String date;

    private List<PairDTO> sources;
    private Integer idDc;

    @DefaultHandler
    public Resolution defaultAction() {
        try {
            sources = DaoFactory.getDaoFactory().getReferncesDao().getRedListSources();
        } catch (Exception e) {
            logger.debug(e, e);
        }
        String forwardPage = "/stripes/redlistimporter.jsp";

        setMetaDescription("Import Red List");
        return new ForwardResolution(forwardPage);
    }

    public Resolution importRedList() {

        String forwardPage = "/stripes/redlistimporter.jsp";

        setMetaDescription("Import Red List");
        if (getContext().getSessionManager().isAuthenticated() && getContext().getSessionManager().isImportExportData_RIGHT()) {
            try {
                SQLUtilities sqlUtil = getContext().getSqlUtilities();
                Integer redlistCatIdDc = new Integer(getContext().getApplicationProperty("redlist.categories.id_dc"));

                if (idDc != null && idDc.intValue() == -1) {
                    if (files != null && files.length > 0) {
                        idDc =
                            DaoFactory.getDaoFactory().getReferncesDao()
                            .insertSource(title, source, publisher, editor, url, date);
                    }
                }

                if (idDc != null && idDc.intValue() != -1 && redlistCatIdDc != null) {
                    int cnt = 0;
                    int cntUnique = 0;
                    List<String> notImported = new ArrayList<String>();
                    List<String> duplicates = new ArrayList<String>();

                    for (FileBean file : files) {
                        if (file != null) {
                            InputStream inputStream = file.getInputStream();

                            RedListsImportParser parser = new RedListsImportParser(sqlUtil, idDc, delete, redlistCatIdDc);

                            parser.execute(inputStream);
                            cnt += parser.getImported();
                            if (parser.getNotImported() != null) {
                                notImported.addAll(parser.getNotImported());
                            }
                            if (parser.getImportedSpecies() != null) {
                                cntUnique += parser.getImportedSpecies().size();
                            }
                            if (parser.getDuplicateSpecies() != null) {
                                duplicates.addAll(parser.getDuplicateSpecies());
                            }
                            file.delete();
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        }
                    }
                    showMessage(cntUnique + " species imported, " + cnt + " species updated!");
                    StringBuilder error = new StringBuilder();
                    if (duplicates != null && duplicates.size() > 0) {
                        error.append("Duplicately matching species (" + duplicates.size() + ") updated: <ul>");
                        for (String name : duplicates) {
                            error.append("<li>" + name + "</li>");
                        }
                        error.append("</ul><br/>");
                    }
                    if (notImported != null && notImported.size() > 0) {
                        error.append("Following species (" + notImported.size() + ") were not imported: <ul>");

                        for (String name : notImported) {
                            error.append("<li>" + name + "</li>");
                        }
                        error.append("</ul>");
                    }
                    if (error.length() > 0) {
                        showWarning(error.toString());
                    }

                    sources = DaoFactory.getDaoFactory().getReferncesDao().getRedListSources();
                } else {
                    handleEunisException("Could not insert new source!", Constants.SEVERITY_WARNING);
                }

            } catch (Exception e) {
                logger.debug(e, e);
                handleEunisException(e.getMessage(), Constants.SEVERITY_ERROR);
            }
        } else {
            handleEunisException("You are not logged in or you do not have enough privileges to view this page!",
                    Constants.SEVERITY_WARNING);
        }
        return new ForwardResolution(forwardPage);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public FileBean[] getFiles() {
        return files;
    }

    public void setFiles(FileBean[] files) {
        this.files = files;
    }

    public Integer getIdDc() {
        return idDc;
    }

    public void setIdDc(Integer idDc) {
        this.idDc = idDc;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public List<PairDTO> getSources() {
        return sources;
    }

    public void setSources(List<PairDTO> sources) {
        this.sources = sources;
    }

}
