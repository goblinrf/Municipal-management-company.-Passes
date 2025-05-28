package company.bot.strategy;


import company.bot.models.Pass;

import java.util.List;

public class Context {
    private PassStatusStrategy strategy;

    public void setStrategy(PassStatusStrategy strategy) {
        this.strategy = strategy;
    }

    public String showMessage(List<Pass> passes) {
        return strategy.execute(passes);
    }
}
