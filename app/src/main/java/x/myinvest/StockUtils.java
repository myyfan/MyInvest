package x.myinvest;

import java.text.DecimalFormat;
import java.util.*;

public class StockUtils {

    public static List<StockSummary> summarizeStocks(List<Stock> stocks) {
        // Step 1: 按 code 聚合
        Map<String, StockAgg> aggMap = new HashMap<>();

        for (Stock stock : stocks) {
            String code = stock.code;
            StockAgg agg = aggMap.get(code);
            if (agg == null) {
                agg = new StockAgg(stock.name);
                aggMap.put(code, agg);
            }
            agg.add(Integer.parseInt(stock.number), Double.parseDouble(stock.nowPrice));
        }

        // Step 2: 计算总市值
        double totalMarketValueAll = 0.0;
        for (StockAgg agg : aggMap.values()) {
            totalMarketValueAll += agg.totalMarketValue;
        }

        // Step 3: 构建结果列表
        List<StockSummary> result = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");

        for (Map.Entry<String, StockAgg> entry : aggMap.entrySet()) {
            String code = entry.getKey();
            StockAgg agg = entry.getValue();

            double percentValue = 0.0;
            if (totalMarketValueAll > 0) {
                percentValue = (agg.totalMarketValue / totalMarketValueAll) * 100;
            }

            result.add(new StockSummary(
                    code,
                    agg.name,
                    agg.totalQuantity,
                    agg.totalMarketValue,
                    df.format(percentValue) + "%",
                    percentValue
            ));
        }

        // 可选：按股票代码排序
        result.sort(Comparator.comparing(StockSummary::getCode));

        return result;
    }

    // 辅助聚合类（内部使用）
    private static class StockAgg {
        String name;
        int totalQuantity = 0;
        double totalMarketValue = 0.0;

        StockAgg(String name) {
            this.name = name;
        }

        void add(int quantity, double price) {
            this.totalQuantity += quantity;
            this.totalMarketValue += quantity * price;
        }
    }
}