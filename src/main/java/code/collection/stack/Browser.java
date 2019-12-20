package code.collection.stack;

/**
 * 〈模拟浏览器 前进/后退〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public class Browser {

    private static String HOME_PAGE = "about:blank";

    private StringStack forwardStack = new StringStack(16);

    private StringStack backwardStack = new StringStack(16);

    private String currentUrl = null;

    public Browser() {
        open(HOME_PAGE);
    }

    public boolean open(String url) {
        if (currentUrl != null) {
            backwardStack.push(currentUrl);
        }
        currentUrl = url;
        forwardStack.clear();
        System.out.println("Open new page: " + url);
        return true;
    }

    public String forward() {
        String forwardUrl = forwardStack.pop();
        if (forwardUrl != null) {
            backwardStack.push(currentUrl);
            currentUrl = forwardUrl;
            System.out.println("Forward to: " + currentUrl);
        } else {
            System.out.println("Can not forward");
        }
        return currentUrl;
    }

    public String backward() {
        String backwardUrl = backwardStack.pop();
        if (backwardUrl != null) {
            forwardStack.push(currentUrl);
            currentUrl = backwardUrl;
            System.out.println("Backward to: " + currentUrl);
        } else {
            System.out.println("Can not backward");
        }
        return currentUrl;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public static void main(String[] args) {
        Browser browser = new Browser();
        browser.backward();

        browser.open("A");
        browser.open("B");
        browser.open("C");

        browser.backward();
        browser.backward();
        browser.forward();

        browser.open("D");
        browser.forward();
    }
}
