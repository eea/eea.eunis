package ro.finsiel.eunis;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ro.finsiel.eunis.jrfTables.Chm62edtLanguageDomain;
import ro.finsiel.eunis.jrfTables.Chm62edtLanguagePersist;
import ro.finsiel.eunis.jrfTables.EunisISOLanguagesDomain;
import ro.finsiel.eunis.jrfTables.EunisISOLanguagesPersist;
import ro.finsiel.eunis.jrfTables.WebContentDomain;
import ro.finsiel.eunis.jrfTables.WebContentPersist;
import ro.finsiel.eunis.search.Utilities;
import ro.finsiel.eunis.utilities.EunisUtil;
import eionet.eunis.util.Pair;
import ro.finsiel.eunis.utilities.SQLUtilities;

import javax.net.ssl.SSLContext;

import static org.apache.http.conn.ssl.SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;


/**
 * Mange the content from the WEB_CONTENT table. Used for HTML editing of the web pages.
 * This object is constructed to store the content of page in a cache. If HTML is edited directly on database,
 * the looks on page might not be resembled in current page without starting a new browser session. This is because
 * page content is loaded on cache only when new session is created.
 *
 * @author finsiel
 */
public class WebContentManagement implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /** Constant for the empty string - used as a return value. */
    private static final String STR_EMPTY = "";

    /** Constant for HTML newline - used as a return value. */
    private static final String STR_BR = "<br />";

    /**
     * This page keeps all the content of the web pages from database when object initialized in order to avoid
     * query overhead. It is declared as static so that all sessions share the same HTML content to avoid memory overheads
     * in Java VM.
     * The content of the hashmap is:
     * <UL>
     * <LI>Key - ID of the page from database</LI>
     * <LI>Value - WebContentPersist associated in database with that ID_PAGE</LI>
     * </UL>
     * So this is a (String, WebContentPersist) pair.
     */
    private HashMap<String, WebContentPersist> htmlContent = new HashMap<String, WebContentPersist>();

    /** User's chosen language - defaults to English */
    private String language = "en";

    /** If in edit-mode then show links to change the content. */
    private boolean editMode = false;
    /** In advanced edit-mode you can edit attribute values - alt, title, value, label. */
    private boolean advancedEditMode = false;

    /**
     * Change current language displayed within web pages.
     *
     * @param language - the language chosen by the user.
     */
    public void setLanguage(final String language) {
        this.language = language;
        reloadLanguageData();
    }

    /**
     * Reload cached phrases for the chosen language.
     */
    public void reloadLanguageData() {
        cacheHTMLContent(language);
    }

    /**
     * Used to cause a newline between the crayon links in editmodes.
     *
     * @return - A HTML BR statement if in edit modes - otherwise nothing.
     */
    public String br() {
        if (!editMode && !advancedEditMode) {
            return STR_EMPTY;
        } else {
            return STR_BR;
        }
    }

    /**
     * Get the text for a token in the content table
     * Tokens can't contain apostrophes
     *
     * @param idPage
     */
    public String cms(String idPage) {
        return getText(idPage);
    }

    /**
     * Get the text for a token in the content table
     * Tokens can't contain apostrophes
     * Not to be used inside HTML attribute values
     * If we're in edit mode, then show an edit-icon.
     *
     * @param idPage
     */
    public String cmsText(String idPage) {
        String ret = Utilities.replace(idPage, "'", "''");

        ret = getText(ret);

        if (editMode) {
            ret += "<a title=\"Edit this text\" href=\"javascript:openContentManager('"
                + idPage
                + "', 'text');\"><img src=\"images/edit-content.gif\" "
                + "style=\"border : 0px; padding-left : 2px;\" width=\"9\" height=\"9\" /></a>";
        }
        return ret;
    }

    /**
     * Look up a short phrase (potentially HTML) in the content table.
     *
     * The cmsPhrase() is used for short strings - typically one liners. The argument is the phrase.
     * When the argument is looked up in the database, it is MD5 encoded first. This ensures that the
     * key kan fit in the ID_PAGE column in wiki:eunis_web_content.  In order to save resource we
     * bypass the database lookup when the language is English.
     *
     * @param idPage - phrase to look up.
     * @return String - phrase to display on webpage.
     */
    public String cmsPhrase(String idPage) {

        if (language != null && language.equalsIgnoreCase("en")) {
            return idPage;
        }

        // If you convert the idPage to MD5 here, you don't need getTextByMD5, you can just call getText
        return getTextByMD5(idPage);
    }

    /**
     * Look up a short phrase (potentially HTML) in the content table
     * replacing tokens in parameter idPage with values from parameter arguments.
     *
     * The cmsPhrase() is used for short strings - typically one liners. The argument is the phrase.
     * When the argument is looked up in the database, it is MD5 encoded first. This ensures that the
     * key kan fit in the ID_PAGE column in wiki:eunis_web_content.  In order to save resource we
     * bypass the database lookup when the language is English.
     *
     * @param idPage - phrase to look up.
     * @param arguments - arguments for the MessageFormat placeholders in the phrase.
     * @return String - phrase to display on webpage.
     */
    public String cmsPhrase(String idPage, Object... arguments) {

        if (language != null && language.equalsIgnoreCase("en")) {
            return MessageFormat.format(idPage, arguments);
        }

        // If you convert the idPage to MD5 here, you don't need getTextByMD5, you can just call getText
        String ret = getTextByMD5(idPage);
        return MessageFormat.format(ret, arguments);
    }

    /**
     * Used for editing of text that is normally not visible on the page - javascript, error messages, page title etc.
     * When in edit-mode the description and token are shown in italics with a crayon at the end of it.
     *
     * @param idPage - token to look up.
     */
    public String cmsMsg(String idPage) {
        if (!editMode) {
            return STR_EMPTY;
        } else {
            String ret = "<strong><em>" + getDescription(idPage)
            + "</em></strong>: ";

            ret += "<em>";
            ret += getText(idPage);
            ret += "</em>";
            ret += "<a title=\"Edit Text from this page (normally not visible online - javascript,"
                + " error messages, page title etc.)\" href=\"javascript:openContentManager('"
                + idPage
                + "', 'msg');\"><img src=\"images/edit-content-msg.gif\" "
                + "style=\"border : 0px;\" width=\"9\" height=\"9\" /></a>";
            return ret;
        }
    }

    /**
     * Advanced edit mode - use this method where the crayon will appear.
     *
     * @param idPage - token to look up.
     */
    public String cmsAlt(String idPage) {
        if (!advancedEditMode) {
            return STR_EMPTY;
        } else {
            return "<a title=\"Edit Alternative text\" href=\"javascript:openContentManager('"
            + idPage
            + "', 'alt_title');\"><img src=\"images/edit-content-alt.gif\" "
            + "style=\"border : 0px; padding-left : 2px;\" width=\"9\" height=\"9\" /></a>";
        }
    }

    /**
     * Advanced edit mode - use this method where the crayon will appear.
     *
     * @param idPage - token to look up.
     */
    public String cmsTitle(String idPage) {
        if (!advancedEditMode) {
            return STR_EMPTY;
        } else {
            return "<a title=\"Edit Alternative text\" href=\"javascript:openContentManager('"
            + idPage
            + "', 'alt_title');\"><img src=\"images/edit-content-title.gif\" "
            + "style=\"border : 0px; padding-left : 2px;\" width=\"9\" height=\"9\" /></a>";
        }
    }

    /**
     * Advanced edit mode - use this method where the crayon will appear.
     *
     * @param idPage - token to look up.
     */
    public String cmsInput(String idPage) {
        if (!advancedEditMode) {
            return STR_EMPTY;
        } else {
            return "<a title=\"Edit the Value attribute\" href=\"javascript:openContentManager('"
            + idPage
            + "', 'alt');\"><img src=\"images/edit-content-msg.gif\" "
            + "style=\"border : 0px; padding-left : 2px;\" width=\"9\" height=\"9\" /></a>";
        }
    }

    /**
     * Advanced edit mode - use this method where the crayon will appear.
     *
     * @param idPage - token to look up.
     */
    public String cmsLabel(String idPage) {
        if (!advancedEditMode) {
            return STR_EMPTY;
        } else {
            return "<a title=\"Edit the Label attribute\" href=\"javascript:openContentManager('"
            + idPage
            + "', 'label');\"><img src=\"images/edit-content-msg.gif\" "
            + "style=\"border : 0px; padding-left : 2px;\" width=\"9\" height=\"9\" /></a>";
        }
    }

    /**
     * Load the HTML content when object initialized from eunis_web_content into a
     * HashMap object and caches for later use while generating pages.
     */
    private void cacheHTMLContent(final String language) {
        long l = new Date().getTime();

        try {
            htmlContent.clear();
            final List<WebContentPersist> pages = new WebContentDomain().findCustom(
                    "select a.* from `eunis_web_content` as a, (select max(record_date) mx,id_page,lang"
                    + " from `eunis_web_content` group by id_page,lang) as b where a.id_page = b.id_page"
                    + " and a.lang = b.lang and a.record_date = b.mx and a.lang='"
                    + language
                    + "' and concat(a.record_date)<>'0000-00-00 00:00:00'");

            for (int i = 0; i < pages.size(); i++) {
                final WebContentPersist text = pages.get(i);
                final String id_page = text.getIDPage().trim();

                htmlContent.put(id_page, text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Query first the cache then the database for the token. Gets the newest
     * if there are several editions.
     *
     * @param idPage - key to look up in database.
     */
    public String getText(String idPage) {
        if (idPage == null) {
            return "";
        }
        String ret = idPage;

        idPage = idPage.trim();

        if (htmlContent.containsKey(idPage)) {
            WebContentPersist text = htmlContent.get(idPage);

            if (text == null) {
                text = htmlContent.get(idPage);
            }

            if (null != text) {
                if (text.getContent() != null) {
                    ret = text.getContent().trim();
                } else {
                    ret = idPage;
                }
            } else {
                System.out.println(
                        "Warning:" + WebContentManagement.class.getName()
                        + "::getText(" + idPage + "): Page not found in cache.");
            }
        } else {
            List<WebContentPersist> dbKeyList = new WebContentDomain().findWhereOrderBy(
                    "ID_PAGE='" + idPage + "' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ",
            "RECORD_DATE DESC");

            if (dbKeyList == null) {
                // Why would it make sense to retry a query that just failed?
                dbKeyList = new WebContentDomain().findWhereOrderBy(
                        "ID_PAGE='" + idPage + "' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ",
                "RECORD_DATE DESC");
            }

            if (!dbKeyList.isEmpty()) {
                htmlContent.put(idPage, dbKeyList.get(0));
                ret = dbKeyList.get(0).getContent();
            } else {
                System.out.println(
                        "Warning:" + WebContentManagement.class.getName()
                        + "::getText(" + idPage
                        + "): Page not found in cache and database.");
            }
        }
        return ret;
    }

    /**
     * Query the cache for a translation to the text.
     * TODO: The method is unfinished! It doesn't look in the database
     * if the language is different than English.
     *
     * @param idPage - key to look up in database - the full English phrase.
     */
    public String getTextByMD5(String idPage) {
        if (idPage == null) {
            return "";
        }
        String md5 = EunisUtil.digestHexDec(idPage, "MD5");

        String ret = idPage;

        idPage = idPage.trim();
        if (htmlContent.containsKey(md5)) {
            final WebContentPersist text = htmlContent.get(md5);

            if (null != text) {
                if (text.getContent() != null) {
                    ret = text.getContent().trim();
                } else {
                    ret = idPage;
                }
            } else {
                System.out.println(
                        "Warning:" + WebContentManagement.class.getName()
                        + "::getTextByMD5(" + idPage
                        + "): Page not found in cache.");
            }
        }
        return ret;
    }

    /**
     * Get the description of a HTML page.
     *
     * @param idPage - token to look up.
     * @return - the description.
     */
    public String getDescription(String idPage) {
        if (idPage == null) {
            return "";
        }
        String ret = idPage;

        idPage = idPage.trim();
        if (htmlContent.containsKey(idPage)) {
            final WebContentPersist text = htmlContent.get(idPage);

            if (null != text) {
                if (text.getDescription() != null) {
                    ret = text.getDescription().trim();
                } else {
                    ret = idPage;
                }
            } else {
                System.out.println(
                        "Warning:" + WebContentManagement.class.getName()
                        + "::getDescription(" + idPage
                        + "): Page not found in cache.");
            }
        } else {
            // Duplicate from line 297
            final List<WebContentPersist> dbKeyList = new WebContentDomain().findWhereOrderBy(
                    "ID_PAGE='" + idPage + "' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ",
            "RECORD_DATE DESC");

            if (!dbKeyList.isEmpty()) {
                htmlContent.put(idPage, dbKeyList.get(0));
                ret = dbKeyList.get(0).getDescription();
            } else {
                System.out.println(
                        "Warning:" + WebContentManagement.class.getName()
                        + "::getDescription(" + idPage
                        + "): Page not found in cache and database.");
            }
        }
        return ret;
    }

    /**
     * Checks if a page with a given id exists for the language.
     */
    public boolean idPageExists(String idPage, String language) {
        boolean ret = false;
        final List<WebContentPersist> dbKeyList = new WebContentDomain().findWhere(
                "ID_PAGE='" + idPage + "' AND LANG='" + language
                + "' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ");

        if (dbKeyList.size() > 0) {
            ret = true;
        }
        return ret;
    }

    /**
     * Same as getText(), but returns the result as WebContentPersist type.
     * This method should be called by getText().
     */
    public WebContentPersist getPersistentObject(String idPage) {
        WebContentPersist ret = null;

        if (idPage == null) {
            return null;
        }
        idPage = idPage.trim();
        if (htmlContent.containsKey(idPage)) {
            ret = htmlContent.get(idPage);
        } else {
            // Duplicate from line 297
            final List<WebContentPersist> dbKeyList = new WebContentDomain().findWhereOrderBy(
                    "ID_PAGE='" + idPage + "' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ",
            "RECORD_DATE DESC");

            if (!dbKeyList.isEmpty()) {
                htmlContent.put(idPage, dbKeyList.get(0));
                ret = dbKeyList.get(0);
            } else {
                System.out.println(
                        "Warning:" + WebContentManagement.class.getName()
                        + "::getPersistentObject(" + idPage
                        + "): Page not found in cache and database.");
            }
        }
        return ret;
    }

    /**
     * Retrieve always last version of english version of page content directly from database.
     * $MODULE$_$PAGE$_$PARAGRAPH$, for example: species_names-result_0 means first paragraph from
     * Species::Names::Results (species-names-result.jsp) page.<BR>
     *
     * To be used for the XLIFF generation? Why is it hardwired to English?
     *
     * @param idPage ID of the page to be retrieved (ID_PAGE from WEB_CONTENT table)
     * @return HTML content of the web page or if ID not found or exception ocurred the empty "" string.
     */
    public WebContentPersist getPageContentEnglish(final String idPage) {
        WebContentPersist ret = new WebContentPersist();

        try {
            final List<WebContentPersist> results = new WebContentDomain().findWhere(
                    "ID_PAGE='" + idPage
                    + "' AND LANG='en' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ORDER BY RECORD_DATE DESC");

            if (results.size() > 0) {
                ret = results.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * Get all the versions for an ID_PAGE. Always, first element is the most recent version.
     *
     * @param idPage
     */
    public List getIDPageVersions(String idPage, String language) {
        List ret = new ArrayList();

        try {
            ret = new WebContentDomain().findWhereOrderBy(
                    "ID_PAGE='" + idPage + "' AND LANG='" + language
                    + "' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ",
            "RECORD_DATE DESC");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * Write an edit crayon allowing editing of the HTML phrase.
     * @param idPage
     * @param showEditTag
     * @deprecated use cmsXXXX methods instead.
     */
    public String writeEditTag(final String idPage, boolean showEditTag) {
        if (!showEditTag) {
            return "";
        } else {
            return "<a title=\"Edit this text\" href=\"javascript:editContent('"
            + idPage + "');\">"
            + "<img src=\"images/edit-content.gif\" style=\"border : 0px;\" width=\"9\" height=\"9\" "
            + "alt=\"Edit this text\" "
            + "title=\"Edit this text\" /></a>";
        }
    }

    // public String writeEditTag( final String idPage ) {
    // return writeEditTag( idPage, editMode );
    // }

    /**
     * Save the modified HTML content.
     * If the language to save in is English, then mark the content invalid - meaning the translations have to be checked.
     *
     * @param modifyAllIdentical - Deletes the historic versions of the page.
     */
    public boolean savePageContentJDBC(String idPage,
                                       final String content,
                                       final String description,
                                       final String lang,
                                       final short contentLength,
                                       final String username,
                                       final boolean modifyAllIdentical) {
        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            if (modifyAllIdentical) {
                ps = con.prepareStatement(
                "DELETE FROM eunis_web_content WHERE ID_PAGE = ? AND LANG = ? ");
                ps.setString(1, idPage);
                ps.setString(2, lang);
                ps.execute();
                ps.close();
            }

            if (lang.equalsIgnoreCase("EN")) {
                ps = con.prepareStatement(
                "INSERT INTO eunis_web_content( ID_PAGE, CONTENT, DESCRIPTION, LANG, CONTENT_LENGTH, RECORD_AUTHOR, RECORD_DATE, CONTENT_VALID ) VALUES ( ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 0 )");
                ps.setString(1, idPage);
                ps.setString(2, content);
                ps.setString(3, description);
                ps.setString(4, lang);
                ps.setShort(5, contentLength);
                ps.setString(6, username);
            } else {
                ps = con.prepareStatement(
                "INSERT INTO eunis_web_content( ID_PAGE, CONTENT, DESCRIPTION, LANG, CONTENT_LENGTH, RECORD_AUTHOR, RECORD_DATE ) VALUES ( ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP )");
                ps.setString(1, idPage);
                ps.setString(2, content);
                ps.setString(3, description);
                ps.setString(4, lang);
                ps.setShort(5, contentLength);
                ps.setString(6, username);
            }
            ps.executeUpdate();
            ps.close();

            // Do not reload all language again, just modify the current key.
            // cacheHTMLContent( this.language );
            idPage = idPage.trim();
            if (htmlContent.containsKey(idPage)) {
                WebContentPersist data = htmlContent.get(idPage);

                data.setContent(content);
                data.setDescription(description);
                data.setContentLength(contentLength);
                htmlContent.remove(idPage);
                htmlContent.put(idPage, data);
            } else {
                System.out.println(
                        "savePageContentJDBC: Could not find in cache id_page= "
                        + idPage);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }
        return result;
    }

    public boolean insertContentJDBC(String idPage,
                                     final String content,
                                     final String description,
                                     final String lang,
                                     final String username,
                                     final boolean modifyAllIdentical) {
        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            if (modifyAllIdentical) {
                ps = con.prepareStatement(
                "DELETE FROM eunis_web_content WHERE ID_PAGE = MD5(?) AND LANG = ? ");
                ps.setString(1, idPage);
                ps.setString(2, lang);
                ps.execute();
                ps.close();
            }

            ps = con.prepareStatement(
            "INSERT INTO eunis_web_content( ID_PAGE, CONTENT, DESCRIPTION, LANG, RECORD_AUTHOR, RECORD_DATE ) VALUES ( MD5(?), ?, ?, ?, ?, CURRENT_TIMESTAMP )");

            ps.setString(1, idPage);
            ps.setString(2, content);
            ps.setString(3, description);
            ps.setString(4, lang);
            ps.setString(5, username);
            ps.executeUpdate();

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            SQLUtilities.closeAll(con, ps, null);
        }
        return result;
    }

    /**
     * Translate language code into language name in english. For example 'da' translates to 'Danish';
     *
     * @param code Code which will be decoded.
     * @return Decoded language or null if code is not found within chm62edt_language
     */
    public String translateLanguageCode(final String code) {
        String value = null;

        try {
            final List<Chm62edtLanguagePersist> items = new Chm62edtLanguageDomain().findWhere(
                    "CODE='" + code + "'");

            if (items.size() > 0) {
                final Chm62edtLanguagePersist languageName = items.get(0);

                value = languageName.getNameEn();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    /**
     * Retrieve the list of languages in which application is already translated.
     *
     * @return List of languages.
     */
    public List<EunisISOLanguagesPersist> getTranslatedLanguages() {
        final List<EunisISOLanguagesPersist> languages = new Vector<EunisISOLanguagesPersist>();

        try {
            final List<WebContentPersist> items = new WebContentDomain().findCustom(
            "SELECT * FROM eunis_web_content WHERE LANG_STATUS > 0 AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' GROUP BY LANG");

            // Decode the languages from chm62edt_language table
            for (WebContentPersist item : items) {
                final List<EunisISOLanguagesPersist> tmp = new EunisISOLanguagesDomain().findWhere(
                        "CODE = '" + item.getLang() + "'");

                if (tmp.size() > 0) {
                    languages.add(tmp.get(0));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println();
        }
        return languages;
    }

    /**
     * Find all available language into which EUNIS can be translated.
     * Formula is: SELECT CODE FROM chm62edt_language - ( SELECT DISTINCT(LANG) eunis_web_content ) )
     */
    public List<Chm62edtLanguagePersist> getAvailableLanguages() {
        List<Chm62edtLanguagePersist> languages = new Vector<Chm62edtLanguagePersist>();

        try {
            final List<Chm62edtLanguagePersist> all_languages = new Chm62edtLanguageDomain().findOrderBy(
            "NAME_EN");
            final List<WebContentPersist> translated_languages = new WebContentDomain().findCustom(
            "SELECT * FROM eunis_web_content WHERE CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' GROUP BY LANG");

            for (Chm62edtLanguagePersist language : all_languages) {
                boolean exists = false;

                for (WebContentPersist existing_lang : translated_languages) {
                    if (existing_lang.getLang().equalsIgnoreCase(
                            language.getCode())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    languages.add(language);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return languages;
    }

    /**
     * Add a new language for translation into the database.
     *
     * @param code Language code from chm62edt_language
     */
    public boolean addLanguage(final String code) throws Exception {
        boolean ret = false;

        if (code != null && !code.equalsIgnoreCase("en")) {
            // Check if languages is not already in database
            List<WebContentPersist> languages = new WebContentDomain().findWhere(
                    "LANG='" + code
                    + "' AND CONCAT(RECORD_DATE)<> '0000-00-00 00:00:00' ");

            if (languages.size() > 0) {
                throw new Exception("Language already added for translation.");
            }
            try {
                // System.out.println( "Saving new language" );
                final WebContentPersist row = new WebContentPersist();

                row.setIDPage("language_support");
                row.setContent("");
                row.setDescription("null");
                row.setLang(code);
                row.setLangStatus((short) 0);
                row.setContentLength((short) 0);
                row.setRecordDate(new Timestamp(new Date().getTime()));
                new WebContentDomain().save(row);
                ret = true;
            } catch (Exception ex) {
                ret = false;
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public String getLanguage() {
        return this.language;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public String getCurrentLanguage() {
        return language;
    }

    public boolean isAdvancedEditMode() {
        return advancedEditMode;
    }

    public void setAdvancedEditMode(boolean advancedEditMode) {
        this.advancedEditMode = advancedEditMode;
    }

    /**
     * Reads the URL and returns the content.
     * Pair id - HTTP status code, Pair value - response body.
     *
     * @param url url to check.
     * @return Pair id - HTTP status code, Pair value - response body.
     */
    public Pair<Integer, String> readContentFromUrl(String url) {
        Pair<Integer, String> result = null;

        if (StringUtils.isNotBlank(url)) {
            try {


                SSLContext sslContext = SSLContexts.custom()
                    .useTLS()
                    .build();

                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1.1", "TLSv1.2"},
                    null,
                    BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

                HttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(f).build();

                HttpGet get = new HttpGet(url);

                HttpResponse response = httpClient.execute(get);

                result = new Pair<>();
                result.setId(response.getStatusLine().getStatusCode());
                if (response.getStatusLine().getStatusCode() != 404) {
                    HttpEntity entity = response.getEntity();
                    if(entity != null) {
                        result.setValue(EntityUtils.toString(entity));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean importNewTexts(String description,
                                  String lang,
                                  String username) {
        Connection con = null;
        PreparedStatement ps = null;
        BufferedReader input = null;
        String DATA_FILE_NAME = "new_texts.txt";

        try {
            con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();

            InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                    DATA_FILE_NAME);

            input = new BufferedReader(new InputStreamReader(is));
            String line = null;

            List<String> new_ids = new ArrayList<String>();

            while ((line = input.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "|");

                if (st.countTokens() == 2) {
                    String text = st.nextToken();

                    if (!new_ids.contains(text)) {
                        ps = con.prepareStatement(
                        "INSERT INTO eunis_web_content( ID_PAGE, CONTENT, DESCRIPTION, LANG, RECORD_AUTHOR, RECORD_DATE ) VALUES ( MD5(?), ?, ?, ?, ?, CURRENT_TIMESTAMP )");
                        ps.setString(1, text);
                        ps.setString(2, text);
                        ps.setString(3, description);
                        ps.setString(4, lang);
                        ps.setString(5, username);

                        ps.executeUpdate();
                        new_ids.add(text);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            SQLUtilities.closeAll(con, ps, null);
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ignore) { }
        }

    }
}

