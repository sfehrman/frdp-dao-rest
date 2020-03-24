/*
 * Copyright (c) 2018-2020, ForgeRock, Inc., All rights reserved
 * Use subject to license terms.
 */
package com.forgerock.frdp.dao.rest;

import com.forgerock.frdp.common.ConstantsIF;
import com.forgerock.frdp.dao.Operation;
import com.forgerock.frdp.dao.OperationIF;
import com.forgerock.frdp.utils.JSON;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Test the REST Data Access implementation
 *
 * @author Scott Fehrman, ForgeRock, Inc.
 */
public class TestRestDataAccess {

   private static final String PROTOCOL = "https";
   private static final String HOST = "uma.example.com";
   private static final String PORT = "443";
   private static final String PATH = "content-server/rest/content-server/content";

   public static void main(String[] args) throws Exception {
      String uriBad = "http://bad.example.com/app/rest/content/BadId123";
      String uriLocation = null;
      String uriBase = PROTOCOL + "://" + HOST + ":" + PORT + "/" + PATH;
      String uriCreate = null;
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

      jsonInfo = new JSONObject();
      jsonInfo.put("language", "java");
      jsonInfo.put("package", "com.forgerock.frdp.dao.rest");
      jsonInfo.put("classname", "TestMongoDataAccess");
      jsonInfo.put("filename", "TestMongoDataAccess.java");

      /*
       * ========
       * ======== setup: no parameters, each operation provides full URI 
       * ========
       */
      // create: 0 ------------------------------------
      dao = new RestDataAccess();

      uriCreate = uriBase;

      jsonData = new JSONObject();
      jsonData.put("firstname", "John");
      jsonData.put("lastname", "Doe");
      jsonData.put("title", "Engineer");
      jsonData.put("organization", "Acme");
      jsonData.put("info", jsonInfo);

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.DATA, jsonData);

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == true : "error should be true";

      jsonOutput = operOutput.getJSON();

      uriLocation = JSON.getString(jsonOutput, ConstantsIF.URI);

