# Android应用架构概述

本项目用于探究与实现当前业内常见的Android客户端架构。

## 架构的三个要点

* 要解决的问题：代码组织、复用、维护性……
* 角色的划分： Model、View、ViewModel、Controller……
* 角色之间的通信： 数据流向（从网络中获取数据绑定到视图）、事件流向（视图上的点击
触摸事件触发数据处理）

## 常见的架构模式

* [x] [MVC架构](./app/src/main/java/xyz/dean/architecture/mvc/MVC架构.md)
* [x] [MVP架构](./app/src/main/java/xyz/dean/architecture/mvp/MVP架构.md)
* [x] [MVVM架构](./app/src/main/java/xyz/dean/architecture/mvvm/MVVM架构.md)
* [ ] MVI（吐了，这东西模板代码也太多了，核心思想是基于事件/数据流构建响应式APP)
* [ ] Redux
* [ ] AAC(Android Architecture Components)
* [ ] 分层模式
* [ ] 客户端/服务器模式（C/S）
* [ ] 主从模式
* [ ] 管道过滤模式
* [ ] 事件总线模式

## 参考

* [GUI 应用程序架构的十年变迁：MVC、MVP、MVVM、Unidirectional、Clean](https://zhuanlan.zhihu.com/p/26799645?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io)
* [浅谈 MVC、MVP 和 MVVM 架构模式](https://draveness.me/mvx/)
* [从状态管理(State Manage)到MVI（Model-View-Intent）](https://juejin.cn/post/6844903552431685645)
* [[译]使用MVI打造响应式APP(一):Model到底是什么](https://juejin.cn/post/6844903789015597070)