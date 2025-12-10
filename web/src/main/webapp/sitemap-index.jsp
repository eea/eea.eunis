<%
  response.setContentType("application/xml;charset=UTF-8");

  java.sql.Connection con = null;
  java.sql.Statement ps = null;
  java.sql.ResultSet rs = null;

  int batch_size = 50000;
  int count;
  int start;

  try {
    con = ro.finsiel.eunis.utilities.TheOneConnectionPool.getConnection();
  }
  catch(Exception e) {

    return;
  }

  out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
  out.println("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
  out.println("<sitemap><loc>http://eunis.eea.europa.eu/sitemap-files.xml</loc></sitemap>");

  try {
    ps = con.createStatement();

    // Species
    rs = ps.executeQuery("SELECT COUNT(*) FROM chm62edt_species WHERE ID_SPECIES=ID_SPECIES_LINK");
    rs.first();
    start = 0;
    count = rs.getInt(1);
    while (count > 0) {
      out.println("<sitemap><loc>" + application.getInitParameter( "DOMAIN_NAME" ) + "/sitemap-species.jsp?start=" + start +
                  "&amp;size=" + batch_size + "</loc></sitemap>");
      count -= batch_size;
      start += batch_size;
    }
    rs.close();


    // Sites
    rs = ps.executeQuery("SELECT COUNT(*) FROM chm62edt_sites");
    rs.first();
    start = 0;
    count = rs.getInt(1);
    while (count > 0) {
      out.println("<sitemap><loc>" + application.getInitParameter( "DOMAIN_NAME" ) + "/sitemap-sites.jsp?start=" + start +
                  "&amp;size=" + batch_size + "</loc></sitemap>");
      count -= batch_size;
      start += batch_size;
    }
    rs.close();
    
 	// Habitats
    rs = ps.executeQuery("SELECT COUNT(*) FROM chm62edt_habitat");
    rs.first();
    start = 0;
    count = rs.getInt(1);
    while (count > 0) {
      out.println("<sitemap><loc>" + application.getInitParameter( "DOMAIN_NAME" ) + "/sitemap-habitats.jsp?start=" + start +
                  "&amp;size=" + batch_size + "</loc></sitemap>");
      count -= batch_size;
      start += batch_size;
    }
    rs.close();
    con.close();

  } catch (Exception e) {
    response.setContentType("text/plain;charset=UTF-8");

  }
  out.println("</sitemapindex>");
%>
