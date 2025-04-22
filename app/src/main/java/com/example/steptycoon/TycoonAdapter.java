package com.example.steptycoon;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TycoonAdapter extends BaseAdapter {
    ArrayList<TycoonObject> tycoons;
    Context context;
    long totalMoney;
    public TycoonAdapter(Context context, ArrayList<TycoonObject> tycoons) {
        this.context = context;
        this.tycoons = tycoons;
        this.totalMoney =0;
    }

    @Override
    public int getCount() {
        return tycoons.size();
    }

    @Override
    public Object getItem(int position) {
        return tycoons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.tycoon_layout,viewGroup,false);
        //Set Texts
        TextView tycoonTitle=view.findViewById(R.id.tycoonTitle);
        tycoonTitle.setText(tycoons.get(i).objectName);
        TextView tycoonCps=view.findViewById(R.id.tycoonCps);
        tycoonCps.setText("CPS:"+tycoons.get(i).getCurrentIncome());
        TextView tycoonCount=view.findViewById(R.id.tycoonCount);
        tycoonCount.setText("Count:"+tycoons.get(i).count);
        TextView costLabel=view.findViewById(R.id.costLabel);
        costLabel.setText("Price:"+tycoons.get(i).price);
        //Set Buy Buttons
        Button buyButton=view.findViewById(R.id.buyButton);
        buyButton.setText("Buy");
        buyButton.setOnClickListener(v->{
            if(totalMoney>=tycoons.get(i).price) {
                tycoons.get(i).buyOne();
                checkUnlockCondition(i);
            }
            else{
                Toast.makeText(context,"Oops. Not enough funds.",Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        });
        Button buyMaxButton=view.findViewById(R.id.buyMaxButton);
        buyMaxButton.setText("Buy Max");
        buyMaxButton.setOnClickListener(v->{
            while(totalMoney>=tycoons.get(i).price) {
                tycoons.get(i).buyOne();
                checkUnlockCondition(i);
            }
            notifyDataSetChanged();
        });
        if(!tycoons.get(i).unlocked){//Dont show if not unlocked
            buyMaxButton.setClickable(false);
            buyButton.setClickable(false);
            buyMaxButton.setBackgroundColor(Color.GRAY);
            buyButton.setBackgroundColor(Color.GRAY);

        }
        else{
            buyMaxButton.setBackgroundColor(Color.rgb(94, 120, 196));
            buyButton.setBackgroundColor(Color.rgb(94, 120, 196));
            buyMaxButton.setClickable(true);
            buyButton.setClickable(true);
        }

        notifyDataSetChanged();

        return view;
    }
    private void checkUnlockCondition(int i){
        if(i<tycoons.size()-1) {
            if (tycoons.get(i).count >= 10) {
                tycoons.get(i+1).unlocked = true;
            }
        }
        notifyDataSetChanged();
    }
    public long updateTotalCps(){
        long totalCps=0;
        for(TycoonObject tycoon:tycoons){
            totalCps+=tycoon.getCurrentIncome();
        }
        totalMoney+=totalCps;
        notifyDataSetChanged();
        return totalCps;
    }
    public long getTotalCps(){
        long totalCps=0;
        for(TycoonObject tycoon:tycoons){
            totalCps+=tycoon.getCurrentIncome();
        }
        return  totalCps;
    }
}
