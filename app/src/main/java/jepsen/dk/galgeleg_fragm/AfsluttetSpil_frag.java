package jepsen.dk.galgeleg_fragm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by Ebbe on 16-11-2015.
 */
public class AfsluttetSpil_frag extends Fragment implements View.OnClickListener{

    private ViewGroup rod;
    private Button again, highscore;
    private TextView status, ordet, tabt;

    public View onCreateView(LayoutInflater i, ViewGroup container,Bundle savedInstanceState) {

        rod = (ViewGroup) i.inflate(R.layout.afsluttetspil_frag, container, false);


            again = (Button) rod.findViewById(R.id.againButton);
            again.setOnClickListener(this);
            again.setAnimation(Velkomst_frag.animation);
            status = (TextView) rod.findViewById(R.id.ord);
            ordet = (TextView) rod.findViewById(R.id.ord2);
            tabt = (TextView) rod.findViewById(R.id.ordetVar);
            highscore = (Button) rod.findViewById(R.id.highscoreButton);
            highscore.setOnClickListener(this);

            if (Velkomst_frag.gl.erSpilletTabt()) {
                tabt.setText("Ordet var:");
                status.setText("Du har tabt!");
                highscore.setText("Se highscore");
            } else if (Velkomst_frag.gl.erSpilletVundet()) {
                tabt.setText("Du gættede:");
                status.setText("Du har vundet!");
                highscore.setText("Gem highscore");
            }
            ordet.setText(Velkomst_frag.gl.getOrdet());

        return rod;
    }

    @Override
    public void onClick(View v) {
        if (v==again){
            //Velkomst_frag.gl.saetsvaerhedsgrad(0);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, new Velkomst_frag())
                    .addToBackStack(null)
                    .commit();

    } else if (v==highscore){
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentindhold, new highscore_cont())
                    .addToBackStack(null)
                    .commit();
        }
}
}
