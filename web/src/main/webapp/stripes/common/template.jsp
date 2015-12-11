<%@ include file="/stripes/common/taglibs.jsp"%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />

<c:set var="hideMenu" value="true"/>

<stripes:layout-definition>
    <%--
      - Author(s) : The EUNIS Database Team.
      - Date :
      - Copyright : (c) 2002-2010 EEA - European Environment Agency.
      - Description : Template
    Parameters:
      pageTitle: the page title
      btrail: breadcrumbs trail (not displayed now)
      downloadLink: link for downloads, displayed in the header
      hideMenu: hides the right column menu
      pdfLink: link for downloadable PDF
    --%>
    <%@page contentType="text/html;charset=UTF-8"%>
    <%
        WebContentManagement cm = SessionManager.getWebContent();
    %>
<!DOCTYPE html>
<html>
    <head>
        <c:if test="${not empty actionBean.context.domainName}">
            <base href="${actionBean.context.domainName}/${base}"/>
        </c:if>
        <jsp:include page="/header-page.jsp">
            <jsp:param name="metaDescription" value="${eunis:replaceTags(actionBean.metaDescription)}" />
        </jsp:include>

        <title>
            <c:choose>
                <c:when test="${empty pageTitle}">EUNIS</c:when>
                <c:otherwise>${eunis:replaceTags(pageTitle)}</c:otherwise>
            </c:choose>
        </title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/eunis.css" />
        <script>
            // <![CDATA[
            // all the non-local links should display on a new tab; this script sets target=_blank for all non-local links
            $(document).ready(function() {
                $("a").each(function(){
                    h = $(this).attr("href");
                    if(h && h.indexOf("http") != -1 && h.indexOf("<%=application.getInitParameter("DOMAIN_NAME")%>") == -1) {
                        $(this).attr("target", "_blank");
                    }
                });
            });
            // ]]>
        </script>

        <stripes:layout-component name="head"/>
    </head>
    <body>
        <jsp:include page="/header.jsp" />
        <!-- visual portal wrapper -->

        <div id="visual-portal-wrapper">
            <!-- The wrapper div. It contains the two columns. -->
            <div id="portal-columns">
                <div id="portal-column-content" <c:if test="${empty hideMenu}">class="column-area"</c:if>>



                    <c:set var="currentTab" value="${fn:substringAfter(btrail, '#index.jsp,')}" />
                    <c:if test="${fn:contains(currentTab, '#')}">
                        <c:set var="currentTab" value="${fn:substringBefore(currentTab, '#')}" />
                    </c:if>

                    <c:if test="${empty currentTab}">
                        <c:set var="currentTab" value="index" />
                    </c:if>

                    <%--<script>  alert("${btrail}" + "  " + "${currentTab}"); </script>--%>

                    <!-- EUNIS MENU with EIONET MARKUP but EEA tabbedmenu style -->
                    <div class="tabbedmenu">
                          <ul>
                                  <li><a href="index.jsp" class="first-tab <c:if test = "${currentTab == 'index'}">current</c:if>">EUNIS Home</a></li>
                                  <li><a href="species.jsp" <c:if test = "${currentTab == 'species'}">class="current"</c:if> >Species</a></li>
                                  <li><a href="habitats.jsp" <c:if test = "${currentTab == 'habitat_types'}">class="current"</c:if> >Habitat types</a></li>
                                  <li><a href="sites.jsp" <c:if test = "${currentTab == 'sites'}">class="current"</c:if> >Sites</a></li>
                                  <%--<li><a href="combined-search.jsp" <c:if test = "${currentTab == 'combined_search'}">class="current"</c:if> >Combined search</a></li>--%>
                                  <li><a href="externalglobal" <c:if test = "${currentTab == 'externalglobal'}">class="current"</c:if> >Global queries</a></li>
                                  <li><a href="references" <c:if test = "${currentTab == 'references'}">class="current"</c:if> >References</a></li>
                                  <li><a href="about" class="last-tab <c:if test = "${currentTab == 'about_EUNIS_database'}">current</c:if>">About EUNIS</a></li>

                              <% if(SessionManager.isAuthenticated()) { %>
                                  <li style="float: right"><a href="index.jsp?operation=logout" id="siteaction-login_eea"><i class="eea-icon eea-icon-user"></i> <%= SessionManager.getUserFullName() %> <i class="eea-icon eea-icon-caret-down"></i></a></li>

                                  <div id="popup_login_form_eea" style="display:none;" class="callout">
                                         <p><a href="services.jsp">Services</a></p>
                                         <p><a href="index.jsp?operation=logout">Logout</a></p>
                                  </div>

                                  <script>var $popup_login_eea=$("#popup_login_form_eea");$("#siteaction-login_eea").click(function(e){$popup_login_eea.slideToggle(100);e.preventDefault()});</script>
                              <% } %>
                          </ul>
                     </div>

                     <div id="content" class="border-tabbedmenu">

                        <c:if test="${empty btrail}"><c:set var="btrail" value="${actionBean.btrail}"/> </c:if>
                        <c:if test="${not empty btrail}">
                            <jsp:include page="/header-dynamic.jsp">
                                <jsp:param name="location" value="${btrail}" />
                                <jsp:param name="downloadLink" value="${downloadLink}"/>
                                <jsp:param name="printLink" value="${pdfLink}"/>
                            </jsp:include>
                        </c:if>

                        <!-- MESSAGES -->
                        <stripes:layout-render name="/stripes/common/messages.jsp"/>


                        <!-- MAIN CONTENT -->
                        <stripes:layout-component name="contents"/>

                    </div>
                        <!--END content -->
                </div>

                <div class="visualClear"><!-- --></div>

                    <stripes:layout-component name="foot"/>
            </div>
                <!-- END column wrapper -->

        </div>
            <!-- END visual portal wrapper -->
        <jsp:include page="/footer-static.jsp" />
    </body>
</html>
</stripes:layout-definition>
