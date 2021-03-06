/*
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is jRelationalFramework.
 *
 * The Initial Developer of the Original Code is is.com.
 * Portions created by is.com are Copyright (C) 2000 is.com.
 * All Rights Reserved.
 *
 * Contributor: Jonathan Carlson (joncrlsn@users.sf.net)
 * Contributor: ____________________________________
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License (the "GPL") or the GNU Lesser General
 * Public license (the "LGPL"), in which case the provisions of the GPL or
 * LGPL are applicable instead of those above.  If you wish to allow use of
 * your version of this file only under the terms of either the GPL or LGPL
 * and not to allow others to use your version of this file under the MPL,
 * indicate your decision by deleting the provisions above and replace them
 * with the notice and other provisions required by either the GPL or LGPL
 * License.  If you do not delete the provisions above, a recipient may use
 * your version of this file under either the MPL or GPL or LGPL License.
 *
 */
package net.sf.jrf.exceptions;

import java.sql.SQLException;

import org.apache.log4j.Category;

/**
 * This exception is a wrapper for errors thrown by the JDBC driver.  Since
 * it is a subclass of Runtime exception, it is not declared in the throws
 * clauses of the framework.
 */
public class DatabaseException
     extends RuntimeException
{

    private final static Category LOG =
        Category.getInstance(DatabaseException.class.getName());

    // contains the embedded exception, if any
    private Exception i_originalException = null;


    // making the empty constructor private makes sure we get a message
    private DatabaseException()
    {
        super();
    }

    /**
     *Constructor for the DatabaseException object
     *
     * @param message  Description of the Parameter
     */
    public DatabaseException(String message)
    {
        super(message);
    }

    /**
     *Constructor for the DatabaseException object
     *
     * @param e  Description of the Parameter
     */
    public DatabaseException(Exception e)
    {
        super(e.getMessage());
        i_originalException = e;
        LOG.error(
            this.getClass().getName() + " is wrapping " + this.getMessage(),
            e);
    }

    /**
     *Constructor for the DatabaseException object
     *
     * @param e        Description of the Parameter
     * @param message  Description of the Parameter
     */
    public DatabaseException(Exception e, String message)
    {
        super(message + ":\n " + e.getMessage());
        i_originalException = e;
        LOG.error(
            this.getClass().getName() + " is wrapping " + this.getMessage(),
            e);
    }

    /**
     * Gets the originalException attribute of the DatabaseException object
     *
     * @return   The originalException value
     */
    public Exception getOriginalException()
    {
        return i_originalException;
    }


    /**
     * Gets the message attribute of the DatabaseException object
     *
     * @return   The message value
     */
    public String getMessage()
    {
        if (i_originalException == null)
        {
            return super.getMessage();
        }
        else
        {
            return i_originalException + ": " + super.getMessage();
        }
    }

}// DatabaseException
