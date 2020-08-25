<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2006 EEA - European Environment Agency.
  - Description : Annex I habitat types tree
--%>
<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="/stripes/common/taglibs.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
%>
<%@ page import="ro.finsiel.eunis.WebContentManagement" %>
<%@ page import="ro.finsiel.eunis.search.Utilities" %>
<%@ page import="ro.finsiel.eunis.search.HabitatTree" %>
<%@ page import="ro.finsiel.eunis.search.HabitatTreeList" %>
<%@ page import="java.util.List" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session"/>
<%
    WebContentManagement cm = SessionManager.getWebContent();
    String eeaHome = application.getInitParameter("EEA_HOME");
    String btrail = "eea#" + eeaHome + ",home#index.jsp,habitat_types#habitats.jsp,habitats_annex1_tree_location";
%>
<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + cm.cms("habitats_annex1-browser_title") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="${title}" btrail="<%= btrail%>">
    <stripes:layout-component name="head">
        <link rel="StyleSheet" href="css/eunistree.css" type="text/css"/>
    </stripes:layout-component>
    <stripes:layout-component name="contents">
        <a name="documentContent"></a>
        <!-- MAIN CONTENT -->
        <h1>
            <%=cm.cmsPhrase("Habitat Annex I Directive hierarchical view: (higher levels are for grouping only)")%>
        </h1>

        <br/>
        <%
            try {
                String expand = Utilities.formatString(request.getParameter("expand"), "");

                HabitatTree root = Utilities.buildTree(expand, "ANNEX1");
                List<HabitatTreeList> habitats = Utilities.treeAsList(root, 1);

                String hide = cm.cmsPhrase("Hide sublevel habitat types");
                String show = cm.cmsPhrase("Show sublevel habitat types");

        %>
        <ul class="eunistree">
            <%
                for (int i = 0; i < habitats.size(); i++) {
                    HabitatTreeList h = habitats.get(i);

                    HabitatTreeList next = null;
                    if (i < habitats.size() - 1) {
                        next = habitats.get(i + 1);
                    }
            %>
            <li>
                <% if (next != null && next.getLevel() > h.getLevel()) { %>
                <a title="<%=hide%>" id="level_<%=h.getIdHabitat()%>" href="habitats-annex1-browser.jsp?expand=<%=Utilities.removeFromExpanded(expand,h.getIdHabitat())%>#level_<%=h.getIdHabitat()%>"><img src="images/img_minus.gif" alt="<%=hide%>"/></a>
                <%=h.getCode()%> : <%=h.getName()%>
                <% } else if (h.hasChildren()) { %>
                <a title="<%=show%>" id="level_<%=h.getIdHabitat()%>" href="habitats-annex1-browser.jsp?expand=<%=Utilities.addToExpanded(expand,h.getIdHabitat())%>#level_<%=h.getIdHabitat()%>"><img src="images/img_plus.gif" alt="<%=show%>"/></a>
                <%=h.getCode()%> : <%=h.getName()%>
                <% } else { %>

                <img src="images/img_bullet.gif" alt="<%=h.getName()%>"/>&nbsp;<a title="<%=h.getName()%>" href="habitats/<%=h.getIdHabitat()%>"><%=h.getCode()%> : <%=h.getName()%>
            </a>
                <%
                    }
                %>
                <br/>
                <%

                    if (h.isOpen()) {
                %>
                <ul class="eunistree">
                    <%
                        }
                        if (next != null && next.getLevel() < h.getLevel()) {
                            for (int j = next.getLevel(); j < h.getLevel(); j++) {
                    %>
                </ul>
                <%
                        }
                    }
                %>
            </li>
            <%
                }
            %>
        </ul>
        <%
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        %>
        <br/>
        <!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>