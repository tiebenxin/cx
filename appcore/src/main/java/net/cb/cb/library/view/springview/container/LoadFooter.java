package net.cb.cb.library.view.springview.container;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.cb.cb.library.R;

/**
 * @author Liszt
 * @date 2020/9/24
 * Description
 */
public class LoadFooter extends BaseFooter {
    private TextView tvTitle;
    private ProgressBar progressBar;

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.default_footer, viewGroup, true);
        tvTitle = view.findViewById(R.id.default_footer_title);
        progressBar = view.findViewById(R.id.default_footer_progressbar);
        tvTitle.setVisibility(View.GONE);
//        progressBar.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {

    }

    @Override
    public void onDropAnim(View rootView, int dy) {

    }

    @Override
    public void onLimitDes(View rootView, boolean upOrDown) {

    }

    @Override
    public void onStartAnim() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishAnim() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
