# demo-request-scope

《基于ThreadLocal实现一个上下文管理组件》技术博客对应源码

https://juejin.cn/post/7153287656624324638

本文基于`ThreadLocal`原理，实现了一个上下文状态管理组件`Scope`，通过开启一个自定义的`Scope`，在`Scope`范围内，可以通过`Scope`各个方法读写数据；

通过自定义线程池实现上下文状态数据的线程间传递；

提出了一种基于`Filter`和`Scope`的`Request`粒度的上下文管理方案。
