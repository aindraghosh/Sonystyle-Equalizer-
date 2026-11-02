package com.aindra.sonystyleequalizer;

import android.app.Activity;
import android.graphics.Color;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

public class MainActivity extends Activity {
    private Equalizer eq;
    private LinearLayout bands;
    private TextView status;
    private short min, max;
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    private TextView label(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); return t;
    }
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(1);
        page.setPadding(dp(22), dp(34), dp(22), dp(28)); page.setBackgroundColor(Color.rgb(12,16,24));
        scroll.addView(page); setContentView(scroll);
        page.addView(label("WALKMAN  •  SOUND", 12, Color.rgb(105,190,255)));
        TextView title = label("Equalizer", 32, Color.WHITE); title.setPadding(0,dp(8),0,dp(4)); page.addView(title);
        page.addView(label("Shape your sound",14,Color.LTGRAY));
        status = label("Checking audio-effect support…",13,Color.rgb(255,195,90));
        status.setPadding(0,dp(20),0,dp(12)); page.addView(status);
        Switch toggle = new Switch(this); toggle.setText("Enable equalizer"); toggle.setTextColor(Color.WHITE);
        toggle.setOnCheckedChangeListener((v,on) -> {
            if (eq == null) { status.setText("Global audio effect unavailable on this device."); v.setChecked(false); return; }
            try { eq.setEnabled(on); status.setText(on ? "Equalizer enabled (device support permitting)" : "Equalizer bypassed"); }
            catch (Exception e) { status.setText("Device rejected the audio effect."); }
        });
        page.addView(toggle);
        TextView p = label("PRESETS",12,Color.rgb(105,190,255)); p.setPadding(0,dp(26),0,dp(10)); page.addView(p);
        LinearLayout presetRow = new LinearLayout(this);
        for (String name : new String[]{"Flat","Bass","Vocal","Bright"}) {
            Button button = new Button(this); button.setText(name); button.setTextSize(11);
            button.setOnClickListener(v -> preset(name));
            presetRow.addView(button,new LinearLayout.LayoutParams(0,dp(48),1));
        }
        page.addView(presetRow);
        TextView h = label("FREQUENCY BANDS",12,Color.rgb(105,190,255)); h.setPadding(0,dp(28),0,dp(8)); page.addView(h);
        bands = new LinearLayout(this); bands.setOrientation(1); page.addView(bands);
        TextView note = label("Compatibility: Android and phone manufacturers control whether apps can process all device audio. Many phones block or ignore global audio effects. This prototype attempts audio session 0; system-wide EQ is not guaranteed.",12,Color.LTGRAY);
        note.setPadding(0,dp(26),0,0); page.addView(note);
        openEffect();
    }
    private void openEffect() {
        try {
            eq = new Equalizer(0,0); eq.setEnabled(false);
            short[] range = eq.getBandLevelRange(); min=range[0]; max=range[1];
            short count=eq.getNumberOfBands();
            for(short i=0;i<count;i++) {
                final short band=i;
                int hz=eq.getCenterFreq(i)/1000;
                String freq=hz>=1000 ? String.format("%.1f kHz",hz/1000.0) : hz+" Hz";
                LinearLayout row=new LinearLayout(this); row.setOrientation(1);
                row.addView(label(freq,14,Color.WHITE));
                SeekBar seek=new SeekBar(this); seek.setMax(max-min); seek.setProgress(eq.getBandLevel(i)-min);
                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar s,int value,boolean user) {
                        if(user && eq!=null) try { eq.setBandLevel(band,(short)(value+min)); } catch(Exception ignored) {}
                    }
                    public void onStartTrackingTouch(SeekBar s) {}
                    public void onStopTrackingTouch(SeekBar s) {}
                });
                row.addView(seek); row.setPadding(0,dp(7),0,dp(7)); bands.addView(row);
            }
            status.setText("Audio effect detected • "+count+" bands. Global operation depends on device support.");
        } catch(Exception e) {
            eq=null; status.setText("Global equalizer unavailable. This phone may block audio session 0.");
            bands.addView(label("No global audio effect exposed by this device.",14,Color.LTGRAY));
        }
    }
    private void preset(String name) {
        if(eq==null) { status.setText("Cannot apply preset: no global equalizer is exposed."); return; }
        try {
            short count=eq.getNumberOfBands();
            for(short i=0;i<count;i++) {
                int value=0;
                if(name.equals("Bass")) value=i<count/2?650:-100;
                if(name.equals("Vocal")) value=(i==count/2 || i==count/2+1)?450:-100;
                if(name.equals("Bright")) value=i>count/2?500:-100;
                eq.setBandLevel(i,(short)Math.max(min,Math.min(max,value)));
            }
            status.setText(name+" preset applied");
        } catch(Exception e) { status.setText("Device could not apply this preset."); }
    }
    @Override protected void onDestroy() {
        if(eq!=null) try { eq.release(); } catch(Exception ignored) {}
        super.onDestroy();
    }
}
