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


package org.pentaho.di.core.plugins;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.pentaho.di.core.bowl.DefaultBowl;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A folder to search plugins in.
 *
 * @author matt
 *
 */
public class PluginFolder implements PluginFolderInterface {

  private String folder;
  private boolean pluginXmlFolder;
  private boolean pluginAnnotationsFolder;
  private boolean searchLibDir;

  /**
   * @param folder
   *          The folder location
   * @param pluginXmlFolder
   *          set to true if the folder needs to be searched for plugin.xml appearances
   * @param pluginAnnotationsFolder
   *          set to true if the folder needs to be searched for jar files with plugin annotations
   */
  public PluginFolder( String folder, boolean pluginXmlFolder, boolean pluginAnnotationsFolder ) {
    this( folder, pluginXmlFolder, pluginAnnotationsFolder, false );
  }

  /**
   * @param folder
   *          The folder location
   * @param pluginXmlFolder
   *          set to true if the folder needs to be searched for plugin.xml appearances
   * @param pluginAnnotationsFolder
   *          set to true if the folder needs to be searched for jar files with plugin annotations
   * @param searchLibDir
   *          look inside the plugins lib dir for additional plugins
   */
  public PluginFolder( String folder, boolean pluginXmlFolder, boolean pluginAnnotationsFolder,
    boolean searchLibDir ) {
    this.folder = folder;
    this.pluginXmlFolder = pluginXmlFolder;
    this.pluginAnnotationsFolder = pluginAnnotationsFolder;
    this.searchLibDir = searchLibDir;
  }

  @Override
  public String toString() {
    return folder;
  }

  /**
   * Create a list of plugin folders based on the specified xml sub folder
   *
   * @param xmlSubfolder
   *          the sub-folder to consider for XML plugin files or null if it's not applicable.
   * @return The list of plugin folders found
   */
  public static List<PluginFolderInterface> populateFolders( String xmlSubfolder ) {
    List<PluginFolderInterface> pluginFolders = new ArrayList<>();
    String folderPaths = EnvUtil.getSystemProperty( Const.PLUGIN_BASE_FOLDERS_PROP );
    if ( folderPaths == null ) {
      folderPaths = Const.DEFAULT_PLUGIN_BASE_FOLDERS;
    }
    String[] folders = folderPaths.split( "," );
    // for each folder in the list of plugin base folders
    // add an annotation and xml path for searching
    // trim the folder - we don't need leading and trailing spaces
    for ( String folder : folders ) {
      folder = folder.trim();
      pluginFolders.add( new PluginFolder( folder, false, true ) );
      if ( !Utils.isEmpty( xmlSubfolder ) ) {
        pluginFolders.add( new PluginFolder( folder + File.separator + xmlSubfolder, true, false ) );
      }
    }
    return pluginFolders;
  }

  @Override
  public FileObject[] findJarFiles() throws KettleFileException {
    return findJarFiles( searchLibDir );
  }

  public FileObject[] findJarFiles( final boolean includeLibJars ) throws KettleFileException {

    try {
      // Find all the jar files in this folder...
      //
      FileObject folderObject = KettleVFS.getInstance( DefaultBowl.getInstance() ).getFileObject( this.getFolder() );

      return folderObject.findFiles( new FileSelector() {
        @Override
        public boolean traverseDescendents( FileSelectInfo fileSelectInfo ) throws Exception {
          FileObject fileObject = fileSelectInfo.getFile();
          String folder = fileObject.getName().getBaseName();
          FileObject kettleIgnore = fileObject.getChild( ".kettle-ignore" );
          return includeLibJars || ( kettleIgnore == null && !"lib".equals( folder ) );
        }

        @Override
        public boolean includeFile( FileSelectInfo fileSelectInfo ) throws Exception {
          FileObject file = fileSelectInfo.getFile();
          return file.isFile() && file.toString().endsWith( ".jar" );
        }
      } );
    } catch ( Exception e ) {
      throw new KettleFileException( "Unable to list jar files in plugin folder '" + toString() + "'", e );
    }
  }

  /**
   * @return the folder
   */
  @Override
  public String getFolder() {
    return folder;
  }

  /**
   * @param folder
   *          the folder to set
   */
  public void setFolder( String folder ) {
    this.folder = folder;
  }

  /**
   * @return the pluginXmlFolder
   */
  @Override
  public boolean isPluginXmlFolder() {
    return pluginXmlFolder;
  }

  /**
   * @param pluginXmlFolder
   *          the pluginXmlFolder to set
   */
  public void setPluginXmlFolder( boolean pluginXmlFolder ) {
    this.pluginXmlFolder = pluginXmlFolder;
  }

  /**
   * @return the pluginAnnotationsFolder
   */
  @Override
  public boolean isPluginAnnotationsFolder() {
    return pluginAnnotationsFolder;
  }

  /**
   * @param pluginAnnotationsFolder
   *          the pluginAnnotationsFolder to set
   */
  public void setPluginAnnotationsFolder( boolean pluginAnnotationsFolder ) {
    this.pluginAnnotationsFolder = pluginAnnotationsFolder;
  }

  @Override public int hashCode() {
    return new HashCodeBuilder()
      .append( folder )
      .append( pluginAnnotationsFolder )
      .append( pluginXmlFolder )
      .append( searchLibDir )
      .hashCode();
  }

  @Override public boolean equals( Object obj ) {
    if ( obj == null ) {
      return false;
    }
    if ( obj == this ) {
      return true;
    }
    if ( obj.getClass() != getClass() ) {
      return false;
    }

    PluginFolder other = (PluginFolder) obj;
    return new EqualsBuilder()
      .append( folder, other.getFolder() )
      .append( pluginAnnotationsFolder, other.isPluginAnnotationsFolder() )
      .append( pluginXmlFolder, other.isPluginXmlFolder() )
      .append( searchLibDir, other.searchLibDir )
      .isEquals();
  }
}
