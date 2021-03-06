<%
  response.setContentType("text/xml;charset=UTF-8");

  String start = request.getParameter("start");
  if(start==null || start.length()==0) {
    return;
  }
  int start_from = new Integer(start).intValue();

  String size = request.getParameter("size");
  if(size==null || size.length()==0) {
    return;
  }
  int nr = new Integer(size).intValue();
  if(nr==0) {
    return;
  }

  java.sql.Connection con = null;
  java.sql.Statement ps = null;
  java.sql.Statement psNames = null;
  java.sql.Statement psGeo = null;
  java.sql.ResultSet rs = null;
  java.sql.ResultSet rsNames = null;
  java.sql.ResultSet rsGeo = null;

  try {
    con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
  }
  catch(Exception e) {
    e.printStackTrace();
    return;
  }

  out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
  try {
    ro.finsiel.eunis.myXML root = new ro.finsiel.eunis.myXML("rdf:RDF");
    root.Attribute.add("xmlns:rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    root.Attribute.add("xmlns:skos","http://www.w3.org/2004/02/skos/core#");

    String strSQL = "SELECT a.ID_SPECIES,a.SCIENTIFIC_NAME,b.COMMON_NAME FROM chm62edt_species AS a";
    strSQL = strSQL + " LEFT OUTER JOIN chm62edt_group_species b ON a.ID_GROUP_SPECIES = b.ID_GROUP_SPECIES";
    strSQL = strSQL + " LIMIT " + start_from + "," + nr;

    ps = con.createStatement();
    rs = ps.executeQuery(strSQL);

    String spec_id;
    String spec_scientific_name;
    String spec_group;
    java.util.HashMap languages = new java.util.HashMap();
    String spec_lang;
    String spec_name;

    ro.finsiel.eunis.myXML concept;

    while(rs.next()) {
      spec_id = rs.getString("ID_SPECIES");
      spec_scientific_name = rs.getString("SCIENTIFIC_NAME");
      spec_scientific_name = spec_scientific_name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
      spec_group = rs.getString("COMMON_NAME");
      spec_group = spec_group.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");

      concept = root.addElement("skos:Concept");
      concept.Attribute.add("rdf:about","http://eunis.eea.eu.int/species/"+spec_id);

      ro.finsiel.eunis.myXML preflabel  = concept.addElement("skos:prefLabel",spec_scientific_name);
      preflabel.Attribute.add("xml:lang","la");

      //insert common names
      String strSQLNames = "SELECT distinct e.code ,c.value";
      strSQLNames = strSQLNames + " from chm62edt_species as a";
      strSQLNames = strSQLNames + " inner join chm62edt_reports as b on a.id_nature_object = b.id_nature_object";
      strSQLNames = strSQLNames + " inner join chm62edt_report_attributes as c on (b.id_report_attributes = c.id_report_attributes and c.name='VERNACULAR_NAME')";
      strSQLNames = strSQLNames + " inner join chm62edt_report_type as d on (b.id_report_type = d.id_report_type and d.lookup_type='language')";
      strSQLNames = strSQLNames + " inner join chm62edt_language as e on d.id_lookup = e.id_language";
      strSQLNames = strSQLNames + " where A.ID_SPECIES = " + spec_id;

      psNames = con.createStatement();
      rsNames = psNames.executeQuery(strSQLNames);

      languages.clear();

      while(rsNames.next()) {
        spec_lang = rsNames.getString("code");
        spec_name = rsNames.getString("value");
        spec_name = spec_name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");

        if(!languages.containsKey(spec_lang)) {
          preflabel  = concept.addElement("skos:prefLabel",spec_name);
          preflabel.Attribute.add("xml:lang",spec_lang);
          languages.put(spec_lang, null);
        } else {
          ro.finsiel.eunis.myXML altlabel  = concept.addElement("skos:altLabel",spec_name);
          altlabel.Attribute.add("xml:lang",spec_lang);
        }
      }

      rsNames.close();
      psNames.close();

      //insert geographical distribution
      String strSQLGeo = "SELECT DISTINCT chm62edt_country.AREA_NAME_EN";
      strSQLGeo = strSQLGeo + " FROM chm62edt_reports, chm62edt_report_type, chm62edt_country, chm62edt_species_status, chm62edt_species";
      strSQLGeo = strSQLGeo + " WHERE chm62edt_reports.ID_REPORT_TYPE = chm62edt_report_type.ID_REPORT_TYPE";
      strSQLGeo = strSQLGeo + " AND chm62edt_reports.ID_GEOSCOPE = chm62edt_country.ID_GEOSCOPE";
      strSQLGeo = strSQLGeo + " AND chm62edt_report_type.LOOKUP_TYPE = 'SPECIES_STATUS'";
      strSQLGeo = strSQLGeo + " AND chm62edt_report_type.ID_LOOKUP = chm62edt_species_status.ID_SPECIES_STATUS";
      strSQLGeo = strSQLGeo + " AND chm62edt_country.AREA_NAME_EN NOT LIKE 'ospar%'";
      strSQLGeo = strSQLGeo + " AND chm62edt_country.AREA_NAME_EN IS NOT NULL";
      strSQLGeo = strSQLGeo + " AND chm62edt_reports.ID_NATURE_OBJECT = chm62edt_species.ID_NATURE_OBJECT";
      strSQLGeo = strSQLGeo + " AND chm62edt_species.ID_SPECIES = " + spec_id;
      strSQLGeo = strSQLGeo + " ORDER BY chm62edt_country.AREA_NAME_EN ASC";

      psGeo = con.createStatement();
      rsGeo = psGeo.executeQuery(strSQLGeo);

      String geo_distribution = " It is geographically distrubuted among the following countries/areas: ";
      boolean geo_found = false;
      while(rsGeo.next()) {
        geo_found = true;
        geo_distribution += rsGeo.getString("AREA_NAME_EN");
        if(!rsGeo.isLast()) {
          geo_distribution += ", ";
        }
      }
      geo_distribution += ".";

      rsGeo.close();
      psGeo.close();

      ro.finsiel.eunis.myXML definition;
      
      if(geo_found) {
        definition  = concept.addElement("skos:definition", spec_scientific_name + " belongs to the " + spec_group + " group." + geo_distribution);
      } else {
        definition  = concept.addElement("skos:definition", spec_scientific_name + " belongs to the " + spec_group + " group.");
      }

      definition.Attribute.add("xml:lang","en");
    }

    rsNames.close();
    psNames.close();

    con.close();

    String sxml = root.serialize();

    out.print(sxml);

  } catch (Exception e) {
    e.printStackTrace();
  }%>
