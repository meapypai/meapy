package project.meapy.meapy;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yassi on 07/03/2018.
 */

public class ErrorView extends android.support.v7.widget.AppCompatTextView {

    public ErrorView(Context context) {
        super(context);
        this.setTextColor(getResources().getColor(R.color.error));
    }
}
