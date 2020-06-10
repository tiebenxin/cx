package net.cb.cb.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Liszt
 * @date 2020/6/9
 * Description 禁止复制粘贴EditText, 应用场景密码输入
 */
public class BanedPasteEditText extends ClearEditText {
    public BanedPasteEditText(Context context) {
        super(context);
        banLongClick();
    }

    public BanedPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        banLongClick();
    }

    public BanedPasteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        banLongClick();
    }

    private void banLongClick() {
        setLongClickable(false);
        setTextIsSelectable(false);
    }


    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            return false;
        }
        return super.onTextContextMenuItem(id);
    }
}
