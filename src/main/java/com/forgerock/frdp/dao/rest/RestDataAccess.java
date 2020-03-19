/*
 * Copyright (c) 2018-2020, ForgeRock, Inc., All rights reserved
 * Use subject to license terms.
 */
package com.forgerock.frdp.dao.rest;

import com.forgerock.frdp.common.ConstantsIF;
import com.forgerock.frdp.common.CoreIF;
import com.forgerock.frdp.dao.DataAccess;
import com.forgerock.frdp.dao.Operation;
import com.forgerock.frdp.dao.OperationIF;
import com.forgerock.frdp.utils.JSON;
import com.forgerock.frdp.utils.STR;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Implements a REST / JSON service that uses the Data Access Object (DAO) to
 * make http calls to a service that can consume the operations. The JAX /
 * Jersey client "REST" library is used to implement this class. The DAO contain
 * data and an operations.
 *
 * There are two modes for this class:
 *
 * 1: Static base target URI Provide information about the target URI, this will
 * be used for all operations Individual operations can provide a supplement
 * path that will be appended to the "base Target" URI. The "base Target" path
 * is set with parameters for components of a base URI: "protocol", "host",
 * "port", "path"
 *
 * 2: Dynamic full target URI No base target URI information is provided during
 * object creation Each operation must provide a full / complete URI
 *
 * DAO Operations map to http operations:
 *
 * <pre>
 * CREATE  | POST
 * SEARCH  | GET
 * READ    | GET
 * REPLACE | PUT
 * DELETE  | DELETE
 * </pre>
 *
 * DAO JSON structure:
 *
 * <pre>
 * {
 *   "uid": "...",
 *   "data | form": {
 *      "attr": "value",
 *      ...
 *   },
 *   "cookies": {
 *     "name": "value"
 *   },
 *   "headers": {
 *     "Content-Type": "application/json",
 *     "name": "value",
 *     ...
 *   },
 *   "queryParams": {
 *     "name1": "value1",
 *     "nameX": "valueX"
 *   },
 *   "path": ".../...", // appended to base target (static base target only)
 *   "uri": "protocol://host:port/..." // (dynamic full target only)
 * }
 * </pre>
 *
 * @author Scott Fehrman, ForgeRock, Inc.
 */
public class RestDataAccess extends DataAccess {

   public static final String PARAM_PROTOCOL = "protocol";
   public static final String PARAM_HOST = "host";
   public static final String PARAM_PORT = "port";
   public static final String PARAM_PATH = "path";

   private boolean _haveBaseTarget = false;
   private final String CLASS = this.getClass().getName();
   private Client _client = null;
   private WebTarget _target = null;
   private JSONParser _parser = null;

   /**
    * Constructor, creates object with a static base target URI.
    *
    * The parameters must include connection information for the URI:
    * "protocol", "host", "port", "path"
    *
    * @param params Map<String, String> configuration parameters
    * @throws Exception
    */
   public RestDataAccess(Map<String, String> params) throws Exception {
      super(params);

      String METHOD = "RestDataAccess(params)";

      _logger.entering(CLASS, METHOD);

      if (params != null && !params.isEmpty()) {
         _haveBaseTarget = true;
      }

      this.init();

      _logger.exiting(CLASS, METHOD);

      return;
   }

   /**
    * Constructor, creates object for dynamic URI operations.
    *
    * Each operation must contain a "uri" attribute for the complete target
    *
    * @throws Exception
    */
   public RestDataAccess() throws Exception {
      super();

      String METHOD = "RestDataAccess()";

      _logger.entering(CLASS, METHOD);

      this.init();

      _logger.exiting(CLASS, METHOD);

      return;
   }

