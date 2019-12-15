package com.jaimovie.acube.model;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface IResult {
    public void notifyJsonSuccess(String requestType, JSONObject response);

    public void notifyError(String requestType, VolleyError error);

    public void notifyJsonSuccess(String requestType, String response, String type);

    public void notifyJsonSuccess(String requestType, JSONObject response, String serviceName);

    public void notifyError(String requestType, String response);
}