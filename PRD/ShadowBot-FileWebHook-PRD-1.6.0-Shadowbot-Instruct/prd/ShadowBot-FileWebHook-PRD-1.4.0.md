现在继续对我们的项目增加1个较为复杂的功能 
"实现影刀指令的跨平台/跨机器/跨用户的传递, 以及影刀指令动态修改/生成"

我先跟你说下背景,如果想让我们的filewebhook正常工作,用户必须在他的影刀应用里面,使用我们编写的FileWebHook-App-Framework 框架指令
这个框架的作用就是自动处理request.json文件的读取, 以及在用户的业务指令流程执行完毕之后,自动发送http响应filewebhook
并且呢FileWebHook-App-Framework这个框架的指令内某些属性值它不是固定的,  比如说filewebhook的ip和端口 以及filewebhook将参数写入写到了哪个文件里面.以及当前的triggerId

就目前我们的设计来看, 用户每次新增1个filewebhook触发器, 那么就要新增1套FileWebHook-App-Framework框架指令,并且上述提到的3个参数可能不一样, 因为用户会修改触发器信息,比如触发器写入的request.json文件路径 和 配置信息,比如端口

并且如果我们全部让用户来自己编写或者说去控制 FileWebHook-App-Framework 框架指令的话很麻烦, 会大大提示用户的使用门槛 

因此我们必须承诺和做到用户可以直接无脑复制每一个具有独特属性的FileWebHook-App-Framework 框架指令

那么如何保证我们的承诺呢? 我们就需要用到影刀指令转储/序列化技术(说白了就是把我们的FileWebHook-App-Framework 框架指令给存储为1和无文件格式的纯文件)
和 动态修改影刀指令参数(就是去根据当前的triggerId和当前软件配置去动态修改这个指令文件)  
以及 影刀指令反序列化(就是让用户点一下filewebhook的按钮,我们就能把当前触发器对应的FileWebHook-App-Framework 框架指令,给写入到用户的系统粘贴板里面. 用户就可以无脑复制了)

现在你应该明白了整个需求的背景了  接下来我来说实现步骤 和 我的想法

首先根据上面的分析 我们其实就是做3件事情 1.指令转储  2.动态修改指令 3.将指令写入系统粘贴板
这三个事情呢, 有一些是我们开发者来触发 比如元指令转储这件事  而第二和第三件事情则是用户在触发器管理页面,对某一行的触发器点击"复制"FileWebHook-App-Framework指令"时才触发动态修改指令 和 将指令写入系统粘贴板

那么我首先来说第一件事情  你怎么实现
1.1 定义1个新的前端菜单页面 "开发者功能"
这个页面目前就只有1个指令转储按钮,当我点了这个按钮之后,你用kotlin代码去实现读取系统剪切板 然后将元FileWebHook-App-Framework 框架指令转储到这个相当路径composeApp/src/commonMain/kotlin/shadowbot/ 文件名字叫 BaseFileWebHookAppFramework
至于说kotlin脚本怎么编写 你不用自己实现 而是抄这个python脚本  composeApp/src/commonMain/kotlin/shadowbot/ShadowbotAppFormwork_InstructDataCopy.py
这个脚本已经可以做到能够将复杂的影刀自定义剪切板格式 给写入到文件内, 你必须保证kotlin的功能 和 这个python脚本一模一样  不能丢失功能
当转储成功后 记得弹窗通知成功

然后现在我继续说第二件事情 和 第三件事情怎么实现
2.1 首先在触发器管理页面  给每一行的触发器元素新增1个按钮 "复制FileWebHook-App-Framework"
当用户点击了这个按钮之后 你需要先收集这4个信息  1当前的triggerId 2.软件配置的端口httpPort 3.ip直接使用127.0.0.1 4.还有软件的触发器文件路径配置triggerFilesPath
然后你需要去读取刚才我们转储下来的那个元FileWebHook-App-Framework 框架指令文件 BaseFileWebHookAppFramework
直接去替换${triggerId}  ${triggerFilesPath}  ${serverIpAndPort}
替换完成之后,将这个专属于此trigger的FileWebHook-App-Framework 框架指令文件, 给写入到用户的粘贴板内即可
至于你如何编写kotlin代码将指令写入到系统粘贴板  你必须参考这个python脚本composeApp/src/commonMain/kotlin/shadowbot/ShadowbotAppFormwork_InstructDataPaste.py  特别要注意的是关于format_name format_id的处理
因为影刀指令在写入粘贴板时属于自定义粘贴板格式  那么对于自定义粘贴板格式  不同的windows机器会分配不同的formatId

另外你要注意的时  你不可能刚开始就知道 元FileWebHook-App-Framework 框架指令文件到底是什么内容, 不用担心你直接读取composeApp/src/commonMain/kotlin/shadowbot/BaseFileWebHookAppFramework 这个文件进行了解即可


另外最最重要的是 你在做上面的所有事情之前  必须先生成准确和比较详细的执行计划md文件 写入到composeApp/src/commonMain/kotlin/shadowbot 文件夹下
然后再一步步执行  千万不要把所有任务都一次性堆在内存里  (重要的事情说三遍)

另外最最重要的是 你在做上面的所有事情之前  必须先生成准确和比较详细的执行计划md文件 写入到composeApp/src/commonMain/kotlin/shadowbot 文件夹下
然后再一步步执行  千万不要把所有任务都一次性堆在内存里  (重要的事情说三遍)

另外最最重要的是 你在做上面的所有事情之前  必须先生成准确和比较详细的执行计划md文件 写入到composeApp/src/commonMain/kotlin/shadowbot 文件夹下
然后再一步步执行  千万不要把所有任务都一次性堆在内存里  (重要的事情说三遍)

如果你对我以上描述还有问题的话  直接问我  我给你回复  我们要对其颗粒度(重要的事情说三遍)
如果你对我以上描述还有问题的话  直接问我  我给你回复  我们要对其颗粒度(重要的事情说三遍)
如果你对我以上描述还有问题的话  直接问我  我给你回复  我们要对其颗粒度(重要的事情说三遍)

