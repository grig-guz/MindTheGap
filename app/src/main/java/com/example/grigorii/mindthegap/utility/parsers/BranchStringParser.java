package com.example.grigorii.mindthegap.utility.parsers;

import com.example.grigorii.mindthegap.model.LatLon;

import com.example.grigorii.mindthegap.utility.exceptions.MalformedLatLonSequenceException;

import java.util.ArrayList;


/**
 * Created by grigorii on 10/06/16.
 *
 * Class that contains static methods for
 * parsing Branch data from query
 */
public class BranchStringParser {

    /**
     * Main method for parsing branch data
     * @param latLonStr branch data from query
     * @return array containing LatLon objects with location of
     * elements of branches
     * @throws MalformedLatLonSequenceException
     * @throws NumberFormatException
     */
    public static ArrayList<LatLon> getBranchData(String latLonStr) throws MalformedLatLonSequenceException, NumberFormatException {

        ArrayList<LatLon> branch = new ArrayList<>();

        //Removing double brackets around initial query data
        latLonStr = removeDoubleBrackets(latLonStr);

        // Splitting latLon tuples in query response by comma
        String[] coordsInBrackets = latLonStr.split("],");

        try {

            for (String latLonEntry : coordsInBrackets) {

                branch.add(formatLatLonEntry(latLonEntry));
            }

            return branch;

        } catch (NumberFormatException e) {

            throw new MalformedLatLonSequenceException("Some of the entries in latLonStr are malformed", e);
        }

    }

    /**
     * Method for checking if specified string is wrapped in double brackets
     * @param latLonStr string to check
     * @return boolean result
     */
    public static boolean checkIfBordersAreBrackets(String latLonStr) {

        return latLonStr.charAt(0) == '[' && latLonStr.charAt(latLonStr.length() - 1) == ']';
    }

    /**
     * Method for removing double square brackets around specified string
     * @param latLonStr string to remove double square brackets in
     * @return string without double square brackets
     * @throws MalformedLatLonSequenceException
     */
    public static String removeDoubleBrackets(String latLonStr) throws MalformedLatLonSequenceException {

        // Throw exception if string is null
        if (latLonStr.equals(""))
            throw new MalformedLatLonSequenceException();

        if (checkIfBordersAreBrackets(latLonStr)) {

            latLonStr = removeSquareBrackets(latLonStr);
            if (checkIfBordersAreBrackets(latLonStr))
                return removeSquareBrackets(latLonStr);
        }

        // If some of the brackets are missing
        throw new MalformedLatLonSequenceException();
    }

    /**
     * Method that removes square brackets around specified string
     * @param latLonStr string to remove brackets in
     * @return string without brackets
     */
    public static String removeSquareBrackets(String latLonStr) {
        return latLonStr.substring(1, latLonStr.length() - 1);
    }

    /**
     * Method that removes single square bracket in front of specified strng
     * @param latLonEntry string to remove bracket in
     * @return
     */
    private static String removeFrontalSquareBracket(String latLonEntry) {
        return latLonEntry.substring(1);
    }

    /**
     *
     * @param entry
     * @return
     * @throws MalformedLatLonSequenceException
     * @throws NumberFormatException
     */
    public static LatLon formatLatLonEntry(String entry) throws MalformedLatLonSequenceException, NumberFormatException {
        if (checkIfBordersAreBrackets(entry)) {

            entry = removeSquareBrackets(entry);
        } else if (entry.charAt(0) == '[') {

            entry = removeFrontalSquareBracket(entry);
        } else throw new MalformedLatLonSequenceException();

        if (entry.contains(",")) {

            String[] latLon = entry.split(",");
            return new LatLon(Double.parseDouble(latLon[0]), Double.parseDouble(latLon[1]));
        }

        throw new MalformedLatLonSequenceException();
    }

}
