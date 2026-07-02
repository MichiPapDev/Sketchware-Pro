package mod.pap.github;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pro.sketchware.databinding.FragmentGithubSettingRepoBinding;

public class FragmentGithubSettingRepo extends Fragment {

    private FragmentGithubSettingRepoBinding binding;

    public FragmentGithubSettingRepo() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGithubSettingRepoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}