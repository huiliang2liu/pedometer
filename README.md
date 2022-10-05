###  使用说明
#### 使用
##### 配置资源来源
```
maven { url = uri("https://gitee.com/liu-huiliang/jarlibs/raw/master") }
```
##### 配置库引用
```
implementation 'com.lhl.pedometer:pedometer:1.0.0'
```

##### 在application中调用
```
new Pedometer.Builder()
                .setContext(this)
                .setListener(this)
                .build()new Pedometer.Builder()
                .setContext(this)
                .setListener(PedometerListener)
                .build()
```

#### api说明
##### PedometerListener
| 方法名 | 方法说明                   | 参数         |
| --- |------------------------|------------|
| onStepChange | 步数发生改吧                 | step当前步数   |
| onDayChange | 日期发生改变                 | step上一天的步数 |

##### Pedometer
| 方法名 | 方法说明                | 参数           |
| --- |---------------------|--------------|
| getSteps | 获取当前步数              |  |
| destroy | 销毁，保存没有保存的步数        |  |


##### Pedometer.Builder
| 方法名 | 方法说明                               | 参数           |
| --- |------------------------------------|--------------|
| setListener | 设置步数改变回调                           |  |
| setContext | 设置上下文                              |  |
| setDebug | 设置调试模式                             |  |
| setSharedPreferences | 设置SharedPreferences                |   |