package android.fr.runforall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AlertDFragment extends DialogFragment{

    public static boolean ok_pressed = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ok_pressed = true;
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                .setIcon(R.mipmap.ic_launcher)
                        // Set Dialog Title
                .setTitle("Run Terminé")
                        // Set Dialog Message

                .setMessage("Vous avez parcouru " + MapsActivity.distance + " km en " + MapsActivity.calculTempsString(MapsActivity.temps))

                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
