<%
  response.setContentType("application/rdf+xml;charset=UTF-8");

  String start = request.getParameter("start");
  if(start==null || start.length()==0) {
    start = "0";
  }
  int start_from = new Integer(start).intValue();

  String size = request.getParameter("size");
  if(size==null || size.length()==0) {
    size="300000";
  }
  int nr = new Integer(size).intValue();
  if(nr==0) {
    return;
  }

  java.sql.Connection con = null;
  java.sql.Statement ps = null;
  java.sql.ResultSet rs = null;

  try {
    con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
  }
  catch(Exception e) {

    return;
  }

  out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
  try {
    ro.finsiel.eunis.myXML root = new ro.finsiel.eunis.myXML("rdf:RDF");
    root.Attribute.add("xmlns:rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    root.Attribute.add("xmlns:rdfs","http://www.w3.org/2000/01/rdf-schema#");
    root.Attribute.add("xmlns:dwc","http://rs.tdwg.org/dwc/terms/");

    String strSQL = "SELECT a.ID_SPECIES,a.SCIENTIFIC_NAME,a.GENUS, a.AUTHOR FROM chm62edt_species AS a";
    strSQL = strSQL + " WHERE VALID_NAME=1";
    strSQL = strSQL + " LIMIT " + start_from + "," + nr;

    ps = con.createStatement();
    rs = ps.executeQuery(strSQL);

    String spec_id;
    String spec_scientific_name;
    String spec_lang;
    String spec_name;
    String author;

    ro.finsiel.eunis.myXML taxon;

    while(rs.next()) {
      spec_id = rs.getString("ID_SPECIES");
      spec_scientific_name = rs.getString("SCIENTIFIC_NAME");
      spec_scientific_name = spec_scientific_name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
      author = rs.getString("AUTHOR").replace("&", "&amp;").replace("<", "&lt;");

      taxon = root.addElement("dwc:Taxon");
      taxon.Attribute.add("rdf:about","http://eunis.eea.europa.eu/species/"+spec_id);

      taxon.addElement("rdfs:label",spec_scientific_name + ", " + author);
      taxon.addElement("dwc:scientificName",spec_scientific_name);
      taxon.addElement("dwc:identifiedBy", author);
      taxon.addElement("dwc:genus", rs.getString("GENUS").replace("&", "&amp;").replace("<", "&lt;"));

    }


    con.close();

    String sxml = root.serialize();

    out.print(sxml);

  } catch (Exception e) {

  }%>
