package mod.pap.github;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import pro.sketchware.R;
import pro.sketchware.databinding.MyprojectGithubSettingBinding;

public class MyprojectGithubSetting extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private MyprojectGithubSettingBinding binding;

    private final FragmentGithubSettingRepo navRepo = new FragmentGithubSettingRepo();

    private Fragment activeFragment = navRepo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MyprojectGithubSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentManager FManager = getSupportFragmentManager();
        FManager.beginTransaction()
                .add(R.id.github_fragment_nav, navRepo, "REPO")
                .commit();
        activeFragment = navRepo;

        binding.githubNavSetting.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_github_repo) {
                showFragment(navRepo);
                return true;

            }
            return false;

        });



        GitHub gitHub = new GitHub(this);

        gitHub.authenticate(new GitHub.DeviceFlowCallBack() {
            @Override
            public void onDeviceCodeReceived(String userCode, String verificationUri) {
                runOnUiThread(() -> {
                    Toast.makeText(MyprojectGithubSetting.this,
                            "🔑 Código: " + userCode + "\nAbre: " + verificationUri,
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onSuccess(String token) {
                runOnUiThread(() -> {
                    Toast.makeText(MyprojectGithubSetting.this, "✅ Token: " + token.substring(0, 15) + "...", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MyprojectGithubSetting.this, "❌ " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });




    }

    private void showFragment(Fragment fragment) {

        if (fragment == activeFragment) {
            return;

        }
        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();

        activeFragment = fragment;
    }
}