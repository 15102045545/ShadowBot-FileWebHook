 我点了现在触发器管理的复制指令按钮 后  会做3件事情
1.底层会读取元指令 并根据当前triggerId ...等参数对元指令动态修改 
 2. 将修改后的指令文件写入到文件   
3. 读取修改文件到用户剪切板
 
但是目前呢这个功能是用不了的  就是说最终生成的新指令文件  就算读取到了剪切板 也无法粘贴到影刀中
那我认为是这个流程中的第一步和第二步还是有缺陷

因此现在请你优化这部分的修改指令 和 写入文件的逻辑
你必须严格参照 composeApp/src/commonMain/kotlin/shadowbot/modify_shadowbot_instructions.py 这个脚本的实现

换句话说 就是把目前项目中用的composeApp/src/commonMain/kotlin/shadowbot/modify_and_save_instructions.py脚本的用法 全部替换为
 composeApp/src/commonMain/kotlin/shadowbot/modify_shadowbot_instructions.py 这个脚本

还有就是要注意元指令文件转储时 要有后缀了 .pkl
响应的 修改后的指令文件也要有此后缀 这些地方哪些要改则是你自己去理解整个功能

