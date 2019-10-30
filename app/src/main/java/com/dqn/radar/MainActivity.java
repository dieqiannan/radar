package com.dqn.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyRadarParentView vRadar = findViewById(R.id.v_radar);
        ArrayList<Double> rankingNumList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rankingNumList.add(30 * i * 1.0);
        }
        vRadar.setNumData(rankingNumList);
        ArrayList<String> rankingStrNameList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rankingStrNameList.add("排名位置 " + i);
        }
        vRadar.setStrData(rankingStrNameList);
        vRadar.setListener(new MyRadarParentView.OnListener() {
            @Override
            public void onClick(int position) {
                //跳转排名页面
            }
        });
    }
}
