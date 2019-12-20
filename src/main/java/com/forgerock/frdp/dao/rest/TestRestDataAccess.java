/*
 * Copyright (c) 2018-2019, ForgeRock, Inc., All rights reserved
 * Use subject to license terms.
 */

package com.forgerock.frdp.dao.rest;

import com.forgerock.frdp.common.ConstantsIF;
import com.forgerock.frdp.dao.Operation;
import com.forgerock.frdp.dao.OperationIF;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Test the REST Data Access implementation
 * 
 * @author Scott Fehrman, ForgeRock, Inc.
 */

public class TestRestDataAccess {

   public static void main(String[] args) throws Exception {
      String uid = null;
      RestDataAccess dao = null;
      Map<String, String> params = new HashMap<>();
      OperationIF operInput = null;
      OperationIF operOutput = null;
      JSONObject jsonData = null;
      JSONObject jsonQuery = null;
      JSONObject jsonInput = null;
      JSONObject jsonOutput = null;
      JSONObject jsonInfo = null;
      JSONObject jsonHeaders = null;
      JSONObject jsonCookies = null;
      JSONObject jsonQueryParams = null;

      // setup
      params.put(RestDataAccess.PARAM_PROTOCOL, "https");
      params.put(RestDataAccess.PARAM_HOST, "127.0.0.1");
      params.put(RestDataAccess.PARAM_PORT, "443");
      params.put(RestDataAccess.PARAM_PATH, "TOMCAT_DEPLOYMENT/content/rest/content-server/content");

      dao = new RestDataAccess(params);

      jsonInfo = new JSONObject();
      jsonInfo.put("language", "java");
      jsonInfo.put("package", "com.forgerock.frdp.dao.rest");
      jsonInfo.put("classname", "TestMongoDataAccess");
      jsonInfo.put("filename", "TestMongoDataAccess.java");

      // CREATE JSON structures:
      // INPUT: { "data": { "attr": "value", ... } }
      // OUTPUT: { "uid": "..." }

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput); // bad create null test

      // create: 1 ------------------------------------

      jsonData = new JSONObject();
      jsonData.put("firstname", "John");
      jsonData.put("lastname", "Doe");
      jsonData.put("title", "Engineer");
      jsonData.put("organization", "Acme");
      jsonData.put("info", jsonInfo);

      jsonQueryParams = new JSONObject();
      jsonQueryParams.put("foo", "bar");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.DATA, jsonData);
      jsonInput.put(ConstantsIF.QUERY_PARAMS, jsonQueryParams);

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== Create output: " + operOutput.toString());
      System.out.println("==== Create json:   " + jsonOutput.toString());
      System.out.println("====");

      // create 2 ---------------------------------------------------
      jsonData = new JSONObject();
      jsonData.put("firstname", "John");
      jsonData.put("lastname", "Hancock");
      jsonData.put("title", "Leader");
      jsonData.put("organization", "Gov");
      jsonData.put("info", jsonInfo);

      jsonHeaders = new JSONObject();
      jsonHeaders.put("X-FRDP-RS-Owner", "bjensen");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.DATA, jsonData);
      jsonInput.put(ConstantsIF.HEADERS, jsonHeaders);

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      // create 3 ---------------------------------------------------
      jsonData = new JSONObject();
      jsonData.put("firstname", "Jack");
      jsonData.put("lastname", "Sparro");
      jsonData.put("title", "Captain");
      jsonData.put("organization", "Trading");
      jsonData.put("info", jsonInfo);

      jsonCookies = new JSONObject();
      jsonCookies.put("ssotoken", "abc-123");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.DATA, jsonData);
      jsonInput.put(ConstantsIF.COOKIES, jsonCookies);

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      // create 4 ---------------------------------------------------
      jsonData = new JSONObject();
      jsonData.put("firstname", "Jack");
      jsonData.put("lastname", "Bauer");
      jsonData.put("title", "Agent");
      jsonData.put("organization", "CTU");
      jsonData.put("info", jsonInfo);

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.DATA, jsonData);

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      uid = jsonOutput.get(ConstantsIF.UID).toString();

      System.out.println("====");
      System.out.println("==== Create output: " + operOutput.toString());
      System.out.println("==== Create json:   " + jsonOutput.toString());
      System.out.println("====");

      /*
       * READ JSON structures: INPUT: { "uid": "..." } OUTPUT: { "id": "...", "data":
       * { "attr": "value", ... } }
       */
      // read -------------------------------------------------------
      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.UID, "BadId123");

      operInput = new Operation(OperationIF.TYPE.READ);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput); // bad read test

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.UID, uid);

      operInput = new Operation(OperationIF.TYPE.READ);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== Read output: " + operOutput.toString());
      System.out.println("==== Read json:   " + jsonOutput.toString());
      System.out.println("====");

      /*
       * REPLACE JSON structures: INPUT: { "uid": "..." "data": { "attr": "value", ...
       * } } OUTPUT: { }
       */
      // replace -----------------------------------------------------
      jsonData = new JSONObject();
      jsonData.put("firstname", "Jack");
      jsonData.put("lastname", "Bauer");
      jsonData.put("title", "Agent");
      jsonData.put("organization", "CTU");
      jsonData.put("comment", "Created from Test for MongoDataAccess class");
      jsonData.put("info", jsonInfo);
      jsonData.put("status", "Updated");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.UID, "NotExistUid"); // bad replace test
      jsonInput.put(ConstantsIF.DATA, jsonData);

      operInput = new Operation(OperationIF.TYPE.REPLACE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== Replace output: " + operOutput.toString());
      System.out.println("==== Replace json:   " + jsonOutput.toString());
      System.out.println("====");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.UID, uid);
      jsonInput.put(ConstantsIF.DATA, jsonData);

      operInput = new Operation(OperationIF.TYPE.REPLACE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== Replace output: " + operOutput.toString());
      System.out.println("==== Replace json:   " + jsonOutput.toString());
      System.out.println("====");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.UID, uid);

      operInput = new Operation(OperationIF.TYPE.READ);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== Read output: " + operOutput.toString());
      System.out.println("==== Read json:   " + jsonOutput.toString());
      System.out.println("====");

      /*
       * DELETE JSON structures: INPUT: { "id": "..." } OUTPUT: { }
       */
      // delete -----------------------------------------------------
      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.UID, uid);

      operInput = new Operation(OperationIF.TYPE.DELETE);
      operInput.setJSON(jsonInput);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== Delete output: " + operOutput.toString());
      System.out.println("==== Delete json:   " + jsonOutput.toString());
      System.out.println("====");

      /*
       * SEARCH JSON structures: INPUT: { "query": { "operator": "all" } }
       */

      // search: all documents
      jsonQuery = new JSONObject();
      jsonQuery.put(ConstantsIF.OPERATOR, ConstantsIF.ALL);

      jsonData = new JSONObject();
      jsonData.put(ConstantsIF.QUERY, jsonQuery);

      operInput = new Operation(OperationIF.TYPE.SEARCH);
      operInput.setJSON(jsonData);

      operOutput = dao.execute(operInput);

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== Search output: " + operOutput.toString());
      System.out.println("==== Search json:   " + jsonOutput.toString());
      System.out.println("====");

      dao.close();

      return;
   }
}
