package com.zzq.demo;

import java.util.List;

/**
 * Created by yhsyzzq on 2017-11-09.
 */
public class Scales {

    private static Scales scales;

    private Scales() {

    }

    /**
     * 创建天平单例
     *
     * @return
     */
    public static Scales newSingleInstance() {
        if (scales == null) {
            return new Scales();
        }
        return scales;
    }

    /**
     * 测试是否平衡
     *
     * @param leftBalls
     * @param rightBalls
     */
    public ScalesStatus testBalance(List<Ball> leftBalls, List<Ball> rightBalls) {
        if (Scales.getSumWeight(leftBalls) > Scales.getSumWeight(rightBalls)) {
            return ScalesStatus.turnLeft;
        } else if (Scales.getSumWeight(leftBalls) == Scales.getSumWeight(rightBalls)) {
            return ScalesStatus.balance;
        } else {
            return ScalesStatus.turnRight;
        }
    }

    private static double getSumWeight(List<Ball> balls) {
        double sumWeight = 0;
        for (Ball ball : balls) {
            sumWeight += ball.getWeight();
        }
        return sumWeight;
    }
}
