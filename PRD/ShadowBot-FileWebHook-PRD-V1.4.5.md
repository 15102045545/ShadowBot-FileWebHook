触发器管理页面 点击复制按钮后,依然无法对triggerId  triggerFilesPath serverIpAndPort这三个 填充指令写入值
我觉得问题出在这个地方: 你是直接读取元指令 然后填充 然后直接写入剪切板,  这中间是不是少了一步: 当填充完毕之后 是不是要先写成file  再去读这个file是不是好一点?
如果你觉得这样会好一点的话  那么我们就把这个填充后的file写到C:\Users\Administrator\.filewebhook\triggerFiles\1  
C:\Users\Administrator\.filewebhook\triggerFiles\2   
C:\Users\Administrator\.filewebhook\triggerFiles\3
这种目录下吧  
