package com.example.lenovo.murphysl.face;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.lenovo.murphysl.model.UserModel;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * FacePPDecet
 *
 * @author: lenovo
 * @time: 2016/5/25 18:54
 */

public class FacePPDecet {

    private static final String TAG = "FacePPDecet";

    public interface CallBack{
        void success(JSONObject result);

        void error(FaceppParseException e);
    }

    /**
     * 根据Bitmap识别人脸
     * @param bm
     * @param callBack
     */
    public static void decet(final Bitmap bm, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);
                    Bitmap bmSmall = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bmSmall.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] arrays = stream.toByteArray();

                    PostParameters parameters = new PostParameters();
                    parameters.setImg(arrays);
                    JSONObject jsonObject = requests.detectionDetect(parameters);

                    if (callBack != null) {
                        callBack.success(jsonObject);
                    }

                } catch (FaceppParseException e) {
                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.error(e);
                    }
                }

            }
        }).start();

    }

    /**
     * 根据URL识别人脸
     *
     * @param url
     * @param callBack
     */
    public static void decet(final String url, final CallBack callBack) {
        Log.i(TAG, "decet: " + url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);

                    JSONObject jsonObject = requests.detectionDetect(new PostParameters().setUrl(url));

                    if (callBack != null) {
                        callBack.success(jsonObject);
                    }

                } catch (FaceppParseException e) {
                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.error(e);
                    }
                }

            }
        }).start();

    }

    public static void compare(final Bitmap bm1, final Bitmap bm2, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);
                String face1 = null, face2 = null;

                try {
                    Bitmap bmF = Bitmap.createBitmap(bm1, 0, 0, bm1.getWidth(), bm1.getHeight());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmF.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] arrays = stream.toByteArray();
                    JSONObject result1 = null;
                    result1 = requests.detectionDetect(new PostParameters().setImg(arrays));
                    face1 = result1.getJSONArray("face").getJSONObject(0).getString("face_id");
                    Log.i("face", "face1的id=" + face1);
                } catch (FaceppParseException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                try {
                    Bitmap bmF = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(), bm2.getHeight());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmF.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] arrays = stream.toByteArray();
                    JSONObject result2 = requests.detectionDetect(new PostParameters().setImg(arrays));
                    face2 = result2.getJSONArray("face").getJSONObject(0).getString("face_id");
                    Log.i("face", "face2的id=" + face2);
                } catch (FaceppParseException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                try {
                    JSONObject Compare = requests.recognitionCompare(new PostParameters().setFaceId1(face1).setFaceId2(face2));
                    Double smilar = Double.valueOf(Compare.getString("similarity"));
                    Log.i("face", "相似度 : " + smilar);
                } catch (FaceppParseException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 识别组中已存在的人脸
     *
     * @param bm
     * @param callBack
     */
    public static void identify(final Bitmap bm , final CallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);
                    Bitmap bmF = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmF.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] arrays = stream.toByteArray();
                    JSONObject jsonObject = requests.recognitionIdentify(new PostParameters()
                            .setGroupName("user").setImg(arrays));

                    if (callBack != null) {
                        callBack.success(jsonObject);
                    }

                } catch (FaceppParseException e) {
                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.error(e);
                    }
                }
            }
        }).start();
    }

    public static void identify(final String url , final String face, final CallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);
                    JSONObject jsonObject = requests.recognitionIdentify(new PostParameters()
                            .setGroupName("user")
                            .setUrl(url)
                            .setFaceId(face));
                    if (callBack != null) {
                        callBack.success(jsonObject);
                    }
                } catch (FaceppParseException e) {
                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.error(e);
                    }
                }
            }
        }).start();
    }

    public static void createPerson(final JSONObject rs, final String name, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);
                    String faceID = rs.getJSONArray("face").getJSONObject(0).getString("face_id");
                    Log.i(TAG, "faceID" + faceID);
                    Log.i(TAG, "name" + name);
                    // -----------------Person-----------------
                    // person/create
                    JSONObject jsonObject = requests.personCreate(new PostParameters()
                            .setPersonName(name)
                            .setFaceId(faceID)
                            .setGroupName("user"));

                    /*// person/add_face
                    requests.personAddFace(new PostParameters()
                            .setPersonName(name)
                            .setFaceId(faceID));

                    // -----------------Group-----------------
                    // group/create
                    if(!"user".equals(requests.groupGetInfo().getString("group_name"))){
                        System.out.println(requests.groupCreate(new PostParameters().setGroupName("user")));
                    }
                    PostParameters parameters1 = new PostParameters();
                    parameters1.setGroupName("user").setPersonName(jsonObject.getString("person_name"));
                    requests.groupAddPerson(parameters1);*/

                    if (callBack != null) {
                        callBack.success(jsonObject);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("tag" , "warning");

                    if (callBack != null) {
                        callBack.error((FaceppParseException) e);
                    }
                }

            }
        }).start();

    }

    public static void train(final String name , final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // recognition/train
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);
                    JSONObject syncRet = requests.trainIdentify(new PostParameters()
                            .setGroupName("user")
                            .setPersonName(name));
                    Log.i("session", String.valueOf(requests.getSessionSync(syncRet.getString("session_id"))));

                    if(callBack != null){
                        callBack.success(syncRet);
                    }

                    //syncRet = requests.trainVerify(new PostParameters().setPersonName(name));
                    //Log.i("session", String.valueOf(requests.getSessionSync(syncRet.get("session_id").toString())));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FaceppParseException e) {
                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.error(e);
                    }
                }
            }
        }).start();
    }

    public static void addFace(final String name, final JSONObject rs, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);
                    String faceID = rs.getJSONObject("face").getString("face_id");

                    // person/add_face
                    JSONObject jsonObject = requests.personAddFace(new PostParameters()
                            .setPersonName(name)
                            .setFaceId(faceID));

                    if (callBack != null) {
                        callBack.success(jsonObject);
                    }

                } catch (FaceppParseException e) {
                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.error(e);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
