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


package org.pentaho.di.trans.steps.writetolog;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * Write data to log.
 *
 * @author Samatar
 * @since 30-06-2008
 */

public class WriteToLog extends BaseStep implements StepInterface {
  private static Class<?> PKG = WriteToLogMeta.class; // for i18n purposes, needed by Translator2!!

  private WriteToLogMeta meta;
  private WriteToLogData data;
  private int rowCounter = 0;
  private boolean rowCounterLimitHit = false;

  public WriteToLog( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
    Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {

    meta = (WriteToLogMeta) smi;
    data = (WriteToLogData) sdi;

    Object[] r = getRow(); // get row, set busy!
    if ( r == null ) { // no more input to be expected...

      setOutputDone();
      return false;
    }

    // Limit hit? skip
    if ( rowCounterLimitHit ) {
      putRow( getInputRowMeta(), r ); // copy row to output
      return true;
    }

    if ( first ) {
      first = false;

      if ( meta.getFieldName() != null && meta.getFieldName().length > 0 ) {
        data.fieldnrs = new int[meta.getFieldName().length];

        for ( int i = 0; i < data.fieldnrs.length; i++ ) {
          data.fieldnrs[i] = getInputRowMeta().indexOfValue( meta.getFieldName()[i] );
          if ( data.fieldnrs[i] < 0 ) {
            logError( BaseMessages.getString( PKG, "WriteToLog.Log.CanNotFindField", meta.getFieldName()[i] ) );
            throw new KettleException( BaseMessages.getString( PKG, "WriteToLog.Log.CanNotFindField", meta
              .getFieldName()[i] ) );
          }
        }
      } else {
        data.fieldnrs = new int[getInputRowMeta().size()];
        for ( int i = 0; i < data.fieldnrs.length; i++ ) {
          data.fieldnrs[i] = i;
        }
      }
      data.fieldnr = data.fieldnrs.length;
      data.loglevel = meta.getLogLevelByDesc();
      data.logmessage = Const.NVL( this.environmentSubstitute( meta.getLogMessage() ), "" );
      if ( !Utils.isEmpty( data.logmessage ) ) {
        data.logmessage += Const.CR + Const.CR;
      }
    } // end if first

    // We don't need to calculate if step log level is lower than the run log level
    if ( getLogLevel().getLevel() >= data.loglevel.getLevel() ) {
      StringBuilder out = new StringBuilder();
      out.append( Const.CR ).append( "------------> " )
        .append( BaseMessages.getString(
          PKG, "WriteToLog.Log.NLigne", "" + getLinesRead() ) )
        .append( "------------------------------" )
        .append( Const.CR );

      out.append( getRealLogMessage() );

      String[] fieldNames = {};

      // Obtaining the field name list is a heavy operation, as so, it was removed from the loop.
      // And, as it's only needed if the header is to be printed, I conditioned the calculation to that scenario.
      if ( meta.isDisplayHeader() ) {
        fieldNames = getInputRowMeta().getFieldNames();
      }

      // Loop through fields
      for ( int i = 0; i < data.fieldnr; i++ ) {
        String fieldValue = getInputRowMeta().getString( r, data.fieldnrs[ i ] );

        if ( meta.isDisplayHeader() ) {
          out.append( fieldNames[ data.fieldnrs[ i ] ] ).append( " = " );
        }

        out.append( fieldValue ).append( Const.CR );
      }

      out.append( Const.CR ).append( "====================" );

      setLog( data.loglevel, out );
    }

    // Increment counter
    if ( meta.isLimitRows() && ++rowCounter >= meta.getLimitRowsNumber() ) {
      rowCounterLimitHit = true;
    }

    putRow( getInputRowMeta(), r ); // copy row to output

    return true;
  }

  private void setLog( LogLevel loglevel, StringBuilder msg ) {
    switch ( loglevel ) {
      case ERROR:
        // Output message to log
        // Log level = ERREUR
        logError( msg.toString() );
        break;
      case MINIMAL:
        // Output message to log
        // Log level = MINIMAL
        logMinimal( msg.toString() );
        break;
      case BASIC:
        // Output message to log
        // Log level = BASIC
        logBasic( msg.toString() );
        break;
      case DETAILED:
        // Output message to log
        // Log level = DETAILED
        logDetailed( msg.toString() );
        break;
      case DEBUG:
        // Output message to log
        // Log level = DEBUG
        logDebug( msg.toString() );
        break;
      case ROWLEVEL:
        // Output message to log
        // Log level = ROW LEVEL
        logRowlevel( msg.toString() );
        break;
      case NOTHING:
        // Output nothing to log
        // Log level = NOTHING
        break;
      default:
        break;
    }
  }

  public String getRealLogMessage() {
    return data.logmessage;
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (WriteToLogMeta) smi;
    data = (WriteToLogData) sdi;

    if ( super.init( smi, sdi ) ) {
      // Add init code here.
      return true;
    }
    return false;
  }

}
