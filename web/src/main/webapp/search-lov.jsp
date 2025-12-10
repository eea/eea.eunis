<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : List of values for species names
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.sql.Connection,
                 java.sql.Statement,
                 java.sql.DriverManager,
                 java.sql.ResultSet,
                 ro.finsiel.eunis.search.Utilities,
                 ro.finsiel.eunis.WebContentManagement"%>
<%@ page import="ro.finsiel.eunis.utilities.SQLUtilities" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%
  String ctl = request.getParameter("ctl");
  String lov = request.getParameter("lov");
  String natureobject = request.getParameter("natureobject");
  String val = request.getParameter("val");
  if(val == null) {
    val="";
  }
  String oper = request.getParameter("oper");
//  System.out.println( "oper=" + oper );
//  System.out.println( "lov=" + lov );

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <jsp:include page="header-page.jsp" />
<%
  WebContentManagement cm = SessionManager.getWebContent();
%>
    <title>
      <%=cm.cmsPhrase("List of values")%>
    </title>
    <script language="JavaScript" type="text/javascript">
    //<![CDATA[
      function setValue(v) {
        window.opener.<%=ctl%>.value=v;
        window.close();
      }
    //]]>
    </script>
  </head>
  <body>
<%
  String SQL="";
  Connection con = null;
  Statement ps = null;
  ResultSet rs = null;

  try {
    con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();


  // Set SQL string
  if(lov.equalsIgnoreCase("Altitude") ||
     lov.equalsIgnoreCase("Chemistry") ||
     lov.equalsIgnoreCase("Water") ||
     lov.equalsIgnoreCase("Usage") ||
     lov.equalsIgnoreCase("Ph") ||
     lov.equalsIgnoreCase("Climate") ||
     lov.equalsIgnoreCase("Cover") ||
     lov.equalsIgnoreCase("Geomorph") ||
     lov.equalsIgnoreCase("Humidity") ||
     lov.equalsIgnoreCase("LifeForm") ||
     lov.equalsIgnoreCase("LightIntensity") ||
     lov.equalsIgnoreCase("DistributionStatus") ||
     lov.equalsIgnoreCase("HumanActivity") ||
     lov.equalsIgnoreCase("Motivation") ||
     lov.equalsIgnoreCase("Marine") ||
     lov.equalsIgnoreCase("Geology") ||
     lov.equalsIgnoreCase("Salinity") ||
     lov.equalsIgnoreCase("Spatial") ||
     lov.equalsIgnoreCase("Temperature") ||
     lov.equalsIgnoreCase("Influences") ||
     lov.equalsIgnoreCase("Substrate") ||
     lov.equalsIgnoreCase("Temporal") ||
     lov.equalsIgnoreCase("Tidal")) {

   if(oper.equalsIgnoreCase("Equal"))
   {
     SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" WHERE NAME='"+ val + "' ORDER BY ID_"+lov.toUpperCase();
   }
   else
   {
     if(oper.equalsIgnoreCase("Contains"))
     {
       SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_"+lov.toUpperCase();
     }
     else
     {
       if(oper.equalsIgnoreCase("Between"))
       {
         SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_"+lov.toUpperCase();
       }
       else
       {
         SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" ORDER BY ID_"+lov.toUpperCase();
       }
     }
   }
  }

  if(lov.equalsIgnoreCase("InternationalThreatStatus"))
  {
    if(oper.equalsIgnoreCase("Equal"))
    {
      SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME='"+ val + "' ORDER BY NAME";
    }
    else
    {
      if(oper.equalsIgnoreCase("Contains"))
      {
        SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
      }
      else
      {
        if(oper.equalsIgnoreCase("Between"))
        {
          SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
        }
        else
        {
          SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status ORDER BY NAME";
        }
      }
    }
  }
  if(lov.equalsIgnoreCase("ThreatStatus"))
  {
    if(oper.equalsIgnoreCase("Equal"))
    {
      SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME='"+ val + "' ORDER BY NAME";
    }
    else
    {
      if(oper.equalsIgnoreCase("Contains"))
      {
        SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
      }
      else
      {
        if(oper.equalsIgnoreCase("Between"))
        {
          SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
        }
        else
        {
          SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status ORDER BY NAME";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("LegalInstruments"))
  {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT LEGAL_INSTRUMENT_ABBREV,LEGAL_INSTRUMENT FROM chm62edt_habitat_DESIGNATED_CODES WHERE LEGAL_INSTRUMENT_ABBREV='"+ val + "' ORDER BY LEGAL_INSTRUMENT_ABBREV";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT LEGAL_INSTRUMENT_ABBREV,LEGAL_INSTRUMENT FROM chm62edt_habitat_DESIGNATED_CODES WHERE LEGAL_INSTRUMENT_ABBREV LIKE '%"+ val + "%' ORDER BY LEGAL_INSTRUMENT_ABBREV";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT LEGAL_INSTRUMENT_ABBREV,LEGAL_INSTRUMENT FROM chm62edt_habitat_DESIGNATED_CODES WHERE LEGAL_INSTRUMENT_ABBREV LIKE '%"+ val + "%' ORDER BY LEGAL_INSTRUMENT_ABBREV";
        } else {
          SQL="SELECT DISTINCT LEGAL_INSTRUMENT_ABBREV,LEGAL_INSTRUMENT FROM chm62edt_habitat_DESIGNATED_CODES ORDER BY LEGAL_INSTRUMENT_ABBREV";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("Abundance")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT DESCRIPTION,CODE FROM chm62edt_"+lov.toLowerCase()+" WHERE DESCRIPTION='"+ val + "' ORDER BY DESCRIPTION";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT DESCRIPTION,CODE FROM chm62edt_"+lov.toLowerCase()+" WHERE DESCRIPTION LIKE '%"+ val + "%' ORDER BY DESCRIPTION";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT DESCRIPTION,CODE FROM chm62edt_"+lov.toLowerCase()+" WHERE DESCRIPTION LIKE '%"+ val + "%' ORDER BY DESCRIPTION";
        } else {
          SQL="SELECT DISTINCT DESCRIPTION,CODE FROM chm62edt_"+lov.toLowerCase()+" ORDER BY ID_"+lov.toUpperCase();
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("InfoQuality")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_info_quality WHERE DESCRIPTION='"+ val + "' ORDER BY ID_INFO_QUALITY";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_info_quality WHERE DESCRIPTION LIKE '%"+ val + "%' ORDER BY ID_INFO_QUALITY";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_info_quality WHERE DESCRIPTION LIKE '%"+ val + "%' ORDER BY ID_INFO_QUALITY";
        } else {
          SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_info_quality ORDER BY ID_INFO_QUALITY";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("Trend")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_trend WHERE STATUS='"+ val + "' ORDER BY ID_"+lov.toUpperCase();
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_trend WHERE STATUS LIKE '%"+ val + "%' ORDER BY ID_"+lov.toUpperCase();
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_trend WHERE STATUS LIKE '%"+ val + "%' ORDER BY ID_"+lov.toUpperCase();
        } else {
          SQL="SELECT DISTINCT STATUS,DESCRIPTION FROM chm62edt_trend ORDER BY STATUS";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("RegionCode")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_region_codes WHERE NAME='"+ val + "' ORDER BY ID_REGION_CODE";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_region_codes WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_REGION_CODE";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_region_codes WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_REGION_CODE";
        } else {
          SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_region_codes ORDER BY ID_REGION_CODE";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("HumanActivity")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_human_activity WHERE NAME='"+ val + "' ORDER BY ID_HUMAN_ACTIVITY";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_human_activity WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_HUMAN_ACTIVITY";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_human_activity WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_HUMAN_ACTIVITY";
        } else {
          SQL="SELECT DISTINCT NAME,DESCRIPTION FROM chm62edt_human_activity ORDER BY ID_HUMAN_ACTIVITY";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("Group")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT COMMON_NAME,SCIENTIFIC_NAME FROM chm62edt_group_species WHERE COMMON_NAME='"+ val + "' ORDER BY COMMON_NAME";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT COMMON_NAME,SCIENTIFIC_NAME FROM chm62edt_group_species WHERE COMMON_NAME LIKE '%"+ val + "%' ORDER BY COMMON_NAME";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT COMMON_NAME,SCIENTIFIC_NAME FROM chm62edt_group_species WHERE COMMON_NAME LIKE '%"+ val + "%' ORDER BY COMMON_NAME";
        } else {
          SQL="SELECT DISTINCT COMMON_NAME,SCIENTIFIC_NAME FROM chm62edt_group_species ORDER BY COMMON_NAME";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("Country")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country WHERE AREA_NAME_EN='"+ val + "' ORDER BY AREA_NAME_EN";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country WHERE AREA_NAME_EN LIKE '%"+ val + "%' ORDER BY AREA_NAME_EN";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country WHERE AREA_NAME_EN LIKE '%"+ val + "%' ORDER BY AREA_NAME_EN";
        } else {
          SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country ORDER BY AREA_NAME_EN";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("SpeciesStatus")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT DESCRIPTION,SHORT_DEFINITION FROM chm62edt_species_status WHERE NAME='"+ val + "' ORDER BY DESCRIPTION";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT DESCRIPTION,SHORT_DEFINITION FROM chm62edt_species_status WHERE NAME LIKE '%"+ val + "%' ORDER BY DESCRIPTION";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT DESCRIPTION,SHORT_DEFINITION FROM chm62edt_species_status WHERE NAME LIKE '%"+ val + "%' ORDER BY DESCRIPTION";
        } else {
          SQL="SELECT DISTINCT DESCRIPTION,SHORT_DEFINITION FROM chm62edt_species_status ORDER BY DESCRIPTION";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("Biogeoregion")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT NAME,CODE FROM chm62edt_biogeoregion WHERE NAME='"+ val + "' ORDER BY NAME";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT NAME,CODE FROM chm62edt_biogeoregion WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT NAME,CODE FROM chm62edt_biogeoregion WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
        } else {
          SQL="SELECT DISTINCT NAME,CODE FROM chm62edt_biogeoregion ORDER BY NAME";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("SourceDatabase")) {
    if(natureobject.equalsIgnoreCase("Habitat")) {
      out.println("<a href=\"javascript:setValue('EUNIS')\">EUNIS</a><br />");
      out.println("<a href=\"javascript:setValue('Annex I')\">Annex I</a><br />");
      out.println("<br />");
    }
    if(natureobject.equalsIgnoreCase("Sites")) {
      out.println("<a href=\"javascript:setValue('Corine biotopes')\">Corine biotopes</a><br />");
      out.println("<a href=\"javascript:setValue('Cdda National')\">Cdda National</a><br />");
      out.println("<a href=\"javascript:setValue('Cdda International')\">Cdda International</a><br />");
      out.println("<a href=\"javascript:setValue('Biogenetic reserves')\">Biogenetic reserves</a><br />");
      out.println("<a href=\"javascript:setValue('European Diploma')\">European Diploma</a><br />");
      out.println("<a href=\"javascript:setValue('Emerald')\">Emerald</a><br />");
      out.println("<br />");
    }
  }

  if(lov.equalsIgnoreCase("ScientificName"))
  {
    if(natureobject.equalsIgnoreCase("Species"))
    {
      SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
      SQL+=" WHERE SCIENTIFIC_NAME LIKE '%"+val+"%'";
      SQL+=" ORDER BY SCIENTIFIC_NAME";
      SQL+=" LIMIT 0,100";
      if( oper.equalsIgnoreCase( cm.cms("species_names_02_Msg") ) )
      {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
        SQL+=" WHERE SCIENTIFIC_NAME = '"+val+"'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if( oper.equalsIgnoreCase( cm.cmsPhrase("contains") ) )
      {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
        SQL+=" WHERE SCIENTIFIC_NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if( oper.equalsIgnoreCase( cm.cmsPhrase("starts with") ) )
      {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
        SQL+=" WHERE SCIENTIFIC_NAME LIKE '"+val+"%'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Between"))
      {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
        SQL+=" WHERE SCIENTIFIC_NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
    }

    if(natureobject.equalsIgnoreCase("Habitat"))
    {

      String isGoodHabitat = " IF(TRIM(chm62edt_habitat.CODE_2000) <> '',RIGHT(chm62edt_habitat.CODE_2000,2),1) <> IF(TRIM(chm62edt_habitat.CODE_2000) <> '','00',2) AND IF(TRIM(chm62edt_habitat.CODE_2000) <> '',LENGTH(chm62edt_habitat.CODE_2000),1) = IF(TRIM(chm62edt_habitat.CODE_2000) <> '',4,1) ";
      SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_HABITAT FROM chm62edt_habitat";
      SQL+=" WHERE   "+isGoodHabitat+" AND SCIENTIFIC_NAME LIKE '%"+val+"%'";
      SQL+=" ORDER BY SCIENTIFIC_NAME";
      SQL+=" LIMIT 0,100";
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_HABITAT FROM chm62edt_habitat";
        SQL+=" WHERE  "+isGoodHabitat+" and SCIENTIFIC_NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Equal")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_HABITAT FROM chm62edt_habitat";
        SQL+=" WHERE  "+isGoodHabitat+" AND SCIENTIFIC_NAME = '"+val+"'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Between")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_HABITAT FROM chm62edt_habitat";
        SQL+=" WHERE  "+isGoodHabitat+" AND SCIENTIFIC_NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
    }
  }

  if(lov.equalsIgnoreCase("Name"))
  {
    if(natureobject.equalsIgnoreCase("Sites")) {
      SQL="SELECT DISTINCT NAME,ID_SITE FROM chm62edt_sites";
      SQL+=" WHERE NAME LIKE '%"+val+"%'";
      SQL+=" ORDER BY NAME";
      SQL+=" LIMIT 0,100";
      if(oper.equalsIgnoreCase("Equal")) {
        SQL="SELECT DISTINCT NAME,ID_SITE FROM chm62edt_sites";
        SQL+=" WHERE NAME = '"+val+"'";
        SQL+=" ORDER BY NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT NAME,ID_SITE FROM chm62edt_sites";
        SQL+=" WHERE NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Between")) {
        SQL="SELECT DISTINCT NAME,ID_SITE FROM chm62edt_sites";
        SQL+=" WHERE NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY NAME";
        SQL+=" LIMIT 0,100";
      }
    }
  }

  if(lov.equalsIgnoreCase("VernacularName"))
  {
    SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE FROM chm62edt_reports ";
    SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
    SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
    SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
    SQL+=" AND (chm62edt_report_attributes.VALUE LIKE '%"+val+"%')";
    SQL+=" LIMIT 0,100";
    if(oper.equalsIgnoreCase("Contains")) {
      SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE FROM chm62edt_reports ";
      SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
      SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
      SQL+=" AND (chm62edt_report_attributes.VALUE LIKE '%"+val+"%')";
      SQL+=" LIMIT 0,100";
    }
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE FROM chm62edt_reports ";
      SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
      SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
      SQL+=" AND (chm62edt_report_attributes.VALUE = '"+val+"')";
      SQL+=" LIMIT 0,100";
    }
    if(oper.equalsIgnoreCase("Between")) {
      SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE FROM chm62edt_reports ";
      SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
      SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
      SQL+=" AND (chm62edt_report_attributes.VALUE LIKE '%"+val+"%')";
      SQL+=" LIMIT 0,100";
    }
    if(oper.equalsIgnoreCase("Starts")) {
      SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE FROM chm62edt_reports ";
      SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
      SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
      SQL+=" AND (chm62edt_report_attributes.VALUE LIKE '"+val+"%')";
      SQL+=" LIMIT 0,100";
    }
  }
  if(lov.equalsIgnoreCase("Designation")){
    SQL="SELECT DISTINCT `chm62edt_designations`.`DESCRIPTION`,`chm62edt_designations`.`ID_DESIGNATION`";
    SQL+=" FROM chm62edt_designations ";
    SQL+=" INNER JOIN `chm62edt_sites` ON (`chm62edt_designations`.`ID_DESIGNATION` = `chm62edt_sites`.`ID_DESIGNATION` AND `chm62edt_designations`.`ID_GEOSCOPE` = `chm62edt_sites`.`ID_GEOSCOPE`)";
    SQL+=" WHERE";
    SQL+=" (`chm62edt_designations`.`DESCRIPTION` LIKE '%"+val+"%')";
    if(oper.equalsIgnoreCase("Contains")) {
      SQL="SELECT DISTINCT `chm62edt_designations`.`DESCRIPTION`,`chm62edt_designations`.`ID_DESIGNATION`";
      SQL+=" FROM chm62edt_designations ";
      SQL+=" INNER JOIN `chm62edt_sites` ON (`chm62edt_designations`.`ID_DESIGNATION` = `chm62edt_sites`.`ID_DESIGNATION` AND `chm62edt_designations`.`ID_GEOSCOPE` = `chm62edt_sites`.`ID_GEOSCOPE`)";
      SQL+=" WHERE";
      SQL+=" (`chm62edt_designations`.`DESCRIPTION` LIKE '%"+val+"%')";
    }
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT `chm62edt_designations`.`DESCRIPTION`,`chm62edt_designations`.`ID_DESIGNATION`";
      SQL+=" FROM chm62edt_designations ";
      SQL+=" INNER JOIN `chm62edt_sites` ON (`chm62edt_designations`.`ID_DESIGNATION` = `chm62edt_sites`.`ID_DESIGNATION` AND `chm62edt_designations`.`ID_GEOSCOPE` = `chm62edt_sites`.`ID_GEOSCOPE`)";
      SQL+=" WHERE";
      SQL+=" (`chm62edt_designations`.`DESCRIPTION` = '"+val+"')";
    }
    if(oper.equalsIgnoreCase("Between")) {
      SQL="SELECT DISTINCT `chm62edt_designations`.`DESCRIPTION`,`chm62edt_designations`.`ID_DESIGNATION`";
      SQL+=" FROM chm62edt_designations ";
      SQL+=" INNER JOIN `chm62edt_sites` ON (`chm62edt_designations`.`ID_DESIGNATION` = `chm62edt_sites`.`ID_DESIGNATION` AND `chm62edt_designations`.`ID_GEOSCOPE` = `chm62edt_sites`.`ID_GEOSCOPE`)";
      SQL+=" WHERE";
      SQL+=" (`chm62edt_designations`.`DESCRIPTION` LIKE '%"+val+"%')";
    }
  }

//  System.out.println("SQL = " + SQL);

  if(SQL.length()>0 && !lov.equalsIgnoreCase("SourceDatabase")) 
  {
    ps = con.createStatement();
    rs = ps.executeQuery(SQL);

    if(!rs.isBeforeFirst()) {
      out.println("<strong>No results were found.</strong>");
      out.println("<br />");
    } else {
      %>
      <u><%=ro.finsiel.eunis.search.Utilities.SplitString(lov)%></u> <em><%=oper%></em> <strong><%=val%></strong>
      <br />
      <br />
    <div id="tab">
      <table border="1" cellpadding="2" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" summary="layout">
      <%
      // Display results.
      int cnt = 0;
      while(rs.next()) {
        cnt++;
        %>
        <tr  bgcolor="<%=(0 == (cnt % 2) ? "#EEEEEE" : "#FFFFFF")%>">
          <td>
          <%
          out.println("<a href=\"javascript:setValue('"+rs.getString(1)+"')\">"+rs.getString(1)+"</a>");
          %>
          </td>
          <td>
          <%
          if(rs.getString(2) != null & rs.getString(2).length()>0) {
            out.println(rs.getString(2));
          } else {
            out.println("&nbsp;");
          }
          %>
          </td>
        </tr>
        <%
      }
      String str = Utilities.getTextMaxLimitForPopup(cm,cnt);
      %>
      </table>
    </div>
      <%=Utilities.getTextWarningForPopup(cnt)%>
      <%=str%>
<%
    }
    rs.close();
  } else {
%>
    <strong>
      <%=cm.cmsPhrase("No list of values available")%>.
    </strong>
    <br />
<%
  }
  ps.close();
  con.close();

    }
    catch(Exception e) {

        return;
    } finally {
      SQLUtilities.closeAll(con,  ps,  rs);
    }
%>
    <br />
      <form action="">
        <input type="button" onClick="javascript:window.close();" value="<%=cm.cmsPhrase("Close")%>" title="<%=cm.cmsPhrase("Close window")%>" id="button2" name="button" class="standardButton" />
      </form>
  </body>
</html>
