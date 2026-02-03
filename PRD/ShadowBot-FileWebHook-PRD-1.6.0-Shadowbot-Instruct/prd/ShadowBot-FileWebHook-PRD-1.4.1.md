 现在还有个小问题  那就是触发器管理页面 点击复制按钮后, 虽然可以将元指令信息写入粘贴板  但是${triggerId} 和
  ${triggerFilesPath}  ${serverIpAndPort}这些并没有被自动填充  如果你觉得根据${}来匹配值不好做的话
  你也可以直接找到这三个"属性填充"指令的key值 它们是triggerId  serverIpAndPort  trigger设置变量来匹配
