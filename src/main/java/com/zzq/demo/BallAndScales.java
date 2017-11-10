package com.zzq.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 332406 on 2017-11-09.
 */
public class BallAndScales {

    private final static int DEFAULT_INITIAL_CAPACITY = 12;

    private final static double DEAFAULT_INITIAL_WEIGHT = 10;

    private final static int SPECIAL_BALL_MAX_WEIGHT = 20;

    //小球集合
    private static List<Ball> balls = new ArrayList<Ball>();

    //天平对象
    private static Scales scales;

    public static void init() {
        //初始化小球
        int randomCode = new Random().nextInt(13);
        double specialWeight = BallAndScales.setSpecialWeight();
        for (int i = 1; i <= DEFAULT_INITIAL_CAPACITY; i++) {
            double weight = randomCode == i ? specialWeight : DEAFAULT_INITIAL_WEIGHT;

            Ball ball = new Ball();
            ball.setCode(i);
            ball.setWeight(weight);
            balls.add(ball);
            System.out.println("====小球编号："+ball.getCode() + "小球重量："+ball.getWeight());
        }

        //初始化天平
        scales = Scales.newSingleInstance();
    }

    /**
     * 设置特殊小球的重量
     *
     * @return
     */
    private static double setSpecialWeight() {
        double weight = new Random().nextInt(SPECIAL_BALL_MAX_WEIGHT);
        if (weight == DEAFAULT_INITIAL_WEIGHT || weight == 0) {
            setSpecialWeight();
        }
        return weight;
    }

    public Ball searchSpecialBall() {
        List<Ball> specialBalls = null;
        List<Ball> leftBalls = null;
        List<Ball> rightBalls = null;
        ScalesStatus status = null;
        Ball specialBall = null;

        //步骤1：1-4号球放入天平左侧，5-8号球放入天平右侧
        leftBalls = balls.subList(0, DEFAULT_INITIAL_CAPACITY / 3);
        rightBalls = balls.subList(DEFAULT_INITIAL_CAPACITY / 3, DEFAULT_INITIAL_CAPACITY * 2 / 3);
        status = scales.testBalance(leftBalls, rightBalls);
        //============1-4号球的总重量与5-8号球的总重量相等,说明重量不一样的小球在9-12号球中================
        if (status == ScalesStatus.balance) {
            specialBalls = balls.subList(DEFAULT_INITIAL_CAPACITY * 2 / 3, DEFAULT_INITIAL_CAPACITY * 3 / 3);
            //============继续测试================//
            //步骤1-1:1-3号球放入天平左侧，9-11号球放入天平右侧
            leftBalls = balls.subList(0,3);
            rightBalls = specialBalls.subList(8,11);
            status = scales.testBalance(leftBalls, rightBalls);
            //===============重量相等，重量不一致的球在12号球=================
            if(status == ScalesStatus.balance){
                specialBall = balls.get(11);
                return specialBall;
            }else if(status == ScalesStatus.turnLeft){
                //=========9-11号球有一个轻了========
                leftBalls.clear();
                leftBalls.add(balls.get(8));
                rightBalls.clear();
                rightBalls.add(balls.get(9));
                status = scales.testBalance(leftBalls, rightBalls);
                //步骤1-1-1:9号球放入天平左侧，10号球放入天平右侧
                if(status == ScalesStatus.balance){
                    //====== 11号球较轻 =====
                    specialBall = balls.get(10);
                    return specialBall;
                }else if(status == ScalesStatus.turnLeft){
                    //====== 10号球较轻 =====
                    specialBall = balls.get(9);
                    return specialBall;
                }else{
                    //====== 9号球较轻 =====
                    specialBall = balls.get(8);
                    return specialBall;
                }
            }else{
                //=========9-11号球有一个重了========
                leftBalls.clear();
                leftBalls.add(balls.get(8));
                rightBalls.clear();
                rightBalls.add(balls.get(9));
                status = scales.testBalance(leftBalls, rightBalls);
                //步骤1-1-1:9号球放入天平左侧，10号球放入天平右侧
                if(status == ScalesStatus.balance){
                    //====== 11号球较重 =====
                    specialBall = balls.get(10);
                    return specialBall;
                }else if(status == ScalesStatus.turnLeft){
                    //====== 10号球较重 =====
                    specialBall = balls.get(9);
                    return specialBall;
                }else{
                    //====== 9号球较重 =====
                    specialBall = balls.get(8);
                    return specialBall;
                }
            }
        }
        //============1-4号球的总重量比5-8号球的总重量要大，说明9-12号球没特殊小球================
        else if (status == ScalesStatus.turnLeft) {


        }

        else {

        }
        return null;
    }

    public static void main(String[] args) {
        init();
    }

}
