package cn.yuntk.radio.bean


class FMActivityBean{
    var name : String=""
    var radioFm : String=""
    var currentFM : Float=0f//在圆中的位置
    var loading:Boolean=false//false:正在加载
    var loadFailed:Boolean=false//false:有网络
    var province:String=""//当前省份
}