刚刚我提了一个需求：
    用户表新增1个字段 回调请求头, 这个字段在底层存储以json字符串存储， 但是在前端展示和编辑时 以key-value的形式展示和编辑  这个字段的应用则是在调用外部系统，也就是调用客户端的时候  会把这个请求头传递给外部系统

   但是似乎你并没有完全完成它  目前user表还没有callbackHeaders这个字段， 但是部分业务方法已经有这个入参变量了callbackHeaders
   而且最主要的是composeApp/src/commonMain/kotlin/ui/screens/user/UserListScreen.kt 这个文件有几处报错
   e: file:///C:/project/ShadowBot-FileWebHook/composeApp/src/commonMain/kotlin/ui/screens/user/UserListScreen.kt:179:69 No value passed for parameter 'callbackHeaders'.
e: file:///C:/project/ShadowBot-FileWebHook/composeApp/src/commonMain/kotlin/ui/screens/user/UserListScreen.kt:197:82 No value passed for parameter 'callbackHeaders'.

所以请你把这个需求补充完整， 把user表新增callbackHeaders字段， 并且把相关的业务逻辑补充完整， 让上面提到的报错消失
