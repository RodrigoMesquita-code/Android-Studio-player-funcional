package com.example.player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton previousButton;

    private SeekBar seekbar;

    private TextView musicTitle;
    private TextView currentTime;
    private TextView totalTime;

    private ListView musicList;

    private MediaPlayer mp;

    private Handler handler;

    private boolean isPlaying = false;

    private int currentMusicIndex = 0;

    // NOMES DAS MUSICAS
    private final String[] musicNames = {
            "Enter Sandman",
            "Hino Botafogo",
            "No More Trouble",
            "Paranoid",
            "Pirata do Tesouro",
            "TNT - AC/DC",
            "DRDRE"
    };

    // ARQUIVOS DA PASTA RAW
    private final int[] musicFiles = {
            R.raw.entersandman,
            R.raw.hino_botafogo,
            R.raw.nomoretrouble,
            R.raw.paranoid,
            R.raw.pirata_tesouro,
            R.raw.tnt_acdc,
            R.raw.drdre
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);

        seekbar = findViewById(R.id.seekbar);

        musicTitle = findViewById(R.id.musicTitle);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);

        musicList = findViewById(R.id.musicList);

        handler = new Handler(Looper.getMainLooper());

        // Lista de músicas
        ArrayList<String> lista = new ArrayList<>();

        for (String nome : musicNames) {
            lista.add(nome);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.music_item,
                R.id.songName,
                musicNames
        ) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {

                android.view.View view = super.getView(position, convertView, parent);

                TextView text = view.findViewById(R.id.songName);

                text.setTextColor(android.graphics.Color.WHITE);

                return view;
            }
        };

        musicList.setAdapter(adapter);

        // Carrega primeira música
        carregarMusica(currentMusicIndex);

        // Clique na lista
        musicList.setOnItemClickListener((parent, view, position, id) -> {

            currentMusicIndex = position;

            carregarMusica(currentMusicIndex);

            mp.start();

            playPauseButton.setImageResource(R.drawable.ic_pause);

            isPlaying = true;

            atualizarPlayer();
        });

        // Play/Pause
        playPauseButton.setOnClickListener(view -> {

            if (mp == null) return;

            if (isPlaying) {

                mp.pause();

                playPauseButton.setImageResource(R.drawable.ic_play);

                isPlaying = false;

            } else {

                mp.start();

                playPauseButton.setImageResource(R.drawable.ic_pause);

                isPlaying = true;

                atualizarPlayer();
            }
        });

        // Próxima
        nextButton.setOnClickListener(view -> {

            currentMusicIndex++;

            if (currentMusicIndex >= musicFiles.length) {
                currentMusicIndex = 0;
            }

            carregarMusica(currentMusicIndex);

            mp.start();

            playPauseButton.setImageResource(R.drawable.ic_pause);

            isPlaying = true;

            atualizarPlayer();
        });

        // Anterior
        previousButton.setOnClickListener(view -> {

            currentMusicIndex--;

            if (currentMusicIndex < 0) {
                currentMusicIndex = musicFiles.length - 1;
            }

            carregarMusica(currentMusicIndex);

            mp.start();

            playPauseButton.setImageResource(R.drawable.ic_pause);

            isPlaying = true;

            atualizarPlayer();
        });

        // Seekbar
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser && mp != null) {
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void carregarMusica(int index) {

        if (mp != null) {
            mp.release();
        }

        mp = MediaPlayer.create(this, musicFiles[index]);

        musicTitle.setText(musicNames[index]);

        seekbar.setMax(mp.getDuration());

        totalTime.setText(formatTime(mp.getDuration()));

        currentTime.setText("0:00");

        seekbar.setProgress(0);

        mp.setOnCompletionListener(mediaPlayer -> {

            currentMusicIndex++;

            if (currentMusicIndex >= musicFiles.length) {
                currentMusicIndex = 0;
            }

            carregarMusica(currentMusicIndex);

            mp.start();

            atualizarPlayer();
        });
    }

    private void atualizarPlayer() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mp != null && mp.isPlaying()) {

                    int posicao = mp.getCurrentPosition();

                    seekbar.setProgress(posicao);

                    currentTime.setText(formatTime(posicao));

                    handler.postDelayed(this, 500);
                }
            }
        }, 0);
    }

    private String formatTime(int milliseconds) {

        int minutes = milliseconds / 1000 / 60;

        int seconds = (milliseconds / 1000) % 60;

        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mp != null) {
            mp.release();
            mp = null;
        }
    }
}