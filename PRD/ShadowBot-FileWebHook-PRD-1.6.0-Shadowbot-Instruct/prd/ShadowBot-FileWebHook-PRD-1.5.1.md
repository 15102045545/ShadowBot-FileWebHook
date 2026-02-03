
现在给你1个单独的任务 就是写1个单独的python脚本 去修改这个文件 C:\Users\Administrator\PycharmProjects\xiaokee-python\InstructData\BaseFileWebHookAppFramework
这个文件目前存储的是影刀的流程指令,
我要求你这个python脚本支持修改指定类型 指定名称的那行指令  比如把设置变量指令的triggerId的值改成1 和
把设置变量指令的triggerFilesPath改成2222 , 然后你的python脚本 和 最终修改完的指令
都存入到C:\Users\Administrator\PycharmProjects\xiaokee-python\InstructData

---

现在尝试使用C:\Users\Administrator\PycharmProjects\xiaokee-python\InstructData\ShadowbotAppFormwork_InstructDataPaste.py脚本
去读取刚刚修改后的指令文件  写入到系统剪切板

---

不  但实际上BaseFileWebHookAppFramework_modified文件的内容并没有被成功写入值  请你分析原因并解决

---

现在把你写的修改指令脚本 和 这个脚本进行对比 
C:\project\ShadowBot-FileWebHook\composeApp\src\commonMain\kotlin\shadowbot\modify_instructions.py
说说这俩脚本在核心修改上 以及打包啊 格式啊 是不是都是一样的?

---

修复

---

现在继续优化modify_shadowbot_instructions.py脚本内容  
注意此次优化不要对原有核心逻辑进行修改  而是优化注释 和 默认行为  , 
移除掉所有默认行为 必须显式指定输入指令文件 输出位置  和 属性变量名称和值 , 
此外给此脚本加上正确的使用说明和注释  我目前看到有一些说明是不需要的 比如查看list啊什么什么的

---

把我刚刚给你的所有输入  就是我对你说的话  都追加写入到C:\project\ShadowBot-FileWebHook\PRD\ShadowBot-FileWebHook-PRD-V1.5.1.md
