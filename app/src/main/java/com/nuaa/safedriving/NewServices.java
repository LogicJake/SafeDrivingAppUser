package com.nuaa.safedriving;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

import static android.provider.Telephony.Mms.Part.CHARSET;

/**
 * Created by SCY on 2017/12/20/0020.
 */

public class NewServices {
    public static String rooturl = "http://app.logicjake.xyz:8080/driving/";
    public static final String AUTHORIZATION_HEADER = "Authorization-Driving";
    private static final String TAG = "NewServices";

    public static JSONObject login(String user_name, String user_passwd) {          //登陆请求
        JSONObject jsonObject = null;
        try {
            String path = rooturl + "account/login";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data =
                "&userName=" + URLEncoder.encode(user_name, "UTF-8") + "&password=" + URLEncoder
                    .encode(user_passwd, "UTF-8");
            urlConnection.setRequestProperty("Content-Length",
                String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                jsonObject = new JSONObject(baos.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static int ChangePass(String token, String newpass) {
        int result = 0;
        try {
            String path = rooturl + "account/modifyPassword";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&new_passwd=" + newpass;
            urlConnection.setRequestProperty("Content-Length",
                String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getInt("hr");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject signup(String user_name, String user_password) {
        JSONObject jsonObject = null;
        try {
            System.out.println(user_password);
            String path = rooturl + "account/signUp";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data =
                "&userName=" + URLEncoder.encode(user_name, "UTF-8") + "&password=" + URLEncoder
                    .encode(user_password, "UTF-8");
            urlConnection.setRequestProperty("Content-Length",
                String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            System.out.print(urlConnection.getResponseCode());
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                jsonObject =
                    new JSONObject(baos.toString());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static int collect(int rideId, int type, String token, String cdata) {
        try {
            String path = rooturl + "ride/addData";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data =
                "&rideId=" + rideId + "&type=" + type + "&data=" + cdata;
            urlConnection.setRequestProperty("Content-Length",
                String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                int res = new JSONObject(baos.toString()).getInt("hr");
                return res;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static JSONObject forgetPassd(String name) {
        JSONObject res = null;
        try {
            String path = rooturl + "index.php?_action=getForgetpasswd&user_name=" + name;
            URL url = new URL(path);
            // 获得连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                res = new JSONObject(baos.toString()).getJSONObject("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int sendCode(String mail, String token) {
        try {
            String path = rooturl + "verifyCode/sendCode";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "toMail=" + mail;
            urlConnection.setRequestProperty("Content-Length",
                String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                return new JSONObject(baos.toString()).getInt("hr");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int verifyCode(String code, String token) {
        try {
            String path = rooturl + "verifyCode/verifyCode";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&code=" + URLEncoder.encode(code, "UTF-8");
            urlConnection.setRequestProperty("Content-Length",
                String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                return new JSONObject(baos.toString()).getInt("hr");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static JSONObject postPic(String token, File file) {
        JSONObject result = null;
        int res = 0;
        String path = rooturl + "account/uploadAvatar";
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(50000);
            urlConnection.setConnectTimeout(50000);
            urlConnection.setDoInput(true); // 允许输入流
            urlConnection.setDoOutput(true); // 允许输出流
            urlConnection.setRequestMethod("POST"); // 请求方式
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            urlConnection.setRequestProperty("Charset", CHARSET); // 设置编码
            urlConnection.setRequestProperty("connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type",
                CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                    + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                    .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                res = urlConnection.getResponseCode();
                if (res == 200) {
                    InputStream input = urlConnection.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    System.out.println(sb1.toString());
                    result = new JSONObject(sb1.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int logout(String token) {
        int result = 0;
        System.out.println(token);
        try {
            String path = rooturl + "account/logout";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getInt("hr");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray getInfo(int type, long date, String token) {
        JSONArray result = null;
        try {
            String path =
                rooturl + "ride/getBusInfo?destination=" + type + "&date=" + date;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONArray("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean insertComment(String token, float rate, String suggestion, String tag) {
        try {
            String path = rooturl + "index.php?_action=postComment&token=" + token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&rate="
                + rate
                + "&suggestion="
                + URLEncoder.encode(suggestion, "UTF-8")
                + "&tag="
                + URLEncoder.encode(tag, "UTF-8");
            urlConnection.setRequestProperty("Content-Length",
                String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                int code = new JSONObject(baos.toString()).getInt("code");
                if (code == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONObject startRide(String region, String token) {
        JSONObject result = null;
        try {
            String path =
                rooturl + "ride/startRide?region=" + region;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                Log.d(TAG, "startRide: " + baos.toString());
                result = new JSONObject(baos.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void endRide(int rideId, String token) {
        JSONObject result = null;
        try {
            String path =
                rooturl + "ride/endRide?rideId=" + rideId;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                Log.d(TAG, "endRide: " + baos.toString());
                result = new JSONObject(baos.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
