# AOPObservable
Java推荐使用getter/setter，而Android并不推荐。然而，为了继承Observable，Android不得不使用setter，同时，setter本身也是繁琐的Boilerplate，并无实效。
AOPObservable使用AOP方式（aspectj）切入所有Observable的field赋值调用，并在advice中通知Observer数据又变更。使得Bean可以保持更Android的方式，重构起来也更快速。
依赖了大神的[Hugo插件](https://github.com/JakeWharton/hugo)，引用方法完全拷贝大神的gradle。

## 使用方法
### 引入
- 下载并引入aspect module
- 在需要AOP的module（仅测试了application project）下，compile project(":aspect")
- 对需要AOP的module apply plugin: 'hugo'

### 使用
- 让任意POJO继承Observable
- 在不需要监听的Field上标记为@IgnoreField
- 在需要KVO的地方addObserver

### 其他

- 使用@IgnoreInvoker标记Class或方法（需要作为离方法声明最近的annotation）可以阻止AOP切入，此时需要在修改Observable时，手动调用Dispatchers#notifyDataChanged，直接调用Observable#notifyDataChanged不能生效，因为Observable#notifyDataChanged会检测changed标记。
- Observer#update的第二个参数，会被设置为修改Observable的类，所谓invoker
- 运行默认带有日志，去掉日志需要修改Dispatchers#DO_LOG


