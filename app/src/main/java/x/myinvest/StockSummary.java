package x.myinvest;

public class StockSummary {
    private String code;
    private String name;
    private int totalQuantity;
    private double totalMarketValue;
    private String percentage;          // 显示用： "60.92%"
    private double percentageValue;     // 排序用： 60.92

    public StockSummary(String code, String name, int totalQuantity, double totalMarketValue, String percentage, double percentageValue) {
        this.code = code;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.totalMarketValue = totalMarketValue;
        this.percentage = percentage;
        this.percentageValue = percentageValue;
    }

    // Getters（按需添加）
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getTotalQuantity() { return totalQuantity; }
    public double getTotalMarketValue() { return totalMarketValue; }
    public String getPercentage() { return percentage; }
    public double getPercentageValue() { return percentageValue; }

    @Override
    public String toString() {
       // return String.format("Code: %s, Name: %s, Qty: %d, Value: %.2f, %%: %s\n",
        //        code, name, totalQuantity, totalMarketValue, percentage);
        return String.format("%s\t%s\t%d\t%.2f\t\t%s\n",name,code,totalQuantity,totalMarketValue,percentage);
    }
}