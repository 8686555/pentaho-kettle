/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.trans.steps.formula;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class RowForumulaContext implements FormulaContext {
  private RowMetaInterface rowMeta;
  private FormulaContext formulaContext;
  private Map<String, Integer> valueIndexMap;
  private Object[] rowData;

  public RowForumulaContext( RowMetaInterface row ) {
    this.formulaContext = new DefaultFormulaContext();
    this.rowMeta = row;
    this.rowData = null;
    this.valueIndexMap = new Hashtable<>();
  }

  public Type resolveReferenceType( Object name ) {
    if ( name instanceof String ) {
      ValueMetaInterface valueMeta = this.rowMeta.searchValueMeta( (String) name );
      if ( valueMeta != null ) {
        switch ( valueMeta.getType() ) {
          case ValueMetaInterface.TYPE_STRING:
            return TextType.TYPE;
          case ValueMetaInterface.TYPE_INTEGER:
          case ValueMetaInterface.TYPE_BIGNUMBER:
          case ValueMetaInterface.TYPE_NUMBER:
            return NumberType.GENERIC_NUMBER;
          default:
            return AnyType.TYPE;
        }
      }
    }
    return AnyType.TYPE;
  }

  /**
   * We return the content of a Value with the given name. We cache the position of the field indexes.
   *
   * @see org.jfree.formula.FormulaContext#resolveReference(java.lang.Object)
   */
  public Object resolveReference( Object name ) throws EvaluationException {
    if ( name instanceof String ) {
      ValueMetaInterface valueMeta;
      Integer idx = valueIndexMap.get( name );
      if ( idx != null ) {
        valueMeta = rowMeta.getValueMeta( idx.intValue() );
      } else {
        int index = rowMeta.indexOfValue( (String) name );
        if ( index < 0 ) {
          ErrorValue errorValue = new LibFormulaErrorValue( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT );
          throw new EvaluationException( errorValue );
        }
        valueMeta = rowMeta.getValueMeta( index );
        idx = new Integer( index );
        valueIndexMap.put( (String) name, idx );
      }
      Object valueData = rowData[idx];
      try {
        return getPrimitive( valueMeta, valueData );
      } catch ( KettleValueException e ) {
        throw new EvaluationException( LibFormulaErrorValue.ERROR_ARITHMETIC_VALUE );
      }
    }
    return null;
  }

  public Configuration getConfiguration() {
    return formulaContext.getConfiguration();
  }

  public FunctionRegistry getFunctionRegistry() {
    return formulaContext.getFunctionRegistry();
  }

  public LocalizationContext getLocalizationContext() {
    return formulaContext.getLocalizationContext();
  }

  public OperatorFactory getOperatorFactory() {
    return formulaContext.getOperatorFactory();
  }

  public TypeRegistry getTypeRegistry() {
    return formulaContext.getTypeRegistry();
  }

  public boolean isReferenceDirty( Object name ) throws EvaluationException {
    return formulaContext.isReferenceDirty( name );
  }

  /**
   * @return the row
   */
  public RowMetaInterface getRowMeta() {
    return rowMeta;
  }

  /**
   * @param rowMeta
   *          the row to set
   */
  public void setRowMeta( RowMetaInterface rowMeta ) {
    this.rowMeta = rowMeta;
  }

  /**
   * @param rowData
   *          the new row of data to inject
   */
  public void setRowData( Object[] rowData ) {
    this.rowData = rowData;
  }

  /**
   * @return the current row of data
   */
  public Object[] getRowData() {
    return rowData;
  }

  public static Object getPrimitive( ValueMetaInterface valueMeta, Object valueData ) throws KettleValueException {
    switch ( valueMeta.getType() ) {
      case ValueMetaInterface.TYPE_BIGNUMBER:
        return valueMeta.getBigNumber( valueData );
      case ValueMetaInterface.TYPE_BINARY:
        return valueMeta.getBinary( valueData );
      case ValueMetaInterface.TYPE_BOOLEAN:
        return valueMeta.getBoolean( valueData );
      case ValueMetaInterface.TYPE_DATE:
        return valueMeta.getDate( valueData );
      case ValueMetaInterface.TYPE_INTEGER:
        return valueMeta.getInteger( valueData );
      case ValueMetaInterface.TYPE_NUMBER:
        return valueMeta.getNumber( valueData );
      case ValueMetaInterface.TYPE_STRING:
        return valueMeta.getString( valueData );
      case ValueMetaInterface.TYPE_TIMESTAMP:
        return valueMeta.getString( valueData );
      default:
        return null;
    }
  }

  public static Class<?> getPrimitiveClass( int valueType ) {
    switch ( valueType ) {
      case ValueMetaInterface.TYPE_BIGNUMBER:
        return BigDecimal.class;
      case ValueMetaInterface.TYPE_BINARY:
        return ( new byte[] {} ).getClass();
      case ValueMetaInterface.TYPE_BOOLEAN:
        return Boolean.class;
      case ValueMetaInterface.TYPE_DATE:
        return Date.class;
      case ValueMetaInterface.TYPE_INTEGER:
        return Long.class;
      case ValueMetaInterface.TYPE_NUMBER:
        return Double.class;
      case ValueMetaInterface.TYPE_STRING:
        return String.class;
      default:
        return null;
    }
  }

  public Date getCurrentDate() {
    return new Date();
  }
}
