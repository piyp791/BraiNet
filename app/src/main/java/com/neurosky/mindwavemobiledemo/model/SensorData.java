package com.neurosky.mindwavemobiledemo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peps on 7/4/17.
 */

public class SensorData {

    List<Integer> dataList;

    public SensorData(){

        dataList = new ArrayList<>();
    }

    public void addData(int dataPoint){

       dataList.add(dataPoint);
    }

    public List<Integer> getdataList(){
        return dataList;
    }

}
