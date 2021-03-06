package com.rbyte.dragernesdal.ui.rules;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;

public class RulesFragment extends Fragment{

    private RulesViewModel rulesViewModel;

    private final String url = "files.runerne.dk/rules.pdf";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rulesViewModel =
                new ViewModelProvider(this).get(RulesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rules, container, false);
        final WebView webView = root.findViewById(R.id.rule_view);
        final ProgressBar progressBar = root.findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                super.onPageFinished(webView, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
        //TODO: Open browser and redirect to pdf. Switch to Home fragment.

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in RulesFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }
}