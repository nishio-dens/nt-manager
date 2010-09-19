/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.hashtag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import twitter.manage.TweetManager;
import static org.junit.Assert.*;
import twitter.util.MultiSortedMap;

/**
 *
 * @author nishio
 */
public class HashtagSearcherTest {

    private TweetManager manager = new TweetManager();
    private HashtagSearcher searcher;

    public HashtagSearcherTest() {
        try {
            manager.loginTwitter();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HashtagSearcherTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HashtagSearcherTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        searcher = new HashtagSearcher(manager);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDescendantHashtagCount method, of class HashtagSearcher.
     */
    @Test
    public void testGetDescendantHashtagCount() {
        System.out.println("getDescendantHashtagCount");
        String searchWord = "シャナ";
        HashtagSearcher instance = searcher;

        //TOO BAD TEST
        MultiSortedMap<Integer, String> result = instance.getDescendantHashtagCount(searchWord);
        for(Integer key : result.getKeys()) {
            System.out.println("KEY:" + key + " value:" + result.get(key));
        }
    }

    /**
     * Test of calcSearchedHashtagCount method, of class HashtagSearcher.
     */
    @Test
    public void testCalcSearchedHashtagCount() {
        System.out.println("calcSearchedHashtagCount");
        String searchWord = "#FF15";
        HashtagSearcher instance = searcher;

        //TOO BAD TEST
        Map result = instance.calcSearchedHashtagCount(searchWord);
        assertEquals(14, result.get("#FF15"));
    }

    /**
     * Test of getHashtagCount method, of class HashtagSearcher.
     */
    @Test
    public void testGetHashtagCount() {
        System.out.println("getHashtagCount");
        String message = "これは#hashタグのテストです";
        HashtagSearcher instance = searcher;

        Map result = instance.getHashtagCount(message);
        assertEquals(1, result.get("#hash") );

        message = "これは#hashタグのテストです#hash,#fj";
        result = instance.getHashtagCount(message);
        assertEquals(2, result.get("#hash"));
        assertEquals(1, result.get("#fj"));
        assertEquals(null, result.get("#test"));

    }

}