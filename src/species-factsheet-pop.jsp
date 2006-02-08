<%--
  - Author(s)   : The EUNIS Database Team.
  - Date        :
  - Copyright   : (c) 2002-2005 EEA - European Environment Agency.
  - Description : Species factsheet - populations.
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.util.*,
                 ro.finsiel.eunis.factsheet.species.FactSheetPopulationWrapper,
                 ro.finsiel.eunis.factsheet.species.SpeciesFactsheet,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.WebContentManagement"%>
<%// Get form parameters here%>
<jsp:useBean id="FormBean" class="ro.finsiel.eunis.formBeans.SpeciesFactSheetBean" scope="page">
  <jsp:setProperty name="FormBean" property="*"/>
</jsp:useBean>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
  String idNatureObj = FormBean.getIdNatureObject();
  // List of population information for species
  Vector list = SpeciesFactsheet.getPopulation(idNatureObj);
  if (list.size() > 0)
  {
%>
    <div style="width : 100%; background-color : #CCCCCC; font-weight : bold;"><%=cm.cmsText("species_factsheet-pop_11")%></div>
    <table summary="<%=cm.cms("species_factsheet-pop_12_Sum")%>" width="100%" border="1" cellspacing="1" cellpadding="0" id="populations" class="sortable">
      <tr>
        <th title="<%=cm.cms("sort_results_on_this_column")%>">
          <%=cm.cmsText("species_factsheet-pop_02")%>
          <%=cm.cmsTitle("sort_results_on_this_column")%>
        </th>
        <th title="<%=cm.cms("sort_results_on_this_column")%>">
          <%=cm.cmsText("species_factsheet-pop_03")%>
          <%=cm.cmsTitle("sort_results_on_this_column")%>
        </th>
        <th title="<%=cm.cms("sort_results_on_this_column")%>">
          <span style="color:#006CAD">  
            <%=cm.cmsText("species_factsheet-pop_04")%>
          </span>
        </th>
        <th title="<%=cm.cms("sort_results_on_this_column")%>">
          <%=cm.cmsText("species_factsheet-pop_05")%>
          <%=cm.cmsTitle("sort_results_on_this_column")%>
        </th>
        <th title="<%=cm.cms("sort_results_on_this_column")%>">
          <%=cm.cmsText("species_factsheet-pop_06")%>
          <%=cm.cmsTitle("sort_results_on_this_column")%>
        </th>
        <th title="<%=cm.cms("sort_results_on_this_column")%>">
          <%=cm.cmsText("species_factsheet-pop_07")%>
          <%=cm.cmsTitle("sort_results_on_this_column")%>
        </th>
        <th title="<%=cm.cms("sort_results_on_this_column")%>">
          <%=cm.cmsText("species_factsheet-pop_08")%>
          <%=cm.cmsTitle("sort_results_on_this_column")%>
        </th>
      </tr>
<%
      String bgColor;
      String reference;
      Vector authorURL;
      for (int i = 0; i < list.size(); i++)
      {
        FactSheetPopulationWrapper aRow = (FactSheetPopulationWrapper)list.get(i);
        bgColor = ( 0 == ( i % 2 ) ? "#EEEEEE" : "#FFFFFF" );
        reference = Utilities.getReferencesByIdDc( aRow.getReference() );
        authorURL = Utilities.getAuthorAndUrlByIdDc( aRow.getReference() );
%>
      <tr style="background-color:<%=bgColor%>">
        <td>
        <%
            if(Utilities.isCountry(aRow.getCountry()))
            {
        %>
          <a href="javascript:goToSpeciesStatistics('<%=Utilities.treatURLSpecialCharacters(aRow.getCountry())%>')" title="<%=cm.cms("species_factsheet-geo_12_Title")%> <%=Utilities.treatURLSpecialCharacters(aRow.getCountry())%>"><%=Utilities.treatURLSpecialCharacters(aRow.getCountry())%></a>
          <%=cm.cmsTitle("species_factsheet-geo_12_Title")%>
        <%
            } else {
        %>
             <%=Utilities.treatURLSpecialCharacters(aRow.getCountry())%>
        <%
             }
        %>
            &nbsp;
          </td>
          <td>
            <%=Utilities.treatURLSpecialCharacters(aRow.getBioregion())%>&nbsp;
          </td>
          <td>
            <%=aRow.getMin()%>/<%=aRow.getMax()%>(<%=aRow.getUnits()%>)&nbsp;
          </td>
          <td>
            <%=aRow.getDate()%>&nbsp;
          </td>
          <td>
            <%=Utilities.treatURLSpecialCharacters(aRow.getStatus())%>&nbsp;
          </td>
          <td>
            <%=Utilities.treatURLSpecialCharacters(aRow.getQuality())%>&nbsp;
          </td>
<%
          if (!authorURL.get(1).toString().equalsIgnoreCase(""))
          {
%>
          <td width="25%" style="text-align:left">
            <span onmouseover="return showtooltip('<%=reference%>')" onmouseout="hidetooltip()">
              <span class="boldUnderline">
                <a href="<%=Utilities.treatURLSpecialCharacters((String)authorURL.get(1))%>"><%=Utilities.treatURLSpecialCharacters((String)authorURL.get(0))%></a>
              </span>
            </span>
          </td>
<%
          }
          else
          {
%>
          <td width="25%" style="text-align:left">
            <span onmouseover="return showtooltip('<%=reference%>')" onmouseout="hidetooltip()">
            <span class="boldUnderline">
              <%=Utilities.treatURLSpecialCharacters((String)authorURL.get(0))%>
            </span>
            </span>
          </td>
<%
          }
%>
      </tr>
<%
      }
%>
    </table>
<%
  }
%>

<%=cm.br()%>
<%=cm.cmsMsg("species_factsheet-pop_12_Sum")%>

<br />
<br />