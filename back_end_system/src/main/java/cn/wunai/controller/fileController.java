package cn.wunai.controller;

import cn.wunai.service.WebTtsWs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.Objects;

import static cn.wunai.service.WebTtsWs.ReturnString;

@Controller
@ResponseBody
public class fileController {

    @Resource
    WebTtsWs webTtsWs;

    @PostMapping("/getMp3")
    public String getMp3(String file,String vcn) throws Exception {
        System.out.println(file);
        System.out.println(vcn);
        webTtsWs.get(file, vcn);
        while (!Objects.equals(ReturnString, "finally")) {
            Thread.sleep(100);
        }
        return ReturnString; //new RestBean(200,OUTPUT_FILE_PATH + "_ok.mp3");
    }

}
