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
package net.sf.jrf.column.columnspecs;

import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.sql.Types;
import net.sf.jrf.*;
import net.sf.jrf.DatabasePolicy;
import net.sf.jrf.column.*;

import net.sf.jrf.domain.PersistentObject;
import net.sf.jrf.exceptions.*;
import net.sf.jrf.join.*;
import net.sf.jrf.join.joincolumns.IntegerJoinColumn;
import net.sf.jrf.sql.*;

/** This subclass of AbstractColumnSpec does Integer-specific things. */
public class IntegerColumnSpec
     extends NumericColumnSpec
{

    /* ===============  Static Variables  =============== */
    protected final static Class s_class = Integer.class;


    /* ===============  Constructors  =============== */
    /** Default constructor * */
    public IntegerColumnSpec()
    {
        super();
    }

    /**
     * Constructs a <code>IntegerColumnSpec</code> with column options assuming
     * reflection will be used for getting and setting values in the <code>PersistentObject</code>.
     *
     * @param columnName    column name.
     * @param columnOption  <code>ColumnOption</code> instance to use.
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  value to use for default if column value is null.
     */
    public IntegerColumnSpec(String columnName, ColumnOption columnOption, String getter, String setter,
        Object defaultValue)
    {
        super(columnName, columnOption, getter, setter, defaultValue);
    }

    /**
     * Constructs a <code>IntegerColumnSpec</code> with column options and a <code>GetterSetter</code> instance.
     *
     * @param columnName        column name.
     * @param columnOption      <code>ColumnOption</code> instance to use.
     * @param defaultValue      value to use for default if column value is null.
     * @param getterSetter      Description of the Parameter
     */
    public IntegerColumnSpec(String columnName, ColumnOption columnOption, GetterSetter getterSetter, Object defaultValue)
    {
        super(columnName, columnOption, getterSetter, defaultValue);
    }


    /**
     * Constructs a <code>IntegerColumnSpec</code> with three option values.
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
    public IntegerColumnSpec(
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
     * Constructs a <code>IntegerColumnSpec</code> with no option values supplied.
     *
     * @param columnName    name of the column
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  default value when the return value from the "getter" is null.
     * @deprecated          Use <code>ColumnOption</code> constructors as an alternative.
     */
    public IntegerColumnSpec(
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
     * Constructs a <code>IntegerColumnSpec</code> with one option value.
     *
     * @param columnName    name of the column
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  default value when the return value from the "getter" is null.
     * @param option1       One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @deprecated          Use <code>ColumnOption</code> constructors as an alternative.
     */
    public IntegerColumnSpec(
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
     * Constructs a <code>IntegerColumnSpec</code> with two option values.
     *
     * @param columnName    name of the column
     * @param getter        name of the method to get the column data from the <code>PersistentObject</code>
     * @param setter        name of the method to set column data in the <code>PersistentObject</code>.
     * @param defaultValue  default value when the return value from the "getter" is null.
     * @param option1       One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option2       Description of the Parameter
     * @deprecated          Use <code>ColumnOption</code> constructors as an alternative.
     */
    public IntegerColumnSpec(
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
     * Constructs a <code>IntegerColumnSpec</code> with three option values and a <code>GetterSetter</code> implementation.
     *
     * @param columnName        name of the column
     * @param getterSetterImpl  an implementation of <code>GetterSetter</code>.
     * @param defaultValue      default value when the return value from the "getter" is null.
     * @param option1           One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option2           One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @param option3           One the six column option constants from <code>JRFConstants</code> or zero to denote no option.
     * @deprecated              Use <code>ColumnOption</code> constructors as an alternative.
     */
    public IntegerColumnSpec(
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
     * Called from the super-class constructors, initializes column parameters with default values
     * of <code>11</code> for the precision, <code>0</code> for the scale and
     * <code>java.sql.Types.INTEGER</code> for the type.
     */
    protected void initColumnParameters()
    {
        this.i_precision = 11;
        this.i_scale = 0;
        this.i_sqlType = java.sql.Types.INTEGER;
    }

    /**
     * This method overrides the superclass implementation.
     *
     * @param obj       a value of type 'Object'
     * @param dbPolicy  a value of type 'DatabasePolicy'
     * @return          a value of type 'String'
     */
    public String formatForSql(Object obj, DatabasePolicy dbPolicy)
    {
        Integer integer = (Integer) obj;// force a runtime error if not an Integer
        return (integer == null ? "null" : integer.toString());
    }// formatForSql(...)


    /**
     * Gets the columnClass attribute of the IntegerColumnSpec object
     *
     * @return   The columnClass value
     */
    public Class getColumnClass()
    {
        return s_class;
    }

    /**
     * This ensures we get an Integer (instead of a BigInteger or something
     * else) from the JDBC driver.
     *
     * @param jrfResultSet      <code>JRFResultSet</code> encapsulating an active <code>java.sql.ResultSet</code>.
     * @return                  a value of type 'Object'
     * @exception SQLException  if an error occurs
     */
    public Object getColumnValueFrom(JRFResultSet jrfResultSet)
        throws SQLException
    {
        return jrfResultSet.getInteger(this.getColumnIdx());
    }

    /**
     * This method goes with encode().  The String parameter must have been
     * created by the encode() method.
     *
     * @param aString  a value of type 'String'
     * @return         Description of the Return Value
     */
    public Object decode(String aString)
    {
        Integer returnValue = null;
        if (aString.trim().equals("null"))
        {
            return null;
        }
        try
        {
            returnValue = Integer.valueOf(aString);
        }
        catch (NumberFormatException e)
        {
            throw new DatabaseException(
                "Invalid parameter to IntegerColumnSpec.getValueFrom(" + aString + ")");
        }
        return returnValue;
    }

    /**
     * This is an override that converts Longs into Integers.  This is needed
     * for sequenced primary keys that are Integers.
     *
     * @param aValue  a value of type 'Object'
     * @param aPO     a value of type 'PersistentObject'
     */
    public void setValueTo(Object aValue,
        PersistentObject aPO)
    {
        if (aValue instanceof Long)
        {
            Long aLong = (Long) aValue;
            aValue = new Integer(aLong.intValue());
        }
        super.setValueTo(aValue, aPO);
    }


    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public Object optimisticLockDefaultValue()
    {
        return new Integer(0);
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public JoinColumn buildJoinColumn()
    {
        return new IntegerJoinColumn(this.getColumnName(),
            this.getSetter());
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
        stmt.setInteger(value, position);
    }

}// IntegerColumnSpec
