现在帮我在C:\project\ShadowBot-FileWebHook\shadowbot文件夹内新增2个windows可运行脚本
这俩脚本很简单
第一个脚本名字叫ShadowbotAppFormwork_InstructDataCopy.bat  作用是将当前系统剪贴板内的内容保存到C:\project\ShadowBot-FileWebHook\shadowbot\InstructData文件内
第二个脚本名字叫ShadowbotAppFormwork_InstructDataCopyPaste.bat 作用是读取C:\project\ShadowBot-FileWebHook\shadowbot\InstructData文件 然后将内容写入当前系统剪贴板内
我这么做是为了能让影刀的流程指令跨平台 跨主机进行复制粘贴  为了让你更好的理解这个需求  我把之前这个需求的对话背景发给你  如下：

Q:
你知道影刀这个平台把  它的应用打开编辑之后  可以看到视图化的 应用流程指令 , 然后我可以ctrl+c复制这些指令,  然后将它黏贴到另外1个应用的编辑页面内,   那么现在问题来了, 我怎么保留住我复制的指令流程,比如将他保存为特殊文件? 然后其他人下载后可以在另一个电脑上粘贴进去

A:
这是一个非常实际的需求。影刀复制出来的可视化指令，默认存放在系统的剪贴板中，一旦重启电脑或复制了其他内容就会丢失。
要将这些流程“保存”为文件并在其他电脑上粘贴，核心的难点在于：可视化的流程图不是纯文本，不能直接存为 .txt 文本文档。
要永久保存并跨电脑迁移，以下是目前最可行的几种方案，按推荐程度排序：
...
方案四：序列化保存（开发者向）
如果你有一定的编程基础，可以通过写一段简单的代码来获取剪贴板的数据流并保存。
原理： 当你复制那些流程指令时，它们是以特定的格式（可能是一种二进制流或 JSON 结构）存在剪贴板里的。
方法： 使用 Python 的 win32clipboard 库，读取当前剪贴板的数据，并将其写入到一个文件（例如 flow_data.dat）中。
还原： 在另一台电脑上，写一个小脚本读取这个 .dat 文件，并将内容重新写入到系统剪贴板中，然后去影刀里粘贴。

