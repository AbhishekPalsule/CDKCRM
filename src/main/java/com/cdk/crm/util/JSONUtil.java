package com.cdk.crm.util;

import com.cdk.crm.dto.ResponseData;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by palsulea on 9/3/2016.
 */
public class JSONUtil {

    public static JSONObject getJsonObject(JSONArray jsonArray, int index) {
        Object dataObject = jsonArray.get(index);
        return (JSONObject)dataObject;
    }

    public static JSONArray getJSONArray(String jsonLeads) throws ParseException {
        JSONParser parser = new JSONParser();
        System.out.println(jsonLeads);
        return (JSONArray)parser.parse(jsonLeads);
    }
    public static String getJSONConvertedData(List<ResponseData> responseList) {

        // System.out.println(responseList.size());
        String jsonString = "[";
        for (ResponseData responseData : responseList) {
            jsonString += new Gson().toJson(responseData) + ",";
        }
        jsonString = jsonString.substring(0, jsonString.length() - 1);
        jsonString += "]";
        System.out.println(jsonString);

        // System.out.println("Sending for mail");
        return jsonString;
    }


}
