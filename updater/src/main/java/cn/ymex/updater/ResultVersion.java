package cn.ymex.updater;

/**
 * Created by ymexc on 2017/11/27.
 * About:TODO
 */

public class ResultVersion {
    private String code;
    private String message;
    private Version data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Version getData() {
        return data;
    }

    public void setData(Version data) {
        this.data = data;
    }

    public static class Version {
        private int id;
        private int pid;
        private int version_code;
        private String version_name;
        private String app_name;
        private String update_url;
        private String update_content;
        private int force;
        private String channel;

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
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

        public int getForce() {
            return force;
        }

        public void setForce(int force) {
            this.force = force;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }

    @Override
    public String toString() {
        return "ResultVersion{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
