# Changelog

v3.2.0

* 启用 Kotlin 协程
* 修改了部分文本
* 修复了从后台返回后，程序依然会被杀死的bug
* 修复了数据会被强制覆盖的bug
* 修复了可能会导致文本显示错误的bug
* 常规优化

v3.1.4

* 现在程序在后台保留30秒后会自动杀死
* 移除了多余的abi, 仅保留了64位abi (arm64-v8a)
* 修改了部分过时的方法
* 修复了部分数据可能会错误初始化的bug
* 修复了刷新按钮在特定环境下无效的bug
* 常规优化

v3.1.3

* 成绩查询中加入平均绩点和加权平均分
* 降低了获取数据的延时
* 重构了文本常量的文件结构
* 修复了部分主题显示错误的bug
* 修复了部分线程进入死循环的bug
* 修复了修改设置后数据不会保存的bug
* 修改了RecycleView的模板
* 增加了一个适用于RecycleView的新的Item
* 更新依赖
* 常规优化

v3.1.2

* 修改了部分Preference的样式
* 修复了RecycleView页面可能闪退的bug
* 更新依赖

v3.1.1

* 修复了重复进入成绩查询时数据未清空而导致出现多个重复成绩的bug

v3.1.0

* 成绩查询
* 修复了部分会导致程序崩溃的bug
* 常规优化

v3.0.1

* 撤销关于 HTTP 302 的特定处理方法 ( 修复了程序崩溃 )

v3.0.0

* 全新 UI
* 全新 icon
* `清除缓存`
* 修改`AlertDialog`
* Gson -> Moshi
* 重写 DataModel
* 重写重置数据的 method
* 现在会检查基本信息是否填写
* 加入 Material Design 3 主题
* 加入了一个 RecycleView 的模板
* 加入更多主题
* 加入工具对应icon
* 修复了登录时可能的崩溃
* 更新依赖
* 常规优化

v2.8.6

* 修复了Channel错误的问题

~~v2.8.5 (useless)~~

* Java 8 -> 11
* 修复了取随机数相关的bug
* 常规优化

v2.8.4

* 现在可以重置应用数据
* 常规优化

v2.8.3-Pre ( *Test* )

* 测试`OrderListData.kt`是否工作正常

v2.8.2

* 加入随机主题
* 常规优化

v2.8.1

* 更新依赖 (OkHttp3)
* 修复了小工具可能会登录失败的bug
* 修正了部分数据模型
* 移除了部分未使用的变量
* 优化UI
* 常规优化

v2.8.0

* 加入首次使用程序时的引导
* 修改了部分过时的方法
* 优化UI
* 常规优化

v2.7.5

* targetApi升级至33 （Tiramisu）
* 修改了部分UI
* 常规优化

v2.7.4-Pre ( *Test* )

* targetApi升级至33 （Tiramisu）
* 修改了部分UI

v2.7.3

* 升级Kotlin (1.6.21 -> 1.7.0)
* 移除了过时的logger
* 添加了`Pre-release`的显示
* 修改部分元数据

v2.7.3-Pre ( *Test* )

* 测试新版本Kotlin
* 移除了过时的logger
* 添加了`Pre-release`的显示
* 修改部分元数据

v2.7.2

* 主页面和关于页面现在会显示使用的是`Release`版本还是`Debug`版本
* 修改了主页面的跳转方法
* 修改部分meta-data的获取方法
* 修复了暂离状态的重置按钮不可见的bug
* 修复了修改语言不生效的bug
* 简化部分代码
* 常规优化

v2.7.1

* 更新依赖

v2.7.0

* 识别到暂离状态的预约时，可以一键重置了
* 修改了部分meta-data
* 统一了Toast的显示时长
* 常规优化

v2.6.4

* 移除混淆

~~v2.6.3 (useless)~~

* 重新设计了混淆格式，解决了程序无法正确运行的问题
* 再次修复了可能会导致登录失败的问题
* 更新依赖（`material`）

~~v2.6.2 (useless)~~

* 提高了获取的order数量，防止无法正确读取有效order
* 获取剩余流量和电量时，现在会检测是否成功登录
* 给一键重置加上了确认dialog，防止误触
* 修复了统一认证平台要求修改密码时，可能会导致的登录失败问题
* 优化安装包大小

v2.6.1

* 修复了一键重置无法使用的问题
* 增加容错性（sso提示修改密码时可能会导致出现问题）

v2.6.0

