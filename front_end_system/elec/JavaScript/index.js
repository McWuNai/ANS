//导入渲染进程替身选项
const { dialog, BrowserWindow } = require('@electron/remote')
//导入Axios
const { default: axios } = require('axios')
//导入child_process
const childProcess = require('child_process');
//导入Qs 处理请求编码体问题
const qs = require('querystring')

window.onload = function () {
    let optionIndex = document.querySelector('.rolesPara-sele') //全局音色
    let optionSelect = document.querySelector('.roles-sele') //局部音色
    let optionIndex_value = 'xiaofeng' //默认音色
    //获取元素ID
    let btn = document.querySelector('.input-btn'); //获取文件路径
    let createFromFile = document.querySelector('.createFromFile-btn') //文件导入
    // let createFromText = document.querySelector('.createFromText-btn') //文字传入
    //let textContent = document.querySelector('.text-content') //文本内容
    const filePath = document.querySelector('.file-path'); //文件路径
    const fileContent = document.querySelector('.file-content'); //文件内容
    const input = document.createElement('input'); //传入
    let left = document.querySelector('.left') //左翻页
    let right = document.querySelector('.right') //右翻页
    let bgmusic = document.querySelector('.bgmusic-div') //合成混音频
    let speed = document.querySelector('.speed-div') //合成混音频
    let badwords = document.querySelector('.badwords-div') //敏感词文件

    let audioname = document.querySelector('.audio-name')
    let audiolist = document.querySelector('.list-icon')
    let audiopause = document.querySelector('.pause-icon')

    //设置路径
    let audio = new Audio()
    let TxtList = new Array(); //文本数组
    let TxtVcn = new Array(); //音色数组
    let rule = new Array(); //敏感词数组
    let SelectText = 0 //翻页计数器
    let target //音频路径
    let bgm //背景音乐路径
    let path = process.resourcesPath + "/resource/jdk/bin/java"
    let path1 = process.resourcesPath + "/resource/"
    let audiopath
    let sentence
    let audioicon = 0

    audiolist.addEventListener('click', () => {
        audiopath = ''
        dialog.showOpenDialog({
            title: '请选择要播放的音频文件',
            properties: ['openFile'],
            filters: [
                { "name": '音频', extensions: ['mp3'] }
            ]
        }).then((ret) => {
            //设置文件路径
            audioicon = 0
            audiopath = ret.filePaths[0]
            audio.src = audiopath
            let name = audio.src.substring(audio.src.lastIndexOf('/') + 1);
            showTaskSuccessTip();
            audioname.textContent = name
            audiostart.innerHTML = currentTime
            audioend.innerHTML = progress
        }).catch((err) => {
            console.log(err)
        })
    })

    audiopause.addEventListener('click', () => {
        if (audioicon == 0) {
            audio.play()
            audioicon = 1
            audiopause.src = '../icon/pause.png'
            audiopause.title = "暂停"
        } else if (audioicon == 1) {
            audio.pause()
            audioicon = 0
            audiopause.src = '../icon/play.png'
            audiopause.title = "播放"
        }

    })
    //获取mp3路径事件
    function Openmp3File(Title, MP3Path) {
        let next = false
        dialog.showOpenDialog({
            title: Title,
            properties: ['openFile'],
            filters: [
                { "name": '音频', extensions: ['mp3'] }
            ]
        }).then((ret) => {
            //设置文件路径
            MP3Path = ret.filePaths[0]
            if (MP3Path != null) {
                next = true
            }
        }).catch((err) => {
            console.log(err)
        })
        while (next = false) {
            sleep(500)
        }
    }

    //监听选择音频文件
    speed.addEventListener('click', async () => {
        Openmp3File("请选择音频文件", target)
    })
    //监听选择BGM事件
    bgmusic.addEventListener('click', async () => {
        Openmp3File("请选择背景音乐", bgm)

        //延时事件
        sleep(5000)
        let cmdStr = path + ' -jar ' + path1 + 'wuNai-0.0.1-SNAPSHOT.jar ';
        console.log(cmdStr)
        childProcess.exec(cmdStr)
        //发送请求
        await axios.post('http://localhost:8080/MergeMp3', qs.stringify({ //处理请求编码体问题
            Target: target,
            BGM: bgm
        }))
            .then(function (response) {
                console.log(response)
                //清空赋值缓存
                target = null
                bgm = null
            })
            .catch(function (error) {
                console.log(error)
            })
    })

    //监听翻页事件
    //左翻页事件
    left.addEventListener('click', () => {
        if (SelectText > -1) {
            //计数器-1
            SelectText -= 1
        }
        //覆写显示内容
        fileContent.value = TxtList[SelectText]
    })
    //右翻页事件
    right.addEventListener('click', () => {
        if (SelectText < TxtList.length) {
            //计数器+1
            SelectText += 1
        }
        //覆写显示内容
        fileContent.value = TxtList[SelectText]
    })

    //监听全局选择音色事件
    if (optionIndex) {
        optionIndex.addEventListener('change', (event) => {
            //获取下拉列表的下标
            let optionIndex_num = optionIndex.selectedIndex
            //获取下拉列表下标的value值
            optionIndex_value = optionIndex.options[optionIndex_num].value
            //检测文本是否存在
            if (TxtList.length != 0) {
                //全局音色赋值
                for (let index = 0; index < TxtList.length; index++) {
                    TxtVcn[index] = optionIndex_value
                }
                showTaskSuccessTip();
            } else {
                showTaskFaildTip();
            }
            //将value值返回到初始状态
            event.target.value = "--未选择--"
        })
    } else {
        console.log("Is null!")
        showTaskFaildTip();
    }

    //监听局部选择音色事件
    if (optionSelect) {
        optionSelect.addEventListener('change', (event) => {
            //获取下拉列表下标
            let optionSelect_num = optionSelect.selectedIndex
            //音色局部赋值
            TxtVcn[SelectText] = optionSelect.options[optionSelect_num].value
            showTaskSuccessTip();
            //将value值返回到初始状态
            event.target.value = "--未选择--"
        })
    } else {
        console.log("Is null!")
        showTaskFaildTip();
        //将value值返回到初始状态
        event.target.value = "--未选择--"
    }

    //敏感词检测
    badwords.addEventListener('click', () => {
        dialog.showOpenDialog({
            title: "请选择敏感词文件",
            properties: ['openFile'],
            filters: [
                { "name": '敏感词文件', extensions: ['txt'] }
            ]
        }).then((ret) => {
            const fs = require('fs');
            fs.readFile(ret.filePaths[0], "UTF-8", (err, data) => {
                if (err) {
                    console.error(err)
                    return
                }
                console.log(data)
                rule.length = 0
                let val = data;
                let parts = val.split(",");
                for (let index = 0; index < parts.length; index++) {
                    rule[index] = parts[index]
                }
                showErrorFileTip();
                document.querySelector('.badwordsNum-a').innerHTML = rule.length;
            })
        }).catch((err) => {
            console.log(err)
        })
    })

    //监听导入文件拖入事件
    //拖放导入文件
    const section2 = document.querySelector('.section2');
    section2.ondragover = function (event) {
        event.preventDefault();
    };
    section2.ondrop = function (event) {

        //清空缓存
        TxtList.length = 0
        TxtVcn.length = 0
        SelectText = 0

        event.preventDefault();
        const file = event.dataTransfer.files[0];
        if (file.type.toLocaleLowerCase() !== "text/plain") {
            showErrorFileTip();
            return;
        }
        const reader = new FileReader();
        reader.onload = function (e) {
            const content = e.target.result;

            // 文字分割
            let font = /“(.*)”/g;
            let parts = content.split(font);
            for (let index = 0; index < parts.length; index++) {
                TxtList[index] = parts[index]
                console.log(TxtList[index])
                for (let i = 0; i < rule.length; i++) {
                    var regex = new RegExp(rule[i], "gi");
                    TxtList[index] = TxtList[index].replace(regex, "*".repeat(rule[i].length));
                }
            }

            // 显示文件名
            filePath.value = file.name;

            // 显示文件内容
            fileContent.value = TxtList[0];

            TxtList[parts.length] = "CloseRun";
            fileContent.value = TxtList[SelectText];
            showTaskSuccessTip()
        };
        reader.readAsText(file);
    };

    //监听导入文件事件
    btn.addEventListener('click', () => {
        TxtList.length = 0
        TxtVcn.length = 0
        SelectText = 0

        input.type = 'file';
        input.accept = '.txt';
        input.onchange = event => {
            const file = event.target.files[0];
            const reader = new FileReader();
            if (file.type.toLocaleLowerCase() !== "text/plain") {
                showErrorFileTip();
                return;
            }
            //读取文件
            reader.readAsText(file, 'utf-8');
            reader.onload = function (e) {
                sentence = e.target.result;
                let font = /“(.*)”/g;
                let parts = sentence.split(font);
                for (let index = 0; index < parts.length; index++) {
                    TxtList[index] = parts[index]
                    console.log(TxtList[index])
                    for (let i = 0; i < rule.length; i++) {
                        var regex = new RegExp(rule[i], "gi");
                        TxtList[index] = TxtList[index].replace(regex, "*".repeat(rule[i].length));
                    }
                }
                TxtList[parts.length] = "CloseRun";
                filePath.value = file.name;
                fileContent.value = TxtList[SelectText];
                showTaskSuccessTip()
            };
        }
        input.click();
    })

    //监听交互事件
    createFromFile.addEventListener('click', async () => {
        let all = true
        if (TxtList.length != 0 && TxtVcn.length == 0) {
            for (let index = 0; index < TxtList.length; index++) {
                TxtVcn[index] = optionIndex_value
            }
        }
        for (let index = 0; index < TxtVcn.length; index++) {
            if (TxtVcn[index] != optionIndex_value && all != false) {
                all = false
            }
        }

        //启动服务请求
        let cmdStr = path + ' -jar ' + path1 + 'wuNai-0.0.1-SNAPSHOT.jar ';
        childProcess.exec(cmdStr)
        console.log(cmdStr)
        sleep(4000)
        //开启请求并交互数据
        alert('已开启服务, 请稍耐心等待结果... ...' + all)
        console.log(all)
        if (all == true) {
            let target
            for (let index = 0; index < TxtList.length - 1; index++) {
                target += TxtList[index]
            }
            TxtList.length = 2
            TxtList[0] = target
            TxtList[1] = "CloseRun"
            TxtVcn.length = 2
            TxtVcn[0] = optionIndex_value
            TxtVcn[1] = optionIndex_value
        }
        for (let index = 0; index < TxtList.length; index++) {
            await axios.post('http://127.0.0.1:8080/getMp3', qs.stringify({ //处理请求编码体问题
                file: TxtList[index],
                vcn: TxtVcn[index]
            }))
                .then(function (response) {
                    console.log(response)
                    if (TxtList[index] == TxtList.length - 1) {
                        alert('已完成,请移步至Out文件夹查看结果')
                    }
                })
                .catch(function (error) {
                    console.log(error)
                    if (TxtList[index] == TxtList.length - 1) {
                        showErrorFileTip()
                    }
                })
        }
        TxtList.length = 0
        TxtVcn.length = 0
        SelectText = 0
        childProcess.exec("ps -ef | grep java | grep -v grep | awk '{print $2}' | xargs kill -9")
    })

    //延迟事件
    function sleep(numberMillis) {
        var now = new Date();
        var exitTime = now.getTime() + numberMillis;
        while (true) {
            now = new Date();
            if (now.getTime() > exitTime)
                return;
        }
    }
}