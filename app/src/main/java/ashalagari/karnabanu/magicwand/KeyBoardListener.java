package ashalagari.karnabanu.magicwand;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class KeyBoardListener  implements TextWatcher{
    MainActivity mainActivity;
    boolean feildresetflag = false;
    boolean autocorrected=false;
    int beforecount=0;

    KeyBoardListener(Context context){
        mainActivity=(MainActivity)context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        System.out.println("\nText changed before " + s + " ," + start + "," + after + "," + count);
        beforecount=count;
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(feildresetflag){
            feildresetflag=false;
            return;
        }
        byte[] command = new byte[2];
        command[0] = 1;
        if ((beforecount) + 1 == count) {
            char newentry=(s.charAt(start + before));
            if(autocorrected){
                feildresetflag=true;
                autocorrected=false;
                System.out.println("enterd with char "+newentry);
                ((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).setText("");
                ((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).setText(String.valueOf(newentry));
                ((EditText)mainActivity.findViewById(R.id.KeyBoardInput)).setSelection(((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).getText().length());
                autocorrected=false;
                System.out.println("Text changed during " + s + " ," + start + "," + before + "," + count+" autocorrected "+autocorrected);
                return;
            }
            command[1] = (byte)newentry;
        }
        else {
            if (beforecount > count) {
                command[1] = 8;
                for (int i = 0; i < (beforecount - count); i++) {
                    mainActivity.sendbuffer(command);
                }
            }
            else {
                int i = start;
                int repeats = 1;
                while (repeats <= count) {
                    command[1] = (byte) s.charAt(i);
                    mainActivity.sendbuffer(command);
                    i++;
                    repeats++;
                }
                autocorrected=!(autocorrected);
            }
            System.out.println("Text changed during " + s + " ," + start + "," + before + "," + count + " autocorrected" + autocorrected);
            return;
        }
        System.out.println("Text changed during " + s + " ," + start + "," + before + "," + count+" autocorrected "+autocorrected);
        mainActivity.sendbuffer(command);
        if(before<start){
            feildresetflag=true;
            ((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).setText("");
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        System.out.println("Text Changed after " + s);

    }
}