   /**
    * Disable copying of the object
    */
   @Override
   public CoreIF copy() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   /**
    * Validate the OperationIF object, overrides superclass method.
    *
    * Checks for "uri" JSON attribute if base target is false
    *
    * @param oper the OperationIF object
    * @throws Exception could not validate the operation
    */
   @Override
   protected void validate(final OperationIF oper) throws Exception {
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      String uri = null;
      JSONObject json = null;

      _logger.entering(CLASS, METHOD);

      super.validate(oper);

      if (!_haveBaseTarget) {
         json = oper.getJSON();
         uri = JSON.getString(json, ConstantsIF.URI);

         if (STR.isEmpty(uri)) {
            throw new Exception("No base target, required attribute '"
               + ConstantsIF.URI + "' is empty");
         }
      }

      _logger.exiting(CLASS, METHOD);

      return;
   }

   /**
    * Execute the HTTP REST operation
    *
    * @param operInput OperationIF input object
    * @return OperationIF output object
    */
   @Override
   public final OperationIF execute(final OperationIF operInput) {
      boolean error = false;
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      StringBuilder msg = new StringBuilder(CLASS + ":" + METHOD + ": ");
      OperationIF operOutput = null;

      _logger.entering(CLASS, METHOD);

      try {
         this.validate(operInput);
      } catch (Exception ex) {
         error = true;
         msg.append(ex.getMessage());
         if (operInput == null) {
            operOutput = new Operation(OperationIF.TYPE.NULL);
         } else {
            operOutput = new Operation(operInput.getType());
         }
         operOutput.setError(true);
         operOutput.setState(STATE.FAILED);
         operOutput.setStatus(msg.toString());
         operOutput.setJSON(new JSONObject());
      }

      if (!error) {
         operOutput = this.submitRequest(operInput);
      } else {
         _logger.log(Level.WARNING,
            operOutput == null ? "dataOutput is null" : operOutput.getStatus());
      }

      _logger.exiting(CLASS, METHOD);

      return operOutput;
   }

   /**
    * Implement close interface method
    */
   @Override
   public void close() {
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();

      _logger.entering(CLASS, METHOD);

      if (_client != null) {
         _client.close();
      }

      _logger.exiting(CLASS, METHOD);

      return;
   }

