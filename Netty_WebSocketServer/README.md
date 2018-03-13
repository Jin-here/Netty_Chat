# 023_Netty_WebSocketServer01
Netty5.0.0.Alpha2：
WebSocket简单示例
对应客户端项目地址为：https://github.com/7gaw/024_Netty_WebSocketClient01.git


消息分类：
    1、功能消息：功能码 + 功能描述
        例如：上线功能码 + 用户唯一标识     （客户端-》服务器）
        	  下线功能码 + 用户唯一标识     （客户端-》服务器）
        	  成功功能码 + 成功描述         （服务器-》客户端）
        	  失败功能码 + 失败描述         （服务器-》客户端）
        
    2、聊天消息：from + to + talk
        例如：caojinchenkaihello：即caojin(from) + chenkai(to) + hello(talk)
                caojin向chenkai发送内容为hello的消息
                
                
注意点：
    1、客户端用户除非特殊不需要主动发送下线消息到服务器，因为一旦客户端断开连接，服务器自动下线客户端