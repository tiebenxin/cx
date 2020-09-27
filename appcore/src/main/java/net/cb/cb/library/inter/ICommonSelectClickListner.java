package net.cb.cb.library.inter;

/**
 * @类名：通用列表选择弹框
 * @Date：2020/9/27
 * @by zjy
 * @备注：默认4个选项，视实际情况自行添加
 */
public interface ICommonSelectClickListner {
    void selectOne();
    void selectTwo();
    void selectThree();
    void selectFour();
    void onCancle();
}
