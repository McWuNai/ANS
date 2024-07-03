package cn.wunai.controller;

import cn.wunai.service.WebTtsWs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.util.Objects;
import static cn.wunai.utils.MP3Merge.getUrl;

@Controller
@ResponseBody
public class MergeMP3 {
    @Resource
    WebTtsWs webTtsWs;

    @PostMapping("/MergeMp3")
    public String getMp3(String Target,String BGM) throws Exception {
        String target = Target.replaceAll("\\\\", File.separator);
        String bgm = BGM.replaceAll("\\\\", File.separator);
        System.out.println(target);
        System.out.println(bgm);

        webTtsWs.mp3(target, bgm);
        while (Objects.equals(getUrl(), "Null")) {
            Thread.sleep(100);
        }
        return getUrl();
    }

}
