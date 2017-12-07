[ ![Download](https://api.bintray.com/packages/ymex/maven/updater/images/download.svg) ](https://bintray.com/ymex/maven/updater/_latestVersion)

# updater
app updater manager


***Now this library is for personal use only, please do not use it.***

## Giadle引入
```
compile 'cn.ymex.app:updater:0.0.2'

//需要依赖以下库
compile 'cn.ymex:rx-retrofit:1.0.1'
compile 'cn.ymex:popup-dialog:1.2.3'

//包含依赖的库
//compile 'com.liulishuo.filedownloader:library:1.6.8'
```


1、 init in Application
```
public class AppContent extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Updater.init(this);
    }
}
```

2、check version for update

```
String url = "http://xxxxxx/api/version/16?channel=default";
Updater.getInstance(AppLaunchActivity.this).setVersionCode(0).checkVersion(url);
```

