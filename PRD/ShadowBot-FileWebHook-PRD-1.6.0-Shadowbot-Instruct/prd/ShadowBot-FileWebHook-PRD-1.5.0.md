需求背景: 
如果想让我们的filewebhook正常工作,用户必须在他的影刀应用里面,使用我们编写的FileWebHook-App-Framework 框架指令
这个框架的作用就是自动处理request.json文件的读取, 以及在用户的业务指令流程执行完毕之后,自动发送http响应filewebhook
并且呢FileWebHook-App-Framework这个框架的指令内某些属性值它不是固定的,  比如说filewebhook的ip和端口 以及filewebhook将参数写入写到了哪个文件里面.以及当前的triggerId
就目前我们的设计来看, 用户每次新增1个filewebhook触发器, 那么就要新增1套FileWebHook-App-Framework框架指令,并且上述提到的3个参数可能不一样, 因为用户会修改触发器信息,比如触发器写入的request.json文件路径 和 配置信息,比如端口
并且如果我们全部让用户来自己编写或者说去控制 FileWebHook-App-Framework 框架指令的话很麻烦, 会大大提示用户的使用门槛 
因此我们必须承诺和做到用户可以直接无脑复制每一个具有独特属性的FileWebHook-App-Framework 框架指令
那么如何保证我们的承诺呢? 我们就需要用到影刀指令转储/序列化技术(说白了就是把我们的FileWebHook-App-Framework 框架指令给存储为1和无文件格式的纯文件)
和 动态修改影刀指令参数(就是去根据当前的triggerId和当前软件配置去动态修改这个指令文件)  
以及 影刀指令反序列化(就是让用户点一下filewebhook的按钮,我们就能把当前触发器对应的FileWebHook-App-Framework 框架指令,给写入到用户的系统粘贴板里面. 用户就可以无脑复制了)
现在你应该明白了整个需求的背景了  接下来我来说下需求

需求标题:
使用python+kotlin结合 重构这部分功能 "影刀指令的跨机器传递 和 影刀指令动态修改以及复制" 

用户需求:
实际上我们的项目以已经有这个功能了
"影刀指令的跨机器传递 和 影刀指令动态修改以及复制"
目前这个功能是kotlin代码实现的 并且由于kotlin不如python写脚本厉害 对于这种复杂的自定义格式的剪切文件,做不到完美流畅的处理
相关代码如下:
composeApp/src/commonMain/kotlin/domain/clipboard/ClipboardManager.kt
composeApp/src/commonMain/kotlin/ui/screens/developer
composeApp/src/desktopMain/kotlin/domain/clipboard/ClipboardManager.kt
因此现在我决定让你使用python+kotlin结合重构这部分功能:
关于指令的文件转储  转储文件动态修改  以及最终读取修改后的指令文件到系统剪切板 你需要使用python实现,相关代码必须严格参考
以下python脚本的参考,务必全部读取并理解python脚本逻辑 composeApp/src/commonMain/kotlin/shadowbot
关于ui和控制逻辑部分 你依然是kotlin去实现

需求实现计划 和 业务逻辑:
首先根据上面的分析 我们其实就是做 7 件事情

1.找到可用的python解释器
我们优先使用用户的系统里面的影刀软件的python解释器 , 在项目启动时 动态从用户的系统里面找到影刀的python解释器,一般是这个路径C:\Program Files (x86)\ShadowBot\shadowbot-5.32.44\python310 ,其中 C:\Program Files (x86) 和 5.32.44 和310都有可能变化
(这个动作发生在项目启动成功之前,因为后续的python脚本调用都需要用到影刀的python解释器)
并且 这个路径是可以在配置页面由用户自己更改的 , 用户可以指定任意的python解释器路径

2.移除kotlin实现

3.使用python来写指令转储代码核心
(这个逻辑发生在当我点了"开发者功能-转储按钮"这个按钮之后,就读取系统剪切板内容 然后将剪切板内容也就是元FileWebHook-App-Framework 框架指令转储到这个相当路径composeApp/src/commonMain/kotlin/shadowbot/ 文件名字叫 BaseFileWebHookAppFramework)

4.1.使用python来写动态修改指令代码,参考 composeApp/src/commonMain/kotlin/shadowbot
4.2.使用python来写将修改后的指令写入C:\Users\Administrator\.filewebhook\{triggerId}\ShadowbotAppFormwork_InstructData文件内 参考 composeApp/src/commonMain/kotlin/shadowbot
4.3.使用python来写读取C:\Users\Administrator\.filewebhook\triggerFiles\{triggerId}\ShadowbotAppFormwork_InstructData文件 到系统剪切板 参考 composeApp/src/commonMain/kotlin/shadowbot
(
在触发器管理页面的每一行的触发器元素的复制按钮 "复制FileWebHook-App-Framework"
当用户点击了这个按钮之后 你需要先收集这4个信息  1当前的triggerId 2.软件配置的端口httpPort 3.ip直接使用127.0.0.1 4.还有软件的触发器文件路径配置triggerFilesPath
然后你需要去读取刚才我们转储下来的那个元FileWebHook-App-Framework 框架指令文件 BaseFileWebHookAppFramework
直接去找到"属性变量"这个类型的指令中的triggerId triggerFilesPath serverIpAndPort 然后对其写入值
值写入完成之后,将这个专属于此trigger的FileWebHook-App-Framework 框架指令文件 存储到 C:\Users\Administrator\.filewebhook\{triggerId}\ShadowbotAppFormwork_InstructData文件内
)

5.使用kotlin将以上逻辑给串起来

另外你要注意的时  你不可能刚开始就知道 元FileWebHook-App-Framework 框架指令文件到底是什么内容, 不用担心你直接读取composeApp/src/commonMain/kotlin/shadowbot/BaseFileWebHookAppFramework 这个文件进行了解即可

另外最最重要的是 你在做上面的所有事情之前  必须先生成准确和详细的执行计划md文件 写入到composeApp/src/commonMain/kotlin/shadowbot 文件夹下
然后再一步步执行
