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

package org.pentaho.di.repository.pur;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.platform.api.repository2.unified.data.node.DataNode;

import java.util.Date;

public abstract class AbstractDelegate {

  protected static final String PROP_NAME = "NAME";
  protected static final String PROP_DESCRIPTION = "DESCRIPTION";

  protected LogChannelInterface log;

  public AbstractDelegate() {
    log = LogChannel.GENERAL;
  }

  protected String sanitizeNodeName( final String name ) {
    StringBuffer result = new StringBuffer( 30 );

    for ( char c : name.toCharArray() ) {
      switch ( c ) {
        case ':':
        case '/':
          result.append( '-' );
          break;
        case '{':
        case '}':
        case '[':
        case ']':
        case ')':
        case '(':
        case '\\':
          result.append( '_' );
          break;
        default:
          if ( Character.isLetterOrDigit( c ) ) {
            result.append( c );
          }
          break;
      }
    }

    return result.toString();
  }

  /**
   * Receive a DataNode object and return the value of key by name. If doesn't exist return an empty string.
   * @param node
   * @param name
   * @return
   */
  protected String getString( DataNode node, String name ) {
    if ( node.hasProperty( name ) ) {
      return node.getProperty( name ).getString();
    } else {
      return "";
    }
  }

  /**
   * Receive a DataNode object and return the value of key by name. If doesn't exist return 0.
   *
   * @param node
   * @param name
   * @return
   */
  protected int getInt( DataNode node, String name ) {
    if ( node.hasProperty( name ) ) {
      return (int) node.getProperty( name ).getLong();
    } else {
      return 0;
    }
  }

  /**
   * Receive a DataNode object and return the value of key by name. If doesn't exist return 0L.
   * @param node
   * @param name
   * @return
   */
  protected long getLong( DataNode node, String name ) {
    if ( node.hasProperty( name ) ) {
      return node.getProperty( name ).getLong();
    } else {
      return 0L;
    }
  }

  /**
   * Receive a DataNode object and return the value of key by name. If doesn't exist return null.
   *
   * @param node
   * @param name
   * @return
   */
  protected Date getDate( DataNode node, String name ) {
    if ( node.hasProperty( name ) ) {
      return node.getProperty( name ).getDate();
    } else {
      return null;
    }
  }

  /**
   * Receive a DataNode object and return the value of key by name. If doesn't exist return false.
   *
   * @param node
   * @param name
   * @return
   */
  protected boolean getBoolean( DataNode node, String name ) {
    return getBoolean( node, name, false );
  }

  /**
   * Receive a DataNode object and return the value of key by name. If doesn't exist return the default value.
   *
   * @param node
   * @param name
   * @param defaultValue
   * @return
   */
  protected boolean getBoolean( DataNode node, String name, boolean defaultValue ) {
    if ( node.hasProperty( name ) ) {
      return node.getProperty( name ).getBoolean();
    } else {
      return defaultValue;
    }
  }

  protected String setNull( String value ) {
    String response = value;
    if ( value == null ) {
      response = "";
    }
    return response;
  }

  public abstract DataNode elementToDataNode( RepositoryElementInterface element ) throws KettleException;

  public boolean equals( RepositoryElementInterface first, RepositoryElementInterface second ) {
    try {
      // check case. if case changes, we need to know.
      return first.getName().equals( second.getName() ) && elementToDataNode( first ).equals( elementToDataNode( second ) );
    } catch ( KettleException e ) {
      return false;
    }
  }
}