   /*
    * =============== PRIVATE METHODS ===============
    */
   /**
    * Process HTTP Request
    *
    * <pre>
    * JSON input:
    * {
    *   "uid": "...", // optional for: READ, REPLACE, DELETE
    *   "data | form": { // required for: CREATE, REPLACE
    *      "attr": "value",
    *      ...
    *   },
    *   "cookies": {
    *     "name": "value"
    *   },
    *   "headers": {
    *     "Content-Type": "application/json",
    *     "name": "value",
    *     ...
    *   },
    *   "queryParams": {
    *     "name1": "value1",
    *     "nameX": "valueX"
    *   },
    *   "path": ".../...", // appended to base target (static base target only)
    *   "uri": "protocol://host:port/..." // (dynamic full target only)
    * }
    * </pre>
    *
    * @param operInput OperationIF input
    * @return OperationIF output
    */
   private OperationIF submitRequest(final OperationIF operInput) {
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      String msg = null;
      String uid = null;
      String path = null;
      String name = null;
      String value = null;
      String uri = null;
      OperationIF operOutput = null;
      Builder builder = null;
      Response response = null;
      JSONObject jsonInput = null;
      JSONObject jsonQueryParams = null;
      JSONObject jsonHeaders = null;
      JSONObject jsonData = null;
      JSONObject jsonCookies = null;
      MediaType acceptType = null;
      MediaType contentType = null;
      MultivaluedMap<String, Object> headers = null;
      WebTarget target = null;
      Form form = null;
      OperationIF.TYPE oper = null;
      List<Cookie> cookies = null;

      _logger.entering(CLASS, METHOD);

      oper = operInput.getType();

      jsonInput = operInput.getJSON();

      if (_logger.isLoggable(DEBUG_LEVEL)) {
         _logger.log(DEBUG_LEVEL, "json=''{0}''",
            new Object[]{jsonInput != null ? jsonInput : NULL});
      }

      /*
       * Set the "target"
       * If flag "_haveBaseTarget" is true ...
       *    _target exists with a base URI, process path options
       * Else ...
       *    _target will be null, need to create a target with "uri"
       */
      if (_haveBaseTarget) {
         /*
          * Get "path" - if "path" exists, append to target
          * Get "uid" - if "uid" exists, append to target
          */
         if (jsonInput.containsKey(ConstantsIF.UID)) {
            uid = JSON.getString(jsonInput, ConstantsIF.UID);
         }

         if (jsonInput.containsKey(ConstantsIF.PATH)) {
            path = JSON.getString(jsonInput, ConstantsIF.PATH);
         }

         if (path != null) {
            if (uid != null) {
               target = _target.path(path).path(uid);
            } else {
               target = _target.path(path);
            }
         } else if (uid != null) {
            target = _target.path(uid);
         } else {
            target = _target;
         }
      } else {
         uri = JSON.getString(jsonInput, ConstantsIF.URI);

         target = _client.target(uri);
      }

      /*
       * Get the Query Parameters, add to the "target"
       */
      if (jsonInput.containsKey(ConstantsIF.QUERY_PARAMS)) {
         jsonQueryParams = JSON.getObject(jsonInput, ConstantsIF.QUERY_PARAMS);

         if (jsonQueryParams != null && !jsonQueryParams.isEmpty()) {
            for (Object o : jsonQueryParams.keySet()) {
               if (o != null && o instanceof String && !STR.isEmpty((String) o)) {
                  name = (String) o;
                  value = JSON.getString(jsonQueryParams, name);

                  if (!STR.isEmpty(value)) {
                     target = target.queryParam(name, value);
                  }
               }
            }
         }
      }

      /*
       * Get the "cookies" from the JSON input add to the cookie list
       */
      if (jsonInput.containsKey(ConstantsIF.COOKIES)) {
         cookies = new LinkedList<>();

         jsonCookies = JSON.getObject(jsonInput, ConstantsIF.COOKIES);

         if (jsonCookies != null && !jsonCookies.isEmpty()) {
            for (Object o : jsonCookies.keySet()) {
               if (o != null && o instanceof String && !STR.isEmpty((String) o)) {
                  name = (String) o;
                  value = JSON.getString(jsonCookies, name);

                  if (!STR.isEmpty(value)) {
                     cookies.add(new Cookie(name, value));
                  }
               }
            }
         }
      }

      // Get the "headers" from the JSON input
      // - if "accept", set accept Type
      // - if "content-type", set content Type
      // - else add to header map
      if (jsonInput.containsKey(ConstantsIF.HEADERS)) {
         headers = new MultivaluedHashMap<String, Object>();

         jsonHeaders = JSON.getObject(jsonInput, ConstantsIF.HEADERS);

         if (jsonHeaders != null && !jsonHeaders.isEmpty()) {
            for (Object o : jsonHeaders.keySet()) {
               if (o != null && o instanceof String && !STR.isEmpty((String) o)) {
                  name = (String) o;
                  value = JSON.getString(jsonHeaders, name);

                  if (!STR.isEmpty(value)) {
                     switch (name) {
                        case ConstantsIF.ACCEPT: {
                           switch (value) {
                              case ConstantsIF.TYPE_JSON: {
                                 acceptType = MediaType.APPLICATION_JSON_TYPE;
                                 break;
                              }
                              case ConstantsIF.TYPE_URLENCODED: {
                                 acceptType = MediaType.APPLICATION_FORM_URLENCODED_TYPE;
                                 break;
                              }
                              case ConstantsIF.TYPE_WILDCARD: {
                                 acceptType = MediaType.WILDCARD_TYPE;
                                 break;
                              }
                           }
                           break;
                        }
                        case ConstantsIF.CONTENT_TYPE: {
                           switch (value) {
                              case ConstantsIF.TYPE_JSON: {
                                 contentType = MediaType.APPLICATION_JSON_TYPE;
                                 break;
                              }
                              case ConstantsIF.TYPE_URLENCODED: {
                                 contentType = MediaType.APPLICATION_FORM_URLENCODED_TYPE;
                                 break;
                              }
                           }
                           break;
                        }
                        default: {
                           headers.add(name, value);
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      if (acceptType == null) {
         acceptType = MediaType.APPLICATION_JSON_TYPE;
      }

      if (contentType == null) {
         contentType = MediaType.APPLICATION_JSON_TYPE;
      }

      /*
       * Create the Request "builder", set the Headers and the Cookies
       */
      builder = target.request(acceptType);

      if (cookies != null && !cookies.isEmpty()) {
         for (Cookie c : cookies) {
            builder = builder.cookie(c);
         }
      }

      if (headers != null && !headers.isEmpty()) {
         builder = builder.headers(headers);
      }

      /*
       * execute "builder", based on operation type
       */
      switch (oper) {
         case CREATE: {
            if (contentType == MediaType.APPLICATION_JSON_TYPE) {
               jsonData = JSON.getObject(jsonInput, ConstantsIF.DATA);

               if (jsonData != null) {
                  response = builder.post(Entity.entity(jsonData.toString(), contentType));
               } else {
                  msg = oper.toString() + ": JSON input does not contain a 'data' object";
               }
            } else if (contentType == MediaType.APPLICATION_FORM_URLENCODED_TYPE) {
               if (jsonInput.containsKey(ConstantsIF.FORM)) {
                  form = this.getForm(jsonInput);

                  if (form != null) {
                     response = builder.post(Entity.entity(form, contentType));
                  } else {
                     msg = oper.toString() + ": URL encoded form is null";
                  }
               } else {
                  msg = oper.toString() + ": JSON input does not contain a 'form' object";
               }
            } else {
               msg = oper.toString() + ": Undefined MediaType";
            }
            break;
         }
         case SEARCH:
         case READ: {
            response = builder.get();
            break;
         }
         case REPLACE: {
            if (contentType == MediaType.APPLICATION_JSON_TYPE) {
               if (jsonInput.containsKey(ConstantsIF.DATA)) {
                  jsonData = JSON.getObject(jsonInput, ConstantsIF.DATA);

                  if (jsonData != null && !jsonData.isEmpty()) {
                     response = builder.put(Entity.entity(jsonData.toString(), contentType));
                  } else {
                     msg = oper.toString() + ": JSON 'data' is null or empty";
                  }
               } else {
                  msg = oper.toString() + ": JSON input does not contain a 'data' object";
               }
            } else if (contentType == MediaType.APPLICATION_FORM_URLENCODED_TYPE) {
               if (jsonInput.containsKey(ConstantsIF.FORM)) {
                  form = this.getForm(jsonInput);

                  if (form != null) {
                     response = builder.put(Entity.entity(form, contentType));
                  } else {
                     msg = oper.toString() + ": URL encoded form is null";
                  }
               } else {
                  msg = oper.toString() + ": JSON input does not contain a 'form' object";
               }
            } else {
               msg = oper.toString() + ": Undefined MediaType";
            }
            break;
         }
         case DELETE: {
            response = builder.delete();
            break;
         }
         default: {
            msg = "Unsupported operation '" + oper.toString() + "'";
            break;
         }
      }

      if (msg == null) {
         operOutput = this.getOperationFromResponse(response, operInput);
      } else {
         operOutput = new Operation(operInput.getType());
         operOutput.setError(true);
         operOutput.setState(STATE.FAILED);
         operOutput.setStatus(msg);
      }

      _logger.exiting(CLASS, METHOD);

      return operOutput;
   }

   /**
    * Get HTTP Form from JSON object
    *
    * @param jsonInput JSONObject form data
    * @return Form HTTP form
    */
   private Form getForm(final JSONObject jsonInput) {
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      String name = null;
      String value = null;
      JSONObject jsonForm = null;
      Form form = null;

      _logger.entering(CLASS, METHOD);

      jsonForm = JSON.getObject(jsonInput, ConstantsIF.FORM);

      if (jsonForm != null && !jsonForm.isEmpty()) {
         form = new Form();
         for (Object o : jsonForm.keySet()) {
            if (o != null && o instanceof String && !STR.isEmpty((String) o)) {
               name = (String) o;
               value = JSON.getString(jsonForm, name);
               if (!STR.isEmpty(value)) {
                  form.param(name, value);
               }
            }
         }
      }

      _logger.exiting(CLASS, METHOD);

      return form;
   }

   /**
    * Get Operation object from HTTP Response
    *
    * @param response Response object
    * @param operInput OperationIF input
    * @return OperationIF output
    */
   private OperationIF getOperationFromResponse(final Response response, final OperationIF operInput) {
      boolean error = false;
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      String uriRequest = null;
      String uriCreated = null;
      String entity = null;
      String value = null;
      OperationIF operOutput = null;
      JSONObject jsonInput = null;
      JSONObject jsonOutput = null;
      JSONObject jsonHeaders = null;

      _logger.entering(CLASS, METHOD);

      jsonOutput = new JSONObject();

      if (response == null) {
         error = true;

         operOutput = new Operation(OperationIF.TYPE.NULL);
         operOutput.setState(STATE.ERROR);
         operOutput.setStatus(METHOD + ": input response is null");

         _logger.log(Level.SEVERE, "input response is null");
      } else {
         if (operInput == null) {
            error = true;

            operOutput = new Operation(OperationIF.TYPE.NULL);
            operOutput.setError(error);
            operOutput.setState(STATE.ERROR);
            operOutput.setStatus(METHOD + ": input operation is null");

            _logger.log(Level.SEVERE, "input operation is null");
         } else {
            operOutput = new Operation(operInput.getType());

            jsonInput = operInput.getJSON();
            if (jsonInput != null && !jsonInput.isEmpty()) {
               uriRequest = JSON.getString(jsonInput, ConstantsIF.URI);
            }
            if (STR.isEmpty(uriRequest)) {
               uriRequest = NULL;
            }
         }
      }

      if (!error && response != null && operInput != null) {
         entity = response.readEntity(String.class);

         switch (operInput.getType()) {
            case CREATE: // HTTP POST
            {
               switch (response.getStatus()) {
                  case 200: // OK
                  case 201: // CREATED
                  {
                     operOutput.setState(STATE.SUCCESS);
                     operOutput.setStatus("Response: "
                        + response.getStatusInfo().toString());

                     if (!STR.isEmpty(entity)) {
                        try {
                           jsonOutput = this.parseEntity(entity);
                        } catch (Exception ex) {
                           operOutput.setStatus(entity);
                        }
                     }

                     try {
                        uriCreated = this.getURIFromResponse(response);
                     } catch (Exception ex) {
                        error = true;
                        operOutput.setError(error);
                        operOutput.setState(STATE.ERROR);
                        operOutput.setStatus("Could not get URI from Response: "
                           + ex.getMessage());
                     }

                     if (!STR.isEmpty(uriCreated)) {
                        jsonOutput.put(ConstantsIF.URI, uriCreated);
                     }
                     break;
                  }
                  case 302: // FOUND (REDIRECT)
                  {
                     jsonHeaders = new JSONObject();

                     for (String s : response.getHeaders().keySet()) {
                        if (!STR.isEmpty(s)) {
                           value = response.getHeaderString(s);
                           if (!STR.isEmpty(value)) {
                              jsonHeaders.put(s, value);
                           }
                        }
                     }

                     jsonOutput.put(ConstantsIF.HEADERS, jsonHeaders);

                     operOutput.setState(STATE.WARNING);
                     operOutput.setStatus("Redirect: " + response.getStatus()
                        + ", " + response.getStatusInfo().toString());
                     break;
                  }
                  case 400: // BAD REQUEST
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.FAILED);
                     operOutput.setStatus("BAD REQUEST: " + response.getStatus()
                        + ", " + response.getStatusInfo().toString()
                        + ", Entity='" + (entity == null ? NULL : entity) + "'");
                     break;
                  }
                  case 403: // FORBIDDEN
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.FAILED);
                     operOutput.setStatus("Forbidden create: "
                        + response.getStatus()
                        + ", " + response.getStatusInfo().toString()
                        + ", Entity='"
                        + (entity == null ? NULL : entity) + "'");
                     break;
                  }
                  case 404: // NOT FOUND
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.NOTEXIST);
                     operOutput.setStatus(response.getStatusInfo().toString()
                        + ", " + uriRequest);
                     break;
                  }
                  default: {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.ERROR);
                     operOutput.setStatus("Default Response: "
                        + response.getStatus()
                        + ", " + response.getStatusInfo().toString()
                        + ", Entity='"
                        + (entity == null ? NULL : entity) + "'");
                     break;
                  }
               }

               break;
            }
            case READ: // HTTP GET
            case SEARCH: // HTTP GET
            {
               if (!STR.isEmpty(entity)) {
                  try {
                     jsonOutput = this.parseEntity(entity);
                  } catch (Exception ex) {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.WARNING);
                     operOutput.setStatus("Could not parse response entity: "
                        + ex.getMessage());
                  }
               }
               if (!error) {
                  switch (response.getStatus()) {
                     case 200: // OK
                     {
                        operOutput.setError(false);
                        operOutput.setState(STATE.SUCCESS);
                        operOutput.setStatus("Found document");
                        break;
                     }
                     case 401: // UNAUTHORIZED
                     {
                        error = true;
                        operOutput.setError(error);
                        operOutput.setState(STATE.NOTAUTHORIZED);
                        operOutput.setStatus(response.getStatusInfo() + ": '"
                           + ", Entity='" + (entity == null ? NULL : entity) + "'");
                        break;
                     }
                     case 400: // BAD REQUEST
                     {
                        error = true;
                        operOutput.setError(error);
                        operOutput.setState(STATE.FAILED);
                        operOutput.setStatus("BAD REQUEST: " + response.getStatus()
                           + ", " + response.getStatusInfo().toString()
                           + ", Entity='" + (entity == null ? NULL : entity) + "'");
                        break;
                     }
                     case 403: // FORBIDDEN
                     {
                        error = true;
                        operOutput.setError(error);
                        operOutput.setState(STATE.FAILED);
                        operOutput.setStatus("Forbidden read/search: "
                           + response.getStatus() + ", " + response.getStatusInfo().toString()
                           + ", Entity='" + (entity == null ? NULL : entity) + "'");
                        break;
                     }
                     case 404: // NOT FOUND
                     {
                        error = true;
                        operOutput.setError(error);
                        operOutput.setState(STATE.NOTEXIST);
                        operOutput.setStatus(response.getStatusInfo().toString()
                           + ", " + uriRequest);
                        break;
                     }
                     default: {
                        error = true;
                        operOutput.setError(error);
                        operOutput.setState(STATE.ERROR);
                        operOutput.setStatus("Default Response: " + response.getStatus()
                           + ", " + response.getStatusInfo().toString()
                           + ", Entity='" + (entity == null ? NULL : entity) + "'");
                        break;
                     }
                  }
               }
               break;
            }
            case REPLACE: // HTTP PUT
            {
               switch (response.getStatus()) {
                  case 200: // OK
                  {
                     operOutput.setState(STATE.SUCCESS);
                     operOutput.setStatus("Replaced document");
                     break;
                  }
                  case 201: // CREATED
                  {
                     try {
                        uriCreated = this.getURIFromResponse(response);
                     } catch (Exception ex) {
                        error = true;
                        operOutput.setError(error);
                        operOutput.setState(STATE.ERROR);
                        operOutput.setStatus("Couldnot get Uid from Response: "
                           + ex.getMessage());
                     }

                     if (!STR.isEmpty(uriCreated)) {
                        jsonOutput.put(ConstantsIF.URI, uriCreated);
                        operOutput.setState(STATE.SUCCESS);
                        operOutput.setStatus("Created document");
                     }
                     break;
                  }
                  case 204: // NO CONTENT
                  {
                     operOutput.setState(STATE.SUCCESS);
                     operOutput.setStatus("Replaced document");
                     break;
                  }
                  case 400: // BAD REQUEST
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.FAILED);
                     operOutput.setStatus("BAD REQUEST: " + response.getStatus()
                        + ", " + response.getStatusInfo().toString()
                        + ", Entity='" + (entity == null ? NULL : entity) + "'");
                     break;
                  }
                  case 403: // FORBIDDEN
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.FAILED);
                     operOutput.setStatus("Forbidden replace: " + response.getStatus()
                        + ", " + response.getStatusInfo().toString()
                        + ", Entity='" + (entity == null ? NULL : entity) + "'");
                     break;
                  }
                  case 404: // NOT FOUND
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.NOTEXIST);
                     operOutput.setStatus(response.getStatusInfo().toString()
                        + ", " + uriRequest);
                     break;
                  }
                  default: {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.ERROR);
                     operOutput.setStatus("Default Response: " + response.getStatus()
                        + ", " + response.getStatusInfo().toString()
                        + ", Entity='" + (entity == null ? NULL : entity) + "'");
                     break;
                  }
               }
               break;
            }
            case DELETE: // HTTP DELETE
            {
               switch (response.getStatus()) {
                  case 200: // OK
                  case 204: // NO CONTENT
                  {
                     operOutput.setError(false);
                     operOutput.setState(STATE.SUCCESS);
                     operOutput.setStatus("Deleted document");
                     break;
                  }
                  case 400: // BAD REQUEST
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.FAILED);
                     operOutput.setStatus("BAD REQUEST: " + response.getStatus()
                        + ", " + response.getStatusInfo().toString()
                        + ", Entity='" + (entity == null ? NULL : entity) + "'");
                     break;
                  }
                  case 403: // FORBIDDEN
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.FAILED);
                     operOutput.setStatus("Forbidden delete: "
                        + response.getStatusInfo().toString());
                     break;
                  }
                  case 404: // NOT FOUND
                  {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.NOTEXIST);
                     operOutput.setStatus(response.getStatusInfo().toString()
                        + ", " + uriRequest);
                     break;
                  }
                  default: {
                     error = true;
                     operOutput.setError(error);
                     operOutput.setState(STATE.ERROR);
                     operOutput.setStatus("Default Response: " + response.getStatus()
                        + ", " + response.getStatusInfo().toString() + ", Entity='"
                        + (entity == null ? NULL : entity) + "'");
                     break;
                  }
               }
               break;
            }
            default: {
               error = true;
               operOutput.setError(error);
               operOutput.setState(STATE.FAILED);
               operOutput.setStatus("Operation type not supported: "
                  + operInput.getType().toString());
               break;
            }
         }
      }

      operOutput.setJSON(jsonOutput);

      if (_logger.isLoggable(DEBUG_LEVEL)) {
         _logger.log(DEBUG_LEVEL, "output=''{0}'', json=''{1}''",
            new Object[]{
               operOutput.toString(),
               jsonOutput == null ? NULL : jsonOutput.toString()});
      }

      _logger.exiting(CLASS, METHOD);

      return operOutput;
   }

   /**
    * Initialize class instance. Setting redirect to FALSE ClientConfig config =
    * new ClientConfig();
    * config.getProperties().put(ClientProperties.FOLLOW_REDIRECTS, true);
    * com.sun.jersey.api.client.Client client = Client.create(config);
    * client.setFollowRedirects(true);
    *
    * @throws Exception
    */
   private void init() throws Exception {
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      ClientConfig config = null;
      StringBuilder base = new StringBuilder();

      _logger.entering(CLASS, METHOD);

      config = new ClientConfig();
      config.property(ClientProperties.FOLLOW_REDIRECTS, false);

      _client = ClientBuilder.newClient(config);

      if (_haveBaseTarget) {
         base.append(this.getParamNotEmpty(PARAM_PROTOCOL))
            .append("://")
            .append(this.getParamNotEmpty(PARAM_HOST))
            .append(":")
            .append(this.getParamNotEmpty(PARAM_PORT));

         _target = _client.target(base.toString()).path(this.getParamNotEmpty(PARAM_PATH));
      }

      _parser = new JSONParser();

      this.setState(STATE.READY);
      this.setStatus("Initialization complete");

      _logger.exiting(CLASS, METHOD);

      return;
   }

