[ ![Download](https://api.bintray.com/packages/ymex/maven/updater/images/download.svg) ](https://bintray.com/ymex/maven/updater/_latestVersion)

# updater
用于app内的下载升级与安装 。兼容android 7.0+

## Giadle引入
```
compile 'cn.ymex.app:updater:0.0.3'

```

需要拥有的必须权限 

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

1、 初始化updater. 在`application onCreate`中初始化
```
//默认配置
Updater.get().config(this);
//自定义配置，
//Updater.get().config(this,MyFileProvider.class);
```

2、下载

```
Updater.get().downloadApp(downListener, url);
```

3、安装

```
Updater.get().installApp(path);
```
