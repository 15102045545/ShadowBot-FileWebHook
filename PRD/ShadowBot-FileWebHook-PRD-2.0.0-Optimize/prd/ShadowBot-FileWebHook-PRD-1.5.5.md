需求1:
当我在开发者功能页面 点击转储指令按钮时 转储成功了 但是弹窗提示的信息是乱码
请修复这个问题
相关代码
composeApp/src/commonMain/kotlin/ui/screens/developer/DeveloperScreen.kt

需求:
当用户在开发者功能页面 点击转储指令按钮时 不要立即执行转储逻辑
而是先弹1个确认对话框 让用户确认是否真的要转储指令
如果用户点击确认按钮 则执行转储逻辑
如果用户点击取消按钮 则关闭对话框 不执行任何操作
相关代码
composeApp/src/commonMain/kotlin/ui/screens/developer/DeveloperScreen.kt

确认对话框的内容是
"
您正在执行1个属于开发者的操作,该操作将读取您的剪切板并覆盖现有的元FileWebHook-APP-Framework指令文件, 您确认要继续吗?
可能带来的问题: 
1.如果你的剪切板内容不是有效的FileWebHook-APP-Framework指令文件内容 或 与当前FileWebHook版本不兼容 那么会导致触发器管理页面的复制指令功能无法使用
"
