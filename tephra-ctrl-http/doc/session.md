# 跨容器Session管理
在负载均衡、分布式集群环境下，通常通过Session复制的方式来同步用户个性化信息。这一过程通常需要修改容器配置，甚至增加一定的工作量。

Ctrl－Http提供了一个更为简洁的方式，只需将Session ID保存在HTTP(S)请求的头信息中，即可达到多节点间自动同步Session数据。

在每次发起HTTP(S)请求时，在Header中添加tephra-session-id，并且值为当前用户的Session ID值即可。