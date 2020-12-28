package com.wnagj.framework.gson;

import java.util.List;

public class VoiceBean {

    /**
     * bg : 0
     * ls : false
     * sn : 1
     * ws : [{"cw":[{"sc":0,"w":"欢迎"}],"bg":13},{"cw":[{"sc":0,"w":"光临"}],"bg":81}]
     * ed : 0
     */
    private int bg;
    private boolean ls;
    private int sn;
    private List<WsEntity> ws;
    private int ed;

    public void setBg(int bg) {
        this.bg = bg;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public void setWs(List<WsEntity> ws) {
        this.ws = ws;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public int getBg() {
        return bg;
    }

    public boolean isLs() {
        return ls;
    }

    public int getSn() {
        return sn;
    }

    public List<WsEntity> getWs() {
        return ws;
    }

    public int getEd() {
        return ed;
    }

    public class WsEntity {
        /**
         * cw : [{"sc":0,"w":"欢迎"}]
         * bg : 13
         */
        private List<CwEntity> cw;
        private int bg;

        public void setCw(List<CwEntity> cw) {
            this.cw = cw;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public List<CwEntity> getCw() {
            return cw;
        }

        public int getBg() {
            return bg;
        }

        public class CwEntity {
            /**
             * sc : 0.0
             * w : 欢迎
             */
            private double sc;
            private String w;

            public void setSc(double sc) {
                this.sc = sc;
            }

            public void setW(String w) {
                this.w = w;
            }

            public double getSc() {
                return sc;
            }

            public String getW() {
                return w;
            }
        }
    }
}
