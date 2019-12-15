package com.jaimovie.acube.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaimovie.acube.app.ACubeApplication;
import com.jaimovie.acube.constants.AppConfig;
import com.jaimovie.acube.model.IResult;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;
import static com.jaimovie.acube.constants.AppConfig.APP_ID;
import static com.jaimovie.acube.constants.AppConstant.KEY_ACCESS_TOKEN;
import static com.jaimovie.acube.constants.AppConstant.KEY_APP_ID;
import static com.jaimovie.acube.constants.AppConstant.KEY_CONTENT_TYPE;
import static com.jaimovie.acube.constants.AppConstant.VALUE_CONTENT_TYPE;
import static com.jaimovie.acube.constants.ErrorMsg.ERROR_WRONG;
import static com.jaimovie.acube.constants.ErrorMsg.INTERNET_CONNECTION;
import static com.jaimovie.acube.constants.ErrorMsg.SOMETHING_WENT_WORNG;
import static com.jaimovie.acube.constants.ErrorMsg.TIMEOUT_ERROR_WRONG;
import static com.jaimovie.acube.constants.PreferenceName.PREFS_TOKEN;
import static com.jaimovie.acube.utils.Utility.dismissProgDialog;

public class VolleyService {

    private IResult mResultCallback = null;
    private Context mContext;
    private static final String TAG = VolleyService.class.getSimpleName();

    public VolleyService(IResult resultCallback, Context context) {
        mResultCallback = resultCallback;
        mContext = context;
    }

