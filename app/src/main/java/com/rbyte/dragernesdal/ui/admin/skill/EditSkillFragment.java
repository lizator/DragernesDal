package com.rbyte.dragernesdal.ui.admin.skill;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.character.skill.alle.AlleFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EditSkillFragment extends Fragment {
    private RecyclerView recyclerView;
    private SkillViewModel skillViewModel = SkillViewModel.getInstance();
    private ArrayList<AbilityDTO> abilityList = new ArrayList<>();
    private AbilityRepository abilityrepo;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_skill_admin, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        abilityrepo = AbilityRepository.getInstance();
        recyclerView = (RecyclerView) root.findViewById(R.id.adminRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(abilityAdapter);
        if(abilityList.size()==0)skillViewModel.getAll();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress", "Back pressed in edit skill");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };


        skillViewModel.getAllAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                abilityList.clear();
                abilityList.addAll(abilityDTOS);
                Collections.sort(abilityList, new Comparator<AbilityDTO>() {
                    public int compare(AbilityDTO left, AbilityDTO right)  {
                        return left.getName().compareTo(right.getName()); // The order depends on the direction of sorting.
                    }
                });
                abilityAdapter.notifyDataSetChanged();
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }


    private class AbilityViewHolder extends RecyclerView.ViewHolder {
        TextView name, cost;
        ImageView checkimg;
        View view;

        public AbilityViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.lineName);
            cost = abilityViews.findViewById(R.id.abilityCostTv);
            checkimg = abilityViews.findViewById(R.id.checkImage);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    private class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            if (abilityList != null) return abilityList.size();
            return 0;
        }

        @Override
        public AbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_skill_line_admin, parent, false);
            AbilityViewHolder vh = new AbilityViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(AbilityViewHolder vh, int position) {
            vh.name.setText(abilityList.get(position).getName());
            vh.cost.setText(abilityList.get(position).getCost()+"");
            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked on:" + abilityList.get(position).getName());
                }
            });
//            if (position % 2 == 1)
//                vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));
        }
    }
}
