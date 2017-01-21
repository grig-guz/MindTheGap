package com.example.grigorii.mindthegap;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by grigorii on 07/06/16.
 */
public class AllTestsSuite extends TestSuite {
    public static Test testSuite() {
        return new TestSuiteBuilder(AllTestsSuite.class)
                .includeAllPackagesUnderHere()
                .build();
    }
}
