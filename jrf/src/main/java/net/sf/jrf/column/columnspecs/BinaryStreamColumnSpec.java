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
 * Contributor: James Evans (jevans@vmguys.com)
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
package net.sf.jrf.column.columnspecs;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import net.sf.jrf.*;

import net.sf.jrf.column.*;
import net.sf.jrf.column.datawrappers.InputStreamWrapper;
import net.sf.jrf.join.*;
import net.sf.jrf.sql.*;
import org.apache.log4j.Category;
/** This subclass of AbstractColumnSpec does String-specific things. */
public class BinaryStreamColumnSpec
     extends BinaryColumnSpec
{

    final static Category LOG = Category.getInstance(BinaryStreamColumnSpec.class.getName());

    /* ===============  Static Variables  =============== */
    protected static Class s_class = InputStreamWrapper.class;

    protected boolean cloneDataFromResultSet = false;

    /* ===============  Constructors  =============== */
    /** Default Constructor * */
    public BinaryStreamColumnSpec()
    {
        super();
    }

    /**
     * Constructs a <code>BinaryStreamColumnSpec</code> with column options assuming
     * reflection will be used for getting and setting values in the <code>PersistentObject</code>.
     *
     * @param columnName    column name.
     * @param columnOption  <code>ColumnOption</code> instance to use.
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  value to use for default if column value is null.
     */
    public BinaryStreamColumnSpec(String columnName, ColumnOption columnOption, String getter, String setter,
        Object defaultValue)
    {
        super(columnName, columnOption, getter, setter, defaultValue);
    }

    /**
     * Constructs an <code>BinaryStreamColumnSpec</code> with column options and a <code>GetterSetter</code> instance.
     *
     * @param columnName        column name.
     * @param columnOption      <code>ColumnOption</code> instance to use.
     * @param defaultValue      value to use for default if column value is null.
     * @param getterSetter      Description of the Parameter
     */
    public BinaryStreamColumnSpec(String columnName, ColumnOption columnOption, GetterSetter getterSetter, Object defaultValue)
    {
        super(columnName, columnOption, getterSetter, defaultValue);
    }


    /**
     * Constructs an <code>BinaryStreamColumnSpec</code> with three option values.
     *
     * @param columnName    name of the column
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  default value when the return value from the "getter" is null.
     * @param option1       One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option2       One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option3       One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @deprecated          Use <code>ColumnOption</code> constructors as an alternative.
     */
    public BinaryStreamColumnSpec(
        String columnName,
        String getter,
        String setter,
        Object defaultValue,
        int option1,
        int option2,
        int option3)
    {
        super(columnName, getter, setter, defaultValue, option1, option2, option3);
    }

    /**
     * Constructs an <code>BinaryStreamColumnSpec</code> with no option values supplied.
     *
     * @param columnName    name of the column
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  default value when the return value from the "getter" is null.
     * @deprecated          Use <code>ColumnOption</code> constructors as an alternative.
     */
    public BinaryStreamColumnSpec(
        String columnName,
        String getter,
        String setter,
        Object defaultValue)
    {
        super(columnName,
            getter,
            setter,
            defaultValue,
            0,
            0,
            0);
    }

    /**
     * Constructs an <code>BinaryStreamColumnSpec</code> with one option value.
     *
     * @param columnName    name of the column
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  default value when the return value from the "getter" is null.
     * @param option1       One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @deprecated          Use <code>ColumnOption</code> constructors as an alternative.
     */
    public BinaryStreamColumnSpec(
        String columnName,
        String getter,
        String setter,
        Object defaultValue,
        int option1)
    {
        super(columnName,
            getter,
            setter,
            defaultValue,
            option1,
            0,
            0);
    }

    /**
     * Constructs an <code>BinaryStreamColumnSpec</code> with two option values.
     *
     * @param columnName    name of the column
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  default value when the return value from the "getter" is null.
     * @param option1       One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option2       Description of the Parameter
     * @deprecated          Use <code>ColumnOption</code> constructors as an alternative.
     */
    public BinaryStreamColumnSpec(
        String columnName,
        String getter,
        String setter,
        Object defaultValue,
        int option1,
        int option2)
    {
        this(columnName,
            getter,
            setter,
            defaultValue,
            option1,
            option2,
            0);
    }

    /**
     * Constructs an <code>BinaryStreamColumnSpec</code> with three option values and a <code>GetterSetter</code> implementation.
     *
     * @param columnName        name of the column
     * @param getterSetterImpl  an implementation of <code>GetterSetter</code>.
     * @param defaultValue      default value when the return value from the "getter" is null.
     * @param option1           One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option2           One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option3           One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @deprecated              Use <code>ColumnOption</code> constructors as an alternative.
     */
    public BinaryStreamColumnSpec(
        String columnName,
        GetterSetter getterSetterImpl,
        Object defaultValue,
        int option1,
        int option2,
        int option3)
    {
        super(columnName, getterSetterImpl, defaultValue, option1, option2, option3);

    }

    /**
     * Gets the columnClass attribute of the BinaryStreamColumnSpec object
     *
     * @return   The columnClass value
     */
    public Class getColumnClass()
    {
        return s_class;
    }


    /**
     * Return a String from JDBCHelper.  If String is one blank, return an
     * empty string.  This resolves a strange issue with SQLServer (and maybe
     * Sybase) that keeps returning empty or whitespace strings as one blank
     * character.  If one blank is truly desired, then maybe this should go
     * into the DatabasePolicy, but then we'll have differing behavior when
     * switching databases.
     *
     * @param jrfResultSet      <code>JRFResultSet</code> encapsulating an active <code>java.sql.ResultSet</code>.
     * @return                  a value of type 'Object'
     * @exception SQLException  if an error occurs in JDBCHelper
     */
    public Object getColumnValueFrom(JRFResultSet jrfResultSet)
        throws SQLException
    {
        InputStream data = jrfResultSet.getBinaryStream(this.getColumnIdx());
        InputStreamWrapper w = new InputStreamWrapper(data);
        return w;
    }


    /**
     * @param stmt              The new preparedColumnValueTo value
     * @param value             The new preparedColumnValueTo value
     * @param position          The new preparedColumnValueTo value
     * @exception SQLException  Description of the Exception
     * @see                     net.sf.jrf.column.ColumnSpec#setPreparedColumnValueTo(JRFPreparedStatement,Object,int)
     */
    public void setPreparedColumnValueTo(JRFPreparedStatement stmt, Object value, int position)
        throws SQLException
    {
        InputStreamWrapper w = (InputStreamWrapper) value;
        stmt.setBinaryStream(w.getStream(), position, w.getLength(), super.i_sqlType);
    }

}// StringColumnSpec
