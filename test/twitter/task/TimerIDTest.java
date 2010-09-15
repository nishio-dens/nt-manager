/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.task;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nishio
 */
public class TimerIDTest {

    public TimerIDTest() {
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
     * Test of contains method, of class TimerID.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        String id = "TEST";
        TimerID instance = TimerID.getInstance();
        boolean expResult = false;
        boolean result = instance.contains(id);
        assertEquals(expResult, result);
    }

    /**
     * Test of addID method, of class TimerID.
     */
    @Test
    public void testAddID() throws Exception {
        System.out.println("addID");
        String id = "TEST";
        TimerID instance = TimerID.getInstance();
        instance.addID(id);
        try {
            //２回同じ物は追加できないはず
            instance.addID(id);
            fail();
        }catch(Exception e) {

        }
    }

    /**
     * Test of removeID method, of class TimerID.
     */
    @Test
    public void testRemoveID() {
        System.out.println("removeID");
        String id = "AIUEO";
        TimerID instance = TimerID.getInstance();
        boolean expResult = false;
        boolean result = instance.removeID(id);
        assertEquals(expResult, result);
        try {
            instance.addID(id);
        } catch (ExistTimerIDException ex) {
            Logger.getLogger(TimerIDTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        result = instance.removeID(id);
        assertEquals(true, result);
    }

    /**
     * Test of createSearchTimerID method, of class TimerID.
     */
    @Test
    public void testCreateSearchTimerID() {
        System.out.println("createSearchTimerID");
        String searchWord = "aiueo";
        String expResult = "SEARCH:aiueo";
        String result = TimerID.createSearchTimerID(searchWord);
        assertEquals(expResult, result);
    }

    /**
     * Test of createTimelineID method, of class TimerID.
     */
    @Test
    public void testCreateTimelineID() {
        System.out.println("createTimelineID");
        String expResult = "TIMELINE";
        String result = TimerID.createTimelineID();
        assertEquals(expResult, result);
    }

    /**
     * Test of createMentionID method, of class TimerID.
     */
    @Test
    public void testCreateMentionID() {
        System.out.println("createMentionID");
        String expResult = "MENTION";
        String result = TimerID.createMentionID();
        assertEquals(expResult, result);
    }

    /**
     * Test of createDirectMessageID method, of class TimerID.
     */
    @Test
    public void testCreateDirectMessageID() {
        System.out.println("createDirectMessageID");
        String expResult = "DIRECTMESSAGE";
        String result = TimerID.createDirectMessageID();
        assertEquals(expResult, result);
    }

    /**
     * Test of createSendDirectMessageID method, of class TimerID.
     */
    @Test
    public void testCreateSendDirectMessageID() {
        System.out.println("createSendDirectMessageID");
        String expResult = "SENDDIRECTMESSAGE";
        String result = TimerID.createSendDirectMessageID();
        assertEquals(expResult, result);
    }

}