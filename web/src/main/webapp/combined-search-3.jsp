<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Step 3 of 'Combined search' function - Selection of 3rd nature object filter.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ include file="/stripes/common/taglibs.jsp"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.sql.Connection,
                 java.sql.PreparedStatement,
                 java.sql.DriverManager,
                 java.sql.ResultSet,
                 ro.finsiel.eunis.WebContentManagement,
                 java.sql.Statement"%>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />

    <%
      WebContentManagement cm = SessionManager.getWebContent();
      String eeaHome = application.getInitParameter( "EEA_HOME" );
      String btrail = "eea#" + eeaHome + ",home#index.jsp,combined_search#combined-search.jsp,combined_search_location_3";
    %>

<c:set var="title" value='<%= application.getInitParameter("PAGE_TITLE") + (request.getParameter("firstnatureobject")!=null?request.getParameter("firstnatureobject"):"") + cm.cms("combined_search") %>'></c:set>

<stripes:layout-render name="/stripes/common/template.jsp" helpLink="combined-help.jsp" pageTitle="${title}" btrail="<%= btrail%>">
<stripes:layout-component name="head">

<script language="JavaScript" type="text/javascript">
//<![CDATA[
  var current_selected="";
  function MM_jumpMenu(targ,selObj,restore){ //v3.0
    eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
    if (restore) selObj.selectedIndex=0;
  }

  function setCurrentSelected(val) {
    current_selected = val;
    return true;
  }

  function choice(ctl, lov, natureobject, oper) {
    var cur_ctl = "window.document.criteria['"+ctl+"'].value";
    var val = eval(cur_ctl);
    URL = 'advanced-search-lov.jsp' + '?ctl=' + ctl + '&lov=' + lov + '&natureobject=' + natureobject + '&val=' + val + '&oper=' + oper;
    eval("page = window.open(URL, '', 'scrollbars=yes,toolbar=0, resizable=yes, location=0,width=400,height=500,left=490,top=0');");
  }

  function getkey(e)
  {
    if (window.event)
       return window.event.keyCode;
    else if (e)
       return e.which;
    else
       return null;
  }

  function textChanged(e)
  {
    var key, keychar;
    key = getkey(e);
    if (key == null) return true;

    // get character
    keychar = String.fromCharCode(key);
    keychar = keychar.toLowerCase();

    // control keys
    if ( key==null || key==0 || key==8 || key==9 || key==13 || key==27 ) {
      return false;
    }

    enableSaveButton();
    return true;
  }

  // action specifies what to do (how to modify the submited url...
  function submitCriteriaForm(criteria, idnode) {
<%--    alert("criteria=" + criteria.value + ", idnode=" + idnode);--%>
    document.criteria.criteria.value=criteria.value;
    document.criteria.attribute.value="";
    document.criteria.operator.value="";
    document.criteria.firstvalue.value="";
    document.criteria.lastvalue.value="";
    document.criteria.idnode.value=idnode;
    document.criteria.action.value="";
    document.criteria.submit();
  }

  function enableSaveButton() {
    document.criteria.Save.disabled=false;
    document.criteria.Search.disabled=true;
    document.getElementById("status").innerHTML="<span style=\"color:Red\"><%=cm.cms("press_save_to_save_criteria")%></span>"
  }

  function disableSaveButton() {
    document.criteria.Save.disabled=true;
    document.criteria.Search.disabled=false;
    document.getElementById("status").innerHTML="<span style=\"color:Red\"><%=cm.cms("criteria_saved")%></span>"
  }

  function submitAttributeForm(attribute, idnode) {
    document.criteria.criteria.value="";
    document.criteria.attribute.value=attribute.value;
    document.criteria.operator.value="";
    document.criteria.firstvalue.value="";
    document.criteria.lastvalue.value="";
    document.criteria.idnode.value=idnode;
    document.criteria.action.value="";
    document.criteria.submit();
  }

  function submitOperatorForm(operator, idnode) {
    document.criteria.criteria.value="";
    document.criteria.attribute.value="";
    document.criteria.operator.value=operator.value;
    document.criteria.firstvalue.value="";
    document.criteria.lastvalue.value="";
    document.criteria.idnode.value=idnode;
    document.criteria.action.value="";
    document.criteria.submit();
  }

  function submitFirstValueForm(firstvalue, idnode) {
    if(firstvalue.value == "") {
      firstvalue.value = document.criteria.oldfirstvalue.value;
      alert('<%=cm.cmsPhrase("Previous values were restored")%>');
      firstvalue.focus();
		  return(false);
    }

    document.criteria.criteria.value="";
    document.criteria.attribute.value="";
    document.criteria.operator.value="";
    document.criteria.firstvalue.value=firstvalue.value;
    document.criteria.lastvalue.value="";
    document.criteria.idnode.value=idnode;
    document.criteria.action.value="";
    var ofv = document.criteria.oldfirstvalue.value;
    var fv = document.criteria.firstvalue.value;
    if(ofv != fv) {
      if(current_selected == "first_binocular") {
        var lov="";
        var natureobject="<%=request.getParameter("natureobject")%>";
        var oper="";
        lov = eval("window.document.criteria['Attribute"+idnode+"'].value");
        choice("First_Value"+idnode, lov, natureobject, oper);
      }
      document.criteria.submit();
    }
  }

  function submitLastValueForm(lastvalue, idnode) {
    if(lastvalue.value == "") {
      lastvalue.value = document.criteria.oldlastvalue.value;
      alert('<%=cm.cmsPhrase("Previous values were restored")%>');
      firstvalue.focus();
		  return false;
    }

    document.criteria.criteria.value="";
    document.criteria.attribute.value="";
    document.criteria.operator.value="";
    document.criteria.firstvalue.value="";
    document.criteria.lastvalue.value=lastvalue.value;
    document.criteria.idnode.value=idnode;
    document.criteria.action.value="";
    var olv = document.criteria.oldlastvalue.value;
    var lv = document.criteria.lastvalue.value;
    if(olv != lv) {
      if(current_selected == "last_binocular") {
        var lov="";
        var natureobject="<%=request.getParameter("natureobject")%>";
        var oper="";
        lov = eval("window.document.criteria['Attribute"+idnode+"'].value");
        choice("Last_Value"+idnode, lov, natureobject, oper);
      }
      document.criteria.submit();
    }
  }

  function submitButtonForm(action, idnode) {
    document.criteria.criteria.value="";
    document.criteria.attribute.value="";
    document.criteria.operator.value="";
    document.criteria.firstvalue.value="";
    document.criteria.lastvalue.value="";
    document.criteria.idnode.value=idnode;
    document.criteria.action.value=action;
    document.criteria.submit();
  }

  function saveFirstValue(val) {
    document.criteria.oldfirstvalue.value=val.value;
  }

  function saveLastValue(val) {
    document.criteria.oldlastvalue.value=val.value;
  }

  function submitClearData() {
    document.criteria.criteria.value="";
    document.criteria.attribute.value="";
    document.criteria.operator.value="";
    document.criteria.firstvalue.value="";
    document.criteria.lastvalue.value="";
    document.criteria.idnode.value="";
    document.criteria.action.value="clear";
    document.criteria.submit();
  }

  function submitKeepData(natureobject) {
    document.criteria.criteria.value="";
    document.criteria.attribute.value="";
    document.criteria.operator.value="";
    document.criteria.firstvalue.value="";
    document.criteria.lastvalue.value="";
    document.criteria.idnode.value="";
    if (document.criteria.natureobject != null && natureobject != null)
       document.criteria.natureobject.value=natureobject;
    document.criteria.action.value="keep";
    document.criteria.submit();
  }

  function changeNatureObject(action) {
   document.criteria.action.value=action;
    document.criteria.submit();
  }
//]]>
</script>

</stripes:layout-component>
<stripes:layout-component name="contents">
                <a name="documentContent"></a>
                <h1><%=cm.cmsPhrase("Combined search")%></h1>
<!-- MAIN CONTENT -->
                <%=cm.cmsPhrase(">Search information using multiple characteristics")%>
                  <br />
                  <br />
                  <table summary="layout" border="0">
                    <tr>
                      <td id="status">
                        <%=cm.cmsPhrase("Specify the search criteria:")%>
                      </td>
                    </tr>
                  </table>
                  <br />
              <%
                if(request.getParameter("combinednatureobject1")!=null && SessionManager.getCombinednatureobject1()==null) {
                  SessionManager.setCombinednatureobject1(request.getParameter("combinednatureobject1"));
                  SessionManager.setCombinedlistcriteria1(request.getParameter("combinedlistcriteria1"));
                  SessionManager.setCombinedexplainedcriteria1(request.getParameter("combinedexplainedcriteria1"));
                }

                if(request.getParameter("combinednatureobject2")!=null && SessionManager.getCombinednatureobject2()==null) {
                  SessionManager.setCombinednatureobject2(request.getParameter("combinednatureobject2"));
                  SessionManager.setCombinedlistcriteria2(request.getParameter("combinedlistcriteria2"));
                  SessionManager.setCombinedexplainedcriteria2(request.getParameter("combinedexplainedcriteria2"));
                }
                if(request.getParameter("sourcedbcriteria")!=null && SessionManager.getSourcedb()==null) {
                  SessionManager.setSourcedb(request.getParameter("sourcedbcriteria"));
                }

                String combinednatureobject3="";
                String combinedlistcriteria3="";
                String combinedexplainedcriteria3="";
                String combinedcombinationtype="";
                String sourcedbcriteria="";

                String IdSession = request.getParameter("idsession");
                String NatureObject = request.getParameter("natureobject");
                String FirstNatureObject = request.getParameter("firstnatureobject");
                String SecondNatureObject = request.getParameter("secondnatureobject");
                String skip = request.getParameter("skip");
                if(skip == null || skip.length()==0 || skip.equalsIgnoreCase("undefined")) {
                  skip = "";
                }
//  System.out.println("skip = " + skip);

