package com.zzq.demo;

import sun.security.ssl.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yhsyzzq on 2017-11-09.
 */
public class BallAndScales {

    private final static int DEFAULT_INITIAL_CAPACITY = 12;

    private final static double DEAFAULT_INITIAL_WEIGHT = 10;

    private final static int SPECIAL_BALL_MAX_WEIGHT = 20;

    //小球集合
    private static List<Ball> balls = new ArrayList<Ball>();

    private static Ball actualSpecialBall = null;

    //天平对象
    private static Scales scales;

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            System.out.println("=============================== 我是分割线 ===================================");
            BallAndScales.init(i+1);
            Ball specialBall = BallAndScales.searchSpecialBall();
            System.out.println("第" + (i+1) + "次实验结果：质量不同的小球编号：" + specialBall.getCode() + "， 重量：" + specialBall.getWeight());
            if(actualSpecialBall.getCode() != specialBall.getCode() || actualSpecialBall.getWeight() != specialBall.getWeight()){
                System.out.println("实验结果异常，请检查程序！");
            }
            System.out.println("=============================== 我是分割线 ===================================\r\n");

        }

        //检查bug调试代码
//        System.out.println("=============================== 我是分割线 ===================================");
//        BallAndScales.init2();
//        Ball specialBall = BallAndScales.searchSpecialBall();
//        System.out.println("实验结果：质量不同的小球编号：" + specialBall.getCode() + "， 重量：" + specialBall.getWeight());
//        if (actualSpecialBall.getCode() != specialBall.getCode() || actualSpecialBall.getWeight() != specialBall.getWeight()) {
//            System.out.println("实验结果异常，请检查程序！");
//        }
//        System.out.println("=============================== 我是分割线 ===================================\r\n");
    }

    public static void init2() {
        balls.clear();
        System.out.println("实验产生的12个小球的基本情况");
        for (int i = 1; i <= DEFAULT_INITIAL_CAPACITY; i++) {
            Ball ball = new Ball();
            ball.setCode(i);
            if (i == 2) {
                ball.setWeight(1.0);
            } else {
                ball.setWeight(DEAFAULT_INITIAL_WEIGHT);
            }
            balls.add(ball);
            System.out.println("====小球编号：" + ball.getCode() + "小球重量：" + ball.getWeight());
        }

        for (Ball ball : balls) {
            if (ball.getWeight() != DEAFAULT_INITIAL_WEIGHT) {
                actualSpecialBall = ball;
                System.out.println("实验预期结果：质量不同的小球编号：" + ball.getCode() + ", 小球重量：" + ball.getWeight());
            }
        }
        //初始化天平
        scales = Scales.newSingleInstance();
    }

    public static void init(int n) {
        balls.clear();
        //初始化小球
        int randomCode = new Random().nextInt(12) + 1; //生成1-12的随机数
        double specialWeight = BallAndScales.setSpecialWeight();
        if (specialWeight == 10.0) {
            System.out.println("断点===10");
        }
        System.out.println("第" + n + "次实验产生的12个小球的基本情况,randomCode="+randomCode+",specialWeight="+specialWeight);
        for (int i = 1; i <= DEFAULT_INITIAL_CAPACITY; i++) {
            double weight = randomCode == i ? specialWeight : DEAFAULT_INITIAL_WEIGHT;

            Ball ball = new Ball();
            ball.setCode(i);
            ball.setWeight(weight);
            balls.add(ball);
            System.out.println("小球编号：" + ball.getCode() + ", 小球重量：" + ball.getWeight());
        }
        for (Ball ball : balls) {
            if (ball.getWeight() != DEAFAULT_INITIAL_WEIGHT) {
                actualSpecialBall = ball;
                System.out.println("第" + n + "次实验预期结果：质量不同的小球编号：" + ball.getCode() + ", 小球重量：" + ball.getWeight());
            }
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
            weight = setSpecialWeight();
        }
        return weight;
    }

    public static Ball searchSpecialBall() {
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
            leftBalls = balls.subList(0, 3);
            rightBalls = balls.subList(8, 11);
            status = scales.testBalance(leftBalls, rightBalls);
            //===============重量相等，重量不一致的球在12号球=================
            if (status == ScalesStatus.balance) {
                specialBall = balls.get(11);
                return specialBall;
            } else if (status == ScalesStatus.turnLeft) {
                //=========9-11号球有一个轻了========
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(8));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(9));
                status = scales.testBalance(leftBalls, rightBalls);
                //步骤1-1-1:9号球放入天平左侧，10号球放入天平右侧
                if (status == ScalesStatus.balance) {
                    //====== 11号球较轻 =====
                    specialBall = balls.get(10);
                    return specialBall;
                } else if (status == ScalesStatus.turnLeft) {
                    //====== 10号球较轻 =====
                    specialBall = balls.get(9);
                    return specialBall;
                } else {
                    //====== 9号球较轻 =====
                    specialBall = balls.get(8);
                    return specialBall;
                }
            } else {
                //=========9-11号球有一个重了========
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(8));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(9));
                status = scales.testBalance(leftBalls, rightBalls);
                //步骤1-1-1:9号球放入天平左侧，10号球放入天平右侧
                if (status == ScalesStatus.balance) {
                    //====== 11号球较重 =====
                    specialBall = balls.get(10);
                    return specialBall;
                } else if (status == ScalesStatus.turnLeft) {
                    //====== 9号球较重 =====
                    specialBall = balls.get(8);
                    return specialBall;
                } else {
                    //====== 10号球较重 =====
                    specialBall = balls.get(9);
                    return specialBall;
                }
            }
        }
        //============1-4号球的总重量比5-8号球的总重量要大，说明9-12号球没特殊小球================
        else if (status == ScalesStatus.turnLeft) {
            //1号球+5,6,7号球放入天平左侧；8,9,10,11号球放入天平右侧
            leftBalls = new ArrayList<Ball>();
            leftBalls.add(balls.get(0));
            leftBalls.add(balls.get(4));
            leftBalls.add(balls.get(5));
            leftBalls.add(balls.get(6));

            rightBalls = new ArrayList<Ball>();
            rightBalls.add(balls.get(7));
            rightBalls.add(balls.get(8));
            rightBalls.add(balls.get(9));
            rightBalls.add(balls.get(10));

            status = scales.testBalance(leftBalls, rightBalls);
            if (status == ScalesStatus.balance) {
                //两边平衡说明2,3,4号小球中的有一个重了
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(1));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(2));
                status = scales.testBalance(leftBalls, rightBalls);

                if (status == ScalesStatus.balance) {
                    //====== 4号球较重 =====
                    specialBall = balls.get(3);
                    return specialBall;
                } else if (status == ScalesStatus.turnLeft) {
                    //====== 2号球较重 =====
                    specialBall = balls.get(1);
                    return specialBall;
                } else {
                    //====== 3号球较重 =====
                    specialBall = balls.get(2);
                    return specialBall;
                }
            } else if (status == ScalesStatus.turnLeft) {
                //通过假设法推断8号球轻了或者1号球重了
                //两边平衡说明2,3,4号小球中的有一个重了
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(0));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(1));
                status = scales.testBalance(leftBalls, rightBalls);

                if (status == ScalesStatus.balance) {
                    //====== 8号球较轻 =====
                    specialBall = balls.get(7);
                    return specialBall;
                } else if (status == ScalesStatus.turnLeft) {
                    //====== 1号球较重 =====
                    specialBall = balls.get(0);
                    return specialBall;
                } else {
                    //此种情况不可能发生，因为1号球较重
                }
            } else {
                //根据假设法推断 5,6,7号球有一个轻了
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(4));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(5));
                status = scales.testBalance(leftBalls, rightBalls);

                if (status == ScalesStatus.balance) {
                    //====== 7号球较轻 =====
                    specialBall = balls.get(6);
                    return specialBall;
                } else if (status == ScalesStatus.turnLeft) {
                    //====== 6号球较轻 =====
                    specialBall = balls.get(5);
                    return specialBall;
                } else {
                    //====== 5号球较轻 =====
                    specialBall = balls.get(4);
                    return specialBall;
                }
            }
        } else {
            //2,3,4,5号球放入天平左侧；1 + 9,10,11号球放入天平右侧
            leftBalls = new ArrayList<Ball>();
            leftBalls.add(balls.get(1));
            leftBalls.add(balls.get(2));
            leftBalls.add(balls.get(3));
            leftBalls.add(balls.get(4));

            rightBalls = new ArrayList<Ball>();
            rightBalls.add(balls.get(0));
            rightBalls.add(balls.get(8));
            rightBalls.add(balls.get(9));
            rightBalls.add(balls.get(10));
            status = scales.testBalance(leftBalls, rightBalls);
            if (status == ScalesStatus.balance) {
                //根据假设法推断，6,7,8号球有一个重了
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(5));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(6));
                status = scales.testBalance(leftBalls, rightBalls);

                if (status == ScalesStatus.balance) {
                    //====== 8号球较重 =====
                    specialBall = balls.get(7);
                    return specialBall;
                } else if (status == ScalesStatus.turnLeft) {
                    //====== 6号球较重 =====
                    specialBall = balls.get(5);
                    return specialBall;
                } else {
                    //====== 7号球较重 =====
                    specialBall = balls.get(6);
                    return specialBall;
                }
            } else if (status == ScalesStatus.turnLeft) {
                //5号球重了或者1号球轻了
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(0));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(1));
                status = scales.testBalance(leftBalls, rightBalls);

                if (status == ScalesStatus.balance) {
                    //====== 5号球较重 =====
                    specialBall = balls.get(4);
                    return specialBall;
                } else if (status == ScalesStatus.turnRight) {
                    //====== 1号球较轻 =====
                    specialBall = balls.get(0);
                    return specialBall;
                } else {
                    //====== 此种情况不可能发生 =====
                }
            } else {
                //根据假设法推断2,3,4中有一个球轻了
                //5号球重了或者1号球轻了
                leftBalls = new ArrayList<Ball>();
                leftBalls.add(balls.get(1));
                rightBalls = new ArrayList<Ball>();
                rightBalls.add(balls.get(2));
                status = scales.testBalance(leftBalls, rightBalls);

                if (status == ScalesStatus.balance) {
                    //====== 4号球较轻 =====
                    specialBall = balls.get(3);
                    return specialBall;
                } else if (status == ScalesStatus.turnLeft) {
                    //====== 3号球较轻 =====
                    specialBall = balls.get(2);
                    return specialBall;
                } else {
                    //====== 2号球较轻 =====
                    specialBall = balls.get(1);
                    return specialBall;
                }
            }
        }
        return null;
    }

}
