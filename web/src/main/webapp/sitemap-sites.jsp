<%
  response.setContentType("application/xml;charset=UTF-8");

  String start = request.getParameter("start");
  if(start==null || start.length()==0) {
    start = "0";
  }
  int start_from = new Integer(start).intValue();

  String size = request.getParameter("size");
  if(size==null || size.length()==0) {
    size="50000";
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

  out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
  out.println("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

  try {
    String strSQL = "SELECT ID_SITE FROM chm62edt_sites";
    strSQL = strSQL + " LIMIT " + start_from + "," + nr;

    ps = con.createStatement();
    rs = ps.executeQuery(strSQL);

    while(rs.next()) {
      out.println("<url><loc>" + application.getInitParameter( "DOMAIN_NAME" ) + "/sites/" + rs.getString("ID_SITE") + "</loc></url>");
    }

    con.close();

  } catch (Exception e) {
    response.setContentType("text/plain;charset=UTF-8");

  }
  out.println("</urlset>");
%>