//  System.out.println("NatureObject = " + NatureObject);
                if(IdSession == null || IdSession.length()==0 || IdSession.equalsIgnoreCase("undefined")) {
                  IdSession=request.getSession().getId();
                }
                if(NatureObject == null || NatureObject.length()==0 || NatureObject.equalsIgnoreCase("undefined")) {
                  System.out.println("No nature object found - Default to Sites");
                  FirstNatureObject="Species";
                  NatureObject="Sites";
                  SecondNatureObject="Habitat";
                }

                String SourceDB = "";
                if(request.getParameter("DIPLOMA")!=null) SourceDB+=",'DIPLOMA'";
                if(request.getParameter("CDDA_NATIONAL")!=null) SourceDB+=",'CDDA_NATIONAL'";
                if(request.getParameter("CDDA_INTERNATIONAL")!=null) SourceDB+=",'CDDA_INTERNATIONAL'";
                if(request.getParameter("BIOGENETIC")!=null) SourceDB+=",'BIOGENETIC'";
                if(request.getParameter("NATURA2000")!=null) SourceDB+=",'NATURA2000'";
                if(request.getParameter("NATURENET")!=null) SourceDB+=",'NATURENET'";
                if(request.getParameter("EMERALD")!=null) SourceDB+=",'EMERALD'";
                if(request.getParameter("CORINE")!=null) SourceDB+=",'CORINE'";

                if(SourceDB.equalsIgnoreCase("")) {
                  SourceDB = "''";
                } else {
                  if(SourceDB.length()>1) {
                    SourceDB = SourceDB.substring(1,SourceDB.length());
                  }
                }
                sourcedbcriteria = SourceDB;

                String SearchType = "";
                if(request.getParameter("SearchType")!=null) SearchType=request.getParameter("SearchType");
              %>
              <%
              if(!skip.equalsIgnoreCase(NatureObject)) {
              %>
              <form method="post" action="combined-search-3.jsp" name="criteria">
              <strong><%=cm.cmsPhrase("Step 3.")%></strong>
              <%
                if(FirstNatureObject.equalsIgnoreCase("Species")) {
                  %>
                  <%=cm.cmsPhrase("Species will be related to Sites meeting the following criteria:")%>
                  <%
                }
                if(FirstNatureObject.equalsIgnoreCase("Habitat")) {
                  %>
                  <%=cm.cmsPhrase("Habitat types will be related to Species meeting the following criteria:")%>
                  <%
                }
                if(FirstNatureObject.equalsIgnoreCase("Sites")) {
                  %>
                  <%=cm.cmsPhrase("Sites will be related to Habitat types meeting the following criteria:")%>
                  <%
                }
              %>
              <hr width="100%" size="1" align="left" />
              <input type="hidden" name="criteria" value="" />
              <input type="hidden" name="attribute" value="" />
              <input type="hidden" name="operator" value="" />
              <input type="hidden" name="firstvalue" value="" />
              <input type="hidden" name="lastvalue" value="" />
              <input type="hidden" name="oldfirstvalue" value="" />
              <input type="hidden" name="oldlastvalue" value="" />
              <input type="hidden" name="action" value="" />
              <input type="hidden" name="idnode" value="" />
              <input type="hidden" name="natureobject" value="<%=NatureObject%>" />
              <input type="hidden" name="firstnatureobject" value="<%=FirstNatureObject%>" />
              <input type="hidden" name="secondnatureobject" value="<%=SecondNatureObject%>" />
              <input type="hidden" name="idsession" value="<%=IdSession%>" />
              <input type="hidden" name="skip" value="<%=skip%>" />
              <%
              }
              %>
              <%
