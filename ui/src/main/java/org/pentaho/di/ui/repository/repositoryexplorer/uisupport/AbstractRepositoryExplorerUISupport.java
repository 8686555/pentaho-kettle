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


package org.pentaho.di.ui.repository.repositoryexplorer.uisupport;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.repository.repositoryexplorer.ControllerInitializationException;
import org.pentaho.di.ui.repository.repositoryexplorer.IUISupportController;
import org.pentaho.di.ui.spoon.XulSpoonResourceBundle;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulOverlay;
import org.pentaho.ui.xul.impl.XulEventHandler;

public abstract class AbstractRepositoryExplorerUISupport implements IRepositoryExplorerUISupport {
  protected List<XulEventHandler> handlers;
  protected List<XulOverlay> overlays;
  protected XulDomContainer container;
  protected List<String> controllerNames;

  public AbstractRepositoryExplorerUISupport() {
    super();
    handlers = new ArrayList<XulEventHandler>();
    overlays = new ArrayList<XulOverlay>();
    controllerNames = new ArrayList<String>();
    setup();
  }

  public void apply( XulDomContainer container ) throws XulException {
    this.container = container;
    container.registerClassLoader( getClass().getClassLoader() );
    for ( XulEventHandler handler : handlers ) {
      container.addEventHandler( handler );
    }
    for ( XulOverlay overlay : overlays ) {
      if ( overlay instanceof RepositoryExplorerDefaultXulOverlay ) {
        container.loadOverlay( overlay.getOverlayUri(), new XulSpoonResourceBundle(
          ( (RepositoryExplorerDefaultXulOverlay) overlay ).getPackageClass() ) );
      } else {
        container.loadOverlay( overlay.getOverlayUri(), overlay.getResourceBundleUri() );
      }
    }
  }

  public List<XulEventHandler> getEventHandlers() {
    return handlers;
  }

  public List<XulOverlay> getOverlays() {
    return overlays;
  }

  public void initControllers( Repository rep ) throws ControllerInitializationException {
    for ( String name : controllerNames ) {
      try {
        IUISupportController controller = (IUISupportController) container.getEventHandler( name );
        controller.init( rep );
      } catch ( XulException e ) {
        throw new ControllerInitializationException( e );
      }
    }
  }

  /**
   * Setup the event handlers and the overlays for a service or capability
   */
  protected abstract void setup();
}
