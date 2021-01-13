package com.rbyte.dragernesdal.ui.character.magic.necro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.magic.MagicRepository;
import com.rbyte.dragernesdal.data.magic.magicSchool.model.MagicSchoolDTO;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.character.magic.MagicViewModel;
import com.rbyte.dragernesdal.ui.character.magic.divine.DivineFragment;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NecroFragment extends Fragment {
    private MagicViewModel magicViewModel = MagicViewModel.getInstance();
    private MagicRepository magicRepository = MagicRepository.getInstance();
    private CharacterRepository charRepo;
    private ArrayList<SpellDTO> spellList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SpellAdapter spellAdapter = new SpellAdapter();
    private ArrayList<Integer> ownedSpellIDs = magicViewModel.getOwnedSpellIDs();
    private PopupHandler popHandler;
    private Handler uiThread = new Handler();
    private View root2;
    private int screenWidth = 0;

    private MutableLiveData<Integer> currentLvl;
    private boolean currentLvlFound = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_magic_necro, container, false);
        currentLvl = new MutableLiveData<>(0);
        charRepo = CharacterRepository.getInstance();
        popHandler = new PopupHandler(getContext());

        spellList = magicRepository.getSpellsBySchool(4);

        recyclerView = (RecyclerView) root.findViewById(R.id.magicRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.getLayoutParams().height = (int) (getScreenWidth(root.getContext()));
        recyclerView.setAdapter(spellAdapter);

        Button buybtn = root.findViewById(R.id.buymagicbtn);
        TextView rank = root.findViewById(R.id.levelValue);

        currentLvl.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer lvl) {
                rank.setText(lvl + "");
                if (lvl < 5){
                    String btnmsg =  "STIG I RANG (" + magicRepository.getLvlCost(lvl) + " EP)";
                    buybtn.setText(btnmsg);
                } else {
                    String btnmsg = "Højeste rang opnået!";
                    buybtn.setText(btnmsg);
                    buybtn.setClickable(false);
                }
            }
        });

        buybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MagicSchoolDTO school = magicRepository.getSchools().get(3);
                int tmp = 0;
                switch (currentLvl.getValue()){
                    case 0:
                        tmp = school.getLvl1ID();
                        break;
                    case 1:
                        tmp = school.getLvl2ID();
                        break;
                    case 2:
                        tmp = school.getLvl3ID();
                        break;
                    case 3:
                        tmp = school.getLvl4ID();
                        break;
                    case 4:
                        tmp = school.getLvl5ID();
                        break;
                }
                int tierID = tmp;
                int currentEP = charRepo.getCurrentChar().getCurrentep();
                if (currentEP >= magicRepository.getLvlCost(currentLvl.getValue())){
                    popHandler.getConfirmBuyMagicAlert(root,
                            currentLvl.getValue() + 1,
                            school.getSchoolName(),
                            magicRepository.getLvlCost(currentLvl.getValue()),
                            charRepo.getCurrentChar().getCurrentep(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Executor bgThread = Executors.newSingleThreadExecutor();
                                    bgThread.execute(() -> {
                                        Result<MagicTierDTO> res = magicRepository.buyMagicTier(charRepo.getCurrentChar().getIdcharacter(), tierID, magicRepository.getLvlCost(currentLvl.getValue()));
                                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                                        uiThread.post(() -> {
                                            if (res instanceof Result.Success) {
                                                magicViewModel.setUpdate(true);
                                                magicViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                            } else {
                                                Toast.makeText(getContext(), "Der skete en fejl, prøv igen", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    });
                                }
                            }).show();
                } else {
                    Toast.makeText(getContext(), "Du har ikke nok EP", Toast.LENGTH_SHORT).show();
                }
            }
        });


        root2 = root;
        return root;
    }

    private int getScreenWidth(Context context) {

        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }
        return screenWidth;
    }

    private class SpellViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView lvl;
        ImageView checkimg;
        View view;
        public SpellViewHolder(View spellViews) {
            super(spellViews);
            view = spellViews;
            name = spellViews.findViewById(R.id.magicName);
            lvl = spellViews.findViewById(R.id.magicLevelTv);
            checkimg = spellViews.findViewById(R.id.checkImage);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
            lvl.setBackgroundResource(android.R.drawable.list_selector_background);
            checkimg.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    private class SpellAdapter extends RecyclerView.Adapter<SpellViewHolder> {
        @Override
        public int getItemCount() {
            if (spellList != null) return spellList.size();
            return 0;
        }

        @Override
        public SpellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_magic_line, parent, false);
            SpellViewHolder vh = new SpellViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(SpellViewHolder vh, int position) {
            vh.name.setText(spellList.get(position).getSpellname());
            int lvl = magicViewModel.getLvl(spellList.get(position).getId());
            vh.lvl.setText(lvl + "");
            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popHandler.getSpellInfoAlert(root2, spellList.get(position).getSpellname(), spellList.get(position).getItem(), spellList.get(position).getDuration(), spellList.get(position).getDesc()).show();
                }
            });
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));
            boolean bought = false;
            for (int id : ownedSpellIDs){
                if (spellList.get(position).getId() == id){
                    vh.checkimg.setVisibility(View.VISIBLE);
                    bought = true;
                    break;
                }
            }
            if (lvl > currentLvl.getValue()){
                if (bought && !currentLvlFound){
                    currentLvl.setValue(lvl);
                } else {
                    currentLvlFound = true;
                }
            }


        }
    }
}