package ro.finsiel.eunis.dataimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

import ro.finsiel.eunis.session.SessionManager;
import ro.finsiel.eunis.utilities.SQLUtilities;

public class PostImportScripts extends HttpServlet {

    private static final long serialVersionUID = -6182473557022340518L;

    private static final Logger logger = Logger.getLogger(PostImportScripts.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String retPage = "dataimport/post-import.jsp";
        ArrayList<String> errors = new ArrayList<String>();

        HttpSession session = request.getSession(false);
        SessionManager sessionManager = (SessionManager) session.getAttribute(
        "SessionManager");

        if (sessionManager.isAuthenticated()
                && sessionManager.isImportExportData_RIGHT()) {
            String SQL_DRV = request.getSession().getServletContext().getInitParameter(
            "JDBC_DRV");
            String SQL_URL = request.getSession().getServletContext().getInitParameter(
            "JDBC_URL");
            String SQL_USR = request.getSession().getServletContext().getInitParameter(
            "JDBC_USR");
            String SQL_PWD = request.getSession().getServletContext().getInitParameter(
            "JDBC_PWD");

            String sites = request.getParameter("sites");
            String spiecesTab = request.getParameter("spiecesTab");
            String habitatsTab = request.getParameter("habitatsTab");
            String sitesTab = request.getParameter("sitesTab");
            String linkeddataTab = request.getParameter("linkeddataTab");
            String conservationstatusTab = request.getParameter("conservationstatusTab");
            String runBackground = request.getParameter("runBackground");

            try {
                if (runBackground != null && runBackground.equals("on")) {
                    SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
                    Scheduler sched = schedFact.getScheduler();

                    sched.start();

                    JobDetail jobDetail = new JobDetail("postImportSrciptsJob",
                            null, PostImportScriptsJob.class);

                    jobDetail.getJobDataMap().put("sqlDrv", SQL_DRV);
                    jobDetail.getJobDataMap().put("sqlUrl", SQL_URL);
                    jobDetail.getJobDataMap().put("sqlUsr", SQL_USR);
                    jobDetail.getJobDataMap().put("sqlPwd", SQL_PWD);

                    jobDetail.getJobDataMap().put("sites", sites);
                    jobDetail.getJobDataMap().put("spiecesTab", spiecesTab);
                    jobDetail.getJobDataMap().put("sitesTab", sitesTab);
                    jobDetail.getJobDataMap().put("habitatsTab", habitatsTab);
                    jobDetail.getJobDataMap().put("linkeddataTab", linkeddataTab);
                    jobDetail.getJobDataMap().put("conservationstatusTab", conservationstatusTab);

                    PostImportScriptsJobListener listener = new PostImportScriptsJobListener();

                    jobDetail.addJobListener(listener.getName());
                    sched.addJobListener(listener);

                    SimpleTrigger trigger = new SimpleTrigger(
                            jobDetail.getName(), null, new Date(), null, 0, 0L);

                    sched.scheduleJob(jobDetail, trigger);

                    retPage = "dataimport/import-log.jsp";
                } else {

                    SQLUtilities sql = new SQLUtilities();
                    sql.Init();

                    if (sites != null && sites.equals("on")) {
                        sql.runPostImportSitesScript(false);
                    }

                    TabScripts scripts = new TabScripts();

                    scripts.Init(false);

                    if (spiecesTab != null && spiecesTab.equals("on")) {
                        scripts.setTabSpecies();
                    }

                    if (habitatsTab != null && habitatsTab.equals("on")) {
                        scripts.setTabHabitats();
                    }

                    if (sitesTab != null && sitesTab.equals("on")) {
                        scripts.setTabSites();
                    }
                    if (linkeddataTab != null && linkeddataTab.equals("on")) {
                        scripts.setLinkedDataTab();
                    }
                    if (conservationstatusTab != null && conservationstatusTab.equals("on")) {
                        scripts.setConservationStatusTab();
                    }
                }

            } catch (Exception e) {
                logger.debug(e, e);
                errors.add(e.getMessage());
            }
            if (runBackground == null || !runBackground.equals("on")) {
                if (errors != null && errors.size() > 0) {
                    session.setAttribute("errors", errors);
                } else {
                    session.setAttribute("success", "Successfully finished!");
                }
            }
        }
        response.sendRedirect(retPage);
    }
}