//   /**
//    * Get unique identifier from HTTP Location header in response
//    *
//    * @param response HTTP Response
//    * @return String unique identifier
//    * @throws Exception
//    */
//   private String getUidFromResponse(Response response) throws Exception {
//      Object obj = null;
//      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
//      String uid = null;
//      String location = null;
//      String[] path = null;
//      MultivaluedMap<String, Object> headers = null;
//      URL url = null;
//
//      _logger.entering(CLASS, METHOD);
//
//      headers = response.getHeaders();
//
//      obj = headers.getFirst(ConstantsIF.LOCATION);
//
//      if (obj != null && obj instanceof String && !STR.isEmpty((String) obj)) {
//         location = (String) obj;
//
//         url = new URL(location);
//
//         path = url.getPath().split("/");
//
//         uid = path[path.length - 1];
//      }
//
//      _logger.exiting(CLASS, METHOD);
//
//      return uid;
//   }
   /**
    * Get URI from HTTP Location header in response
    *
    * @param response HTTP Response
    * @return String unique identifier
    * @throws Exception
    */
   private String getURIFromResponse(Response response) throws Exception {
      Object obj = null;
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      String uri = null;
      String location = null;
      String[] path = null;
      MultivaluedMap<String, Object> headers = null;
      URL url = null;

      _logger.entering(CLASS, METHOD);

      headers = response.getHeaders();

      obj = headers.getFirst(ConstantsIF.LOCATION);

      if (obj != null && obj instanceof String && !STR.isEmpty((String) obj)) {
         uri = (String) obj;
      }

      _logger.exiting(CLASS, METHOD);

      return uri;
   }

   /**
    * Convert JSON formatted string to a JSON object
    *
    * @param entity string representing JSON data
    * @return JSONObject
    * @throws Exception
    */
   private JSONObject parseEntity(final String entity) throws Exception {
      Object obj = null;
      String METHOD = Thread.currentThread().getStackTrace()[1].getMethodName();
      JSONObject jsonOutput = null;

      _logger.entering(CLASS, METHOD);

      if (!STR.isEmpty(entity)) {
         obj = _parser.parse(entity);
         if (obj != null) {
            if (obj instanceof JSONObject) {
               jsonOutput = (JSONObject) obj;
            } else if (obj instanceof JSONArray) {
               jsonOutput = new JSONObject();
               jsonOutput.put(ConstantsIF.RESULTS, (JSONArray) obj);
            } else {
               throw new Exception("Parsed entity has an undefined class: "
                  + obj.getClass().getName());
            }
         } else {
            throw new Exception("Parsed entity is null");
         }
      } else {
         throw new Exception("Entity string is empty");
      }

      _logger.exiting(CLASS, METHOD);

      return jsonOutput;
   }
}
