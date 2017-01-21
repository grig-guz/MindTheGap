package com.example.grigorii.mindthegap.testsForParsers;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.grigorii.mindthegap.model.Branch;
import com.example.grigorii.mindthegap.model.LatLon;
import com.example.grigorii.mindthegap.utility.exceptions.MalformedLatLonSequenceException;
import com.example.grigorii.mindthegap.utility.parsers.BranchStringParser;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grigorii on 11/06/16.
 */
public class TestBranchStringParser extends AndroidTestCase {

    public void testGetBranchData() {
        // Tests for assertTrue
        String testCase1 = "[[[0.093493,51.6037]]]";
        ArrayList<LatLon> testResult1 = new ArrayList<>();
        testResult1.add(new LatLon(0.093493,51.6037));

        String testCase2 = "[[[0.093493,51.6037],[0.091015,51.5956],[0.088596,51.5857]]]";
        ArrayList<LatLon> testResult2 = new ArrayList<>();
        testResult2.add(new LatLon(0.093493, 51.6037));
        testResult2.add(new LatLon(0.091015, 51.5956));
        testResult2.add(new LatLon(0.088596, 51.5857));

        // Tests for catching an exception
        String testCase3 = "[[0.093493,51.6037]]]";
        String testCase4 = "";
        String testCase5 = "[[0.093493,51.6037]";
        String testCase6 = "[[[2332,.fd3132]]]";
        String testCase7 = "gdsfgxcvbvcnb";
        String testCase8 = "[0.093493,51.6037]";

        List<String> exceptionTests = new ArrayList<>();
        exceptionTests.add(testCase3);
        exceptionTests.add(testCase4);
        exceptionTests.add(testCase5);
        exceptionTests.add(testCase6);
        exceptionTests.add(testCase7);
        exceptionTests.add(testCase8);


        try {
            assertTrue(BranchStringParser.getBranchData(testCase1).equals(testResult1));
        } catch (MalformedLatLonSequenceException e) {
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.fail();
        }

        try {
            assertTrue(BranchStringParser.getBranchData(testCase2).equals(testResult2));
        } catch (MalformedLatLonSequenceException e) {
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.fail();
        }

        for (String testEntry : exceptionTests) {
            try {
                BranchStringParser.getBranchData(testEntry);
                Assert.fail();
            }
            catch (MalformedLatLonSequenceException e) {}
            catch (NumberFormatException e) {}
        }



    }

//    public void testCheckIfBoardersAreBrackets() {
//        //Tests cases for assertTrue
//        final String testCase1 = "[hello]";
//        final String testCase2 = "[123.24, 12.22]";
//        final String testCase3 = "[123.24, 12.22]]";
//        final String testCase4 = "[something";
//        final String testCase5 = "something]";
//        final String testCase6 = "something";
//
//        assertTrue(BranchStringParser.checkIfBordersAreBrackets(testCase1));
//        assertTrue(BranchStringParser.checkIfBordersAreBrackets(testCase2));
//        assertTrue(BranchStringParser.checkIfBordersAreBrackets(testCase3));
//        assertFalse(BranchStringParser.checkIfBordersAreBrackets(testCase4));
//        assertFalse(BranchStringParser.checkIfBordersAreBrackets(testCase5));
//        assertFalse(BranchStringParser.checkIfBordersAreBrackets(testCase6));
//    }
//
//
//    public void testRemoveSquareBrackets() {
//        // Test cases for assertTrue
//        final String testCase1 = "[something]";
//        final String testResult1 = "something";
//        final String testCase2 = "[123.24, 12.22]";
//        final String testResult2 = "123.24, 12.22";
//
//        assertTrue(BranchStringParser.removeSquareBrackets(testCase1).equals(testResult1));
//        assertTrue(BranchStringParser.removeSquareBrackets(testCase2).equals(testResult2));
//
//   }
//
//
//    public void testRemoveDoubleBrackets() {
//        //Test cases for assertTrue
//        final String testCase1 = "[[something]]";
//        final String testCase2 = "[[[something]]]";
//        final String testCase3 = "[[123.24, 12.22]]";
//
//        final String testResult1 = "something";
//        final String testResult2 = "[something]";
//        final String testResult3 = "123.24, 12.22";
//
//        //Test cases that should throw an MalformedLatLonException
//        final String testCase4 = "[something]";
//        final String testCase5 = "[123.24, 12.22]";
//        final String testCase6 = "123.24, 12.22";
//
//        try {
//            assertTrue(BranchStringParser.removeDoubleBrackets(testCase1).equals(testResult1));
//            assertTrue(BranchStringParser.removeDoubleBrackets(testCase2).equals(testResult2));
//            assertTrue(BranchStringParser.removeDoubleBrackets(testCase3).equals(testResult3));
//
//        } catch (MalformedLatLonSequenceException e) {
//            Assert.fail("Failed removing double square brackets");
//        }
//        try {
//            BranchStringParser.removeDoubleBrackets(testCase4);
//            Assert.fail("Exception was not thrown for invalid test case 4");
//        } catch (MalformedLatLonSequenceException e1) {
//            try {
//                BranchStringParser.removeDoubleBrackets(testCase5);
//                Assert.fail("Exception was not thrown for invalid test case 5");
//            } catch (MalformedLatLonSequenceException e2){
//                try {
//                    BranchStringParser.removeDoubleBrackets(testCase6);
//                    Assert.fail("Exception was not thrown for invalid test case 6");
//                } catch (MalformedLatLonSequenceException e3) {
//
//                }
//
//            }
//        }
//    }
//
//    public void testFormatLatLonEntry() {
//        // Test for assertTrue
//        final String testCase1 = "[123.24,12.22]";
//        final LatLon testResult1 = new LatLon(123.24, 12.22);
//
//        // Tests that should throw exception
//
//        final String testCase2 = "123.24,12.22]";
//        final String testCase3 = "[123.24,12.22";
//        final String testCase4 = "123.24,12.22";
//
//        try {
//            assertTrue(BranchStringParser.formatLatLonEntry(testCase1).equals(testResult1));
//        } catch (MalformedLatLonSequenceException e) {
//            Assert.fail();
//        }
//
//        try {
//            BranchStringParser.formatLatLonEntry(testCase2);
//            Assert.fail();
//        } catch (MalformedLatLonSequenceException e) {
//
//            try {
//                BranchStringParser.formatLatLonEntry(testCase4);
//                Assert.fail();
//            } catch (MalformedLatLonSequenceException e2) {
//
//            }
//        }
//
//    }


}
