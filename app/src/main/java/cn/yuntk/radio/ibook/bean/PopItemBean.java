package cn.yuntk.radio.ibook.bean;

/*自定义弹出取值*/
public class PopItemBean {

    private String showString;
    private PopTpye type;
    private int minValue;
    private int maxValue;
    private String chooseState = "0";//0为选中 1选中

    //睡眠，章节，播放速度
    public enum PopTpye{
        SLEEP,PAGEITEM,SPEED
    }

    public String getShowString() {
        return showString;
    }

    public PopItemBean setShowString(String showString) {
        this.showString = showString;
        return this;
    }

    public PopTpye getType() {
        return type;
    }

    public PopItemBean setType(PopTpye type) {
        this.type = type;
        return this;
    }

    public int getMinValue() {
        return minValue;
    }

    public PopItemBean setMinValue(int minValue) {
        this.minValue = minValue;
        return this;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public PopItemBean setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public String getChooseState() {
        return chooseState;
    }

    public void setChooseState(String chooseState) {
        this.chooseState = chooseState;
    }
}
