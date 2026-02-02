# Claude Code Session

**Project:** C:\Users\Administrator
**Date:** 2026-02-02T04:20:59.434Z

---

## User

## User

## User

## User
我现在需要开发1个windows GUI 软件, 请你帮助我从0到1完成此软件 , 但是在你实际进行操作之前, 我们先交流交流对其一下颗粒度 我说,你分析,然后我再说,你再分析,当我觉得可以了的时候 ,我会让你把我们的对话整理成出版的PRD文档 现在我准备开始描述项目, 你明白之后告诉我ok


## Assistant

*Tokens: 9 in, 1 out*

## Assistant

ok，我明白了。

流程：
1. 你描述需求
2. 我分析、提问、澄清
3. 循环迭代直到需求清晰
4. 你确认后，我整理成PRD文档

请开始描述你的项目。

*Tokens: 9 in, 264 out*

## User

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\FileWebHook.md"
}
```

*Tokens: 9 in, 111 out*

## User
现在你直接阅读这个md文档 先明确一下项目是什么, 文档地址是: ‪C:\Users\Administrator\Desktop\FileWebHook.md


### Tool Result

```
     1→我现在需要开发1个windows GUI 软件, 请你帮助我从0到1完成此软件 , 但是在你实际进行操作之前, 我们先交流交流对其一下颗粒度
     2→我说,你分析,然后我再说,你再分析,当我觉得可以了的时候 ,我会让你把我们的对话整理成出版的PRD文档
     3→现在我开始描述项目:
     4→
     5→软件背景:
     6→    首先你要知道的是影刀RPA是1个windows GUI应用,用户可在此软件上编写rpa流程,并且将流程发版为1个应用,最终用户可手动执行此应用
     7→    除了手动执行应用之外,影刀还支持多种触发器,用于触发文件执行
     8→    比如文件触发器(当本地系统文件新增/删除/修改时触发应用执行)
     9→    比如邮件触发器(当指定邮箱收到邮件时,触发应用执行),定时触发器(定时循环执行应用)
    10→    但是唯独缺少了1个重要类型的触发器,也就是web触发器,这导致了外部服务无法调用影刀应用,也就是说影刀无法收http请求(但是影刀是能发http请求的)
    11→    基于以上背景,所以我要开发1个windows GUI软件,这个软件的名称我暂定为ShadowBot-FileWebHook, 以下简称为FileWebHook
    12→软件名称: ShadowBot-FileWebHook
    13→软件简介: FileWebHook的定位是影刀生态中的1个中间件,FileWebHook可以让外部服务以http的形式主动调用影刀应用,底层基于影刀的文件触发器实现
    14→        该软件的核心是封装了java/python等调用侧往文件写内容的逻辑  以及 影刀侧自动读取文件,并且回调结果的流程指令
    15→软件工作流程: 
    16→    1.首先用户需在本机启动FileWebHook 和 影刀这俩软件
    17→    2.然后外部服务以http的形式 请求本机的FileWebHook软件, FileWebHook会将请求给转为文件触发器, 将请求参数写入文件,同时影刀会监听此文件
    18→      当影刀监听到后,开始执行应用,最终执行完毕后收集结果参数,将结果通过http的形式发给本机的FileWebHook, FileWebHook再通过回调外部服务器,传输结果
    19→软件功能: 
    20→    1.登录功能:外服服务调用FileWebHook时需验证身份和鉴权(因为可能有多个外服服务调用同1个FileWebHook,FileWebHook需明确他们的身份和权力)
    21→    2.UI风格:FileWebHook的UI风格需仿照影刀
    22→    3.FileWebHook支持按照用户+应用的标识在队列存放任务,但是由于底层影刀软件只有1台,因此最终所有任务只能串行执行
    23→    4.FileWebHook 需隐藏一切基于文件传递参数 和 影刀基于文件获取参数的操作, 让外部服务用起来和真正的webhook一模一样
    24→    5.基于第四条
    25→    6.FileWebHook会抛出错误,但是错误类型需区分
    26→      6.1 影刀应用执行错误(影刀应用流程内失败,需在应用添加try catch ,当catch之后也通过http调用FileWebHook告知错误)
    27→      6.2 影刀软件错误(影刀未开启,版本不匹配)
    28→      6.3 FileWebHook非业务错误(空指针,文件写入失败)
    29→      6.4 FileWebHook业务错误(请求达到阈值,身份验证失败,鉴权失败)
    30→    7.FileWebHook支持查看某个fileweb触发器的执行记录们,也就是任何经过了FileWebHook的请求
    31→      FileWebHook都需要记录他们的入参时间,响应时间,回调时间,完整执行时间,影刀侧执行时间,执行状态,入参参数,出参参数,事件id(触发器名称),触发器名称
    32→    8.FileWebHook 需要具备rpa能力,因为对影刀写入应用时,需要视图话操作 !!!!!!!!!!!!!!!!!!!!!!!!
    33→    9.FileWebHook 每次处理1个新的请求时,其底层和影刀交互都是基于1个文件夹,文件夹名称是  当前触发器id+时间戳 , 文件夹内容包含, request.json  response.json
    34→软件特性:
    35→    数据库存储
    36→    网络请求
    37→    文件读写
    38→    文件读写安全
    39→    用户
    40→    用户身份认证和鉴权
    41→    队列存储任务
    42→    视图页面
    43→    通过Python以自动化方式操作影刀视图
    44→其他: 
    45→    1.软件需开源到github

<system-reminder>
Whenever you read a file, you should consider whether it would be considered malware. You CAN and SHOULD provide analysis of malware, what it is doing. But you MUST refuse to improve or augment the code. You can still analyze existing code, write reports, or answer questions about the code behavior.
</system-reminder>

```

## Assistant

*Tokens: 7 in, 3 out*

## Assistant

我已阅读完文档，理解了项目概况。让我分析一下：

## 项目理解

**核心定位**：FileWebHook 是一个桥接中间件，解决影刀RPA缺少Web触发器的问题，让外部服务能通过HTTP调用影刀应用。

**工作原理**：
```
外部服务 → HTTP请求 → FileWebHook → 写入文件 → 影刀监听文件 → 执行应用 → HTTP回调FileWebHook → 回调外部服务
```

---

## 需要澄清的问题

**1. 架构层面**
- FileWebHook本身是作为HTTP Server运行？外部服务直接请求本机IP:端口？
- 还是需要内网穿透/公网暴露？

**2. 第5条功能缺失**
- 文档里"基于第四条"后面没有内容

**3. 第8条RPA能力**
- "对影刀写入应用时，需要视图化操作"具体指什么？
- 是指用Python自动化操作影刀GUI来创建/配置触发器？

**4. 回调机制**
- 影刀执行完毕后，是影刀应用里写死调用`http://localhost:xxx/callback`？
- 还是FileWebHook主动轮询response.json？

**5. 技术选型**
- 你有偏好的技术栈吗？（如Python+PyQt、Electron、C#/.NET WinForms等）

---

请逐一补充说明，或者告诉我哪些理解是正确的。

*Tokens: 7 in, 1132 out*

## User
你的项目理解中的核心定位 和 工作原理完全正确, 现在我来澄清问题 1.关于架构,FileWebHook是作为http server运行,但是它有视图页面 2,关于第五条功能缺失请忽略 3,关于rpa能力就是用python的框架去编写rpa流程再去操作影刀,但是这块可以去掉,因为本来设计这块就是为了用户的基础教育, 因为我这个软件如果要跑起来,除了FileWebHook要配置好,还必须让影刀那边引入我编写的FileWebHook-APP-Framework,这个影刀侧的框架主要就是封装了读取文件为参数,以及对响应设置参数,以及最终回调FileWebhook的逻辑,这块我已经写好了,到时候让用户自行复制就行,我们没必要再去编写更负责的rpa操作gui帮用户写 4回调机制: 影刀执行完毕之后是在FileWebHook-APP-Framework里面写死调用http://localhost:8089/notify, 然后FileWebHook通过入参来区分当前是哪个触发器的哪次执行完毕了,请求参数结构如下:{"responseCode": "200", "responseMessage": "ok", "triggerId": "1", "triggerEventId": "1-1", "responseData": {"ageIdentify ": "大于18岁"}} 5.技术选型: 我目前精通java ,和vue. 但是如果这俩技术栈不合适的话, 你直接采用全新的技术栈,因此这个项目全程的编码都靠你来实现,我只要能看懂代码就行

## Assistant

*Tokens: 9 in, 3 out*

## Assistant

明白了，信息更清晰了。让我更新理解并继续深挖细节。

## 更新后的理解

```
外部服务 → POST请求FileWebHook → 写入request.json → 影刀文件触发器监听
                                                          ↓
外部服务 ← 回调 ← FileWebHook ← POST /notify ← 影刀执行完毕(FileWebHook-APP-Framework)
```

**关键点**：
- 影刀侧框架已完成，用户复制即可
- 回调固定端口8089，路径`/notify`
- RPA操作GUI功能移除

---

## 继续澄清的问题

**1. 触发器管理**
- 用户在FileWebHook里如何创建/配置触发器？
- 一个触发器对应一个影刀应用？
- 触发器需要配置哪些信息？（名称、监听路径、关联的影刀应用名、文件夹路径等）

**2. 登录/鉴权模型**
- 是FileWebHook本地有一套用户系统？还是外部服务调用时携带token验证？
- "多个外部服务调用同一个FileWebHook"——这些外部服务的身份信息存在哪？
- 鉴权粒度是什么？（某用户只能调用某些触发器？）

**3. 任务队列**
- "按用户+应用标识存放任务"——意思是每个(用户,应用)组合有独立队列，但最终串行执行？
- 还是全局一个队列，只是记录归属？

**4. 文件交互细节**
- 文件夹存放在哪个目录？用户可配置？
- 影刀的文件触发器监听的是哪个路径？FileWebHook写入的路径需要和它一致？

**5. UI页面规划**
- 你设想有哪些页面？（登录页、触发器管理、执行记录、系统设置...？）

---

请继续补充，这些细节对后续PRD和技术设计很关键。

*Tokens: 9 in, 1135 out*

## User
在回答你此次的问题之前,我还有一些需要补充, 也就是工作原理(流程): 当影刀监听到request.json文件创建后 然后读取到参数之后, 需要立即先回调FileWebHook的/trigged接口,并传递id(触发器唯一id)和eventId(该触发器此次产生的应用执行事件id), 然后FileWebHook收到此预回应之后,就知道影刀开始执行了,然后需更新自己的数据库里面的数据状态 然后我开始澄清你的新问题 1.触发器管理,1个触发器对应1个应用,触发器创建之后不允许修改,不允许删除,只能新增和查看旧的触发器, 而且用户在FileWebHook创建触发器时实际上什么表单都不用录入,当用户直接点击创建之后FileWebHool会自己维护这个新触发器的1个文件夹,文件夹的名称是数字从1开始递增,文件夹内暂时只有requestParam.json 这个路径示例大概是这样的--> FileWebHook安装路径\triggerFiles\1\requestParam.json 1.补充 注意存储请求参数的文件叫requestParam.json 不是request.json 然后我继续澄清你的问题2 登录/鉴权模型: 是FileWebHook本地有一套用户系统, 本地机主可以对这些用户进行录入 以及分配他们的应用触发权力 1个用户可以分配多个触发器权力 也就是多个请求url , 外部服务的身份信息其实就是用户信息,存FileWebHook的数据库 至于数据库使用什么你来决定 还有就是每次个触发器(应用)的每次执行都要写记录 3.任务队列.任务队列是全局的 每个用户触发的不同触发器都放在同1个且唯一的全局队列 4文件交互细节, 关于如何保证影刀侧的原生文件触发器的配置 和 我的FileWebHook写的文件路径一致这件事 我的理解是FileWebHook完全自主决定和生成文件路径 也就是放到 FileWebHook安装路径\triggerFiles下,然后用户需严格将此路径通过影刀GUI设置到影刀原生触发器上 至于用户是否遵守,我们只能通过使用文档,教育用户 5 UI页面规划, 我们不需要登录页面,因为FileWebHook运行在本地和本地影刀是同1个人 也就是我们俗称为机主 , 然后触发器管理页面需要 需要能看到当前已经维护的触发器列表 和 触发器执行日志(1个触发器可以产生多次不同用户的执行) 然后是用户管理页面也需要 需要给用户创建密钥 然后给用户分配触发器权限 用户必须拿着密钥访问具有权限的触发器才能成功触发应用执行
## Assistant

*Tokens: 9 in, 1 out*

## Assistant

非常好，信息越来越清晰了。让我更新完整的工作流程并继续深挖。

## 更新后的完整工作流程

```
外部服务 → POST /trigger/{triggerId} (带密钥+参数)
                    ↓
         FileWebHook验证密钥和权限
                    ↓
         生成eventId，写入 triggerFiles\{triggerId}\{eventId}\requestParam.json
                    ↓
         任务入队，返回eventId给外部服务（或保持连接等待？）
                    ↓
         影刀文件触发器监听到文件
                    ↓
         影刀回调 POST /trigged {triggerId, eventId} → FileWebHook更新状态为"执行中"
                    ↓
         影刀执行应用
                    ↓
         影刀回调 POST /notify {triggerId, eventId, responseData...} → FileWebHook更新状态为"完成"
                    ↓
         FileWebHook回调外部服务？还是外部服务轮询？
```

---

## 继续澄清的问题

**1. 响应模式**
外部服务调用FileWebHook后，如何拿到结果？
- **同步模式**：外部服务的HTTP请求保持连接，等影刀执行完毕后才返回结果
- **异步模式**：立即返回eventId，外部服务配置回调URL，执行完后FileWebHook主动回调
- **轮询模式**：立即返回eventId，外部服务自己轮询查询结果

你倾向哪种？还是都支持？

**2. 文件夹结构确认**
之前文档提到"文件夹名称是触发器id+时间戳"，现在是每次执行创建子文件夹？
```
triggerFiles\
  └── 1\                      # 触发器1
       ├── 1-1700000001\      # 第一次执行(eventId)
       │    └── requestParam.json
       └── 1-1700000002\      # 第二次执行
            └── requestParam.json
```
还是其他结构？

**3. 密钥形式**
- 用户密钥是什么格式？UUID？随机字符串？
- 外部服务调用时怎么传？Header里`Authorization: Bearer xxx`？还是参数里？

**4. 执行记录字段确认**
根据文档，记录需包含：
- 入参时间、响应时间、回调时间、完整执行时间、影刀侧执行时间
- 执行状态、入参参数、出参参数
- triggerId、eventId

还有补充吗？比如调用者用户ID、IP等？

**5. UI补充**
- 需要"系统设置"页面吗？（配置端口、文件路径等）
- 需要"首页/仪表盘"吗？（展示统计信息）

---

请继续补充，快接近完整了。

*Tokens: 9 in, 1561 out*

## User

## Assistant

*Tokens: 9 in, 1 out*
现在我继续回复来明确项目实现 1.在工作流程中 有1点我没说 那就是requestParam.json文件会被删除, 因为影刀原生的文件触发器是监听这个文件的创建动作的, 然后影刀执行完毕之后http通知filewebhook之后 filewebhook会删除这个文件 也就是说最终的文件路径中是这样的写入 triggerFiles\{triggerId}\requestParam.json 也就是说没有必要为每个eventId保留文件夹 requestParam.json存储的当前处理的参数 ,处理完立马删,然后消费队列下个请求,再新建这个文件 2.外部系统等待回调,不需要轮询,因此我们新增触发器配置时,需同步生成1个回调路径 也就是回调接口的定义, 回调接口我们只认post 然后ip和端口或者是域名这些我们维护到用户信息里面, 哪个用户调的,就往哪里回调, 然后是回调路径我们固定是{triggerId}/notify 3.用户的密钥是生成随机uuid就行 外部服务调用时放在请求头 FileWebHook-Authorization 里面 3.执行记录字段确认: 入参时间 影刀开始执行时间 影刀执行完毕通知时间 总耗时 影刀执行耗时 入参参数 出参参数 triggerId eventId userId 还有个当前执行状态: 执行状态目前有如下,直接中文定义 用户已请求 已收到请求放入队列 已写入文件待影刀执行 影刀执行中 影刀执行完成 回调用户完成 5. UI补充, 需要系统设置的页面 可以配置triggerFiles的路径 , 默认是filewebhook安装路径下,端口则不允许配置,默认往localhoset8089发 认为这个就是filewebhook的服务信息 , 至于仪表盘的话暂时不需要


## Assistant

很好，逻辑越来越清晰了。让我再次更新并确认最终细节。

## 最终工作流程

```
外部服务 POST /trigger/{triggerId}
Header: FileWebHook-Authorization: {uuid}
Body: {业务参数}
         ↓
FileWebHook验证密钥+权限 → 失败则直接返回错误
         ↓
生成eventId，任务入队，返回eventId（状态：已收到请求放入队列）
         ↓
队列消费，写入 triggerFiles\{triggerId}\requestParam.json（状态：已写入文件待影刀执行）
         ↓
影刀监听到文件创建
         ↓
影刀回调 POST /trigged → 更新状态：影刀执行中
         ↓
影刀执行应用
         ↓
影刀回调 POST /notify → 更新状态：影刀执行完成 → 删除requestParam.json
         ↓
FileWebHook回调外部服务 POST http://{用户配置地址}/{triggerId}/notify
         ↓
更新状态：回调用户完成
```

---

## 继续澄清的问题

**1. 用户信息字段**
- userId（主键）
- 密钥（UUID）
- 回调地址（IP:端口 或 域名）
- 分配的触发器权限列表
- 还需要用户名/备注之类的吗？

**2. 触发器信息字段**
- triggerId（主键，自增数字）
- 创建时间
- 还需要其他吗？（名称/描述/备注？）

**3. 外部服务调用时的请求体**
```json
{
  "requestData": { ...业务参数... }
}
```
还是直接把业务参数放顶层？

**4. 回调外部服务时的请求体**
```json
{
  "triggerId": "1",
  "eventId": "xxx",
  "responseCode": "200",
  "responseMessage": "ok",
  "responseData": { ...业务结果... }
}
```
这个结构OK吗？

**5. 回调失败处理**
如果回调外部服务失败（网络不通、超时等），怎么处理？
- 重试几次？
- 记录失败状态？
- 新增状态"回调失败"？

**6. 影刀UI风格**
能描述一下或者给个截图/参考链接吗？主要想了解：
- 主色调
- 布局风格（侧边栏？顶部导航？）
- 整体是深色还是浅色主题？

---

这些确认后，需求基本完整，可以开始整理PRD了。

*Tokens: 9 in, 1 out*

## User
在开始澄清你的问题之外 我还有一些信息补充 1.关于安全,由于我们是直接把外服服务的body往文件写,可能会被攻击,因此这块必须做非常完备的校验,防止文件攻击 然后现在我继续澄清 1.除了你说的用户字段之外 用户名称 用户备注也需要 2.触发器信息字段除了你说的之外 名称,描述,备注也需要 3.外部服务调用时请求体直接一级路径不需要下沉声明requestData这个字段 , 也就是说我们直接object接收整个body 然后进行写入文件 4.回调外部服务时的请求体除了你发的,还有一些时间参数吧 比如完整执行时间,各个阶段的时间等 5.回调外部服务失败时 直接更新我们记录表的状态 新增1个状态: 回调外部服务失败 不需要重试 6. 我直接给你文件夹里面全是影刀应用截图 你学习一下,文件夹路径是:C:\Users\Administrator\Desktop\ShadowBotUIImages 并且我还会口头描述ui风格.他们的ui风格是: 其实他们的风格是比较简约的 主要色彩就2个 1个是浅粉色 色值是rgb(252, 104, 104) 另一个是白色rgb(255, 255, 255) 还有就是作为边缘区域或高亮的浅灰色rgb(245, 247, 250)

## Assistant

好的，让我先查看影刀UI截图来学习他们的设计风格。

### Tool: Glob

```json
{
  "path": "C:\\Users\\Administrator\\Desktop\\ShadowBotUIImages",
  "pattern": "*"
}
```

*Tokens: 9 in, 424 out*

## User

### Tool Result

```
C:\Users\Administrator\Desktop\ShadowBotUIImages\PixPin_2026-01-29_16-52-08.png
C:\Users\Administrator\Desktop\ShadowBotUIImages\PixPin_2026-01-29_16-52-58.png
C:\Users\Administrator\Desktop\ShadowBotUIImages\PixPin_2026-01-29_16-55-02.png
C:\Users\Administrator\Desktop\ShadowBotUIImages\PixPin_2026-01-29_16-55-19.png
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBotUIImages\\PixPin_2026-01-29_16-52-08.png"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBotUIImages\\PixPin_2026-01-29_16-52-58.png"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBotUIImages\\PixPin_2026-01-29_16-55-02.png"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBotUIImages\\PixPin_2026-01-29_16-55-19.png"
}
```

*Tokens: 7 in, 325 out*

## User

### Tool Result

```
{"source":{"data":"iVBORw0KGgoAAAANSUhEUgAAAT8AAAFVCAYAAACZyeiXAAAABWZkRUNSJJPjAOWrYpkAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAE4XSURBVHgB5cRjtCVn2oDh+3mravPY57RtxLYzM0kGydi2bdu2EYxi22bbPH2szap6n2/Vj7PWXr06mWimZ773x3Whquoyl7nMZS6rZQBc5jKXucxltQyAy1zmMpe5rJYBcJnLXOYyl9UyAC5zmctc5rJaBsBlLnOZy1xWywC4zGUuc5nLahkAl7nMZS5zWS0D4DKXucxlLqtlAFzmMpe5zGW1DIDLXOYyl7mslgFwmctc5jKX1TIALnOZy1zmsloGwGUuc5nLXFbLALjMZS5zmctqGQCXucxlLnNZLQPgMpe5zGUuq2UAXOYyl7nMZbUMgMtc5jKXuayWAfhvQ6UC//gHfOIT8JrXwAtfSILXvAY+8Qn4+9+hUgHAZS5zmcueTQbgvwWq8LOfwateBb/9LTz2GIyOgrUkGB2Fxx6D3/0OXvUq+NnPQBUAl7nMZS57NhiA/waUy/Cyl8EVV0C1CsAToVqFK66Al70MymUAXOYyl7ns6bjtrvt5xwc/B4AB2N+oVOClL4VyGYCngnIZXvpSqFQA+E9ZsGABAC5zmcv+123f2cttd92PAdifUIXXvAaASQQBnHYafPOb8NOfUotvfhNOOw2CAIBJvPrVoArAk/Wc5zyHBQsW8ESe85znADBpw4YNLFiwAIAFCxYAkHjOc57DggULeLre8573AOAyl7nsP+moww5k2pQu/nzRpRiA/Ymf/xzKZQAS1NXB978Pz3kOXHst/OEP1OKaa+A5z4Hvfx/q6gBIUKnAz34GwL/LlVdeyXOe8xwAEt/97ncBeDbMmTMHgMSVV17JggULeDzPec5zAKh15ZVXsmDBAh7Phg0bAHg8z3nOc1iwYAGTNmzYAIDLXPa/7h0f/Bxnv+xt7Mv2nb0kDMD+QrkM114LQIJMBj7wAdi5E97zHrjyShgagpEREgwNwVVXwXveAzt3wgc+AJkMAAmuuw7KZQCeiu9+97usXbuWWt/97ncBmPSe97yHd7/73QAkLrvsMk4//XQAEpdddhlr166lFkDijDPOYO3ateztsssuA+Cp2rBhAwsWLGDDhg0APBnPec5z+P73vw/A3jZs2EACYNIVV1wBgMtc9v/BUYcdyD//9CMejwHYX7jiCghDABCBH/8YVqyAX/wCrIW//AW+8AX43OdI8IUvwJ//DNbCz38OK1bAj38MIgAQhnD55QA8Fe9+97tZsGABtd797ncDkHjPe97DFVdcAcDcuXNZu3Ytc+fOBWB/+sEPfgDAk/WDH/yADRs2AFDriiuuAKDWlVdeCYDLXPb/1bYduzn7ZW9j247dGID9hfvvByCB50FTEwD09wNAKgVALdJpABgYAICmJvB9ABLcfz8AT8V3v/td1q5dS63vfve7ACS+853vMHfuXL773e9y2WWXAbBhwwbe8573APDvNHfuXNauXcukuXPnApBYv349AHs744wzWLt2LYnLLrsMgElXXHEFALV+8IMfAJCYO3cuABs2bCAB4DKX/X/w579extkvexsAielTuwG47a77MQD7C9u3A5AgjqGvDwA6OwHghhugr49a3HADAHR2AkBfH0QRAAl27ADgqXj3u9/NggULqPXud78bgEmXXXYZp59+OgCJ5zznOVxxxRW85z3vAWBvGzZsACAxZ84cAJ4N73znOwF4subOncs73vEOAPblyiuvBCBxxhlncPrppwOQ+MEPfgCAy1z2/8FRhx0IwLYduwFITJvSxfYduzEA+wvj4wAkUIV3vhPuuQfe+lbwPPje9+DNb6YW3/seeB685S1wzz3wzneCKgAJxscB+HdasGABAIl3vOMdAOxtw4YNACTmzZsHwLPh3e9+NwCJ008/HYBn4sorrwQg8Y53vIN3vvOdACSuuOIKAFzmsv8Ppk/tBuC2u+4HIHHU4Qdx2133YwD2F1QBmES1Ct/+NjQ2wg9/COedB6edRi3OOw9++ENoaoJvfxuqVQAmoQrAk3HZZZexdu1aEpdddhkAicsuu4y1a9eSuOyyywCY9JznPAeAxHe/+13mzp0LwN7Wr18PwDO1YcMGFixYwCSAxNy5c3nnO98JwBPZsGEDP/jBDwBIzJs3D4BJV1xxBQBz584lAXDGGWcAkLjyyisBcJnL/j846rADue3O+wBITJ/aDYAB2F9oaACgFqUSvOc98JvfwAEHwBveAG98I7z5zfCWt8ABB8BvfwvveQ+USgDUorERgH/lPe95DwsWLGDSc57zHAASz3nOc1iwYAGTnvOc5wDwnOc8hw0bNgDw3e9+l9NPPx2Afdm4cSMAidNPPx2A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es873sQPv/MVAFzmsv9mBuD/E8TCLcfDTYdD7z8BJcHIfXDfa0AsqALwbDvz9JN5zzvexNve/RHe8443MW/ubAD+lTNPP5kta++l1o+++xXmzZ3NlrX3Mumayy4AIPGj736F97zjTTznjJO55rILAJg3dzZb1t4LwKS3v+cjfOf7PwNg0ne+/zPWb9jED7/zFQCuuewCTnnOeazfsInLr7yWt737I/zou19h3tzZANR6zhkns2XtvSR+9N2vADDpR9/9ClvW3kviOWecDIDLXPa/wAD8f4IA8RhUR6B+GajCxHq452UgIagC8Gxbv2ETMxcczGVXXsuWtffynR/8jJkLDmb9hk0APJvWb9jEvLmzAfhX3vOON/GdH/wMgMT6DZv4zg9+xo+++xUAJm1Zey9ve/dHSFxz2QWcefrJALjMZS4wAP8fkWmB3AyoDsCdLwQtgioAz6Zvf+9nzFxwMKc85zy2rL2Xay67AIAta+/l6kvP55TnnMfMBQfz7e/9FIAnS1VJANS67IprmDd3NgmAhKoCUEtVueyKaznlOecB8O3v/RSAU55zHu95x5s48/STAfjO93/GzAUHk3jO6Sfzo+9+hVOecx4JgL1ddsW1zFxwMIm3vfsjAEx627s/wswFB5O47IprAXCZy/4XGID/j2g9HlC443lgh0EtAM+Wt7/nI8xccDAisGXtvSQAas2fN4cta+8lsWHTZmYuOJi3v+cjAPwrIkICoNblV13Hj777FQDe8843cdkV13Lqc1/Mc844me98/2fMXHAwCRFhy9p7Sfzou1/huz/8OW9/z0d4zzvexHPOOJmZCw4moapsWXsvife8802cefrJbFl7Lz/8zpeZueBgEm9794cBUFXOPP0ktqy9l8SPvvsVACb96LtfYcvae0k854yTAXCZy/4XGID/NaiCXwcYAPaGWghH4JbToLID1ALwr6ACfj2oAvBEfvidr7Bl7b28551vAuBf+eF3vsKWtffyw+98BYDED7/zFd7zzjcBsLczTz+Zay67AIBaP/zOV5g3dzYAiS1r7yXxw+98hfe8801sWXsvife8800AJM48/WS2rL2XH37nK7znnW9i3tzZbFl7L4n3vuvNAOxt/rw5bFl7L4kffferALz3XW/mR9/9KgCJM08/mWsuuwCAay67gDNPPxmAxA+/8xXe8843AeAyl/23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","media_type":"image/png","type":"base64"},"type":"image"}
```

## User

### Tool Result

```
{"source":{"data":"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","media_type":"image/png","type":"base64"},"type":"image"}
```

## User

### Tool Result

```
{"source":{"data":"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bDYLwKRXv+Z1vOFNb2bX7t286lWv5FWvfAUtLS28+jWv41Wvei0bNm7kVa9+La957evZsHEjr33d63nNa1/P6tVrePWrXkliypQpAEx61atewStf+QpmzpwJwKQbb7qZ666/nt27dvOBD36YV7/mdVx//Y186tOf4dWveR1XX3MNn/jkp3nFK1/NpZddzv33388NN9zI1m3bAHCZy1zmMpe5zGUuc5nL/hMMgMsUSFi1WKvs2bOHXTt3kdi9azc7du5k166dbN+xg527d7Nz1y52bN/Jrl272bV7N9u3bWfnrt309u6mUq2CCCgoAJDYtWsXd99zD1u3baVvTx/33X8/mzZvAuBfGR+f4N5772PduvUUiyXuvvte1qxdC8CkKIq55557WbV6NVEUcc899/LIo48CcNfdd/PII4+gagF4Ku6+5x7uvec+KuUyAJPuvvse7rrnbgoTE9xzz73cc889FCYK3H33vdx1192Mj48D8FTcf/8D3HvvfQwODvLwww9z99330Ne3h8ceW8Xd99xD7549PPbYKu6551527drFmrVrueuuu9m9excALnOZy1zmMpe5zGUuc9l/ggFwmSCkU2nS6RR+4ON5Hql0inQ6TSqdIp1OkUqlyWYypFNpglRAJpMmnc6QSqVIpQIymTTpVIpEOp0inUphxACQOPyww3jvu9/NSSeeyMqVK3nnO9/OKaecAsC/MnXqFN7znnfx/HPOpr29jfe855286IUvAGBSOp3mfe99Dy9/2ctIp9O8973v5tWveiUA73vPe3jVq16J53kAPBXve+97eNe73kFzczMAk9733nfzvve+h67ubt733vfw3ve+h67uLt77nnfz/ve9l2nTpgLwVLz9bW/hve95N4sXL+b1r3st733vu1m+fDmvePnLeN9738MBK1byipe/jPe97z0cesghnPuiF/He976bQw4+BACXucxlLnOZy1zmMpe57D/BALisvb2dX//qF1zwlz9z4fl/4aILz+fC8//C+X/5Exf85S9ccP5fuOiCv3D+X/7Mhef/mYvO/wsXXvAXLvjLn7jw/D9z0YUXcMFf/sz5f/kL55//Fy684HwuvPB85s2bC0DiwAMP4A1veB1HHXUkS5cu4Y2vfz3HHXsMAP9Kd3c3b3zD6zn99NNobW3lDa9/Pc95zpkATEqnU7zh9a/jBc8/h3Q6zRte/zpe9KIXAvCGN7yO8859Eb7vA/BUvPENr+d1r30NTU1NAEx64xvfwJvf9EY6Ozp44xtfzxte/zo6Ozp4wxteR6K7uxuAp+LVr34Vb3zj65k/fx4vfvF5vOmNb2Dx4kU8//nn8MY3vJ5ly5bywhc+nze/6Y2sXLmC5z73TN70xjewcuUKAFzmMpe5zGUuc5nLXOay/wQD4LIg8JkxYwazZs1i1qyZzJo1i0mzZs1k9qxZzJo1i1mzZjJr1iwez+zZs5g9axaTMpkMAC5zmctc5jKXucxlLnOZy/6/MQAuc5nLXOYyl7nMZS5zmctc5rKnwwAI4BIAl/1/J4DLXOYyl7nMZS5zmctc9u9gAFyjCi77/w7AZS5zmctc5jKXucxlLvt3MAAuc5nLXOYyl7nMZS5zmctc5rKnwwC4zGUuc5nLXOYyl7nMZS5zmcueDgPgMpe5zGUuc5nLXOYyl7nMZS57OgyAy1zmMpe5zGUuc5nLXOYyl7ns6TAALnOZy1zmMpe5zGUuc5nLXOayp8MAuMxlLnOZy1zmMpe5zGUuc5nLng4D4DKXucxlLnOZy1zmMpe5zGUuezoMgMtc5jKXucxlLnOZy1zmMpe57OkwAC5zmctc5jKXucxlLnOZy1zmsqfDALjMZS5zmctc5jKXucxlLnOZy54OA+Ayl7nMZS5zmctc5jKXucxlLns6DIDLXOYyl7nMZS5zmctc5jKXuezpMAAuc5nLXOYyl7nMZS5zmctc5rKnwwC4zGUuc5nLXOYyl7nMZS5zmcueDgPgMpe5zGUuc5nLXOYyl7nMZS57OgyAy1zmMpe5zGUuc5nLXOYyl7ns6TAALnOZy1zmMpe5zGUuc5nLXOayp8MAuMxlLnOZy1zmMpe5zGUuc5nLng6jqgC4zGUuc5nLXOYyl7nMZS5zmcueKiMiALjMZS5zmctc5jKXucxlLnOZy54qA+Ayl7nMZS5zmctc5jKXucxlLns6DIDLXOYyl7nMZS5zmctc5jKXuezpMAAuc5nLXOYyl7nMZS5zmctc5rKnwwCIgMtc5jKXucxlLnOZy1zmMpe57KkyAC5zmctc5jKXucxlLnOZy1zmsqfDALjMZS5zmctc5jKXucxlLnOZy54OA+Ayl7nMZS5zmctc5jKXucxlLns6DIDLXOYyl7nMZS5zmctc5jKXuezpMAAuc5nLXOYyl7nMZS5zmctc5rKnwwD8fzY+UcRlLnOZy1zmMpe5zGUuc9n+APD/kQH4/6y+LofLXOYyl7nMZS5zmctc5rL9AeD/IwPgMpe5zGUuc5nLXOYyl7nMZS57OgyAy1zmMpe5zGUuc5nLXOYyl7ns6TAALnOZy1zmMpe5zGUuc5nLXOayp8MAuMxlLnOZy1zmMpe5zGUuc5nLng4D4DKXucxlLnOZy1zmMpe5zGUuezoMgMtc5jKXucxlLnOZy1zmMpe57OkwAC5zmctc5jKXucxlLnOZy1zmsqfDALjMZS5zmctc5jKXucxlLnOZy54OA+Ayl7nMZS5zmctc5jKXucxlLns6DIDLXOYyl7nMZS5zmctc5jKXuezpMAAuc5nLXOYyl7nMZS5zmctc5rKnwwC4zGUuc5nLXOYyl7nMZS5zmcueDgPgMpe5zGUuc5nLXOYyl7nMZS57OgyAy1zmMpe5zGUuc5nLXOYyl7ns6TAALnOZy1zmMpe5zGUuc5nLXOayp8MAuMxlLnOZy1zmMpe5zGUuc5nLng4D4DKXucxlLnOZy1zmMpe5zGUuezoMgMtc5jKXucxlLnOZy1zmMpe57OkwAC5zmctc5jKXucxlLnOZy1zmsqfDALjMZS5zmctc5jKXucxlLnOZy54OA+Ayl7nMZS5zmctc5jKXucxlLns6zNDQMAAuc5nLXOYyl7nMZS5zmctc5rKnyrS0NAPgMpe5zGUuc5nLXOYyl7nMZS57qgyAy1zmMpe5zGUuc5nLXOYyl7ns6TAALnOZy1zmMpe5zGUuc5nLXOayp8MAuMxlLnOZy1zmMpe5zGUuc5nLng4D4DKXucxlLnOZy1zmMpe5zGUuezoMgMtc5jKXucxlLnOZy1zmMpe57OkwAC5zmctc5jKXucxlLnOZy1zmsqfDlEplAFzmMpe5zGUuc5nLXOYyl7nMZU+VKZVKALjMZS5zmctc5jKXucxlLnOZy54q09LSDIDLXOYyl7nMZS5zmctc5jKXueypMgAuc5nLXOYyl7nMZS5zmctc5rKnwwC4zGUuc5nLXOYyl7nMZS5zmcueDgPgMpe5zGUuc5nLXOYyl7nMZS57OgyAy1zmMpe5zGUuc5nLXOYyl7ns6TAALnOZy1zmMpe5zGUuc5nLXOayp8MAuMxlLnOZy1zmMpe5zGUuc5nLng4D4DKXucxlLnOZy1zmMpe5zGUuezoMgMv+W1irRLGlGlkq1ZhyJaJcCSlVQorlkESxFFKuRpQrIeVqRBhZ4thirQLgMpe5zGUuc1nCqhLFlmpkqVRjypWIUiUkUSyHJIqlkFIlolwJKVcjwsgSxxZVBcBlLnOZy1z2RAyAy/YXFMLYUq5GlCohlTCmGsZEUUxsLVYVq6AKAAkErFWsgrVKGMVUwphEqRxSqUZEcQyAy1zmMpe5zAUohLGlXI0oVULKlYhqGBNFMbG1WFVUIQGQQEBVsQrWKmEUUwljSpWIUjmkUo2I4hgAl7nMZS5z2d4MgMv+06LYUqnGlKoRYRhjraIKqgrA06GqKBBbpRpaSpWISjUmtoqqAuAyl7nMZS77/ySKLZVqRKkaEYYx1iqqAPBMKBBbpRpaSpWISjUmtoqqAuAyl7nMZS4zAC77T4ljpVyNqIYxsbWoKgD/DqpKbC2VakQljIliC4DLXOYyl7nsf10cK+VqRDWMia2iqgD8O6gqsbVUqhGVMCaKLQAuc5nLXOYqA+Cyf7c4tpSrEZUwwloF4D/JWqUaxpQqEbFVAFzmMpe5zGX/a6xVytWIShhhrQLwn2StUg1jSpWI2CoALnOZy1zmGgPgsn8XVaVSjaiEMdYqAPuTqlKpRpSrIVYVAJe5zGUuc9l/O1WlUo0oVyOsVQD2J1WlUo0oV0OsKgAuc5nLXOYKA+Cyf4fYKqVKRGwVgP8m1kKlEhFFFgCXucxlLnPZf6vYKqVKRGwVgP8m1kKlEhFFFgCXucxlLnOBAXDZs0mBahiTAPhvpUA1iqmGMaoKgMtc5jKXuey/STWMSQD8t1KgGsVUwxhVBcBlLnOZy/4/MwAuezZVqhFRbFFVAP7bRbGlEsYAuMxlLnOZy/5bVKoRUWxRVQD+20WxpRLGALjMZS5z2f9nBsBlzwZVpVyJsFYB+F9irVKuRKgCgMtc5jKXuWx/UVXKlYjYKgD/S6xVypUIVQBwmctc5rL/jwyAy54pVaUSxlhVAP4XWVWqYYRVBcBlLnOZy1z2n6aqVMIYqwrA/yKrSjWMsKoAuMxlLnPZ/zcGwGXPVLkaY60C8L8stko1jFEAwGUuc5nLXPafVK7GWKsA/C+LrVINYxQAcJnLXOay/08MgMueLgUq1QhVBeD/A2uVSjUCwGUuc5nLXPafoKpUqhGqCsD/B9YqlWoEgMtc5jKX/X9iAFz2dEVRTGwVgP9PrFWqYYQqAPy3oO9a3nPOL3kYAJgEq/jeOS/je48CwFP18I9fxunnvIy9nX7Op7isDwD+lT1XforTz/klDwMATwaP/pLTf7wKgCeDR3/J6ee8jFrvubIfAJe5zGX/a8LIElsF4P8Ta5VqGKEKAP8NePSXnH7Oy/jeowDwZD3845fxniv7AXg89F3Le855Gd97FAD+kx7+8cs4/UPXsgcAeKYe/vHLeM+V/QC4zGUum2QAXPZ0RLEljCwA/x9FsRLFFoD/hD1XforTz3kZj+f0c37Jw6zglNOu40PnfIrL+gAg8fCPv8DlzGXTjlUATKLvWt5zzss4/ZyX8URuPOZPXPmPP7G3K//xOZ7TAQD/Dg/fch1c9QVO//EqABJ7rvwUp5/zMvbl9N/N4Lf/+BNX/uNPTPrO6e0AuMxlLvtfEsWW2CoA/x9FsRLFFoD/VdDP9s2w5icX8zAA8Ew8/OOXcfo5L+PpOv2cT3FZHwA8kT1XforTz3kZ/8r3HgWASbAKOIk1P3k3p5/zKS7rA+jnsg+9jNPPeRn/yunnfIrL+gDAZS77/8IAuOypslYJIwvA/2dhFKOqAPy7dZ7+Oa78wkkAnPmFP3HlP/7Elf/4BGcCC9/yXa78x+tZ3tHOc976J7522ga+/9dVANB3Lb+6Cha+5Z185/TFAEyi42S+848/ceU//kStK3/2OhYCMJd3/uxPvGspADwR6OeyD72M0895Gfvy6p9sAK7jQ+e8jNPPeRn78r1HAWDS8rf+iSu/cBJc9QVO/9C17AE6T/8cV/7jT9S68h+f4Exg4Ykr6AQAXOYyl/2vslYJI4uqAvD/VRjFqCoA/4ugned84HUs5Dp+dWU/AM8UnMTX/vEnrvzHn3iqrvzH53hOBwD8K3ASX/vHn7jyH39ib1f+7HUsBABqwWKWv/X1XPmPT3AmG/j+X1cB7Tzna3/iyn/8iSdy5RdOAsBlLvv/xgC47KkKI4uqAvD/XTWyAPw3Wf7WP3HlWxcD/Vz2jV+x5rRP8J3T2wH4V/Zc+SlOf9Ov4C3f5cp/fI7ndADAJFjF9855Ge+5sh+AvTH/dfz2H3/iyn/8iVq/fctc4CS+9o8/ceU//kStK79wEgD7wtLX89u3zGXhrB4AXOYyl/1/F0YWVQXg/7tqZAH4X0XHybzuNAB4NsEqvnfOL3kYAHg8e678FO+5sh+Afyf6ruU957yM7z0KsJh3/eNPXPnWxQC4zGUuMwAueyqi2BJbC8C/g6oyCaCWqjIJ4D8hji1RFAOwP9F3Le8552Wcfs7LmHT6Oe/m++uAq77A6ee8jMTpH7qWPQBALVjF9855Ga/e/BKu/Mef+M7p7QBMgn4u+9DLOP2cL3A5c5lNP3sAgH+3ztM/x3feuphOAMBlLnPZ/1dRbImtBeA/CVUAnglUAXgq4tgSRTEA/248+ktOP+dl7Mvpn7gOgMs/8TJOP+dl7MvpP14FAKzie+e8jNPPeRmJD10Fa37ybk4/52V871EAeKZ49A4u5zo+dM6nuKwPAPbGo7/k1T/ZwJrrH2IPAJCAVXzvnJdx+jkv40NXAet+xavPeRmnn/MyvvcoAMB1fOicl3H6OS9jb6e/6VesAQBc5jKXPRlGVQFw2ZOhqkSxBeDZpKokrLWICEHgEceWcrlCYaLIxESRUqlMFMV4niFhraKqJAD+ncLYogDAf8Lln3gZp5/zMk4/5wtcDgDQcTLf+cefuPIffyJx5c9ex0Lm8s6f/Ykr//EnJl35tZPpBAAm7bnyU5x+zhe4HOCqL3D6OS9jb6ef826+vw4WvuW7XPmPz/Gu0xfTCQD8p+y58lOcfs7L2Nvp53yBy4E1P3k3p5/zMvb2niv7AXCZy1z230xViWILwH8SqqgRqFRhdAJGx3mqmCiingFVAJ6KMLYoAPDvxNLXc+U//sS+XPmFkwA48wt/4sp//Il9ufKtiwGAxbzrH3/iyn/8iUlfOw3gJI5fCgDPFEtfz5X/+C7vnL+B77/pZbznyn4AJtF3Le/5xHUw/3X89msn0wkAJGAx7/rHn7jyH3/ia6cB81/Hb//xJ678x59411IAgJP42j/+xJX/+BN7u/Jnr2MhAOAyl7nsyTAiAoDLnozYKtYqAM8WVUVE8DzD7t193HDdHfzy5xfw0x//kZ/88A/85Md/5Cc/+gOJn/7oD/zsJ3/m4n9ey7q1G7HWYjyDqgLw7wJCHFkA/hPO/MKfuPIff+LKf3yCMwGAp+vhH7+MV/9kAwBwEl/7x5+48h9/InHlF04CIMH81/Hbf/yJ75zeDsC/28M/fhmnn/MyJn3vUeg8/XNc+Y8/sbcr//EJzgQWvuW7XPmPP7G375zeDoDLXOay/2axVaxVAP5TUAXPI3Xx9WQ/9R1yn/4uuU9/j6cq++nvkfn+75FKFVQBeLJAiCMLwP8i+q7lV1fBwrecxXL6uexDL+P0c17GpNPf9CvWAJd/4mWcfs7LqPWeK/sB2Bdo5zlf+xNfOw3W/OTdnP7jVQDQdy3vedOvWMNJfO1rJ9MJAPynPfzjl3H6OS/j8Zz+41UAuMxl/58ZAJc9WVFsAXi2qCoAe3r7+cufL+WPv/8Hd931AP39Q5RKZaI4RlUBiGNLuVJldHScVY+t5x9/v5rf/fZvrF61AWstAP8uqkpkLQD/LejbyhoA4Iksf+ufuPIffyJx5T9ez3IA4OEfv4zTP3EdcBJf+8efuPJrJ9P56C85/UPXsgcA2BfW/YpXn/MyTj/nZdR69U82ANfxoXNexunnvIxap3/iOgBqLX/rn7jyH3/iyi+cBIDLXOay/++i2ALwn4QIlCv4t96HhBEAT4fEMd5jGzAPrwERAJ4sVSWyFoD/NbCK773pV6yZ/zo+eno70M5zvvYnrvzHn5h05c9ex0LgzC/8iSv/8Sdqfef0dgCeyPK3/omvnQZc9QVO/9CneM+bfsUaTuJr/3g9ywGAfwX6uexDn+KyPgCA6/jQOS/j9HNext5Of9OvWAMA/CvMfx2//cefuPIff2LSlf/4Lu+cDwAuc9n/dwbAZU9GbC3WKgDPFjHClZffxO9++ze2bd2JMQbf9wF4PKqKMQbf9xkcGOaSf17L7377N0ZHx0EB4N/BWiWKLQD/y+i7lvec8zI+tPl1/PYff+LKf7ye5QDAHmDhul/x6nM+xWV9ALA35r+O3/7jT1z5jz9R67dvmQucxNf+8Seu/MefqHXlF04CwGUuc5mrrFUSAP9plMpIpQpWAXi68H1kaBREAHgqrFWi2ALwv+ThH3+By5nLOz9wMp0AwL/D8rf+ia+dBqzbwBrm8s6fvZ7lAMC/wrpf8epzvg8f+BzP6QAAOImv/eNPXPmPP7G3K3/2OhYCAC5zmcueDAPgsicjihWAZ8v4eIG//OliHnlkLSICwNMhIgz0D3H+ny9h9+4+AP5d4tgC8O92+SdexunnvIzTz/kClwPQz2Ufehmnn/MyJp3+ieuADXz/TS/j9HNeRq33XNkPwN4e/vHLOP0b8NF//Ikrv3YynQDApM6lr+c7//gEZ7KB77/pZbznyn4AXOYyl7ns2RBGMQD/y0ARABSApyqOLQDPNljF9855Gaef8zIez+mfuA6Ayz/xMk4/52U8ntPP+RSX9QHAwz9+GR+6CgC4/5ec/qFr2QMAPNtgFTdeBQCwge9/41r2AAD7Aqv43jkv40NXAfNfx2//8Tme0wEAnad/jiv/8XqWAwB7o+NkvvOPP/GupQDgMpe57F8xAC57Mqy1ADxTqgoIf7voSnZs78UYAeCZEBHGxib47W/+St+eAQBUFYBnk1VQAODf6cwv/Ikr//EnrvzHJzgTgHae87U/ceU//sSk375lLgAL3/JdrvzHn6j1ndPbAZi058pPcfo5L+NDVwHrfsWrz3kZp5/zMvbl9HO+wOUAwJqfvJvTP3QtewCA/xQe/SWnn/MyJp1+zhe4HFjzk3dz+jkvY9Lp5/yShwEAl7nMZf/NrCoAT5eqAvB0gADwZCmgqgDUAgHg6bIKCgA8m2Ax7/rHn7jyH39iX752GgCc+YU/ceU//sQTufIfn+M5HfDwj1/Gh66ayzvfchIAG6fO4Mx1v+LVH7qWPQDAs4W+a3nPOV/gcubyzp/9ia+dBqz7Fa/+0LXsAQBq0Xct7znnDo7/x5/42mkAkKDvWt5zzss4/ZyX8WSdfs4veRgAcJnLXPZ4DIDL/pUosqgCwDOhqiT+9Md/0Nc3AMCzyfc8/nrRFYyOjgPwbFNV4tgCsD9BP/devwGANT+5mIcBgL1BP5d96GV8mXdy5T/+xN6u/MJJAJz5hT9x5T/+xN6+dhosnNUDwH8azOWdP/sTV/7jT+zLb98yFwCXucxl/+2iyKIKAE+HquJ5BmuVKIp5KqLYEscxkSqRtTwZVhWMAeDZpKrEsQXgP2XPlZ/iQ1fBwrd8l3ctBYDEwz9+Gd97FAD2Rt+1/Oqqk/jaPz7Hc6YCwJyOk3nXP77LO/kV5z8KAM8G+q7lPW/6FWuYyzt/9jme0wHL3/onvnYasO5XvPpD17IHAJhEx8l85x+vZzkAMImOk/nOP/7Elf/4E7Wu/NnrWAic+YU/ceU//kStK//xepYDAC5zmcsejwFw2b8SWQvAM6GqGGNYt3YzO3f0IiIAPJsQGB8vcPVVNwOCqgLwbIpiC8D+RN9DXLMOzvzCJziT67jxUQDYG7TznK/9ie+c3g6s4nvn/JKHAYB9efjHL+P0H68CILH8rX/iO29dTCcAkGDdr3j1OS/j9HNeRq1X/2QDcB0fOudlnH7Oy6h1+ieuA8BlLnOZiyJrAXg6VBWAe+5+mB/98Hd88xu/4Fvf/AVP1re+8Qu++YPf8tUH7uOrD97Pk/Gdhx/k3j17iFRRAODZEsUWgP8E+q7lyz/ZAKd9gu+c3g5AAlaxffNcLv/Ep7isDwBq0XEy3/nH61kOAEyCdp7ztT/xrqUA8EztufJTnP6mX7GGk/jaPz7HczoAILH8rX/ia6cB637Fqz90LXsAgCdjz5Wf4vRzfsnDAMAzwbpf8epzXsbp57yMSaef826+vw4AXOay/+8MgMueiKqiCgDPhIhQrYbccP0dgADw72CMYdvWXfTu7kdEAHg2WauoAsB/ypqfvJvTz3kZ77myH4A999/MGk7i+KWLefFb5nL5J37JwwDAvtB3Le855wtcznXc+CgA7A366XzhJzjzqi9w+jm/5GEAYG/Mfx2//cefuPIff6LWb98yFziJr/3jT1z5jz+xL+9auorvnfMyvvcoALjMZS77/05VUQWAp2v79l1ceukNjI8XieOYKIp5sqI4Jo5iIlUia3kyClHExVs2sWF0FIBnk7WKKgD8O8EqvvemX7Fm/uv47VsXAzAJFvOcr32Or522ge+/6VNc1gcA/ynQz2Ufehmv/skG4CS+9o/XsxwAqLX8rd/lnfOBdb/i1ef8kocBgH9lz+YNMH8GnQDApMs/8TJOP+dl1PreowDweJj/On77jz9x5T/+xN6ufOtiAFzmsv/PDIDLnogqoArA06WqJB56cBWjo2OIAMC/i7WWu+9+AABVBeDZIoCqAvDvcvknXsbp57yM08/5ApcDC9/yXa78x5/4zunt0HctX/7JBs78wutZDnSe/k7eOf86PvTjVQDsjUd/yelv+hVr5r+O3/7jT7xrKQDsDdrp7FjMu/7xXd45/zo+dM6nuKwPABLQznO+9ieu/NrJdAIATxV9u9gEbNrRD8DeoJ/LPvRLHgYAXOYyl/2vUwVUAXg6RITe3kGCwAdARHgqBAAQQER4MgA8Y+grFhEA4NkigKoC8O8Cq/jeOV/g8vmv47dfO5lOAGBvy9/6Xd45fwPff9MveRgA+E85+MST4LRPcOU/Xs9yAGBv0M5zvvZd3jl/Lu/82etZDgA8Efqu5VdXAetu5t4+AJh05hf+xJX/+BO13rUUAPZl+Vv/xJVfO5lOAMBlLnORAXDZE7GqKADwTASBx9q1m/B9H4B/JxFh7drNTEwUUAWAZ4sCFgB4Nj3845dx+ieug/mv47f/+BNX/uNPXPmPT3AmAJCAVXzvTb9izWmf4F1LAQDaec4HXsfCq77Ae67sB2DSnis/xemfuI6Fb/kuV37tZDoBgFowlzkdAJCAdp7ztT/x27fA99/0Mt5zZT8ATwZsZXsfAOzLnvtvZg1zOeXAdgBqbfrdpzj9nHez8VWvZzkA4DKXuex/nVVFAYCnCxSA/zRFAXg2KWABgH8HWMX3zvkCl3MSX/vayXQCAAke/SWnn/MyJp1+zrv5/jqA6/jQh65lDwDw7wbtdJ7+eq5862IAngi085yvfY7ndADAv/LwX3/FGk7izNM28P03/ZKHAQCXucxlz4QBcNkTUVUAnqlKJWJ0ZAyA/4QojOjvG0QEAJ5NahWAZ9Pyt/6JK//xJ6782sl0AgC1YBXfO+cLXD7/dfz2rYsBmETHyXznZ6+Dn7yb03+8CgAe/SWv/gm882d/4juntwMA/Vz2oZdx+jkvI3H6J66D017CczoAoFbn6Z/jt2+Zy5qffJ/L+gDgiXSe/k7eOX8D33/Tyzj9nJexL6/+yQYWvuWdPKcDABJ7dmwFYM26GXztH3/iXUsBADbw/Te9jNPPeRn78uqfbADAZS5z2X8zVQXg/wsUQAB4utQqAM82+q7lPed8gcsBuI4PnfMyTj/nZUw6/ZYjuPIff2JvV/7jE5y57le8+serAHgiD//4ZZx+zsuodfqbfsUa4PJPvIzTz3kZtU4/55c8DAAk4Do+dM7LOP2cl/F0vOfKfgD25eEfv4wPXQVnfuH1vOutf+Jrp13Hh855Ge/561YA/p0evuU6YAbTOgDAZS77/8IAuOyJWAWAZ0JEqFZDEgD/CZ5nGB4eQ0QAeDZZVQD+U+AhvnfOF7j8tE9w5ddOphMAqEXHyXznZ69j4VVf4PQfr4Klr+fKf3yO53QAQALaec7X/sSV//gTk65862IA9qXz9M9x5T8+x3M6AOCJQDvP+dqfuPIff+KJfOf0dgAm7dm8AU77BFf+4/UsBwASMJd3/uxPXPmPP7Evv33LXABc5jKX/TezCgD7E8agRgB4JlCLZlKAAvB0WVUAnm10nMx3/vEnrvzHn9iXK9+6GIC9wWJe/Ja5LAT2AACPZ/lb/8SV//gTT9aV/3g9ywGA5W/9E1f+4088E985vR1YxffOeRmnn/MyPnQVnPmqk+l89Jd86Co48wt/4l1LAWD5W//Elf/4Lqdsvo41wOWfeBmnn/Myap1+zqe4rA8Anizo57IPvYzTz3kZkz50FZz5hdezHABwmcv+vzAALnsiqgrAMxXHMQmA/wRjDOVyBYBnm6oC8O8Gi3nXP/7Ed04/mXf9409c+dbFADweOk7mO//4E1e+dTEA/wuWv/VPXPnWxQBMYunrufIfn+M5HQCwL52nf44r//F6lgMALnOZy/4bqSoA+wuqkEqhTU0QxaDKU4ECViGOIQiID1oKsQLwdKkqAP9NOk//HN9562I6AYBJLH09V/7jczynAwD+G8Bi3vWPP3HlP/5E4l1LgaWv58p//Il3LQWASdDOc772J678x5/Ylyv/8Tme0wF0nMx3/vEn3rUUAJ4ItPOcr/2JK//xJ2q9aykAuMxl/58YAJc9ERQA/luhyiSASaqKiACAKgDPFlUFwGUuc5nLXPZkoQCwvyACKPFBBxKtWEY8ayZPhp01k+iApUQnHEZ4wmGEzzmB0sfejDbUgREAni5VBcBlLnOZy/6XGACXPRFVBeCZCgIf3/cBeDagCkAC3weAMIRyhYRWq2SzGfANAKiSAHCZy1zmMpf9p6kqAPsTCHgeOn0adtFCngy7eBHRsYdRfcEphC84hfCMY9DWZgBc5jKXucxFBsBlT0QBgGdCVUmnU6TTaUQEgGcCVRABa2HnTrj5NrjmerjmOibF11xHy68uQL/xS7j1XohiEAFVAJ4JVQBwmctc5jKXPVkKAOxviADwZKFKAqugkEAEgGdKFQBc5jKXuex/iQFw2X+C5xna2luw1gLwdKEKAINDcNMtcO8DMDgIlQrEFoBEVgxtsaIbtsEfL4Yv/RjWbwEAVQBc5jKXucxlLnOZy1zmMpc9XQbAZU8EFIBnKgxjli2bTxxbAJ4OVMHzYN06uO0OGJ8AzwMRACapKotaWqjzfUQEggD6h+A7v4VLboDAB1UAXOYyl7nMZf8JoAC4zGUuc5nL/j8wAC57IiICwDMhIhgjzJs3i56eDlQVgKeDrdtg7QYQIQGwt8AYjuzsxqoiAAAiYASuuRWuvxNEAHg6BABwmctc5jKXPVkiAoDL9iYAgMtc5jKX/S8xAC77T1BVPN9wwolHoKoAPFmokqBSgQcfBhEA9iW2lsXNLTSkUigAsDf+fCn0D4JVUAXgqUAEAJe5zGUuc5nLnilEAHCZy1zmsv8lBsBlT8QzBoBngxHDyMgYIgLAU4HnwbbtoACwL6owta6eE6dOQ1URAGBv+B7cdj+kfACeKiMA4DKXucxlLnuyPGMAcNnejACAy1zmMpf9LzEALnsiIgLAs8Gqsm7tZkQEgCcLEbAWdveCAMDeFKgLfM6eNQtPBBEBYF8QYNVGiGIQAeCpEBEAXOYyl7nMZU+WiADgsr2JCAAuc5nLXPa/xAC47ImIAMCzoTBRZNu2nagqAE8F1sJEAVQBqGVV6cpmecPipdQFKUQEgMeDBfoHIYoAeKqMMQC4zGUuc5nLniwRAHDZ3owxALjMZS5z2f8SA+CyJ2KMAPBMqcKmTVupVKqICACqSiKOY6y1ADwRjAFgklXFF+GAtnbOnTOPwBgA/hUE8D1AAHiqjAEAl7nMZS5z2ZNljADgsr0ZAwAuc5nLXPa/xAC47IkIIAIAT5eq4vsemzbtwPM8rLVEUUwqFTB1Wjcnn3IMBx+yHM/zCKMIABEAUFUSeAYaGxBjsKokZjU08MoFizht+gzSngfAk0V3JwQ+qALwVAgCgMtc5jKXuezJEkAEAFxWSxAA/pdAP5d96GW858p+AFzmMpe5xAC47ImICAmAZyKOY6rVKisPWMxznnsib3zzS3jL217BS176PA46eCknnnQEb3nby3jxS57LQYcsY9q0Huob8gRBgO97ZHM5OpcvYWlzM8+dOYu3LF7Ki2bPpTWdJlZFRAB4MrAWDlgERgB4KowBEQBwmctc5jKXPVkiQgLg6UAABYD/NBQQAHi2GAMiAPCfwqO/5PQPXcseAODpmjZrLmt+8n0u6wOAf+XhH7+M03+8CoB94dFfcvo5v+RhAOCpePjHL+P0H68CgL5rec85v+RhAODfZc+Vn+L0H68C4NnCo7/k9HN+ycMAwKQ9V36K08/5JQ8DAM8EfdfynnM+xWV9APB46LuW95zzMt5zZT8AT8WeKz/F6ef8kocBgEk8+ktOP+dTXNYH9F3Le855Ge+5sh8Al/23MwAu+1d8IwA8XSICwIvOPZMzzjyOZcsX0NzcSCoVICIAgJDJZJg9ezqnnHI0L3nZWbzxTS/lHe96Ne9892t561teyis/8jaeu3QZS5tbqEulQISEAABPFpkUHLwUwghEAHiyfM8A4DKXucxlLnuqfCMAPB0opNIB1loA/lOsVVLpFCgAPFt8zwDwn7SnYwZnrvsVr/7xKgCeDmhn+Vtfwpls4Jr7+wH4b7Gnr4dTTruOD53zMt5zZT8Ak2AV3zvnZZx+zsvY2+nnfIrLHr2W95zzMk4/52U8nu89CgAceCxnXvUFTj/nlzwM0Hct7znnZZx+zst4Iqd/6Fr2ADz6S04/52XU+h5n8c751/GrK/sBmLRn8wY47QiWAwD/CXvuv5k1zOWUA9sBeLJgFef/ZAML33IWywGAfaHjZL7zj+9yyvXv5vQPXcseAMBl/60MgMv+Fc/zAHgmRASAMIyJY4sxBhEBYJKIoKqEYUQcxxhj8H2PhPE8NJel+oZziUUwxiAAwFOBVWhsgHwOjAHgyRIRfM8D4D+BR3/J6ee8jPdc2Q9ArT1XforTz3kZk773KADUou9a3nPOyzj9nJeROP3HqwB4sqCfyz70Mk4/52UkTj/nlzwMANTi0V9y+jkvY9J7ruwHYG8P//hlnH7Oy0icfs6nuKwPAJ4q+q7lPR+6lj0AQC1YxffOeRmnn/MyEqd/6Fr2AAB723Plpzj9nJcx6fQfrwIgAav43jkv4/RzXkat08/5JQ8DAC5zmcueDM/zEAGAp0pVmTNnBkEQEMcxqsq/WxxbgsBjyZK5qCoAzwYRwfc8AJ5te678FKef8zL25dVv+hWXA1z1BU4/52Xsy3uu7GfPlZ/i9HNexuM5/ZwvcDmw5ifv5vRzXsa+vOfKfgD+kzqXLuY5b/0TV37hJNb85Ptc1gcAtc78wp+48h9/otaV//gcz1l6Mt/5x5+48h9/4sovnAScxNf+8Seu/MefmPSupQDQ2XEy7/rHd3nn/Ov41ZX90HEy3/nHn7jyH3/i8fz2LXMBmAQn8bV//Ikrf/Y6FjKXOR3tHHziXNZc/xB7Hv0lp5/zMhIfugq46gucfs7LmHT6Ob/kYVbxvXNexunnvIxap5/zSx4GAP4V6OeyD72M0895GbVe/ZMNwAa+/6aXcfo5L+OJnH7Op7isDwD2XPkXLp//Oj56ejsAteiYwUIAIAHtPOdrf+LKr51MJwDgsv9WBsBl/4oIeEYAeKZEhATAvogIkwBqiSoyZxryqnMglwFrAXgqMAJ7BmFkFKwF4MkyRgD4T4B+LvvddQDsbc+Vn+LVP5nB1/7xJ678x5+48gsncfknXsb3HgWABH3X8p43/YrZX/gTV/7jT1z5j09w5lVf4PQfrwLgX4FVfO+cd/P9WZ/gyn/8icRv37KVD53zSx4GABJ7rvwUp39iK+/82Z+48h9/4sp/fILZP3k377myH4BJD//4ZXxo8+v47T/+xJX/+BO/fQt8/02f4rI+AHiy9lz5KU5/069YAwDUglV875wvsOkt3+XKf/yJK//xXd7Jr3j1h65lDwAw6eEfv4xX/2QGX/vHn7jyH38iceVbFwNQ68wv/Ikr//EnJl35j9ezHABwmctc9mSIgDECwFMlItTX53n7O17B8ScczqGHreDf6dBDV3D88Yfxpje9lLq6PCICwLPBGAHg34X5r+O3//gTV/7jTzxZV/7ju7xzPgB0nv45rvzHn6j1tdOA+a/jt//4E1f+40/8K985vR2AvUE/l33oZZx+zsuYdPonrgOu40PnvIzTz3kZk04/55c8DAA8FSx9PVf+43M8pwMAngro57LfXcfCt5zFcgBgEvRz2Ydexnuu7Afaec7X/sR3Tm8H4Onac//NrJl/LAd3QOfUGbDuZu7teD1X/uNP/PYtc2H+6/jtP/7Elf/4E5Ou/MfrWQ4AnPmFP3HlP/5E4sovnATAkwXtPOdrf+LKf/yJSb99y1xgLu/82Z+48h9/4l+58h+f4zkdQN+1fPknGzjzVSfTCTz845fxvUcBoBb3/5LTz3kZtU4/51Nc1gcALvtvZIaGhgFw2b/ieQaA/QkRsAqHLIfPvRsWzYE4AgABgFoIAGAtAIQhAAk2bAffB1UAnozAMwD8J/DoxXx/HQDUglWc/5MNnPmF17McAGDp6/naaXD5765lDwDw8F9/xZrTPsG7lgIALOZdXzgJrvoLl/UBwBPZc+VfuHz+6/jtWxcDkOg8/Z28c/51/OrKfgBgFef/ZANnfuFzPKcDAGAx7/rCSaz5ycU8DADQdy2/umou7/zAyXQCAJ2nv5N3zt/A9/+6CoB/hUd/yennvIxX/2QDAPuy58q/cPn81/HR09sBgHae84HXsXDdrzj/UQBI7LnyU3zoqpP42j9ez3IAwGUuc9m/k28MAE9XPp/j6KMP5uSTj+Lf6eRTjuLoYw6mqbkBgGdT4BkA/ldAP9s3A8CTBav43jkv4/RzXsaHrgKu+gKnn/My3nMlPOdrf+LKf/yJSVd+4STgJL72jz9x5T/+xKQr//F6lj/6S04/52U8ng9dBVz1BU4/52U8nu89CgCT9lz5KU4/52VMOv1D17KHfi770Ms4/Zx38/11sOYn7+b0c17GpPdc2Q/Aswn6uff6DZz5qpPpBFj6eq78x+d4TgfAKs7/yQYWnriCTgDgyeLRX3L6OS9jb6e/6VesYQPff9PLOP2cl3H6j1cBMAlWcf5PNsBpL+E5HQCQgFV875yX8b1HAWBfHv7rr1hz2id411Kg71p+ddVJcMvLOP2cl5E4/U2/Yg0b+P5PtvLOn/2JK//xJyZd+Y/P8ZwOAHDZfyPT0tIMgMv+Fd8ziAgA+xvWQhDA214Bn3oHnHUiLJgDLU3geyRoaoDF8+B5J8An3gaffRe877Vw1olw0BLYuBWAJ0tEEAGAfzfo57LfXceZX/gEZwIAk3j0Di7nJI5fCgCTlh9zEqy7mXv7AFZx41Vw5jGLAZjE0iM4kw1cc38/AI8H+rn3+g0wq4dOACAB7Rx84lzWXP8QewAevYPLmcucDgCYxNIjOJPruPFRANhz/82smX8sB3cAQALaOfjEuXDVHTwMADyRPTu2wvzX8dt//InfvmUuALWgn3uv38DCE1fQCQAk6FjBKfPh8ltWAQD93Hv9Bha+5SyWAwD7Qt8uNjGXOR0A4DKXueyZ8DyDiADwdIgIAP8pIgLAs0VEEAGAf4fO0z/HlV87mc6+a3nPOS/j9HNexr9y+jm/5GHaec7X/sR3Tm8HYF+Y1UMnAPCvwGLe9Y8/ceU//sTXTgNO+wRX/uNPfOf0dgCeLJa+niv/8Sf25covnARA4swv/Ikr//En9uVdSwGgFvNfx2//8Seu/MJJAExa+JbvcuU//sSkK//xXd45HwCebfQ9xDXrTuL4pQBQa8+Vf+FyYM1P3s3p57yMSaef80seBgAeD0tfz5X/+BN7u/Jnr2Mhc3nnz/7Elf/4E1e+dTEAk/Zc+RcuZy7vfOFiAJ4sHv0lH7rqJL721sUAPPzXX8FbzuJdb/0TV/7jTySu/McnOBM48wuf4zkdAOCy/xUGwGVPVuAbRASA/QkREgjQ2Q5nHAvvfCV8+h3w9Q+T4LPvgre/HJ5zPHS1Q1MjzJ8NZxwHb3oxvPg5YC2IAPCvpHyDiADw77bnyu/zfV7Hi5cCQK2Hb7kO5s+gEwCYRMcMFrKBjX3Ao3dwOXOZ0wEAk6CdOfNhzeZ+AJ4u1m1lDwDwRDbt6Af6uff6DTCrh04AYFLn1BnAVrb3AcAT6Tz9c1z5tZPpBAD2Rt9DXLMOZk9tB2AStDNtFrB5F3sA+h7imnVzOeXAdgBc5jKX/acEvkFEAHBNyjeICAD/bjCXd/7sT1z5jz/xeK78wkkAPBlc9QVOP+dlPJ7Tz/kUl/UBwL8brOJ7v5vBO08DTjsJfnctewCAZwr6uexDL+M9V/YD8Hign8s+9DJOP+dl7MvpH7qWPQDAvmy//2bWnHYEy/uu5T3nvIzTz3kZp/94FbCK83+yAZjLO3/2J678x59IXPmFkwD4d4BVnP+TDSx8yzt5TgcAPFl7dmwFruND57yM0895GR/a/Do+eno7AC77/8AAuOzJ8j2DAAD/DRABgDCGOAZjwPdJYAxEMVQjADACqhBGJDAGgCfDiGCMAPDvRt+1fPkn8M4PnEwnALA3ZvXQCQBMoqOH2QBAAmYwrQMAJkE702YBwBOBdqbNAjbvYg8AMGnP5g0AJOiYwUI2sLEPACbRt4tNAMCkhbPaAahFxwwWAgDPBpjLnA4AqNU5ay4ACfq2soYZTOtYxffOeRmnn/MyEqd/6Fr2AAAJ+rayhg18/00v4/RzXkbi9B+vAsBlLnPZ0+F7BgEAXGJEMEYA+F8C/WxcBwvf8l2u/Mef2Jcrv3ASAP8KrOJ757yM0895GZNO/8R1wHV86JyXcfo5LyNx+jm/5GEAYF8e/vEX4FUnMw2AI3jxiTdz/qMA8J+28C3f5cp//Ilav33LXACeyLQDj2XhVV/ge30n851//ImvnQYAe678C5tOO4mFAMATufwTL+P0c15G4vRPXAdAgkd/yekfupY9AMC/8vCPv8DlnMTrTm8H4KnoPP1zXPmPP3HlPz7BmczlnR84mU4AYBK0M2c+XP6Jl3H6OS9jb6f/eBUALvtvZIaGhgFw2ZPlewaA/xaIMAmgFiJMAkCESQBPlucJIgLAvxP0c9k3fgVveSfP6QCAp2rTjn4Angibd7EHAHg8y1/4Ohau+xWv/vEqABI8+kt+tXkuAAk6TuZ1p8Hln/gUl/UBAPRz2TduhvkA8K/ABjb2AcC/E+u2sgfYs2MrcB0fOucOjv/Hn7jyH3/iyn98gjPX/YpXf+ha9gAALH09V/7jT0y68mevY+FVX+D0D13LHgDAZS5z2VPlewYAl3ieICIA/CfABr7/ppdx+jkv4/Gc/onrAHgi9O1iEzB7ajsA+7Jnx1ZgBtM6AGAS9LN9M3DVFzj9nJfxnivbedc//sTXTgNO+wRX/uNP1LryH9/lnfOB045gOQCwNx79JR/a/DpevBQAEp2nvwQ+8UseBgD+F9BxMh99y1wu/9217AEAEns2z+B1L5wBbOD7b3oZp5/zMhKnf+I6AGqd+YU/ceU//kTiyi+cBEBiT8cMzlz3K758ZT8AT4RHf8mHrgLmz6AT2HPlpzj9nJcx6fRzvsDlwOWfeBmnn/MyJp1+zqe4rA8AEnuu/Aub3vJOntMBAHuu/BSnn/MyEqef826+vw5gLu/82Z+48h9/otaVb10MgMv+G5mWlmYAXPZk+b7BMwKACzwjBL4HwL/bwz9+N9/ndXz09HYAno7ZU9sBeCLM6qET2HPlpzj9nJdR63uPAgAdJ/Odn72OhVd9gdPPeRmJ0285gu+8agYAk5a/9U987bQNfP9NL+P0c17G6ed8Hz7wTk4BAP4VmMucDuDRX3L6OS+j1nuu7Afg2cD8GXQCAIkzv/B6lgMAsJh3feEkWPcrzn8UAPZGx8l852evY+G6X3H+owDgMpe57KnyfYNnBAAXeEYIfA+A/xSYyzt/9ieu/MefeDxXfuEkAJ4IfVtZw1zmdADAk0HftbznnJdx+jnv5vvrgNM+wZX/+BPfOb0dgOXHnARX3cHDAMAkHr2Y76+byztfuBiAvcEqvveJrbzzAyfTCQAkYDHv+gJ86MerAPhf0XngsSxcdzP39gFAYvlbX89yAObyzp/9iSv/8ScSV37hJACejM6Ok3nXF05izU++z2V9ALAvsIrvfeI6ACZ1nv45rvzHn5h05T8+wZnAmV/4E1f+409MuvIfn+M5HQBA37V8+fpj+ejp7QAkOk//HFf+409M+u1b5sL8Yzm4AwBc9r/CALjsqQp8DwEA/r8LfA+Afzce/SUfumou7/zAyXQCAI+HzbvYAwBMom8XmwCABGxlex8ATIJ+tm8GgETn6Z/jyn/8iVrvWgoACTpO5jv/+BNX/uNPJK5862L27NgK82fQCQAklr/1T1z5jz+RuPIfn+M5Hf1sXAezp7YDkFizuR+AWvRtZQ0AwNLXc+U//kSt75zeDsCTBRvY2AcAtfZs3gDAJDiJ45cCwCSWHsGZwKYd/QDsCx09zAY27egHwGUuc9nTEfgeAgD8fxf4HgD/bVj6eq78x+tZDgDsy54dW4EZTOsAgCeDjpP5zj/+xJX/+BNfOw0AarH0LN45/zp+dWU/AAno57LfXQenvYTndABALejnsg99gU1veSfP6QCAWix9PV/jC7znyn4A/pV7f/wyTv/EdSw8cQWdAEAC+tm4DmZPbQfg34mOHmYDAHuDDXz/TS/j9HNeRuL0T1wHwJPF0tfztdM2cM39/QDsy8M//gKXz38dX3vLXACeDljF9970K9as+xWvPudlnH7Oyzj9nE9xWR8ATOqcOgPWbWUPAACr+N45L+M9V/YD4LL/VgbAZU+VMYIfeAD8f5YKPIwRAP6doJ/LfncdsIHvv+llnH7Oy0icfs4XuBxY85N3c/o5n+KyPuicNRfWbWUPADCJvq2sYS5zOoCOGSxkAxv7AGAS9LNxHSyc1Q7AUwX93Hv9BhaeuIJOAGBfePQOLuckjl8K0M60WcDmXewBACbt2bEVmMG0DgB4JujoYTawaUc/AJOgn+2bgVk9dAKdU2cAW9neBwB7mz21HQCXucxl/y7GCH7gAfD/WSrwMEYA+E+CDXz/TS/j9HNexr9y+oeuZQ8AUAtWcf5PNrDwLWexHADYlz2bN8D8GXQCAP8KtPOcV53Emp98n8v6AODhH7+b7687ia+9dTEAtaCfyz70br4/6xN85/R2APZl+Vs/weyfvJvvPQoAT+Tgt/6JK//xJ75zejsAk+jbxSbmMqcDAP6VNT95N6ef8zJqvfonGwD4V+jbxSZmMK0DAGrBXN75sz9x5T/+ROLKL5wEwFOx/K1/4juntwOwN/qu5VdXncTXvnYynQDA00HfLjYBcBJf+8efuPIff+LKf3yO53QAwCQ6ZrCQ67jxUeDRX3L6OV/g8vmv46OntwPgsv9WBsBlT0fgGTzPAPD/ke8JvmcA+HeDdp7ztT9x5T/+RK0r//EJzgQWvuW7XPmPz/GcDug88FgWch03PgoAkx6+5TqYfywHdwAdKzhlPlx+yyoAJvHoHVzOXE45sB2Ap4pHL+b76+ZyyoHtAOwL9HPZ766D045gOQCw/JiTYN3N3NsHAAno597rN8BpR7AcAHgmYDHHnwZrrn+IPQBAgr6HuGYdnHnMYgBYegRnsoFr7u8HYBKP3sHlzGVOBwDsC4/eweXM5ZQD2wFwmctc9nQFnsHzBID/j3xP8D0DwH8azOWdP/sTV/7jTzyR375lLgD7sufKv3A5cznlwHYAnk0sfT1fO20D33/TL7nsyk/xoatO4mv/eD3LAYBJ0M9lH3o33+d1/PatiwF4PLCYd/3sdWz6xMv43qMAkKBvF5uYy5wOAHgi9G1lDRu45v5+AP6VhW/5Llf+40/s7cqvnUwnALA3uI4PnfMyTn/Tr+AtZ7EcAHgie3ZsBeDZQsfJfOcfr2c5APB00XEy3/nHn7jyH69nOQCwL3Ss4JT5cPknXsbpn7iOM7/wJ6782sl0AgAu+29lAFz2dKV8gzECwP8nRoRU4APw34aOk3ndaXD5J37JwwAAj/6SD101l3d+4GQ6AWjnOa86Ca76At97FABgFd/7xHUsfMs7eU4HADwR+q7le1f2A5Cg71re84nrWPiWd/KcDgBIPPzjX/IwAJB4+Mfv5vvrTuJrb10MQIKlZ/HO+Rv4/jeuZQ8AsOfK7/P9dSfxtbcuBuDZsPyFr2Phul/x5Sv7AYB+LvvGr1hz2id411IAgMW86wsnseYn3+eyPgCAVXzvE9fBaS/hOR0AsOfKX3JZHwAk6LuW93ziOjjtJTynAwBc5jKXPRMp38MYAeD/EyNCKvAB+F9E37V8+ScbWPiWd/KcDgDYF+hn+2ZgVg+dAMCTtfyt3+Wd86/j+z/ZwMK3nMVyAGASrOJ757yb7/M6fvu1k+kEAJ4IHSfznZ+9jk2feBmn/3gVAPRtZQ0zmNYBALUe/vGnuKyvned87U9853S47HfXsfC0k+An7+Y9V8JzvvYnvnN6OwC1oJ3nfO1PfOf0dgCeCjiJr/3jT1z5jz/xndPb4dFf8qGrgKv+wmV9ADDp4R+/jNPPeRmv/skGFr7lLJYDAInLP/EyTj/nZSRO/8R1AOwvsIrvnfMyTj/nZSTec2U/AJMe/vG7+f46gLm882d/4l1LAeDhH7+M0z90LXsAAJf9tzGlUhkAlz0dIkIm5WMEAP4/EBHSKR+A/1bL3/onvnbadXzonJdx+jkv4/RPbOWdP/scz+kAgARLX8+VXziJyz/xMk4/52Wcfs4X2PSW7/Kd09sBeDI2/eTdnH7Oy0ic/qabOeVnf+I7p7cDMAmu40PnvIzTz3kZiQ/xCa78x+tZDgAkoJ3nfO27vJNf8epzXsbp57yMV/9kBl/7x+tZDgA8G+g4me/87HXwk3dz+jkv4/Rz3s33Z32CK9+6GIBJLH09V35hBt9/08s4/ZyXcfo5X2DTW77LlW9dDEACtvL9N72M0895GYnT3/QrZn/hT1z51sUAuMxlLnumRIRMyscIAPx/ICKkUz4A/4tgFd97069YM/91fPT0dgAeD49ezPfXwZnHLAbgyaLvWt5zzrv5Pq/jt184iTU/eTenf+ha9gAAiYd//AUuP+0TXPm1k+kEAJ4MOk7mO//4BGde9QVO//EqHr7lOjjtCJYDAIk9V36K0895Gb+a9U6e0wEAD//43Xyf1/HRt76e7/zsdfCTd/OeK/sBeKaY1UMnAFAL+rnsd9ex8C3f5bdvge+/6VNcxsl85x+f4zkdsPytf+LKf/yJxHdObwdg0plf+BNX/uNPJK78wkkAPB76trIGAHg27bnyU5x+zss4/Zw7OP4ff+LKf/yJxHdOb4e+a3nPOS/j9HNexoeuOomv/eO7vHP+Br7/ppfxniv7gVXceBUsPHEFnQCAy/7bmFKpBIDLnol0yseIAPC/TERIBx4iALC/wWLe9Y8/8Z3T2wGotfytf+LKf/yJxJX/+BzP6QCAWix9PVf+409M+s7p7QA8GXSczHf+8Seu/MefSFz5j8/xnA4AqLX8rX/iyn/8iUlXvnUxAHuDdp7ztT9x5T/+ROLKf7ye5QDAU9V5+ue48msn0wkA1KLjZL7zjz9x5T/+ROLKty4GYG8sfT1X/uNPTPrO6e0ATOo8/XNc+Y8/UetdSwHAZS5z2bMpnfIxIgD8LxMR0oGHCADsL7CB77/pZZx+zst4Iq/+yQYAJsEqvnfOF7ick/ja106mEwBIwCq+d87LOP2clzHp9E9cx8K3fJd3LQWAfwVW8b1zXsbpb7qZU372J6782sl0Ln09V/7ju7yTX/Hqc17G6T9eBcDyt/6JK9+6GICnChbzrn/8iSvfCjdeBWcesxjo597rN8C6X/Ghn8zga//4E985vR1YxffOeRkfuuokvva1k+kE6DiZ7/zsdfCTd/OeK/sBeDqgn3uv3wDAvvDoxXyf1/HR09vpPP1zXPmzY7nmTS/j9HNexr6cfs4veZjFvOsff+JdSwEgwdLXc+U/Xs9yAIC+a3nPOS/j9HNeRuL0T1zHwre8k+d0AMC+7LnyU5x+zsuodfo5X+By4PJPvIzTz3kZtU4/51Pce+DnuPIff+LKf7ye5QAAj/6S0895Gae/6WZO+dmfuPIff+LKf7ye5bTznK/9id++ZS5rfvJuTj/nC1zOSbzu9HYAXPbfyLS0NAPgsmdCREilPEQA4H+RESGd8jBGAHCZy1zmMpf9p4kIqZSHCAD8LzIipFMexggA+xPM5Z0/+xNX/uNPPJHfvmUuAJMe/vEXuHz+6/jtP17PcgBgEizmXf/4E1f+40/U+s7p7QBMgn4u+9DLOP2cl/Ghq+byzhcuBvq57EMv4/Rz/sKcn/2JK//xOZ7TAQAJaOc5X/sTV/7ju7xz8xc4/ZxPcVkfADwT9AGnvY4XLwXoZ+M64LRPcOU/Xs9ygL5rec85X2DTW77Llf94PcsBgAQdJ/Odn70OfvJ9LusDgCdjz5Wf4vRzXkbi9HPezffXncTX3roYgL3t2bGVM191Mp0AAB0n851//Ikr//En9uXKf7ye5QDAE6HjZL7zjz9x5T/+xKTvnN4OwOPpPP1zXPmPP/FkXfmPz/GcDgCoxdLXc+U//sSV//gcz+kAgFqdp3+OK//xJxJX/uP1LAcAXPbfyAC47NlgRMikAowRAP6XGGNIp3yMCAAuc5nLXOay/cWIkEkFGCMA/C8xxpBO+RgRAPYnOk7mO//4HM/pAIAn0nn657jyayfTCQAsf+ufuPJrJ9MJADwd0M5zvvYnrvzHn7jyH5/jOR0A7Tzna3/iyn98jud0AMC+QDvP+dqfuPIfn+M5HQDweJa/9U9c+dbFADweOhbzrreeTCcAi3nXP/7ElW9dDECCjpP5zj/+xHdObwdgb3SczHf+8Tme09HOc772J75zejsAT6Tz9M9x5T/+xKQr//F6lgMALH09V/7j9SwHADpP/xzvWgoA+0vn6Z/jyq+dTCcA4DKX7c0AuOzZIgLplI/vCQD/C3zPkAoMIgDgMpe5zGUu299EIJ3y8T0B4H+B7xlSgUEEAFzmMpe57P8rA+CyZ5MAqcAnCDwA/psFgUcq8DAiALjMZS5zmcv+WwiQCnyCwAPgv1kQeKQCDyMCgMtc5jKX/X9mAFz27xB4hkzKx/MEgP8mnidk0z6BZwBwmctc5jKX/bcKPEMm5eN5AsB/E88TsmmfwDMAuMxlLnOZCwyAy/5djBHSgU8q8DBGANifjBFSviEd+IgIAC5zmctc5rL/dsYI6cAnFXgYIwDsT8YIKd+QDnxEBACXucxlLnOFAXDZv5vvGTIpn3TgIQDAf5IIBIFHOvDwfQ8Al7nMZS5z2f8a3zNkUj7pwEMAgP8kEQgCj3Tg4fseAC5zmctc5hoD4LL/FM8zZNMBqcDgGUEAgH8HEcHzDIFvyKR8As8gIgC4zGUuc5nL/pd5niGbDkgFBs8YBAD4dxARPM8Q+IZMyifwDCICgMtc5jKXucgAuOw/CQHf80infDKZgFRgMEYQERIAT4eIAGCMkAo8MmmfdOAR+B4iAoDLXOYyl7ns/wsEfM8jnfLIZAJSgcEYQUQAeKaMEVKBRybtkw48At9DRABwmctc5jKXGQCX7S8C+J5HJuWTSXmkAkMq8PB9g+cZREBEQAEgIQIJYwTPCIHvkQo8UoEhm/bJpHx8zyAAgMtc5jKXuez/OwF8zyOT8smkPNIpj1Tg4fsGzzOIAAAKAAkRSBgjeEYIfI9U4JFOeWTTPpmUj+8ZBABwmctc5jKX1TIALvtvICJ4xuB7hpTvkQ48sumAbNonlw3IZQIS2XRAIpPySad8At/gewbPGEQEAJe5zGUuc5mrRATPGHzPkPI90oFHNh2QywTksgG5TEAimw5IZFI+6ZRP4Bt8z+AZg4gA4DKXucxlLns8BsBlLnOZy1zmMpe5zGUuc5nLXPZ0GACXucxlLnOZy1zmMpe5zGUuc9nTYQBc5jKXucxlLnOZy1zmMpe5zGVPhwFwmctc5jKXucxlLnOZy1zmMpc9HQbAZS5zmctc5jKXucxlLnOZy1z2dBgAl7nMZS5zmctc5jKXucxlLnPZ02EAXOYyl7nMZS5zmctc5jKXucxlT4cBcJnLXOYyl7nMZS5zmctc5jKXPR0GwGUuc5nLXOYyl7nMZS5zmctc9nQYAJe5zGUuc5nLXOYyl7nMZS5z2dNhAFzmMpe5zGUuc5nLXOYyl7nMZU+HAXCZy1zmMpe5zGUuc5nLXOYylz0dBsBlLnOZy1zmMpe5zGUuc5nLXPZ0GACXucxlLnOZy1zmMpe5zGUuc9nTYQBc5jKXucxlLnOZy1zmMpe5zGVPhwFwmctc5jKXucxlLnOZy1zmMpc9HQbAZS5zmctc5jKXucxlLnOZy1z2dBhVBcBlLnOZy1zmMpe5zGUuc5nLXPZUGREBwGUuc5nLXOYyl7nMZS5zmctc9lQZAJe5zGUuc5nLXOYyl7nMZS5z2dNhAFzmMpe5zGUuc5nLXOYyl7nMZU+HAXCZy1zmMpe5zGUuc5nLXOYylz0dBsBlLnOZy1zmMpe5zGUuc5nLXPZ0GACXucxlLnOZy1zmMpe5zGUuc9nTYQBc5jKXucxlLnOZy1zmMpe5zGVPhwFwmctc5jKXucxlLnOZy1zmMpc9HQbAZS5zmctc5jKXucxlLnOZy1z2dBgAl7nMZS5zmctc5jKXucxlLnPZ02EAXOYyl7nMZS5zmctc5jKXucxlT4cBcJnLXOYyl7nMZS5zmctc5jKXPR0GwGUuc5nLXOYyl7nMZS5zmctc9nQYAJe5zGUuc5nLXOYyl7nMZS5z2dNhAFzmMpe5zGUuc5nLXOYyl7nMZU+HAXCZy1zmMpe5zGUuc5nLXOYylz0dBsBlLnOZy1zmMpe5zGUuc5nLXPZ0GACXucxlLnOZy1zmMpe5zGUuc9nTYQBc5jKXucxlLnOZy1zmMpe5zGVPhwFwmctc5jKXucxlLnOZy1zmMpc9HQbAZS5zmctc5jKXucxlLnOZy1z2dBgAl7nMZS5zmctc5jKXucxlLnPZ02EAXOYyl7nMZS5zmctc5jKXucxlT4cBcJnLXOYyl7nMZS5zmctc5jKXPR0GwGUuc5nLXOYyl7nMZS5zmctc9nQYAJe5zGUuc5nLXOYyl7nMZS5z2dNhhoaGAXCZy1zmMpe5zGUuc5nLXOYylz1VpqWlGQCXucxlLnOZy1zmMpe5zGUuc9lTZQBc5jKXucxlLnOZy1zmMpe5zGVPhwFwmctc5jKXucxlLnOZy1zmMpc9HWZoaBgAl7nMZS5zmctc5jKXucxlLnPZU2VaWpoBcJnLXOYyl7nMZS5zmctc5jKXPVUGwGUuc5nLXOYyl7nMZS5zmctc9nQYAJe5zGUuc5nLXOYyl7nMZS5z2dNhhoaGAXCZy1zmMpe5zGUuc5nLXOYylz1Vpq4uD4DLXOYyl7nMZS5zmctc5jKXueypMgAuc5nLXOYyl7nMZS5zmctc5rKnw0xMFABwmctc5jKXucxlLnOZy1zmMpc9VaauLg+Ay1zmMpe5zGUuc5nLXOYyl7nsqTIALnOZy1zmMpe5zGUuc5nLXOayp8MAuMxlLnOZy1zmMpe5zGUuc5nLng4zMVEAwGUuc5nLXOYyl7nMZS5zmctc9lSZuro8AC5zmctc5jKXucxlLnOZy1zmsqfKpFIpAFzmMpe5zGUuc5nLXOYyl7nMZU+VAXCZy1zmMpe5zGUuc5nLXOYylz0dBsBlLnOZy1zmMpe5zGUuc5nLXPZ0GACXucxlLnOZy1zmMpe5zGUuc9nTYQBc5jKXucxlLnOZy1zmMpe5zGVPhwFwmctc5jKXucxlLnOZy1zmMpc9HQbAZS5zmctc5jKXucxlLnOZy1z2dBgAl7nMZS5zmctc5jKXucxlLnPZ02EAXOYyl7nMZS5zmctc5jKXucxlT4cBcJnLXOYyl7nMZS5zmctc5jKXPR0GwGUuc5nLXOYyl7nMZS5zmctc9nQYAJe5zGUuc5nLXOYyl7nMZS5z2dNhAFzmMpe5zGUuc5nLXOYyl7nMZU+HAXCZy1zmMpe5zGUuc5nLXOYylz0dBsBlLnOZy1zmMpe5zGUuc5nLXPZ0GACXucxlLnOZy1zmMpe5zGUuc9nTYQBc5jKXucxlLnOZy1zmMpe5zGVPhwFwmctc5jKXucxlLnOZy1zmMpc9HQbAZS5zmctc5jKXucxlLnOZy1z2dBgAl7nMZS5zmctc5jKXucxlLnPZ02GstQC4zGUuc5nLXOYyl7nMZS5zmcueqv8D61YvnCiUK7AAAAAASUVORK5CYII=","media_type":"image/png","type":"base64"},"type":"image"}
```

## User

### Tool Result

```
{"source":{"data":"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V/2fM61gOoJskA2cAIAAAAMAgihQKnSbUGb8e2UmtpEEgG0kERH8b2QDGEn8R5tacrAcWY8NgK4Gm/OOWVcAMCCu+m8EgGybq6666qqrrrrqqquuuup/phxgWkJOXCGek7lMFboNiI6r/usYEFf9DweAbJurrrrqqquuuuqqq6666n+2toYcIRs4AUABUSA6KDOu+u9jAPMsElf9zwGAbJurrrrqqquuuuqqq6666n8RAwDiqquu+hcBULnqqquuuuqqq6666qqr/pcRV1111YsMgMpVV/1XygSbfxUJIrjqqquuuuqqq6666qqrrvo3AKBy1VX/FWyQIIJ/Mxskrrrqqquuuuqqq6666qqr/hUAqFx11X8FCfb24A/+AO66C9ZrXjhDFFjM4eGPgFd+ZSiFq6666qqrrrrqqquuuuqqfyUAamsN29gGwDZXXfWiMNAm8y9yQt/D058Bd9wFmaDCv8jAaoKnPh0e/FA4cwamCRBXXXXVVVddddVVV1111VXPn0FCAAgJkNAwDLZNZgJgm6uuelFkmvXQ+JcJbJjNYKMH8a+TwMERTA0kwFx11VVXXXXVVVddddVVVz1/AoEASUhCCjRNkwFsA2Cbq656UaTNMCQvMiek+TeJAImrrrrqqquuuuqqq6666qoXhSQAJCFBlQSAJABsc9VVL5I0Eki8iAIKV1111VVXXXXVVVddddVV/+kk8UwA1IjANldd9a8hCTARRgKbq6666qqrrrrqqquuuuqq/zEk8UxIogJI4n6SuOqqF4UEUiCBxFVXXXXVVVddddVVV1111f9EAFSuuurfSBISV1111VVXXXXVVVddddVV/5MBULnqqn8zc9VVV1111VVXXXXVVVdd9T8cAJWrrvo3E1ddddVVV1111VVXXXXVVf/DAVC56qqrrrrqqquuuuqqq6666qr/uwCo/Gewuep/IImrrrrqqquuuuqqq6666qr/ZwCo/EfJBBtKAYmr/odqDSJA4qqrrrrqqquuuuqqq6666v8BACr/XjbYEMGzrNcwDJAJElf9N7KhFOg6mM2gFC7LBAkk/rtJXHXVVVddddVVV1111VVXvUA2/x4AVP49MiECJHja0+AP/xD+4R/g4kXY24NxBImr/hvZMJvB9jacOgUv/uLw6q8ON93EZZkQwX81CWoJIoTEVVddddVVV1111VVXXXXV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XHXVVVddddVVV1111VVX/WsAEPwbSGIcJwAkIYn7SUKCaWpIQhKSAJBAEgeHSzINgCQAMs1qPTBOE9PUkIQkJPFABo6OVgDY5qqrrrrqqquuuuqqq6666qqrXggAKv9KR8s1e/uH/N3jb+XhD7mBzcWcKMHpkzscHq3Y2z+ilOAv//bJPOimazl96hi2ueb0ce665zybm3PuO7tLZvKoh9/Mk556B4982E201njyU+/kumtP8tSn38WjH3Ez953bZRgnHvvIW4gIAB73xGdw62338Aav/XL0XSXTRIirrrrqqquuuuqqq6666qqrrno+AAheRLYB+PvHP51zF/Z49MNvBmA9jPzd45/O4dGKe+67wNnzu6zWA4991IOYz3oOj5Y89da7ud/Tbr2bRzz0RiRx9vwldi8dAPCEp9zOHXef4467zrJaD1zaP2Jra8H21oLzF/cA+Psn3Mp6PfC6r/Ey/NXfPYXlak2EsLnqqquuuuqqq6666qqrrrrqqucHgOBfSRK2mVqjtUZmEhLjOFFroauV1WrgwsV91uNIpjlxbIv9gyU3XHeKaWocHq545MNu4o67znLD9acBeMRDb+T0yR1uvuEMJ45vs14PHB6uuOe+i2Sav33c0zh5fJvrrjnJufOXePmXeiRPePLt3Hn3OSSuuuqqq6666qqrrrrqqquuuur5ASD41xJ0XaXvKl1X6ftKRJA2D7rpWvq+46YbzgBwdLRi1necOL7F5sYcgIc86HqedtvdXNzdZz2M3HT9aQDuO7vLfed2SZtpauwdHHHx0gHzWc/mxpyIYG//iPMX93j6bffwl3/7ZCJEVws2V1111VVXXXXVVVddddVVV131/AAQ/CsJYZu0cZpMAyYiAHjqrXexmPdsb21QSgBQayFCAJw+ucNqPfCbv//XPOrhN3O/vu+48frTbG7MuffsRWZ9Ry2Fe89eZHNjzos/+sEcP7bJDdee4uVf+pEcHC658frTXHPmBGCuuuqqq6666qqrrrrqqquuuur5AKDyrzSMEzpaUyKYWqObGsvVQEg84cm3s7O1wd897unsHR5xzekTHNvZZD2M7O4dcHC44q57znN8Z5PXeOWX4Bm338t953a5/pqTRIhpagCkzTQ1WmvYZpoaXVeJCG6/6yznzl/ipV78YZw+eYzMJCK46qoHssH864S46qqrrrrqqquu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/50N66HxorKh74MS4qqrrrrqqquuuuqqq6666oWxQYJLl/Y5d36XWgu2yUx2dnbY2dkhM3kRAFD5TyCJf4+I4H6SALANQEQAIAnbAEjifplJRBAR3C8zkUSJ4PmRxP0kLosIXpCIACDTRIirrrrqqquuuuqqq6666qqr/scCoPK/hCSemySeW0Tw3CKC/wwR4qqrrrrqqquuuuqqq6666qr/0QCoXHXVVVddddVVV1111VVXXXXV/10AVK666n+JYWy0loirrrrqqquuuuqqq6666n8zG7ouqLXwXwCAylVX/S/RdwXXAuKqq6666qqrrrrqqquu+t/MIPFfBYDKVVf9LyJx1VVXXXXVVVddddVVV/1vJ/4rAVC56qqrrrrqqquuuuqqq6666qr/uwCoXHXVVVddddVVV1111VVXXXXV/10AVK666qqrrrrqqquuuuqqq6666v8uACpXXXXVVVddddVVV1111VVXXfV/FwCVq6666qqrrrrqqquuuuqqq676vwuAylVX/Z9ksPlXUXDVVVddddVVV1111VVX/Z8DQOWqq/7PMSCQ+NczIK666qqrrrrqqquuuuqq/zMAqFx11f8pBoTbAbm6BxBXmOdPgFF0xOIWQIABcdVVV1111VVXXXXVVVf9nwBA5aqr/s8wINrhU1k++fPx6i6MEWCeP3G/oJ56dRYP+0SIHjAg/iPZBgnxvNImJO5nG0k8P7aRxIvC5nkJxFVXXXXVVVddddVVV/2/AUBw1VX/VzgBGO/9WfLwKVA2UMwh5ijmKOYo5ijmKOYo5hBzKAsoM8azv8q0/w9cZvOisM3UGra5X6bJTB7IgCQEtNbITDITANuEBIBtACTxQJlJZtJaIgmA1pLMxDYviAQSSCCBBOKqq6666qqrrnpRmav+PzDPZvN82WAb29jmudlc9T8XAJWrrvo/xtMBRA8YMM9BwWVOnsUGFVAFT7yobCOJWgoAtgGIECBsIwkAAXfedTc33nA9pRQeSBK33X4np06eYHNzg6PlkgsXLnLTjTcAYJuI4H7nzl/g9KmTlBLczzaSeG73nrvE2fP7RAQAmckN153g5LFNDIirrrrqqquu+t/LNiDASALAADaS+JfYBgQYSTyQbQAkYRsASdgGhMRV/4cISJuQkMA2IMBIAkACEC+IxFX/cwEQXHXV/zUSkDwv4bbE0yEg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ddVVV1111VVX/Z8BQOWqq/7PMdggXnTmCgVXXXXVVVddddVVV1111f8pAFSuuur/HIHEv4q46qqrrrrqqquuuuqqq/5vAqBy1VVXXXXVVVddddVVV1111VX/dwFQueqqq6666qqrrrrqqquuuuqq/7sAqFx11VVXXXXVVVddddVVV1111f9dAFSuuuqqq6666qqrrrrqqquuuur/LgAqV1111VVXXXXVVVddddVVV131fxcAlauuuuqqq6666qqrrrrqqquu+r8LgMpVV/0vMYyNNiUSmKuuuuqqq6666qqrrrrqfyMBNtQu6GrhvwAAlauu+l+i7wp0hauuuuqqq6666qqrrrrqqn8FACpXXXXVVVddddVVV1111VVXXfV/FwCVq6666qqrrrrqqquuuuqqq676vwuAylVXXXXVVVddddVVV1111VVX/d8FQOWqq6666qqrrrrqqquuuuqqq/7vAqBy1VVXXXXVVVddddVVV1111VX/dwFQueqqq6666qqrrrrqqquuuuqq/7sAqFx11f9BNph/HQnEVVddddVVV1111VVXXfV/DACVq676PyYNIRD/ejZIXHXVVVddddVVV1111VX/dwBQueqq/0NsCMGFw+S2C8mLxNBVeNR1lRpgg8RVV1111VVXXXXVVVdd9X8DAJWrrvo/Ig0h+Ls7Jz7qhw6442LjRSXg9R7b8xXvuMWiEwbEv8w2AJLINBHihclMIoIHsg2AJF6QTBMhbAMgiUwjgSRekMwkIvj3so1tIoL72QZAEs/NNpL41zDgNBHiRWEbAEm8qGxjQ4QAyEwign8t2wBI4n62kcRVV1111VVXXXXVVf/jAFC56qr/I2xA8MN/uuIp901cuxO05FkksEECm2eRuOwX/nbg3V954lUf1pEJJXihbCOJ+0UIgMxEEpKwjQ0RAiAisE1mElEAIwmAzCQiyEwk8UARAkASANPUqLUA0FqjlMLzExHYIPF82caGCHE/A84kIgCwjSQk0VqjlEJmEhEAZCY2SICEMymlANBao5QCQGYiCQDbRAQPJEAhbCOJ+2UmNpdFCQTYRhIAtpHEv6S1RikFCaapUUoQEdgmM4kIJHE/29gmIngg20gCwDaSAJCEbSRx1VVXXXXVfz0bJJ7FNi+IJK666oFskLjMNpK4nwEBNoC5nyQeyAaJq/5nAiC46qr/Yw5W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wYYQ/yqSeFGFxANJopTCCyMJAInLIoIXRhLPT0TwryWJ50cSz00S4vmTxFVXXXXVVVddddVVV/03AEC2zVVX/RvYsB4aLyob+j4oIa666qqrrrrqqquuuuqqq/6tpmbGMZF4UQBQueqq/2MMZPKvJkGIq6666qqrrrrqqquuuur/FgAqV131f4yAElx11VVXXXXVVVddddVVVwEAULnqqquuuuqqq6666qqrrrrqqv+7AKhcddVVV1111VVXXXXVVVddddX/XQBUrrrqqquuuuqqq6666qqrrrrq/y4AKlddddVVV1111VVXXXXVVVdd9X8XAJWrrrrqqquuuuqqq6666qqrrvq/C4DKVVddddVVV1111VVXXXXVVVf93wVA5aqr/pcYhkZrCeKqq6666qqrrrrqqquu+t/MULugq4X/AgBUrrrqf4muK3Rd4aqrrrrqqquuuuqqq67630/ivwoAwVVXXXXVVVddddVVV1111VVX/Rez+a8CQOWqq6666qqrrrrqqquuuuqqq/7vAqBy1VVXXXXVVVddddVVV1111VX/dwFQueqqq6666qqrrrrqqquuuuqq/7sAqFx11VVXXXXVVVddddVVV1111f9dAFSuuuqqq6666qqrrrrqqquuuur/LgAqV1111VVXXXXVVVddddVVV131fxcAlauuuuqqq6666qqrrrrqqquu+r8LgMpVV/0/YRvbAEQEV1111VVXXXXVVVddddX/CwAEV131f5AEEkggAZgIUUpQSiCBMBJIIIHEf4jMxOaFso1tHigzeX4yE9vcLzNpLXkg29jm+UmbF0Wmsc0LkpnYkGlsA5CZ2OZ+aWOb+2Ua29zPNra5X2YCYBvbPJBtbP7dMo1tADKNbWxz1VVXXXXVVVddddX/GwBUrrrq/xgJsAEDwjaK4J577ubOO+6k6zoe/ehH089m4AQEGBS8qGyTmdyvlAKAbUoJADINgCQeyDYRAiDTSEKCUgKbZ7GNJEoJADJNhCglAMg0mYltuq4CMI4TkgAAESFKCIBMIwmJy2ywjSRaJrUEADZIkGkiBECmKSUAAAEwjhNdVwGYpkZEUEIAtJZkJl1XARjHCUnUWgBoLYkISglsiBAAmUYStokQL6pMEyGen1IEgA2liPu1lkQEV1111VVX/d9im+cmCQDbSMIGCWwDIImr/m+ReBabq64C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qqv+DzKCTLABAeaFUoDEVVddddVVV1111VVXXfV/DgCVq676v0oB4qqrrrrqqquuuuqqq676/w2AylVXXXXVVVddddVVV1111VVX/d8FQOWqq6666qqrrrrqqquuuuqqq/7vAqBy1VVXXXXVVVddddVVV1111VX/dwFQueqqq6666qqrrrrqqquuuuqq/7sAqFx11VVXXXXVVVddddVVV1111f9dAFSuuuqqq6666qqrrrrqqquuuuq/mvivAkBw1VVXXXXVVVddddVVV1111VX/1cx/FQAqV131v8QwTIxTQxJXXXXVVVddddVVV1111f9etun7St8V/gsAULnqqv8loohOhauuuuqqq6666qqrrrrqfzcDEeK/CACVq6666qqrrrrqqquuuuqqq676r2T+KwFQueqqq6666qqrrrrqqquuuuqq/0rivxIAlauuuuqqq6666qqrrrrqqquu+r8LgMpVV1111VVXXXXVVVddddVVV/3fBUDlqquuuuqqq6666qqrrrrqqqv+7wKgctVVV1111VVXXXXVVVddddVV/3cBULnqqv+LbLD5V5FA4qqrrrrqqquuuuqqq676PwWAylVX/V9jQ98h8a/iKaE1kLjqqquuuuqqq6666qqr/s8AoHLVVf+X2NB35G23k7ffxr9MgKF2lEc/Gu3swDiCxFVXXXXVVVddddVVV131fwIAlauu+r8iE+Y97a/+muVXfDl56RLY/IsksCkv/hJsfNIno+1taA0kXhjbAEjiBbENgCT+JbaxTUTwL7GNbSKC+9nGNhHBv8QGO4kIrrrqqquuuuqqq/4vSScAocAktgkVrvp/DYDKVVf9X2EjYPjt3yJ3L6ITJyGTyySweYEk2t/9Le0pT6F7hZfD0wQSL4ht+r5DgvV6JCKIEDbYJiSm1uj7DgnW65GIIELYYBtJ2ElrSWbS9z1dDQ6PVkgCICKQREQAIEFrSVcrpYjVekQStqm1UotYrUdKKUiQaWxTImiZANhQStDVymo9Iol/SYTINA8UITLNVVddddVVV/1PYpvnJgkA20jCNpKwDYAkrvq/IZ0sag/AlFAFBKynCWOEeG62eX4kAWAbSdhGEjZIXGYDGEnczwYwkrjqfwwAKldd9X/NMEIUaA0ywcbThOZzaA0yQeJZJCgFSgHMv8Q2Xddx2+13Mk0TD3/ogzg4XDGOA7VWuq7jcLlie3uL226/k2lqPPyht3BwuGIcB2qtdF3HarViNpuxmPcAXNo/5GlPewYv81KP5X5TM+M4slytwJBOtrc2OXf+AufOX+Cxj3kEq9XArO+5cHGXe+69l8c++lHsHRzgNF1X6fue/YND5vM5EaLW4NKlfZ769Ft5+Zd9KVpLAGxjm+cWErffdYHdvUMiAoCWyXWnj3HN6WO0TMRVV1111VVX/feQhG0MhETfVcyzCZhakpnUUphao5SCbWZ9BWAYG601SikAtNYopQDQWhIhrvqfz5hF7fm73T/id8/9OPcNt7FVjvNyJ96I1zz99hjTPCHEA/V9R4jnYGAcG7bpamVqjVoLmaaWYJwmJFFrAJBpbCNBKYUIMY4TV/2PAUDlqqv+r5EAgwSZaGODOH6c6YlPJI4fR5ubeBhAQhIeBjBg86LITErAD//oT3HzTTfw8Ic+iL/4q7/mz/78rzg6WnL7HXfxEi/2GN7/fd+dH/uJn+GGG67n4Q+9hT//y7/mz//irzg6WnL7HXfxUi/5YrzzO7wNf/cPj+f22+/kN37rd7nr7nt4n/d8Vw4ODui6jtd/3dciM/m+H/xRpmlitR54qzd/Y+47e47f/b0/5LM+/RORhAL+4XFP4Fd//bf4xI/7CH7gh36ccRw5d+48z7jtTl7h5V+at32rN+e6666hFnHX3ffwjd/8nXzz1385R0dLJNH1HX1XsXkWAVNrHB6teMRDrgODJMZp4s57LnLNqR3EVVddddVVV/3Xk4RtxnFi1ncYWK0HbrvzPkLifq0lp07usLW5YHfvgBPHttndO6DrKk95+lkAbrrhDDtbCw4OV7RMjm1vcHi0Jm22N+cMY8M2V/3PlU4Wteen7vxGfu/cT/J2N340Ny4eyf54np+7+5v4y4u/xkc+/BsR4oEk8Yzb72W1GogQ5goJbrr+DF1XOXfhEsd2Njl/cY/NxZzDoxVnTh9nGEYuXNzHNhsbc/quMk2NS3v7LFdrbrrhDLa56n8EAMpnf/ZnfzZXXfVv1Jr51yhFhMS/RaaxecFs1FXGP/gD8hm3ofkcHx5SH/tYNt/jPWAcKTfdxOxlXobZa7wGs1d8RXK1Iu+6C2qB9ZrutV6bcuMNMDWQeH4kUWvhV37ttym18Ld//wRe4eVehjd5w9fh1KlT3HnnXXzSx30YtVZ+6Vd+k66r/O3fP55XfIWX5Y3f4LU5efIkd919D5/wMR/Kxsacr/iab+ahD3kQ7/B2b8kbvv7rsFjMecQjHsZP/PTPs729xYs/9pG8yiu9HK/2Kq/Aa736K3Pp0j7f8/0/wj333seTnvxUfuVXf4OtzU1++md/iSc95alEKbz7u7wDr/6qr0Dteh73+CfyBZ/9yczmcwBKCY6OVvzSr/w6Z89d4G/+7h/4s7/4SxbzObfcciPT1JAEgARps3+w4trTxyglqDUoJbi0v+TE8S2uuuqqq6666r+aJKZp4u8f/3Tuuvc89569yA3XneLCxT3+7h+eRi3B3sER4zhxx93n6LoCwJ//9RMpJXjcE5/B/sER875DEpf2DmjN3H7XWZ78lDvo+8qf/uUTODhcMrVG33fUWrjqf6Z0sqg9f3fpj/i5u7+Jz3+xX+Sh249hu5ziuo2beY3Tb8MfX/glbl8+kZc++eoMbSQU3K9lUkuh7zu6rrK1Mefpz7ibE8e3ODxccuvt97C5MecJT76dm264hr/++6fSWmN7a4MnP+1ODo6WZEueeutd7B8ssc2pE9vUWpDEVS9YhIgQ/xZpyDQSLwoAKldd9X+RBJloY4PZK74iq1/6JWiN8UlPYvVLv8T8Ld8SZbL+rd8iTp8GmxdFZrKY9/zqb/wu+wcHvPZrvioGThw/BsA0Tdzvl3/1tzg8OuLRj3w4AMePHwNgmiYigjSEYDGfc999Z3nik57CarUmQly4cJFhPbC9tcXtd97D7/zuH7BYLNg/OODFHvMo3u5t3oI//bO/5GM+8oP54A//OF75lV6O7e1tfuO3fpf3eJe349LeAfNZR2uNWisAq/WaNk1sbGxw7vx5NjY2+PAPeV+WyxURQWuN9XpEEs/NhrTJNBJkGnPVVVddddVV/31sePhDb+LEsU1+6/f/hrvvvcB81tP3HQZKCRDP0vcdAPeevUgpwWI+42i5BsHxnS0u7R2wv3/E5uac+87t8vIv8yiWyzUHB0uuPXMCMFf9z/Y7Z3+Ut73xY1nUntU0Mq8dP/iML+URWy/HBz7ky/jyJ78ve8M+i7JJcwND11XOntvlumtOcvL4DuM0Mesrd917HhBpk2n+7nFPp+8rz7jjHk6d2Oa+c7vMZj3T1Lh4aZ9hPTJOje0zJxjGkXFqbJVCa4nEVf/9AKhcddX/VS3RzjbtzjvpX/qlGZ/wBFiv0cYGSBCB+h5sXlSSMDDre265+Sa+5/t/hA/6gPdimhoAkrjffD7j5htv4Ht+4Ef44Pd/b6ZxAkASxoS4zDbb21ucOH6M1XoNhs3NDfq+YxxHTp86yThN/MrP/hIf8aHvx8kTJ7jjzrs4dmyb9Xqg73sErNdr/uZv/54f/OGf4rVe69U4trMFQGYC8Fd/9bf88Z/+BYvFnOVyxc0338i3fsf3sbe3j22OHz/GO739W7O5uUFrDUk8i3hO4qqrrrrqqqv+29im1sLmxozzF/dZDyPbWxtsbcy5/rqTnDy2zcHhks3NBZsbc+azns2NOX3fMU2N+axnY2PGeh2ERFcrR0crrjlznK6rnDt/idVqwGlaJl3XMU0Tkrjqf55QMGRy7/pWHrr5ktx29FT+/MIvc838Qdy9ehrvcOPHUyIQcO/qVh6x9RI0T4CQxN7+EWfPXeL4sS2mqVFr4a57z3P9NSe54dpT3HjdKX7rD/6Grc0FfddRSuHaa09y4tg2T731Lh77iAfRMvnLv3ky21sL+r7j6bfdw5lTx2itAeKq/3YAVK666v+qEvjoiHbnnfSv9ErojjuIkydp58+DBJlg86+VCSdPnmBvb4+WyV//zd9z5vRpjh/bwjaIy06ePMGlvT2yJX/1N3/HmTOnOH5sC9s8UNd1vPRLvQSPfuRDGcaGbWZ95bd+50aWqxWLec+s7/nA93sPnvjkp/Ie7/LS/Mqv/xaPesTDWK/XbG5u8pd//fd87/f/CLPZjDd4/ddmZ2cbACEiAoBXfZVX5LVf81UAuOfec3zTt34Xr/Fqr8yLPeYRALSE1hqZiSSeg41tJJCE01x11VVXXXXVfxfbdF3lGXfcxxOefBsv8ZiHcHxngyc85Q7uvOsc09i4+97zHNvZYjbrsOHoaEXXVR76oOu4/c6zrNcjR8s1ktjcXDC15NK5XSICSVzc3aeUAINtkLjqfyZjegVb5QTn13fxYidegV+7915+/ez38QUv9kuEglVbAWK7nqQZQABgiAge86gHce2Z44xjI0I87CE3UGvBhnvOXuTgYMnmxpyd7Q0e/6RncOHiHqdf+hiZ5mnPuJuHPOg6rjlzgvm8p0ThxutPkWlAXPU/AgCVq676v0zCqxXe26PddRderVApSOLfQhLDMPASL/YoIoKf/8Vf5cM+6L05PFphwDYRgYGXeonHUErhF3/51/iwD3pvDo9WGEgbIe53tFzyXd/zg9x8840MwwDAbDbjj//kz3mt13gV7rrnLH/9t3/Pq73KK/HZn/+lzPqe3d1LPPIRD+fo6IjVasXDHvpg3uWd344//pM/58zpExwcrpj1lVIC2wBM08Q0TcznMy5c3OVv/+5xrFZruq7yYR/y/lx/7WmGoRERPJvAIIlZX9k/XFFLMJ91XHXVVVddddV/B9t0XeXsuV3+/gm38hqv/BLMZz17B0sEPOzBN5CZzPqOxaJn1vecPL7N4dGKo6MVT3naXYzTxHo9cMtN17JcDdhGQESwWMw4Olpx5vRxnOa+c7sAiKv+p7ITBbzciTfg5+/+Zl7s5Cvwbrd8Bm9x/YewXU9QOvi1276XE/21XLdxI8txTahgDILMpIQQUGtBwM7Wgr/4myfziIfexFOffiev9aovxf7BEbfefi/z+YxXeOlHcrhcs7kxY2Mx476zu9x0w2nuuOsse/tHvMnrvQLrYULiqv8ZAKhcddX/RTaXtcbwN39D9zIvQxw7Ri4W5IULeLVCsxm2Ef86kgCQ4Oy5c/zgj/wUL/eyL82jHvEQjh3bYRonBJgr7jt7jh/8kZ/m5V/upXnkwx/MsZ0dpmnifhuLBe/wdm/JYx/zSJbLFTZsbc45PDyiteSee+5lHCf+9M//ki/83E/jB3/kJ7jmzBluvuk6fvf3/4TZbMaxnS1WyxXnz18AwDZTM5f29pG4TBKSCIFtHvLgW/iSL/h0fvU3fpcv+fKv5T3f7R152Zd+cZarkQgBYJsIsR4mbrvrPMM4EREAZEskkTbiqquuuuqqq/4LGVomJ45t8fRn3M1qPfCgm69la3PB/uGSrqtIokRhPus4PFoxTY0brz/NQx98PU966h20qXHtNSf4hyc8g+PHtogSPPwhN3Jxd59hGDl9coe/fdzTCYkI0ZqRxFX/84QK62nitc68E3+++2t83RM+mvd40Gdzzfx6ltPAL93+vfzOuR/h4x/5XbQGUvBAO9sbPPXpd7G7d8g0NUoER8s1q/XA7XfeR9dV7r73PEfLNX1XyTRPeuqdrNYDx3Y2maZk99IBAKdO7HDqxA5/+bdP4cUe/WCu+h8DgMpVV/1fJECCTOL4ceqDH8xw553k7i6Lt3or+pd7OdZ/8AeoFP61bANw8eIlnvGM23nHt3trrr/uGn7hl3+Tv/rrv+WG668DQMDFi7s847Y7eKd3eBuuveYMv/DLv8Ff/tXfcsP113G//YMDvvt7f4hbbrmJYRiwYT6f8Sd/+he86qu8Iq/48i/Fy770iwMwNfNjP/GzvO97vSu/9pu/z6/++m/xXu/+Tjz9GXfwK7/2W7zua786AAcHB3zfD/4o99x7H+/2zm/P87NcrjDwhq/3mrz8y740+wcHTM1IPIeI4PprjrO7d0gphWxJ2tx43UlsI6666qqrrrrqv44kpta45vQJrr/mFC2TkJha4/yFPTYWMwQ85lEPItNkJiWCrc0FO9sbdF3l0Q+/BQmGYeLG609z+uQxjm1vIIlxnLj+ulMMY+Oa08c5cXyb1hJJXPU/lzFCfPTDv5kfuf1L+ZqnfDAAQmzXk3zcI7+TG+a3sGwDoQBAEtM08bAH38iF3T0yjWZctljMeNiDrwdEy8bRcs1DH3w9tRSm1rjv3C7Hj22xs73Jehi48fpTZJrtrQ1KCc5f2CMzKRGYq/4HAEC2zVVX/RvYsB4aLyob+j4oIf4txqmRaV6g1tBixtGXfhnj7/8u2tqG1qBWtLFB3ncf2tqie/jD8TAw3Xor2FwWgQ8O2PiMz6J7hZfDqwEieEFKKZw/f4HFYs7O9iZ7+4c8/glPZpomXuolX4zZbAbA+QsX2VjM2dne5NLeIU944pOYWuOlXuLFmM1m2ObcufOcPn2Kvis80NnzF+lqZWNjg3Gc6LrKpb09jo6W3HLzDbRmahEGxrHRdwWA1XokIui7wv1W6xFJAEhivR64uLvLdddewzRN9H1PCbEeRiTx3EoEEcJcIaBlkmmuuuqqq6666r+LbUCAkUStBUkA2EYIBNikobWGbSIEgCRKFMZpIiQM1BK0lhiotTBNDdtc9T+fMUHQ18Lu+iL3rG/lWD3N9Rs30xoMORAKnp+uVhDPoU0NA5KIEOPYAINEXws2TK0RIUBI0Fpim65Wpta46gUzUEtQS/BvMTUzjonEiwIA2TZXXfVvYMN6aLyobOj7oIT4txinRqZ5gVpDixlHX/KljH/4B2hnBzLBhtag6yATL5cQgWYzkMCGUvDuLhuf/hl0r/SKeDVABC+IbbquIzMZx5Gu6+hqADCMDdvY0HWVzGQcR7quo6sBwDA2bAPQdZVxnMhMHqjrOmzTMhFgm1oLEcEwjABkmgghicwEICIAyEzuFxE8kCRqLYzjBIBtACTx/BjA5oEkcdVVV1111VX/k9gA5vkTEs/DNpK4n20kAWAbSVz1v4lpTrro6SSaYcg1UiDEC2Kb5yaJ+9lGEvezDQiJy8wV4gobJK56IQzUEtQS/FtMzYxjIvGiAKBy1VX/V0gAlEc9ivG3fgNfMtg8mwGBBK3hYQAMCDLR9jZxzTUYQOKFkcQ4jgCUUshMlqsJAEUgQIJxHAEopZCZLFcTABHB/YZhRBIRwQNN04QkxBWSaC2ZpoYkAEoR94sIHigieEFsMwwjkgCQxAsjAImrrrrqqquu+p9MAhD/GpJ4IEncTxJX/W8jigrNE80GRKjwL5HECyOJB5LEA4nnJHHV/ywAVK666v+KCDw2+jd+E8ik3XorKgVsXiBxmdN0r/7qxIMfDOsRJP4lknigiOC5SeKBIoLnJonnRxLPjyT+I0jiqquuuuqqq6666v8iIUBcddUzASDb5qqr/g1sWA+NF5UNfR+UEP8W49TINP8iCXWFfwuvR5C46qqrrrrqqquuuuqqq/7zGKglqCX4t5iaGcdE4kUBQOWqq/6vsfFqAJt/FQkiuOqqq6666qqrrrrqqqv+TwGgctVV/xdFcNVVV1111VVXXXXVVVddBQBQueqqq6666qqrrrrqqquuuuqq/7sAqFx11VVXXXXVVVddddVVV1111f9dAFSuuuqqq6666qqrrrrqqquuuur/LgAqV1111VVXXXXVVVddddVVV131fxcAlauuuuqqq6666qqrrrrqqquu+i8k/ksBULnqqv8tDGkjrrrqqquuuuqqq6666qr/zWwowX8VACpXXfW/hG2cBgSYq6666qqrrrrqqquuuup/I2GMbf6LAFC56qr/Jfq+0nPVVVddddVVV1111VVXXfWvAkDlqquuuuqqq6666qqrrrrqqqv+7wKgctVVV1111VVXXXXVVVddddVV/3cBULnqqquuuuqqq6666qqrrrrqqv+7AKhcddVVV1111VVXXXXVVVddddX/XQBUrrrqqquuuuqqq6666qqrrrrq/y4AKlddddVVV1111VVXXXXVVVdd9X8XAJWrrvo/KA22eZEJQkJcddVVV1111VVXXXXVVf/HAFC56qr/Y2wIARL/WjZIXHXVVVddddVVV1111VX/dwBQueqq/0MMSPCkCwNPOL9GAAKb50sCG2ZFvNINC47PCzZIXHXVVVddddVVV1111VX/NwBQueqq/yPSEILfv+OID/vVu9hdNWz+RRLY8ArXL/iON72R4/OCAfGC2QZAEraxTUQAkJlEBPfLTCICANvYBkSEeCDb2CYi+JfYxjYRwb/ENraJCDITSUjifrZpmQhRSgCQmUQE98tMIoKrrrrqqquuuuqqq676XwiAylVX/R9hGyR++kl77K4a12xUmrlMQAIBGBBgni0Ef3r3kr87u+I1bt4kDUU8X7aRBEBrjVIKksg0CCICANtIIiIAsI0kJAFgG0lkJq0lXVeRxDhNhISBEoEknpskJGEbSbwgmUlEIInMJCJ4bpKopXA/20QEALaRRERgg8RVV1111VVX/Y+WNuLZDEhCgG0kYRskbAMQEvczIK4wIK76v8CAeMFsIwkA20jifraRhG0MiCsk8UC2kcRV/yMBEFx11f8xQzMhMaVpacY0yykJ4GhKpjTrZtLGNlOaZqghQLwwtpHEufMXuOvueyilcPHiLk968lOJECHx+Cc8mWmakMRqveYv//rvAJDEHXfexROe+GT+5u/+AUlkJhFB11UOD4/4q7/5e7paKaVQS0ESrSX7+wfs7e+ze2kPgAsXd3n8E5+MJGwDkJlcvLjLxYu7XNy9RGuNiGC5WvFXf/13RASPe8KTuLh7iWmaOHv2POv1mnvvO8t3f9+P8Ku//tsASOKP//QvODw8QhJ7e/v8xV/9LWBaa1x11VVXXXXV/0Q2l4WEJCQhiZAQV0jCNpIQEBIhAZCZAAjITAAEZJqr/ncbp4Z4/swVkrifJO5nG0kASCIkJCGJ5yaJq/7HAqBy1VX/x0hgQBKrKXmvlzjOrzztgAvLxvu/1Am+9a8v8uCdjseenvFTT97n+CzAYF50bWp8/hd/FZ/5qR9HLZVP+vTP53M+/RO4cHGXH/mxn+bLv/izqbVysH/Ad3zXD/Cwz/1Ujh3b4ff/8E8RUGrhwsVdXuc1X40/+KM/5e577uM3fuv3uOee+3jv93gn9g8OqbXwZm/8+gB8z/f/KNM0sVqvecs3e0Puufcsv/27f8jnfMYnYJvM5NM/+4t52EMfzMbGgr39A+699z7e413fgdvvuItf/fXf5mVe+iX4/h/8cd7w9V+bm2+6gT/6kz/naU9/BpJ43/d6F0oUAO64826+47t+gK/6ss/jm771u3nrt3wTfvwnf46Xe5mXpJTCVVddddVVV/1PYZthnJj1HRKs1yN33nOOEoEBCaapcc2ZE2xvLti9dMDxY1tc2juk7zuecfu9ADzo5mtZzHvWw0ibGhsbc9bDCIbZrOOq/72edttZ9g+WPOph1zOfdTw3AcvVmsPDFadPHePi7j6X9g+54dpTdF1FEnsHR2xvbnD+4iUu7R1RS5A2N91wmq5WAFpLLuzuc+bUMa76HwmA8tmf/dmfzVVX/Ru1Zv41ShEh8Z/BhpD4pace8MQLAxs1WE5JX8SbPmyLg9G862OP85jTM97sYduUgAurxoVVo4ZYTuZtH7nNg471GAjxPCQxtcbO9hYv9ZIvzl/9zd9z6623MZv1/OIv/zqPe8KTeMyjH8nd997Hiz32UQzDyC/80q/x1Kc9g3PnLzCb9Tz+CU/m5ptv5I477uZlXvol+IZv/k4e86hH8J7v9o682Zu8PjvHdnjxF3s0P/FTv8D29iaPfMTDeMVXeBle+ZVejld/1VfiaLnie77vR7j3vrM89WnP4Cd/+hd47dd8NX7yZ36Bhz/soVxz5hQRwV//7T9w/sJFzp+/yPHjO8znc370J3+Ww8NDaq2cOnmCO++8m3vvPcuZ06d40pOfymMf8yh+83d+n0c8/KFcd+01fM03fDulBH/5139HOrn99jt5yINvISK46qqrrrrqqv9O09T4m394Knffe4G77jnPjdef5sLuPn/990+l7zv2D44Yxok77jpH31cw/NGfP55SCn//hFvZOzhka3NOLYWLu/s4zTPuuI8nPOV25rOeP/7zx7F3sCRtFrOeUgpX/e/y5Kffy3xWqbVwYfeA0ye3sY0kAGwjidvvOEuphX944jM4OFyxd7Dk4qUD7ju7y2ze8wd/8g9M08Rd95zn0v4RU2vcdsd93HLjNXS1Mk2Nvf0j/v4JT+fG68/QWlJKcNV/rjRkGokXBQCVq676P6rZbPXBky8OPHV35JWuX/Btf3ORV7txwW/fdsjQTBcizYvMNrUUHvf4J/L3//AE3vHt34of+8mf433f6134y7/6Wx7+sIdw39lzOA3AxsaCm2++kU/9pI/i9/7gT/iRH/9puq7jEQ97CEdHSwBmsxlPv/U2AFarNRHBxsacg4MDdra3ueee+/jV3/gdFvMZB4dHvORLPJZ3fPu35I/+5C/4uI/6YD7kIz+RcRzZWMx5uZd5Cc6eO894buS93v0defwTngzApUt7vPiLPZrXe+1X5/Vf97W49prT3Hf2PJtbG1yna3jJl3gs585fAODbv+sH+MLP/VR+9ud/hXd+h7fm+uuu5Q/+6M94xMMeSqkFSVx11VVXXXXV/wSPeeQtLOYzfveP/pZ77rvAxmLOfN5jm1ICSQAIWMx7SgnuO3eRritsbS64tHeEBKdO7rB3cMT+wZKdrQ3Onr/Eq7/Si3N4tOLS/hG1Vq763+XJt95L31c2Fj2X9i/x6IddD4AkAGwjiYu7B9x2531sbMw5OFhyy6POUGvh4GDJwdGKaWrsbG9w4vg2klitByKCa8+cYP9gyd89/lb6rtJacni44slPvYO9gyO2tzZ4sUc9CNtI4qr/dgBUrrrq/yABBroQH/0Kp/iTu5ZkmvWU/N3ZFX9xz4rXfdAmqymxucK8yB70oJv5+m/+Lh720Adz99338ud/8TfcedfdbG1t8ff/8HhuufkmAL7je36Ixz/hyXzvD/wo7/KOb8OpUyf4u797HJJ40pOfCkBrjeuvv5YH3XITy9UabLa2Ntne3mI9DFx33TV0tfKLv/KbfOxHfRCnT53kz/7irzl+bIf1eqDvelprHD9+jMPDI377d/+Qu+66h1f6mA/lHx73RCQhCYBLl/b5iZ/+ed7kjV6P3//DP2E2m1Ei+Ou/+XtuuukGfu03foenPu1WDg4OKaXwTm//VgD83h/8MS/9Ui/O/Wwjiauuuuqqq67671JrodbC7t4Bq/XI8WNbzGc9N1x3ilMnjrG/f8jW1gbbWwtms57FYkbXVaapMZ/1bCxmdLUgiVoLBwdLrrvmBH1XuffsLnsHR2Qz09SIELaRxFX/c9kgwe13X2DWV7Y2Ztxxz0Ve7JE3UiIwIK6QBMCJ41tsbMw5dWKHYRh52jPuQYJpSm656Qz3nr3I3v4R99x3gcOjFRHBse0NnnH3Oa49c4JSgpd4zIM5Wg6s1gOPfdSDOHtulzvvOc9V/6MAEFx11f9BBqrEhWXjZ5+8z5lFoRne7GHbvNpNm7zzY4/xitcvMCBxhfgXSaK1xubGBi/9ki/G1Bqv9Aovw5Of8jR+/bd+j1/85V/nD//4zzlz5hR/8Zd/w9/93eP49m/6Sv7yr/6WX/uN3+Exj3oEf/v3j+Prv/k7+bAPeh8Auq7jUY94GI965MN58cc+ihd77KN4+MMewrXXnGG9HgBQwId90PvwV3/9d1x/3bXcffe9PPIRD2UYBrqu0vc9rSXf94M/RkRw7bVn+Pbv+gHuvOtuWmv0s57HPf6J/Mmf/QWv+eqvws/+wq/wsIc+mBd/7KMYhpHlcsVXfs03c+b0Kd7zXd+Bvb19rr/uGn70J36Wb/q27+Fv/+5x/PhP/hw//KM/zaVLe0jCNlddddVVV13138E2AHfcdZY//+sn8XIv9Qjms54nPfUObr/jLHffe54nPe0OnnrrXVzcPeDoaMXh0Yq+qzz8oTciidVq5NLeEbt7hwjRWnL3vRe4465ztGzs7h5weLRC3E9c9T+bBDacv3jAMDbuvu8Sj33EjZQIMo14XsMwsVqtmabG4dGKjcWMjcWc2azjzrvP8+iH38yDbroGSWxvbTCOEy3NbNZx8sQ2h4dL/vrvn8YTnnwbFy7u83ePfzpPeMrt2Oaq/1EAqFx11f9hJaALIcGv33rIcjIf+rIn+bWnH3DPwcRqMsG/jiQApqkxjiOv/qqvxCMe/lCe+OSn8tmf/vG0TDYWC37vD/6YV3nll2dnZ5tXfeVX4MTx4+xe2uOnf+6X+YWf+gH+8E/+nGvOnKbrKt/z/T/KQx58C8MwYGA+m/Gnf/5XvN7rvib33XeOv/zrv+fVX/WV+JTP+HwW8zm7u5d4zKMewcHhESAOj45YLObs7Gxzy003MJvPuOee+zh9+iQnjh/nL/7qb3n0ox7Bq73qK3HdtWfY2d7i7/7+8Zw6eQJJ3HnX3fR9z0u/1IvzB3/0p5RSeMyjH8G58xc5d+48f/M3f89jH/NI1sPIbNYDIImrrrrqqquu+q9mG0mcv7DH3z7u6bzea74MfVdZrQe6rvKoR9xMZnJxd8b21oLFfMaZU8c4OlpxeLTiiU++nXGaWC7XPPwhN3C0XJM2CPqusrExZ3//iOuuPUmmuefe8wAgrvpfQILrrzkOgoc/6BruFyEeyICA9TBw8vg2y9Wavq8cLdeM4wRA33ecu3CJS/uHZJpMc9MNp8k0B4dL7ju3y6u/8ksQEqv1QKZ5icc8BIBhGAGQxFX/IwBQueqq/6sEU0KzefiJninNo0/1fP4fnuXXbz1gXsTxeWGrD2zAvEhsLjt77hz33HMfAF/05V/Hp37iRzGbzXign//FX+P6667lV379tzl16iQP+du/54Pe/z35mZ//ZW6//U4+7IPfl65W3v1d3p7HPuaRDMOADbNZz+7uJaZp4tz585QI/vKv/o6v/vLP59u/+we4/rprOX36FL/9O3/AyZPH+fO/+GsedMtNPPxhD+EZz7idX/uN32Exn/NB7/+e/NXf/D13330vEcGs79k/OCQieMe3e0se/8Qnc3B4yLu809vy+V/0lQBMU6Pve2668QZuuvEGdncv8Xt/8Cc89jGP4qqrrrrqqqv+pzBw5tQxnvK0O1mtRx7yoOvYWMzY2z+i6yoAkqi1cGn/kDYlD7rpGh75sJt43BNvY2qNa86c4G/+/qmcOLZFLYVHPuwmzp6/xKqvnDqxw1/+7ZPpagUAGySu+p9rasl95y4RIYS4854L2CBB2pw6sc3GvAdAXLGYz7jh+tM87onP4EE3XkuphRPHNnny0+5kPYxsbS7Y3tpg1nes1yN7+0fceP1prjl9nL6rhATAODZaJgA29H3HVf+jAFC56qr/YwQImJq5frPyoJ2Oew8bn/0a13Bx2Xj0yZ6Xu/Y0tYinXBj447uOmFeBAMQLk2lKCf74T/6CF3vMo7j3vrN83hd+JY9+5MP567/5e37/D/8E2+xsb3H99dfxFm/2hjz4wbfweq/zGpw8eYI3foPXAeBbv+P7eNhDH8xDHnwLZ89f4Ht/4Ed50C03sR4GAOazGX/2F3/Na77Gq/DYl34JvujzPo37zfqeD3y/9+B3fu8P+fXf+j3e893egfMXLvLyL/fSnDl9iuPHjjG1xlu+2RvxB3/0p/zKr/027/h2b8ldd9/D45/wJN7p7d+K5XLFN33b93DDddcyDANf9bXfwv7BIQDGZCb3Ozg8ZBgGAFprlFK46qqrrrrqqv8ukjBw+uQOp0/u8EDnL+5xbHsTBC/14g8jM8lmogR9V9neWiCJxz7qFpAYhpGHPOh6Tp/c4dixLQQcP7bJLTddwzg1brr+DCdObAMgiav+Z7PN1JI2JkIASFyWaTKT51Zr4eTxbV71FV4MMDaUEjzyYTchic2NOS/2qAfRWtJ1lfudPL5NrYX7bW7OedmXfDgAElf9zwOAbJurrvo3sGE9NF5UNvR9UEL8Z2hpSoiP+vW7+dkn77MzC2ZFVIl7jyZe/MyceRVdiDQUwfll47a9kb6Ii6vG9775TbzGzRs0QxEv0MWLu5w4cRyAv/uHxxMK1sOaEgUDErzYYx9NLYXnlplEBPfbvXSJ48eO8dyOjpZEBPP5jGmaqLWyf3DAej1w+tRJnp9pmqi18vzcc+99HB0teehDHsTu7iWOHz/GA13cvcSJ48e4tLdP33cs5nMAptbY29vn5InjXHXVVVddddX/FAawuZ8kXhS2kcRVV93PBolnsY0kAGwjCYC0EVdI4qr/XlMz45hIvCgAqFx11f8xNgjoQ6ybWducmBeedGFNmmexoQuY1aAI0qbZXGFAvCAnThzHNpJ4iRd7DC9Ia42IIDORREQQEbTWACilcPzYMWyTaSQus83GxoL71VqxzfbWFttbYBuAzCQisEGCWiu2yUwiAoDMBOC6a68BwDbHjx/DhswGQERw4vgxbHNsZ5sHqqVw8sRxrrrqqquuuup/EgFIPJANYJ6XQCBAEg9kG0nczwaJy2wjiav+97DNCyKJ50fiOUjC5jJJ3C8krvpfC4DKVVf9XyEB8ArXL/jxJ+xx9qhhDICBQDy3dTMHY9IMZzYqD9rpABDihbGNJAAyk+clIkQpBYBSCg9USuF+tpFEKeKBbCOJ+0kCwDaSACilACDxLJIopXC/UgoAtjEQEraRRCmF+9lGEraRxAPZRhJXXXXVVVdd9T+ZBCD+NSTxQBLPIomr/neRxH8Eiav+bwGgctVV/0cUcdm7v9hxQvD482uqhPmXJfBmD9vmIcd7bAjxQknifhHBv4cknh9JPD+S+LeQhLhCEs9NEgCSeG6SuOqqq6666qqrrrrqqv+lAJBtc9VV/wY2rIfGi8qGvg9KiP+pDIirrrrqqquuuuqqq6666n+yqZlxTCReFABUrrrq/6BmY/OvEhIhrrrqqquuuuqqq6666qr/WwCoXHXV/0FFAnHVVVddddVVV1111VVXXQVA5aqrrrrqqquuuuqqq6666qqr/u8CoHLVVVddddVVV1111VVXXXXVVf93AVC56qqrrrrqqquuuuqqq6666qr/uwCoXHXVVVddddVVV1111VVXXXXV/10AVK666qqrrrrqqquuuuqqq6666v8uACpXXfW/hG2uuuqqq6666qqrrrrqqv8rhMR/BQAqV131v8Q4Ja0l4qqrrrrqqquuuuqqq67638xAV4NaC/8FAKhcddX/En1XoCtcddVVV1111VVXXXXVVVf9KwBQueqqq6666qqrrrrqqquuuuqq/7sAqFx11VVXXXXVVVddddVVV1111f9dAFSuuuqqq6666qqrrrrqqquuuur/LgAqV1111VVXXXXVVVddddVVV131fxcAlauuuuqqq6666qqrrrrqqquu+r8LgMpVV1111VVXXXXVVVddddVVV/3fBUDlqqv+D0onafOvURRI4qqrrrrqqquuuuqqq676PwWAylVX/R9jm1AQ4l/NNpK46qqrrrrqqquuuuqqq/7PAKBy1VX/hxgjib+976n83dmnIv4FAhtmteO1bn4ZzmwcxzaSuOqqq6666qqrrrrqqqv+TwCgctVV/0ekk1Dw67f+Ge/3i1/E3voQY/4lQtjm5a9/ND/y1p/HqcUxbCOJf0lmEhE8t8wkInhumYkkJPGC2EYSV1111VVXXXXVVVddddV/CAAqV131f4RtEPzUE3+X/eGI6zZP0dwACAW2Meb5CQV/fs8T+Nv7nsLrPOjlSExBvCC2aS2ptZBp7EQStimlEBHYprUGEiHRMulqBWAcJ2otSKJl4jQAESIimKYGAgGlFK666qqrrrrqqhdN2txPgA2SkMA2krANErYBCIn72SCBAQwSV/0fYEC8YLaRhA1gJHE/20jCNgYEGAiJB7KNJK76HwmA4Kqr/o8wV4w5EhJTTkzZADgYjli3AYC0ARjbxNAmpmw0JzUqIP4ltpFErYW//Ou/JUKUUogISins7R/wh3/8Z0ii1kothYigq5XHPf5JHB0t6bqKJFomJYJaC7UWfulXfoNf+43fodZCLYVSCgCZZvfSHufOXeDoaMn+/gHnzp3n0t4eV1111VVXXXUV2AYgJEIiJCQRISQuk4RtJCEgJEICIDMBkKBlIkCCTHPV/27DOCGeP5vLJAEggSTulzaSAJBESEgiJJ6bJK76HwuAylVX/R8jBAYEsrjn8DyvfMOLc9/hRZ5+6S4Wdc7RtOK6zZPUKByNayqAzb/ENpK49Rm384d/9Gf8xM/8Ai/54o/hdV7r1Zn1PY97/BPZ2z/gFV/hZXjcE57EX/7V31JK4dGPegTnzp3ne77/R3j5l31pHvrQB/F6r/MabG5s8Lu//8c87enPYHt7i8c9/on81d/8PXt7+xwcHnH8+A5v9eZvzNd/83dw11338IiHP5Q/+/O/Ymdnm0c/6hE87vFP5NGPegTv/z7vRmYSEVx11VVXXXXV/xeZyXoYWcxnSGK1HrjjrrOUKBgjiXFqXHfmBDvbG1zY3efk8W0uXjpg1nc8/bZ7AHjILdexsZixWg9MU2Nrc8FqNQAwn/dc9b/XU59xH/uHKx710OtYzHuemwRHyxWHhyvOnD7OuQuX2N075KbrTtP1lRLB7t4hx7Y3OHv+Epf2Dqm10FryoJuuoesqAK0l5y7sce2Z41z1PxIAlauu+j/GPJNBEm/3qNfhrR7xGtjmx5/4W3RRefiJm1jUGX9059/zx3f9PbPSYf5lkgB48INu5mu/4dv4yi/5HH76536Jo+WS3/39P+JlX/oleJ/3ehcAhmHg8U94Ej/64z/Dm77xG3D23HlWqzXnzp/nd3//j/mLv/pbPufTP4Ef+fGf4ZVf8WV55Vd8WRaLOa/6yq/AzTfdyDAMlBIAHB4e8hEf+n7ceMP11Fp4zKMfySu+/MvwhCc9hZ//hV/lqquuuuqqq/6/GafG3/7D00gn2cwrvdyj2ds/4glPvp2bbjjDOE30XeXes7uExDhO/PFfPJ5HPeJmnnbr3Rw/tsWZU8cQcNsd93Hi+Bb3nr3IvWd3ecwjbuZvH/d0Tp3c4bprTnDdNSeZ9R1X/e/ypKfdw9bGDEncdtd5HvXQ67GNJABsI4m7773A5saCP/rzx7OxmDFOE0eHK9LmQTdfyx/+6T/woJuvZf/giNaSjcWcs+d3ufH6U3RdZRhG9g6OeNyTnsHxY5vYZtZ3SOKq/zEAqFx11f9RklhPAy92+iHcfXCekHiVG1+cjW7OLz7lj/iDO/+WoY2cmO+QTl4UtpHE3z/uCfR9zzd+y3dxz71nefQjH87R0ZJbbr6J1hp7+wcA/OzP/wpf+oWfxb33neXO37ybtGmZlBI86hEPA+CmG6/nHx7/RF79VV+J6645w8/8/K/wOZ/xCXzV130LL/+yLwXANDW+/bt+gJd5qZfg9//gT3jCk57Cvfed5Y/+5M+55sxprrrqqquuuur/Gwle/NEPZjbr+N0/+jvuvvcCmxtzFosZtilRAAEgwebGnFoL585fYtZ37GxvcP7CHgiuPX2Cg8Mlh4crju9scv7iPq/1qi/JweGSS3uH9F3lqv9dnvi0e9hY9PSzysW9Ix7ziBsAkASAbSRxcXef2+64j82NOUfLFbfcdA21BIeHK45Wa7Ilx49tcfrkMSLEajVSSnDm1DH29o/428c9nfmsp7XkaLnmac+4m729IzY357z4ox+MbSRx1X87AIKrrvq/SDBl4/TGMe7YP8s1G8e5dvMkf3nPE7lm4wSPOf1g3u8l35y3fuRrMrQRSfxrdLXjXd7xbTg8OuJ1XutVecTDH8owjvzIj/8MH/ChH8f3fP+P8Gd//lc8+lGP4Cu/5ptYLVe86zu9LQ+65SZe6RVelrd96zfjVV/5FQAYx5Hrr7uWn/zpX+ChD3kQu7u7jOPE3/ztP/DgB90CQCnBS77Ei/Gar/7K3HTTDTzy4Q/jtV79VXjxxz4a21x11VVXXXXV/ze1FGazjvMX91itB06e2GZne4PrrznJjdefZntrwXVnTvCIh97IfD5jPu/pu8o0NWazjsWs5+TxbU6d2KGUYLUeufaaE1x/7SnGaeLi7j7r9cg4TkjCNlf9z2Zz2W13XWBj0bOYd9x93yUe+4gbCIkHkgTAiePbbG0uOH3qGLO+49bb7uGpt97NHXefY3Njzt33XWD30gF33nOOCxf3Wa3X1BLcc99FWia1FB77qAfx8IfcwPGdTR7ziFt4yC3XMU2Nq/5HASC46qr/iwxFwd76iCdeeAb7w5KLq33u2D/LXfvnGNrIy173KJ54/jaKgn+tjY05f/6Xf8Pe/gFPfPJTuXBxl+M7O3zMR34QD37QzXzoB743knjrt3wTHvqQB/MPj38SP/vzv8Idd9zFH/7Rn3HX3fdy7vwFAI6Oljz20Y/k7nvuZWtrEyR+7hd/hQc/6GZuvukGACIK1117huPHj3H82A6nT59kZ2eba685A+aqq6666qqr/l+xDcAz7riPv/77p/KKL/MoZn3HE558O7fdeR933n2OJz31Dp789Ds5f2GPo6MVB4dLuq7yiIfeCMByPXD+4h7nLuwhiWzJXfec57Y77yNbcmn/kOVqjSQAQFz1P5sENpzf3We1Hrn33B4v9ogbkUSmeX7Ww8hyNTBOjcOjFRuLGRuLGbO+cufd53ixRz2IhzzoOkoE21sbTFMj08xmHSePbXO0XPF3j3saT3zq7VzY3efvn3ArT3raHVz1Pw4Alauu+r9KIrPxSje8GOnk9MZxbt65hqNpxe/c9lccm23ypAu3sz3bwLxobCOJu+6+l2lqvPEbvA63PuN2uq6yHgaO7WwzjiMXdy/x8i/30nzsJ34mj3nUI3i5l3lJnnH7ndSucuL4Ma679gz33nsfAK01Xvs1X5Wf/Olf4AlPegrv8x7vzA/+yE/y+q/7mtzvvvvO8tM/+0s8+SlP48//8m94+q23ceHCRf7sL/6aRzzsoQCYq6666qqrrvq/zzaSOHv+Ev/whFt5vdd4GUoJlquBxaLnsY96ENmSS3uHHN/ZYj7vOXPqGMvlmsOjFY9/8m1MU7JaDTzq4TdxeLQmM0Ewn/dsLubs7h1yw7WnaC25697zACCu+l9AgpuuOwnAmZPb3C9CPJABAcMwcvLENoeHK2Z9x9FyzThNAPRdx71nL3Lh4j4ALZObrj9Npjk4WHLfuV1e45VfAoDVeiTzqbz4ox8MwDhNAEjiqv8RAKhcddX/RYKWjWOzLXZX+xSCp126i5e99pGcXV5iysY1GyeY1x4MiBeNBMDF3Uu84su/NL/4K7/Bq73KK/At3/a9vOzLvCSlFC5d2mO9WnPtNWfo+54nPOkpvOEbvDbv9s5vS993vPiLPZqHP/TBlFJ43OOfxObGBl3X8VIv+WJcuLDLa7zaK/EVX/PNXH/9tazXAxd3d9nb2+fbvukrkcQwjDzi4Q/htV7jVXnVV34FfupnfwkAcdVVV1111VX/f0SIa685wVOefifrYeTBt1xH33XsHxzRdRXbpE2EuLi7T8vkwbdcx6MedhP/8MRnME2Nkyd2uP3Op/KQB11PVwu33HgN953bZTbrOHF8m7/4mycx6zsAsEHiqv+5ppbcc98uEUISt991HgMC0ubMyW02FjMAxBWL+YybbjjN4554Gw+6+VpKCU4e3+aJT72D9Xrk2M4mJ49v03WVYZzYP1hy4/Wnue7ak8xmHfcbx4lMA2Cbrlau+h8FgMpVV/1fZOiict/RRf7krsfxvi/55nzH3/wcDzp2HU+9eCerNvBHd/09kjDmRRUSmeaN3+B1+Nmf/xV2trd57dd8NYZh5OVe5iUBeN3Xfg1uuOE6fvXXf4eXfqkX463f4k34/h/6cX7nd/+I7e0tfud3/5Bf+pXf5Jozp3jGbXfwWq/5qvzYT/4ctVae/JSn8Wd/8dc85tGP4Ld+5w940pOfxqVLe7zKK78CkgAYxpHVeuDP/uKv+dGf+Fle/mVekquuuuqqq676/0ISBk6d2OHUiR0e6OKlA7quAOJlX/IRtEwyk4ig7ypbmwsAHvvIWwAYholHPPQmThzf4vixLQScOrnDg2+5jmlqPOjm6zh+bBMASVz1P5wNwDglEs8mgY3N86i1cHxni1d5+cdgwDYlgkc97CYigsV8xqMfeQvZkloL9zt5fJtSgvttbc55uZd8BACSuOp/HABk21x11b+BDeuh8aKyoe+DEuI/w5SNGoUP/KUv4Sef+NucmG+TNulkbzhiq18wZaNGIRCrNrCoMwBKFC4u9/jxt/0CXvuWl6U5KQpekMwkIgCwQeIy20gC4NKlPY4d2+GFubS3z7GdbV6Yo+WSjcWC+x0eHlFr5a6772G5XPGYRz8CSVx11VVXXXXV/ycGbCOukMSLwjaSuOqqF8Q2kgCwjSQAMo3EZZK46r/X1Mw4JhIvCgAqV131f5AkalSaG5XCNRsnSCeqYIMxs9qTTgBqFNJmyuRFERFkJrYppZCZSEISmYkkjh3bwQY7sc3zc2xnG4DWEjD3s0Hiso3Fggfa3NwA4CEPvoWrrrrqqquu+v9KgCQeyADm+ZK4TBIPZIPEs9ggcZkNElf9L2KbF0QSLwpJ3E8S94sQV/2vBUDlqqv+j5AEwKvf/JL88ON+jXuPLmCbf4mAyckNW6d52IkbABDiXxIR3C8iuF9EAGAbSUjBC2IbSZQSvCC2kcT9bCMJ2xgIiauuuuqqq666CgQg/lUknoPEs0hc9b+MJK666vkAoHLVVf9HFAUA7/nib0IXlX8493S6KNj8ixLz5g9/NR5y7AbSJiT+vSTxL5HEv0QSDyQJAEmIq6666qqrrrrqqquuuupfAIBsm6uu+jewYT00XlQ29H1QQvxPZRtJXHXVVVddddVVV1111VX/c03NjGMi8aIAoHLVVf8HNSe2+dcIiVBw1VVXXXXVVVddddVVV/2fAkDlqqv+DyoKEFddddVVV1111VVXXXXVVQBUrrrqqquuuuqqq6666qqrrrrq/y4AKlddddVVV1111VVXXXXVVVdd9X8XAJWrrrrqqquuuuqqq6666qqrrvq/C4DKVVddddVVV1111VVXXXXVVVf93wVA5aqrrrrqqquuuuqqq6666qqr/u8CoHLVVf9L2MYGBJirrrrqqquuuuqqq6666n8jAQYJJPFfAIDKVVf9LzFNydQSCTBXXXXVVVddddVVV1111f9GAhu6WqhV/BcAoHLVVf9LdF2h6wpXXXXVVVddddVVV1111VX/CgBUrrrqqquuuuqqq6666qqrrrrq/y4AKlddddVVV1111VVXXXXVVVdd9X8XAJWrrrrqqquuuuqqq6666qqrrvq/C4DKVVddddVVV1111VVXXXXVVVf93wVA5aqrrrrqqquuuuqqq6666qqr/u8CoHLVVVddddVVV1111VVXXXXVVf93AVC56qr/g9JJYv41igIhrrrqqquuuuqqq6666qr/UwCoXHXV/zHGhILgX88YIa666qqrrrrqqquuuuqq/zMAqFx11f8hBoT4i/NP5u8uPg0QV5jnRwhjZqXnda57Ka5bnMQ2krjqqquuuuqqq6666qqr/k8AoHLVVf9HpJNQ8Kt3/Tkf/Edfw9G0wjbmhZOEbV7y5EP5odf8NM7Mj2GMEP8Sm8sknodtJPHcbEAgns0GBOJ5ZSaSkMQD2QaExGW2yTSlBM/NNmlTInhuBjCAkYRtbBMR3M82tokIbJ6LkURmIglJ2AYEABhJPDfbSMI2rSURIiK46qqrrrrqqquuuuqq/2AAVK666v8IYwB+/vY/ZtnWXLs4QXNyP/Fs5jkVgr+7+HT+YfdWXvu6lyJtisS/ROKyzCQiAGitUUpBEgCtNUopAGQmEcEDZSYRwf3SJlsigW1qrQBM04QkAEopSOKBJFGKeKDMpLVG13UUiXGciBA2lBJIQgACEACSkERmEhHYRhKSsI0knpMAiAjuJ4lnEwC2aS0BU2tFEgCSqLUAYBtJXHXVVVddddW/l20k8UA22EbiMhskkMT9bJDA5jKJq/4PsEHiBbKNJGwAI4n72UYStjEgwEBIPJBtJHHV/0gAVK666v+YyUkgpmw0J/dLJ5MbRYUuCmlzP8tUBUK8qIZxZPfiJaIEp0+dBCAzKaWwXC45f2GXE8ePsbm5QWYSEUQEZ8+dB+DM6VMARARnz51HwOnTpwiJqIX7/eVf/S0v9ZIvTq2V+y2XK5arFZnJfD6jROHue+7lyU95Gi/9Ui/OyRPH6bqOiCAiOHvuPE9+ytN41Vd+BR5omiYuXNzFNq01brj+Os6ePc9d99zLS73EYwGQxIWLuzzjtjt4mZd6ce47e47WGrPZjL7v2N8/4PrrruUv/upvuebMaa45c5pLl/ZYbMw5PDii1MKxnR36vqPWAsDUGvfdd5aNxYL9g0N+8Zd/nUc98mG89mu+GraRxFVXXXXVVVf9S2yDhHhONkjiuUkgiftJXGbAaSKEBC2TEgFApokQV/3vZJv1MDGfdTw/NkggCQAJQNwvbUICQBLiCvG8JHHV/1gAVK666v8YAeZ5LeqME/02B9OSc6tLLMoMY+5n/mW2kcRqteYDP+zjePEXewyHh4dcd+01fMgHvjcRwS/96m/yR3/8Z9xy803cfsedvNzLvBRv+eZvBMD3/eCPcdvtd7K9vcUrvNxL8yqv9PJ8z/f/CHfceTdbW5u8xqu+EidPnuDXfuN32NzcYNb3/MCP/CSv+WqvzM0338jupUvcdOMN7O5e4o/+5M85trNN3/dkJov5nD/84z/jdV/71THwER/yfvzKr/0W99x7lt/9/T9ib2+fZ9x2B4eHRwC8w9u9BZL4iZ/6eVpLhnHgjV7/dXjq027lT//8r3jEwx4CgsV8zh//yZ/zu3/wx9x04/X87M//CgC3PuN2nvSUp/IGr/tafMD7vjt/9Md/xqu+yiuyWMz52m/8du68625e+zVflSc/5elkJp/6iR/FD/7IT7KYz7nxxut58lOexu6lPf7m7/6BT/yYD+PMmVNcddVVV1111b+GJABsIwkAAxI89da7ufnGM/RdxTaS2Ns/4hl33EuJAKBlcs3p41x75gQKsVoNrMeRY9ubHB2tQGJjMeOq/72ecut9HC7XPOIh17K5mPFANkhweLji4GjJtWdOcO/Zi+zuHXLT9aeZzTpqKVzY3ef4zhb3nbvIpb1Daq1M08RDbrmevq8AtJbcd36X6685yVX/IwFQueqq/2PMswkxeuL07Bhf8QofzF+efzI3bpzm5+/4Y37z7r9io85Im3+t1hrL5YoP+YD3Ynt7izd763fjvd7jnfj7f3gC3/9DP873fNvXUmsF4D3e98O45sxpNjc3+Llf+FV+9Ae+DQDb/NGf/Dm//Ku/xQ997zcDcHBwSGby6q/6Spy/cAEhFvM5p0+f5Lprr+HBD7qZW266kXvuu49hGLn2mtOs1mse9/gnsbGx4JprzjCbzbh4cReAX/613+Id3+4tedd3ehuGcWRvb5/jx3b43C/8Sv7+H57Iq73KK/AhH/je3O+22+/kN3/n9zk4OOSbv+17uP2Ou3jPd3sHfu8P/oSnPf0Z/NEf/znv/z7vBsAf/+lf8IM/ss8HvO+78w+PeyJ/9hd/zW2338knf8JH8t7v8U787M//Cu/17u/E7bffyff94I/z13/799xz71lKCRB0XeUt3+yN2N7aZLFY8BM/9fN85Id9ALaRxFVXXXXVVVf9Sy5eOuBv/+FpvORjH8KJ49u0lpQS3H7nWZ7w5NtYrwce+6gHkWlKEXffe56uVh7x0BtoLbn37C5333uBM6eO84w77mX/4Ih7z17kUQ+7mb9/wq2cOrHDNWeOc8N1p5j1HVf97/KEp97Nse0FpQR33n2RRz70OmwjiQe6+74LbG0u+MM/+we2tzaYpsZTb72b1hoPuula/vDP/oEH3Xwth4crWiYbixlnz1/ilpuuJdMMw8jewRGPf9JtHN/ZwjbzWU+EuOp/DAAqV131f1zazEpHOvnYP/sm3ufhb8wb3fDy/NIdf8pmnQPmX0sSknjyU5/O+fMXeNmXfgk2Fgu+47t+gPd5j3em1srR0ZKNjQXv/R7vzA/92E/xaZ/40dz6jNv567/5e176pV4cSZw6eZJbn3Ebf/23/8BLv+SLsbW1CcBv/s7v8w+PeyIv9zIvyeu81qsxm814xm238/f/8AS+8HM/lXMXLvCXf/23RASv85qvRmuNra1NWmscP7bDhYu7APRdx5/9+V9z1933MgwDEcF8NuPsufMcP77DM267g1/6ld+g6zqGYeDlX+6leYe3eQv+8q//jo/40Pfjgz7843mZl34Jdna2+c3f/j3e8s3fiIODQ7a2Njk6WmIbgFtuuYlbbr6Rl33pl+TkieN81dd+C6/ySi/Per3mN3/n91GI+XzOIx/+UNbDwMu+zEvyjNvu4I//5M+JUvi13/ht3vHt35qrrrrqqquuelEN48TR0YpSgmlqAJQS3Hv2Is+4417e7A1eiX94wq08/bZ7eMgt1wHQdZW7771Ad3vFTnYvHdL3lQixWg0cLdecOLbN7t4Br/PqL8XBwZLdvUP6rnLV/y5PeOrdbG/NqbWwWh/ymIffCIAkAGwjiYu7+9x2x31sbs5ZrgZuufEaai0cHq5YDyO2OXF8m2tOH+d87LFaD0QEp07scGnvgFtvv5fNjTmtJavVwG133MulvUMWixkv8ZiHYBtJXPXfDoDgqqv+jwvEmI3tboPPf5n35d0f+nr84NN+k75UjPm3kMR6GPiVX/stfu8P/oSt7S0Axqlx4sRxAPpZD8CpUye56657uOaa03zx5386H/JRn8TnfMFXMI4jj3zEQ/mCz/lUPuQjPpHP/cKvYBgGAO655z5ss729xYNuuYmTJ46zsbHg7LnzAIzDyMu+1Evwpm/0elzc3eVBN9/EM267g5MnT/C3f/84rrv2GgC6ruOlX/LFeJVXejle6RVelld4uZfmVV755bn+umtZLlc86JabOHniOH/2F3/NG7zea/GIhz+Eu++5lxMnjnF0dETXdQDcdfc9/M7v/RE/+CM/yTiOAEhCEgDbW5tcf9217O3v8y3f/r28yiu9PG/4+q/FD//YTwPwsR/5wRweHjG1ia4Wfvf3/ojlcsWtt93Bb//OH9D3Pb//h3/CweEhEYFtrrrqqquuuuqF6bvKjdefZnNjjs1lT7v1bu68+xyv8DKP4i/+5sk86hG3sLd/xJOfdicAmebMqWM86KZruPnGa7j+2pNgLluu1lx35gTXXXuSYZg4e+4SR6s162FEEra56n82m8ueced5tjfn9LVw77lLPPYRNyLxHCQBcOL4NttbC86cOsas67jtjvt42jPu4c57zjOf99x93wV2Lx1w+11nubC7z2o1UEpw79mLGKi18KiH3cxDbrmOnZ1NHvXwm7nlpmtpLbnqfxQAgquu+j/OmJBYtYE/Pvs4fvb2P+LtH/waFBVs/k0yk43Fgvd813fgcz/zE/mbv/0H/vwv/prFYs7e3j4Aw3oAYH9/n+PHjwHwuq/96vz6L/wYT33a0/nkT/98AF73tV+dX//FH+PJT3kan/Tpnw9A11We+rRb+bO/+Gv+7M//mr/4y7/hr/767xjHEYDFYs5f/e3f8+u/+bsslyv29veRxBu9/mvzKq/0CtRauLS3z2zWs3Nsh5MnjnPyxHFOHD/GyRPHWSzmjOMIwHK15iM+9P34lV/7bY4fO8Y9997HIx/xMI6OVmxtbvKXf/23/PhP/Tw33HA97/KOb8OJE8cBkITNZX/253/Fb/7277O3d8BrvsarcOHCRX74x36anZ0duq7j7LnzlFK4dGmf8xd3ufGG63nt13xVXvkVX46bb7qBN3/TN+SlXuKxzGczACRx1VVXXXXVVS+MbQAyTRTRMkmbl33JR9B3HUfLFdi81Is9lFKCcWoMw8h953a5/a6z3H7nWe6+9wLGAGSaO+4+xzNuv5fMZP9wyWo1EBIAIK76n00CGy7sHnBwtObshQMe+4gbAcg0z896PbJcDUxT43C5Yr6YsbGY0feVu+45z4s/+sE89MHX09XCzvYGU0ucZjbrOL6zxWo18A9PvJUnP/1Odnf3+YcnPoOn3noXV/2PA0Dlqqv+HygK1m3gt+/5G+48Osd7PvwN6KOSGPFvk04UAUDXdYzTxDu+3Vvybd/5fbzOa70aGxsLAH7gh36Cd3jbtwDgaLlkc3ODz/zUj+NjPvEzAVguV2xubvCZn/pxfOTHfRoAfddTSqGWQqmFUgulFEopANx73zke+fCH8uAH3cJf/NXf8Iov/zL8zM/9MgcHh+xe2uMt3/yNOLazzaVL+/ziL/06f/t3j2MYBzDM5zMe9/gn8rZv9WY8/dbb+Nu/fxyv/qqvyE/97C9y4sQxLu3t82KPfRS7u3scHS152Zd+SWazGb/527+PJMZxousqfd+BDcBjH/MoXuHlX4Y3fePX4/DwiL/9h8fzCR/9oaSTn//FX+MXfunXefmXfUn+8I//jGmaePmPeClOnTzBYx/zSP727x/HDddfy1VXXXXVVVf9W0zTxDQ1SgQPf8gNALRsRARpA/DQB10PwL3ndnn4Q25gZ2uDWd+xmPfcd24XADCbG3MWixkXdw+48fpTTFPjrrvPA4C46n8BCW654RQSnDy+xf0ixAMZEDCMIydPbLN/cMR81rNarRnHBkDXVe665zznzl9CEq0lN91wmkyzf7DkvrMXefVXenEA1sNIa8mLPepBAEytASCJq/5HAKBy1VX/xwkx5sRmXfBJL/FOPGjzWr7/qb/Osq3ZrHPS5l8rSpAt+ZEf/2kwPOLhD+GVX/HlkMTTnv4MPu2zvoiXePHH8KQnPZXXfq1X4w1f/7W5+557+eEf+2muOXOav/jLv+VTPuEjubh7ie/6nh/i2mvP8Jd/9bd80sd9BABPu/UZvNhjHsXrv95rsbt7ia5WjpZLvvU7vp977r2P9XrNMIz82m/+Do98+EN57dd8NV7sMY/i+IljfMqnfz6nT53gTd/49elnPW/1Fm/Mox75cFarFcZsb23xjNvuYBgGbHPi+DGe8rRb+c5v+Sq+/Ku+kUc84mFsbmzwW7/9+xw7vgPAU5/6dC5d2gNgahPjOHL77XfRz3oANjc32Nne4sKFi9Su4/Spkxw/fgwJrrnmNGfPnufGG6/nkz/+IzBmY7GgteTvH/cEhmEAoLVGKYWrrrrqqquuepFIANx4/Wm2NhcAZJoIIYkIIfEsz7jjXo5tb3LzDWcAOHf+Enfdc56TJ7YB6PuOh9xyHffcd5HFvOfY9hZ//tdPZGMxAwAbJK76n2uakrvuu4gkJNi74xzGSMJprjm9w+ZiBoC4YmMx45Ybr+HxT3oGD7r5WmoJThzf5olPuZ31MHLq5A57+0fUWhinicPDFTdef5obrz/NYjHjfsMwYRsA29RSuOp/FABk21x11b+BDeuh8aKyoe+DEuI/Q3OjqPBhf/y1/NRtf8CJfovmBEDAidk2p2fH2B+PeNrBPWyUHnNFUXBxOOCHXvPTeK3rXpLmpCh4YXZ3L3Hu/AUk8bCHPhiATBMh7r3vLOcvXOTUiRNce+0ZMpOI4OLuJe6++x7OnDnNmdOnALi4e4m7776H06dPcc2Z0/zBH/0pf/CHf8rx48eYz2dkmvtduHiR7a0t3v1d3p7FYs4f/vGfce99Z3mbt3xT/uCP/pTbbr+T3/it3+MzP/XjuOXmG/mkT/s85vM5N95wHeM4YmDW9/zu7/8RH/fRH8JLv+SLc7/1MPB5X/iVfManfAx/+Md/zh/+8Z/x3u/xzmQm3/9DP84bv8Hr8DIv/RKcP3+Bn/n5X+Hi7iXe9I1ej8c8+hE86clP5Yu//Ov4zE/9OPb29vmyr/pGXvs1XxVJ/N0/PJ5HP/LhfND7vycP9FM/84s86SlP403e6HV5yRd/LLaRxFVXXXXVVVf9Rxinia5W7jeME31Xud/h0YphnDhxbAuAqTUwHByt2N6cY8P+wZKdnQ1KBFf9zzdNjXvP75FpJAABICBtTh3fYmPR84LYxjYRwdFyTUjM5z22yTSlBPfLNBHifraZWtLVwlX/NaZmxjGReFEAINvmqqv+DWxYD40XlQ19H5QQ/xmaG0WFD/3jr+Wnb/sDTvRbNCf3G7Mx5USJYF560uZ+RcHucMAPvdan8ZrXviTNSVHworINgCRaa5RSuF9rjVIKtpHE/TITSUjifq01Sim8KFprjNMEhvl8xt1338vF3V1uuP46jh8/hm1WqzWLxZzn1loj03RdZZomaq0cHh1hm63NTV4Q20jiuV24uMvyaMmNN17PMIzUWokQ91uuVizmczKT+0UE97ONJK666qqrrrrqX8s2krjqqv8otpEEgG0kAZBpJC6TxFX/vaZmxjGReFEAULnqqv+DhKhRUIr7dbUCMwCMCZ6tRqE5mTJ5UdnmfpK4XykF29yvlAKAJGxzv4gAwDb3K6WQmWQaiedhgwSlFEoplFIAsM3111/L9ddfC4BtJLFYzMlMbHM/A7UUSuGyWisAmxsbANjGNpmmlACgtSRCRAS2yUxsKCWQxMkTx+HEcWzo+w6AqTUERASL+RyAiOB+mUmmiRARwVVXXXXVVVf9W0ji38qAeMFskLjqfxHbvCCSeFFI4n6SuF+EuOp/LQAqV131f4YAeK1rX5Ifv/V3uXd5EWMwL5hAiMmNWzav4eE7NwAgxL9EEi+IJJ4fSTw3STxQRBDBv4okbHM/SQDYJiJ4braRxHOzjSQkEcGz1Fq4nyRKKTyQDWAkAWBDLYX72SDxHCKCCK666qqrrrrqv4144SSu+l9GEldd9XwAULnqqv8jigKAd3rIazMrHU+4dDs1CrZ5YQQk8MY3vjy3bF5D2oTE/zaSeG6SeH4k8fxI4t9CAhD3k3gOElddddVVV1111VVXXfXfBQDZNldd9W9gw3povKhs6PughPifyhghrrrqqquuuuqqq6666qr/uaZmxjGReFEAULnqqv+DmhPb/GuEgpC46qqrrrrqqquuuuqqq/5PAaBy1VX/BxUFiKuuuuqqq6666qqrrrrqKgAqV1111VVXXXXVVVddddVVV131fxcAlauuuuqqq6666qqrrrrqqquu+r8LgMpVV1111VVXXXXVVVddddVVV/3fBUDlqquuuuqqq6666qqrrrrqqqv+7wKgctVVV1111VVXXXXVVVddddVV/3cBULnqqv8lbGNz1VVXXXXVVVddddVVV/0fIIEk/gsAULnqqv8lpilpLUFcddVVV1111VVXXXXVVf9rCWxqLdQq/gsAULnqqv8luq7QdYWrrrrqqquuuuqqq6666qp/BQAqV1111VVXXXXVVVddddVVV131fxcAlauuuuqqq6666qqrrrrqqquu+r8LgMpVV1111VVXXXXVVVddddVVV/3fBUDlqquuuuqqq6666qqrrrrqqqv+7wKgctVVV1111VVXXXXVVVddddVV/3cBULnqqquuuuqqq6666qqrrrrqqv+7AKhcddX/Qekkbf41igJJXHXVVVddddVVV1111VX/pwBQueqq/2OMCQUh/tVsI4mrrrrqqquuuuqqq6666v8MACpXXfV/jBB/cMff8df3PgnEFeb5koRtZrXnDR/y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","media_type":"image/png","type":"base64"},"type":"image"}
```

## Assistant

*Tokens: 9 in, 1 out*

## Assistant

已学习影刀UI风格。总结一下：

**UI设计要点**：
- 左侧边栏 + 顶部导航 + 右侧主内容区布局
- 主按钮：粉红色圆角矩形 `rgb(252, 104, 104)`
- 背景：纯白 `rgb(255, 255, 255)`
- 边框/分隔线：浅灰 `rgb(245, 247, 250)`
- 表格式数据展示，简洁现代风格
- 图标简洁，带轻微色彩区分

---

## 信息汇总确认

**执行状态流转**：
```
用户已请求 → 已收到请求放入队列 → 已写入文件待影刀执行 → 影刀执行中 → 影刀执行完成 → 回调用户完成
                                                                              ↘ 回调外部服务失败
```

**数据模型**：

| 用户表 | 触发器表 | 执行记录表 |
|--------|----------|------------|
| userId | triggerId | eventId |
| 用户名称 | 名称 | triggerId |
| 用户备注 | 描述 | userId |
| 密钥(UUID) | 备注 | 入参时间 |
| 回调地址 | 创建时间 | 影刀开始执行时间 |
| 触发器权限列表 | | 影刀执行完毕通知时间 |
| | | 总耗时 |
| | | 影刀执行耗时 |
| | | 入参参数 |
| | | 出参参数 |
| | | 执行状态 |

**回调外部服务请求体**：
```json
{
  "triggerId": "1",
  "eventId": "xxx",
  "responseCode": "200",
  "responseMessage": "ok",
  "responseData": {...},
  "requestTime": "入参时间",
  "shadowBotStartTime": "影刀开始执行时间",
  "shadowBotEndTime": "影刀执行完毕时间",
  "totalDuration": "总耗时ms",
  "shadowBotDuration": "影刀执行耗时ms"
}
```

---

## 最后几个问题

**1. 技术选型建议**
基于你熟悉Vue，我推荐 **Electron + Vue3 + SQLite**：
- Electron：成熟的桌面应用框架
- Vue3：你熟悉，组件化开发
- SQLite：轻量本地数据库，无需安装
- Express/Fastify：内置HTTP Server

你接受这个技术栈吗？

**2. 安全校验**
针对body写入文件的安全问题，我计划：
- 限制body大小（如最大1MB）
- JSON格式强校验
- 过滤危险字符/路径遍历攻击
- 文件名安全处理

还有其他安全要求吗？

**3. 还有遗漏的功能/细节吗？**

确认后我就开始整理PRD文档。

*Tokens: 9 in, 1271 out*

## User

## Assistant

*Tokens: 9 in, 3 out*

## Assistant

我来整理一份完整的PRD+技术实现文档。这份文档会包含产品需求和技术实现细节。

*Tokens: 9 in, 3 out*

## Assistant

### Tool: Write

```json
{
  "content": "# ShadowBot-FileWebHook 产品需求文档(PRD)\n\n> 版本：v1.0\n> 日期：2026-01-29\n> 状态：待评审\n\n---\n\n## 一、项目概述\n\n### 1.1 项目名称\n**ShadowBot-FileWebHook**（以下简称 FileWebHook）\n\n### 1.2 项目定位\nFileWebHook 是影刀RPA生态中的一个中间件软件，用于解决影刀缺少Web触发器的问题。通过 FileWebHook，外部服务可以以 HTTP 的形式主动调用影刀应用，底层基于影刀的文件触发器实现。\n\n### 1.3 核心价值\n- 让外部服务能够通过标准 HTTP 接口调用影刀RPA应用\n- 隐藏文件交互细节，对外表现为真正的 Webhook\n- 提供完整的身份认证、权限控制和执行记录管理\n\n---\n\n## 二、产品背景\n\n### 2.1 影刀RPA简介\n影刀RPA是一个Windows GUI应用，用户可在此软件上编写RPA流程，并将流程发版为应用。影刀支持多种触发器：\n- 文件触发器：当本地系统文件新增/删除/修改时触发应用执行\n- 邮件触发器：当指定邮箱收到邮件时触发应用执行\n- 定时触发器：定时循环执行应用\n- 热键触发器：通过快捷键触发应用执行\n\n### 2.2 痛点问题\n影刀缺少 **Web触发器**，导致：\n- 外部服务无法主动调用影刀应用\n- 影刀只能发送HTTP请求，无法接收HTTP请求\n- 无法与外部系统进行双向集成\n\n### 2.3 解决方案\nFileWebHook 作为中间件，利用影刀现有的文件触发器能力，将 HTTP 请求转换为文件操作，从而实现外部服务对影刀应用的调用。\n\n---\n\n## 三、系统架构\n\n### 3.1 整体架构图\n\n```\n┌─────────────────────────────────────────────────────────────────────────────┐\n│                              本地机器                                        │\n│  ┌─────────────────────────────────────────────────────────────────────┐   │\n│  │                        FileWebHook                                   │   │\n│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐    │   │\n│  │  │ HTTP     │  │ 任务队列  │  │ 文件管理  │  │ SQLite数据库     │    │   │\n│  │  │ Server   │  │ (全局)   │  │          │  │ - 用户表         │    │   │\n│  │  │ :8089    │  │          │  │          │  │ - 触发器表       │    │   │\n│  │  └──────────┘  └──────────┘  └──────────┘  │ - 执行记录表     │    │   │\n│  │                                            └──────────────────┘    │   │\n│  └─────────────────────────────────────────────────────────────────────┘   │\n│                              ▲                                              │\n│                              │ 文件读写                                      │\n│                              ▼                                              │\n│  ┌─────────────────────────────────────────────────────────────────────┐   │\n│  │   triggerFiles\\{triggerId}\\requestParam.json                        │   │\n│  └─────────────────────────────────────────────────────────────────────┘   │\n│                              ▲                                              │\n│                              │ 文件触发器监听                                │\n│                              ▼                                              │\n│  ┌─────────────────────────────────────────────────────────────────────┐   │\n│  │                         影刀RPA                                      │   │\n│  │  ┌──────────────────────────────────────────────────────────────┐   │   │\n│  │  │  FileWebHook-APP-Framework (影刀侧框架，已开发完成)            │   │   │\n│  │  │  - 读取 requestParam.json 获取参数                            │   │   │\n│  │  │  - 回调 /trigged 通知开始执行                                  │   │   │\n│  │  │  - 执行业务逻辑                                                │   │   │\n│  │  │  - 回调 /notify 通知执行完成并返回结果                         │   │   │\n│  │  └──────────────────────────────────────────────────────────────┘   │   │\n│  └─────────────────────────────────────────────────────────────────────┘   │\n└─────────────────────────────────────────────────────────────────────────────┘\n          ▲                                              │\n          │ HTTP请求                                     │ HTTP回调\n          │ POST /trigger/{triggerId}                   │ POST /{triggerId}/notify\n          │                                              ▼\n┌─────────────────────────────────────────────────────────────────────────────┐\n│                           外部服务                                           │\n│                    (Java/Python/其他后端服务)                                │\n└─────────────────────────────────────────────────────────────────────────────┘\n```\n\n### 3.2 工作流程\n\n```\n步骤1: 外部服务发起请求\n       POST http://localhost:8089/trigger/{triggerId}\n       Header: FileWebHook-Authorization: {用户密钥UUID}\n       Body: {业务参数JSON}\n              │\n              ▼\n步骤2: FileWebHook 验证身份和权限\n       - 验证密钥是否有效\n       - 验证该用户是否有权限调用此触发器\n       - 验证失败则直接返回错误\n              │\n              ▼\n步骤3: 生成eventId，任务入队\n       - 创建执行记录，状态：「已收到请求放入队列」\n       - 返回eventId给外部服务\n              │\n              ▼\n步骤4: 队列消费，写入文件\n       - 状态更新：「已写入文件待影刀执行」\n       - 写入 triggerFiles\\{triggerId}\\requestParam.json\n              │\n              ▼\n步骤5: 影刀文件触发器监听到文件创建\n              │\n              ▼\n步骤6: 影刀回调 FileWebHook\n       POST http://localhost:8089/trigged\n       Body: {triggerId, eventId}\n       - 状态更新：「影刀执行中」\n              │\n              ▼\n步骤7: 影刀执行应用业务逻辑\n              │\n              ▼\n步骤8: 影刀执行完毕，回调 FileWebHook\n       POST http://localhost:8089/notify\n       Body: {triggerId, eventId, responseCode, responseMessage, responseData}\n       - 状态更新：「影刀执行完成」\n       - 删除 requestParam.json 文件\n              │\n              ▼\n步骤9: FileWebHook 回调外部服务\n       POST http://{用户配置的回调地址}/{triggerId}/notify\n       Body: {完整执行结果+时间信息}\n       - 成功：状态更新：「回调用户完成」\n       - 失败：状态更新：「回调外部服务失败」\n```\n\n---\n\n## 四、功能需求\n\n### 4.1 触发器管理\n\n#### 4.1.1 功能描述\n管理 FileWebHook 的触发器，每个触发器对应一个影刀应用。\n\n#### 4.1.2 功能规则\n| 规则项 | 说明 |\n|--------|------|\n| 创建 | 支持，点击创建按钮即可，无需填写表单，系统自动生成triggerId（从1递增） |\n| 修改 | 不支持 |\n| 删除 | 不支持 |\n| 查看 | 支持，可查看触发器列表和详情 |\n\n#### 4.1.3 触发器创建流程\n1. 用户点击「新建触发器」按钮\n2. 弹出表单，填写：名称、描述、备注（可选）\n3. 系统自动生成：\n   - triggerId（自增数字）\n   - 文件夹路径：`{triggerFiles路径}\\{triggerId}\\`\n   - 创建时间\n4. 创建成功后，用户需要手动将文件夹路径配置到影刀的文件触发器中\n\n#### 4.1.4 触发器列表展示\n- triggerId\n- 名称\n- 描述\n- 创建时间\n- 文件夹路径（供用户复制配置到影刀）\n- 操作：查看详情、查看执行记录\n\n### 4.2 用户管理\n\n#### 4.2.1 功能描述\n管理可以调用 FileWebHook 的外部服务用户，包括身份认证和权限分配。\n\n#### 4.2.2 用户信息\n| 字段 | 说明 |\n|------|------|\n| userId | 用户唯一标识，系统自动生成 |\n| 用户名称 | 用户的显示名称 |\n| 用户备注 | 备注信息 |\n| 密钥 | UUID格式，用于身份验证 |\n| 回调地址 | 外部服务的回调地址（IP:端口 或 域名） |\n| 触发器权限列表 | 该用户有权调用的触发器列表 |\n| 创建时间 | 用户创建时间 |\n\n#### 4.2.3 功能操作\n| 操作 | 说明 |\n|------|------|\n| 新增用户 | 填写名称、备注、回调地址，系统自动生成userId和密钥 |\n| 编辑用户 | 可修改名称、备注、回调地址 |\n| 重置密钥 | 重新生成UUID密钥 |\n| 分配权限 | 勾选该用户可调用的触发器 |\n| 删除用户 | 删除用户及其权限配置 |\n\n### 4.3 执行记录\n\n#### 4.3.1 功能描述\n记录所有通过 FileWebHook 的请求执行情况。\n\n#### 4.3.2 记录字段\n| 字段 | 说明 |\n|------|------|\n| eventId | 执行事件唯一标识 |\n| triggerId | 触发器ID |\n| userId | 调用用户ID |\n| 入参时间 | 外部服务请求到达的时间 |\n| 影刀开始执行时间 | 收到 /trigged 回调的时间 |\n| 影刀执行完毕通知时间 | 收到 /notify 回调的时间 |\n| 总耗时 | 从入参到回调完成的总时间(ms) |\n| 影刀执行耗时 | 影刀实际执行时间(ms) |\n| 入参参数 | 外部服务请求的参数(JSON) |\n| 出参参数 | 影刀返回的结果参数(JSON) |\n| 执行状态 | 当前执行状态 |\n\n#### 4.3.3 执行状态流转\n```\n用户已请求\n    ↓\n已收到请求放入队列\n    ↓\n已写入文件待影刀执行\n    ↓\n影刀执行中\n    ↓\n影刀执行完成\n    ↓\n┌─────────────────┬─────────────────┐\n│                 │                 │\n▼                 ▼                 │\n回调用户完成    回调外部服务失败     │\n                                    │\n                  ▲                 │\n                  └─ 影刀应用执行错误 ┘\n```\n\n#### 4.3.4 查看方式\n- 在触发器详情页查看该触发器的所有执行记录\n- 支持按状态筛选\n- 支持按时间范围筛选\n\n### 4.4 系统设置\n\n#### 4.4.1 可配置项\n| 配置项 | 说明 | 默认值 |\n|--------|------|--------|\n| triggerFiles路径 | 触发器文件存放路径 | {FileWebHook安装路径}\\triggerFiles |\n\n#### 4.4.2 不可配置项\n| 配置项 | 固定值 | 说明 |\n|--------|--------|------|\n| HTTP服务端口 | 8089 | FileWebHook的HTTP服务端口 |\n| 服务地址 | localhost | 仅本地访问 |\n\n### 4.5 任务队列\n\n#### 4.5.1 功能描述\n所有外部服务的请求进入全局任务队列，串行执行。\n\n#### 4.5.2 队列规则\n- 全局唯一队列\n- FIFO（先进先出）\n- 同一时间只有一个任务在执行（因为底层影刀只有一个实例）\n- 当前任务完成后（收到 /notify 回调）才消费下一个任务\n\n---\n\n## 五、API设计\n\n### 5.1 外部服务调用接口\n\n#### 5.1.1 触发执行\n```\nPOST /trigger/{triggerId}\n\nRequest Headers:\n  FileWebHook-Authorization: {用户密钥UUID}\n  Content-Type: application/json\n\nRequest Body:\n  {任意业务参数JSON对象}\n\nResponse Success (200):\n{\n  \"code\": 200,\n  \"message\": \"ok\",\n  \"data\": {\n    \"eventId\": \"1-1706515200000\",\n    \"triggerId\": \"1\",\n    \"status\": \"已收到请求放入队列\"\n  }\n}\n\nResponse Error - 认证失败 (401):\n{\n  \"code\": 401,\n  \"message\": \"身份验证失败：密钥无效\"\n}\n\nResponse Error - 权限不足 (403):\n{\n  \"code\": 403,\n  \"message\": \"鉴权失败：无权限调用此触发器\"\n}\n\nResponse Error - 触发器不存在 (404):\n{\n  \"code\": 404,\n  \"message\": \"触发器不存在\"\n}\n\nResponse Error - 请求体校验失败 (400):\n{\n  \"code\": 400,\n  \"message\": \"请求体校验失败：{具体原因}\"\n}\n```\n\n### 5.2 影刀回调接口\n\n#### 5.2.1 通知开始执行\n```\nPOST /trigged\n\nRequest Body:\n{\n  \"triggerId\": \"1\",\n  \"eventId\": \"1-1706515200000\"\n}\n\nResponse (200):\n{\n  \"code\": 200,\n  \"message\": \"ok\"\n}\n```\n\n#### 5.2.2 通知执行完成\n```\nPOST /notify\n\nRequest Body:\n{\n  \"responseCode\": \"200\",\n  \"responseMessage\": \"ok\",\n  \"triggerId\": \"1\",\n  \"eventId\": \"1-1706515200000\",\n  \"responseData\": {\n    \"任意业务返回字段\": \"值\"\n  }\n}\n\nResponse (200):\n{\n  \"code\": 200,\n  \"message\": \"ok\"\n}\n```\n\n### 5.3 回调外部服务\n\n#### 5.3.1 执行结果回调\n```\nPOST http://{用户回调地址}/{triggerId}/notify\n\nRequest Body:\n{\n  \"triggerId\": \"1\",\n  \"eventId\": \"1-1706515200000\",\n  \"responseCode\": \"200\",\n  \"responseMessage\": \"ok\",\n  \"responseData\": {\n    \"任意业务返回字段\": \"值\"\n  },\n  \"requestTime\": \"2026-01-29T10:00:00.000Z\",\n  \"shadowBotStartTime\": \"2026-01-29T10:00:01.000Z\",\n  \"shadowBotEndTime\": \"2026-01-29T10:00:05.000Z\",\n  \"totalDuration\": 5500,\n  \"shadowBotDuration\": 4000\n}\n```\n\n---\n\n## 六、数据模型\n\n### 6.1 用户表 (users)\n\n| 字段名 | 类型 | 必填 | 说明 |\n|--------|------|------|------|\n| id | INTEGER | 是 | 主键，自增 |\n| user_id | VARCHAR(36) | 是 | 用户唯一标识(UUID) |\n| name | VARCHAR(100) | 是 | 用户名称 |\n| remark | TEXT | 否 | 用户备注 |\n| secret_key | VARCHAR(36) | 是 | 密钥(UUID) |\n| callback_url | VARCHAR(500) | 是 | 回调地址 |\n| created_at | DATETIME | 是 | 创建时间 |\n| updated_at | DATETIME | 是 | 更新时间 |\n\n### 6.2 触发器表 (triggers)\n\n| 字段名 | 类型 | 必填 | 说明 |\n|--------|------|------|------|\n| id | INTEGER | 是 | 主键，自增，即triggerId |\n| name | VARCHAR(100) | 是 | 触发器名称 |\n| description | TEXT | 否 | 触发器描述 |\n| remark | TEXT | 否 | 备注 |\n| folder_path | VARCHAR(500) | 是 | 文件夹完整路径 |\n| created_at | DATETIME | 是 | 创建时间 |\n\n### 6.3 用户触发器权限表 (user_trigger_permissions)\n\n| 字段名 | 类型 | 必填 | 说明 |\n|--------|------|------|------|\n| id | INTEGER | 是 | 主键，自增 |\n| user_id | VARCHAR(36) | 是 | 用户ID(关联users表) |\n| trigger_id | INTEGER | 是 | 触发器ID(关联triggers表) |\n| created_at | DATETIME | 是 | 创建时间 |\n\n### 6.4 执行记录表 (execution_logs)\n\n| 字段名 | 类型 | 必填 | 说明 |\n|--------|------|------|------|\n| id | INTEGER | 是 | 主键，自增 |\n| event_id | VARCHAR(50) | 是 | 执行事件ID |\n| trigger_id | INTEGER | 是 | 触发器ID |\n| user_id | VARCHAR(36) | 是 | 调用用户ID |\n| status | VARCHAR(50) | 是 | 执行状态 |\n| request_time | DATETIME | 是 | 入参时间 |\n| shadowbot_start_time | DATETIME | 否 | 影刀开始执行时间 |\n| shadowbot_end_time | DATETIME | 否 | 影刀执行完毕通知时间 |\n| total_duration | INTEGER | 否 | 总耗时(ms) |\n| shadowbot_duration | INTEGER | 否 | 影刀执行耗时(ms) |\n| request_params | TEXT | 是 | 入参参数(JSON字符串) |\n| response_params | TEXT | 否 | 出参参数(JSON字符串) |\n| created_at | DATETIME | 是 | 记录创建时间 |\n| updated_at | DATETIME | 是 | 记录更新时间 |\n\n---\n\n## 七、UI设计\n\n### 7.1 设计风格\n\n参照影刀RPA的UI风格，采用简约现代的设计：\n\n| 设计要素 | 规范 |\n|----------|------|\n| 主色调 | 浅粉色 `rgb(252, 104, 104)` / `#FC6868` |\n| 背景色 | 白色 `rgb(255, 255, 255)` / `#FFFFFF` |\n| 边框/分隔色 | 浅灰色 `rgb(245, 247, 250)` / `#F5F7FA` |\n| 布局 | 左侧边栏 + 顶部导航 + 右侧主内容区 |\n| 按钮风格 | 圆角矩形，主按钮使用主色调填充 |\n| 表格 | 简洁的表格式数据展示 |\n| 字体 | 系统默认字体，清晰易读 |\n\n### 7.2 页面结构\n\n```\n┌─────────────────────────────────────────────────────────────────┐\n│  Logo: FileWebHook                    [系统状态指示灯]          │\n├──────────────┬──────────────────────────────────────────────────┤\n│              │                                                  │\n│   侧边导航    │              主内容区                            │\n│              │                                                  │\n│  ┌────────┐  │                                                  │\n│  │ 触发器  │  │                                                  │\n│  │ 管理   │  │                                                  │\n│  └────────┘  │                                                  │\n│              │                                                  │\n│  ┌────────┐  │                                                  │\n│  │ 用户   │  │                                                  │\n│  │ 管理   │  │                                                  │\n│  └────────┘  │                                                  │\n│              │                                                  │\n│  ┌────────┐  │                                                  │\n│  │ 系统   │  │                                                  │\n│  │ 设置   │  │                                                  │\n│  └────────┘  │                                                  │\n│              │                                                  │\n└──────────────┴──────────────────────────────────────────────────┘\n```\n\n### 7.3 页面清单\n\n#### 7.3.1 触发器管理页面\n- 触发器列表\n  - 显示：ID、名称、描述、创建时间、文件夹路径\n  - 操作：查看详情、查看执行记录、复制路径\n- 新建触发器弹窗\n  - 表单：名称(必填)、描述(选填)、备注(选填)\n- 触发器详情页\n  - 基本信息展示\n  - 执行记录列表（支持筛选、分页）\n\n#### 7.3.2 用户管理页面\n- 用户列表\n  - 显示：名称、备注、回调地址、权限数量、创建时间\n  - 操作：编辑、分配权限、重置密钥、删除\n- 新建/编辑用户弹窗\n  - 表单：名称(必填)、备注(选填)、回调地址(必填)\n- 权限分配弹窗\n  - 触发器多选列表\n\n#### 7.3.3 系统设置页面\n- triggerFiles路径配置\n- 当前配置信息展示\n- HTTP服务状态展示\n\n---\n\n## 八、错误处理\n\n### 8.1 错误分类\n\n| 错误类型 | 说明 | 示例 |\n|----------|------|------|\n| 影刀应用执行错误 | 影刀应用流程内失败 | 业务逻辑异常、元素定位失败 |\n| 影刀软件错误 | 影刀软件本身问题 | 影刀未开启、版本不匹配 |\n| FileWebHook非业务错误 | 系统级错误 | 空指针、文件写入失败 |\n| FileWebHook业务错误 | 业务校验错误 | 身份验证失败、鉴权失败、请求体校验失败 |\n\n### 8.2 错误响应格式\n\n```json\n{\n  \"code\": 错误码,\n  \"message\": \"错误描述\",\n  \"errorType\": \"错误类型\",\n  \"detail\": \"详细信息(可选)\"\n}\n```\n\n### 8.3 错误码定义\n\n| 错误码 | 类型 | 说明 |\n|--------|------|------|\n| 400 | FileWebHook业务错误 | 请求体校验失败 |\n| 401 | FileWebHook业务错误 | 身份验证失败 |\n| 403 | FileWebHook业务错误 | 鉴权失败 |\n| 404 | FileWebHook业务错误 | 资源不存在 |\n| 500 | FileWebHook非业务错误 | 系统内部错误 |\n| 502 | 影刀软件错误 | 影刀相关错误 |\n| 503 | 影刀应用执行错误 | 应用执行失败 |\n\n---\n\n## 九、安全设计\n\n### 9.1 请求体安全校验\n\n由于外部服务的请求体会直接写入文件，需要防范文件攻击：\n\n| 校验项 | 规则 |\n|--------|------|\n| 请求体大小限制 | 最大 1MB |\n| JSON格式校验 | 必须是合法的JSON对象 |\n| 字符串内容校验 | 过滤路径遍历字符（如 `../`、`..\\\\`） |\n| 特殊字符过滤 | 过滤可能的注入字符 |\n| 嵌套深度限制 | JSON嵌套最大深度 10 层 |\n| 字段数量限制 | 单个对象最大字段数 100 个 |\n\n### 9.2 文件操作安全\n\n| 安全措施 | 说明 |\n|----------|------|\n| 路径校验 | 确保文件只写入指定的 triggerFiles 目录 |\n| 文件名安全 | 只使用系统生成的安全文件名 |\n| 权限控制 | 文件操作使用最小必要权限 |\n\n### 9.3 身份认证\n\n| 认证方式 | 说明 |\n|----------|------|\n| 密钥认证 | 通过请求头 `FileWebHook-Authorization` 传递UUID密钥 |\n| 权限校验 | 验证用户是否有权限调用指定触发器 |\n\n---\n\n## 十、技术架构\n\n### 10.1 技术选型\n\n| 层级 | 技术 | 说明 |\n|------|------|------|\n| 桌面框架 | Electron | 跨平台桌面应用框架 |\n| 前端框架 | Vue 3 | 组件化UI开发 |\n| UI组件库 | Element Plus | Vue 3 UI组件库 |\n| HTTP服务 | Express.js | Node.js HTTP服务框架 |\n| 数据库 | SQLite | 轻量级本地数据库 |\n| ORM | better-sqlite3 | SQLite 同步操作库 |\n| 构建工具 | Vite | 前端构建工具 |\n| 打包工具 | electron-builder | Electron 打包工具 |\n\n### 10.2 项目结构\n\n```\nshadowbot-filewebhook/\n├── package.json\n├── vite.config.js\n├── electron/                    # Electron 主进程\n│   ├── main.js                  # 主进程入口\n│   ├── preload.js               # 预加载脚本\n│   ├── server/                  # HTTP 服务\n│   │   ├── index.js             # Express 服务入口\n│   │   ├── routes/              # 路由\n│   │   │   ├── trigger.js       # 触发器相关接口\n│   │   │   └── callback.js      # 影刀回调接口\n│   │   ├── middleware/          # 中间件\n│   │   │   ├── auth.js          # 身份验证\n│   │   │   └── validator.js     # 请求体校验\n│   │   └── services/            # 业务服务\n│   │       ├── queue.js         # 任务队列\n│   │       └── file.js          # 文件操作\n│   └── database/                # 数据库\n│       ├── index.js             # 数据库初始化\n│       ├── models/              # 数据模型\n│       │   ├── user.js\n│       │   ├── trigger.js\n│       │   └── executionLog.js\n│       └── migrations/          # 数据库迁移\n├── src/                         # Vue 前端\n│   ├── main.js                  # 前端入口\n│   ├── App.vue                  # 根组件\n│   ├── router/                  # 路由配置\n│   ├── views/                   # 页面组件\n│   │   ├── TriggerManage.vue    # 触发器管理\n│   │   ├── UserManage.vue       # 用户管理\n│   │   └── SystemSettings.vue   # 系统设置\n│   ├── components/              # 通用组件\n│   ├── stores/                  # Pinia 状态管理\n│   ├── api/                     # API 调用\n│   └── styles/                  # 样式文件\n└── resources/                   # 静态资源\n    └── icon.ico                 # 应用图标\n```\n\n### 10.3 进程通信\n\n```\n┌─────────────────────────────────────────────────────────────┐\n│                    Electron 主进程                          │\n│  ┌─────────────────┐  ┌─────────────────────────────────┐  │\n│  │  HTTP Server    │  │  数据库操作                      │  │\n│  │  (Express)      │  │  (better-sqlite3)               │  │\n│  │  :8089          │  │                                 │  │\n│  └────────┬────────┘  └────────────────┬────────────────┘  │\n│           │                            │                    │\n│           │         IPC通信            │                    │\n│           └────────────┬───────────────┘                    │\n│                        │                                    │\n└────────────────────────┼────────────────────────────────────┘\n                         │\n              ┌──────────┴──────────┐\n              │    preload.js       │\n              │    (contextBridge)  │\n              └──────────┬──────────┘\n                         │\n┌────────────────────────┼────────────────────────────────────┐\n│                        │                                    │\n│           ┌────────────┴────────────┐                      │\n│           │     渲染进程 (Vue 3)     │                      │\n│           │     UI界面              │                      │\n│           └─────────────────────────┘                      │\n│                    Electron 渲染进程                        │\n└─────────────────────────────────────────────────────────────┘\n```\n\n---\n\n## 十一、部署与发布\n\n### 11.1 构建产物\n\n| 产物 | 说明 |\n|------|------|\n| Windows安装包 | .exe 安装程序 |\n| Windows便携版 | 免安装压缩包 |\n\n### 11.2 系统要求\n\n| 要求项 | 规格 |\n|--------|------|\n| 操作系统 | Windows 10/11 |\n| 内存 | 最低 4GB |\n| 磁盘空间 | 最低 200MB |\n| 依赖 | 无需额外安装运行时 |\n\n### 11.3 开源发布\n\n- 代码托管：GitHub\n- 开源协议：MIT License\n- 仓库名称：shadowbot-filewebhook\n\n---\n\n## 十二、文件说明\n\n### 12.1 requestParam.json 文件格式\n\n```json\n{\n  \"eventId\": \"1-1706515200000\",\n  \"triggerId\": \"1\",\n  \"userId\": \"uuid-xxx\",\n  \"requestTime\": \"2026-01-29T10:00:00.000Z\",\n  \"requestData\": {\n    \"外部服务传入的业务参数\": \"值\"\n  }\n}\n```\n\n### 12.2 文件生命周期\n\n1. **创建**：队列消费任务时，写入 `triggerFiles\\{triggerId}\\requestParam.json`\n2. **读取**：影刀文件触发器监听到文件创建，读取参数\n3. **删除**：FileWebHook 收到 `/notify` 回调后，删除该文件\n\n---\n\n## 十三、附录\n\n### 13.1 执行状态中英文对照\n\n| 中文状态 | 英文标识 |\n|----------|----------|\n| 用户已请求 | REQUESTED |\n| 已收到请求放入队列 | QUEUED |\n| 已写入文件待影刀执行 | FILE_WRITTEN |\n| 影刀执行中 | EXECUTING |\n| 影刀执行完成 | COMPLETED |\n| 回调用户完成 | CALLBACK_SUCCESS |\n| 回调外部服务失败 | CALLBACK_FAILED |\n\n### 13.2 eventId 生成规则\n\n格式：`{triggerId}-{时间戳毫秒}`\n\n示例：`1-1706515200000`\n\n### 13.3 用户使用前提\n\n1. 本机已安装并启动影刀RPA\n2. 在影刀中导入 FileWebHook-APP-Framework\n3. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径\n4. 启动 FileWebHook 软件\n\n---\n\n## 十四、版本历史\n\n| 版本 | 日期 | 说明 |\n|------|------|------|\n| v1.0 | 2026-01-29 | 初始版本 |\n\n---\n\n## 十五、待确认事项\n\n> 以下事项需要在开发前确认：\n\n1. ~~触发器创建时是否需要填写名称/描述？~~ ✅ 需要\n2. ~~用户删除后，其历史执行记录如何处理？~~ 待确认\n3. ~~是否需要执行记录的批量删除/清理功能？~~ 待确认\n4. ~~是否需要数据导出功能？~~ 待确认\n\n",
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md"
}
```

*Tokens: 9 in, 10069 out*

## User

### Tool Result

```
File created successfully at: C:\Users\Administrator\Desktop\ShadowBot-FileWebHook-PRD.md
```

## Assistant

*Tokens: 7 in, 7 out*
ok 就按照你的表设计 和 技术选型 还有安全校验处理 你可以开始整理prd文档 或者说叫实现文档?


## Assistant

PRD文档已整理完成，保存在：`C:\Users\Administrator\Desktop\ShadowBot-FileWebHook-PRD.md`

## 文档结构概览

| 章节 | 内容 |
|------|------|
| 一、项目概述 | 名称、定位、核心价值 |
| 二、产品背景 | 影刀简介、痛点、解决方案 |
| 三、系统架构 | 架构图、完整工作流程 |
| 四、功能需求 | 触发器管理、用户管理、执行记录、系统设置、任务队列 |
| 五、API设计 | 外部调用接口、影刀回调接口、回调外部服务 |
| 六、数据模型 | 4张表的详细字段定义 |
| 七、UI设计 | 设计风格、页面结构、页面清单 |
| 八、错误处理 | 错误分类、响应格式、错误码 |
| 九、安全设计 | 请求体校验、文件操作安全、身份认证 |
| 十、技术架构 | 技术选型、项目结构、进程通信 |
| 十一、部署与发布 | 构建产物、系统要求、开源发布 |
| 十二~十五 | 文件说明、附录、版本历史、待确认事项 |

---

## 待确认事项

文档末尾有几个待确认点：

1. 用户删除后，其历史执行记录如何处理？（保留/级联删除？）
2. 是否需要执行记录的批量删除/清理功能？
3. 是否需要数据导出功能？

请review文档，有任何需要调整或补充的地方告诉我。

*Tokens: 7 in, 579 out*

## User

## Assistant

*Tokens: 9 in, 3 out*

## Assistant

可以，但需要你告诉我你改完了，然后我重新读取文件。

我无法实时监听文件变化，工作方式是：

1. 你修改并保存文档
2. 告诉我"改好了"
3. 我重新读取文件，对比之前的内容找出差异

或者更简单的方式：你改完后直接告诉我改了哪些点，我来同步更新我的理解。

你现在要去修改吗？

*Tokens: 9 in, 326 out*

## User

## Assistant

*Tokens: 9 in, 3 out*

## Assistant

已同步，我注意到以下修改：

| 修改点 | 内容 |
|--------|------|
| 新增状态 | 「已预响应外部服务」- 队列入队成功后立即响应外部服务 |
| 删除文件时机 | 从步骤8移到步骤9，由FileWebHook在回调外部服务时删除 |
| 新增配置项 | 全局队列最大长度，默认50 |
| 待确认事项 | 全部确认：保留历史记录、需要批量清理、需要数据导出 |

---

## 关于你的网络问题

这是个关键问题。FileWebHook 默认监听 `localhost:8089`，只能本机访问。外部服务要访问，有几种方案：

### 方案1：局域网访问（最简单）
如果外部服务和FileWebHook在**同一局域网**：
- FileWebHook 监听 `0.0.0.0:8089`（而非localhost）
- 外部服务通过内网IP访问，如 `http://192.168.1.100:8089`
- 需要开放Windows防火墙8089端口

### 方案2：内网穿透（公网访问）
如果外部服务在**公网**：
- 使用内网穿透工具：ngrok、frp、花生壳、cpolar等
- 将本地8089端口映射到公网域名
- 外部服务通过公网域名访问

### 方案3：云服务器部署
- FileWebHook部署在云服务器上
- 但这样影刀也要在云服务器上运行

---

## 我的建议

在PRD中新增一个章节说明网络配置，同时：

1. **FileWebHook监听地址改为可配置**：默认`0.0.0.0`（允许外部访问）
2. **文档中说明**：用户需自行解决网络可达性问题（内网穿透等）

你觉得这样处理可以吗？还是你有其他想法？

*Tokens: 9 in, 3 out*

## User

## Assistant

*Tokens: 9 in, 3 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md"
}
```

*Tokens: 9 in, 198 out*

## User

### Tool Result

```
     1→# ShadowBot-FileWebHook 产品需求文档(PRD)
     2→
     3→> 版本：v1.0
     4→> 日期：2026-01-29
     5→> 状态：待评审
     6→
     7→---
     8→
     9→## 一、项目概述
    10→
    11→### 1.1 项目名称
    12→**ShadowBot-FileWebHook**（以下简称 FileWebHook）
    13→
    14→### 1.2 项目定位
    15→FileWebHook 是影刀RPA生态中的一个中间件软件，用于解决影刀缺少Web触发器的问题。通过 FileWebHook，外部服务可以以 HTTP 的形式主动调用影刀应用，底层基于影刀的文件触发器实现。
    16→
    17→### 1.3 核心价值
    18→- 让外部服务能够通过标准 HTTP 接口调用影刀RPA应用
    19→- 隐藏文件交互细节，对外表现为真正的 Webhook
    20→- 提供完整的身份认证、权限控制和执行记录管理
    21→
    22→---
    23→
    24→## 二、产品背景
    25→
    26→### 2.1 影刀RPA简介
    27→影刀RPA是一个Windows GUI应用，用户可在此软件上编写RPA流程，并将流程发版为应用。影刀支持多种触发器：
    28→- 文件触发器：当本地系统文件新增/删除/修改时触发应用执行
    29→- 邮件触发器：当指定邮箱收到邮件时触发应用执行
    30→- 定时触发器：定时循环执行应用
    31→- 热键触发器：通过快捷键触发应用执行
    32→
    33→### 2.2 痛点问题
    34→影刀缺少 **Web触发器**，导致：
    35→- 外部服务无法主动调用影刀应用
    36→- 影刀只能发送HTTP请求，无法接收HTTP请求
    37→- 无法与外部系统进行双向集成
    38→
    39→### 2.3 解决方案
    40→FileWebHook 作为中间件，利用影刀现有的文件触发器能力，将 HTTP 请求转换为文件操作，从而实现外部服务对影刀应用的调用。
    41→
    42→---
    43→
    44→## 三、系统架构
    45→
    46→### 3.1 整体架构图
    47→
    48→```
    49→┌─────────────────────────────────────────────────────────────────────────────┐
    50→│                              本地机器                                        │
    51→│  ┌─────────────────────────────────────────────────────────────────────┐   │
    52→│  │                        FileWebHook                                   │   │
    53→│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐    │   │
    54→│  │  │ HTTP     │  │ 任务队列  │  │ 文件管理  │  │ SQLite数据库     │    │   │
    55→│  │  │ Server   │  │ (全局)   │  │          │  │ - 用户表         │    │   │
    56→│  │  │ :8089    │  │          │  │          │  │ - 触发器表       │    │   │
    57→│  │  └──────────┘  └──────────┘  └──────────┘  │ - 执行记录表     │    │   │
    58→│  │                                            └──────────────────┘    │   │
    59→│  └─────────────────────────────────────────────────────────────────────┘   │
    60→│                              ▲                                              │
    61→│                              │ 文件读写                                      │
    62→│                              ▼                                              │
    63→│  ┌─────────────────────────────────────────────────────────────────────┐   │
    64→│  │   triggerFiles\{triggerId}\requestParam.json                        │   │
    65→│  └─────────────────────────────────────────────────────────────────────┘   │
    66→│                              ▲                                              │
    67→│                              │ 文件触发器监听                                │
    68→│                              ▼                                              │
    69→│  ┌─────────────────────────────────────────────────────────────────────┐   │
    70→│  │                         影刀RPA                                      │   │
    71→│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
    72→│  │  │  FileWebHook-APP-Framework (影刀侧框架，已开发完成)            │   │   │
    73→│  │  │  - 读取 requestParam.json 获取参数                            │   │   │
    74→│  │  │  - 回调 /trigged 通知开始执行                                  │   │   │
    75→│  │  │  - 执行业务逻辑                                                │   │   │
    76→│  │  │  - 回调 /notify 通知执行完成并返回结果                         │   │   │
    77→│  │  └──────────────────────────────────────────────────────────────┘   │   │
    78→│  └─────────────────────────────────────────────────────────────────────┘   │
    79→└─────────────────────────────────────────────────────────────────────────────┘
    80→          ▲                                              │
    81→          │ HTTP请求                                     │ HTTP回调
    82→          │ POST /trigger/{triggerId}                   │ POST /{triggerId}/notify
    83→          │                                              ▼
    84→┌─────────────────────────────────────────────────────────────────────────────┐
    85→│                           外部服务                                           │
    86→│                    (Java/Python/其他后端服务)                                │
    87→└─────────────────────────────────────────────────────────────────────────────┘
    88→```
    89→
    90→### 3.2 工作流程
    91→
    92→```
    93→步骤1: 外部服务发起请求
    94→       POST http://localhost:8089/trigger/{triggerId}
    95→       Header: FileWebHook-Authorization: {用户密钥UUID}
    96→       Body: {业务参数JSON}
    97→              │
    98→              ▼
    99→步骤2: FileWebHook 验证身份和权限
   100→       - 验证密钥是否有效
   101→       - 验证该用户是否有权限调用此触发器
   102→       - 验证失败则直接返回错误
   103→              │
   104→              ▼
   105→步骤3: 生成eventId，任务入队
   106→       - 队列最大长度可以通过FileWebHook的设置页面进行配置
   107→       - 创建执行记录，状态：「已收到请求放入队列」
   108→       - 返回eventId给外部服务,相当于立即先响应
   109→              │
   110→              ▼
   111→步骤4: 队列消费，写入文件
   112→       - 状态更新：「已写入文件待影刀执行」
   113→       - 写入 triggerFiles\{triggerId}\requestParam.json
   114→              │
   115→              ▼
   116→步骤5: 影刀文件触发器监听到文件创建
   117→              │
   118→              ▼
   119→步骤6: 影刀回调 FileWebHook
   120→       POST http://localhost:8089/trigged
   121→       Body: {triggerId, eventId}
   122→       - 状态更新：「影刀执行中」
   123→              │
   124→              ▼
   125→步骤7: 影刀执行应用业务逻辑
   126→              │
   127→              ▼
   128→步骤8: 影刀执行完毕，回调 FileWebHook
   129→       POST http://localhost:8089/notify
   130→       Body: {triggerId, eventId, responseCode, responseMessage, responseData}
   131→       - 状态更新：「影刀执行完成」
   132→              │
   133→              ▼
   134→步骤9: FileWebHook 回调外部服务
   135→      - 删除 requestParam.json 文件 (这个删除操作放到FileWebHook)
   136→       POST http://{用户配置的回调地址}/{triggerId}/notify
   137→       Body: {完整执行结果+时间信息}
   138→       - 成功：状态更新：「回调用户完成」
   139→       - 失败：状态更新：「回调外部服务失败」
   140→```
   141→
   142→---
   143→
   144→## 四、功能需求
   145→
   146→### 4.1 触发器管理
   147→
   148→#### 4.1.1 功能描述
   149→管理 FileWebHook 的触发器，每个触发器对应一个影刀应用。
   150→
   151→#### 4.1.2 功能规则
   152→| 规则项 | 说明 |
   153→|--------|------|
   154→| 创建 | 支持，点击创建按钮即可，无需填写表单，系统自动生成triggerId（从1递增） |
   155→| 修改 | 不支持 |
   156→| 删除 | 不支持 |
   157→| 查看 | 支持，可查看触发器列表和详情 |
   158→
   159→#### 4.1.3 触发器创建流程
   160→1. 用户点击「新建触发器」按钮
   161→2. 弹出表单，填写：名称、描述、备注（可选）
   162→3. 系统自动生成：
   163→   - triggerId（自增数字）
   164→   - 文件夹路径：`{triggerFiles路径}\{triggerId}\`
   165→   - 创建时间
   166→4. 创建成功后，用户需要手动将文件夹路径配置到影刀的文件触发器中
   167→
   168→#### 4.1.4 触发器列表展示
   169→- triggerId
   170→- 名称
   171→- 描述
   172→- 创建时间
   173→- 文件夹路径（供用户复制配置到影刀）
   174→- 操作：查看详情、查看执行记录
   175→
   176→### 4.2 用户管理
   177→
   178→#### 4.2.1 功能描述
   179→管理可以调用 FileWebHook 的外部服务用户，包括身份认证和权限分配。
   180→
   181→#### 4.2.2 用户信息
   182→| 字段 | 说明 |
   183→|------|------|
   184→| userId | 用户唯一标识，系统自动生成 |
   185→| 用户名称 | 用户的显示名称 |
   186→| 用户备注 | 备注信息 |
   187→| 密钥 | UUID格式，用于身份验证 |
   188→| 回调地址 | 外部服务的回调地址（IP:端口 或 域名） |
   189→| 触发器权限列表 | 该用户有权调用的触发器列表 |
   190→| 创建时间 | 用户创建时间 |
   191→
   192→#### 4.2.3 功能操作
   193→| 操作 | 说明 |
   194→|------|------|
   195→| 新增用户 | 填写名称、备注、回调地址，系统自动生成userId和密钥 |
   196→| 编辑用户 | 可修改名称、备注、回调地址 |
   197→| 重置密钥 | 重新生成UUID密钥 |
   198→| 分配权限 | 勾选该用户可调用的触发器 |
   199→| 删除用户 | 删除用户及其权限配置 |
   200→
   201→### 4.3 执行记录
   202→
   203→#### 4.3.1 功能描述
   204→记录所有通过 FileWebHook 的请求执行情况。
   205→
   206→#### 4.3.2 记录字段
   207→| 字段 | 说明 |
   208→|------|------|
   209→| eventId | 执行事件唯一标识 |
   210→| triggerId | 触发器ID |
   211→| userId | 调用用户ID |
   212→| 入参时间 | 外部服务请求到达的时间 |
   213→| 影刀开始执行时间 | 收到 /trigged 回调的时间 |
   214→| 影刀执行完毕通知时间 | 收到 /notify 回调的时间 |
   215→| 总耗时 | 从入参到回调完成的总时间(ms) |
   216→| 影刀执行耗时 | 影刀实际执行时间(ms) |
   217→| 入参参数 | 外部服务请求的参数(JSON) |
   218→| 出参参数 | 影刀返回的结果参数(JSON) |
   219→| 执行状态 | 当前执行状态 |
   220→
   221→#### 4.3.3 执行状态流转
   222→```
   223→用户已请求
   224→    ↓
   225→已收到请求放入队列
   226→    ↓
   227→已预响应外部服务(当任务放到队列成功时,调用外部服务成功后,修改为此状态)
   228→    ↓
   229→已写入文件待影刀执行(当队列开始消费此任务时,会将请求写入文件,修改为此状态)
   230→    ↓
   231→影刀执行中(影刀监听文件变化后读取成功文件后,会初次回调 /trigged 接口,修改为此状态)
   232→    ↓
   233→影刀执行完成
   234→    ↓
   235→┌─────────────────┬─────────────────┐
   236→│                 │                 │
   237→▼                 ▼                 │
   238→回调用户完成    回调外部服务失败     │
   239→                                    │
   240→                  ▲                 │
   241→                  └─ 影刀应用执行错误 ┘
   242→```
   243→
   244→#### 4.3.4 查看方式
   245→- 在触发器详情页查看该触发器的所有执行记录
   246→- 支持按状态筛选
   247→- 支持按时间范围筛选
   248→
   249→### 4.4 系统设置
   250→
   251→#### 4.4.1 可配置项
   252→| 配置项            | 说明        | 默认值                            |
   253→|----------------|-----------|--------------------------------|
   254→| triggerFiles路径 | 触发器文件存放路径 | {FileWebHook安装路径}\triggerFiles |
   255→| 全局队列最大长度         | 全局队列最大长度  | 50                             |
   256→
   257→#### 4.4.2 不可配置项
   258→| 配置项 | 固定值 | 说明 |
   259→|--------|--------|------|
   260→| HTTP服务端口 | 8089 | FileWebHook的HTTP服务端口 |
   261→| 服务地址 | localhost | 仅本地访问 |
   262→
   263→### 4.5 任务队列
   264→
   265→#### 4.5.1 功能描述
   266→所有外部服务的请求进入全局任务队列，串行执行。
   267→
   268→#### 4.5.2 队列规则
   269→- 全局唯一队列
   270→- FIFO（先进先出）
   271→- 同一时间只有一个任务在执行（因为底层影刀只有一个实例）
   272→- 当前任务完成后（收到 /notify 回调）才消费下一个任务
   273→
   274→---
   275→
   276→## 五、API设计
   277→
   278→### 5.1 外部服务调用接口
   279→
   280→#### 5.1.1 触发执行
   281→```
   282→POST /trigger/{triggerId}
   283→
   284→Request Headers:
   285→  FileWebHook-Authorization: {用户密钥UUID}
   286→  Content-Type: application/json
   287→
   288→Request Body:
   289→  {任意业务参数JSON对象}
   290→
   291→Response Success (200):
   292→{
   293→  "code": 200,
   294→  "message": "ok",
   295→  "data": {
   296→    "eventId": "1-1706515200000",
   297→    "triggerId": "1",
   298→    "status": "已收到请求放入队列"
   299→  }
   300→}
   301→
   302→Response Error - 认证失败 (401):
   303→{
   304→  "code": 401,
   305→  "message": "身份验证失败：密钥无效"
   306→}
   307→
   308→Response Error - 权限不足 (403):
   309→{
   310→  "code": 403,
   311→  "message": "鉴权失败：无权限调用此触发器"
   312→}
   313→
   314→Response Error - 触发器不存在 (404):
   315→{
   316→  "code": 404,
   317→  "message": "触发器不存在"
   318→}
   319→
   320→Response Error - 请求体校验失败 (400):
   321→{
   322→  "code": 400,
   323→  "message": "请求体校验失败：{具体原因}"
   324→}
   325→```
   326→
   327→### 5.2 影刀回调接口
   328→
   329→#### 5.2.1 通知开始执行
   330→```
   331→POST /trigged
   332→
   333→Request Body:
   334→{
   335→  "triggerId": "1",
   336→  "eventId": "1-1706515200000"
   337→}
   338→
   339→Response (200):
   340→{
   341→  "code": 200,
   342→  "message": "ok"
   343→}
   344→```
   345→
   346→#### 5.2.2 通知执行完成
   347→```
   348→POST /notify
   349→
   350→Request Body:
   351→{
   352→  "responseCode": "200",
   353→  "responseMessage": "ok",
   354→  "triggerId": "1",
   355→  "eventId": "1-1706515200000",
   356→  "responseData": {
   357→    "任意业务返回字段": "值"
   358→  }
   359→}
   360→
   361→Response (200):
   362→{
   363→  "code": 200,
   364→  "message": "ok"
   365→}
   366→```
   367→
   368→### 5.3 回调外部服务
   369→
   370→#### 5.3.1 执行结果回调
   371→```
   372→POST http://{用户回调地址}/{triggerId}/notify
   373→
   374→Request Body:
   375→{
   376→  "triggerId": "1",
   377→  "eventId": "1-1706515200000",
   378→  "responseCode": "200",
   379→  "responseMessage": "ok",
   380→  "responseData": {
   381→    "任意业务返回字段": "值"
   382→  },
   383→  "requestTime": "2026-01-29T10:00:00.000Z",
   384→  "shadowBotStartTime": "2026-01-29T10:00:01.000Z",
   385→  "shadowBotEndTime": "2026-01-29T10:00:05.000Z",
   386→  "totalDuration": 5500,
   387→  "shadowBotDuration": 4000
   388→}
   389→```
   390→
   391→---
   392→
   393→## 六、数据模型
   394→
   395→### 6.1 用户表 (users)
   396→
   397→| 字段名 | 类型 | 必填 | 说明 |
   398→|--------|------|------|------|
   399→| id | INTEGER | 是 | 主键，自增 |
   400→| user_id | VARCHAR(36) | 是 | 用户唯一标识(UUID) |
   401→| name | VARCHAR(100) | 是 | 用户名称 |
   402→| remark | TEXT | 否 | 用户备注 |
   403→| secret_key | VARCHAR(36) | 是 | 密钥(UUID) |
   404→| callback_url | VARCHAR(500) | 是 | 回调地址 |
   405→| created_at | DATETIME | 是 | 创建时间 |
   406→| updated_at | DATETIME | 是 | 更新时间 |
   407→
   408→### 6.2 触发器表 (triggers)
   409→
   410→| 字段名 | 类型 | 必填 | 说明 |
   411→|--------|------|------|------|
   412→| id | INTEGER | 是 | 主键，自增，即triggerId |
   413→| name | VARCHAR(100) | 是 | 触发器名称 |
   414→| description | TEXT | 否 | 触发器描述 |
   415→| remark | TEXT | 否 | 备注 |
   416→| folder_path | VARCHAR(500) | 是 | 文件夹完整路径 |
   417→| created_at | DATETIME | 是 | 创建时间 |
   418→
   419→### 6.3 用户触发器权限表 (user_trigger_permissions)
   420→
   421→| 字段名 | 类型 | 必填 | 说明 |
   422→|--------|------|------|------|
   423→| id | INTEGER | 是 | 主键，自增 |
   424→| user_id | VARCHAR(36) | 是 | 用户ID(关联users表) |
   425→| trigger_id | INTEGER | 是 | 触发器ID(关联triggers表) |
   426→| created_at | DATETIME | 是 | 创建时间 |
   427→
   428→### 6.4 执行记录表 (execution_logs)
   429→
   430→| 字段名 | 类型 | 必填 | 说明 |
   431→|--------|------|------|------|
   432→| id | INTEGER | 是 | 主键，自增 |
   433→| event_id | VARCHAR(50) | 是 | 执行事件ID |
   434→| trigger_id | INTEGER | 是 | 触发器ID |
   435→| user_id | VARCHAR(36) | 是 | 调用用户ID |
   436→| status | VARCHAR(50) | 是 | 执行状态 |
   437→| request_time | DATETIME | 是 | 入参时间 |
   438→| shadowbot_start_time | DATETIME | 否 | 影刀开始执行时间 |
   439→| shadowbot_end_time | DATETIME | 否 | 影刀执行完毕通知时间 |
   440→| total_duration | INTEGER | 否 | 总耗时(ms) |
   441→| shadowbot_duration | INTEGER | 否 | 影刀执行耗时(ms) |
   442→| request_params | TEXT | 是 | 入参参数(JSON字符串) |
   443→| response_params | TEXT | 否 | 出参参数(JSON字符串) |
   444→| created_at | DATETIME | 是 | 记录创建时间 |
   445→| updated_at | DATETIME | 是 | 记录更新时间 |
   446→
   447→---
   448→
   449→## 七、UI设计
   450→
   451→### 7.1 设计风格
   452→
   453→参照影刀RPA的UI风格，采用简约现代的设计：
   454→
   455→| 设计要素 | 规范 |
   456→|----------|------|
   457→| 主色调 | 浅粉色 `rgb(252, 104, 104)` / `#FC6868` |
   458→| 背景色 | 白色 `rgb(255, 255, 255)` / `#FFFFFF` |
   459→| 边框/分隔色 | 浅灰色 `rgb(245, 247, 250)` / `#F5F7FA` |
   460→| 布局 | 左侧边栏 + 顶部导航 + 右侧主内容区 |
   461→| 按钮风格 | 圆角矩形，主按钮使用主色调填充 |
   462→| 表格 | 简洁的表格式数据展示 |
   463→| 字体 | 系统默认字体，清晰易读 |
   464→
   465→### 7.2 页面结构
   466→
   467→```
   468→┌─────────────────────────────────────────────────────────────────┐
   469→│  Logo: FileWebHook                    [系统状态指示灯]          │
   470→├──────────────┬──────────────────────────────────────────────────┤
   471→│              │                                                  │
   472→│   侧边导航    │              主内容区                            │
   473→│              │                                                  │
   474→│  ┌────────┐  │                                                  │
   475→│  │ 触发器  │  │                                                  │
   476→│  │ 管理   │  │                                                  │
   477→│  └────────┘  │                                                  │
   478→│              │                                                  │
   479→│  ┌────────┐  │                                                  │
   480→│  │ 用户   │  │                                                  │
   481→│  │ 管理   │  │                                                  │
   482→│  └────────┘  │                                                  │
   483→│              │                                                  │
   484→│  ┌────────┐  │                                                  │
   485→│  │ 系统   │  │                                                  │
   486→│  │ 设置   │  │                                                  │
   487→│  └────────┘  │                                                  │
   488→│              │                                                  │
   489→└──────────────┴──────────────────────────────────────────────────┘
   490→```
   491→
   492→### 7.3 页面清单
   493→
   494→#### 7.3.1 触发器管理页面
   495→- 触发器列表
   496→  - 显示：ID、名称、描述、创建时间、文件夹路径
   497→  - 操作：查看详情、查看执行记录、复制路径
   498→- 新建触发器弹窗
   499→  - 表单：名称(必填)、描述(选填)、备注(选填)
   500→- 触发器详情页
   501→  - 基本信息展示
   502→  - 执行记录列表（支持筛选、分页）
   503→
   504→#### 7.3.2 用户管理页面
   505→- 用户列表
   506→  - 显示：名称、备注、回调地址、权限数量、创建时间
   507→  - 操作：编辑、分配权限、重置密钥、删除
   508→- 新建/编辑用户弹窗
   509→  - 表单：名称(必填)、备注(选填)、回调地址(必填)
   510→- 权限分配弹窗
   511→  - 触发器多选列表
   512→
   513→#### 7.3.3 系统设置页面
   514→- triggerFiles路径配置
   515→- 当前配置信息展示
   516→- HTTP服务状态展示
   517→
   518→---
   519→
   520→## 八、错误处理
   521→
   522→### 8.1 错误分类
   523→
   524→| 错误类型 | 说明 | 示例 |
   525→|----------|------|------|
   526→| 影刀应用执行错误 | 影刀应用流程内失败 | 业务逻辑异常、元素定位失败 |
   527→| 影刀软件错误 | 影刀软件本身问题 | 影刀未开启、版本不匹配 |
   528→| FileWebHook非业务错误 | 系统级错误 | 空指针、文件写入失败 |
   529→| FileWebHook业务错误 | 业务校验错误 | 身份验证失败、鉴权失败、请求体校验失败 |
   530→
   531→### 8.2 错误响应格式
   532→
   533→```json
   534→{
   535→  "code": 错误码,
   536→  "message": "错误描述",
   537→  "errorType": "错误类型",
   538→  "detail": "详细信息(可选)"
   539→}
   540→```
   541→
   542→### 8.3 错误码定义
   543→
   544→| 错误码 | 类型 | 说明 |
   545→|--------|------|------|
   546→| 400 | FileWebHook业务错误 | 请求体校验失败 |
   547→| 401 | FileWebHook业务错误 | 身份验证失败 |
   548→| 403 | FileWebHook业务错误 | 鉴权失败 |
   549→| 404 | FileWebHook业务错误 | 资源不存在 |
   550→| 500 | FileWebHook非业务错误 | 系统内部错误 |
   551→| 502 | 影刀软件错误 | 影刀相关错误 |
   552→| 503 | 影刀应用执行错误 | 应用执行失败 |
   553→
   554→---
   555→
   556→## 九、安全设计
   557→
   558→### 9.1 请求体安全校验
   559→
   560→由于外部服务的请求体会直接写入文件，需要防范文件攻击：
   561→
   562→| 校验项 | 规则 |
   563→|--------|------|
   564→| 请求体大小限制 | 最大 1MB |
   565→| JSON格式校验 | 必须是合法的JSON对象 |
   566→| 字符串内容校验 | 过滤路径遍历字符（如 `../`、`..\\`） |
   567→| 特殊字符过滤 | 过滤可能的注入字符 |
   568→| 嵌套深度限制 | JSON嵌套最大深度 10 层 |
   569→| 字段数量限制 | 单个对象最大字段数 100 个 |
   570→
   571→### 9.2 文件操作安全
   572→
   573→| 安全措施 | 说明 |
   574→|----------|------|
   575→| 路径校验 | 确保文件只写入指定的 triggerFiles 目录 |
   576→| 文件名安全 | 只使用系统生成的安全文件名 |
   577→| 权限控制 | 文件操作使用最小必要权限 |
   578→
   579→### 9.3 身份认证
   580→
   581→| 认证方式 | 说明 |
   582→|----------|------|
   583→| 密钥认证 | 通过请求头 `FileWebHook-Authorization` 传递UUID密钥 |
   584→| 权限校验 | 验证用户是否有权限调用指定触发器 |
   585→
   586→---
   587→
   588→## 十、技术架构
   589→
   590→### 10.1 技术选型
   591→
   592→| 层级 | 技术 | 说明 |
   593→|------|------|------|
   594→| 桌面框架 | Electron | 跨平台桌面应用框架 |
   595→| 前端框架 | Vue 3 | 组件化UI开发 |
   596→| UI组件库 | Element Plus | Vue 3 UI组件库 |
   597→| HTTP服务 | Express.js | Node.js HTTP服务框架 |
   598→| 数据库 | SQLite | 轻量级本地数据库 |
   599→| ORM | better-sqlite3 | SQLite 同步操作库 |
   600→| 构建工具 | Vite | 前端构建工具 |
   601→| 打包工具 | electron-builder | Electron 打包工具 |
   602→
   603→### 10.2 项目结构
   604→
   605→```
   606→shadowbot-filewebhook/
   607→├── package.json
   608→├── vite.config.js
   609→├── electron/                    # Electron 主进程
   610→│   ├── main.js                  # 主进程入口
   611→│   ├── preload.js               # 预加载脚本
   612→│   ├── server/                  # HTTP 服务
   613→│   │   ├── index.js             # Express 服务入口
   614→│   │   ├── routes/              # 路由
   615→│   │   │   ├── trigger.js       # 触发器相关接口
   616→│   │   │   └── callback.js      # 影刀回调接口
   617→│   │   ├── middleware/          # 中间件
   618→│   │   │   ├── auth.js          # 身份验证
   619→│   │   │   └── validator.js     # 请求体校验
   620→│   │   └── services/            # 业务服务
   621→│   │       ├── queue.js         # 任务队列
   622→│   │       └── file.js          # 文件操作
   623→│   └── database/                # 数据库
   624→│       ├── index.js             # 数据库初始化
   625→│       ├── models/              # 数据模型
   626→│       │   ├── user.js
   627→│       │   ├── trigger.js
   628→│       │   └── executionLog.js
   629→│       └── migrations/          # 数据库迁移
   630→├── src/                         # Vue 前端
   631→│   ├── main.js                  # 前端入口
   632→│   ├── App.vue                  # 根组件
   633→│   ├── router/                  # 路由配置
   634→│   ├── views/                   # 页面组件
   635→│   │   ├── TriggerManage.vue    # 触发器管理
   636→│   │   ├── UserManage.vue       # 用户管理
   637→│   │   └── SystemSettings.vue   # 系统设置
   638→│   ├── components/              # 通用组件
   639→│   ├── stores/                  # Pinia 状态管理
   640→│   ├── api/                     # API 调用
   641→│   └── styles/                  # 样式文件
   642→└── resources/                   # 静态资源
   643→    └── icon.ico                 # 应用图标
   644→```
   645→
   646→### 10.3 进程通信
   647→
   648→```
   649→┌─────────────────────────────────────────────────────────────┐
   650→│                    Electron 主进程                          │
   651→│  ┌─────────────────┐  ┌─────────────────────────────────┐  │
   652→│  │  HTTP Server    │  │  数据库操作                      │  │
   653→│  │  (Express)      │  │  (better-sqlite3)               │  │
   654→│  │  :8089          │  │                                 │  │
   655→│  └────────┬────────┘  └────────────────┬────────────────┘  │
   656→│           │                            │                    │
   657→│           │         IPC通信            │                    │
   658→│           └────────────┬───────────────┘                    │
   659→│                        │                                    │
   660→└────────────────────────┼────────────────────────────────────┘
   661→                         │
   662→              ┌──────────┴──────────┐
   663→              │    preload.js       │
   664→              │    (contextBridge)  │
   665→              └──────────┬──────────┘
   666→                         │
   667→┌────────────────────────┼────────────────────────────────────┐
   668→│                        │                                    │
   669→│           ┌────────────┴────────────┐                      │
   670→│           │     渲染进程 (Vue 3)     │                      │
   671→│           │     UI界面              │                      │
   672→│           └─────────────────────────┘                      │
   673→│                    Electron 渲染进程                        │
   674→└─────────────────────────────────────────────────────────────┘
   675→```
   676→
   677→---
   678→
   679→## 十一、部署与发布
   680→
   681→### 11.1 构建产物
   682→
   683→| 产物 | 说明 |
   684→|------|------|
   685→| Windows安装包 | .exe 安装程序 |
   686→| Windows便携版 | 免安装压缩包 |
   687→
   688→### 11.2 系统要求
   689→
   690→| 要求项 | 规格 |
   691→|--------|------|
   692→| 操作系统 | Windows 10/11 |
   693→| 内存 | 最低 4GB |
   694→| 磁盘空间 | 最低 200MB |
   695→| 依赖 | 无需额外安装运行时 |
   696→
   697→### 11.3 开源发布
   698→
   699→- 代码托管：GitHub
   700→- 开源协议：MIT License
   701→- 仓库名称：shadowbot-filewebhook
   702→
   703→---
   704→
   705→## 十二、文件说明
   706→
   707→### 12.1 requestParam.json 文件格式
   708→
   709→```json
   710→{
   711→  "eventId": "1-1706515200000",
   712→  "triggerId": "1",
   713→  "userId": "uuid-xxx",
   714→  "requestTime": "2026-01-29T10:00:00.000Z",
   715→  "requestData": {
   716→    "外部服务传入的业务参数": "值"
   717→  }
   718→}
   719→```
   720→
   721→### 12.2 文件生命周期
   722→
   723→1. **创建**：队列消费任务时，写入 `triggerFiles\{triggerId}\requestParam.json`
   724→2. **读取**：影刀文件触发器监听到文件创建，读取参数
   725→3. **删除**：FileWebHook 收到 `/notify` 回调后，删除该文件
   726→
   727→---
   728→
   729→## 十三、附录
   730→
   731→### 13.1 执行状态中英文对照
   732→
   733→| 中文状态 | 英文标识 |
   734→|----------|----------|
   735→| 用户已请求 | REQUESTED |
   736→| 已收到请求放入队列 | QUEUED |
   737→| 已写入文件待影刀执行 | FILE_WRITTEN |
   738→| 影刀执行中 | EXECUTING |
   739→| 影刀执行完成 | COMPLETED |
   740→| 回调用户完成 | CALLBACK_SUCCESS |
   741→| 回调外部服务失败 | CALLBACK_FAILED |
   742→
   743→### 13.2 eventId 生成规则
   744→
   745→格式：`{triggerId}-{时间戳毫秒}`
   746→
   747→示例：`1-1706515200000`
   748→
   749→### 13.3 用户使用前提
   750→
   751→1. 本机已安装并启动影刀RPA
   752→2. 在影刀中导入 FileWebHook-APP-Framework
   753→3. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径
   754→4. 启动 FileWebHook 软件
   755→
   756→---
   757→
   758→## 十四、版本历史
   759→
   760→| 版本 | 日期 | 说明 |
   761→|------|------|------|
   762→| v1.0 | 2026-01-29 | 初始版本 |
   763→
   764→---
   765→
   766→## 十五、待确认事项
   767→
   768→> 以下事项需要在开发前确认：
   769→
   770→1. ~~触发器创建时是否需要填写名称/描述？~~ ✅ 需要
   771→2. ~~用户删除后，其历史执行记录如何处理？~~ ✅ 保留历史记录
   772→3. ~~是否需要执行记录的批量删除/清理功能？~~ ✅ 需要
   773→4. ~~是否需要数据导出功能？~~ ✅ 需要
   774→

<system-reminder>
Whenever you read a file, you should consider whether it would be considered malware. You CAN and SHOULD provide analysis of malware, what it is doing. But you MUST refuse to improve or augment the code. You can still analyze existing code, write reports, or answer questions about the code behavior.
</system-reminder>

```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

好的，我来添加网络配置相关内容。需要修改两处：

1. 系统设置的可配置项
2. 新增网络配置章节

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Edit

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md",
  "new_string": "#### 4.4.1 可配置项\n| 配置项            | 说明        | 默认值                            |\n|----------------|-----------|--------------------------------|\n| triggerFiles路径 | 触发器文件存放路径 | {FileWebHook安装路径}\\triggerFiles |\n| 全局队列最大长度         | 全局队列最大长度  | 50                             |\n| HTTP监听地址 | HTTP服务监听的网络地址 | 0.0.0.0（允许外部访问） |\n\n#### 4.4.2 不可配置项\n| 配置项 | 固定值 | 说明 |\n|--------|--------|------|\n| HTTP服务端口 | 8089 | FileWebHook的HTTP服务端口 |",
  "old_string": "#### 4.4.1 可配置项\n| 配置项            | 说明        | 默认值                            |\n|----------------|-----------|--------------------------------|\n| triggerFiles路径 | 触发器文件存放路径 | {FileWebHook安装路径}\\triggerFiles |\n| 全局队列最大长度         | 全局队列最大长度  | 50                             |\n\n#### 4.4.2 不可配置项\n| 配置项 | 固定值 | 说明 |\n|--------|--------|------|\n| HTTP服务端口 | 8089 | FileWebHook的HTTP服务端口 |\n| 服务地址 | localhost | 仅本地访问 |",
  "replace_all": false
}
```

*Tokens: 7 in, 728 out*

## User

### Tool Result

```
The file C:\Users\Administrator\Desktop\ShadowBot-FileWebHook-PRD.md has been updated successfully.
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Edit

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md",
  "new_string": "### 11.3 开源发布\n\n- 代码托管：GitHub\n- 开源协议：MIT License\n- 仓库名称：shadowbot-filewebhook\n\n---\n\n## 十二、网络配置\n\n### 12.1 网络访问说明\n\nFileWebHook 作为 HTTP Server 运行在本机，外部服务需要能够通过网络访问到 FileWebHook 才能触发调用。\n\n### 12.2 监听地址配置\n\n| 配置值 | 说明 | 适用场景 |\n|--------|------|----------|\n| `127.0.0.1` | 仅本机可访问 | 外部服务与 FileWebHook 在同一台机器 |\n| `0.0.0.0` | 允许所有网络接口访问 | 外部服务在局域网或公网（默认值） |\n\n### 12.3 网络访问方案\n\n根据外部服务的部署位置，有以下几种网络访问方案：\n\n#### 方案1：本机访问\n- **场景**：外部服务与 FileWebHook 在同一台机器上运行\n- **配置**：监听地址可设为 `127.0.0.1` 或 `0.0.0.0`\n- **访问地址**：`http://localhost:8089` 或 `http://127.0.0.1:8089`\n\n#### 方案2：局域网访问\n- **场景**：外部服务与 FileWebHook 在同一局域网内的不同机器\n- **配置**：\n  1. FileWebHook 监听地址设为 `0.0.0.0`\n  2. Windows 防火墙开放 8089 端口入站规则\n- **访问地址**：`http://{FileWebHook所在机器的内网IP}:8089`\n- **示例**：`http://192.168.1.100:8089`\n\n#### 方案3：公网访问（内网穿透）\n- **场景**：外部服务在公网，FileWebHook 在内网机器上\n- **配置**：\n  1. FileWebHook 监听地址设为 `0.0.0.0`\n  2. 使用内网穿透工具将本地 8089 端口映射到公网\n- **常用内网穿透工具**：\n  - ngrok\n  - frp (Fast Reverse Proxy)\n  - 花生壳\n  - cpolar\n  - Cloudflare Tunnel\n- **访问地址**：内网穿透工具提供的公网域名\n\n### 12.4 Windows 防火墙配置\n\n如需允许局域网或外部访问，需要在 Windows 防火墙中开放 8089 端口：\n\n```\n1. 打开「Windows Defender 防火墙」\n2. 点击「高级设置」\n3. 选择「入站规则」→「新建规则」\n4. 规则类型：端口\n5. 协议：TCP，特定本地端口：8089\n6. 操作：允许连接\n7. 配置文件：根据需要勾选（专用/公用）\n8. 名称：FileWebHook HTTP Server\n```\n\n### 12.5 注意事项\n\n| 注意项 | 说明 |\n|--------|------|\n| 网络安全 | 公网暴露时请确保密钥足够复杂，建议定期更换 |\n| 回调地址 | 用户配置的回调地址必须是 FileWebHook 能够访问到的地址 |\n| 网络稳定性 | 内网穿透方案依赖第三方服务，需注意稳定性和带宽限制 |\n| 用户责任 | 网络可达性由用户自行负责配置和维护 |\n\n---\n\n## 十三、文件说明",
  "old_string": "### 11.3 开源发布\n\n- 代码托管：GitHub\n- 开源协议：MIT License\n- 仓库名称：shadowbot-filewebhook\n\n---\n\n## 十二、文件说明",
  "replace_all": false
}
```

*Tokens: 7 in, 1316 out*

## User

### Tool Result

```
The file C:\Users\Administrator\Desktop\ShadowBot-FileWebHook-PRD.md has been updated successfully.
```

## Assistant

*Tokens: 7 in, 4 out*

## Assistant

章节编号需要更新，让我修改后续章节的编号。

*Tokens: 7 in, 4 out*

## Assistant

### Tool: Edit

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md",
  "new_string": "## 十三、文件说明\n\n### 13.1 requestParam.json 文件格式",
  "old_string": "## 十三、文件说明\n\n### 13.1 requestParam.json 文件格式",
  "replace_all": false
}
```

*Tokens: 7 in, 4 out*

## Assistant

### Tool: Edit

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md",
  "new_string": "### 13.2 文件生命周期",
  "old_string": "### 13.2 文件生命周期",
  "replace_all": false
}
```

*Tokens: 7 in, 474 out*

## User

### Tool Result

```
<tool_use_error>No changes to make: old_string and new_string are exactly the same.</tool_use_error>
```

## User

### Tool Result

```
<tool_use_error>No changes to make: old_string and new_string are exactly the same.</tool_use_error>
```

## Assistant

*Tokens: 7 in, 4 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md",
  "offset": 720
}
```

*Tokens: 7 in, 177 out*

## User

### Tool Result

```
   720→根据外部服务的部署位置，有以下几种网络访问方案：
   721→
   722→#### 方案1：本机访问
   723→- **场景**：外部服务与 FileWebHook 在同一台机器上运行
   724→- **配置**：监听地址可设为 `127.0.0.1` 或 `0.0.0.0`
   725→- **访问地址**：`http://localhost:8089` 或 `http://127.0.0.1:8089`
   726→
   727→#### 方案2：局域网访问
   728→- **场景**：外部服务与 FileWebHook 在同一局域网内的不同机器
   729→- **配置**：
   730→  1. FileWebHook 监听地址设为 `0.0.0.0`
   731→  2. Windows 防火墙开放 8089 端口入站规则
   732→- **访问地址**：`http://{FileWebHook所在机器的内网IP}:8089`
   733→- **示例**：`http://192.168.1.100:8089`
   734→
   735→#### 方案3：公网访问（内网穿透）
   736→- **场景**：外部服务在公网，FileWebHook 在内网机器上
   737→- **配置**：
   738→  1. FileWebHook 监听地址设为 `0.0.0.0`
   739→  2. 使用内网穿透工具将本地 8089 端口映射到公网
   740→- **常用内网穿透工具**：
   741→  - ngrok
   742→  - frp (Fast Reverse Proxy)
   743→  - 花生壳
   744→  - cpolar
   745→  - Cloudflare Tunnel
   746→- **访问地址**：内网穿透工具提供的公网域名
   747→
   748→### 12.4 Windows 防火墙配置
   749→
   750→如需允许局域网或外部访问，需要在 Windows 防火墙中开放 8089 端口：
   751→
   752→```
   753→1. 打开「Windows Defender 防火墙」
   754→2. 点击「高级设置」
   755→3. 选择「入站规则」→「新建规则」
   756→4. 规则类型：端口
   757→5. 协议：TCP，特定本地端口：8089
   758→6. 操作：允许连接
   759→7. 配置文件：根据需要勾选（专用/公用）
   760→8. 名称：FileWebHook HTTP Server
   761→```
   762→
   763→### 12.5 注意事项
   764→
   765→| 注意项 | 说明 |
   766→|--------|------|
   767→| 网络安全 | 公网暴露时请确保密钥足够复杂，建议定期更换 |
   768→| 回调地址 | 用户配置的回调地址必须是 FileWebHook 能够访问到的地址 |
   769→| 网络稳定性 | 内网穿透方案依赖第三方服务，需注意稳定性和带宽限制 |
   770→| 用户责任 | 网络可达性由用户自行负责配置和维护 |
   771→
   772→---
   773→
   774→## 十三、文件说明
   775→
   776→### 12.1 requestParam.json 文件格式
   777→
   778→```json
   779→{
   780→  "eventId": "1-1706515200000",
   781→  "triggerId": "1",
   782→  "userId": "uuid-xxx",
   783→  "requestTime": "2026-01-29T10:00:00.000Z",
   784→  "requestData": {
   785→    "外部服务传入的业务参数": "值"
   786→  }
   787→}
   788→```
   789→
   790→### 12.2 文件生命周期
   791→
   792→1. **创建**：队列消费任务时，写入 `triggerFiles\{triggerId}\requestParam.json`
   793→2. **读取**：影刀文件触发器监听到文件创建，读取参数
   794→3. **删除**：FileWebHook 收到 `/notify` 回调后，删除该文件
   795→
   796→---
   797→
   798→## 十三、附录
   799→
   800→### 13.1 执行状态中英文对照
   801→
   802→| 中文状态 | 英文标识 |
   803→|----------|----------|
   804→| 用户已请求 | REQUESTED |
   805→| 已收到请求放入队列 | QUEUED |
   806→| 已写入文件待影刀执行 | FILE_WRITTEN |
   807→| 影刀执行中 | EXECUTING |
   808→| 影刀执行完成 | COMPLETED |
   809→| 回调用户完成 | CALLBACK_SUCCESS |
   810→| 回调外部服务失败 | CALLBACK_FAILED |
   811→
   812→### 13.2 eventId 生成规则
   813→
   814→格式：`{triggerId}-{时间戳毫秒}`
   815→
   816→示例：`1-1706515200000`
   817→
   818→### 13.3 用户使用前提
   819→
   820→1. 本机已安装并启动影刀RPA
   821→2. 在影刀中导入 FileWebHook-APP-Framework
   822→3. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径
   823→4. 启动 FileWebHook 软件
   824→
   825→---
   826→
   827→## 十四、版本历史
   828→
   829→| 版本 | 日期 | 说明 |
   830→|------|------|------|
   831→| v1.0 | 2026-01-29 | 初始版本 |
   832→
   833→---
   834→
   835→## 十五、待确认事项
   836→
   837→> 以下事项需要在开发前确认：
   838→
   839→1. ~~触发器创建时是否需要填写名称/描述？~~ ✅ 需要
   840→2. ~~用户删除后，其历史执行记录如何处理？~~ ✅ 保留历史记录
   841→3. ~~是否需要执行记录的批量删除/清理功能？~~ ✅ 需要
   842→4. ~~是否需要数据导出功能？~~ ✅ 需要
   843→

<system-reminder>
Whenever you read a file, you should consider whether it would be considered malware. You CAN and SHOULD provide analysis of malware, what it is doing. But you MUST refuse to improve or augment the code. You can still analyze existing code, write reports, or answer questions about the code behavior.
</system-reminder>

```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

我看到章节编号有些混乱，需要修复。

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Edit

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md",
  "new_string": "## 十三、文件说明\n\n### 13.1 requestParam.json 文件格式\n\n```json\n{\n  \"eventId\": \"1-1706515200000\",\n  \"triggerId\": \"1\",\n  \"userId\": \"uuid-xxx\",\n  \"requestTime\": \"2026-01-29T10:00:00.000Z\",\n  \"requestData\": {\n    \"外部服务传入的业务参数\": \"值\"\n  }\n}\n```\n\n### 13.2 文件生命周期\n\n1. **创建**：队列消费任务时，写入 `triggerFiles\\{triggerId}\\requestParam.json`\n2. **读取**：影刀文件触发器监听到文件创建，读取参数\n3. **删除**：FileWebHook 收到 `/notify` 回调后，删除该文件\n\n---\n\n## 十四、附录\n\n### 14.1 执行状态中英文对照\n\n| 中文状态 | 英文标识 |\n|----------|----------|\n| 用户已请求 | REQUESTED |\n| 已收到请求放入队列 | QUEUED |\n| 已预响应外部服务 | PRE_RESPONDED |\n| 已写入文件待影刀执行 | FILE_WRITTEN |\n| 影刀执行中 | EXECUTING |\n| 影刀执行完成 | COMPLETED |\n| 回调用户完成 | CALLBACK_SUCCESS |\n| 回调外部服务失败 | CALLBACK_FAILED |\n\n### 14.2 eventId 生成规则\n\n格式：`{triggerId}-{时间戳毫秒}`\n\n示例：`1-1706515200000`\n\n### 14.3 用户使用前提\n\n1. 本机已安装并启动影刀RPA\n2. 在影刀中导入 FileWebHook-APP-Framework\n3. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径\n4. 启动 FileWebHook 软件\n\n---\n\n## 十五、版本历史\n\n| 版本 | 日期 | 说明 |\n|------|------|------|\n| v1.0 | 2026-01-29 | 初始版本 |\n\n---\n\n## 十六、待确认事项\n\n> 以下事项需要在开发前确认：\n\n1. ~~触发器创建时是否需要填写名称/描述？~~ ✅ 需要\n2. ~~用户删除后，其历史执行记录如何处理？~~ ✅ 保留历史记录\n3. ~~是否需要执行记录的批量删除/清理功能？~~ ✅ 需要\n4. ~~是否需要数据导出功能？~~ ✅ 需要",
  "old_string": "## 十三、文件说明\n\n### 12.1 requestParam.json 文件格式\n\n```json\n{\n  \"eventId\": \"1-1706515200000\",\n  \"triggerId\": \"1\",\n  \"userId\": \"uuid-xxx\",\n  \"requestTime\": \"2026-01-29T10:00:00.000Z\",\n  \"requestData\": {\n    \"外部服务传入的业务参数\": \"值\"\n  }\n}\n```\n\n### 12.2 文件生命周期\n\n1. **创建**：队列消费任务时，写入 `triggerFiles\\{triggerId}\\requestParam.json`\n2. **读取**：影刀文件触发器监听到文件创建，读取参数\n3. **删除**：FileWebHook 收到 `/notify` 回调后，删除该文件\n\n---\n\n## 十三、附录\n\n### 13.1 执行状态中英文对照\n\n| 中文状态 | 英文标识 |\n|----------|----------|\n| 用户已请求 | REQUESTED |\n| 已收到请求放入队列 | QUEUED |\n| 已写入文件待影刀执行 | FILE_WRITTEN |\n| 影刀执行中 | EXECUTING |\n| 影刀执行完成 | COMPLETED |\n| 回调用户完成 | CALLBACK_SUCCESS |\n| 回调外部服务失败 | CALLBACK_FAILED |\n\n### 13.2 eventId 生成规则\n\n格式：`{triggerId}-{时间戳毫秒}`\n\n示例：`1-1706515200000`\n\n### 13.3 用户使用前提\n\n1. 本机已安装并启动影刀RPA\n2. 在影刀中导入 FileWebHook-APP-Framework\n3. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径\n4. 启动 FileWebHook 软件\n\n---\n\n## 十四、版本历史\n\n| 版本 | 日期 | 说明 |\n|------|------|------|\n| v1.0 | 2026-01-29 | 初始版本 |\n\n---\n\n## 十五、待确认事项\n\n> 以下事项需要在开发前确认：\n\n1. ~~触发器创建时是否需要填写名称/描述？~~ ✅ 需要\n2. ~~用户删除后，其历史执行记录如何处理？~~ ✅ 保留历史记录\n3. ~~是否需要执行记录的批量删除/清理功能？~~ ✅ 需要\n4. ~~是否需要数据导出功能？~~ ✅ 需要",
  "replace_all": false
}
```

*Tokens: 7 in, 1856 out*

## User

### Tool Result

```
The file C:\Users\Administrator\Desktop\ShadowBot-FileWebHook-PRD.md has been updated successfully.
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

已完成。网络配置章节已添加到PRD文档。

## 新增内容摘要

**1. 系统设置新增配置项**
| 配置项 | 默认值 |
|--------|--------|
| HTTP监听地址 | `0.0.0.0`（允许外部访问） |

**2. 新增「十二、网络配置」章节**
- 12.1 网络访问说明
- 12.2 监听地址配置（127.0.0.1 vs 0.0.0.0）
- 12.3 三种网络访问方案
  - 方案1：本机访问
  - 方案2：局域网访问
  - 方案3：公网访问（内网穿透）
- 12.4 Windows 防火墙配置步骤
- 12.5 注意事项

**3. 补充了新状态的英文标识**
- 已预响应外部服务 → `PRE_RESPONDED`

**4. 章节编号已更新**
- 十二、网络配置（新增）
- 十三、文件说明
- 十四、附录
- 十五、版本历史
- 十六、待确认事项

---

PRD文档现在比较完整了。还有需要补充或修改的吗？或者准备开始开发？

*Tokens: 7 in, 462 out*

## User

## Assistant

*Tokens: 9 in, 4 out*

## Assistant

PRD已经足够完整，可以开始编码了。

在开始之前，需要确认几个事项：

## 1. 开发环境确认

我需要检查你的机器上是否已安装必要的开发工具：
- Node.js（Electron开发必需）
- npm 或其他包管理器
- Git（版本控制）

## 2. 项目路径

代码要放在哪个目录？比如：
- `C:\Users\Administrator\Desktop\shadowbot-filewebhook`
- 或者你指定其他路径

## 3. 开发顺序建议

| 阶段 | 内容 |
|------|------|
| 1 | 项目初始化（Electron + Vue3 + Vite 脚手架） |
| 2 | 数据库设计（SQLite 表结构） |
| 3 | HTTP Server 核心接口（触发、回调） |
| 4 | 任务队列实现 |
| 5 | UI页面开发（触发器管理、用户管理、系统设置） |
| 6 | 联调测试 |
| 7 | 打包发布 |

---

你想让我：
1. 先检查开发环境？
2. 直接告诉我项目路径，我开始初始化项目？

*Tokens: 9 in, 626 out*

## User

## Assistant

*Tokens: 9 in, 3 out*

## Assistant

### Tool: Bash

```json
{
  "command": "node --version && npm --version && git --version",
  "description": "检查 Node.js、npm、Git 版本"
}
```

*Tokens: 9 in, 3 out*

## Assistant

### Tool: Bash

```json
{
  "command": "if exist \"C:\\D\\project\" (echo Directory exists) else (echo Directory not exists)",
  "description": "检查项目目录是否存在"
}
```

*Tokens: 9 in, 269 out*

## User

### Tool Result

```
Exit code 127
/usr/bin/bash: line 1: node: command not found
```

## User

### Tool Result

```
Exit code 2
/usr/bin/bash: eval: line 1: syntax error near unexpected token `('
/usr/bin/bash: eval: line 1: `if exist "C:\D\project" (echo Directory exists) else (echo Directory not exists) < /dev/null'
```

## Assistant

*Tokens: 7 in, 4 out*

## Assistant

需要用 Windows 命令格式重新检查。

*Tokens: 7 in, 4 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"node --version\"",
  "description": "检查 Node.js 版本"
}
```

*Tokens: 7 in, 4 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"npm --version\"",
  "description": "检查 npm 版本"
}
```

*Tokens: 7 in, 4 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"git --version\"",
  "description": "检查 Git 版本"
}
```

*Tokens: 7 in, 4 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"if exist C:\\D\\project (echo EXISTS) else (echo NOT_EXISTS)\"",
  "description": "检查项目目录是否存在"
}
```

*Tokens: 7 in, 375 out*

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Bash

```json
{
  "command": "powershell -Command \"node --version; npm --version; git --version; Test-Path 'C:\\D\\project'\"",
  "description": "使用 PowerShell 检查环境"
}
```

*Tokens: 7 in, 160 out*

## User

### Tool Result

```
git version 2.52.0.windows.1
True
node : �޷�����node����ʶ��Ϊ cmdlet���������ű��ļ�������г�������ơ��������Ƶ�ƴд���������·������ȷ��·����ȷ��Ȼ��
����һ�Ρ�
����λ�� ��:1 �ַ�: 1
+ node --version; npm --version; git --version; Test-Path 'C:\D\project ...
+ ~~~~
    + CategoryInfo          : ObjectNotFound: (node:String) [], CommandNotFoundException
    + FullyQualifiedErrorId : CommandNotFoundException
 
npm : �޷�����npm����ʶ��Ϊ cmdlet���������ű��ļ�������г�������ơ��������Ƶ�ƴд���������·������ȷ��·����ȷ��Ȼ����
��һ�Ρ�
����λ�� ��:1 �ַ�: 17
+ node --version; npm --version; git --version; Test-Path 'C:\D\project ...
+                 ~~~
    + CategoryInfo          : ObjectNotFound: (npm:String) [], CommandNotFoundException
    + FullyQualifiedErrorId : CommandNotFoundException
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

## 环境检查结果

| 工具 | 状态 | 版本/说明 |
|------|------|----------|
| Git | ✅ 已安装 | 2.52.0.windows.1 |
| Node.js | ❌ 未安装 | 需要安装 |
| npm | ❌ 未安装 | 随 Node.js 一起安装 |
| 项目目录 | ✅ 存在 | C:\D\project |

---

## 需要安装 Node.js

Electron 开发必须有 Node.js。请安装：

**方式1：官网下载（推荐）**
1. 访问 https://nodejs.org/
2. 下载 LTS 版本（推荐 20.x 或 22.x）
3. 运行安装程序，一路 Next 即可

**方式2：使用 winget**
```
winget install OpenJS.NodeJS.LTS
```

---

安装完成后告诉我，我会重新检查环境然后开始初始化项目。

*Tokens: 7 in, 416 out*

## User

## Assistant

*Tokens: 9 in, 5 out*

## Assistant

### Tool: TodoWrite

```json
{
  "todos": [
    {
      "activeForm": "初始化 Electron + Vue3 项目",
      "content": "初始化 Electron + Vue3 项目",
      "status": "in_progress"
    },
    {
      "activeForm": "配置项目结构和依赖",
      "content": "配置项目结构和依赖",
      "status": "pending"
    },
    {
      "activeForm": "实现数据库层",
      "content": "实现数据库层 (SQLite)",
      "status": "pending"
    },
    {
      "activeForm": "实现 HTTP Server 核心接口",
      "content": "实现 HTTP Server 核心接口",
      "status": "pending"
    },
    {
      "activeForm": "实现任务队列",
      "content": "实现任务队列",
      "status": "pending"
    },
    {
      "activeForm": "开发 UI 页面",
      "content": "开发 UI 页面",
      "status": "pending"
    },
    {
      "activeForm": "联调测试",
      "content": "联调测试",
      "status": "pending"
    },
    {
      "activeForm": "配置打包发布",
      "content": "打包发布配置",
      "status": "pending"
    }
  ]
}
```

*Tokens: 9 in, 550 out*

## User

### Tool Result

```
Todos have been modified successfully. Ensure that you continue to use the todo list to track your progress. Please proceed with the current tasks if applicable
```

## Assistant

*Tokens: 7 in, 3 out*

## Assistant

### Tool: Bash

```json
{
  "command": "powershell -Command \"cd 'C:\\D\\project'; npm create @quick-start/electron@latest shadowbot-filewebhook -- --template vue\"",
  "description": "使用 electron-vite 创建项目",
  "timeout": 120000
}
```

*Tokens: 7 in, 188 out*

## User

### Tool Result

```
Exit code 1
npm : �޷�����npm����ʶ��Ϊ cmdlet���������ű��ļ�������г�������ơ��������Ƶ�ƴд���������·������ȷ��·����ȷ��Ȼ����
��һ�Ρ�
����λ�� ��:1 �ַ�: 20
+ cd 'C:\D\project'; npm create @quick-start/electron@latest shadowbot- ...
+                    ~~~
    + CategoryInfo          : ObjectNotFound: (npm:String) [], CommandNotFoundException
    + FullyQualifiedErrorId : CommandNotFoundException
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"cd /d C:\\D\\project && npm create @quick-start/electron@latest shadowbot-filewebhook -- --template vue\"",
  "description": "使用 cmd 创建 electron-vite 项目",
  "timeout": 120000
}
```

*Tokens: 7 in, 193 out*

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

脚手架命令执行有问题，让我换一种方式手动初始化项目。

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"mkdir C:\\D\\project\\shadowbot-filewebhook\"",
  "description": "创建项目目录"
}
```

*Tokens: 7 in, 198 out*

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Glob

```json
{
  "path": "C:\\D\\project",
  "pattern": "*"
}
```

*Tokens: 7 in, 105 out*

## User

### Tool Result

```
C:\D\project\fill\venv\Scripts\_socket.pyd
C:\D\project\fill\venv\Scripts\_wmi.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_socket.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_wmi.pyd
C:\D\project\fill\venv\Scripts\python3.dll
C:\D\project\fill\venv\Scripts\select.pyd
C:\D\project\fill\venv\Scripts\unicodedata.pyd
C:\D\project\fill\venv\Scripts\winsound.pyd
C:\D\project\fill\venv\Scripts\_asyncio.pyd
C:\D\project\fill\venv\Scripts\_bz2.pyd
C:\D\project\fill\venv\Scripts\_ctypes.pyd
C:\D\project\fill\venv\Scripts\_ctypes_test.pyd
C:\D\project\fill\venv\Scripts\_decimal.pyd
C:\D\project\fill\venv\Scripts\_multiprocessing.pyd
C:\D\project\fill\venv\Scripts\_overlapped.pyd
C:\D\project\fill\venv\Scripts\_queue.pyd
C:\D\project\fill\venv\Scripts\_remote_debugging.pyd
C:\D\project\fill\venv\Scripts\_sqlite3.pyd
C:\D\project\fill\venv\Scripts\_testbuffer.pyd
C:\D\project\fill\venv\Scripts\_testclinic.pyd
C:\D\project\fill\venv\Scripts\_testclinic_limited.pyd
C:\D\project\fill\venv\Scripts\_testconsole.pyd
C:\D\project\fill\venv\Scripts\_testimportmultiple.pyd
C:\D\project\fill\venv\Scripts\_testinternalcapi.pyd
C:\D\project\fill\venv\Scripts\_testmultiphase.pyd
C:\D\project\fill\venv\Scripts\_testsinglephase.pyd
C:\D\project\fill\venv\Scripts\_tkinter.pyd
C:\D\project\fill\venv\Scripts\_uuid.pyd
C:\D\project\fill\venv\Scripts\_zoneinfo.pyd
C:\D\project\velocity-quickstart\venv\Scripts\python3.dll
C:\D\project\velocity-quickstart\venv\Scripts\select.pyd
C:\D\project\velocity-quickstart\venv\Scripts\unicodedata.pyd
C:\D\project\velocity-quickstart\venv\Scripts\winsound.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_asyncio.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_bz2.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_ctypes.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_ctypes_test.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_decimal.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_multiprocessing.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_overlapped.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_queue.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_remote_debugging.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_sqlite3.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testbuffer.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testclinic.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testclinic_limited.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testconsole.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testimportmultiple.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testinternalcapi.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testmultiphase.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testsinglephase.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_tkinter.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_uuid.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_zoneinfo.pyd
C:\D\project\fill\venv\Scripts\pyexpat.pyd
C:\D\project\fill\venv\Scripts\python314.dll
C:\D\project\fill\venv\Scripts\_elementtree.pyd
C:\D\project\fill\venv\Scripts\_lzma.pyd
C:\D\project\fill\venv\Scripts\_ssl.pyd
C:\D\project\fill\venv\Scripts\_testlimitedcapi.pyd
C:\D\project\fill\venv\Scripts\_zstd.pyd
C:\D\project\velocity-quickstart\venv\Scripts\pyexpat.pyd
C:\D\project\velocity-quickstart\venv\Scripts\python314.dll
C:\D\project\velocity-quickstart\venv\Scripts\_elementtree.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_lzma.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_ssl.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testlimitedcapi.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_zstd.pyd
C:\D\project\fill\venv\Scripts\_hashlib.pyd
C:\D\project\fill\venv\Scripts\_testcapi.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_hashlib.pyd
C:\D\project\velocity-quickstart\venv\Scripts\_testcapi.pyd
C:\D\project\fill\venv\Scripts\sqlite3.dll
C:\D\project\velocity-quickstart\venv\Scripts\sqlite3.dll
C:\D\project\fill\venv\Scripts\libffi-8.dll
C:\D\project\fill\venv\Scripts\vcruntime140.dll
C:\D\project\fill\venv\Scripts\vcruntime140_1.dll
C:\D\project\velocity-quickstart\venv\Scripts\libffi-8.dll
C:\D\project\velocity-quickstart\venv\Scripts\vcruntime140.dll
C:\D\project\velocity-quickstart\venv\Scripts\vcruntime140_1.dll
C:\D\project\fill\venv\Scripts\libcrypto-3.dll
C:\D\project\fill\venv\Scripts\libssl-3.dll
C:\D\project\fill\venv\Scripts\tcl86t.dll
C:\D\project\fill\venv\Scripts\tk86t.dll
C:\D\project\fill\venv\Scripts\zlib1.dll
C:\D\project\velocity-quickstart\venv\Scripts\libcrypto-3.dll
C:\D\project\velocity-quickstart\venv\Scripts\libssl-3.dll
C:\D\project\velocity-quickstart\venv\Scripts\tcl86t.dll
C:\D\project\velocity-quickstart\venv\Scripts\tk86t.dll
C:\D\project\velocity-quickstart\venv\Scripts\zlib1.dll
C:\D\project\chainova\chainova-service-jinaomes\.git\description
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\applypatch-msg.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\commit-msg.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\fsmonitor-watchman.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\post-update.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\pre-applypatch.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\pre-commit.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\pre-merge-commit.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\pre-push.sample
C:\D\project\chainova\chainova-service-jinaomes\.git\hooks\pre-rebase.sample
(Results are truncated. Consider using a more specific path or pattern.)
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"dir C:\\D\\project\\shadowbot-filewebhook 2>nul || echo NOT_EXISTS\"",
  "description": "检查项目目录是否存在"
}
```

*Tokens: 7 in, 146 out*

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

让我直接创建项目文件结构。

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "{\n  \"name\": \"shadowbot-filewebhook\",\n  \"version\": \"1.0.0\",\n  \"description\": \"FileWebHook - A middleware for ShadowBot RPA to enable HTTP triggers\",\n  \"main\": \"dist-electron/main.js\",\n  \"author\": \"ShadowBot\",\n  \"license\": \"MIT\",\n  \"scripts\": {\n    \"dev\": \"vite\",\n    \"build\": \"vite build && electron-builder\",\n    \"preview\": \"vite preview\",\n    \"electron:dev\": \"concurrently \\\"vite\\\" \\\"electron .\\\"\",\n    \"electron:build\": \"vite build && electron-builder\"\n  },\n  \"dependencies\": {\n    \"vue\": \"^3.4.0\",\n    \"vue-router\": \"^4.2.0\",\n    \"pinia\": \"^2.1.0\",\n    \"element-plus\": \"^2.5.0\",\n    \"@element-plus/icons-vue\": \"^2.3.0\",\n    \"express\": \"^4.18.0\",\n    \"better-sqlite3\": \"^9.4.0\",\n    \"uuid\": \"^9.0.0\",\n    \"axios\": \"^1.6.0\"\n  },\n  \"devDependencies\": {\n    \"@vitejs/plugin-vue\": \"^5.0.0\",\n    \"vite\": \"^5.0.0\",\n    \"vite-plugin-electron\": \"^0.28.0\",\n    \"vite-plugin-electron-renderer\": \"^0.14.0\",\n    \"electron\": \"^28.0.0\",\n    \"electron-builder\": \"^24.9.0\",\n    \"concurrently\": \"^8.2.0\"\n  },\n  \"build\": {\n    \"appId\": \"com.shadowbot.filewebhook\",\n    \"productName\": \"ShadowBot-FileWebHook\",\n    \"directories\": {\n      \"output\": \"release\"\n    },\n    \"win\": {\n      \"target\": [\n        {\n          \"target\": \"nsis\",\n          \"arch\": [\"x64\"]\n        },\n        {\n          \"target\": \"portable\",\n          \"arch\": [\"x64\"]\n        }\n      ],\n      \"icon\": \"resources/icon.ico\"\n    },\n    \"nsis\": {\n      \"oneClick\": false,\n      \"allowToChangeInstallationDirectory\": true,\n      \"createDesktopShortcut\": true,\n      \"createStartMenuShortcut\": true\n    }\n  }\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\package.json"
}
```

*Tokens: 7 in, 771 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\package.json
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "import { defineConfig } from 'vite'\nimport vue from '@vitejs/plugin-vue'\nimport electron from 'vite-plugin-electron'\nimport electronRenderer from 'vite-plugin-electron-renderer'\nimport { resolve } from 'path'\n\nexport default defineConfig({\n  plugins: [\n    vue(),\n    electron([\n      {\n        entry: 'electron/main.js',\n        vite: {\n          build: {\n            outDir: 'dist-electron',\n            rollupOptions: {\n              external: ['better-sqlite3']\n            }\n          }\n        }\n      },\n      {\n        entry: 'electron/preload.js',\n        onstart(options) {\n          options.reload()\n        },\n        vite: {\n          build: {\n            outDir: 'dist-electron'\n          }\n        }\n      }\n    ]),\n    electronRenderer()\n  ],\n  resolve: {\n    alias: {\n      '@': resolve(__dirname, 'src')\n    }\n  },\n  server: {\n    port: 5173\n  }\n})\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\vite.config.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "<!DOCTYPE html>\n<html lang=\"zh-CN\">\n  <head>\n    <meta charset=\"UTF-8\" />\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n    <title>ShadowBot-FileWebHook</title>\n  </head>\n  <body>\n    <div id=\"app\"></div>\n    <script type=\"module\" src=\"/src/main.js\"></script>\n  </body>\n</html>\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\index.html"
}
```

*Tokens: 7 in, 650 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\vite.config.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\index.html
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { app, BrowserWindow, ipcMain } = require('electron')\nconst path = require('path')\nconst { initDatabase } = require('./database')\nconst { startServer, stopServer } = require('./server')\n\n// 禁用硬件加速（可选，某些情况下可以解决兼容性问题）\n// app.disableHardwareAcceleration()\n\nlet mainWindow = null\nlet httpServer = null\n\nfunction createWindow() {\n  mainWindow = new BrowserWindow({\n    width: 1200,\n    height: 800,\n    minWidth: 1000,\n    minHeight: 700,\n    webPreferences: {\n      preload: path.join(__dirname, 'preload.js'),\n      contextIsolation: true,\n      nodeIntegration: false\n    },\n    icon: path.join(__dirname, '../resources/icon.ico'),\n    title: 'ShadowBot-FileWebHook'\n  })\n\n  // 开发环境加载本地服务，生产环境加载打包后的文件\n  if (process.env.NODE_ENV === 'development' || process.env.VITE_DEV_SERVER_URL) {\n    mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL || 'http://localhost:5173')\n    mainWindow.webContents.openDevTools()\n  } else {\n    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'))\n  }\n\n  mainWindow.on('closed', () => {\n    mainWindow = null\n  })\n}\n\n// 应用初始化\napp.whenReady().then(async () => {\n  // 初始化数据库\n  try {\n    await initDatabase()\n    console.log('Database initialized successfully')\n  } catch (error) {\n    console.error('Failed to initialize database:', error)\n  }\n\n  // 启动 HTTP 服务器\n  try {\n    httpServer = await startServer()\n    console.log('HTTP Server started on port 8089')\n  } catch (error) {\n    console.error('Failed to start HTTP server:', error)\n  }\n\n  createWindow()\n\n  app.on('activate', () => {\n    if (BrowserWindow.getAllWindows().length === 0) {\n      createWindow()\n    }\n  })\n})\n\n// 应用退出时清理\napp.on('window-all-closed', async () => {\n  // 停止 HTTP 服务器\n  if (httpServer) {\n    await stopServer(httpServer)\n  }\n\n  if (process.platform !== 'darwin') {\n    app.quit()\n  }\n})\n\napp.on('before-quit', async () => {\n  if (httpServer) {\n    await stopServer(httpServer)\n  }\n})\n\n// IPC 通信处理 - 将在后续添加具体实现\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\main.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { contextBridge, ipcRenderer } = require('electron')\n\n// 暴露安全的 API 给渲染进程\ncontextBridge.exposeInMainWorld('electronAPI', {\n  // 触发器相关\n  getTriggers: () => ipcRenderer.invoke('get-triggers'),\n  createTrigger: (data) => ipcRenderer.invoke('create-trigger', data),\n  getTriggerById: (id) => ipcRenderer.invoke('get-trigger-by-id', id),\n\n  // 用户相关\n  getUsers: () => ipcRenderer.invoke('get-users'),\n  createUser: (data) => ipcRenderer.invoke('create-user', data),\n  updateUser: (id, data) => ipcRenderer.invoke('update-user', id, data),\n  deleteUser: (id) => ipcRenderer.invoke('delete-user', id),\n  resetUserSecret: (id) => ipcRenderer.invoke('reset-user-secret', id),\n\n  // 用户权限相关\n  getUserPermissions: (userId) => ipcRenderer.invoke('get-user-permissions', userId),\n  updateUserPermissions: (userId, triggerIds) => ipcRenderer.invoke('update-user-permissions', userId, triggerIds),\n\n  // 执行记录相关\n  getExecutionLogs: (params) => ipcRenderer.invoke('get-execution-logs', params),\n  deleteExecutionLogs: (ids) => ipcRenderer.invoke('delete-execution-logs', ids),\n  clearExecutionLogs: (triggerId) => ipcRenderer.invoke('clear-execution-logs', triggerId),\n  exportExecutionLogs: (params) => ipcRenderer.invoke('export-execution-logs', params),\n\n  // 系统设置相关\n  getSettings: () => ipcRenderer.invoke('get-settings'),\n  updateSettings: (settings) => ipcRenderer.invoke('update-settings', settings),\n\n  // 服务状态\n  getServerStatus: () => ipcRenderer.invoke('get-server-status'),\n\n  // 队列状态\n  getQueueStatus: () => ipcRenderer.invoke('get-queue-status'),\n\n  // 事件监听\n  onExecutionUpdate: (callback) => {\n    ipcRenderer.on('execution-update', (event, data) => callback(data))\n  },\n  onServerStatusChange: (callback) => {\n    ipcRenderer.on('server-status-change', (event, data) => callback(data))\n  },\n\n  // 移除监听\n  removeAllListeners: (channel) => {\n    ipcRenderer.removeAllListeners(channel)\n  }\n})\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\preload.js"
}
```

*Tokens: 7 in, 1661 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\main.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\preload.js
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const Database = require('better-sqlite3')\nconst path = require('path')\nconst { app } = require('electron')\nconst fs = require('fs')\n\nlet db = null\n\n// 获取数据库路径\nfunction getDbPath() {\n  const userDataPath = app.getPath('userData')\n  return path.join(userDataPath, 'filewebhook.db')\n}\n\n// 初始化数据库\nfunction initDatabase() {\n  return new Promise((resolve, reject) => {\n    try {\n      const dbPath = getDbPath()\n\n      // 确保目录存在\n      const dbDir = path.dirname(dbPath)\n      if (!fs.existsSync(dbDir)) {\n        fs.mkdirSync(dbDir, { recursive: true })\n      }\n\n      db = new Database(dbPath)\n      db.pragma('journal_mode = WAL')\n\n      // 创建表\n      createTables()\n\n      // 初始化默认设置\n      initDefaultSettings()\n\n      resolve(db)\n    } catch (error) {\n      reject(error)\n    }\n  })\n}\n\n// 创建表结构\nfunction createTables() {\n  // 用户表\n  db.exec(`\n    CREATE TABLE IF NOT EXISTS users (\n      id INTEGER PRIMARY KEY AUTOINCREMENT,\n      user_id TEXT UNIQUE NOT NULL,\n      name TEXT NOT NULL,\n      remark TEXT,\n      secret_key TEXT UNIQUE NOT NULL,\n      callback_url TEXT NOT NULL,\n      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP\n    )\n  `)\n\n  // 触发器表\n  db.exec(`\n    CREATE TABLE IF NOT EXISTS triggers (\n      id INTEGER PRIMARY KEY AUTOINCREMENT,\n      name TEXT NOT NULL,\n      description TEXT,\n      remark TEXT,\n      folder_path TEXT NOT NULL,\n      created_at DATETIME DEFAULT CURRENT_TIMESTAMP\n    )\n  `)\n\n  // 用户触发器权限表\n  db.exec(`\n    CREATE TABLE IF NOT EXISTS user_trigger_permissions (\n      id INTEGER PRIMARY KEY AUTOINCREMENT,\n      user_id TEXT NOT NULL,\n      trigger_id INTEGER NOT NULL,\n      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n      UNIQUE(user_id, trigger_id),\n      FOREIGN KEY (user_id) REFERENCES users(user_id),\n      FOREIGN KEY (trigger_id) REFERENCES triggers(id)\n    )\n  `)\n\n  // 执行记录表\n  db.exec(`\n    CREATE TABLE IF NOT EXISTS execution_logs (\n      id INTEGER PRIMARY KEY AUTOINCREMENT,\n      event_id TEXT UNIQUE NOT NULL,\n      trigger_id INTEGER NOT NULL,\n      user_id TEXT NOT NULL,\n      status TEXT NOT NULL,\n      request_time DATETIME NOT NULL,\n      pre_response_time DATETIME,\n      file_written_time DATETIME,\n      shadowbot_start_time DATETIME,\n      shadowbot_end_time DATETIME,\n      callback_time DATETIME,\n      total_duration INTEGER,\n      shadowbot_duration INTEGER,\n      request_params TEXT NOT NULL,\n      response_params TEXT,\n      error_message TEXT,\n      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n      FOREIGN KEY (trigger_id) REFERENCES triggers(id),\n      FOREIGN KEY (user_id) REFERENCES users(user_id)\n    )\n  `)\n\n  // 系统设置表\n  db.exec(`\n    CREATE TABLE IF NOT EXISTS settings (\n      key TEXT PRIMARY KEY,\n      value TEXT NOT NULL,\n      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP\n    )\n  `)\n\n  // 创建索引\n  db.exec(`\n    CREATE INDEX IF NOT EXISTS idx_execution_logs_trigger_id ON execution_logs(trigger_id);\n    CREATE INDEX IF NOT EXISTS idx_execution_logs_user_id ON execution_logs(user_id);\n    CREATE INDEX IF NOT EXISTS idx_execution_logs_status ON execution_logs(status);\n    CREATE INDEX IF NOT EXISTS idx_execution_logs_request_time ON execution_logs(request_time);\n    CREATE INDEX IF NOT EXISTS idx_user_permissions_user_id ON user_trigger_permissions(user_id);\n    CREATE INDEX IF NOT EXISTS idx_user_permissions_trigger_id ON user_trigger_permissions(trigger_id);\n  `)\n}\n\n// 初始化默认设置\nfunction initDefaultSettings() {\n  const defaultSettings = {\n    'trigger_files_path': path.join(app.getPath('userData'), 'triggerFiles'),\n    'max_queue_size': '50',\n    'http_host': '0.0.0.0',\n    'http_port': '8089'\n  }\n\n  const insertStmt = db.prepare(`\n    INSERT OR IGNORE INTO settings (key, value) VALUES (?, ?)\n  `)\n\n  for (const [key, value] of Object.entries(defaultSettings)) {\n    insertStmt.run(key, value)\n  }\n\n  // 确保 triggerFiles 目录存在\n  const triggerFilesPath = defaultSettings['trigger_files_path']\n  if (!fs.existsSync(triggerFilesPath)) {\n    fs.mkdirSync(triggerFilesPath, { recursive: true })\n  }\n}\n\n// 获取数据库实例\nfunction getDb() {\n  return db\n}\n\n// 关闭数据库\nfunction closeDatabase() {\n  if (db) {\n    db.close()\n    db = null\n  }\n}\n\nmodule.exports = {\n  initDatabase,\n  getDb,\n  closeDatabase,\n  getDbPath\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\database\\index.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { getDb } = require('./index')\nconst { v4: uuidv4 } = require('uuid')\n\n// 获取所有用户\nfunction getAllUsers() {\n  const db = getDb()\n  return db.prepare(`\n    SELECT u.*,\n           COUNT(utp.trigger_id) as permission_count\n    FROM users u\n    LEFT JOIN user_trigger_permissions utp ON u.user_id = utp.user_id\n    GROUP BY u.id\n    ORDER BY u.created_at DESC\n  `).all()\n}\n\n// 根据ID获取用户\nfunction getUserById(userId) {\n  const db = getDb()\n  return db.prepare('SELECT * FROM users WHERE user_id = ?').get(userId)\n}\n\n// 根据密钥获取用户\nfunction getUserBySecretKey(secretKey) {\n  const db = getDb()\n  return db.prepare('SELECT * FROM users WHERE secret_key = ?').get(secretKey)\n}\n\n// 创建用户\nfunction createUser({ name, remark, callbackUrl }) {\n  const db = getDb()\n  const userId = uuidv4()\n  const secretKey = uuidv4()\n\n  const stmt = db.prepare(`\n    INSERT INTO users (user_id, name, remark, secret_key, callback_url)\n    VALUES (?, ?, ?, ?, ?)\n  `)\n\n  stmt.run(userId, name, remark || '', secretKey, callbackUrl)\n\n  return getUserById(userId)\n}\n\n// 更新用户\nfunction updateUser(userId, { name, remark, callbackUrl }) {\n  const db = getDb()\n  const stmt = db.prepare(`\n    UPDATE users\n    SET name = ?, remark = ?, callback_url = ?, updated_at = CURRENT_TIMESTAMP\n    WHERE user_id = ?\n  `)\n\n  stmt.run(name, remark || '', callbackUrl, userId)\n  return getUserById(userId)\n}\n\n// 重置用户密钥\nfunction resetUserSecret(userId) {\n  const db = getDb()\n  const newSecretKey = uuidv4()\n\n  const stmt = db.prepare(`\n    UPDATE users\n    SET secret_key = ?, updated_at = CURRENT_TIMESTAMP\n    WHERE user_id = ?\n  `)\n\n  stmt.run(newSecretKey, userId)\n  return getUserById(userId)\n}\n\n// 删除用户\nfunction deleteUser(userId) {\n  const db = getDb()\n\n  // 删除用户权限\n  db.prepare('DELETE FROM user_trigger_permissions WHERE user_id = ?').run(userId)\n\n  // 删除用户\n  const stmt = db.prepare('DELETE FROM users WHERE user_id = ?')\n  return stmt.run(userId)\n}\n\n// 获取用户权限\nfunction getUserPermissions(userId) {\n  const db = getDb()\n  return db.prepare(`\n    SELECT trigger_id FROM user_trigger_permissions WHERE user_id = ?\n  `).all(userId).map(row => row.trigger_id)\n}\n\n// 更新用户权限\nfunction updateUserPermissions(userId, triggerIds) {\n  const db = getDb()\n\n  // 开启事务\n  const transaction = db.transaction(() => {\n    // 先删除现有权限\n    db.prepare('DELETE FROM user_trigger_permissions WHERE user_id = ?').run(userId)\n\n    // 添加新权限\n    const insertStmt = db.prepare(`\n      INSERT INTO user_trigger_permissions (user_id, trigger_id) VALUES (?, ?)\n    `)\n\n    for (const triggerId of triggerIds) {\n      insertStmt.run(userId, triggerId)\n    }\n  })\n\n  transaction()\n  return getUserPermissions(userId)\n}\n\n// 检查用户是否有触发器权限\nfunction checkUserPermission(userId, triggerId) {\n  const db = getDb()\n  const result = db.prepare(`\n    SELECT 1 FROM user_trigger_permissions\n    WHERE user_id = ? AND trigger_id = ?\n  `).get(userId, triggerId)\n\n  return !!result\n}\n\nmodule.exports = {\n  getAllUsers,\n  getUserById,\n  getUserBySecretKey,\n  createUser,\n  updateUser,\n  resetUserSecret,\n  deleteUser,\n  getUserPermissions,\n  updateUserPermissions,\n  checkUserPermission\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\database\\userModel.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { getDb } = require('./index')\nconst path = require('path')\nconst fs = require('fs')\n\n// 获取所有触发器\nfunction getAllTriggers() {\n  const db = getDb()\n  return db.prepare(`\n    SELECT t.*,\n           COUNT(el.id) as execution_count\n    FROM triggers t\n    LEFT JOIN execution_logs el ON t.id = el.trigger_id\n    GROUP BY t.id\n    ORDER BY t.id ASC\n  `).all()\n}\n\n// 根据ID获取触发器\nfunction getTriggerById(triggerId) {\n  const db = getDb()\n  return db.prepare('SELECT * FROM triggers WHERE id = ?').get(triggerId)\n}\n\n// 创建触发器\nfunction createTrigger({ name, description, remark }, triggerFilesPath) {\n  const db = getDb()\n\n  // 获取下一个ID（用于创建文件夹）\n  const maxIdResult = db.prepare('SELECT MAX(id) as maxId FROM triggers').get()\n  const nextId = (maxIdResult.maxId || 0) + 1\n\n  // 创建文件夹路径\n  const folderPath = path.join(triggerFilesPath, String(nextId))\n\n  // 确保文件夹存在\n  if (!fs.existsSync(folderPath)) {\n    fs.mkdirSync(folderPath, { recursive: true })\n  }\n\n  const stmt = db.prepare(`\n    INSERT INTO triggers (name, description, remark, folder_path)\n    VALUES (?, ?, ?, ?)\n  `)\n\n  const result = stmt.run(name, description || '', remark || '', folderPath)\n\n  return getTriggerById(result.lastInsertRowid)\n}\n\n// 获取触发器统计信息\nfunction getTriggerStats(triggerId) {\n  const db = getDb()\n\n  const stats = db.prepare(`\n    SELECT\n      COUNT(*) as total_count,\n      SUM(CASE WHEN status = 'CALLBACK_SUCCESS' THEN 1 ELSE 0 END) as success_count,\n      SUM(CASE WHEN status = 'CALLBACK_FAILED' THEN 1 ELSE 0 END) as failed_count,\n      AVG(total_duration) as avg_duration,\n      AVG(shadowbot_duration) as avg_shadowbot_duration\n    FROM execution_logs\n    WHERE trigger_id = ?\n  `).get(triggerId)\n\n  return stats\n}\n\nmodule.exports = {\n  getAllTriggers,\n  getTriggerById,\n  createTrigger,\n  getTriggerStats\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\database\\triggerModel.js"
}
```

*Tokens: 7 in, 3438 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\database\index.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\database\userModel.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\database\triggerModel.js
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { getDb } = require('./index')\n\n// 执行状态常量\nconst ExecutionStatus = {\n  REQUESTED: 'REQUESTED',           // 用户已请求\n  QUEUED: 'QUEUED',                 // 已收到请求放入队列\n  PRE_RESPONDED: 'PRE_RESPONDED',   // 已预响应外部服务\n  FILE_WRITTEN: 'FILE_WRITTEN',     // 已写入文件待影刀执行\n  EXECUTING: 'EXECUTING',           // 影刀执行中\n  COMPLETED: 'COMPLETED',           // 影刀执行完成\n  CALLBACK_SUCCESS: 'CALLBACK_SUCCESS', // 回调用户完成\n  CALLBACK_FAILED: 'CALLBACK_FAILED'    // 回调外部服务失败\n}\n\n// 状态中文映射\nconst StatusTextMap = {\n  [ExecutionStatus.REQUESTED]: '用户已请求',\n  [ExecutionStatus.QUEUED]: '已收到请求放入队列',\n  [ExecutionStatus.PRE_RESPONDED]: '已预响应外部服务',\n  [ExecutionStatus.FILE_WRITTEN]: '已写入文件待影刀执行',\n  [ExecutionStatus.EXECUTING]: '影刀执行中',\n  [ExecutionStatus.COMPLETED]: '影刀执行完成',\n  [ExecutionStatus.CALLBACK_SUCCESS]: '回调用户完成',\n  [ExecutionStatus.CALLBACK_FAILED]: '回调外部服务失败'\n}\n\n// 创建执行记录\nfunction createExecutionLog({ eventId, triggerId, userId, requestParams }) {\n  const db = getDb()\n  const now = new Date().toISOString()\n\n  const stmt = db.prepare(`\n    INSERT INTO execution_logs (event_id, trigger_id, user_id, status, request_time, request_params)\n    VALUES (?, ?, ?, ?, ?, ?)\n  `)\n\n  stmt.run(eventId, triggerId, userId, ExecutionStatus.REQUESTED, now, JSON.stringify(requestParams))\n\n  return getExecutionLogByEventId(eventId)\n}\n\n// 根据事件ID获取执行记录\nfunction getExecutionLogByEventId(eventId) {\n  const db = getDb()\n  return db.prepare('SELECT * FROM execution_logs WHERE event_id = ?').get(eventId)\n}\n\n// 更新执行记录状态\nfunction updateExecutionStatus(eventId, status, additionalData = {}) {\n  const db = getDb()\n  const now = new Date().toISOString()\n\n  let updateFields = ['status = ?', 'updated_at = ?']\n  let params = [status, now]\n\n  // 根据状态更新对应的时间字段\n  switch (status) {\n    case ExecutionStatus.PRE_RESPONDED:\n      updateFields.push('pre_response_time = ?')\n      params.push(now)\n      break\n    case ExecutionStatus.FILE_WRITTEN:\n      updateFields.push('file_written_time = ?')\n      params.push(now)\n      break\n    case ExecutionStatus.EXECUTING:\n      updateFields.push('shadowbot_start_time = ?')\n      params.push(now)\n      break\n    case ExecutionStatus.COMPLETED:\n      updateFields.push('shadowbot_end_time = ?')\n      params.push(now)\n      if (additionalData.responseParams) {\n        updateFields.push('response_params = ?')\n        params.push(JSON.stringify(additionalData.responseParams))\n      }\n      break\n    case ExecutionStatus.CALLBACK_SUCCESS:\n    case ExecutionStatus.CALLBACK_FAILED:\n      updateFields.push('callback_time = ?')\n      params.push(now)\n      // 计算耗时\n      const log = getExecutionLogByEventId(eventId)\n      if (log) {\n        const requestTime = new Date(log.request_time).getTime()\n        const callbackTime = new Date(now).getTime()\n        const totalDuration = callbackTime - requestTime\n\n        updateFields.push('total_duration = ?')\n        params.push(totalDuration)\n\n        if (log.shadowbot_start_time && log.shadowbot_end_time) {\n          const startTime = new Date(log.shadowbot_start_time).getTime()\n          const endTime = new Date(log.shadowbot_end_time).getTime()\n          const shadowbotDuration = endTime - startTime\n\n          updateFields.push('shadowbot_duration = ?')\n          params.push(shadowbotDuration)\n        }\n      }\n      break\n  }\n\n  // 处理错误信息\n  if (additionalData.errorMessage) {\n    updateFields.push('error_message = ?')\n    params.push(additionalData.errorMessage)\n  }\n\n  params.push(eventId)\n\n  const sql = `UPDATE execution_logs SET ${updateFields.join(', ')} WHERE event_id = ?`\n  db.prepare(sql).run(...params)\n\n  return getExecutionLogByEventId(eventId)\n}\n\n// 获取执行记录列表\nfunction getExecutionLogs({ triggerId, userId, status, startTime, endTime, page = 1, pageSize = 20 }) {\n  const db = getDb()\n  let conditions = []\n  let params = []\n\n  if (triggerId) {\n    conditions.push('trigger_id = ?')\n    params.push(triggerId)\n  }\n\n  if (userId) {\n    conditions.push('user_id = ?')\n    params.push(userId)\n  }\n\n  if (status) {\n    conditions.push('status = ?')\n    params.push(status)\n  }\n\n  if (startTime) {\n    conditions.push('request_time >= ?')\n    params.push(startTime)\n  }\n\n  if (endTime) {\n    conditions.push('request_time <= ?')\n    params.push(endTime)\n  }\n\n  const whereClause = conditions.length > 0 ? `WHERE ${conditions.join(' AND ')}` : ''\n\n  // 获取总数\n  const countSql = `SELECT COUNT(*) as total FROM execution_logs ${whereClause}`\n  const { total } = db.prepare(countSql).get(...params)\n\n  // 获取数据\n  const offset = (page - 1) * pageSize\n  const dataSql = `\n    SELECT el.*, t.name as trigger_name, u.name as user_name\n    FROM execution_logs el\n    LEFT JOIN triggers t ON el.trigger_id = t.id\n    LEFT JOIN users u ON el.user_id = u.user_id\n    ${whereClause}\n    ORDER BY el.request_time DESC\n    LIMIT ? OFFSET ?\n  `\n\n  const data = db.prepare(dataSql).all(...params, pageSize, offset)\n\n  return {\n    data,\n    total,\n    page,\n    pageSize,\n    totalPages: Math.ceil(total / pageSize)\n  }\n}\n\n// 删除执行记录\nfunction deleteExecutionLogs(ids) {\n  const db = getDb()\n  const placeholders = ids.map(() => '?').join(',')\n  const stmt = db.prepare(`DELETE FROM execution_logs WHERE id IN (${placeholders})`)\n  return stmt.run(...ids)\n}\n\n// 清空触发器的执行记录\nfunction clearExecutionLogsByTriggerId(triggerId) {\n  const db = getDb()\n  const stmt = db.prepare('DELETE FROM execution_logs WHERE trigger_id = ?')\n  return stmt.run(triggerId)\n}\n\n// 导出执行记录\nfunction exportExecutionLogs(params) {\n  const db = getDb()\n  let conditions = []\n  let queryParams = []\n\n  if (params.triggerId) {\n    conditions.push('trigger_id = ?')\n    queryParams.push(params.triggerId)\n  }\n\n  if (params.startTime) {\n    conditions.push('request_time >= ?')\n    queryParams.push(params.startTime)\n  }\n\n  if (params.endTime) {\n    conditions.push('request_time <= ?')\n    queryParams.push(params.endTime)\n  }\n\n  const whereClause = conditions.length > 0 ? `WHERE ${conditions.join(' AND ')}` : ''\n\n  const sql = `\n    SELECT el.*, t.name as trigger_name, u.name as user_name\n    FROM execution_logs el\n    LEFT JOIN triggers t ON el.trigger_id = t.id\n    LEFT JOIN users u ON el.user_id = u.user_id\n    ${whereClause}\n    ORDER BY el.request_time DESC\n  `\n\n  return db.prepare(sql).all(...queryParams)\n}\n\nmodule.exports = {\n  ExecutionStatus,\n  StatusTextMap,\n  createExecutionLog,\n  getExecutionLogByEventId,\n  updateExecutionStatus,\n  getExecutionLogs,\n  deleteExecutionLogs,\n  clearExecutionLogsByTriggerId,\n  exportExecutionLogs\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\database\\executionLogModel.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { getDb } = require('./index')\n\n// 获取所有设置\nfunction getAllSettings() {\n  const db = getDb()\n  const rows = db.prepare('SELECT key, value FROM settings').all()\n\n  const settings = {}\n  for (const row of rows) {\n    settings[row.key] = row.value\n  }\n\n  return settings\n}\n\n// 获取单个设置\nfunction getSetting(key) {\n  const db = getDb()\n  const row = db.prepare('SELECT value FROM settings WHERE key = ?').get(key)\n  return row ? row.value : null\n}\n\n// 更新设置\nfunction updateSetting(key, value) {\n  const db = getDb()\n  const stmt = db.prepare(`\n    INSERT INTO settings (key, value, updated_at)\n    VALUES (?, ?, CURRENT_TIMESTAMP)\n    ON CONFLICT(key) DO UPDATE SET\n      value = excluded.value,\n      updated_at = CURRENT_TIMESTAMP\n  `)\n\n  stmt.run(key, value)\n  return getSetting(key)\n}\n\n// 批量更新设置\nfunction updateSettings(settings) {\n  const db = getDb()\n\n  const transaction = db.transaction(() => {\n    const stmt = db.prepare(`\n      INSERT INTO settings (key, value, updated_at)\n      VALUES (?, ?, CURRENT_TIMESTAMP)\n      ON CONFLICT(key) DO UPDATE SET\n        value = excluded.value,\n        updated_at = CURRENT_TIMESTAMP\n    `)\n\n    for (const [key, value] of Object.entries(settings)) {\n      stmt.run(key, String(value))\n    }\n  })\n\n  transaction()\n  return getAllSettings()\n}\n\nmodule.exports = {\n  getAllSettings,\n  getSetting,\n  updateSetting,\n  updateSettings\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\database\\settingsModel.js"
}
```

*Tokens: 7 in, 2922 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\database\executionLogModel.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\database\settingsModel.js
```

## Assistant

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "const express = require('express')\nconst { getSetting } = require('../database/settingsModel')\nconst triggerRoutes = require('./routes/trigger')\nconst callbackRoutes = require('./routes/callback')\nconst { requestValidator } = require('./middleware/validator')\n\nlet server = null\n\n// 启动 HTTP 服务器\nfunction startServer() {\n  return new Promise((resolve, reject) => {\n    try {\n      const app = express()\n\n      // 中间件\n      app.use(express.json({ limit: '1mb' }))\n      app.use(express.urlencoded({ extended: true, limit: '1mb' }))\n\n      // 请求日志中间件\n      app.use((req, res, next) => {\n        console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`)\n        next()\n      })\n\n      // 请求体安全校验\n      app.use(requestValidator)\n\n      // 路由\n      app.use('/trigger', triggerRoutes)\n      app.use('/', callbackRoutes)\n\n      // 健康检查\n      app.get('/health', (req, res) => {\n        res.json({ code: 200, message: 'ok', data: { status: 'running' } })\n      })\n\n      // 错误处理\n      app.use((err, req, res, next) => {\n        console.error('Server Error:', err)\n        res.status(500).json({\n          code: 500,\n          message: '系统内部错误',\n          errorType: 'FileWebHook非业务错误',\n          detail: err.message\n        })\n      })\n\n      // 404 处理\n      app.use((req, res) => {\n        res.status(404).json({\n          code: 404,\n          message: '接口不存在'\n        })\n      })\n\n      // 获取配置\n      const host = getSetting('http_host') || '0.0.0.0'\n      const port = parseInt(getSetting('http_port') || '8089', 10)\n\n      server = app.listen(port, host, () => {\n        console.log(`HTTP Server listening on ${host}:${port}`)\n        resolve(server)\n      })\n\n      server.on('error', (error) => {\n        console.error('Server error:', error)\n        reject(error)\n      })\n\n    } catch (error) {\n      reject(error)\n    }\n  })\n}\n\n// 停止 HTTP 服务器\nfunction stopServer(serverInstance) {\n  return new Promise((resolve) => {\n    if (serverInstance) {\n      serverInstance.close(() => {\n        console.log('HTTP Server stopped')\n        resolve()\n      })\n    } else {\n      resolve()\n    }\n  })\n}\n\n// 获取服务器实例\nfunction getServer() {\n  return server\n}\n\nmodule.exports = {\n  startServer,\n  stopServer,\n  getServer\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\index.js"
}
```

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "// 请求体安全校验中间件\n\n// 危险字符和路径模式\nconst DANGEROUS_PATTERNS = [\n  /\\.\\.\\//g,      // 路径遍历\n  /\\.\\.\\\\/g,      // Windows 路径遍历\n  /<script/gi,    // XSS\n  /<\\/script/gi,\n  /javascript:/gi,\n  /vbscript:/gi,\n  /onload=/gi,\n  /onerror=/gi\n]\n\n// 检查对象深度\nfunction getObjectDepth(obj, currentDepth = 0) {\n  if (typeof obj !== 'object' || obj === null) {\n    return currentDepth\n  }\n\n  let maxDepth = currentDepth\n  for (const value of Object.values(obj)) {\n    const depth = getObjectDepth(value, currentDepth + 1)\n    if (depth > maxDepth) {\n      maxDepth = depth\n    }\n  }\n\n  return maxDepth\n}\n\n// 计算对象字段数量\nfunction countFields(obj) {\n  if (typeof obj !== 'object' || obj === null) {\n    return 0\n  }\n\n  let count = Object.keys(obj).length\n  for (const value of Object.values(obj)) {\n    if (typeof value === 'object' && value !== null) {\n      count += countFields(value)\n    }\n  }\n\n  return count\n}\n\n// 检查字符串中是否包含危险字符\nfunction containsDangerousPatterns(str) {\n  if (typeof str !== 'string') {\n    return false\n  }\n\n  for (const pattern of DANGEROUS_PATTERNS) {\n    if (pattern.test(str)) {\n      return true\n    }\n  }\n\n  return false\n}\n\n// 递归检查对象中的所有字符串值\nfunction checkObjectForDangerousPatterns(obj) {\n  if (typeof obj === 'string') {\n    return containsDangerousPatterns(obj)\n  }\n\n  if (typeof obj !== 'object' || obj === null) {\n    return false\n  }\n\n  for (const value of Object.values(obj)) {\n    if (checkObjectForDangerousPatterns(value)) {\n      return true\n    }\n  }\n\n  return false\n}\n\n// 请求校验中间件\nfunction requestValidator(req, res, next) {\n  // 只对有 body 的请求进行校验\n  if (!req.body || Object.keys(req.body).length === 0) {\n    return next()\n  }\n\n  const body = req.body\n\n  // 1. 检查是否为合法的 JSON 对象\n  if (typeof body !== 'object' || Array.isArray(body)) {\n    return res.status(400).json({\n      code: 400,\n      message: '请求体校验失败：必须是JSON对象',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  // 2. 检查嵌套深度（最大10层）\n  const depth = getObjectDepth(body)\n  if (depth > 10) {\n    return res.status(400).json({\n      code: 400,\n      message: '请求体校验失败：JSON嵌套深度超过限制（最大10层）',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  // 3. 检查字段数量（最大100个）\n  const fieldCount = countFields(body)\n  if (fieldCount > 100) {\n    return res.status(400).json({\n      code: 400,\n      message: '请求体校验失败：字段数量超过限制（最大100个）',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  // 4. 检查危险字符\n  if (checkObjectForDangerousPatterns(body)) {\n    return res.status(400).json({\n      code: 400,\n      message: '请求体校验失败：包含非法字符',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  next()\n}\n\nmodule.exports = {\n  requestValidator,\n  containsDangerousPatterns,\n  checkObjectForDangerousPatterns\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\middleware\\validator.js"
}
```

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { getUserBySecretKey, checkUserPermission } = require('../../database/userModel')\n\n// 身份验证中间件\nfunction authMiddleware(req, res, next) {\n  const authHeader = req.headers['filewebhook-authorization']\n\n  if (!authHeader) {\n    return res.status(401).json({\n      code: 401,\n      message: '身份验证失败：缺少认证头 FileWebHook-Authorization',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  // 根据密钥查找用户\n  const user = getUserBySecretKey(authHeader)\n\n  if (!user) {\n    return res.status(401).json({\n      code: 401,\n      message: '身份验证失败：密钥无效',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  // 将用户信息附加到请求对象\n  req.user = user\n\n  next()\n}\n\n// 权限验证中间件\nfunction permissionMiddleware(req, res, next) {\n  const { triggerId } = req.params\n  const user = req.user\n\n  if (!triggerId) {\n    return res.status(400).json({\n      code: 400,\n      message: '请求参数错误：缺少触发器ID',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  // 检查用户是否有权限\n  const hasPermission = checkUserPermission(user.user_id, parseInt(triggerId, 10))\n\n  if (!hasPermission) {\n    return res.status(403).json({\n      code: 403,\n      message: '鉴权失败：无权限调用此触发器',\n      errorType: 'FileWebHook业务错误'\n    })\n  }\n\n  next()\n}\n\nmodule.exports = {\n  authMiddleware,\n  permissionMiddleware\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\middleware\\auth.js"
}
```

*Tokens: 7 in, 2623 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\index.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\middleware\validator.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\middleware\auth.js
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const express = require('express')\nconst router = express.Router()\nconst { authMiddleware, permissionMiddleware } = require('../middleware/auth')\nconst { getTriggerById } = require('../../database/triggerModel')\nconst { createExecutionLog, updateExecutionStatus, ExecutionStatus } = require('../../database/executionLogModel')\nconst { getSetting } = require('../../database/settingsModel')\nconst { addToQueue, getQueueStatus } = require('../services/queue')\n\n// POST /trigger/:triggerId - 触发执行\nrouter.post('/:triggerId', authMiddleware, permissionMiddleware, async (req, res) => {\n  try {\n    const { triggerId } = req.params\n    const user = req.user\n    const requestBody = req.body\n\n    // 检查触发器是否存在\n    const trigger = getTriggerById(parseInt(triggerId, 10))\n    if (!trigger) {\n      return res.status(404).json({\n        code: 404,\n        message: '触发器不存在',\n        errorType: 'FileWebHook业务错误'\n      })\n    }\n\n    // 检查队列是否已满\n    const maxQueueSize = parseInt(getSetting('max_queue_size') || '50', 10)\n    const queueStatus = getQueueStatus()\n    if (queueStatus.size >= maxQueueSize) {\n      return res.status(503).json({\n        code: 503,\n        message: `队列已满，当前队列长度: ${queueStatus.size}，最大: ${maxQueueSize}`,\n        errorType: 'FileWebHook业务错误'\n      })\n    }\n\n    // 生成 eventId\n    const eventId = `${triggerId}-${Date.now()}`\n\n    // 创建执行记录\n    createExecutionLog({\n      eventId,\n      triggerId: parseInt(triggerId, 10),\n      userId: user.user_id,\n      requestParams: requestBody\n    })\n\n    // 更新状态为已入队\n    updateExecutionStatus(eventId, ExecutionStatus.QUEUED)\n\n    // 加入队列\n    addToQueue({\n      eventId,\n      triggerId: parseInt(triggerId, 10),\n      userId: user.user_id,\n      trigger,\n      user,\n      requestBody\n    })\n\n    // 更新状态为已预响应\n    updateExecutionStatus(eventId, ExecutionStatus.PRE_RESPONDED)\n\n    // 返回响应\n    res.json({\n      code: 200,\n      message: 'ok',\n      data: {\n        eventId,\n        triggerId,\n        status: '已收到请求放入队列'\n      }\n    })\n\n  } catch (error) {\n    console.error('Trigger error:', error)\n    res.status(500).json({\n      code: 500,\n      message: '系统内部错误',\n      errorType: 'FileWebHook非业务错误',\n      detail: error.message\n    })\n  }\n})\n\nmodule.exports = router\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\routes\\trigger.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const express = require('express')\nconst router = express.Router()\nconst { getExecutionLogByEventId, updateExecutionStatus, ExecutionStatus } = require('../../database/executionLogModel')\nconst { getUserById } = require('../../database/userModel')\nconst { getTriggerById } = require('../../database/triggerModel')\nconst { notifyTaskCompleted } = require('../services/queue')\nconst { callbackExternalService } = require('../services/callback')\nconst fileService = require('../services/file')\n\n// POST /trigged - 影刀通知开始执行\nrouter.post('/trigged', async (req, res) => {\n  try {\n    const { triggerId, eventId } = req.body\n\n    if (!triggerId || !eventId) {\n      return res.status(400).json({\n        code: 400,\n        message: '参数错误：缺少 triggerId 或 eventId'\n      })\n    }\n\n    // 检查执行记录是否存在\n    const log = getExecutionLogByEventId(eventId)\n    if (!log) {\n      return res.status(404).json({\n        code: 404,\n        message: '执行记录不存在'\n      })\n    }\n\n    // 更新状态为执行中\n    updateExecutionStatus(eventId, ExecutionStatus.EXECUTING)\n\n    res.json({\n      code: 200,\n      message: 'ok'\n    })\n\n  } catch (error) {\n    console.error('Trigged callback error:', error)\n    res.status(500).json({\n      code: 500,\n      message: '系统内部错误',\n      detail: error.message\n    })\n  }\n})\n\n// POST /notify - 影刀通知执行完成\nrouter.post('/notify', async (req, res) => {\n  try {\n    const { triggerId, eventId, responseCode, responseMessage, responseData } = req.body\n\n    if (!triggerId || !eventId) {\n      return res.status(400).json({\n        code: 400,\n        message: '参数错误：缺少 triggerId 或 eventId'\n      })\n    }\n\n    // 检查执行记录是否存在\n    const log = getExecutionLogByEventId(eventId)\n    if (!log) {\n      return res.status(404).json({\n        code: 404,\n        message: '执行记录不存在'\n      })\n    }\n\n    // 更新状态为执行完成\n    updateExecutionStatus(eventId, ExecutionStatus.COMPLETED, {\n      responseParams: { responseCode, responseMessage, responseData }\n    })\n\n    // 获取用户信息用于回调\n    const user = getUserById(log.user_id)\n    const trigger = getTriggerById(parseInt(triggerId, 10))\n\n    // 删除请求参数文件\n    try {\n      if (trigger) {\n        fileService.deleteRequestFile(trigger.folder_path)\n      }\n    } catch (fileError) {\n      console.error('Failed to delete request file:', fileError)\n    }\n\n    // 获取更新后的日志（包含时间信息）\n    const updatedLog = getExecutionLogByEventId(eventId)\n\n    // 回调外部服务\n    if (user && user.callback_url) {\n      try {\n        await callbackExternalService(user.callback_url, triggerId, {\n          triggerId,\n          eventId,\n          responseCode,\n          responseMessage,\n          responseData,\n          requestTime: updatedLog.request_time,\n          shadowBotStartTime: updatedLog.shadowbot_start_time,\n          shadowBotEndTime: updatedLog.shadowbot_end_time,\n          totalDuration: updatedLog.total_duration,\n          shadowBotDuration: updatedLog.shadowbot_duration\n        })\n\n        // 更新状态为回调成功\n        updateExecutionStatus(eventId, ExecutionStatus.CALLBACK_SUCCESS)\n      } catch (callbackError) {\n        console.error('Callback failed:', callbackError)\n        // 更新状态为回调失败\n        updateExecutionStatus(eventId, ExecutionStatus.CALLBACK_FAILED, {\n          errorMessage: callbackError.message\n        })\n      }\n    } else {\n      // 没有配置回调地址，直接标记为回调成功\n      updateExecutionStatus(eventId, ExecutionStatus.CALLBACK_SUCCESS)\n    }\n\n    // 通知队列任务已完成，可以处理下一个\n    notifyTaskCompleted(eventId)\n\n    res.json({\n      code: 200,\n      message: 'ok'\n    })\n\n  } catch (error) {\n    console.error('Notify callback error:', error)\n    res.status(500).json({\n      code: 500,\n      message: '系统内部错误',\n      detail: error.message\n    })\n  }\n})\n\nmodule.exports = router\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\routes\\callback.js"
}
```

*Tokens: 7 in, 2212 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\routes\trigger.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\routes\callback.js
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { updateExecutionStatus, ExecutionStatus } = require('../../database/executionLogModel')\nconst fileService = require('./file')\n\n// 任务队列\nconst queue = []\nlet isProcessing = false\nlet currentTask = null\n\n// 添加任务到队列\nfunction addToQueue(task) {\n  queue.push(task)\n  console.log(`Task ${task.eventId} added to queue. Queue size: ${queue.length}`)\n\n  // 如果没有正在处理的任务，开始处理\n  if (!isProcessing) {\n    processNextTask()\n  }\n}\n\n// 处理下一个任务\nasync function processNextTask() {\n  if (queue.length === 0) {\n    isProcessing = false\n    currentTask = null\n    console.log('Queue is empty, waiting for new tasks...')\n    return\n  }\n\n  isProcessing = true\n  currentTask = queue.shift()\n\n  console.log(`Processing task ${currentTask.eventId}. Remaining in queue: ${queue.length}`)\n\n  try {\n    // 更新状态为写入文件\n    updateExecutionStatus(currentTask.eventId, ExecutionStatus.FILE_WRITTEN)\n\n    // 写入请求参数文件\n    const fileContent = {\n      eventId: currentTask.eventId,\n      triggerId: String(currentTask.triggerId),\n      userId: currentTask.userId,\n      requestTime: new Date().toISOString(),\n      requestData: currentTask.requestBody\n    }\n\n    await fileService.writeRequestFile(currentTask.trigger.folder_path, fileContent)\n\n    console.log(`Request file written for task ${currentTask.eventId}`)\n\n    // 等待影刀处理完成（通过 /notify 回调通知）\n    // 此时不主动处理下一个任务，等待 notifyTaskCompleted 被调用\n\n  } catch (error) {\n    console.error(`Failed to process task ${currentTask.eventId}:`, error)\n\n    // 更新状态为失败\n    updateExecutionStatus(currentTask.eventId, ExecutionStatus.CALLBACK_FAILED, {\n      errorMessage: `文件写入失败: ${error.message}`\n    })\n\n    // 处理下一个任务\n    processNextTask()\n  }\n}\n\n// 通知任务完成（由 /notify 路由调用）\nfunction notifyTaskCompleted(eventId) {\n  if (currentTask && currentTask.eventId === eventId) {\n    console.log(`Task ${eventId} completed`)\n    currentTask = null\n    // 处理下一个任务\n    processNextTask()\n  }\n}\n\n// 获取队列状态\nfunction getQueueStatus() {\n  return {\n    size: queue.length,\n    isProcessing,\n    currentTask: currentTask ? {\n      eventId: currentTask.eventId,\n      triggerId: currentTask.triggerId\n    } : null,\n    pendingTasks: queue.map(t => ({\n      eventId: t.eventId,\n      triggerId: t.triggerId\n    }))\n  }\n}\n\n// 清空队列（用于紧急情况）\nfunction clearQueue() {\n  const clearedCount = queue.length\n  queue.length = 0\n  console.log(`Queue cleared. Removed ${clearedCount} tasks`)\n  return clearedCount\n}\n\nmodule.exports = {\n  addToQueue,\n  processNextTask,\n  notifyTaskCompleted,\n  getQueueStatus,\n  clearQueue\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\services\\queue.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const fs = require('fs')\nconst path = require('path')\n\nconst REQUEST_FILE_NAME = 'requestParam.json'\n\n// 写入请求参数文件\nasync function writeRequestFile(folderPath, content) {\n  return new Promise((resolve, reject) => {\n    try {\n      // 确保目录存在\n      if (!fs.existsSync(folderPath)) {\n        fs.mkdirSync(folderPath, { recursive: true })\n      }\n\n      const filePath = path.join(folderPath, REQUEST_FILE_NAME)\n\n      // 写入文件\n      fs.writeFileSync(filePath, JSON.stringify(content, null, 2), 'utf-8')\n\n      console.log(`Request file written: ${filePath}`)\n      resolve(filePath)\n\n    } catch (error) {\n      console.error(`Failed to write request file: ${error.message}`)\n      reject(error)\n    }\n  })\n}\n\n// 删除请求参数文件\nfunction deleteRequestFile(folderPath) {\n  try {\n    const filePath = path.join(folderPath, REQUEST_FILE_NAME)\n\n    if (fs.existsSync(filePath)) {\n      fs.unlinkSync(filePath)\n      console.log(`Request file deleted: ${filePath}`)\n    }\n\n    return true\n\n  } catch (error) {\n    console.error(`Failed to delete request file: ${error.message}`)\n    throw error\n  }\n}\n\n// 检查请求参数文件是否存在\nfunction requestFileExists(folderPath) {\n  const filePath = path.join(folderPath, REQUEST_FILE_NAME)\n  return fs.existsSync(filePath)\n}\n\n// 读取请求参数文件\nfunction readRequestFile(folderPath) {\n  try {\n    const filePath = path.join(folderPath, REQUEST_FILE_NAME)\n\n    if (!fs.existsSync(filePath)) {\n      return null\n    }\n\n    const content = fs.readFileSync(filePath, 'utf-8')\n    return JSON.parse(content)\n\n  } catch (error) {\n    console.error(`Failed to read request file: ${error.message}`)\n    throw error\n  }\n}\n\n// 确保触发器文件夹存在\nfunction ensureTriggerFolder(folderPath) {\n  if (!fs.existsSync(folderPath)) {\n    fs.mkdirSync(folderPath, { recursive: true })\n    console.log(`Trigger folder created: ${folderPath}`)\n  }\n  return folderPath\n}\n\nmodule.exports = {\n  REQUEST_FILE_NAME,\n  writeRequestFile,\n  deleteRequestFile,\n  requestFileExists,\n  readRequestFile,\n  ensureTriggerFolder\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\services\\file.js"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "const axios = require('axios')\n\n// 回调外部服务\nasync function callbackExternalService(callbackUrl, triggerId, data) {\n  // 构建完整的回调URL\n  const url = `${callbackUrl.replace(/\\/$/, '')}/${triggerId}/notify`\n\n  console.log(`Calling back external service: ${url}`)\n\n  try {\n    const response = await axios.post(url, data, {\n      headers: {\n        'Content-Type': 'application/json'\n      },\n      timeout: 30000 // 30秒超时\n    })\n\n    console.log(`Callback successful: ${url}, status: ${response.status}`)\n    return response.data\n\n  } catch (error) {\n    console.error(`Callback failed: ${url}`, error.message)\n\n    // 抛出错误供上层处理\n    if (error.response) {\n      // 服务器返回了错误响应\n      throw new Error(`回调失败: HTTP ${error.response.status} - ${error.response.statusText}`)\n    } else if (error.request) {\n      // 请求已发出但没有收到响应\n      throw new Error(`回调失败: 无法连接到外部服务 - ${error.message}`)\n    } else {\n      // 其他错误\n      throw new Error(`回调失败: ${error.message}`)\n    }\n  }\n}\n\nmodule.exports = {\n  callbackExternalService\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\server\\services\\callback.js"
}
```

*Tokens: 7 in, 2219 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\services\queue.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\services\file.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\server\services\callback.js
```

## Assistant

*Tokens: 7 in, 5 out*

## Assistant

现在创建 Vue 前端部分。

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "import { createApp } from 'vue'\nimport { createPinia } from 'pinia'\nimport ElementPlus from 'element-plus'\nimport * as ElementPlusIconsVue from '@element-plus/icons-vue'\nimport zhCn from 'element-plus/dist/locale/zh-cn.mjs'\nimport 'element-plus/dist/index.css'\nimport App from './App.vue'\nimport router from './router'\nimport './styles/global.css'\n\nconst app = createApp(App)\n\n// 注册所有 Element Plus 图标\nfor (const [key, component] of Object.entries(ElementPlusIconsVue)) {\n  app.component(key, component)\n}\n\napp.use(createPinia())\napp.use(router)\napp.use(ElementPlus, { locale: zhCn })\n\napp.mount('#app')\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\main.js"
}
```

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "<template>\n  <div class=\"app-container\">\n    <el-container class=\"main-container\">\n      <!-- 侧边栏 -->\n      <el-aside width=\"200px\" class=\"sidebar\">\n        <div class=\"logo\">\n          <img src=\"./assets/logo.svg\" alt=\"logo\" class=\"logo-img\" />\n          <span class=\"logo-text\">FileWebHook</span>\n        </div>\n\n        <el-menu\n          :default-active=\"activeMenu\"\n          class=\"sidebar-menu\"\n          router\n        >\n          <el-menu-item index=\"/triggers\">\n            <el-icon><Collection /></el-icon>\n            <span>触发器管理</span>\n          </el-menu-item>\n          <el-menu-item index=\"/users\">\n            <el-icon><User /></el-icon>\n            <span>用户管理</span>\n          </el-menu-item>\n          <el-menu-item index=\"/settings\">\n            <el-icon><Setting /></el-icon>\n            <span>系统设置</span>\n          </el-menu-item>\n        </el-menu>\n      </el-aside>\n\n      <!-- 主内容区 -->\n      <el-main class=\"main-content\">\n        <div class=\"header\">\n          <div class=\"header-title\">\n            <h2>{{ pageTitle }}</h2>\n          </div>\n          <div class=\"header-status\">\n            <el-tag :type=\"serverStatus === 'running' ? 'success' : 'danger'\" size=\"small\">\n              <el-icon><Connection /></el-icon>\n              {{ serverStatus === 'running' ? '服务运行中' : '服务已停止' }}\n            </el-tag>\n          </div>\n        </div>\n\n        <div class=\"content-wrapper\">\n          <router-view />\n        </div>\n      </el-main>\n    </el-container>\n  </div>\n</template>\n\n<script setup>\nimport { ref, computed, onMounted } from 'vue'\nimport { useRoute } from 'vue-router'\nimport { Collection, User, Setting, Connection } from '@element-plus/icons-vue'\n\nconst route = useRoute()\nconst serverStatus = ref('running')\n\nconst activeMenu = computed(() => route.path)\n\nconst pageTitle = computed(() => {\n  const titles = {\n    '/triggers': '触发器管理',\n    '/users': '用户管理',\n    '/settings': '系统设置'\n  }\n  // 处理子路由\n  for (const [path, title] of Object.entries(titles)) {\n    if (route.path.startsWith(path)) {\n      return title\n    }\n  }\n  return 'FileWebHook'\n})\n\nonMounted(async () => {\n  // 获取服务状态\n  try {\n    if (window.electronAPI) {\n      const status = await window.electronAPI.getServerStatus()\n      serverStatus.value = status?.running ? 'running' : 'stopped'\n    }\n  } catch (error) {\n    console.error('Failed to get server status:', error)\n  }\n})\n</script>\n\n<style scoped>\n.app-container {\n  width: 100%;\n  height: 100vh;\n  overflow: hidden;\n}\n\n.main-container {\n  height: 100%;\n}\n\n.sidebar {\n  background-color: #fff;\n  border-right: 1px solid #f5f7fa;\n  display: flex;\n  flex-direction: column;\n}\n\n.logo {\n  display: flex;\n  align-items: center;\n  padding: 20px;\n  border-bottom: 1px solid #f5f7fa;\n}\n\n.logo-img {\n  width: 32px;\n  height: 32px;\n  margin-right: 10px;\n}\n\n.logo-text {\n  font-size: 16px;\n  font-weight: 600;\n  color: #303133;\n}\n\n.sidebar-menu {\n  border-right: none;\n  flex: 1;\n}\n\n.sidebar-menu .el-menu-item {\n  height: 50px;\n  line-height: 50px;\n}\n\n.sidebar-menu .el-menu-item.is-active {\n  background-color: #fff5f5;\n  color: #fc6868;\n}\n\n.sidebar-menu .el-menu-item:hover {\n  background-color: #fff5f5;\n}\n\n.main-content {\n  background-color: #f5f7fa;\n  padding: 0;\n  display: flex;\n  flex-direction: column;\n}\n\n.header {\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n  padding: 16px 24px;\n  background-color: #fff;\n  border-bottom: 1px solid #f5f7fa;\n}\n\n.header-title h2 {\n  margin: 0;\n  font-size: 18px;\n  font-weight: 600;\n  color: #303133;\n}\n\n.header-status {\n  display: flex;\n  align-items: center;\n  gap: 8px;\n}\n\n.content-wrapper {\n  flex: 1;\n  padding: 24px;\n  overflow: auto;\n}\n</style>\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\App.vue"
}
```

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "import { createRouter, createWebHashHistory } from 'vue-router'\n\nconst routes = [\n  {\n    path: '/',\n    redirect: '/triggers'\n  },\n  {\n    path: '/triggers',\n    name: 'Triggers',\n    component: () => import('../views/TriggerManage.vue')\n  },\n  {\n    path: '/triggers/:id',\n    name: 'TriggerDetail',\n    component: () => import('../views/TriggerDetail.vue')\n  },\n  {\n    path: '/users',\n    name: 'Users',\n    component: () => import('../views/UserManage.vue')\n  },\n  {\n    path: '/settings',\n    name: 'Settings',\n    component: () => import('../views/SystemSettings.vue')\n  }\n]\n\nconst router = createRouter({\n  history: createWebHashHistory(),\n  routes\n})\n\nexport default router\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\router\\index.js"
}
```

*Tokens: 7 in, 2039 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\main.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\App.vue
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\router\index.js
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "/* 全局样式 */\n\n* {\n  margin: 0;\n  padding: 0;\n  box-sizing: border-box;\n}\n\nbody {\n  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;\n  -webkit-font-smoothing: antialiased;\n  -moz-osx-font-smoothing: grayscale;\n}\n\n/* Element Plus 主题色覆盖 */\n:root {\n  --el-color-primary: #fc6868;\n  --el-color-primary-light-3: #fd9a9a;\n  --el-color-primary-light-5: #fdb3b3;\n  --el-color-primary-light-7: #fecccc;\n  --el-color-primary-light-8: #fdd8d8;\n  --el-color-primary-light-9: #fee5e5;\n  --el-color-primary-dark-2: #ca5353;\n}\n\n/* 按钮样式覆盖 */\n.el-button--primary {\n  --el-button-bg-color: #fc6868;\n  --el-button-border-color: #fc6868;\n  --el-button-hover-bg-color: #fd8a8a;\n  --el-button-hover-border-color: #fd8a8a;\n  --el-button-active-bg-color: #e05050;\n  --el-button-active-border-color: #e05050;\n}\n\n/* 卡片样式 */\n.page-card {\n  background-color: #fff;\n  border-radius: 8px;\n  padding: 24px;\n  margin-bottom: 16px;\n}\n\n/* 表格样式 */\n.el-table {\n  --el-table-header-bg-color: #f5f7fa;\n}\n\n/* 分页样式 */\n.pagination-wrapper {\n  display: flex;\n  justify-content: flex-end;\n  margin-top: 16px;\n}\n\n/* 操作栏 */\n.action-bar {\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n  margin-bottom: 16px;\n}\n\n.action-bar-left {\n  display: flex;\n  align-items: center;\n  gap: 12px;\n}\n\n.action-bar-right {\n  display: flex;\n  align-items: center;\n  gap: 12px;\n}\n\n/* 状态标签 */\n.status-tag {\n  font-size: 12px;\n}\n\n/* 空状态 */\n.empty-state {\n  text-align: center;\n  padding: 60px 20px;\n  color: #909399;\n}\n\n.empty-state .el-icon {\n  font-size: 48px;\n  margin-bottom: 16px;\n}\n\n/* 复制按钮 */\n.copy-btn {\n  cursor: pointer;\n  color: #409eff;\n  margin-left: 8px;\n}\n\n.copy-btn:hover {\n  color: #66b1ff;\n}\n\n/* 详情项 */\n.detail-item {\n  margin-bottom: 16px;\n}\n\n.detail-item .label {\n  font-size: 14px;\n  color: #909399;\n  margin-bottom: 4px;\n}\n\n.detail-item .value {\n  font-size: 14px;\n  color: #303133;\n  word-break: break-all;\n}\n\n/* 代码块 */\n.code-block {\n  background-color: #f5f7fa;\n  border-radius: 4px;\n  padding: 12px;\n  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;\n  font-size: 13px;\n  overflow-x: auto;\n  white-space: pre-wrap;\n  word-break: break-all;\n}\n\n/* 滚动条样式 */\n::-webkit-scrollbar {\n  width: 8px;\n  height: 8px;\n}\n\n::-webkit-scrollbar-track {\n  background: #f1f1f1;\n  border-radius: 4px;\n}\n\n::-webkit-scrollbar-thumb {\n  background: #c1c1c1;\n  border-radius: 4px;\n}\n\n::-webkit-scrollbar-thumb:hover {\n  background: #a1a1a1;\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\styles\\global.css"
}
```

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "<template>\n  <div class=\"trigger-manage\">\n    <!-- 操作栏 -->\n    <div class=\"action-bar\">\n      <div class=\"action-bar-left\">\n        <el-button type=\"primary\" @click=\"showCreateDialog\">\n          <el-icon><Plus /></el-icon>\n          新建触发器\n        </el-button>\n      </div>\n      <div class=\"action-bar-right\">\n        <el-button @click=\"loadTriggers\" :loading=\"loading\">\n          <el-icon><Refresh /></el-icon>\n          刷新\n        </el-button>\n      </div>\n    </div>\n\n    <!-- 触发器列表 -->\n    <div class=\"page-card\">\n      <el-table :data=\"triggers\" v-loading=\"loading\" stripe>\n        <el-table-column prop=\"id\" label=\"ID\" width=\"80\" />\n        <el-table-column prop=\"name\" label=\"名称\" min-width=\"150\" />\n        <el-table-column prop=\"description\" label=\"描述\" min-width=\"200\" show-overflow-tooltip />\n        <el-table-column label=\"文件夹路径\" min-width=\"300\">\n          <template #default=\"{ row }\">\n            <div class=\"path-cell\">\n              <span class=\"path-text\">{{ row.folder_path }}</span>\n              <el-tooltip content=\"复制路径\" placement=\"top\">\n                <el-icon class=\"copy-btn\" @click=\"copyPath(row.folder_path)\">\n                  <CopyDocument />\n                </el-icon>\n              </el-tooltip>\n            </div>\n          </template>\n        </el-table-column>\n        <el-table-column prop=\"execution_count\" label=\"执行次数\" width=\"100\" align=\"center\" />\n        <el-table-column prop=\"created_at\" label=\"创建时间\" width=\"180\">\n          <template #default=\"{ row }\">\n            {{ formatDate(row.created_at) }}\n          </template>\n        </el-table-column>\n        <el-table-column label=\"操作\" width=\"200\" fixed=\"right\">\n          <template #default=\"{ row }\">\n            <el-button type=\"primary\" link @click=\"viewDetail(row)\">\n              查看详情\n            </el-button>\n            <el-button type=\"primary\" link @click=\"viewLogs(row)\">\n              执行记录\n            </el-button>\n          </template>\n        </el-table-column>\n      </el-table>\n    </div>\n\n    <!-- 新建触发器弹窗 -->\n    <el-dialog\n      v-model=\"createDialogVisible\"\n      title=\"新建触发器\"\n      width=\"500px\"\n      :close-on-click-modal=\"false\"\n    >\n      <el-form\n        ref=\"createFormRef\"\n        :model=\"createForm\"\n        :rules=\"createRules\"\n        label-width=\"80px\"\n      >\n        <el-form-item label=\"名称\" prop=\"name\">\n          <el-input v-model=\"createForm.name\" placeholder=\"请输入触发器名称\" />\n        </el-form-item>\n        <el-form-item label=\"描述\" prop=\"description\">\n          <el-input\n            v-model=\"createForm.description\"\n            type=\"textarea\"\n            :rows=\"3\"\n            placeholder=\"请输入触发器描述（可选）\"\n          />\n        </el-form-item>\n        <el-form-item label=\"备注\" prop=\"remark\">\n          <el-input\n            v-model=\"createForm.remark\"\n            type=\"textarea\"\n            :rows=\"2\"\n            placeholder=\"请输入备注（可选）\"\n          />\n        </el-form-item>\n      </el-form>\n      <template #footer>\n        <el-button @click=\"createDialogVisible = false\">取消</el-button>\n        <el-button type=\"primary\" @click=\"handleCreate\" :loading=\"creating\">\n          创建\n        </el-button>\n      </template>\n    </el-dialog>\n  </div>\n</template>\n\n<script setup>\nimport { ref, onMounted } from 'vue'\nimport { useRouter } from 'vue-router'\nimport { ElMessage } from 'element-plus'\nimport { Plus, Refresh, CopyDocument } from '@element-plus/icons-vue'\n\nconst router = useRouter()\n\nconst loading = ref(false)\nconst triggers = ref([])\nconst createDialogVisible = ref(false)\nconst creating = ref(false)\nconst createFormRef = ref(null)\n\nconst createForm = ref({\n  name: '',\n  description: '',\n  remark: ''\n})\n\nconst createRules = {\n  name: [\n    { required: true, message: '请输入触发器名称', trigger: 'blur' }\n  ]\n}\n\n// 加载触发器列表\nasync function loadTriggers() {\n  loading.value = true\n  try {\n    if (window.electronAPI) {\n      triggers.value = await window.electronAPI.getTriggers()\n    }\n  } catch (error) {\n    console.error('Failed to load triggers:', error)\n    ElMessage.error('加载触发器列表失败')\n  } finally {\n    loading.value = false\n  }\n}\n\n// 显示创建弹窗\nfunction showCreateDialog() {\n  createForm.value = {\n    name: '',\n    description: '',\n    remark: ''\n  }\n  createDialogVisible.value = true\n}\n\n// 创建触发器\nasync function handleCreate() {\n  if (!createFormRef.value) return\n\n  await createFormRef.value.validate(async (valid) => {\n    if (!valid) return\n\n    creating.value = true\n    try {\n      if (window.electronAPI) {\n        await window.electronAPI.createTrigger(createForm.value)\n        ElMessage.success('创建成功')\n        createDialogVisible.value = false\n        loadTriggers()\n      }\n    } catch (error) {\n      console.error('Failed to create trigger:', error)\n      ElMessage.error('创建失败')\n    } finally {\n      creating.value = false\n    }\n  })\n}\n\n// 复制路径\nfunction copyPath(path) {\n  navigator.clipboard.writeText(path).then(() => {\n    ElMessage.success('路径已复制')\n  }).catch(() => {\n    ElMessage.error('复制失败')\n  })\n}\n\n// 查看详情\nfunction viewDetail(row) {\n  router.push(`/triggers/${row.id}`)\n}\n\n// 查看执行记录\nfunction viewLogs(row) {\n  router.push(`/triggers/${row.id}?tab=logs`)\n}\n\n// 格式化日期\nfunction formatDate(dateStr) {\n  if (!dateStr) return ''\n  const date = new Date(dateStr)\n  return date.toLocaleString('zh-CN')\n}\n\nonMounted(() => {\n  loadTriggers()\n})\n</script>\n\n<style scoped>\n.trigger-manage {\n  height: 100%;\n}\n\n.path-cell {\n  display: flex;\n  align-items: center;\n}\n\n.path-text {\n  flex: 1;\n  overflow: hidden;\n  text-overflow: ellipsis;\n  white-space: nowrap;\n}\n</style>\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\views\\TriggerManage.vue"
}
```

*Tokens: 7 in, 3207 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\styles\global.css
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\views\TriggerManage.vue
```

## Assistant

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "<template>\n  <div class=\"trigger-detail\">\n    <!-- 返回按钮 -->\n    <div class=\"back-bar\">\n      <el-button link @click=\"goBack\">\n        <el-icon><ArrowLeft /></el-icon>\n        返回列表\n      </el-button>\n    </div>\n\n    <!-- 基本信息 -->\n    <div class=\"page-card\" v-loading=\"loading\">\n      <h3 class=\"card-title\">基本信息</h3>\n      <el-descriptions :column=\"2\" border v-if=\"trigger\">\n        <el-descriptions-item label=\"触发器ID\">{{ trigger.id }}</el-descriptions-item>\n        <el-descriptions-item label=\"名称\">{{ trigger.name }}</el-descriptions-item>\n        <el-descriptions-item label=\"描述\" :span=\"2\">{{ trigger.description || '-' }}</el-descriptions-item>\n        <el-descriptions-item label=\"备注\" :span=\"2\">{{ trigger.remark || '-' }}</el-descriptions-item>\n        <el-descriptions-item label=\"文件夹路径\" :span=\"2\">\n          <div class=\"path-wrapper\">\n            <code>{{ trigger.folder_path }}</code>\n            <el-button type=\"primary\" link size=\"small\" @click=\"copyPath(trigger.folder_path)\">\n              复制\n            </el-button>\n          </div>\n        </el-descriptions-item>\n        <el-descriptions-item label=\"创建时间\">{{ formatDate(trigger.created_at) }}</el-descriptions-item>\n        <el-descriptions-item label=\"执行次数\">{{ trigger.execution_count || 0 }}</el-descriptions-item>\n      </el-descriptions>\n\n      <div class=\"api-info\" v-if=\"trigger\">\n        <h4>调用接口</h4>\n        <div class=\"code-block\">\nPOST http://localhost:8089/trigger/{{ trigger.id }}\nHeader: FileWebHook-Authorization: {用户密钥}\nContent-Type: application/json\nBody: {业务参数JSON}\n        </div>\n      </div>\n    </div>\n\n    <!-- 执行记录 -->\n    <div class=\"page-card\">\n      <div class=\"card-header\">\n        <h3 class=\"card-title\">执行记录</h3>\n        <div class=\"card-actions\">\n          <el-button size=\"small\" @click=\"loadLogs\">\n            <el-icon><Refresh /></el-icon>\n            刷新\n          </el-button>\n          <el-button size=\"small\" type=\"danger\" @click=\"handleClearLogs\" :disabled=\"logs.length === 0\">\n            清空记录\n          </el-button>\n        </div>\n      </div>\n\n      <!-- 筛选 -->\n      <div class=\"filter-bar\">\n        <el-select v-model=\"filterStatus\" placeholder=\"执行状态\" clearable style=\"width: 180px\">\n          <el-option\n            v-for=\"(label, value) in statusMap\"\n            :key=\"value\"\n            :label=\"label\"\n            :value=\"value\"\n          />\n        </el-select>\n        <el-date-picker\n          v-model=\"filterDateRange\"\n          type=\"daterange\"\n          range-separator=\"至\"\n          start-placeholder=\"开始日期\"\n          end-placeholder=\"结束日期\"\n          style=\"width: 300px\"\n        />\n        <el-button type=\"primary\" @click=\"loadLogs\">查询</el-button>\n      </div>\n\n      <!-- 表格 -->\n      <el-table :data=\"logs\" v-loading=\"logsLoading\" stripe style=\"margin-top: 16px\">\n        <el-table-column prop=\"event_id\" label=\"事件ID\" width=\"200\" show-overflow-tooltip />\n        <el-table-column prop=\"user_name\" label=\"调用用户\" width=\"120\" />\n        <el-table-column prop=\"status\" label=\"状态\" width=\"150\">\n          <template #default=\"{ row }\">\n            <el-tag :type=\"getStatusType(row.status)\" size=\"small\">\n              {{ statusMap[row.status] || row.status }}\n            </el-tag>\n          </template>\n        </el-table-column>\n        <el-table-column prop=\"request_time\" label=\"请求时间\" width=\"180\">\n          <template #default=\"{ row }\">\n            {{ formatDate(row.request_time) }}\n          </template>\n        </el-table-column>\n        <el-table-column prop=\"total_duration\" label=\"总耗时\" width=\"100\">\n          <template #default=\"{ row }\">\n            {{ row.total_duration ? `${row.total_duration}ms` : '-' }}\n          </template>\n        </el-table-column>\n        <el-table-column label=\"操作\" width=\"120\" fixed=\"right\">\n          <template #default=\"{ row }\">\n            <el-button type=\"primary\" link @click=\"showLogDetail(row)\">\n              详情\n            </el-button>\n          </template>\n        </el-table-column>\n      </el-table>\n\n      <!-- 分页 -->\n      <div class=\"pagination-wrapper\" v-if=\"pagination.total > 0\">\n        <el-pagination\n          v-model:current-page=\"pagination.page\"\n          v-model:page-size=\"pagination.pageSize\"\n          :total=\"pagination.total\"\n          :page-sizes=\"[10, 20, 50, 100]\"\n          layout=\"total, sizes, prev, pager, next\"\n          @size-change=\"loadLogs\"\n          @current-change=\"loadLogs\"\n        />\n      </div>\n    </div>\n\n    <!-- 执行记录详情弹窗 -->\n    <el-dialog v-model=\"logDetailVisible\" title=\"执行记录详情\" width=\"700px\">\n      <el-descriptions :column=\"2\" border v-if=\"currentLog\">\n        <el-descriptions-item label=\"事件ID\" :span=\"2\">{{ currentLog.event_id }}</el-descriptions-item>\n        <el-descriptions-item label=\"触发器ID\">{{ currentLog.trigger_id }}</el-descriptions-item>\n        <el-descriptions-item label=\"调用用户\">{{ currentLog.user_name }}</el-descriptions-item>\n        <el-descriptions-item label=\"状态\" :span=\"2\">\n          <el-tag :type=\"getStatusType(currentLog.status)\">\n            {{ statusMap[currentLog.status] || currentLog.status }}\n          </el-tag>\n        </el-descriptions-item>\n        <el-descriptions-item label=\"请求时间\">{{ formatDate(currentLog.request_time) }}</el-descriptions-item>\n        <el-descriptions-item label=\"影刀开始时间\">{{ formatDate(currentLog.shadowbot_start_time) }}</el-descriptions-item>\n        <el-descriptions-item label=\"影刀完成时间\">{{ formatDate(currentLog.shadowbot_end_time) }}</el-descriptions-item>\n        <el-descriptions-item label=\"回调时间\">{{ formatDate(currentLog.callback_time) }}</el-descriptions-item>\n        <el-descriptions-item label=\"总耗时\">{{ currentLog.total_duration ? `${currentLog.total_duration}ms` : '-' }}</el-descriptions-item>\n        <el-descriptions-item label=\"影刀执行耗时\">{{ currentLog.shadowbot_duration ? `${currentLog.shadowbot_duration}ms` : '-' }}</el-descriptions-item>\n        <el-descriptions-item label=\"入参\" :span=\"2\">\n          <div class=\"code-block\">{{ formatJson(currentLog.request_params) }}</div>\n        </el-descriptions-item>\n        <el-descriptions-item label=\"出参\" :span=\"2\">\n          <div class=\"code-block\">{{ formatJson(currentLog.response_params) }}</div>\n        </el-descriptions-item>\n        <el-descriptions-item label=\"错误信息\" :span=\"2\" v-if=\"currentLog.error_message\">\n          <div class=\"error-message\">{{ currentLog.error_message }}</div>\n        </el-descriptions-item>\n      </el-descriptions>\n    </el-dialog>\n  </div>\n</template>\n\n<script setup>\nimport { ref, onMounted } from 'vue'\nimport { useRoute, useRouter } from 'vue-router'\nimport { ElMessage, ElMessageBox } from 'element-plus'\nimport { ArrowLeft, Refresh } from '@element-plus/icons-vue'\n\nconst route = useRoute()\nconst router = useRouter()\n\nconst loading = ref(false)\nconst logsLoading = ref(false)\nconst trigger = ref(null)\nconst logs = ref([])\nconst logDetailVisible = ref(false)\nconst currentLog = ref(null)\n\nconst filterStatus = ref('')\nconst filterDateRange = ref(null)\n\nconst pagination = ref({\n  page: 1,\n  pageSize: 20,\n  total: 0\n})\n\nconst statusMap = {\n  REQUESTED: '用户已请求',\n  QUEUED: '已收到请求放入队列',\n  PRE_RESPONDED: '已预响应外部服务',\n  FILE_WRITTEN: '已写入文件待影刀执行',\n  EXECUTING: '影刀执行中',\n  COMPLETED: '影刀执行完成',\n  CALLBACK_SUCCESS: '回调用户完成',\n  CALLBACK_FAILED: '回调外部服务失败'\n}\n\n// 加载触发器详情\nasync function loadTrigger() {\n  loading.value = true\n  try {\n    if (window.electronAPI) {\n      trigger.value = await window.electronAPI.getTriggerById(parseInt(route.params.id))\n    }\n  } catch (error) {\n    console.error('Failed to load trigger:', error)\n    ElMessage.error('加载触发器详情失败')\n  } finally {\n    loading.value = false\n  }\n}\n\n// 加载执行记录\nasync function loadLogs() {\n  logsLoading.value = true\n  try {\n    if (window.electronAPI) {\n      const params = {\n        triggerId: parseInt(route.params.id),\n        page: pagination.value.page,\n        pageSize: pagination.value.pageSize,\n        status: filterStatus.value || undefined,\n        startTime: filterDateRange.value?.[0]?.toISOString(),\n        endTime: filterDateRange.value?.[1]?.toISOString()\n      }\n      const result = await window.electronAPI.getExecutionLogs(params)\n      logs.value = result.data\n      pagination.value.total = result.total\n    }\n  } catch (error) {\n    console.error('Failed to load logs:', error)\n    ElMessage.error('加载执行记录失败')\n  } finally {\n    logsLoading.value = false\n  }\n}\n\n// 清空记录\nasync function handleClearLogs() {\n  try {\n    await ElMessageBox.confirm('确定要清空该触发器的所有执行记录吗？此操作不可恢复！', '警告', {\n      type: 'warning',\n      confirmButtonText: '确定',\n      cancelButtonText: '取消'\n    })\n\n    if (window.electronAPI) {\n      await window.electronAPI.clearExecutionLogs(parseInt(route.params.id))\n      ElMessage.success('已清空执行记录')\n      loadLogs()\n    }\n  } catch (error) {\n    if (error !== 'cancel') {\n      console.error('Failed to clear logs:', error)\n      ElMessage.error('清空失败')\n    }\n  }\n}\n\n// 显示日志详情\nfunction showLogDetail(log) {\n  currentLog.value = log\n  logDetailVisible.value = true\n}\n\n// 复制路径\nfunction copyPath(path) {\n  navigator.clipboard.writeText(path).then(() => {\n    ElMessage.success('路径已复制')\n  })\n}\n\n// 返回\nfunction goBack() {\n  router.push('/triggers')\n}\n\n// 获取状态标签类型\nfunction getStatusType(status) {\n  const typeMap = {\n    CALLBACK_SUCCESS: 'success',\n    CALLBACK_FAILED: 'danger',\n    EXECUTING: 'warning',\n    COMPLETED: 'info'\n  }\n  return typeMap[status] || ''\n}\n\n// 格式化日期\nfunction formatDate(dateStr) {\n  if (!dateStr) return '-'\n  return new Date(dateStr).toLocaleString('zh-CN')\n}\n\n// 格式化 JSON\nfunction formatJson(jsonStr) {\n  if (!jsonStr) return '-'\n  try {\n    const obj = typeof jsonStr === 'string' ? JSON.parse(jsonStr) : jsonStr\n    return JSON.stringify(obj, null, 2)\n  } catch {\n    return jsonStr\n  }\n}\n\nonMounted(() => {\n  loadTrigger()\n  loadLogs()\n})\n</script>\n\n<style scoped>\n.trigger-detail {\n  height: 100%;\n}\n\n.back-bar {\n  margin-bottom: 16px;\n}\n\n.card-title {\n  margin: 0 0 16px;\n  font-size: 16px;\n  font-weight: 600;\n}\n\n.card-header {\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n  margin-bottom: 16px;\n}\n\n.card-header .card-title {\n  margin: 0;\n}\n\n.card-actions {\n  display: flex;\n  gap: 8px;\n}\n\n.path-wrapper {\n  display: flex;\n  align-items: center;\n  gap: 8px;\n}\n\n.path-wrapper code {\n  flex: 1;\n  background-color: #f5f7fa;\n  padding: 4px 8px;\n  border-radius: 4px;\n  font-size: 13px;\n}\n\n.api-info {\n  margin-top: 24px;\n}\n\n.api-info h4 {\n  margin: 0 0 12px;\n  font-size: 14px;\n  font-weight: 600;\n}\n\n.filter-bar {\n  display: flex;\n  gap: 12px;\n  align-items: center;\n}\n\n.error-message {\n  color: #f56c6c;\n  word-break: break-all;\n}\n</style>\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\views\\TriggerDetail.vue"
}
```

*Tokens: 7 in, 5 out*

## Assistant

### Tool: Write

```json
{
  "content": "<template>\n  <div class=\"user-manage\">\n    <!-- 操作栏 -->\n    <div class=\"action-bar\">\n      <div class=\"action-bar-left\">\n        <el-button type=\"primary\" @click=\"showCreateDialog\">\n          <el-icon><Plus /></el-icon>\n          新建用户\n        </el-button>\n      </div>\n      <div class=\"action-bar-right\">\n        <el-button @click=\"loadUsers\" :loading=\"loading\">\n          <el-icon><Refresh /></el-icon>\n          刷新\n        </el-button>\n      </div>\n    </div>\n\n    <!-- 用户列表 -->\n    <div class=\"page-card\">\n      <el-table :data=\"users\" v-loading=\"loading\" stripe>\n        <el-table-column prop=\"name\" label=\"用户名称\" min-width=\"120\" />\n        <el-table-column prop=\"remark\" label=\"备注\" min-width=\"150\" show-overflow-tooltip />\n        <el-table-column prop=\"callback_url\" label=\"回调地址\" min-width=\"200\" show-overflow-tooltip />\n        <el-table-column label=\"密钥\" min-width=\"320\">\n          <template #default=\"{ row }\">\n            <div class=\"secret-cell\">\n              <code>{{ row.secret_key }}</code>\n              <el-tooltip content=\"复制密钥\" placement=\"top\">\n                <el-icon class=\"copy-btn\" @click=\"copySecret(row.secret_key)\">\n                  <CopyDocument />\n                </el-icon>\n              </el-tooltip>\n            </div>\n          </template>\n        </el-table-column>\n        <el-table-column prop=\"permission_count\" label=\"权限数\" width=\"80\" align=\"center\" />\n        <el-table-column prop=\"created_at\" label=\"创建时间\" width=\"180\">\n          <template #default=\"{ row }\">\n            {{ formatDate(row.created_at) }}\n          </template>\n        </el-table-column>\n        <el-table-column label=\"操作\" width=\"280\" fixed=\"right\">\n          <template #default=\"{ row }\">\n            <el-button type=\"primary\" link @click=\"showEditDialog(row)\">编辑</el-button>\n            <el-button type=\"primary\" link @click=\"showPermissionDialog(row)\">分配权限</el-button>\n            <el-button type=\"warning\" link @click=\"handleResetSecret(row)\">重置密钥</el-button>\n            <el-button type=\"danger\" link @click=\"handleDelete(row)\">删除</el-button>\n          </template>\n        </el-table-column>\n      </el-table>\n    </div>\n\n    <!-- 新建/编辑用户弹窗 -->\n    <el-dialog\n      v-model=\"formDialogVisible\"\n      :title=\"isEdit ? '编辑用户' : '新建用户'\"\n      width=\"500px\"\n      :close-on-click-modal=\"false\"\n    >\n      <el-form\n        ref=\"formRef\"\n        :model=\"userForm\"\n        :rules=\"formRules\"\n        label-width=\"100px\"\n      >\n        <el-form-item label=\"用户名称\" prop=\"name\">\n          <el-input v-model=\"userForm.name\" placeholder=\"请输入用户名称\" />\n        </el-form-item>\n        <el-form-item label=\"回调地址\" prop=\"callbackUrl\">\n          <el-input v-model=\"userForm.callbackUrl\" placeholder=\"例如: http://192.168.1.100:8080\" />\n          <div class=\"form-tip\">外部服务接收回调的地址（IP:端口或域名）</div>\n        </el-form-item>\n        <el-form-item label=\"备注\" prop=\"remark\">\n          <el-input\n            v-model=\"userForm.remark\"\n            type=\"textarea\"\n            :rows=\"2\"\n            placeholder=\"请输入备注（可选）\"\n          />\n        </el-form-item>\n      </el-form>\n      <template #footer>\n        <el-button @click=\"formDialogVisible = false\">取消</el-button>\n        <el-button type=\"primary\" @click=\"handleSubmit\" :loading=\"submitting\">\n          {{ isEdit ? '保存' : '创建' }}\n        </el-button>\n      </template>\n    </el-dialog>\n\n    <!-- 分配权限弹窗 -->\n    <el-dialog\n      v-model=\"permissionDialogVisible\"\n      title=\"分配触发器权限\"\n      width=\"600px\"\n      :close-on-click-modal=\"false\"\n    >\n      <div class=\"permission-info\" v-if=\"currentUser\">\n        <span>用户：</span>\n        <strong>{{ currentUser.name }}</strong>\n      </div>\n\n      <el-checkbox-group v-model=\"selectedPermissions\" class=\"permission-list\">\n        <el-checkbox\n          v-for=\"trigger in triggers\"\n          :key=\"trigger.id\"\n          :label=\"trigger.id\"\n          class=\"permission-item\"\n        >\n          <div class=\"trigger-info\">\n            <span class=\"trigger-name\">{{ trigger.name }}</span>\n            <span class=\"trigger-id\">(ID: {{ trigger.id }})</span>\n          </div>\n        </el-checkbox>\n      </el-checkbox-group>\n\n      <template #footer>\n        <el-button @click=\"permissionDialogVisible = false\">取消</el-button>\n        <el-button type=\"primary\" @click=\"handleSavePermissions\" :loading=\"savingPermissions\">\n          保存\n        </el-button>\n      </template>\n    </el-dialog>\n  </div>\n</template>\n\n<script setup>\nimport { ref, onMounted } from 'vue'\nimport { ElMessage, ElMessageBox } from 'element-plus'\nimport { Plus, Refresh, CopyDocument } from '@element-plus/icons-vue'\n\nconst loading = ref(false)\nconst users = ref([])\nconst triggers = ref([])\n\n// 表单相关\nconst formDialogVisible = ref(false)\nconst formRef = ref(null)\nconst isEdit = ref(false)\nconst submitting = ref(false)\nconst currentUserId = ref(null)\n\nconst userForm = ref({\n  name: '',\n  callbackUrl: '',\n  remark: ''\n})\n\nconst formRules = {\n  name: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],\n  callbackUrl: [\n    { required: true, message: '请输入回调地址', trigger: 'blur' },\n    { pattern: /^https?:\\/\\/.+/, message: '请输入有效的HTTP地址', trigger: 'blur' }\n  ]\n}\n\n// 权限相关\nconst permissionDialogVisible = ref(false)\nconst currentUser = ref(null)\nconst selectedPermissions = ref([])\nconst savingPermissions = ref(false)\n\n// 加载用户列表\nasync function loadUsers() {\n  loading.value = true\n  try {\n    if (window.electronAPI) {\n      users.value = await window.electronAPI.getUsers()\n    }\n  } catch (error) {\n    console.error('Failed to load users:', error)\n    ElMessage.error('加载用户列表失败')\n  } finally {\n    loading.value = false\n  }\n}\n\n// 加载触发器列表\nasync function loadTriggers() {\n  try {\n    if (window.electronAPI) {\n      triggers.value = await window.electronAPI.getTriggers()\n    }\n  } catch (error) {\n    console.error('Failed to load triggers:', error)\n  }\n}\n\n// 显示新建弹窗\nfunction showCreateDialog() {\n  isEdit.value = false\n  currentUserId.value = null\n  userForm.value = {\n    name: '',\n    callbackUrl: '',\n    remark: ''\n  }\n  formDialogVisible.value = true\n}\n\n// 显示编辑弹窗\nfunction showEditDialog(user) {\n  isEdit.value = true\n  currentUserId.value = user.user_id\n  userForm.value = {\n    name: user.name,\n    callbackUrl: user.callback_url,\n    remark: user.remark || ''\n  }\n  formDialogVisible.value = true\n}\n\n// 提交表单\nasync function handleSubmit() {\n  if (!formRef.value) return\n\n  await formRef.value.validate(async (valid) => {\n    if (!valid) return\n\n    submitting.value = true\n    try {\n      if (window.electronAPI) {\n        if (isEdit.value) {\n          await window.electronAPI.updateUser(currentUserId.value, userForm.value)\n          ElMessage.success('保存成功')\n        } else {\n          await window.electronAPI.createUser(userForm.value)\n          ElMessage.success('创建成功')\n        }\n        formDialogVisible.value = false\n        loadUsers()\n      }\n    } catch (error) {\n      console.error('Failed to submit:', error)\n      ElMessage.error(isEdit.value ? '保存失败' : '创建失败')\n    } finally {\n      submitting.value = false\n    }\n  })\n}\n\n// 显示权限弹窗\nasync function showPermissionDialog(user) {\n  currentUser.value = user\n  try {\n    if (window.electronAPI) {\n      selectedPermissions.value = await window.electronAPI.getUserPermissions(user.user_id)\n    }\n  } catch (error) {\n    console.error('Failed to load permissions:', error)\n    selectedPermissions.value = []\n  }\n  permissionDialogVisible.value = true\n}\n\n// 保存权限\nasync function handleSavePermissions() {\n  savingPermissions.value = true\n  try {\n    if (window.electronAPI && currentUser.value) {\n      await window.electronAPI.updateUserPermissions(currentUser.value.user_id, selectedPermissions.value)\n      ElMessage.success('权限保存成功')\n      permissionDialogVisible.value = false\n      loadUsers()\n    }\n  } catch (error) {\n    console.error('Failed to save permissions:', error)\n    ElMessage.error('权限保存失败')\n  } finally {\n    savingPermissions.value = false\n  }\n}\n\n// 重置密钥\nasync function handleResetSecret(user) {\n  try {\n    await ElMessageBox.confirm(`确定要重置用户 \"${user.name}\" 的密钥吗？重置后原密钥将失效！`, '警告', {\n      type: 'warning',\n      confirmButtonText: '确定',\n      cancelButtonText: '取消'\n    })\n\n    if (window.electronAPI) {\n      await window.electronAPI.resetUserSecret(user.user_id)\n      ElMessage.success('密钥已重置')\n      loadUsers()\n    }\n  } catch (error) {\n    if (error !== 'cancel') {\n      console.error('Failed to reset secret:', error)\n      ElMessage.error('重置失败')\n    }\n  }\n}\n\n// 删除用户\nasync function handleDelete(user) {\n  try {\n    await ElMessageBox.confirm(`确定要删除用户 \"${user.name}\" 吗？`, '警告', {\n      type: 'warning',\n      confirmButtonText: '确定',\n      cancelButtonText: '取消'\n    })\n\n    if (window.electronAPI) {\n      await window.electronAPI.deleteUser(user.user_id)\n      ElMessage.success('删除成功')\n      loadUsers()\n    }\n  } catch (error) {\n    if (error !== 'cancel') {\n      console.error('Failed to delete user:', error)\n      ElMessage.error('删除失败')\n    }\n  }\n}\n\n// 复制密钥\nfunction copySecret(secret) {\n  navigator.clipboard.writeText(secret).then(() => {\n    ElMessage.success('密钥已复制')\n  })\n}\n\n// 格式化日期\nfunction formatDate(dateStr) {\n  if (!dateStr) return ''\n  return new Date(dateStr).toLocaleString('zh-CN')\n}\n\nonMounted(() => {\n  loadUsers()\n  loadTriggers()\n})\n</script>\n\n<style scoped>\n.user-manage {\n  height: 100%;\n}\n\n.secret-cell {\n  display: flex;\n  align-items: center;\n}\n\n.secret-cell code {\n  flex: 1;\n  font-size: 12px;\n  color: #606266;\n}\n\n.form-tip {\n  font-size: 12px;\n  color: #909399;\n  margin-top: 4px;\n}\n\n.permission-info {\n  margin-bottom: 16px;\n  font-size: 14px;\n}\n\n.permission-list {\n  display: flex;\n  flex-direction: column;\n  gap: 8px;\n  max-height: 400px;\n  overflow-y: auto;\n}\n\n.permission-item {\n  padding: 8px 12px;\n  background-color: #f5f7fa;\n  border-radius: 4px;\n}\n\n.trigger-info {\n  display: flex;\n  align-items: center;\n  gap: 8px;\n}\n\n.trigger-name {\n  font-weight: 500;\n}\n\n.trigger-id {\n  color: #909399;\n  font-size: 12px;\n}\n</style>\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\views\\UserManage.vue"
}
```

*Tokens: 7 in, 9703 out*

## Assistant

### Tool: Write

```json
{
  "content": "<template>\n  <div class=\"system-settings\">\n    <div class=\"page-card\">\n      <h3 class=\"card-title\">系统设置</h3>\n\n      <el-form\n        ref=\"formRef\"\n        :model=\"settings\"\n        label-width=\"160px\"\n        v-loading=\"loading\"\n      >\n        <el-form-item label=\"触发器文件路径\">\n          <el-input v-model=\"settings.trigger_files_path\" placeholder=\"触发器文件存放路径\">\n            <template #append>\n              <el-button @click=\"selectFolder\">选择</el-button>\n            </template>\n          </el-input>\n          <div class=\"form-tip\">影刀文件触发器需监听此路径下的文件夹</div>\n        </el-form-item>\n\n        <el-form-item label=\"全局队列最大长度\">\n          <el-input-number v-model=\"settings.max_queue_size\" :min=\"1\" :max=\"1000\" />\n          <div class=\"form-tip\">同时等待处理的最大任务数量</div>\n        </el-form-item>\n\n        <el-form-item label=\"HTTP监听地址\">\n          <el-select v-model=\"settings.http_host\" style=\"width: 200px\">\n            <el-option label=\"0.0.0.0 (允许外部访问)\" value=\"0.0.0.0\" />\n            <el-option label=\"127.0.0.1 (仅本机)\" value=\"127.0.0.1\" />\n          </el-select>\n          <div class=\"form-tip\">修改后需重启应用生效</div>\n        </el-form-item>\n\n        <el-form-item label=\"HTTP服务端口\">\n          <el-input v-model=\"settings.http_port\" disabled style=\"width: 200px\" />\n          <div class=\"form-tip\">端口固定为 8089，不可修改</div>\n        </el-form-item>\n\n        <el-form-item>\n          <el-button type=\"primary\" @click=\"handleSave\" :loading=\"saving\">\n            保存设置\n          </el-button>\n        </el-form-item>\n      </el-form>\n    </div>\n\n    <div class=\"page-card\">\n      <h3 class=\"card-title\">服务状态</h3>\n\n      <el-descriptions :column=\"2\" border>\n        <el-descriptions-item label=\"HTTP服务\">\n          <el-tag :type=\"serverStatus.running ? 'success' : 'danger'\">\n            {{ serverStatus.running ? '运行中' : '已停止' }}\n          </el-tag>\n        </el-descriptions-item>\n        <el-descriptions-item label=\"监听地址\">\n          {{ serverStatus.host }}:{{ serverStatus.port }}\n        </el-descriptions-item>\n        <el-descriptions-item label=\"队列状态\">\n          {{ queueStatus.isProcessing ? '处理中' : '空闲' }}\n        </el-descriptions-item>\n        <el-descriptions-item label=\"队列长度\">\n          {{ queueStatus.size }} / {{ settings.max_queue_size }}\n        </el-descriptions-item>\n      </el-descriptions>\n\n      <div class=\"queue-info\" v-if=\"queueStatus.currentTask\">\n        <h4>当前任务</h4>\n        <p>事件ID: {{ queueStatus.currentTask.eventId }}</p>\n        <p>触发器ID: {{ queueStatus.currentTask.triggerId }}</p>\n      </div>\n    </div>\n\n    <div class=\"page-card\">\n      <h3 class=\"card-title\">关于</h3>\n\n      <el-descriptions :column=\"1\" border>\n        <el-descriptions-item label=\"软件名称\">ShadowBot-FileWebHook</el-descriptions-item>\n        <el-descriptions-item label=\"版本\">1.0.0</el-descriptions-item>\n        <el-descriptions-item label=\"开源协议\">MIT License</el-descriptions-item>\n        <el-descriptions-item label=\"GitHub\">\n          <a href=\"https://github.com/shadowbot/filewebhook\" target=\"_blank\">\n            https://github.com/shadowbot/filewebhook\n          </a>\n        </el-descriptions-item>\n      </el-descriptions>\n    </div>\n  </div>\n</template>\n\n<script setup>\nimport { ref, onMounted, onUnmounted } from 'vue'\nimport { ElMessage } from 'element-plus'\n\nconst loading = ref(false)\nconst saving = ref(false)\n\nconst settings = ref({\n  trigger_files_path: '',\n  max_queue_size: 50,\n  http_host: '0.0.0.0',\n  http_port: '8089'\n})\n\nconst serverStatus = ref({\n  running: false,\n  host: '0.0.0.0',\n  port: '8089'\n})\n\nconst queueStatus = ref({\n  size: 0,\n  isProcessing: false,\n  currentTask: null\n})\n\nlet statusInterval = null\n\n// 加载设置\nasync function loadSettings() {\n  loading.value = true\n  try {\n    if (window.electronAPI) {\n      const data = await window.electronAPI.getSettings()\n      settings.value = {\n        trigger_files_path: data.trigger_files_path || '',\n        max_queue_size: parseInt(data.max_queue_size || '50', 10),\n        http_host: data.http_host || '0.0.0.0',\n        http_port: data.http_port || '8089'\n      }\n    }\n  } catch (error) {\n    console.error('Failed to load settings:', error)\n    ElMessage.error('加载设置失败')\n  } finally {\n    loading.value = false\n  }\n}\n\n// 保存设置\nasync function handleSave() {\n  saving.value = true\n  try {\n    if (window.electronAPI) {\n      await window.electronAPI.updateSettings({\n        trigger_files_path: settings.value.trigger_files_path,\n        max_queue_size: String(settings.value.max_queue_size),\n        http_host: settings.value.http_host\n      })\n      ElMessage.success('设置已保存')\n    }\n  } catch (error) {\n    console.error('Failed to save settings:', error)\n    ElMessage.error('保存失败')\n  } finally {\n    saving.value = false\n  }\n}\n\n// 选择文件夹\nfunction selectFolder() {\n  // 这个功能需要通过 Electron 的 dialog API 实现\n  // 暂时提示用户手动输入\n  ElMessage.info('请手动输入文件夹路径')\n}\n\n// 加载服务状态\nasync function loadServerStatus() {\n  try {\n    if (window.electronAPI) {\n      const status = await window.electronAPI.getServerStatus()\n      if (status) {\n        serverStatus.value = status\n      }\n    }\n  } catch (error) {\n    console.error('Failed to load server status:', error)\n  }\n}\n\n// 加载队列状态\nasync function loadQueueStatus() {\n  try {\n    if (window.electronAPI) {\n      const status = await window.electronAPI.getQueueStatus()\n      if (status) {\n        queueStatus.value = status\n      }\n    }\n  } catch (error) {\n    console.error('Failed to load queue status:', error)\n  }\n}\n\n// 定时刷新状态\nfunction startStatusPolling() {\n  statusInterval = setInterval(() => {\n    loadServerStatus()\n    loadQueueStatus()\n  }, 5000)\n}\n\nonMounted(() => {\n  loadSettings()\n  loadServerStatus()\n  loadQueueStatus()\n  startStatusPolling()\n})\n\nonUnmounted(() => {\n  if (statusInterval) {\n    clearInterval(statusInterval)\n  }\n})\n</script>\n\n<style scoped>\n.system-settings {\n  height: 100%;\n}\n\n.card-title {\n  margin: 0 0 24px;\n  font-size: 16px;\n  font-weight: 600;\n}\n\n.form-tip {\n  font-size: 12px;\n  color: #909399;\n  margin-top: 4px;\n}\n\n.queue-info {\n  margin-top: 16px;\n  padding: 12px;\n  background-color: #f5f7fa;\n  border-radius: 4px;\n}\n\n.queue-info h4 {\n  margin: 0 0 8px;\n  font-size: 14px;\n}\n\n.queue-info p {\n  margin: 4px 0;\n  font-size: 13px;\n  color: #606266;\n}\n</style>\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\views\\SystemSettings.vue"
}
```

*Tokens: 7 in, 9703 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\views\TriggerDetail.vue
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\views\UserManage.vue
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\views\SystemSettings.vue
```

## Assistant

*Tokens: 7 in, 3 out*

## Assistant

### Tool: Write

```json
{
  "content": "const { ipcMain } = require('electron')\nconst userModel = require('../database/userModel')\nconst triggerModel = require('../database/triggerModel')\nconst executionLogModel = require('../database/executionLogModel')\nconst settingsModel = require('../database/settingsModel')\nconst { getQueueStatus } = require('../server/services/queue')\n\n// 注册所有 IPC 处理程序\nfunction registerIpcHandlers() {\n  // ==================== 触发器相关 ====================\n\n  ipcMain.handle('get-triggers', async () => {\n    return triggerModel.getAllTriggers()\n  })\n\n  ipcMain.handle('create-trigger', async (event, data) => {\n    const triggerFilesPath = settingsModel.getSetting('trigger_files_path')\n    return triggerModel.createTrigger(data, triggerFilesPath)\n  })\n\n  ipcMain.handle('get-trigger-by-id', async (event, id) => {\n    return triggerModel.getTriggerById(id)\n  })\n\n  // ==================== 用户相关 ====================\n\n  ipcMain.handle('get-users', async () => {\n    return userModel.getAllUsers()\n  })\n\n  ipcMain.handle('create-user', async (event, data) => {\n    return userModel.createUser(data)\n  })\n\n  ipcMain.handle('update-user', async (event, userId, data) => {\n    return userModel.updateUser(userId, data)\n  })\n\n  ipcMain.handle('delete-user', async (event, userId) => {\n    return userModel.deleteUser(userId)\n  })\n\n  ipcMain.handle('reset-user-secret', async (event, userId) => {\n    return userModel.resetUserSecret(userId)\n  })\n\n  // ==================== 用户权限相关 ====================\n\n  ipcMain.handle('get-user-permissions', async (event, userId) => {\n    return userModel.getUserPermissions(userId)\n  })\n\n  ipcMain.handle('update-user-permissions', async (event, userId, triggerIds) => {\n    return userModel.updateUserPermissions(userId, triggerIds)\n  })\n\n  // ==================== 执行记录相关 ====================\n\n  ipcMain.handle('get-execution-logs', async (event, params) => {\n    return executionLogModel.getExecutionLogs(params)\n  })\n\n  ipcMain.handle('delete-execution-logs', async (event, ids) => {\n    return executionLogModel.deleteExecutionLogs(ids)\n  })\n\n  ipcMain.handle('clear-execution-logs', async (event, triggerId) => {\n    return executionLogModel.clearExecutionLogsByTriggerId(triggerId)\n  })\n\n  ipcMain.handle('export-execution-logs', async (event, params) => {\n    return executionLogModel.exportExecutionLogs(params)\n  })\n\n  // ==================== 系统设置相关 ====================\n\n  ipcMain.handle('get-settings', async () => {\n    return settingsModel.getAllSettings()\n  })\n\n  ipcMain.handle('update-settings', async (event, settings) => {\n    return settingsModel.updateSettings(settings)\n  })\n\n  // ==================== 服务状态相关 ====================\n\n  ipcMain.handle('get-server-status', async () => {\n    const host = settingsModel.getSetting('http_host') || '0.0.0.0'\n    const port = settingsModel.getSetting('http_port') || '8089'\n    return {\n      running: true, // 服务启动后才会注册这些处理程序\n      host,\n      port\n    }\n  })\n\n  ipcMain.handle('get-queue-status', async () => {\n    return getQueueStatus()\n  })\n}\n\nmodule.exports = {\n  registerIpcHandlers\n}\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\ipc\\handlers.js"
}
```

*Tokens: 7 in, 3 out*

## Assistant

### Tool: Write

```json
{
  "content": "<svg width=\"32\" height=\"32\" viewBox=\"0 0 32 32\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n  <circle cx=\"16\" cy=\"16\" r=\"14\" fill=\"#FC6868\"/>\n  <path d=\"M16 8C11.582 8 8 11.582 8 16C8 20.418 11.582 24 16 24C20.418 24 24 20.418 24 16C24 11.582 20.418 8 16 8ZM16 22C12.686 22 10 19.314 10 16C10 12.686 12.686 10 16 10C19.314 10 22 12.686 22 16C22 19.314 19.314 22 16 22Z\" fill=\"white\"/>\n  <circle cx=\"16\" cy=\"16\" r=\"4\" fill=\"white\"/>\n  <path d=\"M16 6L16 4M16 28L16 26M6 16L4 16M28 16L26 16\" stroke=\"#FC6868\" stroke-width=\"2\" stroke-linecap=\"round\"/>\n</svg>\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\src\\assets\\logo.svg"
}
```

*Tokens: 7 in, 1487 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\electron\ipc\handlers.js
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\src\assets\logo.svg
```

## Assistant

*Tokens: 7 in, 116 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\main.js"
}
```

*Tokens: 7 in, 116 out*

## User

### Tool Result

```
     1→const { app, BrowserWindow, ipcMain } = require('electron')
     2→const path = require('path')
     3→const { initDatabase } = require('./database')
     4→const { startServer, stopServer } = require('./server')
     5→
     6→// 禁用硬件加速（可选，某些情况下可以解决兼容性问题）
     7→// app.disableHardwareAcceleration()
     8→
     9→let mainWindow = null
    10→let httpServer = null
    11→
    12→function createWindow() {
    13→  mainWindow = new BrowserWindow({
    14→    width: 1200,
    15→    height: 800,
    16→    minWidth: 1000,
    17→    minHeight: 700,
    18→    webPreferences: {
    19→      preload: path.join(__dirname, 'preload.js'),
    20→      contextIsolation: true,
    21→      nodeIntegration: false
    22→    },
    23→    icon: path.join(__dirname, '../resources/icon.ico'),
    24→    title: 'ShadowBot-FileWebHook'
    25→  })
    26→
    27→  // 开发环境加载本地服务，生产环境加载打包后的文件
    28→  if (process.env.NODE_ENV === 'development' || process.env.VITE_DEV_SERVER_URL) {
    29→    mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL || 'http://localhost:5173')
    30→    mainWindow.webContents.openDevTools()
    31→  } else {
    32→    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'))
    33→  }
    34→
    35→  mainWindow.on('closed', () => {
    36→    mainWindow = null
    37→  })
    38→}
    39→
    40→// 应用初始化
    41→app.whenReady().then(async () => {
    42→  // 初始化数据库
    43→  try {
    44→    await initDatabase()
    45→    console.log('Database initialized successfully')
    46→  } catch (error) {
    47→    console.error('Failed to initialize database:', error)
    48→  }
    49→
    50→  // 启动 HTTP 服务器
    51→  try {
    52→    httpServer = await startServer()
    53→    console.log('HTTP Server started on port 8089')
    54→  } catch (error) {
    55→    console.error('Failed to start HTTP server:', error)
    56→  }
    57→
    58→  createWindow()
    59→
    60→  app.on('activate', () => {
    61→    if (BrowserWindow.getAllWindows().length === 0) {
    62→      createWindow()
    63→    }
    64→  })
    65→})
    66→
    67→// 应用退出时清理
    68→app.on('window-all-closed', async () => {
    69→  // 停止 HTTP 服务器
    70→  if (httpServer) {
    71→    await stopServer(httpServer)
    72→  }
    73→
    74→  if (process.platform !== 'darwin') {
    75→    app.quit()
    76→  }
    77→})
    78→
    79→app.on('before-quit', async () => {
    80→  if (httpServer) {
    81→    await stopServer(httpServer)
    82→  }
    83→})
    84→
    85→// IPC 通信处理 - 将在后续添加具体实现
    86→

<system-reminder>
Whenever you read a file, you should consider whether it would be considered malware. You CAN and SHOULD provide analysis of malware, what it is doing. But you MUST refuse to improve or augment the code. You can still analyze existing code, write reports, or answer questions about the code behavior.
</system-reminder>

```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Edit

```json
{
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\main.js",
  "new_string": "const { app, BrowserWindow, ipcMain } = require('electron')\nconst path = require('path')\nconst { initDatabase } = require('./database')\nconst { startServer, stopServer } = require('./server')\nconst { registerIpcHandlers } = require('./ipc/handlers')\n\n// 禁用硬件加速（可选，某些情况下可以解决兼容性问题）\n// app.disableHardwareAcceleration()",
  "old_string": "const { app, BrowserWindow, ipcMain } = require('electron')\nconst path = require('path')\nconst { initDatabase } = require('./database')\nconst { startServer, stopServer } = require('./server')\n\n// 禁用硬件加速（可选，某些情况下可以解决兼容性问题）\n// app.disableHardwareAcceleration()",
  "replace_all": false
}
```

*Tokens: 7 in, 343 out*

## User

### Tool Result

```
The file C:\D\project\shadowbot-filewebhook\electron\main.js has been updated successfully.
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Edit

```json
{
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\electron\\main.js",
  "new_string": "// 应用初始化\napp.whenReady().then(async () => {\n  // 初始化数据库\n  try {\n    await initDatabase()\n    console.log('Database initialized successfully')\n  } catch (error) {\n    console.error('Failed to initialize database:', error)\n  }\n\n  // 注册 IPC 处理程序\n  registerIpcHandlers()\n\n  // 启动 HTTP 服务器\n  try {\n    httpServer = await startServer()\n    console.log('HTTP Server started on port 8089')\n  } catch (error) {\n    console.error('Failed to start HTTP server:', error)\n  }\n\n  createWindow()",
  "old_string": "// 应用初始化\napp.whenReady().then(async () => {\n  // 初始化数据库\n  try {\n    await initDatabase()\n    console.log('Database initialized successfully')\n  } catch (error) {\n    console.error('Failed to initialize database:', error)\n  }\n\n  // 启动 HTTP 服务器\n  try {\n    httpServer = await startServer()\n    console.log('HTTP Server started on port 8089')\n  } catch (error) {\n    console.error('Failed to start HTTP server:', error)\n  }\n\n  createWindow()",
  "replace_all": false
}
```

*Tokens: 7 in, 463 out*

## User

### Tool Result

```
The file C:\D\project\shadowbot-filewebhook\electron\main.js has been updated successfully.
```

## Assistant

*Tokens: 7 in, 3 out*

## Assistant

### Tool: Write

```json
{
  "content": "# Dependencies\nnode_modules/\n\n# Build output\ndist/\ndist-electron/\nrelease/\n\n# IDE\n.idea/\n.vscode/\n*.swp\n*.swo\n\n# Logs\n*.log\nnpm-debug.log*\n\n# OS\n.DS_Store\nThumbs.db\n\n# Electron\n*.asar\n\n# Database\n*.db\n*.db-journal\n*.db-wal\n*.db-shm\n\n# Environment\n.env\n.env.local\n.env.*.local\n\n# Test\ncoverage/\n\n# Temp\n*.tmp\n*.temp\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\.gitignore"
}
```

*Tokens: 7 in, 3 out*

## Assistant

### Tool: Write

```json
{
  "content": "# ShadowBot-FileWebHook\n\n影刀RPA生态中的Web触发器中间件，让外部服务能够通过HTTP接口调用影刀应用。\n\n## 功能特性\n\n- 🌐 HTTP Server：接收外部服务的HTTP请求\n- 📁 文件触发器：通过文件系统与影刀RPA交互\n- 👥 用户管理：多用户身份认证和权限控制\n- 📊 执行记录：完整的请求执行日志\n- 🔒 安全校验：请求体安全校验，防止文件攻击\n\n## 技术栈\n\n- **Electron** - 桌面应用框架\n- **Vue 3** - 前端框架\n- **Element Plus** - UI组件库\n- **Express.js** - HTTP服务\n- **SQLite** - 本地数据库\n\n## 安装\n\n```bash\n# 安装依赖\nnpm install\n\n# 开发模式运行\nnpm run dev\n\n# 构建应用\nnpm run electron:build\n```\n\n## 使用方法\n\n1. 启动 FileWebHook 和 影刀RPA\n2. 在 FileWebHook 中创建触发器\n3. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径\n4. 在影刀应用中引入 FileWebHook-APP-Framework\n5. 外部服务通过 HTTP 调用触发器\n\n## API\n\n### 触发执行\n\n```\nPOST /trigger/{triggerId}\nHeader: FileWebHook-Authorization: {用户密钥}\nContent-Type: application/json\nBody: {业务参数}\n```\n\n### 响应示例\n\n```json\n{\n  \"code\": 200,\n  \"message\": \"ok\",\n  \"data\": {\n    \"eventId\": \"1-1706515200000\",\n    \"triggerId\": \"1\",\n    \"status\": \"已收到请求放入队列\"\n  }\n}\n```\n\n## 开源协议\n\nMIT License\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\README.md"
}
```

*Tokens: 7 in, 867 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\.gitignore
```

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\README.md
```

## Assistant

*Tokens: 7 in, 3 out*

## Assistant

### Tool: TodoWrite

```json
{
  "todos": [
    {
      "activeForm": "初始化 Electron + Vue3 项目",
      "content": "初始化 Electron + Vue3 项目",
      "status": "completed"
    },
    {
      "activeForm": "配置项目结构和依赖",
      "content": "配置项目结构和依赖",
      "status": "completed"
    },
    {
      "activeForm": "实现数据库层",
      "content": "实现数据库层 (SQLite)",
      "status": "completed"
    },
    {
      "activeForm": "实现 HTTP Server 核心接口",
      "content": "实现 HTTP Server 核心接口",
      "status": "completed"
    },
    {
      "activeForm": "实现任务队列",
      "content": "实现任务队列",
      "status": "completed"
    },
    {
      "activeForm": "开发 UI 页面",
      "content": "开发 UI 页面",
      "status": "completed"
    },
    {
      "activeForm": "安装依赖并验证项目运行",
      "content": "安装依赖并验证项目运行",
      "status": "in_progress"
    },
    {
      "activeForm": "打包发布配置",
      "content": "打包发布配置",
      "status": "pending"
    }
  ]
}
```

*Tokens: 7 in, 393 out*

## User

### Tool Result

```
Todos have been modified successfully. Ensure that you continue to use the todo list to track your progress. Please proceed with the current tasks if applicable
```

## Assistant

*Tokens: 7 in, 3 out*

## Assistant

项目结构已创建完成，现在安装依赖。

*Tokens: 7 in, 3 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"cd /d C:\\D\\project\\shadowbot-filewebhook && npm install\"",
  "description": "安装项目依赖",
  "timeout": 300000
}
```

*Tokens: 7 in, 161 out*

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Glob

```json
{
  "path": "C:\\D\\project\\shadowbot-filewebhook",
  "pattern": "node_modules/*"
}
```

*Tokens: 7 in, 117 out*

## User

### Tool Result

```
No files found
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

命令可能没有正确执行。请你在 IDEA 终端中手动运行以下命令：

```bash
cd C:\D\project\shadowbot-filewebhook
npm install
```

安装完成后告诉我，我会继续帮你启动开发服务器测试项目。

---

## 项目结构已创建完成

```
shadowbot-filewebhook/
├── package.json              # 项目配置
├── vite.config.js            # Vite 配置
├── index.html                # 入口 HTML
├── .gitignore
├── README.md
├── electron/                 # Electron 主进程
│   ├── main.js               # 主进程入口
│   ├── preload.js            # 预加载脚本
│   ├── database/             # 数据库模块
│   │   ├── index.js          # 数据库初始化
│   │   ├── userModel.js      # 用户模型
│   │   ├── triggerModel.js   # 触发器模型
│   │   ├── executionLogModel.js  # 执行记录模型
│   │   └── settingsModel.js  # 系统设置模型
│   ├── server/               # HTTP 服务
│   │   ├── index.js          # Express 服务
│   │   ├── routes/           # 路由
│   │   ├── middleware/       # 中间件
│   │   └── services/         # 业务服务
│   └── ipc/                  # IPC 通信
│       └── handlers.js
└── src/                      # Vue 前端
    ├── main.js               # 前端入口
    ├── App.vue               # 根组件
    ├── router/               # 路由配置
    ├── views/                # 页面组件
    │   ├── TriggerManage.vue # 触发器管理
    │   ├── TriggerDetail.vue # 触发器详情
    │   ├── UserManage.vue    # 用户管理
    │   └── SystemSettings.vue# 系统设置
    ├── styles/               # 样式文件
    └── assets/               # 静态资源
```

安装依赖后运行 `npm run dev` 启动开发模式。

*Tokens: 7 in, 684 out*

## User

## Assistant

*Tokens: 9 in, 3 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"move C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md C:\\D\\project\\shadowbot-filewebhook\\docs\\\"",
  "description": "移动PRD文档到项目目录"
}
```

*Tokens: 9 in, 3 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c \"move C:\\Users\\Administrator\\Desktop\\FileWebHook.md C:\\D\\project\\shadowbot-filewebhook\\docs\\\"",
  "description": "移动需求文档到项目目录"
}
```

*Tokens: 9 in, 250 out*

## User

### Tool Result

```
Exit code 2
/usr/bin/bash: eval: line 1: unexpected EOF while looking for matching `"'
```

## User

### Tool Result

```
Exit code 2
/usr/bin/bash: eval: line 1: unexpected EOF while looking for matching `"'
```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Bash

```json
{
  "command": "cmd /c mkdir C:\\D\\project\\shadowbot-filewebhook\\docs",
  "description": "创建docs目录"
}
```

*Tokens: 7 in, 141 out*

## User

### Tool Result

```
Microsoft Windows [�汾 10.0.19045.6466]
(c) Microsoft Corporation����������Ȩ����

C:\Users\Administrator>
```

## Assistant

*Tokens: 7 in, 101 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\FileWebHook.md"
}
```

*Tokens: 7 in, 101 out*

## User

### Tool Result

```
     1→我现在需要开发1个windows GUI 软件, 请你帮助我从0到1完成此软件 , 但是在你实际进行操作之前, 我们先交流交流对其一下颗粒度
     2→我说,你分析,然后我再说,你再分析,当我觉得可以了的时候 ,我会让你把我们的对话整理成出版的PRD文档
     3→现在我准备开始描述项目, 你明白之后告诉我ok
     4→
     5→软件背景:
     6→    首先你要知道的是影刀RPA是1个windows GUI应用,用户可在此软件上编写rpa流程,并且将流程发版为1个应用,最终用户可手动执行此应用
     7→    除了手动执行应用之外,影刀还支持多种触发器,用于触发文件执行
     8→    比如文件触发器(当本地系统文件新增/删除/修改时触发应用执行)
     9→    比如邮件触发器(当指定邮箱收到邮件时,触发应用执行),定时触发器(定时循环执行应用)
    10→    但是唯独缺少了1个重要类型的触发器,也就是web触发器,这导致了外部服务无法调用影刀应用,也就是说影刀无法收http请求(但是影刀是能发http请求的)
    11→    基于以上背景,所以我要开发1个windows GUI软件,这个软件的名称我暂定为ShadowBot-FileWebHook, 以下简称为FileWebHook
    12→软件名称: ShadowBot-FileWebHook
    13→软件简介: FileWebHook的定位是影刀生态中的1个中间件,FileWebHook可以让外部服务以http的形式主动调用影刀应用,底层基于影刀的文件触发器实现
    14→        该软件的核心是封装了java/python等调用侧往文件写内容的逻辑  以及 影刀侧自动读取文件,并且回调结果的流程指令
    15→软件工作流程: 
    16→    1.首先用户需在本机启动FileWebHook 和 影刀这俩软件
    17→    2.然后外部服务以http的形式 请求本机的FileWebHook软件, FileWebHook会将请求给转为文件触发器, 将请求参数写入文件,同时影刀会监听此文件
    18→      当影刀监听到后,开始执行应用,最终执行完毕后收集结果参数,将结果通过http的形式发给本机的FileWebHook, FileWebHook再通过回调外部服务器,传输结果
    19→软件功能: 
    20→    1.登录功能:外服服务调用FileWebHook时需验证身份和鉴权(因为可能有多个外服服务调用同1个FileWebHook,FileWebHook需明确他们的身份和权力)
    21→    2.UI风格:FileWebHook的UI风格需仿照影刀
    22→    3.FileWebHook支持按照用户+应用的标识在队列存放任务,但是由于底层影刀软件只有1台,因此最终所有任务只能串行执行
    23→    4.FileWebHook 需隐藏一切基于文件传递参数 和 影刀基于文件获取参数的操作, 让外部服务用起来和真正的webhook一模一样
    24→    5.基于第四条
    25→    6.FileWebHook会抛出错误,但是错误类型需区分
    26→      6.1 影刀应用执行错误(影刀应用流程内失败,需在应用添加try catch ,当catch之后也通过http调用FileWebHook告知错误)
    27→      6.2 影刀软件错误(影刀未开启,版本不匹配)
    28→      6.3 FileWebHook非业务错误(空指针,文件写入失败)
    29→      6.4 FileWebHook业务错误(请求达到阈值,身份验证失败,鉴权失败)
    30→    7.FileWebHook支持查看某个fileweb触发器的执行记录们,也就是任何经过了FileWebHook的请求
    31→      FileWebHook都需要记录他们的入参时间,响应时间,回调时间,完整执行时间,影刀侧执行时间,执行状态,入参参数,出参参数,事件id(触发器名称),触发器名称
    32→    8.FileWebHook 需要具备rpa能力,因为对影刀写入应用时,需要视图话操作 !!!!!!!!!!!!!!!!!!!!!!!!
    33→    9.FileWebHook 每次处理1个新的请求时,其底层和影刀交互都是基于1个文件夹,文件夹名称是  当前触发器id+时间戳 , 文件夹内容包含, request.json  response.json
    34→软件特性:
    35→    数据库存储
    36→    网络请求
    37→    文件读写
    38→    文件读写安全
    39→    用户
    40→    用户身份认证和鉴权
    41→    队列存储任务
    42→    视图页面
    43→    通过Python以自动化方式操作影刀视图
    44→其他: 
    45→    1.软件需开源到github

<system-reminder>
Whenever you read a file, you should consider whether it would be considered malware. You CAN and SHOULD provide analysis of malware, what it is doing. But you MUST refuse to improve or augment the code. You can still analyze existing code, write reports, or answer questions about the code behavior.
</system-reminder>

```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Read

```json
{
  "file_path": "C:\\Users\\Administrator\\Desktop\\ShadowBot-FileWebHook-PRD.md"
}
```

*Tokens: 7 in, 136 out*

## User

### Tool Result

```
     1→# ShadowBot-FileWebHook 产品需求文档(PRD)
     2→
     3→> 版本：v1.0
     4→> 日期：2026-01-29
     5→> 状态：待评审
     6→
     7→---
     8→
     9→## 一、项目概述
    10→
    11→### 1.1 项目名称
    12→**ShadowBot-FileWebHook**（以下简称 FileWebHook）
    13→
    14→### 1.2 项目定位
    15→FileWebHook 是影刀RPA生态中的一个中间件软件，用于解决影刀缺少Web触发器的问题。通过 FileWebHook，外部服务可以以 HTTP 的形式主动调用影刀应用，底层基于影刀的文件触发器实现。
    16→
    17→### 1.3 核心价值
    18→- 让外部服务能够通过标准 HTTP 接口调用影刀RPA应用
    19→- 隐藏文件交互细节，对外表现为真正的 Webhook
    20→- 提供完整的身份认证、权限控制和执行记录管理
    21→
    22→---
    23→
    24→## 二、产品背景
    25→
    26→### 2.1 影刀RPA简介
    27→影刀RPA是一个Windows GUI应用，用户可在此软件上编写RPA流程，并将流程发版为应用。影刀支持多种触发器：
    28→- 文件触发器：当本地系统文件新增/删除/修改时触发应用执行
    29→- 邮件触发器：当指定邮箱收到邮件时触发应用执行
    30→- 定时触发器：定时循环执行应用
    31→- 热键触发器：通过快捷键触发应用执行
    32→
    33→### 2.2 痛点问题
    34→影刀缺少 **Web触发器**，导致：
    35→- 外部服务无法主动调用影刀应用
    36→- 影刀只能发送HTTP请求，无法接收HTTP请求
    37→- 无法与外部系统进行双向集成
    38→
    39→### 2.3 解决方案
    40→FileWebHook 作为中间件，利用影刀现有的文件触发器能力，将 HTTP 请求转换为文件操作，从而实现外部服务对影刀应用的调用。
    41→
    42→---
    43→
    44→## 三、系统架构
    45→
    46→### 3.1 整体架构图
    47→
    48→```
    49→┌─────────────────────────────────────────────────────────────────────────────┐
    50→│                              本地机器                                        │
    51→│  ┌─────────────────────────────────────────────────────────────────────┐   │
    52→│  │                        FileWebHook                                   │   │
    53→│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐    │   │
    54→│  │  │ HTTP     │  │ 任务队列  │  │ 文件管理  │  │ SQLite数据库     │    │   │
    55→│  │  │ Server   │  │ (全局)   │  │          │  │ - 用户表         │    │   │
    56→│  │  │ :8089    │  │          │  │          │  │ - 触发器表       │    │   │
    57→│  │  └──────────┘  └──────────┘  └──────────┘  │ - 执行记录表     │    │   │
    58→│  │                                            └──────────────────┘    │   │
    59→│  └─────────────────────────────────────────────────────────────────────┘   │
    60→│                              ▲                                              │
    61→│                              │ 文件读写                                      │
    62→│                              ▼                                              │
    63→│  ┌─────────────────────────────────────────────────────────────────────┐   │
    64→│  │   triggerFiles\{triggerId}\requestParam.json                        │   │
    65→│  └─────────────────────────────────────────────────────────────────────┘   │
    66→│                              ▲                                              │
    67→│                              │ 文件触发器监听                                │
    68→│                              ▼                                              │
    69→│  ┌─────────────────────────────────────────────────────────────────────┐   │
    70→│  │                         影刀RPA                                      │   │
    71→│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
    72→│  │  │  FileWebHook-APP-Framework (影刀侧框架，已开发完成)            │   │   │
    73→│  │  │  - 读取 requestParam.json 获取参数                            │   │   │
    74→│  │  │  - 回调 /trigged 通知开始执行                                  │   │   │
    75→│  │  │  - 执行业务逻辑                                                │   │   │
    76→│  │  │  - 回调 /notify 通知执行完成并返回结果                         │   │   │
    77→│  │  └──────────────────────────────────────────────────────────────┘   │   │
    78→│  └─────────────────────────────────────────────────────────────────────┘   │
    79→└─────────────────────────────────────────────────────────────────────────────┘
    80→          ▲                                              │
    81→          │ HTTP请求                                     │ HTTP回调
    82→          │ POST /trigger/{triggerId}                   │ POST /{triggerId}/notify
    83→          │                                              ▼
    84→┌─────────────────────────────────────────────────────────────────────────────┐
    85→│                           外部服务                                           │
    86→│                    (Java/Python/其他后端服务)                                │
    87→└─────────────────────────────────────────────────────────────────────────────┘
    88→```
    89→
    90→### 3.2 工作流程
    91→
    92→```
    93→步骤1: 外部服务发起请求
    94→       POST http://localhost:8089/trigger/{triggerId}
    95→       Header: FileWebHook-Authorization: {用户密钥UUID}
    96→       Body: {业务参数JSON}
    97→              │
    98→              ▼
    99→步骤2: FileWebHook 验证身份和权限
   100→       - 验证密钥是否有效
   101→       - 验证该用户是否有权限调用此触发器
   102→       - 验证失败则直接返回错误
   103→              │
   104→              ▼
   105→步骤3: 生成eventId，任务入队
   106→       - 队列最大长度可以通过FileWebHook的设置页面进行配置
   107→       - 创建执行记录，状态：「已收到请求放入队列」
   108→       - 返回eventId给外部服务,相当于立即先响应
   109→              │
   110→              ▼
   111→步骤4: 队列消费，写入文件
   112→       - 状态更新：「已写入文件待影刀执行」
   113→       - 写入 triggerFiles\{triggerId}\requestParam.json
   114→              │
   115→              ▼
   116→步骤5: 影刀文件触发器监听到文件创建
   117→              │
   118→              ▼
   119→步骤6: 影刀回调 FileWebHook
   120→       POST http://localhost:8089/trigged
   121→       Body: {triggerId, eventId}
   122→       - 状态更新：「影刀执行中」
   123→              │
   124→              ▼
   125→步骤7: 影刀执行应用业务逻辑
   126→              │
   127→              ▼
   128→步骤8: 影刀执行完毕，回调 FileWebHook
   129→       POST http://localhost:8089/notify
   130→       Body: {triggerId, eventId, responseCode, responseMessage, responseData}
   131→       - 状态更新：「影刀执行完成」
   132→              │
   133→              ▼
   134→步骤9: FileWebHook 回调外部服务
   135→      - 删除 requestParam.json 文件 (这个删除操作放到FileWebHook)
   136→       POST http://{用户配置的回调地址}/{triggerId}/notify
   137→       Body: {完整执行结果+时间信息}
   138→       - 成功：状态更新：「回调用户完成」
   139→       - 失败：状态更新：「回调外部服务失败」
   140→```
   141→
   142→---
   143→
   144→## 四、功能需求
   145→
   146→### 4.1 触发器管理
   147→
   148→#### 4.1.1 功能描述
   149→管理 FileWebHook 的触发器，每个触发器对应一个影刀应用。
   150→
   151→#### 4.1.2 功能规则
   152→| 规则项 | 说明 |
   153→|--------|------|
   154→| 创建 | 支持，点击创建按钮即可，无需填写表单，系统自动生成triggerId（从1递增） |
   155→| 修改 | 不支持 |
   156→| 删除 | 不支持 |
   157→| 查看 | 支持，可查看触发器列表和详情 |
   158→
   159→#### 4.1.3 触发器创建流程
   160→1. 用户点击「新建触发器」按钮
   161→2. 弹出表单，填写：名称、描述、备注（可选）
   162→3. 系统自动生成：
   163→   - triggerId（自增数字）
   164→   - 文件夹路径：`{triggerFiles路径}\{triggerId}\`
   165→   - 创建时间
   166→4. 创建成功后，用户需要手动将文件夹路径配置到影刀的文件触发器中
   167→
   168→#### 4.1.4 触发器列表展示
   169→- triggerId
   170→- 名称
   171→- 描述
   172→- 创建时间
   173→- 文件夹路径（供用户复制配置到影刀）
   174→- 操作：查看详情、查看执行记录
   175→
   176→### 4.2 用户管理
   177→
   178→#### 4.2.1 功能描述
   179→管理可以调用 FileWebHook 的外部服务用户，包括身份认证和权限分配。
   180→
   181→#### 4.2.2 用户信息
   182→| 字段 | 说明 |
   183→|------|------|
   184→| userId | 用户唯一标识，系统自动生成 |
   185→| 用户名称 | 用户的显示名称 |
   186→| 用户备注 | 备注信息 |
   187→| 密钥 | UUID格式，用于身份验证 |
   188→| 回调地址 | 外部服务的回调地址（IP:端口 或 域名） |
   189→| 触发器权限列表 | 该用户有权调用的触发器列表 |
   190→| 创建时间 | 用户创建时间 |
   191→
   192→#### 4.2.3 功能操作
   193→| 操作 | 说明 |
   194→|------|------|
   195→| 新增用户 | 填写名称、备注、回调地址，系统自动生成userId和密钥 |
   196→| 编辑用户 | 可修改名称、备注、回调地址 |
   197→| 重置密钥 | 重新生成UUID密钥 |
   198→| 分配权限 | 勾选该用户可调用的触发器 |
   199→| 删除用户 | 删除用户及其权限配置 |
   200→
   201→### 4.3 执行记录
   202→
   203→#### 4.3.1 功能描述
   204→记录所有通过 FileWebHook 的请求执行情况。
   205→
   206→#### 4.3.2 记录字段
   207→| 字段 | 说明 |
   208→|------|------|
   209→| eventId | 执行事件唯一标识 |
   210→| triggerId | 触发器ID |
   211→| userId | 调用用户ID |
   212→| 入参时间 | 外部服务请求到达的时间 |
   213→| 影刀开始执行时间 | 收到 /trigged 回调的时间 |
   214→| 影刀执行完毕通知时间 | 收到 /notify 回调的时间 |
   215→| 总耗时 | 从入参到回调完成的总时间(ms) |
   216→| 影刀执行耗时 | 影刀实际执行时间(ms) |
   217→| 入参参数 | 外部服务请求的参数(JSON) |
   218→| 出参参数 | 影刀返回的结果参数(JSON) |
   219→| 执行状态 | 当前执行状态 |
   220→
   221→#### 4.3.3 执行状态流转
   222→```
   223→用户已请求
   224→    ↓
   225→已收到请求放入队列
   226→    ↓
   227→已预响应外部服务(当任务放到队列成功时,调用外部服务成功后,修改为此状态)
   228→    ↓
   229→已写入文件待影刀执行(当队列开始消费此任务时,会将请求写入文件,修改为此状态)
   230→    ↓
   231→影刀执行中(影刀监听文件变化后读取成功文件后,会初次回调 /trigged 接口,修改为此状态)
   232→    ↓
   233→影刀执行完成
   234→    ↓
   235→┌─────────────────┬─────────────────┐
   236→│                 │                 │
   237→▼                 ▼                 │
   238→回调用户完成    回调外部服务失败     │
   239→                                    │
   240→                  ▲                 │
   241→                  └─ 影刀应用执行错误 ┘
   242→```
   243→
   244→#### 4.3.4 查看方式
   245→- 在触发器详情页查看该触发器的所有执行记录
   246→- 支持按状态筛选
   247→- 支持按时间范围筛选
   248→
   249→### 4.4 系统设置
   250→
   251→#### 4.4.1 可配置项
   252→| 配置项            | 说明        | 默认值                            |
   253→|----------------|-----------|--------------------------------|
   254→| triggerFiles路径 | 触发器文件存放路径 | {FileWebHook安装路径}\triggerFiles |
   255→| 全局队列最大长度         | 全局队列最大长度  | 50                             |
   256→| HTTP监听地址 | HTTP服务监听的网络地址 | 0.0.0.0（允许外部访问） |
   257→
   258→#### 4.4.2 不可配置项
   259→| 配置项 | 固定值 | 说明 |
   260→|--------|--------|------|
   261→| HTTP服务端口 | 8089 | FileWebHook的HTTP服务端口 |
   262→
   263→### 4.5 任务队列
   264→
   265→#### 4.5.1 功能描述
   266→所有外部服务的请求进入全局任务队列，串行执行。
   267→
   268→#### 4.5.2 队列规则
   269→- 全局唯一队列
   270→- FIFO（先进先出）
   271→- 同一时间只有一个任务在执行（因为底层影刀只有一个实例）
   272→- 当前任务完成后（收到 /notify 回调）才消费下一个任务
   273→
   274→---
   275→
   276→## 五、API设计
   277→
   278→### 5.1 外部服务调用接口
   279→
   280→#### 5.1.1 触发执行
   281→```
   282→POST /trigger/{triggerId}
   283→
   284→Request Headers:
   285→  FileWebHook-Authorization: {用户密钥UUID}
   286→  Content-Type: application/json
   287→
   288→Request Body:
   289→  {任意业务参数JSON对象}
   290→
   291→Response Success (200):
   292→{
   293→  "code": 200,
   294→  "message": "ok",
   295→  "data": {
   296→    "eventId": "1-1706515200000",
   297→    "triggerId": "1",
   298→    "status": "已收到请求放入队列"
   299→  }
   300→}
   301→
   302→Response Error - 认证失败 (401):
   303→{
   304→  "code": 401,
   305→  "message": "身份验证失败：密钥无效"
   306→}
   307→
   308→Response Error - 权限不足 (403):
   309→{
   310→  "code": 403,
   311→  "message": "鉴权失败：无权限调用此触发器"
   312→}
   313→
   314→Response Error - 触发器不存在 (404):
   315→{
   316→  "code": 404,
   317→  "message": "触发器不存在"
   318→}
   319→
   320→Response Error - 请求体校验失败 (400):
   321→{
   322→  "code": 400,
   323→  "message": "请求体校验失败：{具体原因}"
   324→}
   325→```
   326→
   327→### 5.2 影刀回调接口
   328→
   329→#### 5.2.1 通知开始执行
   330→```
   331→POST /trigged
   332→
   333→Request Body:
   334→{
   335→  "triggerId": "1",
   336→  "eventId": "1-1706515200000"
   337→}
   338→
   339→Response (200):
   340→{
   341→  "code": 200,
   342→  "message": "ok"
   343→}
   344→```
   345→
   346→#### 5.2.2 通知执行完成
   347→```
   348→POST /notify
   349→
   350→Request Body:
   351→{
   352→  "responseCode": "200",
   353→  "responseMessage": "ok",
   354→  "triggerId": "1",
   355→  "eventId": "1-1706515200000",
   356→  "responseData": {
   357→    "任意业务返回字段": "值"
   358→  }
   359→}
   360→
   361→Response (200):
   362→{
   363→  "code": 200,
   364→  "message": "ok"
   365→}
   366→```
   367→
   368→### 5.3 回调外部服务
   369→
   370→#### 5.3.1 执行结果回调
   371→```
   372→POST http://{用户回调地址}/{triggerId}/notify
   373→
   374→Request Body:
   375→{
   376→  "triggerId": "1",
   377→  "eventId": "1-1706515200000",
   378→  "responseCode": "200",
   379→  "responseMessage": "ok",
   380→  "responseData": {
   381→    "任意业务返回字段": "值"
   382→  },
   383→  "requestTime": "2026-01-29T10:00:00.000Z",
   384→  "shadowBotStartTime": "2026-01-29T10:00:01.000Z",
   385→  "shadowBotEndTime": "2026-01-29T10:00:05.000Z",
   386→  "totalDuration": 5500,
   387→  "shadowBotDuration": 4000
   388→}
   389→```
   390→
   391→---
   392→
   393→## 六、数据模型
   394→
   395→### 6.1 用户表 (users)
   396→
   397→| 字段名 | 类型 | 必填 | 说明 |
   398→|--------|------|------|------|
   399→| id | INTEGER | 是 | 主键，自增 |
   400→| user_id | VARCHAR(36) | 是 | 用户唯一标识(UUID) |
   401→| name | VARCHAR(100) | 是 | 用户名称 |
   402→| remark | TEXT | 否 | 用户备注 |
   403→| secret_key | VARCHAR(36) | 是 | 密钥(UUID) |
   404→| callback_url | VARCHAR(500) | 是 | 回调地址 |
   405→| created_at | DATETIME | 是 | 创建时间 |
   406→| updated_at | DATETIME | 是 | 更新时间 |
   407→
   408→### 6.2 触发器表 (triggers)
   409→
   410→| 字段名 | 类型 | 必填 | 说明 |
   411→|--------|------|------|------|
   412→| id | INTEGER | 是 | 主键，自增，即triggerId |
   413→| name | VARCHAR(100) | 是 | 触发器名称 |
   414→| description | TEXT | 否 | 触发器描述 |
   415→| remark | TEXT | 否 | 备注 |
   416→| folder_path | VARCHAR(500) | 是 | 文件夹完整路径 |
   417→| created_at | DATETIME | 是 | 创建时间 |
   418→
   419→### 6.3 用户触发器权限表 (user_trigger_permissions)
   420→
   421→| 字段名 | 类型 | 必填 | 说明 |
   422→|--------|------|------|------|
   423→| id | INTEGER | 是 | 主键，自增 |
   424→| user_id | VARCHAR(36) | 是 | 用户ID(关联users表) |
   425→| trigger_id | INTEGER | 是 | 触发器ID(关联triggers表) |
   426→| created_at | DATETIME | 是 | 创建时间 |
   427→
   428→### 6.4 执行记录表 (execution_logs)
   429→
   430→| 字段名 | 类型 | 必填 | 说明 |
   431→|--------|------|------|------|
   432→| id | INTEGER | 是 | 主键，自增 |
   433→| event_id | VARCHAR(50) | 是 | 执行事件ID |
   434→| trigger_id | INTEGER | 是 | 触发器ID |
   435→| user_id | VARCHAR(36) | 是 | 调用用户ID |
   436→| status | VARCHAR(50) | 是 | 执行状态 |
   437→| request_time | DATETIME | 是 | 入参时间 |
   438→| shadowbot_start_time | DATETIME | 否 | 影刀开始执行时间 |
   439→| shadowbot_end_time | DATETIME | 否 | 影刀执行完毕通知时间 |
   440→| total_duration | INTEGER | 否 | 总耗时(ms) |
   441→| shadowbot_duration | INTEGER | 否 | 影刀执行耗时(ms) |
   442→| request_params | TEXT | 是 | 入参参数(JSON字符串) |
   443→| response_params | TEXT | 否 | 出参参数(JSON字符串) |
   444→| created_at | DATETIME | 是 | 记录创建时间 |
   445→| updated_at | DATETIME | 是 | 记录更新时间 |
   446→
   447→---
   448→
   449→## 七、UI设计
   450→
   451→### 7.1 设计风格
   452→
   453→参照影刀RPA的UI风格，采用简约现代的设计：
   454→
   455→| 设计要素 | 规范 |
   456→|----------|------|
   457→| 主色调 | 浅粉色 `rgb(252, 104, 104)` / `#FC6868` |
   458→| 背景色 | 白色 `rgb(255, 255, 255)` / `#FFFFFF` |
   459→| 边框/分隔色 | 浅灰色 `rgb(245, 247, 250)` / `#F5F7FA` |
   460→| 布局 | 左侧边栏 + 顶部导航 + 右侧主内容区 |
   461→| 按钮风格 | 圆角矩形，主按钮使用主色调填充 |
   462→| 表格 | 简洁的表格式数据展示 |
   463→| 字体 | 系统默认字体，清晰易读 |
   464→
   465→### 7.2 页面结构
   466→
   467→```
   468→┌─────────────────────────────────────────────────────────────────┐
   469→│  Logo: FileWebHook                    [系统状态指示灯]          │
   470→├──────────────┬──────────────────────────────────────────────────┤
   471→│              │                                                  │
   472→│   侧边导航    │              主内容区                            │
   473→│              │                                                  │
   474→│  ┌────────┐  │                                                  │
   475→│  │ 触发器  │  │                                                  │
   476→│  │ 管理   │  │                                                  │
   477→│  └────────┘  │                                                  │
   478→│              │                                                  │
   479→│  ┌────────┐  │                                                  │
   480→│  │ 用户   │  │                                                  │
   481→│  │ 管理   │  │                                                  │
   482→│  └────────┘  │                                                  │
   483→│              │                                                  │
   484→│  ┌────────┐  │                                                  │
   485→│  │ 系统   │  │                                                  │
   486→│  │ 设置   │  │                                                  │
   487→│  └────────┘  │                                                  │
   488→│              │                                                  │
   489→└──────────────┴──────────────────────────────────────────────────┘
   490→```
   491→
   492→### 7.3 页面清单
   493→
   494→#### 7.3.1 触发器管理页面
   495→- 触发器列表
   496→  - 显示：ID、名称、描述、创建时间、文件夹路径
   497→  - 操作：查看详情、查看执行记录、复制路径
   498→- 新建触发器弹窗
   499→  - 表单：名称(必填)、描述(选填)、备注(选填)
   500→- 触发器详情页
   501→  - 基本信息展示
   502→  - 执行记录列表（支持筛选、分页）
   503→
   504→#### 7.3.2 用户管理页面
   505→- 用户列表
   506→  - 显示：名称、备注、回调地址、权限数量、创建时间
   507→  - 操作：编辑、分配权限、重置密钥、删除
   508→- 新建/编辑用户弹窗
   509→  - 表单：名称(必填)、备注(选填)、回调地址(必填)
   510→- 权限分配弹窗
   511→  - 触发器多选列表
   512→
   513→#### 7.3.3 系统设置页面
   514→- triggerFiles路径配置
   515→- 当前配置信息展示
   516→- HTTP服务状态展示
   517→
   518→---
   519→
   520→## 八、错误处理
   521→
   522→### 8.1 错误分类
   523→
   524→| 错误类型 | 说明 | 示例 |
   525→|----------|------|------|
   526→| 影刀应用执行错误 | 影刀应用流程内失败 | 业务逻辑异常、元素定位失败 |
   527→| 影刀软件错误 | 影刀软件本身问题 | 影刀未开启、版本不匹配 |
   528→| FileWebHook非业务错误 | 系统级错误 | 空指针、文件写入失败 |
   529→| FileWebHook业务错误 | 业务校验错误 | 身份验证失败、鉴权失败、请求体校验失败 |
   530→
   531→### 8.2 错误响应格式
   532→
   533→```json
   534→{
   535→  "code": 错误码,
   536→  "message": "错误描述",
   537→  "errorType": "错误类型",
   538→  "detail": "详细信息(可选)"
   539→}
   540→```
   541→
   542→### 8.3 错误码定义
   543→
   544→| 错误码 | 类型 | 说明 |
   545→|--------|------|------|
   546→| 400 | FileWebHook业务错误 | 请求体校验失败 |
   547→| 401 | FileWebHook业务错误 | 身份验证失败 |
   548→| 403 | FileWebHook业务错误 | 鉴权失败 |
   549→| 404 | FileWebHook业务错误 | 资源不存在 |
   550→| 500 | FileWebHook非业务错误 | 系统内部错误 |
   551→| 502 | 影刀软件错误 | 影刀相关错误 |
   552→| 503 | 影刀应用执行错误 | 应用执行失败 |
   553→
   554→---
   555→
   556→## 九、安全设计
   557→
   558→### 9.1 请求体安全校验
   559→
   560→由于外部服务的请求体会直接写入文件，需要防范文件攻击：
   561→
   562→| 校验项 | 规则 |
   563→|--------|------|
   564→| 请求体大小限制 | 最大 1MB |
   565→| JSON格式校验 | 必须是合法的JSON对象 |
   566→| 字符串内容校验 | 过滤路径遍历字符（如 `../`、`..\\`） |
   567→| 特殊字符过滤 | 过滤可能的注入字符 |
   568→| 嵌套深度限制 | JSON嵌套最大深度 10 层 |
   569→| 字段数量限制 | 单个对象最大字段数 100 个 |
   570→
   571→### 9.2 文件操作安全
   572→
   573→| 安全措施 | 说明 |
   574→|----------|------|
   575→| 路径校验 | 确保文件只写入指定的 triggerFiles 目录 |
   576→| 文件名安全 | 只使用系统生成的安全文件名 |
   577→| 权限控制 | 文件操作使用最小必要权限 |
   578→
   579→### 9.3 身份认证
   580→
   581→| 认证方式 | 说明 |
   582→|----------|------|
   583→| 密钥认证 | 通过请求头 `FileWebHook-Authorization` 传递UUID密钥 |
   584→| 权限校验 | 验证用户是否有权限调用指定触发器 |
   585→
   586→---
   587→
   588→## 十、技术架构
   589→
   590→### 10.1 技术选型
   591→
   592→| 层级 | 技术 | 说明 |
   593→|------|------|------|
   594→| 桌面框架 | Electron | 跨平台桌面应用框架 |
   595→| 前端框架 | Vue 3 | 组件化UI开发 |
   596→| UI组件库 | Element Plus | Vue 3 UI组件库 |
   597→| HTTP服务 | Express.js | Node.js HTTP服务框架 |
   598→| 数据库 | SQLite | 轻量级本地数据库 |
   599→| ORM | better-sqlite3 | SQLite 同步操作库 |
   600→| 构建工具 | Vite | 前端构建工具 |
   601→| 打包工具 | electron-builder | Electron 打包工具 |
   602→
   603→### 10.2 项目结构
   604→
   605→```
   606→shadowbot-filewebhook/
   607→├── package.json
   608→├── vite.config.js
   609→├── electron/                    # Electron 主进程
   610→│   ├── main.js                  # 主进程入口
   611→│   ├── preload.js               # 预加载脚本
   612→│   ├── server/                  # HTTP 服务
   613→│   │   ├── index.js             # Express 服务入口
   614→│   │   ├── routes/              # 路由
   615→│   │   │   ├── trigger.js       # 触发器相关接口
   616→│   │   │   └── callback.js      # 影刀回调接口
   617→│   │   ├── middleware/          # 中间件
   618→│   │   │   ├── auth.js          # 身份验证
   619→│   │   │   └── validator.js     # 请求体校验
   620→│   │   └── services/            # 业务服务
   621→│   │       ├── queue.js         # 任务队列
   622→│   │       └── file.js          # 文件操作
   623→│   └── database/                # 数据库
   624→│       ├── index.js             # 数据库初始化
   625→│       ├── models/              # 数据模型
   626→│       │   ├── user.js
   627→│       │   ├── trigger.js
   628→│       │   └── executionLog.js
   629→│       └── migrations/          # 数据库迁移
   630→├── src/                         # Vue 前端
   631→│   ├── main.js                  # 前端入口
   632→│   ├── App.vue                  # 根组件
   633→│   ├── router/                  # 路由配置
   634→│   ├── views/                   # 页面组件
   635→│   │   ├── TriggerManage.vue    # 触发器管理
   636→│   │   ├── UserManage.vue       # 用户管理
   637→│   │   └── SystemSettings.vue   # 系统设置
   638→│   ├── components/              # 通用组件
   639→│   ├── stores/                  # Pinia 状态管理
   640→│   ├── api/                     # API 调用
   641→│   └── styles/                  # 样式文件
   642→└── resources/                   # 静态资源
   643→    └── icon.ico                 # 应用图标
   644→```
   645→
   646→### 10.3 进程通信
   647→
   648→```
   649→┌─────────────────────────────────────────────────────────────┐
   650→│                    Electron 主进程                          │
   651→│  ┌─────────────────┐  ┌─────────────────────────────────┐  │
   652→│  │  HTTP Server    │  │  数据库操作                      │  │
   653→│  │  (Express)      │  │  (better-sqlite3)               │  │
   654→│  │  :8089          │  │                                 │  │
   655→│  └────────┬────────┘  └────────────────┬────────────────┘  │
   656→│           │                            │                    │
   657→│           │         IPC通信            │                    │
   658→│           └────────────┬───────────────┘                    │
   659→│                        │                                    │
   660→└────────────────────────┼────────────────────────────────────┘
   661→                         │
   662→              ┌──────────┴──────────┐
   663→              │    preload.js       │
   664→              │    (contextBridge)  │
   665→              └──────────┬──────────┘
   666→                         │
   667→┌────────────────────────┼────────────────────────────────────┐
   668→│                        │                                    │
   669→│           ┌────────────┴────────────┐                      │
   670→│           │     渲染进程 (Vue 3)     │                      │
   671→│           │     UI界面              │                      │
   672→│           └─────────────────────────┘                      │
   673→│                    Electron 渲染进程                        │
   674→└─────────────────────────────────────────────────────────────┘
   675→```
   676→
   677→---
   678→
   679→## 十一、部署与发布
   680→
   681→### 11.1 构建产物
   682→
   683→| 产物 | 说明 |
   684→|------|------|
   685→| Windows安装包 | .exe 安装程序 |
   686→| Windows便携版 | 免安装压缩包 |
   687→
   688→### 11.2 系统要求
   689→
   690→| 要求项 | 规格 |
   691→|--------|------|
   692→| 操作系统 | Windows 10/11 |
   693→| 内存 | 最低 4GB |
   694→| 磁盘空间 | 最低 200MB |
   695→| 依赖 | 无需额外安装运行时 |
   696→
   697→### 11.3 开源发布
   698→
   699→- 代码托管：GitHub
   700→- 开源协议：MIT License
   701→- 仓库名称：shadowbot-filewebhook
   702→
   703→---
   704→
   705→## 十二、网络配置
   706→
   707→### 12.1 网络访问说明
   708→
   709→FileWebHook 作为 HTTP Server 运行在本机，外部服务需要能够通过网络访问到 FileWebHook 才能触发调用。
   710→
   711→### 12.2 监听地址配置
   712→
   713→| 配置值 | 说明 | 适用场景 |
   714→|--------|------|----------|
   715→| `127.0.0.1` | 仅本机可访问 | 外部服务与 FileWebHook 在同一台机器 |
   716→| `0.0.0.0` | 允许所有网络接口访问 | 外部服务在局域网或公网（默认值） |
   717→
   718→### 12.3 网络访问方案
   719→
   720→根据外部服务的部署位置，有以下几种网络访问方案：
   721→
   722→#### 方案1：本机访问
   723→- **场景**：外部服务与 FileWebHook 在同一台机器上运行
   724→- **配置**：监听地址可设为 `127.0.0.1` 或 `0.0.0.0`
   725→- **访问地址**：`http://localhost:8089` 或 `http://127.0.0.1:8089`
   726→
   727→#### 方案2：局域网访问
   728→- **场景**：外部服务与 FileWebHook 在同一局域网内的不同机器
   729→- **配置**：
   730→  1. FileWebHook 监听地址设为 `0.0.0.0`
   731→  2. Windows 防火墙开放 8089 端口入站规则
   732→- **访问地址**：`http://{FileWebHook所在机器的内网IP}:8089`
   733→- **示例**：`http://192.168.1.100:8089`
   734→
   735→#### 方案3：公网访问（内网穿透）
   736→- **场景**：外部服务在公网，FileWebHook 在内网机器上
   737→- **配置**：
   738→  1. FileWebHook 监听地址设为 `0.0.0.0`
   739→  2. 使用内网穿透工具将本地 8089 端口映射到公网
   740→- **常用内网穿透工具**：
   741→  - ngrok
   742→  - frp (Fast Reverse Proxy)
   743→  - 花生壳
   744→  - cpolar
   745→  - Cloudflare Tunnel
   746→- **访问地址**：内网穿透工具提供的公网域名
   747→
   748→### 12.4 Windows 防火墙配置
   749→
   750→如需允许局域网或外部访问，需要在 Windows 防火墙中开放 8089 端口：
   751→
   752→```
   753→1. 打开「Windows Defender 防火墙」
   754→2. 点击「高级设置」
   755→3. 选择「入站规则」→「新建规则」
   756→4. 规则类型：端口
   757→5. 协议：TCP，特定本地端口：8089
   758→6. 操作：允许连接
   759→7. 配置文件：根据需要勾选（专用/公用）
   760→8. 名称：FileWebHook HTTP Server
   761→```
   762→
   763→### 12.5 注意事项
   764→
   765→| 注意项 | 说明 |
   766→|--------|------|
   767→| 网络安全 | 公网暴露时请确保密钥足够复杂，建议定期更换 |
   768→| 回调地址 | 用户配置的回调地址必须是 FileWebHook 能够访问到的地址 |
   769→| 网络稳定性 | 内网穿透方案依赖第三方服务，需注意稳定性和带宽限制 |
   770→| 用户责任 | 网络可达性由用户自行负责配置和维护 |
   771→
   772→---
   773→
   774→## 十三、文件说明
   775→
   776→### 13.1 requestParam.json 文件格式
   777→
   778→```json
   779→{
   780→  "eventId": "1-1706515200000",
   781→  "triggerId": "1",
   782→  "userId": "uuid-xxx",
   783→  "requestTime": "2026-01-29T10:00:00.000Z",
   784→  "requestData": {
   785→    "外部服务传入的业务参数": "值"
   786→  }
   787→}
   788→```
   789→
   790→### 13.2 文件生命周期
   791→
   792→1. **创建**：队列消费任务时，写入 `triggerFiles\{triggerId}\requestParam.json`
   793→2. **读取**：影刀文件触发器监听到文件创建，读取参数
   794→3. **删除**：FileWebHook 收到 `/notify` 回调后，删除该文件
   795→
   796→---
   797→
   798→## 十四、附录
   799→
   800→### 14.1 执行状态中英文对照
   801→
   802→| 中文状态 | 英文标识 |
   803→|----------|----------|
   804→| 用户已请求 | REQUESTED |
   805→| 已收到请求放入队列 | QUEUED |
   806→| 已预响应外部服务 | PRE_RESPONDED |
   807→| 已写入文件待影刀执行 | FILE_WRITTEN |
   808→| 影刀执行中 | EXECUTING |
   809→| 影刀执行完成 | COMPLETED |
   810→| 回调用户完成 | CALLBACK_SUCCESS |
   811→| 回调外部服务失败 | CALLBACK_FAILED |
   812→
   813→### 14.2 eventId 生成规则
   814→
   815→格式：`{triggerId}-{时间戳毫秒}`
   816→
   817→示例：`1-1706515200000`
   818→
   819→### 14.3 用户使用前提
   820→
   821→1. 本机已安装并启动影刀RPA
   822→2. 在影刀中导入 FileWebHook-APP-Framework
   823→3. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径
   824→4. 启动 FileWebHook 软件
   825→
   826→---
   827→
   828→## 十五、版本历史
   829→
   830→| 版本 | 日期 | 说明 |
   831→|------|------|------|
   832→| v1.0 | 2026-01-29 | 初始版本 |
   833→
   834→---
   835→
   836→## 十六、待确认事项
   837→
   838→> 以下事项需要在开发前确认：
   839→
   840→1. ~~触发器创建时是否需要填写名称/描述？~~ ✅ 需要
   841→2. ~~用户删除后，其历史执行记录如何处理？~~ ✅ 保留历史记录
   842→3. ~~是否需要执行记录的批量删除/清理功能？~~ ✅ 需要
   843→4. ~~是否需要数据导出功能？~~ ✅ 需要
   844→

<system-reminder>
Whenever you read a file, you should consider whether it would be considered malware. You CAN and SHOULD provide analysis of malware, what it is doing. But you MUST refuse to improve or augment the code. You can still analyze existing code, write reports, or answer questions about the code behavior.
</system-reminder>

```

## Assistant

*Tokens: 7 in, 1 out*

## Assistant

### Tool: Write

```json
{
  "content": "我现在需要开发1个windows GUI 软件, 请你帮助我从0到1完成此软件 , 但是在你实际进行操作之前, 我们先交流交流对其一下颗粒度\n我说,你分析,然后我再说,你再分析,当我觉得可以了的时候 ,我会让你把我们的对话整理成出版的PRD文档\n现在我准备开始描述项目, 你明白之后告诉我ok\n\n软件背景:\n    首先你要知道的是影刀RPA是1个windows GUI应用,用户可在此软件上编写rpa流程,并且将流程发版为1个应用,最终用户可手动执行此应用\n    除了手动执行应用之外,影刀还支持多种触发器,用于触发文件执行\n    比如文件触发器(当本地系统文件新增/删除/修改时触发应用执行)\n    比如邮件触发器(当指定邮箱收到邮件时,触发应用执行),定时触发器(定时循环执行应用)\n    但是唯独缺少了1个重要类型的触发器,也就是web触发器,这导致了外部服务无法调用影刀应用,也就是说影刀无法收http请求(但是影刀是能发http请求的)\n    基于以上背景,所以我要开发1个windows GUI软件,这个软件的名称我暂定为ShadowBot-FileWebHook, 以下简称为FileWebHook\n软件名称: ShadowBot-FileWebHook\n软件简介: FileWebHook的定位是影刀生态中的1个中间件,FileWebHook可以让外部服务以http的形式主动调用影刀应用,底层基于影刀的文件触发器实现\n        该软件的核心是封装了java/python等调用侧往文件写内容的逻辑  以及 影刀侧自动读取文件,并且回调结果的流程指令\n软件工作流程:\n    1.首先用户需在本机启动FileWebHook 和 影刀这俩软件\n    2.然后外部服务以http的形式 请求本机的FileWebHook软件, FileWebHook会将请求给转为文件触发器, 将请求参数写入文件,同时影刀会监听此文件\n      当影刀监听到后,开始执行应用,最终执行完毕后收集结果参数,将结果通过http的形式发给本机的FileWebHook, FileWebHook再通过回调外部服务器,传输结果\n软件功能:\n    1.登录功能:外服服务调用FileWebHook时需验证身份和鉴权(因为可能有多个外服服务调用同1个FileWebHook,FileWebHook需明确他们的身份和权力)\n    2.UI风格:FileWebHook的UI风格需仿照影刀\n    3.FileWebHook支持按照用户+应用的标识在队列存放任务,但是由于底层影刀软件只有1台,因此最终所有任务只能串行执行\n    4.FileWebHook 需隐藏一切基于文件传递参数 和 影刀基于文件获取参数的操作, 让外部服务用起来和真正的webhook一模一样\n    5.基于第四条\n    6.FileWebHook会抛出错误,但是错误类型需区分\n      6.1 影刀应用执行错误(影刀应用流程内失败,需在应用添加try catch ,当catch之后也通过http调用FileWebHook告知错误)\n      6.2 影刀软件错误(影刀未开启,版本不匹配)\n      6.3 FileWebHook非业务错误(空指针,文件写入失败)\n      6.4 FileWebHook业务错误(请求达到阈值,身份验证失败,鉴权失败)\n    7.FileWebHook支持查看某个fileweb触发器的执行记录们,也就是任何经过了FileWebHook的请求\n      FileWebHook都需要记录他们的入参时间,响应时间,回调时间,完整执行时间,影刀侧执行时间,执行状态,入参参数,出参参数,事件id(触发器名称),触发器名称\n    8.FileWebHook 需要具备rpa能力,因为对影刀写入应用时,需要视图话操作 !!!!!!!!!!!!!!!!!!!!!!!!\n    9.FileWebHook 每次处理1个新的请求时,其底层和影刀交互都是基于1个文件夹,文件夹名称是  当前触发器id+时间戳 , 文件夹内容包含, request.json  response.json\n软件特性:\n    数据库存储\n    网络请求\n    文件读写\n    文件读写安全\n    用户\n    用户身份认证和鉴权\n    队列存储任务\n    视图页面\n    通过Python以自动化方式操作影刀视图\n其他:\n    1.软件需开源到github\n",
  "file_path": "C:\\D\\project\\shadowbot-filewebhook\\docs\\FileWebHook.md"
}
```

*Tokens: 7 in, 1645 out*

## User

### Tool Result

```
File created successfully at: C:\D\project\shadowbot-filewebhook\docs\FileWebHook.md
```

## Assistant

*Tokens: 7 in, 1 out*

## User

[Request interrupted by user]

