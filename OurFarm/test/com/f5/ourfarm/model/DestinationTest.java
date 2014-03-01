package com.f5.ourfarm.model;

import com.google.gson.Gson;

import junit.framework.TestCase;

public class DestinationTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDestination() {
//        fail("Not yet implemented");
        Destination d = new Destination();
        Summary scenerySummary = new Summary();
        Gson gson = new Gson();
        String json = gson.toJson(d);
        System.out.println(json);
    }

}
