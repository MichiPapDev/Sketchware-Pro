package mod.pap.github;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//activity
import java.io.File;

import a.a.a.lC;
import pro.sketchware.R;
import pro.sketchware.databinding.MyprojectGithubBinding;


public class MyprojectGithub extends AppCompatActivity implements View.OnClickListener {
    public MyprojectGithubBinding binding;

    private GitHub gitHub;
    public String urlGit;
    public String destPath;
    public boolean isPrivate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MyprojectGithubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gitHub = new GitHub(this);

        binding.toolbar.setNavigationOnClickListener(back -> onBackPressed());

        binding.githubPublic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#276F99")));
        binding.githubPublic.setTextColor(Color.parseColor("#FFFFFF"));

        binding.githubPrivate.setTextColor(Color.parseColor("#E69B9B9B"));

        binding.githubToken.setVisibility(View.GONE);

        binding.githubPublic.setOnClickListener(this);
        binding.githubPrivate.setOnClickListener(this);
        binding.buttonId.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.github_private) {
            binding.githubToken.setVisibility(View.VISIBLE);
            binding.githubPrivate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#276F99")));
            binding.githubPrivate.setTextColor(Color.parseColor("#FFFFFF"));

            binding.githubPublic.setTextColor(Color.parseColor("#E69B9B9B"));
            binding.githubPublic.setBackgroundTintList(ColorStateList.valueOf(0));
        }else if (id == R.id.github_public) {
            binding.githubToken.setVisibility(View.GONE);
            binding.githubPublic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#276F99")));
            binding.githubPublic.setTextColor(Color.parseColor("#FFFFFF"));

            binding.githubPrivate.setTextColor(Color.parseColor("#E69B9B9B"));
            binding.githubPrivate.setBackgroundTintList(ColorStateList.valueOf(0));

        } else if (id == R.id.button_id) {
            Intent intent = new Intent();
            intent.setClass(this, MyprojectGithubSetting.class);
            startActivity(intent);

        }


    }





}