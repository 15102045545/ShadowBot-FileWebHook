触发器管理页面 点击复制按钮后,依然无法对triggerId  triggerFilesPath serverIpAndPort这三个 填充指令写入值

现在开启你的深度思考模式  想想是为什么不能替换成功   我建议你继续参考参考这些python脚本是怎么实现修改指令的
composeApp/src/commonMain/kotlin/shadowbot/modify_instructions.py
composeApp/src/commonMain/kotlin/shadowbot/parse_shadowbot_blocks.py
composeApp/src/commonMain/kotlin/shadowbot/read_instruct_data.py

而且最重要的是 不要再使用${}的方式填充了  现在元指令那边根本没有${}了, 只有1个空值,所以你依然是要根据指令的名称来填充 
找到名称是triggerId  triggerFilesPath serverIpAndPort这三个的设置变量指令  然后给它们写入值即可
