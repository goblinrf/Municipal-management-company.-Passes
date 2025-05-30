package company.bot.strategy;


import company.bot.models.Pass;

import java.util.List;

public interface PassStatusStrategy {
    String execute(List<Pass> passes);
}