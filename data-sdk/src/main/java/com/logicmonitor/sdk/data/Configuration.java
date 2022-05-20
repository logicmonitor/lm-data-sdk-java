/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Setter;

/**
 * This Class is used to configure the device with account name and Lm access credinatial(by using
 * autenticate model).
 */
@Setter
public class Configuration {

  private final String REGEX_COMPANY_NAME = "^[a-zA-Z0-9_.\\-]+$";
  private final Pattern patternCompanyName = Pattern.compile(REGEX_COMPANY_NAME);
  private final String REGEX_AUTH_ID = "^[a-zA-Z0-9]+$";
  private final Pattern PatternAuthId = Pattern.compile(REGEX_AUTH_ID);
  private static String company;
  private static String host;
  private int connectionPoolMaxsize;

  private Boolean asyncRequest;
  private static String accessId;
  private static String accessKey;
  private static String bearerToken;
  private static Configuration configuration;

  private static boolean gZip = true;

  /**
   * LM_COMPANY company name LM_ACCESS_ID Access Id for LMv1 LM_ACCESS_KEY Access key for LMv1
   * LM_BEARER_TOKEN BEARER_TOKEN
   */
  public Configuration() {
    company = System.getenv("LM_COMPANY");
    accessId = System.getenv("LM_ACCESS_ID");
    accessKey = System.getenv("LM_ACCESS_KEY");
    bearerToken = System.getenv("LM_BEARER_TOKEN");

    checkAuthentication();
  }

  /**
   * @param company
   * @param accessId
   * @param accessKey
   * @param bearerToken
   */
  public Configuration(String company, String accessId, String accessKey, String bearerToken) {
    this.company = (company != null) ? company : System.getenv("LM_COMPANY");
    this.accessId = (accessId != null) ? accessId : System.getenv("LM_ACCESS_ID");
    this.accessKey = (accessKey != null) ? accessKey : System.getenv("LM_ACCESS_KEY");
    this.bearerToken = (bearerToken != null) ? bearerToken : System.getenv("LM_BEARER_TOKEN");

    checkAuthentication();
  }

  /**
   * @param data Array of byte variable
   * @return String
   */
  public static String convertToHex(byte[] data) {
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < data.length; ++i) {
      int halfbyte = data[i] >>> 4 & 15;
      int two_halfs = 0;

      do {
        if (0 <= halfbyte && halfbyte <= 9) {
          buf.append((char) (48 + halfbyte));
        } else {
          buf.append((char) (97 + (halfbyte - 10)));
        }

        halfbyte = data[i] & 15;
      } while (two_halfs++ < 1);
    }
    return buf.toString();
  }

  /**
   * @param key
   * @param text
   * @return
   * @throws Exception
   */
  public static String encodeSHA256(String key, String text) throws Exception {
    SecretKey secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(secretKeySpec);
    byte[] digest = mac.doFinal(text.getBytes(StandardCharsets.UTF_8));
    return convertToHex(digest);
  }

  public static String setCompany() {
    company = getCompany();
    host = "https://" + company + ".logicmonitor.com/rest";
    return host;
  }

  /** @return boolean */
  public boolean checkAuthentication() {
    boolean flagBearerCheck = true;
    boolean flagLMv1Check = true;

    if (company == null || company.length() <= 0 || company.equals(" ")) {
      throw new IllegalArgumentException("Company must have your account name");
    }
    if (!isValidCompanyName(company)) {
      throw new IllegalArgumentException("Invalid Company Name");
    }
    if (accessId != null && accessKey != null) {
      flagBearerCheck = false;
    } else if (bearerToken != null) {
      flagLMv1Check = false;
    }
    if ((accessId == null || accessKey == null) && flagLMv1Check) {
      throw new IllegalArgumentException(
          "Authenticate must provide the `LM_ACCESS_ID` and `LM_ACCESS_KEY'");
    }
    if (flagLMv1Check) {
      if (!isValidAuthId(accessId)) {
        throw new IllegalArgumentException("Invalid LM_ACCESS_ID");
      }
      if (accessKey.length() <= 0 || accessKey.equals(" ")) {
        throw new IllegalArgumentException("Invalid LM_ACCESS_KEY");
      }
    }
    if (flagBearerCheck) {
      if (bearerToken == null) {
        throw new IllegalArgumentException("Authenticate must provide LM_BEARER_TOKEN");
      }
      if (bearerToken.length() <= 0 || bearerToken.equals(" ")) {
        throw new IllegalArgumentException("Invalid LM_BEARER_TOKEN");
      }
    }

    this.host = "https://" + this.company + ".logicmonitor.com/rest";
    this.connectionPoolMaxsize = Runtime.getRuntime().availableProcessors() * 5;
    return true;
  }

  /**
   * @param companyName
   * @return boolean
   */
  public boolean isValidCompanyName(String companyName) {
    return patternCompanyName.matcher(companyName).matches();
  }

  /**
   * @param authId
   * @return boolean
   */
  public boolean isValidAuthId(String authId) {
    return PatternAuthId.matcher(authId).matches();
  }

  /**
   * @param body
   * @param method
   * @param resourcePath
   * @return String
   */
  public static String getAuthToken(Object body, String method, String resourcePath) {
    String token = null;
    if (accessId != null && accessKey != null) {
      long epoch = Instant.now().toEpochMilli();
      String msg = null;
      if (body != null) {
        msg = method + epoch + body + resourcePath;
      } else {
        msg = method + epoch + resourcePath;
      }

      try {
        String signature =
            Base64.getEncoder().encodeToString(encodeSHA256(accessKey, msg).getBytes());

        token = "LMv1 " + accessId + ":" + signature + ":" + epoch;
      } catch (Exception e) {
        throw new RuntimeException("Invalid key exception while converting to HMac SHA256");
      }
    } else {
      token = "Bearer " + bearerToken;
    }
    return token;
  }

  /** @return boolean */
  public static boolean getAsyncRequest() {
    return Boolean.FALSE;
  }

  /** @return configuration */
  public static Configuration getConfiguration() {
    return configuration;
  }

  /** @return company */
  public static String getCompany() {
    return company;
  }

  /** @return gZip */
  public static boolean getgZip() {
    return gZip;
  }

  /** @param gZip */
  public static void setgZip(boolean gZip) {
    Configuration.gZip = gZip;
  }
}
