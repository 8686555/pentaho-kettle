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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.ObjectRevision;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.ui.repository.pur.services.IRevisionService;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.VersionSummary;

public class UnifiedRepositoryRevisionService implements IRevisionService {
  private final IUnifiedRepository unifiedRepository;
  private final RootRef rootRef;

  public UnifiedRepositoryRevisionService( IUnifiedRepository unifiedRepository, RootRef rootRef ) {
    this.unifiedRepository = unifiedRepository;
    this.rootRef = rootRef;
  }

  @Override
  public List<ObjectRevision> getRevisions( final RepositoryElementInterface element ) throws KettleException {
    return getRevisions( element.getObjectId() );
  }

  @Override
  public List<ObjectRevision> getRevisions( ObjectId fileId ) throws KettleException {
    String absPath = null;
    try {
      List<ObjectRevision> versions = new ArrayList<ObjectRevision>();
      List<VersionSummary> versionSummaries = unifiedRepository.getVersionSummaries( fileId.getId() );
      for ( VersionSummary versionSummary : versionSummaries ) {
        versions.add( new PurObjectRevision( versionSummary.getId(), versionSummary.getAuthor(), versionSummary
            .getDate(), versionSummary.getMessage() ) );
      }
      return versions;
    } catch ( Exception e ) {
      throw new KettleException( "Could not retrieve version history of object with path [" + absPath + "]", e );
    }
  }

  @Override
  public void restoreJob( ObjectId id_job, String revision, String versionComment ) throws KettleException {
    unifiedRepository.restoreFileAtVersion( id_job.getId(), revision, versionComment );
    rootRef.clearRef();
  }

  @Override
  public void restoreTransformation( ObjectId id_transformation, String revision, String versionComment )
    throws KettleException {
    unifiedRepository.restoreFileAtVersion( id_transformation.getId(), revision, versionComment );
    rootRef.clearRef();
  }

}
