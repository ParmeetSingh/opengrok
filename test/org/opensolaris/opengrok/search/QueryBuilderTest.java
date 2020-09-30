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
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 */

package org.opensolaris.opengrok.search;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.WildcardQuery;
import org.junit.Test;
import static org.junit.Assert.*;


import org.opensolaris.opengrok.configuration.RuntimeEnvironment;
/**
 * Unit test class for QueryBuilder
 * @author Lubos Kosco
 */
public class QueryBuilderTest {
       
    /**
     * Test of setFreetext method, of class QueryBuilder.
     */
    @Test
    public void testParsePath() throws ParseException {        
        QueryBuilder instance = new QueryBuilder();
        String expResult = "+this +is +a +test +path";
        QueryBuilder result = instance.setPath("this/is/a/test/path");
        Query test = result.build();        
        assertEquals(expResult, test.toString(QueryBuilder.PATH) );        
        
        expResult = "+this +is +a +test +path +with +file +. +ext";
        result = instance.setPath("/this/is/a/test/path/with/file.ext");
        test = result.build();        
        assertEquals(expResult, test.toString(QueryBuilder.PATH) );        
        
    }
    
    @Test
    public void testTermQuerySingleClause() throws ParseException {        
        QueryBuilder instance = new QueryBuilder();
        String termText = "termtext";
        
        RuntimeEnvironment.getInstance().setAllowLeadingWildcard(true); 
        
        QueryBuilder result = instance.setFreetext(termText);
        Query test = result.build();
        assertTrue(test instanceof WildcardQuery);
        WildcardQuery query = (WildcardQuery)test;
        assertEquals(query.getTerm().text(),"*termtext*");
    }
    
    @Test
    public void testTermQueryMultipleCluase() throws ParseException {        
        QueryBuilder instance = new QueryBuilder();
        String termText = "termtext";
        String regexText = "/re[g]exText/";
        
        String text = termText + " " + regexText;
        RuntimeEnvironment.getInstance().setAllowLeadingWildcard(true); 
        
        QueryBuilder result = instance.setFreetext(text);
        Query test = result.build();
        
        assertTrue(test instanceof BooleanQuery);
        BooleanQuery query = (BooleanQuery)test;
        
        BooleanClause clause = query.clauses().get(0);
        WildcardQuery wildCardQuery = (WildcardQuery)clause.getQuery();
        assertEquals(wildCardQuery.getTerm().text(),"*termtext*");
        
        clause = query.clauses().get(1);
        RegexpQuery regexQuery = (RegexpQuery)clause.getQuery();
        assertEquals(regexQuery.getRegexp().text(),"re[g]exText");

    }
    
    @Test
    public void testTermQueryMultipleCluase2() throws ParseException {        
        QueryBuilder instance = new QueryBuilder();
        String termText = "termtext";
        String wildCardText = "*wildcardtext*";
        String phraseText = "\"phrase text test\"";
        
        String text = termText + " " + wildCardText + phraseText;
        RuntimeEnvironment.getInstance().setAllowLeadingWildcard(true); 
        
        QueryBuilder result = instance.setFreetext(text);
        Query test = result.build();
        
        assertTrue(test instanceof BooleanQuery);
        BooleanQuery query = (BooleanQuery)test;
        
        BooleanClause clause = query.clauses().get(0);
        WildcardQuery wildCardQuery = (WildcardQuery)clause.getQuery();
        assertEquals(wildCardQuery.getTerm().text(),"*termtext*");
        
        clause = query.clauses().get(1);
        wildCardQuery = (WildcardQuery)clause.getQuery();
        assertEquals(wildCardQuery.getTerm().text(),"*wildcardtext*");
        
        clause = query.clauses().get(2);
        PhraseQuery phraseQuery = (PhraseQuery)clause.getQuery();
        String phraseQueryText = phraseQuery.getTerms()[0].text() + " "
        		+ phraseQuery.getTerms()[1].text() + " "
        		+ phraseQuery.getTerms()[2].text();
        assertEquals(phraseQueryText,"phrase text test");

    }
    
    
}
