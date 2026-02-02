现在多任务协同还是有点问题  我不知道是为什么  但我想问题出在composeApp/src/commonMain/kotlin/domain/queue/TaskQueue.kt这个类里面

目前如果我连续加任务  那么第一个任务是能走完整个生命周期的  但是第二个任务无论如何也触发不了影刀执行  因为影刀没有监听到新的request.json文件的创建   但是实际上request.json明明已经新建了 并且内容参数也是第二个任务的参数

请你排查一下是为什么 
