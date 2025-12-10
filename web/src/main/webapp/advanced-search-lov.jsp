<%--
  - Author(s) : The EUNIS Database Team.
  - Date :
  - Copyright : (c) 2002-2005 EEA - European Environment Agency.
  - Description : List of values (popup) used for 'Advanced search' and 'Combined search' functions.
  - Request params :
      * ctl - ID of the control
      * lov - List of value type
      * natureobject - The nature object type
      * val - value/part of the value which is part of the search.
      * oper - Relation operator between criteria and value
--%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
  request.setCharacterEncoding( "UTF-8");
%>
<%@ page import="java.sql.Connection,
                 java.sql.Statement,
                 java.sql.DriverManager,
                 java.sql.ResultSet"%>
<%@ page import="ro.finsiel.eunis.WebContentManagement"%>
<%@ page import="ro.finsiel.eunis.search.Utilities" %>
<%@ page import="eionet.eunis.util.Constants" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="<%=SessionManager.getCurrentLanguage()%>" xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=SessionManager.getCurrentLanguage()%>">
  <head>
    <jsp:include page="header-page.jsp" />
  <%
    WebContentManagement cm = SessionManager.getWebContent();
  %>
    <title><%=cm.cmsPhrase("List of values")%></title>
  <%
    // Request parameters
    String ctl = request.getParameter("ctl");
    String lov = request.getParameter("lov");

    /*
    if(lov.equalsIgnoreCase("LifeForm")) {
      lov = "Life_Form";
    }
    if(lov.equalsIgnoreCase("LightIntensity")) {
      lov = "Light_Intensity";
    }
    if(lov.equalsIgnoreCase("DistributionStatus")) {
      lov = "Distribution_Status";
    }
    if(lov.equalsIgnoreCase("HumanActivity")) {
      lov = "Human_Activity";
    }
    */

    String natureobject = request.getParameter("natureobject");
    String val = request.getParameter("val");
    if(val == null  || val.equalsIgnoreCase("enter value here...")) {
      val="";
    }
    String oper = request.getParameter("oper");
    //System.out.println("oper = " + oper);
    //System.out.println("val = " + val);
    if(oper.equalsIgnoreCase("Equal") && val.equalsIgnoreCase(""))
    {
      oper="Contains";
      val="%";
    }
