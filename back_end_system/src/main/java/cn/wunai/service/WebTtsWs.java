package cn.wunai.service;


import cn.wunai.utils.CreateSaveFile;
import cn.wunai.utils.MP3Merge;
import cn.wunai.utils.UseAllpcm;
import cn.wunai.WuNaiApplication;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.wunai.utils.pcmToMp3.convertAudioFiles;

/**
 * 语音合成流式 WebAPI 接口调用示例 接口文档（必看）：https://www.xfyun.cn/doc/tts/online_tts/API.html
 * 发音人使用方式：登陆开放平台https://www.xfyun.cn/后，到控制台-我的应用-语音合成-添加试用或购买发音人，添加后即显示该发音人参数值
 * 错误码链接：https://www.xfyun.cn/document/error-code （code返回错误码时必看）
 * 小语种需要传输小语种文本、使用小语种发音人vcn、tte=unicode以及修改文本编码方式
 *
 * @author xlliu24
 */
@Service
public class WebTtsWs {
    // 地址与鉴权信息
    public static final String hostUrl = "https://tts-api.xfyun.cn/v2/tts";
    // 均到控制台-语音合成页面获取
    public static final String appid = "be4290ec";
    public static final String apiSecret = "OTRiOGYyZDM0ZjM3MGUyNjY5NDA3OGZi";
    public static final String apiKey = "df5f64aabc9e45f305ceb9346cc648ce";
    // 合成文本
    public static String TEXT = "";
    // 合成文本编码格式
    public static final String TTE = "UTF8"; // 小语种必须使用UNICODE编码作为值
    // 发音人参数。到控制台-我的应用-语音合成-添加试用或购买发音人，添加后即显示该发音人参数值，若试用未添加的发音人会报错11200
    public static String VCN = "";
    public static List<String> PCM = new ArrayList<>();
    // 合成文件名称
    public static String OUTPUT_FILE_PATH = "";
    public static String OUTPUT_FILE_PATH_MP3 = "";
    // json
    public static final Gson gson = new Gson();
    public static boolean wsCloseFlag = false;
    public static boolean CloseRun = false;
    public static String ReturnString = "";

    public void mp3(String t, String bgm) throws IOException {
        OUTPUT_FILE_PATH = WuNaiApplication.PCMFilePath + System.currentTimeMillis() + ".pcm";
        OUTPUT_FILE_PATH_MP3 = WuNaiApplication.FilePath + System.currentTimeMillis() + ".mp3";
        System.out.println(t);
        System.out.println(bgm);
        MP3Merge.Merge(t, bgm);
    }

    public void get(String p, String vcn) throws Exception {
        if (!CloseRun) {
            //检测是否创建PCM文件夹
            CreateSaveFile.CreateFile(WuNaiApplication.PCMFilePath);
            CloseRun = true;
        }
        TEXT = p;
        VCN = vcn;
        String wsUrl = getAuthUrl(hostUrl, apiKey, apiSecret).replace("https://", "wss://");
        OUTPUT_FILE_PATH = WuNaiApplication.PCMFilePath + System.currentTimeMillis() + ".pcm";
        OUTPUT_FILE_PATH_MP3 = WuNaiApplication.FilePath + System.currentTimeMillis() + ".mp3";
        OutputStream outputStream = Files.newOutputStream(Paths.get(OUTPUT_FILE_PATH));
        websocketWork(wsUrl, outputStream);
    }

    // Websocket方法
    public static void websocketWork(String wsUrl, OutputStream outputStream) {
        try {
            URI uri = new URI(wsUrl);
            WebSocketClient webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    ReturnString = "";
                    System.out.println("ws建立连接成功...");
                }

                @Override
                public void onMessage(String text) {
                    JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
                    if (myJsonParse.code != 0) {
                        System.out.println("发生错误，错误码为：" + myJsonParse.code);
                        System.out.println("本次请求的sid为：" + myJsonParse.sid);
                    }
                    if (myJsonParse.data != null) {
                        try {
                            byte[] textBase64Decode = Base64.getDecoder().decode(myJsonParse.data.audio);
                            outputStream.write(textBase64Decode);
                            outputStream.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (myJsonParse.data.status == 2) {
                            /*try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                            System.out.println("本次请求的sid==>" + myJsonParse.sid);
                            if (!Objects.equals(TEXT, "CloseRun")) {
                                PCM.add(OUTPUT_FILE_PATH);
                            }
                        }
                        // 可以关闭连接，释放资源
                        //wsCloseFlag = true;
                        try {
                            //检测是否到达末尾
                            if (Objects.equals(TEXT, "CloseRun")) {
                                //合并所有数据
                                UseAllpcm.Allpcm(PCM, OUTPUT_FILE_PATH);
                                //将数据转化为mp3格式
                                convertAudioFiles(UseAllpcm.getPcmOutPath(), OUTPUT_FILE_PATH_MP3);
                                //清空泛型变量 PCM 中的缓存
                                PCM.clear();
                                //重新初始化布尔值设置
                                CloseRun = false;
                                System.out.println("合成成功，文件保存路径为==>" + OUTPUT_FILE_PATH_MP3);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                        System.out.println("ws链接已关闭，本次请求完成..." + ReturnString);
                        ReturnString = "finally";
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("发生错误 " + e.getMessage());
                }
            };
            // 建立连接
            webSocketClient.connect();
            while (!webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                //System.out.println("正在连接...");
                Thread.sleep(100);
            }
            MyThread webSocketThread = new MyThread(webSocketClient);
            webSocketThread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 线程来发送音频与参数
    static class MyThread extends Thread {
        WebSocketClient webSocketClient;

        public MyThread(WebSocketClient webSocketClient) {
            this.webSocketClient = webSocketClient;
        }

        public void run() {
            String requestJson;//请求参数json串
            try {
                requestJson = "{\n" +
                        "  \"common\": {\n" +
                        "    \"app_id\": \"" + appid + "\"\n" +
                        "  },\n" +
                        "  \"business\": {\n" +
                        "    \"aue\": \"raw\",\n" +
                        "    \"tte\": \"" + TTE + "\",\n" +
                        "    \"ent\": \"intp65\",\n" +
                        "    \"vcn\": \"" + VCN + "\",\n" +
                        "    \"pitch\": 50,\n" +
                        "    \"speed\": 50\n" +
                        "  },\n" +
                        "  \"data\": {\n" +
                        "    \"status\": 2,\n" +
                        "    \"text\": \"" + Base64.getEncoder().encodeToString(TEXT.getBytes(StandardCharsets.UTF_8)) + "\"\n" +
                        //"    \"text\": \"" + Base64.getEncoder().encodeToString(TEXT.getBytes("UTF-16LE")) + "\"\n" +
                        "  }\n" +
                        "}";
                webSocketClient.send(requestJson);
                // 等待服务端返回完毕后关闭
                while (!wsCloseFlag) {
                    Thread.sleep(200);
                }
                webSocketClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        //System.out.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        return httpUrl.toString();
    }

    //返回的json结果拆解
    class JsonParse {
        int code;
        String sid;
        Data data;
    }

    class Data {
        int status;
        String audio;
    }
}
