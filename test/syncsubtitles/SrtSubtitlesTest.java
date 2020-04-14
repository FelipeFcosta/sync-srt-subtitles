/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syncsubtitles;

import syncsubtitles.SrtSubtitles;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author felip
 */
public class SrtSubtitlesTest {
    
    public SrtSubtitlesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getTotalMinutes method, of class SrtSubtitles.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetTotalMinutes() throws Exception {
        System.out.println("getTotalMinutes");
        File file = new File("resources/test_subtitle_original.srt");
        int expResult = 115;
        int result = SrtSubtitles.getTotalMinutes(file);
        assertEquals(expResult, result);
    }

    /**
     * Test of sync method, of class SrtSubtitles.
     */
    @Test
    public void testSync() throws IOException {
        System.out.println("sync");
        File original = new File("resources/test_subtitle_original.srt");
        File copy = new File("resources/test_subtitle_copy.srt");
        SrtSubtitles.sync(copy, 1.000, 7.0);
        SrtSubtitles.sync(copy, 2.000, 7.0);
        SrtSubtitles.sync(copy, 4.500, 7.0);
        SrtSubtitles.sync(copy, 0.550, 7.0);
        SrtSubtitles.sync(copy, -1.000, 7.0);
        SrtSubtitles.sync(copy, -2.000, 7.0);
        SrtSubtitles.sync(copy, -4.500, 7.0);
        SrtSubtitles.sync(copy, -0.550, 7.0);
        
        assertTrue(FileUtils.contentEquals(copy, original));
    }
}