//    out.println("ctl="+ctl+"<br />");
//    out.println("lov="+lov+"<br />");
//    out.println("val="+val+"<br />");
//    out.println("natureobject="+natureobject+"<br />");
//    out.println("oper="+oper+"<br />");
  %>

  <script language="JavaScript" type="text/javascript">
  //<![CDATA[
    function setValue(v) {
      window.opener.setCurrentSelected("");
      window.opener.document.criteria['<%=ctl%>'].focus();
      window.opener.document.criteria['<%=ctl%>'].value=v;
      window.opener.document.criteria['<%=ctl%>'].blur();
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
  }
  catch(Exception e) {

    return;
  }

  // Create SQL string
  if(lov.equalsIgnoreCase("Altitude") ||
     lov.equalsIgnoreCase("Chemistry") ||
     lov.equalsIgnoreCase("Water") ||
     lov.equalsIgnoreCase("Usage") ||
     lov.equalsIgnoreCase("Ph") ||
     lov.equalsIgnoreCase("Climate") ||
     lov.equalsIgnoreCase("Cover") ||
     lov.equalsIgnoreCase("Depth") ||
     lov.equalsIgnoreCase("Geomorph") ||
     lov.equalsIgnoreCase("Humidity") ||
     lov.equalsIgnoreCase("Life_Form") ||
     lov.equalsIgnoreCase("Light_Intensity") ||
     lov.equalsIgnoreCase("Distribution_Status") ||
     lov.equalsIgnoreCase("Human_Activity") ||
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

   if(oper.equalsIgnoreCase("Equal")) {
     SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" WHERE NAME='"+ val + "' ORDER BY ID_"+lov.toUpperCase();
   } else {
     if(oper.equalsIgnoreCase("Contains")) {
       SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_"+lov.toUpperCase();
     } else {
       if(oper.equalsIgnoreCase("Between")) {
         SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_"+lov.toUpperCase();
       } else {
         SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+lov.toLowerCase()+" ORDER BY ID_"+lov.toUpperCase();
       }
     }
   }
 }

  String splitLov = "";
  if(lov.equalsIgnoreCase("LifeForm")) {
    splitLov = "Life_Form";
  }
  if(lov.equalsIgnoreCase("LightIntensity")) {
    splitLov = "Light_Intensity";
  }
  if(lov.equalsIgnoreCase("DistributionStatus")) {
    splitLov = "Distribution_Status";
  }
  if(lov.equalsIgnoreCase("HumanActivity")) {
    splitLov = "Human_Activity";
  }

  if(lov.equalsIgnoreCase("LifeForm") ||
     lov.equalsIgnoreCase("LightIntensity") ||
     lov.equalsIgnoreCase("DistributionStatus") ||
     lov.equalsIgnoreCase("HumanActivity")) {
   if(oper.equalsIgnoreCase("Equal")) {
     SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+splitLov.toLowerCase()+" WHERE NAME='"+ val + "' ORDER BY ID_"+splitLov.toUpperCase();
   } else {
     if(oper.equalsIgnoreCase("Contains")) {
       SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+splitLov.toLowerCase()+" WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_"+splitLov.toUpperCase();
     } else {
       if(oper.equalsIgnoreCase("Between")) {
         SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+splitLov.toLowerCase()+" WHERE NAME LIKE '%"+ val + "%' ORDER BY ID_"+splitLov.toUpperCase();
       } else {
         SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_"+splitLov.toLowerCase()+" ORDER BY ID_"+splitLov.toUpperCase();
       }
     }
   }
  }

  if(lov.equalsIgnoreCase("LegalInstrument")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT `TITLE`, `ALTERNATIVE` FROM  `dc_index` WHERE `TITLE`='"+ val + "' ORDER BY `TITLE`";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT `TITLE`, `ALTERNATIVE` FROM `dc_index` WHERE `TITLE` LIKE '%"+ val + "%' ORDER BY `TITLE`";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT `TITLE`, `ALTERNATIVE` FROM `dc_index` WHERE `TITLE` LIKE '%"+ val + "%' ORDER BY `TITLE`";
        } else {
          SQL="SELECT DISTINCT `TITLE`, `ALTERNATIVE` FROM  `dc_index` ORDER BY `TITLE`";
        }
      }
    }
  }
  if(lov.equalsIgnoreCase("InternationalThreatStatus")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME='"+ val + "' ORDER BY NAME";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status WHERE NAME LIKE '%"+ val + "%' ORDER BY NAME";
        } else {
          SQL="SELECT DISTINCT NAME, DESCRIPTION FROM chm62edt_conservation_status ORDER BY NAME";
        }
      }
    }
  }


  if(lov.equalsIgnoreCase("LegalInstruments")) {
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT NAME,ID_CLASS_CODE FROM chm62edt_class_code WHERE NAME='"+ val + "' AND LEGAL<>0 ORDER BY NAME";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT NAME,ID_CLASS_CODE FROM chm62edt_class_code WHERE NAME LIKE '%"+ val + "%' AND LEGAL<>0 ORDER BY NAME";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT NAME,ID_CLASS_CODE FROM chm62edt_class_code WHERE NAME LIKE '%"+ val + "%' AND LEGAL<>0 ORDER BY NAME";
        } else {
          SQL="SELECT DISTINCT NAME,ID_CLASS_CODE FROM chm62edt_class_code WHERE LEGAL<>0 ORDER BY NAME";
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
      SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country WHERE SELECTION <> 0 AND AREA_NAME_EN='"+ val + "' ORDER BY AREA_NAME_EN";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country WHERE SELECTION <> 0 AND AREA_NAME_EN LIKE '%"+ val + "%' ORDER BY AREA_NAME_EN";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country WHERE SELECTION <> 0 AND AREA_NAME_EN LIKE '%"+ val + "%' ORDER BY AREA_NAME_EN";
        } else {
          SQL="SELECT DISTINCT AREA_NAME_EN,EUNIS_AREA_CODE FROM chm62edt_country WHERE SELECTION <> 0 ORDER BY AREA_NAME_EN";
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

  if(lov.equalsIgnoreCase("Author")) {
    if(natureobject.equalsIgnoreCase("Species")) {
      String SQLWhere="";
      if(oper.equalsIgnoreCase("Equal")) {
        SQLWhere=" (`dc_index`.`SOURCE` = '"+ val + "')";
      } else {
        if(oper.equalsIgnoreCase("Contains")) {
          SQLWhere=" (`dc_index`.`SOURCE` LIKE '%"+ val + "%')";
        } else {
          if(oper.equalsIgnoreCase("Between")) {
            SQLWhere=" (`dc_index`.`SOURCE` = '"+ val + "')";
          } else {
            SQLWhere=" (`dc_index`.`SOURCE` LIKE '%"+ val + "%')";
          }
        }
      }
      SQL+="    SELECT";
      SQL+="      `dc_index`.`SOURCE`,";
      SQL+="      `dc_index`.`EDITOR`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `chm62edt_reports` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_reports`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_reports`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE";
      SQL+="      (`chm62edt_report_type`.`LOOKUP_TYPE` IN ('DISTRIBUTION_STATUS','LANGUAGE','CONS_STATUS','SPECIES_GEO','LEGAL_STATUS','SPECIES_STATUS','POPULATION_UNIT','TREND'))";
      SQL+="    AND "+SQLWhere;
      SQL+="    UNION";
      SQL+="    SELECT";
      SQL+="      `dc_index`.`SOURCE`,";
      SQL+="      `dc_index`.`EDITOR`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE "+SQLWhere;
      SQL+="    UNION";
      SQL+="    SELECT";
      SQL+="      `dc_index`.`SOURCE`,";
      SQL+="      `dc_index`.`EDITOR`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE "+SQLWhere;
      SQL+="    UNION";
      SQL+="    SELECT";
      SQL+="      `dc_index`.`SOURCE`,";
      SQL+="      `dc_index`.`EDITOR`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `chm62edt_taxonomy` ON (`chm62edt_species`.`ID_TAXONOMY` = `chm62edt_taxonomy`.`ID_TAXONOMY`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_taxonomy`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE "+SQLWhere;
      SQL+="    GROUP BY dc_index.SOURCE,dc_index.EDITOR";
      SQL+="    ORDER BY `dc_index`.`SOURCE`";
      SQL+="    LIMIT 0,100";
    }
    if(natureobject.equalsIgnoreCase("Habitat")) {
      String isGoodHabitat = " IF(TRIM(chm62edt_habitat.CODE_2000) <> '',RIGHT(chm62edt_habitat.CODE_2000,2),1) <> IF(TRIM(chm62edt_habitat.CODE_2000) <> '','00',2) AND IF(TRIM(chm62edt_habitat.CODE_2000) <> '',LENGTH(chm62edt_habitat.CODE_2000),1) = IF(TRIM(chm62edt_habitat.CODE_2000) <> '',4,1) ";

      SQL="SELECT DISTINCT `dc_index`.`SOURCE`,`dc_index`.`EDITOR` FROM `chm62edt_habitat`";
      SQL+=" INNER JOIN `chm62edt_nature_object` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
      if(oper.equalsIgnoreCase("Equal")) {
        SQL+=" WHERE "+isGoodHabitat+" AND (`dc_index`.`SOURCE` = '"+ val + "') ORDER BY `dc_index`.`SOURCE`";
      } else {
        if(oper.equalsIgnoreCase("Contains")) {
          SQL+=" WHERE "+isGoodHabitat+" AND (`dc_index`.`SOURCE` LIKE '%"+ val + "%') ORDER BY `dc_index`.`SOURCE`";
        } else {
          if(oper.equalsIgnoreCase("Between")) {
            SQL+=" WHERE "+isGoodHabitat+" AND (`dc_index`.`SOURCE` = '"+ val + "') ORDER BY `dc_index`.`SOURCE`";
          } else {
            SQL+=" WHERE "+isGoodHabitat+" AND (`dc_index`.`SOURCE` LIKE '%"+ val + "%') ORDER BY `dc_index`.`SOURCE`";
          }
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("Title")) {
    if(natureobject.equalsIgnoreCase("Species")) {
      String SQLWhere="";
      if(oper.equalsIgnoreCase("Equal")) {
        SQLWhere=" (`dc_index`.`TITLE` = '"+ val + "')";
      } else {
        if(oper.equalsIgnoreCase("Contains")) {
          SQLWhere=" (`dc_index`.`TITLE` LIKE '%"+ val + "%')";
        } else {
          if(oper.equalsIgnoreCase("Between")) {
            SQLWhere=" (`dc_index`.`TITLE` = '"+ val + "')";
          } else {
            SQLWhere=" (`dc_index`.`TITLE` LIKE '%"+ val + "%')";
          }
        }
      }
      SQL+="    SELECT";
      SQL+="      `dc_index`.`TITLE`,";
      SQL+="      `dc_index`.`ALTERNATIVE`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `chm62edt_reports` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_reports`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `chm62edt_report_type` ON (`chm62edt_reports`.`ID_REPORT_TYPE` = `chm62edt_report_type`.`ID_REPORT_TYPE`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_reports`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE";
      SQL+="      (`chm62edt_report_type`.`LOOKUP_TYPE` IN ('DISTRIBUTION_STATUS','LANGUAGE','CONS_STATUS','SPECIES_GEO','LEGAL_STATUS','SPECIES_STATUS','POPULATION_UNIT','TREND'))";
      SQL+="    AND "+SQLWhere;
      SQL+="    UNION";
      SQL+="    SELECT";
      SQL+="      `dc_index`.`TITLE`,";
      SQL+="      `dc_index`.`ALTERNATIVE`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE "+SQLWhere;
      SQL+="    UNION";
      SQL+="    SELECT";
      SQL+="      `dc_index`.`TITLE`,";
      SQL+="      `dc_index`.`ALTERNATIVE`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE "+SQLWhere;
      SQL+="    UNION";
      SQL+="    SELECT";
      SQL+="      `dc_index`.`TITLE`,";
      SQL+="      `dc_index`.`ALTERNATIVE`";
      SQL+="    FROM";
      SQL+="      `chm62edt_species`";
      SQL+="      INNER JOIN `chm62edt_nature_object` ON (`chm62edt_species`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+="      INNER JOIN `chm62edt_taxonomy` ON (`chm62edt_species`.`ID_TAXONOMY` = `chm62edt_taxonomy`.`ID_TAXONOMY`)";
      SQL+="      INNER JOIN `dc_index` ON (`chm62edt_taxonomy`.`ID_DC` = `dc_index`.`ID_DC`)";
      SQL+="    WHERE "+SQLWhere;
      SQL+="    GROUP BY dc_index.SOURCE,dc_index.EDITOR";
      SQL+="    ORDER BY `dc_index`.`TITLE`";
      SQL+="    LIMIT 0,100";
    }

    if(natureobject.equalsIgnoreCase("Habitat")) {
      String isGoodHabitat = " IF(TRIM(chm62edt_habitat.CODE_2000) <> '',RIGHT(chm62edt_habitat.CODE_2000,2),1) <> IF(TRIM(chm62edt_habitat.CODE_2000) <> '','00',2) AND IF(TRIM(chm62edt_habitat.CODE_2000) <> '',LENGTH(chm62edt_habitat.CODE_2000),1) = IF(TRIM(chm62edt_habitat.CODE_2000) <> '',4,1) ";

      SQL="SELECT DISTINCT `dc_index`.`TITLE`,`dc_index`.`ALTERNATIVE` FROM `chm62edt_habitat`";
      SQL+=" INNER JOIN `chm62edt_nature_object` ON (`chm62edt_habitat`.`ID_NATURE_OBJECT` = `chm62edt_nature_object`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `dc_index` ON (`chm62edt_nature_object`.`ID_DC` = `dc_index`.`ID_DC`)";
      if(oper.equalsIgnoreCase("Equal")) {
        SQL+=" WHERE "+isGoodHabitat+" AND  (`dc_index`.`TITLE` = '"+ val + "') ORDER BY `dc_index`.`TITLE`";
      } else {
        if(oper.equalsIgnoreCase("Contains")) {
          SQL+=" WHERE "+isGoodHabitat+" AND  (`dc_index`.`TITLE` LIKE '%"+ val + "%') ORDER BY `dc_index`.`TITLE`";
        } else {
          if(oper.equalsIgnoreCase("Between")) {
            SQL+=" WHERE "+isGoodHabitat+" AND  (`dc_index`.`TITLE` = '"+ val + "') ORDER BY `dc_index`.`TITLE`";
          } else {
            SQL+=" WHERE "+isGoodHabitat+" AND  (`dc_index`.`TITLE` LIKE '%"+ val + "%') ORDER BY `dc_index`.`TITLE`";
          }
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("Taxonomy")) {
//    System.out.println("oper = " + oper);
    SQL="SELECT DISTINCT `chm62edt_taxonomy`.`NAME`,`chm62edt_taxonomy`.`LEVEL` FROM `chm62edt_species`";
    SQL+=" INNER JOIN `chm62edt_taxonomy` ON (`chm62edt_species`.`ID_TAXONOMY` = `chm62edt_taxonomy`.`ID_TAXONOMY`)";
    if(oper.equalsIgnoreCase("Equal")) {
      SQL+=" WHERE (`chm62edt_taxonomy`.`NAME` = '"+ val + "') ORDER BY `chm62edt_taxonomy`.`NAME`";
    } else {
      if(oper.equalsIgnoreCase("Contains")) {
        SQL+=" WHERE (`chm62edt_taxonomy`.`NAME` LIKE '%"+ val + "%') ORDER BY `chm62edt_taxonomy`.`NAME`";
      } else {
        if(oper.equalsIgnoreCase("Between")) {
          SQL+=" WHERE (`chm62edt_taxonomy`.`NAME` = '"+ val + "') ORDER BY `chm62edt_taxonomy`.`NAME`";
        } else {
          SQL+=" WHERE (`chm62edt_taxonomy`.`NAME` = '"+ val + "') ORDER BY `chm62edt_taxonomy`.`NAME`";
        }
      }
    }
  }

  if(lov.equalsIgnoreCase("SourceDatabase")) {
    if(natureobject.equalsIgnoreCase("Habitat")) {
      out.println("<a title=\"Click here to select the value\"  href=\"javascript:setValue('EUNIS')\">EUNIS</a><br />");
      out.println("<a title=\"Click here to select the value\" href=\"javascript:setValue('Annex I')\">Annex I</a><br />");
      out.println("<br />");
    }
    if(natureobject.equalsIgnoreCase("Sites")) {
      out.println("<a title=\"Click here to select the value\" href=\"javascript:setValue('Corine biotopes')\">Corine biotopes</a><br />");
      out.println("<a title=\"Click here to select the value\" href=\"javascript:setValue('Cdda National')\">Cdda National</a><br />");
      out.println("<a title=\"Click here to select the value\" href=\"javascript:setValue('Cdda International')\">Cdda International</a><br />");
      out.println("<a title=\"Click here to select the value\" href=\"javascript:setValue('Biogenetic reserves')\">Biogenetic reserves</a><br />");
      out.println("<a title=\"Click here to select the value\" href=\"javascript:setValue('European Diploma')\">European Diploma</a><br />");
      out.println("<a title=\"Click here to select the value\" href=\"javascript:setValue('Emerald')\">Emerald</a><br />");
      out.println("<br />");
    }
  }

  if(lov.equalsIgnoreCase("ScientificName")) {
    if(natureobject.equalsIgnoreCase("Species")) {
      SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
      SQL+=" WHERE SCIENTIFIC_NAME LIKE '%"+val+"%'";
      SQL+=" ORDER BY SCIENTIFIC_NAME";
      SQL+=" LIMIT 0,100";
      if(oper.equalsIgnoreCase("Equal")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
        SQL+=" WHERE SCIENTIFIC_NAME = '"+val+"'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
        SQL+=" WHERE SCIENTIFIC_NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Between")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,ID_SPECIES FROM chm62edt_species";
        SQL+=" WHERE SCIENTIFIC_NAME LIKE '%"+val+"%'";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
    }
    if(natureobject.equalsIgnoreCase("Habitat")) {
       String isGoodHabitat = " IF(TRIM(chm62edt_habitat.CODE_2000) <> '',RIGHT(chm62edt_habitat.CODE_2000,2),1) <> IF(TRIM(chm62edt_habitat.CODE_2000) <> '','00',2) AND IF(TRIM(chm62edt_habitat.CODE_2000) <> '',LENGTH(chm62edt_habitat.CODE_2000),1) = IF(TRIM(chm62edt_habitat.CODE_2000) <> '',4,1) ";

      SQL="SELECT DISTINCT SCIENTIFIC_NAME,CONCAT(IF(CODE_ANNEX1 IS NULL,'',CODE_ANNEX1),IF(EUNIS_HABITAT_CODE IS NULL,'',EUNIS_HABITAT_CODE)) FROM chm62edt_habitat";
      SQL+=" WHERE  "+isGoodHabitat+" AND  SCIENTIFIC_NAME LIKE '%"+val+"%' AND chm62edt_habitat.ID_HABITAT<>'-1' AND chm62edt_habitat.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + " ' ";
      SQL+=" ORDER BY SCIENTIFIC_NAME";
      SQL+=" LIMIT 0,100";
      if(oper.equalsIgnoreCase("Contains")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,CONCAT(IF(CODE_ANNEX1 IS NULL,'',CODE_ANNEX1),IF(EUNIS_HABITAT_CODE IS NULL,'',EUNIS_HABITAT_CODE)) FROM chm62edt_habitat";
        SQL+=" WHERE  "+isGoodHabitat+" AND SCIENTIFIC_NAME LIKE '%"+val+"%'  AND chm62edt_habitat.ID_HABITAT<>'-1' AND chm62edt_habitat.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "' ";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Equal")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,CONCAT(IF(CODE_ANNEX1 IS NULL,'',CODE_ANNEX1),IF(EUNIS_HABITAT_CODE IS NULL,'',EUNIS_HABITAT_CODE)) FROM chm62edt_habitat";
        SQL+=" WHERE  "+isGoodHabitat+" AND SCIENTIFIC_NAME = '"+val+"'  AND chm62edt_habitat.ID_HABITAT<>'-1' AND chm62edt_habitat.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "' ";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
      if(oper.equalsIgnoreCase("Between")) {
        SQL="SELECT DISTINCT SCIENTIFIC_NAME,CONCAT(IF(CODE_ANNEX1 IS NULL,'',CODE_ANNEX1),IF(EUNIS_HABITAT_CODE IS NULL,'',EUNIS_HABITAT_CODE)) FROM chm62edt_habitat";
        SQL+=" WHERE  "+isGoodHabitat+" AND SCIENTIFIC_NAME LIKE '%"+val+"%'  AND chm62edt_habitat.ID_HABITAT<>'-1' AND chm62edt_habitat.ID_HABITAT<>'" + Constants.HABITAT_ANNEX1_ROOT + "' ";
        SQL+=" ORDER BY SCIENTIFIC_NAME";
        SQL+=" LIMIT 0,100";
      }
    }
  }

  if(lov.equalsIgnoreCase("Name")) {
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

  if(lov.equalsIgnoreCase("VernacularName")){
    SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE,' ' FROM chm62edt_reports ";
    SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
    SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
    SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
    SQL+=" AND (chm62edt_report_attributes.VALUE LIKE '%"+val+"%')";
    SQL+=" LIMIT 0,100";
    if(oper.equalsIgnoreCase("Contains")) {
      SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE,' ' FROM chm62edt_reports ";
      SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
      SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
      SQL+=" AND (chm62edt_report_attributes.VALUE LIKE '%"+val+"%')";
      SQL+=" LIMIT 0,100";
    }
    if(oper.equalsIgnoreCase("Equal")) {
      SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE,' ' FROM chm62edt_reports ";
      SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
      SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
      SQL+=" AND (chm62edt_report_attributes.VALUE = '"+val+"')";
      SQL+=" LIMIT 0,100";
    }
    if(oper.equalsIgnoreCase("Between")) {
      SQL="SELECT DISTINCT chm62edt_report_attributes.VALUE,' ' FROM chm62edt_reports ";
      SQL+=" INNER JOIN `chm62edt_species` ON (`chm62edt_reports`.`ID_NATURE_OBJECT` = `chm62edt_species`.`ID_NATURE_OBJECT`)";
      SQL+=" INNER JOIN `chm62edt_report_attributes` ON (`chm62edt_reports`.`ID_REPORT_ATTRIBUTES` = `chm62edt_report_attributes`.`ID_REPORT_ATTRIBUTES`)";
      SQL+=" WHERE (`chm62edt_report_attributes`.`NAME` = 'VERNACULAR_NAME')";
      SQL+=" AND (chm62edt_report_attributes.VALUE LIKE '%"+val+"%')";
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

  try {
    // Execute SQL statement
    if(SQL.length()>0 && !lov.equalsIgnoreCase("SourceDatabase")) {
      //System.out.println("SQL = " + SQL);
      ps = con.createStatement();
      rs = ps.executeQuery(SQL);

      if(!rs.isBeforeFirst()) {
        out.println("<strong>"+cm.cmsPhrase("No results found")+"</strong>");
        out.println("<br />");
      } else {
        %>
        <h2><%=cm.cmsPhrase("List of values")%>:</h2>
        <br />
        <strong>
        <%=cm.cmsPhrase("(Warning: Only the first 100 values are retrieved from the database)")%>
        </strong>
        <br />
        <br />
        <u><%=ro.finsiel.eunis.search.Utilities.SplitString(lov)%></u>
        <em><%=oper%></em>
        <strong><%=val.length()==0?"%":val%></strong>
        <br />
        <br />
        <div id="tab">
        <table summary="layout" class="datatable">
          <tr>
            <th>
              Value
            </th>
            <th>
              Code
            </th>
          </tr>
<%
        int cnt = 0;
        while(rs.next())
        {
          String cssClass = cnt++ % 2 == 0 ? "" : " class=\"zebraeven\"";
%>
          <tr<%=cssClass%>>
            <td>
            <%
            out.println("<a href=\"javascript:setValue('"+ Utilities.treatURLSpecialCharacters(rs.getString(1))+"')\">"+Utilities.treatURLSpecialCharacters(rs.getString(1))+"</a>");
            %>
            </td>
            <td>
            <%
            if(rs.getString(2) != null && rs.getString(2).length()>0) {
              out.println(rs.getString(2));
            } else {
              out.println("&nbsp;");
            }
            %>
            </td>
          </tr>
          <%
        }
        %>
        </table>
        </div>
        <%
      }
      rs.close();
      ps.close();
      con.close();
    } else {
        if(!lov.equalsIgnoreCase("SourceDatabase")) {
          out.println("<strong>"+cm.cmsPhrase("No list of values available")+"</strong>");
          out.println("<br />");
        }
    }
  } catch (Exception e) {

    //System.out.println(e.toString());
    out.println("<strong>"+cm.cmsPhrase("Could not retrieve list of values")+"</strong>");
    out.println("<br />");
  }
%>
    <br />
    <form action="">
      <input type="button" title="<%=cm.cmsPhrase("Close window")%>" value="<%=cm.cmsPhrase("Close")%>" onclick="javascript:window.close()" name="btnclose" id="btnclose" class="standardButton" />
    </form>
  </body>
</html>
