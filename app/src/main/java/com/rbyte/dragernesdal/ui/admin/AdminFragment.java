package com.rbyte.dragernesdal.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;

public class AdminFragment extends Fragment implements View.OnClickListener{


    //TODO: Se Figma

    private AdminViewModel adminViewModel;
    private Button btn_createSkill, btn_editSkill, btn_createRace, btn_editRace,
            btn_createEvent, btn_editEvent, btn_editUser;
    private View root2;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminViewModel =
                new ViewModelProvider(this).get(AdminViewModel.class);
        View root = inflater.inflate(R.layout.fragment_admin, container, false);
        root2 = root;
        btn_createSkill = root.findViewById(R.id.button_create_skill);
        btn_createSkill.setOnClickListener(this);
        btn_editSkill = root.findViewById(R.id.button_edit_skill);
        btn_editSkill.setOnClickListener(this);
        btn_createRace = root.findViewById(R.id.button_create_race);
        btn_createRace.setOnClickListener(this);
        btn_editRace = root.findViewById(R.id.button_edit_race);
        btn_editRace.setOnClickListener(this);
        btn_createEvent = root.findViewById(R.id.button_create_event);
        btn_createEvent.setOnClickListener(this);
        btn_editEvent = root.findViewById(R.id.button_edit_event);
        btn_editEvent.setOnClickListener(this);
        btn_editUser = root.findViewById(R.id.button_edit_user);
        btn_editUser.setOnClickListener(this);




        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in AdminFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }

    private void clickedOnToast(String msg){
        Toast.makeText(getContext(),"Klikket på: " + msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(root2);
        switch (v.getId()){
            case R.id.button_create_skill:
                clickedOnToast("Opret evne");
                Log.d("AdminView","Clicked on create skill");
                navController.navigate(R.id.nav_admin_skill_create);
                break;
            case R.id.button_edit_skill:
                clickedOnToast("Rediger evne");
                Log.d("AdminView","Clicked on edit skill");
                navController.navigate(R.id.nav_admin_skill_edit);
                break;
            case R.id.button_create_race:
                clickedOnToast("Opret race");
                Log.d("AdminView","Clicked on create race");
                navController.navigate(R.id.nav_admin_race_create);
                break;
            case R.id.button_edit_race:
                clickedOnToast("Rediger race");
                Log.d("AdminView","Clicked on edit race");
                navController.navigate(R.id.nav_admin_race_edit);
                break;
            case R.id.button_create_event:
                clickedOnToast("Opret event");
                Log.d("AdminView","Clicked on create event");
                navController.navigate(R.id.nav_admin_event_create);
                break;
            case R.id.button_edit_event:
                clickedOnToast("Rediger event");
                Log.d("AdminView","Clicked on edit event");
                navController.navigate(R.id.nav_admin_event_edit);
                break;
            case R.id.button_edit_user:
                clickedOnToast("Rediger bruger");
                Log.d("AdminView","Clicked on edit user");
                navController.navigate(R.id.nav_admin_user_edit);
                break;
        }
    }
}