* 识别到当天的未开始预约时，可以在到期前进行一键重置
* 重建了工具箱页面，为未来可能会存在的更多的小工具做准备
* 修复了随机预约会错误显示的bug
* 重构部分架构，优化性能

v2.5.2

* 存在第二天的有效预约，但当天无有效预约时，随机预约现在也会启用
* 重构架构，优化性能

v2.5.1

* 现在`关于`页面的Toolbar会保持黑色
* 修复了`浴室预约`可能会出现的线程问题

v2.5.0

* 在没有有效预约时，可以一键随机预约座位了（比如说突然想去了，坐哪无所谓）
* 补充使用的开源项目
* 优化架构，优化性能

v2.4.1

* 更新依赖（`AGP`)

v2.4.0

* 加入`志愿时长`查询
* 长按`退出`现在会杀死进程
* 简化代码，优化性能

v2.3.7:

* ~~修复了`关于`页面StatusBar颜色错误的问题~~

v2.3.6

* 修复了`关于`页面Toolbar颜色错误的问题

v2.3.5

* 因为tx摆烂，移除Bugly的升级功能
* 修复了`关于`页面语言未生效的bug

v2.3.4

* 加入`关于`页面
* 修复了theme模式错误可能引起崩溃的bug
* 修复了第一次启动程序时可能导致的默认主题错误
* 优化架构
* 修改了部分文字

v2.3.3

* 引入Bugly，增加检查更新功能
* 优化架构

v2.3.2:

* 加入快捷方式(Shortcuts)
* 完善各项设置图标
* 优化架构，优化性能

v2.3.1:

* 修改返回上级Activity的方法

v2.3.0:
> P.S. 因重构整体框架，故版本号直接跳至2.3.0

* 加入语言设置
* 重构整体框架，体验增强
* 修复`研修间`无法返回的bug
* 修复`设置`点击左上返回按钮会直接退出的bug
* 修改了`浴室预约`的对话框弹出逻辑
* 修改了部分RGB值
* 修改默认主题

v2.2.0:

* 加入主题切换
* 加入深色模式切换
* 移除部分未使用的资源
* 优化整体架构

v2.1.1:

* 检测到未开始的预约时现在可以一键`开摆`:joy:
* 重新修复了虚拟卡余额不会跟随刷新按钮刷新的错误

v2.1.0:

* 现在`澡堂预约`会校验WIFI的SSID，如果不匹配校园网会提示连接校园网
* 添加了部分新权限来获得WIFI的SSID
* 简化代码，优化性能

v2.0.8:

* `剩余流量`现在也会显示已经使用的流量
* 修复了无法选中当天的有效预约的错误
* 修复了虚拟卡余额不会跟随刷新按钮刷新的错误
* 统一了部分字符常量的格式
* 简化代码，优化性能
* 优化了部分UI

v2.0.7:

* 加入部分提示
* 修复了一个大小写的错误

v2.0.6:

* 修复暂离截止会一直显示的bug

v2.0.5:

* 主页面右下加入版本信息
* `剩余流量`现在会提示网费余额
* `剩余流量`和`剩余电量`现在点击其他地方也可以退出了

v2.0.4:

* 提高`okhttp`的超时时间，防止获取宿舍电量时因超时导致崩溃

v2.0.3:

* `虚拟卡`刷新现在会同时刷新余额

v2.0.2:

* 修复了`剩余流量`显示为已用流量的错误

v2.0.1:

* 修改了`虚拟卡`二维码显示的方法，更便于扫描

v2.0.0:

* 加入工具箱
  * `虚拟卡`
  * `澡堂预约`
  * `剩余流量`
  * `宿舍剩余电量`
* Add English Version

v1.2.0:

* 加入`研修间`
* 修改UI
* 现在会选中更靠近当前时间的预约

v1.1.2:

* 更新依赖
* 简化代码

v1.1.1:

* 现在暂离状态会显示暂离截止时间
* 修改UI

v1.1.0:

* 修改了相关依赖
* 简化代码
* 加入`签到码`
* 加入`刷新`功能
* 支持暂离和未开始（审核通过）状态
* 当前状态会显示在下方
* 修改UI

v1.0.2:

* 修改了当前选中预约的内容，现在会显示`order_id`
* 修改了默认的预约内容值
* 修改UI

v1.0.1:

* 移除签到功能（评估后似乎没用）
* 完善暂离功能

v1.0.0:

* Hello World
