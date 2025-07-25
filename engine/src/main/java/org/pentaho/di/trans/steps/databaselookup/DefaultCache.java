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


package org.pentaho.di.trans.steps.databaselookup;

import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Old code, copied from {@linkplain DatabaseLookup}
 *
 * @author Andrey Khayrutdinov
 */
public class DefaultCache implements DatabaseLookupData.Cache {

  public static DefaultCache newCache( DatabaseLookupData data, int cacheSize ) {
    return new DefaultCache( data, Math.max( 16, (int) ( cacheSize * 1.5 ) ) );
  }

  private final DatabaseLookupData data;
  private final LinkedHashMap<RowMetaAndData, Object[]> map;

  DefaultCache( DatabaseLookupData data, int capacity ) {
    this.data = data;
    map = new LinkedHashMap<>( capacity );
  }

  @Override
  public Object[] getRowFromCache( RowMetaInterface lookupMeta, Object[] lookupRow ) throws KettleException {
    if ( data.allEquals ) {
      // only do the map lookup when all equals otherwise conditions >, <, <> will give wrong results
      Object[] row = map.get( new RowMetaAndData( data.lookupMeta, lookupRow ) );
      if ( row != null ) {
        return row;
      }
    } else { // special handling of conditions <,>, <> etc.
      if ( !data.hasDBCondition ) { // e.g. LIKE not handled by this routine, yet
        // TODO: find an alternative way to look up the data based on the condition.
        // Not all conditions are "=" so we are going to have to evaluate row by row
        // A sorted list or index might be a good solution here...
        //
        for ( Map.Entry<RowMetaAndData, Object[]> entry : map.entrySet() ) {
          final RowMetaAndData key = entry.getKey();
          // Now verify that the key is matching our conditions...
          //
          boolean match = true;
          int lookupIndex = 0;
          for ( int i = 0; i < data.conditions.length && match; i++ ) {
            ValueMetaInterface cmpMeta = lookupMeta.getValueMeta( lookupIndex );
            Object cmpData = lookupRow[ lookupIndex ];
            ValueMetaInterface keyMeta = key.getValueMeta( i );
            Object keyData = key.getData()[ i ];

            switch ( data.conditions[ i ] ) {
              case DatabaseLookupMeta.CONDITION_EQ:
                match = ( cmpMeta.compare( cmpData, keyMeta, keyData ) == 0 );
                break;
              case DatabaseLookupMeta.CONDITION_NE:
                match = ( cmpMeta.compare( cmpData, keyMeta, keyData ) != 0 );
                break;
              case DatabaseLookupMeta.CONDITION_LT:
                match = ( cmpMeta.compare( cmpData, keyMeta, keyData ) > 0 );
                break;
              case DatabaseLookupMeta.CONDITION_LE:
                match = ( cmpMeta.compare( cmpData, keyMeta, keyData ) >= 0 );
                break;
              case DatabaseLookupMeta.CONDITION_GT:
                match = ( cmpMeta.compare( cmpData, keyMeta, keyData ) < 0 );
                break;
              case DatabaseLookupMeta.CONDITION_GE:
                match = ( cmpMeta.compare( cmpData, keyMeta, keyData ) <= 0 );
                break;
              case DatabaseLookupMeta.CONDITION_IS_NULL:
                match = keyMeta.isNull( keyData );
                break;
              case DatabaseLookupMeta.CONDITION_IS_NOT_NULL:
                match = !keyMeta.isNull( keyData );
                break;
              case DatabaseLookupMeta.CONDITION_BETWEEN:
                // Between key >= cmp && key <= cmp2
                ValueMetaInterface cmpMeta2 = lookupMeta.getValueMeta( lookupIndex + 1 );
                Object cmpData2 = lookupRow[ lookupIndex + 1 ];
                match = ( keyMeta.compare( keyData, cmpMeta, cmpData ) >= 0 );
                if ( match ) {
                  match = ( keyMeta.compare( keyData, cmpMeta2, cmpData2 ) <= 0 );
                }
                lookupIndex++;
                break;
              // TODO: add LIKE operator (think of changing the hasDBCondition logic then)
              default:
                match = false;
                data.hasDBCondition = true; // avoid looping in here the next time, also safety when a new condition
                // will be introduced
                break;
            }
            lookupIndex++;
          }
          if ( match ) {
            Object[] row = entry.getValue();
            if ( row != null ) {
              return row;
            }
          }
        }
      }
    }
    return null;
  }

  @Override
  public void storeRowInCache( DatabaseLookupMeta meta, RowMetaInterface lookupMeta, Object[] lookupRow,
                               Object[] add ) {
    map.computeIfAbsent( new RowMetaAndData( lookupMeta, lookupRow ), k -> add );

    // DEinspanjer 2009-02-01: If you had previously set a cache size and then turned on load all, this
    // method would throw out entries if the previous cache size wasn't big enough.
    if ( !meta.isLoadingAllDataInCache() && meta.getCacheSize() > 0 && map.size() > meta.getCacheSize() ) {
      map.remove( map.entrySet().iterator().next().getKey() );
    }
  }
}
