优化1:
软件图标优化
目前项目启动后的windows软件图标是默认的红白企鹅   但是我希望是我们自己品牌的图标,
图标你直接使用这个位置的图标 docs/images/logo.png

优化2:
打包优化  目前我希望将此项目打包为msi安装包  
但是我执行了 PS C:\project\ShadowBot-FileWebHook> ./gradlew :composeApp:packageMsi
之后,窗口报错了
PS C:\project\ShadowBot-FileWebHook> ./gradlew :composeApp:packageMsi

> Task :downloadWix
Download https://github.com/wixtoolset/wix3/releases/download/wix3112rtm/wix311-binaries.zip

> Task :downloadWix FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':downloadWix'.
> A failure occurred while executing org.jetbrains.compose.internal.de.undercouch.gradle.tasks.download.internal.DefaultWorkerExecutorHelper$DefaultWorkAction
   > org.jetbrains.compose.internal.de.undercouch.gradle.tasks.download.org.apache.hc.client5.http.HttpHostConnectException: Connect to https://github.com:443 [github.com/20.205.243.166] failed: Connection timed out: getsockopt
* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to generate a Build Scan (Powered by Develocity).
> Get more help at https://help.gradle.org.
BUILD FAILED in 21s
12 actionable tasks: 1 executed, 11 up-to-date
> 

所以我觉得是不是 C:\project\ShadowBot-FileWebHook\gradlew文件编写的打包脚本有问题  
值得一提的是我已经手动下载了wix311-binaries  位置是在C:\Program Files\wix311-binaries
并且我还对C:\Program Files\wix311-binaries配置了环境变量
请你修复打包问题  确保次次可以在本地打包成功

还有就是目前项目仅需要支持msi生成  暂时先不支持其他平台
