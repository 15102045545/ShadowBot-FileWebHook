现在再新增1个大的菜单:"用户权限管理"

1.列表渲染: 该页面的列表底层查询的数据源是 user_trigger_permissions  也就是用户和触发器直接的关联数据  也就是用户的触发器权限列表

2.页面支持用户id输入框精准搜索某1个用户下的所有触发器权限列表

3.列表每一行元素渲染的字段包含user_trigger_permissions表全部字段

4.该页面不支持删除 修改 和 查询详情, 先不用写这些功能

5.列表每一行元素都有1个复制按钮  该按钮的全称是"拷贝客户端请求示例"
当用户点了该按钮之后  我们需要在底层构建1个请求示例  写入到系统粘贴板

这个请求示例呢 目前我们先暂定为字符串 , 并且呢目前只支持Java客户端的请求示例: 也就是下面这种:
/**
     * 执行应用
     */
    @PostMapping("triggerExecute")
    public String triggerExecute() throws Exception {
        JSONObject request = new JSONObject();
        request.set("triggerId", "2");
        request.set("userId", "121400af-aeb5-4070-bdb8-8717a1abf3d2");
        request.set("secretKey", "957bc127-003a-4a33-b0c7-a13a3101d4bc");

        JSONObject requestParam = new JSONObject();
        requestParam.set("key","value");
        request.set("requestParam", requestParam);

        String response = HttpUtils.doPost("http://127.0.0.1:58700/trigger/execute", request.toString());
        log.info("triggerExecute response: {}", response);
        return response;
    }

    /**
     * 接收应用回调
     * @param object 回调信息
     * @return 回调信息
     */
    @PostMapping("/xiaokeer/filewebhook/notify")
    public Object notify(@RequestBody Object object) {
        log.info("notify object: {}", object);
        return object;
    }
但是呢上面这2个接口 也就是请求示例是我写死的数据  , 而你要做的是不能写死, 而是要动态查询出来  然后字符串填充,最终组成1个相较于"该用户,该触发器,当前filewebhook端口号"的Java客户端请求示例字符串"
然后我总结了一下一共是5行代码要求是动态的
 request.set("triggerId", "动态的triggerId");
request.set("userId", "动态的userId");
request.set("secretKey", "动态的用户secretKey");
String response = HttpUtils.doPost("http://127.0.0.1:动态的端口,ip先写死127.0.0.1 /trigger/execute", request.toString());
@PostMapping("/动态的filewebhookName /filewebhook/notify")
