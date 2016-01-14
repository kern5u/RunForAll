package android.fr.runforall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;

public class DeleteEntryFragment extends Fragment implements OnItemSelectedListener {

    DataBaseHandler bdd;
    List<String> date;
    Spinner spinner;
    Button suppr_one;
    Button suppr_all;
    String date_coisie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_delete_entry, container, false);

        bdd = new DataBaseHandler(getActivity());
        date = bdd.getAllDate();

        spinner = (Spinner)V.findViewById(R.id.spinner);
        suppr_one = (Button)V.findViewById(R.id.suppr_one);
        suppr_all = (Button)V.findViewById(R.id.suppr_all);
        spinner.setOnItemSelectedListener(this);
        loadSpinnerData();

        suppr_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!date.isEmpty()) {
                    bdd.deleteRun(date_coisie);
                    loadSpinnerData();
                }
            }
        });

        suppr_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //MISE EN PLACE DE LA BOITE DE DIALOGUE LORS DE LA SUPPRESSION DE LA BASE
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // Titre de la boite de dialogue
                alertDialogBuilder.setTitle("Confirmation");

                // Settings de la boite de dialogue
                alertDialogBuilder
                        .setMessage("Etes-vous sûr de vouloir effacer toutes vos données ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Réponse OUI
                                //==> Suppression des entrées
                                bdd.deleteAllTable();
                                loadSpinnerData();
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Réponse NON
                                //==> Fermeture de la boite de dialogue
                                dialog.cancel();
                            }
                        });

                // Création de la boite
                AlertDialog alertDialog = alertDialogBuilder.create();

                // Affichage de la boite
                alertDialog.show();
            }
        });
        return V;
    }

    private void loadSpinnerData() {

        DataBaseHandler bdd = new DataBaseHandler(getActivity());
        List<String> date = bdd.getAllDate();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, date);

        // Drop down layout style
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Prise en compte de l'item sélectionné
        date_coisie = parent.getItemAtPosition(position).toString();
        Log.d("Debug", "Label choisi : "+date_coisie);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}