//  System.out.println("NatureObject = " + NatureObject);
//  System.out.println("IdSession = " + IdSession);
                int SQL_LIMIT = Integer.parseInt(application.getInitParameter("SQL_LIMIT"));

                //Utilities.dumpRequestParams(request);
                String p_action = request.getParameter("action");
                if(p_action==null) p_action="";
                String p_idnode = request.getParameter("idnode");
                if(p_idnode==null) p_idnode="";
                String p_criteria = request.getParameter("criteria");
                if(p_criteria==null) p_criteria="";
                String p_attribute = request.getParameter("attribute");
                if(p_attribute==null) p_attribute="";
                String p_operator = request.getParameter("operator");
                if(p_operator==null) p_operator="";
                String p_firstvalue = request.getParameter("firstvalue");
                if(p_firstvalue==null) p_firstvalue="";
                String p_lastvalue = request.getParameter("lastvalue");
                if(p_lastvalue==null) p_lastvalue="";
                //System.out.println(requestURL);

                ro.finsiel.eunis.search.CombinedSearch tas = new ro.finsiel.eunis.search.CombinedSearch();
                tas.SetSQLLimit(SQL_LIMIT);


                //prelucram actiunea curenta
                if(p_action.equalsIgnoreCase("reset")) {
                  ro.finsiel.eunis.search.CombinedSearch tsas;
                  tsas = new ro.finsiel.eunis.search.CombinedSearch();
                  tsas.SetSQLLimit(SQL_LIMIT);
                  String attribute="ScientificName";
                  if(NatureObject.equalsIgnoreCase("Sites")) {
                    attribute="Name";
                  }
                  tsas.DeleteRoot(IdSession,NatureObject,attribute);
                }

                if(p_action.equalsIgnoreCase("deleteroot")) {
                  //out.println("Delete root");
                  String attribute="ScientificName";
                  if(NatureObject.equalsIgnoreCase("Sites")) {
                    attribute="Name";
                  }
                  if(!tas.DeleteRootNoInitialize(IdSession,NatureObject,attribute)) {
                    System.out.println("Error deleting root!");
                    %>
                      <script language="JavaScript" type="text/javascript">
                      //<![CDATA[
                        alert('<%=cm.cms("error_deleting_root")%>');
                      //]]>
                      </script>
                    <%
                  }
                }

                if(p_action.equalsIgnoreCase("addroot")) {
                  //out.println("Add root");
                  String attribute="ScientificName";
                  if(NatureObject.equalsIgnoreCase("Sites")) {
                    attribute="Name";
                  }
                  tas.CreateInitialBranch(IdSession,NatureObject,attribute);
                }

                if(p_action.equalsIgnoreCase("add")) {
                  //out.println("Add branch for node: "+p_idnode);
                  String attribute="ScientificName";
                  if(NatureObject.equalsIgnoreCase("Sites")) {
                    attribute="Name";
                  }
                  if(!tas.InsertBranch(p_idnode,IdSession,NatureObject,attribute)) {
                   System.out.println("Error adding branch!");
                   %>
                     <script language="JavaScript" type="text/javascript">
                     //<![CDATA[
                       alert('<%=cm.cms("error_adding_branch")%>');
                     //]]>
                     </script>
                   <%
                  }
                }

                if(p_action.equalsIgnoreCase("delete")) {
                  //out.println("Delete branch for node: "+p_idnode);
                  if(!tas.DeleteBranch(p_idnode,IdSession,NatureObject)) {
                   System.out.println("Error deleting branch!");
                   %>
                     <script language="JavaScript" type="text/javascript">
                     //<![CDATA[
                       alert('<%=cm.cms("error_deleting_branch")%>');
                     //]]>
                     </script>
                   <%
                  }
                }

                if(p_action.equalsIgnoreCase("compose")) {
                  //out.println("Compose branch for node: "+p_idnode);
                  if(!tas.ComposeBranch(p_idnode,IdSession,NatureObject)) {
                   System.out.println("Error composing branch!");
                   %>
                     <script language="JavaScript" type="text/javascript">
                     //<![CDATA[
                       alert('<%=cm.cms("error_composing_branch")%>');
                     //]]>
                     </script>
                   <%
                  }
                }

                if(p_action.length()==0) {
                  if(p_criteria.length() != 0) {
                    //out.println("New criteria: "+p_criteria+" for node: "+p_idnode);
                    tas.ChangeCriteria(p_idnode,IdSession,NatureObject,p_criteria);
                  }
                  if(p_attribute.length() != 0) {
                    //out.println("New attribute: "+p_attribute+" for node: "+p_idnode);
                    tas.ChangeAttribute(p_idnode,IdSession,NatureObject,p_attribute);
                  }
                  if(p_operator.length() != 0) {
                    //out.println("New operator: "+p_operator+" for node: "+p_idnode);
                    tas.ChangeOperator(p_idnode,IdSession,NatureObject,p_operator);
                  }
                  if(p_firstvalue.length() != 0) {
                    //out.println("New first value: "+p_firstvalue+" for node: "+p_idnode);
                    tas.ChangeFirstValue(p_idnode,IdSession,NatureObject,p_firstvalue);
                  }
                  if(p_lastvalue.length() != 0) {
                    //out.println("New last value: "+p_lastvalue+" for node: "+p_idnode);
                    tas.ChangeLastValue(p_idnode,IdSession,NatureObject,p_lastvalue);
                  }
                }

                String SQL="";
                String NodeType="";
                String IdNode="";
                String val="";
                String selected="";
                String currentAttribute="";
                String currentOperator="";
                String currentValue="";
                int bResults=0;

                Connection con = null;
                PreparedStatement ps = null;
                Statement ps_exec = null;
                ResultSet rs =null;

                try {
                  
                  con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                }
                catch(Exception e) {

                  return;
                }

              if(!skip.equalsIgnoreCase(NatureObject)) {
                SQL="SELECT ";
                SQL+="eunis_combined_search.ID_NODE,";
                SQL+="eunis_combined_search.NODE_TYPE,";
                SQL+="eunis_combined_search_criteria.ATTRIBUTE,";
                SQL+="eunis_combined_search_criteria.OPERATOR,";
                SQL+="eunis_combined_search_criteria.FIRST_VALUE,";
                SQL+="eunis_combined_search_criteria.LAST_VALUE ";
                SQL+="FROM ";
                SQL+="eunis_combined_search ";
                SQL+="LEFT OUTER JOIN eunis_combined_search_criteria ON (eunis_combined_search.ID_SESSION = eunis_combined_search_criteria.ID_SESSION) AND (eunis_combined_search.NATURE_OBJECT = eunis_combined_search_criteria.NATURE_OBJECT) AND (eunis_combined_search.ID_NODE = eunis_combined_search_criteria.ID_NODE) ";
                SQL+="WHERE (eunis_combined_search.ID_SESSION='"+IdSession+"') ";
                SQL+="AND (eunis_combined_search.NATURE_OBJECT='"+NatureObject+"') ";
                SQL+="ORDER BY ";
                SQL+="eunis_combined_search.ID_NODE ";

                ps = con.prepareStatement(SQL);
                rs = ps.executeQuery();
                if(rs.isBeforeFirst()){
                //if(1==1){
                  while(rs.next()) {
                    IdNode=rs.getString("ID_NODE");
                    NodeType=rs.getString("NODE_TYPE");
                    if(!IdNode.equalsIgnoreCase("0")) {out.println("&nbsp;");out.println("&nbsp;");}
                    for(int i=1;i<=IdNode.length()*3;i++) { if(!IdNode.equalsIgnoreCase("0")) { out.println("&nbsp;"); } };

                    if(!IdNode.equalsIgnoreCase("0")) {
                      if(IdNode.length()<=3) {
                        %>
                        <a title="<%=cm.cms("add_criterion")%>" href="javascript:submitButtonForm('add','<%=IdNode%>');"><img alt="<%=cm.cms("add_criterion")%>" border="0" src="images/mini/add.gif" width="13" height="13" title="<%=cm.cms("add_criterion")%>" /></a><%=cm.cmsTitle("add_criterion")%>
                        <%
                      }
                      if(IdNode.equalsIgnoreCase("1")) {
                        %>
                        <img alt="" border="0" src="images/mini/space.gif" />
                        <%
                      } else {
                      %>
                        <a title="<%=cm.cms("delete_criterion")%>" href="javascript:submitButtonForm('delete','<%=IdNode%>');"><img border="0" src="images/mini/delete.gif" alt="<%=cm.cms("delete_criterion")%>" width="13" height="13" title="<%=cm.cms("delete_criterion")%>" /></a><%=cm.cmsTitle("delete_criterion")%>
                      <%
                      }
                      if(IdNode.length()<3) {
                        if(NodeType.equalsIgnoreCase("Criteria")) {
                        %>
                          <a title="<%=cm.cms("compose_criterion")%>" href="javascript:submitButtonForm('compose','<%=IdNode%>');"><img alt="<%=cm.cms("compose_criterion")%>" border="0" src="images/mini/compose.gif" width="13" height="13" title="<%=cm.cms("compose_criterion")%>" /></a><%=cm.cmsTitle("compose_criterion")%>
                        <%
                        }
                      }
                      out.println(" "+IdNode);
                    } else {
                      %>
                      <a title="<%=cm.cms("delete_root_criterion")%>" href="javascript:submitButtonForm('deleteroot','<%=IdNode%>');"><img alt="<%=cm.cms("delete_root_criterion")%>" border="0" src="images/mini/delete.gif" width="13" height="13" title="<%=cm.cms("delete_root_criterion")%>" /></a><%=cm.cmsTitle("delete_root_criterion")%>
                      <%
                    }

                    String cmsCriteria = cm.cmsPhrase("Criteria");
                    String cmsAttribute = cm.cms("advanced_attribute");
                    String cmsOperator = cm.cmsPhrase("Operator");
                    String cmsAll = cm.cms("all");
                    String cmsAny = cm.cms("any");
                    String cmsFollowingCriteria = cm.cms("of_following_criteria_are_met");

                    if (!NodeType.equalsIgnoreCase("Criteria")) {
                      out.println("<label for=\"Criteria" + IdNode + "\" class=\"noshow\">"+cmsCriteria+"</label>");
                      out.println("<select name=\"Criteria" + IdNode + "\" onchange=\"submitCriteriaForm(this,'" + IdNode + "','" + IdSession + "','" + NatureObject + "')\" title=\""+cmsCriteria+"\" id=\"Criteria"+ IdNode + "\">");
                      if (NodeType.equalsIgnoreCase("All")) {
                        selected = " selected=\"selected\"";
                      } else {
                        selected = "";
                      }
                      out.println("<option" + selected + " value=\"All\">"+cmsAll+"</option>");
                      if (NodeType.equalsIgnoreCase("Any")) {
                        selected = " selected=\"selected\"";
                      } else {
                        selected = "";
                      }
                      out.println("<option" + selected + " value=\"Any\">"+cmsAny+"</option>");
                      out.println("</select> " + cmsFollowingCriteria + ":");
                      out.println("<br />");
                    } else {
                      val = rs.getString("ATTRIBUTE");
                      currentAttribute = val;
                      out.println("<label for=\"Attribute" + IdNode + "\" class=\"noshow\">"+cmsAttribute+"</label>");
                      out.println("<select name=\"Attribute" + IdNode + "\" onchange=\"submitAttributeForm(this,'" + IdNode + "','" + IdSession + "','" + NatureObject + "')\" title=\""+cmsAttribute+"\" id=\"Attribute" + IdNode + "\">");

                      if (NatureObject.equalsIgnoreCase("Habitat")) {
                        if (val.equalsIgnoreCase("ScientificName")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"ScientificName\">" + cm.cms("name") + "</option>");
                        if (val.equalsIgnoreCase("Code")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Code\">" + cm.cms("code_eunis_annex") + "</option>");
                        if (val.equalsIgnoreCase("LegalInstruments")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"LegalInstruments\">" + cm.cms("legal_instruments") + "</option>");
                        if (val.equalsIgnoreCase("SourceDatabase")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"SourceDatabase\">" + cm.cms("source_database") + "</option>");
                        if (val.equalsIgnoreCase("Country")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Country\">" + cm.cmsPhrase("Country") + "</option>");
                        if (val.equalsIgnoreCase("Biogeoregion")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Biogeoregion\">" + cm.cms("biogeographic_region") + "</option>");
                        if (val.equalsIgnoreCase("Author")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Author\">" + cm.cms("reference_author") + "</option>");
                        if (val.equalsIgnoreCase("Title")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Title\">" + cm.cms("reference_title") + "</option>");
                        if (val.equalsIgnoreCase("Altitude")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Altitude\">" + cm.cms("altitude") + "</option>");
                        if (val.equalsIgnoreCase("Chemistry")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Chemistry\">" + cm.cms("chemistry") + "</option>");
                        if (val.equalsIgnoreCase("Climate")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Climate\">" + cm.cms("climate") + "</option>");
                        if (val.equalsIgnoreCase("Cover")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Cover\">" + cm.cms("cover") + "</option>");
                        if (val.equalsIgnoreCase("Depth")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Depth\">" + cm.cms("depth") + "</option>");
                        if (val.equalsIgnoreCase("Geomorph")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Geomorph\">" + cm.cms("geomorph") + "</option>");
                        if (val.equalsIgnoreCase("Humidity")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Humidity\">" + cm.cms("humidity") + "</option>");
                        if (val.equalsIgnoreCase("LifeForm")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"LifeForm\">" + cm.cms("life_form") + "</option>");
                        if (val.equalsIgnoreCase("LightIntensity")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"LightIntensity\">" + cm.cms("light_intensity") + "</option>");
                        if (val.equalsIgnoreCase("Salinity")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Salinity\">" + cm.cms("salinity") + "</option>");
                        if (val.equalsIgnoreCase("Spatial")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Spatial\">" + cm.cms("spatial") + "</option>");
                        if (val.equalsIgnoreCase("Substrate")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Substrate\">" + cm.cms("substrate") + "</option>");
                        if (val.equalsIgnoreCase("Temporal")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Temporal\">" + cm.cms("temporal") + "</option>");
                        if (val.equalsIgnoreCase("Water")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Water\">" + cm.cms("water") + "</option>");
                        if (val.equalsIgnoreCase("Usage")) { selected = " selected=\"selected\""; } else { selected = ""; }
                        out.println("<option" + selected + " value=\"Usage\">" + cm.cms("usage") + "</option>");
                        out.println("</select>");
                        %>
                        <%=cm.cmsInput("name")%>
                        <%=cm.cmsInput("code_eunis_annex")%>
                        <%=cm.cmsInput("legal_instruments")%>
                        <%=cm.cmsInput("source_database")%>
                        <%=cm.cmsInput("biogeographic_region")%>
                        <%=cm.cmsInput("reference_author")%>
                        <%=cm.cmsInput("reference_title")%>
                        <%=cm.cmsInput("altitude")%>
                        <%=cm.cmsInput("chemistry")%>
                        <%=cm.cmsInput("climate")%>
                        <%=cm.cmsInput("cover")%>
                        <%=cm.cmsInput("depth")%>
                        <%=cm.cmsInput("geomorph")%>
                        <%=cm.cmsInput("humidity")%>
                        <%=cm.cmsInput("life_form")%>
                        <%=cm.cmsInput("light_intensity")%>
                        <%=cm.cmsInput("marine")%>
                        <%=cm.cmsInput("salinity")%>
                        <%=cm.cmsInput("spatial")%>
                        <%=cm.cmsInput("substrate")%>
                        <%=cm.cmsInput("temporal")%>
                        <%=cm.cmsInput("tidal")%>
                        <%=cm.cmsInput("water")%>
                        <%=cm.cmsInput("usage")%>
                        <%
                      } else if(NatureObject.equalsIgnoreCase("Species")) {
                        if(val.equalsIgnoreCase("ScientificName")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"ScientificName\">"+cm.cmsPhrase("Scientific name")+"</option>");
                        if(val.equalsIgnoreCase("VernacularName")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"VernacularName\">"+cm.cms("vernacular_name")+"</option>");
                        if(val.equalsIgnoreCase("Group")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Group\">"+cm.cmsPhrase("Group")+"</option>");
                        if(val.equalsIgnoreCase("ThreatStatus")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"ThreatStatus\">"+cm.cms("threat_status")+"</option>");
                        if(val.equalsIgnoreCase("InternationalThreatStatus")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"InternationalThreatStatus\">"+cm.cms("international_threat_status")+"</option>");
                        if(val.equalsIgnoreCase("Country")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Country\">"+cm.cmsPhrase("Country")+"</option>");
                        if(val.equalsIgnoreCase("Biogeoregion")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Biogeoregion\">"+cm.cms("biogeoregion")+"</option>");
                        if(val.equalsIgnoreCase("Author")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Author\">"+cm.cms("reference_author")+"</option>");
                        if(val.equalsIgnoreCase("Title")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Title\">"+cm.cms("reference_title")+"</option>");
                        if(val.equalsIgnoreCase("LegalInstrument")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"LegalInstrument\">"+cm.cms("species_advanced_19")+"</option>");
                        if(val.equalsIgnoreCase("Taxonomy")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Taxonomy\">"+cm.cms("taxonomy")+"</option>");
                        if(val.equalsIgnoreCase("Abundance")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Abundance\">"+cm.cms("abundance")+"</option>");
                        if(val.equalsIgnoreCase("Trend")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Trend\">"+cm.cms("trend")+"</option>");
                        if(val.equalsIgnoreCase("DistributionStatus")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"DistributionStatus\">"+cm.cms("distribution_status")+"</option>");
                        out.println("</select>");
                        %>
                        <%=cm.cmsInput("vernacular_name")%>
                        <%=cm.cmsInput("threat_status")%>
                        <%=cm.cmsInput("international_threat_status")%>
                        <%=cm.cmsInput("biogeoregion")%>
                        <%=cm.cmsInput("reference_author")%>
                        <%=cm.cmsInput("reference_title")%>
                        <%=cm.cmsInput("species_advanced_19")%>
                        <%=cm.cmsInput("taxonomy")%>
                        <%=cm.cmsInput("abundance")%>
                        <%=cm.cmsInput("trend")%>
                        <%=cm.cmsInput("distribution_status")%>
                        <%
                      } else if(NatureObject.equalsIgnoreCase("Sites")) {
                        if(val.equalsIgnoreCase("Name")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Name\">"+cm.cms("name")+"</option>");
                        if(val.equalsIgnoreCase("Code")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Code\">"+cm.cms("code_column")+"</option>");
                        if(val.equalsIgnoreCase("DesignationYear")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"DesignationYear\">"+cm.cms("designation_year")+"</option>");
                        if(val.equalsIgnoreCase("Country")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Country\">"+cm.cmsPhrase("Country")+"</option>");
                        if(val.equalsIgnoreCase("Size")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Size\">"+cm.cms("size")+"</option>");
                        if(val.equalsIgnoreCase("Longitude")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Longitude\">"+cm.cms("longitude")+"</option>");
                        if(val.equalsIgnoreCase("Latitude")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Latitude\">"+cm.cms("latitude")+"</option>");
                        if(val.equalsIgnoreCase("MinimumAltitude")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"MinimumAltitude\">"+cm.cms("min_altitude")+"</option>");
                        if(val.equalsIgnoreCase("MaximumAltitude")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"MaximumAltitude\">"+cm.cms("max_altitude")+"</option>");
                        if(val.equalsIgnoreCase("MeanAltitude")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"MeanAltitude\">"+cm.cms("mean_altitude")+"</option>");
                        if(val.equalsIgnoreCase("Designation")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Designation\">"+cm.cms("designation_type")+"</option>");
                        if(val.equalsIgnoreCase("HumanActivity")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"HumanActivity\">"+cm.cms("human_activity")+"</option>");
                        if(val.equalsIgnoreCase("Motivation")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"Motivation\">"+cm.cms("motivation")+"</option>");
                        if(val.equalsIgnoreCase("RegionCode")) { selected=" selected=\"selected\""; } else { selected=""; }
                        out.println("<option"+selected+" value=\"RegionCode\">"+cm.cms("region")+"</option>");
                        out.println("</select>");
                        %>
                        <%=cm.cmsInput("name")%>
                        <%=cm.cmsInput("code_column")%>
                        <%=cm.cmsInput("designation_year")%>
                        <%=cm.cmsInput("size")%>
                        <%=cm.cmsInput("longitude")%>
                        <%=cm.cmsInput("latitude")%>
                        <%=cm.cmsInput("min_altitude")%>
                        <%=cm.cmsInput("max_altitude")%>
                        <%=cm.cmsInput("mean_altitude")%>
                        <%=cm.cmsInput("designation_type")%>
                        <%=cm.cmsInput("human_activity")%>
                        <%=cm.cmsInput("motivation")%>
                        <%
                      }

                      out.println("&nbsp;");

                      val=rs.getString("OPERATOR");
                      currentOperator = val;
                      out.println("<label for=\"Operator" + IdNode + "\" class=\"noshow\">"+cmsOperator+"</label>");
                      out.println("<select name=\"Operator" + IdNode + "\" onchange=\"submitOperatorForm(this,'" + IdNode + "','" + IdSession + "','" + NatureObject + "')\" title=\""+cmsOperator+"\" id=\"Operator" + IdNode + "\">");

                      if(val.equalsIgnoreCase("Equal")) { selected=" selected=\"selected\""; } else { selected=""; }
                      out.println("<option"+selected+" value=\"Equal\">"+cm.cms("equal")+"</option>");
                      if(val.equalsIgnoreCase("Contains")) { selected=" selected=\"selected\""; } else { selected=""; }
                      out.println("<option"+selected+" value=\"Contains\">"+cm.cmsPhrase("Contains")+"</option>");
                      if(val.equalsIgnoreCase("Between")) { selected=" selected=\"selected\""; } else { selected=""; }
                      out.println("<option"+selected+" value=\"Between\">"+cm.cmsPhrase("Between")+"</option>");
                      if(val.equalsIgnoreCase("Regex")) { selected=" selected=\"selected\""; } else { selected=""; }
                      out.println("<option"+selected+" value=\"Regex\">Regex</option>");
                      out.println("</select>");
                      %>
                      <%=cm.cmsInput("equal")%>
                      <%
                      out.println("&nbsp;");

                      val = rs.getString("FIRST_VALUE");
                      currentValue = val;
              %>
                    <label for="First_Value<%=IdNode%>" class="noshow"><%=cm.cmsPhrase("List of values")%></label>
                    <input type="text" title="<%=cm.cmsPhrase("List of values")%>" id="First_Value<%=IdNode%>" name="First_Value<%=IdNode%>" size="25" value="<%=val%>" onblur="submitFirstValueForm(this,'<%=IdNode%>','<%=IdSession%>','<%=NatureObject%>');" onfocus="saveFirstValue(this)" onkeyup="textChanged(event)" />
                    <a title="<%=cm.cmsPhrase("List of values")%>" href="javascript:choice('First_Value<%=IdNode%>','<%=currentAttribute%>','<%=NatureObject%>','<%=currentOperator%>')" name="first_binocular" onmouseover="setCurrentSelected(this.name)" onmouseout="setCurrentSelected('')"><img border="0" src="images/helper/helper.gif" width="11" height="18" alt="<%=cm.cmsPhrase("List of values")%>" /></a>
              <%
                      if (rs.getString("OPERATOR").equalsIgnoreCase("Between")) {
                        out.println(cm.cmsPhrase("and"));
                        val = rs.getString("LAST_VALUE");
                        currentValue = val;
              %>
                    <label for="Last_Value<%=IdNode%>" class="noshow"><%=cm.cmsPhrase("List of values")%></label>
                    <input type="text" title="<%=cm.cmsPhrase("List of values")%>" id="Last_Value<%=IdNode%>" name="Last_Value<%=IdNode%>" size="25" value="<%=val%>" onblur="submitLastValueForm(this,'<%=IdNode%>','<%=IdSession%>','<%=NatureObject%>')" onfocus="saveLastValue(this)" onkeyup="textChanged(event)" />
                    <a title="<%=cm.cmsPhrase("List of values")%>" href="javascript:choice('Last_Value<%=IdNode%>','<%=currentAttribute%>','<%=NatureObject%>','<%=currentOperator%>')" name="last_binocular" onmouseover="setCurrentSelected(this.name)" onmouseout="setCurrentSelected('')"><img border="0" src="images/helper/helper.gif" width="11" height="18" alt="<%=cm.cmsPhrase("List of values")%>" /></a>
              <%
                      }
              %>
                    <br />
                      <%
                    }
                  }
                  %>
                  <% if(NatureObject.equalsIgnoreCase("Sites")) { %>
                    <br />
                    <jsp:include page="sites-databases.jsp" />
                    <br />
                  <% } %>

                  <% if(skip == null || skip.equalsIgnoreCase("")) { %>
                    <br />
                    <%=cm.cmsPhrase("Please specify the relation between Species, Habitat types and Sites:")%>
                    <br />
                    <select title="<%=cm.cms("combined_search_type")%>" name="SearchType" id="SearchType">
                    <% if(FirstNatureObject.equalsIgnoreCase("Species")) { %>
                    <option value="SpeciesHabitatsSpeciesSites" <%=SearchType.equalsIgnoreCase("SpeciesHabitatsSpeciesSites")?"selected=\"selected\"":""%>><%=cm.cms("combined_sph_sps")%></option>
                    <option value="SpeciesHabitatsHabitatsSites" <%=SearchType.equalsIgnoreCase("SpeciesHabitatsHabitatsSites")?"selected=\"selected\"":""%>><%=cm.cms("combined_sph_hs")%></option>
                    <option value="SpeciesSitesSitesHabitats" <%=SearchType.equalsIgnoreCase("SpeciesSitesSitesHabitats")?"selected=\"selected\"":""%>><%=cm.cms("combined_sps_sh")%></option>
                    <option value="SpeciesHabitatsHabitatsSitesSitesSpecies" <%=SearchType.equalsIgnoreCase("SpeciesHabitatsHabitatsSitesSitesSpecies")?"selected=\"selected\"":""%>><%=cm.cms("combined_sph_hs_ssp")%></option>
                    </select>
                    <%
                      if(SearchType.equalsIgnoreCase("SpeciesHabitatsSpeciesSites")) combinedcombinationtype="(Species in Habitat types) and (Species in Sites)";
                      if(SearchType.equalsIgnoreCase("SpeciesHabitatsHabitatsSites")) combinedcombinationtype="(Species in Habitat types) and (Habitat types in Sites)";
                      if(SearchType.equalsIgnoreCase("SpeciesSitesSitesHabitats")) combinedcombinationtype="(Species in Sites) and (Sites with Habitat types)";
                      if(SearchType.equalsIgnoreCase("SpeciesHabitatsHabitatsSitesSitesSpecies")) combinedcombinationtype="(Species in Habitat types) and (Habitat types in Sites) and (Sites with Species)";
                    %>
                    <% } %>
                    <% if(FirstNatureObject.equalsIgnoreCase("Habitat")) { %>
                    <option value="HabitatsSpeciesHabitatsSites" <%=SearchType.equalsIgnoreCase("HabitatsSpeciesHabitatsSites")?"selected=\"selected\"":""%>><%=cm.cms("combined_hsp_hs")%></option>
                    <option value="HabitatsSitesSitesSpecies" <%=SearchType.equalsIgnoreCase("HabitatsSitesSitesSpecies")?"selected=\"selected\"":""%>><%=cm.cms("combined_hs_ssp")%></option>
                    <option value="HabitatsSpeciesSpeciesSites" <%=SearchType.equalsIgnoreCase("HabitatsSpeciesSpeciesSites")?"selected=\"selected\"":""%>><%=cm.cms("combined_hsp_sps")%></option>
                    <option value="HabitatsSitesSitesSpeciesSpeciesHabitats" <%=SearchType.equalsIgnoreCase("HabitatsSitesSitesSpeciesSpeciesHabitats")?"selected=\"selected\"":""%>><%=cm.cms("combined_hs_ssp_hsp")%></option>
                    <%
                      out.println("</select>");
                    %>
                    <%
                      if(SearchType.equalsIgnoreCase("HabitatsSpeciesHabitatsSites")) combinedcombinationtype="(Habitat types with Species) and (Habitat types in Sites)";
                      if(SearchType.equalsIgnoreCase("HabitatsSitesSitesSpecies")) combinedcombinationtype="(Habitat types in Sites) and (Sites with Species)";
                      if(SearchType.equalsIgnoreCase("HabitatsSpeciesSpeciesSites")) combinedcombinationtype="(Habitat types with Species) and (Species in Sites)";
                      if(SearchType.equalsIgnoreCase("HabitatsSitesSitesSpeciesSpeciesHabitats")) combinedcombinationtype="(Habitat types in Sites) and (Sites with Species) and (Habitat types with Species)";
                    %>
                    <% } %>
                    <% if(FirstNatureObject.equalsIgnoreCase("Sites")) { %>
                    <option value="SitesSpeciesSitesHabitats" <%=SearchType.equalsIgnoreCase("SitesSpeciesSitesHabitats")?"selected=\"selected\"":""%>><%=cm.cms("combined_ssp_sh")%></option>
                    <option value="SitesSpeciesSpeciesHabitats" <%=SearchType.equalsIgnoreCase("SitesSpeciesSpeciesHabitats")?"selected=\"selected\"":""%>><%=cm.cms("combined_ssp_sph")%></option>
                    <option value="SitesHabitatsHabitatsSpecies" <%=SearchType.equalsIgnoreCase("SitesHabitatsHabitatsSpecies")?"selected=\"selected\"":""%>><%=cm.cms("combined_sh_hsp")%></option>
                    <option value="SitesSpeciesSpeciesHabitatsHabitatsSites" <%=SearchType.equalsIgnoreCase("SitesSpeciesSpeciesHabitatsHabitatsSites")?"selected=\"selected\"":""%>><%=cm.cms("combined_ssp_sph_sh")%></option>
                    <%
                      out.println("</select>");
                    %>
                    <%
                      if(SearchType.equalsIgnoreCase("SitesSpeciesSitesHabitats")) combinedcombinationtype="(Sites with Species) and (Sites with Habitat types)";
                      if(SearchType.equalsIgnoreCase("SitesSpeciesSpeciesHabitats")) combinedcombinationtype="(Sites with Species) and (Species with Habitat types)";
                      if(SearchType.equalsIgnoreCase("SitesHabitatsHabitatsSpecies")) combinedcombinationtype="(Sites with Habitat types) and (Habitat types with Species)";
                      if(SearchType.equalsIgnoreCase("SitesSpeciesSpeciesHabitatsHabitatsSites")) combinedcombinationtype="(Sites with Species) and (Species with Habitat types) and (Sites with Habitat types)";
                    %>
                    <% } %>
                    <%=cm.cmsTitle("combined_search_type")%>
                    <br />
                  <% } %>
                  <br />
                  <input type="button" class="saveButton" onclick="disableSaveButton()" disabled="disabled" value="Save" id="Save" name="Save" title="<%=cm.cmsPhrase("Save")%>" />
                  &nbsp;&nbsp;&nbsp;
                  <input type="submit" class="submitSearchButton" value="Search" id="Search" name="Search" title="<%=cm.cmsPhrase("Search")%>" />
                  &nbsp;&nbsp;&nbsp;
                  <input type="button" class="standardButton" onclick="submitButtonForm('reset','0')" value="Reset" id="Reset" name="Reset" title="<%=cm.cmsPhrase("Reset")%>" />
                  <%
              } else {
                %>
                  <a title="<%=cm.cms("add_root")%>" href="javascript:submitButtonForm('addroot','0');"><img border="0" src="images/mini/add.gif" width="13" height="13" title="<%=cm.cms("add_root")%>" alt="<%=cm.cms("add_root")%>" /></a>&nbsp;<%=cm.cmsPhrase("Add root criterion")%>
                <%
              }
                rs.close();
                %>
              </form>
                <br />
                <strong>
                  <%=cm.cmsPhrase("Note: Combined search might take a long time")%>
                </strong>
                <br />
                <%
                String criteria=tas.createCriteria(IdSession,NatureObject);
                out.println(cm.cmsPhrase("Calculated search criteria expression:"));
                combinedexplainedcriteria3=criteria.replace('#',' ').replace('[','(').replace(']',')').replaceAll("AND","<strong>AND</strong>").replaceAll("OR","<strong>OR</strong>");
                out.println(combinedexplainedcriteria3);

                out.println("<br />");
                out.println("<br />");

                if(request.getParameter("Search")!=null) {
                  String finalwhere="";
                  String node="";
                  int pos_start=-1;
                  int pos_end=-1;
                  String interpretedcriteria="";
                  String intermediatefilter="";

                  if(NatureObject.equalsIgnoreCase("Species")) {
                    combinednatureobject3="Species";
                    ro.finsiel.eunis.search.CombinedSearch tsas;
                    tsas = new ro.finsiel.eunis.search.CombinedSearch();
                    tsas.SetSQLLimit(SQL_LIMIT);
                    pos_start=criteria.indexOf('#');
                    pos_end=criteria.indexOf('#',pos_start+1);
                    while(pos_start!=-1 && pos_end!=-1) {
                      node=criteria.substring(pos_start+1,pos_end);
                      interpretedcriteria=tsas.InterpretCriteria(node,IdSession,NatureObject);
                      combinedlistcriteria3+=node+": "+interpretedcriteria+"<br />";
                      out.println(cm.cmsPhrase("Searching for: {0}...",interpretedcriteria));
                      intermediatefilter=tsas.BuildFilter(node,IdSession,NatureObject);
                      out.println(cm.cmsPhrase("found: <strong>{0}</strong>",tsas.getResultCount()));
                      if(tsas.getResultCount()>=SQL_LIMIT) {
                          String str = cm.cmsPhrase("<br />&nbsp;&nbsp;(Only the first SQL_LIMIT results were retrieved - this can lead to partial,incomplete or no combined search results at all - you should refine this criteria)");
                          if ( str != null )
                          {
                            str = str.replaceAll( "SQL_LIMIT", "" + SQL_LIMIT );
                            out.println( str );
                          }
                      }
                      out.println("<br />");

                      finalwhere="";
                      finalwhere+=criteria.substring(0,pos_start);
                      finalwhere+="ID_NATURE_OBJECT IN ("+intermediatefilter+")";
                      finalwhere+=criteria.substring(pos_end+1);
                      criteria=finalwhere;

                      pos_start=criteria.indexOf('#',pos_end+1);
                      if(pos_start!=-1) {
                        pos_end=criteria.indexOf('#',pos_start+1);
                      } else {
                      }
                    }
                  }

                  if(NatureObject.equalsIgnoreCase("Habitat")) {
                    combinednatureobject3="Habitats";
                    ro.finsiel.eunis.search.CombinedSearch tsas;
                    tsas = new ro.finsiel.eunis.search.CombinedSearch();
                    tsas.SetSQLLimit(SQL_LIMIT);
                    pos_start=criteria.indexOf('#');
                    pos_end=criteria.indexOf('#',pos_start+1);
                    while(pos_start!=-1 && pos_end!=-1) {
                      node=criteria.substring(pos_start+1,pos_end);
                      interpretedcriteria=tsas.InterpretCriteria(node,IdSession,NatureObject);
                      combinedlistcriteria3+=node+": "+interpretedcriteria+"<br />";
                      out.println(cm.cmsPhrase("Searching for: {0}...",interpretedcriteria));
                      intermediatefilter=tsas.BuildFilter(node,IdSession,NatureObject);
                      out.println(cm.cmsPhrase("found: <strong>{0}</strong>",tsas.getResultCount()));
                      if(tsas.getResultCount()>=SQL_LIMIT) {
                          String str = cm.cmsPhrase("<br />&nbsp;&nbsp;(Only the first SQL_LIMIT results were retrieved - this can lead to partial,incomplete or no combined search results at all - you should refine this criteria)");
                          if ( str != null )
                          {
                            str = str.replaceAll( "SQL_LIMIT", "" + SQL_LIMIT );
                            out.println( str );
                          }
                      }
                      out.println("<br />");

                      finalwhere="";
                      finalwhere+=criteria.substring(0,pos_start);
                      finalwhere+="ID_NATURE_OBJECT IN ("+intermediatefilter+")";
                      finalwhere+=criteria.substring(pos_end+1);
                      criteria=finalwhere;

                      pos_start=criteria.indexOf('#',pos_end+1);
                      if(pos_start!=-1) {
                        pos_end=criteria.indexOf('#',pos_start+1);
                      } else {
                      }
                    }
                  }

                  if(NatureObject.equalsIgnoreCase("Sites")) {
                    combinednatureobject3="Sites";
                    ro.finsiel.eunis.search.CombinedSearch tsas;
                    tsas = new ro.finsiel.eunis.search.CombinedSearch();
                    tsas.SetSourceDB(SourceDB);
                    tsas.SetSQLLimit(SQL_LIMIT);
                    pos_start=criteria.indexOf('#');
                    pos_end=criteria.indexOf('#',pos_start+1);
                    while(pos_start!=-1 && pos_end!=-1) {
                      node=criteria.substring(pos_start+1,pos_end);
                      interpretedcriteria=tsas.InterpretCriteria(node,IdSession,NatureObject);
                      combinedlistcriteria3+=node+": "+interpretedcriteria+"<br />";
                      out.println(cm.cmsPhrase("Searching for: {0}...",interpretedcriteria));
                      intermediatefilter=tsas.BuildFilter(node,IdSession,NatureObject);
                      out.println(cm.cmsPhrase("found: <strong>{0}</strong>",tsas.getResultCount()));
                      if(tsas.getResultCount()>=SQL_LIMIT) {
                          String str = cm.cmsPhrase("<br />&nbsp;&nbsp;(Only the first SQL_LIMIT results were retrieved - this can lead to partial,incomplete or no combined search results at all - you should refine this criteria)");
                          if ( str != null )
                          {
                            str = str.replaceAll( "SQL_LIMIT", "" + SQL_LIMIT );
                            out.println( str );
                          }
                      }
                      out.println("<br />");

                      finalwhere="";
                      finalwhere+=criteria.substring(0,pos_start);
                      finalwhere+="ID_NATURE_OBJECT IN ("+intermediatefilter+")";
                      finalwhere+=criteria.substring(pos_end+1);
                      criteria=finalwhere;

                      pos_start=criteria.indexOf('#',pos_end+1);
                      if(pos_start!=-1) {
                        pos_end=criteria.indexOf('#',pos_start+1);
                      } else {
                      }
                    }
                  }

                  ro.finsiel.eunis.search.CombinedSearch tsas;
                  tsas = new ro.finsiel.eunis.search.CombinedSearch();
                  tsas.SetSourceDB(SourceDB);
                  tsas.SetSQLLimit(SQL_LIMIT);
                  String str=tsas.calculateCriteria(IdSession,NatureObject);

                  tsas.DeleteResults(IdSession,NatureObject);

                  if(NatureObject.equalsIgnoreCase("Species")) {
                    str="SELECT ID_NATURE_OBJECT FROM chm62edt_species WHERE ("+str+")";
                  }
                  if(NatureObject.equalsIgnoreCase("Habitat")) {
                    str="SELECT ID_NATURE_OBJECT FROM chm62edt_habitat WHERE ("+str+")";
                  }
                  if(NatureObject.equalsIgnoreCase("Sites")) {
                    str="SELECT ID_NATURE_OBJECT FROM chm62edt_sites WHERE ("+str+")";
                  }
                  String query = tsas.ExecuteFilterSQL(str,"");
                  out.println("<br /><strong>" + cm.cmsPhrase("Total matches found in database:") + "&nbsp;" + tsas.getResultCount() + "</strong><br /><br />");
                  if (tsas.getResultCount() > 0) {
                    tsas.AddResult(IdSession,NatureObject,query);
                  }

                  if (tsas.getResultCount() > 0) {
                    ps_exec=con.createStatement();

                    //delete previous results
                    SQL="DELETE FROM eunis_combined_search_results";
                    SQL+=" WHERE ID_SESSION = '"+IdSession+"'";
                    SQL+=" AND ID_NATURE_OBJECT > 0";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                      ps = con.prepareStatement(SQL);
                      //System.out.println("Executing: "+SQL);
                      ps.execute();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }
                  }

                  //now the fun part - combine the results
                  //let's try first to see if an unusual search type was entered

                  if (tsas.getResultCount() > 0) {
                    String tmp = "";
                    tmp+=FirstNatureObject;
                    if(FirstNatureObject.equalsIgnoreCase("Habitat")) { tmp+="s"; }
                    tmp+=SecondNatureObject;
                    if(SecondNatureObject.equalsIgnoreCase("Habitat")) { tmp+="s"; }
                    tmp+=FirstNatureObject;
                    if(FirstNatureObject.equalsIgnoreCase("Habitat")) { tmp+="s"; }
                    tmp+=NatureObject;
                    if(NatureObject.equalsIgnoreCase("Habitat")) { tmp+="s"; }
//      System.out.println("tmp = " + tmp);
                    if(SearchType!=null && !SearchType.equalsIgnoreCase("") && !SearchType.equalsIgnoreCase(tmp)) {
                      //an unusual search type was entered for sure
                      // let's calculate all possible combinations
                      System.out.println("An unusual combination was entered");
                      //now we deal with Species-Habitats and inser the final results in ID_NATURE_OBJECT
                      String speciesHabitatsSQL="";
                      speciesHabitatsSQL=" SELECT DISTINCT chm62edt_nature_object_report_type.ID_NATURE_OBJECT";
                      speciesHabitatsSQL+=" FROM chm62edt_nature_object_report_type";
                      speciesHabitatsSQL+=" WHERE ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Species")+")";
                      speciesHabitatsSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        ps = con.prepareStatement(speciesHabitatsSQL);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2,ID_NATURE_OBJECT_COMBINATION_3)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+="-1,";
                          SQL+="-1,-1,-1,";
                          SQL+=rs.getString(1)+","; //column COMBINATION_1 (Species-Habitats)
                          SQL+="-1,-1";
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }

                      // now we deal with Habitats - Sites
                      String habitatsSitesSQL="";
                      habitatsSitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                      habitatsSitesSQL+=" FROM chm62edt_nature_object_report_type";
                      habitatsSitesSQL+=" WHERE 1=1";
                      habitatsSitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                      habitatsSitesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        ps = con.prepareStatement(habitatsSitesSQL);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2,ID_NATURE_OBJECT_COMBINATION_3)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+="-1,";
                          SQL+="-1,-1,-1,";
                          SQL+="-1,";
                          SQL+=rs.getString(1)+","; //column COMBINATION_2 (Habitats-Sites)
                          SQL+="-1";
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }

                      // now we deal with Sites - Species
                      String speciesSitesSQL="";
                      speciesSitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                      speciesSitesSQL+=" FROM chm62edt_nature_object_report_type";
                      speciesSitesSQL+=" WHERE 1=1";
                      speciesSitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                      speciesSitesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Species")+")";

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        ps = con.prepareStatement(speciesSitesSQL);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2,ID_NATURE_OBJECT_COMBINATION_3)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+="-1,";
                          SQL+="-1,-1,-1,";
                          SQL+="-1,-1,";
                          SQL+=rs.getString(1); //column COMBINATION_3 (Sites-Species)
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }

