package code.algorithm.greedy;

import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * 〈田忌赛马〉<p>
 * 1、田忌和齐王的马都按从慢到快排序
 * 2、比较田忌最慢的马比齐王最慢的马
 *  2.1 如果田忌最慢马慢，则用田忌最慢马与齐王最快马比赛，最小代价输
 *  2.2 如果田忌最慢马快，则用田忌最慢马与齐王最慢马比赛，必赢
 *  2.3 如果双方最慢马一样快，则比较田忌最快的马与齐王最快的马
 *      2.3.1 如果田忌最快马慢，则用田忌最慢马与齐王最快马比赛，最小代价输
 *      2.3.2 如果田忌最快马快，比较田忌最快的马与齐王最快的马比赛，必赢
 *      2.3.3 如果双方最快马一样快，则用田忌最慢马与齐王最快马比赛，最小代价输
 *
 *  注: 最慢/最快的马都是指剩余未比赛过的马中最快/最慢
 *
 * @author zixiao
 * @date 2020/1/16
 */
public class TianjiRace {

    /**
     * 马数量
     */
    private int horseNum;

    /**
     * 田忌的马
     */
    private int[] tianHorses;

    /**
     * 齐王的马
     */
    private int[] kingHorses;

    /**
     * 田忌马起始位置
     */
    private int tianFirst;

    /**
     * 田忌马终止位置
     */
    private int tianLast;

    /**
     * 齐王马起始位置
     */
    private int kingFirst;

    /**
     * 齐王马终止位置
     */
    private int kingLast;

    /**
     * 田忌得分
     */
    private int tianScore;

    public void startRace(int[] tHorses, int[] kHorses){
        Assert.isTrue(tHorses.length == kHorses.length, "");
        this.horseNum = tHorses.length;
        this.tianHorses = tHorses;
        this.kingHorses = kHorses;

        //从慢到快排序
        Arrays.sort(this.tianHorses);
        Arrays.sort(this.kingHorses);

        tianScore = 0;
        tianFirst = kingFirst = 0;
        tianLast = kingLast = horseNum - 1;

        System.out.println("*********** Start horse race ************");

        for (int i = 0; i < horseNum; i++) {
            race();
        }

        System.out.println("Tianji score: " + tianScore);
    }

    private void race(){
        //1 比较田忌最慢的马与齐王最慢的马
        int compare = tianHorses[tianFirst] - kingHorses[kingFirst];
        if(compare < 0){
            //1.1 如果田忌最慢马慢，则用田忌最慢马与齐王最快马比赛，最小代价输
            race(tianHorses[tianFirst], kingHorses[kingLast]);
            tianFirst++;
            kingLast--;
        }else if(compare > 0){
            //1.2 如果田忌最慢马快，则用田忌最慢马与齐王最慢马比赛，必赢
            race(tianHorses[tianFirst], kingHorses[kingFirst]);
            tianFirst++;
            kingFirst++;
        }else {
            //1.3 比较田忌最快的马与齐王最快的马
            int compareFast = tianHorses[tianLast] - kingHorses[kingLast];
            if(compareFast < 0){
                //1.3.1 如果田忌最快马慢，则用田忌最慢马与齐王最快马比赛，最小代价输
                race(tianHorses[tianFirst], kingHorses[kingLast]);
                tianFirst++;
                kingLast--;
            }else if(compareFast > 0){
                //1.3.2 如果田忌最快马快，比较田忌最快的马与齐王最快的马比赛，必赢
                race(tianHorses[tianLast], kingHorses[kingLast]);
                tianLast--;
                kingLast--;
            }else{
                //1.3.3 如果双方最快马一样快，则用田忌最慢马与齐王最快马比赛，最小代价输
                race(tianHorses[tianFirst], kingHorses[kingLast]);
                tianFirst++;
                kingLast--;
            }
        }
    }

    /**
     * 比赛，赢加1分，输减1分，平局0分
     * @param tianHorse
     * @param kingHorse
     */
    private void race(int tianHorse, int kingHorse){
        System.out.println(tianHorse + " => " + kingHorse);
        if(tianHorse > kingHorse){
            tianScore++;
        }else if(tianHorse < kingHorse){
            tianScore--;
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        TianjiRace race = new TianjiRace();
        // 30 20 10 : 35 25 15
        race.startRace(new int[]{30, 20, 10}, new int[]{35, 25, 15});

        // 10 20 30 : 10 20 30
        race.startRace(new int[]{10, 20, 30}, new int[]{10, 20, 30});

        // 8 20 31 : 10 20 30
        race.startRace(new int[]{8, 20, 31}, new int[]{10, 20, 30});

    }
}
