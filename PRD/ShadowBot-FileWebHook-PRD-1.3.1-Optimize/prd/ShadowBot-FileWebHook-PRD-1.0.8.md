软件配置功能优化

第一点:
我发现1个bug  就是我们的配置的 fileWebHookName 和 fileWebHookSecretKey这俩默认配置的值并没有生效,每次启动应用都还是空的

然后我继续追查发现是composeApp/src/commonMain/kotlin/data/repository/SettingsRepository.kt这个文件的46行是查询数据库的setting表,获取配置
但是它获取的配置必定为空  因为我们目前项目并没有往settings表里面写数据
我们是通过composeApp/src/commonMain/kotlin/data/model/Settings.kt这个类定义了配置的默认值

所以我们是不是应该加上一段逻辑  当项目启动时,如果settings表里面没有数据 就把Settings.kt里面的默认值写入到settings表里面

然后后续用户修改配置时 都是基于settings表进行交互



