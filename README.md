# updater
app updater manager

just for me .

该lib需要依赖
```
compile 'cn.ymex:rx-retrofit:1.0.1'
compile 'cn.ymex:popup-dialog:1.2.3'

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

