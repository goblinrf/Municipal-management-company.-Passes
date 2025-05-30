package company.bot.strategy.impl;

import company.bot.models.Pass;
import company.bot.strategy.PassStatusStrategy;

import java.util.List;

public class StrategyNotEnded implements PassStatusStrategy {
    @Override
    public String execute(List<Pass> passes) {
        return "✅Активен";
    }
}