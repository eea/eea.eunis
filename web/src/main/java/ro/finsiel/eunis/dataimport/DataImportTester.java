package ro.finsiel.eunis.dataimport;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.xml.sax.SAXException;

import ro.finsiel.eunis.session.SessionManager;
import eionet.eunis.util.Constants;


public class DataImportTester extends HttpServlet {

    private static String BASE_DIR = "";

    /** The temporary dir where parts of the upload are stored temporary (if file size is bigger than memory buffer). */
    private static String TEMP_DIR = "temp";

    /** Maximum file size allowed to be uploaded to the server. */
    private static int MAX_FILE_SIZE = 512 * 1024 * 1024; // Default value

    /** Files with size smaller than this are written directly to disk, others through TEMP_DIR first. */
    private static int MAX_MEM_TRESHOLD = 4096; // Files smaller than 4k are directly written to disk

    private static List<String> errors = null;

    /**
     * Overrides public method doPost of javax.servlet.http.HttpServlet.
     * @param request Request object
     * @param response Response object.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String retPage = "dataimport/data-tester.jsp";

        HttpSession session = request.getSession(false);
        SessionManager sessionManager = (SessionManager) session.getAttribute(
                "SessionManager");

        if (sessionManager.isAuthenticated()
                && sessionManager.isImportExportData_RIGHT()) {
            errors = new ArrayList<String>();

            String SQL_DRV = request.getSession().getServletContext().getInitParameter(
                    "JDBC_DRV");
            String SQL_URL = request.getSession().getServletContext().getInitParameter(
                    "JDBC_URL");
            String SQL_USR = request.getSession().getServletContext().getInitParameter(
                    "JDBC_USR");
            String SQL_PWD = request.getSession().getServletContext().getInitParameter(
                    "JDBC_PWD");

            String SMTP_SERVER = request.getSession().getServletContext().getInitParameter(
                    "SMTP_SERVER");
            String SMTP_USERNAME = request.getSession().getServletContext().getInitParameter(
                    "SMTP_USERNAME");
            String SMTP_PASSWORD = request.getSession().getServletContext().getInitParameter(
                    "SMTP_PASSWORD");
            String SMTP_SENDER = request.getSession().getServletContext().getInitParameter(
                    "SMTP_SENDER");

            String table = "";
            String email = "";
            boolean runBackground = false;

            // Initialise the default settings
            try {
                BASE_DIR = getServletContext().getInitParameter(Constants.APP_HOME_INIT_PARAM);
                TEMP_DIR = BASE_DIR
                        + getServletContext().getInitParameter("TEMP_DIR");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            List items = new ArrayList();
            boolean isMultipart = FileUpload.isMultipartContent(request);
            DiskFileUpload upload = new DiskFileUpload();

            upload.setSizeThreshold(MAX_MEM_TRESHOLD);
            upload.setSizeMax(MAX_FILE_SIZE);
            upload.setRepositoryPath(TEMP_DIR);

            if (isMultipart) {
                try {
                    items = upload.parseRequest(request);
                } catch (FileUploadException ex) {
                    ex.printStackTrace();
                    errors.add(ex.getMessage());
                    response.sendRedirect("dataimport/data-tester.jsp");
                }

                for (int i = 0; i < items.size(); i++) {
                    FileItem item = (FileItem) items.get(i);

                    if (item.isFormField()) {
                        String fieldName = item.getFieldName();
                        String fieldValue = item.getString();

                        if (null != fieldName && fieldName.equals("table")) {
                            if (null != fieldValue && !fieldValue.equals("")) {
                                table = fieldValue;
                            } else {
                                errors.add("Please select table!");
                                break;
                            }
                        } else if (null != fieldName && fieldName.equals("mail")) {
                            if (null != fieldValue && !fieldValue.equals("")) {
                                email = fieldValue;
                            } else {
                                errors.add("Please insert e-mail address!");
                                break;
                            }
                        } else if (null != fieldName && fieldName.equals("back")) {
                            if (null != fieldValue && fieldValue.equals("on")) {
                                runBackground = true;
                            }
                        }
                    }
                }

                for (int i = 0; i < items.size(); i++) {
                    FileItem item = (FileItem) items.get(i);

                    if (!item.isFormField()) {
                        try {
                            File xmlFile = new File(TEMP_DIR + "xmlFile");

                            item.write(xmlFile);
                        } catch (SAXException _ex) {
                            _ex.printStackTrace();
                            errors.add(_ex.getMessage());
                        } catch (ParserConfigurationException _ex) {
                            _ex.printStackTrace();
                            errors.add(_ex.getMessage());
                        } catch (Exception _ex) {
                            _ex.printStackTrace();
                            errors.add(_ex.getMessage());
                        }

                        if (runBackground) {
                            try {
                                SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
                                Scheduler sched = schedFact.getScheduler();

                                sched.start();

                                JobDetail jobDetail = new JobDetail("testerJob",
                                        null, TesterJob.class);

                                jobDetail.getJobDataMap().put("sqlDrv", SQL_DRV);
                                jobDetail.getJobDataMap().put("sqlUrl", SQL_URL);
                                jobDetail.getJobDataMap().put("sqlUsr", SQL_USR);
                                jobDetail.getJobDataMap().put("sqlPwd", SQL_PWD);

                                jobDetail.getJobDataMap().put("smtpServer",
                                        SMTP_SERVER);
                                jobDetail.getJobDataMap().put("smtpUsername",
                                        SMTP_USERNAME);
                                jobDetail.getJobDataMap().put("smtpPassword",
                                        SMTP_PASSWORD);
                                jobDetail.getJobDataMap().put("smtpSender",
                                        SMTP_SENDER);

                                jobDetail.getJobDataMap().put("filePath",
                                        TEMP_DIR + "xmlFile");
                                jobDetail.getJobDataMap().put("table", table);
                                jobDetail.getJobDataMap().put("email", email);
                                jobDetail.getJobDataMap().put("errors", errors);

                                TesterJobListener listener = new TesterJobListener();

                                jobDetail.addJobListener(listener.getName());
                                sched.addJobListener(listener);

                                SimpleTrigger trigger = new SimpleTrigger(
                                        jobDetail.getName(), null, new Date(),
                                        null, 0, 0L);

                                sched.scheduleJob(jobDetail, trigger);

                                session.setAttribute("email", email);
                                retPage = "dataimport/background-tester-started.jsp";

                            } catch (SchedulerException se) {
                                // se.printStackTrace(); // preparing for decommissioning
                                errors.add(se.getMessage());
                            }

                        } else {
                            Tester tester = new Tester();

                            errors = tester.test(TEMP_DIR + "/xmlFile", table,
                                    SQL_DRV, SQL_URL, SQL_USR, SQL_PWD);
                        }
                    }
                }
                if (!runBackground) {
                    if (errors != null && errors.size() > 0) {
                        session.setAttribute("errors", errors);
                    } else {
                        session.setAttribute("success",
                                "Successfully tested! No errors found.");
                    }
                }
            }
        }

        response.sendRedirect(retPage);
    }

}
