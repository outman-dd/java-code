package code.concurrency.example.cat;

import java.io.Serializable;

/**
 * 〈CatSortResult〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/12
 */
public class CatSortResult implements Serializable {

    private String lineString;

    private int count;

    public CatSortResult(String lineString, int count) {
        this.lineString = lineString;
        this.count = count;
    }


    public String getLineString() {
        return lineString;
    }

    public void setLineString(String lineString) {
        this.lineString = lineString;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return count + "\t" + lineString;
    }
}