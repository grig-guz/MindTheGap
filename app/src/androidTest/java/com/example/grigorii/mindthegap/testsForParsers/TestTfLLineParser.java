package com.example.grigorii.mindthegap.testsForParsers;

import android.test.AndroidTestCase;

import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.model.Branch;
import com.example.grigorii.mindthegap.model.LatLon;
import com.example.grigorii.mindthegap.model.Line;
import com.example.grigorii.mindthegap.model.Station;
import com.example.grigorii.mindthegap.model.StationManager;
import com.example.grigorii.mindthegap.utility.exceptions.MalformedLatLonSequenceException;
import com.example.grigorii.mindthegap.utility.exceptions.TfLLineDataMissingException;
import com.example.grigorii.mindthegap.utility.parsers.BranchStringParser;
import com.example.grigorii.mindthegap.utility.parsers.TfLLineParser;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by grigorii on 12/06/16.
 */
public class TestTfLLineParser extends AndroidTestCase {


    public void testParseLine() throws IOException{
        try {
            StringBuilder builder = getDataFromFile(R.raw.line_query_result);
            String queryResult = builder.toString();
            Line correctLine = buildCorrectLineResult();
            Line lineFromRawData = TfLLineParser.parseLine(queryResult);


            assertEquals("Line ids are different",correctLine.getId(), lineFromRawData.getId());
            assertEquals("Line names are different", correctLine.getName(), lineFromRawData.getName());
            assertEquals("Branches are different", correctLine.getBranches(), lineFromRawData.getBranches());


        } catch (JSONException e) {
            Assert.fail("JSONException was thrown");
        } catch (MalformedLatLonSequenceException e) {
            Assert.fail("MalformedLatLonSequenceException was thrown");
        } catch (TfLLineDataMissingException e) {
            Assert.fail("TfLLineDataMissingException was thrown");
        }
    }

    public Line buildCorrectLineResult() throws IOException, JSONException, TfLLineDataMissingException {
        String branchSeq = "[[[-0.335219,51.5923],[-0.316911,51.5818],[-0.308433,51.5702]," +
                "[-0.304,51.5626],[-0.296852,51.5523],[-0.275891,51.544]," +
                "[-0.257882,51.5363],[-0.244282,51.5323],[-0.225014,51.5305]," +
                "[-0.204571,51.5342],[-0.194229,51.535],[-0.185756,51.5298]," +
                "[-0.18378,51.5233],[-0.175686,51.5166],[-0.170147,51.5203]," +
                "[-0.163204,51.5223],[-0.157127,51.5229],[-0.146441,51.5233]," +
                "[-0.141899,51.5152],[-0.133794,51.5101],[-0.127273,51.5074]," +
                "[-0.122662,51.5071],[-0.114776,51.5033],[-0.11231,51.4988]," +
                "[-0.100601,51.4945]]]";

        Line line = new Line("bakerloo", "Bakerloo");
        Set<Branch> branches = new HashSet<>();
        Branch testBranch = new Branch();

        try {
            testBranch.setStationSeq(BranchStringParser.getBranchData(branchSeq));
            branches.add(testBranch);
            line.setBranches(branches);
            StringBuilder builder = getDataFromFile(R.raw.stop_point_sequences);
            line = TfLLineParser.parseStations(new JSONArray(builder.toString()), line);

            return line;

        } catch (MalformedLatLonSequenceException e) {
            Assert.fail("So tired of these numbers...");
        }

        // Unreachable but required
        return null;
    }

    public StringBuilder getDataFromFile (int id) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        mContext.getResources().openRawResource(id)));
        StringBuilder builder = new StringBuilder();
        String buffer;

        while ((buffer=reader.readLine()) != null) {
            builder.append(buffer);
        }
        return builder;
    }
//
//
////    public void testParseBranches() {
////
////        String[] queryResult = new String[] {"[[[-0.335219,51.5923],[-0.316911,51.5818],[-0.308433,51.5702]," +
////                "[-0.304,51.5626],[-0.296852,51.5523],[-0.275891,51.544]," +
////                "[-0.257882,51.5363],[-0.244282,51.5323],[-0.225014,51.5305]," +
////                "[-0.204571,51.5342],[-0.194229,51.535],[-0.185756,51.5298]," +
////                "[-0.18378,51.5233],[-0.175686,51.5166],[-0.170147,51.5203]," +
////                "[-0.163204,51.5223],[-0.157127,51.5229],[-0.146441,51.5233]," +
////                "[-0.141899,51.5152],[-0.133794,51.5101],[-0.127273,51.5074]," +
////                "[-0.122662,51.5071],[-0.114776,51.5033],[-0.11231,51.4988]," +
////                "[-0.100601,51.4945]]]","[[[-0.345,55.34562]]]"};
////
////        Set<Branch> branchesFromTestData = new HashSet<>();
////        Set<Branch> branchesFromRealData;
////
////        JSONArray array = new JSONArray();
////        array.put("[[[-0.335219,51.5923],[-0.316911,51.5818],[-0.308433,51.5702]," +
////                "[-0.304,51.5626],[-0.296852,51.5523],[-0.275891,51.544]," +
////                "[-0.257882,51.5363],[-0.244282,51.5323],[-0.225014,51.5305]," +
////                "[-0.204571,51.5342],[-0.194229,51.535],[-0.185756,51.5298]," +
////                "[-0.18378,51.5233],[-0.175686,51.5166],[-0.170147,51.5203]," +
////                "[-0.163204,51.5223],[-0.157127,51.5229],[-0.146441,51.5233]," +
////                "[-0.141899,51.5152],[-0.133794,51.5101],[-0.127273,51.5074]," +
////                "[-0.122662,51.5071],[-0.114776,51.5033],[-0.11231,51.4988]," +
////                "[-0.100601,51.4945]]]");
////        array.put("[[[-0.335219,51.5923],[-0.316911,51.5818]," +
////                "[-0.308433,51.5702],[-0.304,51.5626],[-0.296852,51.5523]," +
////                "[-0.275891,51.544],[-0.257882,51.5363],[-0.244282,51.5323]," +
////                "[-0.225014,51.5305],[-0.204571,51.5342],[-0.194229,51.535]," +
////                "[-0.185756,51.5298],[-0.18378,51.5233],[-0.175686,51.5166]," +
////                "[-0.170147,51.5203],[-0.163204,51.5223],[-0.157127,51.5229]," +
////                "[-0.146441,51.5233],[-0.141899,51.5152],[-0.133794,51.5101]," +
////                "[-0.127273,51.5074],[-0.122662,51.5071],[-0.114776,51.5033]," +
////                "[-0.11231,51.4988],[-0.100601,51.4945]]]");
////        array.put("[[[-0.345,55.34562]]]");
////
////        try {
////            for (String branch : queryResult) {
////                Branch testBranch = new Branch();
////                testBranch.setStationSeq(BranchStringParser.getBranchData(branch));
////                branchesFromTestData.add(testBranch);
////
////            }
////        } catch (MalformedLatLonSequenceException e) {
////            Assert.fail("Error while forming branchesFromTestData");
////        }
////        try {
////            branchesFromRealData = TfLLineParser.parseBranches(array);
////            if (! branchesFromRealData.equals(branchesFromTestData)) {
////                Assert.fail("Test and real data branches are different");
////            }
////        } catch (Throwable e) {
////            Assert.fail("Error while formingBranches from real data");
////        }
////    }
}
