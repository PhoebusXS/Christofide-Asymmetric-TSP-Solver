package com.example;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

public class HelloWorld {
    public static void main(String[] args) {
        int[] input = new int[] {2,3,1};
        int budget = 20;
        final_brute_force_call(input,budget);
    }

    public static void final_brute_force_call (int[] route_input, double budget){
        ArrayList<ArrayList<Integer>> possibility = permute(route_input);
        ArrayList<ArrayList<Integer>> most_sufficient_mode = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Double>> most_sufficient_time_price = new ArrayList<ArrayList<Double>>();


        for(ArrayList<Integer> route : possibility){
            route.add(0);
            route.add(0,0);

            // declare place to store detailed modes and the time,cost for this particulare route
            ArrayList<ArrayList<Integer>> modes_with_details = new ArrayList<ArrayList<Integer>>();
            ArrayList<ArrayList<Double>> time_price = new ArrayList<>();
            get_time_and_speed(route,modes_with_details,time_price);
            int id = get_limit_time_with_budget(route, 6.96,modes_with_details,time_price);
            most_sufficient_mode.add(modes_with_details.get(id));
            most_sufficient_time_price.add(time_price.get(id));
        }

        // after brute force, get most efficient one

        double lowest_time = Double.POSITIVE_INFINITY;
        int lowest_id = -1;
        for(int j=0;j<most_sufficient_time_price.size();j++){
            if (most_sufficient_time_price.get(j).get(0) < lowest_time){
                if (most_sufficient_time_price.get(j).get(1) <= budget){
                    lowest_time = most_sufficient_time_price.get(j).get(0);
                    lowest_id = j;
                }

            }
        }

        if (lowest_id==-1){
            System.out.println("No match can be found");
        }
        else {
            System.out.println(
                    "the most efficient route is " + Arrays.toString(route_input)
                            + "\n the transportation mode is: (0-bus; 1-taxi, 2-foot)\n"
                            + most_sufficient_mode.get(lowest_id)
                            + "\n with money " + most_sufficient_time_price.get(lowest_id).get(1) + " dollars spent"
                            + "\n and time " + most_sufficient_time_price.get(lowest_id).get(0) + " minutes"
            );
        }
    }


    public static int get_limit_time_with_budget (ArrayList<Integer> route,double budget, ArrayList<ArrayList<Integer>> modes_with_details,ArrayList<ArrayList<Double>> time_price){
        double min_time = Double.POSITIVE_INFINITY;
        int id_for_min = -1;
        ArrayList<Integer> result = new ArrayList<>();
        for (int i=0;i<time_price.size();i++){
            if (time_price.get(i).get(0)<min_time && time_price.get(i).get(1)<=budget){
                min_time = time_price.get(i).get(0);
                id_for_min = i;
            }
        }
        System.out.println("when route is "+ route.toString()+" when mode is "+ modes_with_details.get(id_for_min).toString()+" ; time and money is "+time_price.get(id_for_min).toString());
        return id_for_min;
    }


    public static int[][] get_time_and_speed(ArrayList<Integer> route,ArrayList<ArrayList<Integer>> modes_with_details,ArrayList<ArrayList<Double>> time_price){
        Table info_table = new Table();
        ArrayList<Integer> mode = new ArrayList<Integer>();
        get_time_and_speed(info_table,route,modes_with_details,mode, time_price, 0,0,0);
        return null;
    }

