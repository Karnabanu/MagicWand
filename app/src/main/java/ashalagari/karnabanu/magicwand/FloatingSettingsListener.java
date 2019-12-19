package ashalagari.karnabanu.magicwand;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

/**
 * Created by kalpana on 6/11/2016.
 */
public class FloatingSettingsListener implements View.OnClickListener {
    Context context;
    int ABORT=0;
    MainActivity mainActivity;
    FloatingSettingsListener(Context pcontext){
        context=pcontext;
        mainActivity=(MainActivity)pcontext;
    }
    @Override
    public void onClick(View v) {
        PopupMenu popup=new PopupMenu(context,mainActivity.findViewById(R.id.floatingSettings));
        popup.getMenuInflater().inflate(R.menu.popup_menu,popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_exit_popup) {
                    mainActivity.disconnected(ABORT);
                    mainActivity.finish();
                    return true;
                }
                if (id == R.id.action_disconnect) {
                    System.out.println("Floating disconnected clicked");
                    mainActivity.disconnected(ABORT);
                    return true;
                }


                return true;
            }
        });

        popup.show();
        //Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
    }
}
