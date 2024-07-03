const { app, BrowserWindow } = require('electron')

function CreateWindow() {
    let main = new BrowserWindow({
        width: 800,
        height: 480,
        show: false, //防止白屏
        minWidth: 800, //尺寸控制
        minHeight: 480,
        maxWidth: 800,
        maxHeight: 480,
        title: "测试软件", //标题设置
        icon: 'image/addd.ico', //自定义图标
        frame: true, //用于自定义menu
        autoHideMenuBar: true, //隐藏menu菜单选项
        webPreferences: {
            nodeIntegration: true, //允许渲染进程调用主进程的方法， 俗称展示多窗口
            contextIsolation: false, //配合上述使用
            webviewTag: true,
            webSecurity: false
        }
        //transparent: true
    })
    require('@electron/remote/main').initialize() //引入替身
    require("@electron/remote/main").enable(main.webContents) //引入webContents
    main.loadFile('Html/index.html')
    main.webContents.openDevTools(); //打开webContents测试工具
    main.on('ready-to-show', () => {
        main.show()
    })

}
app.on('ready', CreateWindow)
app.on('window-all-closed', () => {
    app.quit()
})