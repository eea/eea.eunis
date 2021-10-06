package ro.finsiel.eunis;

import javax.servlet.http.HttpServlet;

/**
 * Servlet to initialize the settings (on load)
 */
public class InitServlet  extends HttpServlet {
    public void init() {
        Settings.loadSettings(this);
    }
}
