/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2010 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */

package org.opensolaris.opengrok.analysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.opensolaris.opengrok.analysis.AnalyzerException.ANALYZER_EXCEPTION_TYPE;
import org.opensolaris.opengrok.analysis.archive.ZipAnalyzer;
import org.opensolaris.opengrok.analysis.c.CxxAnalyzerFactory;
import org.opensolaris.opengrok.analysis.executables.JarAnalyzer;
import org.opensolaris.opengrok.analysis.perl.PerlAnalyzerFactory;
import org.opensolaris.opengrok.analysis.plain.PlainAnalyzer;
import org.opensolaris.opengrok.analysis.plain.XMLAnalyzer;
import org.opensolaris.opengrok.analysis.sh.ShAnalyzer;
import org.opensolaris.opengrok.analysis.sh.ShAnalyzerFactory;
import org.opensolaris.opengrok.configuration.Configuration;
import org.opensolaris.opengrok.configuration.ExtensionGroup;
import org.opensolaris.opengrok.configuration.RuntimeEnvironment;

/**
 * Tests for the functionality provided by the AnalyzerGuru class.
 */
public class AnalyzerGuruTest {
	
    /**
     * Test that we get the correct analyzer if the file name exactly matches a
     * known extension.
     */
    @Test
    public void testFileNameSameAsExtension() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "#!/bin/sh\nexec /usr/bin/zip \"$@\"\n".getBytes("US-ASCII"));
        String file = "/dummy/path/to/source/zip";
        FileAnalyzer fa = AnalyzerGuru.getAnalyzer(in, file);
        assertSame(ShAnalyzer.class, fa.getClass());
    }

    @Test
    public void testUTF8ByteOrderMark() throws Exception {
        byte[] xml = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, // UTF-8 BOM
                       '<', '?', 'x', 'm', 'l', ' ',
                       'v', 'e', 'r', 's', 'i', 'o', 'n', '=',
                       '"', '1', '.', '0', '"', '?', '>'};
        ByteArrayInputStream in = new ByteArrayInputStream(xml);
        FileAnalyzer fa = AnalyzerGuru.getAnalyzer(in, "/dummy/file");
        assertSame(XMLAnalyzer.class, fa.getClass());
    }

    @Test
    public void testUTF8ByteOrderMarkPlainFile() throws Exception {
        byte[] bytes = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, // UTF-8 BOM
                       'h', 'e', 'l', 'l', 'o', ' ',
                       'w', 'o', 'r', 'l', 'd'};
        
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        FileAnalyzer fa = AnalyzerGuru.getAnalyzer(in, "/dummy/file");
        assertSame(PlainAnalyzer.class, fa.getClass());
    }

    @Test
    public void addExtension() throws Exception {
        // should not find analyzer for this unlikely extension
        assertNull(AnalyzerGuru.find("file.unlikely_extension"));

        FileAnalyzerFactory
            faf = AnalyzerGuru.findFactory(ShAnalyzerFactory.class.getName());
        // should be the same factory as the built-in analyzer for sh scripts
        assertSame(AnalyzerGuru.find("myscript.sh"), faf);

        // add an analyzer for the extension and see that it is picked up
        AnalyzerGuru.addExtension("UNLIKELY_EXTENSION", faf);
        assertSame(ShAnalyzerFactory.class,
                   AnalyzerGuru.find("file.unlikely_extension").getClass());

        // remove the mapping and verify that it is gone
        AnalyzerGuru.addExtension("UNLIKELY_EXTENSION", null);
        assertNull(AnalyzerGuru.find("file.unlikely_extension"));
    }

    @Test
    public void addPrefix() throws Exception {
        // should not find analyzer for this unlikely extension
        assertNull(AnalyzerGuru.find("unlikely_prefix.foo"));

        FileAnalyzerFactory
            faf = AnalyzerGuru.findFactory(ShAnalyzerFactory.class.getName());
        // should be the same factory as the built-in analyzer for sh scripts
        assertSame(AnalyzerGuru.find("myscript.sh"), faf);

        // add an analyzer for the prefix and see that it is picked up
        AnalyzerGuru.addPrefix("UNLIKELY_PREFIX", faf);
        assertSame(ShAnalyzerFactory.class,
                   AnalyzerGuru.find("unlikely_prefix.foo").getClass());

        // remove the mapping and verify that it is gone
        AnalyzerGuru.addPrefix("UNLIKELY_PREFIX", null);
        assertNull(AnalyzerGuru.find("unlikely_prefix.foo"));
    }

    @Test
    public void testZip() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.putNextEntry(new ZipEntry("dummy"));
        zos.closeEntry();
        zos.close();
        InputStream in = new ByteArrayInputStream(baos.toByteArray());
        FileAnalyzer fa = AnalyzerGuru.getAnalyzer(in, "dummy");
        assertSame(ZipAnalyzer.class, fa.getClass());
    }

    @Test
    public void testJar() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JarOutputStream jos = new JarOutputStream(baos);
        jos.putNextEntry(new JarEntry("dummy"));
        jos.closeEntry();
        jos.close();
        InputStream in = new ByteArrayInputStream(baos.toByteArray());
        FileAnalyzer fa = AnalyzerGuru.getAnalyzer(in, "dummy");
        assertSame(JarAnalyzer.class, fa.getClass());
    }

    @Test
    public void testPlainText() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "This is a plain text file.".getBytes("US-ASCII"));
        assertSame(PlainAnalyzer.class,
                   AnalyzerGuru.getAnalyzer(in, "dummy").getClass());
    }

    @Test
    public void rfe2969() {
        FileAnalyzerFactory faf = AnalyzerGuru.find("foo.hxx");
        assertNotNull(faf);
        assertSame(CxxAnalyzerFactory.class, faf.getClass());
    }

    @Test
    public void rfe3401() {
        FileAnalyzerFactory f1 = AnalyzerGuru.find("main.c");
        assertNotNull(f1);
        FileAnalyzerFactory f2 = AnalyzerGuru.find("main.cc");
        assertNotNull(f2);
        assertNotSame(f1.getClass(), f2.getClass());

    }

    /**
     * Test that matching of full names works. Bug #859.
     */
    @Test
    public void matchesFullName() {
        FileAnalyzerFactory faf = AnalyzerGuru.find("/path/to/Makefile");
        assertSame(ShAnalyzerFactory.class, faf.getClass());
        faf = AnalyzerGuru.find("GNUMakefile");
        assertSame(ShAnalyzerFactory.class, faf.getClass());
    }
    
    /**
     * Test if a group added in the list of excluded Groups 
     * in configuration is not present in the list given to UI
     * @throws AnalyzerException 
     */
    @Test
    public void testGroupRemoval() throws AnalyzerException{
    	
    	Configuration cfg = RuntimeEnvironment.getInstance().getConfiguration();
    	Map<String,String> customFileMapping = AnalyzerGuru.getCustomizedFileTypeDescriptions();
    	String grpName = customFileMapping.keySet().iterator().next();
    	
		List<String> extGroups = new ArrayList<String>();
    	extGroups.add(grpName);
    	cfg.setExcludeGroups(extGroups);
    	cfg.setAnalyzerMapping(null);
    	cfg.setCustomGroups(null);

    	assertNotNull(customFileMapping.get(grpName));
    	
		AnalyzerGuru.populateCustomizedFileTypeDescriptions();
		
    	customFileMapping = AnalyzerGuru.getCustomizedFileTypeDescriptions();    	
    	assertNull(customFileMapping.get(grpName));

    }
    
    /**
     * Test if a group added in the list of excluded Groups which is not in the existing groups
     * should throw an exception
     * @throws AnalyzerException 
     */
    @Test(expected = AnalyzerException.class)
    public void testExistingGroupRemoval() throws AnalyzerException{
    	
    	Configuration cfg = RuntimeEnvironment.getInstance().getConfiguration();
    	
		List<String> extGroups = new ArrayList<String>();
    	extGroups.add("unlikely_group");
    	cfg.setExcludeGroups(extGroups);
    	
    	try{
    		AnalyzerGuru.populateCustomizedFileTypeDescriptions();    		
    	}catch(AnalyzerException ex){
    		assertSame(ex.getType(),ANALYZER_EXCEPTION_TYPE.NON_EXISTING_GROUP);
    		throw ex;
    	}
    	
    	assertTrue(AnalyzerGuru.checkInvalidArguments());

    }
    
    /**
     * Test if a group added in the list of custom groups
     * in configuration is present in the list given to UI
     * and files with those extensions are being mapped to corresponding types
     * @throws AnalyzerException 
     */
    @Test
    public void testCustomGroupAddition() throws AnalyzerException{
    	
    	Configuration cfg = RuntimeEnvironment.getInstance().getConfiguration();
    	Map<String,String> customFileMapping = AnalyzerGuru.getCustomizedFileTypeDescriptions();
    	
    	List<ExtensionGroup> extGrpList = new ArrayList<ExtensionGroup>();
    	ExtensionGroup extGroup = new ExtensionGroup();
    	extGroup.setName("Grind");
    	List<String> extList = new ArrayList<String>();
    	extList.add("java");
    	extList.add("perl");
    	extGroup.setExtList(extList);
    	extGrpList.add(extGroup);
    	
    	cfg.setCustomGroups(extGrpList);
    	cfg.setAnalyzerMapping(null);
    	cfg.setExcludeGroups(null);
    	
		
    	assertNull(customFileMapping.get("grind"));
    	
    	AnalyzerGuru.populateCustomizedFileTypeDescriptions();
		
    	assertFalse(AnalyzerGuru.checkInvalidArguments());
    	customFileMapping = AnalyzerGuru.getCustomizedFileTypeDescriptions();
    	assertNotNull(customFileMapping.get("grind"));
    	
    	assertTrue(AnalyzerGuru.getCustomFileTypeGroups().get("JAVA").contains("Grind"));
    	assertTrue(AnalyzerGuru.getCustomFileTypeGroups().get("PERL").contains("Grind"));
    	
    }
    
    /**
     * Test if a group added in the list of custom groups
     * in configuration throws exception if it is one of default groups
     * @throws AnalyzerException 
     */
    @Test(expected = AnalyzerException.class)
    public void testExistingCustomGroupAddition() throws AnalyzerException{
    	
    	Configuration cfg = RuntimeEnvironment.getInstance().getConfiguration();
    	
    	List<ExtensionGroup> extGrpList = new ArrayList<ExtensionGroup>();
    	ExtensionGroup extGroup = new ExtensionGroup();
    	extGroup.setName("JavA");
    	List<String> extList = new ArrayList<String>();
    	extList.add("perl");
    	extGroup.setExtList(extList);
    	extGrpList.add(extGroup);
    	cfg.setCustomGroups(extGrpList);
    	
    	
    	try{
    		AnalyzerGuru.populateCustomizedFileTypeDescriptions();    		
    	}catch(AnalyzerException ex){
    		assertSame(ex.getType(),ANALYZER_EXCEPTION_TYPE.GROUP_ALREADY_EXISTS);
    		throw ex;
    	}
    	    	
    }
    
    /**
     * Test if for a group added in the list of custom groups
     * does not have non existing extensions
     * @throws AnalyzerException 
     */
    @Test(expected = AnalyzerException.class)
    public void testCustomGroupAdditionNonExistingExtensions() throws AnalyzerException{
    	
    	Configuration cfg = RuntimeEnvironment.getInstance().getConfiguration();
    	
    	List<ExtensionGroup> extGrpList = new ArrayList<ExtensionGroup>();
    	ExtensionGroup extGroup = new ExtensionGroup();
    	extGroup.setName("Grind2");
    	List<String> extList = new ArrayList<String>();
    	extList.add("non_existing_extension");
    	extGroup.setExtList(extList);
    	extGrpList.add(extGroup);
    	
    	cfg.setCustomGroups(extGrpList);
    	cfg.setAnalyzerMapping(null);
    	cfg.setExcludeGroups(null);
		
    	
    	try{
    		AnalyzerGuru.populateCustomizedFileTypeDescriptions();    		
    	}catch(AnalyzerException ex){
    		assertSame(ex.getType(),ANALYZER_EXCEPTION_TYPE.NON_EXISTING_EXTENSION);
    		throw ex;
    	}
    	
    	
    }
    
    /**
     * Test if an extension is added in the mappings
     * in configuration to an analyzer then we are able to find its corresponding analyzer
     * @throws AnalyzerException 
     *
     */
    @Test
    public void testExtensionInGroupAddition() throws AnalyzerException{
    	
    	Configuration cfg = RuntimeEnvironment.getInstance().getConfiguration();
    	
    	FileAnalyzerFactory fa = AnalyzerGuru.find("myscript.random_extension");
    	assertNull(fa);
    	
    	
    	List<ExtensionGroup> extGrpList = new ArrayList<ExtensionGroup>();
    	ExtensionGroup extGroup = new ExtensionGroup();
    	extGroup.setName("Perl");
    	List<String> extList = new ArrayList<String>();
    	extList.add("random_extension");
    	extGroup.setExtList(extList);
    	extGrpList.add(extGroup);
    	cfg.setAnalyzerMapping(extGrpList);

    	
		AnalyzerGuru.populateCustomizedFileTypeDescriptions();
    	
    	fa = AnalyzerGuru.find("myscript.random_extension");
    	assertSame(fa.getClass(),PerlAnalyzerFactory.class);
    }
    
    /**
     * Test if a default extension is added to a pre-existing group then exception
     * should be thrown
     * @throws AnalyzerException 
     */
    @Test(expected = AnalyzerException.class)
    public void testExtensionAdditionInvalid() throws AnalyzerException{
    	
    	Configuration cfg = RuntimeEnvironment.getInstance().getConfiguration();
    	
    	List<ExtensionGroup> extGrpList = new ArrayList<ExtensionGroup>();
    	ExtensionGroup extGroup = new ExtensionGroup();
    	extGroup.setName("Perl");
    	List<String> extList = new ArrayList<String>();
    	extList.add("java");
    	extGroup.setExtList(extList);
    	extGrpList.add(extGroup);
    	cfg.setAnalyzerMapping(extGrpList);
    	cfg.setExcludeGroups(null);
    	cfg.setCustomGroups(null);
    	try{
    		AnalyzerGuru.populateCustomizedFileTypeDescriptions();    		
    	}catch(AnalyzerException ex){
    		assertSame(ex.getType(),ANALYZER_EXCEPTION_TYPE.EXTENSION_ALREADY_EXISTS);
    		throw ex;
    	}  
    	
    	
    }
    
    
}
