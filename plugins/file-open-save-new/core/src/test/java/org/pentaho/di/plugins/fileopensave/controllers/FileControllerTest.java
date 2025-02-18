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


package org.pentaho.di.plugins.fileopensave.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.plugins.fileopensave.api.overwrite.OverwriteStatus;
import org.pentaho.di.plugins.fileopensave.api.providers.File;
import org.pentaho.di.plugins.fileopensave.api.providers.FileProvider;
import org.pentaho.di.plugins.fileopensave.api.providers.Tree;
import org.pentaho.di.plugins.fileopensave.api.providers.exception.FileException;
import org.pentaho.di.plugins.fileopensave.cache.FileCache;
import org.pentaho.di.plugins.fileopensave.providers.ProviderService;
import org.pentaho.di.plugins.fileopensave.providers.TestFileProvider;
import org.pentaho.di.plugins.fileopensave.providers.model.TestDirectory;
import org.pentaho.di.plugins.fileopensave.providers.model.TestFile;
import org.pentaho.di.ui.core.FileDialogOperation.FileLoadListener;
import org.pentaho.di.ui.core.FileDialogOperation.FileLookupInfo;
import org.pentaho.di.ui.core.events.dialog.ProviderFilterType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FileControllerTest {

  private FileController fileController;

  @Before
  public void setup() {
    List<FileProvider> fileProviders = new ArrayList<>();
    fileProviders.add( new TestFileProvider() );
    ProviderService providerService = new ProviderService( fileProviders );
    fileController = new FileController( new FileCache(), providerService );
  }

  @Test
  public void testLoad() {
    List<Tree> trees = fileController.load( ProviderFilterType.ALL_PROVIDERS.toString() );
    Assert.assertEquals( 1, trees.size() );
    Assert.assertEquals( TestFileProvider.TYPE, trees.get( 0 ).getProvider() );
  }

  @Test
  public void testGetFilesCache() throws FileException {
    TestDirectory testDirectory = new TestDirectory();
    testDirectory.setPath( "/" );
    List<File> files = fileController.getFiles( testDirectory, null, true );
    Assert.assertEquals( 8, files.size() );

    Assert.assertTrue( fileController.fileCache.containsKey( testDirectory ) );
    Assert.assertEquals( 8, fileController.fileCache.getFiles( testDirectory ).size() );
  }

  @Test
  public void testFileExists() {
    TestDirectory testDirectory = new TestDirectory();
    testDirectory.setPath( "/directory1" );
    Assert.assertTrue( fileController.fileExists( testDirectory, "/directory1/file1" ) );
    Assert.assertFalse( fileController.fileExists( testDirectory, "/directory1/file5" ) );
  }

  @Test
  public void testGetNewName() {
    TestDirectory testDirectory = new TestDirectory();
    testDirectory.setPath( "/directory1" );
    String newName = (String) fileController.getNewName( testDirectory, "/directory1/file1" ).getData();
    Assert.assertEquals( "/directory1/file1 1", newName );
  }

  @Test
  public void testRename() throws FileException {
    TestDirectory testDirectory = new TestDirectory();
    testDirectory.setParent( "/" );
    testDirectory.setPath( "/directory1" );
    testDirectory.setName( "directory1" );

    fileController.getFiles( testDirectory, "", true );

    TestFile testFile = new TestFile();
    testFile.setParent( "/directory1" );
    testFile.setName( "file1" );
    testFile.setPath( "/directory1/file1" );
    TestFile newFile = (TestFile) fileController.rename( testFile, "/directory1/file1new",
      new OverwriteStatus( null, OverwriteStatus.OverwriteMode.OVERWRITE ) ).getData();
    Assert.assertEquals( "file1new", newFile.getName() );
    Assert.assertEquals( "/directory1/file1new", newFile.getPath() );

    Assert.assertTrue( fileController.fileCache.fileExists( testDirectory, "/directory1/file1new" ) );
    Assert.assertFalse( fileController.fileCache.fileExists( testDirectory, "/directory1/file1" ) );
  }

  @Test
  public void testCopy() throws FileException {
    TestDirectory testDirectory = new TestDirectory();
    testDirectory.setParent( "/" );
    testDirectory.setPath( "/directory1" );
    testDirectory.setName( "directory1" );
    fileController.getFiles( testDirectory, null, true );

    TestDirectory testDirectory4 = new TestDirectory();
    testDirectory4.setParent( "/" );
    testDirectory4.setPath( "/directory4" );
    testDirectory4.setName( "directory4" );
    fileController.getFiles( testDirectory4, null, true );

    TestFile testFile = new TestFile();
    testFile.setParent( "/directory1" );
    testFile.setName( "file1" );
    testFile.setPath( "/directory1/file1" );
    TestFile newFile = (TestFile) fileController.copyFile( testFile, testDirectory4, "/directory4/file1",
      new OverwriteStatus( null, OverwriteStatus.OverwriteMode.OVERWRITE ) ).getData();
    Assert.assertEquals( "file1", newFile.getName() );
    Assert.assertEquals( "/directory4/file1", newFile.getPath() );

    Assert.assertTrue( fileController.fileCache.fileExists( testDirectory, "/directory1/file1" ) );
    Assert.assertTrue( fileController.fileCache.fileExists( testDirectory4, "/directory4/file1" ) );
  }

  @Test
  public void testMove() throws FileException {
    TestDirectory testDirectory = new TestDirectory();
    testDirectory.setParent( "/" );
    testDirectory.setPath( "/directory1" );
    testDirectory.setName( "directory1" );
    fileController.getFiles( testDirectory, "", true );

    TestDirectory testDirectory4 = new TestDirectory();
    testDirectory4.setParent( "/" );
    testDirectory4.setPath( "/directory4" );
    testDirectory4.setName( "directory4" );
    fileController.getFiles( testDirectory4, "", true );

    TestFile testFile = new TestFile();
    testFile.setParent( "/directory1" );
    testFile.setName( "file1" );
    testFile.setPath( "/directory1/file1" );
    TestFile newFile = (TestFile) fileController.moveFile( testFile, testDirectory4, "/directory4/file1",
        new OverwriteStatus( null, OverwriteStatus.OverwriteMode.OVERWRITE ) ).getData();
    Assert.assertEquals( "file1", newFile.getName() );
    Assert.assertEquals( "/directory4/file1", newFile.getPath() );

    Assert.assertFalse( fileController.fileCache.fileExists( testDirectory, "/directory1/file1" ) );
    Assert.assertTrue( fileController.fileCache.fileExists( testDirectory4, "/directory4/file1" ) );
  }

  @Test
  public void testFileLoadListener() throws Exception {
    @SuppressWarnings( "rawtypes" )
    List<FileProvider> fileProviders = new ArrayList<>();
    TestFileProvider provider = new TestFileProvider();
    TestFile newFile = TestFile.create( "target_file", "/directory4/target_file", "/directory4" );
    provider.add( newFile, new Variables() );
    fileProviders.add( provider );
    ProviderService providerService = new ProviderService( fileProviders );

    Set<String> paths = new HashSet<>();
    boolean[] checks =
        new boolean[] {
          false, false };
    FileLoadListener listener = new FileLoadListener() {

      @Override
      public void onFileLoaded( FileLookupInfo file ) {
        paths.add( file.getPath() );
        if ( file.getName().equals( "directory4" ) ) {
          checks[0] = file.hasChildFile( "not_there" );
          checks[1] = file.hasChildFile( "target_file" );
        }
      }

    };
    fileController = new FileController( new FileCache(), providerService, Optional.of( listener ) );
    TestDirectory root = TestDirectory.create( "", "/", null );
    fileController.getFiles( root, "", true );

    Assert.assertEquals( 4, paths.size() );
    Assert.assertTrue( paths.contains( "/directory4" ) );

    Assert.assertFalse( "file shouldn't be found", checks[0] );
    Assert.assertTrue( "child not found", checks[1] );

  }
}
