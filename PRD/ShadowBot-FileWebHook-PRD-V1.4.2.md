 触发器管理页面 点击复制按钮后,依然无法填充指令的${triggerId} 和   ${triggerFilesPath}  ${serverIpAndPort}
 现在让我们换一种方式  来进行参数替换  你可以直接找到3个"设置变量"的指令  然后看看它们的key(注意不是value)是不是 triggerId,triggerFilesPath,serverIpAndPort
 这三个,  如果是的话  直接设置值即可,  而我会将元指令的值改为空  
