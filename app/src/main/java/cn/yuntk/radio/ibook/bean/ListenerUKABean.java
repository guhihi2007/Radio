package cn.yuntk.radio.ibook.bean;

public class ListenerUKABean {


    /**
     * code : 0
     * data : {"cartooncellClickTime":"5000","cartoonviewWillTime":"3000","cellClickTime":"40","listStateCount":"5","listenUKA":"wotingpingshu/1.1.8","listencellClickTime":"50","listenviewWillTime":"300","viewWillTime":"180"}
     * msg : success
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * cartooncellClickTime : 5000
         * cartoonviewWillTime : 3000
         * cellClickTime : 40
         * listStateCount : 5
         * listenUKA : wotingpingshu/1.1.8
         * listencellClickTime : 50
         * listenviewWillTime : 300
         * viewWillTime : 180
         */

        private String cartooncellClickTime;
        private String cartoonviewWillTime;
        private String cellClickTime;
        private String listStateCount;
        private String listenUKA;
        private String listencellClickTime;
        private String listenviewWillTime;
        private String viewWillTime;

        public String getCartooncellClickTime() {
            return cartooncellClickTime;
        }

        public void setCartooncellClickTime(String cartooncellClickTime) {
            this.cartooncellClickTime = cartooncellClickTime;
        }

        public String getCartoonviewWillTime() {
            return cartoonviewWillTime;
        }

        public void setCartoonviewWillTime(String cartoonviewWillTime) {
            this.cartoonviewWillTime = cartoonviewWillTime;
        }

        public String getCellClickTime() {
            return cellClickTime;
        }

        public void setCellClickTime(String cellClickTime) {
            this.cellClickTime = cellClickTime;
        }

        public String getListStateCount() {
            return listStateCount;
        }

        public void setListStateCount(String listStateCount) {
            this.listStateCount = listStateCount;
        }

        public String getListenUKA() {
            return listenUKA;
        }

        public void setListenUKA(String listenUKA) {
            this.listenUKA = listenUKA;
        }

        public String getListencellClickTime() {
            return listencellClickTime;
        }

        public void setListencellClickTime(String listencellClickTime) {
            this.listencellClickTime = listencellClickTime;
        }

        public String getListenviewWillTime() {
            return listenviewWillTime;
        }

        public void setListenviewWillTime(String listenviewWillTime) {
            this.listenviewWillTime = listenviewWillTime;
        }

        public String getViewWillTime() {
            return viewWillTime;
        }

        public void setViewWillTime(String viewWillTime) {
            this.viewWillTime = viewWillTime;
        }
    }
}
