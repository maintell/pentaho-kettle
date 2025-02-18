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

package com.pentaho.repository.importexport;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.utils.IRepositoryFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.pentaho.di.core.util.Assert.assertNull;
import static org.pentaho.di.core.util.Assert.assertNotNull;

/**
 * Created by nbaker on 11/5/15.
 */
//@RunWith( PowerMockRunner.class )
public class PDIImportUtilTest {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  /**
   * @see <a href="https://en.wikipedia.org/wiki/Billion_laughs" />
   */
  private static final String MALICIOUS_XML =
    "<?xml version=\"1.0\"?>\n"
      + "<!DOCTYPE lolz [\n"
      + " <!ENTITY lol \"lol\">\n"
      + " <!ELEMENT lolz (#PCDATA)>\n"
      + " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n"
      + " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n"
      + " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n"
      + " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n"
      + " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n"
      + " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n"
      + " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n"
      + " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n"
      + " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n"
      + "]>\n"
      + "<lolz>&lol9;</lolz>";

  @BeforeClass
  public static void setUp() throws Exception {
    KettleEnvironment.init();
  }

  @AfterClass
  public static void reset() {
    PDIImportUtil.setRepositoryFactory( new IRepositoryFactory.CachingRepositoryFactory() );
  }

  @Test
  public void testConnectToRepository() throws Exception {
    IRepositoryFactory mock = mock( IRepositoryFactory.class );
    PDIImportUtil.setRepositoryFactory( mock );

    PDIImportUtil.connectToRepository( "foo" );

    verify( mock, times( 1 ) ).connect( "foo" );
  }

  @Test( timeout = 2000 )
  public void whenLoadingMaliciousXmlFromStringParsingEndsWithNoErrorAndNullValueIsReturned() throws Exception {
    assertNull( PDIImportUtil.loadXMLFrom( MALICIOUS_XML ) );
  }

  @Test( timeout = 2000 )
  public void whenLoadingMaliciousXmlFromInputStreamParsingEndsWithNoErrorAndNullValueIsReturned() throws Exception {
    assertNull( PDIImportUtil.loadXMLFrom( MALICIOUS_XML ) );
  }

  @Test
  public void whenLoadingLegalXmlFromStringNotNullDocumentIsReturned() throws Exception {
    final String trans = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<transformation>"
      + "</transformation>";

    assertNotNull( PDIImportUtil.loadXMLFrom( trans ) );

  }
}