    public void postDataVolley(final String requestType, String url, final Map<String, String> params) {
        Log.e(TAG, "--->" + params);
        JSONObject parameters = new JSONObject(params);
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObj = new JsonObjectRequest(POST, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "--response-->" + response);
                    if (mResultCallback != null)
                        mResultCallback.notifyJsonSuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "--error-->" + error);
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Log.e(TAG, "--->" + params);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(KEY_APP_ID, APP_ID);
                    headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                    return headers;
                }
            };
            queue.add(jsonObj);
        } catch (Exception e) {
            Log.e(TAG, "--POST-->" + e.toString());
        }
    }

    public void getLogout(final String requestType, String url) {

        final String accessToken = UniversalPreferences.getInstance().get(PREFS_TOKEN, "");
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.DELETE, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Log.e(TAG, "--response---" + response);
                    if (mResultCallback != null)
                        mResultCallback.notifyJsonSuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                        } else if (error instanceof NoConnectionError) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                        }
                    } else {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, ERROR_WRONG);
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(KEY_APP_ID, APP_ID);
                    headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                    headers.put(KEY_ACCESS_TOKEN, accessToken);
                    return headers;
                }
            };
            queue.add(jsonObj);
        } catch (Exception e) {
            Log.e(TAG, "---->" + e.toString());
        }
    }


    public void deleteVolleyData(final String requestType, String url, final String serviceName) {
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.DELETE, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifyJsonSuccess(requestType, response, serviceName);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                        } else if (error instanceof NoConnectionError) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                        }
                    } else {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, ERROR_WRONG);
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(KEY_APP_ID, APP_ID);
                    headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                    return headers;
                }
            };
            queue.add(jsonObj);
        } catch (Exception e) {
            Log.e(TAG, "---->" + e.toString());
        }
    }


    public void getDataVolley(String url, final String type) {
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifyJsonSuccess(type, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError("--", TIMEOUT_ERROR_WRONG);
                        } else if (error instanceof NoConnectionError) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError("--", INTERNET_CONNECTION);
                        }
                    } else {
                        if (mResultCallback != null)
                            mResultCallback.notifyError("---", ERROR_WRONG);
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(KEY_APP_ID, APP_ID);
                    headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                    return headers;
                }
            };
            queue.add(jsonObj);
        } catch (Exception e) {

            Log.e(TAG, "=====>" + e.toString());
        }
    }


    public void postVolleyData(final String requestType, String url, final Map<String, String> params) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (mResultCallback != null)
                    mResultCallback.notifyJsonSuccess(requestType, response, "");
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                    } else if (error instanceof NoConnectionError) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                    }
                } else {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, ERROR_WRONG);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(KEY_APP_ID, APP_ID);
                headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                return headers;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(AppConfig.SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        ACubeApplication.getInstance().addToRequestQueue(stringRequest);
    }


    public void postVolleyDataWithAccessToken(final String requestType, String url, final Map<String, String> params, final String type) {
        final String token = UniversalPreferences.getInstance().get(PREFS_TOKEN, "");
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (mResultCallback != null)
                    mResultCallback.notifyJsonSuccess(requestType, response, type);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                    } else if (error instanceof NoConnectionError) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                    }
                } else {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, ERROR_WRONG);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e(TAG, params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(KEY_APP_ID, APP_ID);
                headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                headers.put(KEY_ACCESS_TOKEN, token);
                Log.e(TAG, "---" +headers.toString());
                return headers;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(AppConfig.SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        ACubeApplication.getInstance().addToRequestQueue(stringRequest);
    }


    public void postVolleyData(final String requestType, String url, final Map<String, String> params, final String type) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e(TAG, "--Response-->" + response);
                if (mResultCallback != null)
                    mResultCallback.notifyJsonSuccess(requestType, response, type);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                    } else if (error instanceof NoConnectionError) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                    }
                } else {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, ERROR_WRONG);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(KEY_APP_ID, APP_ID);
                headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                return headers;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(AppConfig.SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        ACubeApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public void postDataVolleyStringRequest(final String requestType, String url, final Map<String, String> params, final String type) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (mResultCallback != null)
                    mResultCallback.notifyJsonSuccess(requestType, response, type);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                    } else if (error instanceof NoConnectionError) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                    }
                } else {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, ERROR_WRONG);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e(TAG, "--Params--" + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(KEY_APP_ID, APP_ID);
                headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                return headers;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(AppConfig.SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        ACubeApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public void postDataVolleyWithAccess(final String requestType, String url, final Map<String, String> params) {
        final String accessToken = UniversalPreferences.getInstance().get(PREFS_TOKEN, "");
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "--Response-->" + response);
                if (mResultCallback != null)
                    mResultCallback.notifyJsonSuccess(requestType, response, "");
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgDialog();
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                    } else if (error instanceof NoConnectionError) {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                    } else {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, SOMETHING_WENT_WORNG);
                    }
                } else {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, ERROR_WRONG);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put(KEY_APP_ID, APP_ID);
                headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                headers.put(KEY_ACCESS_TOKEN, accessToken);
                return headers;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(AppConfig.SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        ACubeApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public void getVolleyWithAccessToken(final String requestType, String url, String serviceType) {
        final String accessToken = UniversalPreferences.getInstance().get(PREFS_TOKEN, "");
        Log.e(TAG, "- DATA");
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifyJsonSuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                        } else if (error instanceof NoConnectionError) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                        }
                    } else {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, ERROR_WRONG);
                    }
                }
            }) {
                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(KEY_APP_ID, APP_ID);
                    headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                    headers.put(KEY_ACCESS_TOKEN, accessToken);
                    return headers;
                }
            };
            queue.add(jsonObj);
        } catch (Exception e) {
            Log.e(TAG, "---->" + e.toString());
        }
    }

    public void getDependentDeleteVolley(final String requestType, String url) {
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.DELETE, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifyJsonSuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                        } else if (error instanceof NoConnectionError) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                        }
                    } else {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, ERROR_WRONG);
                    }
                }
            }) {
                /**
                 * Passing some request headers
                 *
                 */

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(KEY_APP_ID, APP_ID);
                    headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                    return headers;
                }
            };
            queue.add(jsonObj);
        } catch (Exception e) {
            Log.e(TAG, "=====>" + e.toString());
        }
    }

    public void putResultDataVolley(final String requestType, String url) {

        final String accessToken = UniversalPreferences.getInstance().get(PREFS_TOKEN, "");
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest putrequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifyJsonSuccess(requestType, response);

                    Log.e("Requsest type=======>", "" + requestType);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, TIMEOUT_ERROR_WRONG);
                        } else if (error instanceof NoConnectionError) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, INTERNET_CONNECTION);
                        }
                    } else {
                        if (mResultCallback != null)
                            mResultCallback.notifyError(requestType, ERROR_WRONG);
                    }
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(KEY_APP_ID, APP_ID);
                    headers.put(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE);
                    headers.put(KEY_ACCESS_TOKEN, accessToken);
                    return headers;
                }
            };

            queue.add(putrequest);
        } catch (Exception e) {
            Log.e(TAG, "=====PUT=====>" + e.toString());
        }
    }
}