//        System.out.println("SearchType = " + SearchType);
                      String combinedSQL="";
                      if(SearchType.equalsIgnoreCase("SpeciesHabitatsHabitatsSites")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_species`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_species`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_1`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results1` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `eunis_combined_search_results1`.`ID_NATURE_OBJECT_COMBINATION_2`)";
                      }
                      if(SearchType.equalsIgnoreCase("SpeciesSitesSitesHabitats")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_species`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_species`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_3`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results1` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `eunis_combined_search_results1`.`ID_NATURE_OBJECT_COMBINATION_2`)";
                      }
                      if(SearchType.equalsIgnoreCase("SpeciesHabitatsHabitatsSitesSitesSpecies")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_species`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_species`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_3`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results1` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `eunis_combined_search_results1`.`ID_NATURE_OBJECT_COMBINATION_2`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results2` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `eunis_combined_search_results2`.`ID_NATURE_OBJECT_COMBINATION_1`)";
                      }
                      if(SearchType.equalsIgnoreCase("HabitatsSitesSitesSpecies")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_habitat`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_habitat`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_3`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results1` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `eunis_combined_search_results1`.`ID_NATURE_OBJECT_COMBINATION_2`)";
                      }
                      if(SearchType.equalsIgnoreCase("HabitatsSpeciesSpeciesSites")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_habitat`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_habitat`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_3`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results2` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `eunis_combined_search_results2`.`ID_NATURE_OBJECT_COMBINATION_1`)";
                      }
                      if(SearchType.equalsIgnoreCase("HabitatsSitesSitesSpeciesSpeciesHabitats")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_habitat`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_habitat`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_3`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results1` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `eunis_combined_search_results1`.`ID_NATURE_OBJECT_COMBINATION_2`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results2` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `eunis_combined_search_results2`.`ID_NATURE_OBJECT_COMBINATION_1`)";
                      }
                      if(SearchType.equalsIgnoreCase("SitesSpeciesSpeciesHabitats")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_sites`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_sites`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_3`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results2` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `eunis_combined_search_results2`.`ID_NATURE_OBJECT_COMBINATION_1`)";
                      }
                      if(SearchType.equalsIgnoreCase("SitesHabitatsHabitatsSpecies")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_sites`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_sites`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_2`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results2` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `eunis_combined_search_results2`.`ID_NATURE_OBJECT_COMBINATION_1`)";
                      }
                      if(SearchType.equalsIgnoreCase("SitesSpeciesSpeciesHabitatsHabitatsSites")) {
                        combinedSQL+=" SELECT DISTINCT `chm62edt_sites`.`ID_NATURE_OBJECT`";
                        combinedSQL+=" FROM `chm62edt_sites`";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `eunis_combined_search_results`.`ID_NATURE_OBJECT_COMBINATION_3`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results1` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `eunis_combined_search_results1`.`ID_NATURE_OBJECT_COMBINATION_2`)";
                        combinedSQL+=" INNER JOIN `eunis_combined_search_results` `eunis_combined_search_results2` ON (`chm62edt_sites`.`ID_NATURE_OBJECT` = `eunis_combined_search_results2`.`ID_NATURE_OBJECT_COMBINATION_1`)";
                      }

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        ps = con.prepareStatement(combinedSQL);
                        //System.out.println("Executing: "+combinedSQL);
                        rs = ps.executeQuery();
                        bResults=0;
                        while(rs.next()) {
                          bResults++;
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2,ID_NATURE_OBJECT_COMBINATION_3)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+=rs.getString(1)+",";
                          SQL+="-1,-1,-1,";
                          SQL+="-1,-1,-1";
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }
                      if(bResults > 0) {
                      %>
                        <hr width="100%" size="1" align="left" />
                        <br /><%=bResults%> <%=FirstNatureObject%> <%=cm.cmsPhrase("matching the combination of criteria were found.")%><br /><br />
                        <form name="search" action="select-columns.jsp" method="post">
                          <input type="submit" id="ProceedResults0" title="<%=cm.cms("proceed_to_results")%>"
                                 name="Proceed to results" value="<%=cm.cms("proceed_to_results")%>" class="submitSearchButton" />
                          <%=cm.cmsInput("proceed_to_results")%>
                          <input type="hidden" name="idsession" value="<%=IdSession%>" />
                          <input type="hidden" name="searchedNatureObject" value="<%=FirstNatureObject%>" />
                          <input type="hidden" name="origin" value="Combined" />
                          <input type="hidden" name="combinedexplainedcriteria3" value="<%=combinedexplainedcriteria3%>" />
                          <input type="hidden" name="combinedlistcriteria3" value="<%=combinedlistcriteria3%>" />
                          <input type="hidden" name="combinednatureobject3" value="<%=combinednatureobject3%>" />
                          <input type="hidden" name="combinedcombinationtype" value="<%=combinedcombinationtype%>" />
                          <input type="hidden" name="sourcedbcriteria" value="<%=sourcedbcriteria.replaceAll("'","")%>" />
                        </form>
                      <%} else {%>
                        <br /><%=cm.cmsPhrase("no")%>&nbsp;<%=FirstNatureObject%> <%=cm.cmsPhrase("matching the combination of criteria were found.")%><br />
                      <%
                      }
                      out.println("</div>");
                      out.println("</body>");
                      out.println("</html>");
                      return;
                    }
                  }

                  //we do not have any strange criteria, so proceed...
                  if (tsas.getResultCount() > 0) {
                    if(FirstNatureObject.equalsIgnoreCase("Species")) {
                      if(skip.equalsIgnoreCase("") || skip.equalsIgnoreCase("Sites")) {
                        String speciesHabitatsSQL="";

                        speciesHabitatsSQL=" SELECT DISTINCT chm62edt_nature_object_report_type.ID_NATURE_OBJECT";
                        speciesHabitatsSQL+=" FROM chm62edt_nature_object_report_type";
                        speciesHabitatsSQL+=" WHERE 1=1";
                        speciesHabitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Species")+")";
                        speciesHabitatsSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                        
                        try {
                          con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                //          System.out.println("Executing: "+speciesHabitatsSQL);
                          ps = con.prepareStatement(speciesHabitatsSQL);
                          rs = ps.executeQuery();
                          while(rs.next()) {
                            SQL="INSERT INTO eunis_combined_search_results(";
                            SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                            SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                            SQL+=" VALUES(";
                            SQL+="'"+IdSession+"',";
                            SQL+="0,0,0,0,";
                            SQL+=rs.getString(1)+",";
                            SQL+="0";
                            SQL+=")";
                            ps_exec.execute(SQL);
                          }
                          rs.close();
                          con.close();
                        }
                        catch(Exception e) {

                          return;
                        }
                      }

                      if(skip.equalsIgnoreCase("") || skip.equalsIgnoreCase("Habitat")) {
                        String speciesSitesSQL="";
                        speciesSitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                        speciesSitesSQL+=" FROM chm62edt_nature_object_report_type";
                        speciesSitesSQL+=" WHERE 1=1";
                        speciesSitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                        speciesSitesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Species")+")";

                //        System.out.println("Species-sites started.");

                        
                        try {
                          con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                          ps = con.prepareStatement(speciesSitesSQL);
                          //System.out.println("Executing: "+SQL);
                          rs = ps.executeQuery();
                          while(rs.next()) {
                            SQL="INSERT INTO eunis_combined_search_results(";
                            SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                            SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                            SQL+=" VALUES(";
                            SQL+="'"+IdSession+"',";
                            SQL+="0,0,0,0,0,";
                            SQL+=rs.getString(1);
                            SQL+=")";
                            ps_exec.execute(SQL);
                          }
                          rs.close();
                          con.close();
                        }
                        catch(Exception e) {

                          return;
                        }
                      }

                      String speciesSQL="";
                      speciesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT";
                      speciesSQL+=" FROM chm62edt_species";
                      speciesSQL+=" WHERE 1=1";
                      if(skip.equalsIgnoreCase("")) {
                        speciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                        speciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                      } else {
                        if(skip.equalsIgnoreCase("Sites")) {
                          speciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                        }
                        if(skip.equalsIgnoreCase("Habitat")) {
                          speciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                        }
                      }

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        ps = con.prepareStatement(speciesSQL);
                        //System.out.println("Executing: "+SQL);
                        rs = ps.executeQuery();
                        bResults=0;
                        while(rs.next()) {
                          bResults++;
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+=rs.getString(1)+",";
                          SQL+="0,0,0,";
                          SQL+="0,0";
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }

                    if(bResults > 0) {
                      %>
                      <hr width="100%" size="1" align="left" />
                      <br /><%=bResults%> <%=cm.cmsPhrase("species matching the combination of criteria were found.")%><br /><br />
                      <form name="search" action="select-columns.jsp" method="post">
                        <input type="submit" name="Proceed to results" id="ProceedResults" title="<%=cm.cms("proceed_to_results")%>"
                               value="<%=cm.cms("proceed_to_results")%>" class="submitSearchButton" />
                        <%=cm.cmsInput("proceed_to_results")%>
                        <input type="hidden" name="idsession" value="<%=IdSession%>" />
                        <input type="hidden" name="searchedNatureObject" value="Species" />
                        <input type="hidden" name="combinedexplainedcriteria3" value="<%=combinedexplainedcriteria3%>" />
                        <input type="hidden" name="combinedlistcriteria3" value="<%=combinedlistcriteria3%>" />
                        <input type="hidden" name="combinednatureobject3" value="<%=combinednatureobject3%>" />
                        <input type="hidden" name="combinedcombinationtype" value="<%=combinedcombinationtype%>" />
                        <input type="hidden" name="origin" value="Combined" />
                        <input type="hidden" name="sourcedbcriteria" value="<%=sourcedbcriteria.replaceAll("'","")%>" />
                      </form>
                      <%} else {%>
                      <br />
                      <%=cm.cmsPhrase("No species matching the combination of criteria were found.")%>
                      <br />
                      <%
                    }
                  }

                  //analog vine pentru Habitats
                  if(FirstNatureObject.equalsIgnoreCase("Habitat")) {
                    if(skip.equalsIgnoreCase("") || skip.equalsIgnoreCase("Sites")) {
                      String habitatsSpeciesSQL="";

                      habitatsSpeciesSQL=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                      habitatsSpeciesSQL+=" FROM chm62edt_nature_object_report_type";
                      habitatsSpeciesSQL+=" WHERE 1=1";
                      habitatsSpeciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Species")+")";
                      habitatsSpeciesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                //        System.out.println("Executing: "+habitatsSpeciesSQL);
                        ps = con.prepareStatement(habitatsSpeciesSQL);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+="0,0,0,0,";
                          SQL+=rs.getString(1)+",";
                          SQL+="0";
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }
                    }

                    if(skip.equalsIgnoreCase("") || skip.equalsIgnoreCase("Species")) {
                      System.out.println("Looking for Habitats - Skipping Species");
                      String habitatsSitesSQL="";
                      habitatsSitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                      habitatsSitesSQL+=" FROM chm62edt_nature_object_report_type";
                      habitatsSitesSQL+=" WHERE 1=1";
                      habitatsSitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                      habitatsSitesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        ps = con.prepareStatement(habitatsSitesSQL);
                        //System.out.println("Executing: "+SQL);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+="0,0,0,0,0,";
                          SQL+=rs.getString(1);
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }
                    }

                    String habitatsSQL="";
                    habitatsSQL+=" SELECT DISTINCT ID_NATURE_OBJECT";
                    habitatsSQL+=" FROM chm62edt_habitat";
                    habitatsSQL+=" WHERE 1=1";
                    if(skip.equalsIgnoreCase("")) {
                      habitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                      habitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                    } else {
                      if(skip.equalsIgnoreCase("Sites")) {
                        habitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                      }
                      if(skip.equalsIgnoreCase("Species")) {
                        habitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                      }
                    }

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                      ps = con.prepareStatement(habitatsSQL);
                      //System.out.println("Executing: "+habitatsSQL);
                      rs = ps.executeQuery();
                      bResults=0;
                      while(rs.next()) {
                        bResults++;
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+=rs.getString(1)+",";
                        SQL+="0,0,0,";
                        SQL+="0,0";
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }

                    if(bResults > 0) {
                      %>
                      <hr width="100%" size="1" align="left" />
                      <br /><%=bResults%> <%=cm.cmsPhrase("habitat types matching the combination of criteria were found.")%><br /><br />
                      <form name="search" action="select-columns.jsp" method="post">
                        <input type="submit" name="Proceed to results" title="<%=cm.cms("proceed_to_results")%>"
                               value="<%=cm.cms("proceed_to_results")%>" id="ProceedResults2" class="submitSearchButton" />
                        <%=cm.cmsInput("proceed_to_results")%>
                        <input type="hidden" name="idsession" value="<%=IdSession%>" />
                        <input type="hidden" name="searchedNatureObject" value="Habitats" />
                        <input type="hidden" name="origin" value="Combined" />
                        <input type="hidden" name="combinedexplainedcriteria3" value="<%=combinedexplainedcriteria3%>" />
                        <input type="hidden" name="combinedlistcriteria3" value="<%=combinedlistcriteria3%>" />
                        <input type="hidden" name="combinednatureobject3" value="<%=combinednatureobject3%>" />
                        <input type="hidden" name="combinedcombinationtype" value="<%=combinedcombinationtype%>" />
                        <input type="hidden" name="sourcedbcriteria" value="<%=sourcedbcriteria.replaceAll("'","")%>" />
                      </form>
                      <%
                    } else {
                      %>
                      <br />
                      <%=cm.cmsPhrase("No habitat types matching the combination of criteria were found.")%>
                      <br />
                      <%
                    }
                  }

                  //analog vine pentru Sites
                  if(FirstNatureObject.equalsIgnoreCase("Sites")) {
                    System.out.println("Looking for Sites");
                    if(skip.equalsIgnoreCase("") || skip.equalsIgnoreCase("Habitat")) {
                      System.out.println("Skiping Habitats");
                      String sitesSpeciesSQL="";

                      sitesSpeciesSQL=" SELECT DISTINCT ID_NATURE_OBJECT";
                      sitesSpeciesSQL+=" FROM chm62edt_nature_object_report_type";
                      sitesSpeciesSQL+=" WHERE 1=1";
                      sitesSpeciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                      sitesSpeciesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Species")+")";

                      
                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                //        System.out.println("Executing: "+sitesSpeciesSQL);
                        ps = con.prepareStatement(sitesSpeciesSQL);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+="0,0,0,0,";
                          SQL+=rs.getString(1)+",";
                          SQL+="0";
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }
                    }

                    if(skip.equalsIgnoreCase("") || skip.equalsIgnoreCase("Species")) {
                      //System.out.println("Skiping Species");
                      String sitesHabitatsSQL="";
                      sitesHabitatsSQL+=" SELECT DISTINCT ID_NATURE_OBJECT";
                      sitesHabitatsSQL+=" FROM chm62edt_nature_object_report_type";
                      sitesHabitatsSQL+=" WHERE 1=1";
                      sitesHabitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                      sitesHabitatsSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                      

                      try {
                        con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                        ps = con.prepareStatement(sitesHabitatsSQL);
                        //System.out.println("Executing: "+sitesHabitatsSQL);
                        rs = ps.executeQuery();
                        while(rs.next()) {
                          SQL="INSERT INTO eunis_combined_search_results(";
                          SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                          SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2,ID_NATURE_OBJECT_COMBINATION_3)";
                          SQL+=" VALUES(";
                          SQL+="'"+IdSession+"',";
                          SQL+="-1,-1,-1,-1,-1,";
                          SQL+=rs.getString(1)+",";
                          SQL+="-1";
                          SQL+=")";
                          ps_exec.execute(SQL);
                        }
                        rs.close();
                        con.close();
                      }
                      catch(Exception e) {

                        return;
                      }
                    }

                    String sitesSQL="";
                    sitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT";
                    sitesSQL+=" FROM chm62edt_sites";
                    sitesSQL+=" WHERE 1=1";
                    if(skip.equalsIgnoreCase("")) {
                      sitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                      sitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                    } else {
                      if(skip.equalsIgnoreCase("Habitat")) {
                        sitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                      }
                      if(skip.equalsIgnoreCase("Species")) {
                        sitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                        //System.out.println("sitesSQL = " + sitesSQL);
                      }
                    }

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                      ps = con.prepareStatement(sitesSQL);
                      //System.out.println("Executing: "+sitesSQL);
                      rs = ps.executeQuery();
                      bResults=0;
                      while(rs.next()) {
                        bResults++;
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+=rs.getString(1)+",";
                        SQL+="0,0,0,";
                        SQL+="0,0";
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }

                    if(bResults > 0) {
                    %>
                    <hr width="100%" size="1" align="left" />
                    <br /><%=bResults%> <%=cm.cmsPhrase("sites matching the combination of criteria were found.")%><br /><br />
                    <form name="search" action="select-columns.jsp" method="post">
                      <input type="submit" name="Proceed to results" id="ProceedResults3" title="<%=cm.cms("proceed_to_results")%>"
                             value="<%=cm.cms("proceed_to_results")%>" class="submitSearchButton" />
                      <%=cm.cmsInput("proceed_to_results")%>
                      <input type="hidden" name="idsession" value="<%=IdSession%>" />
                      <input type="hidden" name="searchedNatureObject" value="Sites" />
                      <input type="hidden" name="origin" value="Combined" />
                      <input type="hidden" name="combinedexplainedcriteria3" value="<%=combinedexplainedcriteria3%>" />
                      <input type="hidden" name="combinedlistcriteria3" value="<%=combinedlistcriteria3%>" />
                      <input type="hidden" name="combinednatureobject3" value="<%=combinednatureobject3%>" />
                      <input type="hidden" name="combinedcombinationtype" value="<%=combinedcombinationtype%>" />
                      <input type="hidden" name="sourcedbcriteria" value="<%=sourcedbcriteria.replaceAll("'","")%>" />
                    </form>
                    <%
                    } else {
                    %>
                    <br />
                    <%=cm.cmsPhrase("No sites matching the combination of criteria were found.")%>
                    <br />
                    <%
                    }
                  }
                } else { %>
                   <br />
                     <%=cm.cmsPhrase("No results were found.")%>
                   <br />
                <% }
                }
              } else {
//aici calculam cu varianta skip venita de la pagina 2
                ro.finsiel.eunis.search.CombinedSearch tsas;
                tsas = new ro.finsiel.eunis.search.CombinedSearch();
                tsas.SetSQLLimit(SQL_LIMIT);

                //delete previous results
                SQL="DELETE FROM eunis_combined_search_results";
                SQL+=" WHERE ID_SESSION = '"+IdSession+"'";
                SQL+=" AND ID_NATURE_OBJECT > 0";

                try {
                  ps = con.prepareStatement(SQL);
                  //System.out.println("Executing: "+SQL);
                  ps.execute();
                }
                catch(Exception e) {

                  return;
                }

                ps_exec=con.createStatement();

                if(FirstNatureObject.equalsIgnoreCase("Species")) {
                  if(skip.equalsIgnoreCase("Sites")) {
                    String speciesHabitatsSQL="";
                    speciesHabitatsSQL=" SELECT DISTINCT chm62edt_nature_object_geoscope.ID_NATURE_OBJECT";
                    speciesHabitatsSQL+=" FROM chm62edt_nature_object_geoscope";
                    speciesHabitatsSQL+=" WHERE 1=1";
                    speciesHabitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Species")+")";
                    speciesHabitatsSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                //          System.out.println("Executing: "+speciesHabitatsSQL);
                      ps = con.prepareStatement(speciesHabitatsSQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,";
                        SQL+=rs.getString(1)+",";
                        SQL+="0";
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }

                    speciesHabitatsSQL=" SELECT DISTINCT chm62edt_nature_object_report_type.ID_NATURE_OBJECT";
                    speciesHabitatsSQL+=" FROM chm62edt_nature_object_report_type";
                    speciesHabitatsSQL+=" WHERE 1=1";
                    speciesHabitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Species")+")";
                    speciesHabitatsSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                //          System.out.println("Executing: "+speciesHabitatsSQL);
                      ps = con.prepareStatement(speciesHabitatsSQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,";
                        SQL+=rs.getString(1)+",";
                        SQL+="0";
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }
                  }

                  if(skip.equalsIgnoreCase("Habitat")) {
                    String speciesSitesSQL="";
                    speciesSitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                    speciesSitesSQL+=" FROM chm62edt_nature_object_report_type";
                    speciesSitesSQL+=" WHERE 1=1";
                    speciesSitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                    speciesSitesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Species")+")";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                      ps = con.prepareStatement(speciesSitesSQL);
                      //System.out.println("Executing: "+SQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,0,";
                        SQL+=rs.getString(1);
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }
                  }

                  String speciesSQL="";
                  speciesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT";
                  speciesSQL+=" FROM chm62edt_species";
                  speciesSQL+=" WHERE 1=1";
                  if(skip.equalsIgnoreCase("Sites")) {
                    speciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                  }
                  if(skip.equalsIgnoreCase("Habitat")) {
                    speciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                  }

                  try {
                    con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                    ps = con.prepareStatement(speciesSQL);
                    //System.out.println("Executing: "+SQL);
                    rs = ps.executeQuery();
                    bResults=0;
                    while(rs.next()) {
                      bResults++;
                      SQL="INSERT INTO eunis_combined_search_results(";
                      SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                      SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                      SQL+=" VALUES(";
                      SQL+="'"+IdSession+"',";
                      SQL+=rs.getString(1)+",";
                      SQL+="0,0,0,";
                      SQL+="0,0";
                      SQL+=")";
                      ps_exec.execute(SQL);
                    }
                    rs.close();
                    con.close();
                  }
                  catch(Exception e) {

                    return;
                  }

                  if(bResults > 0) {
                      %>
                      <hr width="100%" size="1" align="left" />
                      <br /><%=bResults%> <%=cm.cmsPhrase("species matching the combination of criteria were found.")%><br /><br />
                      <form name="search" action="select-columns.jsp" method="post">
                        <input type="submit" id="ProceedResults4" title="<%=cm.cms("proceed_to_results")%>" name="Proceed to results"
                               value="<%=cm.cms("proceed_to_results")%>" class="submitSearchButton" />
                        <%=cm.cmsInput("proceed_to_results")%>
                        <input type="hidden" name="idsession" value="<%=IdSession%>" />
                        <input type="hidden" name="searchedNatureObject" value="Species" />
                        <input type="hidden" name="origin" value="Combined" />
                        <input type="hidden" name="combinedexplainedcriteria3" value="<%=combinedexplainedcriteria3%>" />
                        <input type="hidden" name="combinedlistcriteria3" value="<%=combinedlistcriteria3%>" />
                        <input type="hidden" name="combinednatureobject3" value="<%=combinednatureobject3%>" />
                        <input type="hidden" name="combinedcombinationtype" value="<%=combinedcombinationtype%>" />
                        <input type="hidden" name="sourcedbcriteria" value="<%=sourcedbcriteria.replaceAll("'","")%>" />
                      </form>
                      <%} else {%>
                      <br />
                      <%=cm.cmsPhrase("No species matching the combination of criteria were found.")%>
                      <br />
                      <%
                  }
                }

                //analog vine pentru Habitats
                if(FirstNatureObject.equalsIgnoreCase("Habitat")) {
                  if(skip.equalsIgnoreCase("Sites")) {
                    String habitatsSpeciesSQL="";
                    habitatsSpeciesSQL=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                    habitatsSpeciesSQL+=" FROM chm62edt_nature_object_geoscope";
                    habitatsSpeciesSQL+=" WHERE 1=1";
                    habitatsSpeciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Species")+")";
                    habitatsSpeciesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                  //        System.out.println("Executing: "+habitatsSpeciesSQL);
                      ps = con.prepareStatement(habitatsSpeciesSQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,";
                        SQL+=rs.getString(1)+",";
                        SQL+="0";
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }

                    habitatsSpeciesSQL=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                    habitatsSpeciesSQL+=" FROM chm62edt_nature_object_report_type";
                    habitatsSpeciesSQL+=" WHERE 1=1";
                    habitatsSpeciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Species")+")";
                    habitatsSpeciesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                  //        System.out.println("Executing: "+habitatsSpeciesSQL);
                      ps = con.prepareStatement(habitatsSpeciesSQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,";
                        SQL+=rs.getString(1)+",";
                        SQL+="0";
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }
                  }

                  if(skip.equalsIgnoreCase("Species")) {
                    String habitatsSitesSQL="";
                    habitatsSitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                    habitatsSitesSQL+=" FROM chm62edt_nature_object_report_type";
                    habitatsSitesSQL+=" WHERE 1=1";
                    habitatsSitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                    habitatsSitesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                      ps = con.prepareStatement(habitatsSitesSQL);
                      //System.out.println("Executing: "+SQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,0,";
                        SQL+=rs.getString(1);
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }
                  }

                  String habitatsSQL="";
                  habitatsSQL+=" SELECT DISTINCT ID_NATURE_OBJECT";
                  habitatsSQL+=" FROM chm62edt_habitat";
                  habitatsSQL+=" WHERE 1=1";
                  if(skip.equalsIgnoreCase("Sites")) {
                    habitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                  }
                  if(skip.equalsIgnoreCase("Species")) {
                    habitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                  }

                  
                  try {
                    con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                    ps = con.prepareStatement(habitatsSQL);
                    //System.out.println("Executing: "+habitatsSQL);
                    rs = ps.executeQuery();
                    bResults=0;
                    while(rs.next()) {
                      bResults++;
                      SQL="INSERT INTO eunis_combined_search_results(";
                      SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                      SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                      SQL+=" VALUES(";
                      SQL+="'"+IdSession+"',";
                      SQL+=rs.getString(1)+",";
                      SQL+="0,0,0,";
                      SQL+="0,0";
                      SQL+=")";
                      ps_exec.execute(SQL);
                    }
                    rs.close();
                    con.close();
                  }
                  catch(Exception e) {

                    return;
                  }

                  if(bResults > 0) {
                        %>
                        <hr width="100%" size="1" align="left" />
                        <br /><%=bResults%> <%=cm.cmsPhrase("habitat types matching the combination of criteria were found.")%><br /><br />
                        <form name="search" action="select-columns.jsp" method="post">
                          <input type="submit" id="ProceedResults5" title="<%=cm.cms("proceed_to_results")%>"
                                 name="Proceed to results" value="<%=cm.cms("proceed_to_results")%>" class="submitSearchButton" />
                          <%=cm.cmsInput("proceed_to_results")%>
                          <input type="hidden" name="idsession" value="<%=IdSession%>" />
                          <input type="hidden" name="searchedNatureObject" value="Habitats" />
                          <input type="hidden" name="origin" value="Combined" />
                          <input type="hidden" name="combinedexplainedcriteria3" value="<%=combinedexplainedcriteria3%>" />
                          <input type="hidden" name="combinedlistcriteria3" value="<%=combinedlistcriteria3%>" />
                          <input type="hidden" name="combinednatureobject3" value="<%=combinednatureobject3%>" />
                          <input type="hidden" name="combinedcombinationtype" value="<%=combinedcombinationtype%>" />
                          <input type="hidden" name="sourcedbcriteria" value="<%=sourcedbcriteria.replaceAll("'","")%>" />
                        </form>
                        <%
                  } else {
                        %>
                        <br />
                          <%=cm.cmsPhrase("No habitat types matching the combination of criteria were found.")%>
                        <br />
                        <%
                  }
                }

                //analog vine pentru Sites
                if(FirstNatureObject.equalsIgnoreCase("Sites")) {
                  if(skip.equalsIgnoreCase("Habitat")) {
                    String sitesSpeciesSQL="";
                    sitesSpeciesSQL=" SELECT DISTINCT ID_NATURE_OBJECT";
                    sitesSpeciesSQL+=" FROM chm62edt_nature_object_report_type";
                    sitesSpeciesSQL+=" WHERE 1=1";
                    sitesSpeciesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                    sitesSpeciesSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Species")+")";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                  //        System.out.println("Executing: "+sitesSpeciesSQL);
                      ps = con.prepareStatement(sitesSpeciesSQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,";
                        SQL+=rs.getString(1)+",";
                        SQL+="0";
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }
                  }

                  if(skip.equalsIgnoreCase("Species")) {
                    String sitesHabitatsSQL="";
                    sitesHabitatsSQL+=" SELECT DISTINCT ID_NATURE_OBJECT_LINK";
                    sitesHabitatsSQL+=" FROM chm62edt_nature_object_report_type";
                    sitesHabitatsSQL+=" WHERE 1=1";
                    sitesHabitatsSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Sites")+")";
                    sitesHabitatsSQL+=" AND ID_NATURE_OBJECT_LINK IN ("+tsas.CreateList(IdSession,"Habitat")+")";

                    
                    try {
                      con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                      ps = con.prepareStatement(sitesHabitatsSQL);
                      //System.out.println("Executing: "+sitesHabitatsSQL);
                      rs = ps.executeQuery();
                      while(rs.next()) {
                        SQL="INSERT INTO eunis_combined_search_results(";
                        SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                        SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                        SQL+=" VALUES(";
                        SQL+="'"+IdSession+"',";
                        SQL+="0,0,0,0,0,";
                        SQL+=rs.getString(1);
                        SQL+=")";
                        ps_exec.execute(SQL);
                      }
                      rs.close();
                      con.close();
                    }
                    catch(Exception e) {

                      return;
                    }
                  }

                  String sitesSQL="";
                  sitesSQL+=" SELECT DISTINCT ID_NATURE_OBJECT";
                  sitesSQL+=" FROM chm62edt_sites";
                  sitesSQL+=" WHERE 1=1";
                  if(skip.equalsIgnoreCase("Habitat")) {
                    sitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_1")+")";
                  }
                  if(skip.equalsIgnoreCase("Species")) {
                    sitesSQL+=" AND ID_NATURE_OBJECT IN ("+tsas.CreateList(IdSession,"Combination_2")+")";
                  }

                  
                  try {
                    con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
                    ps = con.prepareStatement(sitesSQL);
                    //System.out.println("Executing: "+sitesSQL);
                    rs = ps.executeQuery();
                    bResults=0;
                    while(rs.next()) {
                      bResults++;
                      SQL="INSERT INTO eunis_combined_search_results(";
                      SQL+="ID_SESSION,ID_NATURE_OBJECT,ID_NATURE_OBJECT_SPECIES,ID_NATURE_OBJECT_HABITATS,ID_NATURE_OBJECT_SITES,";
                      SQL+="ID_NATURE_OBJECT_COMBINATION_1,ID_NATURE_OBJECT_COMBINATION_2)";
                      SQL+=" VALUES(";
                      SQL+="'"+IdSession+"',";
                      SQL+=rs.getString(1)+",";
                      SQL+="0,0,0,";
                      SQL+="0,0";
                      SQL+=")";
                      ps_exec.execute(SQL);
                    }
                    rs.close();
                    con.close();
                  }
                  catch(Exception e) {

                    return;
                  }

                  if(bResults > 0) {
                      %>
                      <hr width="100%" size="1" align="left" />
                      <br /><%=bResults%> <%=cm.cmsPhrase("sites matching the combination of criteria were found.")%><br /><br />
                      <form name="search" action="select-columns.jsp" method="post">
                        <input type="submit" id="ProceedResults6" title="<%=cm.cms("proceed_to_results")%>"
                               name="Proceed to results" value="<%=cm.cms("proceed_to_results")%>" class="submitSearchButton" />
                        <%=cm.cmsInput("proceed_to_results")%>
                        <input type="hidden" name="idsession" value="<%=IdSession%>" />
                        <input type="hidden" name="searchedNatureObject" value="Sites" />
                        <input type="hidden" name="origin" value="Combined" />
                        <input type="hidden" name="combinedexplainedcriteria3" value="<%=combinedexplainedcriteria3%>" />
                        <input type="hidden" name="combinedlistcriteria3" value="<%=combinedlistcriteria3%>" />
                        <input type="hidden" name="combinednatureobject3" value="<%=combinednatureobject3%>" />
                        <input type="hidden" name="combinedcombinationtype" value="<%=combinedcombinationtype%>" />
                        <input type="hidden" name="sourcedbcriteria" value="<%=sourcedbcriteria.replaceAll("'","")%>" />
                      </form>
                      <%
                  } else {
                      %>
                      <br />
                      <%=cm.cmsPhrase("No sites matching the combination of criteria were found.")%>
                      <br />
                      <%
                  }
                }
              }

              con.close();
              %>
              <%=cm.br()%>
              <%=cm.cmsMsg("generic_combined-search-step3_01")%>
              <%=cm.br()%>
              <%=cm.cmsMsg("press_save_to_save_criteria")%>
              <%=cm.br()%>
              <%=cm.cmsMsg("criteria_saved")%>
              <%=cm.br()%>
              <%=cm.cmsMsg("any")%>
              <%=cm.br()%>
              <%=cm.cmsMsg("all")%>
              <%=cm.br()%>
              <%=cm.cmsMsg("error_deleting_root")%>
              <%=cm.br()%>
              <%=cm.cmsMsg("error_adding_branch")%>
              <%=cm.br()%>
              <%=cm.cmsMsg("error_deleting_branch")%>
              <%=cm.br()%>
<!-- END MAIN CONTENT -->
    </stripes:layout-component>
</stripes:layout-render>