    public static void get_time_and_speed(Table info_table, ArrayList<Integer> route,ArrayList<ArrayList<Integer>> modes_with_details, ArrayList<Integer> mode, ArrayList<ArrayList<Double>> time_price,double past_time, double past_money, int past_stop){
        boolean last_move = false;
        if (mode.size()==route.size()-1){

            ArrayList<Integer> mode_copy= new ArrayList<>(mode);
            modes_with_details.add(mode_copy);
            ArrayList<Double> time_and_price = new ArrayList<>();

            Double t = new Double(past_time);
            Double p = new Double(past_money);
            time_and_price.add(t);
            time_and_price.add(p);
            time_price.add(time_and_price);
            last_move=true;
        }
        else if (!last_move){
            int this_stop = route.get(past_stop);
            int next_stop = route.get(past_stop+1);

            mode.add(0);
//            System.out.println("at mode 0");
//            System.out.println("time increase from "+ past_time + " with "+ info_table.publicTime[this_stop][next_stop]);
//            System.out.println("money increase "+ past_money + "with "+ info_table.publicCost[this_stop][next_stop]);
//            System.out.println(this_stop + "  "+ next_stop+"\n");

            get_time_and_speed(info_table,route,modes_with_details,mode,time_price,past_time+info_table.publicTime[this_stop][next_stop],past_money+info_table.publicCost[this_stop][next_stop],past_stop+1);
            mode.remove(mode.size() - 1);
//            past_stop-=1;


            mode.add(1);
//            System.out.println("at mode 1");
//            System.out.println("time increase from "+ past_time + " with "+ info_table.taxiTime[this_stop][next_stop]);
//            System.out.println("money increase "+ past_money + "with "+ info_table.taxiCost[this_stop][next_stop]);
//            System.out.println(this_stop + "  "+ next_stop+"\n");

            get_time_and_speed(info_table,route,modes_with_details,mode,time_price,past_time+info_table.taxiTime[this_stop][next_stop],past_money+info_table.taxiCost[this_stop][next_stop],past_stop+1);
            mode.remove(mode.size() - 1);
//            past_time-=info_table.taxiTime[this_stop][next_stop];
//            past_money-=info_table.taxiCost[this_stop][next_stop];
//            past_stop-=1;

            mode.add(2);
//            System.out.println("at mode 3");
//            System.out.println("time increase from "+ past_time + " with "+ info_table.footTime[this_stop][next_stop]);
//            System.out.println("money increase "+ past_money);
//            System.out.println(this_stop + "  "+ next_stop+"\n");
            get_time_and_speed(info_table,route,modes_with_details,mode,time_price,past_time+info_table.footTime[this_stop][next_stop],past_money,past_stop+1);
            mode.remove(mode.size()-1 );


//            past_time-=info_table.footTime[this_stop][next_stop];
//            past_stop-=1;

        }
    }






    public static ArrayList<ArrayList<Integer>> permute(int[] nums) {
        ArrayList<ArrayList<Integer>> results = new ArrayList<ArrayList<Integer>>();
        if(nums == null || nums.length == 0) return results;
        ArrayList<Integer> result = new ArrayList<>();
        dfs(nums, results, result);
        return results;
    }

    public static void dfs(int[] nums, ArrayList<ArrayList<Integer>> results, ArrayList<Integer> result){
        if(nums.length == result.size()){
            ArrayList<Integer> temp = new ArrayList<>(result);
            results.add(temp);
        }
        for(int i=0; i<nums.length; i++){
            if(result.contains(nums[i])) continue;
            result.add(nums[i]);
            dfs(nums, results, result);
            result.remove(result.size()-1);
        }
    }

//    static int factorial(int n){
//        int output = 1;
//        for (int i=1;i<=n;i++){
//            output = output * i;
//        }
//        return output;
//    }



    public static class Table{
        private static double inf = Double.POSITIVE_INFINITY;

        public static double[][] publicCost = new double[][]{
                { inf, 0.83, 1.18, 4.03, 0.88, 1.96 },
                { 0.83, inf, 1.26, 4.03, 0.98, 1.89 },
                { 1.18, 1.26, inf, 2.00, 0.98, 1.99 },
                { 1.18, 1.26, 0.00, inf, 0.98, 1.99 },
                { 0.88, 0.98, 0.98, 3.98, inf, 1.91 },
                { 1.88, 1.96, 2.11, 4.99, 1.91, inf }
        };

        public static double[][] publicTime = new double[][]{
                { inf, 17, 26, 35, 19, 84 },
                { 17, inf, 31, 38, 24, 85 },
                { 24, 29, inf, 10, 18, 85 },
                { 33, 38, 10, inf, 27, 92 },
                { 18, 23, 19, 28, inf, 83 },
                { 86, 87, 86, 96, 84, inf }
        };

        public static double[][] taxiCost = new double[][]{
                { inf, 3.22, 6.96, 8.5, 4.98, 18.4 },
                { 4.32, inf, 7.84, 9.38, 4.76, 18.18 },
                { 8.3, 7.96, inf, 4.54, 6.42, 22.58 },
                { 8.74, 8.4, 3.22, inf, 6.64, 22.8 },
                { 5.32, 4.76, 4.98, 6.52, inf, 18.4 },
                { 22.48, 19.4, 21.48, 23.68, 21.6, inf }
        };

        public static double[][] taxiTime = new double[][]{
                { inf, 3, 14, 19, 8, 30 },
                { 6, inf, 13, 18, 8, 29 },
                { 12, 14, inf, 9, 11, 31 },
                { 13, 14, 4, inf, 12, 32 },
                { 7, 8, 9, 14, inf, 30 },
                { 32, 29, 32, 36, 30, inf }
        };

        public static double[][] footTime = new double[][]{
                { inf, 14, 69, 76, 28, 269 },
                { 14, inf, 82, 88, 39, 264 },
                { 69, 81, inf, 12, 47, 270 },
                { 76, 88, 12, inf, 55, 285 },
                { 28, 39, 47, 55, inf, 264 },
                { 269, 264, 270, 285, 264, inf }
        };
        public Table(){}
    }
}
