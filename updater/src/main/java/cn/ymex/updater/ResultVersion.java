package cn.ymex.updater;

/**
 * Created by ymexc on 2017/11/27.
 * About:TODO
 */

public class ResultVersion {

    /**
     * code : 20000
     * data : {"id":"4","pid":null,"app_name":"乐金所Def","version_name":"v4.2","version_code":"42","update_url":"https://ojlyqybn1.qnssl.com/lejinsuo_latest.apk","update_content":"这个版本，我们做了一些小的调整： \n1.影片列表，点击海报快速观看预告片 \n2.针对预售的影片，显示\u201c想看人数\u201d \n3.增加影院的在线选座停售时间说明，减少购前焦虑 \n4.下单页面，显示放映结束时间，提前知道几点散场","force":"1","channel":"default"}
     * message : success
     * path : /api/version/16
     */

    private int code;
    private Version data;
    private String message;
    private String path;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Version getData() {
        return data;
    }

    public void setData(Version data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static class Version {
        /**
         * id : 4
         * pid : null
         * app_name : 乐金所Def
         * version_name : v4.2
         * version_code : 42
         * update_url : https://ojlyqybn1.qnssl.com/lejinsuo_latest.apk
         * update_content : 这个版本，我们做了一些小的调整：
         1.影片列表，点击海报快速观看预告片
         2.针对预售的影片，显示“想看人数”
         3.增加影院的在线选座停售时间说明，减少购前焦虑
         4.下单页面，显示放映结束时间，提前知道几点散场
         * force : 1
         * channel : default
         */

        private String id;
        private String pid;
        private String app_name;
        private String version_name;
        private String version_code;
        private String update_url;
        private String update_content;
        private String force;
        private String channel;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public String getVersion_code() {
            return version_code;
        }

        public void setVersion_code(String version_code) {
            this.version_code = version_code;
        }

        public String getUpdate_url() {
            return update_url;
        }

        public void setUpdate_url(String update_url) {
            this.update_url = update_url;
        }

        public String getUpdate_content() {
            return update_content;
        }

        public void setUpdate_content(String update_content) {
            this.update_content = update_content;
        }

        public String getForce() {
            return force;
        }

        public void setForce(String force) {
            this.force = force;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }
}