      System.out.println("====");
      System.out.println("==== FAIL TEST     : create 0, DAO constructor, missing uri test");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Create output : " + operOutput.toString());
      System.out.println("==== Create json   : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // CREATE JSON structures:
      // INPUT: { "data": { "attr": "value", ... } }
      // OUTPUT: { "uri": "..." }
      // create: 1 ------------------------------------
      dao = new RestDataAccess();

      jsonData = new JSONObject();
      jsonData.put("firstname", "John");
      jsonData.put("lastname", "Doe");
      jsonData.put("title", "Engineer");
      jsonData.put("organization", "Acme");
      jsonData.put("info", jsonInfo);

      jsonQueryParams = new JSONObject();
      jsonQueryParams.put("foo", "bar");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriCreate);
      jsonInput.put(ConstantsIF.DATA, jsonData);
      jsonInput.put(ConstantsIF.QUERY_PARAMS, jsonQueryParams);

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      uriLocation = JSON.getString(jsonOutput, ConstantsIF.URI);

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : create 1, DAO constructor, with uri");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Create output : " + operOutput.toString());
      System.out.println("==== Create json   : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      /*
       * ========
       * ======== setup: use parameters to a "base" target, used by all operations
       * ========
       */
      params.put(RestDataAccess.PARAM_PROTOCOL, PROTOCOL);
      params.put(RestDataAccess.PARAM_HOST, HOST);
      params.put(RestDataAccess.PARAM_PORT, PORT);
      params.put(RestDataAccess.PARAM_PATH, PATH);

      // create: 2 ------------------------------------
      dao = new RestDataAccess(params);

      jsonInput = null;
      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == true : "error should be true";

      jsonOutput = operOutput.getJSON();

      uriLocation = JSON.getString(jsonOutput, ConstantsIF.URI);

      System.out.println("====");
      System.out.println("==== FAIL TEST     : create 2, DAO constructor params, null input");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Create output : " + operOutput.toString());
      System.out.println("==== Create json   : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // CREATE JSON structures:
      // INPUT: { "data": { "attr": "value", ... } }
      // OUTPUT: { "uri": "..." }
      // create: 3 ------------------------------------
      dao = new RestDataAccess(params);

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

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      uriLocation = JSON.getString(jsonOutput, ConstantsIF.URI);

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : create 3, DAO constructor params, with query parameters");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Create output : " + operOutput.toString());
      System.out.println("==== Create json   : " + (jsonOutput != null ? jsonOutput.toJSONString() : "null"));
      System.out.println("====");

      // create 4 ---------------------------------------------------
      dao = new RestDataAccess(params);

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

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      uriLocation = JSON.getString(jsonOutput, ConstantsIF.URI);

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : create 4, DAO constructor params, with headers");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Create output : " + operOutput.toString());
      System.out.println("==== Create json   : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // create 5 ---------------------------------------------------
      dao = new RestDataAccess(params);

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

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      uriLocation = JSON.getString(jsonOutput, ConstantsIF.URI);

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : create 5, DAO constructor params, with cookies");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Create output : " + operOutput.toString());
      System.out.println("==== Create json   : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // create 6 ---------------------------------------------------
      dao = new RestDataAccess(); // JSON must include 'uri' attribute

      jsonData = new JSONObject();
      jsonData.put("firstname", "Jack");
      jsonData.put("lastname", "Bauer");
      jsonData.put("title", "Agent");
      jsonData.put("organization", "CTU");
      jsonData.put("info", jsonInfo);

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriCreate);
      jsonInput.put(ConstantsIF.DATA, jsonData);

      operInput = new Operation(OperationIF.TYPE.CREATE);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      uriLocation = JSON.getString(jsonOutput, ConstantsIF.URI);

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : create 6, DAO default constructor");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Create output : " + operOutput.toString());
      System.out.println("==== Create json   : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      /*
       * READ JSON structures: 
       * INPUT: { "uri": "..." } 
       * OUTPUT: { "id": "...", "data": { "attr": "value", ... } }
       */
      // read 1, bad -------------------------------------------------------
      dao = new RestDataAccess(); // JSON must include 'uri' attribute

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriBad);

      operInput = new Operation(OperationIF.TYPE.READ);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == true : "error should be true";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== FAIL TEST     : read 1, DAO constructor params, bad uri");
      System.out.println("==== URI Location  : " + (uriBad != null ? uriBad : "null"));
      System.out.println("==== Read output   : " + operOutput.toString());
      System.out.println("==== Read json     : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // read 2, good -------------------------------------------------------
      dao = new RestDataAccess(); // JSON must include 'uri' attribute

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriLocation);

      operInput = new Operation(OperationIF.TYPE.READ);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : read 2, DAO constructor params, good uri");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Read output   : " + operOutput.toString());
      System.out.println("==== Read json     : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      /*
       * REPLACE JSON structures
       * INPUT: { "uri": "..." "data": { "attr": "value", ... } } 
       * OUTPUT: { }
       */
      // replace 1, fail -----------------------------------------------------
      dao = new RestDataAccess(); // JSON must include 'uri' attribute

      jsonData = new JSONObject();
      jsonData.put("firstname", "Jack");
      jsonData.put("lastname", "Bauer");
      jsonData.put("title", "Agent");
      jsonData.put("organization", "CTU");
      jsonData.put("comment", "Created from Test for MongoDataAccess class");
      jsonData.put("info", jsonInfo);
      jsonData.put("status", "Updated");

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriBad); // bad replace test
      jsonInput.put(ConstantsIF.DATA, jsonData);

      operInput = new Operation(OperationIF.TYPE.REPLACE);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput);
      } catch (Exception ex) {
         System.out.println("Caught the error: " + ex.getMessage());
      }

      assert operOutput.isError() == true : "error should be true";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== FAIL TEST     : replace 1, DAO constructor params, bad uri");
      System.out.println("==== URI Location  : " + (uriBad != null ? uriBad : "null"));
      System.out.println("==== Replace output: " + operOutput.toString());
      System.out.println("==== Replace json  : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // replace 2, success -----------------------------------------------------
      dao = new RestDataAccess(); // JSON must include 'uri' attribute

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriLocation);
      jsonInput.put(ConstantsIF.DATA, jsonData);

      operInput = new Operation(OperationIF.TYPE.REPLACE);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : replace 2, DAO constructor params");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Replace output: " + operOutput.toString());
      System.out.println("==== Replace json  : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // read 3, success -----------------------------------------------------
      dao = new RestDataAccess(); // JSON must include 'uri' attribute

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriLocation);

      operInput = new Operation(OperationIF.TYPE.READ);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : read 3, DAO constructor params, after a replace");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Read output   : " + operOutput.toString());
      System.out.println("==== Read json     : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      /*
       * DELETE JSON structures: INPUT: { "id": "..." } OUTPUT: { }
       */
      // delete 1 -----------------------------------------------------
      dao = new RestDataAccess(); // JSON must include 'uri' attribute

      jsonInput = new JSONObject();
      jsonInput.put(ConstantsIF.URI, uriLocation);

      operInput = new Operation(OperationIF.TYPE.DELETE);
      operInput.setJSON(jsonInput);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== SUCCESS TEST  : delete 1, DAO constructor params");
      System.out.println("==== URI Location  : " + (uriLocation != null ? uriLocation : "null"));
      System.out.println("==== Delete output : " + operOutput.toString());
      System.out.println("==== Delete json   : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      /*
       * SEARCH JSON structures: INPUT: { "query": { "operator": "all" } }
       */
      jsonQuery = new JSONObject();
      jsonQuery.put(ConstantsIF.OPERATOR, ConstantsIF.ALL);

      // search 1: use contructor paramters, all documents --------------------
      dao = new RestDataAccess(params);

      jsonData = new JSONObject();
      jsonData.put(ConstantsIF.QUERY, jsonQuery);

      operInput = new Operation(OperationIF.TYPE.SEARCH);
      operInput.setJSON(jsonData);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== SUCCESS TEST : search 1, DAO constructor params");
      System.out.println("==== Search output: " + operOutput.toString());
      System.out.println("==== Search json  : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      // search 2: use URI attribute, all documents ---------------------------
      dao = new RestDataAccess();

      jsonData = new JSONObject();
      jsonData.put(ConstantsIF.URI, uriBase);
      jsonData.put(ConstantsIF.QUERY, jsonQuery);

      operInput = new Operation(OperationIF.TYPE.SEARCH);
      operInput.setJSON(jsonData);

      try {
         operOutput = dao.execute(operInput); // bad create, missing "uri" test
      } catch (Exception ex) {
         System.out.println("Caught exception: " + ex.getMessage());
      }

      assert operOutput.isError() == false : "error should be false";

      jsonOutput = operOutput.getJSON();

      System.out.println("====");
      System.out.println("==== SUCCESS TEST : search 2, DAO default constructor");
      System.out.println("==== URI Base     : " + (uriBase != null ? uriBase : "null"));
      System.out.println("==== Search output: " + operOutput.toString());
      System.out.println("==== Search json  : " + (jsonOutput != null ? jsonOutput.toString() : "null"));
      System.out.println("====");

      dao.close();

      return;
   }
}
